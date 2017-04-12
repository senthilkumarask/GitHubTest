package com.bbb.framework.cache;

import java.io.Serializable;

import atg.nucleus.dms.DASMessage;

public class CategoryRedirectURLCacheInvalidationMessage extends DASMessage implements Serializable{

	private static final long serialVersionUID = 2563218553664361345L;
	private String CacheType;
	
	/**
	 * @return the cacheType
	 */
	public String getCacheType() {
		return CacheType;
	}
	
	/**
	 * @param cacheType the cacheType to set
	 */
	public void setCacheType(String cacheType) {
		CacheType = cacheType;
	}
	
	public CategoryRedirectURLCacheInvalidationMessage(String CacheType) {
		this.CacheType = CacheType;
	}
	

}
