package test;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import randy.DCNs.DCell;
import randy.components.Flow;
import randy.components.IPAddr;
public class DCellTest {

	private DCell dcell;
	@Before
	public void setUp() throws Exception {
		this.dcell = new DCell(4, 2);
	}

	@Test
	public void testDCell() {
		for (int i = 2; i <= 4; i++) {
			for (int j = 0; j <= 2; j++) {
				try {
					this.dcell = new DCell(i, j);
				} catch (Exception ex) {
					fail("construction DCell(" + i + " , " + j + ") fails");
				}
			}
		}
		Assert.assertTrue(true);
	}

	@Test
	public void testDCell0Link() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetLink() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetServerUUIDs() {
		List<UUID> uuids = this.dcell.getServerUUIDs();
		for (UUID uuid : uuids) {
			Assert.assertTrue(this.dcell.getServer(uuid).getUuid().equals(uuid));
		}
	}

	@Test
	public void testDCellRouting() {
//		List<UUID> uuids = this.dcell.getServerUUIDs();
//		for (int i = 0; i < uuids.size(); i++){
//			for (int j = 0; j < uuids.size(); j++){
//				if (i != j){
//					Node source = this.dcell.getServer(uuids.get(i));
//					Node target = this.dcell.getServer(uuids.get(j));
//					Assert.assertNotNull("source is null", source);
//					Assert.assertNotNull("target is null", target);
//					try {
//						Flow flow = this.dcell.DCellRouting(source, target);
//						Assert.assertTrue(flow.getLinks().size() != 0);
//					} catch (Exception ex) {
//						fail("i is " + i + " j is " + j + " test DCellRouting("
//								+ source.getAddr().toString()
//								+ " , " + target.getAddr().toString()
//								+ ") fails");
//					}
//				}
//			}
//		}
		
		//Routing in DCell_0
		IPAddr addr1 = new IPAddr(new Integer[]{0, 0, 1});
		IPAddr addr2 = new IPAddr(new Integer[]{0, 0, 0});
		Flow flow = this.dcell.DCellRouting(this.dcell.getServer(addr1),
				this.dcell.getServer(addr2));
		Assert.assertTrue(flow.getLinks().size() != 0);

		// Routing in DCell_1
		IPAddr addr3 = new IPAddr(new Integer[] { 0, 1, 2 });
		Flow flow1 = this.dcell.DCellRouting(this.dcell.getServer(addr1),
				this.dcell.getServer(addr3));
		Assert.assertTrue(flow1.getLinks().size() != 0);

		// Routing in DCell_2
		IPAddr addr4 = new IPAddr(new Integer[] { 1, 2, 3 });
		Flow flow2 = this.dcell.DCellRouting(this.dcell.getServer(addr1),
				this.dcell.getServer(addr4));
		Assert.assertTrue(flow2.getLinks().size() != 0);

	}

}
