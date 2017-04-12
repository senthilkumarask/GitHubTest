package com.bbb.integration.configkeys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

/**
 * This droplet will accept the config key name,config key type & config key value from the 
 * request parameters and update the config key with the provided value in repository. It will
 * also remove entries in BBBLocalConfigCacheContainer config keys cache map for those config 
 * keys. In case of Mobile config key, it will remove the key from back end coherence cache.
 * 
 * Additional site and channel parameters can also be provided to update the channel-specific 
 * or site-specific config key.
 * 
 */
public class ConfigKeyChangeValueDroplet extends BBBDynamoServlet {

	private static final String MOBILE_WEB = "MobileWeb";
	private static final String BED_BATH_CANADA = "BedBathCanada";
	private static final String BUY_BUY_BABY = "BuyBuyBaby";
	private static final String CONFIG_KEY_TYPE = "configKeyType";
	private static final String CONFIG_KEY_VALUE = "configKeyValue";
	private static final String CONFIG_KEY_NAME = "configKeyName";
	private static final String TRANSLATIONS = "translations";
	private static final String ID = "id";
	private static final String DESKTOP_WEB = "DesktopWeb";
	private static final String CHANNEL = "channel";
	private static final String SITE = "site";
	private static final String CONFIG_VALUE = "configValue";
	private static final String BED_BATH_US = "BedBathUS";
	private static final String CONFIG_VALUE_DEFAULT = "configValueDefault";
	private static final String CONFIG_KEYS_VALUE = "configKeysValue";
	private static final String REPO_CACHE_MAP_TYPE = "RepoCacheMapType";
	private static final String CONFIG_CACHE = "/com/bbb/configurekeys/ConfigureKeys";
	private static final String OPARAM_OUTPUT="output";
	private static final String OPARAM_ERROR="error";
	private static final String OPARAM_EMPTY="empty";

	private MutableRepository configureKeysRepository;
	private BBBLocalCacheContainer localCacheContainer;
	private String configKeyQueryRql;
	private BBBConfigToolsImpl configToolsImpl;
	private CoherenceCacheContainer coherenceCacheContainer;

	@Override
	public final void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logInfo("ConfigKeyChangeValueDroplet.service() method starts");

		String configKeyName = pRequest.getParameter(CONFIG_KEY_NAME);
		String configKeyValue = pRequest.getParameter(CONFIG_KEY_VALUE);
		String configKeyType = pRequest.getParameter(CONFIG_KEY_TYPE);
		String siteId = pRequest.getParameter(SITE);
		String channelId = pRequest.getParameter(CHANNEL);

