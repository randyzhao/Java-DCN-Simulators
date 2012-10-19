/**  
 * Filename:    InterleavingConnector.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Feb 19, 2012 4:51:39 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy.DCNs.ufix;

import java.util.Arrays;
import java.util.List;

import randy.ConstantManager;
import randy.DCNs.UFix;
import randy.DCNs.UFix.UFixDomain;

/**
 * LinkConnector with interleaving
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 4:51:39 PM
 */
public class InterleavingConnector implements ILinkConnector {

	private UFix ufix;

	/**
	 * Calculate the sum of link count currently
	 * 
	 * @return
	 * @author Hongze Zhao
	 */
	private int sigmaLinkCount() {
		int output = 0;
		for (int[] i : this.ufix.getLinkCount()) {
			for (int temp : i) {
				output += temp;
			}
		}
		return output;
	}
	/* (non-Javadoc)
	 * @see randy.DCNs.ufix.ILinkConnector#connect(randy.DCNs.UFix)
	 */
	@Override
	public void connect(UFix ufix) {
		this.ufix = ufix;
		int[][] e = ufix.getLinkCount();
		int m = ufix.getDomains().size();
		List<UFixDomain> domains = ufix.getDomains();
		int[] f = new int[m];
		Arrays.fill(f, 0);
		int sigmae = this.sigmaLinkCount();
		while (sigmae > 0) {
			for (int i = 0; i < m; i++) {
				for (int j = i + 1; j < m; j++) {
					if (e[i][j] <= 0) {
						continue;
					}
					ufix.interConnectProxy(i, j, domains.get(i)
							.getPlannedToUseProxyServer(f[i]), domains.get(j)
							.getPlannedToUseProxyServer(f[j]),
							ConstantManager.LINK_BANDWIDTH);
					f[i]++;
					f[j]++;
					e[i][j]++;
					e[j][i]--;
					sigmae -= 2;
					continue;
				}
			}
		}
	}

}
