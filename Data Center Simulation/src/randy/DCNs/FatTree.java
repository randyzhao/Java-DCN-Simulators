/**  
* Filename:    FatTree.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 12, 2012 8:24:01 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 12, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import randy.BaseDCN;
import randy.ConstantManager;
import randy.components.IPAddr;
import randy.components.Node;

/**
 *
 * @author Hongze Zhao
 * Create At : Feb 12, 2012 8:24:01 PM
 */
public class FatTree extends BaseDCN {

	/**
	 * k value in the paper k means the pods number in BCube
	 */
	private final int k;
	/**
	 * Core switch[i, j] connects all pods' i th agge switchs' j th ports
	 */
	private final HashMap<IPAddr, Node> coreSwitches = new HashMap<IPAddr, Node>();
	private final HashMap<IPAddr, Node> aggeSwitches = new HashMap<IPAddr, Node>();
	private final HashMap<IPAddr, Node> edgeSwitches = new HashMap<IPAddr, Node>();

	public FatTree(int k) {
		this.k = k;
		if (k % 2 != 0) {
			System.out.println("k should be an even number");
			System.exit(1);
		}
		this.addComponents(k);
		Collection<Node> cores = this.coreSwitches.values();
		Iterator<Node> corei = cores.iterator();
		while (corei.hasNext()) {
			System.out.println(corei.next().getAddr().toString());
		}
		this.connectAggeAndCore();
		this.connectEdgeAndAgge();
		this.connectServerAndEdge();
	}

	/**
	 * Add core, agge, edge switches and servers to instance, called in
	 * construction function
	 * 
	 * @param k
	 *            the k value in the paper
	 * @author Hongze Zhao
	 */
	private void addComponents(int k) {
		// add core switches
		for (int i = 0; i < k / 2; i++) {
			for (int j = 0; j < k / 2; j++) {
				IPAddr addr = new IPAddr(new Integer[] { i, j });
				Node core = new Node("core switches");
				core.setAddr(addr);
				this.coreSwitches.put(addr, core);
				this.addSwitch(core);
			}
		}

		// add agge switches
		for (int i = 0; i < k / 2; i++) {
			for (int j = 0; j < k / 2; j++) {
				IPAddr addr = new IPAddr(new Integer[] { i, j });
				Node core = new Node("agge switches");
				core.setAddr(addr);
				this.aggeSwitches.put(addr, core);
				this.addSwitch(core);
			}
		}

		// add edge switches
		for (int i = 0; i < k / 2; i++) {
			for (int j = 0; j < k / 2; j++) {
				IPAddr addr = new IPAddr(new Integer[] { i, j });
				Node core = new Node("edge switches");
				core.setAddr(addr);
				this.edgeSwitches.put(addr, core);
				this.addSwitch(core);
			}
		}

		// add servers
		for (int i = 0; i < k / 2; i++) {
			for (int j = 0; j < k / 2; j++) {
				for (int l = 0; l < k / 2; l++) {
					IPAddr addr = new IPAddr(new Integer[] { i, j, l });
					Node server = new Node("server");
					server.setAddr(addr);
					this.addServer(server);
				}
			}
				}
	}

	/**
	 * Connect servers and edge switches
	 * 
	 * @author Hongze Zhao
	 */
	private void connectServerAndEdge() {
		for (int pod = 0; pod < this.k; pod++) {
			for (int edgev = 0; edgev < this.k / 2; edgev++) {// edge switch
				Node edge = this.getEdgeSwitch(new IPAddr(new Integer[] { pod,
						edgev }));
				assert edge != null;
				for (int serverv = 0; serverv < this.k / 2; serverv++) {
					Node server = this.getServer(new IPAddr(new Integer[] {
							pod, edgev, serverv }));
					assert server != null;
					this.connectNode(edge, server,
							ConstantManager.LINK_BANDWIDTH);
				}
			}
		}
	}

	/**
	 * connect edge switches and agge switches
	 * 
	 * @author Hongze Zhao
	 */
	private void connectEdgeAndAgge() {
		for (int pod = 0; pod < this.k; pod++){
			for (int aggev = 0; aggev < this.k / 2; aggev++){
				Node agge = this.getAggeSwitch(new IPAddr(new Integer[] { pod,
						aggev }));
				assert agge != null;
				for (int edgev = 0; edgev < this.k / 2; edgev++){
					Node edge = this.getEdgeSwitch(new IPAddr(new Integer[] {
							pod, edgev }));
					assert edge != null;
					this.connectNode(agge, edge, ConstantManager.LINK_BANDWIDTH);
				}
			}
		}
	}

	/**
	 * connect agge switches and core switches Note core[i, j] connects all
	 * pods' i th agge switches' j th ports
	 * 
	 * @author Hongze Zhao
	 */
	private void connectAggeAndCore() {
		for (int i = 0; i < this.k / 2; i++) {
			for (int j = 0; j < this.k / 2; j++) {
				Node core = this.getCoreSwitch(new IPAddr(
						new Integer[] { i, j }));
				assert core != null : "get core[" + i + " , " + j + "] fails";
				for (int pod = 0; pod < this.k / 2; pod++) {
					Node agge = this.getAggeSwitch(new IPAddr(new Integer[] {
							pod, i }));
					assert agge != null;
					this.connectNode(core, agge, ConstantManager.LINK_BANDWIDTH);
				}
			}
		}
	}

	/**
	 * Get edge switch
	 * 
	 * @param addr
	 *            the IPAddr of edge switches
	 * @return
	 * @author Hongze Zhao
	 */
	private Node getEdgeSwitch(IPAddr addr) {
		return this.edgeSwitches.get(addr);
	}

	/**
	 * Get agge switch
	 * 
	 * @param addr
	 * @return
	 * @author Hongze Zhao
	 */
	private Node getAggeSwitch(IPAddr addr) {
		return this.aggeSwitches.get(addr);
	}

	/**
	 * Get core switch
	 * 
	 * @param addr
	 * @return
	 * @author Hongze Zhao
	 */
	private Node getCoreSwitch(IPAddr addr) {
		return this.coreSwitches.get(addr);
	}
	/* (non-Javadoc)
	 * @see randy.BaseDCN#route(java.util.UUID, java.util.UUID)
	 */

	@Override
	public RouteResult route(UUID sourceUUID, UUID targetUUID) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 * @author Hongze Zhao	
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FatTree f = new FatTree(8);
		assert false : "the end\n";
	}

}
