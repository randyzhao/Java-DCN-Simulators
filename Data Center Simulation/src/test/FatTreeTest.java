/**  
* Filename:    FatTreeTest.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 17, 2012 1:10:04 AM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 17, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package test;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import randy.IDCN.RouteResult;
import randy.DCNs.FatTree;
import randy.components.Flow;
import randy.components.Node;

/**
 *
 * @author Hongze Zhao
 * Create At : Feb 17, 2012 1:10:04 AM
 */
public class FatTreeTest {

	FatTree fat;

	/**
	 * @throws java.lang.Exception
	 * @author Hongze Zhao	
	 */
	@Before
	public void setUp() throws Exception {
		this.fat = new FatTree(4);
	}

	/**
	 * Test method for {@link randy.DCNs.FatTree#FatTree(int)}.
	 */
	@Test
	public void testFatTree() {
		try {
			for (int i = 4; i <= 10; i += 2) {
				new FatTree(i);

			}
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
		Assert.assertTrue(true);
	}

	/**
	 * Test method for {@link randy.DCNs.FatTree#preRouteCalculation()}.
	 */
	@Test
	public void testPreRouteCalculation() {
		this.fat.preRouteCalculation();
		for (List<Flow> flows : this.fat.flowsBetweenAggeSwitches) {
			for (Flow flow : flows) {
				Assert.assertTrue(flow.toString(), flow.isSorted());
			}
		}
		for (List<Flow> flows : this.fat.flowsBetweenEdgeSwitchesDiffPod) {
			for (Flow flow : flows) {
				Assert.assertTrue(flow.toString(), flow.isSorted());
			}
		}

	}
	
	/**
	 * Test method for
	 * {@link randy.DCNs.FatTree#route(java.util.UUID, java.util.UUID)}.
	 */
	@Test
	public void testRouteMore() {

		// this.fat.route(this.fat
		// .getServer(new IPAddr(new Integer[] { 0, 1, 2 })).getUuid(),
		// this.fat.getServer(new IPAddr(new Integer[] { 6, 2, 3 }))
		// .getUuid());
	}

	/**
	 * Test method for
	 * {@link randy.DCNs.FatTree#route(java.util.UUID, java.util.UUID)}.
	 */
	@Test
	public void testRoute() {
		List<UUID> uuids = this.fat.getServerUUIDs();
		for (int i = 0; i < uuids.size(); i++) {
			for (int j = 0; j < uuids.size(); j++) {
				if (i == j) {
					continue;
				}
				Node source = this.fat.getServer(uuids.get(i));
				Node target = this.fat.getServer(uuids.get(j));
				RouteResult result = this.fat.route(uuids.get(i), uuids.get(j));
				Assert.assertTrue(result.isSuccessful());
				Assert.assertTrue(result.getFlow().getLinks().size() >= 2);
				Assert.assertTrue("fail for " + result.getFlow().toString(),
						result
						.getFlow().isSorted());
				// System.out
				// .println("finish route "
				// + this.fat.getServer(uuids.get(i)).getAddr()
				// .toString()
				// + " to "
				// + this.fat.getServer(uuids.get(j)).getAddr()
				// .toString());
				// System.gc();
			}
		}
	}




}
