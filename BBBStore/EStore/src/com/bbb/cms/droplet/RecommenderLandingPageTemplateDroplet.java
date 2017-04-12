package com.bbb.cms.droplet;



import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.RecommendationLandingPageTemplateVO;
import com.bbb.cms.manager.ContentTemplateManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * RecommendationLandingPageTemplateDroplet retrieves the data from
 * RecommendationLandingPageTemplateRepository
 *
 * @author amit
 *
 */
public class RecommenderLandingPageTemplateDroplet extends BBBDynamoServlet {

	private ContentTemplateManager mContentTemplateManager = null;

	// Defining the Constants
	static final String EMPTY = "empty";
	static final String RECOMMENDER_OUTPUT = "output";
    static final String RECOMMENDER_VO = "RecommenderTemplateVO";
	static final String RECOMMENDER_URL = "recommenderURL";
	static final String TOKEN_ERROR = "tokenError";
	static final String VISIBILITY_FLAG = "visibilityFlag";
	static final String TOKEN_ERROR_LOGIN = "tokenErrorLogin";
	static final String REGISTRY_TYPE = "registryType";
	static final String LOGIN_URL = "logInURL";
	private static final String ARRAY_SEPARETOR = "}";
	private static final String  QUOTE = "\"";
	private static final String  COMMA = ",";
	private static final String ARRAY_SEPARETOR_START = "{";
	private static final String  RECOMMENDER_TEMPLATE = "RecommenderTemplate";


	/**
	 * This method gets the RecommendationLandingPageTemplateVO from
	 * ContentTemplateManager
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		final String className = "RecommenderLandingPageTemplateDroplet";
		
		logDebug("Entering  " + className);
		
		BBBPerformanceMonitor.start(className);
		String registryType = null;
		String mRecommenderURL = null;
		String mTokenError = null;
		String mVisibility = null;
		String mTokenErrorLogin = null;
		String mLogInURL = null;
		String jsonString = null;

		try {
			
				logDebug("Calling RecommenderLandingPageTemplateManager : ");
			
			if (null != request
					.getLocalParameter(BBBCmsConstants.REGISTRY_TYPE)) {
				registryType = (String) request
						.getLocalParameter(BBBCmsConstants.REGISTRY_TYPE);
			}
			if (null != request
					.getLocalParameter(RECOMMENDER_URL)) {
				mRecommenderURL = (String) request
						.getLocalParameter(RECOMMENDER_URL);
			}
			if (null != request
					.getLocalParameter(TOKEN_ERROR)) {
				mTokenError = (String) request
						.getLocalParameter(TOKEN_ERROR);
			}
			if (null != request
					.getLocalParameter(VISIBILITY_FLAG)) {
				mVisibility = (String) request
						.getLocalParameter(VISIBILITY_FLAG);
			}
			if (null != request
					.getLocalParameter(TOKEN_ERROR_LOGIN)) {
				mTokenErrorLogin = (String) request
						.getLocalParameter(TOKEN_ERROR_LOGIN);
			}
			if (null != request
					.getLocalParameter(LOGIN_URL)) {
				mLogInURL = (String) request
						.getLocalParameter(LOGIN_URL);			
			}
			jsonString = ARRAY_SEPARETOR_START + QUOTE;
			
			if(null != registryType){
				 jsonString+=  REGISTRY_TYPE + QUOTE + ":" + QUOTE + registryType + QUOTE + COMMA + QUOTE;
				}
			else
			{
				jsonString+=  REGISTRY_TYPE + QUOTE + ":" + QUOTE + "Wedding" + QUOTE + COMMA + QUOTE;
			}
				if(null!= mRecommenderURL){
					jsonString += RECOMMENDER_URL + QUOTE + ":" + QUOTE + mRecommenderURL + QUOTE  ;
					
				}
				if(null!= mTokenError){
					jsonString += COMMA + QUOTE + TOKEN_ERROR + QUOTE + ":" + QUOTE + mTokenError + QUOTE  ;
					
				}
				if(null!= mVisibility){
					jsonString += COMMA + QUOTE + VISIBILITY_FLAG + QUOTE + ":" + QUOTE + mVisibility + QUOTE  ;
					
				}
				if(null!= mTokenErrorLogin){
					jsonString +=  COMMA + QUOTE + TOKEN_ERROR_LOGIN + QUOTE + ":" + QUOTE + mTokenErrorLogin + QUOTE;
					
				}
				if(null!= mLogInURL){
					jsonString +=  COMMA + QUOTE + LOGIN_URL + QUOTE + ":" + QUOTE + mLogInURL + QUOTE; 
					
				}
				jsonString+= ARRAY_SEPARETOR;
			RecommendationLandingPageTemplateVO recommenderLandingPageTemplateVO = null;
			try {
				logDebug("Value of jsonstring is:" + jsonString);
				recommenderLandingPageTemplateVO = getContentTemplateManager()
						.getRegistrantContent(RECOMMENDER_TEMPLATE, jsonString);
			} catch (RepositoryException e) {
			

				logError(
							LogMessageFormatter.formatMessage(
									request,
									"RecommendERLandingPageTemplateDroplet|service()|RepositoryException",
									"catalog_1042"), e);
				
			}

			if (null != recommenderLandingPageTemplateVO) {
				
				logDebug("Received RecommenderLandingPageTemplateVO : "
							+ recommenderLandingPageTemplateVO.toString());
				
				request.setParameter(RECOMMENDER_VO,
						recommenderLandingPageTemplateVO);
				request.serviceParameter(RECOMMENDER_OUTPUT, request, response);
			} else {
				
				logDebug("Received RecommendationLandingPageTemplateVO as Null: ");
				
				request.serviceParameter(EMPTY, request, response);
			}
		} catch (BBBBusinessException be) {
			

				logError(
						LogMessageFormatter.formatMessage(
								request,
								"RecommendationLandingPageTemplateDroplet|service()|BBBBusinessException",
								"catalog_1042"), be);
			

			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM, request,
					response);
		} catch (BBBSystemException bs) {
			

				logError(
						LogMessageFormatter.formatMessage(
								request,
								"RecommendationLandingPageTemplateDroplet|service()|BBBSystemException",
								"catalog_1043"), bs);
			
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM, request,
					response);
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