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
//Created on: 16-January-2012
//--------------------------------------------------------------------------------

public class OrderDetailsReqVO extends ServiceRequestBase {


	private static final long serialVersionUID = 1L;
	private String mUserToken;
	private String mSiteFlag;
	private String mOrderID;
	private String mProfileId;
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
	 * @return the orderID
	 */
	public String getOrderID() {
		return mOrderID;
	}
	/**
	 * @param orderID the orderID to set
	 */
	public void setOrderID(String orderID) {
		mOrderID = orderID;
	}
	/**
	 * @return the orderID
	 */
	public String getProfileId() {
		return mProfileId;
	}
	/**
	 * @param pProfileId the orderID to set
	 */
	public void setmProfileId(String pProfileId) {
		this.mProfileId = pProfileId;
	}
}
