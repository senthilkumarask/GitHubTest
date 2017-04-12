/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 10-November-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.tibco.vo;

import com.bbb.framework.integration.ServiceRequestBase;

public class SurveyVO extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	

	/* ===================================================== *
 		MEMBER VARIABLES
	 * ===================================================== */
	private String mSubmitDate;
	private String mUserName;
	private String mEmailAddress;
	private String mSendEmail;
	private String mShoppedAtBefore;
	private String mFeatures;
	private String mFavorite;
	private String mComments;
	private String mLocation;
	private String mAge;
	private String mGender;
	private String mSiteFlag;
	
	

	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	
	
	/**
	 * @return the submitDate
	 */
	public String getSubmitDate() {
		return mSubmitDate;
	}
	/**
	 * @return the name
	 */
	public String getUserName() {
		return mUserName;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return mEmailAddress;
	}
	/**
	 * @return the sendEmail
	 */
	public String getSendEmail() {
		return mSendEmail;
	}
	/**
	 * @return the shoppedAtBefore
	 */
	public String getShoppedAtBefore() {
		return mShoppedAtBefore;
	}
	/**
	 * @return the features
	 */
	public String getFeatures() {
		return mFeatures;
	}
	/**
	 * @return the favorite
	 */
	public String getFavorite() {
		return mFavorite;
	}
	/**
	 * @return the comments
	 */
	public String getComments() {
		return mComments;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return mLocation;
	}
	/**
	 * @return the age
	 */
	public String getAge() {
		return mAge;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return mGender;
	}
	/**
	 * @param pSubmitDate the submitDate to set
	 */
	public void setSubmitDate(String pSubmitDate) {
		mSubmitDate = pSubmitDate;
	}
	/**
	 * @param pName the name to set
	 */
	public void setUserName(String pUserName) {
		mUserName = pUserName;
	}
	/**
	 * @param pEmailAddress the emailAddress to set
	 */
	public void setEmailAddress(String pEmailAddress) {
		mEmailAddress = pEmailAddress;
	}
	/**
	 * @param pSendEmail the sendEmail to set
	 */
	public void setSendEmail(String pSendEmail) {
		mSendEmail = pSendEmail;
	}
	/**
	 * @param pShoppedAtBefore the shoppedAtBefore to set
	 */
	public void setShoppedAtBefore(String pShoppedAtBefore) {
		mShoppedAtBefore = pShoppedAtBefore;
	}
	/**
	 * @param pFeatures the features to set
	 */
	public void setFeatures(String pFeatures) {
		mFeatures = pFeatures;
	}
	/**
	 * @param pFavorite the favorite to set
	 */
	public void setFavorite(String pFavorite) {
		mFavorite = pFavorite;
	}
	/**
	 * @param pComments the comments to set
	 */
	public void setComments(String pComments) {
		mComments = pComments;
	}
	/**
	 * @param pLocation the location to set
	 */
	public void setLocation(String pLocation) {
		mLocation = pLocation;
	}
	/**
	 * @param pAge the age to set
	 */
	public void setAge(String pAge) {
		mAge = pAge;
	}
	/**
	 * @param pGender the gender to set
	 */
	public void setGender(String pGender) {
		mGender = pGender;
	}
	
	/**
	 * @return the mSiteFlag
	 */
	public String getSiteFlag() {
		return mSiteFlag;
	}
	/**
	 * @param mSiteFlag the mSiteFlag to set
	 */
	public void setSiteFlag(String pSiteFlag) {
		this.mSiteFlag = pSiteFlag;
	}
	
	/**
	 * Very important to implement this method and let the framework know the
	 * web service name that is being implemented
	 */
	@Override
	public String getServiceName() {
		return "surveyTibcoMessage";
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
