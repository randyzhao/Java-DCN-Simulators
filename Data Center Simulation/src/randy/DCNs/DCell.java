/**  
 * Filename:    DCell.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Jan 24, 2012 9:35:37 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Jan 24, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy.DCNs;

import java.util.Arrays;
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
 * Description:
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 9:35:37 PM
 */
public class DCell extends BaseDCN {

	/**
	 * number of servers in DCell_0
	 */
	private final int n;
	/**
	 * highest level
	 */
	private final int l;

	/**
	 * 
	 * @param n
	 *            number of servers in DCell_0
	 * @param l
	 *            highest level
	 */
	public DCell(int n, int l) {
		this.n = n;
		this.l = l;
		IPAddr pref = new IPAddr();
		this.BuildDCells(pref, n, l);
	}

	public void BuildDCells(IPAddr pref, int n, int l) {
		// System.out.println("into BuildDCells n is " + n + "  l is " + l);
		if (l == 0) {// build DCell_0
			Node Switch = new Node("switch");
			this.addSwitch(Switch);
			for (int i = 0; i < n; i++) {// connect node[pref, i] to its switch
				Node server = new Node("server");
				IPAddr newAddr = new IPAddr(pref);
				newAddr.appendSegment(i);
				server.setAddr(newAddr);
				this.addServer(server);
				// System.out.println("add server " +
				// server.getAddr().toString());
				this.connectNode(Switch, server, ConstantManager.LINK_BANDWIDTH);
			}
			return;
		}

		// l != 0
		// part II
		int gl = DCell.getGk(l, n);
		for (int i = 0; i < gl; i++) {// build the DCell_{l - 1}s
			IPAddr newAddr = new IPAddr(pref);
			newAddr.appendSegment(i);
			this.BuildDCells(newAddr, n, l - 1);
		}
		// part III
		int tlminus1 = DCell.getTk(l - 1, n);
		for (int i = 0; i < tlminus1; i++) {// connect the DCell_{l - 1}s
			int tempgl = DCell.getGk(l, n);
			for (int j = i + 1; j < tempgl; j++) {
				int uid1 = j - 1;
				int uid2 = i;
				IPAddr addr1 = pref.appendAndCopy(i);
				addr1.connect(prefFromUid(uid1, l - 1, n));
				assert addr1.getLength() == this.l + 1 : "the addr len is "
						+ addr1.getLength() + "\n it should be " + (this.l + 1);
				IPAddr addr2 = pref.appendAndCopy(j);
				addr2.connect(prefFromUid(uid2, l - 1, n));
				assert addr2.getLength() == this.l + 1 : "the addr len is "
						+ addr2.getLength() + "\n it should be " + (this.l + 1);
				this.connectServer(addr1, addr2, ConstantManager.LINK_BANDWIDTH);

			}

		}
	}

	@Override
	public List<Node> getServers() {
		return this.servers;
	}

	@Override
	public List<Node> getSwitches() {
		return this.switches;
	}
	/**
	 * Calculate uid from pref
	 * 
	 * @param k
	 * @param n
	 * @param addr
	 * @return
	 * @author Hongze Zhao
	 */
	private static int uidFromPref(IPAddr addr, int k, int n) {
		int uid = 0;
		uid += addr.getSegment(addr.getLength() - 1);
		for (int j = 1; j <= k; j++) {
			uid += addr.getSegment(addr.getLength() - 1 - j) * getTk(j - 1, n);
		}
		return uid;
	}


	/**
	 * Calculate pref from uid
	 * 
	 * @param k
	 *            the level of the uid
	 * @param n
	 * @param uid
	 * @return the result prefix, the length is k + 1
	 * @author Hongze Zhao
	 */
	private static IPAddr prefFromUid(int uid, int k, int n) {
		List<Integer> addr = new LinkedList<Integer>();
		Integer[] a = new Integer[k + 1];
		for (int j = k; j >= 1; j--) {
			int tk = getTk(j - 1, n);
			a[k - j] = uid / tk;
			uid -= tk * a[k - j];
		}
		a[k] = uid;
		return new IPAddr(Arrays.asList(a));
	}

