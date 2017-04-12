/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ProfileRequestVO.java
 *
 *  DESCRIPTION: ProfileRequestVO : Request VO for profile creation web service call - Levelor
 *  HISTORY:
 *  10/2/12 Initial version
 *
 */

package com.bbb.account.webservices.vo;

public class ProfileRequestVO extends GenericProfileRequestVO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private String password;
	private String confirmPassword;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String mobileNumber;
	private boolean emailOptIn;
	private boolean autoExtendProfile;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}
	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}
	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(final String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}
	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(final String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	/**
	 * @return the emailOptIn
	 */
	public boolean isEmailOptIn() {
		return emailOptIn;
	}
	/**
	 * @param emailOptIn the emailOptIn to set
	 */
	public void setEmailOptIn(final boolean emailOptIn) {
		this.emailOptIn = emailOptIn;
	}
	/**
	 * @return the autoExtendProfile
	 */
	public boolean isAutoExtendProfile() {
		return autoExtendProfile;
	}
	/**
	 * @param autoExtendProfile the autoExtendProfile to set
	 */
	public void setAutoExtendProfile(final boolean autoExtendProfile) {
		this.autoExtendProfile = autoExtendProfile;
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
