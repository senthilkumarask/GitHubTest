package com.bbb.account.vo;

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
	private String walletId;
	private String showActiveOnly;
	
	public String getWalletId() {
		return walletId;
	}
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	public String getShowActiveOnly() {
		return showActiveOnly;
	}
	public void setShowActiveOnly(String showActiveOnly) {
		this.showActiveOnly = showActiveOnly;
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
	

	@Override
	public String toString() {
		return "CouponRequestVo [mUserToken=" + mUserToken + ", mSiteFlag="
				+ mSiteFlag + ", mEmailAddr=" + mEmailAddr + ", mMobilePhone="
				+ mMobilePhone + ", mServiceName=" + mServiceName
				+ ", walletId=" + walletId + ", showActiveOnly="
				+ showActiveOnly + "]";
	}
	
	
}
