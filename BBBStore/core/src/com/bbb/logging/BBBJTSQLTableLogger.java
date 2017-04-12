package com.bbb.logging;

import com.bbb.common.event.BBBGSHeartBeatLogEvent;

import atg.nucleus.logging.LogEvent;
import atg.service.datacollection.JTSQLTableLogger;

public class BBBJTSQLTableLogger extends JTSQLTableLogger {
	
	@Override
	public void logEvent(LogEvent pLogEvent) {
		if(pLogEvent instanceof BBBGSHeartBeatLogEvent){
			if (isLoggingDebug()) {
				logDebug("Received Heartbeat Event in BBBJTSQLTableLogger ");
			}
		}
		super.logEvent(pLogEvent);
		if(isLoggingDebug()){
			logDebug("BBBJTSQLTableLogger : End  Log Event");
		}
	}
	
	@Override
	public void addDataItem(Object pDataItem) {
		if(isLoggingDebug()){
			logDebug("BBBJTSQLTableLogger - Start addDataItem ");
		}
		super.addDataItem(pDataItem);
		if(isLoggingDebug()){
			logDebug("BBBJTSQLTableLogger - End addDataItem ");
		}
	}

}
