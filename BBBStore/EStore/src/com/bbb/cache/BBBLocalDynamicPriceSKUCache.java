package com.bbb.cache;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import atg.nucleus.ServiceException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO;
import com.bbb.utils.BBBConfigRepoUtils;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.filter.AlwaysFilter;
import com.tangosol.util.filter.LimitFilter;

/**
 * This class is used to Load SKU Dynamic Price Cache
 * on jvm startup. This is also helper class for BBBLocalDynamicSKUPriceLoader Scheduler
 * which would load the local cache on regular schedule to synch Local Cache with DynamicRepository 
 * which can be modified after PIM/Price Feed deployments
 * 
 * @author ikhan2
 *
 */
public class BBBLocalDynamicPriceSKUCache extends BBBGenericService {

	/** Global map that represents local cache for SKU Dynamic Prices*/
	private Map<String, BBBDynamicPriceSkuVO> skuDynamicPricesMap;
	/** catalogTools component*/
	private BBBCatalogTools catalogTools;

	/**
	 * Check if cache is ready.
	 */
	private boolean skuCacheReady;
	
	/**
	 * cache enable property
	 */
    private boolean cacheEnabled;
    
    /** load sku cache on startup */
    private boolean loadCacheOnStartUp;
    
    /** Cache batch size*/
    private int cacheBatchSize;
    
    /**
     * Build dynamic cache from back cache 
     * 
     * @return Map<Long, BBBDynamicPriceSkuVO>
     * @throws BBBBusinessException
     * @throws SQLException
     */
	public Map<String, BBBDynamicPriceSkuVO> buildLocalDynamicSKUPriceCache() throws BBBBusinessException, SQLException {
		
        vlogDebug("Enter BBBLocalDynamicPriceSKUCache.buildLocalDynamicSKUPriceCache()");
		return loadCacheFromCoherence();
	}
	

	/**
	 * Load local dynamic price cache from coherence
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, BBBDynamicPriceSkuVO>  loadCacheFromCoherence() throws BBBBusinessException, SQLException{
		
		long startTime = System.currentTimeMillis();
		Map<String, BBBDynamicPriceSkuVO> skuDynamicPricesMap = new ConcurrentSkipListMap<String, BBBDynamicPriceSkuVO>();
		BBBDynamicPriceSkuVO skuPriceVo = null;

		logInfo("BBBLocalDynamicPriceSKUCache | Entering loadCacheFromCoherence()");
		BBBPerformanceMonitor.start("BBBLocalDynamicPriceSKUCache",	"loadCacheFromCoherence");

		String cacheName = getCatalogTools().getConfigKeyValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_SKU,"dynamic-repository-near-cache-sku") ;
				
		NamedCache cache = CacheFactory.getCache(cacheName);
		
		if(cache == null) {
			vlogError(cacheName+" Cache is null. Not populating local cache.");
			return skuDynamicPricesMap;
		}
		// Coherence query filter to fetch all records in batches of 1000
		LimitFilter limitFilter = new LimitFilter(AlwaysFilter.INSTANCE, cacheBatchSize);
        //vlogInfo("No of entries found in cache are "+cache.entrySet().size());
		
		while (true) {
		        // Read 1000 entries from backing map
		        Set<Map.Entry<String,BBBDynamicPriceSkuVO>> entries = cache.entrySet(limitFilter);
		                                      
		        if (entries == null || entries.size() == 0)
		             break;
		        
		        // Populate the read data into local map
		        for (Map.Entry<String, BBBDynamicPriceSkuVO> entry : entries) {
		        	
		                String skuId = entry.getKey();
		                BBBDynamicPriceSkuVO dynamicPriceSkuVO = entry.getValue();
		                vlogDebug("Processing Sku "+skuId+" and dynamic cache values are "+dynamicPriceSkuVO);
		                skuDynamicPricesMap.put(skuId, dynamicPriceSkuVO);
		                
		        }
		        //Paginate the query filter to read next thousand
		        limitFilter.nextPage();
		};

		if (!skuDynamicPricesMap.isEmpty()){
			setSkuCacheReady(true);
			logInfo("BBBLocalDynamicPriceSKUCache.buildLocalDynamicSKUPriceCache : "
					+ "Local DynamicPrice Cache rebuilt with size="+ skuDynamicPricesMap.size());
		}

		long endTime = System.currentTimeMillis();
		logInfo("BBBLocalDynamicPriceSKUCache | Time taken to build local cache="+ (endTime-startTime));
		
		logInfo("BBBLocalDynamicPriceSKUCache | Exiting loadCacheFromCoherence()");
		BBBPerformanceMonitor.end("BBBLocalDynamicPriceSKUCache",	"loadCacheFromCoherence");
		return skuDynamicPricesMap;

		
	}

	/**
	 * This method is called on component startup.
	 */
	@Override
	public void doStartService() {
		logDebug("BBBLocalDynamicPriceSKUCache.doStartService : Start");
		
		boolean enable = this.getCatalogTools().isLocalSKUDynamicCacheEnabled();
		setCacheEnabled(enable);
		setSkuCacheReady(false);
		/*
		 * load cache on startup only when this is configured in case there comes coherence contention issue 
		 * as multiple JVMs will put load on coherence to fetch the SKU cache 
		 */
		if(isLoadCacheOnStartUp()){
			prepareLocalCacheForSKUDynamicPricing();
		}
		logDebug("BBBLocalDynamicPriceSKUCache.doStartService : End");
	}
	
