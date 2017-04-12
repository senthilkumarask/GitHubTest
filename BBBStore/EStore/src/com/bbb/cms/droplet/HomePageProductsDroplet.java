package com.bbb.cms.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.cms.HomePageTemplateVO;
import com.bbb.cms.manager.HomePageTemplateManager;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
/**
 * HomePageTemplateDroplet retrieves the data from HomePageRepository
 * @author iteggi
 *
 */
public class HomePageProductsDroplet extends BBBDynamoServlet {

	private HomePageTemplateManager mHomePageTemplateManager = null;
	//Defining the Constants
	static final String EMPTY = "empty";
	static final String HOMEPAGE_OUTPUT = "output";
	static final String HOME_TEMP_VO = "homePageTemplateVO";
	
	/**
	 * @return the homePageTemplateManager
	 */
	public HomePageTemplateManager getHomePageTemplateManager() {
		return mHomePageTemplateManager;
	}

	/**
	 * @param pHomePageTemplateManager the homePageTemplateManager to set
	 */
	public void setHomePageTemplateManager(HomePageTemplateManager pHomePageTemplateManager) {
		mHomePageTemplateManager = pHomePageTemplateManager;
	}

	/**
	 * This method gets the HomePageTemplateVO from HomePageTemplateManager
	 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {
		
		final String className = "HomePageTemplateDroplet";
		
		logDebug("Entering  " + className);
		
		
		String siteId = null;
		HomePageTemplateVO homePageTemplateVO= null;

		try{
			
			logDebug("Calling HomePageTemplateManager : ");
			
			if (null !=request.getLocalParameter(BBBCmsConstants.SITE_ID) ) {
				siteId = (String) request
						.getLocalParameter(BBBCmsConstants.SITE_ID);
			}
			homePageTemplateVO=getHomePageTemplateManager().getHomePageProducts(siteId);	
			if(null != homePageTemplateVO){
				
				logDebug("Received HomePageTemplateVO : "+homePageTemplateVO);
				
				request.setParameter(HOME_TEMP_VO, homePageTemplateVO);
				request.serviceParameter(HOMEPAGE_OUTPUT, request, response);
			} else{
				
				logDebug("Received HomePageTemplateVO as Null: ");
				
				request.serviceParameter(EMPTY, request, response);
			}
		}catch(BBBBusinessException be){
			
			logError(LogMessageFormatter.formatMessage(request, "HomePageProductsDroplet|service()|BBBBusinessException","catalog_1040"),be);
			

			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		} catch(BBBSystemException bs){
			

			logError(LogMessageFormatter.formatMessage(request, "HomePageProductsDroplet|service()|BBBSystemException","catalog_1041"),bs);
			
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		}

		
			logDebug("Exiting  " + className);
		

	}
}