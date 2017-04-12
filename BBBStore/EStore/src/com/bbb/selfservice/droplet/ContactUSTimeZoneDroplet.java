/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 22-November-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.manager.ContactUsManager;

import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;

/**
 * The class is the extension of the ATG DynamoServlet. The class is responsible
 * for rendering the content for subject drop down in contact_us.jsp.
 * 
 */

public class ContactUSTimeZoneDroplet extends BBBDynamoServlet {

	private ContactUsManager mContactUsManager;
	private SiteContext mSiteContext;

	/**
	 * @return the contactUsManager
	 */
	public ContactUsManager getContactUsManager() {
		return mContactUsManager;
	}

	/**
	 * @param pContactUsManager
	 *            the contactUsManager to set
	 */
	public void setContactUsManager(ContactUsManager pContactUsManager) {
		mContactUsManager = pContactUsManager;
	}

	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param pSiteContext the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
	}

	/**
	 * Fetch TimeZone for the dropdown to select a TimeZone.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("ContactUSTimeZoneDroplet.service() method started");

		final String pSiteId = getSiteContext().getSite().getId();
		
		try {

			List<String> timeZoneTypes = new ArrayList<String>();
			
			timeZoneTypes = getContactUsManager().getTimeZones(pSiteId);
			
			pRequest.setParameter("timeZoneTypes", timeZoneTypes);
				logDebug("set output to the display page");

			pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBSystemException systemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException in ContactUSTimeZoneDroplet.service()", BBBCoreErrorConstants.ACCOUNT_ERROR_1188 ), systemException);
		} catch (BBBBusinessException businessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in ContactUSTimeZoneDroplet.service()", BBBCoreErrorConstants.ACCOUNT_ERROR_1189 ), businessException);
		} 

		logDebug("ContactUSTimeZoneDroplet.service() method ends");
	}

}
