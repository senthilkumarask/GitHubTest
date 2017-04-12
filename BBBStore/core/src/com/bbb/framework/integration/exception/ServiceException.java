/*
 *
 * File  : ServiceException.java
 * Project:     BBB
 */
package com.bbb.framework.integration.exception;

import com.bbb.framework.integration.vo.ErrorVOIF;
import com.bbb.framework.integration.vo.ResponseErrorVO;

/**
 * The Class ServiceException.
 * 
 * @version 1.0
 */
public abstract class ServiceException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8801009789205461776L;
	
	
	/**
	 * Instantiates a new service exception.
	 * 
	 * @param errMsg the err msg
	 */
	public ServiceException(final String errMsg) {
		super(errMsg);
	}

	/**
	 * Instantiates a new service exception.
	 * 
	 * @param pException the exception
	 */
	public ServiceException(final Exception pException) {
        super(pException.getMessage());
		if (pException instanceof ServiceException) {
			final ServiceException se = (ServiceException) pException;
			this.setErrorVO(se.getErrorVO());
		} else {
			// if not a ServiceException, create an unknown error
			
			final ResponseErrorVO error = new ResponseErrorVO();
			//TODO: ServiceException - Set error code and error msg. 
			/*error.setErrorCode(ServiceResponseError.UNKNOWN); */
			error.setErrorMsg(pException.toString());
			this.setErrorVO(error);
		}
    }

	/**
	 * Sets the error vo.
	 * 
	 * @param errorVO2 the new error vo
	 */
	public abstract void setErrorVO(final ErrorVOIF errorVO2);
	
	/**
	 * Gets the error vo.
	 * 
	 * @return the error vo
	 */
	public abstract ErrorVOIF getErrorVO();

}
