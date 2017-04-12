package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class ProductTabVO {

  private String mTabName;
  private String mTabContent;
  private int mTabSequence;
  private boolean mOperationFlag;
  private String mSiteId;
  
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
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }
  /**
   * @return the tabName
   */
  public String getTabName() {
    if (!StringUtils.isEmpty(mTabName)) {

      return mTabName.trim();
    }
    return mTabName;
  }
  /**
   * @param pTabName the tabName to set
   */
  public void setTabName(String pTabName) {
    mTabName = pTabName;
  }
  /**
   * @return the tabContent
   */
  public String getTabContent() {
    
    if (!StringUtils.isEmpty(mTabContent)) {

      return mTabContent.trim();
    }
    return mTabContent;
  }
  /**
   * @param pTabContent the tabContent to set
   */
  public void setTabContent(String pTabContent) {
    mTabContent = pTabContent;
  }
  /**
   * @return the tabSequence
   */
  public int getTabSequence() {
    return mTabSequence;
  }
  /**
   * @param pTabSequence the tabSequence to set
   */
  public void setTabSequence(int pTabSequence) {
    mTabSequence = pTabSequence;
  }
  /**
   * @return the operationFlag
   */
  public boolean isOperationFlag() {
    return mOperationFlag;
  }
  /**
   * @param pOperationFlag the operationFlag to set
   */
  public void setOperationFlag(boolean pOperationFlag) {
    mOperationFlag = pOperationFlag;
  }
  
}
