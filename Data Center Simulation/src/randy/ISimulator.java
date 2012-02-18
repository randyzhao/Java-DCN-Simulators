/**  
* Filename:    ISimulator.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 18, 2012 3:56:36 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 18, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy;

/**
 * The interface of the simulator
 * 
 * @author Hongze Zhao Create At : Feb 18, 2012 3:56:36 PM
 */
public interface ISimulator {
	/**
	 * A exception indicates that a required metric is not found in the
	 * simulator
	 * 
	 * @author Hongze Zhao Create At : Feb 18, 2012 4:52:38 PM
	 */
	public class MetricNotFoundException extends Exception {
		String requiredMetricName;

		public MetricNotFoundException(String requiredMetricName) {
			this.requiredMetricName = requiredMetricName;
		}

		@Override
		public String getMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append("The reauired metric ");
			sb.append(this.requiredMetricName);
			sb.append(" is not found");
			return sb.toString();
		}

	}



	/**
	 * Initialize the simulation environment
	 * 
	 * @author Hongze Zhao
	 */
	void initialize();

	/**
	 * Run the simulation
	 * 
	 * @author Hongze Zhao
	 */
	void run();

	/**
	 * get a metric throught metric name
	 * 
	 * @param name
	 * @return
	 * @author Hongze Zhao
	 */
	double getMetric(String name) throws MetricNotFoundException;
	/**
	 * Get result
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	String result();

	/**
	 * Reset all the status
	 * 
	 * @author Hongze Zhao
	 */
	void reset();

}
