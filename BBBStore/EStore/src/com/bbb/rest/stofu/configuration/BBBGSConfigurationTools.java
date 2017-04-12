package com.bbb.rest.stofu.configuration;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.stofu.CentralizedConfiguration;

public interface BBBGSConfigurationTools {
	public CentralizedConfiguration getDeviceConfigParams(String appId,
			String channelId) throws BBBSystemException, BBBBusinessException;

	public CentralizedConfiguration getStoreConfigParams(String storeId,
			String channelId) throws BBBSystemException, BBBBusinessException;

	public CentralizedConfiguration getGlobalConfigParams(String channelId)
			throws BBBSystemException,
			BBBBusinessException;
}
