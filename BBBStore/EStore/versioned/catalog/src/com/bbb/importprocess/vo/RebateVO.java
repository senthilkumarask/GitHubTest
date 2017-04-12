package com.bbb.importprocess.vo;

import java.sql.Timestamp;

public class RebateVO {
 
  private String mDescrip;
  private Timestamp mStartDate;
  private Timestamp mEndDate;
  private String mRebateUrl;
  private String mSiteId;
  /**
   * @return the descrip
   */
  public String getDescrip() {
    return mDescrip;
  }
  /**
   * @param pDescrip the descrip to set
   */
  public void setDescrip(String pDescrip) {
    mDescrip = pDescrip;
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
   * @return the rebateUrl
   */
  public String getRebateUrl() {
    return mRebateUrl;
  }
  /**
   * @param pRebateUrl the rebateUrl to set
   */
  public void setRebateUrl(String pRebateUrl) {
    mRebateUrl = pRebateUrl;
  }
  /**
   * @return the siteId
   */
  public String getSiteId() {
    return mSiteId;
  }
  /**
   * @param pSiteId the siteId to set
   */
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }
  

}
