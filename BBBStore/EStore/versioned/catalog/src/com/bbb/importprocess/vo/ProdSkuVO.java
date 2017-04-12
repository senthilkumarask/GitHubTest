
package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;



public class ProdSkuVO {


  

  private String mChildSkuId;
  private String id;
  private String mRelatedType;
  private Boolean mLikeUnlikeFlag;
  private String mRollupType;
  private String mOperationFlag;
  private String mSequenceNum;
  
  public String getRelatedType() {
    
    if (!StringUtils.isEmpty(mRelatedType)) {

      return mRelatedType.trim();
    }
    return mRelatedType;
  }
  public void setRelatedType(String pRelatedType) {
    this.mRelatedType = pRelatedType;
  }
  public Boolean getLikeUnlikeFlag() {
    return mLikeUnlikeFlag;
  }
  public void setLikeUnlikeFlag(Boolean pLikeUnlikeFlag) {
    this.mLikeUnlikeFlag = pLikeUnlikeFlag;
  }
  public String getOperationFlag() {
    
    if (!StringUtils.isEmpty(mOperationFlag)) {

      return mOperationFlag.trim();
    }
    return mOperationFlag;
  }
  public void setOperationFlag(String pOperationFlag) {
    this.mOperationFlag = pOperationFlag;
  }
  public String getSequenceNum() {
    
    if (!StringUtils.isEmpty(mSequenceNum)) {

      return mSequenceNum.trim();
    }
    return mSequenceNum;
  }
  public void setSequenceNum(String pSequenceNum) {
    this.mSequenceNum = pSequenceNum;
  }
  public String getChildSkuId() {
    
    if (!StringUtils.isEmpty(mChildSkuId)) {

      return mChildSkuId.trim();
    }
    return mChildSkuId;
  }
  public void setChildSkuId(String pChildSkuId) {
    this.mChildSkuId = pChildSkuId;
  }
  public String getId() {
    
    if (!StringUtils.isEmpty(id)) {

      return id.trim();
    }
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getRollupType() {
    
    if (!StringUtils.isEmpty(mRollupType)) {

      return mRollupType.trim();
    }
    return mRollupType;
  }
  public void setRollupType(String pRollupType) {
    this.mRollupType = pRollupType;
  }
  
 
}

