/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written consent is prohibited.
 *
 *  FILE:  RegistryWithATGInfoResponseVO.java
 *
 *  DESCRIPTION: RegistryWithATGInfoResponseVO : Response VO for UpdateRegistryWithATGInfo web service  
 *  HISTORY:
 *  04/02/12 Initial version
 *
 */
package com.bbb.account.vo;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class ProfileSyncResponseVO extends ServiceResponseBase {

	/**
	 * 
	 */
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
	public void setErrorStatus(final ErrorStatus pErrorStatus) {
		this.mErrorStatus = pErrorStatus;
	}

}
