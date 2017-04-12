package com.bbb.account.service.profile;

import java.io.Serializable;

public class RequestVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mIpAddress;
	private String mToken;
	private String mSiteId;
	private int mRequestType;
	private String mSource;
	
	/**
	 * @return the source
	 */
	public String getSource() {
		return mSource;
	}
	/**
	 * @param pSource the source to set
	 */
	public void setSource(String pSource) {
		mSource = pSource;
	}
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return mIpAddress;
	}
	/**
	 * @param pIpAddress the ipAddress to set
	 */
	public void setIpAddress(String pIpAddress) {
		mIpAddress = pIpAddress;
	}
	/**
	 * @return the token
	 */
	public String getToken() {
		return mToken;
	}
	/**
	 * @param pToken the token to set
	 */
	public void setToken(String pToken) {
		mToken = pToken;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return mSiteId;
	}
	/**
	 * @param pSiteId the siteId to set
	 */
	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}
	/**
	 * @return the requestType
	 */
	public int getRequestType() {
		return mRequestType;
	}
	/**
	 * @param pRequestType the requestType to set
	 */
	public void setRequestType(int pRequestType) {
		mRequestType = pRequestType;
	}
	
}
