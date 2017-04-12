package com.bbb.framework.webservices.test.vo;

import com.bbb.framework.integration.ServiceRequestBase;

public class CouponRequestVo extends ServiceRequestBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mUserToken;
	private String mSiteFlag;
	private String mEmailAddr;
	private String mMobilePhone;
	private String mServiceName;
	
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
	
	/**
	 * returns the user token
	 * @return
	 */
	public String getUserToken() {
		return mUserToken;
	}
	
	/**
	 * sets the User token
	 * @param pUserToken
	 */
	public void setUserToken(String pUserToken) {
		this.mUserToken = pUserToken;
	}
	
	/**
	 * gets the Site Flag
	 * @return
	 */
	public String getSiteFlag() {
		return mSiteFlag;
	}
	
	/**
	 * sets the Site Flag
	 * @param pSiteFlag
	 */
	public void setSiteFlag(String pSiteFlag) {
		this.mSiteFlag = pSiteFlag;
	}
	
	/**
	 * get the Email Address
	 * @return
	 */
	public String getEmailAddr() {
		return mEmailAddr;
	}
	
	/**
	 * Sets the Email Address
	 * @param pEmailAddr
	 */
	public void setEmailAddr(String pEmailAddr) {
		this.mEmailAddr = pEmailAddr;
	}
	
	/**
	 * gets the Mobile phone
	 * @return
	 */
	public String getMobilePhone() {
		return mMobilePhone;
	}
	
	/**
	 * sets the Mobile Phone
	 * @param pMobilePhone
	 */
	public void setMobilePhone(String pMobilePhone) {
		this.mMobilePhone = pMobilePhone;
	}
	
	
}
