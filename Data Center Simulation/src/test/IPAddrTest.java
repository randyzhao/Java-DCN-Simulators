/**  
* Filename:    IPAddrTest.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 8, 2012 12:53:21 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 8, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package test;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import randy.components.IPAddr;

/**
 *
 * @author Hongze Zhao
 * Create At : Feb 8, 2012 12:53:21 PM
 */
public class IPAddrTest {

	/**
	 * @throws java.lang.Exception
	 * @author Hongze Zhao	
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link randy.components.IPAddr#parseAddrs(java.lang.String)}.
	 */
	@Test
	public void testParseAddrs() {
		String ad = "1.2.3.4.5";
		List<Integer> output = IPAddr.parseAddrs(ad);
		Integer[] actuals = new Integer[output.size()];
		Assert.assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 },
				output.toArray(actuals));
	}

	/**
	 * Test method for {@link randy.components.IPAddr#getCommonPrefix(randy.components.IPAddr, randy.components.IPAddr)}.
	 */
	@Test
	public void testGetCommonPrefix() {
		IPAddr addr1 = new IPAddr(new Integer[] { 1, 2, 3, 4, 5 });
		IPAddr addr2 = new IPAddr(new Integer[] { 2, 3, 4 });
		IPAddr com1 = IPAddr.getCommonPrefix(addr1, addr2);
		Assert.assertEquals("", com1.toString());
		IPAddr addr3 = new IPAddr(new Integer[] { 1, 2 });
		IPAddr com2 = IPAddr.getCommonPrefix(addr1, addr3);
		Assert.assertEquals("1.2", com2.toString());
		IPAddr addr4 = new IPAddr(new Integer[] { 1, 2, 3, 4, 5, 6 });
		IPAddr com3 = IPAddr.getCommonPrefix(addr1, addr4);
		Assert.assertEquals("1.2.3.4.5", com3.toString());
		IPAddr addr5 = new IPAddr(new Integer[] { 1, 2, 3, 6, 7, 5 });
		IPAddr com4 = IPAddr.getCommonPrefix(addr1, addr5);
		Assert.assertEquals("1.2.3", com4.toString());
	}

	/**
	 * Test method for {@link randy.components.IPAddr#getSegment(int)}.
	 */
	@Test
	public void testGetSegment() {
		Integer[] temp = new Integer[] { 2, 3, 4, 5, 6 };
		IPAddr addr = new IPAddr(temp);
		Integer[] temp1 = new Integer[temp.length];
		for (int i = 0; i < temp1.length; i++) {
			temp1[i] = addr.getSegment(i);
		}
		Assert.assertArrayEquals(temp, temp1);
	}

	/**
	 * Test method for {@link randy.components.IPAddr#insertSegment2Head(int)}.
	 */
	@Test
	public void testInsertSegment2Head() {

	}

	/**
	 * Test method for {@link randy.components.IPAddr#appendSegment(int)}.
	 */
	@Test
	public void testAppendSegment() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#minusSegment()}.
	 */
	@Test
	public void testMinusSegment() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#getLength()}.
	 */
	@Test
	public void testGetLength() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#connect(randy.components.IPAddr)}.
	 */
	@Test
	public void testConnect() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#appendAndCopy(int[])}.
	 */
	@Test
	public void testAppendAndCopy() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#connectAndCopy(randy.components.IPAddr)}.
	 */
	@Test
	public void testConnectAndCopy() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#IPAddr()}.
	 */
	@Test
	public void testIPAddr() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#IPAddr(java.lang.String)}.
	 */
	@Test
	public void testIPAddrString() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#IPAddr(java.util.List)}.
	 */
	@Test
	public void testIPAddrListOfInteger() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#IPAddr(randy.components.IPAddr)}.
	 */
	@Test
	public void testIPAddrIPAddr() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link randy.components.IPAddr#toString()}.
	 */
	@Test
	public void testToString() {
		Integer[] temp = new Integer[] { 1, 2, 3, 4, 5 };
		IPAddr addr = new IPAddr(Arrays.asList(temp));
		Assert.assertEquals("1.2.3.4.5", addr.toString());

	}

}
