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
	private final List<Node> servers = new ArrayList<Node>();
	/**
	 * key is servers' UUID, value is the reference Used to get Node by UUID
	 */
	private final HashMap<UUID, Node> serverUUIDHashMap = new HashMap<UUID, Node>();

	/**
	 * Switches
	 */
	private final List<Node> switches = new ArrayList<Node>();
	/**
	 * Key is switches' UUID, value is the reference
	 */
	private final HashMap<UUID, Node> switchesUUIDHashMap = new HashMap<UUID, Node>();

	/**
	 * All links
	 */
	private final List<Link> links = new ArrayList<Link>();
	/* (non-Javadoc)
	 * @see randy.IDCN#route(java.util.UUID, java.util.UUID)
	 */
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

}
