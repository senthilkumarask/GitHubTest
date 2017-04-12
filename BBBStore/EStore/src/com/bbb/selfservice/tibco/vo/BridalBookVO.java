/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: jsidhu
 *
 * Created on: 13-December-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.tibco.vo;

import java.util.Date;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreConstants.BRIDAL_REQUEST_TYPE;
import com.bbb.framework.integration.ServiceRequestBase;


public class BridalBookVO extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/* ===================================================== *
 		MEMBER VARIABLES
	 * ===================================================== */
	private  BBBCoreConstants.BRIDAL_REQUEST_TYPE mType;

	private String mEmailAddr;
	private String mFirstName;
	private String mLastName;
	private String mAddressLine1;
	private String mAddressLine2;
	private String mCity;
	private String mState;
	private String mZipcode;
	private String mPhoneNumber;
	private boolean mEmailOffer;
	private Date mEventDate;
	private String mSiteId;
	private String dateAsString;
	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	/**
	 * @return the dateAsString
	 */
	public String getDateAsString() {
		return dateAsString;
	}
	/**
	 * @param dateAsString the dateAsString to set
	 */
	public void setDateAsString(String dateAsString) {
		this.dateAsString = dateAsString;
	}
	public BRIDAL_REQUEST_TYPE getType() {
		return mType;
	}
	public void setType( BBBCoreConstants.BRIDAL_REQUEST_TYPE pType) {
		this.mType = pType;
	}	
	
	public String getEmailAddr() {
		return mEmailAddr;
	}
	public void setEmailAddr(String pEmailAddr) {
		this.mEmailAddr = pEmailAddr;
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
	
	public boolean getEmailOffer() {
		return mEmailOffer;
	}
	public void setEmailOffer(boolean pEmailOffer) {
		this.mEmailOffer = pEmailOffer;
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
		return "bridalBookRequestTibcoMessage";
	}

	/**
	 * Very important to implement this method and let the framework know the
	 * whether the web service response needs to be cached
	 */
	@Override
	public Boolean isCacheEnabled() {
		return false;
	}
	/**
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return mEventDate;
	}
	/**
	 * @param pEventDate the eventDate to set
	 */
	public void setEventDate(Date pEventDate) {
		mEventDate = pEventDate;
	}

	

}
