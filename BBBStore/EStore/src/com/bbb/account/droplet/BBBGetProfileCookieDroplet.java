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

import java.util.ArrayList;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.logging.LogMessageFormatter;



import static com.bbb.constants.BBBAccountConstants.*;

public class BBBGetProfileCookieDroplet extends BBBDynamoServlet {
	
	private static final String SITE_ID = "s";
	private static final String PROFILE_ID = "p";
	
	private String profileCookieName;
	private int profileCookieAge;
	private String profileCookiePath;
	
	/**
	 * @return the profileCookieName
	 */
	public String getProfileCookieName() {
		return profileCookieName;
	}

	/**
	 * @param profileCookieName the profileCookieName to set
	 */
	public void setProfileCookieName(String profileCookieName) {
		this.profileCookieName = profileCookieName;
	}

	/**
	 * @return the profileCookieAge
	 */
	public int getProfileCookieAge() {
		return profileCookieAge;
	}

	/**
	 * @param profileCookieAge the profileCookieAge to set
	 */
	public void setProfileCookieAge(int profileCookieAge) {
		this.profileCookieAge = profileCookieAge;
	}

	/**
	 * @return the profileCookiePath
	 */
	public String getProfileCookiePath() {
		return profileCookiePath;
	}

	/**
	 * @param profileCookiePath the profileCookiePath to set
	 */
	public void setProfileCookiePath(String profileCookiePath) {
		this.profileCookiePath = profileCookiePath;
	}

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		final Cookie[] allCookies = pRequest.getCookies();
		if (allCookies != null) {
			for (int i = 0; i < allCookies.length; i++) {
				if (allCookies[i].getName().equals("IDP"))
				{
					JSONObject jsonObject = null;
					try{
						jsonObject = (JSONObject) JSONSerializer.toJSON(allCookies[i].getValue());
					}catch (JSONException e) {
						
	    				logError(LogMessageFormatter.formatMessage(pRequest, "JSESSION ID: " + pRequest.getSession().getId()+ ", Exception while creating the JSON from cookie, cookie value: " + allCookies[i].getValue(), "profileID_error"),e);
	    				
					}
					if(jsonObject != null){
						DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
						List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);					
						
						String siteId = getSiteIdFromJsonObject(JSONResultbean, dynaBeanProperties);
						String profileId = null;
						if(StringUtils.isNotBlank(siteId) && StringUtils.equals(siteId, SiteContextManager.getCurrentSiteId())){
							if(getProfileIdFromJsonObject(JSONResultbean, dynaBeanProperties) != null){
								profileId = getProfileIdFromJsonObject(JSONResultbean, dynaBeanProperties);
							}
						}
						
						if(null != profileId){
							pRequest.setParameter("profileId", profileId);
							pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
						}
					} else {
						pRequest.serviceParameter(OPARAM_ERROR, pRequest,
								pResponse);
					}				
					
				} 
			}
		} else {
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}
	}
	
	/**
	 * Get Site id from json object
	 * @param JSONResultbean
	 * @param dynaBeanProperties
	 */
	private String getSiteIdFromJsonObject(DynaBean JSONResultbean, List<String> dynaBeanProperties) {
		String siteId = null;
		if (dynaBeanProperties.contains(SITE_ID) &&
				JSONResultbean.get(SITE_ID) != null) {
			siteId = JSONResultbean.get(SITE_ID).toString();
		}
		return siteId;
	}
	
	/*
	 * To get the properties names from JSON result string
	 */
	private List<String> getPropertyNames(DynaBean pDynaBean) {

		DynaClass dynaClass = pDynaBean.getDynaClass();
		DynaProperty properties[] = dynaClass.getDynaProperties();
		List<String> propertyNames = new ArrayList<String>();
		for (int i = 0; i < properties.length; i++) {
			String name = properties[i].getName();
			propertyNames.add(name);
		}
		return propertyNames;
	}
	
	/**
	 * @param JSONResultbean
	 * @param dynaBeanProperties
	 */
	private String getProfileIdFromJsonObject(DynaBean JSONResultbean, List<String> dynaBeanProperties) {
		String profileId = null;
		if (dynaBeanProperties.contains(PROFILE_ID) &&
				JSONResultbean.get(PROFILE_ID) != null) {
			profileId = JSONResultbean.get(PROFILE_ID).toString();
		}
		return profileId;
	}
}