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
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.BridalBookManager;
import com.bbb.selfservice.tibco.vo.BridalBookVO;
import com.bbb.selfservice.tibco.vo.TellAFriendVO;
import com.bbb.utils.BBBUtility;

public class BridalBookFormHandler extends BBBGenericFormHandler {

	private final String DATE_FORMAT = "yyyy/mm/dd";
	private BridalBookManager mBridalBookManager;

	private SiteContext mSiteContext;
	private BridalBookVO mBridalBookVO;
	private TellAFriendVO mTellAFriendVO;
	private boolean mSuccessMessage = false;
	private boolean mContextAdded = false;
	private String mBridalBookSuccessURL;
	private String mBridalBookErrorURL;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private String mTemplateUrl;
	private TemplateEmailSender mEmailSender;
	private TemplateEmailInfoImpl mEmailInfo;
	private String mMailSubject;
	private Profile mProfile = null;
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

	/**
	 * Validate Bridal Book Request form - Email Address validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void validateEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {

		if (!BBBUtility.isEmpty(getBridalBookVO().getEmailAddr())
				&& !BBBUtility.isValidEmail(getBridalBookVO().getEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid email from validateEmail of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1019));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_email_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_email_invalid"));
		}
	}

	/**
	 * Validate Bridal Book Request form - Empty field check for
	 * Address/City/State
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void emptyChkValidation(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {

		if (!BBBUtility
				.isValidAddressLine1(getBridalBookVO().getAddressLine1())) {
			errorPlaceHolderMap.put("fieldName", "Address Line1");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid address from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1017));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_addr1_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_addr1_invalid"));
		}
		if (!BBBUtility.isEmpty(getBridalBookVO().getAddressLine2())) {
			if (!BBBUtility.isValidAddressLine2(getBridalBookVO()
					.getAddressLine2())) {
				errorPlaceHolderMap.put("fieldName", "Address Line2");
				logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid address 2 from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1016));
				addFormException(new DropletException(
						getLblTxtTemplateManager().getErrMsg(
								"err_bridalbook_addr2_invalid",
								pRequest.getLocale().getLanguage(),
								errorPlaceHolderMap, null),"err_bridalbook_addr2_invalid"));
			}
		}

		if (!BBBUtility.isValidCity(getBridalBookVO().getCity())) {
			errorPlaceHolderMap.put("fieldName", "City");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid city from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1018));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_city_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_city_invalid"));
		}
		if (!BBBUtility.isValidZip(getBridalBookVO().getZipcode())) {
			errorPlaceHolderMap.put("fieldName", "Zip");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid zip from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1031));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_zip_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_zip_invalid"));
		}
		if (!BBBUtility.isValidPhoneNumber(getBridalBookVO().getPhoneNumber())) {
			errorPlaceHolderMap.put("fieldName", "Phone number");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid phone from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1023));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_phone_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_phone_invalid"));
		}
		if (getBridalBookVO().getEventDate() == null) {
			
					
			errorPlaceHolderMap.put("fieldName", "Event Date");
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_event_date_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_event_date_invalid"));
		} else {
				
			try {
				DateFormat format = new SimpleDateFormat(BBBCoreConstants.DATE_FORMAT);
			    format.setLenient(false);
			    Date eventDate =getBridalBookVO().getEventDate();
			    String date = format.format(eventDate);
			    format.parse(date);
			} catch (java.text.ParseException e) {
				errorPlaceHolderMap.put("fieldName", "Event Date");
				logError(LogMessageFormatter.formatMessage(pRequest, "Event Date Parsing exception from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1020),e);
				addFormException(new DropletException(
						getLblTxtTemplateManager().getErrMsg(
								"err_bridalbook_event_date_invalid",
								pRequest.getLocale().getLanguage(),
								errorPlaceHolderMap, null),"err_bridalbook_event_date_invalid"));
			}
		}
		if (BBBUtility.isEmpty(getBridalBookVO().getState())
				|| getBridalBookVO().getState().equals("-1")) {
			errorPlaceHolderMap.put("fieldName", "State");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal Book invalid state from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1030));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_state_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_state_invalid"));
		}

	}

	/**
	 * Validate Bridal Book Request form - First & Last Name validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void validateName(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {
		// First Name validation
		if (!BBBUtility.isValidFirstName(getBridalBookVO().getFirstName())) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid first name from validateName of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1021));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_invalid_fname",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_invalid_fname"));
		}
		// Last Name validation
		if (!BBBUtility.isValidLastName(getBridalBookVO().getLastName())) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid last name from validateName of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1022));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_invalid_lname",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_invalid_lname"));
		}
	}

	/**
	 * Validate Tell a Friend form - First & Last Name validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void nameValidationTAF(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {
		// Sender First name validation
		if (!BBBUtility.isValidFirstName(getTellAFriendVO()
				.getSenderFirstName())) {
			errorPlaceHolderMap.put("fieldName", "Sender First Name");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid sender first name from nameValidationTAF of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1028));
			addFormException(new DropletException(getLblTxtTemplateManager()
					
					.getErrMsg("err_bridalbook_sender_fname_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_sender_fname_invalid"));
		}
		// Sender Last name validation
		if (!BBBUtility.isValidLastName(getTellAFriendVO().getSenderLastName())) {
			errorPlaceHolderMap.put("fieldName", "Sender Last Name");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid sender last name from nameValidationTAF of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1029));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_sender_lname_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_sender_lname_invalid"));

		}

		// Recipient first Name validation
		if (!BBBUtility.isValidFirstName(getTellAFriendVO()
				.getRecipientFirstName())) {
			errorPlaceHolderMap.put("fieldName", "Recipient First Name");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid receiver first name from nameValidationTAF of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1025));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_receiver_fname_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_receiver_fname_invalid"));
		}

		// Recipient Last Name validation
		if (!BBBUtility.isValidLastName(getTellAFriendVO()
				.getRecipientLastName())) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid receiver last name from nameValidationTAF of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1026));
			errorPlaceHolderMap.put("fieldName", "Recipient Last Name");
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_receiver_lname_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_receiver_lname_invalid"));

		}

	}

	/**
	 * Validate Tell a Friend form - Email Validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param errorPlaceHolderMap
	 */
	private void emailValidationTAF(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse,
			Map<String, String> errorPlaceHolderMap) {
		// Sender Email validation
		if (!BBBUtility.isValidEmail(getTellAFriendVO().getSenderEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Sender Email Address");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid sender email from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1027));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_sender_email_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_sender_email_invalid"));
		}

