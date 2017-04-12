package com.bbb.framework.cache;

import java.util.Iterator;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import weblogic.utils.collections.ConcurrentHashMap;

import com.bbb.common.BBBGenericService;

import atg.dms.patchbay.MessageSink;


/**
 * This class implements interface methods to provide local layer for atg.service.Cache.
 * for caching configure keys and label , text areas and error messages
 *
 * @author BBB
 *
 */

@SuppressWarnings("rawtypes")
public class BBBLocalCacheContainer extends BBBGenericService implements BBBWebCacheIF, MessageSink {

	private static final String ALL_CACHE = "AllCache";
	private int maxLimit;
	private boolean enabled;
	private String cacheType;
	private Map mMap = new ConcurrentHashMap();
	
	@SuppressWarnings("unchecked")
	@Override
	public void put(Object key, Object value) {
		logDebug("BBBLocalCacheContainer putting -- key : "+ key + " and value : " + value);
		this.mMap.put(key, value);
	}

	@Override
	public Object get(Object key){
		Object obj=null;
		if(this.getEnabled()){
			logDebug("BBBLocalCacheContainer getting value for the key : "+key);
			try {
				obj = this.mMap.get(key);
			} catch (Exception e) {
				logError("BBBLocalCacheContainer.get(Object key) :: Exception - ",e);
			}
		} else {
			logDebug("BBB Local Cache is not enabled");
		}
		return obj;
	}

	@Override
	public void receiveMessage(String pPortName,  Message pMessage) throws JMSException {
        if(!(pMessage instanceof ObjectMessage))
            throw new MessageFormatException("Not an ObjectMessage");
        ObjectMessage objMessage = (ObjectMessage)pMessage;
        Object obj = objMessage.getObject();
        logDebug((new StringBuilder()).append("Received ").append(obj.toString()).append(" on port ").append(pPortName).toString());
        if(obj instanceof LocalCacheInvalidationMessage) {
        	processCacheInvalidation((LocalCacheInvalidationMessage)obj);
        } else {
            logDebug("BBBLocalCacheContainer.receiveMessage() | Not a LocalCacheInvalidationMessage" + " on port " + pPortName + " is an instance of " + pMessage.getClass() + " Hence ignore this function");
        }
    }

	private void processCacheInvalidation(LocalCacheInvalidationMessage localCacheInvalidationMessage){
		
		logDebug("START:BBBLocalCacheContainer-processCacheInvalidation for LocalCacheInvalidationMessage");
		StringBuffer stbuff = new StringBuffer();
		long removeCacheStartTime = System.currentTimeMillis();
		
		if(localCacheInvalidationMessage.isPaternMatch() 
				&& this.getCacheType().equalsIgnoreCase(localCacheInvalidationMessage.getCacheType().toLowerCase()) 
				&& ALL_CACHE.equalsIgnoreCase(localCacheInvalidationMessage.getEntryKey().toLowerCase())){
			logDebug("---- clearing all cache for cache type " + this.getCacheType() + " ----");
			clearCache();
		} else {
			if (remove(localCacheInvalidationMessage.getEntryKey())){
				stbuff.append(localCacheInvalidationMessage.getEntryKey());
			}
		}
		long removeCacheEndTime = System.currentTimeMillis();
		
		logDebug("END:BBBLocalCacheContainer.processCacheInvalidation() |  invoked by LocalCacheInvalidationMessage for " 
					+ localCacheInvalidationMessage.getEntryKey() +" following key removed : " + stbuff.toString()
					+ ". Total time take="+ (removeCacheEndTime-removeCacheStartTime));
		
	}

	@Override
	public Object get(Object key, String pCacheName) {
		return this.get(key);
	}

	@Override
	public void put(Object key, Object value, String pCacheName) {
		this.put(key, value);		
	}

	@Override
	public void put(Object key, Object value, String pCacheName, long timeout) {
		if(this.getEnabled()) {
			if(this.mMap.size() >= this.getMaxLimit()){
				return;
			}
			this.put(key, value);
		} else {
			logDebug("BBB Local Cache is not enabled for cacheName = " + pCacheName);
		}
	}

	@Override
	public boolean remove(Object key, String pCacheName) {
		return this.remove(key);
	}

	  /**
	  *   description: Returns all keys in the Map.
	  **/  
	 @Override
	public Iterator getAllKeys(){
	   return this.mMap.keySet().iterator();
	 }

	/**
	 * 
	 */
	public void clearCache() {
		this.mMap.clear ();
		if(isLoggingInfo()){
			logDebug("Local Cache cleared");
		}
	}
	
	@Override
	public boolean remove(Object key) {
		if(key != null) {
			this.mMap.remove(key);
			return true;
		}
		return false;
	}

	@Override
	public Iterator getAllKeys(String pCacheName) {
		return this.getAllKeys();
	}

	@Override
	public void clearCache(String pCacheName) {
		this.clearCache();
	}

	/**
	 * @return the maxConfigLimit
	 */
	public int getMaxLimit() {
		return this.maxLimit;
	}

	/**
	 * @param maxLimit the maxLimit to set
	 */
	public void setMaxLimit(int maxLimit) {
		this.maxLimit = maxLimit;
	}

	/**
	 * @return the enabled
	 */
	public boolean getEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the cacheType
	 */
	public String getCacheType() {
		return this.cacheType;
	}

	/**
	 * @param cacheType the cacheType to set
	 */
	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}
}
