package com.bbb.importprocess.vo;

import java.sql.Timestamp;

import atg.core.util.StringUtils;

public class AttributeVO {

  private String mDescription;
  private Timestamp mStartDT;
  private Timestamp mEndDT;
  private String mSiteFlag;
  private String mImageURL;
  private String mActionURL;
  private String mPlaceHolder;
  private String mPriority;
  private String mIntlFlag;

  /**
   * @return the description
   */
  public String getDescription() {
    if (!StringUtils.isEmpty(mDescription)) {

      return mDescription.trim();
    }
    return mDescription;
  }

  /**
   * @param pDescription
   *          the description to set
   */
  public void setDescription(String pDescription) {
    mDescription = pDescription;
  }

  /**
   * @return the startDT
   */
  public Timestamp getStartDT() {
    return mStartDT;
  }

  /**
   * @param pStartDT
   *          the startDT to set
   */
  public void setStartDT(Timestamp pStartDT) {
    mStartDT = pStartDT;
  }

  /**
   * @return the endDT
   */
  public Timestamp getEndDT() {
    return mEndDT;
  }

  /**
   * @param pEndDT
   *          the endDT to set
   */
  public void setEndDT(Timestamp pEndDT) {
    mEndDT = pEndDT;
  }

  /**
   * @return the siteFlag
   */
  public String getSiteFlag() {
    
    if (!StringUtils.isEmpty(mSiteFlag)) {

      return mSiteFlag.trim();
    }
    return mSiteFlag;
  }

  /**
   * @param pSiteFlag
   *          the siteFlag to set
   */
  public void setSiteFlag(String pSiteFlag) {
    mSiteFlag = pSiteFlag;
  }

  /**
   * @return the imageURL
   */
  public String getImageURL() {
    
    if (!StringUtils.isEmpty(mImageURL)) {

      return mImageURL.trim();
    }
    return mImageURL;
  }

  /**
   * @param pImageURL
   *          the imageURL to set
   */
  public void setImageURL(String pImageURL) {
    mImageURL = pImageURL;
  }

  /**
   * @return the actionURL
   */
  public String getActionURL() {
    
    if (!StringUtils.isEmpty(mActionURL)) {

      return mActionURL.trim();
    }
    return mActionURL;
  }

  /**
   * @param pActionURL
   *          the actionURL to set
   */
  public void setActionURL(String pActionURL) {
    mActionURL = pActionURL;
  }

  /**
   * @return the placeHolder
   */
  public String getPlaceHolder() {
    
    if (!StringUtils.isEmpty(mPlaceHolder)) {

      return mPlaceHolder.trim();
    }
    return mPlaceHolder;
  }

  /**
   * @param pPlaceHolder
   *          the placeHolder to set
   */
  public void setPlaceHolder(String pPlaceHolder) {
    mPlaceHolder = pPlaceHolder;
  }

  /**
   * @return the priority
   */
  public String getPriority() {
    
    if (!StringUtils.isEmpty(mPriority)) {

      return mPriority.trim();
    }
    return mPriority;
  }

  /**
   * @param pPriority
   *          the priority to set
   */
  public void setPriority(String pPriority) {
    mPriority = pPriority;
  }
  
  /**
   * @return the mIntlFlag
   */
  public String getIntlFlag() {
    
    if (!StringUtils.isEmpty(mIntlFlag)) {

      return mIntlFlag.trim();
    }
    return mIntlFlag;
  }

  /**
   * @param mIntlFlag
   *          the Intl flag to set
   */
  public void setIntlFlag(String pIntlFlag) {
	  mIntlFlag = pIntlFlag;
  }

}
