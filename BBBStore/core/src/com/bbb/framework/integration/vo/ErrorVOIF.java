/*
 *
 * File  : ErrorVOIF.java
 * Project:     BBB
 */
package com.bbb.framework.integration.vo;

import com.bbb.framework.integration.exception.ServiceError;
import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceErrorType;

/**
 * The Interface ErrorVOIF.
 * 
 * @version 1.0
 */
public interface ErrorVOIF {
	
	/**
	 * Gets the error code.
	 * 
	 * @return the error code
	 */
	public ServiceError getErrorCode();
	
	/**
	 * Gets the trans id.
	 * 
	 * @return the trans id
	 */
	public String getTransId();
	
	/**
	 * Gets the error fields.
	 * 
	 * @return the error fields
	 */
	public String[] getErrorFields();
	
	/**
	 * Gets the error type.
	 * 
	 * @return the error type
	 */
	public ServiceErrorType getErrorType();
	
	/**
	 * Gets the error msg.
	 * 
	 * @return the error msg
	 */
	public String getErrorMsg();
	
	/**
	 * Gets the actor.
	 * 
	 * @return the actor
	 */
	public String getActor();
}
