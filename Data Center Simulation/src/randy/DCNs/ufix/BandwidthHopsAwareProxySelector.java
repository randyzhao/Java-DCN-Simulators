/**  
* Filename:    BandwidthHopsAwareProxySelector.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 3:33:17 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import randy.IDCN.RouteResult;
import randy.DCNs.UFix.UFixDomain;
import randy.components.Link;
import randy.components.Node;

/**
 * Proxy selector which use bandwidth and hops to select proxy servers
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 3:33:17 PM
 */
public abstract class BandwidthHopsAwareProxySelector implements IProxySelector {
	protected UFixDomain domain = null;
	protected HashMap<Node, Integer> hops = new HashMap<Node, Integer>();
	protected HashMap<Node, Double> bandwidth = new HashMap<Node, Double>();
	protected List<Node> proxy;
	/**
	 * Used to display a pair of aggregate bandwidth and hops which is generated
	 * in the same one-to-all traffic
	 * 
	 * @author Hongze Zhao Create At : Feb 19, 2012 3:34:56 PM
	 */
	private class BandwidthHopsPair {
		int hopsCount = 0;
		double bandwidthCount = 0;

		public BandwidthHopsPair(int hopsCount, double bandwidthCount) {
			this.hopsCount = hopsCount;
			this.bandwidthCount = bandwidthCount;
		}

		public int getHopsCount() {
			return this.hopsCount;
		}

		public double getBandwidthCount() {
			return this.bandwidthCount;
		}

	}

	/**
	 * Calculate all the hopsCount and bandwidth count of one-to-all traffic of every proxy server
	 * 
	 * @author Hongze Zhao
	 */
	protected void processHopsBandwidth(){
		this.proxy = new ArrayList<Node>(this.domain.getU());
		this.proxy.addAll(this.domain.getAvailableProxyServers());
		for (Node node : this.proxy) {
			BandwidthHopsPair pair = this.generateOneToAllTraffic(node);
			this.hops.put(node, pair.getHopsCount());
			this.bandwidth.put(node, pair.getBandwidthCount());
		}

	}
	/**
	 * Generate one-to-all traffic for a proxy server and calculates the
	 * aggregate bandwidth and hops
	 * 
	 * @param sourceUUID
	 * @return
	 * @author Hongze Zhao
	 */
	private BandwidthHopsPair generateOneToAllTraffic(Node source) {
		List<Node> servers = this.domain.getServers();
		Iterator<Node> nodei = servers.iterator();
		int hopsCount = 0;
		while (nodei.hasNext()) {
			Node server = nodei.next();
			if (source.equals(server)) {
				continue;
			}
			RouteResult result = this.domain.getDCN().route(source.getUuid(),
					server.getUuid());
			assert result.isSuccessful();
			hopsCount += result.getFlow().getLinks().size();
		}

		double bandwidthCount = 0;
		for (Link l : source.getLinks()) {
			bandwidthCount += l.getBandwidth();
		}
		return new BandwidthHopsPair(hopsCount, bandwidthCount);
	}

	@Override
	public abstract void select(UFixDomain domain);

}
