package com.bbb.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.NamedQueryView;
import atg.repository.ParameterSupportView;
import atg.repository.Query;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
//import com.bbb.framework.performance.BBBPerformanceConstants;
//import com.bbb.framework.performance.BBBPerformanceMonitor;

/**This Class is responsible for taking EmailHandlerHostName and
 * EmailHandlerPort from SMTPConfig repository and set them at server
 * startup. The values are being assigned inside SMTPEmail.properties
 * 
 * @author apanw1
 *
 */
public class BBBEmailConfiguration extends BBBGenericService {


	public static final String CONFIG_KEYS_NOT_AVAILABLE="6006" ;
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="2003";
	
	private MutableRepository configureRepository;
	private String mEmailHandlerHostName;
	private int mEmailHandlerPort;
	private String mConfigType;
	private String mHostConfigKey;
	private String mPortConfigKey;

	/**
	 * @return the configRepository
	 */
	public MutableRepository getConfigureRepository() {

		return configureRepository;
	}

	/**
	 * @param configRepository the configRepository to set
	 */
	public void setConfigureRepository(final MutableRepository configRepository) {

		this.configureRepository = configRepository;
	}
	/**
	 * @return the hostConfigKey
	 */
	public String getHostConfigKey() {
		return mHostConfigKey;
	}

	/**
	 * @param hostConfigKey
	 *            the hostConfigKey to set
	 */
	public void setHostConfigKey(String pHostConfigKey) {
		this.mHostConfigKey = pHostConfigKey;
	}

	/**
	 * @return the portConfigKey
	 */
	public String getPortConfigKey() {
		return mPortConfigKey;
	}

	/**
	 * @param portConfigKey
	 *            the portConfigKey to set
	 */
	public void setPortConfigKey(String pPortConfigKey) {
		this.mPortConfigKey = pPortConfigKey;
	}

	/**
	 * @return the configType
	 */
	public String getConfigType() {
		return mConfigType;
	}

	/**
	 * @param configType
	 *            the configType to set
	 */
	public void setConfigType(String pConfigType) {
		this.mConfigType = pConfigType;
	}

	/**
	 * @return the emailHandlerPort
	 */
	public int getEmailHandlerPort() {
		String smtpConfigvalue = getSmtpConfigValue(getPortConfigKey());
		this.mEmailHandlerPort = smtpConfigvalue != null ? Integer
				.valueOf(smtpConfigvalue) : 25;
		return mEmailHandlerPort;
	}

	/**
	 * @param emailHandlerPort
	 *            the emailHandlerPort to set
	 */
	public void setEmailHandlerPort(int pEmailHandlerPort) {
		this.mEmailHandlerPort = pEmailHandlerPort;
	}

	/**
	 * @return the emailHandlerHostName
	 */
	public String getEmailHandlerHostName() {
		this.mEmailHandlerHostName = getSmtpConfigValue(getHostConfigKey());
		return mEmailHandlerHostName;
	}

	/**
	 * @param emailHandlerHostName
	 *            the emailHandlerHostName to set
	 */
	public void setEmailHandlerHostName(String pEmailHandlerHostName) {
		this.mEmailHandlerHostName = pEmailHandlerHostName;
	}

	public String getSmtpConfigValue(String key) {
		String smtpConfigvalue = null;
		List<String> smtpList = new ArrayList<String>();
		try {
			smtpList = this.getAllValuesForKey(getConfigType(),
					key);
		} catch (BBBBusinessException bbbbEx) {
			logError("BBBBusinessException :" + bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("BBBSystemException" + bbbsEx);
		}
		if (smtpList != null && !smtpList.isEmpty()) {
			smtpConfigvalue = smtpList.get(0);
		}
		return smtpConfigvalue;

	}
	private Object getProperyValue(Object pItem, String pPropertyName){
		if(pItem instanceof RepositoryItemImpl){
			return ((RepositoryItemImpl)pItem).getPropertyValue(pPropertyName);
		} else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getAllValuesForKey(String configType, String key)
			throws BBBSystemException, BBBBusinessException {
		List<String> allValues = new ArrayList<String>();
		Map<String, String> configValueForConfigKeys = null;

		if (null == configType) {
			
		    logError("Config Type passed to getAllValuesForKey() method is Null");
			
			throw new BBBBusinessException(BBBCoreErrorConstants.EMAIL_ERROR_1000,"Config Type cannot be null");
		} 
		else {
			Object[] params = {configType};

			try {
				configValueForConfigKeys = new HashMap<String, String>(); 
				RepositoryItem[] configKeysRepo = this.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,
						params, BBBCatalogConstants.IDN_CONFIGKEYS);

				if(configKeysRepo !=null){
					Set<RepositoryItem> configKeyValues = (Set)getProperyValue(configKeysRepo[0], BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY);
					
					for (RepositoryItem configKeyValue : configKeyValues)
					{
						configValueForConfigKeys.put((String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY));
					}

					for (RepositoryItem configKeys : configKeysRepo) {
						if (configValueForConfigKeys != null) {
							String keyValue = configValueForConfigKeys.get(key);

							if (keyValue != null) {
								String[] splitValues = keyValue.split(BBBCatalogConstants.DELIMITER);

								for (String value : splitValues) {
									allValues.add(value);
								}


							} else {
								
									logError(
											"No Value found for Key "+key+" in Config Type "+configType+" passed to getAllValuesForKey() method");
								
								throw new BBBSystemException (CONFIG_KEYS_NOT_AVAILABLE,CONFIG_KEYS_NOT_AVAILABLE);
							}
						}
						else{
							
								logError("NO DATA in config keys");
							
							throw new BBBSystemException (CONFIG_KEYS_NOT_AVAILABLE,CONFIG_KEYS_NOT_AVAILABLE);
						}
					}
				} 
			}catch (RepositoryException e) {
				
					logError("Exception occured in getAllValuesForKey() method:: " + e);
				

				throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
			}
		}

		return allValues;
	}
	
	public RepositoryItem[] executeConfigNamedRQLQuery(final String pQueryName,
			final Object[] pParams, final String pViewName)
					throws RepositoryException {

		RepositoryItem[] repositoryItems = null;

		if (this.getConfigureRepository() == null) {
		
				logDebug("Can't execute RQL query. The config repository object is null");
			
			return null;
		}

		if (StringUtils.isEmpty(pQueryName) || StringUtils.isEmpty(pViewName)) {

			
				logDebug("Empty query name or view name passed. Returning back");
			
			return null;
		}

		
			logDebug("About to execute RQL query:" + pQueryName
					+ "on itemdescriptor:" + pViewName);
		
		NamedQueryView view = (NamedQueryView) configureRepository
				.getView(pViewName);

		if (view != null)
		{
			Query query = view.getNamedQuery(pQueryName);

			if (query != null) {
				repositoryItems = ((ParameterSupportView) view).executeQuery(query,
						pParams);
			}
		}


		return repositoryItems;
	}

}
