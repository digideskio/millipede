package lbms.plugins.mldht.kad;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lbms.plugins.mldht.kad.DHT.LogLevel;
import lbms.plugins.mldht.kad.messages.MessageBase;

/**
 * Performs a task on K nodes provided by a KClosestNodesSearch.
 * This is a base class for all tasks.
 *
 * @author Damokles
 */
public abstract class Task implements RPCCallListener {

	protected List<KBucketEntry>		visited;			// nodes visited
	protected SortedSet<KBucketEntry>	todo;				// nodes todo
	protected Node						node;

	protected Key						targetKey;

	protected String					info;
	protected RPCServerBase				rpc;
	
	private AtomicInteger				outstandingReqs = new AtomicInteger();
	private int							sentReqs;
	private int							recvResponses;
	private int							failedReqs;
	private int							taskID;
	private boolean						taskFinished;
	private boolean						queued;
	private List<TaskListener>			listeners;
	private ScheduledFuture<?>			timeoutTimer;

	/**
	 * Create a task.
	 * @param rpc The RPC server to do RPC calls
	 * @param node The node
	 */
	Task (Key target, RPCServerBase rpc, Node node) {
		this.targetKey = target;
		this.rpc = rpc;
		this.node = node;
		queued = true;
		todo = new TreeSet<KBucketEntry>(new KBucketEntry.DistanceOrder(targetKey));
		visited = new ArrayList<KBucketEntry>();
		taskFinished = false;
	}

	/**
	 * @param rpc The RPC server to do RPC calls
	 * @param node The node
	 * @param info info that should be displayed to the user, eg. download name on announce task
	 */
	Task (Key target, RPCServerBase rpc, Node node, String info) {
		this(target, rpc, node);
		this.info = info;
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallListener#onResponse(lbms.plugins.mldht.kad.RPCCall, lbms.plugins.mldht.kad.messages.MessageBase)
	 */
	public void onResponse (RPCCallBase c, MessageBase rsp) {
		outstandingReqs.decrementAndGet();

		recvResponses++;

		if (!isFinished()) {
			callFinished(c, rsp);

			if (canDoRequest() && !isFinished()) {
				update();
			}
		}
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.RPCCallListener#onTimeout(lbms.plugins.mldht.kad.RPCCall)
	 */
	public void onTimeout (RPCCallBase c) {
		outstandingReqs.decrementAndGet();

		failedReqs++;

		if (!isFinished()) {
			callTimeout(c);

			if (canDoRequest() && !isFinished()) {
				update();
			}
		}
	}

	/**
	 *  Start the task, to be used when a task is queued.
	 */
	void start () {
		if (queued) {
			DHT.logDebug("Starting Task: " + this.getClass().getSimpleName()
					+ " TaskID:" + taskID);
			queued = false;
			startTimeout();
			try
			{
				update();
			} catch (Exception e)
			{
				DHT.log(e, LogLevel.Error);
			}
		}
	}

	/**
	 * Will continue the task, this will be called every time we have
	 * rpc slots available for this task. Should be implemented by derived classes.
	 */
	abstract void update ();

	/**
	 * A call is finished and a response was received.
	 * @param c The call
	 * @param rsp The response
	 */
	abstract void callFinished (RPCCallBase c, MessageBase rsp);

	/**
	 * A call timedout
	 * @param c The call
	 */
	abstract void callTimeout (RPCCallBase c);

	/**
	 * Do a call to the rpc server, increments the outstanding_reqs variable.
	 * @param req THe request to send
	 * @return true if call was made, false if not
	 */
	boolean rpcCall (MessageBase req) {
		if (!canDoRequest()) {
			return false;
		}

		RPCCallBase c = rpc.doCall(req);
		c.addListener(this);
		outstandingReqs.incrementAndGet();
		
		sentReqs++;
		return true;
	}

	/// See if we can do a request
	boolean canDoRequest () {
		return outstandingReqs.get() < DHTConstants.MAX_CONCURRENT_REQUESTS;
	}

	/// Is the task finished
	public boolean isFinished () {
		return taskFinished;
	}

	/// Set the task ID
	void setTaskID (int tid) {
		taskID = tid;
	}

	/// Get the task ID
	public int getTaskID () {
		return taskID;
	}

	/**
	 * @return the Count of Failed Requests
	 */
	public int getFailedReqs () {
		return failedReqs;
	}

	/**
	 * @return the Count of Received Responses
	 */
	public int getRecvResponses () {
		return recvResponses;
	}

	/**
	 * @return the Count of Sent Requests
	 */
	public int getSentReqs () {
		return sentReqs;
	}

	public int getTodoCount () {
		return todo.size();
	}

	/**
	 * @return the targetKey
	 */
	public Key getTargetKey () {
		return targetKey;
	}

	/**
	 * @return the info
	 */
	public String getInfo () {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo (String info) {
		this.info = info;
	}

	public void addToTodo (KBucketEntry e) {
		synchronized (todo) {
			todo.add(e);
		}
	}

	/// Get the number of outstanding requests
	public int getNumOutstandingRequests () {
		return outstandingReqs.get();
	}

	public boolean isQueued () {
		return queued;
	}

	/// Kills the task
	void kill () {
		taskFinished = true;
		finished(this);
	}

	/**
	 * Starts the Timeout Timer
	 */
	private void startTimeout () {
		DHT.getScheduler().schedule(new Runnable() {
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run () {
				if (!taskFinished) {
					DHT.logDebug("Task was Killed by Timeout. TaskID: "
							+ taskID);
					kill();
				}
			}
		}, DHTConstants.TASK_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	/**
	 * Add a node to the todo list
	 * @param ip The ip or hostname of the node
	 * @param port The port
	 */
	void addDHTNode (InetAddress ip, int port) {
		InetSocketAddress addr = new InetSocketAddress(ip, port);
		synchronized (todo) {
			todo.add(new KBucketEntry(addr, Key.createRandomKey()));
		}
	}

	/**
	 * The task is finsihed.
	 * @param t The Task
	 */
	private void finished (Task t) {
		DHT.logDebug("Task finished: " + getTaskID());
		if (timeoutTimer != null) {
			timeoutTimer.cancel(false);
		}
		if (listeners != null) {
			for (TaskListener tl : listeners) {
				tl.finished(this);
			}
		}
	}

	protected void done () {
		taskFinished = true;
		finished(this);
	}

	public void addListener (TaskListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<TaskListener>(1);
		}
		// listener is added after the task already terminated, thus it won't get the event, trigger it manually
		if(taskFinished)
			listener.finished(this);
		listeners.add(listener);
	}

	public void removeListener (TaskListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
}
