package com.bbb.rest.selfservice;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import atg.commerce.inventory.InventoryException;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StoreSpecialityVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.selfservice.common.StoreLocator;
import com.bbb.selfservice.manager.ScheduleAppointmentManager;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.selfservice.tools.StoreTools;
import com.bbb.utils.BBBUtility;

/**
 * @author Sandeep Yadav
 * 
 */
public class SearchStoreService extends BBBGenericService {

	public static final String SEARCH_SKU_IN_STORE_ERROR_CODE = "70701";
	private SearchStoreManager mSearchStoreManager;
	private String mRadius;
	private BBBCatalogTools mCatalogTools;
	private BBBInventoryManager mInventoryManager;
	private ScheduleAppointmentManager mScheduleAppointmentManager;
	private static final String SEARCHSTRING = "searchString";

	private static final String APPOINTMENTCODE = "appointmentCode";
	private static final String SKUID = "skuID";
	private static final String ORDEREDQTY = "orderedQty";
	private BBBStoreInventoryContainer mStoreInventoryContainer;
	private StoreTools mStoreTools;
	/**
	 * @return the mScheduleAppointmentManager
	 */
	public ScheduleAppointmentManager getScheduleAppointmentManager() {
		return mScheduleAppointmentManager;
	}

	/**
	 * @param mScheduleAppointmentManager the mScheduleAppointmentManager to set
	 */
	public void setScheduleAppointmentManager(
			ScheduleAppointmentManager mScheduleAppointmentManager) {
		this.mScheduleAppointmentManager = mScheduleAppointmentManager;
	}

	private Repository storeRepository;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private static final String EXCLUDED_STORE_TYPE = "20";

	/**
	 * @return the storeRepository
	 */
	public Repository getStoreRepository() {
		return storeRepository;
	}

	/**
	 * @param storeRepository
	 *            the storeRepository to set
	 */
	public void setStoreRepository(Repository storeRepository) {
		this.storeRepository = storeRepository;
	}

	/**
	 * @return the storeTools
	 */
	public StoreTools getStoreTools() {
		return mStoreTools;
	}

