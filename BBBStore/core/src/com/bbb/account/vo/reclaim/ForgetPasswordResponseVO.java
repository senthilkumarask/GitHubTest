/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ForgetPasswordResponseVO.java
 *
 *  DESCRIPTION: ForgetPasswordResponseVO : Response VO for forget password web service  
 *  HISTORY:
 *  19/12/11 Initial version
 *
 */
package com.bbb.account.vo.reclaim;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class ForgetPasswordResponseVO extends ServiceResponseBase{

	
	private static final long serialVersionUID = 1L;
	
	private ErrorStatus mErrorStatus;
	
	
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
	
	private String mMemberToken;
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
	
}