	/**
	 * This method caches DynamicPricing Data for SKUs
	 */
	private void prepareLocalCacheForSKUDynamicPricing() {
		logInfo("BBBLocalDynamicPriceSKUCache.prepareLocalCacheForSKUDynamicPricing : Start");
		try {
			if (isCacheEnabled()) {
				logInfo("Local dynamic sku price cache enabled: " + isCacheEnabled());
				setSkuCacheReady(false);
				this.clearSKUPriceCache();
				setSkuDynamicPricesMap(buildLocalDynamicSKUPriceCache());
			}
		} catch (Exception e) {
			logError("BBBLocalDynamicPriceSKUCache: Error in preparing local dynamic price cache"+e);
		}
		logInfo("BBBLocalDynamicPriceSKUCache.prepareLocalCacheForSKUDynamicPricing : End");
	}
	
	/**
	 * Scheduler start service to build Local DynamicPrice Cache for SKUs
	 */
	@Override
	public void doStopService() throws ServiceException {
		logDebug("BBBLocalDynamicPriceSKUCache.doStopService : Start");
		if (isCacheEnabled()) {
			this.clearSKUPriceCache();;
		}
		super.doStopService();
		logDebug("BBBLocalDynamicPriceSKUCache.doStopService : End");
	}
	
	/**
	 * This method rebuilds local dynamic price SKU cache.
	 */
	public void doRebuildLocalDynamicPriceSKUCache() {
		logDebug("BBBLocalDynamicPriceSKUCache.doRebuildLocalDynamicPriceSKUCache : Start");
		if (isCacheEnabled()) {
			this.clearSKUPriceCache();
			try {
				setSkuDynamicPricesMap(buildLocalDynamicSKUPriceCache());
			} catch (Exception e) {
				logError("Error in rebuilding cache in BBBLocalDynamicPriceSKUCache cache : ", e);
			}
		}
		logDebug("BBBLocalDynamicPriceSKUCache.doRebuildLocalDynamicPriceSKUCache : End");
	}
	
	/**
	 * This method clears the cache.
	 */
	private void clearSKUPriceCache() {
		logDebug("BBBLocalDynamicPriceSKUCache.clearSKUPriceCache : Start");
		setSkuCacheReady(false);
		if(getSkuDynamicPricesMap()!=null){
			getSkuDynamicPricesMap().clear();
		}
		logDebug("BBBLocalDynamicPriceSKUCache.clearSKUPriceCache : End");
	}
	
	/**
	 * @param Lookup method in the Local SKU DynamicPrice Cache
	 * @return the BBBDynamicPriceSkuVO for the SKU
	 */
	public BBBDynamicPriceSkuVO lookUPSKUItemInCache(String skuID) {
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.LOOKUP_LOCAL_DYN_SKU_CACHE, 
				BBBCoreConstants.BUILD_LOCAL_DYN_SKU_CAHE);
		logDebug("BBBLocalDynamicPriceSKUCache.lookUPSKUItemInCache : Start");
		BBBDynamicPriceSkuVO dnamicPriceSkuVO = null;
		try {
			ConcurrentSkipListMap<String, BBBDynamicPriceSkuVO> localCache =
					(ConcurrentSkipListMap<String, BBBDynamicPriceSkuVO>) this.getSkuDynamicPricesMap();
			if (localCache != null) {
				dnamicPriceSkuVO = localCache.get(skuID);
				logDebug("BBBLocalDynamicPriceSKUCache.lookUPSKUItemInCache : fetched for SKUID="+skuID+ " dnamicPriceSkuVO="+dnamicPriceSkuVO);
			}
		} catch (Exception e) {
			logError("Error fecthing SKU Detail Price Info from skuDynamicPricesMap: ", e);
		}
		
		logDebug("BBBLocalDynamicPriceSKUCache.lookUPSKUItemInCache : End");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.LOOKUP_LOCAL_DYN_SKU_CACHE, 
				BBBCoreConstants.BUILD_LOCAL_DYN_SKU_CAHE);
		return dnamicPriceSkuVO;
	}
	
	/**
	 * @return the skuDynamicPricesMap
	 */
	public Map<String, BBBDynamicPriceSkuVO> getSkuDynamicPricesMap() {
		return skuDynamicPricesMap;
	}

	/**
	 * @param skuDynamicPricesMap the usSKUDynamicPricesMap to set
	 */
	public void setSkuDynamicPricesMap(Map<String, BBBDynamicPriceSkuVO> pSkuDynamicPricesMap) {
		this.skuDynamicPricesMap = pSkuDynamicPricesMap;
	}

	/**
	 * 
	 * @return the skuCacheReady
	 */
	public boolean isSkuCacheReady() {
		return skuCacheReady;
	}

	/**
	 * 
	 * @param skuCacheReady the skuCacheReady to set
	 */
	public void setSkuCacheReady(boolean ipCacheReady) {
		this.skuCacheReady = ipCacheReady;
	}

	/**
	 * @return the cacheEnabled
	 */
	public boolean isCacheEnabled() {
		return getCatalogTools().isLocalSKUDynamicCacheEnabled();
	}

	/**
	 * @param cacheEnabled the cacheEnabled to set
	 */
	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 *  
	 * @return loadCacheOnStartUp
	 */
	public boolean isLoadCacheOnStartUp() {
		return this.loadCacheOnStartUp;
	}

	/**
	 * 
	 * @param loadCacheOnStartUp
	 */
	public void setLoadCacheOnStartUp(boolean loadCacheOnStartUp) {
		this.loadCacheOnStartUp = loadCacheOnStartUp;
	}


	/**
	 * @return cacheBatchSize
	 */
	public int getCacheBatchSize() {
		return cacheBatchSize;
	}


	/**
	 * @param cacheBatchSize
	 */
	public void setCacheBatchSize(int cacheBatchSize) {
		this.cacheBatchSize = cacheBatchSize;
	}
}

