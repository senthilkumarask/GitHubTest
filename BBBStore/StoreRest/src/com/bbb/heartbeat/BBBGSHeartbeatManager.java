package com.bbb.heartbeat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.common.BBBGenericService;
import com.bbb.common.event.BBBGSHeartBeatLogEvent;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

public class BBBGSHeartbeatManager extends BBBGenericService{
	public Map<String, String> appStateMap;
	private final String FORM_FACTOR_1 = "FF1"; 
	public Map<String, String> getAppStateMap() {
		return appStateMap;
	}
	
	public void setAppStateMap(Map<String, String> appStateMap) {
		this.appStateMap = appStateMap;
	}
	
	public List<String> appStateKey;
	
	public List<String> getAppStateKey() {
		return appStateKey;
	}
	public void setAppStateKey(List<String> appStateKey) {
		this.appStateKey = appStateKey;
	}
	
	private List<String> gsApplicableChannels;
	
	public List<String> getGsApplicableChannels() {
		return gsApplicableChannels;
	}
	public void setGsApplicableChannels(List<String> gsApplicableChannels) {
		this.gsApplicableChannels = gsApplicableChannels;
	}
	
	public static final String EMPTY_STRING = "" ;
	
	public boolean submitHeartbeatService(String appId, String appState, String terminalId, String friendlyName){
		int appStateValue = -1;
		logDebug("BBBGSHeartbeatManager:submitHeartbeatService - Start");
		if( BBBUtility.isEmpty(appId) || BBBUtility.isEmpty(appState) || BBBUtility.isEmpty(friendlyName) ){
			logError(BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID_ERROR);
			return false;
		}
		if( BBBUtility.isEmpty(terminalId) ){
			terminalId = EMPTY_STRING ;
			logDebug("appId : " + appId + " appState : " + appState + " friendlyName : " + friendlyName);
		}else{
			logDebug("appId : " + appId + " appState : " + appState + " terminalId : " + terminalId + " friendlyName : " + friendlyName);
		}
		for(String str : this.getAppStateMap().keySet()){
			if( appState.equalsIgnoreCase(str) ){
				appStateValue = Integer.parseInt(this.getAppStateMap().get(str));
				break;
			}
		}
		
		if(appStateValue == -1){
			logError("App State Value ["+appStateValue+"] " + BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID_ERROR);
			return false;
		}
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);		
		
		String storeId = pRequest.getHeader(BBBCoreConstants.HEADER_STORE_ID);
	
		if(BBBUtility.isEmpty(channel)
				|| BBBUtility.isEmpty(storeId)
				|| !(this.getGsApplicableChannels().contains(channel))){
			logError(BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID_ERROR);
			return false;
		}

		String channelTheme = pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME);
		if(BBBUtility.isEmpty(channelTheme)){
			channelTheme = EMPTY_STRING ;
		}
		
		logDebug("Channel ["+channel+"] , Store Id ["+storeId+"] , Channel Theme ["+channelTheme+"]"); 

		if(channel.equalsIgnoreCase(FORM_FACTOR_1)){
			if(!BBBUtility.isValidIpAddress(appId)){
				logError("Invalid IP Address ["+appId+"] " + BBBCatalogErrorCodes.INPUT_PARAMETER_INVALID_ERROR);
				return false;
			}
		}
		
		Date currentTimestamp = new Date();
		
//		this method logs heart beat information in database using sql logger. 
		this.sendLogEvent(new BBBGSHeartBeatLogEvent("sendHeartbeatService", currentTimestamp, storeId, channel, appId, appStateValue, terminalId, friendlyName, channelTheme));
		
		logDebug("BBBGSHeartbeatManager:submitHeartbeatService - End");
		return true;
	}

}
