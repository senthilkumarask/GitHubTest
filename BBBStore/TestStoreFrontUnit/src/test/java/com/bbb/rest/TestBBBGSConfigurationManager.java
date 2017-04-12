package com.bbb.rest;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BaseTestCase;
import com.bbb.framework.jaxb.stofu.CentralizedConfiguration;
import com.bbb.rest.stofu.configuration.BBBGSConfigurationManager;
import com.bbb.rest.stofu.configuration.BBBGSConfigurationTools;

public class TestBBBGSConfigurationManager extends BaseTestCase {
	@Mock
	BBBGSConfigurationTools bbbGSConfigurationTools;
	@Mock
	CentralizedConfiguration globalConfig;
	@Mock
	CentralizedConfiguration storeConfig;
	@Mock
	CentralizedConfiguration deviceConfig;

	@Spy
	BBBGSConfigurationManager bbbGSConfigurationManager = new BBBGSConfigurationManager();

	@Test
	public void testGetCentralizedConfigServiceSucc1()
			throws FileNotFoundException, BBBSystemException,
			BBBBusinessException, JAXBException {

		String appId = "10.12.12.12";
		String channelId = "FF1";
		String storeId = "1184";

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL))
				.thenReturn(channelId);
		bbbGSConfigurationManager
				.setBbbGSConfigurationTools(bbbGSConfigurationTools);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getDeviceConfigParams(anyString(), anyString()))
				.thenReturn(deviceConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getStoreConfigParams(anyString(), anyString()))
				.thenReturn(storeConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getGlobalConfigParams(anyString())).thenReturn(
				globalConfig);

		doReturn(storeId).when(bbbGSConfigurationManager)
				.getStoreIdFromDeviceParams(deviceConfig);
		try {
			bbbGSConfigurationManager.getCentralizedConfigService(appId);
			assertTrue(true);
		} catch (BBBBusinessException e) {
			assertTrue(false);
		}

	}
	
	@Test
	public void testGetCentralizedConfigServiceSucc2()
			throws FileNotFoundException, BBBSystemException,
			BBBBusinessException, JAXBException {

		String appId = "delvmpllbbab26";
		String channelId = "FF2";
		String storeId = "1190";

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL))
				.thenReturn(channelId);
		bbbGSConfigurationManager
				.setBbbGSConfigurationTools(bbbGSConfigurationTools);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getDeviceConfigParams(anyString(), anyString()))
				.thenReturn(deviceConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getStoreConfigParams(anyString(), anyString()))
				.thenReturn(storeConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getGlobalConfigParams(anyString())).thenReturn(
				globalConfig);

		doReturn(storeId).when(bbbGSConfigurationManager)
				.getStoreIdFromDeviceParams(deviceConfig);
		try {
			bbbGSConfigurationManager.getCentralizedConfigService(appId);
			assertTrue(true);
		} catch (BBBBusinessException e) {
			assertTrue(false);
		}

	}

	@Test
	public void testGetCentralizedConfigServiceErr1()
			throws FileNotFoundException, BBBSystemException,
			BBBBusinessException, JAXBException {

		String appId = "delvmpllbbab26";
		String channelId = "FF1";
		String storeId = "1190";

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL))
				.thenReturn(channelId);
		bbbGSConfigurationManager
				.setBbbGSConfigurationTools(bbbGSConfigurationTools);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getDeviceConfigParams(anyString(), anyString()))
				.thenReturn(deviceConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getStoreConfigParams(anyString(), anyString()))
				.thenReturn(storeConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getGlobalConfigParams(anyString())).thenReturn(
				globalConfig);

		doReturn(storeId).when(bbbGSConfigurationManager)
				.getStoreIdFromDeviceParams(deviceConfig);

		try {
			bbbGSConfigurationManager.getCentralizedConfigService(appId);
			assertTrue(false);
		} catch (BBBBusinessException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testGetCentralizedConfigServiceErr2()
			throws FileNotFoundException, BBBSystemException,
			BBBBusinessException, JAXBException {

		String appId = "delvmpllbbab26";
		String channelId = "FF1";
		String storeId = "";

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL))
				.thenReturn(channelId);
		bbbGSConfigurationManager
				.setBbbGSConfigurationTools(bbbGSConfigurationTools);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getDeviceConfigParams(anyString(), anyString()))
				.thenReturn(deviceConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getStoreConfigParams(anyString(), anyString()))
				.thenReturn(storeConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getGlobalConfigParams(anyString())).thenReturn(
				globalConfig);

		doReturn(storeId).when(bbbGSConfigurationManager)
				.getStoreIdFromDeviceParams(deviceConfig);

		try {
			bbbGSConfigurationManager.getCentralizedConfigService(appId);
			assertTrue(false);
		} catch (BBBBusinessException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testGetCentralizedConfigServiceErr3()
			throws FileNotFoundException, BBBSystemException,
			BBBBusinessException, JAXBException {

		String appId = "";
		String channelId = "FF1";
		String storeId = "1184";

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL))
				.thenReturn(channelId);
		bbbGSConfigurationManager
				.setBbbGSConfigurationTools(bbbGSConfigurationTools);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getDeviceConfigParams(anyString(), anyString()))
				.thenReturn(deviceConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getStoreConfigParams(anyString(), anyString()))
				.thenReturn(storeConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getGlobalConfigParams(anyString())).thenReturn(
				globalConfig);

		doReturn(storeId).when(bbbGSConfigurationManager)
				.getStoreIdFromDeviceParams(deviceConfig);

		try {
			bbbGSConfigurationManager.getCentralizedConfigService(appId);
			assertTrue(false);
		} catch (BBBBusinessException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testGetCentralizedConfigServiceErr4()
			throws FileNotFoundException, BBBSystemException,
			BBBBusinessException, JAXBException {

		String appId = "10.12.12.12";
		String channelId = "FF3";
		String storeId = "1184";

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL))
				.thenReturn(channelId);
		bbbGSConfigurationManager
				.setBbbGSConfigurationTools(bbbGSConfigurationTools);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getDeviceConfigParams(anyString(), anyString()))
				.thenReturn(deviceConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getStoreConfigParams(anyString(), anyString()))
				.thenReturn(storeConfig);
		when(
				bbbGSConfigurationManager.getBbbGSConfigurationTools()
						.getGlobalConfigParams(anyString())).thenReturn(
				globalConfig);

		doReturn(storeId).when(bbbGSConfigurationManager)
				.getStoreIdFromDeviceParams(deviceConfig);

		try {
			bbbGSConfigurationManager.getCentralizedConfigService(appId);
			assertTrue(false);
		} catch (BBBBusinessException e) {
			assertTrue(true);
		}

	}


}
