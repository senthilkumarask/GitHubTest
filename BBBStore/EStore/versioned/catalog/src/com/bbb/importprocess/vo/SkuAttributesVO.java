package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class SkuAttributesVO {
  private String mId;
  private String mOperationCode;
  private String mAttribute;
  private String mRelatedType;
  private String mItemValueId;

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

  public String getRelatedType() {

    if (!StringUtils.isEmpty(mRelatedType)) {

      return mRelatedType.trim();
    }
    return mRelatedType;
  }

  public void setRelatedType(String pRelatedType) {
    this.mRelatedType = pRelatedType;
  }

  public void setItemValueId(String pItemValueId) {
    this.mItemValueId = pItemValueId;
  }

  public String getItemValueId() {

    if (!StringUtils.isEmpty(mItemValueId)) {

      return mItemValueId.trim();
    }
    return mItemValueId;
  }

  public String toString() {
    StringBuffer sbf = new StringBuffer();
    return sbf.append("Id=").append(getId()).append("OperationCode=").append(getOperationCode())
        .append("AttributeValue=").append(getAttribute()).toString();
  }

}
