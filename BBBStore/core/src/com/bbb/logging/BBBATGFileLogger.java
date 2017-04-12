package com.bbb.logging;

import java.io.File;

import atg.nucleus.logging.RotatingFileLogger;

import com.bbb.constants.BBBLoggingConstants;

public class BBBATGFileLogger extends RotatingFileLogger {
	
    private static final String WEBLOGIC_SERVER = System.getProperty(BBBLoggingConstants.WL_SRVR_NAME);

	@Override
    public void setLogArchivePath(File pLogArchivePath) {
        super.setLogArchivePath(new File(pLogArchivePath, WEBLOGIC_SERVER != null ? WEBLOGIC_SERVER : "no_server_logs"));
    }
    
    @Override
    public void setLogFilePath(File pLogFilePath) {
        super.setLogFilePath(new File(pLogFilePath, WEBLOGIC_SERVER != null ? WEBLOGIC_SERVER : "no_server_logs"));
    }

	
}
