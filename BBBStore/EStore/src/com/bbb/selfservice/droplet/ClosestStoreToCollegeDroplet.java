package com.bbb.selfservice.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

public class ClosestStoreToCollegeDroplet extends BBBDynamoServlet {

	private SearchStoreManager mSearchStoreManager;
	
	
	/**
	 * @return the searchStoreManager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}

	/**
	 * @param pSearchStoreManager
	 *            the searchStoreManager to set
	 */
	public void setSearchStoreManager(SearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}
	
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		final String logMessage = getClass().getName() + "service || ";

		logDebug(logMessage + " Inside Service Method...");

		logDebug(logMessage + " Starts here.");
		String searchString = null;
		String searchType = null;
		String storeType = null;
		String siteId = null;
		String weekday=null;
		String[] weekdayStoreArr=null;
		String radius = req.getParameter(BBBCoreConstants.RADIUS);
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		StringBuilder strBuild = new StringBuilder();
		
		searchString = req.getParameter(BBBCoreConstants.SEARCHSTRING);
		searchType = req.getParameter(BBBCoreConstants.SEARCHTYPE);
		siteId = req.getParameter(BBBCoreConstants.SITE_ID);
		
		req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius);
		strBuild.append(SelfServiceConstants.LOCATION_EQUALS);
		strBuild.append(searchString);
		if (!BBBUtility.isEmpty(searchType)
				&& !BBBUtility.isEmpty(searchString)) {
			
			logDebug(logMessage + " || "
					+ " Store Search based on input address. . "
					+ searchString + searchType);
		
		try {
			storeType = getSearchStoreManager().getStoreType(siteId);
			req.getSession().setAttribute(BBBCoreConstants.STORE_TYPE, storeType);
			req.getSession().setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
			objStoreDetailsWrapper = getSearchStoreManager()
					.searchStoreByAddress(strBuild.toString(), searchType,
							null);
			if (null != objStoreDetailsWrapper) {
				if (objStoreDetailsWrapper.getStoreDetails() != null
						&& !objStoreDetailsWrapper.getStoreDetails()
									.isEmpty()) {

						req.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,
								objStoreDetailsWrapper.getStoreDetails().get(0));

						req.serviceLocalParameter(BBBCoreConstants.OUTPUT, req,
								res);
					}  else {
					req.serviceLocalParameter(BBBCoreConstants.EMPTY, req,
							res);
				}
			}
				
		} catch (BBBBusinessException e) {
			logError(
					LogMessageFormatter.formatMessage(
							req,
							"BBBBusinessException in ClosestStoreToCollegeDroplet while Store Search based on input address",
							BBBCoreErrorConstants.ACCOUNT_ERROR_1208), e);
			req.setParameter(BBBCoreConstants.ERRORMESSAGE, e.getErrorCode()
					+ e.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
		} catch (BBBSystemException e) {
			logError(
					LogMessageFormatter.formatMessage(
							req,
							"BBBSystemException in ClosestStoreToCollegeDroplet while Store Search based on input address",
							BBBCoreErrorConstants.ACCOUNT_ERROR_1209), e);
			req.setParameter(BBBCoreConstants.ERRORMESSAGE, e.getErrorCode()
					+ e.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
		}
	  }
	}
	
	/*
     * Parse the store timings
     */
     private String storeTimings(String storeTimings) {

            String storeTime = storeTimings;
            storeTime = storeTime.replace("Mon", "M");
            storeTime = storeTime.replace("Tue", "T");
            storeTime = storeTime.replace("Wed", "W");
            storeTime = storeTime.replace("Thu", "T");
            storeTime = storeTime.replace("Fri", "F");
            

            return storeTime;
     }
}
