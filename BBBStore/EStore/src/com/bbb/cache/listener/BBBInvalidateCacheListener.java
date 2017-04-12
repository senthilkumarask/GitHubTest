package com.bbb.cache.listener;

import static com.bbb.constants.BBBCoreConstants.BOOSTED_PRODUCTS_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.BOOSTING_STRATEGY_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.CLEAR_DROPLET_CACHE_ON_DEPLOYMENT;
import static com.bbb.constants.BBBCoreConstants.CLEAR_LOCAL_CACHE_FLAG;
import static com.bbb.constants.BBBCoreConstants.CLEAR_MAPQUEST_CACHE_ON_DEPLOYMENT;
import static com.bbb.constants.BBBCoreConstants.DISABLE_COLLECTION_PARENT_CACHE;
import static com.bbb.constants.BBBCoreConstants.DROPLET_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS;
import static com.bbb.constants.BBBCoreConstants.HEADER_FLYOUT_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.INVALIDATE_FLYOUT_CACHE_ON_PIM_DEPLOYMENT;
import static com.bbb.constants.BBBCoreConstants.INVALIDATE_MOBILE_CMSFLYOUT_CACHE;
import static com.bbb.constants.BBBCoreConstants.IS_CLEAR_ENDECA_CACHE_ON_DEPLOYMENT;
import static com.bbb.constants.BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.MAPQUEST_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.MOBILE_ALL_TOP_CATEGORIES_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.MOBILE_NAV_FLYOUT_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;
import static com.bbb.constants.BBBCoreConstants.SEARCH_RESULT_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.cache.BBBCacheDroplet;
import com.bbb.framework.cache.BBBCacheInvalidatorSource;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.cache.BBBWebCacheIF;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.redirectURLs.CategoryRedirectURLLoader;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;
import atg.repository.RepositoryException;

/**
 * 
 * @author BBB
 * 
 * BBBInvalidateCacheListener listens for deployment events fired from ATG publishing
 * This listener is added tocomponent /atg/epub/DeploymentAgent
 * If the the deployment event is complete, it will clear the cache
 * 
 */
