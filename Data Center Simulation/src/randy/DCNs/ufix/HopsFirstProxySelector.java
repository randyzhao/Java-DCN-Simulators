/**  
* Filename:    HopsFirstProxySelector.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 4:33:10 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import java.util.Collections;
import java.util.Comparator;

import randy.DCNs.UFix.UFixDomain;
import randy.components.Node;

/**
 *
 * @author Hongze Zhao
 * Create At : Feb 19, 2012 4:33:10 PM
 */
public class HopsFirstProxySelector extends BandwidthHopsAwareProxySelector {

	/* (non-Javadoc)
	 * @see randy.DCNs.ufix.BandwidthHopsAwareProxySelector#select(randy.DCNs.UFix.UFixDomain)
	 */
	@Override
	public void select(UFixDomain domain) {
		this.domain = domain;
		this.processHopsBandwidth();
		Collections.sort(this.proxy, new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				assert HopsFirstProxySelector.this.hops.containsKey(o1)
						&& HopsFirstProxySelector.this.hops.containsKey(o2)
						&& HopsFirstProxySelector.this.bandwidth
								.containsKey(o1)
						&& HopsFirstProxySelector.this.bandwidth
								.containsKey(o2);
				
				int hop1 = HopsFirstProxySelector.this.hops.get(o1);
				int hop2 = HopsFirstProxySelector.this.hops.get(o2);
				double b1 = HopsFirstProxySelector.this.bandwidth.get(o1);
				double b2 = HopsFirstProxySelector.this.bandwidth.get(o2);
				if (hop1 < hop2) {
					return 1;
				} else if (hop1 > hop2) {
					return -1;
				} else {
					if (b1 > b2) {
						return 1;
					} else if (b1 < b2) {
						return -1;
					}else{
						return 0;
					}
				}
			}
		});

		int u = this.domain.getU();
		for (int i = 0; i < u; i++) {
			this.domain.addPlanToUseProxyServer(this.proxy.get(i));
		}

	}

}
