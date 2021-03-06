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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import randy.BaseDCN;
import randy.ConstantManager;
import randy.ISimulator;
import randy.UFixSimulator;
import randy.XMLDCNBuilder;
import randy.DCNs.ufix.GeneralLinkCounter;
import randy.DCNs.ufix.ILinkConnector;
import randy.DCNs.ufix.ILinkCounter;
import randy.DCNs.ufix.IProxySelector;
import randy.DCNs.ufix.InterleavingConnector;
import randy.DCNs.ufix.ShuffleProxySelector;
import randy.components.Flow;
import randy.components.Link;
import randy.components.Node;

/**
 * UFIx architecture
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 10:53:00 AM
 */
public class UFix extends BaseDCN {

	private int intraFails = 0, interFails = 0;
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
	private final List<Node> proxyServers = new LinkedList<Node>();
	/**
	 * Whethe the function preRouteCalculation has been executed
	 */
	private boolean preRoute = false;
	List<UFixDomain> domains = new ArrayList<UFix.UFixDomain>(10);
	public UFix(double connectDegree, BaseDCN... dcnList){
		for (BaseDCN dcn : dcnList){
			this.domains.add(new UFixDomain(dcn, connectDegree,
					new ShuffleProxySelector()));
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

	public UFix(double connectDegree, List<BaseDCN> dcnList) {
		for (BaseDCN dcn : dcnList) {
			this.domains.add(new UFixDomain(dcn, connectDegree,
					new ShuffleProxySelector()));
			for (Node server : dcn.getServers()) {
				this.addServer(server);
			}
			for (Node sw : dcn.getSwitches()) {
				this.addSwitch(sw);
			}
			this.links.addAll(dcn.getLinks());
		}
		this.linkCount = new int[dcnList.size()][];
		for (int i = 0; i < dcnList.size(); i++) {
			this.linkCount[i] = new int[dcnList.size()];
			Arrays.fill(this.linkCount[i], 0);
		}
		ILinkCounter counter = new GeneralLinkCounter();
		counter.count(this);
		ILinkConnector connector = new InterleavingConnector();
		connector.connect(this);
	}

	public static UFix fromXMLElement(Element ele) {
		NodeList paramNodeList = ele.getElementsByTagName("param");
		double connectDegree = -1;
		List<BaseDCN> dcnList = new LinkedList<BaseDCN>();
		for (int i = 0; i < paramNodeList.getLength(); i++){
			org.w3c.dom.Node paramNode = paramNodeList.item(i);
			String paramName = ((Element)paramNode).getAttribute("name");
			String paramValue = ((Element)paramNode).getAttribute("value");
			if (!paramName.equals("")){
				if (paramName.equals("degree")){
					connectDegree = Double.parseDouble(paramValue);
				}
				if (paramName.equals("uFixCells")){

					org.w3c.dom.Node child = paramNode.getFirstChild();
					if (child == null){
						System.err.println("UFix should contain at least one DCN");
						return null;
					}
					while (child != null){
						BaseDCN dcn = XMLDCNBuilder
								.fromXMLElement((Element) child);
						if (dcn != null) {
							dcnList.add(dcn);
						}
						child = child.getNextSibling();
					}
				}
			}
		}
		return new UFix(connectDegree, dcnList);
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
		// System.out
		// .println("[UFix.interConnectProxy]: connect " + domain1 + " "
		// + head.getUuid().toString() + " - " + domain2 + " "
		// + tail.getUuid().toString());
		Link l = new Link(bandwidth, head, tail);
		this.links.add(l);
		this.addInterLink(domain1, domain2, l);
		this.proxyServers.add(head);
		this.proxyServers.add(tail);
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
	}

	public void addLinkCount(int domain1, int domain2, int count) {
		this.linkCount[domain1][domain2] += count;
	}

	public List<Node> proxyServer() {
		return this.proxyServers;
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
		// remove failed link
		for (List<Link> list : this.interLinks.values()) {
			List<Link> removeList = new LinkedList<Link>();
			for (Link l : list) {
				if (l.isFailed()) {
					removeList.add(l);
				}
			}
			for (Link l : removeList) {
				list.remove(l);
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

	private List<Link> getInterLinks(int domain1, int domain2) {
		assert domain1 >= 0 && domain2 >= 0;
		int d1 = Math.min(domain1, domain2);
		int d2 = Math.max(domain2, domain1);
		int code = this.domainPairCode(d1, d2);
		List<Link> list = this.interLinks.get(code);
		assert list != null : "domain pair " + d1 + " : " + d2
				+ " is not found";
		return list;
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

	/**
	 * Get vaild interFlows make sure that the flow's source is in domain1 and
	 * flow's target is in domain2
	 * 
	 * @param domain1
	 * @param domain2
	 * @return
	 * @author Hongze Zhao
	 */
	private List<Flow> getInterFlows(int domain1, int domain2) {
		List<Link> links = this.getInterLinks(domain1, domain2);
		List<Flow> output = new LinkedList<Flow>();
		// System.out.println("[UFix.getInterFlows]: into");
		for (Link l : links) {
			Node source = null, target = null;
			if (l == null) {
				return null;
			} else {
				if (this.domains.get(domain1).dcn.containNode(l.getHead()
						.getUuid())) {// links' head is in domain1
					source = l.getHead();
					target = l.getTail();

				} else {
					source = l.getTail();
					target = l.getHead();
				}
			}
			Flow flow = new Flow(source, target);
			// System.out.println("[UFIx.getInterFlows]: source "
			// + source.getUuid() + " target " + target.getUuid());
			flow.addLink(l);
			output.add(flow);
		}
		// System.out.println("[UFix.getInterFlows]: exit");
		return output;
	}

	/**
	 * Used to display the result of inter-link selection result
	 * 
	 * @author Hongze Zhao Create At : Feb 19, 2012 11:39:21 PM
	 */
	private class InterRoutePair {
		private final Link interLink;
		private final Flow headFlow, tailFlow;

		public InterRoutePair(Link interLink, Flow headFlow, Flow tailFlow) {
			this.interLink = interLink;
			this.headFlow = headFlow;
			this.tailFlow = tailFlow;
		}

		public Link getInterLink() {
			return this.interLink;
		}

		public Flow getHeadFlow() {
			return this.headFlow;
		}

		public Flow getTailFlow() {
			return this.tailFlow;
		}

	}

	/**
	 * select proper inter-link and result the generated flow before and after
	 * the inter-link and return the flow
	 * 
	 * @param source
	 * @param target
	 * @param sourceDomain
	 * @param targetDomain
	 * @return
	 * @author Hongze Zhao
	 */
	private Flow getValidFlow(Node source, Node target, int sourceDomain,
			int targetDomain) {
		List<Flow> output = new LinkedList<Flow>();
		List<Flow> flows = this.getInterFlows(sourceDomain, targetDomain);
		if (flows.isEmpty()) {
			this.interFails++;
			System.out.println("inter : " + this.interFails + " intra : "
					+ this.intraFails);
			return null;
		}
		for (Flow f : flows) {
			RouteResult result1 = this.domains.get(sourceDomain).dcn.route(
					source.getUuid(), f.getSource()
					.getUuid());
			if (!result1.isSuccessful()) {
				this.intraFails++;
				System.out.println("inter : " + this.interFails + " intra : "
						+ this.intraFails);
				continue;
			}
			RouteResult result2 = this.domains.get(targetDomain).dcn.route(f
					.getTarget().getUuid(),
					target.getUuid());
			if (!result2.isSuccessful()) {
				this.intraFails++;
				System.out.println("inter : " + this.interFails + " intra : "
						+ this.intraFails);
				continue;
			}
			Flow flow = result1.getFlow();
			flow.connect(f);
			flow.connect(result2.getFlow());
			output.add(flow);
		}
		if (output.isEmpty()) {
			return null;
		} else {
			// System.out.println("[UFix.getValidFlow]: Candidate flows "
			// + output.size());

			return output.get(ConstantManager.ran.nextInt(output.size()));
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
		// System.out.println("[UFix.route]: sourceDomainID : " + sourceDomainID
		// + " targetDomainID : " + targetDomainID);
		if (sourceDomainID == targetDomainID) {
			// System.out.println(1);
			return sourceDCN.route(sourceUUID,
					targetUUID);
		} else {
			Flow flow = this.getValidFlow(source, target, sourceDomainID,
					targetDomainID);
			if (flow == null) {
				return new RouteResult(source, target);
			}
			assert flow.isValid();
			assert flow.getSource().equals(source)
			&& flow.getTarget().equals(target);
			return new RouteResult(flow, source, target);
		}
	}

	private static UFix uFix1() {
		int fatTreeSize = 4;
		double interRatio = 0.8;
		double outerRatio = 0.8;
		UFix uFixCell1 = new UFix(interRatio, new FatTree(fatTreeSize),
				new FatTree(fatTreeSize), new FatTree(fatTreeSize));
		UFix uFixCell2 = new UFix(interRatio, new FatTree(fatTreeSize),
				new FatTree(fatTreeSize), new FatTree(fatTreeSize));
		UFix uFixCell3 = new UFix(interRatio, new FatTree(fatTreeSize),
				new FatTree(fatTreeSize), new FatTree(fatTreeSize));
		return new UFix(outerRatio, uFixCell1, uFixCell2, uFixCell3);
	}

	private static UFix uFix2() {
		int fatTreeSize = 4;
		int bCubeSize = 4;
		int dCellSize = 4;
		UFix uFixCell1 = new UFix(0.8, new FatTree(fatTreeSize),
				new FatTree(fatTreeSize), new FatTree(fatTreeSize));
		UFix uFixCell2 = new UFix(
				0.8, new BCube(bCubeSize, 1), new BCube(
						bCubeSize, 1), new BCube(bCubeSize, 1));
		UFix uFixCell3 = new UFix(0.8, new DCell(dCellSize, 1), new DCell(
				dCellSize, 1),
				new DCell(dCellSize, 1));
		return new UFix(0.8, uFixCell1, uFixCell2, uFixCell3);
	}

	private static UFix uFix3(double interRatio, double outerRatio) {
		int fatTreeSize = 4;
		int bCubeSize = 4;
		int dCellSize = 4;
		UFix uFixCell1 = new UFix(interRatio, new FatTree(fatTreeSize),
				new BCube(
						bCubeSize, 1), new DCell(dCellSize, 1));
		UFix uFixCell2 = new UFix(interRatio, new FatTree(fatTreeSize),
				new BCube(
						bCubeSize, 1), new DCell(dCellSize, 1));
		UFix uFixCell3 = new UFix(interRatio, new FatTree(fatTreeSize),
				new BCube(
						bCubeSize, 1), new DCell(dCellSize, 1));
		return new UFix(outerRatio, uFixCell1, uFixCell2, uFixCell3);
	}

	private static UFix uFix4() {
		double ratio = 0.8;
		int fatTreeSize = 4;
		return new UFix(ratio, new FatTree(fatTreeSize), new FatTree(
				fatTreeSize), new FatTree(fatTreeSize));
	}
	private static UFix testUFix() {
		return UFix.uFix1();
	}
	/**
	 * @param args
	 * @author Hongze Zhao
	 */
	public static void main(String[] args) {
		ISimulator sim = new UFixSimulator(UFix.uFix1());
		sim.initialize();
		sim.run();
		try {
			System.out
			.println(String
					.format("ABT %1f \nThroughput per Port %2f\nAGT %3f\nServer Num %4f",
							sim.getMetric("ABT"),
							sim.getMetric("ThroughputPerLink"),
							sim.getMetric("AGT"),
							sim.getMetric("ServerNum")));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		// int repeatTimes = 10;
		// for (double interRatio = 0.8; interRatio <= 0.81; interRatio += 0.2)
		// {
		// for (double outerRatio = 0.8; outerRatio <= 0.81; outerRatio += 0.2)
		// {
		// double sum = 0;
		// for (int i = 0; i < repeatTimes; i++) {
		// ISimulator sim = new UFixSimulator(UFix.uFix3(interRatio,
		// outerRatio));
		// sim.initialize();
		// sim.run();
		// try {
		// // System.out
		// // .println(String
		// //
		// .format("ABT %1f \nThroughput per Port %2f\nAGT %3f\nServer Num %4f",
		// // sim.getMetric("ABT"),
		// // sim.getMetric("ThroughputPerLink"),
		// // sim.getMetric("AGT"), sim
		// // .getMetric("ServerNum")));
		// sum += sim.getMetric("ABT");
		// } catch (Exception ex) {
		// System.out.println(ex.getMessage());
		// }
		// }
		// System.out.println("interRatio " + interRatio + " outerRatio "
		// + outerRatio + " : " + sum / repeatTimes);
		// }
		// }
		// ISimulator sim = new UFixSimulator(UFix.);
		// sim.initialize();
		// sim.run();
		// try {
		// System.out.println(String.format(
		// "ABT %1f \nThroughput per Port %2f\nAGT %3f\nServer Num %4f",
		// sim.getMetric("ABT"), sim.getMetric("ThroughputPerLink"),
		// sim.getMetric("AGT"),
		// sim.getMetric("ServerNum")));
		// } catch (Exception ex) {
		// System.out.println(ex.getMessage());
		// }

		// ISimulator sim = new FailureSimulator(0, 0, 0,
		// new UFix(0.9, new UFix(0.9, new BCube(4, 1), new FatTree(4),
		// new DCell(4, 1)), new UFix(0.5, new BCube(4, 1),
		// new FatTree(4), new DCell(4, 1)), new UFix(0.5,
		// new BCube(4, 1), new FatTree(4), new DCell(4, 1))));
		// sim.initialize();
		// sim.run();
		// try {
		// System.out.println(String.format(
		// "ABT %1f \nThroughput per Port %2f\nAGT %3f",
		// sim.getMetric("ABT"), sim.getMetric("ThroughputPerLink"),
		// sim.getMetric("AGT")));
		// } catch (Exception ex) {
		// System.out.println(ex.getMessage());
		// }

		// ISimulator sim = new OneToOneSimulator(
		// new UFix(0.9, new UFix(0.9, new BCube(4, 1), new FatTree(4),
		// new DCell(4, 1)), new UFix(0.5, new BCube(4, 1),
		// new FatTree(4), new DCell(4, 1)), new UFix(0.5,
		// new BCube(4, 1), new FatTree(4), new DCell(4, 1))));
		// sim.initialize();
		// sim.run();
		// try {
		// System.out.println(String.format(
		// "ABT %1f \n Throughput per Port %2f\n",
		// sim.getMetric("ABT"), sim.getMetric("ThroughputPerLink")));
		// } catch (Exception ex) {
		// System.out.println(ex.getMessage());
		// }
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		for (UFixDomain domain : this.domains) {
			domain.dcn.reset();
		}
	}

}
