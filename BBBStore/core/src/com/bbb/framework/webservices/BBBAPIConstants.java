/*
 *
 * File  : BBBAPIConstants.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.webservices;

public class BBBAPIConstants {
	
	/**
	 * DEFAULT TIME OUT FOR WEB SERVICE
	 */
	public static int DEFAULT_WS_TIMEOUT = 5000;	// 5 secs
	
	/**
	 * Service Handler component path
	 */	
	public static final String SERVICE_HANDLER = "/com/bbb/framework/webservices/handlers/ServiceHandler";
	public static final String MSG_SERVICE_HANDLER = "/com/bbb/framework/messaging/handlers/MessageServiceHandler";
	public BBBAPIConstants(){
		super();
	}
}