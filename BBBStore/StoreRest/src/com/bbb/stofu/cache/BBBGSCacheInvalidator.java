package com.bbb.stofu.cache;

import static com.bbb.constants.BBBCoreConstants.GS_HEADER_FLYOUT_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.GS_KEYWORD_SEARCH_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.GS_SEARCH_RESULT_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;

import java.util.ArrayList;
import java.util.List;

import com.bbb.common.BBBGenericService;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.utils.BBBConfigRepoUtils;

public class BBBGSCacheInvalidator extends BBBGenericService {

	private List<String> mEndecaRepoList = new ArrayList<String>();

	private BBBObjectCache mObjectCache;

	public BBBObjectCache getObjectCache() {
		return mObjectCache;
	}

	public void setObjectCache(BBBObjectCache mObjectCache) {
		this.mObjectCache = mObjectCache;
	}

	public List<String> getEndecaRepoList() {
		return mEndecaRepoList;
	}

	public void setEndecaRepoList(List<String> mEndecaRepoList) {
		this.mEndecaRepoList = mEndecaRepoList;
	}

	public void invalidateEndecaCache() {
		invalidateObjectCache(GS_HEADER_FLYOUT_CACHE_NAME);
		invalidateObjectCache(GS_KEYWORD_SEARCH_CACHE_NAME);
		invalidateObjectCache(GS_SEARCH_RESULT_CACHE_NAME);

	}

	private void invalidateObjectCache(String cacheName) {

		List<String> derivedCacheNames = BBBConfigRepoUtils.getAllValues(
				OBJECT_CACHE_CONFIG_KEY, cacheName);
		if (!(derivedCacheNames == null) && !(derivedCacheNames.isEmpty())) {
			for (String objectCache : derivedCacheNames) {
				getObjectCache().clearCache(objectCache);
				
				logInfo("clearing cache:" + objectCache);
				
			}
		}

		else {
			
				logInfo("No values Present For The Cache : " + cacheName);
			

		}

	}
}
