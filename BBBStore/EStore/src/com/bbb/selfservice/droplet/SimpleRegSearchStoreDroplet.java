package com.bbb.selfservice.droplet;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 03-November-2011
//
//Modified by: Seema Singhal - Added code for multiple address suggestions
//
//Modified on: 19-January-2011
//--------------------------------------------------------------------------------

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

public class SimpleRegSearchStoreDroplet extends BBBDynamoServlet {
	/**
	 * 
	 */
	private SearchStoreManager mSearchStoreManager;
	private String mRadius;
	private BBBCatalogTools mCatalogTools;
	private static final String RADIUS_CANADA = "100";
	private static final String UNITS_CANADA = "k";
	private String mUnits;

	/** The Constant CANADA_SITE. */
	public static final String CANADA_SITE = "BedBathCanada";

	/**
	 * This method checks the input parameter type and call the searchStore
	 * methods based on input parameter. Input parameter can be SiteId, PageKey
	 * & Page Number if these parameter not passed the it will take inputs from
	 * cookies files if exists Output parameter will be output & empty, if
	 * search result not return any records then it will EMPTY otherwise OUTPUT
	 * will be there.
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		final String logMessage = getClass().getName();
		logDebug(new StringBuilder(logMessage).append(" service || Begins").toString());
		String searchString = null;
		String searchType = null;
		StringBuffer strTempSearch = null;
		String storeType = null;
		String siteId = this.getCurrentSiteId();
		try {
			storeType = getSearchStoreManager().getStoreType(siteId);
		} catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(req,	"err_store_search_tech_error",BBBCoreErrorConstants.ACCOUNT_ERROR_1206),bbbSystemException);
			req.setParameter(BBBCoreConstants.ERROR_MESSAGES,bbbSystemException.getErrorCode()+ bbbSystemException.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
			return;
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(req,"err_store_search_tech_error",BBBCoreErrorConstants.ACCOUNT_ERROR_1207),bbbBusinessException);
			req.setParameter(BBBCoreConstants.ERROR_MESSAGES,bbbBusinessException.getErrorCode()+ bbbBusinessException.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
			return;
		}
		strTempSearch = new StringBuffer();
		searchString = req.getParameter(BBBCoreConstants.SEARCHSTRING);
		String originalSearchString = searchString;
		searchType = req.getParameter(BBBCoreConstants.SEARCHTYPE);
		if (BBBUtility.isEmpty(searchType)) {
			searchType = (String) req.getSession().getAttribute(BBBCoreConstants.TYPE);
		}
		String radiusParam = req.getParameter(BBBCoreConstants.RADIUS);
		if (BBBUtility.isNotEmpty(radiusParam)) {
			setRadius(radiusParam);
		}
		if (siteId.equalsIgnoreCase(CANADA_SITE)) {
			String configValue = null;
			try {
				List<String> keysList = getCatalogTools().getAllValuesForKey("MapQuestStoreType", "radius_default_ca");
				if (!BBBUtility.isListEmpty(keysList)) {
					configValue = keysList.get(0);
				}
				if (!BBBUtility.isEmpty(configValue)) {
					setRadius(configValue);
				} else {
					setRadius(RADIUS_CANADA);
				}
			} catch (BBBSystemException e) {
				logError("BBBSystemException occured");
			} catch (BBBBusinessException e) {
				logError("BBBBusinessException occured");
			}
			mUnits = UNITS_CANADA;
		}
		strTempSearch.append(BBBCoreConstants.STORE_TYPE + storeType);
		strTempSearch.append(BBBCoreConstants.AMPERSAND).append(SelfServiceConstants.MAPQUEST_COLUMN_LIST);

		if (siteId.equalsIgnoreCase(CANADA_SITE)) {
			strTempSearch.append(BBBCoreConstants.ORIGINPARAM).append(searchString).append(BBBCoreConstants.RADIUSPARAM).append(getRadius()).append(BBBCoreConstants.UNITSPARAM)
					.append(mUnits);
			searchString = strTempSearch.toString();
		} else {
			strTempSearch.append(BBBCoreConstants.ORIGINPARAM).append(searchString).append(BBBCoreConstants.RADIUSPARAM).append(getRadius());
			searchString = strTempSearch.toString();
		}
		req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES,	getRadius());
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		try {
			String storeId = (String) req.getObjectParameter(BBBCoreConstants.STOREID);
			if (!BBBUtility.isEmpty(storeId)) {
				logDebug(new StringBuilder(logMessage).append(" Search based on StoreId...").append(storeId).toString());
				StoreDetails objStoreDetails = null;
				objStoreDetails = getSearchStoreManager().searchStoreById(storeId, siteId, storeType);
				if (null != objStoreDetails) {
					// Commenting the code to get Contact Flag from CatalogTools
					// and returning default value as false as of now till
					// requirement is closed.
					try {
						StoreVO storeVO = getCatalogTools().getStoreDetails(
								storeId);
						if (storeVO.isContactFlag()) {
							objStoreDetails.setContactFlag(storeVO
									.isContactFlag());
						} else {
							objStoreDetails.setContactFlag(false);
						}
					} catch (BBBBusinessException e) {
						objStoreDetails.setContactFlag(false);
					}

					req.setParameter(BBBCoreConstants.STOREDETAILS,
							objStoreDetails);
					req.serviceLocalParameter(BBBCoreConstants.OUTPUT, req, res);
				} else {
					req.serviceLocalParameter(BBBCoreConstants.EMPTY, req, res);
				}
			} else {
				if (!BBBUtility.isEmpty(searchType)	&& !BBBUtility.isEmpty(searchString)) {
					String pageSize = req.getParameter(BBBCoreConstants.PAGESIZE);
					if (searchType.equals(SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH)) {
						logDebug(new StringBuilder(logMessage).append(" Store Search based on zipcode. . ").append(originalSearchString).toString());
						req.getSession().setAttribute(BBBCoreConstants.STORE_TYPE, storeType);
						req.getSession().setAttribute(BBBCoreConstants.STATUS,"location=".concat(originalSearchString));
						req.getSession().setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE);
						objStoreDetailsWrapper = getSearchStoreManager().searchStoreByAddress("location=".concat(originalSearchString),searchType, pageSize);
						req.getSession().setAttribute(BBBCoreConstants.STORE_TYPE, "");
						req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, "");
						req.getSession().setAttribute(BBBCoreConstants.STATUS,"");
					} else {
						objStoreDetailsWrapper = getSearchStoreManager().searchStoreByAddress((String) req.getSession().getAttribute(BBBCoreConstants.STATUS),searchType, pageSize);
					}
					if (null != objStoreDetailsWrapper) {
						if (objStoreDetailsWrapper.getStoreDetails() != null && !objStoreDetailsWrapper.getStoreDetails().isEmpty()) {
							req.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER,objStoreDetailsWrapper);
								req.setParameter(BBBCoreConstants.STOREDETAILS_FOR_REGISTLRY,
										objStoreDetailsWrapper
												.getStoreDetails().get(0));
							req.serviceLocalParameter(BBBCoreConstants.OUTPUT,
									req, res);
						}
						// If Store details Wrapper has populated Address
						// suggestion VO it means no success and need to display
						// list of multiple suggested addresses.
						else if (null != objStoreDetailsWrapper
								.getStoreAddressSuggestion()) {
							req.setParameter(
									BBBCoreConstants.STOREDETAILSWRAPPER,
									objStoreDetailsWrapper);
							req.serviceLocalParameter(
									BBBCoreConstants.ADDRESSSUGGESTION, req,
									res);
						} else {
							req.serviceLocalParameter(BBBCoreConstants.EMPTY,
									req, res);
						}
					} else {
						req.serviceLocalParameter(BBBCoreConstants.EMPTY, req,
								res);
					}
					// No search String exists in cookies-- Means no previous
					// search
				}
			}
		} catch (BBBBusinessException e) {
			logError(
					LogMessageFormatter.formatMessage(
							req,
							"BBBBusinessException in SimpleRegSearchStoreDroplet while Store Search based on input address",
							BBBCoreErrorConstants.ACCOUNT_ERROR_1208), e);
			req.setParameter(BBBCoreConstants.ERRORMESSAGE, e.getErrorCode()
					+ e.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
		} catch (BBBSystemException e) {
			logError(
					LogMessageFormatter.formatMessage(
							req,
							"BBBSystemException in SimpleRegSearchStoreDroplet while Store Search based on input address",
							BBBCoreErrorConstants.ACCOUNT_ERROR_1209), e);
			req.setParameter(BBBCoreConstants.ERRORMESSAGE, e.getErrorCode()
					+ e.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
		}

	}

	/**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSite().getId();
	}

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

	/**
	 * @return the radius
	 */
	public String getRadius() {
		return mRadius;
	}

	/**
	 * @param pRadius
	 *            the radius to set
	 */
	public void setRadius(String pRadius) {
		mRadius = pRadius;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

}
