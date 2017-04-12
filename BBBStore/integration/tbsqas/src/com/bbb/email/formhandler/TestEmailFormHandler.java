package com.bbb.email.formhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBGenericFormHandler;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

public class TestEmailFormHandler extends BBBGenericFormHandler {

	private String mRecipientEmail	= null;
	private String mSenderEmail		= null;
	private String mMessage			= null;
	private String mSiteId			= null;
	private String mSubject			= null;
	private String sucessUrl		= null;
	private String errorUrl			= null;
	private boolean  successMessage = false;
	private SMTPEmailSender emailSender = null;
	
	public String getSubject() {
		return mSubject;
	}
	
	public void setSubject(final String pSubject) {
		mSubject = pSubject;
	}
	
	public String getRecipientEmail() {
		return mRecipientEmail;
	}
	
	public void setRecipientEmail(final String pRecipientEmail) {
		mRecipientEmail = pRecipientEmail.trim();
	}
	
	public String getSenderEmail() {
		return mSenderEmail;
	}
	
	public void setSenderEmail(final String pSenderEmail) {
		mSenderEmail = pSenderEmail.trim();
	}
	
	public String getSiteId() {
		return mSiteId;
	}
	
	public void setSiteId(final String pSiteId) {
		mSiteId = pSiteId;
	}
	
	public String getMessage() {
		return mMessage;
	}

	public void setMessage(final String pMessage) {
		mMessage = pMessage;
	}

	public String getSucessUrl() {
		return sucessUrl;
	}
	public void setSucessUrl(String sucessUrl) {
		this.sucessUrl = sucessUrl;
	}
	public String getErrorUrl() {
		return errorUrl;
	}
	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}
	public boolean isSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(boolean successMessage) {
		this.successMessage = successMessage;
	}
	
	public SMTPEmailSender getEmailSender() {
		return emailSender;
	}

	public void setEmailSender(SMTPEmailSender emailSender) {
		this.emailSender = emailSender;
	}
	private void validateEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {

		logDebug("TestEmailFormHandler.validateEmail() method started");
		if (BBBUtility.isEmpty(getSenderEmail())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			addFormException(new DropletException("Invalid Senders Email Id"));
		}		
		if (!BBBUtility.isValidEmail(getSenderEmail())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			addFormException(new DropletException("Invalid Senders Email Id"));
		}
		if (BBBUtility.isEmpty(getRecipientEmail())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			addFormException(new DropletException("Invalid Recupents Email Id"));
		}		
		if (!BBBUtility.isValidEmail(getRecipientEmail())) {
			errorPlaceHolderMap.put("fieldName", "Email Address");
			addFormException(new DropletException("Invalid Recipents Email Id"));
		}
		
		logDebug("TestEmailFormHandler.validateEmail() method ends");
	}
	
	public boolean handleSendEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		logDebug("TestEmailFormHandler.handleSendEmail() method started");
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateEmail(pRequest, pResponse, errorPlaceHolderMap);
		if (!getFormError()) {
			try {
				logDebug("sendFailedRecordsReport :Sender :"+getSenderEmail()+" recipients: "+this.getRecipientEmail()+" subject "+getSubject());
				this.getEmailSender().sendEmailMessage(getSenderEmail(),this.getRecipientEmail(), getSubject(), getMessage());
				successMessage = true;
			}
			catch (EmailException e) {
				if(isLoggingDebug()){
					logDebug("EmailException when sending mail for bazaar voice");
				}
				logError(LogMessageFormatter.formatMessage(null, "TestEmailFormHandler.handleSendEmail() | EmailException Failed to send email message:" +
							"Remember to set /atg/dynamo/service/SMTPEmail.emailHandlerHostName and " +
							"/atg/dynamo/service/SMTPEmail.emailHandlerPort","catalog_1071" ),e);
			}

		}
		logDebug("TestEmailFormHandler.handleSendEmail() method ends");
		return checkFormRedirect(getSucessUrl(), getErrorUrl(), pRequest, pResponse);
	}
}