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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import randy.BaseDCN;
import randy.ConstantManager;
import randy.FailureSimulator;
import randy.ISimulator;
import randy.components.Flow;
import randy.components.IPAddr;
import randy.components.Link;
import randy.components.Node;

/**
 * 
 * @author Hongze Zhao Create At : Feb 12, 2012 8:24:01 PM
 */
public class FatTree extends BaseDCN {

	/**
	 * k value in the paper k means the pods number in BCube
	 */
	private final int k;

	/**
	 * Whether the preRouteCalculation has done yet
	 */
	private boolean preRoute = false;

	/**
	 * Flows between agge switches under different pods Note only same sequence
	 * agge switches is connected by core switches
	 */
	public final List<List<Flow>> flowsBetweenAggeSwitches = new ArrayList<List<Flow>>();
	/**
	 * Flows between edge switches under different pods
	 */
	public final List<List<Flow>> flowsBetweenEdgeSwitchesDiffPod = new ArrayList<List<Flow>>();
	/**
	 * Core switch[i, j] connects all pods' i th agge switchs' j th ports
	 */
	private final HashMap<IPAddr, Node> coreSwitches = new HashMap<IPAddr, Node>();
	private final HashMap<IPAddr, Node> aggeSwitches = new HashMap<IPAddr, Node>();
	private final HashMap<IPAddr, Node> edgeSwitches = new HashMap<IPAddr, Node>();

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		this.preRoute = false;
		this.flowsBetweenAggeSwitches.clear();
		this.flowsBetweenEdgeSwitchesDiffPod.clear();
	}

	public FatTree(int k) {
		this.k = k;
		if (k % 2 != 0) {
			System.out.println("k should be an even number");
			System.exit(1);
		}
		this.addComponents();
		// Collection<Node> cores = this.coreSwitches.values();
		// Iterator<Node> corei = cores.iterator();
		// while (corei.hasNext()) {
		// System.out.println(corei.next().getAddr().toString());
		// }
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
	private void addComponents() {
		// add core switches
		for (int i = 0; i < this.k / 2; i++) {
			for (int j = 0; j < this.k / 2; j++) {
				IPAddr addr = new IPAddr(new Integer[] { i, j });
				Node core = new Node("core switches");
				core.setAddr(addr);
				this.coreSwitches.put(addr, core);
				this.addSwitch(core);
			}
		}

		// add agge switches
		for (int i = 0; i < this.k; i++) {
			for (int j = 0; j < this.k / 2; j++) {
				IPAddr addr = new IPAddr(new Integer[] { i, j });
				Node core = new Node("agge switches");
				core.setAddr(addr);
				this.aggeSwitches.put(addr, core);
				this.addSwitch(core);
			}
		}

		// add edge switches
		for (int i = 0; i < this.k; i++) {
			for (int j = 0; j < this.k / 2; j++) {
				IPAddr addr = new IPAddr(new Integer[] { i, j });
				Node core = new Node("edge switches");
				core.setAddr(addr);
				this.edgeSwitches.put(addr, core);
				this.addSwitch(core);
			}
		}

		// add servers
		for (int i = 0; i < this.k; i++) {
			for (int j = 0; j < this.k / 2; j++) {
				for (int l = 0; l < this.k / 2; l++) {
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
		for (int pod = 0; pod < this.k; pod++) {
			for (int aggev = 0; aggev < this.k / 2; aggev++) {
				Node agge = this.getAggeSwitch(new IPAddr(new Integer[] { pod,
						aggev }));
				assert agge != null : (new IPAddr(new Integer[] { pod, aggev }))
						.toString();
				for (int edgev = 0; edgev < this.k / 2; edgev++) {
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
				for (int pod = 0; pod < this.k; pod++) {
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

	/**
	 * Get all valid flows between two agge switches under different pods
	 * 
	 * @param pod1
	 * @param pod2
	 * @param seq
	 * @return
	 * @author Hongze Zhao
	 */
	private List<Flow> getFlowsBetweenAggeSwitches(int pod1, int pod2, int seq) {
		assert pod1 != pod2;
		assert seq < this.k / 2;
		return this.flowsBetweenAggeSwitches.get(pod1 * this.k * this.k / 2
				+ pod2 * this.k / 2 + seq);
	}

	/**
	 * Get flows between edge switches under different pods
	 * 
	 * @param pod1
	 * @param pod2
	 * @param seq
	 * @return
	 * @author Hongze Zhao
	 */
	private List<Flow> getFLowsBetweenEdgeSwitchesDiffPod(int pod1, int pod2,
			int seq1, int seq2) {
		return this.flowsBetweenEdgeSwitchesDiffPod.get(pod1 * this.k * this.k
				/ 2 * this.k / 2 + pod2 * this.k / 2 * this.k / 2 + seq1
				* this.k / 2 + seq2);
	}

	/**
	 * Doing neccessary calculation before route. Includes calculate valid flows
	 * between agge switches under different pods calculate valid flows between
	 * edge switches under same and different pods
	 * 
	 * @author Hongze Zhao
	 */
	public void preRouteCalculation() {
		// System.out.println("fattree: pre routing");
		// valid flows between agge switches under different pods
		for (int pod1 = 0; pod1 < this.k; pod1++) {
			for (int pod2 = 0; pod2 < this.k; pod2++) {
				for (int i = 0; i < this.k / 2; i++) {
					if (pod1 == pod2) {
						this.flowsBetweenAggeSwitches
								.add(new ArrayList<Flow>());
						continue;
					}
					if (pod1 > pod2) {
						// reverse all the flows
						List<Flow> f1 = this.getFlowsBetweenAggeSwitches(pod2,
								pod1, i);
						List<Flow> flows = new ArrayList<Flow>();
						for (int j = 0; j < f1.size(); j++) {
							flows.add(f1.get(j).reverseAndCopy());
						}
						this.flowsBetweenAggeSwitches.add(flows);
						continue;
					}
					List<Flow> flows = new ArrayList<Flow>();
					Node agge1 = this.getAggeSwitch(new IPAddr(new Integer[] {
							pod1, i }));
					Node agge2 = this.getAggeSwitch(new IPAddr(new Integer[] {
							pod2, i }));
					for (int j = 0; j < this.k / 2; j++) {
						Link l1 = agge1.getLinks().get(j);
						Link l2 = agge2.getLinks().get(j);
						if (!l1.isFailed() && !l2.isFailed()) {
							Flow flow = new Flow(agge1, agge2);
							flow.addLink(l1);
							flow.addLink(l2);
							flows.add(flow);
						}
					}
					this.flowsBetweenAggeSwitches.add(flows);
				}
			}
		}
		// valid flows between edge switches under different pods
		for (int pod1 = 0; pod1 < this.k; pod1++) {
			for (int pod2 = 0; pod2 < this.k; pod2++) {
				for (int i = 0; i < this.k / 2; i++) {
					for (int j = 0; j < this.k / 2; j++) {
						if (pod1 == pod2) {
							this.flowsBetweenEdgeSwitchesDiffPod
									.add(new ArrayList<Flow>());
						} else {
							// pod1 < pod2
							Node edge1 = this.getEdgeSwitch(new IPAddr(
									new Integer[] { pod1, i }));
							Node edge2 = this.getEdgeSwitch(new IPAddr(
									new Integer[] { pod2, j }));
							List<Flow> flows = new ArrayList<Flow>();
							for (int l = 0; l < this.k / 2; l++) {
								// the sequence number of agge switches
								List<Flow> tempFlows = new ArrayList<Flow>();
								Node agge1 = this.getAggeSwitch(new IPAddr(
										new Integer[] { pod1, l }));
								Node agge2 = this.getAggeSwitch(new IPAddr(
										new Integer[] { pod2, l }));
								Link l1 = edge1.getLinks().get(l);
								Link l2 = edge2.getLinks().get(l);
								if (!l1.isFailed() && !l2.isFailed()) {
									Flow f1 = new Flow(edge1, agge1);
									f1.addLink(l1);
									Flow f2 = new Flow(agge2, edge2);
									f2.addLink(l2);
									List<Flow> mediaFlows = this
											.getFlowsBetweenAggeSwitches(pod1,
													pod2, l);
									for (Flow flow : mediaFlows) {
										Flow temp = new Flow(f1);
										temp.connect(flow);
										temp.connect(f2);
										assert temp.isValid();
										tempFlows.add(temp);
									}
								}
								flows.addAll(tempFlows);
							}
							this.flowsBetweenEdgeSwitchesDiffPod.add(flows);
						}
					}
				}
			}
		}
		this.preRoute = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see randy.BaseDCN#route(java.util.UUID, java.util.UUID)
	 */
	@Override
	public RouteResult route(UUID sourceUUID, UUID targetUUID) {
		// System.out.println("into fattree route");
		if (!this.preRoute) {
			this.preRouteCalculation();
		}
		Node source = this.getServer(sourceUUID);
		Node target = this.getServer(targetUUID);
		if (sourceUUID.equals(targetUUID)) {
			return new RouteResult(new Flow(source, target), source, target);
		}

		assert source.getLinks().size() == 1;
		assert target.getLinks().size() == 1;
		Link sourceUpLink = source.getLinks().get(0);
		Link targetUpLink = target.getLinks().get(0);
		if (source.isFailed() || target.isFailed() || sourceUpLink.isFailed()
				|| targetUpLink.isFailed()) {
			// route failed
			return new RouteResult(source, target);
		}

		IPAddr commAddr = IPAddr.getCommonPrefix(source.getAddr(),
				target.getAddr());
		switch (commAddr.getLength()) {
		case 2:
			// under same edge switch
			// System.out.println(1);
			return this.sameEdgeRoute(source, target);
		case 1:
			// under same pod
			// System.out.println(2);
			return this.samePodRoute(source, target);
		case 0:
			// System.out.println(3);
			return this.diffPodRoute(source, target);
		}
		assert false : "this code should not be executed\nsource addr is "
				+ source.getAddr().toString() + " target addr is "
				+ target.getAddr().toString();
		return null;
	}

	/**
	 * Route two servers under same edge switch
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @author Hongze Zhao
	 */
	private RouteResult sameEdgeRoute(Node source, Node target) {
		Flow flow = new Flow(source, target);
		flow.addLink(source.getLinks().get(0));
		flow.addLink(target.getLinks().get(0));
		return new RouteResult(flow, source, target);
	}

	/**
	 * Route two servers under same pod
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @author Hongze Zhao
	 */
	private RouteResult samePodRoute(Node source, Node target) {
		Node edge1 = source.getLinks().get(0).getHead();
		Node edge2 = target.getLinks().get(0).getHead();
		assert edge1.getName().equals("edge switches");
		assert edge2.getName().equals("edge switches");
		Flow mediaFlow = this.getOneValidFlowBetweenTwoEdges(edge1, edge2);
		if (mediaFlow == null) {
			return new RouteResult(source, target);
		}
		Flow flow1 = new Flow(source, edge1);
		flow1.addLink(source.getLinks().get(0));
		Flow flow2 = new Flow(edge2, target);
		flow2.addLink(target.getLinks().get(0));
		flow1.connect(mediaFlow);
		flow1.connect(flow2);
		return new RouteResult(flow1, source, target);
	}

	/**
	 * Route two servers under different pod
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @author Hongze Zhao
	 */
	private RouteResult diffPodRoute(Node source, Node target) {
		Node edge1 = source.getLinks().get(0).getHead();
		Node edge2 = target.getLinks().get(0).getHead();
		assert edge1.getName().equals("edge switches");
		assert edge2.getName().equals("edge switches");
		List<Flow> flows = this.getFLowsBetweenEdgeSwitchesDiffPod(source
				.getAddr().getSegment(0), target.getAddr().getSegment(0),
				source.getAddr().getSegment(1), target.getAddr().getSegment(1));
		if (flows.size() == 0) {
			return new RouteResult(source, target);
		}
		Flow mediaFlow = flows.get(ConstantManager.ran.nextInt(flows.size()));
		assert mediaFlow.isValid() : mediaFlow.toString();
		Flow f1 = new Flow(source, edge1);
		assert f1.isValid();
		Flow f2 = new Flow(edge2, target);
		assert f2.isValid();
		f1.addLink(source.getLinks().get(0));
		f2.addLink(target.getLinks().get(0));
		f1.connect(mediaFlow);
		f1.connect(f2);
		assert f1.isValid();
		return new RouteResult(f1, source, target);
	}

	/**
	 * Get all the flows between two edge switches under same pod which are not
	 * failed
	 * 
	 * @param edge1
	 *            edge switch 1
	 * @param edge2
	 *            edge switch 2
	 * @return
	 * @author Hongze Zhao
	 */
	private List<Flow> getFlowsListBetweenTwoEdges(Node edge1, Node edge2) {
		assert IPAddr.getCommonPrefix(edge1.getAddr(), edge2.getAddr())
				.getLength() == 1;
		List<Flow> flows = new ArrayList<Flow>();
		for (int i = 0; i < this.k / 2; i++) {
			Link l1 = edge1.getLinks().get(i);
			Link l2 = edge2.getLinks().get(i);
			if (!l1.isFailed() && !l2.isFailed()) {
				Flow flow = new Flow(edge1, edge2);
				flow.addLink(l1);
				flow.addLink(l2);
				flows.add(flow);
			}
		}
		return flows;
	}

	/**
	 * Get a flow between two edge switches under same pod which is not failed
	 * 
	 * @param edge1
	 * @param edge2
	 * @return
	 * @author Hongze Zhao
	 */
	private Flow getOneValidFlowBetweenTwoEdges(Node edge1, Node edge2) {
		List<Flow> flows = this.getFlowsListBetweenTwoEdges(edge1, edge2);
		if (flows.size() == 0) {
			return null;
		} else {
			return flows.get(ConstantManager.ran.nextInt(flows.size()));
		}
	}

	/**
	 * @param args
	 * @author Hongze Zhao
	 */
	public static void main(String[] args) {
		for (double rat = 0; rat < 1.01; rat += 0.1) {
			ISimulator sim = new FailureSimulator(rat, 0, 0, new FatTree(10));
			sim.initialize();
			sim.run();
			try {
				System.out.println(sim.getMetric("ABT") + " "
						+ sim.getMetric("SuccCount"));
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

}
