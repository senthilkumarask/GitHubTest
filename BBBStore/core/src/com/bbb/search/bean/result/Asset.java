/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;


/**
 * @author agoe21
 *
 */
public class Asset implements Serializable{

	private static final long serialVersionUID = 1L;

	private int mCount;
	
	private boolean mHasResults;
	
	private String mQuery;

	private String assetFilter;
	

	/**
	 * @return the assetFilter
	 */
	public String getAssetFilter() {
		return assetFilter;
	}

	/**
	 * @param assetFilter the assetFilter to set
	 */
	public void setAssetFilter(String assetFilter) {
		this.assetFilter = assetFilter;
	}

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
	 * @return the hasResults
	 */
	public boolean isHasResults() {
		return mHasResults;
	}

	/**
	 * @param pHasResults the hasResults to set
	 */
	public void setHasResults(final boolean pHasResults) {
		this.mHasResults = pHasResults;
	}

	/**
	 * @return the mQuery
	 */
	public String getQuery() {
		return mQuery;
	}

	/**
	 * @param mQuery the mQuery to set
	 */
	public void setQuery(String mQuery) {
		this.mQuery = mQuery;
	}
}