	/**
	 * @param pStoreTools
	 *            the storeTools to set
	 */
	public void setStoreTools(StoreTools pStoreTools) {
		mStoreTools = pStoreTools;
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

	/**
	 * @return the storeInventoryContainer
	 */
	public BBBStoreInventoryContainer getStoreInventoryContainer() {
		return mStoreInventoryContainer;
	}

	/**
	 * @param pStoreInventoryContainer
	 *            the storeInventoryContainer to set
	 */
	public void setStoreInventoryContainer(
			BBBStoreInventoryContainer pStoreInventoryContainer) {
		mStoreInventoryContainer = pStoreInventoryContainer;
	}

	/**
	 * @return the inventoryManager
	 */
	public BBBInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * @param pInventoryManager
	 *            the inventoryManager to set
	 */
	public void setInventoryManager(BBBInventoryManager pInventoryManager) {
		mInventoryManager = pInventoryManager;
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
	 * This method will return the StoreDetailsWrapper object based on inputs
	 * given
	 * 
	 * @param inputParam
	 *            , A map that has keys like
	 *            searchString,radius,pageKey,pageNumber,pageSize
	 * @return StoreDetailsWrapper
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public StoreDetailsWrapper searchStores(Map<String, String> inputParam,
			String storeType) throws BBBSystemException, BBBBusinessException {

		logDebug("Entering SearchStoreService.searchStores method");

		StoreDetailsWrapper objStoreDetailsWrapper = null;

		if(BBBUtility.isEmpty(storeType))
		{
			storeType = getSearchStoreManager().getStoreType(extractSiteId());
		}

		if (null == inputParam || inputParam.isEmpty()) {
			throw new BBBSystemException(
					SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_INPUT,
					SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_INPUT);
		}
		logDebug("Input Param = " + inputParam);
		String appointmentType=null;
		String searchString = inputParam.get(SEARCHSTRING);
		String radiusStr = inputParam.get(SelfServiceConstants.RADIUS);
		String latitude = inputParam.get(SelfServiceConstants.LATITUDE);
		String longitude = inputParam.get(SelfServiceConstants.LONGITUDE);
		if(inputParam.containsKey(APPOINTMENTCODE)){
			appointmentType=inputParam.get(APPOINTMENTCODE);
		}

		String configValue = null;
		StoreLocator storeLocator = null;
		String pageSize = inputParam.get(BBBCoreConstants.PAGESIZE);
		String pageKey = inputParam.get(BBBCoreConstants.PAGEKEY);
		String pageNumber = inputParam.get(BBBCoreConstants.PAGENUMBER);
		StringBuilder searchStringBuilder = null;
		String excludedstoretype = null;
		int preSelecetedServiceRef=0;

		/*if (BBBUtility.isEmpty(searchString) || BBBUtility.isEmpty(radiusStr)) {
   throw new BBBBusinessException(
     SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT,
     SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT);
  }
		 */

		storeLocator = new StoreLocator();

		if (BBBUtility.isNotEmpty(radiusStr)) {
			try {
				storeLocator.setRadius(Integer.parseInt(radiusStr));
				if (storeLocator.getRadius() <= 0) {
					throw new BBBBusinessException(
							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_RADIUS,
							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_RADIUS);
				}
			} catch (NumberFormatException exception) {
				logError(SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_RADIUS
						+ exception);
				throw new BBBBusinessException(
						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_RADIUS,
						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_RADIUS);
			}
		}

		if (BBBUtility.isNotEmpty(pageNumber)) {
			try {
				storeLocator.setPagenumber(Integer.parseInt(pageNumber));
				if (storeLocator.getPagenumber() <= 0) {
					throw new BBBBusinessException(
							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER,
							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER);
				}
			} catch (NumberFormatException exception) {
				logError(SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER
						+ exception);
				throw new BBBBusinessException(
						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER,
						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER);
			}
		}

		if (BBBUtility.isNotEmpty(pageSize)) {
			
			
			
			
			
			try {
				if (Integer.parseInt(pageSize) <= 0) {
					throw new BBBBusinessException(
							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGESIZE,
							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGESIZE);
				}
			} catch (NumberFormatException exception) {
				logError(SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER
						+ exception);
				throw new BBBBusinessException(
						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER,
						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_PAGENUMBER);
			}
		}

		searchStringBuilder = new StringBuilder();

		try {

			if (BBBUtility.isNotEmpty(pageKey)
					&& BBBUtility.isNotEmpty(pageNumber)) {

				objStoreDetailsWrapper = getSearchStoreManager()
						.searchStorePerPage(pageKey, pageNumber);

			} else {

				/*
				 * searchKeyURL =
				 * getSearchStoreManager().getMapQuestSearchString();
				 * searchStringBuilder.append(searchKeyURL);
				 * searchStringBuilder.append("%7C");
				 * if(BBBUtility.isNotEmpty(storeType)) {
				 * searchStringBuilder.append(BBBCoreConstants.STORE_TYPE +
				 * storeType); }
				 * searchStringBuilder.append(BBBCoreConstants.AMPERSAND);
				 * searchStringBuilder
				 * .append(SelfServiceConstants.MAPQUEST_COLUMN_LIST);
				 * searchStringBuilder
				 * .append(BBBCoreConstants.RADIUSPARAM).append
				 * (storeLocator.getRadius());
				 * searchStringBuilder.append(BBBCoreConstants
				 * .ORIGINPARAM).append(searchString);
				 * if(BBBUtility.isNotEmpty(pageSize)){
				 * searchStringBuilder.append(AMPERSAND);
				 * searchStringBuilder.append(PAGESIZE);
				 * searchStringBuilder.append(pageSize); }
				 */

				// getting lat / lonf from request
				if(!BBBUtility.isEmpty(searchString))
				{
					searchString = searchString.replace("|", ",");
					if (searchString.contains(BBBCoreConstants.COMMA)) {
						String[] latLng = searchString
								.split(BBBCoreConstants.COMMA);
						latitude = latLng[0];
						longitude = latLng[1];

					}
					// getting address/zip code from request
					searchStringBuilder.append(getSearchStoreManager()
							.getMapQuestSearchStringForLatLng());
					searchStringBuilder
					.append(SelfServiceConstants.LOCATION_EQUALS);
					searchString = searchString.replace(BBBCoreConstants.SPACE,
							"%20");
					searchStringBuilder.append(searchString);
				}

				//taking from cookie
				else 
				{
					String latLngCookie = ServletUtil.getCurrentRequest()
							.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE);
					if (!BBBUtility.isEmpty(latLngCookie)) 
					{
						String[] arrLatLng = latLngCookie
								.split(BBBCoreConstants.COMMA);
						latitude = arrLatLng[1];
						longitude = arrLatLng[0];
					}
				}

				DynamoHttpServletRequest req =  ServletUtil.getCurrentRequest();
				req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES,radiusStr);
				req.setParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION, inputParam.get(BBBCoreConstants.USE_MY_CURRENT_LOCATION));

				if(BBBCoreConstants.TRUE.equals(inputParam.get(BBBCoreConstants.IS_FROM_STORE_LOCATOR)))
				{
					req.setParameter(BBBCoreConstants.SET_COOKIE, BBBCoreConstants.FALSE);
				}
				objStoreDetailsWrapper = getStoreTools().getStoresForMobile(
						latitude, longitude, searchStringBuilder.toString(),
						storeType);
				// objStoreDetailsWrapper =
				// getStoreTools().searchStore(searchType
				// ,searchStringBuilder.toString());
			}

			if (objStoreDetailsWrapper != null) {
				List<String> keysList = getCatalogTools().getAllValuesForKey(
						BBBCoreConstants.MAPQUESTSTORETYPE,
						"excluded_store_type");
				if (keysList != null && keysList.size() > 0) {
					configValue = keysList.get(0);
				}
				if (BBBUtility.isEmpty(configValue)) {
					excludedstoretype = EXCLUDED_STORE_TYPE;
				} else {
					excludedstoretype = configValue;
				}
				Iterator<StoreDetails> objStoreDetailsWrapperItr = (Iterator<StoreDetails>) objStoreDetailsWrapper
						.getStoreDetails().listIterator();
				while (objStoreDetailsWrapperItr.hasNext()) {
					StoreDetails strDetails = (StoreDetails) objStoreDetailsWrapperItr
							.next();
					if (strDetails != null
							&& strDetails.getStoreType() != null
							&& strDetails.getStoreType().equalsIgnoreCase(
									excludedstoretype)) {
						objStoreDetailsWrapperItr.remove();
						logDebug("SearchStoreService.searchStores  method removed store type"
								+ objStoreDetailsWrapper.getStoreDetails());
					}
				}
				storeSpecialityCode(objStoreDetailsWrapper.getStoreDetails());
				if(appointmentType!=null && objStoreDetailsWrapper.getStoreDetails() != null && !objStoreDetailsWrapper.getStoreDetails().isEmpty()) {
					Map<String, Boolean> appointmentMap = null;    
					try {
						appointmentMap = getScheduleAppointmentManager().checkAppointmentAvailability(objStoreDetailsWrapper.getStoreDetails(),
								appointmentType);
						preSelecetedServiceRef=getScheduleAppointmentManager().fetchPreSelectedServiceRef(appointmentType);
						if(appointmentMap!=null && !appointmentMap.isEmpty()){
							final boolean appointmentEligible = getScheduleAppointmentManager().canScheduleAppointmentForSiteId(extractSiteId());
							for (final StoreDetails storeDetails : objStoreDetailsWrapper.getStoreDetails()) {
								getScheduleAppointmentManager().checkAppointmentEligible( storeDetails,  appointmentMap, appointmentEligible);
								storeDetails.setPreSelectedServiceRef(preSelecetedServiceRef);
							}
						}
					} catch (BBBSystemException bbbException) {
						logError(SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR
								+ BBBCoreConstants.MAPQUESTSTORETYPE, bbbException);
						throw new BBBSystemException(
								SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR,
								SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR);
					}

				}

				DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
				Profile profile = (Profile) req
						.resolveName(BBBCoreConstants.ATG_PROFILE);
				String favoriteStoreId = getSearchStoreManager()
						.fetchFavoriteStoreId(extractSiteId(), profile);
				objStoreDetailsWrapper.setFavStoreId(favoriteStoreId);
				objStoreDetailsWrapper = setBabyCanadaFlag(objStoreDetailsWrapper, extractSiteId());

			}
		}

