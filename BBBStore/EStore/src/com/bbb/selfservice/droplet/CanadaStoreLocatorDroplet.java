/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 19-December-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.droplet;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.StoreSpecialityVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.CanadaStoreLocatorManager;

public class CanadaStoreLocatorDroplet extends BBBDynamoServlet {

	
	private CanadaStoreLocatorManager  mCanadaStoreLocatorManager;
	
	/**
	 * @return the canadaStoreLocatorManager
	 */
	public CanadaStoreLocatorManager getCanadaStoreLocatorManager() {
		return mCanadaStoreLocatorManager;
	}

	/**
	 * @param pCanadaStoreLocatorManager the canadaStoreLocatorManager to set
	 */
	public void setCanadaStoreLocatorManager(
			CanadaStoreLocatorManager pCanadaStoreLocatorManager) {
		mCanadaStoreLocatorManager = pCanadaStoreLocatorManager;
	}

	/**
	 * This method is used to fetch data of Store Locators for Canada.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("CanadaStoreLocatorDroplet.service() method started");
		
		try {
			
			List<StoreVO> canadaStoreLocatorVOs = getCanadaStoreLocatorManager().getCanadaStoreLocator();

			Map<String,StoreSpecialityVO> imageMap = new HashMap<String,StoreSpecialityVO>();
			
			for (StoreVO storeVO : canadaStoreLocatorVOs) {
				
				if(storeVO != null){
					
					List<StoreSpecialityVO> storeSpecialityVOs = storeVO.getStoreSpecialityVO();
					
					if(storeSpecialityVOs != null){
						
					    for (StoreSpecialityVO storeSpecialityVO : storeSpecialityVOs) {
								
								String imagePath = storeSpecialityVO.getCodeImage();
								
								if(imagePath != null && !"".equals(imagePath) && !imageMap.containsKey(imagePath)){
									imageMap.put(imagePath, storeSpecialityVO);	
								}
							}
					}
				}
				
				
			}
			
			
			pRequest.setParameter("canadaStoreLocator", canadaStoreLocatorVOs);
			pRequest.setParameter("imageMap", imageMap);
			
			logDebug("set output to the display page");

			pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBBusinessException businessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in CanadaStoreLocatorDroplet.service()", BBBCoreErrorConstants.ACCOUNT_ERROR_1184 ), businessException);
			pRequest.setParameter(BBBCoreConstants.SYSTEM_ERROR, "err_fetching_canada_stores");
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		}  catch (BBBSystemException systemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException in CanadaStoreLocatorDroplet.service()", BBBCoreErrorConstants.ACCOUNT_ERROR_1185 ), systemException);
			pRequest.setParameter(BBBCoreConstants.SYSTEM_ERROR, "err_fetching_canada_stores");
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest, pResponse);
		} 

		logDebug("CanadaStoreLocatorDroplet.service() method ends");
	}
}
