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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import randy.BaseDCN;
import randy.ConstantManager;
import randy.DCNs.ufix.GeneralLinkCounter;
import randy.DCNs.ufix.HopsFirstProxySelector;
import randy.DCNs.ufix.ILinkConnector;
import randy.DCNs.ufix.ILinkCounter;
import randy.DCNs.ufix.IProxySelector;
import randy.DCNs.ufix.InterleavingConnector;
import randy.components.Flow;
import randy.components.Link;
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
		private final HashSet<Node> availableProxyServer = new HashSet<Node>();
		/**
		 * proxy server which is planned to use in the connection of this level
		 */
		private final List<Node> planToUseProxyServer = new ArrayList<Node>(
				this.u);

		public UFixDomain(BaseDCN dcn, double connectDegree,
				IProxySelector selector) {
			this.dcn = dcn;
			this.connectDegree = connectDegree;
			this.processAvailableProxyServer();
			this.u = (int) (this.connectDegree * this.getX());
			selector.select(this);
		}

		public List<Node> getServers() {
			return this.dcn.getServers();
		}

		public BaseDCN getDCN() {
			return this.dcn;
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

		public HashSet<Node> getAvailableProxyServers() {
			return this.availableProxyServer;
		}

		public Node getPlannedToUseProxyServer(int i) {
			return this.planToUseProxyServer.get(i);
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

		public void addPlanToUseProxyServer(Node server) {
			assert this.availableProxyServer.contains(server);
			this.planToUseProxyServer.add(server);
			assert this.planToUseProxyServer.size() <= this.u;
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


	public class DomainPair {
		private final int domain1;
		private final int domain2;

		public DomainPair(int domain1, int domain2) {
			this.domain1 = Math.min(domain1, domain2);
			this.domain2 = Math.max(domain1, domain2);
		}
		@Override
		public int hashCode() {

			System.out.println("hashcode for (" + this.domain1 + " , "
					+ this.domain2 + ") = "
					+ (this.domain1 + this.domain2 * 200));
			return this.domain1 + this.domain2 * 200;
		}

	}
	/**
	 * interlinks of domains
	 */
	private final HashMap<Integer, List<Link>> interLinks = new HashMap<Integer, List<Link>>();
	private final int[][] linkCount;
	/**
	 * Whethe the function preRouteCalculation has been executed
	 */
	private boolean preRoute = false;
	List<UFixDomain> domains = new ArrayList<UFix.UFixDomain>(10);
	public UFix(double connectDegree, BaseDCN... dcnList){
		for (BaseDCN dcn : dcnList){
			this.domains.add(new UFixDomain(dcn, connectDegree,
					new HopsFirstProxySelector()));
			for (Node server : dcn.getServers()) {
				this.addServer(server);
			}
			for (Node sw : dcn.getSwitches()) {
				this.addSwitch(sw);
			}
			this.links.addAll(dcn.getLinks());
		}
		this.linkCount = new int[dcnList.length][];
		for (int i = 0; i < dcnList.length; i++) {
			this.linkCount[i] = new int[dcnList.length];
			Arrays.fill(this.linkCount[i], 0);
		}
		ILinkCounter counter = new GeneralLinkCounter();
		counter.count(this);
		ILinkConnector connector = new InterleavingConnector();
		connector.connect(this);
	}
	
	/**
	 * Add a link into interLinks
	 * 
	 * @param domain1
	 * @param domain2
	 * @param l
	 * @author Hongze Zhao
	 */
	private void addInterLink(int domain1, int domain2, Link l) {
		assert domain1 != domain2;
		int d1 = Math.min(domain1, domain2);
		int d2 = Math.max(domain1, domain2);
		int code = this.domainPairCode(d1, d2);
		if (!this.interLinks.containsKey(code)) {
			this.interLinks.put(code, new LinkedList<Link>());
			assert this.interLinks.containsKey(code);
		}
		this.interLinks.get(code).add(l);
	}

	/**
	 * the hash code for domain pair used in this.interLinks
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 * @author Hongze Zhao
	 */
	private int domainPairCode(int d1, int d2) {
		return d1 + d2 * 200;
	}

	/**
	 * Interconnect two proxy servers Not to add new link to the node, or the
	 * intra-domain routing will be violated somehow
	 * 
	 * @param source
	 * @param target
	 * @author Hongze Zhao
	 */
	public void interConnectProxy(int domain1, int domain2, Node head,
			Node tail, double bandwidth) {
		Link l = new Link(bandwidth, head, tail);
		this.links.add(l);
		this.addInterLink(domain1, domain2, l);
	}

	/**
	 * count of links between any pair of UFix domains the e_{ij} value in paper
	 */


	public int[][] getLinkCount() {
		return this.linkCount;
	}
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

	/**
	 * Some precedure must be executed before the route to accelerate it
	 * 
	 * @author Hongze Zhao
	 */
	private void preRouteCalculation() {
		if (this.preRoute) {
			return;
		}
		// remove failed interlink
		for (List<Link> list : this.interLinks.values()) {
			for (Link l : list) {
				if (l.isFailed()) {
					list.remove(l);
				}
			}
		}

		this.preRoute = true;
	}

	/**
	 * Get interLink of two flow
	 * 
	 * @param domain1
	 * @param domain2
	 * @return
	 * @author Hongze Zhao
	 */
	private Link getInterLink(int domain1, int domain2) {
		assert domain1 >= 0 && domain2 >= 0;
		int d1 = Math.min(domain1, domain2);
		int d2 = Math.max(domain2, domain1);
		int code = this.domainPairCode(d1, d2);
		List<Link> list = this.interLinks.get(code);
		if (list == null) {
			int a = 1;
		}
		assert list != null : "domain pair " + d1 + " : " + d2
				+ " is not found";
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(ConstantManager.ran.nextInt(list.size()));
		}
	}

	/**
	 * Get a flow containing interlink of two domains and make sure that the
	 * flow's source is in domain1 and flow's target is in domain2
	 * 
	 * @param domain1
	 * @param domain2
	 * @return
	 * @author Hongze Zhao
	 */
	private Flow getInterFlow(int domain1, int domain2) {
		Link l = this.getInterLink(domain1, domain2);
		Node source = null, target = null;
		if (l == null) {
			return null;
		} else {
			if (this.domains.get(domain1).dcn
					.containNode(l.getHead().getUuid())) {// links' head is in
															// domain1
				source = l.getHead();
				target = l.getTail();

			} else {
				source = l.getTail();
				target = l.getHead();
			}
			Flow flow = new Flow(source, target);
			flow.addLink(l);
			return flow;
		}
	}

	@Override
	public RouteResult route(UUID sourceUUID, UUID targetUUID) {
		this.preRouteCalculation();
		int sourceDomainID = -1, targetDomainID = -1;
		for (int i = 0; i < this.domains.size(); i++){
			if (this.domains.get(i).getDCN().containNode(sourceUUID)){
				sourceDomainID = i;
			}
			if (this.domains.get(i).getDCN().containNode(targetUUID)) {
				targetDomainID = i;
			}
		}
		assert sourceDomainID != -1 && targetDomainID != -1;
		Node source = this.domains.get(sourceDomainID).dcn
				.getServer(sourceUUID);
		Node target = this.domains.get(targetDomainID).dcn
				.getServer(targetUUID);
		BaseDCN sourceDCN = this.domains.get(sourceDomainID).getDCN();
		BaseDCN targetDCN = this.domains.get(targetDomainID).getDCN();
		if (sourceDomainID == targetDomainID) {
			return sourceDCN.route(sourceUUID,
					targetUUID);
		} else {
			assert sourceDomainID >= 0 && targetDomainID >= 0;
			Flow interFlow = this.getInterFlow(sourceDomainID, targetDomainID);
			if (interFlow == null) {// no interFlow
				return new RouteResult(source, target);
			} else {
				Flow output;
				if (!sourceUUID.equals(interFlow.getSource().getUuid())) {
					RouteResult result1 = sourceDCN.route(sourceUUID, interFlow
							.getSource().getUuid());
					if (!result1.isSuccessful()) {
						return new RouteResult(source, target);
					}
					output = result1.getFlow();
				} else {
					output = new Flow(interFlow.getSource(),
							interFlow.getSource());
				}

				output.connect(interFlow);

				if (!interFlow.getTarget().getUuid().equals(targetUUID)) {
					RouteResult result2 = targetDCN.route(interFlow.getTarget()
							.getUuid(), targetUUID);
					if (!result2.isSuccessful()) {
						return new RouteResult(source, target);
					}
					output.connect(result2.getFlow());
				}
				return new RouteResult(output, source, target);
			}
		}
	}
	/**
	 * @param args
	 * @author Hongze Zhao
	 */
	public static void main(String[] args) {
		new UFix(0.5, new BCube(4, 1), new BCube(4, 1));
		assert false : "the end";
	}
}
