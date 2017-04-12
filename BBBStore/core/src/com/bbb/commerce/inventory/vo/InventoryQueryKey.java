package com.bbb.commerce.inventory.vo;

import java.io.Serializable;

/**
 * This class is data holder for Inventory Query
 * 
 * @author jpadhi
 * @version $Revision: #1 $
 */

public class InventoryQueryKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* ===================================================== *
	 	MEMBER VARIABLES
	 * ===================================================== */
	
	private String mSkuId;
	private String mStoreId;
	private String mSiteId;
	
	/* ===================================================== *
	 	GETTERS and SETTERS
	 * ===================================================== */
	public String getSkuId() {
		return mSkuId;
	}
	public void setSkuId(final String skuId) {
		this.mSkuId = skuId;
	}
	public String getStoreId() {
		return mStoreId;
	}
	public void setStoreId(final String storeId) {
		this.mStoreId = storeId;
	}
	public String getSiteId() {
		return mSiteId;
	}
	public void setSiteId(final String siteId) {
		this.mSiteId = siteId;
	}
	
	
	
}
