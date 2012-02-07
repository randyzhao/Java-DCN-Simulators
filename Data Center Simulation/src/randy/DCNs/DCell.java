/**  
* Filename:    DCell.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Jan 24, 2012 9:35:37 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Jan 24, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs;

import java.util.UUID;

import randy.BaseDCN;
import randy.ConstantManager;
import randy.components.IPAddr;
import randy.components.Node;
/**Description:
 * @author Hongze Zhao
 * Create At : Jan 24, 2012 9:35:37 PM
 */
public class DCell extends BaseDCN {

	/**
	 * number of servers in DCell_0
	 */
	private int n;
	/**
	 * highest level
	 */
	private int l;

	public void BuildDCells(IPAddr pref, int n, int l){
		this.n = n;
		if (l == 0){//build DCell_0
			Node Switch = new Node("switch");
			this.addSwitch(Switch);
			for (int i = 0; i < n; i++){//connect node[pref, i] to its switch
				Node server = new Node("server");
				IPAddr newAddr = new IPAddr(pref);
				newAddr.appendSegment(i);
				server.setAddr(newAddr);
				this.connectNode(Switch, server, ConstantManager.LINK_BANDWIDTH);
			}
			return;
		}
		

		//l != 0
		//part II
		int gl = this.getGk(l, n);
		for (int i = 0; i < gl; i++) {// build the DCell_{l - 1}s
			IPAddr newAddr = new IPAddr(pref);
			newAddr.appendSegment(i);
			this.BuildDCells(newAddr, n, l - 1);
		}
		// part III
		int tlminus1 = this.getTk(l - 1, n);
		for (int i = 0; i < tlminus1; i++) {// connect the DCell_{l - 1}s
			int tempgl = this.getGk(l, n);
			for (int j = i + 1; j < tempgl; j++) {
				int uid1 = j - 1;
				int uid2 = i;
				IPAddr addr1 = pref.appendAndCopy(i, uid1);
				IPAddr addr2 = pref.appendAndCopy(j, uid2);
				this.connectNode(addr1, addr2, ConstantManager.LINK_BANDWIDTH);

			}

		}
	}

	/**
	 * Calculate g_k
	 * 
	 * @param k
	 * @return g_k
	 * @author Hongze Zhao
	 */
	private int getGk(int k, int n) {
		if (k == 0) {
			return 1;
		} else {
			return this.getTk(k - 1, n) + 1;
		}
	}

	/**
	 * Calculate t_k
	 * 
	 * @param k
	 * @return
	 * @author Hongze Zhao
	 */
	private int getTk(int k, int n) {
		if (k == 0){
			return n;
		} else {
			return this.getGk(k, n) * this.getTk(k - 1, n);
		}
	}

	/* (non-Javadoc)
	 * @see randy.BaseDCN#route(java.util.UUID, java.util.UUID)
	 */
	@Override
	public RouteResult route(UUID sourceUUID, UUID targetUUID) {
		// TODO Auto-generated method stub
		return null;
	}

}
