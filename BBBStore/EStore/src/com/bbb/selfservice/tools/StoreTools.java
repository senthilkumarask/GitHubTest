package com.bbb.selfservice.tools;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 03-November-2011
//Modified by: Seema Singhal: Added code for address suggestions use case
//Modified on: 19-January-2012
//--------------------------------------------------------------------------------

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.http.client.ClientProtocolException;

import com.bbb.cache.LocalStoreVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBInventoryManagerImpl;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.selfservice.common.Maneuvers;
import com.bbb.selfservice.common.RouteDetails;
import com.bbb.selfservice.common.RouteLegs;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreAddressSuggestion;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.utils.BBBUtility;

import atg.adapter.gsa.GSARepository;
import atg.apache.xerces.impl.xpath.regex.RegularExpression;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import net.sf.ezmorph.MorphException;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
/**
* @author Jaswinder Sidhu
* 
 */
/**
 * @author kchau7
 *
 */
public class StoreTools extends BBBGenericService {

       /**
       * instance for HTTPCallInvoker
       */
       HTTPCallInvoker mHttpCallInvoker;
       /** The Catalog tools. */
       private BBBCatalogTools mCatalogTools;
       private MutableRepository localStoreRepository;

       private String regularExpression;

       private static final String METHOD_NAME = "getStoreDetails";
       private static final String OPERATION_NAME = "GET_STORE_DETAILS_FROM_DB";
       private static final String RADIUS_CANADA = "100";
       
       private CoherenceCacheContainer cacheContainer;
       private BBBInventoryManager inventoryManager;

		/**
		 * @return the cacheContainer
		 */
		public CoherenceCacheContainer getCacheContainer() {
			return cacheContainer;
		}
		
		/**
		 * @param cacheContainer
		 *            the cacheContainer to set
		 */
		public void setCacheContainer(CoherenceCacheContainer cacheContainer) {
			this.cacheContainer = cacheContainer;
		}

       /**
       * @return HTTPCallInvoker
       */
       public HTTPCallInvoker getHttpCallInvoker() {
              return this.mHttpCallInvoker;
       }

       /**
       * @param httpCallInvoker
       *            set HTTPCallInvoker
       */
       public void setHttpCallInvoker(final HTTPCallInvoker pHttpCallInvoker) {
              this.mHttpCallInvoker = pHttpCallInvoker;
       }

       public String getRegularExpression() {
              return regularExpression;
       }

       public void setRegularExpression(String regularExpression) {
              this.regularExpression = regularExpression;
       }

       public MutableRepository getLocalStoreRepository() {
              return localStoreRepository;
       }

       public void setLocalStoreRepository(MutableRepository localStoreRepository) {
              this.localStoreRepository = localStoreRepository;
       }

       public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

