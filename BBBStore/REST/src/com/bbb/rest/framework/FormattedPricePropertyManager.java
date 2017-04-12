/**
 * 
 */
package com.bbb.rest.framework;

import java.util.Map;

import com.bbb.common.BBBGenericService;


public class FormattedPricePropertyManager extends BBBGenericService {
	
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
