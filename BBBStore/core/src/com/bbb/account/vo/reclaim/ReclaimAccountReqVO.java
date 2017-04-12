/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ReclaimAccountReqVO.java
 *
 *  DESCRIPTION: ReclaimAccountReqVO : Request VO for reclaim related web service  
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.account.vo.reclaim;

import com.bbb.framework.integration.ServiceRequestBase;

public class ReclaimAccountReqVO extends ServiceRequestBase{

	private static final long serialVersionUID = 1L;
	private String mUserToken;
	private String mSiteFlag;
	private String mServiceName;
	private String mProfileId;
	private String mMemberToken;
	
	/**
	 * @return the mProfileId
	 */
	public String getProfileId() {
		return mProfileId;
	}
	/**
	 * @param mProfileId the mProfileId to set
	 */
	public void setProfileId(String pProfileId) {
		this.mProfileId = pProfileId;
	}

	/**
	 * @return the mMemberToken
	 */
	public String getMemberToken() {
		return mMemberToken;
	}
	/**
	 * @param mMemberToken the mMemberToken to set
	 */
	public void setMemberToken(String pMemberToken) {
		this.mMemberToken = pMemberToken;
	}
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
	 * @return the mServiceName
	 */
	public String getServiceName() {
		return mServiceName;
	}
	/**
	 * @param mServiceName the mServiceName to set
	 */
	public void setServiceName(String mServiceName) {
		this.mServiceName = mServiceName;
	}
}
