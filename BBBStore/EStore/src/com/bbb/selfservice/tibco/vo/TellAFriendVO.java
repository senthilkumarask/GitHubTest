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

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE;
import com.bbb.framework.integration.ServiceRequestBase;


public class TellAFriendVO extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/* ===================================================== *
 		MEMBER VARIABLES
	 * ===================================================== */
	private  BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE mType;

	private String mRecipientEmailAddr;
	private String mRecipientFirstName;
	private String mRecipientLastName;
	private String mSenderEmailAddr;
	private String mSenderFirstName;
	private String mSenderLastName;
	private boolean mEmailCopy;
	private String mSiteId;
	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	public TELLAFRIEND_REQUEST_TYPE getType() {
		return mType;
	}
	public void setType( BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE pType) {
		this.mType = pType;
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
		return "tellAFriendTibcoMessage";
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
	 * @return the recipientEmailAddr
	 */
	public String getRecipientEmailAddr() {
		return mRecipientEmailAddr;
	}
	/**
	 * @param pRecipientEmailAddr the recipientEmailAddr to set
	 */
	public void setRecipientEmailAddr(String pRecipientEmailAddr) {
		mRecipientEmailAddr = pRecipientEmailAddr;
	}
	/**
	 * @return the recipientFirstName
	 */
	public String getRecipientFirstName() {
		return mRecipientFirstName;
	}
	/**
	 * @param pRecipientFirstName the recipientFirstName to set
	 */
	public void setRecipientFirstName(String pRecipientFirstName) {
		mRecipientFirstName = pRecipientFirstName;
	}
	/**
	 * @return the recipientLastName
	 */
	public String getRecipientLastName() {
		return mRecipientLastName;
	}
	/**
	 * @param pRecipientLastName the recipientLastName to set
	 */
	public void setRecipientLastName(String pRecipientLastName) {
		mRecipientLastName = pRecipientLastName;
	}
	/**
	 * @return the senderEmailAddr
	 */
	public String getSenderEmailAddr() {
		return mSenderEmailAddr;
	}
	/**
	 * @param pSenderEmailAddr the senderEmailAddr to set
	 */
	public void setSenderEmailAddr(String pSenderEmailAddr) {
		mSenderEmailAddr = pSenderEmailAddr;
	}
	/**
	 * @return the senderFirstName
	 */
	public String getSenderFirstName() {
		return mSenderFirstName;
	}
	/**
	 * @param pSenderFirstName the senderFirstName to set
	 */
	public void setSenderFirstName(String pSenderFirstName) {
		mSenderFirstName = pSenderFirstName;
	}
	/**
	 * @return the senderLastName
	 */
	public String getSenderLastName() {
		return mSenderLastName;
	}
	/**
	 * @param pSenderLastName the senderLastName to set
	 */
	public void setSenderLastName(String pSenderLastName) {
		mSenderLastName = pSenderLastName;
	}
	/**
	 * @return the emailCopy
	 */
	public boolean isEmailCopy() {
		return mEmailCopy;
	}
	/**
	 * @param pEmailCopy the emailCopy to set
	 */
	public void setEmailCopy(boolean pEmailCopy) {
		mEmailCopy = pEmailCopy;
	}	

}
