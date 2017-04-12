/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;

/**
 * @author agoe21
 *
 */
public class Facet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int mCount;
	
	private List<String> mChildFacets;
	
	private boolean mInMultiSelect;

	/**
	 * @return the count
	 */
	public int getCount() {
		return mCount;
	}

	/**
	 * @param pCount the count to set
	 */
	public void setCount(final int pCount) {
		this.mCount = pCount;
	}

	/**
	 * @return the childFacets
	 */
	public List<String> getChildFacets() {
		return mChildFacets;
	}

	/**
	 * @param pChildFacets the childFacets to set
	 */
	public void setChildFacets(final List<String> pChildFacets) {
		this.mChildFacets = pChildFacets;
	}

	/**
	 * @return the inMultiSelect
	 */
	public boolean isInMultiSelect() {
		return mInMultiSelect;
	}

	/**
	 * @param pInMultiSelect the inMultiSelect to set
	 */
	public void setInMultiSelect(final boolean pInMultiSelect) {
		this.mInMultiSelect = pInMultiSelect;
	}

}
