/**
 * --------------------------------------------------------------------------------
 * Copyright 2013 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: amohile
 *
 * Created on: 13-April-2013
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.tibco.vo;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreConstants.BABY_BOOK_REQUEST_TYPE;
import com.bbb.framework.integration.ServiceRequestBase;


public class ActivateBigBlueVO extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/* ===================================================== *
 		MEMBER VARIABLES
	 * ===================================================== */
	private  BBBCoreConstants.BABY_BOOK_REQUEST_TYPE mType;

	private String mOfferCd;
	private String mEmailAddr;
	private String mMobilePhone;
	private String mPromoEmailFlag;
	private String mPromoMobileFlag;	
	private String mSiteId;
	//private Date mSubmitDate;
	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	public String getOfferCd() {
		return mOfferCd;
	}
	public void setOfferCd(String mOfferCd) {
		this.mOfferCd = mOfferCd;
	}
	
	public String getEmailAddr() {
		return mEmailAddr;
	}
	public void setEmailAddr(String pEmailAddr) {
		this.mEmailAddr = pEmailAddr;
	}
	
	public String getMobilePhone() {
		return mMobilePhone;
	}
	public void setMobilePhone(String mMobilePhone) {
		this.mMobilePhone = mMobilePhone;
	}
	
	public String getPromoEmailFlag() {
		return mPromoEmailFlag;
	}
	public void setPromoEmailFlag(String mPromoEmailFlag) {
		this.mPromoEmailFlag = mPromoEmailFlag;
	}
	
	public String getPromoMobileFlag() {
		return mPromoMobileFlag;
	}
	public void setPromoMobileFlag(String mPromoMobileFlag) {
		this.mPromoMobileFlag = mPromoMobileFlag;
	}	
	/*
	public Date getSubmitDate() {
		return mSubmitDate;
	}
	public void setSubmitDate(Date pSubmitDate) {
		mSubmitDate = pSubmitDate;
	}
	*/	
	public BABY_BOOK_REQUEST_TYPE getType() {
		return mType;
	}
	public void setType( BBBCoreConstants.BABY_BOOK_REQUEST_TYPE pType) {
		this.mType = pType;
	}	
	
	public String getSiteId() {
		return mSiteId;
	}
	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}
	/**
	 * Very important to implement this method and let the framework know the
	 * web service name that is being implemented
	 */
	@Override
	public String getServiceName() {
		return "ActBigBlueWS";
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
