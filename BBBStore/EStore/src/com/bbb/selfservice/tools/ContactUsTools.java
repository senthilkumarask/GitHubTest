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
package com.bbb.selfservice.tools;


import atg.multisite.SiteContext;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * 
 * This class provides the low level functionality for ContactUS
 * fetch the data from contactus repository
 * 
 * 
 */

public class ContactUsTools extends BBBGenericService {

	 private MutableRepository mContactUsRepository;
	 private boolean mSendEmailInSeparateThread;
	 private boolean mPersistEmails;
	 private TemplateEmailSender mTemplateEmailSender;
	 private SiteContext mSiteContext;
	    
	 public MutableRepository getContactUsRepository() {
			return mContactUsRepository;
		}

	 public void setContactUsRepository(MutableRepository contactUsRepository) {
			mContactUsRepository = contactUsRepository;
	  }
	 
	 
	 /**
	 * @return the sendEmailInSeparateThread
	 */
	public boolean isSendEmailInSeparateThread() {
		return mSendEmailInSeparateThread;
	}

	/**
	 * @return the persistEmails
	 */
	public boolean isPersistEmails() {
		return mPersistEmails;
	}

	/**
	 * @return the templateEmailSender
	 */
	public TemplateEmailSender getTemplateEmailSender() {
		return mTemplateEmailSender;
	}

	/**
	 * @param pSendEmailInSeparateThread the sendEmailInSeparateThread to set
	 */
	public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
		mSendEmailInSeparateThread = pSendEmailInSeparateThread;
	}

	/**
	 * @param pPersistEmails the persistEmails to set
	 */
	public void setPersistEmails(boolean pPersistEmails) {
		mPersistEmails = pPersistEmails;
	}

	/**
	 * @param pTemplateEmailSender the templateEmailSender to set
	 */
	public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
		mTemplateEmailSender = pTemplateEmailSender;
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
	  * This method is used to fetch subject category from contactus repository   
	  * @return Array of RepositoryItem 
	  * @throws BBBBusinessException,BBBSystemException
	 */
	 
	 public RepositoryItem[] getContactUsItem() throws BBBSystemException {
	    	
			logDebug("ContactUsTools.getContactUsItem() method started");
		    RepositoryItem[] items = null;
		    final String siteId = mSiteContext.getSite().getId();
		 	try{
			    RepositoryView view = getContactUsRepository().getView("contactus");
		    	QueryBuilder queryBuilder = view.getQueryBuilder();
		    	QueryExpression pProperty = queryBuilder.createPropertyQueryExpression("siteName");
		    	QueryExpression pValue = queryBuilder.createConstantQueryExpression(siteId);
		    	final Query query = queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
		    	items = view.executeQuery(query);
		 	}catch (RepositoryException eRepositoryException) {
		 		logError(LogMessageFormatter.formatMessage(null, "Method ContactUsTools.getContactUsItem() throws error in fetch subject category from contactus repository", BBBCoreErrorConstants.ACCOUNT_ERROR_1231 ), eRepositoryException);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1332,eRepositoryException.getMessage(),eRepositoryException);
			}
	    	
	    	if(!BBBUtility.isArrayEmpty(items)){
	    	    	return items;
	    	}
	    	
			logDebug("ContactUsTools.getContactUsItem() method ends");
	    	
	    	return items;
	    }
	 
	 /**
		 * Sends email to the users passed as input with the template passed in
		 * pTemplateInfo parameter
		 * 
		 * @param users
		 * @param pTemplateParams
		 * @param pTemplateInfo
		 * @throws BBBSystemException
		 */
		public void sendEmail(TemplateEmailInfoImpl pTemplateInfo,Object[] pRecipients) throws BBBSystemException {
			logDebug("ContactUsTools.sendEmail() method started");
			
			try{
					mTemplateEmailSender.sendEmailMessage(pTemplateInfo, pRecipients,mSendEmailInSeparateThread,mPersistEmails);
				}catch (TemplateEmailException templateEmailException) {
			 		logError(LogMessageFormatter.formatMessage(null, "TemplateEmailException in Method ContactUsTools.sendEmail() throws error in sending Email", BBBCoreErrorConstants.ACCOUNT_ERROR_1232 ), templateEmailException);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1333,templateEmailException.getMessage(), templateEmailException);
			}
			logDebug("ContactUsTools.sendEmail() method Ends");
		}
}
