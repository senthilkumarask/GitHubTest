/*
 *
 * File  : ServiceError.java
 * Project:     BBB
 */
package com.bbb.framework.integration.exception;

import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceErrorType;

/**
 * The Interface ServiceError.
 * 
 * @version 1.0
 */
public interface ServiceError {
	
	/**
	 * Gets the error type.
	 * 
	 * @return the error type
	 */
	public ServiceErrorType getErrorType();
}
