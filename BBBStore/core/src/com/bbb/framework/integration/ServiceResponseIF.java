/*
 *
 * File  : ServiceResponseIF.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.integration;

import java.io.Serializable;

import com.bbb.framework.integration.vo.ResponseErrorVO;

/**
 * The Interface ServiceResponseIF.
 * 
 * @version 1.0
 */
public interface ServiceResponseIF extends Serializable {
	
	/**
	 * Gets the error.
	 * 
	 * @return the error
	 */
	public ResponseErrorVO getError();
	
	/**
	 * Sets the error.
	 * 
	 * @param error the new error
	 */
	public void setError(final ResponseErrorVO error);
	
	/**
	 * Checks for error.
	 * 
	 * @return true, if successful
	 */
	public boolean hasError();
}
