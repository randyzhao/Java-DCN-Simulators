/**  
* Filename:    BaseDCN.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Jan 24, 2012 9:13:55 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Jan 24, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import randy.components.IPAddr;
import randy.components.Link;
import randy.components.Node;

/**
 * Description: Base class of data center network inherited from IDCN Support
 * some containers to hold basic somponents of a data center network
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 9:13:55 PM
 */
public abstract class BaseDCN implements IDCN {



	private final Random ran = new Random();
	/**
	 * Servers
	 */
	protected final List<Node> servers = new ArrayList<Node>();
	/**
	 * key is servers' UUID, value is the reference Used to get Node by UUID
	 */
	protected final HashMap<UUID, Node> serverUUIDHashMap = new HashMap<UUID, Node>();

	/**
	 * Switches
	 */
	protected final List<Node> switches = new ArrayList<Node>();
	/**
	 * Key is switches' UUID, value is the reference
	 */
	protected final HashMap<UUID, Node> switchesUUIDHashMap = new HashMap<UUID, Node>();

	/**
	 * All links
	 */
	protected final List<Link> links = new ArrayList<Link>();
	/* (non-Javadoc)
	 * @see randy.IDCN#route(java.util.UUID, java.util.UUID)
	 */

	/**
	 * Add a switch to switches and switchesUUIDHashMap
	 * 
	 * @param Switch
	 * @author Hongze Zhao
	 */
	protected void addSwitch(Node Switch) {
		this.switches.add(Switch);
		this.switchesUUIDHashMap.put(Switch.getUuid(), Switch);
	}

	/**
	 * Add a server to servers and serversUUIDHashMap
	 * 
	 * @param server
	 * @author Hongze Zhao
	 */
	protected void addServer(Node server) {
		this.servers.add(server);
		this.serverUUIDHashMap.put(server.getUuid(), server);
	}

	/**
	 * get a server by addr
	 * 
	 * @param addr
	 * @author Hongze Zhao
	 * @return
	 */
	public Node getServer(IPAddr addr) {
		for (Node server : this.servers) {
			if (server.getAddr().equals(addr)) {
				return server;
			}
		}
		return null;
	}

	/**
	 * Get server by UUID
	 * 
	 * @param uuid
	 * @return
	 * @author Hongze Zhao
	 */
	public Node getServer(UUID uuid) {
		return this.serverUUIDHashMap.get(uuid);
	}

	public List<Link> getLinks() {
		return this.links;
	}
	/**
	 * Connect two nodes with one link with specified bandwidth
	 * 
	 * @param n1
	 * @param n2
	 * @param bandwidth
	 *            the bandwidth of the link
	 * @author Hongze Zhao
	 */
	protected void connectNode(Node n1, Node n2, double bandwidth){
		Link link = new Link(bandwidth, n1, n2);
		this.links.add(link);
		n1.addLink(link);
		n2.addLink(link);
	}

	protected void connectServer(IPAddr addr1, IPAddr addr2, double bandwidth) {
		Node server1 = this.getServer(addr1);
		Node server2 = this.getServer(addr2);
		assert (server1 != null) : "server1 is null, addr is "
				+ addr1.toString();
		assert (server2 != null) : "server2 is null, addr is "
				+ addr2.toString();
		this.connectNode(server1, server2, bandwidth);
	}

	@Override
	abstract public RouteResult route(UUID sourceUUID, UUID targetUUID);

	/* (non-Javadoc)
	 * @see randy.IDCN#containNode(java.util.UUID)
	 */
	@Override
	public boolean containNode(UUID nodeUUID) {
		return this.serverUUIDHashMap.containsKey(nodeUUID);
	}

	@Override
	public void randomFailServers(double ration) {
		for (Node node : this.servers) {
			double temp = this.ran.nextDouble();
			if (temp < ration) {
				node.setFailed(true);
				List<Link> links = node.getLinks();
				for (Link l : links) {
					l.setFailed(true);
				}
			} else {
				node.setFailed(false);
			}
		}

	}

	@Override
	public void randomFailSwitches(double ration) {
		for (Node node : this.switches) {
			double temp = this.ran.nextDouble();
			if (temp < ration) {
				node.setFailed(true);
				List<Link> links = node.getLinks();
				for (Link l : links) {
					l.setFailed(true);
				}
			} else {
				node.setFailed(false);
			}
		}
	}

	public List<Node> getServers() {
		return this.servers;
	}

	public List<Node> getSwitches() {
		return this.switches;
	}
	@Override
	public void randomFailLinks(double ration) {
		for (Link l : this.links) {
			double temp = this.ran.nextDouble();
			if (temp < ration) {
				l.setFailed(true);
			} else {
				l.setFailed(false);
			}
		}
	}

	@Override
	public List<UUID> getServerUUIDs() {
		return new ArrayList<UUID>(this.serverUUIDHashMap.keySet());
	}

	@Override
	public void reset() {
		for (Link l : this.links){
			l.reset();
		}
		for (Node server : this.servers) {
			server.reset();
		}
		for (Node sw : this.switches) {
			sw.reset();
		}
		
	}
}
