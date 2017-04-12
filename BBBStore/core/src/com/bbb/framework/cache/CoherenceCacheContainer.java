package com.bbb.framework.cache;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import atg.dms.patchbay.MessageSink;
import atg.nucleus.ServiceException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.RequestPolicyException;
import com.tangosol.net.RequestTimeoutException;
import com.tangosol.net.cache.NearCache;
import com.bbb.commerce.browse.vo.BrandsListingVO;

/**
 * This call implements interface methods to provide wrapper layer for Coherence
 * cache. The component made of CoherenceCacheContainer needs to be injected in
 * BBBCacheDroplet's cacheContainer property if Coherence cache need to be used.
 * 
 * @author pprave
 * 
 */
public class CoherenceCacheContainer extends BBBGenericService implements
		BBBWebCacheIF, Serializable, MessageSink {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8640549142227518679L;

	// Property to capture last RequestTimeOut timeStamp in milliseconds.
	/** The last request time out. */
	private long lastRequestTimeOut;

	// Property to capture coherence call disable timeStamp in milliseconds.
	/** The coherence disable time stamp. */
	private long coherenceDisableTimeStamp;

	// Global requestTimeOut increment counter.
	/** The global counter. */
	private int globalCounter;

	// Property contain global request timeout error Count Threshold
	/** The counter threshold. */
	private long counterThreshold;

	// coherence enable threshold time stamp
	/** The coherence enable threshold time. */
	private long coherenceEnableThresholdTime;

	// recovery time period
	/** The recovery time. */
	private long recoveryTime;
	
	// Coherence optimization  enable flag
	/** The coherence optimization enable. */
	private boolean coherenceOptimizationEnable;
	
	/** The coherence enabled map. */
	private Map<String, Boolean> coherenceEnabledMap = new ConcurrentHashMap<String, Boolean>();			// This Map is used to have enabled flag associated to each
																											// cache	
	/** The last req time out map. */
																											private Map<String, Long> lastReqTimeOutMap = new ConcurrentHashMap<String, Long>();					// This Map contains last request time out associated to each
																											// cache	
	/** The cache counter map. */
																											private Map<String, Integer> cacheCounterMap = new ConcurrentHashMap<String, Integer>();				// This Map contains counter associated to each cache
																											
	/** The coherence disable time stamp map. */
	private Map<String, Long> coherenceDisableTimeStampMap = new ConcurrentHashMap<String, Long>();			// This Map disable time stamp associated to each cache

	/** The catalog tools. */
	private BBBCatalogTools catalogTools;

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#doStartService()
	 */
	public void doStartService() throws ServiceException {
		// PS-5496 - Cache configuration changes , Remove below method call
		// CacheFactory.ensureCluster();
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#doStopService()
	 */
	@Override
	public void doStopService() throws ServiceException {
		//do nothing
	}

	/**
	 * Gets the cache.
	 *
	 * @param sName the s name
	 * @return the cache
	 */
	public NamedCache getCache(String sName) {
		NamedCache c = CacheFactory.getCache(sName);
		return c;
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#get(java.lang.Object, java.lang.String)
	 */
	public Object get(Object key, String pCacheName) {
		
		Object result = null;
		boolean isCoherenceEnabled = true;
		Boolean containsCacheKey = this.getCoherenceEnabledMap().get(pCacheName);
		
		if(containsCacheKey !=null ){
			isCoherenceEnabled = containsCacheKey.booleanValue();
		}
		
		if (isCoherenceEnabled) {
			// catching RequestTimeoutException and calling
			// coherenceServerDisableCheck() method to check and disable
			// coherence server
			try {
				NamedCache nCache = getCache(pCacheName);
				if (nCache != null) {
					result = nCache.get(key);
				} else {
					logDebug("Cache with " + pCacheName + " Not Found.");
				}
			} catch (RequestTimeoutException e) {
				coherenceServerDisableCheck(pCacheName);
				logError(
						"CoherenceCacheContainer.get() :: Catching RequestTimeoutException for cache : "+ pCacheName + " and key : " + key.toString(), e);

			} catch (RequestPolicyException rp) {
				coherenceServerDisableCheck(pCacheName);
				logError(
						"CoherenceCacheContainer.get() :: Catching RequestPolicyException ",
						rp);
			}
		}
		return result;
	}

	/**
	 * This method check if requestTimeOut Exception is coming from a time
	 * period greater then a given recovery time then initialize the global
	 * increment counter and last exception time period else increment the
	 * global counter by 1 and if global counter greater then the given
	 * threshold then disable coherence.
	 *
	 * @param cacheName the cache name
	 */
	private void coherenceServerDisableCheck(String cacheName) {

		logDebug("CoherenceCacheContainer.coherenceServerDisableCheck() - start");
		
		List<String> coherenceOptEnable = null;
		
		try {
			coherenceOptEnable = getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.COHERENCE_OPTIMIZATION_ENABLE);
		} catch (BBBSystemException e) {
			logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, e);
		} catch (BBBBusinessException e) {
			logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, e);
		}
		
		if (null != coherenceOptEnable
				&& null != coherenceOptEnable.get(0)){
			setCoherenceOptimizationEnable(Boolean.parseBoolean(coherenceOptEnable.get(0)));
		}
		
		//execute custom logic 
		if(isCoherenceOptimizationEnable()){
		long currentTimeStamp = Calendar.getInstance().getTimeInMillis();
		
		long recoveryTimePeriod = getConfiguredValues(BBBCoreConstants.RECOVERY_TIME_PERIOD);
		
		long countThreshold = getConfiguredValues(BBBCoreConstants.COHERENCE_EXCEPTION_COUNT_THRESHOLD);
		
		if(recoveryTimePeriod != 0){			
			setRecoveryTime(recoveryTimePeriod);	
		}
		
		if(countThreshold != 0){
			setCounterThreshold(countThreshold);
		}
		
		
		long lastRequestTimeOut = 0;
		int globalCounter = 0;
		
		if(this.getLastReqTimeOutMap().containsKey(cacheName)){
			lastRequestTimeOut = this.getLastReqTimeOutMap().get(cacheName);
		}
	
		if(this.getCacheCounterMap().containsKey(cacheName)){
			globalCounter = this.getCacheCounterMap().get(cacheName);
		}
		
		if ((currentTimeStamp - lastRequestTimeOut)> getRecoveryTime()) {  	// if last time exception occur time period is greater 
			this.getCacheCounterMap().put(cacheName, BBBCoreConstants.ONE);										// then recovery time then initialize time and counter 
			this.getLastReqTimeOutMap().put(cacheName, currentTimeStamp);			
			logInfo("CoherenceCacheContainer.coherenceServerDisableCheck() - Initialize counter and last request timeout");
		} else {

			this.getCacheCounterMap().put(cacheName, globalCounter + 1 );									// Increment global counter by 1
			logInfo("CoherenceCacheContainer.coherenceServerDisableCheck() - getGlobalCounter() :: " + globalCounter);
																											
			if (this.getCacheCounterMap().get(cacheName) > getCounterThreshold()) {								// if counter value greater then counter threshold
				this.getCoherenceEnabledMap().put(cacheName, BBBCoreConstants.RETURN_FALSE);					// disable calls to coherence	
				this.getCoherenceDisableTimeStampMap().put(cacheName, currentTimeStamp);
				this.getCacheCounterMap().put(cacheName, BBBCoreConstants.ZERO);
				this.getLastReqTimeOutMap().put(cacheName, (long) BBBCoreConstants.ZERO);
				logInfo("CoherenceCacheContainer.coherenceServerDisableCheck() - Coherence calls are disabled");
			}else{
				this.getLastReqTimeOutMap().put(cacheName, currentTimeStamp);									// set current time to last request time stamp
			}
		}		
		
		logDebug("CoherenceCacheContainer.coherenceServerDisableCheck() - end");
		}
	}

	/**
	 * This method is used to get the configured value from the BCC.
	 *
	 * @param key            in <code>Dtring</code> format
	 * @return the configured values
	 */
	public long getConfiguredValues(String key) {
		
		long result = 0;
		logDebug("CoherenceCacheContainer.getConfiguredValues() - start");
		List<String> resultList = null;
		logDebug("CoherenceCacheContainer.getConfiguredValues() - key :: " + key);
		try {
			resultList = getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.CONTENT_CATALOG_KEYS, key);
		} catch (BBBBusinessException e) {
			logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, e);
		} catch (BBBSystemException bs) {
			logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, bs);
		}
		
		try {
			if (null != resultList && null != resultList.get(0)) {
				result = Long.parseLong(resultList.get(0).toString());
				logDebug("config result :: " + result);
			}
		} catch (NumberFormatException e) {
			logError("CoherenceCacheContainer:getConfiguredValues() :: Number format Exception", e);
		}
		
		logDebug("CoherenceCacheContainer.getConfiguredValues() - start");
		
		return result;		
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#put(java.lang.Object, java.lang.Object, java.lang.String)
	 */
	public void put(Object key, Object value, String pCacheName) {
		boolean containsCacheKey = this.getCoherenceEnabledMap().containsKey(pCacheName);
		
		boolean isCoherenceEnabled = true;
		
		if(containsCacheKey){
			isCoherenceEnabled = this.getCoherenceEnabledMap().get(pCacheName);
		}
		
		if (isCoherenceEnabled) {
			// catching RequestTimeoutException and calling
			// coherenceServerDisableCheck()
			// method to check for disabling coherence server
			try {
				NamedCache nCache = getCache(pCacheName);
				if (nCache != null) {
					nCache.put(key,value);
				}
				 else {
					logDebug("Cache with " + pCacheName + " Not Found.");
				}
			} catch (RequestTimeoutException e) {
				coherenceServerDisableCheck(pCacheName);
				logError(
						"CoherenceCacheContainer.put() :: Catching RequestTimeoutException ",
						e);
			} catch (RequestPolicyException rp) {
				coherenceServerDisableCheck(pCacheName);
				logError(
						"CoherenceCacheContainer.get() :: Catching RequestPolicyException  ",
						rp);
			}
		}

	}
	
	public <K, V> void putAll(Map<K, V> valueMap, String pCacheName) {
		BBBPerformanceMonitor.start("CoherenceCacheContainer", "run");
		logDebug("CoherenceCacheContainer putAll method START..");
		
		boolean containsCacheKey = this.getCoherenceEnabledMap().containsKey(pCacheName);		
		
		boolean isCoherenceEnabled = true;
		
		if(containsCacheKey){
			isCoherenceEnabled = this.getCoherenceEnabledMap().get(pCacheName);
		}

		if (isCoherenceEnabled) {
			try {
				NamedCache nCache = getCache(pCacheName);
				if (nCache != null) {
					nCache.putAll(valueMap);
				} else {
					logDebug("Cache with " + pCacheName + " Not Found.");
				}
			} catch (RequestTimeoutException e) {
				coherenceServerDisableCheck(pCacheName);
				logError("CoherenceCacheContainer.putAll() :: Catching RequestTimeoutException", e);
				BBBPerformanceMonitor.cancel("CoherenceCacheContainer", "run");
			} catch (RequestPolicyException rp) {
				coherenceServerDisableCheck(pCacheName);
				logError("CoherenceCacheContainer.putAll() :: Catching RequestPolicyException",	rp);
				BBBPerformanceMonitor.cancel("CoherenceCacheContainer", "run");
			}
		}
		BBBPerformanceMonitor.end("CoherenceCacheContainer", "run");
		logDebug("CoherenceCacheContainer putAll method END.");		
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#put(java.lang.Object, java.lang.Object, java.lang.String, long)
	 */
	public void put(Object key, Object value, String pCacheName, long time) {
		
		boolean containsCacheKey = this.getCoherenceEnabledMap().containsKey(pCacheName);		
		
		boolean isCoherenceEnabled = true;
		
		if(containsCacheKey){
			isCoherenceEnabled = this.getCoherenceEnabledMap().get(pCacheName);
		}
		

		if (isCoherenceEnabled) {
			// catching RequestTimeoutException and calling
			// coherenceServerDisableCheck()
			// method to check for disabling coherence server
			// This check is added for Checking BrandPage Caching Issue
			if(key!=null && key instanceof String && key.toString().contains("_BrandList")) {
				logDebug("Brand Key:" + key + " Cache data to be put in Coherence for Brand Page is " + ((BrandsListingVO)value).getListBrands());
			}
			try {
				NamedCache nCache = getCache(pCacheName);
				if (nCache != null) {
					nCache.put(key, value, time);
				} else {
					logDebug("Cache with " + pCacheName + " Not Found.");
				}
			} catch (RequestTimeoutException e) {
				coherenceServerDisableCheck(pCacheName);
				logError(
						"CoherenceCacheContainer.put() :: Catching RequestTimeoutException",
						e);
			} catch (RequestPolicyException rp) {
				coherenceServerDisableCheck(pCacheName);
				logError(
						"CoherenceCacheContainer.get() :: Catching RequestPolicyException",
						rp);

			}
		}

	}
	
	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#remove(java.lang.Object, java.lang.String)
	 */
	public boolean remove(Object key, String pCacheName) {
		NamedCache nCache = getCache(pCacheName);
		if (key != null && nCache.containsKey(key)) {
			nCache.remove(key);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#getAllKeys(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	public synchronized Iterator getAllKeys(String pCacheName) {
		NamedCache nCache = getCache(pCacheName);
		return nCache.keySet().iterator();
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void put(Object key, Object value) {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object key) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#getAllKeys()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getAllKeys() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.cache.BBBWebCacheIF#clearCache(java.lang.String)
	 */
	@Override
	public void clearCache(String pCacheName) {
		NamedCache nCache = getCache(pCacheName);
		if (nCache != null) {
			nCache.clear();
			logDebug("Cleared Cache with name " + pCacheName);
		}
	}
	
	/**
	 * Clear cache.
	 *
	 * @param pCacheName the cache name
	 * @param keys the keys
	 */
	public void clearCache(String pCacheName, List<String> keys) {
		NamedCache nCache = getCache(pCacheName);
		if (nCache != null) {
			if (keys != null && !keys.isEmpty()) {
				for (String key : keys) {
					if (nCache.containsKey(key)) {
						nCache.remove(key);
					} else {
						logDebug(pCacheName + " Cache does not contain key: "
								+ key);
					}
				}
			} else {
				nCache.clear();
			}
			logDebug("Cleared Cache with name " + pCacheName);
		}
	}

	/**
	 * Gets the last request time out.
	 *
	 * @return the lastRequestTimeOut
	 */
	public long getLastRequestTimeOut() {
		return lastRequestTimeOut;
	}

	/**
	 * Sets the last request time out.
	 *
	 * @param lastRequestTimeOut            the lastRequestTimeOut to set
	 */
	public void setLastRequestTimeOut(final long lastRequestTimeOut) {
		this.lastRequestTimeOut = lastRequestTimeOut;
	}

	/**
	 * Gets the coherence disable time stamp.
	 *
	 * @return the coherenceDisableTimeStamp
	 */
	public long getCoherenceDisableTimeStamp() {
		return coherenceDisableTimeStamp;
	}

	/**
	 * Sets the coherence disable time stamp.
	 *
	 * @param coherenceDisableTimeStamp            the coherenceDisableTimeStamp to set
	 */
	public void setCoherenceDisableTimeStamp(final long coherenceDisableTimeStamp) {
		this.coherenceDisableTimeStamp = coherenceDisableTimeStamp;
	}

	/**
	 * Gets the global counter.
	 *
	 * @return the globalCounter
	 */
	public int getGlobalCounter() {
		return globalCounter;
	}

	/**
	 * Sets the global counter.
	 *
	 * @param globalCounter            the globalCounter to set
	 */
	public void setGlobalCounter(int globalCounter) {
		this.globalCounter = globalCounter;
	}

	/**
	 * Gets the counter threshold.
	 *
	 * @return the counterThreshold
	 */
	public long getCounterThreshold() {
		return counterThreshold;
	}

	/**
	 * Sets the counter threshold.
	 *
	 * @param counterThreshold            the counterThreshold to set
	 */
	public void setCounterThreshold(final long counterThreshold) {
		this.counterThreshold = counterThreshold;
	}

	/**
	 * Gets the coherence enable threshold time.
	 *
	 * @return the coherenceEnableThresholdTime
	 */
	public long getCoherenceEnableThresholdTime() {
		return coherenceEnableThresholdTime;
	}

	/**
	 * Sets the coherence enable threshold time.
	 *
	 * @param coherenceEnableThresholdTime            the coherenceEnableThresholdTime to set
	 */
	public void setCoherenceEnableThresholdTime(
			long coherenceEnableThresholdTime) {
		this.coherenceEnableThresholdTime = coherenceEnableThresholdTime;
	}

	/**
	 * Gets the recovery time.
	 *
	 * @return the recoveryTime
	 */
	public long getRecoveryTime() {
		return recoveryTime;
	}

	/**
	 * Sets the recovery time.
	 *
	 * @param recoveryTime            the recoveryTime to set
	 */
	public void setRecoveryTime(final long recoveryTime) {
		this.recoveryTime = recoveryTime;
	}

	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Checks if is coherence optimization enable.
	 *
	 * @return the coherenceOptimizationEnable
	 */
	public boolean isCoherenceOptimizationEnable() {
		return coherenceOptimizationEnable;
	}

	/**
	 * Sets the coherence optimization enable.
	 *
	 * @param coherenceOptimizationEnable the coherenceOptimizationEnable to set
	 */
	public void setCoherenceOptimizationEnable(final boolean coherenceOptimizationEnable) {
		this.coherenceOptimizationEnable = coherenceOptimizationEnable;
	}

	/**
	 * Gets the coherence enabled map.
	 *
	 * @return the coherence enabled map
	 */
	public Map<String, Boolean> getCoherenceEnabledMap() {
		return coherenceEnabledMap;
	}

	/**
	 * Sets the coherence enabled map.
	 *
	 * @param coherenceEnabledMap the coherence enabled map
	 */
	public void setCoherenceEnabledMap(Map<String, Boolean> coherenceEnabledMap) {
		this.coherenceEnabledMap = coherenceEnabledMap;
	}
	
	/**
	 * Gets the last req time out map.
	 *
	 * @return the last req time out map
	 */
	public Map<String, Long> getLastReqTimeOutMap() {
		return lastReqTimeOutMap;
	}

	/**
	 * Sets the last req time out map.
	 *
	 * @param lastReqTimeOutMap the last req time out map
	 */
	public void setLastReqTimeOutMap(Map<String, Long> lastReqTimeOutMap) {
		this.lastReqTimeOutMap = lastReqTimeOutMap;
	}

	/**
	 * Gets the cache counter map.
	 *
	 * @return the cache counter map
	 */
	public Map<String, Integer> getCacheCounterMap() {
		return cacheCounterMap;
	}

	/**
	 * Sets the cache counter map.
	 *
	 * @param cacheCounterMap the cache counter map
	 */
	public void setCacheCounterMap(Map<String, Integer> cacheCounterMap) {
		this.cacheCounterMap = cacheCounterMap;
	}

	/**
	 * Gets the coherence disable time stamp map.
	 *
	 * @return the coherence disable time stamp map
	 */
	public Map<String, Long> getCoherenceDisableTimeStampMap() {
		return coherenceDisableTimeStampMap;
	}

	/**
	 * Sets the coherence disable time stamp map.
	 *
	 * @param coherenceDisableTimeStampMap the coherence disable time stamp map
	 */
	public void setCoherenceDisableTimeStampMap(
			Map<String, Long> coherenceDisableTimeStampMap) {
		this.coherenceDisableTimeStampMap = coherenceDisableTimeStampMap;
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.dms.patchbay.MessageSink#receiveMessage(java.lang.String,
	 * javax.jms.Message)
	 */
	@Override
	public void receiveMessage(String pPortName, Message pMessage)
			throws JMSException {
		if (!(pMessage instanceof ObjectMessage))
			throw new MessageFormatException("ERROR: Not an ObjectMessage");

		ObjectMessage objMessage = (ObjectMessage) pMessage;
		Object obj = objMessage.getObject();

		logDebug((new StringBuilder()).append("Received ")
				.append(obj.toString()).append(" on port ").append(pPortName)
				.toString());

		if (obj instanceof CacheInvalidationMessage) {
			processCacheInvalidation((CacheInvalidationMessage) obj);
		} else {
			logDebug("CoherenceCacheContainer.receiveMessage() | Not a InvalidationMessage on port "
					+ pPortName + " is an instance of " + pMessage.getClass());
		}
	}

	/**
	 * Process cache invalidation.
	 *
	 * @param nearCacheInvalidationMessage the near cache invalidation message
	 */
	private void processCacheInvalidation(
			CacheInvalidationMessage nearCacheInvalidationMessage) {
		clearNearCache(nearCacheInvalidationMessage.getEntryKey(), nearCacheInvalidationMessage.getKeys());
	}

	/**
	 * Clear near cache.
	 *
	 * @param cacheName the cache name
	 * @param keys the keys
	 */
	public void clearNearCache(final String cacheName, final List<String> keys) {
		NearCache nCache = (NearCache) CacheFactory.getCache(cacheName);
		if (nCache != null) {
			if (keys != null && !keys.isEmpty()) {
				for (String key : keys) {
					if (nCache.containsKey(key)) {
						nCache.getFrontMap().remove(key);
					}
				}
			} else {
				nCache.getFrontMap().clear();
			}
		}
	}
	
	/**
	 * Gets the back cache size.
	 *
	 * @param pCacheName the cache name
	 * @return the back cache size
	 */
	public int getBackCacheSize(String pCacheName) {
		NamedCache nCache = getCache(pCacheName);
		if (nCache != null) {
			return nCache.size();
		}
		return 0;
	}
	
	/**
	 * Gets the all cache keys.
	 *
	 * @param pCacheName the cache name
	 * @return the all cache keys
	 */
	@SuppressWarnings("rawtypes")
	public synchronized Iterator getAllCacheKeys(String pCacheName) {
		NearCache nCache = (NearCache) CacheFactory.getCache(pCacheName);
		return nCache.keySet().iterator();
	}

	
	


}
