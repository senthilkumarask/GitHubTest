package com.bbb.commerce.order.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SkuShipRestrictionsVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nonShippableStates;
	private Map<String, String> zipCodesRestrictedForSkuMap;
	 
	public Map<String, String> getZipCodesRestrictedForSkuMap() {
		return zipCodesRestrictedForSkuMap;
	}

	public void setZipCodesRestrictedForSkuMap(
			Map<String, String> zipCodesRestrictedForSkuMap) {
		this.zipCodesRestrictedForSkuMap = zipCodesRestrictedForSkuMap;
	}
	/**
	 * @return the nonShippableStates
	 */
	public String getNonShippableStates() {
		return nonShippableStates;
	}
	/**
	 * @param nonShippableStates the nonShippableStates to set
	 */
	public void setNonShippableStates(String nonShippableStates) {
		this.nonShippableStates = nonShippableStates;
	}
	
	
	
}