		catch (BBBSystemException bbbSystemException) {
			logError(SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR
					+ BBBCoreConstants.MAPQUESTSTORETYPE, bbbSystemException);
			throw new BBBSystemException(
					SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR,
					SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR);

		} catch (BBBBusinessException bbbBusinessException) {
			logError(SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR
					+ BBBCoreConstants.MAPQUESTSTORETYPE, bbbBusinessException);
			throw new BBBBusinessException(
					SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR,
					SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR);
		} finally {
			logDebug("SearchStoreService.searchStores  method ends");
		}
		return objStoreDetailsWrapper;
	}

	/**
	 * This method will return the SearchInStoreVO object based on inputs given
	 * 
	 * @param inputParam
	 *            , A map that has keys like
	 *            searchString,radius,pageKey,pageNumber,pageSize,skuid,quantity
	 * @return SearchInStoreVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public SearchInStoreVO searchSKUInStore(Map<String, String> inputParam)
			throws BBBBusinessException {

		SearchInStoreVO searchInStoreVO = null;

		logDebug("Entering SearchStoreService.searchSKUInStore method");

		if (BBBUtility.isMapNullOrEmpty(inputParam)) {
			return getSearchInStoreErrorVO(
					SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_INPUT,
					SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		}

		logDebug("Input Param = " + inputParam);

		String skuID = inputParam.get(SKUID);
		String quantity = inputParam.get(ORDEREDQTY);
		String searchString = inputParam.get("searchString");
		Long produtQty = 0l;

		if (BBBCoreConstants.TRUE.equals(inputParam
				.get(BBBCoreConstants.IS_FROM_PDP))
				&& BBBCoreConstants.TRUE.equals(inputParam
						.get(BBBCoreConstants.FAVOURITE_STORE))) {
			skuID = inputParam.get(BBBCoreConstants.SKUID);
		}
		if (BBBUtility.isEmpty(skuID) || BBBUtility.isEmpty(quantity)) {
			logError("Received empty productId = " + skuID + "in request");
			return getSearchInStoreErrorVO(
					SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT,
					SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		}

		/*if (BBBUtility.isNotEmpty(quantity)) {*/
			try {

				produtQty = Long.parseLong(quantity);

				if (produtQty <= 0) {
					searchInStoreVO = getSearchInStoreErrorVO(
							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_QUANTITY,
							SEARCH_SKU_IN_STORE_ERROR_CODE, true);
				}
			} catch (NumberFormatException e) {
				logError("Received invalid quanity = " + quantity
						+ "in request.");
				return getSearchInStoreErrorVO(
						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_QUANTITY,
						SEARCH_SKU_IN_STORE_ERROR_CODE, true);
			}
		//}
		try {
			// see if request is from PDP
			if (BBBCoreConstants.TRUE.equals(inputParam
					.get(BBBCoreConstants.IS_FROM_PDP))) {
				DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
				req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES,
						inputParam.get(BBBCoreConstants.RADIUS));

				String latLngCookie = req
						.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE);
				String latitude = inputParam.get(SelfServiceConstants.LATITUDE);
				String longitude = inputParam
						.get(SelfServiceConstants.LONGITUDE);
				if (!BBBUtility.isEmpty(latLngCookie)
						&& BBBUtility.isEmpty(searchString)) {
					/*if (!BBBUtility.isEmpty(latLngCookie)) {*/
						String[] arrLatLng = latLngCookie
								.split(BBBCoreConstants.COMMA);
						latitude = arrLatLng[1];
						longitude = arrLatLng[0];
					/*}*/
					if (BBBCoreConstants.TRUE.equals(inputParam
							.get(BBBCoreConstants.FAVOURITE_STORE))) {
						searchInStoreVO = getLocalStoreDetailsForPDP(
								inputParam, latitude, longitude, true);
					} else {
						searchInStoreVO = getNearestStoreDetailsForPDP(
								inputParam, latitude, longitude, true);
					}
				} else if (BBBCoreConstants.TRUE.equals(inputParam
						.get(BBBCoreConstants.FAVOURITE_STORE))) {
					searchInStoreVO = getLocalStoreDetailsForPDP(inputParam,
							latitude, longitude, false);
				} else if (BBBCoreConstants.FALSE.equals(inputParam
						.get(BBBCoreConstants.FAVOURITE_STORE))) {
					searchInStoreVO = getNearestStoreDetailsForPDP(inputParam,
							latitude, longitude, false);
				}
			}

			else {
				searchInStoreVO = getStoresForSku(inputParam, produtQty);
			}
			if (searchInStoreVO != null
					&& searchInStoreVO.getObjStoreDetails() != null) {
				storeSpecialityCode(searchInStoreVO.getObjStoreDetails()
						.getStoreDetails());
			}

		} catch (BBBSystemException exception) {
			logError("System exception while fetching store details: "
					+ exception);
			searchInStoreVO = getSearchInStoreErrorVO(
					SelfServiceConstants.ERROR_SEARCH_STORE_ERROR,
					SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		}
		logDebug("SearchStoreService.searchSKUInStore method ends");

		return searchInStoreVO;

	}


	/**
	 * This method will return the SearchInStoreVO object based on inputs given
	 * 
	 * @param inputParam
	 *            , A map that has keys like
	 *            searchString,radius,pageKey,pageNumber,pageSize,skuid,quantity
	 * @return SearchInStoreVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public SearchInStoreVO searchStoreForPLP(Map<String, String> inputParam)
			throws BBBBusinessException {

		SearchInStoreVO searchInStoreVO = null;

		logDebug("Entering SearchStoreService.searchSKUInStore method");

		if (null == inputParam || inputParam.isEmpty()) {
			return getSearchInStoreErrorVO(
					SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_INPUT,
					SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		}

		logDebug("Input Param = " + inputParam);

		//String skuID = inputParam.get(SKUID);
		//String quantity = inputParam.get(ORDEREDQTY);
		String searchString = inputParam.get("searchString");
		// Long produtQty = 0l;

		//  if (BBBCoreConstants.TRUE.equals(inputParam
		//    .get(BBBCoreConstants.IS_FROM_PDP))
		//    && BBBCoreConstants.TRUE.equals(inputParam
		//      .get(BBBCoreConstants.FAVOURITE_STORE))) {
		//   skuID = inputParam.get(BBBCoreConstants.SKUID);
		//  }
		//  if (BBBUtility.isEmpty(skuID) || BBBUtility.isEmpty(quantity)) {
		//   logError("Received empty productId = " + skuID + "in request");
		//   return getSearchInStoreErrorVO(
		//     SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT,
		//     SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		//  }
		//
		//  if (BBBUtility.isNotEmpty(quantity)) {
		//   try {
		//
		//    produtQty = Long.parseLong(quantity);
		//

		//				if (produtQty <= 0) {
		//					searchInStoreVO = getSearchInStoreErrorVO(
		//							SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_QUANTITY,
		//							SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		//				}
		//			} catch (NumberFormatException e) {
		//				logError("Received invalid quanity = " + quantity
		//						+ "in request.");
		//				return getSearchInStoreErrorVO(
		//						SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_QUANTITY,
		//						SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		//			}
		//		}
		boolean callFromPLP = false;
		if(!BBBUtility.isEmpty(inputParam.get("callFromPLP"))){
			String plpCall= inputParam.get("callFromPLP");
			callFromPLP = Boolean.parseBoolean(plpCall);
		}
		try {
			// see if request is from PDP
			if (callFromPLP) {
				DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
				req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS));

				String storeIdFromURL = inputParam.get("storeIdFromURL");
				String siteId = inputParam.get("siteId");
				String latLng = null;
				if(!storeIdFromURL.isEmpty() && !siteId.isEmpty()){
					latLng = modifyLatLngCookieForMobile(storeIdFromURL, siteId);
					if(isLoggingDebug()){
						logDebug("Latitude and Longitude value from DB using Store and Site ID : " + latLng);
					}

					//Set lat/lng in the cookie if storeID parameter is found in the URL
					//storeID can be inserted in the URL by selecting a store on PLP OR by directing to PLP from any place where URL is hosted already with storeID
					if (req != null && latLng != null && BBBUtility.getCookie(req, SelfServiceConstants.LAT_LNG_COOKIE) != null) {
						Cookie[] requestCookies = req.getCookies();
						if (requestCookies != null) {
							for (Cookie cookie : requestCookies) {
								if (cookie.getName().equals(SelfServiceConstants.LAT_LNG_COOKIE)) {
									if(cookie.getValue() != latLng.toString()){

										if(isLoggingDebug()){
											logDebug("SearchStoreService.searchStoreForPLP ===> Start: remove existingLatLngCookie method ");
										}
										final Cookie existingLatLngCookie = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, "");
										existingLatLngCookie.setMaxAge(0);
										existingLatLngCookie.setPath(BBBCoreConstants.SLASH);
										BBBUtility.addCookie(ServletUtil.getCurrentResponse(), existingLatLngCookie, false);
										req.getSession().setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE);
										if(isLoggingDebug()){
											logDebug("SearchStoreService.searchStoreForPLP ===> End: remove existingLatLngCookie" + ": cookie removed");
										}

										if(isLoggingDebug()){
											logDebug("SearchStoreService.searchStoreForPLP ===> Start: add newLatLngCookie method ");
										}
										Cookie latLngCookie = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE,latLng);
										latLngCookie.setMaxAge(getStoreTools().getCookieTimeOut());
										latLngCookie.setDomain(req.getServerName());
										latLngCookie.setPath(BBBCoreConstants.SLASH);
										BBBUtility.addCookie(ServletUtil.getCurrentResponse(), latLngCookie, false);
										req.getSession().setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE);
										if(isLoggingDebug()){
											logDebug("SearchStoreService.searchStoreForPLP ===> End: add newLatLngCookie" + ": cookie added");
										}

									}
								}
							}
						}
					}else if (req != null && latLng != null && BBBUtility.getCookie(req, SelfServiceConstants.LAT_LNG_COOKIE) == null) {
						if(isLoggingDebug()){
							logDebug("BBBSetLatLngCookieDroplet.service ===> Start: add newLatLngCookie method ");
						}
						Cookie latLngCookie = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE,latLng);
						latLngCookie.setMaxAge(getStoreTools().getCookieTimeOut());
						latLngCookie.setDomain(req.getServerName());
						latLngCookie.setPath(BBBCoreConstants.SLASH);
						BBBUtility.addCookie(ServletUtil.getCurrentResponse(), latLngCookie, false);
						req.getSession().setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE);
						if(isLoggingDebug()){
							logDebug("BBBSetLatLngCookieDroplet.service ===> End: add newLatLngCookie" + ": cookie added");
						}
					}

				}

				String latLngCookie = req.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE);
				String latitude = inputParam.get(SelfServiceConstants.LATITUDE);
				String longitude = inputParam.get(SelfServiceConstants.LONGITUDE); 
				if (!BBBUtility.isEmpty(latLng) && latLng != latLngCookie) {
					String[] arrLatLng = latLng.split(BBBCoreConstants.COMMA);
					latitude = arrLatLng[1];
					longitude = arrLatLng[0];
					if (BBBCoreConstants.TRUE.equals(inputParam
							.get(BBBCoreConstants.FAVOURITE_STORE))) {
						searchInStoreVO = getLocalStoreDetailsForPLP(
								inputParam, latitude, longitude, true,callFromPLP);
					} else {
						searchInStoreVO = getNearestStoreDetailsForPLP(
								inputParam, latitude, longitude, true,callFromPLP);
					}
				}
				else if (!BBBUtility.isEmpty(latLngCookie)
						&& BBBUtility.isEmpty(searchString)) {
					/*if (!BBBUtility.isEmpty(latLngCookie)) {*/
						String[] arrLatLng = latLngCookie
								.split(BBBCoreConstants.COMMA);
						latitude = arrLatLng[1];
						longitude = arrLatLng[0];
					/*}*/
					if (BBBCoreConstants.TRUE.equals(inputParam
							.get(BBBCoreConstants.FAVOURITE_STORE))) {
						searchInStoreVO = getLocalStoreDetailsForPLP(
								inputParam, latitude, longitude, true,callFromPLP);
					} else {
						searchInStoreVO = getNearestStoreDetailsForPLP(
								inputParam, latitude, longitude, true,callFromPLP);
					}
				} else if (BBBCoreConstants.TRUE.equals(inputParam
						.get(BBBCoreConstants.FAVOURITE_STORE))) {
					searchInStoreVO = getLocalStoreDetailsForPLP(inputParam,
							latitude, longitude, false, callFromPLP);
				} else if (BBBCoreConstants.FALSE.equals(inputParam
						.get(BBBCoreConstants.FAVOURITE_STORE))) {
					searchInStoreVO = getNearestStoreDetailsForPLP(inputParam,
							latitude, longitude, false, callFromPLP);
				}
			}

			else {
				long productQty =1;
				searchInStoreVO = getStoresForSku(inputParam, productQty);
			}
			if (searchInStoreVO != null
					&& searchInStoreVO.getObjStoreDetails() != null) {
				storeSpecialityCode(searchInStoreVO.getObjStoreDetails()
						.getStoreDetails());
			}

		} catch (BBBSystemException exception) {
			logError("System exception while fetching store details: "
					+ exception);
			searchInStoreVO = getSearchInStoreErrorVO(
					SelfServiceConstants.ERROR_SEARCH_STORE_ERROR,
					SEARCH_SKU_IN_STORE_ERROR_CODE, true);
		}
		logDebug("SearchStoreService.searchSKUInStore method ends");

		return searchInStoreVO;

	}



	/**
	 * This method get stores for sku
	 * 
	 * @param inputParam
	 * @param produtQty
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private SearchInStoreVO getStoresForSku(Map<String, String> inputParam,
			long produtQty) throws BBBSystemException, BBBBusinessException {
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		String skuID = inputParam.get(SKUID);
		String radius = inputParam.get("radius");
		String searchString = inputParam.get("searchString");
		SearchInStoreVO searchInStoreVO = null;
 		Map<String, Integer> productStatusMap = null;
		String storeType = getSearchStoreManager().getStoreType(
				extractSiteId());
		objStoreDetailsWrapper = searchStores(inputParam, storeType);

		if (extractSiteId() != null
				&& extractSiteId().equals("BedBathCanada")) {
			int count = 0;
			int babyCount = 0;
			int bedBathCount = 0;
			List<StoreDetails> babyCanadaStore = new ArrayList<StoreDetails>();
			List<StoreDetails> bedBathCanadaStore = new ArrayList<StoreDetails>();
			if (inputParam.get("babyCA").equalsIgnoreCase("true")) {
				if (objStoreDetailsWrapper != null
						&& objStoreDetailsWrapper.getStoreDetails() != null) {
					for (count = 0; count < objStoreDetailsWrapper
							.getStoreDetails().size(); count++) {
						if (objStoreDetailsWrapper.getStoreDetails().get(count)
								.getStoreId()
								.startsWith(BBBCoreConstants.STORE_CA_START)) {
							objStoreDetailsWrapper.getStoreDetails().get(count)
							.setBabyCanadaFlag(true);
							babyCanadaStore.add(objStoreDetailsWrapper
									.getStoreDetails().get(count));
							babyCount++;
						} else {
							objStoreDetailsWrapper.getStoreDetails().get(count)
							.setBabyCanadaFlag(false);
							bedBathCanadaStore.add(objStoreDetailsWrapper
									.getStoreDetails().get(count));
							bedBathCount++;
						}
					}
					babyCanadaStore.addAll(bedBathCanadaStore);
					Collections.copy(objStoreDetailsWrapper.getStoreDetails(),
							babyCanadaStore);
				}
			} else {
				if (objStoreDetailsWrapper != null
						&& objStoreDetailsWrapper.getStoreDetails() != null) {
					for (count = 0; count < objStoreDetailsWrapper
							.getStoreDetails().size(); count++) {
						if (objStoreDetailsWrapper.getStoreDetails().get(count)
								.getStoreId()
								.startsWith(BBBCoreConstants.STORE_CA_START)) {
							// Fixing the baby logo Issue not displaying
							// against baby store
							objStoreDetailsWrapper.getStoreDetails().get(count)
							.setBabyCanadaFlag(true);
							babyCanadaStore.add(objStoreDetailsWrapper
									.getStoreDetails().get(count));
							babyCount++;
						} else {
							// Fixing the baby logo Issue not displaying
							// against baby store
							objStoreDetailsWrapper.getStoreDetails().get(count)
							.setBabyCanadaFlag(false);
							bedBathCanadaStore.add(objStoreDetailsWrapper
									.getStoreDetails().get(count));
							bedBathCount++;
						}
					}
				}
			}
			int labelBabyCount = 1, labelBedBathCount = 1;
			if (babyCount > 1 || babyCount == 0) {
				labelBabyCount = 0;
			}
			if (bedBathCount > 1 || bedBathCount == 0) {
				labelBedBathCount = 0;
			}
			StringBuilder stringBuilder = new StringBuilder();
			String bedcount = Integer.toString(labelBedBathCount);
			String bacount = Integer.toString(labelBabyCount);
			stringBuilder.append("txt_store_canada_");
			stringBuilder.append(bacount);
			stringBuilder.append(BBBCoreConstants.UNDERSCORE);
			stringBuilder.append(bedcount);
			stringBuilder.append("_found_msg");
			String stringLabel = stringBuilder.toString();
			String bedBathCountString = Integer.toString(bedBathCount);
			String babyCanadaCountString = Integer.toString(babyCount);
			Map<String, String> pPlaceHolderMap = new HashMap<String, String>();
			pPlaceHolderMap.put(BBBCoreConstants.BED_BATH_CANADA_STORE_COUNT,
					bedBathCountString);
			pPlaceHolderMap.put(BBBCoreConstants.BABY_CANADA_STORE_COUNT,
					babyCanadaCountString);
			pPlaceHolderMap.put(BBBCoreConstants.STORE_MILES, radius);
			pPlaceHolderMap.put(BBBCoreConstants.STORE_SEARCH_STRING,
					searchString);
			String labelForMobileCanada = null;
			if (ServletUtil.getCurrentRequest() != null
					&& ServletUtil.getCurrentRequest().getLocale() != null) {
				labelForMobileCanada = getLblTxtTemplateManager()
						.getPageTextArea(
								stringLabel,
								ServletUtil.getCurrentRequest().getLocale()
								.getLanguage(), pPlaceHolderMap, null);
			}
			if (objStoreDetailsWrapper != null) {
				objStoreDetailsWrapper
				.setLabelForMobileCanada(labelForMobileCanada);
			}
		}

		if (objStoreDetailsWrapper != null) {

			if (objStoreDetailsWrapper.getStoreDetails() != null
					&& !objStoreDetailsWrapper.getStoreDetails().isEmpty()) {

				productStatusMap = checkProductAvailability(
						objStoreDetailsWrapper.getStoreDetails(),
						extractSiteId(), skuID, produtQty);

				if (productStatusMap != null) {

					searchInStoreVO = new SearchInStoreVO();
					searchInStoreVO.setObjStoreDetails(objStoreDetailsWrapper);
					searchInStoreVO.setProductStatusMap(productStatusMap);

				} else {
					searchInStoreVO = getSearchInStoreErrorVO(
							SelfServiceConstants.ERROR_SEARCH_STORE_PRODUCT_NOT_AVAILABLE,
							SEARCH_SKU_IN_STORE_ERROR_CODE, true);
				}

			} else if (objStoreDetailsWrapper.getStoreAddressSuggestion() != null
					&& !objStoreDetailsWrapper.getStoreAddressSuggestion()
					.isEmpty()) {

				searchInStoreVO = new SearchInStoreVO();
				searchInStoreVO
				.setStoreAddressSuggestion(objStoreDetailsWrapper
						.getStoreAddressSuggestion());
			}
			objStoreDetailsWrapper.setRadius(radius);
			objStoreDetailsWrapper.setEmptyDOMResponse(ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM));
		}

		if (null == searchInStoreVO) {
			searchInStoreVO = new SearchInStoreVO();
		}
		/*objStoreDetailsWrapper.setRadius(radius);
		objStoreDetailsWrapper.setEmptyDOMResponse(ServletUtil.getCurrentRequest().getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM));*/
		searchInStoreVO.setObjStoreDetails(objStoreDetailsWrapper);
		return searchInStoreVO;
	}

	/**
	 * This method is used to create a SearchInStoreVO instance and set error
	 * message and code.
	 * 
	 * @return
	 */
	private SearchInStoreVO getSearchInStoreErrorVO(final String errorMessage,
			final String errorCode, final boolean errorExist) {
		final SearchInStoreVO searchInStoreVO = new SearchInStoreVO();
		searchInStoreVO.setErrorMessage(errorMessage);
		searchInStoreVO.setErrorExist(errorExist);
		searchInStoreVO.setErrorCode(errorCode);
		logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "
				+ errorCode);
		logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "
				+ errorMessage);
		return searchInStoreVO;
	}

	/**
	 * Product availability and bopus exclusion check for searched Store
	 * 
	 * @param pStoreDetails
	 * @param pSiteId
	 * @param pSkuId
	 * @param pReqQty
	 * @param operation
	 * @return Map, storeInventoryMap
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private Map<String, Integer> checkProductAvailability(
			List<StoreDetails> pStoreDetails, String pSiteId, String pSkuId,
			long pReqQty) throws BBBSystemException, BBBBusinessException {

		logDebug("Entering SearchStoreService.checkProductAvailability method");

		Map<String, Integer> productAvailStatus = new HashMap<String, Integer>();
		List<String> bopusEligibleStates = null;
		List<String> bopusInEligibleStore = null;
		bopusEligibleStates = getCatalogTools().getBopusEligibleStates(pSiteId);

		String pStoreId = getSearchStoreManager().getStoreType(pSiteId);
		bopusInEligibleStore = getCatalogTools().getBopusInEligibleStores(
				pStoreId, pSiteId);

		logDebug(" Inside  SearchInStoreDroplet.checkProductAvailability---BopusEligibleState List "
				+ bopusEligibleStates);
		List<String> storeIds = new ArrayList<String>();
		for (StoreDetails storeDetails : pStoreDetails) {
			// Check for BOPUS excluscion at State Level
			if (bopusEligibleStates == null) {
				// Store pick not available for state
				productAvailStatus.put(storeDetails.getStoreId(),
						SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			} else if (bopusEligibleStates != null && storeDetails.getState() != null
					&& !bopusEligibleStates.contains(storeDetails.getState().trim())) {
				// Store pick not available for state
				productAvailStatus.put(storeDetails.getStoreId(),
						SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			} else if (bopusInEligibleStore != null
					&& bopusInEligibleStore.contains(storeDetails.getStoreId())) {
				// Store is not Bopus Eligible
				productAvailStatus.put(storeDetails.getStoreId(),
						SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			}
			storeIds.add(storeDetails.getStoreId());

		}
		Map<String, Integer> storeInventoryMap;

		try {
			BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer)ServletUtil.getCurrentRequest().resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer");
			storeInventoryMap = getInventoryManager()
					.getBOPUSProductAvailability(pSiteId, pSkuId, storeIds,
							pReqQty, BBBInventoryManager.ONLINE_STORE,
							storeInventoryContainer, false, null, false,
							false);

		} catch (InventoryException inventoryException) {
			if(isLoggingDebug()){
			logDebug("Inventory exception while fetching store details: "
					+ inventoryException);
			}
			throw new BBBSystemException(
					BBBCoreErrorConstants.ACCOUNT_ERROR_1203,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1203);
		}

		for (String storeId : storeInventoryMap.keySet()) {
			/*
			 * if(storeInventoryMap.get(storeId)==BBBInventoryManager.LIMITED_STOCK
			 * ) { storeInventoryMap.put(storeId,
			 * BBBInventoryManager.AVAILABLE); }
			 */
			Integer boupusStatus = productAvailStatus.get(storeId);
			Integer inventoryStatus = storeInventoryMap.get(storeId);
			if (boupusStatus != null) {
				// merge the No Bopus flag with Inventory
				String mergedData = boupusStatus.toString();
				if (inventoryStatus != null) {
					mergedData += inventoryStatus.toString();
				}
				productAvailStatus.put(storeId, Integer.parseInt(mergedData));
			} else {
				productAvailStatus.put(storeId, storeInventoryMap.get(storeId));
			}
		}
		logDebug("productAvailStatus is: " + productAvailStatus);
		return productAvailStatus;
	}

	@SuppressWarnings("unchecked")
	private void storeSpecialityCode(List<StoreDetails> storeList)
			throws BBBSystemException {
		try {
			if (storeList != null) {
				for (StoreDetails store : storeList) {
					if (store != null) {
						RepositoryView view = getStoreRepository().getView(
								"specialityCodeMap");

						RqlStatement statement = RqlStatement
								.parseRqlStatement("specialityShopCd=?0");

						Object params[] = new Object[1];
						params[0] = store.getSpecialtyShopsCd();
						RepositoryItem[] items = extractDBCall(view, statement, params);
						if (items != null && items[0] != null) {
							List<RepositoryItem> specialityItemList = (List<RepositoryItem>) items[0]
									.getPropertyValue("specialityCd");
							Set<RepositoryItem> specialityItemSet = new HashSet<RepositoryItem>();
							if (specialityItemList != null
									&& !specialityItemList.isEmpty()) {
								for (RepositoryItem item : specialityItemList) {
									specialityItemSet.add(item);
								}
								List<StoreSpecialityVO> specialityVOlist = getCatalogTools()
										.getStoreSpecialityList(
												specialityItemSet);
								store.setStoreSpecialityVO(specialityVOlist);
							}
						}
					}
				}
			}
		} catch (RepositoryException e) {
			logError("Repository Exception while fetching store speciality code"
					+ e);
			throw new BBBSystemException("err_store_speciallty_code",
					"Repository Exception while fetching store speciality code");
		}
	}

	/** extracted method to Mock database call
	 * @param RepositoryView,RqlStatement,Object[]
	 * @return RepositoryItem[]
	 */
	
	protected RepositoryItem[] extractDBCall(RepositoryView view, RqlStatement statement, Object[] params)
			throws RepositoryException {
		RepositoryItem[] items = statement.executeQuery(view,
				params);
		return items;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(
			LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * This method get fav stores based on store id/lat lng
	 * 
	 * @param moblRequest
	 * @return LocalStoreVO
	 */
	@SuppressWarnings("unchecked")
	public SearchInStoreVO getLocalStoreDetailsForPDP(
			Map<String, String> moblRequest, String latitude, String longitude,
			boolean isfromCookie) {
		logDebug("Inside getLocalStoreDetailsForPDP() method");
		StoreDetailsWrapper storeDetailsWrapper = null;
		List<StoreDetails> storeDetails = null;
		SearchInStoreVO searchInStoreVO = new SearchInStoreVO();
		try {
			DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse res = ServletUtil.getCurrentResponse();
			String siteId = extractSiteId();
			Profile profile = (Profile) req
					.resolveName(BBBCoreConstants.ATG_PROFILE);
			String favoriteStoreId = getSearchStoreManager()
					.fetchFavoriteStoreId(siteId, profile);

			req.setParameter(BBBCoreConstants.ORDER_QUANTITY,
					moblRequest.get(BBBCoreConstants.ORDER_QUANTITY));
			req.setParameter(BBBCoreConstants.RADIUS,
					moblRequest.get(BBBCoreConstants.RADIUS));
			req.setParameter(BBBCoreConstants.SKUID,
					moblRequest.get(BBBCoreConstants.SKUID));


			if (isfromCookie) {
				logDebug("Search is based on cookie");
				getSearchStoreManager().getFavStoreByCoordinates(siteId, req,
						res, latitude, longitude, false);
			} else if (!BBBUtility.isEmpty(favoriteStoreId)) {
				logDebug("Search is based on Fav Store");
				getSearchStoreManager().getFavStoreByStoreId(siteId, req, res,
						favoriteStoreId);
			} else if (!BBBUtility.isEmpty(latitude)
					&& !BBBUtility.isEmpty(longitude)) {
				logDebug("Search is based on Lat/Lng");
				getSearchStoreManager().getFavStoreByCoordinates(siteId, req,
						res, latitude, longitude, false);
			}
			//returning if search criterion is empty
			else
			{
				storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
						storeDetails);
				storeDetailsWrapper.setEmptyInput(BBBCoreConstants.TRUE);
				searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
				return searchInStoreVO;
			}

			Object objStDetails = req
					.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS);
			if (objStDetails != null) {
				storeDetails = (List<StoreDetails>) objStDetails;
			}
			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			if(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES) != null)
			{
				storeDetailsWrapper.setRadius(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES).toString());
			}
			storeDetailsWrapper
			.setInventoryNotAvailable(req
					.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE));
			storeDetailsWrapper
			.setErrorIneligibleStates(req
					.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE));
			if (isLoggingDebug()) {
				logDebug("Local Store Details " + storeDetailsWrapper);
			}
			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);

		} catch (Exception e) {
			String errMsg = getLblTxtTemplateManager().getErrMsg(
					BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,
					BBBCoreConstants.DEFAULT_LOCALE, null);
			if (BBBUtility.isEmpty(errMsg)) {
				errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE;
			}
			logDebug("Exception occurred while getting nearest store to fav store ",
					e);
			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			storeDetailsWrapper.setErrorInViewFavStores(errMsg);
			//set baby canada flag
			storeDetailsWrapper = setBabyCanadaFlag(storeDetailsWrapper, extractSiteId());
			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
		}
		logDebug("getLocalStoreDetailsForPDP() method ends");
		return searchInStoreVO;

	}
	
