package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import atg.commerce.inventory.InventoryException;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.TBSCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBInventoryManagerImpl;
import com.bbb.commerce.inventory.TBSBopusInventoryService;
import com.bbb.commerce.inventory.TBSInventoryManagerImpl;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.tbs.selfservice.tools.TBSStoreTools;
import com.bbb.utils.BBBUtility;

public class TBSPDPInventoryDroplet extends DynamoServlet {
	
	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	/**
	 * mInventoryManager to hold BBBInventoryManagerImpl reference
	 */
	private BBBInventoryManagerImpl mInventoryManager;
	
	private TBSInventoryManagerImpl mTbsInventoryManager;
	
	/**
	 * @return the tbsInventoryManager
	 */
	public TBSInventoryManagerImpl getTbsInventoryManager() {
		return mTbsInventoryManager;
	}
	/**
	 * @param pTbsInventoryManager the tbsInventoryManager to set
	 */
	public void setTbsInventoryManager(TBSInventoryManagerImpl pTbsInventoryManager) {
		mTbsInventoryManager = pTbsInventoryManager;
	}
	private BBBCatalogTools mCatalogTools;
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	private TBSBopusInventoryService mBopusService;
	
	/**
	 * @return the bopusService
	 */
	public TBSBopusInventoryService getBopusService() {
		return mBopusService;
	}
	/**
	 * @param pBopusService the bopusService to set
	 */
	public void setBopusService(TBSBopusInventoryService pBopusService) {
		mBopusService = pBopusService;
	}
	/**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}
	/**
	 * @return the inventoryManager
	 */
	public BBBInventoryManagerImpl getInventoryManager() {
		return mInventoryManager;
	}
	/**
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}
	/**
	 * @param pInventoryManager the inventoryManager to set
	 */
	public void setInventoryManager(BBBInventoryManagerImpl pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}
	
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		vlogDebug("TBSPDPInventoryDroplet :: service() method :: START");
		
		boolean domCallFlag = false;
		try {
			domCallFlag = getCatalogTools().getValueForConfigKey(TBSConstants.CONFIG_TYPE_FLAG_DRIVEN_FUNCTIONS, TBSConstants.CONFIG_KEY_DOM_CALL_FLA, domCallFlag);
		} catch (BBBSystemException e) {
			logError("TBSPDPInventoryDroplet :: service() : Exception occurred in fetching config key: " + TBSConstants.CONFIG_KEY_DOM_CALL_FLA);
		} catch (BBBBusinessException e) {
			logError("TBSPDPInventoryDroplet :: service() : Exception occurred in fetching config key: " + TBSConstants.CONFIG_KEY_DOM_CALL_FLA);
		}
		
		/* If domCallFlag flag is true, it will check onlineInventory first. 
		 * If not available in online inventory then only it will make DOM call otherwise not.
		 * This performance change is made for story BBB-745.
		 * If domCallFlag flag is false, it will keep executing existing code. 
		*/
		
		if(domCallFlag){
			vlogDebug("TBSPDPInventoryDroplet :: flag for avoiding DOM call is true.");

			getOnlineOrDOMInventory(pRequest, pResponse);
		} else {
			vlogDebug("TBSPDPInventoryDroplet :: flag for avoiding DOM call is false.");

			getInventoryFromDOM(pRequest, pResponse);
		}
		
