/**
 * 
 */
package com.bbb.cms.manager;

import java.util.List;

import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.bbb.cms.PromoBoxLayoutVO;
import com.bbb.cms.RecommendationLandingPageTemplateVO;
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
public class RecommendationLandingPageTemplateManager extends BBBGenericService{
	
	
	//Defining the Constants
	private static final String RECOMMENDATION_DESCRIPTOR = "recommendationLanding";
	private static final String HOME_SITE = "site";
	private static final String RECOMMENDATION_LAYOUT = "promoBoxMid";
	private static final String RECOMMENDATION_PROMO_TIERL1 = "promoBoxTop";
	private static final String RECOMMENDATION_PROMO_TIERL2 = "promoBoxBottom";
	//Defining the Error Constants
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="6001";
	public static final String RETRIEVED_MORE_DATA_FROM_REPOSITORY="6002";
	public static final String RETRIEVED_NO_DATA_FROM_REPOSITORY="6003";
	public static final String SITE_ID_IS_NULL="6004";
	
	private BBBCatalogTools mCatalogTools;
	private LandingTemplateManager mLandingTemplateManager;
	private Repository mRecommendationLandingPageTemplate;
	

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
	/**
	 * @return the RecommendationLandingPageTemplate
	 */
	public Repository getRecommendationLandingPageTemplate() {
		return mRecommendationLandingPageTemplate;
	}
	/**
	 * @param pRecommendationLandingPageTemplate the RecommendationLandingPageTemplate to set
	 */
	public void setRecommendationLandingPageTemplate(Repository pRecommendationLandingPageTemplate) {
		mRecommendationLandingPageTemplate = pRecommendationLandingPageTemplate;
	}

	
	@SuppressWarnings({ "null", "unchecked" })
	/**
	 * This method would interact with RecommendationLandingPageTemplate repository and based on SiteId from Context
	 * @return RecommendationLandingPageTemplateVO
	 */
	public RecommendationLandingPageTemplateVO getRecommendationData(String pSiteId) throws BBBSystemException, BBBBusinessException{
		
		//Initializing the variables
		RepositoryView view = null;
		QueryBuilder queryBuilder;
		QueryExpression queryExpSiteId;
		QueryExpression querySiteId;
		RepositoryItem[] items = null;
		final Query queryRecommendationPage;
		final RecommendationLandingPageTemplateVO recommendationLandingPageTemplateVO = new RecommendationLandingPageTemplateVO();
		
		final String methodName = "getRecommendationData";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RECOMMENDATION_PAGE_DATA,
				methodName);
		

			logDebug("Entering  " + methodName);
				
		try {
			
			if (null != pSiteId && !(StringUtils.isEmpty(pSiteId))){ 
				
				view = getRecommendationLandingPageTemplate().getView(RECOMMENDATION_DESCRIPTOR);
		
				queryBuilder = view.getQueryBuilder();
				
				queryExpSiteId = queryBuilder.createPropertyQueryExpression(HOME_SITE);
				querySiteId = queryBuilder.createConstantQueryExpression(pSiteId);
				
	
				final Query[] queries = new Query[1];
				
				queries[0] = queryBuilder.createComparisonQuery(querySiteId,queryExpSiteId, QueryBuilder.EQUALS);
				queryRecommendationPage = queryBuilder.createAndQuery(queries);
				
				
				logDebug("RecommendationPage Query to retrieve data : "+queryRecommendationPage);
				
				//Executing the Query to retirve Recommendation Repository Items
				items = view.executeQuery(queryRecommendationPage);
				
				if(null !=items){
					
					if(items.length==1){
					
					for (RepositoryItem item : items) {
						
						//setting the SiteID
						recommendationLandingPageTemplateVO.setSiteId(pSiteId);
						
					
					//Calling LandingTemplateManager's setPromoTierLayOut method for getting content of PromoLayouts
						final List<RepositoryItem> promoTierLayOutFirst= (List<RepositoryItem>) item.getPropertyValue(RECOMMENDATION_PROMO_TIERL1);
						if(null!=promoTierLayOutFirst && !promoTierLayOutFirst.isEmpty()){
							final List<PromoBoxLayoutVO> promoTierLayOutFirstList = getLandingTemplateManager()
									.setPromoTierLayOut(promoTierLayOutFirst);
							/*recommendationLandingPageTemplateVO
							.setPromoTierLayout1(promoTierLayOutFirstList);
*/
						}else{
							
							logDebug("Promo Tier Layout1 is Null for" + pSiteId );
							
						}
						
						//Calling LandingTemplateManager's setPromoBox method for getting content of PromoBox1
						if(null!= item.getPropertyValue(RECOMMENDATION_LAYOUT)){
						final RepositoryItem promoTierLayOut1 = (RepositoryItem) item.getPropertyValue(RECOMMENDATION_LAYOUT);
						
							
							logDebug("promoTierLayOut  is not Null");
							
							/*final PromoBoxVO promoBoxVO1 = getLandingTemplateManager()
									.setPromoBoxVO(promoTierLayOut1);
							recommendationLandingPageTemplateVO.setPromoBoxFirst(promoBoxVO1);*/
						}else{
							
							logDebug("promoTierLayOut  is Null for" + pSiteId );
							
						}
						
						final List<RepositoryItem> promoTierLayOutSecond= (List<RepositoryItem>) item.getPropertyValue(RECOMMENDATION_PROMO_TIERL2);
						if(null!=promoTierLayOutSecond && !promoTierLayOutSecond.isEmpty()){
							final List<PromoBoxLayoutVO> promoTierLayOutSecondList = getLandingTemplateManager().setPromoTierLayOut(promoTierLayOutSecond);
							/*recommendationLandingPageTemplateVO
									.setPromoTierLayout2(promoTierLayOutSecondList);
							*/
						}else{
							
							logDebug("Promo Tier Layout2  is Null for" + pSiteId );
							
						}
      				}
					
					}else{
						
						logDebug(methodName+ "RecommendationPage Repository Items returned more than 1 item");
							
						throw new BBBBusinessException (RETRIEVED_MORE_DATA_FROM_REPOSITORY,RETRIEVED_MORE_DATA_FROM_REPOSITORY);
						
					}
				}else{
					
					logDebug(methodName+ "RecommendationPage Repository Items returned null");
						
					throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);
					
				}
			}else{
				
				
				logDebug("RecommendationLandingPageTemplateManager : SiteId is Null:"+ pSiteId);
				
				throw new BBBBusinessException (SITE_ID_IS_NULL,SITE_ID_IS_NULL);
			}
		 
			
		}catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(null, "RecommendationLandingPageTemplateManager.getRecommendationData() | RepositoryException ","catalog_1056"), e);
			
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.RECOMMENDATION_PAGE_DATA,
					methodName);
		}
		
		
		logDebug("Exiting  " + methodName);
		
		
		return recommendationLandingPageTemplateVO;
		
		
	}	
}
