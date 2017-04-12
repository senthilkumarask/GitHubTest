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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import nl.captcha.Captcha;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.manager.SurveyManager;
import com.bbb.selfservice.tibco.vo.SurveyVO;
import com.bbb.utils.BBBUtility;

public class SurveyFormHandler extends BBBGenericFormHandler {

	private SurveyManager mSurveyManager;
	private SiteContext mSiteContext;
	private String[] mAge;
	private String[] mGender;

	private String mEverShopped;
	private String mFeaturesMesssage;
	private String mWebSiteMessage;
	private String mOtherMessage;
	private String mEmailRequired;
	private String mLocation;
	private String mSelectedAge;
	private String mSelectedGender;
	private String mUserName;
	private String mEmail;

	private boolean mSuccessMessage = false;

	private String mSurveySuccessURL;
	private String mSurveyErrorURL;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private String captchaAnswer;
	private boolean validateCaptcha;
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
	 * @return the surveyManager
	 */
	public SurveyManager getSurveyManager() {
		return mSurveyManager;
	}

	/**
	 * @return the age
	 */
	public String[] getAge() {
		return mAge;
	}

	/**
	 * @return the gender
	 */
	public String[] getGender() {
		return mGender;
	}

	/**
	 * @return the everShopped
	 */
	public String getEverShopped() {
		return mEverShopped;
	}

	/**
	 * @return the featuresMesssage
	 */
	public String getFeaturesMesssage() {
		return mFeaturesMesssage;
	}

	/**
	 * @return the webSiteMessage
	 */
	public String getWebSiteMessage() {
		return mWebSiteMessage;
	}

	/**
	 * @return the otherMessage
	 */
	public String getOtherMessage() {
		return mOtherMessage;
	}

	/**
	 * @return the emailRequired
	 */
	public String getEmailRequired() {
		return mEmailRequired;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return mLocation;
	}

	/**
	 * @return the selectedAge
	 */
	public String getSelectedAge() {
		return mSelectedAge;
	}

	/**
	 * @return the selectedGender
	 */
	public String getSelectedGender() {
		return mSelectedGender;
	}

	/**
	 * @return the name
	 */

	public String getUserName() {
		return mUserName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return mEmail;
	}

	/**
	 * @return the successMessage
	 */
	public boolean isSuccessMessage() {
		return mSuccessMessage;
	}

	/**
	 * @return the surveySuccessURL
	 */
	public String getSurveySuccessURL() {
		return mSurveySuccessURL;
	}

	/**
	 * @return the surveyErrorURL
	 */
	public String getSurveyErrorURL() {
		return mSurveyErrorURL;
	}

	/**
	 * @param pSurveyManager
	 *            the surveyManager to set
	 */
	public void setSurveyManager(SurveyManager pSurveyManager) {
		mSurveyManager = pSurveyManager;
	}

	/**
	 * @param pAge
	 *            the age to set
	 */
	public void setAge(String[] pAge) {
		mAge = pAge;
	}

	/**
	 * @param pGender
	 *            the gender to set
	 */
	public void setGender(String[] pGender) {
		mGender = pGender;
	}

	/**
	 * @param pEverShopped
	 *            the everShopped to set
	 */
	public void setEverShopped(String pEverShopped) {
		mEverShopped = pEverShopped;
	}

	/**
	 * @param pFeaturesMesssage
	 *            the featuresMesssage to set
	 */
	public void setFeaturesMesssage(String pFeaturesMesssage) {
		mFeaturesMesssage = pFeaturesMesssage;
	}

	/**
	 * @param pWebSiteMessage
	 *            the webSiteMessage to set
	 */
	public void setWebSiteMessage(String pWebSiteMessage) {
		mWebSiteMessage = pWebSiteMessage;
	}

	/**
	 * @param pOtherMessage
	 *            the otherMessage to set
	 */
	public void setOtherMessage(String pOtherMessage) {
		mOtherMessage = pOtherMessage;
	}

	/**
	 * @param pEmailRequired
	 *            the emailRequired to set
	 */
	public void setEmailRequired(String pEmailRequired) {
		mEmailRequired = pEmailRequired;
	}

