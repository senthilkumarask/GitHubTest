package com.bbb.rest;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBGenericService;
import com.bbb.common.event.BBBGSHeartBeatLogEvent;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.BaseTestCase;
import com.bbb.heartbeat.BBBGSHeartbeatManager;
public class TestHeartbeatService extends BaseTestCase{
	@Spy BBBGSHeartbeatManager heartbeatManager = new BBBGSHeartbeatManager();	
	List<String> appStateKey = new LinkedList<String>();
	Map<String,String> appStateMap =  new HashMap<String, String>();
	@Mock BBBGenericService bbbGenericService;
	@Mock BBBGSHeartBeatLogEvent bbbHeartbeatLogEvent;
	
	@Override
	public void setUp() {
		super.setUp();
		appStateMap.put("starting", "0");
		appStateMap.put("heartbeat", "1");
		appStateMap.put("closing", "2");		
	}	

	@Test
	public void testSubmitHeartbeatServiceSucc1() {
		this.setUp();
		String appId = "10.202.12.12" ;
		String appState = "heartbeat" ;
		String terminalId = "bbby-app-01" ;
		String friendlyName = "application 1" ;

		DynamoHttpServletRequest pRequest  = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL)).thenReturn("FF1");
		when(pRequest.getHeader(BBBCoreConstants.HEADER_STORE_ID)).thenReturn("1184");
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME)).thenReturn("channelTheme1");		
		
		
		heartbeatManager.setAppStateKey(appStateKey);
		heartbeatManager.setAppStateMap(appStateMap);
		heartbeatManager.sendLogEvent(bbbHeartbeatLogEvent);
		
		//heartbeatManager.sendLogEvent(pLogEvent);logHeartBeatInfo(anyString(), anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), (Date) anyObject());		
		boolean	result = heartbeatManager.submitHeartbeatService(appId, appState, terminalId, friendlyName);
		assertTrue(result);		
	}
	@Test
	public void testSubmitHeartbeatServiceSucc2() {
		this.setUp();
		String appId = "delvmpllbbab54" ;
		String appState = "closing" ;
		String terminalId = null ;
		String friendlyName = "application 1" ;

		DynamoHttpServletRequest pRequest  = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL)).thenReturn("FF2");
		when(pRequest.getHeader(BBBCoreConstants.HEADER_STORE_ID)).thenReturn("1184");
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME)).thenReturn(null);		
		
		
		heartbeatManager.setAppStateKey(appStateKey);
		heartbeatManager.setAppStateMap(appStateMap);
		heartbeatManager.sendLogEvent(bbbHeartbeatLogEvent);
	//	heartbeatManager.logHeartBeatInfo(anyString(), anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), (Date) anyObject());		
		boolean	result = heartbeatManager.submitHeartbeatService(appId, appState, terminalId, friendlyName);
		assertTrue(result);		
	}
	@Test
	public void testSubmitHeartbeatServiceErr1() {
		this.setUp();
		String appId = "delvmpllbbab54" ;
		String appState = "heartbeat" ;
		String terminalId = "bbby-app-01" ;
		String friendlyName = "application 1" ;

		DynamoHttpServletRequest pRequest  = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL)).thenReturn("FF1");
		when(pRequest.getHeader(BBBCoreConstants.HEADER_STORE_ID)).thenReturn("1184");
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME)).thenReturn("channelTheme1");		
		
		
		heartbeatManager.setAppStateKey(appStateKey);
		heartbeatManager.setAppStateMap(appStateMap);
		heartbeatManager.sendLogEvent(bbbHeartbeatLogEvent);
		//heartbeatManager.logHeartBeatInfo(anyString(), anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), (Date) anyObject());		
		boolean	result = heartbeatManager.submitHeartbeatService(appId, appState, terminalId, friendlyName);
		assertFalse(result);		
	}
	@Test
	public void testSubmitHeartbeatServiceErr2() {
		this.setUp();
		String appId = null ;
		String appState = "heartbeat" ;
		String terminalId = "bbby-app-01" ;
		String friendlyName = "application 1" ;

		DynamoHttpServletRequest pRequest  = ServletUtil.getCurrentRequest();
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL)).thenReturn("FF2");
		when(pRequest.getHeader(BBBCoreConstants.HEADER_STORE_ID)).thenReturn("1184");
		when(pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME)).thenReturn("channelTheme1");		
		
		
		heartbeatManager.setAppStateKey(appStateKey);
		heartbeatManager.setAppStateMap(appStateMap);
		heartbeatManager.sendLogEvent(bbbHeartbeatLogEvent);
		//heartbeatManager.logHeartBeatInfo(anyString(), anyString(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), (Date) anyObject());		
		boolean	result = heartbeatManager.submitHeartbeatService(appId, appState, terminalId, friendlyName);
		assertFalse(result);		

	}

}
