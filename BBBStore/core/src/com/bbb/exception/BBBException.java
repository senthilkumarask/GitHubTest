package com.bbb.exception;

/**
 * This class is wrapper over Exception.
 *  
 * @author Pradeep Reddy
 */
public class BBBException extends Exception {
	/* ===================================================== *
	 	CONSTANTS
	 * ===================================================== */
	private static final long serialVersionUID = 1L;
	
	/* ===================================================== *
	 	MEMBER VARIABLE
	 ======================================================== */
	private String mErrorCode;
	
	/* ===================================================== *
	 	SETTER and GETTER
	 * ===================================================== */
	public String getErrorCode() {
		return mErrorCode;
	}
	public void setErrorCode(String errorCode) {
		this.mErrorCode = errorCode;
	}	
	/* ===================================================== *
	 	CONSTRUCTORS
	 * ===================================================== */
	public BBBException(final String errorMsg) {
		super(errorMsg);
	}	
	public BBBException(final String errorCode, final String errorMsg) {
		super(errorMsg);
		this.mErrorCode = errorCode;
	}
	public BBBException(final String errorMsg, final Throwable cause) {
		super(errorMsg,cause);
	}
	public BBBException(final String errorCode, final String errorMsg, final Throwable cause) {
		super(errorMsg,cause);
		this.mErrorCode = errorCode;
	}
	
	public BBBException(String message, Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
