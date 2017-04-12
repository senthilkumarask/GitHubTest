package com.bbb.framework.cache;

import java.util.Map;

import atg.droplet.Cache;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;

/**
 * This class implements interface methods to provide local layer for
 * atg.service.Cache. for caching dynamic repository data
 * 
 * @author ssi191
 * 
 */

@SuppressWarnings("rawtypes")
public class BBBDynamicPriceCacheContainer extends Cache {
	private static final String PRODUCT_OBJECT="productObject";
	private static final String SKU_OBJECT="skuObject";
	/*changing the type of cacheContainer because now only Coherence cache will be used*/
	private CoherenceCacheContainer cacheContainer;
	private boolean enabled;
	

	public Object get(Object key) {
		Object cacheObject = null;
		if (this.isEnabled()) {

			long getCacheStartTime = 0;
			long getCacheEndTime = 0;
			String cacheName=null;
			if (getCacheContainer() instanceof CoherenceCacheContainer) {
				
				
				if(key.toString().contains("product")){
				 cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_PRODUCT);
				}else{
				 cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_SKU);
				}
				getCacheStartTime = System.currentTimeMillis();

				cacheObject = getCacheContainer().get(key, cacheName);
				;

				getCacheEndTime = System.currentTimeMillis();

				if (cacheObject != null) {
					if (isLoggingDebug()) {
						logDebug(new StringBuffer(
								"DynamicPriceCacheContainer.get | cacheContainer is CoherenceCacheContainer: Object for key: ")
								.append(key)
								.append(" found in cache ")
								.append(cacheName)
								.append(". Total time taken to search the object ")
								.append((getCacheEndTime - getCacheStartTime))
								.toString());
					}
				} else {
					if (isLoggingDebug()) {
						logDebug(new StringBuffer(
								"DynamicPriceCacheContainer.get | cacheContainer is CoherenceCacheContainer : Object for key: ")
								.append(key)
								.append(" not found in cache ")
								.append(cacheName)
								.append(". Total time taken to search the object ")
								.append((getCacheEndTime - getCacheStartTime))
								.toString());
					}
				}
				return cacheObject;
			}

			getCacheStartTime = System.currentTimeMillis();

			cacheObject = getCacheContainer().get(key);

			getCacheEndTime = System.currentTimeMillis();

			if (cacheObject != null) {
				if (isLoggingDebug()) {
					logDebug(new StringBuffer(
							"DynamicPriceCacheContainer.get | cacheContainer is BBBLocalCacheContainer: Object for key: ")
							.append(key)
							.append(". Total time taken to search the object ")
							.append((getCacheEndTime - getCacheStartTime))
							.toString());
				}
			} else {
				if (isLoggingDebug()) {
					logDebug(new StringBuffer(
							"DynamicPriceCacheContainer.get | cacheContainer is BBBLocalCacheContainer : Object for key: ")
							.append(key)
							.append(". Total time taken to search the object ")
							.append((getCacheEndTime - getCacheStartTime))
							.toString());
				}

			}

		} else {
			logDebug("DynamicPriceCacheContainer is not enabled for caching");
		}
		return cacheObject;
	}

	public void put(Object key, Object value,String objectType) {
		
		if (this.isEnabled()) {
			long putCacheStartTime = 0;
			long putCacheEndTime = 0;
			String cacheName=null;
			

			if (isLoggingDebug()) {
				logDebug("Adding item to cache\nkey=" + key + "\nvalue="
						+ value);
			}

			if (getCacheContainer() instanceof CoherenceCacheContainer) {
				if(PRODUCT_OBJECT.equalsIgnoreCase(objectType)){
				 cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_PRODUCT);
				}else if(SKU_OBJECT.equalsIgnoreCase(objectType)){
				 cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_SKU);
				}
		//	long timeout =Long.parseLong(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_TIMEOUT));
			putCacheStartTime = System.currentTimeMillis();
			getCacheContainer().put(key, value, cacheName);
			putCacheEndTime = System.currentTimeMillis();

				if (isLoggingDebug()) {
					logDebug(new StringBuffer(
							"DynamicPriceCacheContainer.put | cacheContainer is CoherenceCacheContainer : Object for key ")
							.append(key).append(" is put in cache")
							.append(cacheName).append(" with expiry timeout=")
							.append(". Total time taken to put the object ")
							.append((putCacheEndTime - putCacheStartTime))
							.toString());
				}

				return;
			}

			putCacheStartTime = System.currentTimeMillis();
			getCacheContainer().put(key, value);
			putCacheEndTime = System.currentTimeMillis();

			if (isLoggingDebug()) {
				logDebug(new StringBuffer(
						"DynamicPriceCacheContainer.put | cacheContainer is BBBLocalCacheContainer : Object for key ")
						.append(key)
						.append(" is put in BBBLocalCacheContainer. Total time taken to put the object ")
						.append((putCacheEndTime - putCacheStartTime))
						.toString());
			}
		} else {
			logDebug("DynamicPriceCacheContainer is not enabled for caching");
		}
	}

	

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void bulkLoad(Map buffer, String objectType) {
		if (this.isEnabled()) {
			long putCacheStartTime = 0;
			long putCacheEndTime = 0;
			String cacheName=null;
			int count = 0;
			if (getCacheContainer() instanceof CoherenceCacheContainer) {
				if(PRODUCT_OBJECT.equalsIgnoreCase(objectType)){
				 cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_PRODUCT);
				}else if(SKU_OBJECT.equalsIgnoreCase(objectType)){
				 cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_NAME_SKU);
				}
		//	long timeout =Long.parseLong(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,BBBCoreConstants.DYNAMIC_REPOSITORY_CACHE_TIMEOUT));
			putCacheStartTime = System.currentTimeMillis();
			getCacheContainer().putAll(buffer, cacheName);
			putCacheEndTime = System.currentTimeMillis();

				if (isLoggingDebug()) {
					logDebug(new StringBuffer(
							"DynamicPriceCacheContainer.put | cacheContainer is CoherenceCacheContainer : Object for key ")
							.append(cacheName).append(" with expiry timeout=")
							.append(". Total time taken to put the object ")
							.append((putCacheEndTime - putCacheStartTime))
							.toString());
				}

				return;
			}

			
			putCacheEndTime = System.currentTimeMillis();

			if (isLoggingDebug()) {
				logDebug(new StringBuffer(
						"DynamicPriceCacheContainer.put | cacheContainer is BBBLocalCacheContainer : Object for key ")
						.append(" is put in BBBLocalCacheContainer. Total time taken to put the object ")
						.append((putCacheEndTime - putCacheStartTime))
						.toString());
			}
		} else {
			logDebug("DynamicPriceCacheContainer is not enabled for caching");
		}
		
	}

	/**
	 * @return the cacheContainer
	 */
	public final CoherenceCacheContainer getCacheContainer() {
		return cacheContainer;
	}

	/**
	 * @param cacheContainer the cacheContainer to set
	 */
	public final void setCacheContainer(CoherenceCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}
}
