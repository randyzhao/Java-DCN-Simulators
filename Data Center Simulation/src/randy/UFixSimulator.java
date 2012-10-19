/**  
 * Filename:    UFixSimulator.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Sep 9, 2012 11:33:28 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Sep 9, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import randy.DCNs.UFix;
import randy.DCNs.UFix.UFixDomain;

/**
 *
 * @author Hongze Zhao
 * Create At : Sep 9, 2012 11:33:28 PM
 */
public class UFixSimulator extends GeneralSimulator {

	/* (non-Javadoc)
	 * @see randy.GeneralSimulator#initialize()
	 */
	@Override
	public void initialize() {
		this.dcn.reset();
	}

	public UFixSimulator(IDCN dcn) {
		this.dcn = dcn;
	}
	/**
	 * @param args
	 * @author Hongze Zhao	
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void preparePairs() {
		UFix ufix = (UFix) this.dcn;
		List<UFixDomain> domains = ufix.getDomains();
		List<List<UUID>> uuidListList = new ArrayList<List<UUID>>();
		for (int i = 0; i < domains.size(); i++) {
			UFix u = (UFix) domains.get(i).getDCN();
			List<UFixDomain> tempDomains = u.getDomains();
			for (int j = 0; j < tempDomains.size(); j++) {
				uuidListList.add(tempDomains.get(j).getDCN().getServerUUIDs());
			}
		}
		for (int i = 0; i < uuidListList.size(); i++) {
			for (int j = i + 1; j < uuidListList.size(); j++) {
				List<UUID> ul1 = uuidListList.get(i);
				List<UUID> ul2 = uuidListList.get(j);
				for (UUID home : ul1) {
					for (UUID away : ul2) {
						this.pairs.add(new RoutePair(home, away));
					}
				}
			}
		}
	}

}
