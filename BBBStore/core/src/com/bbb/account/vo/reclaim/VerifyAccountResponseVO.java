/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  VerifyAccountResponseVO.java
 *
 *  DESCRIPTION: VerifyAccountResponseVO : Response VO for verify account web service  
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */
package com.bbb.account.vo.reclaim;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class VerifyAccountResponseVO extends ServiceResponseBase{

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

}