		if(!BBBUtility.isEmpty(configKeyName) && !BBBUtility.isEmpty(configKeyValue) && !BBBUtility.isEmpty(configKeyType)) {
			try {
				logInfo("Update config key ["+configKeyName+"] of config type ["+configKeyType+"] with the value ["+configKeyValue+"]");
				Object params[] = new Object[1];
				params[0] = configKeyType;

				final RepositoryItem[] configKeyTypeRepo = this.getConfigToolsImpl().executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE, params, BBBCatalogConstants.IDN_CONFIGKEYS);
				if(configKeyTypeRepo != null){
					final Set<RepositoryItem> configKeyTypeValues = (Set<RepositoryItem>) configKeyTypeRepo[0].getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY);

					RepositoryView configKeysView = getConfigureKeysRepository().getView(CONFIG_KEYS_VALUE); 
					RqlStatement configKeysRqlStatement = RqlStatement.parseRqlStatement(this.getConfigKeyQueryRql());

					params[0] = configKeyName;
					MutableRepositoryItem[] configKeys = null;

					configKeys = (MutableRepositoryItem[]) configKeysRqlStatement.executeQuery(configKeysView, params);

					if(configKeyTypeValues != null && !configKeyTypeValues.isEmpty() && configKeys != null) {
						boolean isConfigKeyFound = false;
						for(MutableRepositoryItem configKey : configKeys){

							if(configKeyTypeValues.contains(configKey)){

								isConfigKeyFound = true;
								logInfo("Config key found in the repository in the mentioned config type");
								Set<MutableRepositoryItem> translationItems =  (Set<MutableRepositoryItem>) configKey.getPropertyValue(TRANSLATIONS);
								int timesConfigKeyTobeUpdated = 0;
								boolean isConfigKeyUpdated = false;

								logInfo("\n Config key values BEFORE the changes : ");
								printAllValuesForConfigKeys(configKeyType,configKeyName,configKey,translationItems);

								if(BBBUtility.isEmpty(siteId) && BBBUtility.isEmpty(channelId)){

									updateConfigKeyInRepo(configKey, CONFIG_VALUE_DEFAULT,configKeyValue);

									if(translationItems != null) 
										for(MutableRepositoryItem translation : translationItems){
											updateConfigKeyInRepo(translation, CONFIG_VALUE,configKeyValue);
										}
									clearCacheKeyName(configKeyType, configKeyName, "", "");
									pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);

								} else if(!BBBUtility.isEmpty(siteId) && BBBUtility.isEmpty(channelId)){

									if(siteId.equalsIgnoreCase(BED_BATH_US)){
										timesConfigKeyTobeUpdated++;
										updateConfigKeyInRepo(configKey, CONFIG_VALUE_DEFAULT,configKeyValue);
									}
									if(translationItems != null)
										for(MutableRepositoryItem translation : translationItems){
											if(siteId.equalsIgnoreCase((String)((RepositoryItem)translation.getPropertyValue(SITE)).getPropertyValue(ID))){
												timesConfigKeyTobeUpdated++;
												updateConfigKeyInRepo(translation, CONFIG_VALUE,configKeyValue);
											}
										}
									if(timesConfigKeyTobeUpdated != 2){
										if(siteId.equalsIgnoreCase(BUY_BUY_BABY)){
											updateConfigKeyInRepo(configKey, CONFIG_VALUE_DEFAULT,configKeyValue);
										} else if(siteId.equalsIgnoreCase(BED_BATH_CANADA)){
											updateConfigKeyInRepo(configKey, CONFIG_VALUE_DEFAULT,configKeyValue);
										}
									}
									clearCacheKeyName(configKeyType, configKeyName, "", "");
									pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);

								} else if(!BBBUtility.isEmpty(channelId) && BBBUtility.isEmpty(siteId)){

									if(translationItems != null)
										for(MutableRepositoryItem translation : translationItems){
											if(channelId.equalsIgnoreCase((String)((RepositoryItem)translation.getPropertyValue(CHANNEL)).getPropertyValue(ID))){
												timesConfigKeyTobeUpdated++;
												updateConfigKeyInRepo(translation, CONFIG_VALUE,configKeyValue);
												if(channelId.equalsIgnoreCase(MOBILE_WEB))
													clearCacheKeyName(configKeyType, configKeyName, (String)((RepositoryItem)translation.getPropertyValue(SITE)).getPropertyValue(ID), channelId);
											}
										}

									if( (channelId.equalsIgnoreCase(DESKTOP_WEB)) || (channelId.equalsIgnoreCase(MOBILE_WEB) && timesConfigKeyTobeUpdated != 3)){
										updateConfigKeyInRepo(configKey, CONFIG_VALUE_DEFAULT,configKeyValue);
										clearCacheKeyName(configKeyType, configKeyName, "", "");
									}
									pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);

								} else if(!BBBUtility.isEmpty(channelId) && !BBBUtility.isEmpty(siteId)){

									if(siteId.equalsIgnoreCase(BED_BATH_US) && channelId.equalsIgnoreCase(DESKTOP_WEB)){
										updateConfigKeyInRepo(configKey, CONFIG_VALUE_DEFAULT,configKeyValue);
										clearCacheKeyName(configKeyType, configKeyName, "", "");
										isConfigKeyUpdated = true;
									}
									if(translationItems != null)
										for(MutableRepositoryItem translation : translationItems){
											if(channelId.equalsIgnoreCase((String)((RepositoryItem)translation.getPropertyValue(CHANNEL)).getPropertyValue(ID)) && siteId.equalsIgnoreCase((String)((RepositoryItem)translation.getPropertyValue(SITE)).getPropertyValue(ID))){
												updateConfigKeyInRepo(translation, CONFIG_VALUE,configKeyValue);
												clearCacheKeyName(configKeyType, configKeyName, siteId, channelId);
												isConfigKeyUpdated = true;
											}
										}
									if(!isConfigKeyUpdated){
										updateConfigKeyInRepo(configKey, CONFIG_VALUE_DEFAULT,configKeyValue);
										clearCacheKeyName(configKeyType, configKeyName, "", "");

									}
									pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
								}
								logInfo("\n Config key values AFTER the changes : ");
								printAllValuesForConfigKeys(configKeyType,configKeyName,configKey,translationItems);
							}
						} if(!isConfigKeyFound){
							logInfo("Config Key not found in the mentioned Config type");
						}
					} else {
						logInfo("Config key not found in the repository");
					}
				} else {
					logInfo("Invalid Config Key Type provided or Config Key Type doesn't exists in repository");
				}
			} catch (RepositoryException e) {
				logError("Repository exception from ConfigKeyChangeValueDroplet while updating the config key in repository", e);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
			}
		} else {
			logInfo("One of the required input request parameters is empty");
			pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);
		}

		this.logInfo("ConfigKeyChangeValueDroplet.service() method ends");
	}

	private void printAllValuesForConfigKeys(String configKeyType,
			String configKeyName, MutableRepositoryItem configKey, Set<MutableRepositoryItem> translationItems) {

		String cacheKey = null;
		ArrayList<String> siteCodes = new ArrayList<String>(Arrays.asList(BED_BATH_US, BUY_BUY_BABY, BED_BATH_CANADA));
		ArrayList<String> channels = new ArrayList<String>(Arrays.asList(DESKTOP_WEB, MOBILE_WEB));

		logInfo("LOCAL CACHE Values");
		for(String site : siteCodes)
			for(String channelId : channels) {
				cacheKey = configKeyName + "_" + configKeyType + "_" + channelId + "_" + site;
				if(getLocalCacheContainer() != null){
					logInfo("for site["+site+"] & channel["+channelId+"] is : "+getLocalCacheContainer().get(cacheKey));
				}
			}
		
		logInfo("\nREPOSITORY Values");
		logInfo("Default value : ["+ configKey.getPropertyValue(CONFIG_VALUE_DEFAULT)+"]");
		if(translationItems != null){
			for(MutableRepositoryItem translation : translationItems){
				String channel = (String)((RepositoryItem)translation.getPropertyValue(CHANNEL)).getPropertyValue(ID);
				String site = (String)((RepositoryItem)translation.getPropertyValue(SITE)).getPropertyValue(ID);
				logInfo("for site["+site+"] & channel["+channel+"] is : ["+translation.getPropertyValue(CONFIG_VALUE)+"]");
			}
		}
	}

	/**
	 * Generic method to update the repository item property value.
	 * 
	 * @param repositoryItem
	 * @param propertyName
	 * @param propertyValue
	 * @throws RepositoryException
	 */
	private void updateConfigKeyInRepo(MutableRepositoryItem repositoryItem,
			String propertyName, String propertyValue) throws RepositoryException {

		repositoryItem.setPropertyValue(propertyName, propertyValue);
		getConfigureKeysRepository().updateItem(repositoryItem);
	}

	/**
	 * This method will remove the config keys from the Local config cache layer.
	 * Also it will remove the config key from the mobile back end cache based on
	 * certain conditions.
	 * 
	 * @param pConfigType
	 * @param pKey
	 * @param siteId
	 * @param channel
	 */
	private void clearCacheKeyName(final String pConfigType, final String pKey, String siteId, String channel) {
		String cacheKey = null;
		ArrayList<String> siteCodes = new ArrayList<String>(Arrays.asList(BED_BATH_US, BUY_BUY_BABY, BED_BATH_CANADA));
		ArrayList<String> channels = new ArrayList<String>(Arrays.asList(DESKTOP_WEB, MOBILE_WEB));
		String mobileCoherenceKey = "getAllValuesForKey_"+pConfigType+"_"+pKey;
		String mobileCoherenceSiteKey = "";

		if(BBBUtility.isEmpty(siteId) && BBBUtility.isEmpty(channel)) {
			for(String site : siteCodes)
				for(String channelId : channels) {
					cacheKey = pKey + "_" + pConfigType + "_" + channelId + "_" + site;
					getLocalCacheContainer().remove(cacheKey);
					if(channelId.equalsIgnoreCase(MOBILE_WEB)){
						mobileCoherenceSiteKey = mobileCoherenceKey+"_"+site;
						invalidateCacheRegion(CONFIG_CACHE,mobileCoherenceSiteKey);
					}
				}
		} else if(!BBBUtility.isEmpty(siteId) && !BBBUtility.isEmpty(channel)) {
			cacheKey = pKey + "_" + pConfigType + "_" + channel + "_" + siteId;
			getLocalCacheContainer().remove(cacheKey);
			if(channel.equalsIgnoreCase(MOBILE_WEB)){
				mobileCoherenceSiteKey = mobileCoherenceKey+"_"+siteId;
				invalidateCacheRegion(CONFIG_CACHE, mobileCoherenceSiteKey);
			}
		}
	}

	/**
	 * This method removes the config key from the local config cache container map. 
	 * 
	 * @param cacheName
	 */
	private void invalidateCacheRegion(String cacheName, String mobileCoherenceSiteKey){
		String cacheRegion = BBBConfigRepoUtils.getStringValue(REPO_CACHE_MAP_TYPE, cacheName);
		if(cacheRegion != null)
			getCoherenceCacheContainer().remove(mobileCoherenceSiteKey, cacheRegion);
	}

	/**
	 * @return the configureKeysRepository
	 */
	public MutableRepository getConfigureKeysRepository() {
		return this.configureKeysRepository;
	}

	/**
	 * @param configureKeysRepository the configureKeysRepository to set
	 */
	public void setConfigureKeysRepository(MutableRepository configureKeysRepository) {
		this.configureKeysRepository = configureKeysRepository;
	}

	/**
	 * @return the localCacheContainer
	 */
	public BBBLocalCacheContainer getLocalCacheContainer() {
		return this.localCacheContainer;
	}

	/**
	 * @param localCacheContainer the localCacheContainer to set
	 */
	public void setLocalCacheContainer(BBBLocalCacheContainer localCacheContainer) {
		this.localCacheContainer = localCacheContainer;
	}

	/**
	 * @return the configKeyQueryRql
	 */
	public String getConfigKeyQueryRql() {
		return this.configKeyQueryRql;
	}

	/**
	 * @param configKeyQueryRql the configKeyQueryRql to set
	 */
	public void setConfigKeyQueryRql(String configKeyQueryRql) {
		this.configKeyQueryRql = configKeyQueryRql;
	}

	/**
	 * @return the configToolsImpl
	 */
	public BBBConfigToolsImpl getConfigToolsImpl() {
		return this.configToolsImpl;
	}

	/**
	 * @param configToolsImpl the configToolsImpl to set
	 */
	public void setConfigToolsImpl(BBBConfigToolsImpl configToolsImpl) {
		this.configToolsImpl = configToolsImpl;
	}

	/**
	 * @return the coherenceCacheContainer
	 */
	public CoherenceCacheContainer getCoherenceCacheContainer() {
		return this.coherenceCacheContainer;
	}

	/**
	 * @param coherenceCacheContainer the coherenceCacheContainer to set
	 */
	public void setCoherenceCacheContainer(CoherenceCacheContainer coherenceCacheContainer) {
		this.coherenceCacheContainer = coherenceCacheContainer;
	}

}
