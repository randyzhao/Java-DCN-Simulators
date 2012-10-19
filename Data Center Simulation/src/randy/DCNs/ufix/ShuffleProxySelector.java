/**  
 * Filename:    ShuffleProxySelector.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Sep 25, 2012 12:56:02 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Sep 25, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy.DCNs.ufix;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import randy.DCNs.UFix.UFixDomain;
import randy.components.Node;

/**
 *
 * @author Hongze Zhao
 * Create At : Sep 25, 2012 12:56:02 PM
 */
public class ShuffleProxySelector implements IProxySelector {

	/* (non-Javadoc)
	 * @see randy.DCNs.ufix.IProxySelector#select(randy.DCNs.UFix.UFixDomain)
	 */
	@Override
	public void select(UFixDomain domain) {
		List<Node> proxy = new LinkedList<Node>();
		proxy.addAll(domain.getAvailableProxyServers());
		// System.out.println("[ShuffleProxySelector.select]: add " +
		// proxy.size()
		// + " proxies");
		Collections.shuffle(proxy);
		for (Node n : proxy) {
			domain.addPlanToUseProxyServer(n);
			// System.out.println("[ShuffleProxySelector.select]: UUID: "
			// + n.getUuid().toString());
		}

	}

	/**
	 * @param args
	 * @author Hongze Zhao	
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
