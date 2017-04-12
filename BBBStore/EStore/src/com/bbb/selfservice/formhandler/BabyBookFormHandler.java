/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: akhajuria	
 *
 * Created on: 12-April-2012
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.formhandler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.BabyBookManager;
import com.bbb.selfservice.tibco.vo.BabyBookVO;
import com.bbb.selfservice.tibco.vo.TellAFriendVO;
import com.bbb.utils.BBBUtility;

public class BabyBookFormHandler extends BBBGenericFormHandler {

	private final String DATE_FORMAT = "yyyy/mm/dd";
	
	private SiteContext mSiteContext;
	private BabyBookVO mBabyBookVO;
	private TellAFriendVO mTellAFriendVO;
	private boolean mSuccessMessage = false;
	private boolean mContextAdded = false;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private String mMailSubject;
	private BabyBookManager mBabyBookManager;
	private String mBabyBookSuccessURL;
	private String mBabyBookErrorURL;
	private String mTemplateUrl;
	private TemplateEmailSender mEmailSender;
	private TemplateEmailInfoImpl mEmailInfo;
	private String contextPathFlag;
	private Profile mProfile = null;
	private String fromPage;// Page Name set from JSP
	private Map<String,String> successUrlMap;
	private Map<String,String> errorUrlMap;

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
	 * Sets The user profile associated with the email. The default profile is
	 * used here. This is configured in the component property file.
	 * 
	 * @param pProfile
	 *            - the user profile of the logged in user.
	 */
	public void setProfile(Profile pProfile) {
		mProfile = pProfile;
	}

	/**
	 * Gets the user profile associated with the email. The default profile is
	 * used here. This is configured in the component property file.
	 * 
	 * @return the user profile of the logged in user.
	 */
	public Profile getProfile() {
		return mProfile;
	}

	
	public String getContextPathFlag() {
		return contextPathFlag;
	}

	public void setContextPathFlag(String contextPathFlag) {
		this.contextPathFlag = contextPathFlag;
	}

	/**
	 * @return the templateUrl
	 */
	public String getTemplateUrl() {
		return mTemplateUrl;
	}

	/**
	 * @param pTemplateUrl the templateUrl to set
	 */
	public void setTemplateUrl(String pTemplateUrl) {
		mTemplateUrl = pTemplateUrl;
	}

	/**
	 * @return the emailSender
	 */
	public TemplateEmailSender getEmailSender() {
		return mEmailSender;
	}

	/**
	 * @param pEmailSender the emailSender to set
	 */
	public void setEmailSender(TemplateEmailSender pEmailSender) {
		mEmailSender = pEmailSender;
	}

	/**
	 * @return the emailInfo
	 */
	public TemplateEmailInfoImpl getEmailInfo() {
		return mEmailInfo;
	}

	/**
	 * @param pEmailInfo the emailInfo to set
	 */
	public void setEmailInfo(TemplateEmailInfoImpl pEmailInfo) {
		mEmailInfo = pEmailInfo;
	}

	/**
	 * @return the babyBookErrorURL
	 */
	public String getBabyBookErrorURL() {
		return mBabyBookErrorURL;
	}

	/**
	 * @param pBabyBookErrorURL
	 *            the babyBookErrorURL to set
	 */
	public void setBabyBookErrorURL(String pBabyBookErrorURL) {
		mBabyBookErrorURL = pBabyBookErrorURL;
	}

	/**
	 * @return the babyBookSuccessURL
	 */
	public String getBabyBookSuccessURL() {
		return mBabyBookSuccessURL;
	}

	/**
	 * @param pBabyBookSuccessURL
	 *            the babyBookSuccessURL to set
	 */
	public void setBabyBookSuccessURL(String pBabyBookSuccessURL) {
		mBabyBookSuccessURL = pBabyBookSuccessURL;
	}

	/**
	 * @param pBabyBookVO
	 *            the babyBookVO to set
	 */
	public void setBabyBookVO(BabyBookVO pBabyBookVO) {
		mBabyBookVO = pBabyBookVO;
	}

	/**
	 * @return the babyBookManager
	 */
	public BabyBookManager getBabyBookManager() {
		return mBabyBookManager;
	}

