package test;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import randy.IDCN.RouteResult;
import randy.DCNs.DCell;
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
	public void testGetServerUUIDs() {
		List<UUID> uuids = this.dcell.getServerUUIDs();
		for (UUID uuid : uuids) {
			Assert.assertTrue(this.dcell.getServer(uuid).getUuid().equals(uuid));
		}
	}

	@Test
	public void testDCellRouting() {
		List<UUID> uuids = this.dcell.getServerUUIDs();
		for (int i = 0; i < uuids.size(); i++) {
			for (int j = 0; j < uuids.size(); j++) {
				if (i == j) {
					continue;
				}
				RouteResult result = this.dcell.route(uuids.get(i),
						uuids.get(j));
				Assert.assertTrue(result.isSuccessful());
				Assert.assertTrue(result.getFlow().isSorted());
				Assert.assertTrue(result.getFlow().isValid());
			}

		}

	}

}
