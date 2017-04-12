package com.bbb.commerce.catalog;

import java.util.List;
import java.util.Map;

//import atg.nucleus.GenericService;



import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public interface BBBConfigTools {
	
	public List<String> getAllValuesForKey(String configType, String key)
			throws BBBSystemException, BBBBusinessException;
	
	public Map<String, String> getConfigValueByconfigType(String configType)
			throws BBBSystemException, BBBBusinessException;

	public List<String> getAllValuesForKeyRestCall(String configType, String key);
	
	/**
	 * Fetch the config value for given type and config key combination
	 * In the case of no value or exception return the default value
	 * @param configType
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getConfigKeyValue(final String configType, final String key, final String defaultValue);
	
	/**
	 * Fetch the config value for given type and config key combination
	 * In the case of no value or exception return the default value
	 * @param configType
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getValueForConfigKey(final String configType, final String key, final int defaultValue); 
	
}
