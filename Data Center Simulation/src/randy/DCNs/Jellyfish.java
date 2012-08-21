/**  
 * Filename:    Jellifish.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Aug 20, 2012 8:44:17 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Aug 20, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy.DCNs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

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
 * @author Hongze Zhao
 * Create At : Aug 20, 2012 8:44:17 PM
 */
public class Jellyfish extends BaseDCN {


	/**
	 * a switch which can store route table
	 * 
	 * @author Hongze Zhao Create At : Aug 20, 2012 9:03:25 PM
	 */
	private class JellyfishSwitch extends Node {

		private Random ran = new Random();
		private class RouteTableEntry {
			private int destTORId;
			private List<JellyfishSwitch> nextHops = new ArrayList<Jellyfish.JellyfishSwitch>();
			private int hopsCount;
			public int getDestTORId() {
				return this.destTORId;
			}

			public List<JellyfishSwitch> getNextHops() {
				return this.nextHops;
			}

			public void addNextHop(JellyfishSwitch nextHop) {
				this.nextHops.add(nextHop);
			}

			public int getHopsCount() {
				return this.hopsCount;
			}

			public RouteTableEntry(int destTORId, JellyfishSwitch nextHop,
					int hopsCount) {
				this.destTORId = destTORId;
				this.hopsCount = hopsCount;
				this.nextHops.add(nextHop);
			}
		}

		private int torId;

		public int getTORId() {
			return this.torId;
		}

		private List<JellyfishSwitch> neiborSwitches = new ArrayList<JellyfishSwitch>();

		private HashMap<Integer, RouteTableEntry> routeTable = new HashMap<Integer, RouteTableEntry>();

		public int hopsToDestTOR(int destTORId) {
			if (this.routeTable.containsKey(destTORId)) {
				return this.routeTable.get(destTORId).getHopsCount();
			} else {
				return Integer.MAX_VALUE;
			}
		}

		public JellyfishSwitch randomNextHop(int destTORId){
			if (!this.routeTable.containsKey(destTORId)){
				return null;
			}else{
				List<JellyfishSwitch> nextHops = this.routeTable.get(destTORId)
						.getNextHops();
				return nextHops.get(this.ran.nextInt(nextHops.size()));
			}
		}
		/**
		 * count of ports available to connect other JellyfishSwitch
		 */
		private int availablePort;

		public int getAvailablePort() {
			return this.availablePort;
		}
		private void addNeiborSwitch(JellyfishSwitch sw) {
			this.neiborSwitches.add(sw);
		}

		public List<JellyfishSwitch> getNeiborSwitches() {
			return this.neiborSwitches;
		}
		public void connectJellyfishSwitch(JellyfishSwitch sw) {
			this.addNeiborSwitch(sw);
			this.availablePort--;

		}

		/**
		 * 
		 * @param destTORId
		 * @param nextHopTORId
		 * @param hopCount
		 * @return whether the route table has been changed. return false when
		 *         just add one route
		 * @author Hongze Zhao
		 */
		public boolean updateRouteTable(int destTORId, JellyfishSwitch sw,
				int hopCount) {
			if (this.routeTable.containsKey(destTORId)) {
				RouteTableEntry rte = this.routeTable.get(destTORId);
				if (rte.getHopsCount() > hopCount + 1){
					//update the table entry
					this.routeTable.remove(destTORId);
					this.routeTable.put(destTORId, new RouteTableEntry(
							destTORId, sw, hopCount + 1));
					return true;
				} else if (rte.getHopsCount() == hopCount + 1) {
					// add one route
					rte.addNextHop(sw);
					return false;
				} else {
					return false;
				}
			} else {
				this.routeTable.put(destTORId, new RouteTableEntry(destTORId,
						sw, hopCount + 1));
				return true;
			}
		}
		/**
		 * @param name
		 */
		public JellyfishSwitch(String name, int torId, int availablePort) {
			super(name);
			this.torId = torId;
			this.availablePort = availablePort;
		}

	}

	private HashMap<Integer, JellyfishSwitch> switchHashMap = new HashMap<Integer, Jellyfish.JellyfishSwitch>();

