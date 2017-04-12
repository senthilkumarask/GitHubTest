package com.bbb.exception;

/**
 * This class is wrapper over Exception. This is used wherever Business condition violation occurs
 *  
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBBusinessException extends BBBException {
	/* ===================================================== *
	 	CONSTANTS
	 * ===================================================== */
	private static final long serialVersionUID = 1L;
	
	/* ===================================================== *
	 	MEMBER VARIABLE
	 ======================================================== */
	private String mErrorCode;
	
	
	/* ===================================================== *
 	CONSTRUCTORS
 * ===================================================== */
	public BBBBusinessException(final String errorMsg) {
		super(errorMsg, null, true, false);
	}	
	
	public BBBBusinessException(final String errorCode, final String errorMsg) {
		super(errorCode + ":" + errorMsg, null, true, false);
		
		this.mErrorCode = errorCode;
	}
	
	
	public BBBBusinessException(final String errorMsg, final Throwable cause) {
		super(errorMsg,cause, true, false);
	}
	public BBBBusinessException(final String errorCode, final String errorMsg, final Throwable cause) {
		super(errorCode + ":" + errorMsg, cause, true, false);
		this.mErrorCode = errorCode;
	}
	/* ===================================================== *
	 	SETTER and GETTER
	 * ===================================================== */
	
	public String getErrorCode() {
		return mErrorCode;
	}
	public void setErrorCode(String errorCode) {
		this.mErrorCode = errorCode;
	}	
	
	
	
}