/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  GetAccountInfoResponseVO.java
 *
 *  DESCRIPTION: GetAccountInfoResponseVO : Response VO for get account info web service  
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */
package com.bbb.account.vo.reclaim;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class GetAccountInfoResponseVO extends ServiceResponseBase{

	
	private static final long serialVersionUID = 1L;
	
	private ErrorStatus mErrorStatus;
	private String mMemberId;
	private String mFirstName;
	private String mLastName;
	private String mDayPhone;
	private String mMobilePhone;
	private String mMemberToken;
    
	
	/**
	 * @return the mErrorStatus
	 */
	public ErrorStatus getErrorStatus() {
		return mErrorStatus;
	}
	/**
	 * @param pErrorStatus the mErrorStatus to set
	 */
	public void setErrorStatus(ErrorStatus pErrorStatus) {
		this.mErrorStatus = pErrorStatus;
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
	 * @return the mMemberId
	 */
	public String getMemberId() {
		return mMemberId;
	}
	/**
	 * @param mMemberId the mMemberId to set
	 */
	public void setMemberId(String pMemberId) {
		this.mMemberId = pMemberId;
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
	public void setFirstName(String pFirstName) {
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
	public void setLastName(String pLastName) {
		this.mLastName = pLastName;
	}
	/**
	 * @return the mDayPhone
	 */
	public String getDayPhone() {
		return mDayPhone;
	}
	/**
	 * @param mDayPhone the mDayPhone to set
	 */
	public void setDayPhone(String pDayPhone) {
		this.mDayPhone = pDayPhone;
	}
	/**
	 * @return the mMobilePhone
	 */
	public String getMobilePhone() {
		return mMobilePhone;
	}
	/**
	 * @param mMobilePhone the mMobilePhone to set
	 */
	public void setMobilePhone(String pMobilePhone) {
		this.mMobilePhone = pMobilePhone;
	}
	
	
}
