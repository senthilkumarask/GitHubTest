/**
 * 
 */
package com.bbb.framework.cache;

import java.util.Iterator;

import atg.service.cache.Cache;

/**
 * @author alakra
 *
 */
public class BBBATGCache extends Cache{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6324264204211005078L;

	public Object get(Object pKey, String pCacheName) {
		Object obj=null;
		try {
			obj = super.get(pKey);
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Error while retrieving from cache ", e);
			}
		}
		return obj;
	}

	public void put(Object pKey, Object pValue, String pCacheName) {
		super.put(pKey, pValue);		
	}

	public void put(Object pKey, Object pValue, String pCacheName, long pTimeout) {
		super.put(pKey, pValue);
	}

	public boolean remove(Object pKey, String pCacheName) {
		return super.remove(pKey);
	}

	@SuppressWarnings("rawtypes")
	public Iterator getAllKeys(String pCacheName) {
		return super.getAllKeys();
	}
}