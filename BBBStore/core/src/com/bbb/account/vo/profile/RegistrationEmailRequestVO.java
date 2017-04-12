/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE: RegistrationEmailRequestVO.java
 *
 *  DESCRIPTION: VO to hold the user registration Information
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  27/03/12 Initial version
 *
 */
package com.bbb.account.vo.profile;

import javax.xml.datatype.XMLGregorianCalendar;

import com.bbb.framework.integration.ServiceRequestBase;

public class RegistrationEmailRequestVO extends ServiceRequestBase {

	private static final long serialVersionUID = -3675504005014591834L;

	private String serviceName = "registrationEmailTibcoMessage";

	@Override
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

	private String emailType;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String siteFlag;
	private String phone1;
	private String phone2;
	private String optin;
	private String profileId;
	private String shareAccount;
	
	private transient XMLGregorianCalendar submitDate;

	/**
	 * @return the emailType
	 */
	public String getEmailType() {
		return emailType;
	}

	/**
	 * @param emailType the emailType to set
	 */
	public void setEmailType(final String emailType) {
		this.emailType = emailType;
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
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the submitDate
	 */
	public XMLGregorianCalendar getSubmitDate() {
		return submitDate;
	}

	/**
	 * @param submitDate the submitDate to set
	 */
	public void setSubmitDate(final XMLGregorianCalendar submitDate) {
		this.submitDate = submitDate;
	}

	/**
	 * @return the siteFlag
	 */
	public String getSiteFlag() {
		return siteFlag;
	}

	/**
	 * @param siteFlag the siteFlag to set
	 */
	public void setSiteFlag(final String siteFlag) {
		this.siteFlag = siteFlag;
	}

	/**
	 * @return the phone1
	 */
	public String getPhone1() {
		return phone1;
	}

	/**
	 * @param phone1 the phone1 to set
	 */
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	/**
	 * @return the phone2
	 */
	public String getPhone2() {
		return phone2;
	}

	/**
	 * @param phone2 the phone2 to set
	 */
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	/**
	 * @return the optin
	 */
	public String getOptin() {
		return optin;
	}

	/**
	 * @param optin the optin to set
	 */
	public void setOptin(String optin) {
		this.optin = optin;
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
	 * @return the shareAccount
	 */
	public String getShareAccount() {
		return shareAccount;
	}

	/**
	 * @param shareAccount the shareAccount to set
	 */
	public void setShareAccount(String shareAccount) {
		this.shareAccount = shareAccount;
	}
}