public class BBBInvalidateCacheListener extends BBBGenericService implements
		DeploymentEventListener {
	
	private final String EPH_CATEGORY_MAP_CACHE_LOAD_CACHE_NAME = "KEYWORD_EPH_CAT_MAP_CACHE_NAME";
	private static final String PRODUCT_CATALOG_CACHE = "/atg/commerce/catalog/ProductCatalog";
	private static final String REPO_CACHE_MAP_TYPE = "RepoCacheMapType";
	private static final String SERVICE_CACHE_MAP_TYPE = "ServiceCacheMapType";
	private static final String OBJECT_CACHE_KEYS = "ObjectCacheKeys";
	private static final String SITE_REPO_CACHE = "/atg/multisite/SiteRepository";
	private static final String LABEL_CACHE = "/com/bbb/cms/repository/LabelTemplate";
	private static final String CAT_PAGE_CONTENT_CACHE = "/com/bbb/cms/repository/WSCategoryLandingTemplate";
	private static final String HOME_PAGE_CONTENT_CACHE = "/com/bbb/cms/repository/WSHomePageTemplate";
	private static final String STATIC_CONTENT_CACHE = "/com/bbb/cms/repository/WSStaticTemplate";
	private static final String CONFIG_CACHE = "/com/bbb/configurekeys/ConfigureKeys";
	private static final String SUBJECT_CACHE = "/com/bbb/selfservice/contactus/ContactUsRepository";
	private static final String CLP_CACHE = "/com/bbb/cms/repository/CustomLandingTemplate";
	private static final String ALL_CATEGORIES_CACHE = "getAllCategories";
	private static final String MOBILE_ALL_CATEGORIES_CACHE = "getAllCategoriesMobileWeb";
	private List<String> mEndecaRepoList = new ArrayList<String>();
	private BBBObjectCache mObjectCache;
	private BBBCacheDroplet mCacheDroplet;
	private CoherenceCacheContainer coherenceCacheContainer;
	private BBBWebCacheIF configCacheContainer;
	private BBBWebCacheIF labelCacheContainer;
	private BBBCacheInvalidatorSource bbbCacheInvalidatorMessageSource;
	private CategoryRedirectURLLoader categoryRedirectURL;
	private String categoryRedirectURLRepository;
	
	/**
	 * @return the bbbCacheInvalidatorMessageSource
	 */
	public BBBCacheInvalidatorSource getBbbCacheInvalidatorMessageSource() {
		return bbbCacheInvalidatorMessageSource;
	}
	/**
	 * @param bbbCacheInvalidatorMessageSource the bbbCacheInvalidatorMessageSource to set
	 */
	public void setBbbCacheInvalidatorMessageSource(
			BBBCacheInvalidatorSource bbbCacheInvalidatorMessageSource) {
		this.bbbCacheInvalidatorMessageSource = bbbCacheInvalidatorMessageSource;
	}
	/**
	 * @return the localLabelCacheContainer
	 */
	public BBBWebCacheIF getLabelCacheContainer() {
		return this.labelCacheContainer;
	}
	/**
	 * @param labelCacheContainer the labelCacheContainer to set
	 */
	public void setLabelCacheContainer(BBBWebCacheIF labelCacheContainer) {
		this.labelCacheContainer = labelCacheContainer;
	}
	/**
	 * @return the localCacheContainer
	 */
	public BBBWebCacheIF getConfigCacheContainer() {
		return this.configCacheContainer;
	}
	/**
	 * @param configCacheContainer the configCacheContainer to set
	 */
	public void setConfigCacheContainer(BBBWebCacheIF configCacheContainer) {
		this.configCacheContainer = configCacheContainer;
	}
	
	/**
	 * @return the coherenceCacheContainer
	 */
	public CoherenceCacheContainer getCoherenceCacheContainer() {
		return this.coherenceCacheContainer;
	}

	/**
	 * @param coherenceCacheContainer the coherenceCacheContainer to set
	 */
	public void setCoherenceCacheContainer(CoherenceCacheContainer coherenceCacheContainer) {
		this.coherenceCacheContainer = coherenceCacheContainer;
	}
	
	/**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return this.mObjectCache;
	}

	/**
	 * @param pObjectCache the pObjectCache to set
	 */
	public void setObjectCache(BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}

	/**
	 * @return the mCacheDroplet
	 */
	public BBBCacheDroplet getCacheDroplet() {
		return this.mCacheDroplet;
	}

	/**
	 * @param pCacheDroplet the pCacheDroplet to set
	 */
	public void setCacheDroplet(BBBCacheDroplet pCacheDroplet) {
		this.mCacheDroplet = pCacheDroplet;
	}
	/**
	 * @return the mEndecaRepoList
	 */
	public List<String> getEndecaRepoList() {
		return this.mEndecaRepoList;
	}

	/**
	 * @param pEndecaRepoList the pEndecaRepoList to set
	 */
	public void setEndecaRepoList(List<String> pEndecaRepoList) {
		this.mEndecaRepoList = pEndecaRepoList;
	}
	
	/**
	 * @return the categoryRedirectURL
	 */
	public CategoryRedirectURLLoader getCategoryRedirectURL() {
		return categoryRedirectURL;
	}
	
	/**
	 * @param categoryRedirectURL the categoryRedirectURL to set
	 */
	public void setCategoryRedirectURL(CategoryRedirectURLLoader categoryRedirectURL) {
		this.categoryRedirectURL = categoryRedirectURL;
	}
	
	/**
	 * @return the categoryRedirectURLRepository
	 */
	public String getCategoryRedirectURLRepository() {
		return categoryRedirectURLRepository;
	}
	
	/**
	 * @param categoryRedirectURLRepository the categoryRedirectURLRepository to set
	 */
	public void setCategoryRedirectURLRepository(String categoryRedirectURLRepository) {
		this.categoryRedirectURLRepository = categoryRedirectURLRepository;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deploymentEvent(DeploymentEvent event) {
		
		final String METHOD_NAME="deploymentEvent()";
		logDebug("-----"+event.getAffectedItemTypes());
		
		if(event.getAffectedItemTypes()!=null) {
		  logDebug("Endeca Feed Type="+event.getAffectedItemTypes().get("endecaFeedType"));
		}
		logInfo(getClass().toString()+":"+METHOD_NAME+":"+"Starting Cache Invalidation for Deployment Event");
		final String newState = DeploymentEvent.stateToString(event
				.getNewState());
		final String oldState = DeploymentEvent.stateToString(event
				.getOldState());
		final String deploymentId = event.getDeploymentID();
		logDebug("New State as String :: " + newState + " state as int "
				+ event.getNewState());
		logDebug("Old State as String :: " + oldState + " state as int "
				+ event.getOldState());
		
		boolean clearEndecaCacheFlag = true;
		String clearEndecaCacheFlagStr = BBBConfigRepoUtils.getStringValue(FLAG_DRIVEN_FUNCTIONS, IS_CLEAR_ENDECA_CACHE_ON_DEPLOYMENT);
		if(null!=clearEndecaCacheFlagStr){
			clearEndecaCacheFlag = Boolean.parseBoolean(clearEndecaCacheFlagStr);
		}
				
		logDebug("clear endeca cache flag on deployment value :: " + clearEndecaCacheFlag);
		
		if (event.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE) {
			logDebug("Deployment is complete");
			logDebug(" Deployment id " + deploymentId);
			logDebug(" Affected Item Types:" + event.getAffectedItemTypes());			
			
			if(event.getAffectedItemTypes()!=null && !event.getAffectedItemTypes().isEmpty() && clearEndecaCacheFlag){
				invalidateEndecaCache(event.getAffectedItemTypes().keySet(),String.valueOf(event.getAffectedItemTypes().get("endecaFeedType")));
			}
			boolean clearCacheDroplet = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, CLEAR_DROPLET_CACHE_ON_DEPLOYMENT));
			if(clearCacheDroplet){
				invalidateDropletCache();
			}
			boolean clearCacheMapquest = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, CLEAR_MAPQUEST_CACHE_ON_DEPLOYMENT));
			if(clearCacheMapquest){
				invalidateObjectCache(MAPQUEST_CACHE_NAME);
			}
			
			final boolean clearCategoryRedirectURL = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.CLEAR_CATEGORY_REDIRECTURL_CACHE));
			final Set<String> repositoryList = event.getAffectedItemTypes().keySet();
			if(repositoryList != null && !repositoryList.isEmpty()) {
				for(final String repository : repositoryList) {
					if(clearCategoryRedirectURL && repository != null && getCategoryRedirectURLRepository().equalsIgnoreCase(repository)) {
						invalidateCategoryRedirectURLCache();
						break;
					}
				}
			}			
			
			if(event.getAffectedItemTypes()!=null && !event.getAffectedItemTypes().isEmpty()){
				invalidateLocalCacheContainer(event.getAffectedItemTypes().keySet());
				//calling method to invalidate Clp cache
				invalidateCLPDropletCache(event.getAffectedItemTypes().keySet());
				
				boolean disableCollectionParentCache = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, DISABLE_COLLECTION_PARENT_CACHE));
				logDebug("BBBInvalidateCacheListener.deploymentEvent disableCollectionParentCache " +  disableCollectionParentCache);
				if (!disableCollectionParentCache) {
					invalidateCollectionChildRelnCache(event.getAffectedItemTypes().keySet());
				}
			}
		}
		logInfo(getClass()+":"+METHOD_NAME+":"+"Completed Cache Invalidation for Deployment Event");
	}
	
	/**
	 *  Invalidating categoryredirectURLCache
	 */
	private void invalidateCategoryRedirectURLCache() {
		final String categoryredirectURLCache = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.CATEGORY_REDIRECTURLS_CACHE_NAME);
		final String mobilecategoryredirectURLCache = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.MOBILE_COHERENCE_CACHE_CONFIG_KEY, BBBCoreConstants.CATEGORIES_REDIRECTURLS_MOBILEWEB);
		logDebug("Clearing Cache......"+categoryredirectURLCache);
		getObjectCache().clearCache(categoryredirectURLCache);
		getObjectCache().clearCache(mobilecategoryredirectURLCache);
		try {
			this.getCategoryRedirectURL().fetchCategoryRedirectURLs();
		} catch (RepositoryException e) {
			logDebug("Unable to re-populate CategoryRedirectURLMap..."+e.getMessage());
		}
		logDebug("Firing Message to Clear CategoryRedirectURLMap on all JVMs......");
		getBbbCacheInvalidatorMessageSource().fireCategoryredirectURLCacheInvalidationMessage(getCategoryRedirectURLRepository());
		
	}
	
	/**
	 * 
	 * @param affectedItems
	 */
	private void invalidateCollectionChildRelnCache(Set<String> affectedItems) {
		for(String item:affectedItems) {
			if(PRODUCT_CATALOG_CACHE.equalsIgnoreCase(item)) {
				getBbbCacheInvalidatorMessageSource().fireCollectionChildRelnCacheInvalidationMessage(PRODUCT_CATALOG_CACHE);
				logInfo("clearing cache: CollectionChildRelnCache" );
				break;
			}
		}		
	}
		
	public void invalidateCollectionChildRelnCache() {
		getBbbCacheInvalidatorMessageSource().fireCollectionChildRelnCacheInvalidationMessage(PRODUCT_CATALOG_CACHE);
		logInfo("clearing cache: CollectionChildRelnCache" );
	}
	
	//Adding logic to invalidate Clp cache
	public void invalidateCLPDropletCache(Set<String> affectedItems) {
		// TODO Auto-generated method stub
		for(String item:affectedItems){
			if(CLP_CACHE.equalsIgnoreCase(item)){
				invalidateCLPDropletCache();
			}
		}
	}
	/**
	 * Invalidating Clp cache
	 */
	public void invalidateCLPDropletCache(){
		getBbbCacheInvalidatorMessageSource().fireCLPDropletCacheInvalidationMessage();
		logInfo("clearing cache: ClpDropletCache");
	}
	
	
	private void invalidateLocalCacheContainer(Set<String> affectedItems) {
		boolean clearLocalCacheFlag = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, CLEAR_LOCAL_CACHE_FLAG));
		for(String item:affectedItems){
			if(CONFIG_CACHE.equalsIgnoreCase(item) && clearLocalCacheFlag){
				this.getBbbCacheInvalidatorMessageSource().fireConfigCacheContainerInvalidationMessage();
			}
			else if(CONFIG_CACHE.equalsIgnoreCase(item)){
				invalidateLocalConfigCacheContainer();
			}
			if(LABEL_CACHE.equalsIgnoreCase(item) && clearLocalCacheFlag){
				this.getBbbCacheInvalidatorMessageSource().fireLocalLabelCacheContainerInvalidationMessage();
			}
			else if(LABEL_CACHE.equalsIgnoreCase(item)){
				invalidateLocalLabelCacheContainer();
			}
		}
	}
	private void invalidateEndecaCache(Set<String> affectedItems,String feedType){
		if(feedType.equalsIgnoreCase("full")){
			
			for(String item:affectedItems){
				if(getEndecaRepoList().contains(item)){
					logDebug("Full Endeca Indexing -> Invalidating Flyout, Search Results and Keyword Search Cache.");
					invalidateHeaderFlyoutCache();
					invalidateObjectCache(SEARCH_RESULT_CACHE_NAME);
					invalidateObjectCache(KEYWORD_SEARCH_CACHE_NAME);
					invalidateObjectCache(BBBCoreConstants.CLEARANCE_PRODS_CACHE_NAME);
					break;
				}
			}
		}
		else{
			for(String item:affectedItems){
				if(getEndecaRepoList().contains(item)){
					logDebug("Not Full Endeca Indexing -> Invalidating Search Results and Keyword Search Cache.");
					invalidateObjectCache(SEARCH_RESULT_CACHE_NAME);
					invalidateObjectCache(KEYWORD_SEARCH_CACHE_NAME);
					invalidateObjectCache(BBBCoreConstants.CLEARANCE_PRODS_CACHE_NAME);
					break;
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void invalidateLocalConfigCacheContainer(){
		getConfigCacheContainer().clearCache(CONFIG_CACHE);
		logInfo("clearing cache: localConfigCacheContainer");
	}
	
	/**
	 * 
	 */
	public void invalidateLocalLabelCacheContainer(){
		getLabelCacheContainer().clearCache(LABEL_CACHE);
		logInfo("clearing cache: localLabelCacheContainer");
	}

	
	private void invalidateObjectCache(String cacheName){
		String objectCache = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, cacheName);
		getObjectCache().clearCache(objectCache);
		logInfo("clearing cache:" + objectCache);
	}
	
	/**
	 * 
	 */
	public void invalidateDropletCache(){
		String dropletCache = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, DROPLET_CACHE_NAME);
		getCacheDroplet().clearCache(dropletCache);
		logInfo("clearing cache:" + dropletCache);
	}
	
	/**
	 * 
	 */
	public void invalidateAllCache(){
		logInfo("BBBInvalidateCacheListener: invalidateAll() triggered");
		invalidateObjectCache(SEARCH_RESULT_CACHE_NAME);
		invalidateObjectCache(KEYWORD_SEARCH_CACHE_NAME);
		invalidateHeaderFlyoutCache();
		invalidateObjectCache(MAPQUEST_CACHE_NAME);
		invalidateDropletCache();
		invalidatePopularKeywordsCache();
		invalidateAllDynamicRepositoryCacheProduct();
		invalidateAllDynamicRepositoryCacheSKU();
		//Removing Dynamic repository cache clearing methods BBBH-7058
		/*invalidateAllDynamicRepositoryCacheProduct();
		invalidateAllDynamicRepositoryCacheSKU();*/
		logInfo("BBBInvalidateCacheListener: invalidateAll() exitting");
	}
	
	/**
	 * 
	 */
	public void invalidateSearchResultCache(){
		invalidateObjectCache(SEARCH_RESULT_CACHE_NAME);
	}
	
	/**
	 * 
	 */
	public void invalidateKeywordSearchCache(){
		invalidateObjectCache(KEYWORD_SEARCH_CACHE_NAME);
	}
	
	/**
	 * 
	 */
	public void invalidateHeaderFlyoutCache(){
		String invalidateHeaderFlyoutCache = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, INVALIDATE_FLYOUT_CACHE_ON_PIM_DEPLOYMENT);
		logInfo("invalidateHeaderFlyoutCache key value is :" + invalidateHeaderFlyoutCache);
		if (BBBUtility.isNotEmpty(invalidateHeaderFlyoutCache) && TRUE.equalsIgnoreCase(invalidateHeaderFlyoutCache)) {
			logInfo("Invalidate" +  HEADER_FLYOUT_CACHE_NAME);
		invalidateObjectCache(HEADER_FLYOUT_CACHE_NAME);
		}
	}
	
	
	/**
	 * This method is invalidating cache for Mobile CMS Flyout
	 */
	public void invalidateMobileCMSNavCache(){
		
		String invalidateMobleFlyoutCache = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, INVALIDATE_MOBILE_CMSFLYOUT_CACHE);
		logInfo("invalidateMobileCMSNavCache key value is :" + invalidateMobleFlyoutCache);
		
		//Invalidate if its value is not false; Invalidate cache even when it is null
		if (BBBUtility.isNotEmpty(invalidateMobleFlyoutCache) && TRUE.equalsIgnoreCase(invalidateMobleFlyoutCache.trim())) {
			logInfo("Invalidating " +  MOBILE_NAV_FLYOUT_CACHE_NAME);
			invalidateObjectCache(MOBILE_NAV_FLYOUT_CACHE_NAME);
		}
	}
	
	/**
	 * This method is invalidating cache for Popular Keywords coherence cache
	 */
	public void invalidatePopularKeywordsCache(){
		
		String invalidatePopularKeywordsCache = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.INVALIDATE_POPULAR_KEYWORDS_CACHE);
		logInfo("invalidatePopularKeywordsCache key value is :" + invalidatePopularKeywordsCache);
		
		//Invalidate if its value is not false; 
		if (BBBUtility.isNotEmpty(invalidatePopularKeywordsCache) && TRUE.equalsIgnoreCase(invalidatePopularKeywordsCache.trim())) {
			logInfo("Invalidating " +  BBBCoreConstants.POPULAR_KEYWORDS_CACHE_NAME);
			invalidateObjectCache(BBBCoreConstants.POPULAR_KEYWORDS_CACHE_NAME);
		}
	}	
	
	/**
	 * 
	 */
	public void invalidateMapQuestCache(){
		invalidateObjectCache(MAPQUEST_CACHE_NAME);
	}
	
	/**
	 * 
	 */
	public void invalidateAllMobileCache(){
		invalidateCacheRegion(SITE_REPO_CACHE);
		invalidateCacheRegion(LABEL_CACHE);
		invalidateCacheRegion(CAT_PAGE_CONTENT_CACHE);
		invalidateCacheRegion(HOME_PAGE_CONTENT_CACHE);
		invalidateCacheRegion(STATIC_CONTENT_CACHE);
		invalidateCacheRegion(CONFIG_CACHE);
		invalidateCacheRegion(SUBJECT_CACHE);
		invalidateObjectCache(MOBILE_NAV_FLYOUT_CACHE_NAME);
		invalidateObjectCache(MOBILE_ALL_TOP_CATEGORIES_CACHE_NAME);
		invalidateAllCategoriesCache();
		invalidateAllMobileCategoriesCache();
	}
	
	private void invalidateCacheRegion(String cacheName){
		String cacheRegion = BBBConfigRepoUtils.getStringValue(REPO_CACHE_MAP_TYPE, cacheName);
		getCoherenceCacheContainer().clearCache(cacheRegion);
		logInfo("clearing cache:" + cacheRegion);
	}
	
	/**
	 * 
	 */
	public void invalidateSiteRepoCache(){
		invalidateCacheRegion(SITE_REPO_CACHE);
	}
	
	/**
	 * 
	 */
	public void invalidateLabelCache(){
		invalidateCacheRegion(LABEL_CACHE);
	}
	
	/**
	 * 
	 */
	public void invalidateCategoryPageContentCache(){
		invalidateCacheRegion(CAT_PAGE_CONTENT_CACHE);
	}
	
	/**
	 * 
	 */
	public void invalidateHomePageContentCache(){
		invalidateCacheRegion(HOME_PAGE_CONTENT_CACHE);
	}
	
	/**
	 * 
	 */
	public void invalidateStaticContentCache(){
		invalidateCacheRegion(STATIC_CONTENT_CACHE);
	}
	
	/**
	 * 
	 */
	public void invalidateConfigCache(){
		invalidateCacheRegion(CONFIG_CACHE);
	}
	
	/**
	 * 
	 */
	public void invalidateSubjectCache(){
		invalidateCacheRegion(SUBJECT_CACHE);
	}
	
	/**
	 * 
	 */
	public void invalidateAllCategoriesCache(){
		String cacheRegion = BBBConfigRepoUtils.getStringValue(SERVICE_CACHE_MAP_TYPE, ALL_CATEGORIES_CACHE);
		getCoherenceCacheContainer().clearCache(cacheRegion);
		logInfo("clearing cache for getAllCategories:" + cacheRegion);
	}
	
	public void invalidateAllDynamicRepositoryCacheProduct(){
		String cacheRegion = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_KEYS, BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_PRODUCT);
		getCoherenceCacheContainer().clearCache(cacheRegion);
		logInfo("clearing cache for :" + cacheRegion);
	}
	public void invalidateAllDynamicRepositoryCacheSKU(){
		String cacheRegion = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_KEYS, BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_SKU);
		getCoherenceCacheContainer().clearCache(cacheRegion);
		logInfo("clearing cache for :" + cacheRegion);
	}
	
	public void invalidateAllInteractiveChecklistCache(){
		getBbbCacheInvalidatorMessageSource().fireInteractiveInvalidationMessage();
		getBbbCacheInvalidatorMessageSource().fireRegistryChecklistCacheDropletInvalidationMessage();
		logInfo("clearing cache: AllInteractiveChecklistCache" );
	}
	
	public void invalidateAllMobileCategoriesCache(){
		String cacheRegion = BBBConfigRepoUtils.getStringValue(SERVICE_CACHE_MAP_TYPE, MOBILE_ALL_CATEGORIES_CACHE);	
		getCoherenceCacheContainer().clearCache(cacheRegion);
		logInfo("clearing cache for getAllCategories:" + cacheRegion);
	}
	
	/*
	 * invalidate coherence cache keyword-eph-cat-near-cache
	 */
	public void invalidateEPHCategoryMapCache(){
		String cacheRegion = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_KEYS, EPH_CATEGORY_MAP_CACHE_LOAD_CACHE_NAME);
		getCoherenceCacheContainer().clearCache(cacheRegion);
		logInfo("clearing cache for :" + cacheRegion);
	}
	
	/**
	 *  Invalidate Coherance boosted-products-near-cache
	 */
	public void invalidateBoostProductsResultCache(){
		invalidateObjectCache(BOOSTED_PRODUCTS_CACHE_NAME);
	}

	/**
	 *  Invalidate Coherance cache of boosting-strategy-near-cache
	 */
	public void invalidateBoostingStrategyCache(){
		invalidateObjectCache(BOOSTING_STRATEGY_CACHE_NAME);
	}
}