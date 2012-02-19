/**  
* Filename:    GeneralLinkCounter.java  
* Description:   
* Copyright:   Copyright (c)2011 
* Company:    company 
* @author:     Hongze Zhao 
* @version:    1.0  
* Create at:   Feb 19, 2012 2:42:32 PM  
*  
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* Feb 19, 2012    Hongze Zhao   1.0         1.0 Version  
*/
package randy.DCNs.ufix;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import randy.DCNs.UFix;
import randy.DCNs.UFix.UFixDomain;

/**
 * implementation of LinkCounting Algorithm in the paper
 * 
 * @author Hongze Zhao Create At : Feb 19, 2012 2:42:32 PM
 */
public class GeneralLinkCounter implements ILinkCounter {

	private UFix ufix;
	/* (non-Javadoc)
	 * @see randy.DCNs.ufix.LinkCounter#count()
	 */
	@Override
	public void count(UFix ufix) {
		this.ufix = ufix;
		List<UFixDomain> domains = this.ufix.getDomains();
		int m = domains.size();
		int meshN = Integer.MAX_VALUE;
		for (UFixDomain d : domains){
			if (d.getU() < meshN){
				meshN = d.getU();
			}
		}
		meshN /= (m - 1);
		
		int v[] = new int[domains.size()];
		Arrays.fill(v, 0);
		
		for (int i = 0; i < m; i++) {
			v[i] = domains.get(i).getU() - meshN * (m - 1);
			for (int j = 0; j < m; j++) {
				if (j == i){
					continue;
				}
				this.ufix.addLinkCount(i, j, meshN);
			}
		}

		while (this.sigmavi(v) > this.maxvi(v)) {
			int p = this.argmaxvi(v);
			HashSet<Integer> U = this.buildU(v, p);
			while (v[p] > 0 && !U.isEmpty()) {
				int q = this.argmaxviinU(v, U);
				assert U.contains(q);
				U.remove(q);
				v[p]--;
				v[q]--;
				this.ufix.addLinkCount(p, q);
			}

		}
	}

	/**
	 * Calculate \sum v_i
	 * 
	 * @param v
	 * @return
	 * @author Hongze Zhao
	 */
	private int sigmavi(int v[]) {
		int output = 0;
		for (int i : v) {
			output += i;
		}
		return output;
	}

	/**
	 * calculate \max{v_i}
	 * 
	 * @param v
	 * @return
	 * @author Hongze Zhao
	 */
	private int maxvi(int v[]) {
		int max = Integer.MIN_VALUE;
		for (int i : v) {
			if (i > max) {
				max = i;
			}
		}
		return max;
	}

	/**
	 * calculate argmax_i {v_i}
	 * 
	 * @param v
	 * @return
	 * @author Hongze Zhao
	 */
	private int argmaxvi(int v[]) {
		int max = Integer.MIN_VALUE;
		int argmax = 0;
		for (int i = 0; i < v.length; i++) {
			if (v[i] > max) {
				max = v[i];
				argmax = i;
			}
		}
		return argmax;
	}

	/**
	 * U is the set of v_i when v_i > 0 && i != p calculate argmax v_i \in U
	 * 
	 * @param v
	 * @param p
	 * @return
	 * @author Hongze Zhao
	 */
	private int argmaxviinU(int[] v, HashSet<Integer> U) {
		int max = 0;
		int q = -1;
		Iterator<Integer> i = U.iterator();
		while (i.hasNext()) {
			int seq = i.next();
			int temp = v[seq];
			if (temp > max) {
				max = temp;
				q = seq;
			}
		}
		return q;
	}

	/**
	 * Build set U in the paper
	 * 
	 * @param v
	 * @param p
	 * @return
	 * @author Hongze Zhao
	 */
	private HashSet<Integer> buildU(int v[], int p) {
		HashSet<Integer> output = new HashSet<Integer>();
		for (int i = 0; i < v.length; i++) {
			if (i == p) {
				continue;
			}
			if (v[i] > 0) {
				output.add(i);
			}
		}
		return output;
	}

}
