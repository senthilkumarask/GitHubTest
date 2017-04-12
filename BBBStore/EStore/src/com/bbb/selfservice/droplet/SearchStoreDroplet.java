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

import atg.multisite.SiteContext;
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

/**
 * @author Jaswinder Sidhu
 * 
 */
public class SearchStoreDroplet extends BBBDynamoServlet {
	/**
                * 
                 */
	private SearchStoreManager mSearchStoreManager;

	private String mMapQuestTable;
	private String mStaticMapSize;
	private String mStaticMapZoom;
	private String mRadius;
	private BBBCatalogTools mCatalogTools;
	private SiteContext mSiteContext;
	private static final String SUGGESTION = "suggestion";
	private static final String ADDRESS = "address";
	private static final String CITY = "city";
	private static final String STATE_CODE = "stateCode";
	private static final String RADIUS_CANADA = "100";
	private static final String UNITS_CANADA = "k";
	private String mUnits;

	/** The Constant CANADA_SITE. */
	public static final String CANADA_SITE = "BedBathCanada";

	/**
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return mSiteContext;
	}

	/**
	 * @param pSiteContext
	 *            the siteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		mSiteContext = pSiteContext;
	}

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
		final String logMessage = getClass().getName() + "service || ";

		logDebug(logMessage + " Inside Service Method...");

		logDebug(logMessage + " Starts here.");
		String searchString = null;
		String searchType = null;
		StringBuffer strTempSearch = null;
		StringBuffer strSearchBasedOn = null;
		String storeType = null;

		try {
			storeType = getSearchStoreManager().getStoreType(
					getSiteContext().getSite().getId());
		} catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(req,
					"err_store_search_tech_error",
					BBBCoreErrorConstants.ACCOUNT_ERROR_1206),
					bbbSystemException);
			req.setParameter(
					BBBCoreConstants.ERROR_MESSAGES,
					bbbSystemException.getErrorCode()
							+ bbbSystemException.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
			return;
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(req,
					"err_store_search_tech_error",
					BBBCoreErrorConstants.ACCOUNT_ERROR_1207),
					bbbBusinessException);
			req.setParameter(
					BBBCoreConstants.ERROR_MESSAGES,
					bbbBusinessException.getErrorCode()
							+ bbbBusinessException.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
			return;
		}

		if ((!BBBUtility.isEmpty(req
				.getParameter(BBBCoreConstants.SEARCHBASEDON)))
				&& (req.getParameter(BBBCoreConstants.SEARCHBASEDON))
						.equalsIgnoreCase("cookie")) {
			searchString = (String) req.getSession().getAttribute(
					BBBCoreConstants.STATUS);
			searchType = (String) req.getSession().getAttribute(
					BBBCoreConstants.TYPE);
			strSearchBasedOn = new StringBuffer();
			String siteId = this.getSiteContext().getSite().getId();
			if (siteId.equalsIgnoreCase(CANADA_SITE)) {
				strSearchBasedOn = new StringBuffer();
				mUnits = UNITS_CANADA;
				strSearchBasedOn.append(searchString)
						.append(BBBCoreConstants.UNITSPARAM).append(mUnits);
				searchString = strSearchBasedOn.toString();
			}
		} else {
			strTempSearch = new StringBuffer();
			searchString = req.getParameter(BBBCoreConstants.SEARCHSTRING);
			searchType = req.getParameter(BBBCoreConstants.SEARCHTYPE);
			if (BBBUtility.isEmpty(searchType)) {
				searchType = (String) req.getSession().getAttribute(
						BBBCoreConstants.TYPE);
			}
			String radiusParam = req.getParameter(BBBCoreConstants.RADIUS);
			if (BBBUtility.isNotEmpty(radiusParam)) {
				mRadius = radiusParam;
			}
			String siteId = this.getSiteContext().getSite().getId();
			if (siteId.equalsIgnoreCase(CANADA_SITE)) {

				String configValue = null;
				try {
					List<String> keysList = getCatalogTools()
							.getAllValuesForKey("MapQuestStoreType",
									"radius_default_ca");
					if (!BBBUtility.isListEmpty(keysList)) {
						configValue = keysList.get(0);
					}
					if (!BBBUtility.isEmpty(configValue)) {
						mRadius = configValue;
					} else {
						mRadius = RADIUS_CANADA;
					}
				} catch (BBBSystemException e) {
					logError("BBBSystemException occured");
				} catch (BBBBusinessException e) {
					logError("BBBBusinessException occured");
				}
				// mRadius = RADIUS_CANADA;
				mUnits = UNITS_CANADA;
			}
			strTempSearch.append(BBBCoreConstants.STORE_TYPE + storeType);
			strTempSearch.append(BBBCoreConstants.AMPERSAND).append(
					SelfServiceConstants.MAPQUEST_COLUMN_LIST);

			String isSuggestion = req.getParameter(SUGGESTION);
			if (BBBCoreConstants.TRUE.equalsIgnoreCase(isSuggestion)) {
				String address = req.getParameter(ADDRESS);
				String city = req.getParameter(CITY);
				String stateCode = req.getParameter(STATE_CODE);
				searchString = address + BBBCoreConstants.SPACE + city
						+ BBBCoreConstants.COMMA + stateCode;
			}
			if (siteId.equalsIgnoreCase(CANADA_SITE)) {
				strTempSearch.append(BBBCoreConstants.ORIGINPARAM)
						.append(searchString)
						.append(BBBCoreConstants.RADIUSPARAM).append(mRadius)
						.append(BBBCoreConstants.UNITSPARAM).append(mUnits);
				searchString = strTempSearch.toString();
			} else {
				strTempSearch.append(BBBCoreConstants.ORIGINPARAM)
						.append(searchString)
						.append(BBBCoreConstants.RADIUSPARAM).append(mRadius);
				searchString = strTempSearch.toString();
			}
		}

		StoreDetailsWrapper objStoreDetailsWrapper = null;
		try {
			String storeId = (String) req
					.getObjectParameter(BBBCoreConstants.STOREID);
			if (!BBBUtility.isEmpty(storeId)) {
				logDebug(logMessage + " Search based on StoreId..." + storeId);
				StoreDetails objStoreDetails = null;
				objStoreDetails = getSearchStoreManager().searchStoreById(
						storeId, getSiteContext().getSite().getId(), storeType);
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
			} else if ((!BBBUtility.isEmpty((String) req
					.getObjectParameter(BBBCoreConstants.PAGEKEY)))
					&& (!BBBUtility.isEmpty((String) req
							.getObjectParameter(BBBCoreConstants.PAGENUMBER)))) {
				logDebug(logMessage + " Get records for page Number.."
						+ BBBCoreConstants.PAGENUMBER);
				// Code comment out to use alternative flow of page key function

				objStoreDetailsWrapper = getSearchStoreManager()
						.searchStorePerPage(
								(String) req
										.getObjectParameter(BBBCoreConstants.PAGEKEY),
								(String) req
										.getObjectParameter(BBBCoreConstants.PAGENUMBER));

				if (null != objStoreDetailsWrapper) {
					req.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER,
							objStoreDetailsWrapper);
					// If Store details Wrapper has populated Address suggestion
					// VO it means no success and need to display list of
					// multiple suggested addresses.
					if (null != objStoreDetailsWrapper
							.getStoreAddressSuggestion()) {
						req.serviceLocalParameter(
								BBBCoreConstants.ADDRESSSUGGESTION, req, res);
					} else {
						req.serviceLocalParameter(BBBCoreConstants.OUTPUT, req,
								res);
					}
				} else {
					req.serviceLocalParameter(BBBCoreConstants.EMPTY, req, res);
				}
			} else {
				// Change the cookie name or get it from property file
				// Read cookie file to reload the last performed search
				if (!BBBUtility.isEmpty(searchType)
						&& !BBBUtility.isEmpty(searchString)) {
					logDebug(logMessage + " || "
							+ " Store Search based on input address. . "
							+ searchString);
					String pageSize = req
							.getParameter(BBBCoreConstants.PAGESIZE);
					if (pageSize != null) {
						// BBBSL-5093 - Commented for Production Patch to solve
						// issues with BOPUS orders. Value of page size was set
						// as 1-96 (Alphanumeric)
						// Causing error (NumberFormat Exception) while find a
						// store functionality.
						// getSearchStoreManager().setPageSize(pageSize);
						logDebug("Call to set page size in Search Store Manager . Page Size ["
								+ pageSize + "] ");
					}
					if (searchType
							.equals(SelfServiceConstants.COORDINATES_BASED_SEARCH)) {
						searchString = searchString.replace("|", ",");
						// BBBSL-5093 - Change in method signature
						// (searchStoreByAddress) to pass Page Size as input
						// parameter.
						objStoreDetailsWrapper = getSearchStoreManager()
								.searchStoreByCoordinates(
										(String) req.getSession().getAttribute(
												BBBCoreConstants.STATUS),
										searchType, pageSize);
					} else {
						if(req.getSession().getAttribute(BBBCoreConstants.STATUS) != null)
						{
							searchString = req.getSession().getAttribute(BBBCoreConstants.STATUS).toString();
						}
						objStoreDetailsWrapper = getSearchStoreManager()
								.searchStoreByAddress(searchString, searchType,
										pageSize);
					}
					String inputSearchString = (String) req
							.getSession()
							.getAttribute(
									SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME);
					String inputType = (String) req.getSession().getAttribute(
							BBBCoreConstants.TYPE);

					req.setParameter(BBBCoreConstants.INPUTSEARCHSTRING,
							inputSearchString);
					req.setParameter(BBBCoreConstants.TYPE, inputType);

					if (null != objStoreDetailsWrapper) {
						if (objStoreDetailsWrapper.getStoreDetails() != null
								&& !objStoreDetailsWrapper.getStoreDetails()
										.isEmpty()) {
							req.setParameter(
									BBBCoreConstants.STOREDETAILSWRAPPER,
									objStoreDetailsWrapper);
								req.setParameter(
										BBBCoreConstants.STOREDETAILS_FOR_REGISTLRY,
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
							"BBBBusinessException in SearchStoreDroplet while Store Search based on input address",
							BBBCoreErrorConstants.ACCOUNT_ERROR_1208), e);
			req.setParameter(BBBCoreConstants.ERRORMESSAGE, e.getErrorCode()
					+ e.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
		} catch (BBBSystemException e) {
			logError(
					LogMessageFormatter.formatMessage(
							req,
							"BBBSystemException in SearchStoreDroplet while Store Search based on input address",
							BBBCoreErrorConstants.ACCOUNT_ERROR_1209), e);
			req.setParameter(BBBCoreConstants.ERRORMESSAGE, e.getErrorCode()
					+ e.getMessage());
			req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
		}

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
	 * @return the mapQuestTable
	 */
	public String getMapQuestTable() {
		return mMapQuestTable;
	}

	/**
	 * @param pMapQuestTable
	 *            the mapQuestTable to set
	 */
	public void setMapQuestTable(String pMapQuestTable) {
		mMapQuestTable = pMapQuestTable;
	}

	/**
	 * @return the mapQuestKey
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getStaticMapKey() throws BBBSystemException,
			BBBBusinessException {
		String configValue = null;
		List<String> keysList = getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,
						BBBCoreConstants.STATICMAPKEY);
		if (!BBBUtility.isListEmpty(keysList)) {
			configValue = keysList.get(0);
		}
		return configValue;
	}

	/**
	 * @return the size
	 */
	public String getStaticMapSize() {
		return mStaticMapSize;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setStaticMapSize(String pStaticMapSize) {
		mStaticMapSize = pStaticMapSize;
	}

	/**
	 * @return the staticMapZoom
	 */
	public String getStaticMapZoom() {
		return mStaticMapZoom;
	}

	/**
	 * @param staticMapZoom
	 *            the staticMapZoom to set
	 */
	public void setStaticMapZoom(String staticMapZoom) {
		mStaticMapZoom = staticMapZoom;
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
