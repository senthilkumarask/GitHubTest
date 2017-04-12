/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Akhajuria
 *
 * Created on: 11-April-2012
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
import com.bbb.selfservice.tibco.vo.BabyBookVO;
import com.bbb.selfservice.tibco.vo.TellAFriendVO;

/**
 * 
 * This class provides the low level functionality for Subscription Module
 * 
 * 
 */

public class BabyBookManager extends BBBGenericService {

	/**
	 * This method is used to call the TIBCO services for Baby Book request
	 * 
	 * @return TIBCO success message
	 * @throws BBBBusinessException
	 */

	public void requestBabyBookTIBCO(BabyBookVO pBabyBookVO) throws BBBBusinessException {
		String methodName = BBBCoreConstants.BABYBOOK_TIBCO_CALL;
		logDebug(methodName + " method started");
		if (pBabyBookVO != null) {

			logDebug("Type :" + pBabyBookVO.getType());
			logDebug("Email Address :" + pBabyBookVO.getEmailAddr());
			logDebug("First Name :" + pBabyBookVO.getFirstName());
			logDebug("Last Name :" + pBabyBookVO.getLastName());
			logDebug("Address Line1 :" + pBabyBookVO.getAddressLine1());
			logDebug("Address Line2 :" + pBabyBookVO.getAddressLine2());
			logDebug("City :" + pBabyBookVO.getCity());
			logDebug("State :" + pBabyBookVO.getState());
			logDebug("Zipcode :" + pBabyBookVO.getZipcode());
			logDebug("Phone Number :" + pBabyBookVO.getPhoneNumber());
			logDebug("Email Offer :" + pBabyBookVO.getEmailOffer());

			BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);

			try {
				this.sendBabyBook(pBabyBookVO);

			} catch (BBBSystemException ex) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in Baby Book manager while requestBabyBookTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1216 ), ex);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
				throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1319,ex.getMessage(), ex);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
			}
		} else {
			BBBBusinessException sysExc = new BBBBusinessException("pBabyBookVO value is null");
			throw sysExc;
		}

		logDebug(methodName + " method ends");
	}

	/**
	 * @param pBabyBookVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void sendBabyBook(BabyBookVO pBabyBookVO)
			throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.send(pBabyBookVO);
	}

	/**
	 * This method is used to call the TIBCO services for Baby Book request by
	 * passing TellAFriendVO.
	 * 
	 * @param pTellAFriendVO
	 * @throws BBBBusinessException
	 */
	public void requestTellAFriendTIBCO(TellAFriendVO pTellAFriendVO) throws BBBBusinessException {
		String methodName = BBBCoreConstants.REQUEST_TELL_A_FRIEND_TIBCO;
		logDebug(methodName + " method started");
		if (pTellAFriendVO != null) {

			logDebug("Type :" + pTellAFriendVO.getType());
			logDebug("Sender Email Address :" + pTellAFriendVO.getSenderEmailAddr());
			logDebug("Sender First Name :" + pTellAFriendVO.getSenderFirstName());
			logDebug("Sender Last Name :" + pTellAFriendVO.getSenderLastName());
			logDebug("Recipient Email Address :" + pTellAFriendVO.getRecipientEmailAddr());
			logDebug("Recipient First Name :" + pTellAFriendVO.getRecipientFirstName());
			logDebug("Recipient Last Name :" + pTellAFriendVO.getRecipientLastName());
			logDebug("Email Copy :" + pTellAFriendVO.isEmailCopy());

			BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);

			try {
				this.sendTellAFriend(pTellAFriendVO);

			} catch (BBBSystemException ex) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in Baby Book manager while requestTellAFriendTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1217 ), ex);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
				throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1320,ex.getMessage(), ex);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
			}
		} else {
			BBBBusinessException sysExc = new BBBBusinessException("pTellAFriendVO value is null");
			throw sysExc;
		}

		logDebug(methodName + " method ends");
	}

	/**
	 * @param pTellAFriendVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void sendTellAFriend(TellAFriendVO pTellAFriendVO)
			throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.send(pTellAFriendVO);
	}

}
