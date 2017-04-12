package com.bbb.cache.tibco.vo;

import java.util.ArrayList;
import java.util.List;

import com.bbb.framework.integration.ServiceRequestBase;


public class CacheInvalidatorVO extends ServiceRequestBase{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8862369606826625021L;
	
	private boolean mClearDropletCache;
	private boolean mClearObjectCache;
	private List<String> mObjectCacheTypes = new ArrayList<String>();
	/**
	 * @return the mClearDropletCache
	 */
	public boolean isClearDropletCache() {
		return mClearDropletCache;
	}
	/**
	 * @param mClearDropletCache the mClearDropletCache to set
	 */
	public void setClearDropletCache(boolean pClearDropletCache) {
		this.mClearDropletCache = pClearDropletCache;
	}
	/**
	 * @return the mClearObjectCache
	 */
	public boolean isClearObjectCache() {
		return mClearObjectCache;
	}
	/**
	 * @param mClearObjectCache the mClearObjectCache to set
	 */
	public void setClearObjectCache(boolean pClearObjectCache) {
		this.mClearObjectCache = pClearObjectCache;
	}
	/**
	 * @return the mObjectCacheTypes
	 */
	public List<String> getObjectCacheTypes() {
		return mObjectCacheTypes;
	}
	/**
	 * @param mObjectCacheTypes the mObjectCacheTypes to set
	 */
	public void setObjectCacheTypes(List<String> pObjectCacheTypes) {
		this.mObjectCacheTypes = pObjectCacheTypes;
	}
	
	private String mServiceName = "cacheInvalidatorJMSMessage";
	
	@Override
	public String getServiceName() {
		return mServiceName;
	}
	
	public void setServiceName(String pServiceName) {
		this.mServiceName = pServiceName;
	}
}
