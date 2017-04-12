package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class BrandVO {
  
  private String mName;
  private String mDescrip;
  private String mBrandImage;
  private String mSiteId;
  private boolean mDisplayFlag;
  
  
  /**
   * @return the descrip
   */
  public String getDescrip() {
    
    if (!StringUtils.isEmpty(mDescrip)) {

      return mDescrip.trim();
    }
    return mDescrip;
  }
  /**
   * @param pDescrip the descrip to set
   */
  public void setDescrip(String pDescrip) {
    mDescrip = pDescrip;
  }
  
  /**
   * @return the name
   */
  public String getName() {
    
    if (!StringUtils.isEmpty(mName)) {

      return mName.trim();
    }
    return mName;
  }
  /**
   * @param pName the name to set
   */
  public void setName(final String pName) {
    mName = pName;
  }
  /**
   * @return the brandImage
   */
  public String getBrandImage() {
    
    if (!StringUtils.isEmpty(mBrandImage)) {

      return mBrandImage.trim();
    }
    return mBrandImage;
  }
  /**
   * @param pBrandImage the brandImage to set
   */
  public void setBrandImage(final String pBrandImage) {
    mBrandImage = pBrandImage;
  }
  /**
   * @return the siteId
   */
  public String getSiteId() {
    
    if (!StringUtils.isEmpty(mSiteId)) {

      return mSiteId.trim();
    }
    return mSiteId;
  }
  /**
   * @param pSiteId the siteId to set
   */
  public void setSiteId(final String pSiteId) {
    mSiteId = pSiteId;
  }
  /**
   * @return the displayFlag
   */
  public boolean isDisplayFlag() {
    
    return mDisplayFlag;
  }
  /**
   * @param pDisplayFlag the displayFlag to set
   */
  public void setDisplayFlag(boolean pDisplayFlag) {
    mDisplayFlag = pDisplayFlag;
  }


}