	/**
	 * @param pBabyBookManager
	 *            the babyBookManager to set
	 */
	public void setBabyBookManager(BabyBookManager pBabyBookManager) {
		mBabyBookManager = pBabyBookManager;
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

	/**
	 * Validate Baby Book Request form - Email Address validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void validateEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {
		logDebug("BabyBookFormHandler.validateEmail() method started");
		
		if (!BBBUtility.isEmpty(getBabyBookVO().getEmailAddr()) && !BBBUtility.isValidEmail(getBabyBookVO().getEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid email from validate email of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1019));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_email_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_email_invalid"));
		}
		
		logDebug("BabyBookFormHandler.validateEmail() method ends");
	}

	/**
	 * Validate Baby Book Request form - Empty field check for
	 * Address/City/State
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void emptyChkValidation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {
		logDebug("BabyBookFormHandler.emptyChkValidation() method started");
		if (!BBBUtility.isValidAddressLine1(getBabyBookVO().getAddressLine1())) {
			errorPlaceHolderMap.put("fieldName", "Address Line1");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid address from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1017));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_addr1_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_addr1_invalid"));
		}
		if (!BBBUtility.isEmpty(getBabyBookVO().getAddressLine2())) {
			if (!BBBUtility.isValidAddressLine2(getBabyBookVO().getAddressLine2())) {
				errorPlaceHolderMap.put("fieldName", "Address Line2");
				logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid address2 from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1016));
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_addr2_invalid",
						pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_addr2_invalid"));
			}
		}

		if (!BBBUtility.isValidCity(getBabyBookVO().getCity())) {
			errorPlaceHolderMap.put("fieldName", "City");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid city from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1018));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_city_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_city_invalid"));
		}
		if (!BBBUtility.isValidZip(getBabyBookVO().getZipcode())) {
			errorPlaceHolderMap.put("fieldName", "Zip");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid zip from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1031));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_zip_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_zip_invalid"));
		}
		if (!BBBUtility.isValidPhoneNumber(getBabyBookVO().getPhoneNumber())) {
			errorPlaceHolderMap.put("fieldName", "Phone number");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid phone from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1023));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_phone_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_phone_invalid"));
		}
		if (getBabyBookVO().getEventDate() != null) {
			try {
				DateFormat format = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
			    format.setLenient(false);
			    Date eventDate =getBabyBookVO().getEventDate();
			    String date = format.format(eventDate);
			    format.parse(date);
			} catch (java.text.ParseException e) {
				errorPlaceHolderMap.put("fieldName", "Event Date");
				logError(LogMessageFormatter.formatMessage(pRequest, "Event Date Parsing exception from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1020));
				addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_event_date_invalid",
						pRequest.getLocale().getLanguage(),	errorPlaceHolderMap, null),"err_bridalbook_event_date_invalid"));
			}
		}
		if (BBBUtility.isEmpty(getBabyBookVO().getState()) || getBabyBookVO().getState().equals("-1")) {
			errorPlaceHolderMap.put("fieldName", "State");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby Book invalid state from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1030));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_state_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_state_invalid"));
		}
		if (!getFormError()){
			if (!BBBUtility.isNonPOBoxAddress(getBabyBookVO().getAddressLine1(), getBabyBookVO().getAddressLine2())) {
				errorPlaceHolderMap.put("fieldName", "Address Line1");
				addFormException(new DropletException((getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.ERROR_POBOX_ADDRESS, pRequest.getLocale().getLanguage(), null)),BBBCheckoutConstants.ERROR_POBOX_ADDRESS));
			}
		}
		logDebug("BabyBookFormHandler.emptyChkValidation() method ends");

	}

	/**
	 * Validate Baby Book Request form - First & Last Name validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void validateName(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {
		
		logDebug("BabyBookFormHandler.validateName() method starts");
		// First Name validation
		if (!BBBUtility.isValidFirstName(getBabyBookVO().getFirstName())) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid first name from validateName of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1021));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_invalid_fname",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_invalid_fname"));
		}
		// Last Name validation
		if (!BBBUtility.isValidLastName(getBabyBookVO().getLastName())) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid last name from validateName of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1022));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_invalid_lname",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_invalid_lname"));
		}
		
		logDebug("BabyBookFormHandler.validateName() method ends");
	}

	/**
	 * Validate Tell a Friend form - First & Last Name validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void nameValidationTAF(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {
		
		logDebug("BabyBookFormHandler.nameValidationTAF() method starts");
		// Sender First name validation
		if (!BBBUtility.isValidFirstName(getTellAFriendVO().getSenderFirstName())) {
			errorPlaceHolderMap.put("fieldName", "Sender First Name");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid sender first name from nameValidationTAF of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1028));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_sender_fname_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_sender_fname_invalid"));
		}
		// Sender Last name validation
		if (!BBBUtility.isValidLastName(getTellAFriendVO().getSenderLastName())) {
			errorPlaceHolderMap.put("fieldName", "Sender Last Name");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid sender last name from nameValidationTAF of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1029));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_sender_lname_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_sender_lname_invalid"));

		}

		// Recipient first Name validation
		if (!BBBUtility.isValidFirstName(getTellAFriendVO().getRecipientFirstName())) {
			errorPlaceHolderMap.put("fieldName", "Recipient First Name");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid receiver first name from nameValidationTAF of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1025));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_receiver_fname_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_receiver_fname_invalid"));
		}

		// Recipient Last Name validation
		if (!BBBUtility.isValidLastName(getTellAFriendVO().getRecipientLastName())) {
			errorPlaceHolderMap.put("fieldName", "Recipient Last Name");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid receiver last name from nameValidationTAF of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1026));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_receiver_lname_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_receiver_lname_invalid"));

		}
		
		logDebug("BabyBookFormHandler.nameValidationTAF() method ends");

	}

	/**
	 * Validate Tell a Friend form - Email Validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void emailValidationTAF(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {
		logDebug("BabyBookFormHandler.emailValidationTAF() method starts");
		// Sender Email validation
		if (!BBBUtility.isValidEmail(getTellAFriendVO().getSenderEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Sender Email Address");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid sender email from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1027));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_sender_email_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_sender_email_invalid"));
		}

		// Recipient Email validation
		if (!BBBUtility.isValidEmail(getTellAFriendVO().getRecipientEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Recipient Email Address");
			logError(LogMessageFormatter.formatMessage(pRequest, "Baby book Invalid receiver email from emptyChkValidation of BabyBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1024));
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_receiver_email_invalid",
					pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null),"err_bridalbook_receiver_email_invalid"));
		}
		logDebug("BabyBookFormHandler.emailValidationTAF() method ends");
	}

	/**
	 * This method performs the validation for Baby book form
	 * 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */

	public void preRequestValidation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		logDebug("BabyBookFormHandler.preRequestValidation() method started");
		
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		checkDateAsString();
		validateEmail(pRequest, pResponse, errorPlaceHolderMap);
		validateName(pRequest, pResponse, errorPlaceHolderMap);
		emptyChkValidation(pRequest, pResponse, errorPlaceHolderMap);
		logDebug("BabyBookFormHandler.preRequestValidation() method ends");
		
	}

