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

import org.apache.commons.lang.StringEscapeUtils;

import nl.captcha.Captcha;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.manager.ContactUsManager;
import com.bbb.selfservice.tibco.vo.ContactUsVO;
import com.bbb.selfservice.tools.ContactUsTools;
import com.bbb.utils.BBBUtility;

public class ContactUsFormHandler extends BBBGenericFormHandler {

	private ContactUsManager mContactUsManager;
	private ContactUsTools mTools;
	private SiteContext mSiteContext;
	private String[] mTimeCall;

	private String mConfirmEmail;
	private String mEmailMessage;
	private String mOrderNumber;
	private String mContactType;
	private String mAmPM;
	private String mSubjectCategory;
	private String mSelectedTimeZone;
	private String mSelectedTimeCall;
	private String mFirstName;
	private String mLastName;
	private String mGender;
	private String mEmail;
	private String mPhoneNumber;
	private String mPhoneExt;
	private boolean mSuccessMessage = false;
	private String mContactUsSuccessURL;
	private String mContactUsErrorURL;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private TemplateEmailInfo mRegistrationTemplateEmailInfo;
	private String captchaAnswer;
	private boolean validateCaptcha;
	private boolean captchaError;
	
	/**
	 * @return the captchaError
	 */
	public boolean isCaptchaError() {
		return captchaError;
	}

	/**
	 * @param captchaError the captchaError to set
	 */
	public void setCaptchaError(boolean captchaError) {
		this.captchaError = captchaError;
	}

	/**
	 * @return the tools
	 */
	public ContactUsTools getTools() {
		return mTools;
	}

	/**
	 * @param pTools
	 *            the tools to set
	 */
	public void setTools(ContactUsTools pTools) {
		mTools = pTools;
	}

	/**
	 * @return the registrationTemplateEmailInfo
	 */
	public TemplateEmailInfo getRegistrationTemplateEmailInfo() {
		return mRegistrationTemplateEmailInfo;
	}

