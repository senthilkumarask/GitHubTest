/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ValidateCouponRequestVO.java
 *
 *  DESCRIPTION: ValidateCouponResponseVO : Response VO for ProcessCoupon web service  
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */
package com.bbb.account.validatecoupon;


import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class ActivateCouponResponseVO extends ServiceResponseBase {
	private static final long serialVersionUID = 1L;
	private ErrorStatus mStatus;
	private String mCouponStatus;
	private String endDate;
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the status
	 */
	public ErrorStatus getStatus() {
		return mStatus;
	}
	/**
	 * @param pStatus the status to set
	 */
	public void setStatus(ErrorStatus pStatus) {
		mStatus = pStatus;
	}
	/**
	 * @return the couponStatus
	 */
	public String getCouponStatus() {
		return mCouponStatus;
	}
	/**
	 * @param pCouponStatus the couponStatus to set
	 */
	public void setCouponStatus(String pCouponStatus) {
		mCouponStatus = pCouponStatus;
	}
	

	
}
