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

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.selfservice.tibco.vo.SubscriptionVO;
import com.bbb.common.BBBGenericService;

/**
 * 
 * This class provides the low level functionality for Subscription Module
 * 
 * 
 */

public class SubscriptionManager extends BBBGenericService {
	
		/**
	 * This method is used to call the TIBCO services   
	 * @return TIBCO success message 
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	
	public void requestInfoTIBCO(SubscriptionVO pSubscriptionVO) throws BBBBusinessException, BBBSystemException{
		
		logDebug("SubscriptionManager.requestInfoTIBCO() method started");
		
		logDebug("Type :"+pSubscriptionVO.getType());
		logDebug("Frequency :"+pSubscriptionVO.getFrequency());
		logDebug("Email Address :"+pSubscriptionVO.getEmailAddr());
		logDebug("Confirm Email Address :"+pSubscriptionVO.getConfirmEmailAddr());
		logDebug("Salutation :"+pSubscriptionVO.getSalutation());
		logDebug("First Name :"+pSubscriptionVO.getFirstName());
		logDebug("Last Name :"+pSubscriptionVO.getLastName());
		logDebug("Address Line1 :"+pSubscriptionVO.getAddressLine1());
		logDebug("Address Line2 :"+pSubscriptionVO.getAddressLine2());
		logDebug("City :"+pSubscriptionVO.getCity());
		logDebug("State :"+pSubscriptionVO.getState());
		logDebug("Zipcode :"+pSubscriptionVO.getZipcode());
		logDebug("Phone Number :"+pSubscriptionVO.getPhoneNumber());
		logDebug("Mobile Number :"+pSubscriptionVO.getMobileNumber());
		logDebug("Email Offer :"+pSubscriptionVO.getEmailOffer());
		logDebug("Direct Mail Offer :"+pSubscriptionVO.getDirectMailOffer());
		logDebug("Mobile Offer :"+pSubscriptionVO.getMobileOffer());
		
		String methodName = BBBCoreConstants.SUBSCRIPTION_TIBCO_CALL;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);
		
		
		 try {
			 send(pSubscriptionVO); 
		
			} finally{
				 BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);	    	
		    }
			logDebug("SubscriptionManager.requestInfoTIBCO() method ends");
		}

		/**
		 * @param pSubscriptionVO
		 * @throws BBBBusinessException
		 * @throws BBBSystemException
		 */
		protected void send(SubscriptionVO pSubscriptionVO) throws BBBBusinessException, BBBSystemException {
			ServiceHandlerUtil.send(pSubscriptionVO);
		}

		

}
