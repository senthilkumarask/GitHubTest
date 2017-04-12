/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBStoreSessionDroplet.java
 *
 *  DESCRIPTION: BBBStoreSessionDroplet extends ATG OOTB DynamoServlet
 *  			 and helps storing the ipaddress, current time and token
 * 				 in the user's profile. After storing the info it will 
 * 				 append the token into hart&hanks url. 
 *  HISTORY:
 *  2/1/2012 Initial version
 *
 */
package com.bbb.account;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.Base64;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;
import com.bbb.framework.goldengate.DCPrefixIdGenerator;

public class BBBStoreSessionDroplet extends BBBDynamoServlet {

	private Profile mProfile;
	private BBBCatalogTools mCatalogTools;
	//BSL-4794 | Mobile App - Unable to link content to 3rd party
	private DCPrefixIdGenerator idGenerator;
	private static final String DC_VAL1="&dc=1";
	private static final String DC_VAL2="&dc=2";
	/**
	 * @return the idGenerator
	 */
	public DCPrefixIdGenerator getIdGenerator() {
		return idGenerator;
	}

	/**
	 * @param idGenerator the idGenerator to set
	 */
	public void setIdGenerator(DCPrefixIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return mProfile;
	}

	/**
	 * @param pProfile
	 *            the profile to set
	 */
	public void setProfile(Profile pProfile) {
		mProfile = pProfile;
	}
	
	

	/**
	 * This droplet process the info into the profile i.e storing the ipaddress,
	 * current time in the user's profile. After storing the info it will append
	 * the token into hart&hanks url.
	 * 
	 * @param pReq
	 * @param pRes
	 * @return void
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {
			logDebug("BBBStoreSessionDroplet.getResponseVO() method start");
		String profileId = (String) mProfile
				.getPropertyValue(BBBCoreConstants.ID);
		String currSite = pReq.getParameter(BBBCoreConstants.CURR_SITE);
		boolean status = false;
		if (profileId != null && currSite != null) {
			Map map = (Map) mProfile
					.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
			MutableRepositoryItem item = (MutableRepositoryItem) map
					.get(currSite);
			String token = Base64.encodeToString(pReq.getSession().getId());
			if (map != null && !map.isEmpty()) {
				MutableRepository repo = (MutableRepository) mProfile
						.getRepository();
				try {
					Calendar currentDate = Calendar.getInstance();
					item.setPropertyValue(BBBCoreConstants.TOKEN, token);
					item.setPropertyValue(BBBCoreConstants.IP_ADDRESS,
							pReq.getRemoteAddr());
					item.setPropertyValue(BBBCoreConstants.TIME_STAMP,
							currentDate.getTime());
					repo.updateItem(item);
					status = true;
				} catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(pReq, "RepositoryException from service method of BBBStoreSessionDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1180 ), e);
				}
			}
			if (status) {
				String hhURL = BBBCoreConstants.BLANK;
				List<String> keysList;
				try {
					keysList = getCatalogTools().getAllValuesForKey(
							BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.HARTE_AND_HANKS_URL);
				
						if (keysList != null && keysList.size() > BBBCoreConstants.ZERO) {
							hhURL = keysList.get(BBBCoreConstants.ZERO);
							hhURL = hhURL + BBBCoreConstants.TOKEN_STRING + token
									+ BBBCoreConstants.SITE_STRING + currSite;
							//BSL-4794 | Adding dc=1/dc=2 parameter on call to Harte Hanks
							if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){
								final String prefix=this.getIdGenerator().getDcPrefix();
								if(BBBCoreConstants.DATA_CENTER1.equalsIgnoreCase(prefix)){
									hhURL= hhURL + DC_VAL1;
								}
								else{
									hhURL= hhURL + DC_VAL2;
								}
							}
							
								logDebug("URL = " + hhURL);
							
						} else {
							logError(LogMessageFormatter.formatMessage(pReq, "Cannot Find ConfigKey for " + BBBCoreConstants.HARTE_AND_HANKS_URL, BBBCoreErrorConstants.ACCOUNT_ERROR_1181 ));
						}
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(pReq, "BBBSystemException from service method of BBBStoreSessionDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1182 ),e);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pReq, "BBBBusinessException from service method of BBBStoreSessionDroplet", BBBCoreErrorConstants.ACCOUNT_ERROR_1183 ),e);
				}
				pReq.setParameter(BBBCoreConstants.URL, hhURL);
				pReq.setParameter(BBBCoreConstants.ATG_TOKEN_HARTE_HANKS, token);
				
				pReq.serviceLocalParameter(BBBCoreConstants.SUCCESS, pReq, pRes);
			} else {
				pReq.serviceLocalParameter(BBBCoreConstants.FAIL, pReq, pRes);
			}
		}
			logDebug("BBBStoreSessionDroplet.getResponseVO() method start");
	}
	
	
	
	
	
	/**
	 * get Preferences for the rest API
	 * @param request
	 * @param response
	 * @return hart n hanks url
	 * @throws BBBSystemException
	 */

	public String getPreferencesAPI() throws BBBSystemException
	{
		logDebug("Starting method getHartNHanksURLAPI for rest module");
		String hartnHanksURL = null;
		try{
			DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		    
			String siteId = SiteContextManager.getCurrentSiteId();
			request.setParameter(BBBCoreConstants.CURR_SITE, siteId);
		   
		    service(request, response);

		    hartnHanksURL = (String) request.getObjectParameter(BBBCoreConstants.URL);
		  
		    logDebug("End method getHartNHanksURLAPI for rest module");
		}
		catch (IOException e) {
			 throw new BBBSystemException(BBBCatalogErrorCodes.GET_HartNHANKS_URL_IO_EXCEPTION, "IO Exception While fetching hart n hanks url",e);

		} catch (ServletException e) {
			 throw new BBBSystemException(BBBCatalogErrorCodes.GET_HartNHANKS_URL_SERVLET_EXCEPTION, "Servlet Exception While fetching hart n hanks url",e);
		}
			
		return hartnHanksURL;
	}
		
}
