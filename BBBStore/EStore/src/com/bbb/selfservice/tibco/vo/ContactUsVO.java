/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 04-November-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.tibco.vo;

import com.bbb.framework.integration.ServiceRequestBase;


public class ContactUsVO extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	
	
	/* ===================================================== *
 		MEMBER VARIABLES
	 * ===================================================== */
	private String mEmailMessage;
	private String mOrderNumber;
	private String mContactType;
	private String mSubjectCategory;
	private String mTimeZone;
	private String mTimeCall;
	private String mFirstName;
	private String mLastName;
	private String mGender;
	private String mEmail;
	private String mPhoneNumber;
	private String mPhoneExt;
	private String mSubmitDate;
	private String mSiteFlag;
	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	/**
	 * @return the emailMessage
	 */
	public String getEmailMessage() {
		return mEmailMessage;
	}
	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return mOrderNumber;
	}
	/**
	 * @return the contactType
	 */
	public String getContactType() {
		return mContactType;
	}
	/**
	 * @return the subjectCategory
	 */
	public String getSubjectCategory() {
		return mSubjectCategory;
	}
	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return mTimeZone;
	}
	/**
	 * @return the timeCall
	 */
	public String getTimeCall() {
		return mTimeCall;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return mFirstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return mLastName;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return mGender;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return mEmail;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	/**
	 * @return the phoneExt
	 */
	public String getPhoneExt() {
		return mPhoneExt;
	}
	/**
	 * @param pEmailMessage the emailMessage to set
	 */
	public void setEmailMessage(String pEmailMessage) {
		mEmailMessage = pEmailMessage;
	}
	/**
	 * @param pOrderNumber the orderNumber to set
	 */
	public void setOrderNumber(String pOrderNumber) {
		mOrderNumber = pOrderNumber;
	}
	/**
	 * @param pContactType the contactType to set
	 */
	public void setContactType(String pContactType) {
		mContactType = pContactType;
	}
	/**
	 * @param pSubjectCategory the subjectCategory to set
	 */
	public void setSubjectCategory(String pSubjectCategory) {
		mSubjectCategory = pSubjectCategory;
	}
	/**
	 * @param pTimeZone the timeZone to set
	 */
	public void setTimeZone(String pTimeZone) {
		mTimeZone = pTimeZone;
	}
	/**
	 * @param pTimeCall the timeCall to set
	 */
	public void setTimeCall(String pTimeCall) {
		mTimeCall = pTimeCall;
	}
	/**
	 * @param pFirstName the firstName to set
	 */
	public void setFirstName(String pFirstName) {
		mFirstName = pFirstName;
	}
	/**
	 * @param pLastName the lastName to set
	 */
	public void setLastName(String pLastName) {
		mLastName = pLastName;
	}
	/**
	 * @param pGender the gender to set
	 */
	public void setGender(String pGender) {
		mGender = pGender;
	}
	/**
	 * @param pEmail the email to set
	 */
	public void setEmail(String pEmail) {
		mEmail = pEmail;
	}
	/**
	 * @param pPhoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String pPhoneNumber) {
		mPhoneNumber = pPhoneNumber;
	}
	/**
	 * @param pPhoneExt the phoneExt to set
	 */
	public void setPhoneExt(String pPhoneExt) {
		mPhoneExt = pPhoneExt;
	}
	
	
	/**
	 * @return the submitDate
	 */
	public String getSubmitDate() {
		return mSubmitDate;
	}
	/**
	 * @param pSubmitDate the submitDate to set
	 */
	public void setSubmitDate(String pSubmitDate) {
		mSubmitDate = pSubmitDate;
	}
	/**
	 * @return the siteFlag
	 */
	public String getSiteFlag() {
		return mSiteFlag;
	}
	/**
	 * @param pSiteFlag the siteFlag to set
	 */
	public void setSiteFlag(String pSiteFlag) {
		mSiteFlag = pSiteFlag;
	}
	/**
	 * Very important to implement this method and let the framework know the
	 * web service name that is being implemented
	 */
	@Override
	public String getServiceName() {
		return "contactusTibcoMessage";
	}

	/**
	 * Very important to implement this method and let the framework know the
	 * whether the web service response needs to be cached
	 */
	@Override
	public Boolean isCacheEnabled() {
		return false;
	}

	

}
