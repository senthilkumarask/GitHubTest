package com.bbb.commerce.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session scope component
 * 
 * @author manohar
 * @story UC_checkout_billing
 * @created 12/2/2011
 */
public class BBBStoreInventoryContainer {

	private Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<>();
	private Map<String, Integer> networkInventoryMap = new ConcurrentHashMap<>();
	private Map<String, Integer> sddStoreInventoryMap = new ConcurrentHashMap<>();

	/**
	 * @return
	 */
	public Map<String, Integer> getStoreIdInventoryMap() {
		return mStoreIdInventoryMap;
	}

	/**
	 * @param mStoreIdInventoryMap
	 */
	public void setStoreIdInventoryMap(Map<String, Integer> mStoreIdInventoryMap) {
		this.mStoreIdInventoryMap = mStoreIdInventoryMap;
	}

	/**
	 * @return the networkInventoryMap
	 */
	public Map<String, Integer> getNetworkInventoryMap() {
		return networkInventoryMap;
	}

	/**
	 * @param networkInventoryMap
	 *            the networkInventoryMap to set
	 */
	public void setNetworkInventoryMap(Map<String, Integer> networkInventoryMap) {
		this.networkInventoryMap = networkInventoryMap;
	}

	/**
	 * @return the sddStoreInventoryMap
	 */
	public Map<String, Integer> getSddStoreInventoryMap() {
		return sddStoreInventoryMap;
	}

	/**
	 * @param sddStoreInventoryMap
	 *            the sddStoreInventoryMap to set
	 */
	public void setSddStoreInventoryMap(Map<String, Integer> sddStoreInventoryMap) {
		this.sddStoreInventoryMap = sddStoreInventoryMap;
	}

}
