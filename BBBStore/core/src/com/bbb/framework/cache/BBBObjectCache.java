package com.bbb.framework.cache;

import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHING_ENABLED;

import javax.jms.JMSException;
import javax.jms.Message;

import atg.core.util.StringUtils;
import atg.dms.patchbay.MessageSink;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBConfigRepoUtils;

public class BBBObjectCache extends BBBGenericService implements MessageSink {

	private BBBWebCacheIF cacheContainer;

	public BBBWebCacheIF getCacheContainer() {
		return cacheContainer;
	}

	public void setCacheContainer(BBBWebCacheIF cacheContainer) {
		this.cacheContainer = cacheContainer;
	}
	@Override
	public void receiveMessage(String arg0, Message arg1) throws JMSException {
		//receive message, do nothing
	}
	
	public void put(Object key, Object value, String pCacheName, long time) {
		String methodName = BBBCoreConstants.OBJECT_CACHE_PUT_TIME;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CACHE_INTEGRATION, methodName);
		
		boolean isCachingEnabled = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY,OBJECT_CACHING_ENABLED));
		if(isCachingEnabled && !StringUtils.isEmpty(pCacheName)){
			if (getCacheContainer() != null) {
				getCacheContainer().put(key, value, pCacheName, time);
			}
		} else {
		    logDebug("Caching is disabled or cacheName is missing");
		}
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CACHE_INTEGRATION, methodName);
	}
	
	public void put(Object key, Object value, String pCacheName) {
		String methodName = BBBCoreConstants.OBJECT_CACHE_PUT;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CACHE_INTEGRATION, methodName);
        
		boolean isCachingEnabled = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY,OBJECT_CACHING_ENABLED));
		if(isCachingEnabled && !StringUtils.isEmpty(pCacheName)){
			if (getCacheContainer() != null) {
				getCacheContainer().put(key, value, pCacheName);
			}
		} else {
            logDebug("Caching is disabled or cacheName is missing");
        }
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CACHE_INTEGRATION, methodName);		
	}
	
	public Object get(Object key, String pCacheName) {
		
		String methodName = BBBCoreConstants.OBJECT_CACHE_GET;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CACHE_INTEGRATION, methodName);
        
        Object object = null;
		boolean isCachingEnabled = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY,OBJECT_CACHING_ENABLED));
		if(isCachingEnabled && !StringUtils.isEmpty(pCacheName)){
			if (getCacheContainer() != null) {
				object = getCacheContainer().get(key, pCacheName);
			}
		} else {
            logDebug("Caching is disabled or cacheName is missing");
        }
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CACHE_INTEGRATION, methodName);
		
		return object;
	}
	
	public void clearCache(String pCacheName){
		boolean isCachingEnabled = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY,OBJECT_CACHING_ENABLED));
		if(isCachingEnabled && !StringUtils.isEmpty(pCacheName)){
			if (getCacheContainer() != null) {
				getCacheContainer().clearCache(pCacheName);
			}
		}
	}
}
