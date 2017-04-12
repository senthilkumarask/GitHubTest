package com.bbb.commerce.inventory.vo;

import java.io.Serializable;

/**
 * This class is VO used as data holder in Inventory Query.
 * 
 * @author jpadhi
 * @version $Revision: #1 $
 */

public class BBBStoreInventoryVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * ===================================================== * MEMBER VARIABLES
	 * =====================================================
	 */
	private long mThresholdLimited;
	private long mThresholdAvailable;
	private long mStoreInventoryStock;
	private String mStoreId;
	private String skuId;

	/*
	 * ===================================================== * GETTERS and
	 * SETTERS =====================================================
	 */
	public String getStoreId() {
		return mStoreId;
	}

	public void setStoreId(String mStoreId) {
		this.mStoreId = mStoreId;
	}

	public long getThresholdLimited() {
		return mThresholdLimited;
	}

	public void setThresholdLimited(long thresholdLimited) {
		this.mThresholdLimited = thresholdLimited;
	}

	public long getThresholdAvailable() {
		return mThresholdAvailable;
	}

	public void setThresholdAvailable(long thresholdAvailable) {
		this.mThresholdAvailable = thresholdAvailable;
	}

	public long getStoreInventoryStock() {
		return mStoreInventoryStock;
	}

	public void setStoreInventoryStock(long storeInventoryStock) {
		this.mStoreInventoryStock = storeInventoryStock;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

}
