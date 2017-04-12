package com.bbb.framework.cache;

import java.util.Iterator;
/**
 * BBBWebCacheIF interface will be used to provide wraper around the Cache service to be used by BBBDropletCache.
 * The component implementing this interface could be configured to provide caching service.
 *
 * @author pprave
 *
 */
public interface BBBWebCacheIF {

	public Object get(Object key);

	public void put(Object key, Object value);

	public boolean remove(Object key);
	
	public Iterator getAllKeys();
	
	public Object get(Object key, String pCacheName);

	public void put(Object key, Object value, String pCacheName);
	
	public void put(Object key, Object value, String pCacheName, long timeout);

	public boolean remove(Object key, String pCacheName);

	public Iterator getAllKeys(String pCacheName);
	
	public void clearCache(String pCacheName);
}
