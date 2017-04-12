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

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.tibco.vo.SurveyVO;


/**
 * 
 * This class provides the low level functionality for Survey maintain business
 * logic
 * 
 * 
 */

public class SurveyManager extends BBBGenericService {

	private BBBCatalogTools mCatalogTools;


	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * This method is used to fetch user location by calling catalogtools's
	 * getStates method
	 * 
	 * @return List of StateVO
	 * @throws BBBSystemException, BBBBusinessException
	 */
		public List<StateVO> getUserLocation(String pSiteId,boolean showMilitaryStates, String noShowPage)
		throws BBBSystemException, BBBBusinessException {
		
		logDebug("SurveyManager.getUserLocation() method started");
			return mCatalogTools.getStates(pSiteId,showMilitaryStates,noShowPage);
		}

	/**
	 * This method is used to call the TIBCO services
	 * 
	 * @return void
	 * @throws BBBBusinessException
	 */

	public void requestInfoTIBCO(SurveyVO pSurveyVO)
			throws BBBBusinessException {

		logDebug("SurveyManager.requestInfoTIBCO() method started");

		String methodName = "requestInfoTIBCO";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);
		try {
			send(pSurveyVO);

		} catch (BBBSystemException ex) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in SurveyManager while requestInfoTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1230 ), ex);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1325,ex.getMessage(),ex);
		}finally{
			 BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
		}
		logDebug("SurveyManager.requestInfoTIBCO() method ends");
	}

	/**
	 * @param pSurveyVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void send(SurveyVO pSurveyVO) throws BBBBusinessException, BBBSystemException {
		ServiceHandlerUtil.send(pSurveyVO);
	}

}
