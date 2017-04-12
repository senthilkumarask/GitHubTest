package com.bbb.userprofiling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.CookieManager;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;



/**
 * The Class BBBCookieManager
 * Description - This class has been added for story BBBP-5384 in which we need to create cookies for profile
 */
public class BBBCookieManager extends CookieManager {

	/**
	 * Create cookies for profile.
	 *
	 * @param profileId the profile id
	 * @param pRequest the request
	 * @param pResponse the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void createCookiesForProfile(String profileId, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		if (isLoggingDebug()){
			logDebug("createCookiesForProfile: Start ");
		}
		String siteId = SiteContextManager.getCurrentSiteId();
		if((StringUtils.isEmpty(profileId)) || (!siteId.isEmpty() && (TBSConstants.SITE_TBS_BAB_US.equals(siteId) || TBSConstants.SITE_TBS_BBB.equals(siteId) || TBSConstants.SITE_TBS_BAB_CA.equals(siteId)))){
			if (isLoggingDebug()){
				logDebug("createCookiesForProfile: Not creating cookies "+"siteId:"+siteId+"profile:"+profileId);
			}
			return;
		}
		//get dyn user cookie value
		String dynUserCookieValue = extractProfileId(pRequest, pResponse);

		if (StringUtils.isNotEmpty(dynUserCookieValue)) {
			//if cookie found delete existing cookies 
			Cookie[] cookies = pRequest.getCookies();
			for(Cookie cookie : cookies){
				if(StringUtils.isNotEmpty(cookie.getName()) && (cookie.getName().equals(BBBCoreConstants.DYN_USER_ID) || (cookie.getName().equals(BBBCoreConstants.DYN_USER_CONFIRM)))){
					cookie.setMaxAge(0);
					pResponse.addCookie(cookie);
				}	
			}
		} 

		//create cookie for profile
		Cookie userCookie = generateCookieForProfile(profileId);
		if (userCookie != null) {
			pResponse.addCookie(userCookie);
			if (isLoggingDebug()){
				logDebug("createCookiesForProfile: Creating profile cookies for id " + profileId);
			}
		}
		
		if (isLoggingDebug()){
			logDebug("createCookiesForProfile: End ");
		}
	}

	/**
	 * Generate profile id cookie for profile.
	 *
	 * @param profileId the profile id
	 * @return the cookie
	 */
	Cookie generateCookieForProfile(String profileId)
	{
		Cookie cookie = ServletUtil.createCookie(BBBCoreConstants.DYN_USER_ID, profileId);
		initializeCookieForProfile(cookie);
		return cookie;
	}

	/**
	 * Initialize profile cookie for profile.
	 *
	 * @param pCookie the cookie
	 */
	void initializeCookieForProfile(Cookie pCookie)
	{
		if (isLoggingDebug()){
			logDebug("initializeCookieForProfile: setting values in cookie");
		}
		String comment = getProfileCookieComment();
		String domain = getProfileCookieDomain();
		int maxAge = getProfileCookieMaxAge();
		String path = getProfileCookiePath();
		boolean secure = isProfileCookieSecure();
		if (comment != null)
			pCookie.setComment(comment);
		if (domain != null)
			pCookie.setDomain(domain);
		if (maxAge >= 0)
			pCookie.setMaxAge(maxAge);
		if (path != null)
			pCookie.setPath(path);
		pCookie.setSecure(secure);
		
		if (isLoggingDebug()){
			logDebug("Cookie properties: comment"+comment+"domain"+domain+"maxAge"+maxAge+"path"+path+"secure"+secure);
		}
	}

}

