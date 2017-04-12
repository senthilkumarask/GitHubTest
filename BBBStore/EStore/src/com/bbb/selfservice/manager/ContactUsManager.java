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

package com.bbb.selfservice.manager;

import java.util.List;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.tibco.vo.ContactUsVO;
import com.bbb.selfservice.tools.ContactUsTools;

/**
 * 
 * This class provides the low level functionality for ContactUS
 * maintain business logic 
 * 
 * 
 */

public class ContactUsManager extends BBBGenericService {
	
	private ContactUsTools mContactUsTools;
	private BBBCatalogTools mCatalogTools;
	 
	 /**
	 * @return the contactUsTools
	 */
	public ContactUsTools getContactUsTools() {
		return mContactUsTools;
	}

	/**
	 * @param pContactUsTools the contactUsTools to set
	 */
	public void setContactUsTools(ContactUsTools pContactUsTools) {
		mContactUsTools = pContactUsTools;
	}

	
	/**
	 * This method is used to fetch subject category from contact us repository by calling tools's getContactUsItem method  
	 * @return Array of RepositoryItem 
	 * @throws RepositoryException
	 */
	public RepositoryItem[] getContactUsItem() throws BBBBusinessException,BBBSystemException {
		logDebug("ContactUsManager.getContactUsItem() method started");
	    return mContactUsTools.getContactUsItem();
	 }
	
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}
	
	
	
	/**
	 * This method is used to fetch time zones by calling catalogtools's
	 * getTimeZones method
	 * 
	 * @return List of String
	 * @throws BBBBusinessException,BBBSystemException
	 */
	public List<String> getTimeZones(String pSiteId)
			throws BBBBusinessException,BBBSystemException {

		logDebug("ContactUsManager.getTimeZones() method started");
		return mCatalogTools.getTimeZones(pSiteId);
	}
	
	/**
	 * This method is used to fetch getcustomerCareEmailAddress by calling catalogtools's
	 * getcustomerCareEmailAddress method
	 * 
	 * @return String
	 * @throws BBBBusinessException,BBBSystemException
	 */
	public String getcustomerCareEmailAddress(String pSiteId)
			throws BBBBusinessException,BBBSystemException {

		logDebug("ContactUsManager.getcustomerCareEmailAddress() method started");
		return mCatalogTools.getcustomerCareEmailAddress(pSiteId);
	}
	

	
	
	
	/**
	 * This method is used to call the TIBCO services   
	 * @return TIBCO success message 
	 * @throws BBBBusinessException
	 */
	
	public void requestInfoTIBCO(ContactUsVO pContactUsVO) throws BBBBusinessException{
		
		
		logDebug("ContactUsManager.requestInfoTIBCO() method started");
		
		logDebug("Email :"+pContactUsVO.getEmail());
		logDebug("Email Message :"+pContactUsVO.getEmailMessage());
		logDebug("First Name :"+pContactUsVO.getFirstName());
		logDebug("Last Name :"+pContactUsVO.getLastName());
		logDebug("Phone :"+pContactUsVO.getPhoneNumber());
		logDebug("Phone Ext :"+pContactUsVO.getPhoneExt());
		logDebug("Order number :"+pContactUsVO.getOrderNumber());
		logDebug("Gender :"+pContactUsVO.getGender());
		logDebug("Contact Type :"+pContactUsVO.getContactType());
		logDebug("Time Zone :"+pContactUsVO.getTimeZone());
		logDebug("Time Call :"+pContactUsVO.getTimeCall());
		logDebug("Site Flag :"+pContactUsVO.getSiteFlag());
		
		String methodName = BBBCoreConstants.CONTACTUS_TIBCO_CALL;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);
		
		
		 try {
			 
			 this.sendRequestInfoTIBCO(pContactUsVO);
		
			} catch (BBBSystemException ex) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in ContactUsManager while requestInfoTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1220 ), ex);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);		
				throw new BBBBusinessException (BBBCoreErrorConstants.ACCOUNT_ERROR_1322,ex.getMessage(),ex);
		    }finally{
				 BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);		    	
		    }
			logDebug("ContactUsManager.requestInfoTIBCO() method ends");
	 }

	/**
	 * @param pContactUsVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void sendRequestInfoTIBCO(ContactUsVO pContactUsVO)
			throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.send(pContactUsVO);
	}
}
