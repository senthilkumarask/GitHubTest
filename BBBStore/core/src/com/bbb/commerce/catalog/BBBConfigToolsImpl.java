package com.bbb.commerce.catalog;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.NamedQueryView;
import atg.repository.ParameterSupportView;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBWebCacheIF;
import com.bbb.framework.goldengate.DCPrefixIdGenerator;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;
import com.bbb.commerce.catalog.BBBConfigKeyValues;
import com.bbb.commerce.catalog.vo.ShipMethodVO;

/**
 * 
 */
public class BBBConfigToolsImpl extends BBBGenericService implements BBBConfigTools {

	public static final String CONFIG_KEYS_NOT_AVAILABLE="6006" ;
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="2003";
	private MutableRepository siteRepository;
	private MutableRepository configureRepository;
	private DCPrefixIdGenerator idgen;
	private String previewDataPath;
	private boolean stagingServer;
	private String stagingSuffix;
	private BBBWebCacheIF configCacheContainer;
	private String cacheName;
	private long cacheTimeout;
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private BBBConfigKeySiteChannelMapper mConfigKeySiteChannelMapper;
	//This property should be removed as part of SLR phase 3
	private boolean mOverrideEnabledFromComponent;
	private boolean mAdditionalLoggingDebug;
	
	//Jupiter code refactor
	private MutableRepository catalogRepository;
	
	/** The managed catalog repository. */
	private Repository managedCatalogRepository;
	
	/** The shipping repository. */
	private MutableRepository shippingRepository;
	
	private String sddShipMethodId;//Required for ShippingRepoTools & SiteRepoTools
	
	// Error codes from BBBCatalogErrorCodes - Estore module - Jupiter - CodeRefactor
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION="2003";
	
	public static final String INPUT_PARAMETER_IS_NULL="2006" ;
	
	/**
	 * Gets the sdd ship method id.
	 *
	 * @return the sdd ship method id
	 */
	public String getSddShipMethodId() {
		return sddShipMethodId;
	}
	
	/**
	 * Sets the sdd ship method id.
	 *
	 * @param sddShipMethodId the new sdd ship method id
	 */
	public void setSddShipMethodId(String sddShipMethodId) {
		this.sddShipMethodId = sddShipMethodId;
	}
	
	/**
	 * Gets the bbb managed catalog repository.
	 * 
	 * @return the managedCatalogRepository
	 */
	public Repository getBbbManagedCatalogRepository() {
		return managedCatalogRepository;
	}

	/**
	 * Sets the bbb managed catalog repository.
	 * @param managedCatalogRepository the managedCatalogRepository to set
	 */
	public void setBbbManagedCatalogRepository(Repository managedCatalogRepository) {
		this.managedCatalogRepository = managedCatalogRepository;
	}

	/**
	 * @return the shippingRepository
	 */
	public MutableRepository getShippingRepository() {
		return shippingRepository;
	}

	/**
	 * @param shippingRepository the shippingRepository to set
	 */
	public void setShippingRepository(MutableRepository shippingRepository) {
		this.shippingRepository = shippingRepository;
	}


	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return the cacheTimeout
	 */
	public long getCacheTimeout() {
		return this.cacheTimeout;
	}

	/**
	 * @param cacheTimeout the cacheTimeout to set
	 */
	public void setCacheTimeout(long cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return this.cacheName;
	}

	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	/**
	 * @return the localCacheContainer
	 */
	public BBBWebCacheIF getConfigCacheContainer() {
		return this.configCacheContainer;
	}
	/**
	 * @param configCacheContainer the configCacheContainer to set
	 */
	public void setConfigCacheContainer(BBBWebCacheIF configCacheContainer) {
		this.configCacheContainer = configCacheContainer;
	}
	public void setStagingServer(final boolean stagingServer ){
		this.stagingServer=stagingServer;
	}
	public boolean isStagingServer(){
		return this.stagingServer;
	}
	public void setStagingSuffix(final String stagingSuffix){
		this.stagingSuffix=stagingSuffix;
	}
	public String getStagingSuffix(){
		return this.stagingSuffix;
	}

	public String getPreviewDataPath() {
		return this.previewDataPath;
	}

	public void setPreviewDataPath(final String previewDataPath) {
		this.previewDataPath = previewDataPath;
	}
	/**
	 * @return the siteRepository
	 */
	public MutableRepository getSiteRepository() {
		return this.siteRepository;
	}

	/**
	 * @param siteRepository the siteRepository to set
	 */
	public void setSiteRepository(final MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}

	public DCPrefixIdGenerator getIdgen() {
		return this.idgen;
	}

	public void setIdgen(final DCPrefixIdGenerator idgen) {
		this.idgen = idgen;
	}

	/**
	 * @return the configRepository
	 */
	public MutableRepository getConfigureRepository() {

		return this.configureRepository;
	}

	/**
	 * @param configRepository the configRepository to set
	 */
	public void setConfigureRepository(final MutableRepository configRepository) {

		this.configureRepository = configRepository;
	}

	private static Object getProperyValue(final Object pItem, final String pPropertyName){
		if(pItem instanceof RepositoryItemImpl){
			return ((RepositoryItemImpl)pItem).getPropertyValue(pPropertyName);
		}

		return null;
	}
	/**
	 * Below getter setter for mOverrideEnabledFromComponent and its references need to be removed as part of SLR Phase 3
	 */
	public boolean isOverrideEnabledFromComponent() {
		return mOverrideEnabledFromComponent;
	}

