package com.bbb.rest.stofu.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.stofu.CentralizedConfiguration;

public class BBBGSConfigurationToolsImpl extends BBBGenericService implements
		BBBGSConfigurationTools {

	private BBBCatalogToolsImpl bbbCatalogToolsImpl;

	private String deviceConfigFilePath ;

	public String getDeviceConfigFilePath() {
		return deviceConfigFilePath;
	}

	public void setDeviceConfigFilePath(String deviceConfigFilePath) {
		this.deviceConfigFilePath = deviceConfigFilePath;
	}

	private String storeConfigFilePath;

	public String getStoreConfigFilePath() {
		return storeConfigFilePath;
	}

	public void setStoreConfigFilePath(String storeConfigFilePath) {
		this.storeConfigFilePath = storeConfigFilePath;
	}

	public BBBCatalogToolsImpl getBbbCatalogToolsImpl() {
		return bbbCatalogToolsImpl;
	}

	private String globalConfigFilePath;

	public String getGlobalConfigFilePath() {
		return globalConfigFilePath;
	}

	public void setGlobalConfigFilePath(String globalConfigFilePath) {
		this.globalConfigFilePath = globalConfigFilePath;
	}

	public void setBbbCatalogToolsImpl(BBBCatalogToolsImpl bbbCatalogToolsImpl) {
		this.bbbCatalogToolsImpl = bbbCatalogToolsImpl;
	}

	private String deviceFileNamePrefix;

	public String getDeviceFileNamePrefix() {
		return deviceFileNamePrefix;
	}

	public void setDeviceFileNamePrefix(String deviceFileNamePrefix) {
		this.deviceFileNamePrefix = deviceFileNamePrefix;
	}

	private String storeFileNamePrefix;

	public String getStoreFileNamePrefix() {
		return storeFileNamePrefix;
	}

	public void setStoreFileNamePrefix(String storeFileNamePrefix) {
		this.storeFileNamePrefix = storeFileNamePrefix;
	}

	private String globalFileNamePrefix;

	public String getGlobalFileNamePrefix() {
		return globalFileNamePrefix;
	}

	public void setGlobalFileNamePrefix(String globalFileNamePrefix) {
		this.globalFileNamePrefix = globalFileNamePrefix;
	}

	private final String CONFIG_FILES_SERVER_PATH = "gs_config_server_path";

	private CentralizedConfiguration centralizedConfiguration;
	private FileInputStream fstream;
	private String filePath;

	@Override
	public CentralizedConfiguration getDeviceConfigParams(String appId,
			String channelId) throws BBBSystemException, BBBBusinessException {

		logDebug("BBBGSConfigurationToolsImpl:getDeviceConfigParams - Start");

		centralizedConfiguration = new CentralizedConfiguration();

		filePath = this
				.getBbbCatalogToolsImpl()
				.getConfigValueByconfigType(
						BBBCatalogConstants.CONTENT_CATALOG_KEYS)
				.get(CONFIG_FILES_SERVER_PATH);

		filePath = filePath + this.getDeviceConfigFilePath() + channelId + "/"
				+ this.getDeviceFileNamePrefix() + appId + ".xml";
		logDebug("Device Config File Path [" + filePath + "]");
		try {
			fstream = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			logError("Device Configuration file not found on this path :"
					+ filePath, e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.FILE_NOT_FOUND_EXCEPTION, e);
		}

		centralizedConfiguration = configurationFileUnmarshaller(fstream);

		logDebug("BBBGSConfigurationToolsImpl:getDeviceConfigParams - End");

		return centralizedConfiguration;
	}

	@Override
	public CentralizedConfiguration getStoreConfigParams(String storeId,
			String channelId) throws BBBSystemException, BBBBusinessException {

		logDebug("BBBGSConfigurationToolsImpl:getStoreConfigParams - Start");

		centralizedConfiguration = new CentralizedConfiguration();

		String filePath = this
				.getBbbCatalogToolsImpl()
				.getConfigValueByconfigType(
						BBBCatalogConstants.CONTENT_CATALOG_KEYS)
				.get(CONFIG_FILES_SERVER_PATH);

		filePath = filePath + this.getStoreConfigFilePath() + channelId + "/"
				+ this.getStoreFileNamePrefix() + storeId + ".xml";

		logDebug("Store Config File Path [" + filePath + "]");
		try {
			fstream = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			logError("Store Configuration file not found on this path"
					+ filePath, e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.FILE_NOT_FOUND_EXCEPTION, e);
		}

		centralizedConfiguration = configurationFileUnmarshaller(fstream);

		logDebug("BBBGSConfigurationToolsImpl:getStoreConfigParams - End");

		return centralizedConfiguration;
	}

	@Override
	public CentralizedConfiguration getGlobalConfigParams(String channelId)
			throws BBBSystemException, BBBBusinessException {

		logDebug("BBBGSConfigurationToolsImpl:getGlobalConfigParams - Start");

		centralizedConfiguration = new CentralizedConfiguration();

		String filePath = this
				.getBbbCatalogToolsImpl()
				.getConfigValueByconfigType(
						BBBCatalogConstants.CONTENT_CATALOG_KEYS)
				.get(CONFIG_FILES_SERVER_PATH);

		filePath = filePath + this.getGlobalConfigFilePath() + channelId
				+ ".xml";
		logDebug("Global Config File Path [" + filePath + "]");
		try {
			fstream = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			logError("Global Configuration file not found on this path"
					+ filePath, e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.FILE_NOT_FOUND_EXCEPTION, e);
		}

		centralizedConfiguration = configurationFileUnmarshaller(fstream);

		logDebug("BBBGSConfigurationToolsImpl:getStoreConfigParams - End");

		return centralizedConfiguration;
	}

	private CentralizedConfiguration configurationFileUnmarshaller(
			FileInputStream fstream) throws BBBSystemException {

		CentralizedConfiguration centralizedConfigurationUnmarshalled = null;

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext
					.newInstance(CentralizedConfiguration.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			centralizedConfigurationUnmarshalled = (CentralizedConfiguration) jaxbUnmarshaller
					.unmarshal(fstream);
		} catch (JAXBException e) {
			logError("JaxB Exception Occurred", e);
			throw new BBBSystemException("JaxB Exception Occurred", e);
		}

		return centralizedConfigurationUnmarshalled;
	}

}
