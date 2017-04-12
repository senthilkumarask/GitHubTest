/**
 * 
 */
package com.bbb.account.vo.order;

import com.bbb.framework.integration.ServiceRequestBase;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 23-November-2011
//--------------------------------------------------------------------------------

public class OrderHistory2ReqVO extends ServiceRequestBase {


	private static final long serialVersionUID = 1L;
	private String mUserToken;
	private String mSiteFlag;
	private String mMemberID;
	private String mMbrNum;
	private String mProfileId;
	private String mOrderType;
	
	public String getOrderType() {
		return mOrderType;
	}
	public void setOrderType(String mOrderType) {
		this.mOrderType = mOrderType;
	}
	public String getMbrNum() {
		return mMbrNum;
	}
	public void setMbrNum(String pMbrNum) {
		this.mMbrNum = pMbrNum;
	}
	private String mServiceName;
	
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
	 * @return the memberID
	 */
	public String getMemberID() {
		return mMemberID;
	}
	/**
	 * @param pMemberID the memberID to set
	 */
	public void setMemberID(String pMemberID) {
		mMemberID = pMemberID;
	}
	/**
	 * @return the ProfileId
	 */
	public String getProfileId() {
		return mProfileId;
	}
	/**
	 * @param pProfileId the profileId to set
	 */
	public void setProfileId(String pProfileId) {
		this.mProfileId = pProfileId;
	}
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
}
