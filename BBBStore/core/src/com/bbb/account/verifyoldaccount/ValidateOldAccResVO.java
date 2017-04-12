package com.bbb.account.verifyoldaccount;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class ValidateOldAccResVO extends ServiceResponseBase{

	private static final long serialVersionUID = 1L;
	private ErrorStatus mStatus;
	private boolean mIsValid;
	
	
	/**
	 * @return the isValid
	 */
	public boolean getIsValid() {
		return mIsValid;
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
	 * @param pIsValid the isValid to set
	 */
	public void setIsValid(boolean pIsValid) {
		mIsValid = pIsValid;
	}
	
	
	
}
