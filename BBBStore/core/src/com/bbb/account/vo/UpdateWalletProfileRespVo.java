package com.bbb.account.vo;

import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class UpdateWalletProfileRespVo extends ServiceResponseBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ErrorStatus mErrorStatus;


	public ErrorStatus getmErrorStatus() {
		return mErrorStatus;
	}

	public void setmErrorStatus(ErrorStatus mErrorStatus) {
		this.mErrorStatus = mErrorStatus;
	}
}

