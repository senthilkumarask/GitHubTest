/**
 * 
 */
package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.bbb.cms.CategoryContainerVO;
import com.bbb.cms.HomePageTemplateVO;
import com.bbb.cms.PromoBoxLayoutVO;
import com.bbb.cms.PromoBoxVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author iteggi
 *
 */
public class HomePageTemplateManager extends BBBGenericService{
	
	protected static final String SUB_CATEGORIES = "subCategories";
	protected static final String CATEGORY = "category";
	protected static final String CAT_CONTIANER = "catContianer";
	//Defining the Constants
	protected static final String HOMEPAGE_DESCRIPTOR = "homepage";
	protected static final String HOME_SITE = "site";
	protected static final String HOME_HERO_IMAGE = "heroImages";
	protected static final String PROD_TIER_TEMP_LAYOUT1 = "promoTierTempLayOut1";
	protected static final String PROD_TIER_TEMP_LAYOUT2 = "promoTierTempLayOut2";
	protected static final String REGISTRY_STATUS = "registryStatus";
	protected static final String FEATURED_PRODUCT = "featuredProduct";
	protected static final String SECONDARY_PRODUCTS = "secondaryfeaturedProducts";
	protected static final String HOME_PROMO_TIERL1 = "promoTierLayOut1";
	protected static final String HOME_PROMO_TIERL2 = "promoTierLayOut2";
	protected static final String HOME_PROMO_TIERL3 = "promoTierLayOut3";
	protected static final String HOME_POP_SEARCH = "homePopSearch";
	protected static final String JUST_FOR_YOU = "justForYouFlag";
	protected static final String CLEARANCE_DEALS = "clearanceDealsFlag";
	private static final String HOME_HERO_IMAGE_INTL = "heroImagesIntl";
	
	//Defining the Error Constants
	protected static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="6001";
	protected static final String RETRIEVED_MORE_DATA_FROM_REPOSITORY="6002";
	protected static final String RETRIEVED_NO_DATA_FROM_REPOSITORY="6003";
	protected static final String SITE_ID_IS_NULL="6004";
	
