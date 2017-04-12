package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class MiscVO {
  private String mDeptid;
  private String mDescrip;
  private String mSubDeptid;
  private String mClassid;
  private String mSkuid;
  private String mSiteFlag;
  private String mType;
  private long mThresholdLimited;
  private long mThresholdAvailable;
  private String mSkuThresholdid;
  
  public String getSkuThresholdid() {
	    
	    if (!StringUtils.isEmpty(mSkuThresholdid)) {

	      return mSkuThresholdid.trim();
	    }
	    return mSkuThresholdid;
	  }

	  public void setSkuThresholdid(String pSkuThresholdid) {
	    this.mSkuThresholdid = pSkuThresholdid;
	  }
  

  public String getDescrip() {
    if (!StringUtils.isEmpty(mDescrip)) {

      return mDescrip.trim();
    }
    return mDescrip;
  }

  public void setDescrip(String pDescrip) {
    this.mDescrip = pDescrip;
  }

  public String getDeptid() {
    if (!StringUtils.isEmpty(mDeptid)) {

      return mDeptid.trim();
    }
    return mDeptid;
  }

  public void setDeptid(String pDeptid) {
    this.mDeptid = pDeptid;
  }

  public String getSubDeptid() {
    
    if (!StringUtils.isEmpty(mSubDeptid)) {

      return mSubDeptid.trim();
    }
    return mSubDeptid;
  }

  public void setSubDeptid(String pSubDeptid) {
    this.mSubDeptid = pSubDeptid;
  }

  public String getClassid() {
    
    if (!StringUtils.isEmpty(mClassid)) {

      return mClassid.trim();
    }
    return mClassid;
  }

  public void setClassid(String pClassid) {
    this.mClassid = pClassid;
  }

  public String getSkuid() {
    
    if (!StringUtils.isEmpty(mSkuid)) {

      return mSkuid.trim();
    }
    return mSkuid;
  }

  public void setSkuid(String pSkuid) {
    this.mSkuid = pSkuid;
  }

  public String getSiteFlag() {
    if (!StringUtils.isEmpty(mSiteFlag)) {

      return mSiteFlag.trim();
    }
    return mSiteFlag;
  }

  public void setSiteFlag(String pSiteFlag) {
    this.mSiteFlag = pSiteFlag;
  }

  public String getType() {
    if (!StringUtils.isEmpty(mType)) {

      return mType.trim();
    }
    return mType;
  }

  public void setType(String pType) {
    this.mType = pType;
  }

  public long getThresholdLimited() {
    return mThresholdLimited;
  }

  public void setThresholdLimited(long pThresholdLimited) {
    this.mThresholdLimited = pThresholdLimited;
  }

  public long getThresholdAvailable() {
    return mThresholdAvailable;
  }

  public void setThresholdAvailable(long pThresholdAvailable) {
    this.mThresholdAvailable = pThresholdAvailable;
  }



}
