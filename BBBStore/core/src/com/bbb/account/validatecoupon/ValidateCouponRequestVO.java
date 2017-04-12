/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ValidateCouponRequestVO.java
 *
 *  DESCRIPTION: CouponsResponseVO : Request VO for ProcessCoupon web service  
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */
package com.bbb.account.validatecoupon;

import com.bbb.framework.integration.ServiceRequestBase;

public class ValidateCouponRequestVO extends ServiceRequestBase{

	private static final long serialVersionUID = 1L;
	private String mUserToken;
	private String mSiteFlag;
	private String mAction;
	private String mEmailAddr;
	private String mMobilePhone;
	private String mEntryCd;
	private String mServiceName;
	
	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return mServiceName;
	}

	/**
	 * @param pServiceName the serviceName to set
	 */
	public void setServiceName(String pServiceName) {
		mServiceName = pServiceName;
	}
	
	/**
	 * @return the userToken
	 */
	public String getUserToken() {
		return mUserToken;
	}
	/**
	 * @param pUserToken the userToken to set
	 */
	public void setUserToken(String pUserToken) {
		mUserToken = pUserToken;
	}
	/**
	 * @return the siteFlag
	 */
	public String getSiteFlag() {
		return mSiteFlag;
	}
	/**
	 * @param pSiteFlag the siteFlag to set
	 */
	public void setSiteFlag(String pSiteFlag) {
		mSiteFlag = pSiteFlag;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return mAction;
	}
	/**
	 * @param pAction the action to set
	 */
	public void setAction(String pAction) {
		mAction = pAction;
	}
	/**
	 * @return the emailAddr
	 */
	public String getEmailAddr() {
		return mEmailAddr;
	}
	/**
	 * @param pEmailAddr the emailAddr to set
	 */
	public void setEmailAddr(String pEmailAddr) {
		mEmailAddr = pEmailAddr;
	}
	/**
	 * @return the mobilePhone
	 */
	public String getMobilePhone() {
		return mMobilePhone;
	}
	/**
	 * @param pMobilePhone the mobilePhone to set
	 */
	public void setMobilePhone(String pMobilePhone) {
		mMobilePhone = pMobilePhone;
	}
	/**
	 * @return the entryCd
	 */
	public String getEntryCd() {
		return mEntryCd;
	}
	/**
	 * @param pEntryCd the entryCd to set
	 */
	public void setEntryCd(String pEntryCd) {
		mEntryCd = pEntryCd;
	}
}
