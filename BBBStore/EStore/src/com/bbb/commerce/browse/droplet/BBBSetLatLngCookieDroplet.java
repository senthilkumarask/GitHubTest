/*
 *  Copyright 2016, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBSetLatLngCookieDroplet.java
 *
 *  DESCRIPTION: BBBSetLatLngCookieDroplet sets the cookie on when Brands/Category/Search jsp page is called.
 *  			 This droplet should come just after <dsp:page> tag.
 *  			 
 *  HISTORY:
 *  07/22/2016 Initial version
 *
 * @author mrughani
 *
 */
package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class BBBSetLatLngCookieDroplet extends BBBDynamoServlet {
	
	private BBBCatalogTools catalogTools;
	private SearchStoreManager searchStoreManager;
	
	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		String path= pRequest.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH);		 
		String domain= pRequest.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN);
		String storeIdFromURL = pRequest.getParameter("storeIdFromURL");
		String siteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
		if(BBBUtility.isEmpty(domain)){
			domain=pRequest.getServerName();
		}
		if(BBBUtility.isEmpty(path)){
			path="/";
		}
		if(isLoggingDebug()){
			logDebug("path to set:" + path);
			logDebug("domain to set:" + domain);
		}
			
		try{
			String latLng = getSearchStoreManager().modifyLatLngCookie(storeIdFromURL, siteId);
			if(isLoggingDebug()){
				logDebug("Latitude and Longitude value from DB using Store and Site ID : " + latLng);
			}

			//Set lat/lng in the cookie if storeID parameter is found in the URL
			//storeID can be inserted in the URL by selecting a store on PLP OR by directing to PLP from any place where URL is hosted already with storeID
			if(latLng != null){
				if ( BBBUtility.getCookie(pRequest, SelfServiceConstants.LAT_LNG_COOKIE) != null) {
					Cookie[] requestCookies = pRequest.getCookies();
						for (Cookie cookie : requestCookies) {
							if (cookie.getName().equals(SelfServiceConstants.LAT_LNG_COOKIE)) {
								if(cookie.getValue() != latLng.toString()){

									if(isLoggingDebug()){
										logDebug("BBBSetLatLngCookieDroplet.service ===> Start: remove existingLatLngCookie method ");
									}
									final Cookie existingLatLngCookie = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, "");
									existingLatLngCookie.setMaxAge(0);
									existingLatLngCookie.setPath(BBBCoreConstants.SLASH);
									BBBUtility.addCookie(pResponse, existingLatLngCookie, true);
									if(isLoggingDebug()){
										logDebug("BBBSetLatLngCookieDroplet.service ===> End: remove existingLatLngCookie" + ": cookie removed");
									}

									if(isLoggingDebug()){
										logDebug("BBBSetLatLngCookieDroplet.service ===> Start: add newLatLngCookie method ");
									}
									Cookie latLngCookie = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE,latLng.toString());
									latLngCookie.setMaxAge(getCookieTimeOut());
									latLngCookie.setDomain(pRequest.getServerName());
									latLngCookie.setPath(BBBCoreConstants.SLASH);
									BBBUtility.addCookie(pResponse, latLngCookie, false);
									if(isLoggingDebug()){
										logDebug("BBBSetLatLngCookieDroplet.service ===> End: add newLatLngCookie" + ": cookie added");
									}

								}
							}
					}
				}else  {
					if(isLoggingDebug()){
						logDebug("BBBSetLatLngCookieDroplet.service ===> Start: add newLatLngCookie method ");
					}
					Cookie latLngCookie = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE,latLng.toString());
					latLngCookie.setMaxAge(getCookieTimeOut());
					latLngCookie.setDomain(pRequest.getServerName());
					latLngCookie.setPath(BBBCoreConstants.SLASH);
					BBBUtility.addCookie(pResponse, latLngCookie, false);
					if(isLoggingDebug()){
						logDebug("BBBSetLatLngCookieDroplet.service ===> End: add newLatLngCookie" + ": cookie added");
					}
				}
			}

		pRequest.setParameter("latLngFromPLP",latLng);
		}catch (Exception e) {
			logError(
					"Exception occurred while setting lat/lng in the Cookie ",e);
		}
		pRequest.serviceLocalParameter(BBBAccountConstants.OPARAM_OUTPUT, pRequest, pResponse);
	}
	
	/**
	 * This method takes max age of cookie from config keys
	 * 
	 * @return int
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	 public int getCookieTimeOut() {
		int configValue = 0;
		try
		{
			List<String> keysList = getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG);
			//if (keysList != null && keysList.size() > 0)
			if (!BBBUtility.isListEmpty(keysList))	
			{
				configValue = Integer.parseInt(keysList.get(0));
			}
		}
		catch (BBBSystemException e1)
		{
			logError("BBBSystemException | Error occurred while getting cookie time out value for lat/lng");
		} catch (BBBBusinessException e1)
		{
			logError("BBBBusinessException | Error occurred while getting cookie time out value for lat/lng");
		}
		return configValue;
	 }
	
	
}