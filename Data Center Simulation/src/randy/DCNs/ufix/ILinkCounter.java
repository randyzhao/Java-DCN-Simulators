/**  
* Filename:    ILinkCounter.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 3:22:30 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import randy.DCNs.UFix;

/**
 * Interface of linkCounter which executes linkCounting procedure in the paper
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 3:22:30 PM
 */
public interface ILinkCounter {
	void count(UFix ufix);
}