	/**
	 * connect two Jellyfish Switch add neibor switch to each other
	 * 
	 * @param sw1
	 * @param sw2
	 * @param bw
	 * @author Hongze Zhao
	 */
	private void connectSwitches(JellyfishSwitch sw1, JellyfishSwitch sw2,
			double bw) {
		super.connectNode(sw1, sw2, bw);
		sw1.connectJellyfishSwitch(sw2);
		sw2.connectJellyfishSwitch(sw1);
	}
	/**
	 * build a TOR switche and its subnet servers
	 * 
	 * @param torID
	 * @param torSize
	 * @author Hongze Zhao
	 */
	private void buildTOR(int torID, int torSize, int torPort) {
		JellyfishSwitch tor = new JellyfishSwitch("tor "
				+ String.valueOf(torID), torID, torPort);
		tor.setAddr(new IPAddr(new Integer[] { torID }));
		this.addSwitch(tor);
		this.switchHashMap.put(torID, tor);
		for (int i = 0; i < torSize; i++) {
			Node server = new Node(String.format("server-%1d-%2d", torID, i));
			this.addServer(server);
			server.setAddr(new IPAddr(new Integer[] { torID, i }));
			this.connectNode(tor, server, ConstantManager.LINK_BANDWIDTH);
		}
	}

	/**
	 * connect all the TORs using connectSwitches randomly Note we will end the
	 * connecting procedure while one switch has more than one available port.
	 * This is different from the paper
	 * 
	 * @author Hongze Zhao
	 */
	private void connectTORs() {
		Random ran = new Random();
		List<JellyfishSwitch> availableTORs = new LinkedList<Jellyfish.JellyfishSwitch>();
		for (Node sw : this.switches) {
			JellyfishSwitch js = (JellyfishSwitch) sw;
			if (js.getAvailablePort() > 0) {
				availableTORs.add(js);
			}
		}

		while (availableTORs.size() > 1) {// can connect
			int ran1 = ran.nextInt(availableTORs.size());
			int ran2 = ran1;
			while (ran1 == ran2) {
				ran2 = ran.nextInt(availableTORs.size());
			}
			// ran1 != ran2
			JellyfishSwitch sw1 = availableTORs.get(ran1);
			JellyfishSwitch sw2 = availableTORs.get(ran2);
			System.out.println("Connect switch " + sw1.getName() + " -- "
					+ sw2.getName());
			this.connectSwitches(sw1, sw2, ConstantManager.LINK_BANDWIDTH);
			if (sw1.getAvailablePort() == 0) {
				availableTORs.remove(sw1);
			}
			if (sw2.getAvailablePort() == 0) {
				availableTORs.remove(sw2);
			}
		}
	}

	/**
	 * broadcast a specific route table entry from a JellyfishSwitch if update
	 * neibor switch, add it to update queue list
	 * 
	 * @param sw
	 * @param destTORId
	 * @author Hongze Zhao
	 */
	private void broadcastRouteEntry(JellyfishSwitch sw, int destTORId,
			Queue<JellyfishSwitch> updateQueue) {
		for (JellyfishSwitch neibor : sw.getNeiborSwitches()) {
			if (neibor.updateRouteTable(destTORId, sw,
					sw.hopsToDestTOR(destTORId))) {
				System.out.println(String.format(
						"update switch %1s to dest %2d hops %3d",
						neibor.getName(), destTORId,
						sw.hopsToDestTOR(destTORId)));
				updateQueue.add(neibor);
			}
		}
	}

	/**
	 * broadcast route information for one switch
	 * 
	 * @param sw
	 * @author Hongze Zhao
	 */
	private void buildRouteEntryForSwitch(JellyfishSwitch sw) {
		sw.updateRouteTable(sw.getTORId(), null, 0);
		Queue<JellyfishSwitch> updateQueue = new LinkedBlockingDeque<Jellyfish.JellyfishSwitch>();
		updateQueue.add(sw);
		while (!updateQueue.isEmpty()){
			JellyfishSwitch s = updateQueue.poll();
			this.broadcastRouteEntry(s, sw.getTORId(), updateQueue);
		}
	}

