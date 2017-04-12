package com.bbb.rest.stofu.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.stofu.Category;
import com.bbb.framework.jaxb.stofu.CentralizedConfiguration;
import com.bbb.framework.jaxb.stofu.Config;
import com.bbb.rest.stofu.vo.BBBGSCategoryVO;
import com.bbb.rest.stofu.vo.DefaultCategoryVO;
import com.bbb.utils.BBBUtility;

public class BBBGSConfigurationManager extends BBBGenericService {

	private static final String DEFAULT = "default";
	private static final String DEFAULT_CATEGORIES = "defaultCategories";
	private static final String DEVICE_FF1_STORE = "storeID";
	private static final String DEVICE_FF2_STORE = "storeNumber";

	private BBBGSConfigurationTools bbbGSConfigurationTools;

	public BBBGSConfigurationTools getBbbGSConfigurationTools() {
		return bbbGSConfigurationTools;
	}

	public void setBbbGSConfigurationTools(
			BBBGSConfigurationTools bbbGSConfigurationTools) {
		this.bbbGSConfigurationTools = bbbGSConfigurationTools;
	}
	private List<String> gsApplicableChannels ;
	
	public List<String> getGsApplicableChannels() {
		return gsApplicableChannels;
	}
	public void setGsApplicableChannels(List<String> gsApplicableChannels) {
		this.gsApplicableChannels = gsApplicableChannels;
	}

	public Map<String, Object> getCentralizedConfigService(
			String appId) throws BBBSystemException, BBBBusinessException {

		logDebug("BBBGSConfigurationManager:getCentralizedConfigService - Start");

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();

		String channelId = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		String channelThemeId = pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME);
		logDebug("Channel Id ["+channelId+"] , Theme Id ["+channelThemeId+"] , App ID ["+appId+"]");
		/*if (BBBUtility.isEmpty(channelId)
				|| BBBUtility.isEmpty(appId)
				|| !(channelId.equalsIgnoreCase(BBBCoreConstants.FF1) || channelId
						.equalsIgnoreCase(BBBCoreConstants.FF2))) {*/
		if (BBBUtility.isEmpty(channelId)
				|| BBBUtility.isEmpty(appId)
				|| !(this.getGsApplicableChannels().contains(channelId))) {			
			logError(BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID_ERROR);
			throw new BBBBusinessException(
					BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID);
		}

		if (channelId.equalsIgnoreCase(BBBCoreConstants.FF1)) {
			if (!BBBUtility.isValidIpAddress(appId)) {
				logError("Invalid IP Address [" + appId + "] "
						+ BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID_ERROR);
				throw new BBBBusinessException(
						BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID);
			}
		}

		String storeId = "";

		Map<String, Object> centralizedConfigMap = new HashMap<String, Object>();

		CentralizedConfiguration deviceConfigParams = this
				.getBbbGSConfigurationTools().getDeviceConfigParams(appId,
						channelId);
		if (null != deviceConfigParams) {
			storeId = getStoreIdFromDeviceParams(deviceConfigParams);
		}
		
		if(BBBUtility.isEmpty(storeId)){
			logError("Store ID is missing in the device configuration file");
			throw new BBBBusinessException("Store ID is missing in the device configuration file");
		}
		
		CentralizedConfiguration storeConfigParams = this
				.getBbbGSConfigurationTools().getStoreConfigParams(storeId,
						channelId);

		CentralizedConfiguration globalConfigParams = this
				.getBbbGSConfigurationTools().getGlobalConfigParams(channelId);
		
		logDebug("Adding device specific configuration parameters - Start");
		for (Config config : deviceConfigParams.getConfig()) {
			if (config.getTheme().equalsIgnoreCase(DEFAULT) || (BBBUtility.isNotEmpty(channelThemeId)
					&& config.getTheme().equalsIgnoreCase(channelThemeId))) {
				centralizedConfigMap.put(config.getKey(), config.getValue());
			}
		}
		
		logDebug("Adding device specific configuration parameters - End");
		
		logDebug("Adding store specific configuration parameters - Start");

		for (Config config : storeConfigParams.getConfig()) {
			if (config.getTheme().equalsIgnoreCase(DEFAULT) || (BBBUtility.isNotEmpty(channelThemeId)
					&& config.getTheme().equalsIgnoreCase(channelThemeId))) {
				centralizedConfigMap.put(config.getKey(), config.getValue());
			}
		}
		logDebug("Adding store specific configuration parameters - End");

		logDebug("Adding global configuration parameters - Start");

		for (Config config : globalConfigParams.getConfig()) {
			if (config.getTheme().equalsIgnoreCase(DEFAULT) || (BBBUtility.isNotEmpty(channelThemeId)
					&& config.getTheme().equalsIgnoreCase(channelThemeId))) {
				centralizedConfigMap.put(config.getKey(), config.getValue());
			}
		}
		logDebug("Adding global configuration parameters - End");


		List<BBBGSCategoryVO> listDefaultCategories = new ArrayList<BBBGSCategoryVO>() ;
		DefaultCategoryVO defaultCategoryVO = new DefaultCategoryVO() ;
		
		if (!(globalConfigParams.getDefaultCategories().isEmpty() || globalConfigParams
				.getDefaultCategories() == null)) {
			logDebug("Adding default categories configuration parameters");
			for(Category defaultCategory : globalConfigParams.getDefaultCategories().get(0).getCategory()){
				if(null != defaultCategory.getTheme() && defaultCategory.getTheme().equalsIgnoreCase(channelThemeId)){
					BBBGSCategoryVO categoryVO = new BBBGSCategoryVO() ;
					categoryVO.setDefaultCategory(defaultCategory.getDefaultCategory());
					categoryVO.setId(defaultCategory.getId());
					categoryVO.setName(defaultCategory.getName());
					listDefaultCategories.add(categoryVO);				
				}				
			}
			
			defaultCategoryVO.setCategory(listDefaultCategories);
			
			centralizedConfigMap.put(DEFAULT_CATEGORIES, defaultCategoryVO);
		}

		logDebug("BBBGSConfigurationManager:getCentralizedConfigService - End");
		return centralizedConfigMap;
	}

	public String getStoreIdFromDeviceParams(
			CentralizedConfiguration deviceConfigParams) {

		String storeId = "";

		for (Config list : deviceConfigParams.getConfig()) {
			if (list.getKey().equals(DEVICE_FF1_STORE)
					|| list.getKey().equals(DEVICE_FF2_STORE)) {
				storeId = list.getValue();
				break;
			}
		}

		return storeId;
	}
}
