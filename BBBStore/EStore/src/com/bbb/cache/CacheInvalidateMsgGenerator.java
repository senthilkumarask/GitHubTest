package com.bbb.cache;

import static com.bbb.constants.BBBCoreConstants.DROPLET_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;
import static com.bbb.constants.BBBCoreConstants.SEARCH_RESULT_CACHE_NAME;

import com.bbb.cache.tibco.vo.CacheInvalidatorVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;

public class CacheInvalidateMsgGenerator extends BBBGenericService{

	private BBBCatalogTools mCatalogTools;
	
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	public void inavlidateDropletCache(){
		CacheInvalidatorVO invalidationRequest = new CacheInvalidatorVO();
		try {
			invalidationRequest.setClearDropletCache(true);
			String dropletCacheName = getCatalogTools().getAllValuesForKey(OBJECT_CACHE_CONFIG_KEY , DROPLET_CACHE_NAME).get(0);
			invalidationRequest.getObjectCacheTypes().add(dropletCacheName);
			submitCacheInvalidationMesssage(invalidationRequest);
		} catch (BBBBusinessException e) {
			logError("inavlidateDropletCache:" ,e);
		} catch (BBBSystemException e) {
			logError("inavlidateDropletCache:" ,e);
		}
	}
	
	public void inavlidateAllObjectCache() {
		CacheInvalidatorVO invalidationRequest = new CacheInvalidatorVO();
		try {
			invalidationRequest.setClearObjectCache(true);
			String searchCacheName = getCatalogTools().getAllValuesForKey(OBJECT_CACHE_CONFIG_KEY , SEARCH_RESULT_CACHE_NAME).get(0);
			String keywordCacheName = getCatalogTools().getAllValuesForKey(OBJECT_CACHE_CONFIG_KEY , KEYWORD_SEARCH_CACHE_NAME).get(0);
			invalidationRequest.getObjectCacheTypes().add(searchCacheName);
			invalidationRequest.getObjectCacheTypes().add(keywordCacheName);
			submitCacheInvalidationMesssage(invalidationRequest);
		} catch (BBBBusinessException e) {
			logError("inavlidateObjectCache:" ,e);
		} catch (BBBSystemException e) {
			logError("inavlidateObjectCache:" ,e);
		}
	}

	private void submitCacheInvalidationMesssage(CacheInvalidatorVO invalidateRequest) throws BBBBusinessException, BBBSystemException {
		
			logDebug("START: Submitting Cache Clear Message");
		
		invalidateRequest.setServiceName(BBBCoreConstants.CACHE_INVALIDATION_SERVICE);
		ServiceHandlerUtil.sendTextMessage(invalidateRequest);

			logDebug("End: Submitting Cache Clear Message");
		
	}
	
}
