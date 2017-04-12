package com.bbb.commerce.catalog;

import java.util.Map;

import com.bbb.common.BBBGenericService;

public class BBBConfigKeyValues extends BBBGenericService {
	private Map<String, String> mConfigKeyValuesMap;

	public Map<String, String> getConfigKeyValuesMap() {
		return mConfigKeyValuesMap;
	}

	public void setConfigKeyValuesMap(Map<String, String> pConfigKeyValuesMap) {
		mConfigKeyValuesMap = pConfigKeyValuesMap;
	}
}