       /**
       * Search store method sends a MapQuest request via MapQuestService class
       * and get the Latitude and longitude if search is zip code or address
       * based. else it calls method getStoreDetails() to get store details
       * 
        * @param pSearchString
       * @throws IOException
       * @throws ClientProtocolException
       * @throws BBBBusinessException
       */
       public StoreDetailsWrapper searchStore(String searchType,
                     String pSearchString) throws ClientProtocolException, IOException,
                     BBBBusinessException {
              String logMessage = getClass().getName() + ".searchStore()";
              logDebug(logMessage + " || " + " Starts here.");
              logDebug(logMessage + " || " + " Search Criteria String --> "
                           + pSearchString);
              String searchString = pSearchString;
              DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
              DynamoHttpServletResponse res = ServletUtil.getCurrentResponse();
              searchString = parseSearchString(searchString);
              StoreDetailsWrapper objStoreDetailsWrapper = null;
              String searchResult = null;
              String storeType = null;
              if (req.getSession().getAttribute(BBBCoreConstants.STORE_TYPE) != null) {
                     storeType = req.getSession()
                                  .getAttribute(BBBCoreConstants.STORE_TYPE).toString();
              }
              try {
                     // if search is coordinate based
                     if (SelfServiceConstants.COORDINATES_BASED_SEARCH
                                  .equals(searchType)) {
                           objStoreDetailsWrapper = getStoreDetails(req.getSession()
                                         .getAttribute(SelfServiceConstants.LAT).toString(),
                                         ServletUtil.getCurrentRequest().getSession()
                                                       .getAttribute(SelfServiceConstants.LNG)
                                                       .toString(), storeType);
                     }
                     // if search is address or zip code based
                     else if (SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH
                                  .equals(searchType)
                                  || SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH
                                                .equals(searchType)) {

                           // take lat/lng from cookie only when user has not changed the
                           // store.
                           if ((!BBBCoreConstants.FALSE.equalsIgnoreCase((String) req
                                         .getSession().getAttribute(
                                                       SelfServiceConstants.SEARCH_BASED_ON_COOKIE)))) {
                                  // Check if lat/lng is there in cookie for the address/zip
                                  // code entered previously by user
                                  String latLngCookie = BBBUtility.getCookie(req,
                                                SelfServiceConstants.LAT_LNG_COOKIE);
                                  if (!BBBUtility.isEmpty(latLngCookie)) {
                                         String[] arrLatLng = latLngCookie
                                                       .split(BBBCoreConstants.COMMA);
                                         objStoreDetailsWrapper = getStoreDetails(arrLatLng[1],
                                                       arrLatLng[0], storeType);
                                  } else {
                                	  logDebug(SelfServiceConstants.LAT_LNG_COOKIE + "is empty. Making map quest call with parameter : searchString=" + searchString); 
                                         searchResult = getHttpCallInvoker().executeQuery(
                                                       searchString);
                                         if (!BBBUtility.isEmpty(searchResult)) {
                                                objStoreDetailsWrapper = getStoreDetailsByLatLng(
                                                              searchResult, searchString, storeType, req,
                                                              res);
                                         }
                                  }
                           }
                           // if lat/lng is not there in cookie , query map quest
                           else {
                        	   logDebug("making map quest call with parameter : searchString=" + searchString);   
                        	   searchResult = getHttpCallInvoker().executeQuery(
                                                searchString);
                                  if (!BBBUtility.isEmpty(searchResult)) {
                                         objStoreDetailsWrapper = getStoreDetailsByLatLng(
                                                       searchResult, searchString, storeType, req, res);
                                  }
                           }

                     } else {
                           searchResult = getHttpCallInvoker().executeQuery(searchString);
                           objStoreDetailsWrapper = storeJSONObjectParser(searchResult,
                                         searchString);
                     }
              } catch (BBBSystemException e) {
                     logError("\n StoreTools.searchStore Request sent to MapQuest web service call is " +  searchString, e);
                     BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_MAPQUEST_1001,
                                  "Exception in calling Mapquest in searchStore");
              } catch (Exception e) {
                     logError("Exception occurred while getting store details " , e);
              } finally{
            	  logDebug(logMessage + " || " + " Ends here.");
              }
              return objStoreDetailsWrapper;
       }

       /**
       * This method get store details when request is from mobile
       * 
        * @param latitude
       * @param longitude
       * @param searchString
       * @return StoreDetailsWrapper
       * @throws BBBBusinessException
       * @throws BBBSystemException
       */
       public StoreDetailsWrapper getStoresForMobile(String latitude,
                     String longitude, String searchString, String storeType)
                     throws BBBBusinessException, BBBSystemException {
    	   	  String logMessage = getClass().getName() + ".getStoresForMobile()";   
    	   	  logDebug(logMessage + " starts || fetches store details from DB for mobile with Input paramaeters : latitude=" + latitude + ", longitude=" + longitude + "searchString=" + searchString + ",StroeType=" + storeType);
              StoreDetailsWrapper objStoreDetailsWrapper = null;
              String searchResult = null;
              StringBuilder latLng = new StringBuilder();
              DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
              if ((!BBBUtility.isEmpty(latitude))  && (!BBBUtility.isEmpty(longitude)) ) {
                     objStoreDetailsWrapper = getStoreDetails(latitude, longitude, storeType);
                 	if (req != null && BBBCoreConstants.TRUE.equals(req.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION))) {
                 		latLng.append(longitude).append(BBBCoreConstants.COMMA).append(latitude);
						Cookie cookie = new Cookie(
								SelfServiceConstants.LAT_LNG_COOKIE,
								latLng.toString());
						cookie.setMaxAge(getCookieTimeOut());
						cookie.setDomain(req.getServerName());
						cookie.setPath(BBBCoreConstants.SLASH);
						BBBUtility.addCookie(ServletUtil.getCurrentResponse(), cookie, true);
						req.getSession().setAttribute(
								SelfServiceConstants.SEARCH_BASED_ON_COOKIE,
								BBBCoreConstants.TRUE);
					}
              }

              else if (!BBBUtility.isEmpty(searchString)) {
                     try {
                   	  		logDebug(SelfServiceConstants.LAT_LNG_COOKIE + "is empty. Making map quest call with parameter : searchString=" + searchString); 
                   	  		searchResult = getHttpCallInvoker().executeQuery(searchString);
                            if (!BBBUtility.isEmpty(searchResult)) {
                                  objStoreDetailsWrapper = getStoreDetailsByLatLng(
                                                searchResult, searchString, storeType, req, ServletUtil.getCurrentResponse());
                           }
                     } catch (Exception e) {
                           throw new BBBBusinessException(e.getMessage());
                     }
              }
              logDebug(logMessage + " ends");
              return objStoreDetailsWrapper;
       }

       /**
       * This method is used to parse StoreJSON object and populate the store
       * details in StoreDetailsWrapper object
       * 
        * @param pJsonResultString
       * @throws BBBBusinessException
       */
       @SuppressWarnings("unchecked")
       protected StoreDetailsWrapper storeJSONObjectParser(String pJsonResultString,
                     String pRequestString) throws BBBBusinessException {
              final String logMessage = getClass().getName()
                           + "StoreJSONObjectParser";
              logDebug(logMessage + " Starts here");
              logDebug(logMessage + " Search Result --> " + pJsonResultString);
              logDebug("StoreTools.storeJSONObjectParser Request sent to MapQuest web service call is "
                           + pRequestString);
              JSONObject jsonObject = null;
              List<StoreDetails> alStoreDetails = new ArrayList<StoreDetails>();
              List<StoreAddressSuggestion> alStoreAddressSuggestions = new ArrayList<StoreAddressSuggestion>();
              StoreDetailsWrapper objStoreDetailsWrapper = null;
              // try {
              // //System.out.println(jsonString);
              jsonObject = (JSONObject) JSONSerializer.toJSON(pJsonResultString);
              DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
              List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);

              int statusCode = 1;
              try {
                     statusCode = dynaBeanProperties.contains(SelfServiceConstants.INFO) ? ((Integer) ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO))
                                  .get(SelfServiceConstants.STATUSCODE)).intValue() : 1;
              } catch (MorphException e) {
                     statusCode = dynaBeanProperties.contains(SelfServiceConstants.INFO) ? ((Integer) ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO)).get("statuscode"))
                                  .intValue() : 1;
              }

              if (statusCode == 0) {
                     int recordCount = 0;
                     int totalPages = 0;
                     String distanceUnit = null;
                     String pageKey = null;
                     int currentPage = 0;
                     String imageCode = null;
                     if (dynaBeanProperties.contains(SelfServiceConstants.TOTALPAGES)) {
                           totalPages = ((Integer) ((MorphDynaBean) JSONResultbean)
                                         .get(SelfServiceConstants.TOTALPAGES)).intValue();
                     }

                     if (dynaBeanProperties.contains(SelfServiceConstants.OPTIONS)) {
                           DynaBean optionsBean = (DynaBean) JSONResultbean
                                         .get(SelfServiceConstants.OPTIONS);
                           List<String> optionsBeanProperties = getPropertyNames(optionsBean);
                           if (optionsBeanProperties.contains(SelfServiceConstants.UNITS)) {
                                  distanceUnit = (String) optionsBean
                                                .get(SelfServiceConstants.UNITS);
                           }
                           if (optionsBeanProperties
                                         .contains(SelfServiceConstants.PAGEKEY)) {
                                  pageKey = (String) optionsBean
                                                .get(SelfServiceConstants.PAGEKEY);
                           }

                           if (optionsBeanProperties
                                         .contains(SelfServiceConstants.CURRENTPAGE)) {
                                  currentPage = ((Integer) optionsBean
                                                .get(SelfServiceConstants.CURRENTPAGE)).intValue();
                           }
                     }

                     if (dynaBeanProperties.contains(SelfServiceConstants.RESULTSCOUNT)) {
                           recordCount = ((Integer) JSONResultbean
                                         .get(SelfServiceConstants.RESULTSCOUNT)).intValue();
                     }

                     if (recordCount > 0) {
                           List<DynaBean> alResults = null;
                           if (dynaBeanProperties
                                         .contains(SelfServiceConstants.SEARCHRESULTS)) {
                                  alResults = (ArrayList<DynaBean>) JSONResultbean
                                                .get(SelfServiceConstants.SEARCHRESULTS);
                           }

                           // FIXME
                           if (!BBBUtility.isListEmpty(alResults)) {
                                  for (Iterator<DynaBean> iterator = alResults.iterator(); iterator
                                                .hasNext();) {
                                         MorphDynaBean searchResultBean = (MorphDynaBean) iterator
                                                       .next();
                                         List<String> searchResultBeanProperties = getPropertyNames(searchResultBean);

                                         if (searchResultBeanProperties
                                                       .contains(SelfServiceConstants.FIELDS)) {
                                                MorphDynaBean fieldsBean = (MorphDynaBean) searchResultBean
                                                              .get(SelfServiceConstants.FIELDS);
                                                List<String> fieldsBeanProperties = getPropertyNames(fieldsBean);

                                                if (fieldsBeanProperties
                                                              .contains(SelfServiceConstants.DISPLAY_ONLINE)
                                                              && fieldsBean
                                                                            .get(SelfServiceConstants.DISPLAY_ONLINE) != null
                                                              && !fieldsBean
                                                                            .get(SelfServiceConstants.DISPLAY_ONLINE)
                                                                            .equals(SelfServiceConstants.DISPLAY_ONLINE_FLAG)) {
                                                       continue;

                                                }

                                                String pSatStoreTimings = "";
                                                String pSunStoreTimings = "";
                                                String pWeekdaysStoreTimings = "";
                                                String pOtherTimings1 = "";
                                                String pOtherTimings2 = "";
                                                if (fieldsBeanProperties
                                                              .contains(SelfServiceConstants.HOURS)) {
                                                       String storeTimingStr = (String) fieldsBean
                                                                     .get(SelfServiceConstants.HOURS);
                                                       if (!BBBUtility.isEmpty(storeTimingStr)) {
                                                              storeTimingStr = storeTimingsParser(storeTimingStr);
                                                       }
                                                       StringTokenizer hrs = new StringTokenizer(
                                                                     storeTimingStr, ",");
                                                       pWeekdaysStoreTimings = hrs.hasMoreTokens() ? hrs
                                                                     .nextToken() : " ";
                                                       pSatStoreTimings = hrs.hasMoreTokens() ? hrs
                                                                     .nextToken() : "";
                                                       pSunStoreTimings = hrs.hasMoreTokens() ? hrs
                                                                     .nextToken() : "";
                                                       pOtherTimings1 = hrs.hasMoreTokens() ? hrs
                                                                     .nextToken() : "";
                                                       pOtherTimings2 = hrs.hasMoreTokens() ? hrs
                                                                     .nextToken() : "";
                                                       if (!BBBUtility.isEmpty(pWeekdaysStoreTimings)) {
                                                              String temp[] = pWeekdaysStoreTimings
                                                                           .split(BBBCoreConstants.COLON, 2);
                                                              pWeekdaysStoreTimings = temp[0]
                                                                           + BBBCoreConstants.COLON
                                                                           + BBBCoreConstants.COMMA + temp[1];
                                                       }
                                                       if (!BBBUtility.isEmpty(pSatStoreTimings)) {
                                                              String temp[] = pSatStoreTimings.split(
                                                                           BBBCoreConstants.COLON, 2);
                                                              pSatStoreTimings = temp[0]
                                                                           + BBBCoreConstants.COLON
                                                                           + BBBCoreConstants.COMMA + temp[1];
                                                       }
                                                       if (!BBBUtility.isEmpty(pSunStoreTimings)) {
                                                              String temp[] = pSunStoreTimings.split(
                                                                           BBBCoreConstants.COLON, 2);
                                                              pSunStoreTimings = temp[0]
                                                                           + BBBCoreConstants.COLON
                                                                           + BBBCoreConstants.COMMA + temp[1];
                                                       }
                                                }

                                                if (!(fieldsBeanProperties
                                                              .contains(SelfServiceConstants.POSTAL) && ((String) fieldsBean
                                                              .get(SelfServiceConstants.POSTAL))
                                                              .equals("00000"))) {
                                                       boolean contactFlag = false;
                                                       if (fieldsBeanProperties
                                                                     .contains(SelfServiceConstants.RECORDID)) {
                                                              try {
                                                                     StoreVO storeVO = getCatalogTools()
                                                                                  .getStoreDetails(
                                                                                                (String) fieldsBean
                                                                                                              .get(SelfServiceConstants.RECORDID));
                                                                     contactFlag = storeVO.isContactFlag();
                                                              } catch (BBBSystemException e) {
                                                                     contactFlag = false;
                                                              } catch (BBBBusinessException e) {
                                                                     contactFlag = false;
                                                              }

                                                       }

                                                       String lng = "";
                                                       String lat = "";
                                                       String specialty_shops_cd = "";
                                                       String store_type = "";
                                                       if ( fieldsBeanProperties
                                                               .contains(SelfServiceConstants.LNG) && null != fieldsBean
                                                                                  .get(SelfServiceConstants.LNG)) {
                                                              lng = String.valueOf(fieldsBean
                                                                            .get(SelfServiceConstants.LNG));
                                                       }

                                                       if( fieldsBeanProperties.contains(SelfServiceConstants.LAT) && null != fieldsBean
                                                                                  .get(SelfServiceConstants.LAT)) {
                                                              lat = String.valueOf(fieldsBean
                                                                            .get(SelfServiceConstants.LAT));
                                                       }

                                                       if ( fieldsBeanProperties.contains(SelfServiceConstants.SPECIALTY_SHOPS_CD.toLowerCase()) && null != fieldsBean
                                                                                  .get(SelfServiceConstants.SPECIALTY_SHOPS_CD
                                                                                                .toLowerCase())) {
                                                              specialty_shops_cd = String
                                                                           .valueOf(fieldsBean
                                                                                         .get(SelfServiceConstants.SPECIALTY_SHOPS_CD
                                                                                                       .toLowerCase()));
                                                       }

                                                       if (fieldsBeanProperties.contains(SelfServiceConstants.STORE_TYPE.toLowerCase())&&
                                                    		   null != fieldsBean.get(SelfServiceConstants.STORE_TYPE
                                                                                                .toLowerCase())) {
                                                              store_type = String
                                                                           .valueOf(fieldsBean
                                                                                         .get(SelfServiceConstants.STORE_TYPE
                                                                                                       .toLowerCase()));
                                                       }

                                                       alStoreDetails
                                                                     .add(new StoreDetails(
                                                                                  (fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.RECORDID)) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.RECORDID)
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.NAME) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.NAME)
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.SPECIAL_MSG) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.SPECIAL_MSG)
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.ADDRESS
                                                                                                              .toLowerCase()) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.ADDRESS
                                                                                                              .toLowerCase())
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.CITY
                                                                                                              .toLowerCase()) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.CITY
                                                                                                              .toLowerCase())
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.STATE
                                                                                                              .toLowerCase()) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.STATE
                                                                                                              .toLowerCase())
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.COUNTRY
                                                                                                              .toLowerCase()) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.COUNTRY
                                                                                                              .toLowerCase())
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.POSTAL
                                                                                                              .toLowerCase()) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.POSTAL
                                                                                                              .toLowerCase())
                                                                                                : "",
                                                                                  pSatStoreTimings,
                                                                                  pSunStoreTimings,
                                                                                  pWeekdaysStoreTimings,
                                                                                  pOtherTimings1,
                                                                                  pOtherTimings2,
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.PHONE) ? (String) fieldsBean
                                                                                                .get(SelfServiceConstants.PHONE)
                                                                                                : "",
                                                                                  imageCode,
                                                                                  searchResultBeanProperties
                                                                                                .contains(SelfServiceConstants.DISTANCE
                                                                                                              .toLowerCase()) ? searchResultBean
                                                                                                .get(SelfServiceConstants.DISTANCE
                                                                                                              .toLowerCase())
                                                                                                .toString()
                                                                                                : "",
                                                                                  distanceUnit,
                                                                                  searchResultBeanProperties
                                                                                                .contains(SelfServiceConstants.RESULTNUMBER
                                                                                                              .toLowerCase()) ? searchResultBean
                                                                                                .get(SelfServiceConstants.RESULTNUMBER
                                                                                                              .toLowerCase())
                                                                                                .toString()
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.LNG) ? lng
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.LAT) ? lat
                                                                                                : "",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.SPECIALTY_SHOPS_CD
                                                                                                              .toLowerCase()) ? specialty_shops_cd
                                                                                                : "1",
                                                                                  fieldsBeanProperties
                                                                                                .contains(SelfServiceConstants.STORE_TYPE
                                                                                                              .toLowerCase()) ? store_type
                                                                                                : "", contactFlag));
                                                }
                                         }
                                  }
                           }
                           objStoreDetailsWrapper = new StoreDetailsWrapper(currentPage,
                                         totalPages, pageKey, alStoreDetails);
                     }// If response has multiple address suggestions then statuscode is
                           // 610.
              } else if (statusCode == 610) {
                     List<DynaBean> alResults = null;
                     List<MorphDynaBean> alAddress = null;
                     try {
                           alResults = (ArrayList<DynaBean>) JSONResultbean
                                         .get(SelfServiceConstants.COLLECTIONS);

                           for (Iterator<DynaBean> iterator = alResults.iterator(); iterator
                                         .hasNext();) {
                                  alAddress = (ArrayList<MorphDynaBean>) iterator.next();

                                  for (Iterator<MorphDynaBean> iterator1 = alAddress
                                                .iterator(); iterator1.hasNext();) {
                                         MorphDynaBean searchResultBean = iterator1.next();

                                         List<String> searchResultBeanProperties = getPropertyNames(searchResultBean);

                                         alStoreAddressSuggestions
                                                       .add(new StoreAddressSuggestion(
                                                                     searchResultBeanProperties
                                                                                  .contains(SelfServiceConstants.ADMINAREA5) ? (String) searchResultBean
                                                                                  .get(SelfServiceConstants.ADMINAREA5)// City
                                                                                  : "::",
                                                                     searchResultBeanProperties
                                                                                  .contains(SelfServiceConstants.ADMINAREA4) ? (String) searchResultBean
                                                                                  .get(SelfServiceConstants.ADMINAREA4)// address
                                                                                  : "::",
                                                                     searchResultBeanProperties
                                                                                  .contains(SelfServiceConstants.ADMINAREA3) ? (String) searchResultBean
                                                                                  .get(SelfServiceConstants.ADMINAREA3)// stateCode
                                                                                  : "::",
                                                                     searchResultBeanProperties
                                                                                  .contains(SelfServiceConstants.STREET) ? (String) searchResultBean
                                                                                  .get(SelfServiceConstants.STREET)
                                                                                  : "" // street
                                                       ));

                                  }
                           }
                           objStoreDetailsWrapper = new StoreDetailsWrapper(
                                         alStoreAddressSuggestions);
                     } catch (MorphException e) {
                           // Handling Exception in case of Ambiguous Result
                           logError("MorphException in storeJSONObjectParser statusCode"
                                         + statusCode);
                     }
              }

              else {
                     // Some error code returned by MapQuest
                     String errorMsg = ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO)).get(
                                  SelfServiceConstants.MESSAGES).toString();
                     logError("\n StoreTools.storeJSONObjectParser Request sent to MapQuest web service call is "
                                  + pRequestString
                                  + "\n StoreTools.storeJSONObjectParser Result String returned from MapQuest web service call is "
                                  + pJsonResultString
                                  + "\n StoreTools.storeJSONObjectParser Error Message returned in Result String from MapQuest web service call is "
                                  + errorMsg);

                     throw new BBBBusinessException(String.valueOf(statusCode), errorMsg);
              }
              logDebug(logMessage + " Ends here");
              return objStoreDetailsWrapper;
       }

       /**
       * This method parses JSon response of Map quest to get latitude and
       * longitude and then call method to get store details from db
       * 
        * @param pJsonResultString
       * @param pRequestString
       * @param storeType
       * @return StoreDetailsWrapper
       * @throws BBBBusinessException
       * @throws BBBSystemException
       */
       @SuppressWarnings("unchecked")
       public StoreDetailsWrapper getStoreDetailsByLatLng(
                     String pJsonResultString, String pRequestString, String storeType,
                     DynamoHttpServletRequest req, DynamoHttpServletResponse res)
                     throws BBBBusinessException, BBBSystemException {

              final String logMessage = getClass().getName()
                           + "getStoreDetailsByLatLng";
              logDebug(logMessage + " Starts here :parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db");
              logDebug(logMessage + " Search Result  --> " + pJsonResultString);
              logDebug("StoreTools.storeJSONObjectParser Request sent to MapQuest web service call is "
                           + pRequestString);
              JSONObject jsonObject = null;
              StoreDetailsWrapper objStoreDetailsWrapper = null;
              jsonObject = (JSONObject) JSONSerializer.toJSON(pJsonResultString);
              DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
              List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);
              int statusCode = 1;
              StringBuffer latLng = new StringBuffer();
              String lng = BBBCoreConstants.BLANK;
              String lat = BBBCoreConstants.BLANK;
              try {
                     statusCode = dynaBeanProperties.contains(SelfServiceConstants.INFO) ? ((Integer) ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO))
                                  .get(SelfServiceConstants.STATUS_CODE)).intValue() : 1;

              } catch (MorphException e) {
                     statusCode = dynaBeanProperties.contains(SelfServiceConstants.INFO) ? ((Integer) ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO))
                                  .get(SelfServiceConstants.STATUS_CODE)).intValue() : 1;
              }

              if (statusCode == 0) {
                     try {
                           List<DynaBean> alResults = null;
                           if (dynaBeanProperties.contains(SelfServiceConstants.RESULTS)) {
                                  alResults = (ArrayList<DynaBean>) JSONResultbean
                                                .get(SelfServiceConstants.RESULTS);
                           }

                           if (!BBBUtility.isListEmpty(alResults)) {
                                  for (Iterator<DynaBean> iterator = alResults.iterator(); iterator
                                                .hasNext();) {
                                         MorphDynaBean searchResultBean = (MorphDynaBean) iterator
                                                       .next();

                                         List<String> searchResultBeanProperties = getPropertyNames(searchResultBean);
                                         if (searchResultBeanProperties
                                                       .contains(SelfServiceConstants.LOCATIONS)) {
                                                List<DynaBean> lLocations = (ArrayList<DynaBean>) searchResultBean
                                                              .get(SelfServiceConstants.LOCATIONS);
                                                MorphDynaBean searchResultBean1 = (MorphDynaBean) lLocations
                                                              .iterator().next();
                                                List<String> searchResultBeanProperties1 = getPropertyNames(searchResultBean1);

                                                if (searchResultBeanProperties1
                                                              .contains(SelfServiceConstants.LATLNG)) {
                                                       MorphDynaBean fieldsBean = (MorphDynaBean) searchResultBean1
                                                                     .get(SelfServiceConstants.LATLNG);
                                                       if (null != fieldsBean
                                                                     && null != fieldsBean
                                                                                  .get(SelfServiceConstants.LOCATIONLNG)) {
                                                              lng = String
                                                                           .valueOf(fieldsBean
                                                                                         .get(SelfServiceConstants.LOCATIONLNG));
                                                              latLng.append(lng);
                                                       }

                                                       if (null != fieldsBean
                                                                     && null != fieldsBean
                                                                                  .get(SelfServiceConstants.LOCATIONLAT)) {
                                                              lat = String
                                                                           .valueOf(fieldsBean
                                                                                         .get(SelfServiceConstants.LOCATIONLAT));
                                                              latLng.append(BBBCoreConstants.COMMA)
                                                                           .append(lat);
                                                       }
                                                       break;
                                                }
                                         }
                                  }
					if (req != null && !(BBBCoreConstants.FALSE.equals(req.getParameter(BBBCoreConstants.SET_COOKIE)))) {
						Cookie cookie = new Cookie(
								SelfServiceConstants.LAT_LNG_COOKIE,
								latLng.toString());
						cookie.setMaxAge(getCookieTimeOut());
						cookie.setDomain(req.getServerName());
						cookie.setPath(BBBCoreConstants.SLASH);
						BBBUtility.addCookie(res, cookie, true);

						req.getSession().setAttribute(
								SelfServiceConstants.SEARCH_BASED_ON_COOKIE,
								BBBCoreConstants.TRUE);
					}
                              if(req!=null)
                            	 objStoreDetailsWrapper = getStoreDetails(lat, lng,
                                                storeType);

                           }
                           
                           if(isLoggingDebug())
                           {
                        	   logDebug("Latitude:Longitude returned from Mapquest "+lat+" : "+lng);
                           }

                     } catch (MorphException e) {
                           // Handling Exception in case of Ambiguous Result
                           logError("MorphException in storeJSONObjectParser statusCode"
                                         + statusCode);
                     }
              }

              else {
                     // Some error code returned by MapQuest
                     String errorMsg = ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO)).get(
                                  SelfServiceConstants.MESSAGES).toString();
                     logError("\n StoreTools.getStoreDetailsByLatLng Request sent to MapQuest web service call is "
                                  + pRequestString
                                  + "\n StoreTools.getStoreDetailsByLatLng Result String returned from MapQuest web service call is "
                                  + pJsonResultString
                                  + "\n StoreTools.getStoreDetailsByLatLng Error Message returned in Result String from MapQuest web service call is "
                                  + errorMsg);

                     throw new BBBBusinessException(String.valueOf(statusCode), errorMsg);
              }
              logDebug(logMessage + " Ends here");
              return objStoreDetailsWrapper;
       }

       /*
       * Parse the store timings
       */
       private String storeTimingsParser(String storeTimings) {
    	      final String logMessage = getClass().getName()
   				+ ".storeTimingsParser()";
    	      
    	      String[] arrHours =null;
    	      logDebug(logMessage	+ " Starts here : Parses store timings");
              String storeTime = storeTimings;
              storeTime = storeTime.replace(BBBCoreConstants.MONDAY, "Mon");
              storeTime = storeTime.replace(BBBCoreConstants.TUESDAY, "Tue");
              storeTime = storeTime.replace(BBBCoreConstants.WEDNESDAY, "Wed");
              storeTime = storeTime.replace(BBBCoreConstants.THURSDAY, "Thu");
              storeTime = storeTime.replace(BBBCoreConstants.FRIDAY, "Fri");
              storeTime = storeTime.replace(BBBCoreConstants.SATURDAY, "Sat");
              storeTime = storeTime.replace(BBBCoreConstants.SUNDAY, "Sun");
              
              //start new store hours code
              arrHours = storeTime.split(BBBCoreConstants.COMMA);
              String[] updatedArrHours = new String[5];
              for(int i=0; i<arrHours.length; i++){
            	  for(int j=0 ; j<5; j++){
            		  if(i==j){
            			  updatedArrHours[j]=  arrHours[i];
            		  }
            	  }
              }
              
              StringBuilder updateHoursString = new StringBuilder();
              
              for (String storeHourString : updatedArrHours) {
            	  if(storeHourString!=null)
            	  updateHoursString.append(storeHourString).append(',');
              }
              if (updatedArrHours.length != 0)
            	  updateHoursString.deleteCharAt(updateHoursString.length()-1);
              storeTime =  updateHoursString.toString();
              
              //End new store hours code 
              
              
              logDebug(logMessage	+ " Ends here");
              return storeTime;
       }

       /**
       * This method takes max age of cookie from config keys
       * 
        * @return int
       * @throws BBBSystemException
       * @throws BBBBusinessException
       */
       public int getCookieTimeOut() throws BBBSystemException,
                     BBBBusinessException {
    	   final String logMessage = getClass().getName()
      				+ ".getCookieTimeOut()";
       	      logDebug(logMessage	+ " Starts here : Fetches timeout for LatLongCookie from  ConfigKey=CookieLatLng & ConfigType=ThirdPartyURLs");	  
              int configValue = 0;
              List<String> keysList = getCatalogTools().getAllValuesForKey(
                           "ThirdPartyURLs", "CookieLatLng");
              if (!BBBUtility.isListEmpty(keysList)) {
                     configValue = Integer.parseInt(keysList.get(0));
              }
              logDebug(logMessage + " Ends here");
              return configValue;
       }

       /*
       * To get the properties names from JSON result string
       */
       private List<String> getPropertyNames(DynaBean pDynaBean) {
    	   final String logMessage = getClass().getName()
     				+ ".getPropertyNames()";
      	      logDebug(logMessage	+ " Starts here : Fetches property names for JSON result string");	  

              DynaClass dynaClass = pDynaBean.getDynaClass();
              DynaProperty properties[] = dynaClass.getDynaProperties();
              List<String> propertyNames = new ArrayList<String>();
              for (int i = 0; i < properties.length; i++) {
                     String name = properties[i].getName();
                     propertyNames.add(name);
              }
              logDebug(logMessage	+ " Ends here");
              return propertyNames;
       }

       public RouteDetails storeDirections(String pDirectionsString)
                     throws IOException, BBBBusinessException {
              String logMessage = getClass().getName() + ".storeDirections()";
              logDebug(logMessage + " || " + " Starts here : Makes Mapquest call to find out direction for the store");
              logDebug(logMessage + " || " + " Search Criteria String --> "
                           + pDirectionsString);
              RouteDetails objP2PRouteDetails = null;

              String directionString = pDirectionsString;
              directionString = parseSearchString(directionString);
              String routeDirections = null;
              try {
                     routeDirections = getHttpCallInvoker()
                                  .executeQuery(directionString);
              } catch (BBBSystemException e) {
                     logError("\n StoreTools.storeDirections Request sent to MapQuest web service call is "
                                  + directionString);
                     BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_MAPQUEST_1001,
                                  "Exception in calling Mapquest in storeDirections");

              }

              if (!BBBUtility.isEmpty(routeDirections)) {
                     objP2PRouteDetails = p2PJSONObjectParser(routeDirections,
                                  directionString);
              }
              logDebug(logMessage + " || " + " Ends here");
              return objP2PRouteDetails;
       }

       /**
       * @param pJsonString
       * @return p2p
       * @throws BBBBusinessException
       */
       public RouteDetails p2PJSONObjectParser(String pJsonString,
                     String pRequestString) throws BBBBusinessException {

    	   String logMessage = getClass().getName() + ".p2PJSONObjectParser()"; 
    	   logDebug(logMessage + " || " + " Starts here : Makes Mapquest call to find out route for the store");
    	   JSONObject jsonObject = null;
              RouteDetails objRouteDetails = null;
              jsonObject = (JSONObject) JSONSerializer.toJSON(pJsonString);
              DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
              List<String> dynaBeanProperties = getPropertyNames(JSONResultbean);

              int statusCode = 1;
              try {
                     statusCode = dynaBeanProperties.contains(SelfServiceConstants.INFO) ? ((Integer) ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO))
                                  .get(SelfServiceConstants.STATUSCODE)).intValue() : 1;
              } catch (MorphException e) {
                     statusCode = dynaBeanProperties.contains(SelfServiceConstants.INFO) ? ((Integer) ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO)).get("statuscode"))
                                  .intValue() : 1;
              }
              if (statusCode == 0) {
                     if (dynaBeanProperties.contains(SelfServiceConstants.ROUTE)) {
                           MorphDynaBean routeDetails = (MorphDynaBean) JSONResultbean
                                         .get(SelfServiceConstants.ROUTE);
                           if (routeDetails != null) {
                                  objRouteDetails = new RouteDetails();
                                  setRouteDetails(routeDetails, objRouteDetails);
                           }
                     }
              } else {
                     // Some error code returned by MapQuest
                     String errorMsg = ((DynaBean) JSONResultbean
                                  .get(SelfServiceConstants.INFO)).get(
                                  SelfServiceConstants.MESSAGES).toString();

                     logError("\n StoreTools.p2PJSONObjectParser Request sent to MapQuest web service call is "
                                  + pRequestString
                                  + "\n StoreTools.p2PJSONObjectParser Result String returned from MapQuest web service call is "
                                  + pJsonString
                                  + "\n StoreTools.p2PJSONObjectParser Error Message returned in MapQuest web service call is "
                                  + errorMsg);

                     throw new BBBBusinessException(String.valueOf(statusCode), errorMsg);
              }
              logDebug(logMessage + " || " + " Ends here");
              return objRouteDetails;
       }

       /**
       * @param pLegBean
       * @param p2pRouteLegs
       * @return p2p
       */
       public RouteDetails setRouteDetails(MorphDynaBean pRouteBean,
                     RouteDetails p2pRouteDetails) {
    	   String logMessage = getClass().getName() + ".setRouteDetails()"; 
    	   logDebug(logMessage + " || " + " Starts here : Set route details and return RouteDetails");   
    	   List<String> routeProperties = getPropertyNames(pRouteBean);
              Object tempPropetyValue = null;
              if (!BBBUtility.isListEmpty(routeProperties)) {

                     if (routeProperties.contains(SelfServiceConstants.DISTANCE)) {

                           String[] distanceSplit = pRouteBean
                                         .get(SelfServiceConstants.DISTANCE).toString()
                                         .split("\\.");
                           if (distanceSplit.length > 1
                                         && distanceSplit[BBBCoreConstants.ONE].length() > 2) {
                                  p2pRouteDetails
                                                .setTotalDistance(distanceSplit[BBBCoreConstants.ZERO]
                                                              + "."
                                                              + distanceSplit[BBBCoreConstants.ONE]
                                                                           .substring(0, 2));
                           } else {
                                  p2pRouteDetails
                                                .setTotalDistance(distanceSplit[BBBCoreConstants.ZERO]);
                           }

                     }
                     if (routeProperties.contains(SelfServiceConstants.SESSIONID)) {
                           tempPropetyValue = pRouteBean
                                         .get(SelfServiceConstants.SESSIONID);
                           p2pRouteDetails.setSessionId(tempPropetyValue.toString());
                     }

                     if (routeProperties.contains(SelfServiceConstants.FORMATTED_TIME)) {
                           tempPropetyValue = pRouteBean
                                         .get(SelfServiceConstants.FORMATTED_TIME);
                           p2pRouteDetails
                                         .setFormattedTime(convertTime((String) tempPropetyValue));
                     }

                     setRouteExtraDetails(routeProperties, pRouteBean, p2pRouteDetails);
                     // To populate location details in RouteDetails
                     setLocation(pRouteBean, p2pRouteDetails, routeProperties);
                     if (routeProperties.contains(SelfServiceConstants.LEGS)) {
                           List<RouteLegs> alRouteLegs = new ArrayList<RouteLegs>();
                           @SuppressWarnings("unchecked")
                           List<MorphDynaBean> alRoutelegsBean = (ArrayList<MorphDynaBean>) pRouteBean
                                         .get(SelfServiceConstants.LEGS);
                           if (!BBBUtility.isListEmpty(alRoutelegsBean)) {
                                  for (MorphDynaBean legBean : alRoutelegsBean) {
                                         alRouteLegs.add(setLegsList(legBean, new RouteLegs()));
                                  }
                                  p2pRouteDetails.setRouteLegs(alRouteLegs);
                           }
                     }

              }
              logDebug(logMessage + " || " + " Ends here");
              return p2pRouteDetails;
       }

       private void setRouteExtraDetails(List<String> routeProperties,
                     MorphDynaBean pRouteBean, RouteDetails p2pRouteDetails) {
    	   String logMessage = getClass().getName() + ".setRouteExtraDetails()"; 
    	   logDebug(logMessage + " || " + " Starts here : Set route extra details");    
    	   Object tempPropetyValue = null;
              if (routeProperties.contains(SelfServiceConstants.HAS_UNPAVED)) {
                     tempPropetyValue = pRouteBean.get(SelfServiceConstants.HAS_UNPAVED);
                     p2pRouteDetails.setHasUnpaved(((Boolean) tempPropetyValue)
                                  .booleanValue());
              }

              if (routeProperties.contains(SelfServiceConstants.HAS_HIGHWAY)) {
                     tempPropetyValue = pRouteBean.get(SelfServiceConstants.HAS_HIGHWAY);
                     p2pRouteDetails.setHasHighway(((Boolean) tempPropetyValue)
                                  .booleanValue());
              }

              if (routeProperties.contains(SelfServiceConstants.HAS_FERRY)) {
                     tempPropetyValue = pRouteBean.get(SelfServiceConstants.HAS_FERRY);
                     p2pRouteDetails.setHasFerry(((Boolean) tempPropetyValue)
                                  .booleanValue());
              }
              if (routeProperties.contains(SelfServiceConstants.HAS_SEASONAL_CLOSURE)) {
                     tempPropetyValue = pRouteBean
                                  .get(SelfServiceConstants.HAS_SEASONAL_CLOSURE);
                     p2pRouteDetails.setHasSeasonalClosure(((Boolean) tempPropetyValue)
                                  .booleanValue());
              }

              if (routeProperties.contains(SelfServiceConstants.HAS_COUNTRY_CROSS)) {
                     tempPropetyValue = pRouteBean
                                  .get(SelfServiceConstants.HAS_COUNTRY_CROSS);
                     p2pRouteDetails.setHasCountryCross(((Boolean) tempPropetyValue)
                                  .booleanValue());
              }
              if (routeProperties.contains(SelfServiceConstants.HAS_TOLL_ROAD)) {
                     tempPropetyValue = pRouteBean
                                  .get(SelfServiceConstants.HAS_TOLL_ROAD);
                     p2pRouteDetails.setHasTollRoad(((Boolean) tempPropetyValue)
                                  .booleanValue());
              }
              logDebug(logMessage + " || " + " Ends here");
       }

       private void setLocation(MorphDynaBean pRouteBean,
                     RouteDetails p2pRouteDetails, List<String> routeProperties) {
    	   String logMessage = getClass().getName() + ".setRouteExtraDetails()"; 
    	   logDebug(logMessage + " || " + " Starts here : Set route extra details");       
    	   if (routeProperties.contains(SelfServiceConstants.LOCATIONS)) {
                     @SuppressWarnings("unchecked")
                     List<MorphDynaBean> alLocations = (ArrayList<MorphDynaBean>) pRouteBean
                                  .get(SelfServiceConstants.LOCATIONS);
                     if (!BBBUtility.isListEmpty(alLocations)) {

                           MorphDynaBean startPointLoc = alLocations
                                         .get(BBBCoreConstants.ZERO);
                           setRouteStartPoint(p2pRouteDetails, startPointLoc);
                           MorphDynaBean endPointLoc = alLocations
                                         .get(alLocations.size() - 1);
                           setRouteEndPoint(p2pRouteDetails, endPointLoc);
                     }
             }
    	   logDebug(logMessage + " || " + " Ends");
       }

       private void setRouteStartPoint(RouteDetails p2pRouteDetails,
                     MorphDynaBean startPointLoc) {
    	   String logMessage = getClass().getName() + ".setRouteStartPoint()"; 
    	   logDebug(logMessage + " || " + " Starts here : Set RouteStartPoint");  
              List<String> LocProperties = getPropertyNames(startPointLoc);
              if (LocProperties.contains(SelfServiceConstants.LATLNG)) {
                     MorphDynaBean startLatLng = (MorphDynaBean) startPointLoc
                                  .get(SelfServiceConstants.LATLNG);
                     List<String> latlngProperties = getPropertyNames(startLatLng);
                     if (latlngProperties.contains(SelfServiceConstants.LOCATIONLAT)
                                  && latlngProperties
                                                .contains(SelfServiceConstants.LOCATIONLNG)) {
                           p2pRouteDetails.setStartPointLat(startLatLng.get(
                                         SelfServiceConstants.LOCATIONLAT).toString());
                           p2pRouteDetails.setStartPointLng(startLatLng.get(
                                         SelfServiceConstants.LOCATIONLNG).toString());
                     }

              }
              setStartPoint(p2pRouteDetails, startPointLoc, LocProperties);
              logDebug(logMessage + " || " + " Ends");
       }

       private void setStartPoint(RouteDetails p2pRouteDetails,
                     MorphDynaBean startPointLoc, List<String> LocProperties) {
              StringBuilder strStartPoint = new StringBuilder();
              strStartPoint.append(LocProperties
                           .contains(SelfServiceConstants.ADMINAREA5) ? startPointLoc
                           .get(SelfServiceConstants.ADMINAREA5) : "");
              if (LocProperties.contains(SelfServiceConstants.ADMINAREA3)) {
                     strStartPoint
                                  .append(!BBBUtility.isEmpty(strStartPoint.toString()) ? ", "
                                                : " ");
                     strStartPoint.append(startPointLoc
                                  .get(SelfServiceConstants.ADMINAREA3));

              }
              if (LocProperties.contains(SelfServiceConstants.POSTALCODE)) {
                     strStartPoint
                                  .append(!BBBUtility.isEmpty(strStartPoint.toString()) ? " "
                                                : "");
                     strStartPoint.append(startPointLoc
                                  .get(SelfServiceConstants.POSTALCODE));

              }

              p2pRouteDetails.setRouteStartPoint(strStartPoint.toString());
       }

       private void setRouteEndPoint(RouteDetails p2pRouteDetails,
                     MorphDynaBean endPointLoc) {
              List<String> LocProperties = getPropertyNames(endPointLoc);
              LocProperties = getPropertyNames(endPointLoc);
              if (LocProperties.contains(SelfServiceConstants.LATLNG)) {
                     MorphDynaBean endLatLng = (MorphDynaBean) endPointLoc
                                  .get(SelfServiceConstants.LATLNG);
                     List<String> latlngProperties = getPropertyNames(endLatLng);
                     if (latlngProperties.contains(SelfServiceConstants.LOCATIONLAT)
                                  && latlngProperties
                                                .contains(SelfServiceConstants.LOCATIONLNG)) {
                           p2pRouteDetails.setEndPointLat(endLatLng.get(
                                         SelfServiceConstants.LOCATIONLAT).toString());
                           p2pRouteDetails.setEndPointLng(endLatLng.get(
                                         SelfServiceConstants.LOCATIONLNG).toString());
                     }
              }

       }

       private String convertTime(String pTime) {
              StringTokenizer time = new StringTokenizer(pTime, ":");
              StringBuilder strHrsMin = new StringBuilder();
              String hrs = time.nextToken();
              String minute = time.nextToken();
              if (!(hrs.equals("0") || hrs.equals("00"))) {
                     strHrsMin.append(hrs).append(SelfServiceConstants.DRIVINGHOURS);
              }
              if (!(minute.equals("0") || minute.equals("00"))) {
                     strHrsMin.append(!BBBUtility.isEmpty(strHrsMin.toString()) ? ", "
                                  : " ");
                     strHrsMin.append(minute).append(SelfServiceConstants.DRIVINGMINUTE);
              }
              return strHrsMin.toString();
       }

       private void hasTollRoad(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs
                           .setHasTollRoad((legsProperties
                                         .contains(SelfServiceConstants.HAS_TOLL_ROAD)) ? ((Boolean) pLegBean
                                         .get(SelfServiceConstants.HAS_TOLL_ROAD))
                                         .booleanValue() : false);
       }

       private void setIndex(RouteLegs p2pRouteLegs, List<String> legsProperties,
                     MorphDynaBean pLegBean) {
              p2pRouteLegs.setIndex((legsProperties
                            .contains(SelfServiceConstants.INDEX)) ? pLegBean.get(
                           SelfServiceConstants.INDEX).toString() : "");
       }

       private void setLegDistance(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs.setLegDistance(legsProperties
                           .contains(SelfServiceConstants.DISTANCE) ? pLegBean.get(
                           SelfServiceConstants.DISTANCE).toString() : "");
       }

       private void setHasSeasonalClosure(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs
                           .setHasSeasonalClosure(legsProperties
                                         .contains(SelfServiceConstants.HAS_SEASONAL_CLOSURE) ? ((Boolean) pLegBean
                                         .get(SelfServiceConstants.HAS_SEASONAL_CLOSURE))
                                         .booleanValue() : false);
       }

       private void setHasCountryCross(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs
                           .setHasCountryCross(legsProperties
                                         .contains(SelfServiceConstants.HAS_COUNTRY_CROSS) ? ((Boolean) pLegBean
                                         .get(SelfServiceConstants.HAS_COUNTRY_CROSS))
                                         .booleanValue() : false);
       }

       private void setFormattedTime(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs.setFormattedTime(legsProperties
                           .contains(SelfServiceConstants.FORMATTED_TIME) ? pLegBean.get(
                           SelfServiceConstants.FORMATTED_TIME).toString() : "");
       }

       private void setHasUnpaved(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs
                           .setHasUnpaved(legsProperties
                                         .contains(SelfServiceConstants.HAS_UNPAVED) ? ((Boolean) pLegBean
                                         .get(SelfServiceConstants.HAS_UNPAVED)).booleanValue()
                                         : false);
       }

       private void setHasHighway(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs
                           .setHasHighway(legsProperties
                                         .contains(SelfServiceConstants.HAS_HIGHWAY) ? ((Boolean) pLegBean
                                         .get(SelfServiceConstants.HAS_HIGHWAY)).booleanValue()
                                         : false);

       }

       private void setHasFerry(RouteLegs p2pRouteLegs,
                     List<String> legsProperties, MorphDynaBean pLegBean) {
              p2pRouteLegs.setHasFerry(legsProperties
                           .contains(SelfServiceConstants.HAS_FERRY) ? ((Boolean) pLegBean
                           .get(SelfServiceConstants.HAS_FERRY)).booleanValue() : false);
       }

       /**
       * @param pLegBean
       * @param p2pRouteLegs
       * @return p2p
       */
       public RouteLegs setLegsList(MorphDynaBean pLegBean, RouteLegs p2pRouteLegs) {
              List<String> legsProperties = getPropertyNames(pLegBean);

                     hasTollRoad(p2pRouteLegs, legsProperties, pLegBean);
                     setIndex(p2pRouteLegs, legsProperties, pLegBean);
                     setLegDistance(p2pRouteLegs, legsProperties, pLegBean);
                     setHasSeasonalClosure(p2pRouteLegs, legsProperties, pLegBean);
                     setHasCountryCross(p2pRouteLegs, legsProperties, pLegBean);
                     setFormattedTime(p2pRouteLegs, legsProperties, pLegBean);
                     setHasUnpaved(p2pRouteLegs, legsProperties, pLegBean);
                     setHasHighway(p2pRouteLegs, legsProperties, pLegBean);
                     setHasFerry(p2pRouteLegs, legsProperties, pLegBean);

                     if (legsProperties.contains(SelfServiceConstants.MANEUVERS)) {
                           @SuppressWarnings("unchecked")
                           List<Maneuvers> maneuversList = setManeuvers((ArrayList<MorphDynaBean>) pLegBean
                                         .get(SelfServiceConstants.MANEUVERS));
                           p2pRouteLegs.setManeuvers(maneuversList);
                     }

              return p2pRouteLegs;
       }

       /**
       * @param pList
       * @return
       */
       public List<Maneuvers> setManeuvers(List<MorphDynaBean> pList) {
              List<Maneuvers> maneuversList = null;
              if (pList != null) {
                     Iterator<MorphDynaBean> maneuList = pList.iterator();
                     maneuversList = new ArrayList<Maneuvers>();
                     Maneuvers maneuObj = null;
                     while (maneuList.hasNext()) {
                           maneuObj = new Maneuvers();
                           MorphDynaBean bean = maneuList.next();
                           List<String> maneuversProperties = getPropertyNames(bean);
                           if (!BBBUtility.isListEmpty(maneuversProperties)) {

                                  setManeuversValues(maneuversProperties, bean, maneuObj);
                                  if (maneuversProperties
                                                .contains(SelfServiceConstants.TRANSPORTMODE)) {
                                         maneuObj.setTransportMode((String) bean
                                                       .get(SelfServiceConstants.TRANSPORTMODE));
                                  }

                                  if (maneuversProperties
                                                .contains(SelfServiceConstants.INDEX)) {
                                         maneuObj.setIndex((Integer) bean
                                                       .get(SelfServiceConstants.INDEX));
                                  }

                                  if (maneuversProperties
                                                .contains(SelfServiceConstants.DIRECTION)) {
                                         maneuObj.setDirection((Integer) bean
                                                       .get(SelfServiceConstants.DIRECTION));
                                  }

                                  if (maneuversProperties
                                                .contains(SelfServiceConstants.NARRATIVE)) {
                                         maneuObj.setNarrative((String) bean
                                                       .get(SelfServiceConstants.NARRATIVE));
                                  }

                                  if (maneuversProperties
                                                .contains(SelfServiceConstants.DISTANCE)) {
                                         String[] distanceSplit = bean
                                                       .get(SelfServiceConstants.DISTANCE).toString()
                                                       .split("\\.");
                                         if (distanceSplit.length > 1
                                                       && distanceSplit[BBBCoreConstants.ONE].length() > 2) {
                                                maneuObj.setDistance(distanceSplit[BBBCoreConstants.ZERO]
                                                              + "."
                                                              + distanceSplit[BBBCoreConstants.ONE]
                                                                           .substring(0, 2));
                                         } else {
                                                maneuObj.setDistance(distanceSplit[BBBCoreConstants.ZERO]);
                                         }

                                  }

                                  maneuversList.add(maneuObj);

                           }
                     }
              }
              return maneuversList;
       }

       private void setManeuversValues(List<String> maneuversProperties,
                     MorphDynaBean bean, Maneuvers maneuObj) {

              if (maneuversProperties.contains(SelfServiceConstants.ICONURL)) {
                     maneuObj.setIconUrl((String) bean.get(SelfServiceConstants.ICONURL));
              }
              if (maneuversProperties.contains(SelfServiceConstants.ATTRIBUTES)) {
                     maneuObj.setAttributes((Integer) bean
                                   .get(SelfServiceConstants.ATTRIBUTES));
              }
              if (maneuversProperties.contains(SelfServiceConstants.MAPURL)) {
                     String url = (String) bean.get(SelfServiceConstants.MAPURL);
                     if (BBBUtility.isNotEmpty(url) && url.contains("http:")) {
                           url = url.substring(5);
                     }
                     maneuObj.setMapUrl(url);
              }

              if (maneuversProperties.contains(SelfServiceConstants.START_POINT)) {
                     maneuObj.setStartPoint((MorphDynaBean) bean
                                  .get(SelfServiceConstants.START_POINT));
              }

              if (maneuversProperties.contains(SelfServiceConstants.FORMATTED_TIME)) {
                     maneuObj.setFormattedTime((String) bean
                                  .get(SelfServiceConstants.FORMATTED_TIME));
              }
              if (maneuversProperties.contains(SelfServiceConstants.TIME)) {
                     maneuObj.setTime((Integer) bean.get(SelfServiceConstants.TIME));
              }
              if (maneuversProperties.contains(SelfServiceConstants.DIRECTION_NAME)) {
                     maneuObj.setDirectionName((String) bean
                                  .get(SelfServiceConstants.DIRECTION_NAME));
              }
       }

       private String parseSearchString(String pSearchString)
                     throws UnsupportedEncodingException {
              String searchString = pSearchString;
              if (!BBBUtility.isEmpty(pSearchString)) {

                     if (pSearchString.contains("|")) {
                           String pipe = URLEncoder.encode("|", "UTF-8");
                           searchString = pSearchString.replace("|", pipe);
                     }
                     if (pSearchString.contains(" ")) {
                           String space = URLEncoder.encode(" ", "UTF-8");
                           searchString = pSearchString.replaceAll(" ", space);
                     }
              }
              return searchString;
       }

       /**
       * This method fetch store details from db
       * 
        * @param latitude
       * @param longitude
       * @param storeType
       * @return StoreDetailsWrapper
       * @throws BBBBusinessException
       * @throws BBBSystemException
       */
       public StoreDetailsWrapper getStoreDetails(String latitude,
                     String longitude, String storeType) throws BBBBusinessException,
                     BBBSystemException {
    	   	  String logMessage = getClass().getName() + ".getStoreDetails()"; 
    	      logDebug(logMessage + " || " + " Starts here : Fetches Store Details from DB using parameter : longitude=" + longitude + ",latitude=" + latitude + "storeType=" + storeType); 
              BBBPerformanceMonitor.start(OPERATION_NAME, METHOD_NAME);
              logDebug("Getting Store Details From Db");
              double radius = BBBCoreConstants.DOUBLE_TWENTY_FIVE;
              String distanceUnit = SelfServiceConstants.MILES;
              double distance = BBBCoreConstants.DOUBLE_ZERO;
              int recordNo = BBBCoreConstants.ZERO;

              StoreDetailsWrapper objStoreDetailsWrapper = null;
              if (ServletUtil.getCurrentRequest().getSession()
                           .getAttribute(SelfServiceConstants.RADIUSMILES) != null && !BBBUtility.isEmpty(ServletUtil.getCurrentRequest().getSession()
                           .getAttribute(SelfServiceConstants.RADIUSMILES).toString())) {
            	  try{
                     radius = Double.parseDouble(ServletUtil.getCurrentRequest()
                                  .getSession()
                                  .getAttribute(SelfServiceConstants.RADIUSMILES).toString());
            	  }
            	  catch(NumberFormatException e){
            		  logError("NumberFormatException occurred while getting radius" ,e);
            		  radius = BBBCoreConstants.DOUBLE_TWENTY_FIVE;
            	  }
              }

              if (BBBCoreConstants.SITE_BAB_CA.equals(getCurrentSiteId()))
              radius*=0.621371;
              
              if (latitude != null && longitude != null) {
                     MutableRepository storeRepository = ((BBBCatalogToolsImpl) getCatalogTools())
                                  .getStoreRepository();

                     try {
                           List<StoreDetails> alStoreDetails = new ArrayList<StoreDetails>();
                           RepositoryView storeView = storeRepository
                                         .getView(SelfServiceConstants.STORE);
                           Object[] params = null;
                           RqlStatement statement = null;
                                  statement = parseRqlStatement("displayOnline = ?0 AND storeType =?1");
                                  params = new Object[2];
                                  params[0] = BBBCoreConstants.ONE;
                                  params[1] = storeType;

                           RegularExpression objRegEx = new RegularExpression(
                                         getRegularExpression());
                           RepositoryItem[] repositoryItems = statement.executeQuery(
                                         storeView, params);
                           if (repositoryItems != null && repositoryItems.length > 0) {
                                  for (int iCount = 0; iCount < repositoryItems.length; iCount++) {
                                         String storeId = repositoryItems[iCount]
                                                       .getRepositoryId();
                                         if (objRegEx.matches(storeId)) {
                                        	 Object latitudeRepoValue = repositoryItems[iCount].getPropertyValue(SelfServiceConstants.LATITUDE);
                                        	 Object longitudeRepoValue = repositoryItems[iCount].getPropertyValue(SelfServiceConstants.LONGITUDE);

                                                if (latitudeRepoValue != null && longitudeRepoValue != null) {

                                                       // calculate distance based on latitude ,
                                                       // longitude
                                                       /*distance = calculateDistance(distance,
                                                                     repositoryItems[iCount], latitude,
                                                                     longitude);*/
                                                	distance = calculateDistance((String) latitudeRepoValue, (String) longitudeRepoValue, latitude, longitude);

                                                       if (distance <= radius) {
                                                              Double toBeTruncated = new Double(distance);
                                                              Double truncatedDistance = new BigDecimal(
                                                                           toBeTruncated).setScale(4,
                                                                           BigDecimal.ROUND_HALF_UP)
                                                                           .doubleValue();
                                                              String distanceFinal = Double
                                                                           .toString(truncatedDistance);
                                                              if (BBBCoreConstants.SITE_BAB_CA
                                                                           .equals(getCurrentSiteId())) {
                                                                     double distanceCa = distance * 1.609344;
                                                                     Double toBeTruncatedCa = new Double(
                                                                                  distanceCa);
                                                                     distanceFinal = Double
                                                                                  .toString(new BigDecimal(
                                                                                                toBeTruncatedCa)
                                                                                                .setScale(
                                                                                                              BBBCoreConstants.FOUR,
                                                                                                              BigDecimal.ROUND_HALF_UP)
                                                                                                .doubleValue());
                                                                     distanceUnit = SelfServiceConstants.KILO_METERS;
                                                              }
                                                              // adding store details to list
                                                              alStoreDetails = addStoreDetailsToList(
                                                                           repositoryItems[iCount],
                                                                           alStoreDetails, distanceFinal,
                                                                           distanceUnit, storeId,
                                                                           storeRepository,
                                                                           String.valueOf(recordNo++));
                                                       }
                                                }
                                         }
                                  }
                                  // sort result on basis of distance
                                  Collections.sort(alStoreDetails,
                                                new Comparator<StoreDetails>() {
                                                       public int compare(StoreDetails c1,
                                                                     StoreDetails c2) {
                                                              return Double.compare(Double.parseDouble(c1
                                                                           .getDistance()), Double
                                                                            .parseDouble(c2.getDistance()));
                                                       }
                                                });
                           }
                           // objStoreDetailsWrapper.setStoreDetails(alStoreDetails);
                           int totalPageCount = 0;

                           if (repositoryItems != null) {
                                  totalPageCount = alStoreDetails.size();
                           }
                           objStoreDetailsWrapper = new StoreDetailsWrapper(1,
                                         totalPageCount, null, alStoreDetails);

                     } catch (Exception e) {
                           throw new BBBBusinessException(e.getMessage());
                     }

                     BBBPerformanceMonitor.end(OPERATION_NAME, METHOD_NAME);
              }
              logDebug(logMessage + " || " + " Ends Here");
              return objStoreDetailsWrapper;
       }

	/**
	 * @return
	 * @throws RepositoryException
	 */
	protected RqlStatement parseRqlStatement(String query) throws RepositoryException {
		return RqlStatement
		                .parseRqlStatement(query);
	}

	/**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

       /**
       * This method fetches specialityShopCd from db
       * 
        * @param storeRepository
       * @param repositoryItem	
       * @return String
       * @throws BBBBusinessException
       */
	protected String getSpecialityShopCd(MutableRepository storeRepository,
                     RepositoryItem repositoryItem) throws BBBBusinessException {
    	String logMessage = getClass().getName() + ".getSpecialityShopCd()"; 
 	    logDebug(logMessage + " || " + " Starts here : Fetches speciality Shop Code from DB"); 
		String specialityShopCd = BBBCoreConstants.BLANK;
		try {
			if (repositoryItem
					.getPropertyValue(SelfServiceConstants.SPECIALITY_CODE_ID) != null
					&& ((Set) (repositoryItem
							.getPropertyValue(SelfServiceConstants.SPECIALITY_CODE_ID)))
							.size() > 0) {
				{
					String specialityCodeId = ((RepositoryItem) ((Set) (repositoryItem
							.getPropertyValue(SelfServiceConstants.SPECIALITY_CODE_ID)))
							.iterator().next()).getRepositoryId();

					RepositoryView storeCodeNameView = storeRepository
							.getView(SelfServiceConstants.SPECIALITY_CODE);

					RqlStatement rqlStatementForSpecCodeName = parseRqlStatement("id = ?0");

					Object[] paramsForSpecialCodeName = new Object[1];
					paramsForSpecialCodeName[0] = specialityCodeId;

					RepositoryItem[] repItemsForSpecialCodeName = rqlStatementForSpecCodeName
							.executeQuery(storeCodeNameView,
									paramsForSpecialCodeName);
					if (null != repItemsForSpecialCodeName
							&& repItemsForSpecialCodeName.length > 0) {
						String specialityCodeName = repItemsForSpecialCodeName[0]
								.getPropertyValue(
										SelfServiceConstants.SPECIALITY_CODE_NAME)
								.toString();

						RepositoryView storeCodeView = storeRepository
								.getView(SelfServiceConstants.SPECIALITY_CODE_MAP);

						RqlStatement rqlStatementForSpecCode = parseRqlStatement("specialityCdName = ?0");

						Object[] paramsForSpecialCode = new Object[1];
						paramsForSpecialCode[0] = specialityCodeName;
						RepositoryItem[] repItemsForSpecialCode;

						repItemsForSpecialCode = rqlStatementForSpecCode
								.executeQuery(storeCodeView,
										paramsForSpecialCode);

						if (null != repItemsForSpecialCode
								&& repItemsForSpecialCode.length > 0) {
							specialityShopCd = repItemsForSpecialCode[0]
									.getPropertyValue(
											SelfServiceConstants.SPECIALITY_SHOP_CD)
									.toString();
						}
					}
				}
			}
		} catch (Exception e) {
			logError("Exception occurred while getting speciality code cd " ,e);
			throw new BBBBusinessException(e.getMessage());
		}
		logDebug(logMessage + " || " + " Ends Here");
		return specialityShopCd;
	}

       /**
       * This method calculates distance in miles based on lat/lng
       * 
        * @param distance
       * @param repositoryItem
       * @param latitude
       * @param longitude
       * @return double
       */
       private double calculateDistance(String latitudeRepoValue, String longitudeRepoValue, String latitude, String longitude) {
    	   String logMessage = getClass().getName() + ".calculateDistance()"; 
    	   logDebug(logMessage + " || " + "return distance in miles based on lat/lng");    
    	   return  Math
                           .sqrt(Math.pow(
                                         ((Double.parseDouble(latitudeRepoValue) - (Double
                                                       .parseDouble(latitude))) * 69.17), 2)
                                         + Math.pow(
                                                       (((Double.parseDouble(longitudeRepoValue) - (Double
                                                                     .parseDouble(longitude))) * 69.17) * Math
                                                                     .cos(40.463011 * 3.14159 / 180)), 2));
       }

       /**
       * This method adds store details to list
       * 
        * @param repositoryItem
       * @param alStoreDetails
       * @param distanceFinal
       * @param distanceUnit
       * @param storeId
       * @param storeRepository
       * @return List<StoreDetails>
       * @throws BBBBusinessException
       */
       public List<StoreDetails> addStoreDetailsToList(
                     RepositoryItem repositoryItem, List<StoreDetails> alStoreDetails,
                     String distanceFinal, String distanceUnit, String storeId,
                     MutableRepository storeRepository, String recordNo)
                     throws BBBBusinessException {
    	   String logMessage = getClass().getName() + ".addStoreDetailsToList()"; 
    	   logDebug(logMessage + " || " + "Starts Here. Method adds store details to list");
    	   String[] arrHours = null;
              String pWeekdaysStoreTimings = null;
              String pSatStoreTimings = null;
              String pSunStoreTimings = null;
              String pOtherTimings1 = null;
              String pOtherTimings2 = null;
              String state = "";

				if (BBBCoreConstants.SITE_BAB_CA.equals(getCurrentSiteId())
						|| BBBCoreConstants.SITEBAB_CA_TBS.equals(getCurrentSiteId())) {
					state = repositoryItem.getPropertyValue(SelfServiceConstants.PROVINCE) != null
							? repositoryItem.getPropertyValue(SelfServiceConstants.PROVINCE).toString() : "";
				} else {
					state = repositoryItem.getPropertyValue(SelfServiceConstants.STATE_LOWER_CASE) != null
							? repositoryItem.getPropertyValue(SelfServiceConstants.STATE_LOWER_CASE).toString() : "";
				}
              if(isLoggingDebug())
              {
            	  logDebug("Distance of Store "+storeId+" is "+distanceFinal);
              }
              if (repositoryItem.getPropertyValue(SelfServiceConstants.HOURS) != null) {
            	  String hours = repositoryItem
                          .getPropertyValue(SelfServiceConstants.HOURS).toString();
            	  hours = storeTimingsParser(hours);
                     arrHours = hours.split(BBBCoreConstants.COMMA);
                     if (arrHours.length == 1)
                           pWeekdaysStoreTimings = arrHours[0];
                     else if (arrHours.length == 2) {
                           pWeekdaysStoreTimings = arrHours[0];
                           pSatStoreTimings = arrHours[1];
                     } else if (arrHours.length == 3) {
                           pWeekdaysStoreTimings = arrHours[0];
                           pSatStoreTimings = arrHours[1];
                           pSunStoreTimings = arrHours[2];
                     } else if (arrHours.length == 4) {
                           pWeekdaysStoreTimings = arrHours[0];
                           pSatStoreTimings = arrHours[1];
                           pSunStoreTimings = arrHours[2];
                           pOtherTimings1 = arrHours[3];
                     } else if (arrHours.length == 5) {
                           pWeekdaysStoreTimings = arrHours[0];
                           pSatStoreTimings = arrHours[1];
                           pSunStoreTimings = arrHours[2];
                           pOtherTimings1 = arrHours[3];
                           pOtherTimings2 = arrHours[4];
                     }

              }

              // adding favorite store at first index
             
              if (ServletUtil.getCurrentRequest() != null && ServletUtil.getCurrentRequest().getAttribute(
                           BBBCoreConstants.FAVOURITE_STORE_ID) != null && !ServletUtil.getCurrentRequest().getAttribute(
                                         BBBCoreConstants.FAVOURITE_STORE_ID).equals("null") && ServletUtil.getCurrentRequest()
                                         .getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID)
                                         .toString().equals(storeId)) {
                           alStoreDetails
                                         .add(0,
                                                       new StoreDetails(
                                                                     storeId,
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.STORE_NAME) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.STORE_NAME)
                                                                                  .toString()
                                                                                  : "",
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.SPECIAL_MSG_PROPERTY) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.SPECIAL_MSG_PROPERTY)
                                                                                  .toString()
                                                                                  : "",
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.ADDRESS_LOWER_CASE) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.ADDRESS_LOWER_CASE)
                                                                                  .toString()
                                                                                  : "",
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.CITY_LOWER_CASE) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.CITY_LOWER_CASE)
                                                                                  .toString()
                                                                                  : "",
                                                                                  state,
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.COUNTRY_CODE) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.COUNTRY_CODE)
                                                                                  .toString()
                                                                                  : "",
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.POSTALCODE) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.POSTALCODE)
                                                                                  .toString()
                                                                                  : "",
                                                                     pSatStoreTimings,
                                                                     pSunStoreTimings,
                                                                     pWeekdaysStoreTimings,
                                                                     pOtherTimings1,
                                                                     pOtherTimings2,
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.PHONE_LOWER_CASE) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.PHONE_LOWER_CASE)
                                                                                  .toString()
                                                                                  : "",
                                                                     "",
                                                                     distanceFinal,
                                                                     distanceUnit,
                                                                     recordNo,
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.LONGITUDE) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.LONGITUDE)
                                                                                  .toString()
                                                                                  : "",
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.LATITUDE) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.LATITUDE)
                                                                                  .toString()
                                                                                  : "",
                                                                     getSpecialityShopCd(storeRepository,
                                                                                  repositoryItem),
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.STORE_TYPE_PROPERTY) != null ? repositoryItem
                                                                                  .getPropertyValue(
                                                                                                SelfServiceConstants.STORE_TYPE_PROPERTY)
                                                                                  .toString()
                                                                                  : "",
                                                                     repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.CONTACT_FLAG) != null ? (Boolean) repositoryItem
                                                                                  .getPropertyValue(SelfServiceConstants.CONTACT_FLAG)
                                                                                  : false));
                     
              } else {
                     alStoreDetails
                                  .add(new StoreDetails(
                                                storeId,
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.STORE_NAME) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.STORE_NAME)
                                                              .toString() : "",
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.SPECIAL_MSG_PROPERTY) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.SPECIAL_MSG_PROPERTY)
                                                              .toString()
                                                              : "",
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.ADDRESS_LOWER_CASE) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.ADDRESS_LOWER_CASE)
                                                              .toString()
                                                              : "",
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.CITY_LOWER_CASE) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.CITY_LOWER_CASE)
                                                              .toString()
                                                              : "",
                                                              state,
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.COUNTRY_CODE) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.COUNTRY_CODE)
                                                              .toString() : "",
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.POSTALCODE) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.POSTALCODE)
                                                              .toString() : "",
                                                pSatStoreTimings,
                                                pSunStoreTimings,
                                                pWeekdaysStoreTimings,
                                                pOtherTimings1,
                                                pOtherTimings2,
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.PHONE_LOWER_CASE) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.PHONE_LOWER_CASE)
                                                              .toString()
                                                              : "",
                                                "",
                                                distanceFinal,
                                                distanceUnit,
                                                recordNo,
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.LONGITUDE) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                           SelfServiceConstants.LONGITUDE)
                                                              .toString() : "",
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.LATITUDE) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                           SelfServiceConstants.LATITUDE)
                                                              .toString() : "",
                                                getSpecialityShopCd(storeRepository, repositoryItem),
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.STORE_TYPE_PROPERTY) != null ? repositoryItem
                                                              .getPropertyValue(
                                                                            SelfServiceConstants.STORE_TYPE_PROPERTY)
                                                              .toString()
                                                              : "",
                                                repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.CONTACT_FLAG) != null ? (Boolean) repositoryItem
                                                              .getPropertyValue(SelfServiceConstants.CONTACT_FLAG)
                                                              : false));
                     
              }
              logDebug("No Of stores Returned from db " + alStoreDetails.size());
              logDebug(logMessage + " || " + " Ends Here");
              return alStoreDetails;
       }

       /**
       * This method gets stores by store id of user's favorite store
       * 
        * @param StoreId
       * @param storeType
       * @return StoreDetailsWrapper
       * @throws BBBBusinessException
       */
       public StoreDetailsWrapper getStoresByStoreId(String storeId,
    		   String storeType , boolean isFromPDP) throws BBBBusinessException {
    	   String logMessage = getClass().getName() + ".getStoresByStoreId()"; 
    	   logDebug(logMessage + " || " + "Starts Here. Method gets stores by store id of user's favorite store. Parmaeters : storeId="+ storeId + ",storeType=" + storeType + ",IsformPDPFalg=" + isFromPDP);
    	   StoreDetailsWrapper objStoreDetailsWrapper = null;
    	   try {
    		   RepositoryItem[] repositoryItem = getStores(storeId, storeType);
    		   if(repositoryItem != null){
    			   if(isFromPDP)
    			   {
    				   //getting nearest favorite store
    				   objStoreDetailsWrapper = getStoreDetails(repositoryItem[0]
    						   .getPropertyValue(SelfServiceConstants.LATITUDE).toString(), repositoryItem[0]
    								   .getPropertyValue(SelfServiceConstants.LONGITUDE).toString(), storeType);
    			   }
    			   else
    			   {
    				   //getting favorite store
    				   MutableRepository storeRepository = ((BBBCatalogToolsImpl) getCatalogTools())
    						   .getStoreRepository();

    				   List<StoreDetails> alStoreDetails = new ArrayList<StoreDetails>();
    				   String distanceUnit = SelfServiceConstants.MILES;
    				   if (BBBCoreConstants.SITE_BAB_CA
    						   .equals(getCurrentSiteId())) {
    					   distanceUnit = SelfServiceConstants.KILO_METERS;
    				   }
    				   alStoreDetails = addStoreDetailsToList(repositoryItem[0], alStoreDetails, BBBCoreConstants.STRING_ZERO, distanceUnit, storeId, storeRepository, BBBCoreConstants.STRING_ZERO);
    				   objStoreDetailsWrapper = new StoreDetailsWrapper(1,
    						   BBBCoreConstants.ZERO, null, alStoreDetails);
    			   }
    		   }
    	   } catch (Exception e) {
    		   throw new BBBBusinessException(e.getMessage());
    	   }
    	   logDebug(logMessage + " || " + " Ends Here");
    	   return objStoreDetailsWrapper;
       }

       /**
       * This method returns stores from store repository for the store id passed
       * 
        * @param storeId
       * @param storeType
       * @return
       * @throws BBBBusinessException
       */
       public RepositoryItem[] getStores(String storeId, String storeType)
                     throws BBBBusinessException {
    	   String logMessage = getClass().getName() + ".getStores()"; 
    	   logDebug(logMessage + " || " + "Starts Here. method returns stores from store repository for the store id passed. Parameters : storeId="+ storeId + ",storeType=" + storeType);   
    	   RqlStatement statement = null;
              Object[] params = null;
              RepositoryItem[] repositoryItem = null;
              try {
                     MutableRepository storeRepository = ((BBBCatalogToolsImpl) getCatalogTools())
                                  .getStoreRepository();
                     RepositoryView storeView = storeRepository.getView(SelfServiceConstants.STORE);
                           statement =parseRqlStatement("id = ?0 AND storeType=?1");
                           params = new Object[2];
                           params[0] = storeId;
                           params[1] = storeType;
                     repositoryItem = statement.executeQuery(storeView, params);
              } catch (RepositoryException e) {
                     throw new BBBBusinessException(e.getMessage());
              }
              logDebug(logMessage + " || " + " Ends Here");
              return repositoryItem;
       }
       
       
       /**This method gets inventory for one sku/store
     * @param skuId
     * @param storeId
     * @return
     * @throws Exception
     */
    public LocalStoreVO getInventoryForStoreFromDb(String skuId , String storeId) throws Exception
       {
    	   String logMessage = getClass().getName() + ".getInventoryForStoreFromDb()"; 
    	   logDebug(logMessage + " || " + "Starts Here. Method gets inventory for one sku/store. Parameters : storeId="+ storeId + ",skuId=" + skuId);   
    	   LocalStoreVO localStoreVO = null;
    	   MutableRepository localRepository = getLocalStoreRepository();

    	   RepositoryView storeLocalView = localRepository.getView(BBBCoreConstants.STORE_LOCAL_INVENTORY);
    	   Object[] params = null;
    	   RqlStatement statement = null;
    	   statement = parseRqlStatement("storeId = ?0 AND skuId =?1");
    	   params = new Object[2];
    	   params[0] = storeId;
    	   params[1] = skuId;

    	   RepositoryItem[] repositoryItems = statement.executeQuery(
    			   storeLocalView, params);
    	   if(repositoryItems != null && repositoryItems.length > 0 &&
    			   repositoryItems[0].getPropertyValue(BBBCoreConstants.STOCK_LEVEL) != null)
    	   {
    		   localStoreVO = new LocalStoreVO();
    		   localStoreVO.setStockLevel(Integer.parseInt(repositoryItems[0].getPropertyValue(BBBCoreConstants.STOCK_LEVEL).toString()));
    	   }
    	   logDebug(logMessage + " || " + " Ends Here");
		return localStoreVO;
       }
       
       /**This method queries db to get stock level of store/sku
     * @param lStoreDetails
     * @param skuId
     * @param productQty
     * @param skuThresholdVO
     * @return
     * @throws Exception
     */
    public StoreDetails getInventoryFromDb(List<StoreDetails> lStoreDetails , String skuId , long productQty, ThresholdVO skuThresholdVO , List<String> bopusInEligibleStore) throws Exception
    {
    	BBBPerformanceMonitor.start(
    			BBBPerformanceConstants.LOCAL_INVENTORY_DB_CALL+" getInventoryFromDb");
    	String logMessage = getClass().getName() + ".getInventoryFromDb()";
    	logDebug(logMessage + "Starts here. method queries db to get stock level of store/sku. Parameters : skuId=" + skuId + ", productQty=" + productQty);
    	Connection connection = null;
    	int storeCount = 1;
    	StoreDetails storeDetails = null;
    	ResultSet rs = null;
    	PreparedStatement preparedStatement = null;
    	if(!BBBUtility.isListEmpty(lStoreDetails))
    	{
    		try
    		{
    			connection = ((GSARepository) getLocalStoreRepository()).getDataSource().getConnection();
    			String sql = "SELECT * FROM BBB_STORE_LOCAL_INVENTORY WHERE STORE_ID"+getInClause(lStoreDetails)+"and SKU_ID = ? ";		
    			preparedStatement = connection.prepareStatement(sql);	

    			//prepared statement for store Ids
    			for (final StoreDetails stDetails : lStoreDetails) {
    				preparedStatement.setString(storeCount , stDetails.getStoreId());
    		    	logDebug("List of Stores passed to Db "+stDetails.getStoreId()+BBBCoreConstants.COMMA);
    				storeCount++;
    			}
    			preparedStatement.setString(storeCount, skuId);
    			rs = executeQuery(preparedStatement);

    			while(rs.next())
    			{
    				String storeId = rs.getString(BBBCoreConstants.STORE_ID_COLUMN);
    				if ((!BBBUtility.isListEmpty(bopusInEligibleStore)
    						&& !bopusInEligibleStore.contains(storeId)) || BBBUtility.isListEmpty(bopusInEligibleStore))
    				{
    					int inventory = rs.getInt(BBBCoreConstants.STOCK_LEVEL_COLUMN);
    					logDebug("Inventory returned from Db for store "+storeId+" is "+inventory);
    					int inventoryStatus = (((BBBInventoryManagerImpl) getInventoryManager())
    							.getInventoryStatus(inventory, productQty, skuThresholdVO , storeId));
    					if (inventoryStatus == BBBInventoryManager.AVAILABLE || inventoryStatus == BBBInventoryManager.LIMITED_STOCK) {
    						getCacheContainer().put(storeId + BBBCoreConstants.HYPHEN + skuId, inventory , BBBCoreConstants.CACHE_STORE_INV);
    						for (StoreDetails stDetails : lStoreDetails) {
    							if(stDetails.getStoreId().equals(storeId))
    							{
    								storeDetails = stDetails;
    								break;
    							}
    						}

    						break;
    					}
    				}
    			}
    		}

    		catch(Exception ex)
    		{
    			BBBPerformanceMonitor.end(
    					BBBPerformanceConstants.LOCAL_INVENTORY_DB_CALL+" getInventoryFromDb");
    			throw ex;
    		}
    		finally
    		{
    			if(rs != null)
    			{
    				rs.close();
    			}
    			if(preparedStatement != null)
    			{
    				preparedStatement.close();
    			}
    			if(connection != null)
    			{
    				connection.close();
    			}
    			BBBPerformanceMonitor.end(
    					BBBPerformanceConstants.LOCAL_INVENTORY_DB_CALL+" getInventoryFromDb");
    			logDebug(logMessage + " || " + " Ends Here");
    		}
    	}
    	return storeDetails;
    }

	/**
	 * @param preparedStatement
	 * @return
	 * @throws SQLException
	 */
	protected ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException {
		return preparedStatement.executeQuery();
	}
    
    /**Get Inclause for store Ids
     * @param lStoreDetails
     * @return
     */
    private String getInClause(List<StoreDetails> lStoreDetails)
    {
    	int inClauseCount = 0;
    	StringBuffer inClause = new StringBuffer();
    	inClause.append(" IN(");
    	for (final StoreDetails stDetails : lStoreDetails) {
    		inClause.append(BBBCoreConstants.QUESTION_MARK);
    		if(inClauseCount < (lStoreDetails.size())-1)
    		{
    			inClause.append(BBBCoreConstants.COMMA);
    		}
    		inClauseCount++;
    	}
    	inClause.append(") ");
    	
		return inClause.toString();
    }

       /**This method gets the inventory for local store from coherence
     * @param storeId
     * @param skuId
     * @return
     * @throws RepositoryException
     */
       public LocalStoreVO getInventoryFromLocalStore(String storeId, String skuId)
   			throws Exception {
    	String logMessage = getClass().getName() + ".getInventoryFromLocalStore()";
       	logDebug(logMessage + "Starts here. method gets the inventory for local store from coherence. Parameters : skuId=" + skuId + ", storeId=" + storeId);
   		LocalStoreVO localStoreVo = null;
   		try {
   			logDebug("StoreTools | getInventoryFromLocalStore | Starts");

			  if (null != getCacheContainer().get(storeId + BBBCoreConstants.HYPHEN + skuId, BBBCoreConstants.CACHE_STORE_INV)){
				localStoreVo = new LocalStoreVO();
		   		localStoreVo.setSkuId(skuId);
		   		localStoreVo.setStoreId(storeId);
				int invCount = Integer.parseInt(getCacheContainer().get(
						storeId + BBBCoreConstants.HYPHEN + skuId, BBBCoreConstants.CACHE_STORE_INV).toString());
				logDebug("StoreTools | getInventoryFromLocalStore | Coherence value is "+invCount +" | Store id is "+storeId);
				localStoreVo.setStockLevel(invCount);
			} else {
				logDebug("StoreTools | getInventoryFromLocalStore | Coherence value for localstoreVO is null");
			}
			logDebug("StoreTools | getInventoryFromLocalStore | Ends");
		} catch (Exception e) {
			 logError("NumberFormatException occurred while getInventoryFromLocalStore ", e);
			throw e;
		}
   		logDebug(logMessage + " || " + " Ends Here");
   		return localStoreVo;
   	}

       /**
       * Gets the catalog tools.
       * 
        * @return the mCatalogTools
       */
       public BBBCatalogTools getCatalogTools() {
              return this.mCatalogTools;
       }

       /**
       * Sets the catalog tools.
       * 
        * @param pCatalogTools
       *            the new catalog tools
       */
       public void setCatalogTools(BBBCatalogTools pCatalogTools) {
              this.mCatalogTools = pCatalogTools;
       }
}

