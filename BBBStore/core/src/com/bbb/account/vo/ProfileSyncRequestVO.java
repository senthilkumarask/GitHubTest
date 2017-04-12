/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written consent is prohibited.
 *
 *  FILE:  RegistryWithATGInfoRequestVO.java
 *
 *  DESCRIPTION: RegistryWithATGInfoRequestVO : Request VO for UpdateRegistryWithATGInfo web service  
 *  HISTORY:
 *  04/02/12 Initial version
 *
 */

package com.bbb.account.vo;

import com.bbb.framework.integration.ServiceRequestBase;

public class ProfileSyncRequestVO extends ServiceRequestBase{

	private static final long serialVersionUID = 1L;
	private String mServiceName;
	private String mUserToken;
	private String mSiteFlag;
	private String mProfileId;
	private String mEmailAddress;
	private String mFirstName;
	private String mLastName;
	private String mPhoneNumber;
	private String mMobileNumber;
	/**
	 * @return the mServiceName
	 */
	public String getServiceName() {
		return mServiceName;
	}
	/**
	 * @param mServiceName the mServiceName to set
	 */
	public void setServiceName(final String pServiceName) {
		this.mServiceName = pServiceName;
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
	public void setUserToken(final String pUserToken) {
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
	public void setSiteFlag(final String pSiteFlag) {
		this.mSiteFlag = pSiteFlag;
	}
	/**
	 * @return the mProfileId
	 */
	public String getProfileId() {
		return mProfileId;
	}
	/**
	 * @param mProfileId the mProfileId to set
	 */
	public void setProfileId(final String pProfileId) {
		this.mProfileId = pProfileId;
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
	public void setEmailAddress(final String pEmailAddress) {
		this.mEmailAddress = pEmailAddress;
	}
	/**
	 * @return the mFirstName
	 */
	public String getFirstName() {
		return mFirstName;
	}
	/**
	 * @param mFirstName the mFirstName to set
	 */
	public void setFirstName(final String pFirstName) {
		this.mFirstName = pFirstName;
	}
	/**
	 * @return the mLastName
	 */
	public String getLastName() {
		return mLastName;
	}
	/**
	 * @param mLastName the mLastName to set
	 */
	public void setLastName(final String pLastName) {
		this.mLastName = pLastName;
	}
	/**
	 * @return the mPhoneNumber
	 */
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	/**
	 * @param mPhoneNumber the mPhoneNumber to set
	 */
	public void setPhoneNumber(final String pPhoneNumber) {
		this.mPhoneNumber = pPhoneNumber;
	}
	/**
	 * @return the mMobileNumber
	 */
	public String getMobileNumber() {
		return mMobileNumber;
	}
	/**
	 * @param mMobileNumber the mMobileNumber to set
	 */
	public void setMobileNumber(final String pMobileNumber) {
		this.mMobileNumber = pMobileNumber;
	}
	@Override
	public String toString() {
		return "ProfileSyncRequestVO [mServiceName=" + mServiceName
				+ ", mUserToken=" + mUserToken + ", mSiteFlag=" + mSiteFlag
				+ ", mProfileId=" + mProfileId + ", mEmailAddress="
				+ mEmailAddress + ", mFirstName=" + mFirstName + ", mLastName="
				+ mLastName + ", mPhoneNumber=" + mPhoneNumber
				+ ", mMobileNumber=" + mMobileNumber + "]";
	}
	
}
