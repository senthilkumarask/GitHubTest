
package com.bbb.logging;

import com.bbb.common.event.BBBGSHeartBeatLogEvent;
import com.bbb.common.event.PersistedInfoLogEvent;
import com.bbb.constants.BBBCoreConstants;

import atg.nucleus.logging.DebugLogEvent;
import atg.nucleus.logging.ErrorLogEvent;
import atg.nucleus.logging.InfoLogEvent;
import atg.nucleus.logging.LogEvent;
import atg.nucleus.logging.LogListenerQueue;
import atg.nucleus.logging.TraceLogEvent;
import atg.nucleus.logging.WarningLogEvent;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;


public class BBBLogListenerQueue extends LogListenerQueue {

	private static final String NOT = "!";
	private static final String DOMAIN_START = "domain=[";
	private static final String BRACKET_CLOSE = "]";
	private boolean mPrefixThreadName;
	private boolean mPrefixJSessionId;

	public void logEvent(LogEvent pLogEvent) {
		LogEvent logEvent = pLogEvent;
		
		if (mPrefixThreadName && pLogEvent != null) {
			StringBuilder originator = new StringBuilder();
			originator.append(pLogEvent.getOriginator() != null ? pLogEvent.getOriginator().replace('\n', ' ') : pLogEvent.getNullObjectReplacement());
			DynamoHttpServletRequest pReq  = (DynamoHttpServletRequest)ServletUtil.getCurrentRequest();
			if(pReq != null && pReq.getHeader(BBBCoreConstants.CLIENT_ID_PARM) !=null){
				originator.append(pLogEvent.getTokenSeperator());
				originator.append(BBBCoreConstants.CLIENT_ID);
				originator.append(pReq.getHeader(BBBCoreConstants.CLIENT_ID_PARM));
			}
			originator.append(pLogEvent.getTokenSeperator());
			originator.append(Thread.currentThread().getName());
			if (pReq != null) {
				appendDomainInformation(pLogEvent.getTokenSeperator(), originator, pReq.getServerName());
			}
			
			
			if (pLogEvent instanceof BBBGSHeartBeatLogEvent){				
				BBBGSHeartBeatLogEvent heartBeatLogEvent = this.getHeartBeatLogEvent(pLogEvent) ;
				super.logEvent(heartBeatLogEvent);
				return;
			}
			
			if(pLogEvent instanceof PersistedInfoLogEvent){
				PersistedInfoLogEvent pPersLogEvent = new PersistedInfoLogEvent(pLogEvent.getMessage(),((PersistedInfoLogEvent) pLogEvent).getIdentifier(),((PersistedInfoLogEvent) pLogEvent).getUpcCode(),((PersistedInfoLogEvent) pLogEvent).getLongitude(),((PersistedInfoLogEvent) pLogEvent).getLatitude(),((PersistedInfoLogEvent) pLogEvent).getSiteID(),((PersistedInfoLogEvent) pLogEvent).getChannel(),((PersistedInfoLogEvent) pLogEvent).getStoreID(),((PersistedInfoLogEvent) pLogEvent).getTime(), ((PersistedInfoLogEvent) pLogEvent).getType(), ((PersistedInfoLogEvent) pLogEvent).getMessageDescription());
				super.logEvent(pPersLogEvent);
				return;
			}
			
			
			if (pLogEvent instanceof DebugLogEvent) {
				logEvent = new DebugLogEvent(pLogEvent.getMessage(), originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof ErrorLogEvent) {
				logEvent = new ErrorLogEvent(pLogEvent.getMessage(), originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof InfoLogEvent) {
				logEvent = new InfoLogEvent(pLogEvent.getMessage(), originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof WarningLogEvent) {
				logEvent = new WarningLogEvent(pLogEvent.getMessage(), originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof TraceLogEvent) {
				logEvent = new TraceLogEvent(pLogEvent.getMessage(), originator.toString(), pLogEvent.getThrowable());
			}
		} else if (isPrefixJSessionId() && pLogEvent != null) {
			StringBuilder originator = new StringBuilder();
			originator.append(pLogEvent.getOriginator() != null ? pLogEvent
					.getOriginator().replace('\n', ' ') : pLogEvent
					.getNullObjectReplacement());
			originator.append(pLogEvent.getTokenSeperator());
			if (SiteContextManager.getCurrentSiteContext() != null) {
				String jSessionId = SiteContextManager.getCurrentSiteContext()
						.getSiteSession().getHttpSessionId();
				if (jSessionId != null) {
					int index = jSessionId.indexOf(NOT);
					if (index > 0) {
						originator.append("["
								+ jSessionId.substring(0,
										jSessionId.indexOf(NOT)) + "]");
					} else {
						originator.append("[" + jSessionId + "]");
					}

				}
			}
			if (pLogEvent instanceof DebugLogEvent) {
				logEvent = new DebugLogEvent(pLogEvent.getMessage(),
						originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof ErrorLogEvent) {
				logEvent = new ErrorLogEvent(pLogEvent.getMessage(),
						originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof InfoLogEvent) {
				logEvent = new InfoLogEvent(pLogEvent.getMessage(),
						originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof WarningLogEvent) {
				logEvent = new WarningLogEvent(pLogEvent.getMessage(),
						originator.toString(), pLogEvent.getThrowable());
			} else if (pLogEvent instanceof TraceLogEvent) {
				logEvent = new TraceLogEvent(pLogEvent.getMessage(),
						originator.toString(), pLogEvent.getThrowable());
			}
		}
		super.logEvent(logEvent);
	}

	private void appendDomainInformation(String pattern, StringBuilder originator, String serverName) {
		
			originator.append(pattern);
			originator.append(DOMAIN_START);
			originator.append(serverName);
			originator.append(BRACKET_CLOSE);
	}

	private BBBGSHeartBeatLogEvent getHeartBeatLogEvent(LogEvent pLogEvent) {
		BBBGSHeartBeatLogEvent inputEvent = (BBBGSHeartBeatLogEvent)pLogEvent; 
		BBBGSHeartBeatLogEvent heartBeatLogEvent = new BBBGSHeartBeatLogEvent(pLogEvent.getMessage(), inputEvent.getLogTime(), inputEvent.getStoreID(), inputEvent.getChannel(),inputEvent.getAppID(), inputEvent.getAppStateValue(), inputEvent.getTerminalID(), inputEvent.getFriendlyName(), inputEvent.getChannelTheme() );
		return heartBeatLogEvent;
	}

	public boolean isPrefixThreadName() {
		return mPrefixThreadName;
	}

	public void setPrefixThreadName(boolean prefixThreadName) {
		this.mPrefixThreadName = prefixThreadName;
	}

	/**
	 * @return the mPrefixJSessionId
	 */
	public boolean isPrefixJSessionId() {
		return mPrefixJSessionId;
	}

	/**
	 * @param mPrefixJSessionId the mPrefixJSessionId to set
	 */
	public void setPrefixJSessionId(boolean mPrefixJSessionId) {
		this.mPrefixJSessionId = mPrefixJSessionId;
	}
}
