/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ForgotPasswordRequestVO.java
 *
 *  DESCRIPTION: ForgotPasswordRequestVO : Request VO for forgot password web service call - Levelor
 *  HISTORY:
 *  9/2/12 Initial version
 *
 */

package com.bbb.account.webservices.vo;

public class ForgotPasswordRequestVO extends GenericProfileRequestVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private String siteId;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(final String email) {
		this.email = email;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(final String siteId) {
		this.siteId = siteId;
	}

	
}
