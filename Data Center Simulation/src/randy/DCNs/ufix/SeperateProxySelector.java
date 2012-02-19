/**  
* Filename:    SeperateProxySelector.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 4:37:56 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import java.util.ArrayList;
import java.util.List;

import randy.DCNs.UFix.UFixDomain;
import randy.components.Node;

/**
 * Select proxy seperatedly
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 4:37:56 PM
 */
public class SeperateProxySelector implements IProxySelector {

	/* (non-Javadoc)
	 * @see randy.DCNs.ufix.IProxySelector#select(randy.DCNs.UFix.UFixDomain)
	 */
	@Override
	public void select(UFixDomain domain) {
		List<Node> proxy = new ArrayList<Node>(domain.getX());
		proxy.addAll(domain.getAvailableProxyServers());
		double ratio = (domain.getX() / domain.getU());
		double temp = ratio + 0.001;
		int count = 0;
		int seq = 0;
		while (count < domain.getU()) {
			temp += 1;
			if (temp > ratio) {
				domain.addPlanToUseProxyServer(proxy.get(seq));
				temp -= ratio;
				count++;
			}
			seq++;
		}
	}

}
