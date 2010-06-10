package lbms.plugins.mldht.kad;

import lbms.plugins.mldht.kad.DHT.DHTtype;
import lbms.plugins.mldht.kad.messages.FindNodeRequest;
import lbms.plugins.mldht.kad.messages.FindNodeResponse;
import lbms.plugins.mldht.kad.messages.MessageBase;
import lbms.plugins.mldht.kad.messages.MessageBase.Method;
import lbms.plugins.mldht.kad.messages.MessageBase.Type;

/**
 * @author Damokles
 *
 */
public class KeyspaceCrawler extends Task {
	
	KeyspaceCrawler (RPCServerBase rpc, Node node) {
		super(Key.createRandomKey(),rpc, node);
		setInfo("Exhaustive Keyspace Crawl");
	}

	@Override
	synchronized void update () {
		// go over the todo list and send find node calls
		// until we have nothing left
		synchronized (todo) {

			while (todo.size() > 0 && canDoRequest()) {
				KBucketEntry e = todo.first();
				todo.remove(e);
				// only send a findNode if we haven't allready visited the node
				if (!visited.contains(e)) {
					// send a findNode to the node
					FindNodeRequest fnr;

					fnr = new FindNodeRequest(node.getOurID(),Key.createRandomKey());
					fnr.setWant4(rpc.getDHT().getType() == DHTtype.IPV4_DHT || DHT.getDHT(DHTtype.IPV4_DHT).getNode().getNumEntriesInRoutingTable() < DHTConstants.BOOTSTRAP_IF_LESS_THAN_X_PEERS);
					fnr.setWant6(rpc.getDHT().getType() == DHTtype.IPV6_DHT || DHT.getDHT(DHTtype.IPV6_DHT).getNode().getNumEntriesInRoutingTable() < DHTConstants.BOOTSTRAP_IF_LESS_THAN_X_PEERS);
					fnr.setDestination(e.getAddress());
					rpcCall(fnr);


					if(canDoRequest())
					{
						fnr = new FindNodeRequest(node.getOurID(),e.getID());
						fnr.setWant4(rpc.getDHT().getType() == DHTtype.IPV4_DHT || DHT.getDHT(DHTtype.IPV4_DHT).getNode().getNumEntriesInRoutingTable() < DHTConstants.BOOTSTRAP_IF_LESS_THAN_X_PEERS);
						fnr.setWant6(rpc.getDHT().getType() == DHTtype.IPV6_DHT || DHT.getDHT(DHTtype.IPV6_DHT).getNode().getNumEntriesInRoutingTable() < DHTConstants.BOOTSTRAP_IF_LESS_THAN_X_PEERS);
						fnr.setDestination(e.getAddress());
						rpcCall(fnr);						
					}
					
					synchronized (visited) {
						visited.add(e);
					}
				}
				// remove the entry from the todo list
			}
		}

		if (todo.size() == 0 && getNumOutstandingRequests() == 0
				&& !isFinished()) {
			done();
		} 
	}

	@Override
	void callFinished (RPCCallBase c, MessageBase rsp) {
		if (isFinished()) {
			return;
		}

		// check the response and see if it is a good one
		if (rsp.getMethod() == Method.FIND_NODE
				&& rsp.getType() == Type.RSP_MSG) {

			FindNodeResponse fnr = (FindNodeResponse) rsp;
			
			for (DHTtype type : DHTtype.values())
			{
				byte[] nodes = fnr.getNodes(type);
				if (nodes == null)
					continue;
				int nval = nodes.length / type.NODES_ENTRY_LENGTH;
				if (type == rpc.getDHT().getType())
				{
					synchronized (todo)
					{
						for (int i = 0; i < nval; i++)
						{
							// add node to todo list
							KBucketEntry e = PackUtil.UnpackBucketEntry(nodes, i * type.NODES_ENTRY_LENGTH, type);
							if (!e.getID().equals(node.getOurID()) && !todo.contains(e) && !visited.contains(e))
							{
								todo.add(e);
							}
						}
					}
				} else
				{
					System.out.println("found nodes6");
					for (int i = 0; i < nval; i++)
					{
						KBucketEntry e = PackUtil.UnpackBucketEntry(nodes, i * type.NODES_ENTRY_LENGTH, type);
						DHT.getDHT(type).addDHTNode(e.getAddress().getAddress().getHostAddress(), e.getAddress().getPort());
					}
				}
			}


		}
	}
	
	@Override
	boolean canDoRequest() {
		return getNumOutstandingRequests() < DHTConstants.MAX_CONCURRENT_REQUESTS * 5;
	}
	
	@Override
	void kill() {
		// do nothing to evade safeties
	}
	

	@Override
	void callTimeout (RPCCallBase c) {

	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.Task#start()
	 */
	@Override
	void start () {
		int added = 0;

		// delay the filling of the todo list until we actually start the task
		
		KBucket[] buckets = node.getBuckets();
		outer: for (int i = buckets.length -1; i >= 1; i--)
			if (buckets[i] != null)
				for (KBucketEntry e : buckets[i].getEntries())
					if (!e.isBad())
					{
						todo.add(e);
						added++;
					}

		super.start();
	}

	@Override
	protected void done () {
		super.done();
		System.out.println("crawled "+visited.size()+" nodes");
	}
}
