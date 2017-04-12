package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class SkuPricingVO {
  
  private String mWasPrice;
  private String mCAWasPrice;
  private String mJDARetailPrice;
  private String mCAJDARetailPrice;
  private String mMXWasPrice;
  private String mMXJDARetailPrice;
  private String mInCartPrice;
  private String mInCartPriceCA;
  private String mInCartPriceMX;  
  
/**
   * @return the wasPrice
   */
  public String getWasPrice() {
    
    if (!StringUtils.isEmpty(mWasPrice)) {

      return mWasPrice.trim();
    }
    return mWasPrice;
  }
  /**
   * @param pWasPrice the wasPrice to set
   */
  public void setWasPrice(String pWasPrice) {
    
    mWasPrice = pWasPrice;
  }
  /**
   * @return the cAWasPrice
   */
  public String getCAWasPrice() {
    
    if (!StringUtils.isEmpty(mCAWasPrice)) {

      return mCAWasPrice.trim();
    }
    return mCAWasPrice;
  }
  /**
   * @param pCAWasPrice the cAWasPrice to set
   */
  public void setCAWasPrice(String pCAWasPrice) {
    mCAWasPrice = pCAWasPrice;
  }
  /**
   * @return the jDARetailPrice
   */
  public String getJDARetailPrice() {
    
    if (!StringUtils.isEmpty(mJDARetailPrice)) {

      return mJDARetailPrice.trim();
    }
    return mJDARetailPrice;
  }
  /**
   * @param pJDARetailPrice the jDARetailPrice to set
   */
  public void setJDARetailPrice(String pJDARetailPrice) {
    mJDARetailPrice = pJDARetailPrice;
  }
  /**
   * @return the cAJDARetailPrice
   */
  public String getCAJDARetailPrice() {
    if (!StringUtils.isEmpty(mCAJDARetailPrice)) {

      return mCAJDARetailPrice.trim();
    }
    return mCAJDARetailPrice;
  }
  /**
   * @param pCAJDARetailPrice the cAJDARetailPrice to set
   */
  public void setCAJDARetailPrice(String pCAJDARetailPrice) {
    mCAJDARetailPrice = pCAJDARetailPrice;
  }
  /**
   * @return the MXJDARetailPrice
   */
  public String getMXJDARetailPrice() {
    if (!StringUtils.isEmpty(mMXJDARetailPrice)) {

      return mMXJDARetailPrice.trim();
    }
    return mMXJDARetailPrice;
  }
  /**
   * @param pMXJDARetailPrice the pMXJDARetailPrice to set
   */
  public void setMXJDARetailPrice(String pMXJDARetailPrice) {
    mMXJDARetailPrice = pMXJDARetailPrice;
  }
  
  /**
   * @return the mMXWasPrice
   */
  public String getMXWasPrice() {
    
    if (!StringUtils.isEmpty(mMXWasPrice)) {

      return mMXWasPrice.trim();
    }
    return mMXWasPrice;
  }
  /**
   * @param pMXWasPrice the pMXWasPrice to set
   */
  public void setMXWasPrice(String pMXWasPrice) {
    mMXWasPrice = pMXWasPrice;
  }
 
   public String getInCartPrice() {
	  if (!StringUtils.isEmpty(mInCartPrice)) {
	      return mInCartPrice.trim();
	    }
		return mInCartPrice;
	}
   
	public void setInCartPrice(String pInCartPrice) {
		this.mInCartPrice = pInCartPrice;
	}
	
	public String getInCartPriceCA() {
		if (!StringUtils.isEmpty(mInCartPriceCA)) {
		      return mInCartPriceCA.trim();
		    }
		return mInCartPriceCA;
	}
	
	public void setInCartPriceCA(String pInCartPriceCA) {
		this.mInCartPriceCA = pInCartPriceCA;
	}
	
	public String getInCartPriceMX() {
		if (!StringUtils.isEmpty(mInCartPriceMX)) {
		      return mInCartPriceMX.trim();
		    }
		return mInCartPriceMX;
	}
	
	public void setInCartPriceMX(String pInCartPriceMX) {
		this.mInCartPriceMX = pInCartPriceMX;
	}
	
	
}
