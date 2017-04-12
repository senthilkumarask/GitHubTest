package com.bbb.exception;

/**
 * This class is wrapper over Exception. This is used wherever there is error due to system failure
 *  
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class BBBSystemException extends BBBException {
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
	
	public BBBSystemException(String errorMsg) {
		super(errorMsg);
	}
	public BBBSystemException(final String errorCode, final String errorMsg) {
		super(errorCode+":"+errorMsg);
		this.mErrorCode = errorCode;
	}
	public BBBSystemException(String errorMsg, Throwable cause) {
		super(errorMsg,cause);
	}
	public BBBSystemException(final String errorCode, final String errorMsg, final Throwable cause) {
		super(errorCode+":"+errorMsg,cause);
		this.mErrorCode = errorCode;
	}
}
