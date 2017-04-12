package com.bbb.cms.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.goldengate.DCPrefixIdGenerator;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class ConfigkeysManager extends BBBGenericService{

	private MutableRepository configRepo;
	private DCPrefixIdGenerator idgen;

	/**
	 * @return the configRepo
	 */
	public MutableRepository getConfigRepo() {
		return configRepo;
	}

	/**
	 * @param configRepo the configRepo to set
	 */
	public void setConfigRepo(MutableRepository configRepo) {
		this.configRepo = configRepo;
	}



	/**
	 * @return the idgen
	 */
	public DCPrefixIdGenerator getIdgen() {
		return idgen;
	}

	/**
	 * @param idgen the idgen to set
	 */
	public void setIdgen(DCPrefixIdGenerator idgen) {
		this.idgen = idgen;
	}

	private Object getProperyValue(Object pItem, String pPropertyName){
		if(pItem instanceof RepositoryItemImpl){
			return ((RepositoryItemImpl)pItem).getPropertyValue(pPropertyName);
		} else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAllValuesForKey(String configType, String key) throws  BBBBusinessException, BBBSystemException{
		final List<String> allValues = new ArrayList<String>();
		Map<String, String> configValueForConfigKeys = null;

		if (null == configType) {			
			logInfo("Config Type passed to getAllValuesForKey() method is Null");			
			throw new BBBBusinessException(BBBCoreErrorConstants.CMS_ERROR_1001,"Config Type cannot be null");
		} 
		else {


			try {
				BBBPerformanceMonitor.start(
						BBBPerformanceConstants.CATALOG_API_CALL+" getAllValuesForKey");
				RepositoryItem[] configKeysRepo;
				Object[] params = new Object[1];
				params[0]=configType;

				configValueForConfigKeys = new HashMap<String, String>(); 
				configKeysRepo=this.executeRQLQuery("configType=?0", params, "configKeys",getConfigRepo ());

			
				if(configKeysRepo !=null){
					Set<RepositoryItem> configKeyValues = (Set)getProperyValue(configKeysRepo[0], BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY);
					
					for (RepositoryItem configKeyValue : configKeyValues)
					{
						configValueForConfigKeys.put((String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY));
					}
					for (RepositoryItem configKeys : configKeysRepo) {
						
						configValueForConfigKeys = (Map<String, String>) configKeys.getPropertyValue(BBBCatalogConstants.IPN_CONFIGKEYS_CONFIGVALUE);
						String keyValue  =null;
						if (configValueForConfigKeys != null) {

							final String prefix=this.getIdgen().getDcPrefix();
							if(!StringUtils.isEmpty(prefix)){
								final String keyWithPrefix=prefix+key;
								keyValue = configValueForConfigKeys.get(keyWithPrefix);
								logDebug("keyValue for prefixed key : "+keyWithPrefix +" :"+keyValue );
							}

							if (StringUtils.isEmpty(keyValue)) {
								keyValue = configValueForConfigKeys.get(key);
							} 
							if (!StringUtils.isEmpty(keyValue)) {								
								final	String[] splitValues = keyValue.split(BBBCatalogConstants.DELIMITER);
								for (String value : splitValues) {
									allValues.add(value);
								}
							}
							else{
								logError(" No Value found for Key "+key+" in Config Type passed to getAllValuesForKey() method");
								throw new BBBBusinessException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
							}

						}
						else{
						
							logError(" NO DATA in config keys");
							
							throw new BBBBusinessException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
						}
					}
				} 
			}catch (RepositoryException e) {
				
				logError(" Exception occured in getAllValuesForKey() method:: ",e);
				

				throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
			}
			finally
			{
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.CATALOG_API_CALL+" getAllValuesForKey");
			}
		}

		return allValues;
	}
	private RepositoryItem[] executeRQLQuery(String rqlQuery, Object[] params,
			String viewName,MutableRepository repository) throws RepositoryException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {

				statement = RqlStatement.parseRqlStatement(rqlQuery);
				final RepositoryView view = repository.getView(viewName);
				if (view == null ) {
					logDebug(" View "+viewName+" is null");
				}
				queryResult = statement.executeQuery(view, params);
				if ( queryResult == null) {

					logDebug("No results returned for query ["+rqlQuery+"]");
				}

			} else {

				logDebug(" Repository has no data");

			}
		} else {
			
				logError("Query String is null");
			
		}

		return queryResult;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getConfigValueByconfigType(String configType)
			throws BBBSystemException, BBBBusinessException {

		String methodName = "getConfigValueByconfigType(String configType)";

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>();
		if (null == configType) {
			throw new BBBBusinessException(BBBCoreErrorConstants.CMS_ERROR_1001,"Config Type cannot be null");
		} else {
			//Object[] params = {configType };

			try {
				BBBPerformanceMonitor.start(
						BBBPerformanceConstants.CATALOG_API_CALL+" getConfigValueByconfigType");
				RepositoryItem[] configKeys;
				Object[] params = new Object[1];
				params[0]=configType;


				configKeys=this.executeRQLQuery("configType=?0", params, "configKeys",getConfigRepo ());


				if (null != configKeys) {
					Set<RepositoryItem> configKeyValues = (Set)getProperyValue(configKeys[0], BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY);
					
					for (RepositoryItem configKeyValue : configKeyValues)
					{
						configValueForConfigKeys.put((String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY));
					}
				}
				String prefix=this.getIdgen().getDcPrefix();					
				if(!StringUtils.isEmpty(prefix)){
					Map<String, String> configValueCopy=new HashMap<String, String>();
					configValueCopy.putAll(configValueForConfigKeys);
					for(String key:configValueCopy.keySet()){	
						if(key.startsWith(prefix)){
							int prefixIndex=prefix.length();
							logDebug("prefix"+ prefix+" length of prefix "+prefixIndex+"  key after removing prefix  "+key.substring(prefixIndex));
							configValueForConfigKeys.put(key.substring(prefixIndex),
									configValueCopy.get(key));
						}
					}
				}


			} catch (RepositoryException e) {
				
				logError("catalog_1010: Exception occured in getConfigValueByconfigType() method:: "+configType);			

				throw new BBBSystemException (BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
			}
			finally
			{
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.CATALOG_API_CALL+" getConfigValueByconfigType");
			}
		}
		logDebug("Exiting  " + methodName + " with configValueForConfigKeys: " + configValueForConfigKeys);
		return configValueForConfigKeys;
	}

}
