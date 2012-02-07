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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: Define the IP address in the data center network
 * 
 * @author Hongze Zhao Create At : Jan 24, 2012 7:43:19 PM
 */
public class IPAddr {

	private List<Integer> addrs = new ArrayList<Integer>();

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

	static public IPAddr getCommonPrefix(IPAddr a1, IPAddr a2) {
		List<Integer> temp = new LinkedList<Integer>();
		int length = Math.min(a1.getLength(), a2.getLength());
		for (int i = 0; i < length; i++) {
			if (a1.getSegment(i) == a2.getSegment(i)) {
				temp.add(a1.getSegment(i));
			} else {
				break;
			}
		}
		return new IPAddr(temp);
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

	/**
	 * the number of segments
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	public int getLength() {
		return this.addrs.size();
	}

	/**
	 * Append any number of segments and copy and return
	 * 
	 * @param addrList
	 * @return appended IPAddr
	 * @author Hongze Zhao
	 */
	public IPAddr appendAndCopy(int... addrList) {
		List<Integer> addr = new LinkedList<Integer>();
		addr.addAll(this.addrs);
		for (int i = 0; i < addrList.length; i++) {
			addr.add(addrList[i]);
		}
		return new IPAddr(addr);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IPAddr)) {
			return false;
		}
		// obj is an instance of IPAddr
		List<Integer> ad = ((IPAddr) obj).addrs;
		if (ad.size() != this.addrs.size()) {
			return false;
		}
		for (int i = 0; i < ad.size(); i++) {
			if (this.addrs.get(i) != ad.get(i)) {
				return false;
			}
		}
		return true;
	}

	public IPAddr() {
	}
	public IPAddr(String addrStr) {
		this.addrs = IPAddr.parseAddrs(addrStr);
	}

	public IPAddr(List<Integer> addrList) {
		this.addrs = addrList;
	}

	public IPAddr(IPAddr ip) {
		this.addrs.addAll(ip.addrs);
	}

}
