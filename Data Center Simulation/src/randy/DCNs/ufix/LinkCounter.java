/**  
* Filename:    LinkCounter.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 2:36:30 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import randy.DCNs.UFix;

/**
 * Used to execute linkCounter of UFix
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 2:36:30 PM
 */
public abstract class LinkCounter {
	protected UFix ufix;

	public LinkCounter(UFix ufix) {
		this.ufix = ufix;
	}

	abstract public void count();
}
