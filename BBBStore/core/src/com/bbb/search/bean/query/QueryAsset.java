/**
 * 
 */
package com.bbb.search.bean.query;

import java.io.Serializable;

/**
 * @author agoe21
 *
 */
public class QueryAsset implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean mNeedsResults;
	
	private int mCount;
	
	private AssetType mAssetType;

	/**
	 * @return the needsResults
	 */
	public boolean isNeedsResults() {
		return mNeedsResults;
	}

	/**
	 * @param pNeedsResults the needsResults to set
	 */
	public void setNeedsResults(final boolean pNeedsResults) {
		this.mNeedsResults = pNeedsResults;
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
	 * @return the assetType
	 */
	public AssetType getAssetType() {
		return mAssetType;
	}

	/**
	 * @param pAssetType the assetType to set
	 */
	public void setAssetType(final AssetType pAssetType) {
		this.mAssetType = pAssetType;
	}
}
