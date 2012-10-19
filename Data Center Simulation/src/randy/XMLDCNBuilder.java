/**  
 * Filename:    XMLDCNBuilder.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Oct 19, 2012 10:08:14 AM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Oct 19, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy;

import org.w3c.dom.Element;

import randy.DCNs.BCube;
import randy.DCNs.DCell;
import randy.DCNs.FatTree;
import randy.DCNs.UFix;
/**
 *
 * @author Hongze Zhao
 * Create At : Oct 19, 2012 10:08:14 AM
 */
public class XMLDCNBuilder {

	public static BaseDCN fromXMLElement(Element ele) {
		String dcnName = ele.getAttribute("name");
		if (dcnName.equals("FatTree")) {
			return FatTree.fromXMLElement(ele);
		}
		if (dcnName.equals("BCube")) {
			return BCube.fromXMLElement(ele);
		}
		if (dcnName.equals("DCell")) {
			return DCell.fromXMLElement(ele);
		}
		if (dcnName.equals("UFix")) {
			return UFix.fromXMLElement(ele);
		}
		return null;
	}
	/**
	 * @param args
	 * @author Hongze Zhao	
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
