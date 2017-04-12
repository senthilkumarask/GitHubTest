package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.cms.CategoryContainerVO;
import com.bbb.cms.HomePageTemplateVO;
import com.bbb.cms.PromoBoxLayoutVO;
import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.PromoImageVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

public class TBSHomePageTemplateManager extends HomePageTemplateManager {
	
	protected static final String FEATURED_CAT_CONTAINER = "featuredCatContainer";
	protected static final String LOGO_IMAGE = "logoImage";
	protected static final String USE_ANNOUNCEMENT_FROMWEB = "useAnnouncementFromWeb";
	protected static final String ANNOUNCEMENT = "announcement";
	protected static final String USE_POPULAR_CATEGORIES_FROMWEB = "usePopularCategoriesFromWeb";
	protected static final String USE_FEATURED_CATEGORIES_FROMWEB = "useFeaturedCategoriesFromWeb";
	protected static final String USE_PROMO_REGISTRYPANEL_FROMWEB = "usePromoRegistryPanelFromWeb";
	protected static final String USE_PROMOTIER_LAYOUT1_FROMWEB = "usePromoTierLayout1FromWeb";
	
	private Map<String,String> mOverrideSiteMap;
	private LandingTemplateManager mLandingTemplateManager;
	
	/**
	 * @return the overrideSiteMap
	 */
	public Map<String, String> getOverrideSiteMap() {
		return mOverrideSiteMap;
	}
	/**
	 * @param pOverrideSiteMap the overrideSiteMap to set
	 */
	public void setOverrideSiteMap(Map<String, String> pOverrideSiteMap) {
		this.mOverrideSiteMap = pOverrideSiteMap;
	}
	/**
	 * @return the landingTemplateManager
	 */
	public LandingTemplateManager getLandingTemplateManager() {
		return mLandingTemplateManager;
	}
	/**
	 * @param pTBSLandingTemplateManager the landingTemplateManager to set
	 */

	public void setLandingTemplateManager(LandingTemplateManager pLandingTemplateManager) {
		mLandingTemplateManager = pLandingTemplateManager;
	}
	
	/**
	 * This method would return the overrideItem based on SiteId from Context
	 * @return overrideItem
	 */
	public RepositoryItem getOverrideItem(String pSiteId) throws BBBSystemException, BBBBusinessException{
		
		RepositoryItem overrideItem=null;
		String overrideSiteId = null;
		if (getOverrideSiteMap()!=null){
			overrideSiteId = getOverrideSiteMap().get(pSiteId);
		}
		vlogDebug ("overrideSiteId=="+overrideSiteId);
		try{
			if (overrideSiteId!=null){
				//Initializing the variables
				RepositoryView view = null;
				QueryBuilder queryBuilder;
				QueryExpression queryExpSiteId;
				QueryExpression querySiteId;
				RepositoryItem[] items = null;
				final Query queryHomePage;
				view = getHomePageTemplate().getView(HOMEPAGE_DESCRIPTOR);

				queryBuilder = view.getQueryBuilder();
				queryExpSiteId = queryBuilder.createPropertyQueryExpression(HOME_SITE);
				querySiteId = queryBuilder.createConstantQueryExpression(overrideSiteId);
				final Query[] queries = new Query[1];
				queries[0] = queryBuilder.createComparisonQuery(querySiteId,queryExpSiteId, QueryBuilder.EQUALS);
				queryHomePage = queryBuilder.createAndQuery(queries);
				
				vlogDebug("HomePage Query to retrieve data : "+queryHomePage);
				//Executing the Query to retirve HomePage Repository Items
				items = view.executeQuery(queryHomePage);
				
				if(null !=items && items.length==1){
					overrideItem = items[0];
				}
			}
		}catch (RepositoryException e) {
			vlogError(LogMessageFormatter.formatMessage(null, "BBBHomePageTemplateManager.getOverrideItem() | RepositoryException ","catalog_1056"), e);
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}
		vlogDebug ("overrideItem=="+overrideItem);
		return overrideItem;
	}
	
