package com.bbb.account.verifyoldaccount;

import com.bbb.framework.integration.ServiceRequestBase;


public class ValidateOldAccReqVO extends ServiceRequestBase{

	private static final long serialVersionUID = 1L;
	private String mUserToken;
	private String mEmail;
	
	private String mSiteFlag;
	private String mPassword;
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
	 * @return the password
	 */
	public String getPassword() {
		return mPassword;
	}
	/**
	 * @param pPassword the password to set
	 */
	public void setPassword(String pPassword) {
		mPassword = pPassword;
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
	 * @return the email
	 */
	public String getEmail() {
		return mEmail;
	}
	/**
	 * @param pEmail the email to set
	 */
	public void setEmail(String pEmail) {
		mEmail = pEmail;
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
}
