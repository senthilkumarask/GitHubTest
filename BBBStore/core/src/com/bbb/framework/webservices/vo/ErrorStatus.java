/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ErrorStatus.java
 *
 *  DESCRIPTION: ErrorStatus : Error VO web service  
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */
package com.bbb.framework.webservices.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author jsidhu
 *
 */
public class ErrorStatus  implements Serializable {

	 private static final long serialVersionUID = 1L;
	 private boolean mErrorExists; 
	 private String mErrorMessage; 
	 private String mDisplayMessage; 
	 private int mErrorId; 
	 private List<ValidationError> mValidationErrors;
	 
	 
	 
	/**
	 * @return the errorExists
	 */
	public boolean isErrorExists() {
		return mErrorExists;
	}
	/**
	 * @param pErrorExists the errorExists to set
	 */
	public void setErrorExists(boolean pErrorExists) {
		mErrorExists = pErrorExists;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return mErrorMessage;
	}
	/**
	 * @param pErrorMessage the errorMessage to set
	 */
	public void setErrorMessage(String pErrorMessage) {
		mErrorMessage = pErrorMessage;
	}
	/**
	 * @return the displayMessage
	 */
	public String getDisplayMessage() {
		return mDisplayMessage;
	}
	/**
	 * @param pDisplayMessage the displayMessage to set
	 */
	public void setDisplayMessage(String pDisplayMessage) {
		mDisplayMessage = pDisplayMessage;
	}
	/**
	 * @return the errorId
	 */
	public int getErrorId() {
		return mErrorId;
	}
	/**
	 * @param pErrorId the errorId to set
	 */
	public void setErrorId(int pErrorId) {
		mErrorId = pErrorId;
	}
	/**
	 * @return the validationErrors
	 */
	public List<ValidationError> getValidationErrors() {
		return mValidationErrors;
	}
	/**
	 * @param pValidationErrors the validationErrors to set
	 */
	public void setValidationErrors(List<ValidationError> pValidationErrors) {
		mValidationErrors = pValidationErrors;
	}
	 

	
}
