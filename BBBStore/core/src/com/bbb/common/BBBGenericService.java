package com.bbb.common;

import atg.nucleus.GenericService;

import com.bbb.common.event.PersistedInfoLogEvent;

public class BBBGenericService extends GenericService {
	
	public BBBGenericService() {
		super();
	}

	public void logError(String msg, Throwable e){
		if (isLoggingError()) {
			super.logError(msg, e);
		}
	}
	public void logError(String msg){
		if (isLoggingError()) {
			super.logError(msg);
		}
	}
	public void logError(Throwable e){
		if (isLoggingError()) {
			super.logError(e);
		}
	}
	
	public void logDebug(String msg, Throwable e){
		if (isLoggingDebug()) {
			super.logDebug(msg, e);
		}
	}
	public void logDebug(String msg){
		if (isLoggingDebug()) {
			super.logDebug(msg);
		}
	}
	public void logDebug(Throwable e) {
		if (isLoggingDebug()) {
			super.logDebug(e);
		}
	}
	public void logInfo(String msg, Throwable e) {
		if (isLoggingInfo()) {
			super.logInfo(msg,e);
		}
	}

	public void logInfo(String msg) {
		if (isLoggingInfo()) {
			super.logInfo(msg);
		}
	}

	public void logInfo(Throwable e) {
		if (isLoggingInfo()) {
			super.logInfo(e);
		}
	}
	
	public void logTrace(String msg, Throwable e){
		if (isLoggingTrace()) {
			super.logTrace(msg, e);
		}
	}
	public void logTrace(String msg){
		if (isLoggingTrace()) {
			super.logTrace(msg);
		}
	}
	public void logTrace(Throwable e){
		if (isLoggingTrace()) {
			super.logTrace(e);
		}
	}
	
	public void logPersistedInfo (String pMessage,String upcCode, String longitude,String latitude,String siteID,String channel,String storeID,String time)  {
              this.sendLogEvent(new PersistedInfoLogEvent( pMessage, upcCode,  longitude, latitude, siteID, channel, storeID, time) );
}
	
	public void logPersistedInfo (String pMessage,String identifier, String upcCode, String longitude,String latitude,String siteID,String channel,String storeID,String time)  {
        this.sendLogEvent(new PersistedInfoLogEvent( pMessage, identifier, upcCode,  longitude, latitude, siteID, channel, storeID, time) );
}
	public void logPersistedInfo (String pMessage, String identifier, String upcCode, String longitude, String latitude, String siteID, String channel, String storeID, String time, String type, String messageDescription)  {
        this.sendLogEvent(new PersistedInfoLogEvent( pMessage, identifier, upcCode, longitude, latitude, siteID, channel, storeID, time, type, messageDescription) );
	}


	}

