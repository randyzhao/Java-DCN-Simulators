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

	private IPAddr a1 = new IPAddr(new Integer[] {});
	private IPAddr a2 = new IPAddr(new Integer[] { 1, 2 });
	private IPAddr a3 = new IPAddr(new Integer[] { 3, 4, 5 });
	/**
	 * @throws java.lang.Exception
	 * @author Hongze Zhao	
	 */
	@Before
	public void setUp() throws Exception {
		this.a1 = new IPAddr(new Integer[] {});
		this.a2 = new IPAddr(new Integer[] { 1, 2 });
		this.a3 = new IPAddr(new Integer[] { 3, 4, 5 });
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
		IPAddr addr1 = new IPAddr(new Integer[] { 1, 2 });
		addr1.insertSegment2Head(0);
		Assert.assertEquals("0.1.2", addr1.toString());
	}

	/**
	 * Test method for {@link randy.components.IPAddr#appendSegment(int)}.
	 */
	@Test
	public void testAppendSegment() {
		IPAddr addr1 = new IPAddr(new Integer[] { 1, 2 });
		addr1.appendSegment(3);
		Assert.assertEquals("1.2.3", addr1.toString());
	}

	/**
	 * Test method for {@link randy.components.IPAddr#minusSegment()}.
	 */
	@Test
	public void testMinusSegment() {
		IPAddr addr1 = new IPAddr(new Integer[] { 1, 2 });
		addr1.minusSegment();
		Assert.assertEquals("1", addr1.toString());
	}

	/**
	 * Test method for {@link randy.components.IPAddr#getLength()}.
	 */
	@Test
	public void testGetLength() {
		IPAddr addr1 = new IPAddr(new Integer[] {});
		Assert.assertEquals(0, addr1.getLength());
		IPAddr addr2 = new IPAddr(new Integer[] { 1, 2, 3 });
		Assert.assertEquals(3, addr2.getLength());
	}

	/**
	 * Test method for {@link randy.components.IPAddr#connect(randy.components.IPAddr)}.
	 */
	@Test
	public void testConnect() {
		this.a1.connect(this.a2);
		Assert.assertEquals("1.2", this.a1.toString());
		this.a1.connect(this.a3);
		Assert.assertEquals("1.2.3.4.5", this.a1.toString());

	}

	/**
	 * Test method for {@link randy.components.IPAddr#appendAndCopy(int[])}.
	 */
	@Test
	public void testAppendAndCopy() {
		IPAddr addr1 = this.a1.appendAndCopy(3, 4, 5);
		Assert.assertEquals("1.2.3.4.5", addr1.toString());
		IPAddr addr2 = addr1.appendAndCopy(7, 8, 9);
		Assert.assertEquals("1.2.3.4.5.7.8.9", addr2.toString());
	}

	/**
	 * Test method for {@link randy.components.IPAddr#connectAndCopy(randy.components.IPAddr)}.
	 */
	@Test
	public void testConnectAndCopy() {
		IPAddr addr1 = this.a1.connectAndCopy(this.a2);
		Assert.assertEquals("1.2", addr1.toString());
		IPAddr addr2 = addr1.connectAndCopy(this.a3);
		Assert.assertEquals("1.2.3.4.5", addr2.toString());
	}

	/**
	 * Test method for {@link randy.components.IPAddr#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		IPAddr addr1 = new IPAddr(new Integer[] {});
		Assert.assertTrue(addr1.equals(this.a1));
		IPAddr addr2 = new IPAddr(new Integer[] { 1, 2 });
		Assert.assertTrue(addr2.equals(this.a2));
	}

	/**
	 * Test method for {@link randy.components.IPAddr#IPAddr(java.lang.String)}.
	 */
	@Test
	public void testIPAddrString() {
		Assert.assertEquals("1.2.3.4.5", (new IPAddr("1.2.3.4.5")));
	}

	/**
	 * Test method for {@link randy.components.IPAddr#IPAddr(java.util.List)}.
	 */
	@Test
	public void testIPAddrListOfInteger() {
		Integer[] temp = new Integer[] { 1, 2, 3, 4, 5 };
		Assert.assertEquals("1.2.3.4.5", (new IPAddr(Arrays.asList(temp))));
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
