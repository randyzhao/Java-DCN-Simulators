/**  
* Filename:    GeneralSimulator.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 18, 2012 3:59:51 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 18, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import randy.IDCN.RouteResult;
import randy.components.Flow;
import randy.components.Link;

/**
 * General simulator which use random simulation calculate AGT and ABT
 * 
 * @author Hongze Zhao Create At : Feb 18, 2012 3:59:51 PM
 */
public abstract class GeneralSimulator implements ISimulator {

	/**
	 * Indicate a pair of server to calculate route
	 * 
	 * @author Hongze Zhao Create At : Feb 18, 2012 4:05:59 PM
	 */
	class RoutePair {
		public RoutePair(UUID home, UUID away) {
			this.home = home;
			this.away = away;
		}

		private final UUID home, away;

		public UUID getHome() {
			return this.home;
		}

		public UUID getAway() {
			return this.away;
		}

	}

	/**
	 * the dcn to simulate
	 */
	private IDCN dcn;
	private final List<RoutePair> pairs = new LinkedList<RoutePair>();
	private final List<Flow> flows = new LinkedList<Flow>();
	private final HashMap<Flow, Double> flowBandwidth = new HashMap<Flow, Double>();
	/**
	 * the number of successful route pair
	 */
	private int successfulCount = 0;
	/**
	 * the number of failed route pair
	 */
	private int failedCount = 0;

	/* (non-Javadoc)
	 * @see randy.ISimulator#initialize()
	 */
	@Override
	public abstract void initialize();

	/* (non-Javadoc)
	 * @see randy.ISimulator#run()
	 */
	@Override
	public void run() {
		this.preRun();
		this.inRun();
		this.postRun();

	}

	/**
	 * all the process before run
	 * 
	 * @author Hongze Zhao
	 */
	private void preRun() {
		this.preparePairs();
	}

	/**
	 * all the process in run
	 * 
	 * @author Hongze Zhao
	 */
	private void inRun() {
		Iterator<RoutePair> pairIterator = this.pairs.iterator();
		while (pairIterator.hasNext()) {
			RoutePair pair = pairIterator.next();
			RouteResult result = this.dcn.route(pair.getHome(), pair.getAway());
			if (result.isSuccessful()) {
				this.flows.add(result.getFlow());
				this.attachFlow(result);
				this.successfulCount++;
			} else {
				this.failedCount++;
			}
		}
	}

	/**
	 * all the process after run
	 * 
	 * @author Hongze Zhao
	 */
	private void postRun() {
		this.calculateFlowBandwidth();
	}
	private void preparePairs() {
		List<UUID> uuids = this.dcn.getServerUUIDs();
		for (int i = 0; i < uuids.size(); i++) {
			for (int j = i + 1; j < uuids.size(); j++) {
				this.pairs.add(new RoutePair(uuids.get(i), uuids.get(j)));
			}
		}
		// shuffle
		Collections.shuffle(this.pairs);
	}

	/**
	 * Attach a flow to all its links when a route is successful
	 * 
	 * @param result
	 * @author Hongze Zhao
	 */
	private void attachFlow(RouteResult result) {
		assert result.isSuccessful();
		Flow flow = result.getFlow();
		List<Link> links = result.getFlow().getLinks();
		for (Link l : links) {
			l.attachFlow(flow);
		}
	}

	/**
	 * Calculate flow bandwidth of each flow after run
	 * 
	 * @author Hongze Zhao
	 */
	private void calculateFlowBandwidth() {
		Iterator<Flow> flowIterator = this.flows.iterator();
		while (flowIterator.hasNext()) {
			Flow flow = flowIterator.next();
			double bw = flow.bandwidth();
			this.flowBandwidth.put(flow, bw);
		}
	}

	/* (non-Javadoc)
	 * @see randy.ISimulator#result()
	 */
	@Override
	public String result() {
		StringBuilder sb = new StringBuilder();
		sb.append("Server number is ");
		sb.append(this.dcn.getServerUUIDs().size());
		sb.append("\n");
		sb.append("Route pair number is ");
		sb.append(this.pairs.size());
		sb.append("\n");
		sb.append("Successful route number is ");
		sb.append(this.successfulCount + "\n");
		sb.append("Failed route number is " + this.failedCount + "\n");
		sb.append("successful route ratio is " + (double) this.successfulCount
				/ this.pairs.size() + "\n");
		sb.append("ABT is " + this.ABT() + "\n");
		return sb.toString();
	}

	/**
	 * calculate aggregate bottleneck throughtput of the traffic
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	private double ABT() {
		Double output = Double.MAX_VALUE;
		Iterator<Double> bws = this.flowBandwidth.values().iterator();
		while (bws.hasNext()) {
			double temp = bws.next();
			if (temp < output) {
				output = temp;
			}
		}
		return output * this.successfulCount;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.dcn = null;
		this.pairs.clear();
		this.successfulCount = 0;
		this.failedCount = 0;
	}

}
