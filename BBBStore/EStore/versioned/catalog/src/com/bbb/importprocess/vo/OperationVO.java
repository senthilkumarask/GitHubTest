package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class OperationVO {
  private String mId;
  private String mOperationCode;
  private String mAttribute;
  private String mRelatedType;
  private String mSequence;
  private String mPrimaryNodeId;
  private String mProductSeqNumber;
 

  public String getProductSeqNumber() {
	  return mProductSeqNumber;
  }

  public void setProductSeqNumber(String mProductSeqNumber) {
	  this.mProductSeqNumber = mProductSeqNumber;
  }

/**
   * @return the mSequence
   */
  public String getSequence() {
    return mSequence;
  }

  /**
   * @param mSequence
   *          the mSequence to set
   */
  public void setSequence(String mSequence) {
    this.mSequence = mSequence;
  }

  /**
   * @return the id
   */
  public String getId() {
    if (!StringUtils.isEmpty(mId)) {

      return mId.trim();
    }
    return mId;
  }

  /**
   * @param pId
   *          the id to set
   */
  public void setId(final String pId) {

    mId = pId;
  }

  /**
   * @return the operationCode
   */
  public String getOperationCode() {
    
    if (!StringUtils.isEmpty(mOperationCode)) {

      return mOperationCode.trim();
    }
    return mOperationCode;
  }

  /**
   * @param pOperationCode
   *          the operationCode to set
   */
  public void setOperationCode(final String pOperationCode) {
    mOperationCode = pOperationCode;
  }

  public String getAttribute() {
    
    if (!StringUtils.isEmpty(mAttribute)) {

      return mAttribute.trim();
    }
    return mAttribute;
  }

  public void setSiteIds(String pSiteIds) {
    this.mAttribute = pSiteIds;
  }

  public String getRelatedType() {
    
    if (!StringUtils.isEmpty(mRelatedType)) {

      return mRelatedType.trim();
    }
    return mRelatedType;
  }

  public void setRelatedType(String pRelatedType) {
    this.mRelatedType = pRelatedType;
  }

  public String getSiteIds() {
    
    if (!StringUtils.isEmpty(mAttribute)) {

      return mAttribute.trim();
    }
    return mAttribute;
  }

  public String toString() {
    StringBuffer sbf = new StringBuffer();
    return sbf.append("Id=").append(getId()).append("OperationCode=").append(getOperationCode())
        .append("AttributeValue=").append(getAttribute()).toString();
  }

  public String getPrimaryNodeId() {
    return mPrimaryNodeId;
  }

  public void setPrimaryNodeId(String pPrimaryNodeId) {
    this.mPrimaryNodeId = pPrimaryNodeId;
  }  
}
