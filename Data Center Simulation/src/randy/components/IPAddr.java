/**  
* Filename:    IPAddr.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Jan 24, 2012 7:43:19 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Jan 24, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.components;

import java.util.LinkedList;
import java.util.List;

/**
 * Description: Define the IP address in the data center network
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 7:43:19 PM
 */
public class IPAddr {

	private List<Integer> addrs;

	/**
	 * Parse IP address from a string the format should be
	 * "XXX.XXX.XXX.XXX....."
	 * 
	 * @param addrStr
	 *            the address string
	 * @return a list of number of each position
	 * @author Hongze Zhao
	 */
	static public List<Integer> parseAddrs(String addrStr) {
		List<Integer> output = new LinkedList<Integer>();
		String[] s = addrStr.split(".");
		for (int i = 0; i < s.length; i++) {
			output.add(Integer.parseInt(s[i]));
		}
		return output;
	}

	/**
	 * Get a segment of the IP addrs
	 * 
	 * @param segID
	 *            the id of the segment, which is started from 0
	 * @return the content of the given segment
	 * @author Hongze Zhao
	 */
	public int getSegment(int segID) {
		return this.addrs.get(segID);
	}

	/**
	 * Insert a segment to the head
	 * 
	 * @param content
	 * @author Hongze Zhao
	 */
	public void insertSegment2Head(int content) {
		this.addrs.add(0, content);
	}
	/**
	 * Append a segment to the IP addr
	 * 
	 * @param content
	 *            the content of the segment
	 * @author Hongze Zhao
	 */
	public void appendSegment(int content) {
		this.addrs.add(content);
	}

	/**
	 * Remove a segment from the IP addr
	 * 
	 * @author Hongze Zhao
	 */
	public void minusSegment() {
		this.addrs.remove(this.addrs.size() - 1);
	}

	public IPAddr() {
	}

	public IPAddr(String addrStr) {
		this.addrs = IPAddr.parseAddrs(addrStr);
	}

	public IPAddr(List<Integer> addrList) {
		this.addrs = addrList;
	}
}
