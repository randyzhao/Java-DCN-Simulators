/**  
* Filename:    BCube.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 12, 2012 8:21:11 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 12, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import randy.BaseDCN;
import randy.ConstantManager;
import randy.components.IPAddr;
import randy.components.Node;

/**
 * BCube
 * 
 * @author Hongze Zhao Create At : Feb 12, 2012 8:21:11 PM
 */
public class BCube extends BaseDCN {

	/**
	 * k value in the paper. The maxinum level of the BCube
	 */
	private final int k;
	/**
	 * n value in the paper the number of ports of the switch in the BCube
	 */
	private final int n;

	/**
	 * a cell of BCube This class is used in the construction function of class
	 * BCube
	 * 
	 * @author Hongze Zhao Create At : Feb 18, 2012 5:26:37 PM
	 */
	private class BCubeCell {
		private final List<Node> servers = new ArrayList<Node>();
		private final List<Node> switches = new ArrayList<Node>();

		/**
		 * k value in the paper. The maxinum level of the BCube
		 */
		private final int k;
		/**
		 * n value in the paper the number of ports of the switch in the BCube
		 */
		private final int n;

		/**
		 * 
		 * @param k
		 */
		public BCubeCell(int k, int n){
			this.k = k;
			this.n = n;
			if (k == 0){//building block
				Node sw = new Node("switch");
				this.switches.add(sw);
				for (int i = 0; i < n; i++){
					Node server = new Node("server");
					server.setAddr(new IPAddr(new Integer[]{i}));
					BCube.this.connectNode(sw, server, ConstantManager.LINK_BANDWIDTH);
					this.servers.add(server);
				}
			} else {// k != 0
				//not a building block
				List<BCubeCell> cells = new ArrayList<BCubeCell>(n);
				for (int i = 0; i < n; i++) {
					BCubeCell cell = new BCubeCell(this.k - 1, this.n);
					cell.addPrefix(i);
					this.servers.addAll(cell.getServers());
					cells.add(cell);
				}
				int cellServersCount = cells.get(0).getServers().size();
				for (int i = 0; i < cellServersCount; i++) {
					Node sw = new Node("switch");
					this.switches.add(sw);
					for (int j = 0; j < n; j++) {
						BCube.this.connectNode(sw, cells.get(j).getServer(i),
								ConstantManager.LINK_BANDWIDTH);
					}
				}
			}
		}

		public List<Node> getServers() {
			return this.servers;
		}

		public List<Node> getSwitches() {
			return this.switches;
		}
		/**
		 * Get the number of servers
		 * 
		 * @return
		 * @author Hongze Zhao
		 */
		public int serversCount() {
			return this.servers.size();
		}

		/**
		 * Add prefix to all its servers
		 * 
		 * @param prefix
		 * @author Hongze Zhao
		 */
		public void addPrefix(int prefix) {
			Iterator<Node> seri = this.servers.iterator();
			while (seri.hasNext()) {
				seri.next().getAddr().insertSegment2Head(prefix);
			}
		}

		/**
		 * Get the seq th server in the servers
		 * 
		 * @param seq
		 * @author Hongze Zhao
		 */
		public Node getServer(int seq) {
			return this.servers.get(seq);
		}
	}

	/**
	 * Use BCubeCell to construct BCube
	 * 
	 * @param k
	 * @param n
	 */
	public BCube(int k, int n) {
		BCubeCell cell = new BCubeCell(k, n);
		this.k = k;
		this.n = n;
		List<Node> servers = cell.getServers();
		List<Node> switches = cell.getSwitches();
		for (int i = 0; i < servers.size(); i++) {
			this.addServer(servers.get(i));
		}
		for (int i = 0; i < switches.size(); i++) {
			this.addSwitch(switches.get(i));
		}
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
		new BCube(2, 4);

	}

}
