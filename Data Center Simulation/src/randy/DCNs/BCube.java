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
import java.util.LinkedList;
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
					this.servers.addAll(cell.servers);
					this.switches.addAll(cell.switches);
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
	public BCube(int n, int k) {
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
		Node source = this.getServer(sourceUUID);
		Node target = this.getServer(targetUUID);
		if (sourceUUID.equals(targetUUID)) {
			return new RouteResult(new Flow(source, target), source, target);
		}
		// System.out.println("into bcube route");
		List<Flow> flows = this.buildFlowSet(this.getServer(sourceUUID),
				this.getServer(targetUUID));
		if (flows.isEmpty()) {
			return new RouteResult(this.getServer(sourceUUID),
					this.getServer(targetUUID));
		}
		return new RouteResult(flows.get(ConstantManager.ran.nextInt(flows
				.size())), this.getServer(sourceUUID),
				this.getServer(targetUUID));

	}

	/**
	 * Build all valid flow set from souce to target It is called by the
	 * function route
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @author Hongze Zhao
	 */
	private List<Flow> buildFlowSet(Node source, Node target) {
		List<Flow> flowSet = new LinkedList<Flow>();
		IPAddr sourceAddr = source.getAddr();
		IPAddr targetAddr = target.getAddr();
		assert sourceAddr.getLength() == targetAddr.getLength();
		for (int i = 0; i < sourceAddr.getLength(); i++) {
			Flow flow;
			if (sourceAddr.getSegment(i) != targetAddr.getSegment(i)) {
				flow = this.DCRouting(sourceAddr, targetAddr, i);
			} else {
				flow = this.AltDCRouting(sourceAddr, targetAddr, i,
						this.neiborAddr(sourceAddr, i));
			}
			if (flow != null) {
				flowSet.add(flow);
				assert flow.isValid();
			}
		}
		return flowSet;
	}

	/**
	 * Get a neibor of addr at level level
	 * 
	 * @param addr
	 * @param level
	 * @return
	 * @author Hongze Zhao
	 */
	private IPAddr neiborAddr(IPAddr addr, int level) {
		IPAddr out = new IPAddr(addr);
		int ran = ConstantManager.ran.nextInt(this.n - 1);
		if (ran >= out.getSegment(level)) {
			ran++;
		}
		out.setSegment(level, ran);
		return out;
	}
	private Flow DCRouting(IPAddr sourceAddr, IPAddr targetAddr, int i) {
		int m = this.k;
		int permu[] = new int[this.k + 1];
		for (int j = i; j >= i - this.k; j--) {
			int temp = j;
			while (temp < 0) {
				temp += this.k + 1;
			}
			int resi = temp % (this.k + 1);
			permu[m] = resi;
			m--;
		}
		return this.BCubeRouting(sourceAddr, targetAddr, permu);
	}

	private Flow AltDCRouting(IPAddr sourceAddr, IPAddr targetAddr, int i,
			IPAddr neiborAddr) {
		Flow flow = new Flow(this.getServer(sourceAddr),
				this.getServer(sourceAddr));
		int m = this.k;
		int[] permu = new int[this.k + 1];
		for (int j = i - 1; j >= i - 1 - this.k; j--) {
			int temp = j;
			while (temp < 0) {
				temp += this.k + 1;
			}
			int resi = temp % (this.k + 1);
			permu[m] = resi;
			m--;
		}
		assert this.getServer(sourceAddr) != null : sourceAddr.toString()
				+ " is not founded";
		assert this.getServer(neiborAddr) != null : neiborAddr.toString()
				+ " is not founded";
		Flow flow1 = this.getNeiborServerFlow(this.getServer(sourceAddr),
				this.getServer(neiborAddr));
		if (flow1 == null) {
			return null;
		}
		flow.connect(flow1);
		Flow flow2 = this.BCubeRouting(neiborAddr, targetAddr, permu);
		if (flow2 == null) {
			return null;
		}
		flow.connect(flow2);
		return flow;
	}
	/**
	 * Fina a path from source to target the algorithm corrects one digit at one
	 * step The digit correcting order is decided by the predefined permutation
	 * permu
	 * 
	 * @param sourceAddr
	 * @param targetAddr
	 * @param permu
	 * @return
	 * @author Hongze Zhao
	 */
	private Flow BCubeRouting(IPAddr sourceAddr, IPAddr targetAddr, int[] permu) {
		Flow flow = new Flow(this.getServer(sourceAddr),
				this.getServer(sourceAddr));

		IPAddr iNode = new IPAddr(sourceAddr);
		for (int i = this.k; i >= 0; i--){
			if (sourceAddr.getSegment(permu[i]) != targetAddr.getSegment(permu[i])){
				iNode.setSegment(permu[i], targetAddr.getSegment(permu[i]));
				if (!this.appendServer(flow, flow.getTarget(),
						this.getServer(iNode))) {
					return null;
				}
			}
		}
		return flow;
	}

	/**
	 * Append two links from s1 to s2 to the flow Called by the function
	 * BCubeRouting
	 * 
	 * @param flow
	 * @param server
	 * @return
	 * @author Hongze Zhao
	 */
	private boolean appendServer(Flow flow, Node s1, Node s2) {
		Flow tempFlow = this.getNeiborServerFlow(s1, s2);
		if (tempFlow == null) {
			return false;
		}
		flow.connect(tempFlow);
		return true;
	}

	/**
	 * Get flow between two neibor server which is linked by a switch
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 * @author Hongze Zhao
	 */
	private Flow getNeiborServerFlow(Node s1, Node s2) {
		assert s1 != null && s2 != null;
		IPAddr addr1 = s1.getAddr();
		IPAddr addr2 = s2.getAddr();
		assert addr1 != null && addr2 != null;
		int temp = 0;
		for (int i = addr1.getLength() - 1; i >= 0; i--) {
			if (addr1.getSegment(i) != addr2.getSegment(i)) {
				Link l1 = s1.getLinks().get(temp);
				assert l1 != null;
				Link l2 = s2.getLinks().get(temp);
				assert l2 != null;
				if (!l1.isFailed() && !l2.isFailed()) {
					Flow flow = new Flow(s1, s2);
					flow.addLink(l1);
					flow.addLink(l2);
					return flow;
				} else {
					return null;
				}

			}
			temp++;
		}
		assert false : "this code should not be executed";
		return null;
	}

	public int switchesCount() {
		return this.switches.size();
	}

	public int serversCount() {
		return this.servers.size();
	}
	/**
	 * @param args
	 * @author Hongze Zhao
	 */
	public static void main(String[] args) {
		ISimulator sim = new FailureSimulator(0, 0, 0, new BCube(4, 2));
		sim.initialize();
		sim.run();
		try {
			System.out.println(String.format(
					"ABT %1f \n Throughput per Port %2f\n",
					sim.getMetric("ABT"), sim.getMetric("ThroughputPerLink")));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		//
		// for (double rat = 0; rat < 1.01; rat += 0.1) {
		// ISimulator sim = new FailureSimulator(rat, 0, 0, new BCube(6, 2));
		// sim.initialize();
		// sim.run();
		// try {
		// System.out.println(sim.getMetric("ABT") + " "
		// + sim.getMetric("SuccCount"));
		// } catch (Exception ex) {
		// System.out.println(ex.getMessage());
		// }
		// }

	}

}
