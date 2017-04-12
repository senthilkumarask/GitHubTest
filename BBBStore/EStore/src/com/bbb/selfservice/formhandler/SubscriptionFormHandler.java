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
package com.bbb.selfservice.formhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileTools;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBCoreConstants.FREQUENCY;
import com.bbb.constants.BBBCoreConstants.SUBSCRIPTION_TYPE;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.SubscriptionManager;
import com.bbb.selfservice.tibco.vo.SubscriptionVO;
import com.bbb.selfservice.tools.SubscriptionTools;
import com.bbb.utils.BBBUtility;

public class SubscriptionFormHandler extends BBBGenericFormHandler {

	private SubscriptionManager mSubscriptionManager;
	private SubscriptionTools mSubscriptionTools;
	private SiteContext mSiteContext;
	private SUBSCRIPTION_TYPE mType;
	private FREQUENCY mFrequency;
	private String mEmailAddr;
	private String mConfirmEmailAddr;
	private String[] mSalutation;
	private String mSelectedSalutation;
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
	private boolean mSuccessMessage = false;
	private String mSubscriptionSuccessURL;
	private String mSubscriptionErrorURL;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBProfileTools mTools;
	private Map<String, String> mErrorMap;
	private String fromPage;// Page Name set from JSP
	private Map<String,String> successUrlMap;
	private Map<String,String> errorUrlMap;
	/**
	 * @return the fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}

	/**
	 * @param fromPage the fromPage to set
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	/**
	 * @return the successUrlMap
	 */
	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	/**
	 * @param successUrlMap the successUrlMap to set
	 */
	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	/**
	 * @return the errorUrlMap
	 */
	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	/**
	 * @param errorUrlMap the errorUrlMap to set
	 */
	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	/**
	 * @return the errorMap
	 */
	public Map<String, String> getErrorMap() {
		return mErrorMap;
	}

	/**
	 * @param pErrorMap the errorMap to set
	 */
	public void setErrorMap(Map<String, String> pErrorMap) {
		mErrorMap = pErrorMap;
	}

	/**
	 * @return the mtools
	 */
	

	public SubscriptionManager getSubscriptionManager() {
		return mSubscriptionManager;
	}

	/**
	 * @return the tools
	 */
	public BBBProfileTools getTools() {
		return mTools;
	}

	/**
	 * @param pTools the tools to set
	 */
	public void setTools(BBBProfileTools pTools) {
		mTools = pTools;
	}

	public void setSubscriptionManager(SubscriptionManager pSubscriptionManager) {
		this.mSubscriptionManager = pSubscriptionManager;
	}

	public SubscriptionTools getSubscriptionTools() {
		return mSubscriptionTools;
	}

	public void setSubscriptionTools(SubscriptionTools mSubscriptionTools) {
		this.mSubscriptionTools = mSubscriptionTools;
	}

	public SUBSCRIPTION_TYPE getType() {
		return mType;
	}

