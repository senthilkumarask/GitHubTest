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

package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.SurveyManager;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;

/**
 * The class is the extension of the ATG DynamoServlet. The class is responsible
 * for rendering the content for location drop down in survey.jsp.
 * 
 */

public class StateDroplet extends BBBDynamoServlet {

	private SurveyManager mSurveyManager;
	public static final String SHOW_MILITARY_STATES = "showMilitaryStates";
	public static final String NO_SHOW_PAGE = "NoShowUSTerr";

	/**
	 * @return the SurveyManager
	 */
	public SurveyManager getSurveyManager() {
		return mSurveyManager;
	}

	/**
	 * @param pSurveyManager
	 *            the SurveyManager to set
	 */
	public void setSurveyManager(SurveyManager pSurveyManager) {
		mSurveyManager = pSurveyManager;
	}

	 

	/**
	 * This method is used to fetch Location value in the dropdown to select a location for user.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("StateDroplet.service() method started");

	//  final String siteId = mSiteContext.getSite().getId();
	   
		 final String siteId = SiteContextManager.getCurrentSiteId();
	 	
		try {
			boolean showMilitaryStates = true;
			
			String noShowPage = pRequest.getParameter(NO_SHOW_PAGE);
	        
	        if(("FALSE").equalsIgnoreCase(pRequest.getParameter(SHOW_MILITARY_STATES))){
	        	showMilitaryStates = false;
	        }
	        List<StateVO> states = getSurveyManager().getUserLocation(siteId,showMilitaryStates,noShowPage);

			pRequest.setParameter("location", states);
				logDebug("set output to the display page");

			pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBBusinessException businessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in StateDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1210 ), businessException);
		}  catch (BBBSystemException systemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException in StateDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1211 ), systemException);
		} 

		logDebug("StateDroplet.service() method ends");

	}

}
