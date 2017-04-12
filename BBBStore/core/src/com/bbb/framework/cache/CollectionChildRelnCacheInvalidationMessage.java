package com.bbb.framework.cache;

import java.io.Serializable;

import atg.nucleus.dms.DASMessage;

public class CollectionChildRelnCacheInvalidationMessage extends DASMessage implements Serializable {

	private static final long serialVersionUID = -4791562578930066086L;
    private String cacheType;
    
   
	/**
	 * @return the cacheType
	 */
	public String getCacheType() {
		return cacheType;
	}

	/**
	 * @param cacheType the cacheType to set
	 */
	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	
	public CollectionChildRelnCacheInvalidationMessage(String cacheType) {
		this.cacheType = cacheType;
	}


}
