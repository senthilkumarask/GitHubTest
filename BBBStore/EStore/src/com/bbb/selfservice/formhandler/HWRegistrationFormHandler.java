/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Prashanth K Bhoomula	
 *
 * Created on: 05-July-2012
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.formhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.HWRegistrationManager;
import com.bbb.selfservice.tibco.vo.HWRegistrationVO;
import com.bbb.utils.BBBUtility;

public class HWRegistrationFormHandler extends BBBGenericFormHandler {

	private SiteContext mSiteContext;
	private boolean mSuccessMessage = false;
	private boolean mContextAdded = false;
	private HWRegistrationVO hwRegistrationVO;
	private String mTemplateUrl;	
	private LblTxtTemplateManager lblTxtTemplateManager;
	private HWRegistrationManager hwRegistrationManager;
	private String mHWSuccessURL;
	private String mHWErrorURL;

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
	public void setLblTxtTemplateManager(LblTxtTemplateManager pLblTxtTemplateManager) {
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
	
	public HWRegistrationVO getHwRegistrationVO() {
		if (hwRegistrationVO == null) {
			hwRegistrationVO = new HWRegistrationVO();
		}
		return hwRegistrationVO;	
	}

	public void setHwRegistrationVO(HWRegistrationVO hwRegistrationVO) {
		this.hwRegistrationVO = hwRegistrationVO;
	}

	
	public HWRegistrationManager getHwRegistrationManager() {
		return hwRegistrationManager;
	}

	public void setHwRegistrationManager(HWRegistrationManager hwRegistrationManager) {
		this.hwRegistrationManager = hwRegistrationManager;
	}

	
	
	public String getHWSuccessURL() {
		return mHWSuccessURL;
	}

	public void setHWSuccessURL(String mHWSuccessURL) {
		this.mHWSuccessURL = mHWSuccessURL;
	}

	public String getHWErrorURL() {
		return mHWErrorURL;
	}

	public void setHWErrorURL(String mHWErrorURL) {
		this.mHWErrorURL = mHWErrorURL;
	}
	
	/**
	 * @return the templateUrl
	 */
	public String getTemplateUrl() {
		return mTemplateUrl;
	}

	/**
	 * @param pTemplateUrl
	 *            the templateUrl to set
	 */
	public void setTemplateUrl(String pTemplateUrl) {
		mTemplateUrl = pTemplateUrl;
	}
	
	/**
	 * Add Context path to the Property bean
	 * @param pContextPath
	 */
	private void addContextPath(String pContextPath) {
		if (pContextPath != null && !isContextAdded()) {
			setTemplateUrl(pContextPath + getTemplateUrl());
			setContextAdded(true);
		}
	}

	/**
	 * @return the mContextAdded
	 */
	public boolean isContextAdded() {
		return mContextAdded;
	}

	/**
	 * @param mContextAdded the mContextAdded to set
	 */
	public void setContextAdded(boolean pContextAdded) {
		this.mContextAdded = pContextAdded;
	}

	/**
	 * Validate HealthyWoman Request form - Email Address validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void validateEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {

		logDebug("HWRegistrationFormHandler.validateEmail() method started");
		if (BBBUtility.isEmpty(getHwRegistrationVO().getEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_js_required_common_name",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_js_required_common_name"));
		}		
		if (!BBBUtility.isValidEmail(getHwRegistrationVO().getEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid email from validateEmail of HWRegistrationFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1019));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_email_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_email_invalid"));
		}
		logDebug("HWRegistrationFormHandler.validateEmail() method ends");
	}

	/**
	 * Validate Healthy Woman Registration form - Empty field check for
	 * Address/City/State
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void validateFields(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {
		logDebug("HWRegistrationFormHandler.emptyChkValidation() method started");
		if(!BBBUtility.isEmpty(getHwRegistrationVO().getAddressLine1()) && !BBBUtility.isValidAddressLine1(getHwRegistrationVO().getAddressLine1())) {
				errorPlaceHolderMap.put("fieldName", "Address Line1");
				logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid address from validateFields of HWRegistrationFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1017));
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_addr1_invalid",
						pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_addr1_invalid"));
		}
		if (!BBBUtility.isEmpty(getHwRegistrationVO().getAddressLine2())&& !BBBUtility.isValidAddressLine2(getHwRegistrationVO().getAddressLine2())) {
				errorPlaceHolderMap.put("fieldName", "Address Line2");
				logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid address 2 from validateFields of HWRegistrationFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1016));
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_addr2_invalid",
						pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_addr2_invalid"));
		}
		if (!BBBUtility.isEmpty(getHwRegistrationVO().getCity()) && !BBBUtility.isValidCity(getHwRegistrationVO().getCity())) {
			errorPlaceHolderMap.put("fieldName", "City");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid city from validateFields of HWRegistrationFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1018));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_city_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_city_invalid"));
		}
	
		if (!BBBUtility.isEmpty(getHwRegistrationVO().getState()) && getHwRegistrationVO().getState().equals("-1")) {
			errorPlaceHolderMap.put("fieldName", "State");
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_state_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_state_invalid"));
		}
		if (!BBBUtility.isEmpty(getHwRegistrationVO().getZipcode()) && !BBBUtility.isValidZip(getHwRegistrationVO().getZipcode())) {
			errorPlaceHolderMap.put("fieldName", "Zip");
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_zip_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_zip_invalid"));
		}
		logDebug("HWRegistrationFormHandler.emptyChkValidation() method ends");

	}

	/**
	 * This method performs the validation for Healthy Woman Registration form
	 * 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */

	public void preRequestValidation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		logDebug("HWRegistrationFormHandler.preRequestValidation() method started");
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateEmail(pRequest, pResponse, errorPlaceHolderMap);
		validateFields(pRequest, pResponse, errorPlaceHolderMap);
		logDebug("HWRegistrationFormHandler.preRequestValidation() method ends");
	}
	public boolean handleRequestHealthyWomanRegistration(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		return handleHWRegistrationRequest(pRequest, pResponse);
	}
	/**
	 * Handle request for Healthy Woman Registration form
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleHWRegistrationRequest(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		logDebug("HWRegistrationFormHandler.handleHWRegistrationRequest() method started");
		addContextPath(pRequest.getContextPath());
		preRequestValidation(pRequest, pResponse);
		final String siteId = mSiteContext.getSite().getId();
		if (!getFormError()) {
			// getHwRegistrationVO().setType(mType);
			getHwRegistrationVO().setSiteId(siteId);

			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				getHwRegistrationManager().requestHealthyWomanTIBCO(getHwRegistrationVO());
				setSuccessMessage(true);
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg("err_subscription_tibco_exception", pRequest.getLocale().getLanguage(),
						null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
		}
		logDebug("HWRegistrationFormHandler.handleHWRegistrationRequest() method ends");
		return checkFormRedirect(getHWSuccessURL(), getHWErrorURL(), pRequest, pResponse);
	}
}
