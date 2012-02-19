/**  
* Filename:    LinkConnector.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 2:39:43 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import randy.DCNs.UFix;

/**
 * Used to execute linkConnecting
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 2:39:43 PM
 */
public abstract class LinkConnector {
	protected UFix ufix;

	public LinkConnector(UFix ufix) {
		this.ufix = ufix;
	}

	abstract public void connect();
}