	public void setOverrideEnabledFromComponent(
			boolean pOverrideEnabledFromComponent) {
		mOverrideEnabledFromComponent = pOverrideEnabledFromComponent;
	}
	
	public BBBConfigKeySiteChannelMapper getConfigKeySiteChannelMapper() {
		return mConfigKeySiteChannelMapper;
	}

	public void setConfigKeySiteChannelMapper(
			BBBConfigKeySiteChannelMapper pConfigKeySiteChannelMapper) {
		mConfigKeySiteChannelMapper = pConfigKeySiteChannelMapper;
	}

	public boolean isAdditionalLoggingDebug() {
		return mAdditionalLoggingDebug;
	}

	public void setAdditionalLoggingDebug(boolean pAdditionalLoggingDebug) {
		mAdditionalLoggingDebug = pAdditionalLoggingDebug;
	}

	/**
	 * This method gets Rest Web service call with a configType, key and returns a List which contains config
	 * key-values split up by a delimiter
	 *
	 * @param configType
	 *            - The configType for which the map of config key to config value has to be
	 *            returned. key - the key for which the List of String values will be returned
	 * @param key as config key
	 * @return List - The list of String values returned for a particular key
	 */
	@Override
	public List<String> getAllValuesForKeyRestCall(final String configType, final String key){
		
		List<String> allValues = null;
		try {
			allValues = getAllValuesForKey(configType, key);
		} catch (BBBSystemException e) {
			logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION , e);
		} catch (BBBBusinessException e) {
			logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010);
		}
		
		return allValues;
	}

	
	/**
	 * This method gets called with a configType, key and returns a List which contains config
	 * key-values split up by a delimiter
	 *
	 * @param configType
	 *            - The configType for which the map of config key to config value has to be
	 *            returned. key - the key for which the List of String values will be returned
	 * @param key as config key
	 * @return List - The list of String values returned for a particular key
	 * @throws BBBSystemException, BBBBusinessException
	 */
	@Override
	public List<String> getAllValuesForKey(final String configType, final String key)
			throws BBBSystemException, BBBBusinessException {
		
		this.logTrace("configType = " + configType + " - Key " + key );
		
		List<String> allValues = new ArrayList<String>();
		Map<String, String> configValueForConfigKeys = null;
		if (StringUtils.isEmpty(configType)) {
			
			this.logError("catalog_1011: Config Type passed to getAllValuesForKey() method is Null");
			
			throw new BBBBusinessException(BBBCoreErrorConstants.CATALOG_ERROR_1010,"Config Type cannot be null");
		}
		final Object[] params = {configType};

		try {
			configValueForConfigKeys = new HashMap<String, String>();
			boolean foundInComponent = false;
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.CATALOG_API_CALL+" getAllValuesForKey");
			
			// firstly found the config key in the local config cache container,
			// if not found , then do a repository call
			final String nameOfCache = getCacheName();
			final String cacheKey = getCacheKeyName(configType,key);
			long timeoutOfCache = getCacheTimeout();
			if (null != getConfigCacheContainer() && null != getConfigCacheContainer().get(cacheKey, nameOfCache)) {
				allValues = (List<String>) getConfigCacheContainer().get(cacheKey, nameOfCache);
			} 
			else {
			
				final RepositoryItem[] configKeysRepo = this.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,
						params, BBBCatalogConstants.IDN_CONFIGKEYS);
				if(isOverrideEnabledFromComponent()) {
					String[] siteChannelArr = getSiteAndChannelId();
					if (!StringUtils.isEmpty(siteChannelArr[0])
							&& !StringUtils.isEmpty(siteChannelArr[1])) {
						foundInComponent = getValueFromComponent(configType, key, configValueForConfigKeys, siteChannelArr[0], siteChannelArr[1]);
						if(!foundInComponent){
							foundInComponent = getValueFromComponent(configType, key, configValueForConfigKeys, siteChannelArr[0], null);
							
							if(!foundInComponent) {
								foundInComponent = getValueFromComponent(configType, key, configValueForConfigKeys, null, null);
							}
						}
					} else if (!StringUtils.isEmpty(siteChannelArr[0])) {
						foundInComponent = getValueFromComponent(configType, key, configValueForConfigKeys, siteChannelArr[0], null);
						
						if(!foundInComponent) {
							foundInComponent = getValueFromComponent(configType, key, configValueForConfigKeys, null, null);
						}
					} else {
						foundInComponent = getValueFromComponent(configType, key, configValueForConfigKeys, null, null);
					}
				}
				if(!isOverrideEnabledFromComponent() || !foundInComponent) {
					this.logDebug("Either overrideEnabledFromComponent is false or value not found in component, hence looking in repository for configKey: " + key + " of type: " + configType);

					this.logTrace("configKeysRepo = " + Arrays.toString (configKeysRepo));

					final Set<RepositoryItem> configKeyValues = (Set<RepositoryItem>)BBBConfigToolsImpl.getProperyValue(configKeysRepo[0], BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY);
					
					this.logTrace("configKeyValues = " + configKeyValues);
					
					for (final RepositoryItem configKeyValue : configKeyValues) {
						configValueForConfigKeys.put((String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY));
					}
				}
	
				for (final RepositoryItem configKeys : configKeysRepo) {
	
					String keyValue = null;
					if (configValueForConfigKeys != null) {
						if(this.isStagingServer()){
							if(!StringUtils.isEmpty(this.getStagingSuffix())){
								final String keyWithStgSuffix=key+this.getStagingSuffix();
								keyValue = configValueForConfigKeys.get(keyWithStgSuffix);
								this.logTrace("keyValue for stagingprefixed key : "+
										keyWithStgSuffix +" :"+keyValue );
							}
						}
						else {
							final String prefix=this.getIdgen().getDcPrefix();
							if(!StringUtils.isEmpty(prefix)){
								final String keyWithPrefix=prefix+key;
								keyValue = configValueForConfigKeys.get(keyWithPrefix);
								this.logTrace("keyValue for prefixed key : "+keyWithPrefix +" :"+keyValue );
							}
						}
						if (StringUtils.isEmpty(keyValue)) {
							keyValue = configValueForConfigKeys.get(key);
						}
						if (!StringUtils.isEmpty(keyValue)) {
							final String[] splitValues = keyValue.split(BBBCatalogConstants.DELIMITER);
							for (final String value : splitValues) {
								allValues.add(value);
							}
						}
						else{
							if(configType.equalsIgnoreCase(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE)) {
								this.logDebug("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method");
								throw new BBBBusinessException (CONFIG_KEYS_NOT_AVAILABLE,CONFIG_KEYS_NOT_AVAILABLE);
							}
							else {								
							this.logError("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method");
							throw new BBBBusinessException (CONFIG_KEYS_NOT_AVAILABLE,CONFIG_KEYS_NOT_AVAILABLE);
						}
					}
					}
					else{
						
						this.logError("catalog_1013: NO DATA in config keys");
						
						throw new BBBBusinessException (CONFIG_KEYS_NOT_AVAILABLE,CONFIG_KEYS_NOT_AVAILABLE);
					}
				}
				// put the config key value in the local config cache container
				if(null != getConfigCacheContainer()){
					getConfigCacheContainer().put(cacheKey, allValues, nameOfCache, timeoutOfCache);
				}
			}
		}catch (final RepositoryException e) {
			
			this.logError("catalog_1014: Exception occured in getAllValuesForKey() method:: ");
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.CATALOG_API_CALL+" getAllValuesForKey");
			
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}
		finally
		{
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.CATALOG_API_CALL+" getAllValuesForKey");
		}
		return allValues;
	}
	/**
	 * This method can be used for getting config key value for boolean type.
	 * 
	 * @param configType - Parent Config key
	 * @param key - config key for which we have to fetch the value
	 * @param defaultValue - default value if config key is not present
	 * @return boolean : return config key value if present,  otherwise returns default value passed as param
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
			
	public boolean getValueForConfigKey(final String configType, final String key, final boolean defaultValue) throws BBBSystemException, BBBBusinessException{
		List<String> configValuesList = this.getAllValuesForKey(configType, key);
		if(!BBBUtility.isListEmpty(configValuesList)){
			String configValue = configValuesList.get(0);
			if(!BBBUtility.isEmpty(configValue)){
				return Boolean.parseBoolean(configValue);
			}else{
				return defaultValue;
			}
		}else{
			return defaultValue;
		}
	}
	/**
	 * This method can be used for getting config key value for String type.
	 * 
	 * @param configType - Parent Config key
	 * @param key - config key for which we have to fetch the value
	 * @param defaultValue - default value if config key is not present
	 * @return String : return config key value if present,  otherwise returns default value passed as param
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
			
	public String getValueForConfigKey(final String configType, final String key, final String defaultValue) throws BBBSystemException, BBBBusinessException{
		List<String> configValuesList = this.getAllValuesForKey(configType, key);
		if(!BBBUtility.isListEmpty(configValuesList)){
			String configValue = configValuesList.get(0);
			if(!BBBUtility.isEmpty(configValue)){
				return configValue;
			}else{
				return defaultValue;
			}
		}else{
			return defaultValue;
		}
	}
	private String getCacheKeyName(final String pConfigType, final String pKey){
		String cacheKey = null;
		String siteId =null;
		String channelId =null;
		if(null != ServletUtil.getCurrentRequest()) {
			siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
			channelId = BBBUtility.getChannel();
		}
		if (BBBUtility.isEmpty(siteId)){
			siteId = extractSiteID();
		}
		if(ServletUtil.getCurrentRequest()!=null && ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.IS_FROM_SCHEDULER)!=null && 
				ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.IS_FROM_SCHEDULER).equalsIgnoreCase(BBBCoreConstants.TRUE)){
			siteId = ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.SITE_ID);
			channelId = ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.CHANNEL);
		}
		if (null != ServletUtil.getCurrentRequest()
				&& BBBUtility.isEmpty(channelId)) {
			channelId = ServletUtil.getCurrentRequest().getParameter(
					BBBCoreConstants.CHANNEL);
		}
		if (null == pKey) {
			if(BBBUtility.isNotEmpty(channelId)){		
				cacheKey = pConfigType + "_" + channelId + "_" + siteId;
			}else {
				cacheKey = pConfigType + "_" + siteId;
			}
		} else {
			if(BBBUtility.isNotEmpty(channelId)){		
				cacheKey = pKey + "_" + pConfigType + "_" + channelId + "_" + siteId;
			}else {
				cacheKey = pKey + "_" + pConfigType + "_" + siteId;
			}
		}
		return cacheKey;
	}

	/**
	 * @return
	 */
	protected String extractSiteID() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * This method gets called with a configType and returns a Map which contains config key-value
	 * pairs
	 *
	 * @param configType
	 *            - The configType for which the map of config key to config value has to be
	 *            returned.
	 * @return Map - The config key - config value pair
	 * @throws BBBSystemException, BBBBusinessException
	 */
	@Override
	public Map<String, String> getConfigValueByconfigType(final String configType)
			throws BBBSystemException, BBBBusinessException {

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>();
		if (null == configType) {
			throw new BBBBusinessException(BBBCoreErrorConstants.CATALOG_ERROR_1014,"Config Type cannot be null");
		}

		final Object[] params = {configType };


		try {
			BBBPerformanceMonitor.start(
					BBBPerformanceConstants.CATALOG_API_CALL+" getConfigValueByconfigType");
			
			// firstly found the config key the the local config cache container,
			// if not found , then do a repository call
			final String nameOfCache = getCacheName();
			final String cacheKey = getCacheKeyName(configType,null);
			long timeoutOfCache = getCacheTimeout();
			boolean foundInComponent = false;
			if (null != getConfigCacheContainer() && null != getConfigCacheContainer().get(cacheKey, nameOfCache)) {
				configValueForConfigKeys = (Map<String, String>) getConfigCacheContainer().get(cacheKey, nameOfCache);
			} 
			else {
				final RepositoryItem[] configKeys = this
						.executeConfigNamedRQLQuery(
								BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,
								params,
								BBBCatalogConstants.IDN_CONFIGKEYS);
	
				if(configKeys !=null){
					@SuppressWarnings("unchecked")
					final Set<RepositoryItem> configKeyValues = (Set<RepositoryItem>)BBBConfigToolsImpl.getProperyValue(configKeys[0], BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY);
	
					for (final RepositoryItem configKeyValue : configKeyValues)
					{
						if(isOverrideEnabledFromComponent()) {
							String[] siteChannelArr = getSiteAndChannelId();
							if (!StringUtils.isEmpty(siteChannelArr[0])
									&& !StringUtils.isEmpty(siteChannelArr[1])) {
								foundInComponent = getValueFromComponent(configType, (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), configValueForConfigKeys, siteChannelArr[0], siteChannelArr[1]);
								if(!foundInComponent){
									foundInComponent = getValueFromComponent(configType, (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), configValueForConfigKeys, siteChannelArr[0], null);
									
									if(!foundInComponent) {
										foundInComponent = getValueFromComponent(configType, (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), configValueForConfigKeys, null, null);
									}
								}
							} else if (!StringUtils.isEmpty(siteChannelArr[0])) {
								foundInComponent = getValueFromComponent(configType, (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), configValueForConfigKeys, siteChannelArr[0], null);
								
								if(!foundInComponent) {
									foundInComponent = getValueFromComponent(configType, (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), configValueForConfigKeys, null, null);
								}
							} else {
								foundInComponent = getValueFromComponent(configType, (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), configValueForConfigKeys, null, null);
							}
						}
						if(!isOverrideEnabledFromComponent() || !foundInComponent) {
							this.logDebug("Either overrideEnabledFromComponent is false or value not found in component, hence looking in repository for configType: " + configType);
							configValueForConfigKeys.put((String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY), (String)configKeyValue.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY));
						}
					}
				}

				//For staging we need to replace values of keys with _staging keys
				if(this.isStagingServer()){
					final Map<String, String> configValueCopy=new HashMap<String, String>();
					configValueCopy.putAll(configValueForConfigKeys);
					for(final String key:configValueCopy.keySet()){
						if(!StringUtils.isEmpty(this.getStagingSuffix())){
							if(key.endsWith(this.getStagingSuffix())){
								configValueForConfigKeys.put(key.split(this.getStagingSuffix())[0],
										configValueCopy.get(key));
							}
						}
					}
				}
				else{
					final String prefix=this.getIdgen().getDcPrefix();
					if(!StringUtils.isEmpty(prefix)){
						final Map<String, String> configValueCopy=new HashMap<String, String>();
						configValueCopy.putAll(configValueForConfigKeys);
						for(final String key:configValueCopy.keySet()){
								if(key.startsWith(prefix)){
									final int prefixIndex=prefix.length();
									//logDebug("prefix"+ prefix+" length of prefix "+prefixIndex+"  key after removing prefix  "+key.substring(prefixIndex));
									configValueForConfigKeys.put(key.substring(prefixIndex),
											configValueCopy.get(key));
								}
						}
					}
	
				}
			
				// put the config key value in the local config cache container
				if(null != getConfigCacheContainer()){
					getConfigCacheContainer().put(cacheKey, configValueForConfigKeys, nameOfCache, timeoutOfCache);
				}
			
		    }
			
		} catch (final RepositoryException e) {
			
			this.logError("catalog_1010: Exception occured in getConfigValueByconfigType() method:: "+configType);
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.CATALOG_API_CALL+" getConfigValueByconfigType");
			throw new BBBSystemException (UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION,e);
		}
		finally
		{
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.CATALOG_API_CALL+" getConfigValueByconfigType");
		}
		return configValueForConfigKeys;
	}


	public RepositoryItem[] executeConfigNamedRQLQuery(final String pQueryName,
			final Object[] pParams, final String pViewName)
					throws RepositoryException {

		RepositoryItem[] repositoryItems = null;

		if (this.getConfigureRepository() == null) {
			this.logDebug("Can't execute RQL query. The config repository object is null");
			return null;
		}

		if (StringUtils.isEmpty(pQueryName) || StringUtils.isEmpty(pViewName)) {
			//logDebug("Empty query name or view name passed. Returning back");
			return null;
		}

		//logDebug("About to execute RQL query:" + pQueryName
			//	+ "on itemdescriptor:" + pViewName);

		final NamedQueryView view = (NamedQueryView)this.getConfigureRepository().getView(pViewName);

		if (view != null)
		{
			final Query query = view.getNamedQuery(pQueryName);

			if (query != null) {
				repositoryItems = extractDBCall(pParams, view, query);
			}
		}


		return repositoryItems;
	}

	/**
	 * @param pParams
	 * @param view
	 * @param query
	 * @return
	 * @throws RepositoryException
	 */
	protected RepositoryItem[] extractDBCall(final Object[] pParams, final NamedQueryView view, final Query query)
			throws RepositoryException {
		return ((ParameterSupportView) view).executeQuery(query,
				pParams);
	}
	
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return mLblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(
			LblTxtTemplateManager mLblTxtTemplateManager) {
		this.mLblTxtTemplateManager = mLblTxtTemplateManager;
	}

	/**
	 * Fetch the config value for given type and config key combination
	 * In the case of no value or exception return the default value
	 * @param configType
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getValueForConfigKey(String configType, String key,int defaultValue) {
		List<String> configValuesList;
		try {
			configValuesList = this.getAllValuesForKey(configType, key);
			if(!BBBUtility.isListEmpty(configValuesList)){
				String configValue = configValuesList.get(0);
				if(BBBUtility.isInteger(configValue)){
					return Integer.parseInt(configValue);	
				}else{
					return defaultValue;
				}
			}else{
				return defaultValue;
			}
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Exception while fetching value for config type "+ configType +" config key "+ key +" Exception "+ e.getMessage());;
			return defaultValue;
		}		
	}
    
	/**
	 * Fetch the config value for given type and config key combination
	 * In the case of no value or exception return the default value
	 * @param configType
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getConfigKeyValue(String configType, String key, String defaultValue) {
		List<String> configValuesList;
		try {
			configValuesList = this.getAllValuesForKey(configType, key);
			if(!BBBUtility.isListEmpty(configValuesList)){
				String configValue = configValuesList.get(0);
				if(!BBBUtility.isEmpty(configValue)){
					return configValue;
				}else{
					return defaultValue;
				}
			}else{
				return defaultValue;
			}
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Exception while fetching value for config type "+ configType +" config key "+ key +" Exception "+ e.getMessage());;
			return defaultValue;
		} catch (Exception e) {
			logError("Exception while fetching value for config type "+ configType +" config key "+ key +" Exception "+ e.getMessage());;
			return defaultValue;
		} 
		
	}

	
	/**
	 * @return SiteID at index 0 and channelId at index 1
	 */
	public String[] getSiteAndChannelId() {
		String siteId =null;
		String channelId =null;
		String[] siteChannelArr = new String[2];
		if(null != ServletUtil.getCurrentRequest()) {
			siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
			channelId = BBBUtility.getChannel();
		}
		if (BBBUtility.isEmpty(siteId)){
			siteId = extractSiteID();
		}
		if(ServletUtil.getCurrentRequest()!=null && ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.IS_FROM_SCHEDULER)!=null && 
				ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.IS_FROM_SCHEDULER).equalsIgnoreCase(BBBCoreConstants.TRUE)){
			siteId = ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.SITE_ID);
			channelId = ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.CHANNEL);
		}
		if (null != ServletUtil.getCurrentRequest()
				&& BBBUtility.isEmpty(channelId)) {
			channelId = ServletUtil.getCurrentRequest().getParameter(
					BBBCoreConstants.CHANNEL);
		}
		
		siteChannelArr[0] = siteId;
		if(BBBUtility.isNotEmpty(channelId)) {
			siteChannelArr[1] = channelId;
		}
		
		return siteChannelArr;
	}
	
	/**
	 * Fetches value from component instead of ConfigureKey Repository.
	 * 
	 * @param configType
	 * @param key
	 * @param configValueForConfigKeys
	 * @param site
	 * @param channel
	 * @return true if value found in component else false
	 */
	public boolean getValueFromComponent(String configType, String key,
			Map<String, String> configValueForConfigKeys, String site, String channel) {
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.CATALOG_API_CALL+" getValueFromComponent");
		if(isAdditionalLoggingDebug()) {
			this.logDebug("OverrideEnabledFromComponent is true, hence getting value from component for requested configKey: "
				+ key + " of type: " + configType);
		}

		String prefix = this.getIdgen().getDcPrefix();
		String suffix = this.getStagingSuffix();
		HashMap<String, String> tempMap = new HashMap<String, String>();
		boolean valueFound = false;

		if (!StringUtils.isEmpty(site)
				&& !StringUtils.isEmpty(channel)) {
			if(isAdditionalLoggingDebug()) {
				this.logDebug("Using Site channel map");
			}
			BBBConfigKeyValues siteChannelMapComponent = (BBBConfigKeyValues) getConfigKeySiteChannelMapper()
					.getSiteChannelToConfigKeyMap()
					.get(site + channel);
			if (null != siteChannelMapComponent
					&& null != siteChannelMapComponent.getConfigKeyValuesMap()) {
				tempMap = (HashMap<String, String>) siteChannelMapComponent
						.getConfigKeyValuesMap();
			} else {
				if(isAdditionalLoggingDebug()) {
					this.logDebug("Either entry missing BBBConfigKeyComponent configKeyValuesComponent Map for "
						+ "site: "
						+ site
						+ " channel: "
						+ channel
						+ " or Config key value map is empty");
				}
			}
		} else if (!StringUtils.isEmpty(site)) {
			if(isAdditionalLoggingDebug()) {
				this.logDebug("Using Site map");
			}
			BBBConfigKeyValues siteMapComponent = (BBBConfigKeyValues) getConfigKeySiteChannelMapper()
					.getSiteChannelToConfigKeyMap()
					.get(site);
			if (null != siteMapComponent
					&& null != siteMapComponent.getConfigKeyValuesMap()) {
				tempMap = (HashMap<String, String>) siteMapComponent
						.getConfigKeyValuesMap();
			} else {
				if(isAdditionalLoggingDebug()) {
					this.logDebug("Either entry missing BBBConfigKeyComponent configKeyValuesComponent Map for "
							+ "site: "
							+ site
							+ "or map in Config key value map is empty");
				}
			}
		} else {
			if(isAdditionalLoggingDebug()) {
				this.logDebug("Using default map");
			}
			BBBConfigKeyValues defaultMapComponent = (BBBConfigKeyValues) getConfigKeySiteChannelMapper()
					.getSiteChannelToConfigKeyMap().get("default");
			if (null != defaultMapComponent
					&& null != defaultMapComponent.getConfigKeyValuesMap()) {
				tempMap = (HashMap<String, String>) defaultMapComponent
						.getConfigKeyValuesMap();
			} else {
				if(isAdditionalLoggingDebug()) {
					this.logDebug("Either entry missing BBBConfigKeyComponent configKeyValuesComponent Map for "
							+ "key default"
							+ " or map in Config key value map is empty");
				}
			}
		}

		if (this.isStagingServer() && !StringUtils.isEmpty(suffix)
				&& !key.endsWith(suffix)) {
			if(isAdditionalLoggingDebug()) {
				this.logDebug("Using Staging Suffix");
			}
			String valueFromMap = tempMap.get(configType + "#" + key + suffix);
			if (valueFromMap != null) {
				configValueForConfigKeys.put(key + suffix,
						valueFromMap);
				valueFound = true;
			}
		} else if (!StringUtils.isEmpty(prefix) && !key.startsWith(prefix)) {
			if(isAdditionalLoggingDebug()) {
				this.logDebug("Using DC Prefix");
			}
			String valueFromMap = tempMap.get(configType + "#" + prefix + key);
			if (valueFromMap != null) {
				configValueForConfigKeys.put(prefix + key,
						valueFromMap);
				valueFound = true;
			}
		}

		if (!valueFound) {
			if(isAdditionalLoggingDebug()) {
				this.logDebug("Using key without suffix or prefix");
			}
			String valueFromMap = tempMap.get(configType + "#" + key);
			if (valueFromMap != null) {
				configValueForConfigKeys.put(key,
						valueFromMap);
				valueFound = true;
			}
		}
		if(isAdditionalLoggingDebug()) {
			this.logDebug("configValueForConfigKeys = "
				+ configValueForConfigKeys.toString());
		}

		if(isAdditionalLoggingDebug()) {
			this.logDebug("Value found in component: " + valueFound);
		}
		
		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.CATALOG_API_CALL+" getValueFromComponent");

		return valueFound;
	}
	
	//Jupiter code refactoring - 

 	/**
     * Execute rql query.
     *
     * @param rqlQuery the rql query
     * @param viewName the view name
     * @param repository the repository
     * @return Repository Items Result
     * @throws RepositoryException the repository exception
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public  RepositoryItem[] executeRQLQuery(final String rqlQuery, final String viewName,
                    final MutableRepository repository)
                    throws RepositoryException, BBBSystemException, BBBBusinessException {
        return this.executeRQLQuery(rqlQuery, new Object[1], viewName, repository);
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#executeRQLQuery(java.lang.String, java.lang.String)
     */
    public RepositoryItem[] executeRQLQuery(final String rqlQuery, final String viewName)
                    throws RepositoryException, BBBSystemException, BBBBusinessException {
        return this.executeRQLQuery(rqlQuery, new Object[1], viewName, this.getCatalogRepository());
    }
 	/* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#executeRQLQuery(java.lang.String, java.lang.Object[], java.lang.String, atg.repository.MutableRepository)
     */
    public RepositoryItem[] executeRQLQuery(final String rqlQuery, final Object[] params, final String viewName,
                    final MutableRepository repository) throws BBBSystemException {
        RqlStatement statement = null;
        RepositoryItem[] queryResult = null;
        if (rqlQuery != null) {
            if (repository != null) {
                try {
                    statement = RqlStatement.parseRqlStatement(rqlQuery);
                    final RepositoryView view = repository.getView(viewName);
                    if ((view == null) && this.isLoggingError()) {
                        this.logError("catalog_1019 : View " + viewName + " is null");
                    }

                    queryResult = extractDbCallForOverloadedMethod(params, statement, view);
                    if (queryResult == null) {

                    	this.logDebug("No results returned for query [" + rqlQuery + "]");

                    }

                } catch (final RepositoryException e) {
                    if (this.isLoggingError()) {
                        this.logError("catalog_1020 : Unable to retrieve data");
                    }

                    throw new BBBSystemException(
                                    UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                    UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
                }
            } else {
                if (this.isLoggingError()) {
                    this.logError("catalog_1021 : Repository has no data");
                }
            }
        } else {
            if (this.isLoggingError()) {
                this.logError("catalog_1022 : Query String is null");
            }
        }

        return queryResult;
    }

	/**
	 * @param params
	 * @param statement
	 * @param view
	 * @return
	 * @throws RepositoryException
	 */
	protected RepositoryItem[] extractDbCallForOverloadedMethod(final Object[] params, RqlStatement statement,
			final RepositoryView view) throws RepositoryException {
		return statement.executeQuery(view, params);
	}
    
	/**
	 * The method return the Week Ends as integer.
	 * 
	 * @param siteId -  the site id
	 * @return          the week end days
	 * @throws BBBSystemException the BBB system exception
	 * 
	 */
	protected Set<Integer> getWeekEndDays(final String siteId)
			throws BBBSystemException {
		RepositoryItem siteConfiguration = null;
		try {
			siteConfiguration = this.getSiteRepository().getItem(siteId,
					BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
		} catch (final RepositoryException e) {
			this.logError("RepositoryException ");
			throw new BBBSystemException(
					UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		@SuppressWarnings("unchecked")
		final Set<RepositoryItem> bbbWeekendsRepositoryItem = (Set<RepositoryItem>) siteConfiguration
				.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME);
		final Set<Integer> set = new HashSet<Integer>();
		for (final RepositoryItem repositoryItem : bbbWeekendsRepositoryItem) {
			final String weekEndDays = (String) repositoryItem
					.getPropertyValue("weekendDays");
			if (BBBCatalogConstants.SUNDAY.equalsIgnoreCase(weekEndDays)) {
				set.add(Integer.valueOf(1));
			} else if (BBBCatalogConstants.MONDAY.equalsIgnoreCase(weekEndDays)) {
				set.add(Integer.valueOf(2));
			} else if (BBBCatalogConstants.TUESDAY
					.equalsIgnoreCase(weekEndDays)) {
				set.add(Integer.valueOf(3));
			} else if (BBBCatalogConstants.WEDNESDAY
					.equalsIgnoreCase(weekEndDays)) {
				set.add(Integer.valueOf(4));
			} else if (BBBCatalogConstants.THURSDAY
					.equalsIgnoreCase(weekEndDays)) {
				set.add(Integer.valueOf(5));
			} else if (BBBCatalogConstants.FRIDAY.equalsIgnoreCase(weekEndDays)) {
				set.add(Integer.valueOf(6));
			} else if (BBBCatalogConstants.SATURDAY
					.equalsIgnoreCase(weekEndDays)) {
				set.add(Integer.valueOf(7));
			}
		}
		return set;
	}
	

	 /**
	 *  The method read holiday list from reposityory.
	 *
	 * @param siteId the site id
	 * @return the holiday list
	 * @throws BBBSystemException the BBB system exception
	 * 
	 */
	// This method is a common method - uses both SiteRepo & Shipping repo
	 protected Set<Date> getHolidayList(final String siteId) throws BBBSystemException {
	   final Set<Date> holidayList = new HashSet<Date>();
	    RepositoryItem siteConfiguration = null;
	    try {
	        siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
	    } catch (final RepositoryException e) {
	        this.logError("RepositoryException ");
	        throw new BBBSystemException(UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
	    }
	   @SuppressWarnings ("unchecked")
	    final Set<RepositoryItem> bbbHolidayRepositoryItem = (Set<RepositoryItem>) siteConfiguration
	                    .getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME);
	    for (final RepositoryItem repositoryItem : bbbHolidayRepositoryItem) {
	        final Date holidayDate = (Date) repositoryItem.getPropertyValue("holidayDate");
	        final Calendar callenderDate = Calendar.getInstance();
	        callenderDate.setTime(holidayDate);
	        final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	        final String finalDate = dateFormat.format(callenderDate.getTime());
	        Date dt = null;
	        try {
	            dt = dateFormat.parse(finalDate);
	        } catch (final ParseException e) {
	            this.logError("Parsing Exception" + e);
	        }
	        holidayList.add(dt);
	    }
	    return holidayList;

	 }

	 /**
	 *  The method gets the statename corresponding to a state code.
	 *
	 * @param stateCode the state code
	 * @return the state name
	 * @throws RepositoryException the repository exception
	 */
	 
	 public String getStateName(final String stateCode) throws RepositoryException {
	    String stateName = "";
	    final RepositoryItem statesRepositoryItem = this.getShippingRepository().getItem(stateCode.trim(),
	                    BBBCatalogConstants.STATES_ITEM_DESCRIPTOR);
	    if ((statesRepositoryItem != null)
	                    && (statesRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) != null)) {
	        stateName = (String) statesRepositoryItem
	                        .getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME);
	    }
	    return stateName;
	 }


	    /* (non-Javadoc)
	     * @see com.bbb.commerce.catalog.BBBCatalogTools#isEcoFeeEligibleForState(java.lang.String)
	     */
	    public final boolean isEcoFeeEligibleForState(final String pStateId)
	                    throws BBBBusinessException, BBBSystemException {
	        this.logDebug("Catalog API Method Name [isEcoFeeEligibleForState] State Id [" + pStateId + "]");
	        RepositoryView view = null;
	        QueryBuilder queryBuilder;
	        QueryExpression queryExpStateId;
	        QueryExpression queryStateId;
	        RepositoryItem[] items = null;
	        Query queryEcoFeeState;

	        try {
	            if (!StringUtils.isEmpty(pStateId)) {
	                view = this.getCatalogRepository().getView(BBBCatalogConstants.ECO_FEE_ITEM_DESCRIPTOR);
	                queryBuilder = view.getQueryBuilder();
	                queryExpStateId = queryBuilder
	                                .createPropertyQueryExpression(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME);
	                queryStateId = queryBuilder.createConstantQueryExpression(pStateId);
	                queryEcoFeeState = queryBuilder.createComparisonQuery(queryStateId, queryExpStateId,
	                                QueryBuilder.EQUALS);
	                this.logDebug("EcoFeeEligibleForState Query to retrieve data : " + queryEcoFeeState);
	                // Executing the Query to retirve Eco Free Relations Repository Items
	                items = extractDBCallForIsEcoFeeEligibleForState(view, queryEcoFeeState);

	                if (null != items) {
	                    return true;
	                }

	            } else {
	                this.logDebug("input parameter state id is null");
	                throw new BBBBusinessException(INPUT_PARAMETER_IS_NULL,
	                                INPUT_PARAMETER_IS_NULL);
	            }
	        } catch (final RepositoryException e) {
	            this.logError("Catalog API Method Name [isEcoFeeEligibleForState]: RepositoryException ");
	            throw new BBBSystemException(UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
	                            UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
	        }

	        return false;
	    }

		/**
		 * @param view
		 * @param queryEcoFeeState
		 * @return
		 * @throws RepositoryException
		 */
		protected RepositoryItem[] extractDBCallForIsEcoFeeEligibleForState(RepositoryView view, Query queryEcoFeeState)
				throws RepositoryException {
			return view.executeQuery(queryEcoFeeState);
		}
	    
	    /**
	     * Fetch dates corresponding to sites e.g. Canada has diff format for showing dates
	     *
	     * @param siteId the site id
	     * @param date the date
	     * @param includeYearFlag the include year flag
	     * @return formatted date string value
	     */
	 	public String getSiteBasedFormattedDate(String siteId,
	 			Calendar date, boolean includeYearFlag) {
	 		String formattedDate ="";
	 		
	 		int day = date.get(Calendar.DAY_OF_MONTH);
	     	int month = date.get(Calendar.MONTH) + 1;
	     	
	 		  if(includeYearFlag) {
	 	      	final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	 	      	formattedDate = dateFormat.format(date.getTime());
	 	      }else{
	 	    	  if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)) {
	 	  			formattedDate = String.format("%02d", day) + "/" + String.format("%02d", month);
	 	  		} else {
	 	  			formattedDate = String.format("%02d", month) + "/" + String.format("%02d", day);
	 	  		}
	 	      }
	 		
	 		return formattedDate;
	 	}
	 	
	 	/**
	     * Checks if is holiday.
	     *
	     * @param holidays the holidays
	     * @param date the date
	     * @return true, if is holiday
	     */
	    protected static boolean isHoliday(final Set<Date> holidays, final Calendar date) {
	        if ((holidays != null) && (holidays.size() > 0)) {
	            for (final Date holiday : holidays) {
	                final Calendar calHoliday = Calendar.getInstance();
	                calHoliday.setTime(holiday);
	                if ((calHoliday.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH))
	                                && (calHoliday.get(Calendar.MONTH) == date.get(Calendar.MONTH))
	                                && (calHoliday.get(Calendar.YEAR) == date.get(Calendar.YEAR))) {
	                    return true;
	                }
	            }
	        }
	        return false;
	    }
	    
	    /**
		 * Enable repo callfor dyn price.
		 *
		 * @return true, if successful
		 */
		public boolean enableRepoCallforDynPrice() {

			boolean enableRepositoryCall=true;
			List<String> enableRepositoryCallList;
			try {
				enableRepositoryCallList = getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE);
			if(!BBBUtility.isListEmpty(enableRepositoryCallList)){
				enableRepositoryCall=Boolean.parseBoolean(enableRepositoryCallList.get(0));
				}
			} catch (BBBSystemException | BBBBusinessException e) {
				logError(e.getMessage());
	 	} 			
			
			return enableRepositoryCall;
		}
		
		
		/**
		 * Update list with sdd ship method.
		 *
		 * @param shipMethodVOList the ship method vo list
		 * @throws RepositoryException the repository exception
		 */
		public void updateListWithSddShipMethod(List<ShipMethodVO> shipMethodVOList)
				throws RepositoryException {
			
			logDebug("BBBCatalogToolsImpl.updateListWithSddShipMethod starts with shipMethodVOList"+shipMethodVOList);
			// we will fetch the SDD shipping group from Shipping Repository
			RepositoryItem sddShippingMethodItem = getShippingRepository().
					getItem(getSddShipMethodId(), BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR);
			if(sddShippingMethodItem != null){
				ShipMethodVO shipMethodVO = new ShipMethodVO(sddShippingMethodItem);
				shipMethodVO.setEligibleShipMethod(true);
				shipMethodVO.setShipMethodId(sddShippingMethodItem.getRepositoryId());
				shipMethodVOList.add(shipMethodVO);
				logDebug("ShippingMethod list updated with SDD shipping method");
			}
			logDebug("BBBCatalogToolsImpl.updateListWithSddShipMethod ends with shipMethodVOList"+shipMethodVOList);
		}
}