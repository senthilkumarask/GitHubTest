package com.bbb.common;

import atg.nucleus.GenericService;

public class TBSSessionComponent extends GenericService {
	
	/**
	 * mEmailId
	 */
	private String mEmailId;
	/**
	 * mMobileNumber
	 */
	private String mMobileNumber;
	
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return mEmailId;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mMobileNumber;
	}

	/**
	 * @param pEmailId the emailId to set
	 */
	public void setEmailId(String pEmailId) {
		mEmailId = pEmailId;
	}

	/**
	 * @param pMobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String pMobileNumber) {
		mMobileNumber = pMobileNumber;
	}

}
