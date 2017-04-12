package com.bbb.utils;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

public class BBBConfigRepoUtils {
	private final static String BBB_CATALOG_TOOLS = "/com/bbb/commerce/catalog/BBBCatalogTools";
	private final static Nucleus NUCLEUS = Nucleus.getGlobalNucleus();
	private static BBBCatalogTools bbbCatalogTools;
	
	public static BBBCatalogTools getBbbCatalogTools() {
		if(bbbCatalogTools == null) {
			bbbCatalogTools = (BBBCatalogTools) NUCLEUS
						.resolveName(BBB_CATALOG_TOOLS);
		} 
		return bbbCatalogTools;
	}

	public static void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		BBBConfigRepoUtils.bbbCatalogTools = bbbCatalogTools;
	}

	private static final ApplicationLogging MLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(BBBConfigRepoUtils.class);
	
	public static String getStringValue(String configType, String key){
		String value = null;
		try {
			List<String> config = getBbbCatalogTools().getAllValuesForKey(configType , key);
			if(config != null && config.size() > 0) {
				value = config.get(0);
				if(MLOGGING.isLoggingDebug()){
					MLOGGING.logDebug("ConfigType is:" + configType);
					MLOGGING.logDebug("Key is:" + key);
					MLOGGING.logDebug("Value is:" + value);
				}
			}
		} catch (BBBSystemException e) {
			if(MLOGGING.isLoggingError()){
				MLOGGING.logError("BBBSystemException for ConfigType:" + configType	+" and key:" +key, e);
			}
		} catch (BBBBusinessException e) {
			if(MLOGGING.isLoggingError()){
				MLOGGING.logError("BBBBusinessException for ConfigType:" + configType	+" and key:" +key, e);
			}
		}
		return value;
	}
	
	public static List<String> getAllValues(String configType, String key){
		List<String> values = null;
		try {
			values = getBbbCatalogTools().getAllValuesForKey(configType , key);
			if(MLOGGING.isLoggingDebug()){
				MLOGGING.logDebug("ConfigType is:" + configType);
				MLOGGING.logDebug("Key is:" + key);
				MLOGGING.logDebug("Value is:" + values);
			}
		} catch (BBBSystemException e) {
			if(MLOGGING.isLoggingError()){
				MLOGGING.logError("BBBSystemException for ConfigType:" + configType	+" and key:" +key, e);
			}
		} catch (BBBBusinessException e) {
			if(MLOGGING.isLoggingError()){
				MLOGGING.logError("BBBBusinessException for ConfigType:" + configType	+" and key:" +key, e);
			}
		}
		return values;
	}
	
	public static Map<String,String> getConfigMap(String configType){
		Map<String,String> configMap = null;
		try {
			configMap = getBbbCatalogTools().getConfigValueByconfigType(configType);
			if(MLOGGING.isLoggingDebug()){
				MLOGGING.logDebug("ConfigType is:" + configType);
				MLOGGING.logDebug("configMap is:" + configMap);
			}
		} catch (BBBSystemException e) {
			if(MLOGGING.isLoggingError()){
				MLOGGING.logError("BBBSystemException for ConfigType:" + configType, e);
			}
		} catch (BBBBusinessException e) {
			if(MLOGGING.isLoggingError()){
				MLOGGING.logError("BBBSystemException for ConfigType:" + configType, e);
			}
		}
		return configMap;
	}
}
