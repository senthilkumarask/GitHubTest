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


import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * 
 * This class provides the low level functionality for ContactUS
 * fetch the data from contactus repository
 * 
 * 
 */

public class SubscriptionTools extends BBBGenericService {

	 private MutableRepository mContactUsRepository;
	 private boolean mSendEmailInSeparateThread;
	 private boolean mPersistEmails;
	 private TemplateEmailSender mTemplateEmailSender;
	    
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
	  * This method is used to fetch subject category from contactus repository   
	  * @return Array of RepositoryItem 
	  * @throws BBBBusinessException,BBBSystemException
	 */
	 
	 public RepositoryItem[] getContactUsItem() throws BBBSystemException {
	    	
			logDebug("ContactUsTools.getContactUsItem() method started");
		    RepositoryItem[] items = null;
		 	try{
			    RepositoryView view = getContactUsRepository().getView("contactus");
		    	RqlStatement rql = RqlStatement.parseRqlStatement("ALL");
		    	items = rql.executeQuery(view,null);
		 	}catch (RepositoryException eRepositoryException) {
		 		logError(LogMessageFormatter.formatMessage(null, "RepositoryException in ContactUsTools.getContactUsItem() throws error in fetch subject category from contactus repository", BBBCoreErrorConstants.ACCOUNT_ERROR_1233 ), eRepositoryException);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1334,eRepositoryException.getMessage(),eRepositoryException);
			}
	    	
	    	if(items!=null && items.length>0){
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
			 		logError(LogMessageFormatter.formatMessage(null, "TemplateEmailException in ContactUsTools.sendEmail() throws error in sending Email", BBBCoreErrorConstants.ACCOUNT_ERROR_1234 ), templateEmailException);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1335,templateEmailException.getMessage(), templateEmailException);
			}
			logDebug("ContactUsTools.sendEmail() method Ends");
			
		}
}