	/**
	 * build route tables of all the jellyswitches
	 * 
	 * @author Hongze Zhao
	 */
	private void buildRouteTables() {
		for (Node n : this.switches) {
			JellyfishSwitch sw = (JellyfishSwitch) n;
			this.buildRouteEntryForSwitch(sw);
		}
	}

	/**
	 * 
	 * @param torSize
	 *            the number of servers under a TOR switch
	 * @param torCount
	 *            the total number of TOR switches
	 * @author Hongze Zhao
	 */
	private void buildJellifish(int torSize, int torCount, int torPortCount) {
		for (int i = 0; i < torCount; i++) {
			this.buildTOR(i, torSize, torPortCount);
		}
		this.connectTORs();
		this.buildRouteTables();
	}

	public Jellyfish(int torSize, int torCount, int torPortCount) {
		this.buildJellifish(torSize, torCount, torPortCount);
	}

	/**
	 * get Flow from sw1 to sw2
	 * 
	 * @param sw1
	 * @param sw2
	 * @return
	 * @author Hongze Zhao
	 */
	private Flow switchToSwitchFlow(JellyfishSwitch sw1, JellyfishSwitch sw2) {
		Flow flow = new Flow(sw1, sw2);
		JellyfishSwitch pos = sw1;
		while (!pos.equals(sw2)) {
			JellyfishSwitch nextHop = pos.randomNextHop(sw2.getTORId());
			if (nextHop == null) {
				return null;
			}
			Link link = pos.getLink(nextHop);
			if (link == null) {
				System.out.println(String.format(
						"[switchToSwitcFlow]: %1s do not have neibor %2s",
						pos.getName(), nextHop.getName()));
				return null;
			}
			flow.addLink(link);
			pos = nextHop;
		}
		return flow;

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see randy.BaseDCN#route(java.util.UUID, java.util.UUID)
	 */
	@Override
	public RouteResult route(UUID sourceUUID, UUID targetUUID) {
		// TODO do not support failure routing now!
		Node source = this.getServer(sourceUUID);
		Node target = this.getServer(targetUUID);
		// System.out.println(String.format("[route]: source %1s target %2s",
		// source.getName(), target.getName()));
		if (source == null || target == null) {
			System.err
			.println("[Jellyfish.route]: source or target is not existed");
			return null;
		}

		JellyfishSwitch sourceSwitch = this.switchHashMap.get(source.getAddr()
				.getSegment(0));
		JellyfishSwitch targetSwitch = this.switchHashMap.get(target.getAddr()
				.getSegment(0));
		if (source.getAddr().getSegment(0) == target.getAddr().getSegment(0)) {
			Flow flow = new Flow(source, target);
			if (source.getLink(sourceSwitch) == null
					|| target.getLink(targetSwitch) == null) {
				System.err
				.println("[Jellyfish.route]: server to switch link not existed\n");
			}
			flow.addLink(source.getLink(sourceSwitch));
			flow.addLink(target.getLink(targetSwitch));
			// System.out.println(String.format("inner-TOR-flow"));
			return new RouteResult(flow, source, target);
		}
		if (sourceSwitch == null || targetSwitch == null) {
			System.err
			.println("[Jellyfish.route]: source or target switch is not existed");
			return null;
		}
		Flow flow = this.switchToSwitchFlow(sourceSwitch, targetSwitch);
		RouteResult rr = null;
		if (flow == null) {
			rr = new RouteResult(source, target);
		} else {
			flow.addLink(source.getLink(sourceSwitch));
			flow.addLink(target.getLink(targetSwitch));
			rr = new RouteResult(flow, source, target);
			// System.out.println(String.format("flow size %1d", rr.getFlow()
			// .getLinks().size()));
		}
		return rr;
	}

	/**
	 * @param args
	 * @author Hongze Zhao
	 */
	public static void main(String[] args) {
		BaseDCN dcn = new Jellyfish(2, 30, 4);
		ISimulator sim = new FailureSimulator(0, 0, 0, dcn);
		sim.initialize();
		sim.run();
		for (Link link : dcn.getLinks()) {
			System.out.println(link.toString());
		}
		try {
			System.out.println(String.format(
					"ABT %1f \n Throughput per Port %2f\n",
					sim.getMetric("ABT"), sim.getMetric("ThroughputPerLink")));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}
}