	/**
	 * @param pLocation
	 *            the location to set
	 */
	public void setLocation(String pLocation) {
		mLocation = pLocation;
	}

	/**
	 * @param pSelectedAge
	 *            the selectedAge to set
	 */
	public void setSelectedAge(String pSelectedAge) {
		mSelectedAge = pSelectedAge;
	}

	/**
	 * @param pSelectedGender
	 *            the selectedGender to set
	 */
	public void setSelectedGender(String pSelectedGender) {
		mSelectedGender = pSelectedGender;
	}

	/**
	 * @param pName
	 *            the name to set
	 */
	public void setUserName(String pUserName) {
		mUserName = pUserName;
	}

	/**
	 * @param pEmail
	 *            the email to set
	 */
	public void setEmail(String pEmail) {
		mEmail = pEmail;
	}

	/**
	 * @param pSuccessMessage
	 *            the successMessage to set
	 */
	public void setSuccessMessage(boolean pSuccessMessage) {
		mSuccessMessage = pSuccessMessage;
	}

	/**
	 * @param pSurveySuccessURL
	 *            the surveySuccessURL to set
	 */
	public void setSurveySuccessURL(String pSurveySuccessURL) {
		mSurveySuccessURL = pSurveySuccessURL;
	}

	/**
	 * @param pSurveyErrorURL
	 *            the surveyErrorURL to set
	 */
	public void setSurveyErrorURL(String pSurveyErrorURL) {
		mSurveyErrorURL = pSurveyErrorURL;
	}
	
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param mSiteContext
	 *            the mSiteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}
	
	/**
	 * @return the captchaAnswer
	 */
	public String getCaptchaAnswer() {
		return captchaAnswer;
	}

	/**
	 * @param captchaAnswer the captchaAnswer to set
	 */
	public void setCaptchaAnswer(final String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}

	/**
	 * @return the validateCaptcha
	 */
	public boolean isValidateCaptcha() {
		return validateCaptcha;
	}

	/**
	 * @param validateCaptcha the validateCaptcha to set
	 */
	public void setValidateCaptcha(boolean validateCaptcha) {
		this.validateCaptcha = validateCaptcha;
	}

	/**
	 * This method is customized to validate the different form fields to match
	 * the business rules
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 *            Hash Map to store error message key
	 * @return void
	 */
	private void validateRequestInfo(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {

		String errorMessage = "";

			logDebug("SurveyFormHandler.validateRequestInfo() method started");

		if (BBBCoreConstants.YES.equalsIgnoreCase(mEmailRequired)
				&& !BBBUtility.isValidEmail(mEmail)) {

			errorPlaceHolderMap.put("fieldName", "Email Address");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					"err_profile_invalid", pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ACCOUNT_ERROR_1318));
		}

		if (!BBBUtility.isValidName(mUserName)) {
			errorPlaceHolderMap.put("fieldName", "Name");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					"err_profile_invalid", pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ACCOUNT_ERROR_1318));
		}

		if (!BBBUtility.isEmpty(mFeaturesMesssage)
				&& !BBBUtility.isStringLengthValid(mFeaturesMesssage,
						BBBCoreConstants.ONE, BBBCoreConstants.ONE_THOUSAND)) {
			errorPlaceHolderMap.put("fieldName", "Features Messages");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					"err_profile_invalid", pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ACCOUNT_ERROR_1318));
		}

		if (!BBBUtility.isEmpty(mWebSiteMessage)
				&& !BBBUtility.isStringLengthValid(mWebSiteMessage,
						BBBCoreConstants.ONE, BBBCoreConstants.ONE_THOUSAND)) {
			errorPlaceHolderMap.put("fieldName", "Web Site Messages");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					"err_profile_invalid", pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ACCOUNT_ERROR_1318));
		}

		if (!BBBUtility.isEmpty(mOtherMessage)
				&& !BBBUtility.isStringLengthValid(mOtherMessage,
						BBBCoreConstants.ONE, BBBCoreConstants.FIFTEEN_HUNDRED)) {
			errorPlaceHolderMap.put("fieldName", "Other Messages");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					"err_profile_invalid", pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ACCOUNT_ERROR_1318));
		}

			logDebug("SurveyFormHandler.validateRequestInfo() method ends");

	}

	/**
	 * This method is performed the following task
	 * 
	 * 1. validate the input information provided by user according business
	 * rules by calling validateRequestInfo 2. sends email to registered user 2.
	 * calls TIBCO postCreateUser for ProfileFormHandler class 3. Associates
	 * siteId to the registered user
	 * 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */

	public boolean handleRequestInfo(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response) throws ServletException,
			IOException {

			logDebug("SurveyFormHandler.handleRequestInfo() method started");
			String siteId = getSiteContext().getSite().getId();
			if (StringUtils.isNotEmpty(getFromPage())) {
				setSurveySuccessURL(request.getContextPath()
						+ getSuccessUrlMap().get(getFromPage()));
			setSurveyErrorURL(request.getContextPath()
						+ getErrorUrlMap().get(getFromPage()));

			}

		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();

		validateRequestInfo(request, response, errorPlaceHolderMap);
		validatingCaptcha(request, response);

		if (!getFormError()) {

			SurveyVO surveyVO = new SurveyVO();
			
			Calendar date = Calendar.getInstance();
			SimpleDateFormat dateformatter = new SimpleDateFormat(
					BBBCoreConstants.DATE_FORMAT1);
			String sumitDate = dateformatter.format(date.getTime());
			surveyVO.setSubmitDate(sumitDate);
			surveyVO.setSendEmail(mEmailRequired != null ? mEmailRequired : "");
			surveyVO.setEmailAddress(mEmail != null ? mEmail : "");
			surveyVO.setShoppedAtBefore(mEverShopped != null ? mEverShopped
					: "");
			surveyVO.setFeatures(mFeaturesMesssage != null ? mFeaturesMesssage
					: "");
			surveyVO.setFavorite(mWebSiteMessage != null ? mWebSiteMessage : "");
			surveyVO.setComments(mOtherMessage != null ? mOtherMessage : "");
			surveyVO.setUserName(mUserName != null ? mUserName : "");
			surveyVO.setAge(mSelectedAge != null ? mSelectedAge : "");
			surveyVO.setGender(mSelectedGender != null ? mSelectedGender : "");
			surveyVO.setLocation(mLocation != null ? mLocation : "");
			surveyVO.setSiteFlag(siteId);

			try {

				/*
				 * Call TIBCO Service
				 */
				mSurveyManager.requestInfoTIBCO(surveyVO);
				setSuccessMessage(true);

			} catch (BBBBusinessException ex) {
			
				logError(LogMessageFormatter.formatMessage(request, "err_contactus_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1122),ex);
			
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_contactus_tibco_exception",
						request.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_contactus_tibco_exception"));
			}

		}

			logDebug("SurveyFormHandler.handleRequestInfo() method ends");

		return checkFormRedirect(getSurveySuccessURL(), getSurveyErrorURL(),
				request, response);
	}
	
	/**
	 * This Method validates the value of the captcha entered by the user and sends back the appropriate response.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private void validatingCaptcha(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		boolean success = true;
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
		Captcha captcha = sessionBean.getCaptcha();
		pRequest.setCharacterEncoding("UTF-8");
		logDebug("SessionId = " + pRequest.getSession().getId());
		if(null!= captcha){
			logDebug("Captcha return from Session = " + captcha.toString());
		}
		logDebug("Captcha entered by User = " + getCaptchaAnswer());
		if (isValidateCaptcha() && (null==captcha || !captcha.isCorrect(getCaptchaAnswer()))) {
			success = false;
		}
		logDebug("Is Valid Captcha value = " + success);
		if(!success){
			String errorMessage = getLblTxtTemplateManager().getErrMsg("err_captcha_invalid", pRequest.getLocale().getLanguage(), null, null);
			addFormException(new DropletException(errorMessage,"err_captcha_invalid"));
		}
	}

}
