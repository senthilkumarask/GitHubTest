package com.bbb.cache.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import atg.adapter.gsa.GSARepository;
import atg.dms.patchbay.MessageSink;

import com.bbb.cache.BBBInteractiveCheckListCacheLoader;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.cache.CacheInvalidationMessage;
import com.bbb.utils.BBBUtility;

public class InteractiveRepositoryCacheSink extends BBBGenericService implements MessageSink {

	private GSARepository checkListRepository;
	private BBBInteractiveCheckListCacheLoader cacheLoader;
	private BBBLocalCacheContainer cacheContainer;
	
	@Override
	public void receiveMessage(String pPortName,  Message pMessage) throws JMSException {

		logDebug("InteractiveRepositoryCacheSink invoked for caching Checklist Cache");
		if (!(pMessage instanceof ObjectMessage))
			throw new MessageFormatException("ERROR: Not an ObjectMessage");

		ObjectMessage objMessage = (ObjectMessage) pMessage;
		Object obj = objMessage.getObject();

		logDebug((new StringBuilder()).append("Received ").append(obj.toString()).append(" on port ").append(pPortName).toString());
		
		if (obj instanceof CacheInvalidationMessage) {
			processCacheInvalidation((CacheInvalidationMessage)obj);
		} else {
			logDebug("InteractiveRepositoryCacheSink.receiveMessage() | Not a InvalidationMessage on port "
					+ pPortName
					+ " is an instance of "
					+ pMessage.getClass());
		}
		logDebug("InteractiveRepositoryCacheSink ends for caching Checklist Cache");		
}

	/** Invalidates the cache of RegistryCheckList.
	 * Process cache invalidation.
	 * @param obj 
	 */
	private void processCacheInvalidation(CacheInvalidationMessage cacheInvalidationMessage) {

		if (BBBCoreConstants.INTERACTIVE_CHECKLIST_REPOSITORY_CACHE.equals(cacheInvalidationMessage.getEntryKey())) {
			logInfo("START:BBBLocalCacheContainer-processCacheInvalidation for LocalCacheInvalidationMessage");
			long removeCacheStartTime = System.currentTimeMillis();
			invalidateRepositoryCache();
			// Clear local cache for checklist
			getCacheContainer().clearCache(getCacheContainer().getCacheType());
			buildInteractiveChecklistCaches();
			long removeCacheEndTime = System.currentTimeMillis();
			logInfo("END:BBBLocalCacheContainer.processCacheInvalidation() Total time take="
					+ (removeCacheEndTime - removeCacheStartTime));
		}
	}
	
	/**
	 * Invalidate external and internal caches for repository
	 */
	private void invalidateRepositoryCache() {
		
		logDebug("Inside invalidateRepositoryCache");
		// Invalidate checklist repo caches
		if(getCheckListRepository() !=null){
			getCheckListRepository().invalidateCaches();
			getCheckListRepository().invalidateExternalCaches();
		}
		
		logDebug("Exiting invalidateRepositoryCache");
	}
	
	private void buildInteractiveChecklistCaches() {
		
			logDebug("buildInteractiveChecklistCaches Is enabled");
			long startTime = System.currentTimeMillis();
			logDebug("buildInteractiveChecklistCaches start at  :"
					+ startTime);
			this.logDebug("Going to Build interactive checklist cache  start : ");
			List<String> regTypeList = new ArrayList<String>();
			String registryTypeConfigKeys;
			if(null != this.getCacheLoader() && null != this.getCacheLoader().getCatalogTools()) {
				registryTypeConfigKeys = this.getCacheLoader().getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.CHECKLIST_REGISTRY_TYPES, BBBCoreConstants.BLANK);
				
				if (BBBUtility.isNotEmpty(registryTypeConfigKeys)) {
					regTypeList = Arrays.asList(registryTypeConfigKeys.split(BBBCoreConstants.COMMA));
				
				for(String regType:regTypeList){
					try {
						this.getCacheLoader().populateCheckListVO(regType);
					} catch (BBBSystemException | BBBBusinessException e) {
							logError("Exception in Method buildInteractiveChecklistCaches while Rebuilding the Local Cache : " + e.getMessage());
							logDebug("Exception in Method buildInteractiveChecklistCaches while Rebuilding the Local Cache : " , e);
						}
					}
				}
			}

			this.logDebug("Going to Build interactive checklist cache  End : ");
			long endTime = System.currentTimeMillis();
			logDebug("BuildInteractiveChecklistCaches end at  :" + endTime);


	}


	public GSARepository getCheckListRepository() {
		return checkListRepository;
	}

	public void setCheckListRepository(GSARepository checkListRepository) {
		this.checkListRepository = checkListRepository;
	}

	public BBBLocalCacheContainer getCacheContainer() {
		return cacheContainer;
	}

	public void setCacheContainer(BBBLocalCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}
	
	/**
	 * @return the cacheLoader
	 */
	public BBBInteractiveCheckListCacheLoader getCacheLoader() {
		return cacheLoader;
	}

	/**
	 * @param cacheLoader the cacheLoader to set
	 */
	public void setCacheLoader(BBBInteractiveCheckListCacheLoader cacheLoader) {
		this.cacheLoader = cacheLoader;
	}
}
