package com.bbb.rest.stofu.vo;

import java.util.Date;

public class HeartbeatMonitoringVO {
	private String appId;
	private String appState;
	private String terminalId;
	private String friendlyName;
	private String channel;
	private String channelTheme;
	private Date logTime;
	private String storeId;
	private String health;
	
	public HeartbeatMonitoringVO() {
		// TODO Auto-generated constructor stub
	}

	public HeartbeatMonitoringVO(String appId, String appState,
			String terminalId, String friendlyName, String channel,
			String channelTheme, Date logTime, String storeId, String health) {
		super();
		this.appId = appId;
		this.appState = appState;
		this.terminalId = terminalId;
		this.friendlyName = friendlyName;
		this.channel = channel;
		this.channelTheme = channelTheme;
		this.logTime = logTime;
		this.storeId = storeId;
		this.health = health;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppState() {
		return appState;
	}

	public void setAppState(String appState) {
		this.appState = appState;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannelTheme() {
		return channelTheme;
	}

	public void setChannelTheme(String channelTheme) {
		this.channelTheme = channelTheme;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}
	
}
