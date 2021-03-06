/**  
 * Filename:    Node.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Jan 24, 2012 7:20:22 PM  
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
 * Description: Define the node in the data center network
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 7:20:22 PM
 */
public class Node {

	private final UUID uuid;
	/**
	 * Name of the components
	 */
	private final String name;
	/**
	 * Links attached to this components
	 */
	private final List<Link> links = new ArrayList<Link>();
	/**
	 * Whether this component is failed or not
	 */
	private boolean failed = false;
	/**
	 * The address of the node
	 */
	private IPAddr addr = new IPAddr();

	public final boolean isFailed() {
		return this.failed;
	}

	/**
	 * Set failure tag for a node and all its connected links
	 * 
	 * @param failed
	 * @author Hongze Zhao
	 */
	public final void setFailed(boolean failed) {
		this.failed = failed;
	}

	public final IPAddr getAddr() {
		return this.addr;
	}

	public final void setAddr(IPAddr addr) {
		this.addr = addr;
	}

	public final UUID getUuid() {
		return this.uuid;
	}

	public final String getName() {
		return this.name;
	}

	/**
	 * Get link
	 * 
	 * @param neibor
	 *            the neibor connected with the link
	 * @return the link connect the neibor
	 * @author Hongze Zhao
	 */
	public Link getLink(Node neibor) {
		for (int i = 0; i < this.links.size(); i++) {
			Link l = this.links.get(i);
			if (l.getHead() == neibor || l.getTail() == neibor) {
				return l;
			}
		}
		return null;
	}

	public void addLink(Link l) {
		this.links.add(l);
	}

	public final List<Link> getLinks() {
		return this.links;
	}

	/**
	 * Reset a node's status. Include failure
	 * 
	 * @author Hongze Zhao
	 */
	public void reset() {
		this.failed = false;
	}
	public Node(String name) {
		this.name = name;
		this.uuid = UUID.randomUUID();
		this.addr = null;
	}

	@Override
	public boolean equals(Object obj) {
		Node node = (Node) obj;
		return this.uuid.equals(node.uuid);
	}


}
