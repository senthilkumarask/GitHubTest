package com.bbb.account.vo;

import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class CouponResponseVo extends ServiceResponseBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ErrorStatus mErrorStatus;
	private List<CouponListVo> mCouponList;	
	
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
	 * Returns the coupon List
	 * @return
	 */
	public List<CouponListVo> getCouponList() {
		return mCouponList;
	}
	
	/**
	 * Sets the coupons list
	 * @param mCouponList
	 */
	public void setCouponList(List<CouponListVo> pCouponList) {
		this.mCouponList = pCouponList;
	}


	@Override
	public String toString() {
		return "CouponResponseVo [mErrorStatus=" + mErrorStatus
				+ ", mCouponList=" + mCouponList + "]";
	}
	
}
