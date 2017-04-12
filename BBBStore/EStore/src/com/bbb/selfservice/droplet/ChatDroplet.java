package com.bbb.selfservice.droplet;
import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * Droplet to check if Chat is enabled for the Site.
 * Copyright 2011, Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: njai13
 * Created on: November-2011
 * @author agu117
 *
 */
public class ChatDroplet extends BBBDynamoServlet {

	public final static String  OPARAM_ERROR="error";
	public final static String  OPARAM_OUTPUT="output";
	public final static String  OPARAM_CHAT_GLOBAL_FLAG="chatglobalFlag";

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




	public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response) throws ServletException, IOException
	{
	    String methodName = "ChatDroplet_service";
        BBBPerformanceMonitor.start(methodName );

		String siteId = SiteContextManager.getCurrentSiteId();

    	boolean globalChatEnabled = false;

    	logDebug("Entering ChatDroplet.service()");
    	logDebug("Parameters value [siteId="+siteId+"]");

		try {
			globalChatEnabled = getCatalogTools().checkGlobalChat(siteId);
			request.setParameter(OPARAM_CHAT_GLOBAL_FLAG, globalChatEnabled);
			request.serviceParameter(OPARAM_OUTPUT, request, response);

		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(request, "System Exception from service of ChatDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1024),e);
			request.serviceParameter(OPARAM_ERROR, request,response);
        }
        logDebug("Exiting ChatAskAndAnswerDroplet.service()");
		BBBPerformanceMonitor.end(methodName );
	}
}