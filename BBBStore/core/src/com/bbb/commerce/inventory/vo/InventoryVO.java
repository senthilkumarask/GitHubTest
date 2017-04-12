/**
 * 
 */
package com.bbb.commerce.inventory.vo;

import java.io.Serializable;
import java.sql.Timestamp;



/**
 * @author alakra
 *
 */
public class InventoryVO implements Comparable<InventoryVO>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private volatile int hashCode = 0;
	
	private String mInventoryID;
	
	private String mSiteID;
	
	private String mStoreID;
	
	private String mSkuID;
	
	private Timestamp mCreationDate;
	
	private Timestamp mStartDate;
	
	private Timestamp mEndDate;
	
	private String mDisplayName;
	
	private String mDescription;
	
	private Timestamp mAvailabilityDate;
	
	private Long mGlobalStockLevel;
	
	private Long mSiteStockLevel;
	
	private Long mGiftRegistryStockLevel;
	
	private Long mCatalogThreshold;
	
	private Long mOrderedQuantity;
	
	private Long mBASiteStockLevel;
	
	private Long mBAGiftRegistryStockLevel;
	
	private Long mCASiteStockLevel;
	
	private Long mCAGiftRegistryStockLevel;
	
	private String mDeliveryType;
	/**
	 * 
	 */
	public InventoryVO() {
		// Empty constructor
	}


	/**
	 * @return the inventoryID
	 */
	public String getInventoryID() {
		return mInventoryID;
	}


	/**
	 * @param pInventoryID the inventoryID to set
	 */
	public void setInventoryID(String pInventoryID) {
		mInventoryID = pInventoryID;
	}


	/**
	 * @return the siteID
	 */
	public String getSiteID() {
		return mSiteID;
	}


	/**
	 * @param pSiteID the siteID to set
	 */
	public void setSiteID(String pSiteID) {
		mSiteID = pSiteID;
	}


	/**
	 * @return the storeID
	 */
	public String getStoreID() {
		return mStoreID;
	}


	/**
	 * @param pStoreID the storeID to set
	 */
	public void setStoreID(String pStoreID) {
		mStoreID = pStoreID;
	}


	/**
	 * @return the catalogThreshold
	 */
	public Long getCatalogThreshold() {
		return mCatalogThreshold;
	}


	/**
	 * @param pCatalogThreshold the catalogThreshold to set
	 */
	public void setCatalogThreshold(Long mCatalogThreshold) {
		this.mCatalogThreshold = mCatalogThreshold;
	}


	/**
	 * @return the creationDate
	 */
	public Timestamp getCreationDate() {
		return mCreationDate;
	}
	/**
	 * @param pCreationDate the creationDate to set
	 */
	public void setCreationDate(Timestamp pCreationDate) {
		mCreationDate = pCreationDate;
	}


	/**
	 * @return the startDate
	 */
	public Timestamp getStartDate() {
		return mStartDate;
	}


	/**
	 * @param pStartDate the startDate to set
	 */
	public void setStartDate(Timestamp pStartDate) {
		mStartDate = pStartDate;
	}


	/**
	 * @return the endDate
	 */
	public Timestamp getEndDate() {
		return mEndDate;
	}


	/**
	 * @param pEndDate the endDate to set
	 */
	public void setEndDate(Timestamp pEndDate) {
		mEndDate = pEndDate;
	}


	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return mDisplayName;
	}


	/**
	 * @param pDisplayName the displayName to set
	 */
	public void setDisplayName(String pDisplayName) {
		mDisplayName = pDisplayName;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}


	/**
	 * @param pDescription the description to set
	 */
	public void setDescription(String pDescription) {
		mDescription = pDescription;
	}


	/**
	 * @return the skuID
	 */
	public String getSkuID() {
		return mSkuID;
	}


	/**
	 * @param pSkuID the skuID to set
	 */
	public void setSkuID(String pSkuID) {
		mSkuID = pSkuID;
	}


	/**
	 * @return the availabilityDate
	 */
	public Timestamp getAvailabilityDate() {
		return mAvailabilityDate;
	}


	/**
	 * @param pAvailabilityDate the availabilityDate to set
	 */
	public void setAvailabilityDate(Timestamp pAvailabilityDate) {
		mAvailabilityDate = pAvailabilityDate;
	}


	/**
	 * @return the globalStockLevel
	 */
	public Long getGlobalStockLevel() {
		if(mGlobalStockLevel == null){
			return Long.valueOf(0);
		}else{
			return mGlobalStockLevel;
		}
	}	

	/**
	 * @param pGlobalStockLevel the globalStockLevel to set
	 */
	public void setGlobalStockLevel(Long mGlobalStockLevel) {
		this.mGlobalStockLevel = mGlobalStockLevel;
	}
	
	/**
	 * @return the siteStockLevel
	 */
	public Long getSiteStockLevel() {
		if(mSiteStockLevel == null){
			return Long.valueOf(0);
		}else{
			return mSiteStockLevel;
		}		
	}

	/**
	 * @param pSiteStockLevel the siteStockLevel to set
	 */
	public void setSiteStockLevel(Long mSiteStockLevel) {
		this.mSiteStockLevel = mSiteStockLevel;
	}
	

	/**
	 * @return the giftRegistryStockLevel
	 */
	
	public Long getGiftRegistryStockLevel() {
		if(mGiftRegistryStockLevel == null){
			return Long.valueOf(0);
		}else{
			return mGiftRegistryStockLevel;
		}
	}


	/**
	 * @param pGiftRegistryStockLevel the giftRegistryStockLevel to set
	 */
	public void setGiftRegistryStockLevel(Long mGiftRegistryStockLevel) {
		this.mGiftRegistryStockLevel = mGiftRegistryStockLevel;
	}
		

	/**
	 * @return the mOrderedQuantity
	 */
	public Long getOrderedQuantity() {
		if(mOrderedQuantity == null){
			return Long.valueOf(0);
		}else{
			return mOrderedQuantity;
		}
	}


	/**
	 * @param mOrderedQuantity the mOrderedQuantity to set
	 */
	public void setOrderedQuantity(Long mOrderedQuantity) {
		this.mOrderedQuantity = mOrderedQuantity;
	}


	@Override
	public int compareTo(InventoryVO inventory) {
		
		int compareResult = mSiteStockLevel.compareTo( inventory.mSiteStockLevel);
		
		return compareResult;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
        return mSiteStockLevel.equals((((InventoryVO)obj).mSiteStockLevel)); 
    }
	@Override
	public int hashCode() {
	    final int multiplier = 23;
        if (hashCode == 0) {
            int code = 133;            
            code = multiplier * code + getInventoryID().hashCode();
            hashCode = code;
        }
        return hashCode;
	}


	/**
	 * @return the mBASiteStockLevel
	 */
	public Long getBASiteStockLevel() {
		return mBASiteStockLevel;
	}


	/**
	 * @param mBASiteStockLevel the mBASiteStockLevel to set
	 */
	public void setBASiteStockLevel(Long mBASiteStockLevel) {
		this.mBASiteStockLevel = mBASiteStockLevel;
	}


	/**
	 * @return the mCAGiftRegistryStockLevel
	 */
	public Long getCAGiftRegistryStockLevel() {
		return mCAGiftRegistryStockLevel;
	}


	/**
	 * @param mCAGiftRegistryStockLevel the mCAGiftRegistryStockLevel to set
	 */
	public void setCAGiftRegistryStockLevel(Long mCAGiftRegistryStockLevel) {
		this.mCAGiftRegistryStockLevel = mCAGiftRegistryStockLevel;
	}


	/**
	 * @return the mBAGiftRegistryStockLevel
	 */
	public Long getBAGiftRegistryStockLevel() {
		return mBAGiftRegistryStockLevel;
	}


	/**
	 * @param mBAGiftRegistryStockLevel the mBAGiftRegistryStockLevel to set
	 */
	public void setBAGiftRegistryStockLevel(Long mBAGiftRegistryStockLevel) {
		this.mBAGiftRegistryStockLevel = mBAGiftRegistryStockLevel;
	}


	/**
	 * @return the mCASiteStockLevel
	 */
	public Long getCASiteStockLevel() {
		return mCASiteStockLevel;
	}


	/**
	 * @param mCASiteStockLevel the mCASiteStockLevel to set
	 */
	public void setCASiteStockLevel(Long mCASiteStockLevel) {
		this.mCASiteStockLevel = mCASiteStockLevel;
	}


	public String getDeliveryType() {
		return mDeliveryType;
	}


	public void setDeliveryType(String mDeliveryType) {
		this.mDeliveryType = mDeliveryType;
	}
		
}