	/**
	 * @param pRegistrationTemplateEmailInfo
	 *            the registrationTemplateEmailInfo to set
	 */
	public void setRegistrationTemplateEmailInfo(
			TemplateEmailInfo pRegistrationTemplateEmailInfo) {
		mRegistrationTemplateEmailInfo = pRegistrationTemplateEmailInfo;
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
	 * @return the phoneExt
	 */
	public String getPhoneExt() {
		return mPhoneExt;
	}

	/**
	 * @param pPhoneExt
	 *            the phoneExt to set
	 */
	public void setPhoneExt(String pPhoneExt) {
		mPhoneExt = pPhoneExt;
	}

	/**
	 * @return the contactUsSuccessURL
	 */
	public String getContactUsSuccessURL() {
		return mContactUsSuccessURL;
	}

	/**
	 * @return the contactUsErrorURL
	 */
	public String getContactUsErrorURL() {
		return mContactUsErrorURL;
	}

	/**
	 * @param pContactUsSuccessURL
	 *            the contactUsSuccessURL to set
	 */
	public void setContactUsSuccessURL(String pContactUsSuccessURL) {
		mContactUsSuccessURL = pContactUsSuccessURL;
	}

	/**
	 * @param pContactUsErrorURL
	 *            the contactUsErrorURL to set
	 */
	public void setContactUsErrorURL(String pContactUsErrorURL) {
		mContactUsErrorURL = pContactUsErrorURL;
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
	 * @param pFirstName
	 *            the firstName to set
	 */
	public void setFirstName(String pFirstName) {
		mFirstName = pFirstName;
	}

	/**
	 * @param pLastName
	 *            the lastName to set
	 */
	public void setLastName(String pLastName) {
		mLastName = pLastName;
	}

	/**
	 * @param pGender
	 *            the gender to set
	 */
	public void setGender(String pGender) {
		mGender = pGender;
	}

	/**
	 * @param pEmail
	 *            the email to set
	 */
	public void setEmail(String pEmail) {
		mEmail = pEmail;
	}

	/**
	 * @param pPhoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String pPhoneNumber) {
		mPhoneNumber = pPhoneNumber;
	}

	/**
	 * @return the contactUsManager
	 */
	public ContactUsManager getContactUsManager() {
		return mContactUsManager;
	}

	/**
	 * @return the timeCall
	 */
	public String[] getTimeCall() {
		return mTimeCall;
	}

	/**
	 * @return the confirmEmail
	 */
	public String getConfirmEmail() {
		return mConfirmEmail;
	}

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
	 * @return the amPM
	 */
	public String getAmPM() {
		return mAmPM;
	}

	/**
	 * @return the subjectCategory
	 */
	public String getSubjectCategory() {
		return mSubjectCategory;
	}

	/**
	 * @param pContactUsManager
	 *            the contactUsManager to set
	 */
	public void setContactUsManager(ContactUsManager pContactUsManager) {
		mContactUsManager = pContactUsManager;
	}

	/**
	 * @param pTimeCall
	 *            the timeCall to set
	 */
	public void setTimeCall(String[] pTimeCall) {
		mTimeCall = pTimeCall;
	}

	/**
	 * @param pConfirmEmail
	 *            the confirmEmail to set
	 */
	public void setConfirmEmail(String pConfirmEmail) {
		mConfirmEmail = pConfirmEmail;
	}

	/**
	 * @param pEmailMessage
	 *            the emailMessage to set
	 */
	public void setEmailMessage(String pEmailMessage) {
		mEmailMessage = pEmailMessage;
	}

	/**
	 * @param pOrderNumber
	 *            the orderNumber to set
	 */
	public void setOrderNumber(String pOrderNumber) {
		mOrderNumber = pOrderNumber;
	}

	/**
	 * @param pContactType
	 *            the contactType to set
	 */
	public void setContactType(String pContactType) {
		mContactType = pContactType;
	}

	/**
	 * @param pAmPM
	 *            the amPM to set
	 */
	public void setAmPM(String pAmPM) {
		mAmPM = pAmPM;
	}

	/**
	 * @param pSubjectCategory
	 *            the subjectCategory to set
	 */
	public void setSubjectCategory(String pSubjectCategory) {
		mSubjectCategory = pSubjectCategory;
	}

	/**
	 * @return the selectedTimeZone
	 */
	public String getSelectedTimeZone() {
		return mSelectedTimeZone;
	}

	/**
	 * @return the selectedTimeCall
	 */
	public String getSelectedTimeCall() {
		return mSelectedTimeCall;
	}

	/**
	 * @param pSelectedTimeZone
	 *            the selectedTimeZone to set
	 */
	public void setSelectedTimeZone(String pSelectedTimeZone) {
		mSelectedTimeZone = pSelectedTimeZone;
	}

	/**
	 * @param pSelectedTimeCall
	 *            the selectedTimeCall to set
	 */
	public void setSelectedTimeCall(String pSelectedTimeCall) {
		mSelectedTimeCall = pSelectedTimeCall;
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
	 * 
	 */
	private void validateRequestInfo(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {
		String errorMessage;

			logDebug("ContactUsFormHandler.validateRequestInfo() method started");

		if (BBBCoreConstants.BLANK.equals(mSubjectCategory)) {

			errorPlaceHolderMap.put("fieldName", "Subject");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_SUBJECT_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_SUBJECT_INVALID));
		}

		if (!BBBUtility.isEmpty(mOrderNumber)
				&& !BBBUtility.isValidOrderNumber(mOrderNumber)) {
			errorPlaceHolderMap.put("fieldName", "Order Number");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_FIRSTNAME_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_FIRSTNAME_INVALID));
		}

		if (!BBBUtility.isStringLengthValid(mEmailMessage,
				BBBCoreConstants.ONE, BBBCoreConstants.FIFTEEN_HUNDRED)) {

			errorPlaceHolderMap.put("fieldName", "Email Message");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_EMAIL_LENGTH_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_EMAIL_LENGTH_INVALID));
		}
		if (!BBBUtility.isValidFirstName(mFirstName)) {
			errorPlaceHolderMap.put("fieldName", "First Name");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_FIRSTNAME_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_FIRSTNAME_INVALID));
		}
		if (!BBBUtility.isValidLastName(mLastName)) {
			errorPlaceHolderMap.put("fieldName", "Last Name");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_LASTNAME_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_LASTNAME_INVALID));
		}
		if (!(BBBCoreConstants.PHONE.equals(mContactType) || BBBCoreConstants.EMAIL
				.equals(mContactType))) {

			errorPlaceHolderMap.put("fieldName", "Contact Type");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_CONTACT_TYPE_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_CONTACT_TYPE_INVALID));
		}

		if (BBBCoreConstants.EMAIL
				.equals(mContactType) && !BBBUtility.isValidEmail(mEmail)) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_EMAIL_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_EMAIL_INVALID));
		}

		if (BBBCoreConstants.EMAIL
				.equals(mContactType) && !BBBUtility.isValidEmail(mConfirmEmail)) {
			errorPlaceHolderMap.put("fieldName", "Confirm Email Address");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_CONFIRM_EMAIL_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_CONFIRM_EMAIL_INVALID));
		}
		if (!BBBUtility.isEmpty(mPhoneNumber)
				&& !BBBUtility.isValidPhoneNumber(mPhoneNumber)) {

			errorPlaceHolderMap.put("fieldName", "Phone Number");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_PHONENUMBER_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_PHONENUMBER_INVALID));
		}

		if (!BBBUtility.isEmpty(mPhoneExt)
				&& !BBBUtility.isValidPhoneExt(mPhoneExt)) {

			errorPlaceHolderMap.put("fieldName", "Phone Ext.");
			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_PHONE_EXT_INVALID, pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_PHONE_EXT_INVALID));
		}

		if (!BBBUtility.isEmpty(mEmail) && !BBBUtility.isEmpty(mConfirmEmail)
				&& !BBBUtility.isSameEmail(mEmail, mConfirmEmail)) {

			errorMessage = getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERROR_CONTACTUS_EMAIL_CONFIRMEMAIL_NOT_SAME,
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap,
					null);
			addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_EMAIL_CONFIRMEMAIL_NOT_SAME));

		}
		
		if (BBBCoreConstants.PHONE.equals(mContactType))
		{
			if (BBBCoreConstants.BLANK.equals(mPhoneNumber)) {
				errorPlaceHolderMap.put("fieldName", "Phone Number");
				errorMessage = getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_CONTACTUS_PHONENUMBER_INVALID, pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);
				addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_PHONENUMBER_INVALID));
			}
			if (BBBCoreConstants.BLANK.equals(mSelectedTimeZone)) {
				errorPlaceHolderMap.put("fieldName", "Time Zone");
				errorMessage = getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_CONTACTUS_TIMEZONE_INVALID, pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);
				addFormException(new DropletException(errorMessage,BBBCoreErrorConstants.ERROR_CONTACTUS_TIMEZONE_INVALID));
			}
			
		}

			logDebug("ContactUsFormHandler.validateRequestInfo() method ends");
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

	public boolean handleRequestInfo(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

			logDebug("ContactUsFormHandler.handleRequestInfo() method started");
		getRegistrationTemplateEmailInfo().setTemplateURL(pRequest.getContextPath() + getRegistrationTemplateEmailInfo().getTemplateURL());	
		final String siteId = mSiteContext.getSite().getId();
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateRequestInfo(pRequest, pResponse, errorPlaceHolderMap);
		//validatingCaptcha(pRequest, pResponse);
		if (!getFormError()) {
			ContactUsVO contactUsVO = new ContactUsVO();
			contactUsVO.setSubjectCategory(mSubjectCategory);
			if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel())){
				String emailMessageEscapeXML = StringEscapeUtils.escapeXml(mEmailMessage);
				contactUsVO.setEmailMessage(emailMessageEscapeXML);
			}
			else{
				contactUsVO.setEmailMessage(mEmailMessage);
			}
			contactUsVO.setContactType(mContactType);
			contactUsVO.setEmail(mEmail);
			contactUsVO.setFirstName(mFirstName);
			contactUsVO.setLastName(mLastName);
			contactUsVO.setGender(mGender != null ? mGender : "");
			contactUsVO
					.setPhoneNumber(mPhoneNumber != null ? mPhoneNumber : "");
			contactUsVO.setPhoneExt(mPhoneExt != null ? mPhoneExt : "");
			contactUsVO
					.setOrderNumber(mOrderNumber != null ? mOrderNumber : "");
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(mSelectedTimeCall != null ? mSelectedTimeCall
					: "");
			stringBuilder.append(BBBCoreConstants.SPACE);
			stringBuilder.append(mAmPM != null ? mAmPM : "");
			contactUsVO.setTimeCall(stringBuilder.toString());
			contactUsVO.setTimeZone(mSelectedTimeZone);
			Calendar date = Calendar.getInstance();
			SimpleDateFormat dateformatter = new SimpleDateFormat(
					BBBCoreConstants.DATE_FORMAT1);
			String pSumitDate = dateformatter.format(date.getTime());
			contactUsVO.setSubmitDate(pSumitDate);
			contactUsVO.setSiteFlag(siteId);
			/*
			 * Send email for confirmation
			 */
			try {
				Object[] pRecipients = new String[] { mEmail };
				TemplateEmailInfoImpl pTemplateInfo = (TemplateEmailInfoImpl) getRegistrationTemplateEmailInfo();

				if (pTemplateInfo == null) {
					String errorMessage = getLblTxtTemplateManager().getErrMsg(
							"err_profile_system_email_template_not_set",
							pRequest.getLocale().getLanguage(), null, null);
					addFormException(new DropletException(errorMessage,"err_profile_system_email_template_not_set"));
				} else {
					String customerCareEmailAddress = mContactUsManager
							.getcustomerCareEmailAddress(siteId);
					if (!BBBUtility.isEmpty(customerCareEmailAddress)) {
						pTemplateInfo.setMailingId(customerCareEmailAddress);
						pTemplateInfo.setMessageTo(customerCareEmailAddress);
						if(BBBUtility.isNotEmpty(mEmail)){
							pTemplateInfo.setMessageReplyTo(mEmail);	
						}
						pTemplateInfo.setMessageSubject(mSubjectCategory + " "
								+ contactUsVO.getTimeCall() + " "
								+ contactUsVO.getTimeZone());
						pTemplateInfo.setMessageFrom(mEmail);
						Map pTemplateParams = new HashMap();
						Map placeHolderMap = new HashMap();
						placeHolderMap.put(BBBCoreConstants.EMAIL_TYPE,
								BBBCoreConstants.ET_ContactUs);
						placeHolderMap.put(BBBCoreConstants.FORM_SITE, siteId);
						placeHolderMap.put(BBBCoreConstants.FORM_FNAME,
								mFirstName);
						placeHolderMap.put(BBBCoreConstants.FORM_LNAME,
								mLastName);
						placeHolderMap.put(
								BBBCoreConstants.FORM_CONTACT_METHOD,
								mContactType);
						placeHolderMap.put(BBBCoreConstants.FORM_MESSAGE,
								mEmailMessage);
						placeHolderMap.put(BBBCoreConstants.FORM_ORDER,
								mOrderNumber != null ? mOrderNumber : "");
						placeHolderMap.put(BBBCoreConstants.FORM_PHONE,
								mPhoneNumber + "-" + mPhoneExt);
						placeHolderMap.put(
								BBBCoreConstants.FORM_TIME_TO_CALL,
								contactUsVO.getTimeCall() + " "
										+ contactUsVO.getTimeZone());
						placeHolderMap.put(BBBCoreConstants.FORM_EMAIL, mEmail);
						placeHolderMap.put(
								BBBCoreConstants.EMAIL_SUBJECT,
								mSubjectCategory + " "
										+ contactUsVO.getTimeCall() + " "
										+ contactUsVO.getTimeZone());
						pTemplateParams.put(BBBCoreConstants.PLACE_HOLDER,
								placeHolderMap);
						pTemplateInfo.setTemplateParameters(pTemplateParams);

						pRecipients[0] = customerCareEmailAddress;
						mTools.sendEmail(pTemplateInfo, pRecipients);
						/*
						 * set success message to true so that only delivery
						 * confirm message will be populate on UI
						 */

						setSuccessMessage(true);
					} else {

						String errorMessage = getLblTxtTemplateManager()
								.getErrMsg("err_contactus_customercare_email",
										pRequest.getLocale().getLanguage(),
										null, siteId);
						addFormException(new DropletException(errorMessage,"err_contactus_customercare_email"));
					}
				}
			} catch (BBBSystemException systemException) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_profile_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1123),systemException);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_profile_send_email",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_profile_send_email"));
				setSuccessMessage(false);
			} catch (BBBBusinessException businessException) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_profile_send_email" , BBBCoreErrorConstants.ACCOUNT_ERROR_1123),businessException);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_profile_send_email",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_profile_send_email"));
				setSuccessMessage(false);
			}
			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				mContactUsManager.requestInfoTIBCO(contactUsVO);
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_contactus_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1122),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_contactus_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_contactus_tibco_exception"));
				setSuccessMessage(false);
			}
		}

			logDebug("ContactUsFormHandler.handleRequestInfo() method ends");

		return checkFormRedirect(getContactUsSuccessURL(),
				getContactUsErrorURL(), pRequest, pResponse);
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
		pRequest.setCharacterEncoding(BBBCoreConstants.UTF_ENCODING);
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
			setCaptchaError(true);
			String errorMessage = getLblTxtTemplateManager().getErrMsg(BBBCoreErrorConstants.ERROR_CAPTCH_INVALID, pRequest.getLocale().getLanguage(), null, null);
			addFormException(new DropletFormException(errorMessage, "/com/bbb/selfservice/ContactUsFormHandler.captchaAnswer", BBBCoreErrorConstants.ERROR_CAPTCH_INVALID));
		}
	}
}
