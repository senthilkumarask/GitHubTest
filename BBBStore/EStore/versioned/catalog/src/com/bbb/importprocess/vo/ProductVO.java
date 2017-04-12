package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class ProductVO {
  
 
  private String mKeywords;
 
  private String mPriceRangeDescrip;
  private String mBABPriceRangeDescrip;
  private String mCAPriceRangeDescrip;
  private String mSkuLowPrice;
  private String mCASKULowPrice;
  private String mSkuHightPrice;
  private String mCASkuHightPrice;
  
  private String mGSPriceRangeDescrip;
  private String mGSBABPriceRangeDescrip;
  private String mGSCAPriceRangeDescrip;
  private String mGSSkuLowPrice;
  private String mGSCASKULowPrice;
  private String mGSSkuHightPrice;
  private String mGSCASkuHightPrice;
  
  private String mShowImagesCollection;
  private String mRollupTypeProd;
  private String mRolluptypeColl;
  
  
  
// Rare

  private boolean mCollectionFlag; 
  private String mProductFlag; 
  private String mSwatchFlag; 
 
  private String mShopGuideId; 
  private String mBrandId; 
  private Boolean mGiftCertProduct; 
  private Boolean mLeadProduct; 
  
  //LTL-34(PIM feed processing changes for Products)
  private String mLtlFlag; 
  private String mIntlRestricted;

  private String vendorId;
  
	public String getVendorId() {
	return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	/**
	 * @return mLtlFlag
	 */
	public String getLtlFlag() {
		return mLtlFlag;
	}

	/**
	 * @param mLtlFlag
	 */
	public void setLtlFlag(String mLtlFlag) {
		this.mLtlFlag = mLtlFlag;
	}
	
	/**
	 * @return mIntlRestricted
	 */
	public String getIntlRestricted() {
		return mIntlRestricted;
	}

	/**
	 * @param mIntlRestricted
	 */
	public void setIntlRestricted(String pIntlRestricted) {
		this.mIntlRestricted = pIntlRestricted;
	}
  
  
  //Added quantity tool tip for stofu
 /* private int mQuantityToolTip;
 
  private Map<String,String> mProductImages;
 
public int getQuantityToolTip() {
	return mQuantityToolTip;
}
public void setQuantityToolTip(int mQuantityToolTip) {
	this.mQuantityToolTip = mQuantityToolTip;
}*/

public String getGSPriceRangeDescrip() {
	if (!StringUtils.isEmpty(mGSPriceRangeDescrip)) {

	      return mGSPriceRangeDescrip.trim();
	    }
	return mGSPriceRangeDescrip;
}
public void setGSPriceRangeDescrip(String gSPriceRangeDescrip) {
	mGSPriceRangeDescrip = gSPriceRangeDescrip;
}
public String getGSBABPriceRangeDescrip() {
	if (!StringUtils.isEmpty(mGSBABPriceRangeDescrip)) {

	      return mGSBABPriceRangeDescrip.trim();
	    }
	return mGSBABPriceRangeDescrip;
}
public void setGSBABPriceRangeDescrip(String gSBABPriceRangeDescrip) {
	mGSBABPriceRangeDescrip = gSBABPriceRangeDescrip;
}
public String getGSCAPriceRangeDescrip() {
	if (!StringUtils.isEmpty(mGSCAPriceRangeDescrip)) {

	      return mGSCAPriceRangeDescrip.trim();
	    }
	return mGSCAPriceRangeDescrip;
}
public void setGSCAPriceRangeDescrip(String gSCAPriceRangeDescrip) {
	mGSCAPriceRangeDescrip = gSCAPriceRangeDescrip;
}
public String getGSSkuLowPrice() {
	if (!StringUtils.isEmpty(mGSSkuLowPrice)) {

	      return mGSSkuLowPrice.trim();
	    }
	return mGSSkuLowPrice;
}
public void setGSSkuLowPrice(String gSSkuLowPrice) {
	mGSSkuLowPrice = gSSkuLowPrice;
}
public String getGSCASKULowPrice() {
	if (!StringUtils.isEmpty(mGSCASKULowPrice)) {

	      return mGSCASKULowPrice.trim();
	    }
	return mGSCASKULowPrice;
}
public void setGSCASKULowPrice(String gSCASKULowPrice) {
	mGSCASKULowPrice = gSCASKULowPrice;
}
public String getGSSkuHightPrice() {
	if (!StringUtils.isEmpty(mGSSkuHightPrice)) {

	      return mGSSkuHightPrice.trim();
	    }
	return mGSSkuHightPrice;
}
public void setGSSkuHightPrice(String gSSkuHightPrice) {
	mGSSkuHightPrice = gSSkuHightPrice;
}
public String getGSCASkuHightPrice() {
	if (!StringUtils.isEmpty(mGSCASkuHightPrice)) {

	      return mGSCASkuHightPrice.trim();
	    }
	return mGSCASkuHightPrice;
}
public void setGSCASkuHightPrice(String gSCASkuHightPrice) {
	mGSCASkuHightPrice = gSCASkuHightPrice;
}
/**
   * @return the priceRangeDescrip
   */
  public String getPriceRangeDescrip() {
    
    if (!StringUtils.isEmpty(mPriceRangeDescrip)) {

      return mPriceRangeDescrip.trim();
    }
    return mPriceRangeDescrip;
  }
  /**
   * @param pPriceRangeDescrip the priceRangeDescrip to set
   */
  public void setPriceRangeDescrip(final String pPriceRangeDescrip) {
    
    mPriceRangeDescrip = pPriceRangeDescrip;
  }
  /**
   * @return the CAPriceRangeDescrip
   */
  public String getCAPriceRangeDescrip() {
    
    if (!StringUtils.isEmpty(mCAPriceRangeDescrip)) {

      return mCAPriceRangeDescrip.trim();
    }
    return mCAPriceRangeDescrip;
  }
  /**
   * @param pCAPriceRangeDescrip the CAPriceRangeDescrip to set
   */
  public void setCAPriceRangeDescrip(final String pCAPriceRangeDescrip) {
    mCAPriceRangeDescrip = pCAPriceRangeDescrip;
  }
  
  /**
   * @return the bABPriceRangeDescrip
   */
  public String getBABPriceRangeDescrip() {
    
    if (!StringUtils.isEmpty(mBABPriceRangeDescrip)) {

      return mBABPriceRangeDescrip.trim();
    }
    return mBABPriceRangeDescrip;
  }
  /**
   * @param pBABPriceRangeDescrip the bABPriceRangeDescrip to set
   */
  public void setBABPriceRangeDescrip(String pBABPriceRangeDescrip) {
    mBABPriceRangeDescrip = pBABPriceRangeDescrip;
  }
  /**
   * @return the skuLowPrice
   */
  public String getSkuLowPrice() {
    
    if (!StringUtils.isEmpty(mSkuLowPrice)) {

      return mSkuLowPrice.trim();
    }
    return mSkuLowPrice;
  }
  /**
   * @param pSkuLowPrice the skuLowPrice to set
   */
  public void setSkuLowPrice(String pSkuLowPrice) {
    mSkuLowPrice = pSkuLowPrice;
  }
  /**
   * @return the CASKULowPrice
   */
  public String getCASkuLowPrice() {
    
    if (!StringUtils.isEmpty(mCASKULowPrice)) {

      return mCASKULowPrice.trim();
    }
    return mCASKULowPrice;
  }
  /**
   * @param pCASKULowPrice the CASKULowPrice to set
   */
  public void setCASkuLowPrice(String pCASKULowPrice) {
    mCASKULowPrice = pCASKULowPrice;
  }
  /**
   * @return the skuHightPrice
   */
  public String getSkuHightPrice() {
    
    if (!StringUtils.isEmpty(mSkuHightPrice)) {

      return mSkuHightPrice.trim();
    }
    return mSkuHightPrice;
  }
  /**
   * @param pSkuHightPrice the skuHightPrice to set
   */
  public void setSkuHightPrice(String pSkuHightPrice) {
    mSkuHightPrice = pSkuHightPrice;
  }
  /**
   * @return the CASkuHightPrice
   */
  public String getCASkuHightPrice() {
    
    if (!StringUtils.isEmpty(mCASkuHightPrice)) {

      return mCASkuHightPrice.trim();
    }
    return mCASkuHightPrice;
  }
  /**
   * @param pCASkuHightPrice the CASkuHightPrice to set
   */
  public void setCASkuHightPrice(String pCASkuHightPrice) {
    mCASkuHightPrice = pCASkuHightPrice;
  }
  /**
   * @return the showImagesCollection
   */
  public String getShowImagesCollection() {
    
    if (!StringUtils.isEmpty(mShowImagesCollection)) {

      return mShowImagesCollection.trim();
    }
    return mShowImagesCollection;
  }
  /**
   * @param pShowImagesCollection the showImagesCollection to set
   */
  public void setShowImagesCollection(String pShowImagesCollection) {
    mShowImagesCollection = pShowImagesCollection;
  }
  /**
   * @return the rollupTypeProd
   */
  public String getRollupTypeProd() {
    
    if (!StringUtils.isEmpty(mRollupTypeProd)) {

      return mRollupTypeProd.trim();
    }
    return mRollupTypeProd;
  }
  /**
   * @param pRollupTypeProd the rollupTypeProd to set
   */
  public void setRollupTypeProd(String pRollupTypeProd) {
    mRollupTypeProd = pRollupTypeProd;
  }
  /**
   * @return the rolluptypeColl
   */
  public String getRolluptypeColl() {
    
    if (!StringUtils.isEmpty(mRolluptypeColl)) {

      return mRolluptypeColl.trim();
    }
    return mRolluptypeColl;
  }
  /**
   * @param pRolluptypeColl the rolluptypeColl to set
   */
  public void setRolluptypeColl(final String pRolluptypeColl) {
    mRolluptypeColl = pRolluptypeColl;
  }
  
  /**
   * @return the collectionFlag
   */
  public boolean isCollectionFlag() {
    return mCollectionFlag;
  }
  /**
   * @param pCollectionFlag the collectionFlag to set
   */
  public void setCollectionFlag(final boolean pCollectionFlag) {
    mCollectionFlag = pCollectionFlag;
  }
  /**
   * @return the productFlag
   */
  public String getProductFlag() {
    
    if (!StringUtils.isEmpty(mProductFlag)) {

      return mProductFlag.trim();
    }
    return mProductFlag;
  }
  /**
   * @param pProductFlag the productFlag to set
   */
  public void setProductFlag(final String pProductFlag) {
    mProductFlag = pProductFlag;
  }
 
  /**
   * @return the swatchFlag
   */
  public String isSwatchFlag() {
    return mSwatchFlag;
  }
  /**
   * @param pSwatchFlag the swatchFlag to set
   */
  public void setSwatchFlag(final String pSwatchFlag) {
    mSwatchFlag = pSwatchFlag;
  }
 
  /**
   * @return the shopGuideId
   */
  public String getShopGuideId() {
    
    if (!StringUtils.isEmpty(mShopGuideId)) {

      return mShopGuideId.trim();
    }
    return mShopGuideId;
  }
  /**
   * @param pShopGuideId the shopGuideId to set
   */
  public void setShopGuideId(final String pShopGuideId) {
    mShopGuideId = pShopGuideId;
  }

 
 
  /**
   * @return the brandId
   */
  public String getBrandId() {
    
    if (!StringUtils.isEmpty(mBrandId)) {

      return mBrandId.trim();
    }
    return mBrandId;
  }
  /**
   * @param pBrandId the brandId to set
   */
  public void setBrandId(final String pBrandId) {
    mBrandId = pBrandId;
  }
  /**
   * @return the mkeywords
   */
  public String getKeywords() {
    
    if (!StringUtils.isEmpty(mKeywords)) {

      return mKeywords.trim();
    }
    return mKeywords;
  }
  /**
   * @param pMkeywords the mkeywords to set
   */
  public void setKeywords(String pKeywords) {
    mKeywords = pKeywords;
  }
  public Boolean getGiftCertProduct() {
  
    return mGiftCertProduct;
  }
  public void setGiftCertProduct(Boolean pGiftCertProduct) {
    this.mGiftCertProduct = pGiftCertProduct;
  }
 
  public Boolean getLeadProduct() {
    
    return mLeadProduct;
  }
  public void setLeadProduct(Boolean pLeadProduct) {
    this.mLeadProduct = pLeadProduct;
  }
   

}
