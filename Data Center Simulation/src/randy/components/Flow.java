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

}
