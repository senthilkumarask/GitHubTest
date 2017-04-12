package com.bbb.cms.droplet;



import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.RecommendationLandingPageTemplateVO;
import com.bbb.cms.manager.ContentTemplateManager;
//import com.bbb.cms.manager.RecommendationLandingPageTemplateManager;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * RecommendationLandingPageTemplateDroplet retrieves the data from RecommendationLandingPageTemplateRepository
 * @author amit
 *
 */
public class RecommendationLandingPageTemplateDroplet extends BBBDynamoServlet {

	//private RecommendationLandingPageTemplateManager mRecommendationLandingPageTemplateManager = null;
	//Defining the Constants
	static final String EMPTY = "empty";
	static final String RECOMMENDATION_OUTPUT = "output";
	static final String RECOMMENDATION_VO = "RecommendationLandingPageTemplateVO";
	static final String REGISTRY_URL = "registryURL";
	static final String REGISTRY_TYPE ="registryType";
	private ContentTemplateManager mContentTemplateManager = null;
	private static final String ARRAY_SEPARETOR = "}";
	private static final String  QUOTE = "\"";
	private static final String  COMMA = ",";
	private static final String ARRAY_SEPARETOR_START = "{";
	//private static final String REGISTRANT_TEMPLATE = "RegistrantTemplate";

	/**
	 * This method gets the RecommendationLandingPageTemplateVO from ContentTemplateManager
	 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {
		
		final String className = "RecommendationLandingPageTemplateDroplet";
		
			logDebug("Entering  " + className);
		
		BBBPerformanceMonitor.start(className);

		String registryType = null;
		String registryURL = null;
		String jsonString = null;

		try{
			
			logDebug("Calling ContentTemplateManager : ");
			
		
			if (null != request.getLocalParameter(BBBCmsConstants.REGISTRY_TYPE) ) {
				registryType = (String) request
						.getLocalParameter(BBBCmsConstants.REGISTRY_TYPE);
			}
			if (null != request.getLocalParameter(REGISTRY_URL) ) {
				registryURL = (String) request
						.getLocalParameter(REGISTRY_URL);
			}
	
			jsonString = ARRAY_SEPARETOR_START + QUOTE;
			if(null != registryType){
			 jsonString += REGISTRY_TYPE + QUOTE + ":" + QUOTE + registryType + QUOTE ;
			}
			if(null!= registryURL){
				jsonString += COMMA + QUOTE + REGISTRY_URL + QUOTE + ":" + QUOTE + registryURL + QUOTE ;
			}
			jsonString += ARRAY_SEPARETOR;
			RecommendationLandingPageTemplateVO recommendationLandingPageTemplateVO = null;
			try {
				recommendationLandingPageTemplateVO = getContentTemplateManager().getRegistrantContent("RegistrantTemplate",jsonString);
			} catch (RepositoryException e) {
				
				logError(LogMessageFormatter.formatMessage(request, "RecommendationLandingPageTemplateDroplet|service()|RepositoryException","catalog_1042"),e);
				
			}
			if(null != recommendationLandingPageTemplateVO){
				
				logDebug("Received RecommendationLandingPageTemplateVO : "+recommendationLandingPageTemplateVO.toString());
											
				request.setParameter(RECOMMENDATION_VO, recommendationLandingPageTemplateVO);
				request.serviceParameter(RECOMMENDATION_OUTPUT, request, response);
			} else{
				
				logDebug("Received RecommendationLandingPageTemplateVO as Null: ");
				
				request.serviceParameter(EMPTY, request, response);
			}
		}catch(BBBBusinessException be){
			logError(LogMessageFormatter.formatMessage(request, "RecommendationLandingPageTemplateDroplet|service()|BBBBusinessException","catalog_1042"),be);
			
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		} catch(BBBSystemException bs){
			
			logError(LogMessageFormatter.formatMessage(request, "RecommendationLandingPageTemplateDroplet|service()|BBBSystemException","catalog_1043"),bs);
			
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		}

		
		logDebug("Exiting  " + className);
		
		BBBPerformanceMonitor.end(className);

	}	

	public ContentTemplateManager getContentTemplateManager() {
		return mContentTemplateManager;
	}

	public void setContentTemplateManager(
			ContentTemplateManager pContentTemplateManager) {
		mContentTemplateManager = pContentTemplateManager;
	}

	

	

}