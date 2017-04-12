package com.bbb.framework.cache.vo;

import java.io.Serializable;

public class MobileNearCacheVO implements Serializable{
	
	String size;
	String errorMessage;
	String errorExists;
	String cacheName;
	
	
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the isErrorExists
	 */
	public String getErrorExists() {
		return errorExists;
	}
	/**
	 * @param isErrorExists the isErrorExists to set
	 */
	public void setErrorExists(String errorExists) {
		this.errorExists = errorExists;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return cacheName;
	}
	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MobileNearCacheVO [size=" + size + ", errorMessage=" + errorMessage
				+ ", isErrorExists=" + errorExists + ", cacheName="
				+ cacheName + "]";
	}
	
}
