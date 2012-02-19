/**  
* Filename:    ProxySelector.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 2:38:12 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import randy.DCNs.UFix;

/**
 * Used to select proper proxy server from ufix
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 2:38:12 PM
 */
public abstract class ProxySelector {
	protected UFix ufix;

	public ProxySelector(UFix ufix) {
		this.ufix = ufix;
	}

	abstract public void connect();
}
