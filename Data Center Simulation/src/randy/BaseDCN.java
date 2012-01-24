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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import randy.components.Node;

/**
 * Description: Base class of data center network inherited from IDCN Support
 * some containers to hold basic somponents of a data center network
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 9:13:55 PM
 */
public abstract class BaseDCN implements IDCN {

	/**
	 * Servers
	 */
	private final List<Node> servers = new LinkedList<Node>();
	/**
	 * key is servers' UUID, value is the reference Used to get Node by UUID
	 */
	private final HashMap<UUID, Node> serverUUIDHashMap = new HashMap<UUID, Node>();

	/**
	 * Switches
	 */
	private final List<Node> switches = new LinkedList<Node>();
	/**
	 * Key is switches' UUID, value is the reference
	 */
	private final HashMap<UUID, Node> switchesUUIDHashMap = new HashMap<UUID, Node>();

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

}
