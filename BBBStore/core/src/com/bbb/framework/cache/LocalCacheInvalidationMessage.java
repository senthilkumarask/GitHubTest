package com.bbb.framework.cache;

import atg.nucleus.dms.DASMessage;

/**
 * @author BBB
 *
 */
public class LocalCacheInvalidationMessage extends DASMessage {

	private static final long serialVersionUID = 1L;
    private String entryKey;
    private String cacheType;
    private boolean paternMatch = true;

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

	/**
     * @return mEntryKey
     */
    public String getEntryKey() {
		return this.entryKey;
	}

	/**
	 * @param entryKey
	 */
	public void setEntryKey(String entryKey) {
		this.entryKey = entryKey;
	}

	/**
	 * @return mPaternMatch
	 */
	public boolean isPaternMatch() {
		return this.paternMatch;
	}

	/**
	 * @param paternMatch
	 */
	public void setPaternMatch(boolean paternMatch) {
		this.paternMatch = paternMatch;
	}

	/**
	 * @param pCacheType
	 * @param pPaternMatch
	 * @param pEntryKey 
	 */
	public LocalCacheInvalidationMessage(String pCacheType, boolean pPaternMatch , String pEntryKey){
    	this.cacheType = pCacheType;
    	this.paternMatch = pPaternMatch;
    	this.entryKey = pEntryKey;
    }
	/**
	 * @param pEntryKey
	 */
	public LocalCacheInvalidationMessage(String pEntryKey){
    	this.entryKey = pEntryKey;
    }
	
	@Override
	public String toStringProperties(){
		return "cacheType=" + this.cacheType + " ,paternMatch=" + this.paternMatch + ",entryKey=" + this.entryKey;
	}
}
