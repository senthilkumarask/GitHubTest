package com.bbb.common.event;


//import com.sprint.common.ContextObject;


import java.util.Date;

import atg.nucleus.logging.LogEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class BBBGSHeartBeatLogEvent.
 */
public class BBBGSHeartBeatLogEvent extends LogEvent {

	public BBBGSHeartBeatLogEvent(String pMessage, Date logTime,
			String storeID, String channel, String appID,
			int appStateValue, String terminalID, String friendlyName,
			String channelTheme) {
		super(pMessage);
		this.logTime = logTime;
		this.storeID = storeID;
		this.channel = channel;
		this.appID = appID;
		this.appStateValue = appStateValue;
		this.terminalID = terminalID;
		this.friendlyName = friendlyName;
		this.channelTheme = channelTheme;
	}


	private Date logTime;
	private String storeID;
	private String channel;
	private String appID;
	private int appStateValue; 
	private String terminalID; 
	private String friendlyName; 
	private String channelTheme ; 
	
	public Date getLogTime() {
		return logTime;
	}


	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	
	public String getStoreID() {
		return storeID;
	}


	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}


	public String getChannel() {
		return channel;
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getAppID() {
		return appID;
	}


	public void setAppID(String appID) {
		this.appID = appID;
	}


	public int getAppStateValue() {
		return appStateValue;
	}


	public void setAppStateValue(int appStateValue) {
		this.appStateValue = appStateValue;
	}


	public String getTerminalID() {
		return terminalID;
	}


	public void setTerminalID(String terminalID) {
		this.terminalID = terminalID;
	}


	public String getFriendlyName() {
		return friendlyName;
	}


	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}


	public String getChannelTheme() {
		return channelTheme;
	}


	public void setChannelTheme(String channelTheme) {
		this.channelTheme = channelTheme;
	}		
}