		// Recipient Email validation
		if (!BBBUtility
				.isValidEmail(getTellAFriendVO().getRecipientEmailAddr())) {
			errorPlaceHolderMap.put("fieldName", "Recipient Email Address");
			logError(LogMessageFormatter.formatMessage(pRequest, "Bridal book Invalid receiver email from emptyChkValidation of BridalBookFormHandler", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1024));
			addFormException(new DropletException(getLblTxtTemplateManager()
					.getErrMsg("err_bridalbook_receiver_email_invalid",
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null),"err_bridalbook_receiver_email_invalid"));
		}
	}

	/**
	 * This method performs the validation for bridal book form
	 * 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */

	public void preRequestValidation(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		logDebug("BridalBookFormHandler.preRequestValidation() method started");
		
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		checkDateAsString();
		validateEmail(pRequest, pResponse, errorPlaceHolderMap);
		validateName(pRequest, pResponse, errorPlaceHolderMap);
		emptyChkValidation(pRequest, pResponse, errorPlaceHolderMap);
		if (!getFormError()){
			if (!BBBUtility.isNonPOBoxAddress(getBridalBookVO().getAddressLine1(), getBridalBookVO().getAddressLine2())) {
				errorPlaceHolderMap.put("fieldName", "Address Line1");
				addFormException(new DropletException((getLblTxtTemplateManager().getErrMsg(BBBCheckoutConstants.ERROR_POBOX_ADDRESS, pRequest.getLocale().getLanguage(), null)),BBBCheckoutConstants.ERROR_POBOX_ADDRESS));
			}
		}

	}
	
	/**
	 * Method to convert string to date object if event date provided is String
	 */
	public void checkDateAsString() 
	  { 
		if(null == getBridalBookVO().getEventDate()){
		    String dateString = getBridalBookVO().getDateAsString(); 
		    try {
		    	if(!BBBUtility.isEmpty(dateString)){
				    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); 
				    Date convertedDate;
					convertedDate = dateFormat.parse(dateString);
					getBridalBookVO().setEventDate(convertedDate);
		    	}
			} catch (ParseException e) {
				logError("BridalBookFormHandler.handleBridalBookRequest: Error occurred while parsing event date",e);
			} 
		  }
	  } 
	
	/**
	 * Handle request for Bridal book
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	//commenting off this method as it was not referenced anywhere as a part of BPSI-358
/*	public boolean handleRequestBridalBook(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("BridalBookFormHandler.handleRequestBridalBook() method started");
		
		return this.handleBridalBookRequest(pRequest, pResponse);
	}	*/
	
	/**
	 * Handle request for Bridal book
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleBridalBookRequest(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("BridalBookFormHandler.handleBridalBookRequest() method started");
			final String siteId = mSiteContext.getSite().getId();
			if (StringUtils.isNotEmpty(getFromPage())) {
				setBridalBookSuccessURL(pRequest.getContextPath()
						+ getSuccessUrlMap().get(getFromPage()));
				setBridalBookErrorURL(pRequest.getContextPath()
						+ getErrorUrlMap().get(getFromPage()));

			}
			addContextPath(pRequest.getContextPath());	
		preRequestValidation(pRequest, pResponse);
		
		
		if (!getFormError()) {
			// getBridalBookVO().setType(mType);
			getBridalBookVO().setSiteId(siteId);

			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				getBridalBookManager()
						.requestBridalBookTIBCO(getBridalBookVO());
				setSuccessMessage(true);
				if(!BBBUtility.isEmpty(getBridalBookSuccessURL())&&!getBridalBookSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL)){
					setBridalBookSuccessURL(getBridalBookSuccessURL() + BBBCoreConstants.BRIDALPOPUP
						+ isSuccessMessage());
				}
			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
		} else {
			if(!BBBUtility.isEmpty(getBridalBookSuccessURL())&&!getBridalBookSuccessURL().equalsIgnoreCase(BBBCoreConstants.REST_REDIRECT_URL)){
				setBridalBookSuccessURL(getBridalBookSuccessURL() + BBBCoreConstants.BRIDALPOPUP
					+ isSuccessMessage());
			}
		}
			logDebug("BridalBookFormHandler.handleBridalBookRequest() method ends");
		return checkFormRedirect(getBridalBookSuccessURL(), getBridalBookErrorURL(), pRequest,
				pResponse);
	}

	/**
	 * Tell a Friend request - Validation
	 * 
	 * @param pRequest
	 * @param pResponse
	 */
	private void preTAFValidation(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		nameValidationTAF(pRequest, pResponse, errorPlaceHolderMap);
		emailValidationTAF(pRequest, pResponse, errorPlaceHolderMap);
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
	public boolean handleTellAFriend(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			logDebug("BridalBookFormHandler.handleTellAFriend() method started");
		addContextPath(pRequest.getContextPath());	
		preTAFValidation(pRequest, pResponse);
		final String siteId = mSiteContext.getSite().getId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setBridalBookSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setBridalBookErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}
		if (!getFormError()) {
			getTellAFriendVO().setSiteId(siteId);
			/*
			 * Call TIBCO Service throw JMS integration
			 */
			try {
				getBridalBookManager().requestTellAFriendTIBCO(
						getTellAFriendVO());
				sendEmail(pRequest, pResponse);
				setSuccessMessage(true);

			} catch (BBBBusinessException ex) {
				
				logError(LogMessageFormatter.formatMessage(pRequest, "err_subscription_tibco_exception" , BBBCoreErrorConstants.ACCOUNT_ERROR_1121),ex);
				
				String errorMessage = getLblTxtTemplateManager().getErrMsg(
						"err_subscription_tibco_exception",
						pRequest.getLocale().getLanguage(), null, null);
				addFormException(new DropletException(errorMessage,"err_subscription_tibco_exception"));
				setSuccessMessage(false);
			}
		}
			logDebug("BridalBookFormHandler.handleTellAFriend() method ends");
		return checkFormRedirect(getBridalBookSuccessURL(),
				getBridalBookErrorURL(), pRequest, pResponse);
	}

	protected Map collectParams() {
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

	private void sendEmail(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws BBBBusinessException {
		addContextPath(pRequest.getContextPath());
		getEmailInfo().setTemplateURL(getTemplateUrl());
		getEmailInfo().setTemplateParameters(collectParams());
		getEmailInfo().setMailingId((String) collectParams().get("senderEmail"));
		getEmailInfo().setMessageTo((String) collectParams().get("recipientEmail"));
		getEmailInfo().setSiteId(mSiteContext.getSite().getId());
		// If emailcopy option is opt out
		if (getTellAFriendVO().isEmailCopy()) {
			getEmailInfo()
					.setMessageCc(getTellAFriendVO().getSenderEmailAddr());
		}
		List<String> recipientList = new ArrayList<String>();
		recipientList.add((String) collectParams().get("recipientEmail"));
		String bridalBookId = getLblTxtTemplateManager().getPageLabel("lbl_bridalbook_bookId", pRequest.getLocale().getLanguage(), null);
		Map pTemplateParams = new HashMap();
		Map placeHolderMap = new HashMap();
		final Calendar currentDate = Calendar.getInstance();				
		long uniqueKeyDate = currentDate.getTimeInMillis();
		String emailPersistId = getProfile().getRepositoryId() + uniqueKeyDate;
		
		placeHolderMap.put(BBBCoreConstants.EMAIL_TYPE, BBBCoreConstants.ET_BRIDAL);
		placeHolderMap.put(BBBCoreConstants.FORM_SITE, mSiteContext.getSite().getId());
		placeHolderMap.put(BBBCoreConstants.FORM_FNAME, (String)collectParams().get("senderFirstName"));
		placeHolderMap.put(BBBCoreConstants.FORM_LNAME, (String)collectParams().get("senderLastName"));
		placeHolderMap.put(BBBCoreConstants.FORM_FRND_FNAME, (String)collectParams().get("recipientFirstName"));
		placeHolderMap.put(BBBCoreConstants.FORM_FRND_LNAME, (String)collectParams().get("recipientLastName"));
		placeHolderMap.put(BBBCoreConstants.FORM_BRIDAL_BOOK_ID, bridalBookId);
		placeHolderMap.put(BBBCoreConstants.FORM_SITE_NAME, pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND  + pRequest.getServerName() + pRequest.getContextPath());
		placeHolderMap.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);		
		pTemplateParams.put(BBBCoreConstants.PLACE_HOLDER, placeHolderMap);
		getEmailInfo().setTemplateParameters(pTemplateParams);
		try {
			((BBBTemplateEmailSender)getEmailSender()).sendEmailMessage(getEmailInfo(), recipientList,
					true, false);
		} catch (TemplateEmailException ex) {
			
			logError(LogMessageFormatter.formatMessage(pRequest, "TemplateEmailException Error in sending Bridal Book Email", BBBCoreErrorConstants.ACCOUNT_ERROR_1213 ), ex);
			
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1213,ex.getMessage(), ex);
		}
	}

	/**
	 * @return the bridalBookVO
	 */
	public BridalBookVO getBridalBookVO() {
		if (mBridalBookVO == null) {
			mBridalBookVO = new BridalBookVO();
			return mBridalBookVO;
		} else {
			return mBridalBookVO;
		}
	}

	/**
	 * @param pBridalBookVO
	 *            the bridalBookVO to set
	 */
	public void setBridalBookVO(BridalBookVO pBridalBookVO) {
		mBridalBookVO = pBridalBookVO;
	}

	/**
	 * @return the bridalBookSuccessURL
	 */
	public String getBridalBookSuccessURL() {
		return mBridalBookSuccessURL;
	}

	/**
	 * @param pBridalBookSuccessURL
	 *            the bridalBookSuccessURL to set
	 */
	public void setBridalBookSuccessURL(String pBridalBookSuccessURL) {
		mBridalBookSuccessURL = pBridalBookSuccessURL;
	}

	/**
	 * @return the bridalBookErrorURL
	 */
	public String getBridalBookErrorURL() {
		return mBridalBookErrorURL;
	}

	/**
	 * @param pBridalBookErrorURL
	 *            the bridalBookErrorURL to set
	 */
	public void setBridalBookErrorURL(String pBridalBookErrorURL) {
		mBridalBookErrorURL = pBridalBookErrorURL;
	}

	/**
	 * @return the bridalBookManager
	 */
	public BridalBookManager getBridalBookManager() {
		return mBridalBookManager;
	}

	/**
	 * @param pBridalBookManager
	 *            the bridalBookManager to set
	 */
	public void setBridalBookManager(BridalBookManager pBridalBookManager) {
		mBridalBookManager = pBridalBookManager;
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
	 * @return the emailSender
	 */
	public TemplateEmailSender getEmailSender() {
		return mEmailSender;
	}

	/**
	 * @param pEmailSender
	 *            the emailSender to set
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
	 * @param pEmailInfo
	 *            the emailInfo to set
	 */
	public void setEmailInfo(TemplateEmailInfoImpl pEmailInfo) {
		mEmailInfo = pEmailInfo;
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
