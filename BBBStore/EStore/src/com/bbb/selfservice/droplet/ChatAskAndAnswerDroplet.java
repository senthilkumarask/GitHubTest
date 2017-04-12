package com.bbb.selfservice.droplet;
import java.io.IOException;
import javax.servlet.ServletException;

import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.PDPAttributesVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * Droplet to access all data related to Chat and Ask and Answer for Product Details page.
 * Copyright 2011, Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: njai13
 * Created on: November-2011
 * @author agu117
 *
 */
public class ChatAskAndAnswerDroplet extends BBBDynamoServlet {

	public final static String  OPARAM_ERROR="error";
	public final static String  OPARAM_OUTPUT="output";
	public final static String  OPARAM_PDP_ATTRIBUTES_VO="PDPAttributesVo";
	public final static String  PARAMETER_PRODUCTID ="productId";
	public final static String  PARAMETER_CATEGORYID ="categoryId";
	public final static String  PARAMETER_POC ="poc";
	public final static String  PARAMETER_SITEID ="siteId";




	private BBBCatalogTools mCatalogTools;
	private SiteContext mSiteContext;

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
	    String methodName = "ChatAskAndAnswerDroplet_service";
        BBBPerformanceMonitor.start(methodName );

        PDPAttributesVO pdpAttributesVo = new PDPAttributesVO();
        
        /**
         * extracted method to obtain SiteId from SiteContextManager
         */
        String siteId = extractSiteID();

        if(!(BBBUtility.isEmpty(request.getParameter(PARAMETER_PRODUCTID)))){
        	String productId =request.getParameter(PARAMETER_PRODUCTID);
        	String categoryId =request.getParameter(PARAMETER_CATEGORYID);
        	String poc =request.getParameter(PARAMETER_POC);

        	logDebug("Entering ChatAskAndAnswerDroplet.service()");
	    	logDebug("Request Parameters value[productId="+productId+"][siteId="+siteId+"]"+"][categoryId="+categoryId+"]"+"][poc="+poc+"]");

			try {
		        pdpAttributesVo = getCatalogTools().PDPAttributes(productId, categoryId, poc, siteId);

				request.setParameter(OPARAM_PDP_ATTRIBUTES_VO, pdpAttributesVo);
				request.serviceParameter(OPARAM_OUTPUT, request, response);
		        logDebug("Added Values in the PDP Attributes Vo");

			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(request, "System Exception [Product Detail] from service of ChatAskAndAnswerDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1024),e);
				request.serviceParameter(OPARAM_ERROR, request,response);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(request, "Business Exception [Product Detail] from service of ChatAskAndAnswerDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1025),e);
				request.serviceParameter(OPARAM_ERROR, request,response);
			}
        }
        logDebug("Exiting ChatAskAndAnswerDroplet.service()");
		BBBPerformanceMonitor.end(methodName );
	}

	protected String extractSiteID() {
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}
}