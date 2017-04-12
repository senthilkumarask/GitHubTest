/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CouponsResponseVO.java
 *
 *  DESCRIPTION: CouponsResponseVO : Response VO for ProcessCoupon web service  
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */
package com.bbb.account.validatecoupon;

import com.bbb.framework.integration.ServiceResponseBase;

public class CouponsResponseVO extends ServiceResponseBase {

	private static final long serialVersionUID = 1L;
	private String mEntryCd;
	private String mDescription;

	/**
	 * @return the entryCd
	 */
	public String getEntryCd() {
		return mEntryCd;
	}

	/**
	 * @param pEntryCd
	 *            the entryCd to set
	 */
	public void setEntryCd(String pEntryCd) {
		mEntryCd = pEntryCd;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * @param pDescription
	 *            the description to set
	 */
	public void setDescription(String pDescription) {
		mDescription = pDescription;
	}

}
