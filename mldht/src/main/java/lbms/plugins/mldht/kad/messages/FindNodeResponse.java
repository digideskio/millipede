package lbms.plugins.mldht.kad.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lbms.plugins.mldht.kad.DHT;
import lbms.plugins.mldht.kad.DHTConstants;
import lbms.plugins.mldht.kad.Key;
import lbms.plugins.mldht.kad.DHT.DHTtype;

import org.gudy.azureus2.core3.util.BEncoder;
/**
 * @author Damokles
 *
 */
public class FindNodeResponse extends MessageBase {

	protected byte[]	nodes;
	protected byte[]	nodes6;

	/**
	 * @param mtid
	 * @param id
	 * @param nodes
	 */
	public FindNodeResponse (byte[] mtid, Key id, byte[] nodes, byte[] nodes6) {
		super(mtid, Method.FIND_NODE, Type.RSP_MSG, id);
		this.nodes = nodes;
		this.nodes6 = nodes6;
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.messages.MessageBase#apply(lbms.plugins.mldht.kad.DHT)
	 */
	@Override
	public void apply (DHT dh_table) {
		dh_table.response(this);
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.messages.MessageBase#encode()
	 */
	@Override
	public byte[] encode () throws IOException {
		Map<String, Object> base = new HashMap<String, Object>();
		Map<String, Object> inner = new HashMap<String, Object>();
		base.put(DHTConstants.RSP, inner);
		inner.put("id", id.getHash());
		if(nodes != null)
			inner.put("nodes", nodes);
		if(nodes6 != null)
			inner.put("nodes6", nodes6);

		base.put(DHTConstants.TID, mtid);
		base.put(DHTConstants.TYP, DHTConstants.RSP);
		base.put(DHTConstants.VER, DHTConstants.getVersion());

		return BEncoder.encode(base);
                
	}
	
	public byte[] getNodes(DHTtype type)
	{
		if(type == DHTtype.IPV4_DHT)
			return nodes;
		if(type == DHTtype.IPV6_DHT)
			return nodes6;
		return null;
	}

	/**
	 * @return the nodes
	 */
	public byte[] getNodes () {
		return nodes;
	}
	
	/**
	 * @return the nodes
	 */
	public byte[] getNodes6 () {
		return nodes6;
	}
	
	public String toString() {
		return super.toString() + (nodes != null ? "contains: "+ (nodes.length/DHTtype.IPV4_DHT.NODES_ENTRY_LENGTH) + " nodes" : "") + (nodes6 != null ? "contains: "+ (nodes6.length/DHTtype.IPV6_DHT.NODES_ENTRY_LENGTH) + " nodes6" : "");
	}
}
