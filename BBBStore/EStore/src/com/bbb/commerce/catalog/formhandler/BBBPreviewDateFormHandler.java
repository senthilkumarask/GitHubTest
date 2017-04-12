package com.bbb.commerce.catalog.formhandler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.PreviewAttributes;
import com.bbb.common.BBBGenericFormHandler;

public class BBBPreviewDateFormHandler extends BBBGenericFormHandler{
	
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";
	private static final String VALID_DATE_TIME_EXCEPTION = "Enter valid Date and Time";
	private static final String PARSING_EXCEPTION = "The entered date cannot be parsed";
	private String mDate;
	private String mTime;
	private PreviewAttributes mPreviewAttributes;
	private String mSuccessURL;
	private String mErrorURL;
		
	public String getDate() {
		return mDate;
	}
	public void setDate(String pDate) {
		this.mDate = pDate;
	}
	public String getTime() {
		return mTime;
	}
	public void setTime(String pTime) {
		this.mTime = pTime;
	}
	public PreviewAttributes getPreviewAttributes() {
		return mPreviewAttributes;
	}
	public void setPreviewAttributes(PreviewAttributes pPreviewAttributes) {
		this.mPreviewAttributes = pPreviewAttributes;
	}
	

	public boolean handleSubmit(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ParseException, ServletException, IOException {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT,pRequest.getLocale());
			
			logDebug("BBBPreview Date:::: "+dateFormat);
			
			if (getDate() != null && getTime() != null && !getDate().equals("") && !getTime().equals("")) {
				Date date = dateFormat.parse(getDate() + " " + getTime());
				getPreviewAttributes().setPreviewDate(date);
			} else {
				addFormException(new DropletException(VALID_DATE_TIME_EXCEPTION,VALID_DATE_TIME_EXCEPTION));
			}
		} catch (ParseException e) {
			addFormException(new DropletException(PARSING_EXCEPTION,PARSING_EXCEPTION));
		}
		return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
	}
	/**
	 * @return the successURL
	 */
	public String getSuccessURL() {
		return mSuccessURL;
	}
	/**
	 * @param successURL the successURL to set
	 */
	public void setSuccessURL(String successURL) {
		this.mSuccessURL = successURL;
	}
	/**
	 * @return the errorURL
	 */
	public String getErrorURL() {
		return mErrorURL;
	}
	/**
	 * @param errorURL the errorURL to set
	 */
	public void setErrorURL(String errorURL) {
		this.mErrorURL = errorURL;
	}	
	
}
