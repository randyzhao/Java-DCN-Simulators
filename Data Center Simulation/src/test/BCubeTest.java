/**  
* Filename:    BCubeTest.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 18, 2012 8:17:23 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 18, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package test;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import randy.IDCN.RouteResult;
import randy.DCNs.BCube;

/**
 *
 * @author Hongze Zhao
 * Create At : Feb 18, 2012 8:17:23 PM
 */
public class BCubeTest {

	BCube bcube;
	/**
	 * @throws java.lang.Exception
	 * @author Hongze Zhao	
	 */
	@Before
	public void setUp() throws Exception {
		this.bcube = new BCube(4, 2);
	}

	/**
	 * Test method for {@link randy.DCNs.BCube#route(java.util.UUID, java.util.UUID)}.
	 */
	@Test
	public void testRoute() {
		List<UUID> uuids = this.bcube.getServerUUIDs();
		for (int i = 0; i < uuids.size(); i++) {
			for (int j = i + 1; j < uuids.size(); j++) {
				RouteResult result = this.bcube.route(uuids.get(i),
						uuids.get(j));
				Assert.assertTrue(result.isSuccessful());
				Assert.assertTrue(result.getFlow().isSorted());
			}
		}
	}

	private int pow(int base, int power) {
		int temp = 1;
		for (int i = 0; i < power; i++) {
			temp *= base;
		}
		return temp;
	}
	/**
	 * Test method for {@link randy.DCNs.BCube#BCube(int, int)}.
	 */
	@Test
	public void testBCube() {
		for (int i = 2; i <= 8; i += 2) {
			for (int j = 0; j < 5; j++) {
				BCube bcube = new BCube(i, j);
				Assert.assertTrue(bcube.serversCount() == this.pow(i, j + 1));
				Assert.assertTrue(
						"i is " + i + " j is " + j + " Expect : "
								+ this.pow(i, j)
						* (j + 1)
						+ " Actual : " + bcube.switchesCount(),
						bcube.switchesCount() == this.pow(i, j)
						* (j + 1));
			}
		}
	}

}
