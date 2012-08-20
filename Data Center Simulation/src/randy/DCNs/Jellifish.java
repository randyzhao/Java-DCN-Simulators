/**  
 * Filename:    Jellifish.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Aug 20, 2012 8:44:17 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Aug 20, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy.DCNs;

import java.util.UUID;

import randy.BaseDCN;

/**
 *
 * @author Hongze Zhao
 * Create At : Aug 20, 2012 8:44:17 PM
 */
public class Jellifish extends BaseDCN {


	/**
	 * 
	 * @param torSize
	 *            the number of servers under a TOR switch
	 * @param torCount
	 *            the total number of TOR switches
	 * @author Hongze Zhao
	 */
	private void buildJellifish(int torSize, int torCount) {
		// TODO:
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see randy.BaseDCN#route(java.util.UUID, java.util.UUID)
	 */
	@Override
	public RouteResult route(UUID sourceUUID, UUID targetUUID) {
		// TODO Auto-generated method stub
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
