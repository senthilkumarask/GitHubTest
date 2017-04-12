/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBSetCookieDroplet.java
 *
 *  DESCRIPTION: BBBSetCookieDroplet sets the cookie on when a jsp page is called.
 *  			 This droplet should come just after <dsp:page> tag.
 *  			 
 *  HISTORY:
 *  02/16/2012 Initial version
 *	02/20/2012 Moved constants to BBBAccountConstants.java file
 *
 */
package com.bbb.account.droplet;

import java.io.IOException;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.utils.BBBUtility;

import static com.bbb.constants.BBBAccountConstants.*;

public class BBBSetCookieDroplet extends BBBDynamoServlet {
	
	private Map<String,String> mCookies;

	/**
	 * @return the mCookies
	 */
	public Map<String, String> getCookies() {
		return mCookies;
	}

	/**
	 * @param mCookies the mCookies to set
	 */
	public void setCookies(Map pCookies) {
		this.mCookies = (Map<String,String>)pCookies;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String path= pRequest.getParameter(PARAM_COOKIE_PATH);		 
		String domain= pRequest.getParameter(PARAM_COOKIE_DOMAIN);
		if(BBBUtility.isEmpty(domain)){
			domain=pRequest.getServerName();
		}
		if(BBBUtility.isEmpty(path)){
			path="/";
		}
			logDebug("cookies to set:" + getCookies());
			logDebug("path to set:" + path);
			logDebug("domain to set:" + domain);
		if(getCookies()!=null && !getCookies().isEmpty()){
			final Set<String> keys = getCookies().keySet();
			for(String key:keys){
				final String value= getCookies().get(key);
				final Cookie cookie = new Cookie(key, value);
				cookie.setDomain(domain);
				cookie.setPath(path);
				BBBUtility.addCookie(pResponse, cookie, true);
				//pResponse.addCookie(cookie);
			}
		}
			logDebug("cookies set successfully");
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
	}
}