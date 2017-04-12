package com.bbb.redirectURLs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class CategoryRedirectURLLoader extends BBBGenericService {
	
	private Map<String, String> categoryRedirectURLMap = new HashMap<String, String>();
	private BBBObjectCache objectCache;
	private BBBCatalogTools catalogTools;
    private MutableRepository catalogRepository;
		

	/**
	 * @return the categoryRedirectURLMap
	 */
	public Map<String, String> getCategoryRedirectURLMap() {
		return categoryRedirectURLMap;
	}
	

	/**
	 * @param categoryRedirectURLMap the categoryRedirectURLMap to set
	 */
	public void setCategoryRedirectURLMap(Map<String, String> categoryRedirectURLMap) {
		this.categoryRedirectURLMap = categoryRedirectURLMap;
	}
	

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	
	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}
	

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return the objectCache
	 */
	public BBBObjectCache getObjectCache() {
		return objectCache;
	}
	

	/**
	 * @param objectCache the objectCache to set
	 */
	public void setObjectCache(BBBObjectCache objectCache) {
		this.objectCache = objectCache;
	}
	

	/** 
	 *  this method starts the service and retrieves category redirect urls 
	 *  from repository
	 *  
	 *  @throws ServiceException
	 */
	public void doStartService() throws ServiceException {
		
		if(isLoggingDebug()) {
			logDebug("Inside CategoryRedirectURLLoader :- CategoryRedirectURLLoader Service Starts.....");
		}	
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATEGORY_REDIRECT_URL_LOADER, BBBPerformanceConstants.DO_START_SERVICE);
		
		// Retrieve category redirectURLs from repository
		try {
			this.fetchCategoryRedirectURLs();
		} catch (RepositoryException e) {
			logError("Unable to retrieve CategoryRedirectURLs from the repository " +e);
		}
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATEGORY_REDIRECT_URL_LOADER, BBBPerformanceConstants.DO_START_SERVICE);
		if(isLoggingDebug()) {
			logDebug("Inside CategoryRedirectURLLoader :- CategoryRedirectURLLoader Service Ends.....");
		}	
	}

    /**
     *  this method starts the service and retrieves category redirect urls 
	 *  from repository
     * @return 
     * @throws RepositoryException 
	 *  
     */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchCategoryRedirectURLs() throws RepositoryException {
		
		if(isLoggingDebug()) {
			logDebug("Inside CategoryRedirectURLLoader :- fetchCategoryRedirectURLs Service Started.....");
		}		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATEGORY_REDIRECT_URL_LOADER, BBBPerformanceConstants.FETCH_CATEGORY_REDIRECTURLS);
		Map<String, String> mobileRedirectURLMap = new HashMap<String, String>();
		try{
			final String cacheName = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.CATEGORY_REDIRECTURLS_CACHE_NAME).get(0);
			NamedCache objectCache = getCache();      	
        	Map<String, Object> cacheMap = convertObjectToMap(objectCache);
        		        	
        	if(BBBUtility.isMapNullOrEmpty(cacheMap)) {
				RepositoryItemDescriptor redirectURLDesc = getCatalogRepository().getItemDescriptor(BBBCatalogConstants.CATEGORY_REDIRECTURL_ITEM_DESCRIPTOR);
				RepositoryView redirectURLView = redirectURLDesc.getRepositoryView();
				RqlStatement rqlStatement = RqlStatement.parseRqlStatement("ALL");
				
				final RepositoryItem[] redirectURLRepositoryItems = executeRQLQuery(redirectURLView, rqlStatement);
				if (redirectURLRepositoryItems != null) {
					final CategoryRedirectURLsVO redirectURLVO = new CategoryRedirectURLsVO();
					
					for(final RepositoryItem redirectURLRepositoryItem : redirectURLRepositoryItems) {
						 
						if(!BBBUtility.isEmpty((String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.MOBILE_TARGET_REDIRECT_URL))) {
							
							mobileRedirectURLMap.put(((RepositoryItem) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.SOURCE_CATEGORY_ID)).getRepositoryId().toString(), 
									(String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.MOBILE_TARGET_REDIRECT_URL));
							
							// populating CategoryRedirectURLsVO
							redirectURLVO.setMobileCategoryRedirectURL((String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.MOBILE_TARGET_REDIRECT_URL));
							
							logDebug("Populated desktopRedirectURLMap for Mobile with key : "+redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.SOURCE_CATEGORY_ID)
									+"value : "+(String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.MOBILE_TARGET_REDIRECT_URL));
						}
						
						if(!BBBUtility.isEmpty((String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.TARGET_REDIRECT_URL))) {
							
							// populating CategoryRedirectURLsVO
							redirectURLVO.setDesktopCategoryRedirectURL((String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.TARGET_REDIRECT_URL));
							
							// populating categoryRedirectURLMap for Desktop/TBS with default URL and Redirect URL
							this.getCategoryRedirectURLMap().put(((RepositoryItem) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.SOURCE_CATEGORY_ID)).getRepositoryId().toString(), 
									(String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.TARGET_REDIRECT_URL));
							
							
							logDebug("Populated mobileRedirectURLMap for Desktop/TBS with key : "+redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.SOURCE_CATEGORY_ID)
									+"value : "+(String) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.TARGET_REDIRECT_URL));
						}
						
						// populating CATEGORY_REDIRECTURLS_CACHE_NAME cache with redirect urls 
						getObjectCache().put(((RepositoryItem) redirectURLRepositoryItem.getPropertyValue(BBBCatalogConstants.SOURCE_CATEGORY_ID)).getRepositoryId().toString(),
								redirectURLVO, cacheName);
						
						// setting redirectURLVO to null
						redirectURLVO.setDesktopCategoryRedirectURL(null);
						redirectURLVO.setMobileCategoryRedirectURL(null);
					}
					
				}
        	} else {
        		for(Entry<String, Object> cache : cacheMap.entrySet()) {
        			// populating categoryRedirectURLMap in desktop and mobile if cache is not empty
        			mobileRedirectURLMap.put(cache.getKey(), ((HashMap<String,String>) cache.getValue()).get("mobileCategoryRedirectURL"));
        			this.getCategoryRedirectURLMap().put(cache.getKey(), ((HashMap<String, String>) cache.getValue()).get("desktopCategoryRedirectURL"));        			
        		} 
        	}
		} catch(Exception e) {
			logError("Unable to retrieve CategoryRedirectURLs from the repository " +e);
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATEGORY_REDIRECT_URL_LOADER, BBBPerformanceConstants.FETCH_CATEGORY_REDIRECTURLS);
		if(isLoggingDebug()) {
			logDebug("Inside CategoryRedirectURLLoader :- fetchCategoryRedirectURLs Service Ends.....");
		}
		
		return mobileRedirectURLMap;
		
	}


	/**
	 * @param redirectURLView
	 * @param rqlStatement
	 * @return
	 * @throws RepositoryException
	 */
	protected RepositoryItem[] executeRQLQuery(RepositoryView redirectURLView, RqlStatement rqlStatement)
			throws RepositoryException {
		return rqlStatement.executeQuery(redirectURLView, null);
	}


	/**
	 * @param objectCache
	 * @return
	 */
	protected Map<String, Object> convertObjectToMap(NamedCache objectCache) {
		return BBBUtility.convertObjectToMap(objectCache);
	}


	/**
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	protected NamedCache getCache() throws BBBSystemException, BBBBusinessException {
		return CacheFactory.getCache(this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.CATEGORY_REDIRECTURLS_CACHE_NAME).get(0));
	}
}