	/**
	 * Calculate g_k
	 * 
	 * @param k
	 * @return g_k
	 * @author Hongze Zhao
	 */
	private static int getGk(int k, int n) {
		if (k == 0) {
			return 1;
		} else {
			return getTk(k - 1, n) + 1;
		}
	}

	/**
	 * Calculate t_k
	 * 
	 * @param k
	 * @return
	 * @author Hongze Zhao
	 */
	private static int getTk(int k, int n) {
		if (k == 0) {
			return n;
		} else {
			return getGk(k, n) * getTk(k - 1, n);
		}
	}

	/**
	 * Get flow between two nodes in the same DCell0
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @author Hongze Zhao
	 */
	public Flow DCell0Link(Node source, Node target) {
		Flow flow = new Flow(source, target);
		// Note every Server's first link in links is connected to switch
		flow.addLink(source.getLinks().get(0));
		flow.addLink(target.getLinks().get(0));
		return flow;
	}

	/**
	 * GetLink(pref, s_{k-m}, d_{k-m} function in Paper calculate the link that
	 * interconnects the two sub-DCells
	 * 
	 * @param pref
	 *            common prefix
	 * @param skm
	 *            indices of one subDCell
	 * @param dkm
	 *            indices of another sub-DCell
	 * @return
	 * @author Hongze Zhao
	 */
	public Flow GetLink(IPAddr pref, int skm, int dkm) {
		IPAddr addr1 = new IPAddr(pref);
		IPAddr addr2 = new IPAddr(pref);
		addr1.appendSegment(skm);
		addr1.connect(prefFromUid(dkm < skm ? dkm : dkm - 1,
				this.l - pref.getLength() - 1,
				this.n));
		addr2.appendSegment(dkm);
		addr2.connect(prefFromUid(skm < dkm ? skm : skm - 1,
				this.l - pref.getLength() - 1, this.n));
		Node source = this.getServer(addr1);
		assert source != null : "addr1 is " + addr1.toString();
		Node target = this.getServer(addr2);
		assert target != null : "addr1 is " + addr1.toString() + " addr2 is "
				+ addr2.toString();
		Link l = source.getLink(target);
		assert l != null : "addr1 is " + addr1.toString() + " addr2 is "
				+ addr2.toString();
		Flow flow = new Flow(source, target);
		flow.addLink(l);
		return flow;
	}

	/**
	 * DCellRouting function in the paper Assume all the link is not failed
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @author Hongze Zhao
	 */
	public Flow DCellRouting(Node source, Node target) {
		if (source.getAddr().equals(target.getAddr())) {
			return new Flow(source, target);
		}
		IPAddr pref = IPAddr
				.getCommonPrefix(source.getAddr(), target.getAddr());
		if (pref.getLength() == this.l) {// in the same DCell0
			return this.DCell0Link(source, target);
		} else {
			// in the different DCell_0
			Flow interFlow = this.GetLink(pref,
					source.getAddr().getSegment(pref.getLength()), target
							.getAddr().getSegment(pref.getLength()));
			assert interFlow != null;
			Flow path1 = this.DCellRouting(source, interFlow.getSource());
			Flow path2 = this.DCellRouting(interFlow.getTarget(), target);
			path1.connect(interFlow);
			path1.connect(path2);
			return path1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see randy.BaseDCN#route(java.util.UUID, java.util.UUID)
	 */
	@Override
	public RouteResult route(UUID sourceUUID, UUID targetUUID) {
		// System.out.println("into dcell route");
		// TODO Auto-generated method stub
		Node source = this.getServer(sourceUUID);
		Node target = this.getServer(targetUUID);
		assert source != null && target != null;
		if (sourceUUID.equals(targetUUID)) {
			return new RouteResult(new Flow(source, target), source, target);
		}
		Flow flow = this.DCellRouting(source, target);
		if (!flow.isValid()) {
			return new RouteResult(source, target);
		} else {
			return new RouteResult(flow, source, target);
		}
		// TODO;
	}

	public static void main(String[] args) {
		for (double rat = 0; rat < 0.41; rat += 0.05) {
			ISimulator sim = new FailureSimulator(0, 0, rat, new DCell(8, 1));
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
