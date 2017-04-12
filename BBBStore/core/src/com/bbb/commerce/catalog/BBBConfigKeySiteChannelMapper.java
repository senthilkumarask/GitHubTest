package com.bbb.commerce.catalog;

import atg.nucleus.ServiceMap;

import com.bbb.common.BBBGenericService;

public class BBBConfigKeySiteChannelMapper extends BBBGenericService {
	private ServiceMap mSiteChannelToConfigKeyMap;

	public ServiceMap getSiteChannelToConfigKeyMap() {
		return mSiteChannelToConfigKeyMap;
	}

	public void setSiteChannelToConfigKeyMap(ServiceMap pSiteChannelToConfigKeyMap) {
		mSiteChannelToConfigKeyMap = pSiteChannelToConfigKeyMap;
	}
}
