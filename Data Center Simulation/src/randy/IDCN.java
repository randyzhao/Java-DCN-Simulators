/**  
 * Filename:    IDCN.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Jan 24, 2012 7:10:53 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Jan 24, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy;

import java.util.List;
import java.util.UUID;

import randy.components.Flow;
import randy.components.Node;

/**
 * Description: Basic interface of data center network
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 7:10:53 PM
 */
public interface IDCN {

	/**
	 * Description: package the route's result
	 * 
	 * @author Hongze Zhao Create At : Jan 24, 2012 8:57:02 PM
	 */
	public class RouteResult {
		private final boolean successful;
		private final Flow flow;
		private final Node source;
		private final Node target;

		/**
		 * Used when route is successful
		 * 
		 * @param flow
		 * @param source
		 * @param target
		 */
		public RouteResult(Flow flow, Node source, Node target) {
			this.successful = true;
			this.flow = flow;
			this.source = source;
			this.target = target;
		}

		/**
		 * Used when route is failed
		 * 
		 * @param source
		 * @param target
		 */
		public RouteResult(Node source, Node target) {
			this.successful = false;
			this.flow = null;
			this.source = source;
			this.target = target;
		}

		public final boolean isSuccessful() {
			return successful;
		}
		public final Flow getFlow() {
			assert this.successful : "A failed route's flow should not be accessed\n";
			return flow;
		}
		public final Node getSource() {
			return source;
		}
		public final Node getTarget() {
			return target;
		}
	}

	/**
	 * Get the route result of two nodes
	 * 
	 * @param sourceUUID
	 *            source node's UUID
	 * @param targetUUID
	 *            target node's UUID
	 * @return route's result
	 * @author Hongze Zhao
	 */
	RouteResult route(UUID sourceUUID, UUID targetUUID);

	/**
	 * Whether the data center network contains the node
	 * 
	 * @param nodeUUID
	 * @return
	 * @author Hongze Zhao
	 */
	boolean containNode(UUID nodeUUID);

	/**
	 * random fail the servers and all their connected links
	 * 
	 * @param ration
	 * @author Hongze Zhao
	 */
	void randomFailServers(double ration);

	/**
	 * Random fail the switches and all their connected switches
	 * 
	 * @param ration
	 * @author Hongze Zhao
	 */
	void randomFailSwitches(double ration);

	/**
	 * random fail the links
	 * 
	 * @param ration
	 * @author Hongze Zhao
	 */
	void randomFailLinks(double ration);

	/**
	 * Reset all elements' status Include failure, flow assignment
	 * 
	 * @author Hongze Zhao
	 */
	void reset();
	/**
	 * Get the list of servers' UUIDs
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	List<UUID> getServerUUIDs();
}
