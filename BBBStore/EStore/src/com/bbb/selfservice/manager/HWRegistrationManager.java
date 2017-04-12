/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Prashanth Kumar Bhoomula
 *
 * Created on: 05-July-2012
 * --------------------------------------------------------------------------------
 */

package com.bbb.selfservice.manager;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.tibco.vo.HWRegistrationVO;

/**
 * 
 * This class provides the low level functionality for Healthy Woman Registration
 * 
 * 
 * 
 */

public class HWRegistrationManager extends BBBGenericService {

	/**
	 * This method is used to call the TIBCO services for HealthWoman Registration Registration
	 * 
	 * @return TIBCO success message
	 * @throws BBBBusinessException
	 */

	public void requestHealthyWomanTIBCO(HWRegistrationVO hwRegistrationVO) throws BBBBusinessException {

		logDebug("HWRegistrationManager.requestHealthyWomanTIBCO method started");
		if (hwRegistrationVO != null) {

			logDebug("Type :" + hwRegistrationVO.getType());
			logDebug("Email Address :" + hwRegistrationVO.getEmailAddr());
			logDebug("First Name :" + hwRegistrationVO.getFirstName());
			logDebug("Last Name :" + hwRegistrationVO.getLastName());
			logDebug("Address Line1 :" + hwRegistrationVO.getAddressLine1());
			logDebug("Address Line2 :" + hwRegistrationVO.getAddressLine2());
			logDebug("City :" + hwRegistrationVO.getCity());
			logDebug("State :" + hwRegistrationVO.getState());
			logDebug("Zipcode :" + hwRegistrationVO.getZipcode());
			logDebug("Email Offer :" + hwRegistrationVO.getEmailOffer());

			BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, "requestHealthyWomanTIBCO");

			try {
				this.sendHealthyWomenTIBCO(hwRegistrationVO);

			} catch (BBBSystemException ex) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in HWRegistrationManager while requestHealthyWomanTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1221 ), ex);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, "requestHealthyWomanTIBCO");
				throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1323,ex.getMessage(), ex);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, "requestHealthyWomanTIBCO");
			}
		} else {
			BBBBusinessException sysExc = new BBBBusinessException("hwRegistrationVO value is null");
			throw sysExc;
		}
		logDebug("HWRegistrationManager.requestHealthyWomanTIBCO method ends");
	}

	/**
	 * @param hwRegistrationVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void sendHealthyWomenTIBCO(HWRegistrationVO hwRegistrationVO)
			throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.send(hwRegistrationVO);
	}
}
