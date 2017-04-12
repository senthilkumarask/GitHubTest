package com.bbb.rest.checkout.vo;

import java.io.Serializable;
import java.util.List;


/**
 * VO to get all coupons information
 * 
 *
 */
public class AppliedCouponsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean errorExist;
	private String errorMsg;
	private List<AppliedCouponListVO> appliedCpnListVO;
	
	public boolean isErrorExist() {
		return this.errorExist;
	}
	public void setErrorExist(final boolean errorExist) {
		this.errorExist = errorExist;
	}
	public String getErrorMsg() {
		return this.errorMsg;
	}
	public void setErrorMsg(final String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List<AppliedCouponListVO> getAppliedCouponListVOs() {
		return this.appliedCpnListVO;
	}
	public void setAppliedCouponListVOs(
			final List<AppliedCouponListVO> appliedCpnListVO) {
		this.appliedCpnListVO = appliedCpnListVO;
	}
	
	
}
