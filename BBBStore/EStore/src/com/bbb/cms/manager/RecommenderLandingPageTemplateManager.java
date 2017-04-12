/**
 * 
 */
package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.List;

import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;


import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.RecommenderLandingPageTemplateVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * @author Amit
 *
 */
public class RecommenderLandingPageTemplateManager extends BBBGenericService{
	
	
	//Defining the Constants
	private static final String RECOMMENDER_DESCRIPTOR = "recommenderLanding";
	private static final String HOME_SITE = "site";
	private static final String CHANNEL = "channel";
	private static final String REGISTRY_TYPE = "registryType";
	private static final String RECOMMENDER_TOP = "upperPromoBoxSlot";
	private static final String RECOMMENDER_BOTTOM = "bottomPromoBoxSlot";
	private static final String RECOMMENDER_MIDDLE_LIST = "middlePromoBoxSlot";
	
	//Defining the Error Constants
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="6001";
	public static final String RETRIEVED_MORE_DATA_FROM_REPOSITORY="6002";
	public static final String RETRIEVED_NO_DATA_FROM_REPOSITORY="6003";
	public static final String SITE_ID_IS_NULL="6004";
	
	private BBBCatalogTools mCatalogTools;
	private LandingTemplateManager mLandingTemplateManager;
	private Repository mRecommenderLandingPageTemplate;
	

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}
	/**
	 * @return the landingTemplateManager
	 */
	public LandingTemplateManager getLandingTemplateManager() {
		return mLandingTemplateManager;
	}
	/**
	 * @param pLandingTemplateManager the landingTemplateManager to set
	 */
	public void setLandingTemplateManager(
			LandingTemplateManager pLandingTemplateManager) {
		mLandingTemplateManager = pLandingTemplateManager;
	}
	

	
	@SuppressWarnings({ "null", "unchecked" })
	/**
	 * This method would interact with RecommendationLandingPageTemplate repository and based on SiteId from Context
	 * @return RecommendationLandingPageTemplateVO
	 */
	public RecommenderLandingPageTemplateVO getRecommenderData(String pSiteId,String pChannel,String pRegistryType) throws BBBSystemException, BBBBusinessException{
		
		//Initializing the variables
		RepositoryView view = null;
		QueryBuilder queryBuilder;
		QueryExpression queryExpSiteId;
		QueryExpression querySiteId;
		QueryExpression queryExpChannel = null;
		QueryExpression queryChannel = null;
		QueryExpression queryExpRegistryType =null;
		QueryExpression queryRegistryType =null;
		RepositoryItem[] items = null;
		final Query queryRecommenderPage;
		final RecommenderLandingPageTemplateVO recommenderLandingPageTemplateVO = new RecommenderLandingPageTemplateVO();
		
		final String methodName = "getRecommendationData";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RECOMMENDATION_PAGE_DATA,
				methodName);
		
		logDebug("Entering  " + methodName);
		
		
		try {
			
			if (null != pSiteId && !(StringUtils.isEmpty(pSiteId))){ 
				
				view = getRecommenderLandingPageTemplate().getView(RECOMMENDER_DESCRIPTOR);
		
				queryBuilder = view.getQueryBuilder();
				
				queryExpSiteId = queryBuilder.createPropertyQueryExpression(HOME_SITE);
				querySiteId = queryBuilder.createConstantQueryExpression(pSiteId);
				
				if (null != pChannel && !(StringUtils.isEmpty(pChannel))){
					queryExpChannel = queryBuilder.createPropertyQueryExpression(CHANNEL);
					queryChannel = queryBuilder.createConstantQueryExpression(pChannel);
				}
				
				if (null != pRegistryType && !(StringUtils.isEmpty(pRegistryType))){
					queryExpRegistryType = queryBuilder.createPropertyQueryExpression(REGISTRY_TYPE);
					queryRegistryType = queryBuilder.createConstantQueryExpression(pRegistryType);
				}
	
				final Query[] queries = new Query[3];
				
				queries[0] = queryBuilder.createComparisonQuery(querySiteId,queryExpSiteId,QueryBuilder.EQUALS);
				queries[1] = queryBuilder.createComparisonQuery(queryChannel,queryExpChannel,QueryBuilder.EQUALS);
				queries[2] = queryBuilder.createComparisonQuery(queryRegistryType,queryExpRegistryType,QueryBuilder.EQUALS);
				queryRecommenderPage = queryBuilder.createAndQuery(queries);
				
				logDebug("RecommendationPage Query to retrieve data : "+queryRecommenderPage);
				
				//Executing the Query to retirve Recommendation Repository Items
				items = view.executeQuery(queryRecommenderPage);
				
				if(null !=items){
					
					if(items.length==1){
					
					for (RepositoryItem item : items) {
						
						//setting the SiteID
						recommenderLandingPageTemplateVO.setSiteId(pSiteId);
						recommenderLandingPageTemplateVO.setRegistryType(pRegistryType);
						recommenderLandingPageTemplateVO.setChannel(pChannel);
					
					//Calling LandingTemplateManager's setPromoBox method for getting content of  Upper PromoBox
											
						if(null!= item.getPropertyValue(RECOMMENDER_TOP)){
						final RepositoryItem promoBoxTop = (RepositoryItem) item.getPropertyValue(RECOMMENDER_TOP);
						
							
							logDebug("promoBoxTop  is not Null");
							
							final PromoBoxVO promoBoxVO1 = getLandingTemplateManager()
									.setPromoBoxVO(promoBoxTop);
							recommenderLandingPageTemplateVO.setPromoBox(promoBoxVO1);
						}else{
							
							logDebug("Upper promoBox  is Null for" + pSiteId );
							
						}

						//Calling LandingTemplateManager's setPromoBox method for getting content of Bottom PromoBox
						final List<RepositoryItem> promoBoxMiddle= (List<RepositoryItem>) item.getPropertyValue(RECOMMENDER_MIDDLE_LIST);
						 List<PromoBoxVO> promoBoxMiddleList = new ArrayList<PromoBoxVO>();
						if(null!=promoBoxMiddle && !promoBoxMiddle.isEmpty()){							
							recommenderLandingPageTemplateVO.setPromoBoxList(getLandingTemplateManager().setPromoBox(promoBoxMiddle));
							
						}else{
							
							logDebug("Promo Tier Middle  is Null for" + pSiteId );
							
						}
						
						
						if(null!= item.getPropertyValue(RECOMMENDER_BOTTOM)){
						final RepositoryItem promoBoxTop = (RepositoryItem) item.getPropertyValue(RECOMMENDER_BOTTOM);
						
						
						logDebug("promoBoxBottom  is not Null");
						
						final PromoBoxVO promoBoxVO2 = getLandingTemplateManager()
								.setPromoBoxVO(promoBoxTop);
						recommenderLandingPageTemplateVO.setPromoBoxBottom(promoBoxVO2);
					}else{
						
						logDebug("Upper promoBox  is Null for" + pSiteId );
						
					}

      				}
					
					}else{
						
						logDebug(methodName+ "RecommendationPage Repository Items returned more than 1 item");
							
						throw new BBBBusinessException (RETRIEVED_MORE_DATA_FROM_REPOSITORY,RETRIEVED_MORE_DATA_FROM_REPOSITORY);
						
					}
				}else{
					
					logDebug(methodName+ "RecommenderPage Repository Items returned null");
						
					throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);
					
				}
			}else{
							
					logDebug("RecommenderLandingPageTemplateManager : SiteId is Null:"+ pSiteId);
				
				throw new BBBBusinessException (SITE_ID_IS_NULL,SITE_ID_IS_NULL);
			}
		 
			
		}catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(null, "RecommenderLandingPageTemplateManager.getRecommendationData() | RepositoryException ","catalog_1056"), e);
			
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.RECOMMENDER_PAGE_DATA,
					methodName);
		}
		
	
			logDebug("Exiting  " + methodName);
		
		
		return recommenderLandingPageTemplateVO;
		
		
	}
	public Repository getRecommenderLandingPageTemplate() {
		return mRecommenderLandingPageTemplate;
	}
	public void setRecommenderLandingPageTemplate(
			Repository pRecommenderLandingPageTemplate) {
		mRecommenderLandingPageTemplate = pRecommenderLandingPageTemplate;
	}	
}