		vlogDebug("TBSPDPInventoryDroplet :: service() method :: END");

	}
	
	private void getOnlineOrDOMInventory(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		vlogDebug("TBSPDPInventoryDroplet :: getOnlineOrDOMInventory() method :: START");

		String sku = pRequest.getParameter("sku");
		String siteId = pRequest.getParameter("siteId");
		String caFlag = null;
		
		 try {
        	final RepositoryItem skuRepositoryItem = this.getCatalogTools().getSkuRepositoryItem(sku);
        	if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) != null) {
        		caFlag = (String) skuRepositoryItem.getPropertyValue("ecomFulfillment");
        	}
        } catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1194 ), bbbSystemException);
			pRequest.setParameter("errorMessage", bbbSystemException.getErrorCode() + bbbSystemException.getMessage());
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
			return;
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1195 ), bbbBusinessException);
			pRequest.setParameter("errorMessage", bbbBusinessException.getErrorCode() + bbbBusinessException.getMessage());
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
			return;
		}		
		int availability = BBBInventoryManager.AVAILABLE;

		try{
			availability = getInventoryManager().getNonVDCProductAvailability(siteId, sku, caFlag, BBBInventoryManager.PRODUCT_DISPLAY, BBBCoreConstants.CACHE_ENABLED, 1);
		} catch (InventoryException e) {
			logDebug("TBSPDPInventoryDroplet :: getOnlineOrDOMInventory() : Exception occurred in getting Inventory information  for sku " + sku);
			availability = BBBInventoryManager.NOT_AVAILABLE;
		}
		
		if(availability == BBBInventoryManager.AVAILABLE){
			DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			request.setParameter(TBSConstants.NEAR_BY_STORE_LINK, true);
			request.setParameter(TBSConstants.TIME_FRAME, "0001");
			pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse); 
		} else {				
			getInventoryFromDOM(pRequest, pResponse);
		}
		vlogDebug("TBSPDPInventoryDroplet :: getOnlineOrDOMInventory() method :: END");
	}
	
	
	private void getInventoryFromDOM(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		vlogDebug("TBSPDPInventoryDroplet :: getInventoryFromDOM() method :: START");
		String timeFrame = "0004";
		String sku = pRequest.getParameter("sku");
		String siteId = pRequest.getParameter("siteId");
		String storeId = (String)pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		Integer currentStoreQuantity = 0;
		String currentSiteId = SiteContextManager.getCurrentSiteId();
		//get the storeNumber from cookie.
		if(StringUtils.isBlank(storeId)){
			storeId = storeNumFromCookie(pRequest);
		}
		boolean warehouseFlag = false;
		boolean regionaStoreslFlag = false;
		boolean otherStoresFlag = false;
		boolean departmentFlag=false;
		boolean nearbyStoreLink=false;
        boolean bopusExclusion = false;
       
        try {
        	final RepositoryItem skuRepositoryItem = this.getCatalogTools().getSkuRepositoryItem(sku);
        	if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) != null) {
        		bopusExclusion = ((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME)).booleanValue();
        	}
        } catch (BBBSystemException bbbSystemException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1194 ), bbbSystemException);
			pRequest.setParameter("errorMessage", bbbSystemException.getErrorCode() + bbbSystemException.getMessage());
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
			return;
		} catch (BBBBusinessException bbbBusinessException) {
			logError(LogMessageFormatter.formatMessage(pRequest, "err_store_search_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1195 ), bbbBusinessException);
			pRequest.setParameter("errorMessage", bbbBusinessException.getErrorCode() + bbbBusinessException.getMessage());
			pRequest.serviceLocalParameter("error", pRequest, pResponse);
			return;
		}
        
        Boolean enableEOMFlag = Boolean.FALSE;
		try {
			enableEOMFlag = getCatalogTools().getValueForConfigKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCoreConstants.EOM_ENABLE_FLAG, false);
		} catch (BBBSystemException e1) {
			vlogError(e1, "TBSSearchStoreManager.getShipTime: BBBSystemException while trying to get EnableEOM flag.");
		} catch (BBBBusinessException e1) {
			vlogError(e1, "TBSSearchStoreManager.getShipTime: BBBBusinessException while trying to get EnableEOM flag.");
		}
		if (enableEOMFlag) {
			vlogDebug("TBSPDPInventoryDroplet.getInventoryFromDOM: Calling EOM network to get ship time");
			Map<String, String> networkInventoryOutput = getSearchStoreManager().getShipTimeByEOM(sku, 0, siteId,
					storeId);
			if (TBSConstants.OUTPUT.equals(networkInventoryOutput.get(TBSConstants.SERVICE_PARAMETER))) {
				pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
			} else if (TBSConstants.EMPTY.equals(networkInventoryOutput.get(TBSConstants.SERVICE_PARAMETER))) {
				pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest, pResponse);
			} else if (TBSConstants.ERROR.equals(networkInventoryOutput.get(TBSConstants.SERVICE_PARAMETER))) {
				pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
			}
			vlogDebug("TBSPDPInventoryDroplet.getInventoryFromDOM: Ends");
			return;
		}
		
		if (!BBBUtility.isEmpty(storeId)) {
			vlogDebug(" Search based on StoreId..." + storeId);
			
			StoreDetails objStoreDetails = null;
			List<StoreDetails> wareHouseStoreDetails = null;
			List<StoreDetails> bedBathStoreDetails = null;
			List<StoreDetails> canadaStoreDetails = null;
			List<StoreDetails> buyBuyBabyStoreDetails = null;
			List<StoreDetails> storeDetails = new ArrayList<StoreDetails>();
			List<StoreDetails> nearbyStores = new ArrayList<StoreDetails>();
			Map latLongMap = (Map) pRequest.getObjectParameter("latLongMap");
			Object changeRadius = pRequest.getObjectParameter("changedRadius");
			RepositoryItem currentStoreItem = null;
			
			//invoking the searchNearByStores method for getting the near by stores
			// Do not add nearby stores to lookup if SKU is not BOPUS enabled
			RepositoryItem[] storeList = null;
			if( !bopusExclusion ) {
				try {
					currentStoreItem = mSearchStoreManager.getStoreRepository().getItem(storeId, TBSConstants.STORE);
					if(latLongMap!=null && changeRadius != null){
						storeList = mSearchStoreManager.searchNearByStoresFromLatLong(latLongMap, changeRadius);
					}else{
						storeList = mSearchStoreManager.searchNearByStores(storeId);
					}
					if(storeList != null && storeList.length > 0){
						for (RepositoryItem repositoryItem : storeList) {
							//converting the storeItem into StoreDetails object
							if(latLongMap!=null){
								objStoreDetails = mSearchStoreManager.convertStoreItemToStore(repositoryItem, null, latLongMap);
							}else{
								objStoreDetails = mSearchStoreManager.convertStoreItemToStore(repositoryItem, currentStoreItem, null);
							}
							storeDetails.add(objStoreDetails);
						}
						nearbyStores.addAll(storeDetails);
					}
				} catch (RepositoryException e) {
					vlogError("RepositoryException occurred while searching for near by stores");
					pRequest.setParameter("errorMessage", e.getMessage());
					pRequest.serviceLocalParameter("error", pRequest, pResponse);
				} catch (SQLException e) {
					vlogError("SQLException occurred while searching for near by stores");
					pRequest.setParameter("errorMessage", e.getMessage());
					pRequest.serviceLocalParameter("error", pRequest, pResponse);
				}
			}
			
			wareHouseStoreDetails = mSearchStoreManager.includeWarehouseStoreDetails();
			if(wareHouseStoreDetails != null && wareHouseStoreDetails.size() > TBSConstants.ZERO){
				storeDetails.addAll(wareHouseStoreDetails);
			}
			
			bedBathStoreDetails = mSearchStoreManager.includeBedBathUSStoreDetails();
			canadaStoreDetails = mSearchStoreManager.includeCanadaStoreDetails();
			buyBuyBabyStoreDetails = mSearchStoreManager.includeBuyBuyBabyStoreDetails();
			
			if(!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase(TBSConstants.BED_BATH_US)){
				storeDetails.addAll(bedBathStoreDetails);
			}
			if(!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase(TBSConstants.BED_BATH_CA)){
				storeDetails.addAll(canadaStoreDetails);
			}
			if(!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase(TBSConstants.BUY_BUY_BABY)){
				storeDetails.addAll(buyBuyBabyStoreDetails);
			}
			if(!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase("TBS_BedBathUS")){
				storeDetails.addAll(bedBathStoreDetails);
			}
			if(!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase("TBS_BuyBuyBaby")){
				storeDetails.addAll(buyBuyBabyStoreDetails);
			}
			if(!StringUtils.isBlank(siteId) && siteId.equalsIgnoreCase("TBS_BedBathCanada")){
				storeDetails.addAll(canadaStoreDetails);
			}
			
			if (storeDetails.size() > TBSConstants.ZERO) {
				Map<String, Integer> storeInventoryMap = null;
				try {
					BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer)
							pRequest.resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
					storeInventoryMap = mSearchStoreManager.checkProductAvailability(storeDetails, siteId, sku, null, false, 1, 
							BBBInventoryManager.STORE_STORE, storeInventoryContainer, pRequest);
					
					logDebug("storeInventoryMap :: "+storeInventoryMap);
				} catch (InventoryException e) {
					vlogDebug(LogMessageFormatter.formatMessage(pRequest, "InventoryException in SearchInStoreDroplet while check Product Availability", BBBCoreErrorConstants.ACCOUNT_ERROR_1198 ), e);
					pRequest.setParameter("errorMessage", e.getMessage());
					pRequest.serviceLocalParameter("error", pRequest, pResponse);
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException in SearchInStoreDroplet while check Product Availability", BBBCoreErrorConstants.ACCOUNT_ERROR_1199 ), e);
					pRequest.setParameter("errorMessage", e.getMessage());
					pRequest.serviceLocalParameter("error", pRequest, pResponse);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in SearchInStoreDroplet while check Product Availability", BBBCoreErrorConstants.ACCOUNT_ERROR_1200 ), e);
					pRequest.setParameter("errorMessage", e.getMessage());
					pRequest.serviceLocalParameter("error", pRequest, pResponse);
				}
				
				if (storeInventoryMap != null && storeInventoryMap.size() > 0) {
					
					logDebug("current store :: "+storeId +" inventory data :: "+storeInventoryMap.get(storeId));
					
					if(storeInventoryMap.get(storeId) != null && storeInventoryMap.get(storeId) > 0){
						currentStoreQuantity = storeInventoryMap.get(storeId);
					} 
					if(wareHouseStoreDetails != null && !wareHouseStoreDetails.isEmpty()){
						for (StoreDetails warehouseStore : wareHouseStoreDetails) {
							if(storeInventoryMap.get(warehouseStore.getStoreId()) != null &&
									storeInventoryMap.get(warehouseStore.getStoreId()) > 0){
								logDebug("warehouseStore :: "+storeId +" inventory data :: "+storeInventoryMap.get(warehouseStore.getStoreId()));
								warehouseFlag = true;
								break;
							}
						}
					}
					if(warehouseFlag){
						vlogDebug("Inventory available at warehouse stores");
						timeFrame = "0001";
						nearbyStoreLink = true;
					} else if(!warehouseFlag){
						//verifying regional fulfillments
						storeDetails.removeAll(nearbyStores);
						storeDetails.removeAll(wareHouseStoreDetails);
						if(storeDetails != null && !storeDetails.isEmpty()){
							for (StoreDetails store : storeDetails) {
								if(storeInventoryMap.get(store.getStoreId()) != null &&
										storeInventoryMap.get(store.getStoreId()) > 0){
									logDebug("regional fulfillment Store :: "+storeId +" inventory data :: "+storeInventoryMap.get(store.getStoreId()));
									regionaStoreslFlag = true;
									break;
								}
							}
						}
					} 
					if(!warehouseFlag && regionaStoreslFlag){
						vlogDebug("Inventory available at regional stores");
						timeFrame = "0002";
						nearbyStoreLink = true;
					} 
					
					//verifying the Other stores
					Map<String, Integer> nearbyStoreInventoryMap = new HashMap<String, Integer>();
					if(nearbyStores != null && !nearbyStores.isEmpty()){
						for (StoreDetails storeDetail : nearbyStores) {
							if(storeInventoryMap.get(storeDetail.getStoreId()) != null &&
									storeInventoryMap.get(storeDetail.getStoreId()) > 0){
								logDebug("other Store :: "+storeId +" inventory data :: "+storeInventoryMap.get(storeDetail.getStoreId()));
								//if not available at whereHouse and regional stores
								if(!warehouseFlag && !regionaStoreslFlag){
									otherStoresFlag = true;
								}
							}
							//setting the nearby store inventory data
							nearbyStoreInventoryMap.put(storeDetail.getStoreId(), storeInventoryMap.get(storeDetail.getStoreId()));
						}
						pRequest.setParameter("nearbyStores", nearbyStores);
						pRequest.setParameter("nearbyStoresInventory", nearbyStoreInventoryMap);
					}
					if (otherStoresFlag) {
						vlogDebug("Inventory available at other stores");
						timeFrame = "0003";
						nearbyStoreLink =true;
					} 
					// If Inventory not found in other Stores, check for the availability in Special Departments
					if(!warehouseFlag && !regionaStoreslFlag && !otherStoresFlag){
						Map<String, Integer> specialDeptInventoryMap = new HashMap<String, Integer>();
						ThresholdVO skuThresholdVO = null;
						List<String> deptIds = null;
						try {
							deptIds = ((TBSCatalogToolsImpl)getCatalogTools()).getSkuDepartMents(sku);
							List<String> specialDeptIds = getCatalogTools().getAllValuesForKey("ContentCatalogKeys", "SpecialDepartments");
							if(deptIds != null && !deptIds.isEmpty() && specialDeptIds != null && !specialDeptIds.isEmpty()){
								for (String deptId : deptIds) {
									if(specialDeptIds.contains(deptId)){
										try {
											skuThresholdVO = ((TBSCatalogToolsImpl)getCatalogTools()).getSkuThresholdForSplDept(deptId, 1);
										} catch (RepositoryException e) {
											logError("Exception in cheking Threshhold of sku", e);
											pRequest.setParameter("errorMessage", e.getMessage());
											pRequest.serviceLocalParameter("error", pRequest, pResponse);
											departmentFlag = true;
										} 
										if(skuThresholdVO != null && skuThresholdVO.getThresholdAvailable() > 0){
											departmentFlag=true;
											logDebug("Inventory available in the department"+specialDeptInventoryMap.get(deptId));
											break;
										}
									}
								}
								
							}
						} catch (BBBBusinessException e) {
							logError("Exception in querying Inventory by department", e);
							pRequest.setParameter("errorMessage", e.getMessage());
							pRequest.serviceLocalParameter("error", pRequest, pResponse);
							departmentFlag = false;
						} catch (BBBSystemException e) {
							logError("Exception in cheking Threshhold of sku", e);
							pRequest.setParameter("errorMessage", e.getMessage());
							pRequest.serviceLocalParameter("error", pRequest, pResponse);
							departmentFlag = false;
						}
						if(departmentFlag){
							timeFrame = "0005";
						}
					}
					pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse); 
				} else {
					vlogDebug("Inventory Data not found......");
					pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest, pResponse);
				}
			}
		}
		// this call is made when there is no inventory found in warehouse and regional store, 
		//Inventory check by network
		Map<String, Integer> networkInventoryMap = null;
       
		if(!warehouseFlag && !regionaStoreslFlag && !departmentFlag){
			 String mNetworkInvFlag=null;
			 try {
					mNetworkInvFlag=getSearchStoreManager().getNetworkInventoryFlag(currentSiteId);
		        }  catch (BBBSystemException bbbSystemException) {
					logError(LogMessageFormatter.formatMessage(pRequest, "err_network_flag_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1194 ), bbbSystemException);
					return;
				} catch (BBBBusinessException bbbBusinessException) {
					logError(LogMessageFormatter.formatMessage(pRequest, "err_network_flag_tech_error", BBBCoreErrorConstants.ACCOUNT_ERROR_1195 ), bbbBusinessException);
					return;
				}
			if (BBBCoreConstants.TRUE.equalsIgnoreCase(mNetworkInvFlag)) {
				try {
	        		networkInventoryMap = ((TBSStoreTools)getSearchStoreManager().getStoreTools()).fetchORFInventory(siteId, sku);
					if(networkInventoryMap !=null && networkInventoryMap.get(sku)!=null 
						&& networkInventoryMap.get(sku) >= getSearchStoreManager().getOrfThreshold(currentSiteId)){
							vlogDebug("Inventory available in the network");
						timeFrame = "0003";
					} 
				} catch (BBBBusinessException e) {
				logError("Exception in querying Inventory by network", e);
					pRequest.setParameter("errorMessage", e.getMessage());
					pRequest.serviceLocalParameter("error", pRequest, pResponse);
				}
			}
		} 
		if(!otherStoresFlag){
			pRequest.setParameter("nearbyStore", "nearbyStore notavailable");
		}
		pRequest.setParameter(TBSConstants.TIME_FRAME, timeFrame);
		pRequest.setParameter(TBSConstants.NEAR_BY_STORE_LINK, nearbyStoreLink);
		pRequest.setParameter(TBSConstants.CURRENT_STORE_QTY, currentStoreQuantity);
		vlogDebug("TBSPDPInventoryDroplet :: getInventoryFromDOM() method :: END");
	}
	
	/**
	 * This is used to get the store number from cookie
	 * @param pRequest
	 * @return
	 */
	private String storeNumFromCookie(HttpServletRequest pRequest) {
		Cookie cookies[] = pRequest.getCookies();
		String cookieValue = null;
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if (TBSConstants.STORE_NUMBER_COOKIE.equals(name)) {
					cookieValue = cookies[i].getValue();
					break;
				}
			}
		return cookieValue;
	}

}
