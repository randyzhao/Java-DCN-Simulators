/**  
* Filename:    UFixTest.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 8:05:28 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package test;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import randy.BaseDCN;
import randy.IDCN.RouteResult;
import randy.DCNs.BCube;
import randy.DCNs.DCell;
import randy.DCNs.FatTree;
import randy.DCNs.UFix;

/**
 *
 * @author Hongze Zhao
 * Create At : Feb 19, 2012 8:05:28 PM
 */
public class UFixTest {

	private UFix ufix;

	/**
	 * @throws java.lang.Exception
	 * @author Hongze Zhao	
	 */
	@Before
	public void setUp() throws Exception {
		this.ufix = new UFix(0.5, new BCube(4, 1), new FatTree(8), new DCell(4,
				1));
	}

	/**
	 * Test method for {@link randy.DCNs.UFix#route(java.util.UUID, java.util.UUID)}.
	 */
	@Test
	public void testRoute() {
		List<UUID> uuids = this.ufix.getServerUUIDs();
		for (int i = 0; i < uuids.size(); i++) {
			for (int j = 0; j < uuids.size(); j++) {
				if (i == j) {
					continue;
				}
				RouteResult result = this.ufix
						.route(uuids.get(i), uuids.get(j));
				Assert.assertTrue(result.isSuccessful());
				Assert.assertTrue("fail for " + result.getFlow().toString(),
						result.getFlow().isSorted());
				Assert.assertTrue(result.getFlow().isValid());
			}
		}
	}

	/**
	 * Test method for {@link randy.DCNs.UFix#UFix(double, randy.BaseDCN[])}.
	 */
	@Test
	public void testUFix() {
		BaseDCN dcn1 = new BCube(4, 1), dcn2 = new FatTree(8), dcn3 = new DCell(
				4, 1);
		Assert.assertTrue(this.ufix.getServers().size() == dcn1.getServers()
				.size() + dcn2.getServers().size() + dcn3.getServers().size());
		Assert.assertTrue(this.ufix.getSwitches().size() == dcn1.getSwitches()
				.size() + dcn2.getSwitches().size() + dcn3.getSwitches().size());
		Assert.assertTrue(this.ufix.getLinks().size() > dcn1.getLinks().size()
				+ dcn2.getLinks().size() + dcn3.getLinks().size());
	}

}
