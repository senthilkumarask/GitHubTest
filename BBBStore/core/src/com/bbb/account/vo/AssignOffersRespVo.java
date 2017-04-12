package com.bbb.account.vo;

//import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class AssignOffersRespVo extends ServiceResponseBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ErrorStatus mErrorStatus;
	private String uniqueCouponCd; 

	public String getUniqueCouponCd() {
		return uniqueCouponCd;
	}

	public void setUniqueCouponCd(String uniqueCouponCd) {
		this.uniqueCouponCd = uniqueCouponCd;
	}

	public ErrorStatus getmErrorStatus() {
		return mErrorStatus;
	}

	public void setmErrorStatus(ErrorStatus mErrorStatus) {
		this.mErrorStatus = mErrorStatus;
	}
}

