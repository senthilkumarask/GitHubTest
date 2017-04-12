/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ForgetPasswordReqVO.java
 *
 *  DESCRIPTION: ForgetPasswordReqVO : Request VO for forget password web service  
 *  HISTORY:
 *  19/12/11 Initial version
 *
 */

package com.bbb.account.vo.reclaim;

import com.bbb.framework.integration.ServiceRequestBase;

public class ForgetPasswordReqVO extends ServiceRequestBase{

	
	private static final long serialVersionUID = 1L;
	private String mUserToken;
	private String mSiteFlag;
	private String mEmailAddress;
	private String mServiceName;
	
	
	/**
	 * @return the mUserToken
	 */
	public String getUserToken() {
		return mUserToken;
	}
	/**
	 * @param mUserToken the mUserToken to set
	 */
	public void setUserToken(String pUserToken) {
		this.mUserToken = pUserToken;
	}
	/**
	 * @return the mSiteFlag
	 */
	public String getSiteFlag() {
		return mSiteFlag;
	}
	/**
	 * @param mSiteFlag the mSiteFlag to set
	 */
	public void setSiteFlag(String pSiteFlag) {
		this.mSiteFlag = pSiteFlag;
	}
	/**
	 * @return the mEmailAddress
	 */
	public String getEmailAddress() {
		return mEmailAddress;
	}
	/**
	 * @param mEmailAddress the mEmailAddress to set
	 */
	public void setEmailAddress(String pEmailAddress) {
		this.mEmailAddress = pEmailAddress;
	}
	/**
	 * @return the mServiceName
	 */
	public String getServiceName() {
		return mServiceName;
	}
	/**
	 * @param mServiceName the mServiceName to set
	 */
	public void setServiceName(String pServiceName) {
		this.mServiceName = pServiceName;
	}

}