	@SuppressWarnings("unchecked")
	private List<CategoryContainerVO> getCategoryContainer(String pSiteId, RepositoryItem pItem, String propertyName){
		//Setting the Category and SubCategories
		final List<RepositoryItem> categoryContainer = new ArrayList<RepositoryItem>();
		List<CategoryContainerVO> categoryContainerList = null;
		if(null != pItem.getPropertyValue(propertyName)){
			categoryContainer.addAll((List<RepositoryItem>)pItem.getPropertyValue(propertyName));
			 categoryContainerList=new ArrayList<CategoryContainerVO>();
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
					vlogDebug("Main Category is not defined for Category Contianer");
				}
			 } 
		}
		return categoryContainerList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
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
		BBBPerformanceMonitor.start(BBBPerformanceConstants.HOME_PAGE_DATA,	methodName);
		vlogDebug("Entering  " + methodName);
		
		try {
			if (null != pSiteId && !(StringUtils.isEmpty(pSiteId))){ 
				view = getHomePageTemplate().getView(HOMEPAGE_DESCRIPTOR);
				queryBuilder = view.getQueryBuilder();
				
				queryExpSiteId = queryBuilder.createPropertyQueryExpression(HOME_SITE);
				querySiteId = queryBuilder.createConstantQueryExpression(pSiteId);
				final Query[] queries = new Query[1];
				queries[0] = queryBuilder.createComparisonQuery(querySiteId,queryExpSiteId, QueryBuilder.EQUALS);
				queryHomePage = queryBuilder.createAndQuery(queries);
				vlogDebug("HomePage Query to retrieve data : "+queryHomePage);
				//Executing the Query to retirve HomePage Repository Items
				items = view.executeQuery(queryHomePage);
				RepositoryItem overrideItem = (RepositoryItem) getOverrideItem(pSiteId);
				
				if(null !=items){
						if(items.length==1){
						for (RepositoryItem item : items) {
							//setting the SiteID
							homePageTemplateVO.setSiteId(pSiteId);
							vlogDebug("Site Id  is:" + pSiteId );
							//Calling LandingTemplateManager's setPromoBox method for getting content of heroImages
							
							if(null!=item.getPropertyValue(LOGO_IMAGE)){
								final RepositoryItem logoImage =(RepositoryItem) item.getPropertyValue(LOGO_IMAGE);
								vlogDebug("LogoImage  is not Null");
								final PromoImageVO promoImageVO = getLandingTemplateManager().setPromoImage(logoImage);
								vlogDebug("LogoImage VO:"+promoImageVO.toString());
								homePageTemplateVO.setLogoImage(promoImageVO);
							}else{
								vlogDebug("LogoImage  is Null for:" + pSiteId );
							}
							
							//this method is used for getting the web announcements
							getAnnouncements(pSiteId, homePageTemplateVO, overrideItem, item);
							
							if(null!=item.getPropertyValue(HOME_HERO_IMAGE)){
								final List<RepositoryItem> heroImage =(List<RepositoryItem>) item.getPropertyValue(HOME_HERO_IMAGE);
								vlogDebug("HeroImage  is not Null");
								final List<PromoBoxVO> heroImageList = getLandingTemplateManager().setPromoBox(heroImage);
								homePageTemplateVO.setHeroImages(heroImageList);
							}else{
								vlogDebug("HeroImage  is Null for" + pSiteId );
							}
							
							//Calling LandingTemplateManager's setPromoBox method for getting content of PromoBox1
							final Boolean usePromoRegistryPanelFromWeb = (Boolean) item.getPropertyValue(USE_PROMO_REGISTRYPANEL_FROMWEB);
							vlogDebug("usePromoRegistryPanelFromWeb  is :"+usePromoRegistryPanelFromWeb);
							
							getTierLayoutInfo(pSiteId, homePageTemplateVO, overrideItem, item, usePromoRegistryPanelFromWeb);
							
							//registry status
							registryStatus(pSiteId, homePageTemplateVO, overrideItem, item, usePromoRegistryPanelFromWeb);
							
							populateJustForYouInfo(pSiteId, homePageTemplateVO, item);
								
							populateClearanceInfo(pSiteId, homePageTemplateVO, item);
							
							vlogDebug("Calling the LandingTemplate Manager for PromoLayouts");
							
							//Calling LandingTemplateManager's setPromoTierLayOut method for getting content of PromoLayouts
							final Boolean usePromoTierLayout1FromWeb = (Boolean) item.getPropertyValue(USE_PROMOTIER_LAYOUT1_FROMWEB);
							getPromoLayoutInfo(pSiteId, homePageTemplateVO, overrideItem, item, usePromoTierLayout1FromWeb);
							
							populateHomePromoTiers(pSiteId, homePageTemplateVO, item);
							
							//getting populer categories
							getPopulerCategories(pSiteId, homePageTemplateVO, overrideItem, item);
							//getting featured categories
							getFeaturedCategories(pSiteId, homePageTemplateVO, overrideItem, item);
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
				vlogDebug("HomePageTemplateManager : SiteId is Null:"+ pSiteId);
				throw new BBBBusinessException (SITE_ID_IS_NULL,SITE_ID_IS_NULL);
			}
		}catch (RepositoryException e) {
			vlogError(LogMessageFormatter.formatMessage(null, "BBBHomePageTemplateManager.getHomePageData() | RepositoryException ","catalog_1056"), e);
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.HOME_PAGE_DATA, methodName);
		}
		vlogDebug("Exiting  " + methodName);
		return homePageTemplateVO;
	}
	
	/**
	 * This is used to populate the home promo tier info
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param item
	 */
	@SuppressWarnings("unchecked")
	private void populateHomePromoTiers(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem item) {
		final List<RepositoryItem> promoTierLayOutSecond= (List<RepositoryItem>) item.getPropertyValue(HOME_PROMO_TIERL2);
		if(null!=promoTierLayOutSecond && !promoTierLayOutSecond.isEmpty()){
			final List<PromoBoxLayoutVO> promoTierLayOutSecondList = this.setPromoTierLayOut(promoTierLayOutSecond);
			homePageTemplateVO.setPromoTierLayout2(promoTierLayOutSecondList);
			
		}else{
			vlogDebug("Promo Tier Layout2  is Null for " + pSiteId );
		}
		
		final List<RepositoryItem> promoTierLayOutThird= (List<RepositoryItem>) item.getPropertyValue(HOME_PROMO_TIERL3);
		if(null!=promoTierLayOutThird && !promoTierLayOutThird.isEmpty()){
			final List<PromoBoxLayoutVO> promoTierLayOutThirdList = getLandingTemplateManager()
					.setPromoTierLayOut(promoTierLayOutThird);
			homePageTemplateVO.setPromoTierLayout3(promoTierLayOutThirdList);
		}else{
			vlogDebug("Promo Tier Layout3  is Null for " + pSiteId );
		}
	}
	
	/**
	 * This is used to populate the clearance data info
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param item
	 */
	private void populateClearanceInfo(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem item) {
		
		if (null != item.getPropertyValue(CLEARANCE_DEALS)){
			final Boolean clearanceDealsFlag = (Boolean) item.getPropertyValue(CLEARANCE_DEALS);
			if(null != clearanceDealsFlag) {
				vlogDebug("clearanceDealsFlag  is not Null");
				homePageTemplateVO.setClearanceDealsFlag(clearanceDealsFlag);
			}
		}else{
			vlogDebug("Clearance Deals  is Null for" + pSiteId );
		}
	}
	
	/**
	 * This method is used to populate the just for you info
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param item
	 */
	private void populateJustForYouInfo(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem item) {
		if (null != item.getPropertyValue(JUST_FOR_YOU)){
			final Boolean justForYouFlag = (Boolean) item.getPropertyValue(JUST_FOR_YOU);
			if(null != justForYouFlag) {
				vlogDebug("justForYouFlag  is not Null");
				homePageTemplateVO.setJustForYouFlag(justForYouFlag);
			}
		}else{
			vlogDebug("Just For You Flag  is Null for" + pSiteId );
		}
	}
	
	/**
	 * this method is used to populate the tier layout info
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param overrideItem
	 * @param item
	 * @param usePromoRegistryPanelFromWeb
	 */
	private void getTierLayoutInfo(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem overrideItem, RepositoryItem item,
			Boolean usePromoRegistryPanelFromWeb) {
		
		if (usePromoRegistryPanelFromWeb && null != overrideItem){
			if(null!= overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT1)){
				final RepositoryItem promoTierLayOut1 = (RepositoryItem) overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT1);
				
					vlogDebug("overrideItem promoTierLayOut1  is not Null:"+promoTierLayOut1);
					final PromoBoxVO promoBoxVO1 = getLandingTemplateManager().setPromoBoxVO(promoTierLayOut1);
					homePageTemplateVO.setPromoBoxFirst(promoBoxVO1);
				}else{
					vlogDebug("overrideItem promoTierLayOut1  is Null for" + pSiteId );
				}
		} else {
			if(null!= item.getPropertyValue(PROD_TIER_TEMP_LAYOUT1)){
			final RepositoryItem promoTierLayOut1 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT1);
			
				vlogDebug("promoTierLayOut1  is not Null:"+promoTierLayOut1);
				final PromoBoxVO promoBoxVO1 = getLandingTemplateManager()
						.setPromoBoxVO(promoTierLayOut1);
					homePageTemplateVO.setPromoBoxFirst(promoBoxVO1);
			}else{ //get the default value from the override site
				if(null != overrideItem && null!= overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT1)){
					final RepositoryItem promoTierLayOut1 = (RepositoryItem) overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT1);
					
						vlogDebug("default promoTierLayOut1  is not Null");
						final PromoBoxVO promoBoxVO1 = getLandingTemplateManager().setPromoBoxVO(promoTierLayOut1);
						homePageTemplateVO.setPromoBoxFirst(promoBoxVO1);
					} else {
						vlogDebug("default promoTierLayOut1  is Null for" + pSiteId );
					}
			}
		}
		//Calling LandingTemplateManager's setPromoBox method for getting content of PromoBox2
		if (usePromoRegistryPanelFromWeb && null != overrideItem){
			if(null!=overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT2)){
				final RepositoryItem promoTierLayOut2 = (RepositoryItem) overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT2);
				
					vlogDebug("overrideItem promoTierLayOut2  is not Null:"+promoTierLayOut2);
					final PromoBoxVO promoBoxVO2 = getLandingTemplateManager().setPromoBoxVO(promoTierLayOut2);
					homePageTemplateVO.setPromoBoxSecond(promoBoxVO2);
				}else{
					vlogDebug("overrideItem promoTierLayOut2  is Null for" + pSiteId );
				}
		} else {
			if(null!=item.getPropertyValue(PROD_TIER_TEMP_LAYOUT2)){
			final RepositoryItem promoTierLayOut2 = (RepositoryItem) item.getPropertyValue(PROD_TIER_TEMP_LAYOUT2);
				vlogDebug("promoTierLayOut2  is not Null");
				final PromoBoxVO promoBoxVO2 = getLandingTemplateManager().setPromoBoxVO(promoTierLayOut2);
				homePageTemplateVO.setPromoBoxSecond(promoBoxVO2);
			}else{ //get the default value from the override site
				if(null != overrideItem && null!= overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT2)){
					final RepositoryItem promoTierLayOut2 = (RepositoryItem) overrideItem.getPropertyValue(PROD_TIER_TEMP_LAYOUT2);
					
						vlogDebug("default promoTierLayOut1  is not Null");
						final PromoBoxVO promoBoxVO2 = getLandingTemplateManager().setPromoBoxVO(promoTierLayOut2);
						homePageTemplateVO.setPromoBoxSecond(promoBoxVO2);
					} else {
						vlogDebug("promoTierLayOut2  is Null for" + pSiteId );
					}
			}
		}
	}
	
	/**
	 * This method is used to get the promo layout info
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param overrideItem
	 * @param item
	 * @param usePromoTierLayout1FromWeb
	 */
	@SuppressWarnings("unchecked")
	private void getPromoLayoutInfo(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem overrideItem, RepositoryItem item,
			Boolean usePromoTierLayout1FromWeb) {
		
		if (usePromoTierLayout1FromWeb && null != overrideItem){
			final List<RepositoryItem> promoTierLayOutFirst= (List<RepositoryItem>) overrideItem.getPropertyValue(HOME_PROMO_TIERL1);
			if(null!=promoTierLayOutFirst && !promoTierLayOutFirst.isEmpty()){
				List<PromoBoxLayoutVO> promoTierLayOutFirstList = getLandingTemplateManager().setPromoTierLayOut(promoTierLayOutFirst);
				vlogDebug("setting override promoTierLayOutFirstList");
				homePageTemplateVO.setPromoTierLayout1(promoTierLayOutFirstList);

			}else{
				vlogDebug("overrideItem Promo Tier Layout1 is Null for" + pSiteId );
			}
		} else {
			final List<RepositoryItem> promoTierLayOutFirst= (List<RepositoryItem>) item.getPropertyValue(HOME_PROMO_TIERL1);
			if(null!=promoTierLayOutFirst && !promoTierLayOutFirst.isEmpty()){
				final List<PromoBoxLayoutVO> promoTierLayOutFirstList = getLandingTemplateManager()
						.setPromoTierLayOut(promoTierLayOutFirst);
					homePageTemplateVO.setPromoTierLayout1(promoTierLayOutFirstList);
			}else { //get the default value from the override site
				if(null != overrideItem && null!= overrideItem.getPropertyValue(HOME_PROMO_TIERL1)){
					final List<RepositoryItem> oPromoTierLayOutFirst= (List<RepositoryItem>) overrideItem.getPropertyValue(HOME_PROMO_TIERL1);
					if(null!=oPromoTierLayOutFirst && !oPromoTierLayOutFirst.isEmpty()){
						vlogDebug("Setting default promoTierLayOutFirstList");
						final List<PromoBoxLayoutVO> oPromoTierLayOutFirstList = getLandingTemplateManager()
								.setPromoTierLayOut(oPromoTierLayOutFirst);
						homePageTemplateVO.setPromoTierLayout1(oPromoTierLayOutFirstList);
					}
				} else {
					vlogDebug("Default Promo Tier Layout1 is Null for " + pSiteId );
				}
			}
		}
	}
	
	/**
	 * This method is used to get the registry status
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param overrideItem
	 * @param item
	 * @param usePromoRegistryPanelFromWeb
	 */
	private void registryStatus(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem overrideItem, RepositoryItem item,
			Boolean usePromoRegistryPanelFromWeb) {
		
		//Setting content of RegistryStatus
		if (usePromoRegistryPanelFromWeb && null != overrideItem){
			if(null!=overrideItem.getPropertyValue(REGISTRY_STATUS)){
				vlogDebug("overrideItem Registry Status  is not Null for" + pSiteId );
				final String registryStatus= ((Integer) overrideItem.getPropertyValue(REGISTRY_STATUS)).toString();
				homePageTemplateVO.setRegistryStatus(registryStatus);
				}else{
					vlogDebug("overrideItem Registry Status  is Null for" + pSiteId );
				}
		} else {
			if(null!=item.getPropertyValue(REGISTRY_STATUS)){
				final String registryStatus= ((Integer) item.getPropertyValue(REGISTRY_STATUS)).toString();
				homePageTemplateVO.setRegistryStatus(registryStatus);
			}else{ //get the default value from the override site
				if (null != overrideItem && null!=overrideItem.getPropertyValue(REGISTRY_STATUS)){
					final String registryStatus= ((Integer) overrideItem.getPropertyValue(REGISTRY_STATUS)).toString();
					vlogDebug("Setting default registryStatus");
					homePageTemplateVO.setRegistryStatus(registryStatus);
				} else {
					vlogDebug("Default Registry Status  is Null for" + pSiteId );
				}
			}
		}
	}
	
	/**
	 * This method is used to get the featured categories
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param overrideItem
	 * @param item
	 */
	private void getFeaturedCategories(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem overrideItem, RepositoryItem item) {
		//Setting the featured categories
		if (null != item.getPropertyValue(USE_FEATURED_CATEGORIES_FROMWEB)){
			final Boolean useFeaturedCategoriesFromWeb = (Boolean) item.getPropertyValue(USE_FEATURED_CATEGORIES_FROMWEB);
			if(null != useFeaturedCategoriesFromWeb) {
				vlogDebug("useFeaturedCategoriesFromWeb  is not Null");
				// Use the override for TBS Site
				if (useFeaturedCategoriesFromWeb && null != overrideItem) {
					List<CategoryContainerVO> featuredCategoryContainerList = getCategoryContainer(pSiteId, overrideItem, FEATURED_CAT_CONTAINER);
					if (featuredCategoryContainerList!=null){
						homePageTemplateVO.setFeaturedCategoryContainer(featuredCategoryContainerList);
						vlogDebug("Override Featured Categories  is set for:" + pSiteId );
					}else{
						vlogDebug("Override Featured Categories  is Null for:" + pSiteId );
					}
				} else {
					List<CategoryContainerVO> featuredCategoryContainerList = getCategoryContainer(pSiteId, item, FEATURED_CAT_CONTAINER);
					if (featuredCategoryContainerList!=null){
						homePageTemplateVO.setFeaturedCategoryContainer(featuredCategoryContainerList);
						vlogDebug("Featured Categories  is set for:" + pSiteId );
					} else if (null != overrideItem) {
						 // Set the default value
						List<CategoryContainerVO> oFeaturedCategoryContainerList = getCategoryContainer(pSiteId, overrideItem, FEATURED_CAT_CONTAINER);
						if (oFeaturedCategoryContainerList!=null){
							homePageTemplateVO.setFeaturedCategoryContainer(oFeaturedCategoryContainerList);
							vlogDebug("Default Featured Categories  is set for:" + pSiteId );
						}else{
							vlogDebug("Default Featured Categories  is Null for:" + pSiteId );
						}
					}
				}
			}
		}else{
			vlogDebug("Use Featured Categories From Web Flag is Null for:" + pSiteId );
		}
	}
	
	/**
	 * This method is used to populate the populer categories into VO object
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param overrideItem
	 * @param item
	 */
	private void getPopulerCategories(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem overrideItem, RepositoryItem item) {
		//Setting the popular categories
		if (null != item.getPropertyValue(USE_POPULAR_CATEGORIES_FROMWEB)){
			final Boolean usePopularCategoriesFromWeb = (Boolean) item.getPropertyValue(USE_POPULAR_CATEGORIES_FROMWEB);
			if(null != usePopularCategoriesFromWeb) {
				vlogDebug("usePopularCategoriesFromWeb  is not Null");
				// Use the override for TBS Site
				if (usePopularCategoriesFromWeb && null != overrideItem) {
					List<CategoryContainerVO> categoryContainerList = getCategoryContainer(pSiteId, overrideItem, CAT_CONTIANER);
					if (categoryContainerList!=null){
						homePageTemplateVO.setCategoryContainer(categoryContainerList);
						vlogDebug("Override Popular Categories  is set for:" + pSiteId );
					}else{
						vlogDebug("Override Popular Categories  is Null for:" + pSiteId );
					}
				} else {
					List<CategoryContainerVO> categoryContainerList = getCategoryContainer(pSiteId, item, CAT_CONTIANER);
					if (categoryContainerList!=null){
						homePageTemplateVO.setCategoryContainer(categoryContainerList);
						vlogDebug("Popular Categories  is set for:" + pSiteId );
					}else if (null != overrideItem) {
						// Set the default value
						List<CategoryContainerVO> oCategoryContainerList = getCategoryContainer(pSiteId, overrideItem, CAT_CONTIANER);
						if (oCategoryContainerList!=null){
							homePageTemplateVO.setCategoryContainer(oCategoryContainerList);
							vlogDebug("Default Popular Categories  is set for:" + pSiteId );
						}else{
							vlogDebug("Default Popular Categories  is Null for:" + pSiteId );
						}
					}
				}
			}
		}else{
			vlogDebug("Use Popular Categories From Web Flag is Null for:" + pSiteId );
		}
	}
	
	/**
	 * This method is used to get the web announcements
	 * @param pSiteId
	 * @param homePageTemplateVO
	 * @param pOverrideItem
	 * @param pItem
	 */
	private void getAnnouncements(String pSiteId, HomePageTemplateVO homePageTemplateVO, RepositoryItem pOverrideItem, RepositoryItem pItem) {
		
		if (null != pItem.getPropertyValue(USE_ANNOUNCEMENT_FROMWEB)){
			final Boolean useAnnouncementFromWeb = (Boolean) pItem.getPropertyValue(USE_ANNOUNCEMENT_FROMWEB);
			if(null != useAnnouncementFromWeb) {
				vlogDebug("useAnnouncementFromWeb  is not Null:"+useAnnouncementFromWeb);
				
				// Use the override for TBS Site
				if (useAnnouncementFromWeb && null != pOverrideItem) {
					if (null != pOverrideItem.getPropertyValue(ANNOUNCEMENT)){
						RepositoryItem announcementItem = (RepositoryItem) pOverrideItem.getPropertyValue(ANNOUNCEMENT);
						PromoBoxVO announcement = getLandingTemplateManager().setPromoBoxVO(announcementItem);
						homePageTemplateVO.setAnnouncement(announcement);
						vlogDebug("override item announcement:"+announcement);
					} else {
						vlogDebug("override item announcement is null");
					}
				} else {
					if (null != pItem.getPropertyValue(ANNOUNCEMENT)){
						RepositoryItem announcementItem = (RepositoryItem) pItem.getPropertyValue(ANNOUNCEMENT);
						PromoBoxVO announcement = getLandingTemplateManager().setPromoBoxVO(announcementItem);
					    homePageTemplateVO.setAnnouncement(announcement);
						vlogDebug("announcement:"+announcement);
					} else { //get the default value from the override site
						if (null != pOverrideItem && null != pOverrideItem.getPropertyValue(ANNOUNCEMENT)){
							RepositoryItem announcementItem = (RepositoryItem) pOverrideItem.getPropertyValue(ANNOUNCEMENT);
							PromoBoxVO announcement = getLandingTemplateManager().setPromoBoxVO(announcementItem);
							homePageTemplateVO.setAnnouncement(announcement);
							vlogDebug("default item announcement:"+announcement);
						} else {
							vlogDebug("default item announcement is null");
						}
					}
				}
			}
		}else{
			vlogDebug("Use Announcement From Web  is Null for" + pSiteId );
		}
	}

}
