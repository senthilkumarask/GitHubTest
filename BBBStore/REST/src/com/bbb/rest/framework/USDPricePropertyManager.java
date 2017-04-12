package com.bbb.rest.framework;

import java.util.Map;

import com.bbb.common.BBBGenericService;
/**
 * This class is used by  USDPriceCustomizer for mapping properties
 * 
 */
public class USDPricePropertyManager extends BBBGenericService {

	private Map<String, String> pricesPropertyMap;

	public Map<String, String> getPricesPropertyMap() {
		return pricesPropertyMap;
	}

	public void setPricesPropertyMap(Map<String, String> pricesPropertyMap) {
		this.pricesPropertyMap = pricesPropertyMap;
	}

	public String getPricesPropertyName(String pPropertyName) {
		return getPricesPropertyMap().get(pPropertyName);
	}
	
}
