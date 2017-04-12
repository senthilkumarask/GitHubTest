package com.bbb.framework.security;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

public class SessionSecurityBean extends BBBGenericService {

	private static String SESSION_TOKEN = "SESSION-SECURE-ID";
	private static String SECURE_TOKEN_FLAG = "secureTokenFlag";
	private static String SECURE_COOKIE = "COOKIE-SECURE-SID";
	
	public String getSecureTokenstatus() {
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		return (String)request.getSession().getAttribute(SECURE_TOKEN_FLAG);
	}

	public void setSecureTokenstatus(String secureTokenstatus) {
		
		logDebug("SessionSecurityBean.setSecureTokenstatus starts");
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		String sessionToken = null;
		String secureCookieValue = request.getCookieParameter(SECURE_COOKIE);
		logDebug("secureCookieValue = " + secureCookieValue );
		if (null!=request.getSession().getAttribute(SESSION_TOKEN)) {
			sessionToken = (String) request.getSession().getAttribute(SESSION_TOKEN);
			logDebug("sessionToken = " + sessionToken );
		}

		if (null!=sessionToken && sessionToken.equals(secureCookieValue)) {
			request.getSession().setAttribute(SECURE_TOKEN_FLAG, BBBCoreConstants.TRUE);
			logDebug("secureTokenFlag is set to true in the session");
		}
		logDebug("SessionSecurityBean.setSecureTokenstatus ends");
	}
}