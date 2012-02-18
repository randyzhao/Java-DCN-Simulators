/**  
* Filename:    Flow.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Jan 24, 2012 8:02:43 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Jan 24, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.components;

import java.util.LinkedList;
import java.util.List;

/**
 * Description: define the flow, which is represented by a series of links
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 8:02:43 PM
 */
public class Flow {
	private final Node source;
	private Node target;
	private final List<Link> links = new LinkedList<Link>();

	/**
	 * Add an link to the flow
	 * 
	 * @param link
	 * @author Hongze Zhao
	 */
	public void addLink(Link link) {
		this.links.add(link);
	}

	/**
	 * Add a series of link to the flow
	 * 
	 * @param links
	 * @author Hongze Zhao
	 */
	public void addLinks(List<Link> link) {
		this.links.addAll(link);
	}

	/**
	 * Sort the link's sequence in links in order that the next link has one
	 * comman node with the previous link
	 * 
	 * @author Hongze Zhao
	 */
	public void sortLinks() {
		// TODO: implement Flow.sortLinks
		throw new UnsupportedOperationException();
	}

	/**
	 * Whether the link's sequence in links is in the order that the next link
	 * has one comman node with the previous link
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	public boolean isSorted(){
		if (this.source.getAddr().toString().equals("0.0")
				&& this.target.getAddr().toString().equals("1.0")) {
			int a = 0;
			a++;
		}
		Link headLink = this.links.get(0);
		Link tailLink = this.links.get(this.links.size() - 1);
		if (!headLink.contain(this.source)) {
			return false;
		}
		if (!tailLink.contain(this.target)) {
			return false;
		}
		for (int i = 1; i < this.links.size(); i++) {
			if (!this.links.get(i).canConnect(this.links.get(i - 1))) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Get the bandwidth of this flow It is determined by the min average
	 * bandwidth of each link in the links
	 * 
	 * @return the bandwidth of this flow
	 * @author Hongze Zhao
	 */
	public double bandwidth() {
		double output = Double.MAX_VALUE;
		for (Link l : this.links) {
			if (l.currentBandwidth() < output) {
				output = l.currentBandwidth();
			}
		}
		return output;
	}

	public Node getSource() {
		return this.source;
	}

	public Node getTarget() {
		return this.target;
	}

	public List<Link> getLinks() {
		return this.links;
	}
	
	/**
	 * Reverse the flow and copy to a new flow
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	public Flow reverseAndCopy() {
		Flow flow = new Flow(this.target, this.source);
		for (int i = this.links.size() - 1; i >= 0; i--) {
			flow.addLink(this.links.get(i));
		}
		return flow;
	}

	/**
	 * Connect this and flow Ensure this's target == flow' soruce
	 * 
	 * @param flow
	 *            the flow to connect
	 * @author Hongze Zhao
	 */
	public void connect(Flow flow) {
		assert this.target == flow.source;
		this.links.addAll(flow.links);
		this.target = flow.target;
	}

	public Flow(Node source, Node target) {
		this.source = source;
		this.target = target;
	}

	public Flow(Flow flow) {
		this.source = flow.source;
		this.target = flow.target;
		this.links.addAll(flow.links);
	}

	/**
	 * Whether all of its links is valid
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	public boolean isValid() {
		for (int i = 0; i < this.links.size(); i++) {
			if (this.links.get(i).isFailed()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("source is ");
		sb.append(this.source.getAddr().toString());
		sb.append(" target is ");
		sb.append(this.target.getAddr().toString());
		sb.append("  ");
		for (int i = 0; i < this.links.size(); i++) {
			sb.append(this.links.get(i).toString());
			sb.append("  ");
		}
		return sb.toString();
	}

}
