/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ForgotPasswordResponseVO.java
 *
 *  DESCRIPTION: ForgotPasswordResponseVO : Response VO for forgot password web service call - Levelor
 *  HISTORY:
 *  9/2/12 Initial version
 *
 */

package com.bbb.account.webservices.vo;

import java.io.Serializable;
import java.util.Map;

public class ForgotPasswordResponseVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private boolean resetSuccess;
	private String profileId;
	private boolean error;
	private Map<String, String> errorMap;
	

	/**
	 * @return the resetSuccess
	 */
	public boolean isResetSuccess() {
		return resetSuccess;
	}
	/**
	 * @param resetSuccess the resetSuccess to set
	 */
	public void setResetSuccess(final boolean resetSuccess) {
		this.resetSuccess = resetSuccess;
	}
	/**
	 * @return the errorMap
	 */
	public Map<String, String> getErrorMap() {
		return errorMap;
	}
	/**
	 * @param errorMap the errorMap to set
	 */
	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
	/**
	 * @return the profileId
	 */
	public String getProfileId() {
		return profileId;
	}
	/**
	 * @param profileId the profileId to set
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(final boolean error) {
		this.error = error;
	}
	
}
