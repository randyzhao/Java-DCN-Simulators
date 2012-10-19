/**  
 * Filename:    XMLSimulatorRunner.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Oct 19, 2012 10:54:28 AM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Oct 19, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Hongze Zhao Create At : Oct 19, 2012 10:54:28 AM
 */
public class XMLSimulatorRunner {

	public static void runFromXMLElement(Element ele, IDCN dcn) {
		// TODO:
		NodeList simNodeList = ele.getElementsByTagName("simulator");
		if (simNodeList.getLength() != 1) {
			System.err.println("a testcase should contain one simulator");
		}
		Node simNode = simNodeList.item(0);
		Element simElement = (Element) simNode;
		String simName = simElement.getAttribute("name");
		if (simName.equals("UFixSimulator")) {
			ISimulator sim = new UFixSimulator(dcn);
			sim.initialize();
			sim.run();
			try {
				System.out
				.println(String
						.format("ABT %1f \nThroughput per Port %2f\nAGT %3f\nServer Num %4f",
								sim.getMetric("ABT"),
								sim.getMetric("ThroughputPerLink"),
								sim.getMetric("AGT"),
								sim.getMetric("ServerNum")));
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		} else {
			System.err.println("Simulator unsupported");
		}
	}

	/**
	 * @param args
	 * @author Hongze Zhao
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