/**
 *extracted method for obtaining site Id 
 *@return String
 */
	protected String extractSiteId() {
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}



	/**
	 * This method get fav stores based on store id/lat lng
	 * 
	 * @param moblRequest
	 * @return LocalStoreVO
	 */
	@SuppressWarnings("unchecked")
	public SearchInStoreVO getLocalStoreDetailsForPLP(
			Map<String, String> moblRequest, String latitude, String longitude,
			boolean isfromCookie, boolean callFromPLP) {
		logInfo("Inside getLocalStoreDetailsForPDP() method");
		StoreDetailsWrapper storeDetailsWrapper = null;
		List<StoreDetails> storeDetails = null;
		SearchInStoreVO searchInStoreVO = new SearchInStoreVO();
		try {
			DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse res = ServletUtil.getCurrentResponse();
			String siteId = extractSiteId();
			Profile profile = (Profile) req
					.resolveName(BBBCoreConstants.ATG_PROFILE);
			String favoriteStoreId = getSearchStoreManager()
					.fetchFavoriteStoreId(siteId, profile);



			if (isfromCookie) {
				logInfo("Search is based on cookie");
				getSearchStoreManager().getFavStoreByCoordinates(siteId, req,
						res, latitude, longitude,callFromPLP);
			} else if (!BBBUtility.isEmpty(favoriteStoreId)) {
				logInfo("Search is based on Fav Store");
				getSearchStoreManager().getFavStoreByStoreIdForPLP(siteId, req, res,
						favoriteStoreId);
			} else if (!BBBUtility.isEmpty(latitude)
					&& !BBBUtility.isEmpty(longitude)) {
				logInfo("Search is based on Lat/Lng");
				getSearchStoreManager().getFavStoreByCoordinates(siteId, req,
						res, latitude, longitude,callFromPLP);
			}
			//returning if search criterion is empty
			else
			{
				storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
						storeDetails);
				storeDetailsWrapper.setEmptyInput(BBBCoreConstants.TRUE);
				searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
				return searchInStoreVO;
			}

			Object objStDetails = req
					.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS);
			if (objStDetails != null) {
				storeDetails = (List<StoreDetails>) objStDetails;
			}
			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			if(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES) != null)
			{
				storeDetailsWrapper.setRadius(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES).toString());
			}
			storeDetailsWrapper
			.setErrorIneligibleStates(req
					.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE));
			if (isLoggingDebug()) {
				logDebug("Local Store Details " + storeDetailsWrapper);
			}
			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);

		} catch (Exception e) {
			String errMsg = getLblTxtTemplateManager().getErrMsg(
					BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,
					BBBCoreConstants.DEFAULT_LOCALE, null);
			if (BBBUtility.isEmpty(errMsg)) {
				errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE;
			}
			logInfo("Exception occurred while getting nearest store to fav store ",
					e);
			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			storeDetailsWrapper.setErrorInViewFavStores(errMsg);
			//set baby canada flag
			storeDetailsWrapper = setBabyCanadaFlag(storeDetailsWrapper, extractSiteId());
			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
		}
		logDebug("getLocalStoreDetailsForPDP() method ends");
		return searchInStoreVO;

	}

	/**
	 * This method get details of nearest stores based on cookie/Fav
	 * store/Lat-Lng for mobile
	 * 
	 * @param moblRequest
	 * @return SearchInStoreVO
	 */
	@SuppressWarnings("unchecked")
	public SearchInStoreVO getNearestStoreDetailsForPDP(
			Map<String, String> moblRequest, String latitude, String longitude,
			boolean isFromCookie) {
		logDebug("Inside getNearestStoreDetailsForPDP() method");
		StoreDetailsWrapper storeDetailsWrapper = null;
		SearchInStoreVO searchInStoreVO = new SearchInStoreVO();
		List<StoreDetails> storeDetails = null;
		try {
			DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse res = ServletUtil.getCurrentResponse();

			//setting radius change request
			req.setParameter(SelfServiceConstants.RADIUS_CHANGE , moblRequest.get(SelfServiceConstants.RADIUS_CHANGE));

			String siteId = extractSiteId();
			Profile profile = (Profile) req
					.resolveName(BBBCoreConstants.ATG_PROFILE);
			String favoriteStoreId = getSearchStoreManager()
					.fetchFavoriteStoreId(siteId, profile);

			req.setParameter(BBBCoreConstants.ORDER_QUANTITY,
					moblRequest.get(BBBCoreConstants.ORDER_QUANTITY));
			req.setParameter(BBBCoreConstants.SKUID, moblRequest.get(SKUID));
			req.setParameter(BBBCoreConstants.RADIUS,
					moblRequest.get(BBBCoreConstants.RADIUS));
			req.setParameter(BBBCoreConstants.SITE_ID,
					extractSiteId());

			if (isFromCookie
					&& BBBUtility.isEmpty(moblRequest
							.get(BBBCoreConstants.SEARCHSTRING))) {
				logDebug("Search is based on cookie");
				getSearchStoreManager().getNearestStoresByCoordinates(latitude,
						longitude, req, res, false);
			} else if (!BBBUtility.isEmpty(favoriteStoreId)
					&& BBBUtility.isEmpty(moblRequest
							.get(BBBCoreConstants.SEARCHSTRING))) {
				logDebug("Search is based on Fav Store");
				getSearchStoreManager().getNearestStoresByStoreId(
						favoriteStoreId, req, res, favoriteStoreId, false);
			} else if (!BBBUtility.isEmpty(moblRequest
					.get(SelfServiceConstants.LATITUDE))
					&& !BBBUtility.isEmpty(moblRequest
							.get(SelfServiceConstants.LONGITUDE))
							&& BBBUtility.isEmpty(moblRequest
									.get(BBBCoreConstants.SEARCHSTRING))) {
				logDebug("Search is based on Lat/Lng");
				getSearchStoreManager().getNearestStoresByCoordinates(latitude,
						longitude, req, res, false);
			} else if (!BBBUtility.isEmpty(moblRequest
					.get(BBBCoreConstants.SEARCHSTRING))) {
				logDebug("Search is based on Search String");
				getSearchStoreManager().getStoresBySearchString(
						moblRequest.get(BBBCoreConstants.SEARCHSTRING), req,
						res, false);
			}
			//returning if search criterion is empty
			else
			{
				storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
						storeDetails);
				storeDetailsWrapper.setEmptyInput(BBBCoreConstants.TRUE);
				searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
				return searchInStoreVO;
			}
			Object objStDetails = (Object) req
					.getObjectParameter(BBBCoreConstants.STORE_DETAILS);
			if (objStDetails != null) {
				storeDetails = (List<StoreDetails>) objStDetails;
			}

			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			if(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES) != null)
			{
				storeDetailsWrapper.setRadius(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES).toString());
			}
			storeDetailsWrapper.setFavStoreId(favoriteStoreId);
			storeDetailsWrapper.setEmptyDOMResponse(req.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM));

			//set baby canada flag
			storeDetailsWrapper = setBabyCanadaFlag(storeDetailsWrapper, siteId);

			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);

			searchInStoreVO
			.setProductStatusMap((Map<String, Integer>) req
					.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE));
			if (isLoggingDebug()) {
				logDebug("Nearest Store Details " + searchInStoreVO);
			}

		} catch (Exception e) {
			String errMsg = getLblTxtTemplateManager().getErrMsg(
					BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,
					BBBCoreConstants.DEFAULT_LOCALE, null);
			if (BBBUtility.isEmpty(errMsg)) {
				errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES;
			}
			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			storeDetailsWrapper.setErrorInViewAllStores(errMsg);
			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
			logError(
					"Exception occurred while getting nearest store to fav store by coordinates ",
					e);
		}
		logDebug("GetNearestStoreDetailsForPDP() ends");
		return searchInStoreVO;
	}


	/**
	 * This method get details of nearest stores based on cookie/Fav
	 * store/Lat-Lng for mobile
	 * 
	 * @param moblRequest
	 * @return SearchInStoreVO
	 */
	@SuppressWarnings("unchecked")
	public SearchInStoreVO getNearestStoreDetailsForPLP(
			Map<String, String> moblRequest, String latitude, String longitude,
			boolean isFromCookie, boolean callFromPLP) {
		logInfo("Inside getNearestStoreDetailsForPDP() method");
		StoreDetailsWrapper storeDetailsWrapper = null;
		SearchInStoreVO searchInStoreVO = new SearchInStoreVO();
		List<StoreDetails> storeDetails = null;
		try {
			DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse res = ServletUtil.getCurrentResponse();

			//setting radius change request
			req.setParameter(SelfServiceConstants.RADIUS_CHANGE , moblRequest.get(SelfServiceConstants.RADIUS_CHANGE));

			String siteId = extractSiteId();
			Profile profile = (Profile) req
					.resolveName(BBBCoreConstants.ATG_PROFILE);
			String favoriteStoreId = getSearchStoreManager()
					.fetchFavoriteStoreId(siteId, profile);


			req.setParameter(BBBCoreConstants.RADIUS,
					moblRequest.get(BBBCoreConstants.RADIUS));
			req.setParameter(BBBCoreConstants.SITE_ID,
					extractSiteId());

			if (isFromCookie
					&& BBBUtility.isEmpty(moblRequest
							.get(BBBCoreConstants.SEARCHSTRING))) {
				logInfo("Search is based on cookie");
				getSearchStoreManager().getNearestStoresByCoordinates(latitude,
						longitude, req, res, callFromPLP);
			} else if (!BBBUtility.isEmpty(favoriteStoreId)
					&& BBBUtility.isEmpty(moblRequest
							.get(BBBCoreConstants.SEARCHSTRING))) {
				logInfo("Search is based on Fav Store");
				getSearchStoreManager().getNearestStoresByStoreId(
						favoriteStoreId, req, res, favoriteStoreId, callFromPLP);
			} else if (!BBBUtility.isEmpty(moblRequest
					.get(SelfServiceConstants.LATITUDE))
					&& !BBBUtility.isEmpty(moblRequest
							.get(SelfServiceConstants.LONGITUDE))
							&& BBBUtility.isEmpty(moblRequest
									.get(BBBCoreConstants.SEARCHSTRING))) {
				logInfo("Search is based on Lat/Lng");
				getSearchStoreManager().getNearestStoresByCoordinates(latitude,
						longitude, req, res, callFromPLP);
			} else if (!BBBUtility.isEmpty(moblRequest
					.get(BBBCoreConstants.SEARCHSTRING))) {
				logInfo("Search is based on Search String");
				getSearchStoreManager().getStoresBySearchString(
						moblRequest.get(BBBCoreConstants.SEARCHSTRING), req,
						res, callFromPLP);
			}
			//returning if search criterion is empty
			else
			{
				storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
						storeDetails);
				storeDetailsWrapper.setEmptyInput(BBBCoreConstants.TRUE);
				searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
				return searchInStoreVO;
			}
			Object objStDetails = (Object) req
					.getObjectParameter(BBBCoreConstants.STORE_DETAILS);
			if (objStDetails != null) {
				storeDetails = (List<StoreDetails>) objStDetails;
			}

			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			if(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES) != null)
			{
				storeDetailsWrapper.setRadius(req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES).toString());
			}
			storeDetailsWrapper.setFavStoreId(favoriteStoreId);
			storeDetailsWrapper.setEmptyDOMResponse(req.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM));

			//set baby canada flag
			storeDetailsWrapper = setBabyCanadaFlag(storeDetailsWrapper, siteId);

			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);

			if (isLoggingDebug()) {
				logDebug("Nearest Store Details " + searchInStoreVO);
			}

		} catch (Exception e) {
			String errMsg = getLblTxtTemplateManager().getErrMsg(
					BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,
					BBBCoreConstants.DEFAULT_LOCALE, null);
			if (BBBUtility.isEmpty(errMsg)) {
				errMsg = BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES;
			}
			storeDetailsWrapper = new StoreDetailsWrapper(1, 1, "1",
					storeDetails);
			storeDetailsWrapper.setErrorInViewAllStores(errMsg);
			searchInStoreVO.setObjStoreDetails(storeDetailsWrapper);
			logError(
					"Exception occurred while getting nearest store to fav store by coordinates ",
					e);
		}
		logInfo("GetNearestStoreDetailsForPDP() ends");
		return searchInStoreVO;
	}

	/**This method sets babycanadaflag to show baby logo on canada site for baby stores
	 * @param objStoreDetailsWrapper
	 * @param siteId
	 * @return
	 */
	private StoreDetailsWrapper setBabyCanadaFlag(StoreDetailsWrapper objStoreDetailsWrapper , String siteId)
	{
		logDebug("Inside setBabyCanadaFlag() method");
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA))
		{
			List<StoreDetails> babyCanadaStore=new ArrayList<StoreDetails>();
			List<StoreDetails> bedBathCanadaStore=new ArrayList<StoreDetails>();
			int count=0;

			if(objStoreDetailsWrapper!=null && objStoreDetailsWrapper.getStoreDetails() !=null)
			{
				for(count=0;count<objStoreDetailsWrapper.getStoreDetails().size();count++)
				{
					if(objStoreDetailsWrapper.getStoreDetails().get(count).getStoreId().startsWith(BBBCoreConstants.STORE_CA_START))
					{
						objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(true);
					}
					else
					{
						objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(false);
					}
					if(isLoggingDebug())
					{
						logDebug("Baby Canada Flag of store id "+objStoreDetailsWrapper.getStoreDetails().get(count).getStoreId()+" is "+
								objStoreDetailsWrapper.getStoreDetails().get(count).isBabyCanadaFlag());
					}
				}
				babyCanadaStore.addAll(bedBathCanadaStore) ; 
				Collections.copy(objStoreDetailsWrapper.getStoreDetails(),babyCanadaStore);
			}

		}
		logDebug("Inside setBabyCanadaFlag() method ends");
		return objStoreDetailsWrapper;
	}

	public String modifyLatLngCookieForMobile (String storeIdFromURL , String pSiteId)
			throws BBBBusinessException, BBBSystemException {
		String storeType;
		StringBuffer latLng = new StringBuffer();
		String lat = BBBCoreConstants.BLANK;
		String lng = BBBCoreConstants.BLANK;
		if(isLoggingDebug()){
			logDebug("Inside  SearchStoreManager.modifyLatLngCookie Starts, storeId and siteId is : " + storeIdFromURL + "," + pSiteId);
		}
		if (storeIdFromURL != null && !storeIdFromURL.isEmpty()) {
			storeType = getSearchStoreManager().getStoreType(pSiteId);
			if(isLoggingDebug()){
				logDebug("SearchStoreManager.modifyLatLngCookie ===> storeType : " + storeType);
			}
			try {
				RepositoryItem[] repositoryItem = getStoreTools().getStores(storeIdFromURL, storeType);
				if(repositoryItem != null){
					lat = repositoryItem[0].getPropertyValue(SelfServiceConstants.LATITUDE).toString();
					lng = repositoryItem[0].getPropertyValue(SelfServiceConstants.LONGITUDE).toString();
					if(isLoggingDebug()){
						logDebug("SearchStoreManager.modifyLatLngCookie ===> Derived lat & lng : " + lat + lng);
					}
					latLng.append(lng).append(BBBCoreConstants.COMMA).append(lat);
				}
			}catch (Exception e) {
				throw new BBBBusinessException(e.getMessage());
			}
		}
		return latLng.toString();
	}

}
