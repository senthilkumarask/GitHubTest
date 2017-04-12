package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class CategoryVO {
  
  private String mImageUrl;
  private boolean mIsCollege;
  private String mShopGuideId;
  private boolean mDisable;
  private int mSequenceNum;
  private String mNodeType;
  private String mOperationFlag;
  private boolean phantomCategory;
  private String mKeywords;
 
  //properties added for STOFU
  private String mScene7URL;
  private  int mGSImageOrientation;
  
  
    
public int getGSimageorientation() {
	return mGSImageOrientation;
}
public void setGSimageorientation(final int mGSImageOrientation){
	this. mGSImageOrientation = mGSImageOrientation;
}
public String getScene7URL() {
	return mScene7URL;
}
public void setScene7URL(String mScene7URL) {
	this.mScene7URL = mScene7URL;
}


private String mCategoryName;
  /**
   * @return the categoryName
   */
  public String getCategoryName() {
    
    if (!StringUtils.isEmpty(mCategoryName)) {

      return mCategoryName.trim();
    }
    return mCategoryName;
  }
  /**
   * @param pCategoryName the categoryName to set
   */
  public void setCategoryName(final String pCategoryName) {
    mCategoryName = pCategoryName;
  }
  /**
   * @return the imageUrl
   */
  public String getImageUrl() {
    
    if (!StringUtils.isEmpty(mImageUrl)) {

      return mImageUrl.trim();
    }
    return mImageUrl;
  }
  /**
   * @param pImageUrl the imageUrl to set
   */
  public void setImageUrl(final String pImageUrl) {
    mImageUrl = pImageUrl;
  }
  /**
   * @return the isCollege
   */
  public boolean isIsCollege() {
    
    return mIsCollege;
  }
  /**
   * @param pIsCollege the isCollege to set
   */
  public void setIsCollege(final boolean pIsCollege) {
    mIsCollege = pIsCollege;
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
   * @return the disable
   */
  public boolean isDisable() {
    return mDisable;
  }
  /**
   * @param pDisable the disable to set
   */
  public void setDisable(final boolean pDisable) {
    mDisable = pDisable;
  }
  /**
   * @return the sequenceNum
   */
  public int getSequenceNum() {
    return mSequenceNum;
  }
  /**
   * @param pSequenceNum the sequenceNum to set
   */
  public void setSequenceNum(final int pSequenceNum) {
    
    mSequenceNum = pSequenceNum;
  }
  /**
   * @return the nodeType
   */
  public String getNodeType() {
    
    if (!StringUtils.isEmpty(mNodeType)) {

      return mNodeType.trim();
    }
    return mNodeType;
  }
  /**
   * @param pNodeType the nodeType to set
   */
  public void setNodeType(final String pNodeType) {
    
    mNodeType = pNodeType;
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
  public void setOperationFlag(final String pOperationFlag) {
    mOperationFlag = pOperationFlag;
  }
  /**
   * @return the phantomCategory
   */
  public boolean getPhantomCategory() {
    return phantomCategory;
  }
  /**
   * @param pPhantomCategory the phantomCategory to set
   */
  public void setPhantomCategory(boolean pPhantomCategory) {
    phantomCategory = pPhantomCategory;
  }
  /**
   * @return the keywords
   */
  public String getKeywords() {
    
    if (!StringUtils.isEmpty(mKeywords)) {

      return mKeywords.trim();
    }
    return mKeywords;
  }
  /**
   * @param pKeywords the keywords to set
   */
  public void setKeywords(String pKeywords) {
    mKeywords = pKeywords;
  }

}
