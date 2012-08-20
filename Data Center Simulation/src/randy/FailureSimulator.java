/**  
* Filename:    FailureSimulator.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 18, 2012 4:42:16 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 18, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy;

import randy.DCNs.FatTree;

/**
 * the simulator which could support switch failure, server failure and link
 * failure
 * 
 * @author Hongze Zhao Create At : Feb 18, 2012 4:42:16 PM
 */
public class FailureSimulator extends GeneralSimulator {

	private final double switchFailRatio;
	private final double serverFailRatio;
	private final double linkFailRatio;

	public FailureSimulator(double switchFailRatio, double serverFailRatio,
			double linkFailRatio, IDCN dcn) {
		this.switchFailRatio = switchFailRatio;
		this.serverFailRatio = serverFailRatio;
		this.linkFailRatio = linkFailRatio;
		this.dcn = dcn;
	}
	/* (non-Javadoc)
	 * @see randy.GeneralSimulator#initialize()
	 */
	@Override
	public void initialize() {
		this.dcn.reset();
		this.dcn.randomFailLinks(this.linkFailRatio);
		this.dcn.randomFailServers(this.serverFailRatio);
		this.dcn.randomFailSwitches(this.switchFailRatio);

	}

	/**
	 * @param args
	 * @author Hongze Zhao	
	 */
	public static void main(String[] args) {
		for (double sfr = 0; sfr < 1.001; sfr += 0.05) {
			FailureSimulator sim = new FailureSimulator(0, 0, sfr, new FatTree(
					10));
			sim.initialize();
			sim.run();
			try {
				Double abt = sim.getMetric("ABT");
				System.out.println(abt.intValue());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

	}

}
