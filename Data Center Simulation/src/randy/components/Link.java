/**  
 * Filename:    Link.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Jan 24, 2012 7:13:56 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Jan 24, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Description: Define the link between two NICs
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 7:13:56 PM
 */
public class Link {

	private final UUID uuid;
	/**
	 * The head node of the link. The sequence of head node and tail node may be
	 * of no sense for some links
	 */
	private final Node head;
	/**
	 * The tail node of the link.
	 */
	private final Node tail;
	/**
	 * Maximum bandwidth of this link
	 */
	private final double bandwidth;
	/**
	 * Whether this link is failed or not
	 */
	private boolean failed = false;
	/**
	 * Flows already been attached to the link
	 */
	private final List<Flow> flows = new ArrayList<Flow>();

	public boolean isFailed() {
		return this.failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public Node getHead() {
		return this.head;
	}

	public Node getTail() {
		return this.tail;
	}

	public double getBandwidth() {
		return this.bandwidth;
	}

	public List<Flow> getFlows() {
		return this.flows;
	}
	
	/**
	 * Whether two links has one node in comman
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	public boolean canConnect(Link link) {
		return this.head.equals(link.head) || this.head.equals(link.tail)
				|| this.tail.equals(link.head) || this.tail.equals(link.tail);
	}
	
	/**
	 * Whether this link contain the node
	 * 
	 * @param node
	 * @return
	 * @author Hongze Zhao
	 */
	public boolean contain(Node node) {
		return this.head.equals(node) || this.tail.equals(node);
	}

	/**
	 * attach a flow to this link
	 * 
	 * @param f
	 *            the flow to be attached
	 * @author Hongze Zhao
	 */
	public void attachFlow(Flow f) {
		this.flows.add(f);
	}

	/**
	 * calculate current average bandwidth of flows been attached to this link
	 * already
	 * 
	 * @return current average bandwidth
	 * @author Hongze Zhao
	 */
	public double currentBandwidth() {
		return this.bandwidth / this.flows.size();
	}

	public Link(double bandwidth, Node head, Node tail) {
		this.bandwidth = bandwidth;
		this.head = head;
		this.tail = tail;
		this.uuid = UUID.randomUUID();
	}

	/**
	 * Reset a link's status. Include flow attachment and failure
	 * 
	 * @author Hongze Zhao
	 */
	public void reset() {
		this.failed = false;
		this.flows.clear();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.head.getName());
		sb.append(" : ");
		sb.append(this.head.getAddr().toString());
		sb.append(" --- ");
		sb.append(this.tail.getName());
		sb.append(" : ");
		sb.append(this.tail.getAddr().toString());
		return sb.toString();
	}

}
