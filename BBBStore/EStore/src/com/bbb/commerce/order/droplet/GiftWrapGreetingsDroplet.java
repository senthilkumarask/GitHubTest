//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Vishal Agrawal
//
//Created on: 02-December-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * This Droplet fetches all the gift wrap messages from the SiteRepository based
 * on passed siteId.
 * 
 */
public class GiftWrapGreetingsDroplet extends BBBDynamoServlet {

	/**
	 * Constant for BBBCatalogTools
	 */
	private BBBCatalogTools mBBBCatalogTools;

	/**
	 * This method fetches all the gift wrap messages from the SiteRepository
	 * based on passed siteId.
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	    BBBPerformanceMonitor.start("GiftWrapGreetingsDroplet", "service");

		logDebug("Starting method GiftWrapGreetingsDroplet.service");	

		try {

			final String siteId = (String) pRequest
					.getObjectParameter(BBBCheckoutConstants.SITEID);

			 logDebug("Passed parameters tp GiftWrapGreetingsDroplet.service --> siteId: "
						+ siteId);
		
			if (!StringUtils.isEmpty(siteId)) {

				Map<String, String> giftWrapMsg;
				giftWrapMsg = getBBBCatalogTools().getCommonGreetings(siteId);				
				logDebug("Returned gift Wrap Messages : " + giftWrapMsg);		

				pRequest.setParameter(BBBCheckoutConstants.GIFT_WRAP_MESSAGES,
						giftWrapMsg);
				pRequest.serviceLocalParameter(BBBCheckoutConstants.OUTPUT,
						pRequest, pResponse);

			} else {
				pRequest.serviceParameter(BBBCheckoutConstants.EMPTY, pRequest,
						pResponse);
			}

		} catch (BBBBusinessException exception) {
			
				logError("Error getting gift messages", exception);
			
		} catch (BBBSystemException exception) {
			
			    logError("Error getting gift messages", exception);
			
		}

		logDebug("Existing method GiftWrapGreetingsDroplet.service");
		
		BBBPerformanceMonitor.end("GiftWrapGreetingsDroplet", "service");
	}

	/**
	 * @return BBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}

	/**
	 * @param pBBBCatalogTools
	 */
	public void setBBBCatalogTools(final BBBCatalogTools pBBBCatalogTools) {
		this.mBBBCatalogTools = pBBBCatalogTools;
	}

	
	/**
	 * Fetch GetGiftWrapMsg by calling service method
	 * @return map
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, String> getCommonGreeting(Map <String,String> inputParam) throws BBBSystemException, BBBBusinessException{

		logDebug("GiftWrapGreetingsDroplet.getGiftWrapMsg() method starts");
		
		Map<String, String> giftWrapMsg = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();
		String siteId =  SiteContextManager.getCurrentSiteId();
		pRequest.setParameter(BBBCheckoutConstants.SITEID, siteId);
		try {
			service(pRequest, pResponse);
			giftWrapMsg = (Map<String, String>)pRequest.getObjectParameter(BBBCheckoutConstants.GIFT_WRAP_MESSAGES);
			if(giftWrapMsg == null || giftWrapMsg.isEmpty()){
				logError("GiftWrapGreetingsDroplet.getGiftWrapMsg() recived null giftWrapMsg object");
				throw new ServletException(); 
			} else {
				logDebug("GiftWrapGreetingsDroplet.getGiftWrapMsg() method ends");				
				return giftWrapMsg;
			}
		} catch (ServletException e) {
			 throw new BBBSystemException("err_giftwrap_greetings_droplet", "Internal Error!! BBBSystemException in GiftWrapGreetingsDroplet.getGiftWrapMsg()");
		} catch (IOException e) {
			 throw new BBBSystemException("err_io_giftwrap_greetings_droplet", "BBBSystemException in GiftWrapGreetingsDroplet.getGiftWrapMsg()");
		}
	}
}