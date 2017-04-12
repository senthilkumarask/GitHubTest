package com.bbb.affiliates.tags;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import atg.multisite.SiteContextManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class MOM365TagUtils{
	enum MOM365 {
		BUYBUYBABY("MomTag_baby"),
		BEDBATHUS("MomTag_us"), 
		BEDBATHCANADA("MomTag_ca");

		private String key;

		private MOM365(String c) {
			key = c;
		}

		public String siteKey() {
			return key;
		}
	}

	private static final String MOM365_KEYS = "MOM365Keys";
	private static final String REFERER_SEPERATOR = ",";
	private static final String CONTENT_CATALOG_KEYS = "ContentCatalogKeys";
	private static final String PATH = "/";
	private static final ApplicationLogging MLOGGING =
			    ClassLoggingFactory.getFactory().getLoggerForClass(MOM365TagUtils.class);
	
	public  static void checkAndSetMom365Cookie(final DynamoHttpServletRequest req, final DynamoHttpServletResponse resp){

		final Map<String, String> mom365Keys = BBBConfigRepoUtils.getConfigMap(MOM365_KEYS);
		String validReferer = null;
		final String referer = req.getHeader(BBBCoreConstants.REFERRER);
		if (referer != null && mom365Keys != null) {
			validReferer = isValidReferrer(referer,req.getRequestURL().toString() ,mom365Keys.keySet());
		}
		if(MLOGGING.isLoggingDebug()){
			MLOGGING.logDebug("referrer is " + referer);
			MLOGGING.logDebug("mom365 valid referrer is " + validReferer);	
		}
		if (validReferer != null) {
			final List<String> cookieStr = BBBConfigRepoUtils.getAllValues(MOM365_KEYS, validReferer);
			if(MLOGGING.isLoggingDebug()){
				MLOGGING.logDebug("cookie to be created is:" + cookieStr);
			}
			final Cookie cookie = new Cookie(cookieStr.get(0), cookieStr.get(1));
			cookie.setDomain(req.getServerName());
			cookie.setPath(PATH);
			//resp.addCookie(cookie);
			BBBUtility.addCookie(resp, cookie, true);
			if(MLOGGING.isLoggingDebug()){
				MLOGGING.logDebug("cookie added successfully");
			}
		}
	}
	
	private static String isValidReferrer(final String referrer, final String destUrl, final Set<String> affiliates){
		if(affiliates !=null && !affiliates.isEmpty()){
			for(String affiliate: affiliates){
				String[] key = affiliate.split(BBBCatalogConstants.DELIMITER);
					if(key!=null && key.length==2){
					String[] referrerKeys= key[0].split(REFERER_SEPERATOR);
					String destUrlKey= key[1];
					if(referrerKeys!=null && referrerKeys.length>0){
						for(String referrerKey:referrerKeys){
							if(referrer.toLowerCase().contains(referrerKey.toLowerCase())&&
								destUrl.toLowerCase().contains(destUrlKey.toLowerCase())){
								return affiliate;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static boolean isMom365Enabled(){
		boolean isMom365Enabled = false;
		String siteId = SiteContextManager.getCurrentSiteId();
		String value = null;
		if(BBBCoreConstants.SITE_BAB_US.equals(siteId)){
			value = BBBConfigRepoUtils.getStringValue(CONTENT_CATALOG_KEYS, MOM365.BEDBATHUS.siteKey());
		}else if(BBBCoreConstants.SITE_BBB.equals(siteId)){
			value =  BBBConfigRepoUtils.getStringValue(CONTENT_CATALOG_KEYS, MOM365.BUYBUYBABY.siteKey());
		}else {
			value =  BBBConfigRepoUtils.getStringValue(CONTENT_CATALOG_KEYS, MOM365.BEDBATHCANADA.siteKey());
		}
		if(value != null && !value.isEmpty()){
			isMom365Enabled = Boolean.parseBoolean(value);
		}
		if(MLOGGING.isLoggingDebug()){
			MLOGGING.logDebug("mom365 status for site " + siteId + " is " +isMom365Enabled);	
		}

		return isMom365Enabled;
	}
}
