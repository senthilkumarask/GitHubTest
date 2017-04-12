package com.bbb.importprocess.vo;

import java.sql.Timestamp;

import atg.core.util.StringUtils;

public class ItemMediaVO {
  

  private String mMediaId;
  private Timestamp mStartDate;
  private Timestamp mEndDate;
  private String mComments;
  private String mWidget;
  private String mOperationFlag;
  private String mSiteFlag;
  private String mSequenceNum;
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
   * @return the comments
   */
  public String getComments() {
    
    if (!StringUtils.isEmpty(mComments)) {

      return mComments.trim();
    }
    return mComments;
  }
  /**
   * @param pComments the comments to set
   */
  public void setComments(String pComments) {
    mComments = pComments;
  }
  /**
   * @return the widget
   */
  public String getWidget() {
    if (!StringUtils.isEmpty(mWidget)) {

      return mWidget.trim();
    }
    return mWidget;
  }
  /**
   * @param pWidget the widget to set
   */
  public void setWidget(String pWidget) {
    mWidget = pWidget;
  }
  /**
   * @return the operationFlag
   */
  public String getOperationFlag() {
    if (!StringUtils.isEmpty(mOperationFlag)) {

      return mOperationFlag.trim();
    }
    return mOperationFlag;
  }
  /**
   * @param pOperationFlag the operationFlag to set
   */
  public void setOperationFlag(String pOperationFlag) {
    mOperationFlag = pOperationFlag;
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
   * @param pSiteFlag the siteFlag to set
   */
  public void setSiteFlag(String pSiteFlag) {
    mSiteFlag = pSiteFlag;
  }
  /**
   * @return the sequenceNum
   */
  public String getSequenceNum() {
    if (!StringUtils.isEmpty(mSequenceNum)) {

      return mSequenceNum.trim();
    }
    return mSequenceNum;
  }
  /**
   * @param pSequenceNum the sequenceNum to set
   */
  public void setSequenceNum(String pSequenceNum) {
    mSequenceNum = pSequenceNum;
  }
  /**
   * @return the mediaId
   */
  public String getMediaId() {
    if (!StringUtils.isEmpty(mMediaId)) {

      return mMediaId.trim();
    }
    return mMediaId;
  }
  /**
   * @param pMediaId the mediaId to set
   */
  public void setMediaId(String pMediaId) {
    mMediaId = pMediaId;
  }

}
