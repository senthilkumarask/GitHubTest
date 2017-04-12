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

import com.bbb.common.BBBGenericService;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.tibco.vo.BridalBookVO;
import com.bbb.selfservice.tibco.vo.TellAFriendVO;

/**
 * 
 * This class provides the low level functionality for Subscription Module
 * 
 * 
 */

public class BridalBookManager extends BBBGenericService {

	/**
	 * This method is used to call the TIBCO services for Bridal Book request
	 * 
	 * @return TIBCO success message
	 * @throws BBBBusinessException
	 */

	public void requestBridalBookTIBCO(BridalBookVO pBridalBookVO)
			throws BBBBusinessException {
		String methodName = BBBCoreConstants.BRIDALBOOK_TIBCO_CALL;
		logDebug(methodName + " method started");

		logDebug("Type :" + pBridalBookVO.getType());			
		logDebug("Email Address :" + pBridalBookVO.getEmailAddr());			
		logDebug("First Name :" + pBridalBookVO.getFirstName());
		logDebug("Last Name :" + pBridalBookVO.getLastName());
		logDebug("Address Line1 :" + pBridalBookVO.getAddressLine1());
		logDebug("Address Line2 :" + pBridalBookVO.getAddressLine2());
		logDebug("City :" + pBridalBookVO.getCity());
		logDebug("State :" + pBridalBookVO.getState());
		logDebug("Zipcode :" + pBridalBookVO.getZipcode());
		logDebug("Phone Number :" + pBridalBookVO.getPhoneNumber());			
		logDebug("Email Offer :" + pBridalBookVO.getEmailOffer());			

		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL,
				methodName);

		try {
			this.sendBridalBook(pBridalBookVO);

		} catch (BBBSystemException ex) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL,
					methodName);	
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in Bridal Book manager while requestTellAFriendTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1218 ), ex);
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1321,ex.getMessage(), ex);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL,
					methodName);			
		}

		logDebug(methodName + " method ends");
	}
	/**
	 * @param pBridalBookVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void sendBridalBook(BridalBookVO pBridalBookVO)
			throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.send(pBridalBookVO);
	}
	
	public void requestTellAFriendTIBCO(TellAFriendVO pTellAFriendVO)
			throws BBBBusinessException {
		String methodName = "requestTellAFriendTIBCO";
		logDebug(methodName + " method started");
		logDebug("Type :" + pTellAFriendVO.getType());			
		logDebug("Sender Email Address :" + pTellAFriendVO.getSenderEmailAddr());			
		logDebug("Sender First Name :" + pTellAFriendVO.getSenderFirstName());
		logDebug("Sender Last Name :" + pTellAFriendVO.getSenderLastName());
		logDebug("Recipient Email Address :" + pTellAFriendVO.getRecipientEmailAddr());			
		logDebug("Recipient First Name :" + pTellAFriendVO.getRecipientFirstName());
		logDebug("Recipient Last Name :" + pTellAFriendVO.getRecipientLastName());			
		logDebug("Email Copy :" + pTellAFriendVO.isEmailCopy());			

		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL,
				methodName);

		try {
			this.sendTellAFriendBride(pTellAFriendVO);

		} catch (BBBSystemException ex) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in Bridal Book manager while requestTellAFriendTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1219 ), ex);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL,
					methodName);
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1321,ex.getMessage(), ex);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL,
					methodName);			
		}

		logDebug(methodName + " method ends");
	}
	/**
	 * @param pTellAFriendVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void sendTellAFriendBride(TellAFriendVO pTellAFriendVO)
			throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.send(pTellAFriendVO);
	}

}
