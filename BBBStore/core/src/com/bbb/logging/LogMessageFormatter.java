/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  LogMessageFormatter.java
 *
 *  DESCRIPTION: Handles message formatting for logged messages 
 *
 *  HISTORY:
 *  10/13/11 Initial version
 *
 */
package com.bbb.logging;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;

import com.bbb.constants.BBBCoreConstants;


/**
  * This class helps formatting messages before logging the message in the logs.
  * This is work in progress.
  * 
  * 
  * 
  */
public final class LogMessageFormatter {
    
	public final static String MY_RESOURCE_NAME  ="com/bbb/logging/UserResources.properties";
	public final static Properties MESSAGE_SOURCE = new Properties();
	static {
	    try {
            MESSAGE_SOURCE.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(MY_RESOURCE_NAME));
        } catch (IOException e) {
            Nucleus.getGlobalNucleus().logError("LogMessageFormatter: Error loading the messages file", e);
        }
	}
		
	private LogMessageFormatter() {
	    
	}
	//---------------------------------------------------------------------

	
	//---------------------------------------------------------------------------
	/**
	 * Get the locale to use for user-visible error messages.  Returns the
	 * request locale from <code>pRequest</code> if set, otherwise
	 * returns the default server locale.
	 **/

	public static Locale getLocale(DynamoHttpServletRequest pRequest)
	{
		if (pRequest!= null && pRequest.getRequestLocale() != null){
			return pRequest.getRequestLocale().getLocale();
		}
		else{
			return Locale.getDefault();
		}
	}
	
	/**
	 * Return a message from a resource file, taking into account the
	 * locale used for user-visible messages.
	 *
	 * @see atg.core.util.getMsgResource
	 **/

	public static String getMsgResource(String pMessageKey, DynamoHttpServletRequest pRequest)
	{
		

		String results = MESSAGE_SOURCE.getProperty(pMessageKey);
		if(results == null) {
		    return pMessageKey;
		}
        return results;
	}
	
	//---------------------------------------------------------------------
	

	
	/**
	 * This method takes request object and message String. Formats the message with proper attributes
	 * and returns the string. It also fetches error messages for error codes.
	 * @param request
	 * @param message
	 * @return String
	 */
    static public String formatMessage(DynamoHttpServletRequest pRequest, String pMessageKey) {
    	String msgResource = null;
        StringBuffer messageString = new StringBuffer("");
        if(pRequest != null) {
            Profile user = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
            if (user != null) {
                messageString.append(user.getRepositoryId()).append(BBBCoreConstants.PIPE_SYMBOL);
                messageString.append(user.isTransient()).append(BBBCoreConstants.PIPE_SYMBOL);
            }
        }
        if(pMessageKey != null){
        	msgResource = getMsgResource(pMessageKey, pRequest);
        }
        
        messageString.append(msgResource == null ? pMessageKey : msgResource);
        return messageString.toString();
    }
    
    /**
	 * This method takes request object and message String. Formats the message with proper attributes
	 * and returns the string. It also fetches error messages for error codes.
	 * @param request
	 * @param message
	 * @key unique error id
	 * @return String
	 */
    static public String formatMessage(DynamoHttpServletRequest pRequest, String pMessageKey, String key) {
    	String msgResource = null;
        StringBuffer messageString = new StringBuffer("");
        messageString.append(key + ": ");
        if(pRequest != null) {
            Profile user = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
            if (user != null) {
                messageString.append(user.getRepositoryId()).append(BBBCoreConstants.PIPE_SYMBOL);
                messageString.append(user.isTransient()).append(BBBCoreConstants.PIPE_SYMBOL);
            }
        }
        if(pMessageKey != null ) {
        	msgResource = getMsgResource(pMessageKey, pRequest);
        }
        
        messageString.append(msgResource == null ? pMessageKey : msgResource);
        return messageString.toString();
    }
}