	private BBBCatalogTools mCatalogTools;
	private LandingTemplateManager mLandingTemplateManager;
	private Repository mHomePageTemplate;
	

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
	 * @return the homePageTemplate
	 */
	public Repository getHomePageTemplate() {
		return mHomePageTemplate;
	}
	/**
	 * @param pHomePageTemplate the homePageTemplate to set
	 */
	public void setHomePageTemplate(Repository pHomePageTemplate) {
		mHomePageTemplate = pHomePageTemplate;
	}

	
	@SuppressWarnings({ "null", "unchecked" })
	/**
	 * This method would interact with HomePageTemplate repository and based on SiteId from Context
	 * @return HomePageTemplateVO
	 */
	public HomePageTemplateVO getHomePageData(String pSiteId) throws BBBSystemException, BBBBusinessException{
		
		//Initializing the variables
		RepositoryView view = null;
		QueryBuilder queryBuilder;
		QueryExpression queryExpSiteId;
		QueryExpression querySiteId;
		RepositoryItem[] items = null;
		final Query queryHomePage;
		final HomePageTemplateVO homePageTemplateVO = new HomePageTemplateVO();
		
		final String methodName = "getHomePageData";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.HOME_PAGE_DATA,
				methodName);
		
		
			logDebug("Entering  " + methodName);
		
		
		try {
			
			if (null != pSiteId && !(StringUtils.isEmpty(pSiteId))){ 
				
				view = getHomePageTemplate().getView(HOMEPAGE_DESCRIPTOR);
				//System.out.println(view);
				queryBuilder = view.getQueryBuilder();
				
				queryExpSiteId = queryBuilder.createPropertyQueryExpression(HOME_SITE);
				querySiteId = queryBuilder.createConstantQueryExpression(pSiteId);
				
	
				final Query[] queries = new Query[1];
				
				queries[0] = queryBuilder.createComparisonQuery(querySiteId,queryExpSiteId, QueryBuilder.EQUALS);
				queryHomePage = queryBuilder.createAndQuery(queries);
				
				
					logDebug("HomePage Query to retrieve data : "+queryHomePage);
				
				//Executing the Query to retirve HomePage Repository Items
				items = view.executeQuery(queryHomePage);
				
				if(null !=items){
					
					if(items.length==1){
					
					for (RepositoryItem item : items) {
						
						//setting the SiteID
						homePageTemplateVO.setSiteId(pSiteId);
						
						//Calling LandingTemplateManager's setPromoBox method for getting content of heroImages
						
						if(null!=item.getPropertyValue(HOME_HERO_IMAGE)){
						final List<RepositoryItem> heroImage =(List<RepositoryItem>) item.getPropertyValue(HOME_HERO_IMAGE);
						
							
								logDebug("HeroImage  is not Null");
							
							final List<PromoBoxVO> heroImageList = getLandingTemplateManager()
									.setPromoBox(heroImage);
							
							homePageTemplateVO.setHeroImages(heroImageList);
						
						}else{
							
								logDebug("HeroImage  is Null for" + pSiteId );
							
							}
						
						//Calling LandingTemplateManager's setPromoBox method for getting content of PromoBox1
						if(null!= item.getPropertyValue(PROD_TIER_TEMP_LAYOUT1)){
						final RepositoryItem promoTierLayOut1 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT1);
						
						
								logDebug("promoTierLayOut1  is not Null");
							
							final PromoBoxVO promoBoxVO1 = getLandingTemplateManager()
									.setPromoBoxVO(promoTierLayOut1);
							homePageTemplateVO.setPromoBoxFirst(promoBoxVO1);
						}else{
							
								logDebug("promoTierLayOut1  is Null for" + pSiteId );
							
							}
						
						
						//Calling LandingTemplateManager's setPromoBox method for getting content of PromoBox2
						if(null!=item.getPropertyValue(PROD_TIER_TEMP_LAYOUT2)){
						final RepositoryItem promoTierLayOut2 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT2);
						
							
								logDebug("promoTierLayOut2  is not Null");
							
							final PromoBoxVO promoBoxVO2 = getLandingTemplateManager()
									.setPromoBoxVO(promoTierLayOut2);
							homePageTemplateVO.setPromoBoxSecond(promoBoxVO2);
						}else{
							
								logDebug("promoTierLayOut2  is Null for" + pSiteId );
							
							}
						
						//Setting content of RegistryStatus
						if(null!=item.getPropertyValue(REGISTRY_STATUS)){
						final String registryStatus= ((Integer) item.getPropertyValue(REGISTRY_STATUS)).toString();
						homePageTemplateVO.setRegistryStatus(registryStatus);
						}else{
							
								logDebug("Registry Status  is Null for" + pSiteId );
							
							}
						
						// BBBSL-4068 set Popular Search Items
						if (null != item.getPropertyValue(HOME_POP_SEARCH)){
				        	final List<String> homePopSearch = (List<String>) item.getPropertyValue(HOME_POP_SEARCH);
				        	if(!BBBUtility.isListEmpty(homePopSearch)) {
				        		
				        		logDebug("Home Page Popular Seach Term is not Null");
				        		
				        		homePageTemplateVO.setHomePopSearch(homePopSearch);
						}
						}else{
						
							logDebug("Home Page Popular Seach Term is Null for" + pSiteId );
							
						}
				          
						if (null != item.getPropertyValue(JUST_FOR_YOU)){
							final Boolean justForYouFlag = (Boolean) item.getPropertyValue(JUST_FOR_YOU);
							if(null != justForYouFlag) {
								
									logDebug("justForYouFlag  is not Null");
									
								homePageTemplateVO.setJustForYouFlag(justForYouFlag);
							}
						}else{
							
								logDebug("Just For You Flag  is Null for" + pSiteId );
							
							}
							
						if (null != item.getPropertyValue(CLEARANCE_DEALS)){
							final Boolean clearanceDealsFlag = (Boolean) item.getPropertyValue(CLEARANCE_DEALS);
							if(null != clearanceDealsFlag) {
								
									logDebug("clearanceDealsFlag  is not Null");
									
								homePageTemplateVO.setClearanceDealsFlag(clearanceDealsFlag);
							}
						}else{
							
								logDebug("Clearance Deals  is Null for" + pSiteId );
							
							}
						
						
							logDebug("Calling the LandingTemplate Manager for PromoLayouts");
						
						
						//Calling LandingTemplateManager's setPromoTierLayOut method for getting content of PromoLayouts
						final List<RepositoryItem> promoTierLayOutFirst= (List<RepositoryItem>) item.getPropertyValue(HOME_PROMO_TIERL1);
						if(null!=promoTierLayOutFirst && !promoTierLayOutFirst.isEmpty()){
							final List<PromoBoxLayoutVO> promoTierLayOutFirstList = getLandingTemplateManager()
									.setPromoTierLayOut(promoTierLayOutFirst);
							homePageTemplateVO
							.setPromoTierLayout1(promoTierLayOutFirstList);

						}else{
							
								logDebug("Promo Tier Layout1 is Null for" + pSiteId );
							
							}
						final List<RepositoryItem> promoTierLayOutSecond= (List<RepositoryItem>) item.getPropertyValue(HOME_PROMO_TIERL2);
						if(null!=promoTierLayOutSecond && !promoTierLayOutSecond.isEmpty()){
							final List<PromoBoxLayoutVO> promoTierLayOutSecondList = this.setPromoTierLayOut(promoTierLayOutSecond);
							homePageTemplateVO
									.setPromoTierLayout2(promoTierLayOutSecondList);
							
						}else{
							
								logDebug("Promo Tier Layout2  is Null for" + pSiteId );
							
							}
						final List<RepositoryItem> promoTierLayOutThird= (List<RepositoryItem>) item.getPropertyValue(HOME_PROMO_TIERL3);
						if(null!=promoTierLayOutThird && !promoTierLayOutThird.isEmpty()){
							final List<PromoBoxLayoutVO> promoTierLayOutThirdList = getLandingTemplateManager()
									.setPromoTierLayOut(promoTierLayOutThird);
							homePageTemplateVO
									.setPromoTierLayout3(promoTierLayOutThirdList);
						}else{
							
								logDebug("Promo Tier Layout3  is Null for" + pSiteId );
							
							}
						
						//Setting the Category and SubCategories
						final List<RepositoryItem> categoryContainer = new ArrayList<RepositoryItem>();
						if(null != item.getPropertyValue(CAT_CONTIANER)){
							categoryContainer.addAll((List<RepositoryItem>)item.getPropertyValue(CAT_CONTIANER));
							 List<CategoryContainerVO> categoryContainerList=new ArrayList<CategoryContainerVO>();
							 for (RepositoryItem categoryContainerItem : categoryContainer) {
								 CategoryContainerVO categoryContainerElement=new CategoryContainerVO();
								 
								 if(null!=categoryContainerItem.getPropertyValue(CATEGORY)){
									 
								 categoryContainerElement.setCategoryId(((RepositoryItem)categoryContainerItem.getPropertyValue(CATEGORY)).getRepositoryId());
								 
								 final List<RepositoryItem> subCategoriesItem = new ArrayList<RepositoryItem>();
								 
								 if(null!= categoryContainerItem.getPropertyValue(SUB_CATEGORIES)){
									 subCategoriesItem.addAll((List<RepositoryItem>)categoryContainerItem.getPropertyValue(SUB_CATEGORIES));
									 List<String> subCategoryIds= new ArrayList<String>();
									 for (RepositoryItem subCategoryItem : subCategoriesItem) {
										 subCategoryIds.add(subCategoryItem.getRepositoryId());
									 }
									 categoryContainerElement.setSubCategories(subCategoryIds);
									 
									 }
								 categoryContainerList.add(categoryContainerElement);
								 }else{
										
											logDebug("Main Category is not defined for Category Contianer");
										
										}
									}
							 homePageTemplateVO.setCategoryContainer(categoryContainerList);
							
						}else{
							
								logDebug("Popular Categories  is Null for" + pSiteId );
							
							}
												
					}
					
					}else{
						
							logDebug(methodName+ "HomePage Repository Items returned more than 1 item");
							
						throw new BBBBusinessException (RETRIEVED_MORE_DATA_FROM_REPOSITORY,RETRIEVED_MORE_DATA_FROM_REPOSITORY);
						
					}
				}else{
					
						logDebug(methodName+ "HomePage Repository Items returned null");
						
					throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);
					
				}
			}else{
				
				
					logDebug("HomePageTemplateManager : SiteId is Null:"+ pSiteId);
				
				throw new BBBBusinessException (SITE_ID_IS_NULL,SITE_ID_IS_NULL);
			}
		 
			
		}catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(null, "BBBHomePageTemplateManager.getHomePageData() | RepositoryException ","catalog_1056"), e);
			
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.HOME_PAGE_DATA,
					methodName);
		}
		
		
			logDebug("Exiting  " + methodName);
		
		
		return homePageTemplateVO;
		
		
	}
	
	@SuppressWarnings({ "null", "unchecked" })
	/**
	 * This method would interact with HomePageTemplate repository and based on SiteId from Context and will fetch
	 * the products
	 * @return HomePageTemplateVO
	 */
	public HomePageTemplateVO getHomePageProducts(String pSiteId) throws BBBSystemException, BBBBusinessException{
		HomePageTemplateVO homePageTemplateVO = new HomePageTemplateVO();
		final String methodName = "getHomePageProducts()";
		try {
			if (null != pSiteId && !(StringUtils.isEmpty(pSiteId))){ 
				
				RepositoryView view = getHomePageTemplate().getView(HOMEPAGE_DESCRIPTOR);
				//System.out.println(view);
				QueryBuilder queryBuilder = view.getQueryBuilder();
				
				QueryExpression queryExpSiteId = queryBuilder.createPropertyQueryExpression(HOME_SITE);
				QueryExpression querySiteId = queryBuilder.createConstantQueryExpression(pSiteId);
				
	
				final Query[] queries = new Query[1];
				
				queries[0] = queryBuilder.createComparisonQuery(querySiteId,queryExpSiteId, QueryBuilder.EQUALS);
				Query queryHomePage = queryBuilder.createAndQuery(queries);
				
				
					logDebug("HomePage Query to retrieve data : "+queryHomePage);
				
				//Executing the Query to retirve HomePage Repository Items
				RepositoryItem[] items = view.executeQuery(queryHomePage);
				
				if(null !=items){
					
					if(items.length==1){
					
					for (RepositoryItem item : items) {
						
						// featured product
						if (!BBBUtility.isEmpty((String)item.getPropertyValue(FEATURED_PRODUCT))) {
							//Calling BBBCatalogTools's getProductDetails for getting content of Featured Product
							final String featuredProduct = ((String) item.getPropertyValue(FEATURED_PRODUCT));
							logDebug("featuredProduct " + featuredProduct);
							final String featuredProducts [] = featuredProduct.split(BBBCoreConstants.COMMA);
									for(String productId: featuredProducts){
											try{
												logDebug("creating featured product VO for productId" + productId);
												final ProductVO featuredProductVO = getCatalogTools().getProductDetails(pSiteId,productId,true);
												homePageTemplateVO.setFeaturedProduct(featuredProductVO);
												logDebug("featuredProduct  is set");
												break;
											}catch(BBBBusinessException bse){
												logError("HomePateTemplateManager:In method getHomePageProducts product " + productId +"is disabled");
											}	
									}
						}else{
							
								logDebug("Featured Product is Null for" + pSiteId );
							
							}
						
						//Calling BBBCatalogTools's getProductDetails for getting content of Secondary Featured Products
						final List<RepositoryItem> secondaryProducts = new ArrayList<RepositoryItem>();
						if(null!= item.getPropertyValue(SECONDARY_PRODUCTS)){
						secondaryProducts.addAll((Set<RepositoryItem>) item.getPropertyValue(SECONDARY_PRODUCTS));
						if ( null!=secondaryProducts ) {
							
								logDebug("Calling the LandingTemplate Manager for Secondary Product");
							
							final List<ProductVO> secondaryProductsList = getLandingTemplateManager()
									.setProduct(pSiteId, secondaryProducts);
							homePageTemplateVO
									.setSecondaryProducts(secondaryProductsList);
						}
						}else{
							
								logDebug("Secondary Products  is Null for" + pSiteId );
							
							}
						}
					
					}else{
						
							logDebug(methodName+ "HomePage Repository Items returned more than 1 item");
							
						throw new BBBBusinessException (RETRIEVED_MORE_DATA_FROM_REPOSITORY,RETRIEVED_MORE_DATA_FROM_REPOSITORY);
						
					}
				}else{
					
						logDebug(methodName+ "HomePage Repository Items returned null");
						
					throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);
					
				}
			}else{
				
					logDebug("HomePageTemplateManager : SiteId is Null:"+ pSiteId);
				
				throw new BBBBusinessException (SITE_ID_IS_NULL,SITE_ID_IS_NULL);
			}
		 
			
		}catch (RepositoryException e) {
		
			logError(LogMessageFormatter.formatMessage(null, "BBBHomePageTemplateManager.getHomePageProducts() | RepositoryException ","catalog_1057"), e);
			
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}
		return homePageTemplateVO;
	}
	
	 public List<PromoBoxLayoutVO> setPromoTierLayOut(List<RepositoryItem> pPromoTierLayOut){

		    List<PromoBoxLayoutVO> promoTierLayOutList=new ArrayList<PromoBoxLayoutVO>();

		      logDebug("LandingTemplateManager.setPromoTierLayOut() Method Entering");


		    for (RepositoryItem item : pPromoTierLayOut) {

		      PromoBoxLayoutVO promoBoxLayoutVO = new PromoBoxLayoutVO();
		      RepositoryItem promoTierLayOut1 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT1);

		      PromoBoxVO promoBoxVO1=getLandingTemplateManager().setPromoBoxVO(promoTierLayOut1);

		      promoBoxLayoutVO.setPromoBoxFirstVOList(promoBoxVO1);

		      RepositoryItem promoTierLayOut2 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT2);
		      PromoBoxVO promoBoxVO2=getLandingTemplateManager().setPromoBoxVO(promoTierLayOut2);

		      promoBoxLayoutVO.setPromoBoxSecondVOList(promoBoxVO2);


		      promoTierLayOutList.add(promoBoxLayoutVO);
		    }

		   
		      logDebug("LandingTemplateManager.setPromoTierLayOut() Method Ending");


		    return promoTierLayOutList;
		  }
	 
	 @SuppressWarnings({ "null", "unchecked" })
		/**
		 * This method would interact with HomePageTemplate repository 
		 * and based on SiteId from Context fetch hero images for international 
		 * customers and set in to the HomePageTemplateVO
		 * 
		 * @param siteId in <code>String</code> format
		 * @throws BBBSystemException
		 * @throws BBBBusinessException
		 * @return HomePageTemplateVO
		 */
		public HomePageTemplateVO getHeroImagesForIntlCustomer(final String pSiteId)
				throws BBBSystemException, BBBBusinessException {
	 
			// Initializing the variables
			RepositoryView view = null;
			QueryBuilder queryBuilder;
			QueryExpression queryExpSiteId;
			QueryExpression querySiteId;
			RepositoryItem[] items = null;
			final Query queryHomePage;
			final HomePageTemplateVO homePageTemplateVO = new HomePageTemplateVO();
	
			final String methodName = "getHeroImagesForIntlCustomer";
			BBBPerformanceMonitor.start(BBBPerformanceConstants.HOME_PAGE_DATA,
					methodName);
			this.logDebug("Entering into  method " + methodName);

			try {

				if(BBBUtility.isEmpty(pSiteId)){
					logDebug(" HomePageTemplateManager:"+methodName
							+ "HomePageTemplateManager : SiteId is Null:" + pSiteId);
					throw new BBBBusinessException(SITE_ID_IS_NULL, SITE_ID_IS_NULL);
}

				view = getHomePageTemplate().getView(HOMEPAGE_DESCRIPTOR);
				queryBuilder = view.getQueryBuilder();
				queryExpSiteId = queryBuilder.createPropertyQueryExpression(HOME_SITE);
				querySiteId = queryBuilder.createConstantQueryExpression(pSiteId);

				final Query[] queries = new Query[1];
				queries[0] = queryBuilder.createComparisonQuery(querySiteId,
						queryExpSiteId, QueryBuilder.EQUALS);
				queryHomePage = queryBuilder.createAndQuery(queries);

				logDebug(" HomePageTemplateManager:"+methodName
						+ "HomePage Query to retrieve data : " + queryHomePage);
				
				// Executing the Query to retirve HomePage Repository Items
				items = view.executeQuery(queryHomePage);

				if(null != items && items.length == 1) {
					for (RepositoryItem item : items) {
						// setting the SiteID
						homePageTemplateVO.setSiteId(pSiteId);
						// Calling LandingTemplateManager's setPromoBox
						// method for getting content of heroImages

						if (null != item.getPropertyValue(HOME_HERO_IMAGE_INTL)) {
							final List<RepositoryItem> heroImage = (List<RepositoryItem>) item
									.getPropertyValue(HOME_HERO_IMAGE_INTL);

							logDebug(" HomePageTemplateManager:"+methodName
									+ "HomePageTemplateManager:HomePageTemplateVO() HeroImage  is not Null");
							final List<PromoBoxVO> heroImageList = getLandingTemplateManager()
									.setPromoBox(heroImage);

							homePageTemplateVO.setHeroImages(heroImageList);

						} else {
							logDebug(" HomePageTemplateManager:getHeroImagesForIntlCustomer"
									+ "HeroImage for International customer   is Null for " + pSiteId);
						}
					}
				} else {

					logDebug(" HomePageTemplateManager:"+methodName + 
							"HomePage Repository Items returned null or more then 1 item");
					throw new BBBSystemException(RETRIEVED_NO_DATA_FROM_REPOSITORY,
							RETRIEVED_NO_DATA_FROM_REPOSITORY);

				}

			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null,
								"BBBHomePageTemplateManager.getHomeImagesForIntlCustomer() | RepositoryException ",
								"catalog_1056"), e);
				throw new BBBSystemException(
						UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,
						UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
			} finally {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.HOME_PAGE_DATA, methodName);
			}

			this.logDebug("Exiting from method " + methodName);
			return homePageTemplateVO;
		}

	 
	 
	
}
