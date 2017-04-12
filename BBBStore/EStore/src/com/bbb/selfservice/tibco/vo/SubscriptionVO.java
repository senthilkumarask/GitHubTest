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

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreConstants.FREQUENCY;
import com.bbb.constants.BBBCoreConstants.SUBSCRIPTION_TYPE;
import com.bbb.framework.integration.ServiceRequestBase;


public class SubscriptionVO extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	
	
	/* ===================================================== *
 		MEMBER VARIABLES
	 * ===================================================== */
	private  BBBCoreConstants.SUBSCRIPTION_TYPE mType;
	private BBBCoreConstants.FREQUENCY mFrequency;
	private String mEmailAddr;
	private String mConfirmEmailAddr;
	private String mSalutation;
	private String mFirstName;
	private String mLastName;
	private String mAddressLine1;
	private String mAddressLine2;
	private String mCity;
	private String mState;
	private String mZipcode;
	private String mPhoneNumber;
	private String mMobileNumber;
	private boolean mEmailOffer;
	private boolean mDirectMailOffer;
	private boolean mMobileOffer;
	private String mSiteId;
	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	public SUBSCRIPTION_TYPE getType() {
		return mType;
	}
	public void setType( BBBCoreConstants.SUBSCRIPTION_TYPE pType) {
		this.mType = pType;
	}
	
	public FREQUENCY getFrequency() {
		return mFrequency;
	}
	public void setFrequency(FREQUENCY pFrequency) {
		this.mFrequency = pFrequency;
	}
	public String getEmailAddr() {
		return mEmailAddr;
	}
	public void setEmailAddr(String pEmailAddr) {
		this.mEmailAddr = pEmailAddr;
	}
	public String getConfirmEmailAddr() {
		return mConfirmEmailAddr;
	}
	public void setConfirmEmailAddr(String pConfirmEmailAddr) {
		this.mConfirmEmailAddr = pConfirmEmailAddr;
	}
	public String getSalutation() {
		return mSalutation;
	}
	public void setSalutation(String pSalutation) {
		this.mSalutation = pSalutation;
	}
	public String getFirstName() {
		return mFirstName;
	}
	public void setFirstName(String pFirstName) {
		this.mFirstName = pFirstName;
	}
	public String getLastName() {
		return mLastName;
	}
	public void setLastName(String pLastName) {
		this.mLastName = pLastName;
	}
	public String getAddressLine1() {
		return mAddressLine1;
	}
	public void setAddressLine1(String pAddressLine1) {
		this.mAddressLine1 = pAddressLine1;
	}
	public String getAddressLine2() {
		return mAddressLine2;
	}
	public void setAddressLine2(String pAddressLine2) {
		this.mAddressLine2 = pAddressLine2;
	}
	public String getCity() {
		return mCity;
	}
	public void setCity(String pCity) {
		this.mCity = pCity;
	}
	public String getState() {
		return mState;
	}
	public void setState(String pState) {
		this.mState = pState;
	}
	public String getZipcode() {
		return mZipcode;
	}
	public void setZipcode(String pZipcode) {
		this.mZipcode = pZipcode;
	}
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	public void setPhoneNumber(String pPhoneNumber) {
		this.mPhoneNumber = pPhoneNumber;
	}
	public String getMobileNumber() {
		return mMobileNumber;
	}
	public void setMobileNumber(String pMobileNumber) {
		this.mMobileNumber = pMobileNumber;
	}
	public boolean getEmailOffer() {
		return mEmailOffer;
	}
	public void setEmailOffer(boolean pEmailOffer) {
		this.mEmailOffer = pEmailOffer;
	}
	public boolean getDirectMailOffer() {
		return mDirectMailOffer;
	}
	public void setDirectMailOffer(boolean pDirectMailOffer) {
		this.mDirectMailOffer = pDirectMailOffer;
	}
	public boolean getMobileOffer() {
		return mMobileOffer;
	}
	public void setMobileOffer(boolean pMobileOffer) {
		this.mMobileOffer = pMobileOffer;
	}
	/**
	 * @return the siteFlag
	 */
	public String getSiteId() {
		return mSiteId;
	}
	/**
	 * @param pSiteFlag the siteFlag to set
	 */
	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}
	/**
	 * Very important to implement this method and let the framework know the
	 * web service name that is being implemented
	 */
	@Override
	public String getServiceName() {
		return "subscriptionTibcoMessage";
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
