package com.bbb.logging;

import atg.nucleus.logging.DebugLogEvent;
import atg.nucleus.logging.ErrorLogEvent;
import atg.nucleus.logging.InfoLogEvent;
import atg.nucleus.logging.LogEvent;
import atg.nucleus.logging.PrintStreamLogger;
import atg.nucleus.logging.TraceLogEvent;
import atg.nucleus.logging.WarningLogEvent;

public class BBBPrintStreamLogger extends PrintStreamLogger {

	private boolean mPrefixThreadName;

	public void logEvent(LogEvent pLogEvent) {
		LogEvent logEvent = pLogEvent;
		if (mPrefixThreadName && pLogEvent != null) {
			StringBuilder originator = new StringBuilder();
			originator.append(pLogEvent.getOriginator() != null ? pLogEvent.getOriginator().replace('\n', ' ') : pLogEvent.getNullObjectReplacement());
			originator.append(pLogEvent.getTokenSeperator());
			originator.append(Thread.currentThread().getName());
			
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
		}
		super.logEvent(logEvent);
	}

	public boolean isPrefixThreadName() {
		return mPrefixThreadName;
	}

	public void setPrefixThreadName(boolean prefixThreadName) {
		this.mPrefixThreadName = prefixThreadName;
	}
}
