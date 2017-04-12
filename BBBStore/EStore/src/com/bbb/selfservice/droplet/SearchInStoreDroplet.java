package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderHolder;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.selfservice.manager.ScheduleAppointmentManager;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 28-December-2011
//--------------------------------------------------------------------------------
public class SearchInStoreDroplet extends BBBDynamoServlet {

	private SearchStoreManager mSearchStoreManager;
	private BBBInventoryManager mInventoryManager;
	
	private BBBCatalogTools mCatalogTools;
	private ScheduleAppointmentManager mScheduleAppointmentManager;
	private String redirectURL;

	private static final String STOREID = "storeId";
	private static final String PAGENUMBER = "pageNumber";
	private static final String PAGEKEY = "pageKey";
	private static final String SEARCHSTRING = "searchString";
	private static final String SEARCHTYPE = "searchType";
	private static final String SEARCHBASEDON = "searchBasedOn";
	private static final String PRODUCTID = "productId";
	private static final String ORDEREDQTY = "orderedQty";
	private static final String SHOPPING_CART = "/atg/commerce/ShoppingCart";
	private static final String RADIUSMILES = "miles";
	private static final String REGISTRYID = "registryId";
	private static final String CHANGE_CURRENT_STORE = "changeCurrentStore";

	
	/**
	 * This method checks the input parameter type and call the searchStore
	 * methods based on input parameter. It will take inputs from cookies files
	 * if exists Output parameter will be output & empty, if search result not
	 * return any records then it will EMPTY otherwise OUTPUT will be there.
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
	    BBBPerformanceMonitor.start("SearchInStoreDroplet", "service");
	    long startTime = System.currentTimeMillis();
		final String logMessage = getClass().getName() + "service || ";
		logInfo(logMessage + " Inside Service Method...");
		logDebug(logMessage + " Starts here  .");
		//get the store type
		String storeType = null;
		String siteId = req.getParameter("siteId");
		boolean scheduleAppointment = false;
		String appointmentType = null;
        try {
        	if(null != req.getParameter("appointmentType")){
        		appointmentType = (String) req.getParameter("appointmentType");
        	}
        	if(null != req.getParameter("scheduleAppointment")){
        		scheduleAppointment =Boolean.parseBoolean(req.getParameter("scheduleAppointment"));
        	}
			storeType = getSearchStoreManager().getStoreType(siteId);
		}  catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(req, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1194 ), bbbSystemException);
			req.setParameter("errorMessage", bbbSystemException.getErrorCode() + bbbSystemException.getMessage());
			req.serviceLocalParameter("error", req, res);
			return;
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(req, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1195 ), bbbBusinessException);
			req.setParameter("errorMessage", bbbBusinessException.getErrorCode() + bbbBusinessException.getMessage());
			req.serviceLocalParameter("error", req, res);
			return;
		}
		
        if(scheduleAppointment == false){
		boolean errorOccurredFlag = false;
		String searchString = null;
		String searchType = null;
		int radiusMiles = 0;
		String productId = req.getParameter(PRODUCTID);
		long productQty = 0L;
		if (req.getParameter(ORDEREDQTY) != null) {
			productQty = Long.parseLong(req.getParameter(ORDEREDQTY));
			req.getSession().setAttribute(PRODUCTID, productId);
			req.getSession().setAttribute(ORDEREDQTY, productQty);
		}
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		
		String registryId = "";
		if(req.getParameter(REGISTRYID)!= null){
			registryId = req.getParameter(REGISTRYID);
		}
		boolean isChangeStore = false;
		if(req.getParameter(CHANGE_CURRENT_STORE)!= null){
			isChangeStore = Boolean.valueOf(req.getParameter(CHANGE_CURRENT_STORE));
		}
		
		logDebug("Request Parameters are");
		logDebug("registryId " + registryId);
		logDebug("changeStore " + isChangeStore);
		logDebug("productId " + productId);
		logDebug("productQty " + productQty);
		

		String storeId = (String) req.getObjectParameter(STOREID);
		//Product availablity check for favorite store
		if (!BBBUtility.isEmpty(storeId)) {
			logDebug(logMessage + " Search based on StoreId..." + storeId);
			StoreDetails objStoreDetails = null;
			try {
				objStoreDetails = getSearchStoreManager().searchStoreById(
						storeId,siteId,storeType);
			} 
			catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(req, "BBBBusinessException in SearchInStoreDroplet while getting store Details", BBBCoreErrorConstants.ACCOUNT_ERROR_1196 ), e);
				req.setParameter("errorMessage", e.getMessage());
				req.serviceLocalParameter("error", req, res);
			}
			catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(req, "BBBSystemException in SearchInStoreDroplet while getting store Details", BBBCoreErrorConstants.ACCOUNT_ERROR_1197 ), e);
				req.setParameter("errorMessage", e.getMessage());
				req.serviceLocalParameter("error", req, res);
			}
			List<StoreDetails> alStoreDetails = new ArrayList<StoreDetails>();
			if (null != objStoreDetails) {
				logDebug(" Values return by StoreManager---- "+objStoreDetails.toString());
				alStoreDetails.add(objStoreDetails);
				req.setParameter("StoreDetails", objStoreDetails);				
				try {
					Map<String, Integer> productStatusMap;
				
                    productStatusMap = checkProductAvailability(
							alStoreDetails, siteId, productId, registryId, isChangeStore, productQty,
							BBBInventoryManager.STORE_STORE, req);

					if (!BBBUtility.isMapNullOrEmpty(productStatusMap)) {
						req.setParameter("productAvailable", productStatusMap);
						req.serviceLocalParameter("favstoreoutput", req, res); 
					}
				} catch (InventoryException e) {
					if(isLoggingDebug()){
					logDebug(LogMessageFormatter.formatMessage(req, "InventoryException in SearchInStoreDroplet while check Product Availability", BBBCoreErrorConstants.ACCOUNT_ERROR_1198 ), e);
					}
					req.setParameter("errorMessage", e.getMessage());
					req.serviceLocalParameter("error", req, res);
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(req, "BBBSystemException in SearchInStoreDroplet while check Product Availability", BBBCoreErrorConstants.ACCOUNT_ERROR_1199 ), e);
					req.setParameter("errorMessage", e.getMessage());
					req.serviceLocalParameter("error", req, res);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(req, "BBBBusinessException in SearchInStoreDroplet while check Product Availability", BBBCoreErrorConstants.ACCOUNT_ERROR_1200 ), e);
					req.setParameter("errorMessage", e.getMessage());
					req.serviceLocalParameter("favstoreerror", req, res);
				}
			} else {
				req.serviceLocalParameter("favstoreempty", req, res);
			}
		} 
			if ((!BBBUtility.isEmpty((String) req.getParameter(SEARCHBASEDON)))
					&& ((String) req.getParameter(SEARCHBASEDON))
							.equalsIgnoreCase("cookie")) {
				
				searchString = (String) req.getSession().getAttribute(BBBCoreConstants.STATUS);
				searchType = (String) req.getSession().getAttribute(BBBCoreConstants.TYPE);

				//NullPointer check BBBSL-2019
				if( req.getSession().getAttribute(RADIUSMILES)!=null){
					radiusMiles = Integer.parseInt((req.getSession().getAttribute(RADIUSMILES)).toString());
					
				}
				req.setParameter(RADIUSMILES, radiusMiles);
				logDebug("Search Based on Cookie : SearchString=["+searchString + " ]"
						+" searchType=["+searchType+ "] radius =" +radiusMiles);
				
				if(req.getSession().getAttribute(PRODUCTID)!=null){
					productId = (String) req.getSession().getAttribute(PRODUCTID);
				}
				if(req.getSession().getAttribute(ORDEREDQTY) !=null){
					productQty = Long.parseLong(req.getSession().getAttribute(ORDEREDQTY).toString());
				}
			} else {
				searchString = (String) req.getParameter(SEARCHSTRING);
				searchType = (String) req.getParameter(SEARCHTYPE);
			}
			if ((!BBBUtility.isEmpty(productId) && productQty > 0)
					&& (!BBBUtility.isEmpty(searchType))
					&& (!BBBUtility.isEmpty(searchString))) {
				try {
					if ((!BBBUtility.isEmpty((String) req
							.getObjectParameter(PAGEKEY)))
							&& (!BBBUtility.isEmpty((String) req
									.getObjectParameter(PAGENUMBER)))) {
							logDebug(logMessage
									+ " Get records for page Number.."
									+ PAGENUMBER);
						objStoreDetailsWrapper = getSearchStoreManager()
								.searchStorePerPage(
										(String) req
												.getObjectParameter(PAGEKEY),
										(String) req
												.getObjectParameter(PAGENUMBER));
					} else {
						objStoreDetailsWrapper = getSearchStoreManager()
								.searchStoreByAddress(searchString, searchType, null);

					}
					
					BBBSessionBean sessionBean = (BBBSessionBean) req.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);	
					int babyCount=0,bedBathCount=0;
					if(siteId.equalsIgnoreCase("BedBathCanada"))
					{
						List<StoreDetails> babyCanadaStore=new ArrayList<StoreDetails>();
						List<StoreDetails> bedBathCanadaStore=new ArrayList<StoreDetails>();
						int count=0;
						if(sessionBean.getBabyCA()!=null)
						{
						if(objStoreDetailsWrapper!=null && objStoreDetailsWrapper.getStoreDetails() !=null)
						{
						for(count=0;count<objStoreDetailsWrapper.getStoreDetails().size();count++)
						{
							if(objStoreDetailsWrapper.getStoreDetails().get(count).getStoreId().startsWith(BBBCoreConstants.STORE_CA_START))
							{
								objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(true);
								babyCanadaStore.add(objStoreDetailsWrapper.getStoreDetails().get(count));
								babyCount++;
							}
							else
							{
								objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(false);
								bedBathCanadaStore.add(objStoreDetailsWrapper.getStoreDetails().get(count));
								bedBathCount++;
							}
						}
					         babyCanadaStore.addAll(bedBathCanadaStore) ; 
					         Collections.copy(objStoreDetailsWrapper.getStoreDetails(),babyCanadaStore);
						}
						
					    } 
						else
						{
							if(objStoreDetailsWrapper!=null && objStoreDetailsWrapper.getStoreDetails() !=null)
							{
							for(count=0;count<objStoreDetailsWrapper.getStoreDetails().size();count++)
							{
								if(objStoreDetailsWrapper.getStoreDetails().get(count).getStoreId().startsWith(BBBCoreConstants.STORE_CA_START))
								{
									objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(true);
									babyCanadaStore.add(objStoreDetailsWrapper.getStoreDetails().get(count));
									babyCount++;
								}
								else
								{
									objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(false);
									bedBathCanadaStore.add(objStoreDetailsWrapper.getStoreDetails().get(count));
									bedBathCount++;
								}
							}
						}
						}
						if(objStoreDetailsWrapper!=null){
						objStoreDetailsWrapper.setBabyCanadaStoreCount(babyCount);
						objStoreDetailsWrapper.setBedBathCanadaStoreCount(bedBathCount);
						}
					}
					
				} 
				catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(req, "BBBBusinessException in SearchInStoreDroplet for cookie based search while  Get records for page Number", BBBCoreErrorConstants.ACCOUNT_ERROR_1201 ), e);
					req.setParameter("errorMessage",
							e.getErrorCode() + e.getMessage());
					req.serviceLocalParameter("error", req, res);
					errorOccurredFlag = true;
				}
				catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(req, "BBBSystemException in SearchInStoreDroplet for cookie based search while  Get records for page Number", BBBCoreErrorConstants.ACCOUNT_ERROR_1202 ), e);
					req.setParameter("errorMessage",
							e.getErrorCode() + e.getMessage());
					req.serviceLocalParameter("error", req, res);
					errorOccurredFlag = true;
				}
			}
			req.setParameter("inputSearchString", searchString);
			
			if (null != objStoreDetailsWrapper) {
				if (objStoreDetailsWrapper.getStoreDetails() != null && !objStoreDetailsWrapper.getStoreDetails().isEmpty()) {
					req.setParameter("StoreDetailsWrapper", objStoreDetailsWrapper);
					logDebug(" Values return by StoreManager---- " + objStoreDetailsWrapper.toString());
					// Check the product availability in stores
					try {
						Map<String, Integer> productStatusMap;
						productStatusMap = checkProductAvailability(
								objStoreDetailsWrapper.getStoreDetails(),
								siteId, productId, registryId, isChangeStore, productQty,
								BBBInventoryManager.STORE_STORE, req);

						if (!BBBUtility.isMapNullOrEmpty(productStatusMap)) {
							req.setParameter("productAvailable", productStatusMap);
							req.serviceLocalParameter("output", req, res);
						}
					} catch (InventoryException e) {
						if(isLoggingDebug()){
						logDebug(LogMessageFormatter.formatMessage(req, "InventoryException in SearchInStoreDroplet while  check product availability for search based on cookie", BBBCoreErrorConstants.ACCOUNT_ERROR_1203 ), e);
						}
						req.setParameter("errorMessage", e.getMessage());
						req.serviceLocalParameter("error", req, res);
					} catch (BBBSystemException e) {
						logError(LogMessageFormatter.formatMessage(req, "BBBSystemException in SearchInStoreDroplet while  check product availability for search based on cookie", BBBCoreErrorConstants.ACCOUNT_ERROR_1204 ), e);
						req.setParameter("errorMessage", e.getMessage());
						req.serviceLocalParameter("error", req, res);
					} catch (BBBBusinessException e) {
						logError(LogMessageFormatter.formatMessage(req, "BBBBusinessException in SearchInStoreDroplet while  check product availability for search based on cookie", BBBCoreErrorConstants.ACCOUNT_ERROR_1205 ), e);
						req.setParameter("errorMessage", e.getMessage());
						req.serviceLocalParameter("error", req, res);
					}
				} else {
					// Address suggestion
					if (null != objStoreDetailsWrapper.getStoreAddressSuggestion()) {
						req.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER, objStoreDetailsWrapper);
						req.serviceLocalParameter("addressSuggestion", req, res);
					} else {
						req.serviceLocalParameter("empty", req, res);
					}
				}
			}else if (BBBUtility.isEmpty(searchString)) {
				logInfo("SearchInStoreDroplet-SessionExpired Block Start ");
				logInfo("searchString : " + searchString);
				logInfo("searchType : " + searchType);
				logInfo("radiusMiles : " + radiusMiles);
				logInfo("productId : " + productId);
				logInfo("productQty : " + productQty);				
				logInfo("SearchInStoreDroplet-SessionExpired Block End ");
				String contextPath = "";
				if (req.getContextPath() != null) {
					contextPath = req.getContextPath();
				}
				res.setHeader("BBB-ajax-redirect-url", contextPath + getRedirectURL());
			}
			else if (!errorOccurredFlag) {
				req.serviceLocalParameter("empty", req, res);
			}
        }
        else
        {
        	String searchString = null;
    		String searchType = null;
    		int radiusMiles = 0;
    		boolean errorOccurredFlag = false;
    		StoreDetailsWrapper objStoreDetailsWrapper = null;
			if ((!BBBUtility.isEmpty((String) req.getParameter(SEARCHBASEDON)))
					&& ((String) req.getParameter(SEARCHBASEDON))
							.equalsIgnoreCase("cookie")) {
				
				searchString = (String) req.getSession().getAttribute(BBBCoreConstants.STATUS);
				searchType = (String) req.getSession().getAttribute(BBBCoreConstants.TYPE);

				//NullPointer check BBBSL-2019
				if( req.getSession().getAttribute(RADIUSMILES)!=null){
				radiusMiles = ((Integer) req.getSession().getAttribute(RADIUSMILES)).intValue();					
				}
				
				req.setParameter(RADIUSMILES, radiusMiles);
				logDebug("Search Based on Cookie : SearchString=["+searchString + " ]"
						+" searchType=["+searchType+ "] radius =" +radiusMiles);				
			} else {
				searchString = (String) req.getParameter(SEARCHSTRING);
				searchType = (String) req.getParameter(SEARCHTYPE);
			}
			if ((!BBBUtility.isEmpty(searchType))
					&& (!BBBUtility.isEmpty(searchString))) {
				try {
					if ((!BBBUtility.isEmpty((String) req
							.getObjectParameter(PAGEKEY)))
							&& (!BBBUtility.isEmpty((String) req
									.getObjectParameter(PAGENUMBER)))) {
							logDebug(logMessage
									+ " Get records for page Number.."
									+ PAGENUMBER);
						objStoreDetailsWrapper = getSearchStoreManager()
								.searchStorePerPage(
										(String) req
												.getObjectParameter(PAGEKEY),
										(String) req
												.getObjectParameter(PAGENUMBER));
					} else {
						objStoreDetailsWrapper = getSearchStoreManager()
								.searchStoreByAddress(searchString, searchType, null);

					}
					
					BBBSessionBean sessionBean = (BBBSessionBean) req.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);	
					int babyCount=0,bedBathCount=0;
					if(siteId.equalsIgnoreCase("BedBathCanada"))
					{
						int count=0;
						if(sessionBean.getBabyCA()!=null)
						{
							List<StoreDetails> babyCanadaStore=new ArrayList<StoreDetails>();
							List<StoreDetails> bedBathCanadaStore=new ArrayList<StoreDetails>();
						if(objStoreDetailsWrapper!=null && objStoreDetailsWrapper.getStoreDetails() !=null)
						{
						for(count=0;count<objStoreDetailsWrapper.getStoreDetails().size();count++)
						{
							if(objStoreDetailsWrapper.getStoreDetails().get(count).getStoreId().startsWith(BBBCoreConstants.STORE_CA_START))
							{
								objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(true);
								babyCanadaStore.add(objStoreDetailsWrapper.getStoreDetails().get(count));
								babyCount++;
							}
							else
							{
								objStoreDetailsWrapper.getStoreDetails().get(count).setBabyCanadaFlag(false);
								bedBathCanadaStore.add(objStoreDetailsWrapper.getStoreDetails().get(count));
								bedBathCount++;
							}
						}
					         babyCanadaStore.addAll(bedBathCanadaStore) ; 
					         Collections.copy(objStoreDetailsWrapper.getStoreDetails(),babyCanadaStore);
						}
						
					    } 
						else
						{
							if(objStoreDetailsWrapper!=null && objStoreDetailsWrapper.getStoreDetails() !=null)
							{
							for(count=0;count<objStoreDetailsWrapper.getStoreDetails().size();count++)
							{
								if(objStoreDetailsWrapper.getStoreDetails().get(count).getStoreId().startsWith(BBBCoreConstants.STORE_CA_START))
								{
									babyCount++;
								}
								else
								{
									bedBathCount++;
								}
							}
						}
						}
						if(objStoreDetailsWrapper!=null){
						objStoreDetailsWrapper.setBabyCanadaStoreCount(babyCount);
						objStoreDetailsWrapper.setBedBathCanadaStoreCount(bedBathCount);
						}
					}
					
				} 
				catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(req, "BBBBusinessException in SearchInStoreDroplet for cookie based search while  Get records for page Number", BBBCoreErrorConstants.ACCOUNT_ERROR_1201 ), e);
					req.setParameter("errorMessage",
							e.getErrorCode() + e.getMessage());
					req.serviceLocalParameter("error", req, res);
					errorOccurredFlag = true;
				}
				catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(req, "BBBSystemException in SearchInStoreDroplet for cookie based search while  Get records for page Number", BBBCoreErrorConstants.ACCOUNT_ERROR_1202 ), e);
					req.setParameter("errorMessage",
							e.getErrorCode() + e.getMessage());
					req.serviceLocalParameter("error", req, res);
					errorOccurredFlag = true;
				}
			}
			req.setParameter("inputSearchString", searchString);
			
			if (null != objStoreDetailsWrapper) {
				if (objStoreDetailsWrapper.getStoreDetails() != null && !objStoreDetailsWrapper.getStoreDetails().isEmpty()) {
					req.setParameter("StoreDetailsWrapper", objStoreDetailsWrapper);
					logDebug(" Values return by StoreManager---- " + objStoreDetailsWrapper.toString());
					Map<String, Boolean> appointmentAvailableMap = null;				
					try {
						appointmentAvailableMap = getScheduleAppointmentManager().checkAppointmentAvailability(objStoreDetailsWrapper.getStoreDetails(),
								 appointmentType);
					} catch (BBBSystemException e) {
						// TODO Auto-generated catch block
						logError(e);
					}

					if (!BBBUtility.isMapNullOrEmpty(appointmentAvailableMap)) {
						req.setParameter("appointmentAvailableMap", appointmentAvailableMap);
						req.serviceLocalParameter("output", req, res);
					}
				} else {
					// Address suggestion
					if (null != objStoreDetailsWrapper.getStoreAddressSuggestion()) {
						req.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER, objStoreDetailsWrapper);
						req.serviceLocalParameter("addressSuggestion", req, res);
					} else {
						req.serviceLocalParameter("empty", req, res);
					}
				}
			}else if (BBBUtility.isEmpty(searchString)) {
				logInfo("SearchInStoreDroplet-SessionExpired Block Start ");
				logInfo("searchString : " + searchString);
				logInfo("searchType : " + searchType);
				logInfo("radiusMiles : " + radiusMiles);							
				logInfo("SearchInStoreDroplet-SessionExpired Block End ");
				String contextPath = "";
				if (req.getContextPath() != null) {
					contextPath = req.getContextPath();
				}
				res.setHeader("BBB-ajax-redirect-url", contextPath + getRedirectURL());
			}
			else if (!errorOccurredFlag) {
				req.serviceLocalParameter("empty", req, res);
			}
		
        }
        long endTime = System.currentTimeMillis();
		logDebug("[SearchInStoreDroplet]-Total Time taken : " + (endTime - startTime) + "ms");
		BBBPerformanceMonitor.end("SearchInStoreDroplet", "service");

	}

	/**
	 * Product availability and bopus exclusion check for searched Store
	 * 
	 * @param pStoreDetails
	 * @param pSiteId
	 * @param pSkuId
	 * @param pReqQty
	 * @param operation
	 * @return
	 * @throws InventoryException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private Map<String, Integer> checkProductAvailability(
			List<StoreDetails> pStoreDetails, String pSiteId, String pSkuId, String pRegistryId, boolean pChangeStore,
			long pReqQty, String operation, DynamoHttpServletRequest pRequest) throws InventoryException,
			BBBSystemException, BBBBusinessException {
		Map<String, Integer> productAvailStatus = new HashMap<String, Integer>();
		List<String> bopusEligibleStates = null;
		List<String> bopusInEligibleStore = null;
		bopusEligibleStates = getCatalogTools().getBopusEligibleStates(pSiteId);
		
		String pStoreId  = getSearchStoreManager().getStoreType(pSiteId);
		bopusInEligibleStore = getCatalogTools().getBopusInEligibleStores(pStoreId, pSiteId ); 
		
		logDebug("Inside  SearchInStoreDroplet.checkProductAvailability---bopusInEligibleStore List " + bopusInEligibleStore);
		logDebug("Inside  SearchInStoreDroplet.checkProductAvailability---BopusEligibleState List " +bopusEligibleStates);
		
		List<String> storeIds = new ArrayList<String>();
		for (StoreDetails storeDetails : pStoreDetails) {
			// Check for BOPUS excluscion at State Level
			if (bopusEligibleStates == null) {
				// Store pick not available for state
				productAvailStatus.put(storeDetails.getStoreId(),
						SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			} else if (storeDetails.getState() != null && !bopusEligibleStates.contains(storeDetails.getState().trim())) {
				// Store pick not available for state
				productAvailStatus.put(storeDetails.getStoreId(),
						SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			}else if(bopusInEligibleStore !=null && bopusInEligibleStore.contains(storeDetails.getStoreId())){
				// Store  is not Bopus Eligible 
				productAvailStatus.put(storeDetails.getStoreId(), SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			}
			storeIds.add(storeDetails.getStoreId());
			
		}
		Map<String, Integer> storeInventoryMap;

		OrderHolder cart = (OrderHolder) pRequest.resolveName(SHOPPING_CART);
		BBBOrder order = (BBBOrder) cart.getCurrent();
		//long skuOrderQuantity = 0L;
		//skuOrderQuantity = skuOrderQuantity(order, pSkuId);
		//long requestQty = pReqQty + skuOrderQuantity;

		storeInventoryMap = getInventoryManager()
				.getBOPUSProductAvailability(pSiteId, pSkuId, storeIds,
						pReqQty, BBBInventoryManager.STORE_STORE, getStoreInventoryContainer(), false, pRegistryId, pChangeStore , false);
		
		for (String storeId : storeInventoryMap.keySet()) {
			/*if(storeInventoryMap.get(storeId)==BBBInventoryManager.LIMITED_STOCK)	{
				storeInventoryMap.put(storeId, BBBInventoryManager.AVAILABLE);
			}*/
			Integer boupusStatus = productAvailStatus.get(storeId);
			Integer inventoryStatus = storeInventoryMap.get(storeId);
			if(boupusStatus!=null){
				//merge the No Bopus flag with Inventory
				String mergedData = boupusStatus.toString(); 
				if(inventoryStatus !=null && inventoryStatus != BBBInventoryManager.DUMMY_STOCK){
					mergedData += inventoryStatus.toString();	
				}
				productAvailStatus.put(storeId, Integer.parseInt(mergedData));
			}else{
				productAvailStatus.put(storeId,storeInventoryMap.get(storeId));
			}
		}
		logDebug("productAvailStatus is: "+productAvailStatus);
		return productAvailStatus;
	}
	

	/**
	 * @return the searchStoreManager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return this.mSearchStoreManager;
	}

	/**
	 * @param pSearchStoreManager
	 *            the searchStoreManager to set
	 */
	public void setSearchStoreManager(SearchStoreManager pSearchStoreManager) {
		this.mSearchStoreManager = pSearchStoreManager;
	}

	/**
	 * @return the bBBInventoryManager
	 */
	public BBBInventoryManager getInventoryManager() {
		return this.mInventoryManager;
	}

	/**
	 * @param pBBBInventoryManager
	 *            the bBBInventoryManager to set
	 */
	public void setInventoryManager(BBBInventoryManager pBBBInventoryManager) {
		this.mInventoryManager = pBBBInventoryManager;
	}



	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	private BBBStoreInventoryContainer mStoreInventoryContainer;
	
	public BBBStoreInventoryContainer getStoreInventoryContainer() {
		return this.mStoreInventoryContainer;
	}

	public void setStoreInventoryContainer(
			BBBStoreInventoryContainer mStoreInventoryContainer) {
		this.mStoreInventoryContainer = mStoreInventoryContainer;
	}
	
	private long skuOrderQuantity(final BBBOrder pOrder, final String pSkuId) {
		BBBOrder order = pOrder;
		List<CommerceItem> commerceItemList = null;
		long skuOrderQuantity = 0L;
		try {
			commerceItemList = order.getCommerceItemsByCatalogRefId(pSkuId);
		} catch (CommerceItemNotFoundException e) {
			logError(e.getMessage(),e);
			return skuOrderQuantity;
		} catch (InvalidParameterException e) {
			logError(e.getMessage(),e);
			return skuOrderQuantity;
		}

		if (commerceItemList != null && !commerceItemList.isEmpty()) {
			for (CommerceItem tempItem : commerceItemList) {
				if (tempItem instanceof BBBCommerceItem) {
					final BBBCommerceItem bbbItem = (BBBCommerceItem) tempItem;
					skuOrderQuantity = bbbItem.getQuantity();
				}
			}
		}
		return skuOrderQuantity;
	}

	/**
	 * @return the redirectURL
	 */
	public String getRedirectURL() {
		return this.redirectURL;
	}

	/**
	 * @param redirectURL the redirectURL to set
	 */
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public ScheduleAppointmentManager getScheduleAppointmentManager() {
		return mScheduleAppointmentManager;
	}

	public void setScheduleAppointmentManager(
			ScheduleAppointmentManager pScheduleAppointmentManager) {
		mScheduleAppointmentManager = pScheduleAppointmentManager;
	}
	
	
}
