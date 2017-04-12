package com.bbb.importprocess.vo;

import java.sql.Timestamp;

import atg.core.util.StringUtils;

public class SkuDisplayAttributesVO {
  
   private String mItemAttributeId;
   private String mValueId;
   private String mSkuId;
   private String mSiteFlag;
   private String mMiscInfo;
   private Timestamp mStartDT;
   private Timestamp mEndDT;
   private String mOperationFlag;
  /**
   * @return the itemAttributeId
   */
  public String getItemAttributeId() {
    return mItemAttributeId;
  }
  /**
   * @param pItemAttributeId the itemAttributeId to set
   */
  public void setItemAttributeId(String pItemAttributeId) {
    mItemAttributeId = pItemAttributeId;
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
   * @return the miscInfo
   */
  public String getMiscInfo() {
    return mMiscInfo;
  }
  /**
   * @param pMiscInfo the miscInfo to set
   */
  public void setMiscInfo(String pMiscInfo) {
    mMiscInfo = pMiscInfo;
  }
  /**
   * @return the startDT
   */
  public Timestamp getStartDT() {
    return mStartDT;
  }
  /**
   * @param pStartDT the startDT to set
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
   * @param pEndDT the endDT to set
   */
  public void setEndDT(Timestamp pEndDT) {
    mEndDT = pEndDT;
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
   * @return the valueId
   */
  public String getItemValueId() {
    return mValueId;
  }
  /**
   * @param pValueId the valueId to set
   */
  public void setValueId(String pValueId) {
    mValueId = pValueId;
  }
  /**
   * @return the skuId
   */
  public String getSkuId() {
    return mSkuId;
  }
  /**
   * @param pSkuId the skuId to set
   */
  public void setSkuId(String pSkuId) {
    mSkuId = pSkuId;
  }
}