	/**
	 * Handle request for BabyBook
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleRequestBabyBook(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		logDebug("BabyBookFormHandler.handleRequestBabyBook() method started");
		
		if(getBabyBookVO().getDateAsString()== null || "".equals(getBabyBookVO().getDateAsString())){
			addFormException(new DropletException(getLblTxtTemplateManager().getErrMsg("err_bridalbook_event_date_invalid",
					pRequest.getLocale().getLanguage(),	null, null),"err_bridalbook_event_date_invalid"));
		}
		return this.handleBabyBookRequest(pRequest, pResponse);
	}
	
	/**
	 * Handle request for BabyBook
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleBabyBookRequest(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		logDebug("BabyBookFormHandler.handleBabyBookRequest() method started");
		final String siteId = mSiteContext.getSite().getId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setBabyBookSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setBabyBookErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}
		if((!BBBUtility.isEmpty(getBabyBookSuccessURL())) && (getBabyBookSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL))){
		addContextPath(pRequest.getContextPath());
		}
		preRequestValidation(pRequest, pResponse);
		
		if (!getFormError()) {
			// getBabyBookVO().setType(mType);
			getBabyBookVO().setSiteId(siteId);

			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				getBabyBookManager().requestBabyBookTIBCO(getBabyBookVO());
				setSuccessMessage(true);
				if(!BBBUtility.isEmpty(getBabyBookSuccessURL())&&!getBabyBookSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL)){
					setBabyBookSuccessURL(getBabyBookSuccessURL() + BBBCoreConstants.BABYPOPUP
						+ isSuccessMessage());
				}
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg("err_subscription_tibco_exception", pRequest.getLocale().getLanguage(),
						null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
		} else {
			if(!BBBUtility.isEmpty(getBabyBookSuccessURL())&&!getBabyBookSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL)){
				setBabyBookSuccessURL(getBabyBookSuccessURL() + BBBCoreConstants.BABYPOPUP + isSuccessMessage());
			}
		}
		
		logDebug("BabyBookFormHandler.handleBabyBookRequest() method ends");
		
		return checkFormRedirect(getBabyBookSuccessURL(), getBabyBookErrorURL(), pRequest, pResponse);
	}

	/**
	 * Method to convert string to date object if event date provided is String
	 */
	public void checkDateAsString()
	  { 
		if(null == getBabyBookVO().getEventDate()){
		    String dateString = getBabyBookVO().getDateAsString(); 
		    try {
		    	if(null != dateString){
				    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); 
				    Date convertedDate = dateFormat.parse(dateString);
					getBabyBookVO().setEventDate(convertedDate);
		    	}
			} catch (ParseException e) {
				DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
				logError("BabyBookFormHandler.handleBridalBookRequest: Error occurred while parsing event date",e);
				addFormException(new DropletException(
						getLblTxtTemplateManager().getErrMsg(
								"err_bridalbook_event_date_invalid",
								request.getLocale().getLanguage(), null),
						"err_bridalbook_event_date_invalid"));
			} 
		  }
	  } 
	
	/**
	 * Tell a Friend request - Validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 */
	private void preTAFValidation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
		
		logDebug("BabyBookFormHandler.preTAFValidation() method ends");
		
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		nameValidationTAF(pRequest, pResponse, errorPlaceHolderMap);
		emailValidationTAF(pRequest, pResponse, errorPlaceHolderMap);
		logDebug("BabyBookFormHandler.preTAFValidation() method ends");
		
	}
	public boolean handleRequestTellAFriend(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		return handleTellAFriend(pRequest, pResponse);
	}
	/**
	 * Handle Tell a Friend request
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleTellAFriend(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		logDebug("BabyBookFormHandler.handleTellAFriend() method started");
		
		if((!BBBUtility.isEmpty(getBabyBookSuccessURL())) && (getBabyBookSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL))){
		addContextPath(pRequest.getContextPath());
		}
		preTAFValidation(pRequest, pResponse);
		final String siteId = mSiteContext.getSite().getId();
		
		if (!getFormError()) {
			getTellAFriendVO().setSiteId(siteId);
			if(getTellAFriendVO().getType()==null){
				getTellAFriendVO().setType(TELLAFRIEND_REQUEST_TYPE.TYPE_TELL_A_FRIEND);
			}
			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				getBabyBookManager().requestTellAFriendTIBCO(getTellAFriendVO());
				handleSendEmail(pRequest, pResponse);
				setSuccessMessage(true);

			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg("err_subscription_tibco_exception", pRequest.getLocale().getLanguage(),
						null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
		}
		
		logDebug("BabyBookFormHandler.handleTellAFriend() method ends");
		
		return checkFormRedirect(getBabyBookSuccessURL(), getBabyBookErrorURL(), pRequest, pResponse);
	}

	/**
	 * @return the bridalBookVO
	 */
	public BabyBookVO getBabyBookVO() {
		if (mBabyBookVO == null) {
			mBabyBookVO = new BabyBookVO();
			return mBabyBookVO;
		} else {
			return mBabyBookVO;
		}
	}

	/**
	 * @param pBridalBookVO
	 *            the bridalBookVO to set
	 */
	public void setBridalBookVO(BabyBookVO pBabyBookVO) {
		mBabyBookVO = pBabyBookVO;
	}

	/**
	 * @return the tellAFriendVO
	 */
	public TellAFriendVO getTellAFriendVO() {
		if (mTellAFriendVO == null) {
			mTellAFriendVO = new TellAFriendVO();
			return mTellAFriendVO;
		} else {
			return mTellAFriendVO;
		}
	}

	/**
	 * @param pTellAFriendVO
	 *            the tellAFriendVO to set
	 */
	public void setTellAFriendVO(TellAFriendVO pTellAFriendVO) {
		mTellAFriendVO = pTellAFriendVO;
	}

	/**
	 * @return the mailSubject
	 */
	public String getMailSubject() {
		return mMailSubject;
	}

	/**
	 * @param pMailSubject
	 *            the mailSubject to set
	 */
	public void setMailSubject(String pMailSubject) {
		mMailSubject = pMailSubject;
	}
	
	/**
	 * Handles sending email functionality
	 * @param pRequest
	 * @param pResponse
	 * @throws BBBBusinessException
	 */
	private void handleSendEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws BBBBusinessException {
		
		logDebug("BabyBookFormHandler.handleSendEmail() method starts");
		
		if((!BBBUtility.isEmpty(getBabyBookSuccessURL())) && (getBabyBookSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL))){
		addContextPath(pRequest.getContextPath());
		}
		getEmailInfo().setTemplateURL(getTemplateUrl());
		getEmailInfo().setTemplateParameters(collectParams());
		getEmailInfo().setMailingId((String) collectParams().get(BBBCoreConstants.SENDER_EMAIL_PARAM_NAME));
		getEmailInfo().setMessageTo((String) collectParams().get(BBBCoreConstants.RECIPIENT_EMAIL_PARAM_NAME));
		getEmailInfo().setSiteId(mSiteContext.getSite().getId());
		// If emailcopy option is opt out
		if (getTellAFriendVO().isEmailCopy()) {
			getEmailInfo()
					.setMessageCc(getTellAFriendVO().getSenderEmailAddr());
		}
		List<String> recipientList = new ArrayList<String>();
		recipientList.add((String) collectParams().get(BBBCoreConstants.RECIPIENT_EMAIL_PARAM_NAME));
		String babyBookId = getLblTxtTemplateManager().getPageLabel("lbl_babybook_bookId", pRequest.getLocale().getLanguage(), null);
		Map<String,Map<String,String>> pTemplateParams = new HashMap<String,Map<String,String>>();
		Map<String,String> placeHolderMap = new HashMap<String,String>();
		final Calendar currentDate = Calendar.getInstance();				
		long uniqueKeyDate = currentDate.getTimeInMillis();
		String emailPersistId = getProfile().getRepositoryId() + uniqueKeyDate;	
		
		placeHolderMap.put(BBBCoreConstants.EMAIL_TYPE, BBBCoreConstants.ET_BABY);
		placeHolderMap.put(BBBCoreConstants.FORM_SITE, mSiteContext.getSite().getId());
		placeHolderMap.put(BBBCoreConstants.FORM_FNAME, (String)collectParams().get(BBBCoreConstants.SENDER_FIRST_NAME));
		placeHolderMap.put(BBBCoreConstants.FORM_LNAME, (String)collectParams().get(BBBCoreConstants.SENDER_LAST_NAME));
		placeHolderMap.put(BBBCoreConstants.FORM_FRND_FNAME, (String)collectParams().get(BBBCoreConstants.RECIPIENT_FIRST_NAME));
		placeHolderMap.put(BBBCoreConstants.FORM_FRND_LNAME, (String)collectParams().get(BBBCoreConstants.RECIPIENT_LAST_NAME));
		placeHolderMap.put(BBBCoreConstants.FORM_BRIDAL_BOOK_ID, babyBookId);
		placeHolderMap.put(BBBCoreConstants.FORM_SITE_NAME, pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND  + pRequest.getServerName() + pRequest.getContextPath());
		placeHolderMap.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);		
		pTemplateParams.put(BBBCoreConstants.PLACE_HOLDER, placeHolderMap);
		getEmailInfo().setTemplateParameters(pTemplateParams);
		try {
			((BBBTemplateEmailSender)getEmailSender()).sendEmailMessage(getEmailInfo(), recipientList,
					true, false);
		} catch (TemplateEmailException ex) {
			
			logError(LogMessageFormatter.formatMessage(pRequest, "TemplateEmailException Error in sending Baby Book Email", BBBCoreErrorConstants.ACCOUNT_ERROR_1212 ), ex);
			
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1212,ex.getMessage(), ex);
		}
		
		logDebug("BabyBookFormHandler.handleSendEmail() method ends");
		
	}
	
	/**
	 * Return a collection map of emailParams
	 * @return Map
	 */
	protected Map<String,String> collectParams() {
		// collect params from form handler to map and pass them into tools
		Map<String,String> emailParams = new HashMap<String,String>();
		emailParams.put("senderFirstName", getTellAFriendVO()
				.getSenderFirstName());
		emailParams.put("senderLastName", getTellAFriendVO()
				.getSenderLastName());
		emailParams.put("senderEmail", getTellAFriendVO().getSenderEmailAddr());
		emailParams.put("recipientFirstName", getTellAFriendVO()
				.getRecipientFirstName());
		emailParams.put("recipientLastName", getTellAFriendVO()
				.getRecipientLastName());
		emailParams.put("recipientEmail", getTellAFriendVO()
				.getRecipientEmailAddr());
		emailParams.put("templateUrl", getTemplateUrl());
		emailParams.put("mailSubject", getMailSubject());
		return emailParams;
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
}
