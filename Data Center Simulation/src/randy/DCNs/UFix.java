/**  
* Filename:    UFix.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 10:53:00 AM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import randy.BaseDCN;
import randy.components.Node;

/**
 * UFIx architecture
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 10:53:00 AM
 */
public class UFix extends BaseDCN {

	/**
	 * Used to abstract a domain of UFix
	 * 
	 * @author Hongze Zhao Create At : Feb 19, 2012 11:05:18 AM
	 */
	public class UFixDomain {

		private final BaseDCN dcn;
		private final double connectDegree;
		private final int u;
		
		/**
		 * Available proxy server in the domain Note when the dcn is an
		 * instanceof UFix, excludes the proxy server in dcn from the proxy
		 * server in this domain.
		 */
		private HashSet<Node> availableProxyServer;
		public UFixDomain(BaseDCN dcn, double connectDegree) {
			this.dcn = dcn;
			this.connectDegree = connectDegree;
			this.processAvailableProxyServer();
			this.u = (int) (this.connectDegree * this.getX());
		}

		/**
		 * Calculate all the available proxy server Exclude the proxy server in
		 * dcn when dcn is an instance of Ufix As a server can only be a proxy
		 * server at most once
		 * 
		 * @author Hongze Zhao
		 */
		private void processAvailableProxyServer(){
			this.availableProxyServer.addAll(this.dcn.getServers());
			if (this.dcn instanceof UFix) {
				UFix ufix = (UFix) this.dcn;
				for (Node n : ufix.proxyServer()) {
					if (this.availableProxyServer.contains(n)) {
						this.availableProxyServer.remove(n);
					}
				}
			}
		}

		/**
		 * get x value in paper: number of available uFix proxy server in the
		 * domain Assume all the server can be used as uFix proxy server
		 * 
		 * @return
		 * @author Hongze Zhao
		 */
		public int getX() {
			return this.availableProxyServer.size();
		}

		/**
		 * get u value in paper: number of available uFix proxy servers planned
		 * to used in this level
		 * 
		 * @author Hongze Zhao
		 */
		public int getU() {
			return this.u;
		}
	}
	
	List<UFixDomain> domains = new ArrayList<UFix.UFixDomain>(10);
	public UFix(double connectDegree, BaseDCN... dcnList){
		for (BaseDCN dcn : dcnList){
			this.domains.add(new UFixDomain(dcn, connectDegree));
		}
		// initial linkCount
		this.linkCount = new int[dcnList.length][];
		for (int i = 0; i < dcnList.length; i++) {
			this.linkCount[i] = new int[dcnList.length];
			for (int j = 0; j < dcnList.length; j++) {
				this.linkCount[i][j] = 0;
			}
		}
		//TODO:
	}
	
	/**
	 * count of links between any pair of UFix domains
	 * the e_{ij} valud in paper
	 */
	private final int[][] linkCount;

	/**
	 * Add link count between domain1 and domain2
	 * 
	 * @param domain1
	 * @param domain2
	 * @author Hongze Zhao
	 */
	public void addLinkCount(int domain1, int domain2) {
		this.linkCount[domain1][domain2]++;
		this.linkCount[domain2][domain1]++;
	}
	
	public void addLinkCount(int domain1, int domain2, int count) {
		this.linkCount[domain1][domain2] += count;
		this.linkCount[domain2][domain1] += count;
	}

	public List<Node> proxyServer() {
		// TODO:
		return null;
	}
	
	public List<UFixDomain> getDomains() {
		return this.domains;
	}

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

	}
}
