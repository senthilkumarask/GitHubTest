/*
 *  Copyright 2012, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBRestAccessController.java
 *
 *  DESCRIPTION: A pipeline servlet handled the authorization logic for 
 *  			 users
 *  HISTORY:
 *  10/12/12 Initial version
 *
 */
package com.bbb.pipeline;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;


import javax.servlet.http.Cookie;

import atg.nucleus.naming.ComponentName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;


/**
 * @author ugoel
 *
 */
public class BBBRestAccessController extends BBBAccessController{
	private boolean mEnableRestCheckLogin;
	private String mRestContextPath;
	private Map<String, String> mobileLoginRequiredURLs;
	private Map<String, String> mobileAutoLoginRequiredURLs;

	public final static Boolean HANDLE_SUCCESS = Boolean.TRUE;
	
 
	private static final Integer COOKIE_LOGIN_SECURITY_STATUS = Integer.valueOf(2);
	private static final Integer LOGGED_IN_USER_SECURITY_STATUS = Integer.valueOf(4);

	

	/**
	 * @return the restContextPath
	 */
	public String getRestContextPath() {
		return mRestContextPath;
	}

	/**
	 * @param pRestContextPath the restContextPath to set
	 */
	public void setRestContextPath(String pRestContextPath) {
		mRestContextPath = pRestContextPath;
	}

	/**
	 * @return the enableRestCheckLogin
	 */
	public boolean isEnableRestCheckLogin() {
		return mEnableRestCheckLogin;
	}

	/**
	 * @param pEnableRestCheckLogin the enableRestCheckLogin to set
	 */
	public void setEnableRestCheckLogin(boolean pEnableRestCheckLogin) {
		mEnableRestCheckLogin = pEnableRestCheckLogin;
	}
	

	/**
	 * @return the mobileLoginRequiredURLs
	 */
	public Map<String, String> getMobileLoginRequiredURLs() {
		return mobileLoginRequiredURLs;
	}

	/**
	 * @param mobileLoginRequiredURLs the mobileLoginRequiredURLs to set
	 */
	public void setMobileLoginRequiredURLs(
			Map<String, String> mobileLoginRequiredURLs) {
		this.mobileLoginRequiredURLs = mobileLoginRequiredURLs;
	}

	/**
	 * @return the mobileAutoLoginRequiredURLs
	 */
	public Map<String, String> getMobileAutoLoginRequiredURLs() {
		return mobileAutoLoginRequiredURLs;
	}

	/**
	 * @param mobileAutoLoginRequiredURLs the mobileAutoLoginRequiredURLs to set
	 */
	public void setMobileAutoLoginRequiredURLs(
			Map<String, String> mobileAutoLoginRequiredURLs) {
		this.mobileAutoLoginRequiredURLs = mobileAutoLoginRequiredURLs;
	}

	/**
	 * OverRideded method from ATG OOTB BBBAccessControlerServlet and does
	 * authorization task according to the business rules i.e when the user not
	 * logged in and try to access the authorized page then it user will be
	 * return error.
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return void
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		String methodName = BBBCoreConstants.SERVICE;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ACCESS_CONTROLLER,	methodName);
		if (isLoggingDebug()) {
			logDebug("BBBRestAccessController.service() method started");
			logDebug("requestURI = " + pRequest.getRequestURI());
		}
		String contextPath = pRequest.getContextPath();
		String uri = pRequest.getRequestURI();
		String uriWithoutContextPath = null;
		String compPath = null;
		String [] array = uri.split(BBBCoreConstants.SLASH, 4);
		ComponentName profileComponentName = ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE);
		Profile profile = (Profile) pRequest.resolveName(profileComponentName);

		if (array != null && array.length > 3) {
			uriWithoutContextPath = BBBCoreConstants.SLASH + array[2] + BBBCoreConstants.SLASH + array[3];
			compPath = BBBCoreConstants.SLASH + array[3];
		}

		Integer currentSecurityCode = 0;
		currentSecurityCode=profileSecurityStatus(profile);
		if (contextPath.equalsIgnoreCase(mRestContextPath)) {
			
			// Profile Status 0, 1
			// Restrict if trying to access restricted URL
			if (profile == null || (profile != null && profile.isTransient())
					|| (profile != null && !profile.isTransient() && COOKIE_LOGIN_SECURITY_STATUS > currentSecurityCode)) {
				if (getMobileLoginRequiredURLs().containsKey(uriWithoutContextPath)
						|| getMobileAutoLoginRequiredURLs().containsKey(uriWithoutContextPath)) {
					logError("Client tried to access " + uriWithoutContextPath + " which requires login to access");
					pResponse.sendError(401, "UNAUTHORIZED ACCESS:Profile not logged in");
					throw new IOException("UNAUTHORIZED ACCESS:Profile not logged in " + compPath);

				}
			}

			// Profile Status 2, 3
			// Restrict if trying to access restricted URL
			else if (LOGGED_IN_USER_SECURITY_STATUS > currentSecurityCode && COOKIE_LOGIN_SECURITY_STATUS <= currentSecurityCode) {
				if (getMobileLoginRequiredURLs().containsKey(uriWithoutContextPath)) {
					logError("Client tried to access " + uriWithoutContextPath + " which requires login to access");
					pResponse.sendError(401, "UNAUTHORIZED ACCESS:Profile not logged in");
					throw new IOException("UNAUTHORIZED ACCESS:Profile not logged in " + compPath);
				}
				if (COOKIE_LOGIN_SECURITY_STATUS.equals(currentSecurityCode)) {
					pResponse.addCookie(new Cookie("AUTO_LOGIN_COOKIE", "true"));
				}
			}
		}

		if (isLoggingDebug()) {
			logDebug("BBBRestAccessController.service() method ends");
		}
		
		super.service(pRequest, pResponse);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ACCESS_CONTROLLER, methodName);
	}
	

	 /**
	 * Return The profile Security Status
	 * @param profile
	 * @return
	 */
	private static Integer profileSecurityStatus(final Profile profile) {
	        final Integer securityStatus = (Integer) profile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);

	        return securityStatus;

	    }

}
