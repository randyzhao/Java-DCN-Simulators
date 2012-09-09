/**  
 * Filename:    OneToOneSimulator.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Aug 22, 2012 12:42:57 AM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Aug 22, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Hongze Zhao
 * Create At : Aug 22, 2012 12:42:57 AM
 */
public class OneToOneSimulator extends GeneralSimulator {


	/**
	 * @param args
	 * @author Hongze Zhao	
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see randy.GeneralSimulator#initialize()
	 */
	@Override
	public void initialize() {
		this.dcn.reset();

	}

	@Override
	protected void preparePairs() {
		Random ran = new Random();
		List<UUID> uuids = new ArrayList<UUID>();
		uuids.addAll(this.dcn.getServerUUIDs());
		while (uuids.size() > 1) {
			int ran1 = ran.nextInt(uuids.size());
			int ran2 = ran1;
			while (ran2 == ran1) {
				ran2 = ran.nextInt(uuids.size());
			}
			UUID home = uuids.get(ran1);
			UUID away = uuids.get(ran2);
			this.pairs.add(new RoutePair(home, away));
			uuids.remove(home);
			uuids.remove(away);
		}
	}

	public OneToOneSimulator(IDCN dcn) {
		this.dcn = dcn;
	}

}