	public void setType(SUBSCRIPTION_TYPE pType) {
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

	public void setEmailAddr(String mEmailAddr) {
		this.mEmailAddr = mEmailAddr;
	}

	public String getConfirmEmailAddr() {
		return mConfirmEmailAddr;
	}

	public void setConfirmEmailAddr(String mConfirmEmailAddr) {
		this.mConfirmEmailAddr = mConfirmEmailAddr;
	}

	public String[] getSalutation() {
		return mSalutation;
	}

	public void setSalutation(String[] pSalutation) {
		this.mSalutation = pSalutation;
	}

	public String getSelectedSalutation() {
		return mSelectedSalutation;
	}

	public void setSelectedSalutation(String pSelectedSalutation) {
		this.mSelectedSalutation = pSelectedSalutation;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String mFirstName) {
		this.mFirstName = mFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}

	public String getAddressLine1() {
		return mAddressLine1;
	}

	public void setAddressLine1(String mAddressLine1) {
		this.mAddressLine1 = mAddressLine1;
	}

	public String getAddressLine2() {
		return mAddressLine2;
	}

	public void setAddressLine2(String mAddressLine2) {
		this.mAddressLine2 = mAddressLine2;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}

	public String getState() {
		return mState;
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public String getZipcode() {
		return mZipcode;
	}

	public void setZipcode(String mZipcode) {
		this.mZipcode = mZipcode;
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
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

	public String getSubscriptionSuccessURL() {
		return mSubscriptionSuccessURL;
	}

	public void setSubscriptionSuccessURL(String mSubscriptionSuccessURL) {
		this.mSubscriptionSuccessURL = mSubscriptionSuccessURL;
	}

	public String getSubscriptionErrorURL() {
		return mSubscriptionErrorURL;
	}

	public void setSubscriptionErrorURL(String mSubscriptionErrorURL) {
		this.mSubscriptionErrorURL = mSubscriptionErrorURL;
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * @param pLblTxtTemplateManager
	 *            the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(
			LblTxtTemplateManager pLblTxtTemplateManager) {
		lblTxtTemplateManager = pLblTxtTemplateManager;
	}

	/**
	 * @return the successMessage
	 */
	public boolean isSuccessMessage() {
		return mSuccessMessage;
	}

	/**
	 * @param pSuccessMessage
	 *            the successMessage to set
	 */
	public void setSuccessMessage(boolean pSuccessMessage) {
		mSuccessMessage = pSuccessMessage;
	}

	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param pSiteContext
	 *            the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
	}

	private void validateEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		if (!BBBUtility.isValidEmail(mEmailAddr)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_email_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_email_invalid"));

		}
		if (!BBBUtility.isValidEmail(mConfirmEmailAddr)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_confirmemail_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_confirmemail_invalid"));
		}
	}

	private void emptyChkValidation(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {

		if (!BBBUtility.isValidAddressLine1(mAddressLine1)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_addressline1_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_addressline1_invalid"));
		}

		if (!BBBUtility.isEmpty(mAddressLine2)) {
			if (!BBBUtility.isValidAddressLine2(mAddressLine2)) {
				addFormException(new DropletException(
						getLblTxtTemplateManager().getErrMsg(
								"err_subscribtion_addressline2_invalid",
								pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_addressline2_invalid"));
			}
		}

		if (!BBBUtility.isValidCity(mCity)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_city_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_city_invalid"));
		}

		if (!BBBUtility.isValidZip(mZipcode)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_zip_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_zip_invalid"));
		}
		if (BBBUtility.isEmpty(mState) || (mState != null && mState.equalsIgnoreCase(BBBCoreConstants.MINUS_ONE)) ) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_state_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_state_invalid"));
		}
		
	}

	private void validateName(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {

		if (!BBBUtility.isValidFirstName(mFirstName)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_firstname_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_firstname_invalid"));

		}

		if (!BBBUtility.isValidLastName(mLastName)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_lastname_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_lastname_invalid"));

		}

	}

	/**
	 * This method performs the validation for email and direct mail
	 * subscription
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */

	public void preSubscribe(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("SubscriptionFormHandler.preSubscribe() method started");

		validateEmail(pRequest, pResponse);
		validateName(pRequest, pResponse);
		emptyChkValidation(pRequest, pResponse);

		if (mMobileOffer && BBBUtility.isEmpty(mMobileNumber)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_mobilenumberrequired",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_mobilenumberrequired"));
		}
		if (!getFormError()){
			if (!BBBUtility.isNonPOBoxAddress(mAddressLine1, mAddressLine2)) {
				addFormException(new DropletException((getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.ERROR_POBOX_ADDRESS, pRequest.getLocale().getLanguage(), null)),BBBCheckoutConstants.ERROR_POBOX_ADDRESS));
			}
		}
	}

	public boolean handleSubscribe(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("SubscriptionFormHandler.handleSubscribe() method started");
			final String siteId = mSiteContext.getSite().getId();
			if (StringUtils.isNotEmpty(getFromPage())) {
				setSubscriptionSuccessURL(pRequest.getContextPath()
						+ getSuccessUrlMap().get(getFromPage()));
			setSubscriptionErrorURL(pRequest.getContextPath()
						+ getErrorUrlMap().get(getFromPage()));

			}
		preSubscribe(pRequest, pResponse);
		if (!getFormError()) {
			SubscriptionVO subscriptionVO = new SubscriptionVO();
			subscriptionVO.setType(mType);
			subscriptionVO.setEmailAddr(mEmailAddr);
			subscriptionVO.setConfirmEmailAddr(mConfirmEmailAddr);
			subscriptionVO.setSalutation(mSelectedSalutation);
			subscriptionVO.setFirstName(mFirstName);
			subscriptionVO.setLastName(mLastName);
			subscriptionVO.setAddressLine1(mAddressLine1);
			subscriptionVO.setAddressLine2(mAddressLine2);
			subscriptionVO.setCity(mCity);
			subscriptionVO.setZipcode(mZipcode);
			subscriptionVO.setPhoneNumber(mPhoneNumber);
			subscriptionVO.setMobileNumber(mMobileNumber);
			subscriptionVO.setEmailOffer(mEmailOffer);
			subscriptionVO.setMobileOffer(mMobileOffer);
			subscriptionVO.setDirectMailOffer(mDirectMailOffer);
			subscriptionVO.setSiteId(siteId);
			subscriptionVO.setState(mState);
			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				mSubscriptionManager.requestInfoTIBCO(subscriptionVO);
				setSuccessMessage(true);
			} catch (BBBSystemException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
		}
			logDebug("SubscriptionFormHandler.handleSubscribe() method ends");
		if (!getFormError()) {
			pRequest.getSession().setAttribute(BBBCoreConstants.SUBSCRIBE,
					BBBCoreConstants.YES);
		}
		return checkFormRedirect(getSubscriptionSuccessURL(),
				getSubscriptionErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method performs the validation for email unsubscription
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */
	public void preUnSubscribeEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("SubscriptionFormHandler.preUnSubscribeEmail() method started");
		if (!BBBUtility.isValidEmail(mEmailAddr)) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_email_invalid",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_email_invalid"));

		}
		if (mFrequency == null || BBBUtility.isEmpty(mFrequency.toString())) {
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_subscribtion_frequency_empty",
							pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_frequency_empty"));

		}
			logDebug("SubscriptionFormHandler.preUnSubscribeEmail() method ends");
	}

	public boolean handleUnSubscribeEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("SubscriptionFormHandler.handleUnSubscribeEmail() method started");
			final String siteId = mSiteContext.getSite().getId();
			if (StringUtils.isNotEmpty(getFromPage())) {
				setSubscriptionSuccessURL(pRequest.getContextPath()
						+ getSuccessUrlMap().get(getFromPage()));
			setSubscriptionErrorURL(pRequest.getContextPath()
						+ getErrorUrlMap().get(getFromPage()));

			}
		preUnSubscribeEmail(pRequest, pResponse);
		if (!getFormError()) {
			SubscriptionVO subscriptionVO = new SubscriptionVO();
			subscriptionVO.setType(mType);
			subscriptionVO.setFrequency(getFrequency());
			subscriptionVO.setEmailAddr(mEmailAddr);
			subscriptionVO.setSiteId(siteId);

			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				mSubscriptionManager.requestInfoTIBCO(subscriptionVO);
				setSuccessMessage(true);
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			} catch (BBBSystemException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
		}

			logDebug("SubscriptionFormHandler.handleUnSubscribeEmail() method ends");
		return checkFormRedirect(getSubscriptionSuccessURL(),
				getSubscriptionErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method performs the validation for direct mail unsubscription
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */
	public void preUnSubscribeDirectMail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("SubscriptionFormHandler.preUnSubscribeDirectMail() method started");
		if(!BBBUtility.isEmpty(mEmailAddr)){
			if (!BBBUtility.isValidEmail(mEmailAddr)) {
				addFormException(new DropletException(getLblTxtTemplateManager()
						.getErrMsg("err_subscribtion_email_invalid",
								pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_email_invalid"));
	
			} else if(!mTools.isDuplicateEmailAddress(mEmailAddr, getSiteContext()
					.getSite().getId())){
				mErrorMap = new HashMap<String,String>();
				mErrorMap.put(BBBCoreConstants.EMAIL_ERROR, getLblTxtTemplateManager()
						.getErrMsg("err_subscribtion_email_invalid",
								pRequest.getLocale().getLanguage(), null, null));
				addFormException(new DropletException(getLblTxtTemplateManager()
						.getErrMsg("err_subscribtion_email_invalid",
								pRequest.getLocale().getLanguage(), null, null),"err_subscribtion_email_invalid"));
				
			} 
		}
		validateName(pRequest, pResponse);
		emptyChkValidation(pRequest, pResponse);
		if (!getFormError()){
			if (!BBBUtility.isNonPOBoxAddress(mAddressLine1, mAddressLine2)) {
				addFormException(new DropletException((getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.ERROR_POBOX_ADDRESS, pRequest.getLocale().getLanguage(), null)),BBBCheckoutConstants.ERROR_POBOX_ADDRESS));
			}
		}
			logDebug("SubscriptionFormHandler.preUnSubscribeDirectMail() method ends");

	}

	public boolean handleUnSubscribeDirectMail(
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("SubscriptionFormHandler.handleUnSubscribeDirectMail() method started");
			final String siteId = mSiteContext.getSite().getId();
			if (StringUtils.isNotEmpty(getFromPage())) {
				setSubscriptionSuccessURL(pRequest.getContextPath()
						+ getSuccessUrlMap().get(getFromPage()));
			setSubscriptionErrorURL(pRequest.getContextPath()
						+ getErrorUrlMap().get(getFromPage()));

			}
		preUnSubscribeDirectMail(pRequest, pResponse);
		
		if (!getFormError()) {
			SubscriptionVO subscriptionVO = new SubscriptionVO();
			subscriptionVO.setType(mType);
			subscriptionVO.setEmailAddr(mEmailAddr);
			subscriptionVO.setFirstName(mFirstName);
			subscriptionVO.setLastName(mLastName);
			subscriptionVO.setMobileNumber(mMobileNumber);
			subscriptionVO.setAddressLine1(mAddressLine1);
			subscriptionVO.setAddressLine2(mAddressLine2);
			subscriptionVO.setCity(mCity);
			subscriptionVO.setState(mState);
			subscriptionVO.setZipcode(mZipcode);
			subscriptionVO.setSiteId(siteId);

			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				mSubscriptionManager.requestInfoTIBCO(subscriptionVO);
				setSuccessMessage(true);
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			} catch (BBBSystemException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
			
		}

			logDebug("SubscriptionFormHandler.handleUnSubscribeDirectMail() method ends");
		return checkFormRedirect(getSubscriptionSuccessURL(),
				getSubscriptionErrorURL(), pRequest, pResponse);
	}

	public boolean handleRequestInfo(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

			logDebug("SubscriptionFormHandler.handleRequestInfo() method started");
		final String siteId = mSiteContext.getSite().getId();
		if (!getFormError()) {
			SubscriptionVO subscriptionVO = new SubscriptionVO();
			subscriptionVO.setType(mType);
			subscriptionVO.setFrequency(mFrequency);
			subscriptionVO.setEmailAddr(mEmailAddr);
			subscriptionVO.setConfirmEmailAddr(mConfirmEmailAddr);
			subscriptionVO.setSalutation(mSelectedSalutation);
			subscriptionVO.setFirstName(mFirstName);
			subscriptionVO.setLastName(mLastName);
			subscriptionVO.setAddressLine1(mAddressLine1);
			subscriptionVO.setAddressLine2(mAddressLine2);
			subscriptionVO.setCity(mCity);
			subscriptionVO.setState(mState);
			subscriptionVO.setZipcode(mZipcode);
			subscriptionVO.setPhoneNumber(mPhoneNumber);
			subscriptionVO.setMobileNumber(mMobileNumber);
			subscriptionVO.setEmailOffer(mEmailOffer);
			subscriptionVO.setMobileOffer(mMobileOffer);
			subscriptionVO.setDirectMailOffer(mDirectMailOffer);
			subscriptionVO.setSiteId(siteId);

			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				mSubscriptionManager.requestInfoTIBCO(subscriptionVO);
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			} catch (BBBSystemException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
			
		}

			logDebug("SubscriptionFormHandler.handleRequestInfo() method ends");

		return checkFormRedirect(getSubscriptionSuccessURL(),
				getSubscriptionErrorURL(), pRequest, pResponse);
	}

}
