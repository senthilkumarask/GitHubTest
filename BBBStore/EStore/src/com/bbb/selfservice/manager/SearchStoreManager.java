package com.bbb.selfservice.manager;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

import static com.bbb.constants.BBBCoreConstants.MAPQUEST_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.http.client.ClientProtocolException;

import com.bbb.cache.LocalStoreVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBInventoryManagerImpl;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.RouteDetails;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.selfservice.tools.StoreTools;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.commerce.inventory.InventoryException;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
/**
 * @author Jaswinder Sidhu
 * This class get the Store details & Store directions details from MapQuest.
 */
public class SearchStoreManager extends BBBGenericService {

	private StoreTools mStoreTools;
	private String mPageSize;
	private String mStaticMapSize;
	private String mStaticMapZoom;
	private BBBObjectCache mObjectCache;
	private BBBCatalogToolsImpl mCatalogTools;
/*	private MutableRepository localStoreRepository;*/
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBInventoryManager inventoryManager;
	private static final String AMPERSAND="&";
	private static final String PAGEKEY="pageKey=";
	private static final String CURRENTPAGE="currentPage=";
	private static final String TO="to=";
	private static final String FROM="from=";
	private static final String ROUTETYPE="routeType=";
	private static final String AVOIDS="avoids=";
	private static final String SESSION="session=";
	private static final String SCENTER="scenter=";
	private static final String ECENTER="ecenter=";
	private static final String SIZE="size=";
	private static final String ZOOM="zoom=";	
	private static final String CHANGE_CURRENT_STORE = "changeCurrentStore";
	private static final String ORDEREDQTY = "orderedQty";
	
	/**
	 * @return the objectCache
	 */
	public BBBObjectCache getObjectCache() {
		return mObjectCache;
	}

	/**
	 * @param pObjectCache the objectCache to set
	 */
	public void setObjectCache(BBBObjectCache pObjectCache) {
		mObjectCache = pObjectCache;
	}
	
/*	public MutableRepository getLocalStoreRepository() {
		return localStoreRepository;
	}

	public void setLocalStoreRepository(MutableRepository localStoreRepository) {
		this.localStoreRepository = localStoreRepository;
	}
	*/
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}


	/**
	 * This method will call StoreTools.searchStore method and pass the search
	 * string
	 * 
	 * @param pSearchString
	 * @return StoreDetailsWrapper
	 * @throws BBBBusinessException
	 */
	private StoreDetailsWrapper searchStore(String searchType , String pSearchString ,boolean...saveInCache)
			throws BBBBusinessException {
		BBBPerformanceMonitor.start("SearchStoreManager.searchStore()-Invoke");
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		try {
			/*Fetch store details from cache*/
			objStoreDetailsWrapper = getStoreTools().searchStore(searchType , pSearchString);
		
			/*if(saveInCache!=null && saveInCache.length>0 && !saveInCache[0]){
				objStoreDetailsWrapper = getStoreTools().searchStore(pSearchString);
			}else{
				objStoreDetailsWrapper = getCachedStoreDetails(pSearchString);
				If it was cache miss, retrieve the store details and put it in cache
				if(objStoreDetailsWrapper == null) {
					//Search Store request to MapQuest Starts here
					objStoreDetailsWrapper = getStoreTools().searchStore(pSearchString);
					putStoreDetailsInCache(pSearchString, objStoreDetailsWrapper);
				}s
			}*/
		} catch (ClientProtocolException e) {
			logError(LogMessageFormatter.formatMessage(null, "ClientProtocolException in SearchStoreManager while searchStore ", BBBCoreErrorConstants.ACCOUNT_ERROR_1222 ), e);
		} catch (IOException e) {
			logError(LogMessageFormatter.formatMessage(null, "IOException in SearchStoreManager while searchStore ", BBBCoreErrorConstants.ACCOUNT_ERROR_1223 ), e);
		}finally {
			BBBPerformanceMonitor.end("SearchStoreManager.searchStore()-Invoke");
		}

		return objStoreDetailsWrapper;
	}

	/*private void putStoreDetailsInCache(String pKey, StoreDetailsWrapper pValue) throws BBBSystemException, BBBBusinessException {
		String cacheName = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, MAPQUEST_CACHE_NAME);
		long timeout = Long.parseLong(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, MAPQUEST_CACHE_TIMEOUT));
		logDebug("cacheName:" + cacheName);
		getObjectCache().put(pKey, pValue, cacheName,timeout);
		
	}*/

	/**
	 * @param pSearchString
	 * @return
	 * @throws Exception
	 */
	private StoreDetailsWrapper getCachedStoreDetails(String pSearchString){
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		try {
			String cacheName = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, MAPQUEST_CACHE_NAME);
			objStoreDetailsWrapper = (StoreDetailsWrapper) getObjectCache().get(pSearchString,cacheName);
		} catch (Exception e) {
			logError(LogMessageFormatter.formatMessage(null, "Exception in SearchStoreManager while getCachedStoreDetails ", BBBCoreErrorConstants.ACCOUNT_ERROR_1225 ), e);
			
		}
		return objStoreDetailsWrapper;
	}

	/**
	 * This method is used to get favorite store id.
	 * @param siteId
	 * @param profile
	 * @return favorite store id
	 * @throws BBBSystemException 
	 */
	public String fetchFavoriteStoreId(final String siteId, final Profile profile) throws BBBSystemException {
		logDebug("SearchStoreManager.fetchFavoriteStoreId() - start with params siteId: "
				+ siteId + "profile: " + profile);
		String favoriteStoreId = null;
		if(!profile.isTransient()){
			Repository repository = profile.getProfileTools().getProfileRepository();
			RepositoryItem profileItem = null;
			try {
				profileItem = repository.getItem(profile.getRepositoryId(), "user");
				Map lUserSitesMap = (Map) profileItem.getPropertyValue("userSiteItems");
				if(lUserSitesMap != null && !lUserSitesMap.isEmpty()){
					RepositoryItem userSiteAssoc = (RepositoryItem) lUserSitesMap.get(siteId);
					favoriteStoreId = (String) userSiteAssoc.getPropertyValue("favouriteStoreId");
				}
			} catch (RepositoryException e) {
				
				logError(LogMessageFormatter.formatMessage(null, "Error while fetching the Favorite store ID for User :"+profile.getRepositoryId()), e);
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_FETCH_DATA_REPOSITORY_EXCEPTION, e);
			}
			
		}
		logDebug("SearchStoreManager.fetchFavoriteStoreId() - "
				+ "end with return param favoriteStoreId: " + favoriteStoreId);
		return favoriteStoreId;
	}
	
	/**
	 * 
	 * @param storeId
	 * @param siteId
	 * @return
	 * @throws Exception 
	 */
	public StoreDetails fetchFavStoreDetails(final String storeId,
			final String siteId) throws Exception {
		logDebug("SearchStoreManager.fetchFavStoreDetails() - start with params storeId: "
				+ storeId + "siteId: " + siteId);
		StoreDetails storeDetails = null;
			String storeType = getStoreType(siteId);
			List<String> bopusInEligibleStore = getCatalogTools()
					.getBopusInEligibleStores(storeType, siteId);
			if (bopusInEligibleStore != null
					&& !bopusInEligibleStore.contains(storeId)) {
				storeDetails = searchStoreById(storeId, siteId, storeType);


					List<String> bopusEligibleStates = getCatalogTools()
							.getBopusEligibleStates(siteId);
					if (bopusEligibleStates != null
							&& !bopusEligibleStates.contains(storeDetails
									.getState())) {
						return null;
					}
			}
		logDebug("Favorite Store Deatils: " + storeDetails);
		logDebug("SearchStoreManager.fetchFavStoreDetails() - end");
		return storeDetails;
	}
	
	/**This method returns fav store for PDP
	 * @param storeId
	 * @param siteId
	 * @return Store Details
	 * @throws Exception 
	 */
	public StoreDetails fetchFavStoreDetailsForPDP(final String storeId,
			final String siteId, DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws Exception {
		logDebug("SearchStoreManager.fetchFavStoreDetails() - start with params storeId: "
				+ storeId + "siteId: " + siteId);
		long productQty = 1;
		if (req.getParameter(ORDEREDQTY) != null) {
			productQty = Long.parseLong(req.getParameter(ORDEREDQTY));
		}
		StoreDetails storeDetails = null;
		String skuId = req.getParameter(BBBCoreConstants.SKUID);
		List<StoreDetails> lStoreDetails = new ArrayList<StoreDetails>();
		LocalStoreVO localStoreVO = null;
		boolean getNearestStore = false;
		try {
			//check if favourite store is eligible and has inventory
			String storeType = getStoreType(siteId);
			List<String> bopusInEligibleStore = getCatalogTools()
					.getBopusInEligibleStores(storeType, siteId);
			if ((!BBBUtility.isListEmpty(bopusInEligibleStore)
					&& !bopusInEligibleStore.contains(storeId)) || BBBUtility.isListEmpty(bopusInEligibleStore)) {
				localStoreVO = getInventoryFromLocalStore(storeId,
						skuId);
				
				//get from db if not found in coherence
				if(localStoreVO == null && BBBCoreConstants.TRUE.equalsIgnoreCase(getLocalStoreDbKey()))
				{
					localStoreVO = getStoreTools().getInventoryForStoreFromDb(skuId, storeId);
				}
				int inventory = 0;
				if(localStoreVO != null)
				{
					inventory = localStoreVO.getStockLevel();
					if(isLoggingDebug())
					{
						logDebug("Inventory of skuId:storeId"+skuId+":"+storeId+" returned From Db is "+inventory);
					}
				}
				
				//checking if favorite store has inventory
				ThresholdVO skuThresholdVO = getCatalogTools().getSkuThreshold(siteId, skuId);
				int inventoryStatus = (((BBBInventoryManagerImpl) getInventoryManager()).getInventoryStatus(inventory, productQty, skuThresholdVO ,storeId ));
				if (inventoryStatus == BBBInventoryManager.AVAILABLE || inventoryStatus == BBBInventoryManager.LIMITED_STOCK ) {
					RepositoryItem[] storeItems = getStoreTools().getStores(
							storeId, storeType);
					for (RepositoryItem repositoryItem : storeItems) {
						lStoreDetails = getStoreTools().addStoreDetailsToList(
								repositoryItem, lStoreDetails, null, null,
								storeId, ((BBBCatalogToolsImpl)getCatalogTools()).getStoreRepository(), null);
						if(!BBBUtility.isListEmpty(lStoreDetails))
						storeDetails = lStoreDetails.get(0);
					}
				} 
				//get nearest store to fav store if fav store does not have inventory
				else
				{
					getNearestStore = true;
				}
			}
			//get nearest store to fav store if fav store falls in ineligible states
			else
			{
				getNearestStore = true;
			}
				// getting store near to fav stores having inventory
				if(getNearestStore == true) {
					lStoreDetails = searchStoresById(storeId, siteId, storeType);
					if (!BBBUtility.isListEmpty(lStoreDetails)) {
						// Getting only that store in which sku is available
						// atleast one of the store should have stock . If not
						// error
						// message is shown to user.
						storeDetails = getStoreWithInventory(lStoreDetails,
								req, res, siteId,storeType);
					}
				}
			
		} catch (Exception e) {
			logError("Exception occurred while getting Fav Stores for PDP ",e);
			throw e;
		}

		logDebug("Favorite Store Deatils: " + storeDetails);
		logDebug("SearchStoreManager.fetchFavStoreDetails() - end");
		return storeDetails;
	}
	
	/**This method returns eligible store having inventory status as Available/Limited
	 * @param lStoreDetails
	 * @param req
	 * @param res
	 * @param siteId
	 * @return Store Details
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws IOException
	 */
	public StoreDetails getStoreWithInventory(List<StoreDetails> lStoreDetails ,
			DynamoHttpServletRequest req,
			DynamoHttpServletResponse res , String siteId , String storeType) throws Exception
	{
		long productQty = 1;
		LocalStoreVO localStoreVO  = null;
		if (req.getParameter(ORDEREDQTY) != null) {
			productQty = Long.parseLong(req.getParameter(ORDEREDQTY));
		}
		String skuId = req.getParameter(BBBCoreConstants.SKUID);
		int countInventoryStatus = 0;
		int countEligibleStates = 0;
		StoreDetails storeDetails = null;
		List<String> bopusInEligibleStore = getCatalogTools()
				.getBopusInEligibleStores(storeType, siteId);
		ThresholdVO skuThresholdVO = getCatalogTools()
				.getSkuThreshold(siteId, skuId);
		for (final StoreDetails stDetails : lStoreDetails) {
			if ((!BBBUtility.isListEmpty(bopusInEligibleStore)
					&& !bopusInEligibleStore.contains(stDetails.getStoreId())) || BBBUtility.isListEmpty(bopusInEligibleStore)) {
				countEligibleStates++;
				localStoreVO = getInventoryFromLocalStore(
						stDetails.getStoreId(),
						req.getParameter(BBBCoreConstants.SKUID));
				if (localStoreVO != null) {
					int inventory = 0;

					inventory = localStoreVO.getStockLevel();
				
					int inventoryStatus = (((BBBInventoryManagerImpl) getInventoryManager())
							.getInventoryStatus(inventory, productQty, skuThresholdVO , localStoreVO.getStoreId()));
					if (inventoryStatus == BBBInventoryManager.AVAILABLE || inventoryStatus == BBBInventoryManager.LIMITED_STOCK) {
						countInventoryStatus++;
						storeDetails = stDetails;
						break;
					}
				}
			}
		}
		//get stock level from db if coherence cache is empty
		if(countInventoryStatus == 0)
		{
			if(BBBCoreConstants.TRUE.equalsIgnoreCase(getLocalStoreDbKey()))
			{
				storeDetails = getStoreTools().getInventoryFromDb(lStoreDetails, skuId, productQty, skuThresholdVO , bopusInEligibleStore);
				if(storeDetails != null)
				{
					countInventoryStatus++;
				}
			}
		}
		
		//set error message if inventory is not available
		 if (countInventoryStatus == 0 || countEligibleStates == 0) {
			req.setParameter(
					BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE,
					getLblTxtTemplateManager()
							.getErrMsg(
									BBBCoreConstants.INVENTORY_NOT_AVAILABLE,
									BBBCoreConstants.DEFAULT_LOCALE, null));
			req.setParameter(BBBCoreConstants.RADIUS, ServletUtil.getCurrentRequest().getSession()
                    .getAttribute(SelfServiceConstants.RADIUSMILES));
			req.serviceParameter(BBBCoreConstants.OUTPUT_INVENTORY_NOT_AVAILABLE,
					req, res);
			return null;
		}
		
		return storeDetails;
		
	}
	
	
	
	/**This method returns eligible store having inventory status as Available/Limited
	 * @param lStoreDetails
	 * @param req
	 * @param res
	 * @param siteId
	 * @return Store id
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getStoreWithInventoryByStoreId(final List<String> storeIds,
			final DynamoHttpServletRequest req, final DynamoHttpServletResponse res,
			String siteId) throws BBBBusinessException, BBBSystemException,
			RepositoryException, ServletException, IOException {

		logDebug("Inside  SearchStoreManager.getStoreWithInventoryByStoreId Starts, storeId and siteId is"
				+ storeIds + "," + siteId);
		String storeType = getStoreType(siteId);
		String availStoreId = null;
		long productQty = 1;
		LocalStoreVO localStoreVO = null;
		String skuId = req.getParameter(BBBCoreConstants.SKUID);
		int countInventoryStatus = 0;
		int countEligibleStates = 0;
		List<String> bopusInEligibleStore = getCatalogTools()
				.getBopusInEligibleStores(storeType, siteId);
		final ThresholdVO skuThresholdVO = getCatalogTools().getSkuThreshold(siteId,
				skuId);
		for (final String stDetails : storeIds) {
			if (BBBUtility.isListEmpty(bopusInEligibleStore) || (!bopusInEligibleStore.contains(stDetails))) {
				logDebug(" SearchStoreManager.getStoreWithInventoryByStoreId bopusInEligibleStore is for storeId"
						+ bopusInEligibleStore + " for " + stDetails);
				countEligibleStates++;
				try {
					localStoreVO = getInventoryFromLocalStore(stDetails,
							req.getParameter(BBBCoreConstants.SKUID));
					if(localStoreVO == null && BBBCoreConstants.TRUE.equalsIgnoreCase(getLocalStoreDbKey()))
					{
						localStoreVO = getStoreTools().getInventoryForStoreFromDb(skuId, stDetails);
					} 
				} catch (Exception e) {
					// Fix is required for general Exception. 
					logError("Excpetion",e);
					throw new BBBBusinessException("Error from Inventory");
				}
				
				
				if (localStoreVO != null) {
					int inventory = 0;

					inventory = localStoreVO.getStockLevel();

					int inventoryStatus = (((BBBInventoryManagerImpl) getInventoryManager())
							.getInventoryStatus(inventory, productQty,
									skuThresholdVO, localStoreVO.getStoreId()));

					logDebug(" SearchStoreManager.getStoreWithInventoryByStoreId inventoryStatus is"
							+ inventoryStatus);
					if (inventoryStatus == BBBInventoryManager.AVAILABLE
							|| inventoryStatus == BBBInventoryManager.LIMITED_STOCK) {
						countInventoryStatus++;
						availStoreId = stDetails;
						break;
					}
				}
			}
		}

		if (countInventoryStatus == 0 || countEligibleStates == 0) {

			logDebug(" SearchStoreManager.getStoreWithInventoryByStoreId countInventoryStatus, countEligibleStates is"
					+ countInventoryStatus + "," + countEligibleStates);

			return null;
		}

		logDebug(" SearchStoreManager.getStoreWithInventoryByStoreId Ends and availStoreId is"
				+ availStoreId);
		return availStoreId;

	}
	
	
	
	/**This method returns eligible store having inventory status as Available/Limited
	 * @param lStoreDetails
	 * @param req
	 * @param res
	 * @param siteId
	 * @return Store id
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws IOException
	 */
	public String getStoreWithInventoryByStoreIdChanged(final List<String> storeIds,
			final DynamoHttpServletRequest req, final DynamoHttpServletResponse res,
			String siteId) throws BBBBusinessException, BBBSystemException,
			RepositoryException, ServletException, IOException {

		logDebug("Inside  SearchInStoreDroplet.getStoreWithInventoryByStoreId Starts, storeId and siteId is"
				+ storeIds + "," + siteId);
		String storeType = getStoreType(siteId);
		String availStoreId = null;
		long productQty = 1;
		//	LocalStoreVO localStoreVO = null;
		String skuId = req.getParameter(BBBCoreConstants.SKUID);
		//int countInventoryStatus = 0;
		//int countEligibleStates = 0;
		/*List<String> bopusInEligibleStore = getCatalogTools()
				.getBopusInEligibleStores(storeType, siteId);*/
		/*final ThresholdVO skuThresholdVO = getCatalogTools().getSkuThreshold(siteId,
				skuId);*/
		for (final String stDetails : storeIds) {
			
			boolean result = checkStoreHasInventoryForSkuId(stDetails, skuId, productQty, siteId);
			if(result){
				
				availStoreId = stDetails;
				break;
			}
			
			/*
			if ((!BBBUtility.isListEmpty(bopusInEligibleStore) && !bopusInEligibleStore
					.contains(storeIds))
					|| BBBUtility.isListEmpty(bopusInEligibleStore)) {
				logDebug(" SearchInStoreDroplet.getStoreWithInventoryByStoreId bopusInEligibleStore is for storeId"
						+ bopusInEligibleStore + " for " + stDetails);
				countEligibleStates++;
				localStoreVO = getInventoryFromLocalStore(stDetails,
						req.getParameter(BBBCoreConstants.SKUID));
				if (localStoreVO != null) {
					int inventory = 0;

					inventory = localStoreVO.getStockLevel();

					int inventoryStatus = (((BBBInventoryManagerImpl) getInventoryManager())
							.getInventoryStatus(inventory, productQty,
									skuThresholdVO, localStoreVO.getStoreId()));

					logDebug(" SearchInStoreDroplet.getStoreWithInventoryByStoreId inventoryStatus is"
							+ inventoryStatus);
					if (inventoryStatus == BBBInventoryManager.AVAILABLE
							|| inventoryStatus == BBBInventoryManager.LIMITED_STOCK) {
						countInventoryStatus++;
						availStoreId = stDetails;
						break;
					}
				}
			}
		*/}

		/*if (countInventoryStatus == 0 || countEligibleStates == 0) {

			logDebug(" SearchInStoreDroplet.getStoreWithInventoryByStoreId countInventoryStatus, countEligibleStates is"
					+ countInventoryStatus + "," + countEligibleStates);

			return null;
		}*/

		logDebug(" SearchInStoreDroplet.getStoreWithInventoryByStoreId Ends and availStoreId is"
				+ availStoreId);
		return availStoreId;

	}
	
	/**
	 * This method will get all the stores with available inventory, given the
	 * string StoreId.
	 * 
	 * @param storeIds
	 * @param req
	 * @param res
	 * @param siteId
	 * @returnv
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean checkStoreHasInventoryForSkuId(String storeId, String skuId, Long productQty, String siteId)
			throws BBBBusinessException, BBBSystemException, RepositoryException, ServletException, IOException {
		logDebug("checkStoreHasInventoryForSkuId STARTS storeId :: " + storeId + " skuId :: " + skuId
				+ " productQty :: " + productQty + "siteId :: " + siteId);
		String storeType = getStoreType(siteId);
		LocalStoreVO localStoreVO = null;

		List<String> bopusInEligibleStore = getCatalogTools().getBopusInEligibleStores(storeType, siteId);
		ThresholdVO skuThresholdVO = getCatalogTools().getSkuThreshold(siteId, skuId);

		if ((!BBBUtility.isListEmpty(bopusInEligibleStore) && !bopusInEligibleStore.contains(storeId))
				|| BBBUtility.isListEmpty(bopusInEligibleStore)) {

			try {
				localStoreVO = getInventoryFromLocalStore(storeId, skuId);
				
				//get from db if not found in coherence
				if(localStoreVO == null && BBBCoreConstants.TRUE.equalsIgnoreCase(getLocalStoreDbKey()))
				{
					localStoreVO = getStoreTools().getInventoryForStoreFromDb(skuId, storeId);
				}
				
			} catch (Exception e) {
				// Fix is required for general Exception.
				logError("Excpetion",e);
				throw new BBBBusinessException("Error from Inventory");
			}
			if (localStoreVO != null) {
				int inventory = 0;

				inventory = localStoreVO.getStockLevel();
				logDebug("Inventory :: " + inventory);

				int inventoryStatus = (((BBBInventoryManagerImpl) getInventoryManager()).getInventoryStatus(inventory,
						productQty, skuThresholdVO, localStoreVO.getStoreId()));
				logDebug("inventory Status :: " + inventoryStatus);
				if (inventoryStatus == BBBInventoryManager.AVAILABLE
						|| inventoryStatus == BBBInventoryManager.LIMITED_STOCK) {

					logDebug("inventory Found returning true ");
					return true;

				}
			}
		}

		logDebug("checkStoreHasInventoryForSkuId ENDS, possibly no inventory found");
		return false;

	}
	
	

	public LocalStoreVO getInventoryFromLocalStore(String storeId , String skuId) throws Exception
	{
		LocalStoreVO localStoreVO = getStoreTools().getInventoryFromLocalStore(storeId, skuId);
		return localStoreVO;
	}

	/**
	 * This method will create a search string based on StoreId and call
	 * searchStore method
	 * 
	 * @param pStoreId
	 * @return StoreDetails
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */

	public StoreDetails searchStoreById(String pStoreId,String pSiteId, String pStoreType)
			throws BBBBusinessException, BBBSystemException {
		
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		StoreDetails objStoreDetails = null;
		objStoreDetailsWrapper = getStoreTools().getStoresByStoreId(pStoreId  , pStoreType , false);
		/*StringBuilder searchStringBuilder = new StringBuilder();
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		StoreDetails objStoreDetails = null;
		searchStringBuilder.append(getMapQuestRecordInfoString());
		searchStringBuilder.append("%7C");
		searchStringBuilder.append("%22RecordId%22");
		searchStringBuilder.append("=?");
		searchStringBuilder.append("%7C");
		searchStringBuilder.append(pStoreId);
		searchStringBuilder.append("%7C");
		searchStringBuilder.append(BBBCoreConstants.AMPERSAND).append(BBBCoreConstants.STORE_TYPE + pStoreType);
		searchStringBuilder.append(BBBCoreConstants.AMPERSAND).append(SelfServiceConstants.MAPQUEST_COLUMN_LIST);
		objStoreDetailsWrapper = searchStore( pStoreType , searchStringBuilder.toString());*/
		if (null != objStoreDetailsWrapper
				&& objStoreDetailsWrapper.getStoreDetails() != null
				&& objStoreDetailsWrapper.getStoreDetails().size() > 0) {
			objStoreDetails = objStoreDetailsWrapper.getStoreDetails().get(0);
		}
		return objStoreDetails;
	}

	
	public List<StoreDetails> searchStoresById(String pStoreId,String pSiteId, String pStoreType)
			throws BBBBusinessException, BBBSystemException {
		if(pStoreType == null)
		{
			pStoreType = getStoreType(pSiteId);
		}
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		List<StoreDetails> objStoreDetails = null;
		objStoreDetailsWrapper = getStoreTools().getStoresByStoreId(pStoreId  , pStoreType , true);
		if (null != objStoreDetailsWrapper
				&& objStoreDetailsWrapper.getStoreDetails() != null
				&& objStoreDetailsWrapper.getStoreDetails().size() > 0) {
			objStoreDetails = objStoreDetailsWrapper.getStoreDetails();
		}
		return objStoreDetails;
	}
	
	/**
	 * This method will create new Lat/Lng Cookie or update existing Lat/Lng Cookie based on Lat/Lng derived using StoreID and SiteID
	 * 
	 * @param storeIdFromURL
	 * @return latLng
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	
	public String modifyLatLngCookie (String storeIdFromURL , String pSiteId)
			throws BBBBusinessException, BBBSystemException {
	String storeType;
	StringBuffer latLng = new StringBuffer();
    String lat = BBBCoreConstants.BLANK;
    String lng = BBBCoreConstants.BLANK;
    if(isLoggingDebug()){
    	logDebug("Inside  SearchStoreManager.modifyLatLngCookie Starts, storeId and siteId is : " + storeIdFromURL + "," + pSiteId);
    }
	if (storeIdFromURL != null && !storeIdFromURL.isEmpty()) {
		storeType = getStoreType(pSiteId);
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
	
	/**
	 * This method will create a search string based on Address and call
	 * searchStore method
	 * 
	 * @param pSearchCriteria
	 * @param searchType
	 * @return StoreDetailsWrapper
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	public StoreDetailsWrapper searchStoreByAddress(String pSearchCriteria,
			String searchType, String pageSize) throws BBBBusinessException,
			BBBSystemException {
		StringBuilder searchStringBuilder = new StringBuilder();
		String searchKeyURL = null;
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		if (!BBBUtility.isEmpty(searchType)
				&& !BBBUtility.isEmpty(pSearchCriteria)) {
			pSearchCriteria = pSearchCriteria.replace(BBBCoreConstants.SPACE,
					"%20");
			searchKeyURL = getSearchKeyURL(searchType);
			searchStringBuilder.append(searchKeyURL);
			searchStringBuilder.append(pSearchCriteria);
			objStoreDetailsWrapper = searchStore(searchType,
					searchStringBuilder.toString());
		}

/*if (!BBBUtility.isEmpty(searchType)
		&& !BBBUtility.isEmpty(pSearchCriteria)) {
	searchKeyURL = getSearchKeyURL(searchType);
	searchStringBuilder.append(searchKeyURL);
	searchStringBuilder.append("%7C");
	searchStringBuilder.append(pSearchCriteria);
	searchStringBuilder.append(AMPERSAND);
	searchStringBuilder.append(PAGESIZE);
	if (pageSize == null) {
		searchStringBuilder.append(getPageSize());
		pageSize = getPageSize();
	} else {
		searchStringBuilder.append(pageSize);
	}
	objStoreDetailsWrapper = searchStore(pageSize , searchType , searchStringBuilder.toString());
}*/
		
		return objStoreDetailsWrapper;
	}
	
	/**
	 * This method will create a search string based on Coordiantes and call
	 * searchStore method
	 * 
	 * @param pSearchCriteria
	 * @param searchType
	 * @return StoreDetailsWrapper
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	public StoreDetailsWrapper searchStoreByCoordinates(String pSearchCriteria,String searchType, String pageSize) throws BBBBusinessException, BBBSystemException {

		StringBuilder searchStringBuilder = new StringBuilder();
		//String searchKeyURL = null;
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		if (!BBBUtility.isEmpty(searchType) && !BBBUtility.isEmpty(pSearchCriteria)){
			/*searchKeyURL = getSearchKeyURL(searchType);
			searchStringBuilder.append(searchKeyURL);
			searchStringBuilder.append("%7C");
			searchStringBuilder.append(pSearchCriteria);
			searchStringBuilder.append(AMPERSAND);
			searchStringBuilder.append(PAGESIZE);
			if (pageSize == null) {
				searchStringBuilder.append(getPageSize());
			} else {
				searchStringBuilder.append(pageSize);
			}

			if (pageSize == null) {
				pageSize = getPageSize();
			}*/
			objStoreDetailsWrapper = searchStore(searchType , searchStringBuilder.toString());
		}
		return objStoreDetailsWrapper;
	}
	
	/**This method fetches details based on lat/lng 
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	public List<StoreDetails> searchStoreByLatLng(String latitude,
			String longitude , String storeType) throws BBBBusinessException, BBBSystemException {
		StoreDetailsWrapper storeDetailsWrapper = getStoreTools()
				.getStoreDetails(latitude, longitude, storeType);
		if (storeDetailsWrapper != null) {
			return storeDetailsWrapper.getStoreDetails();
		} else {
			return null;
		}
	}

	/**
	 * This method will create a search string based on Pagination and call
	 * searchStore method
	 * 
	 * @param pPageKey
	 * @param pPageNumber
	 * @return StoreDetailsWrapper
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	public StoreDetailsWrapper searchStorePerPage(String pPageKey,
			String pPageNumber) throws BBBBusinessException, BBBSystemException {
		StringBuilder searchStringBuilder = new StringBuilder();
		StoreDetailsWrapper objStoreDetailsWrapper = null;
		if (!BBBUtility.isEmpty(pPageKey) && !BBBUtility.isEmpty(pPageNumber)) {
			searchStringBuilder.append(getMapQuestPaginationString());
			searchStringBuilder.append(AMPERSAND).append(PAGEKEY).append(pPageKey)
					.append(AMPERSAND).append(CURRENTPAGE).append(pPageNumber);
			objStoreDetailsWrapper = searchStore(null, searchStringBuilder.toString(), false );
		}
		return objStoreDetailsWrapper;
	}

	/**
	 * This method will return MapQuest URL based on search type
	 * 
	 * @param pSearchType
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	private String getSearchKeyURL(String pSearchType) throws BBBSystemException, BBBBusinessException {
		String searchKeyURL = null;
		if (!BBBUtility.isEmpty(pSearchType)) {
			if (pSearchType.equals(SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH)) {
				//searchKeyURL = getMapQuestSearchString();
				searchKeyURL = getMapQuestSearchStringForLatLng();
				//searchKeyURL="http://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6120u8nh%2C2w%3Do5-lwt2l&";
			} else if (pSearchType.equals(SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH)) {
				//searchKeyURL = getMapQuestRadiusString();
				searchKeyURL = getMapQuestSearchStringForLatLng();
				//searchKeyURL="http://www.mapquestapi.com/geocoding/v1/address?key=Gmjtd%7Clu6120u8nh%2C2w%3Do5-lwt2l&";
				
			}else if (pSearchType.equals(SelfServiceConstants.COORDINATES_BASED_SEARCH)){
			    searchKeyURL = getMapQuestCoordinateString();
			}

		}
		return searchKeyURL;
	}

	public RouteDetails getStoreDirections(String pStartPoints,
			String pEndPoints, String pRouteType, boolean isSeasonalRoad,
			boolean isHighWays, boolean isTollRoad)throws BBBBusinessException, BBBSystemException {
		StringBuilder p2pDirectionsString = new StringBuilder();
		RouteDetails objP2PRouteDetails = null;
		p2pDirectionsString.append(getMapQuestDirectionsString())
				.append(AMPERSAND+TO).append(pEndPoints).append(AMPERSAND+FROM)
				.append(pStartPoints);
		p2pDirectionsString.append(AMPERSAND+ROUTETYPE).append(
				!BBBUtility.isEmpty(pRouteType) ? pRouteType
						: SelfServiceConstants.ROUTE_FASTEST);
		if (isSeasonalRoad) {
			p2pDirectionsString.append(AMPERSAND+AVOIDS).append(
					SelfServiceConstants.AVOIDS_SEASONAL_ROADS);
		}
		if (isHighWays) {
			p2pDirectionsString.append(AMPERSAND+AVOIDS).append(
					SelfServiceConstants.AVOIDS_HIGHWAYS);
		}
		if (isTollRoad) {
			p2pDirectionsString.append(AMPERSAND+AVOIDS).append(
					SelfServiceConstants.AVOIDS_TOLLROAD);
		}
		try {
			//Store directions request to MapQuest Starts here
			BBBPerformanceMonitor.start("SearchStoreManager.getStoreDirections()-Invoke");
			objP2PRouteDetails = getStoreTools().storeDirections(
					p2pDirectionsString.toString());
			if (objP2PRouteDetails != null) {
				StringBuilder strRouteMap = new StringBuilder(getRouteMapKey());
				strRouteMap.append(AMPERSAND+SESSION)
						.append(objP2PRouteDetails.getSessionId())
						.append(AMPERSAND+SCENTER)
						.append(objP2PRouteDetails.getStartPointLat())
						.append(",")
						.append(objP2PRouteDetails.getStartPointLng())
						.append(AMPERSAND+ECENTER)
						.append(objP2PRouteDetails.getEndPointLat())
						.append(",")
						.append(objP2PRouteDetails.getEndPointLng())
						.append(AMPERSAND+SIZE).append(getStaticMapSize())
						.append(AMPERSAND+ZOOM).append(getStaticMapZoom());
				objP2PRouteDetails.setRouteMap(strRouteMap.toString());
			}

		} catch (ClientProtocolException e) {
			logError(LogMessageFormatter.formatMessage(null, "ClientProtocolException in SearchStoreManager while get storeDirections ", BBBCoreErrorConstants.ACCOUNT_ERROR_1226 ), e);
		} catch (IOException e) {
			logError(LogMessageFormatter.formatMessage(null, "IOException in SearchStoreManager while get storeDirections ", BBBCoreErrorConstants.ACCOUNT_ERROR_1227 ), e);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in SearchStoreManager while get storeDirections ", BBBCoreErrorConstants.ACCOUNT_ERROR_1228 ), e);
				throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1324,"IOException---", e);
			} finally {
				BBBPerformanceMonitor.end("SearchStoreManager.getStoreDirections()-Invoke");				
			}
		/*
		 * if (null != objP2PRoute && objP2PRoute.getManeuvers() != null &&
		 * objP2PRoute.getManeuvers().size() > 0) { objStoreDetails =
		 * objStoreDetailsWrapper.getStoreDetails().get(0); }
		 */
		return objP2PRouteDetails;

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
	 * @return the mapQuestSearchString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestCoordinateString() throws BBBSystemException, BBBBusinessException {
		
	    String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		return configValue;
	}
	
	/**
	 * @return the mapQuestRadiusString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestRadiusString() throws BBBSystemException, BBBBusinessException {
		
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestRadiusString");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		return configValue;
	}


	/**
	 * @return the mapQuestRecordInfoString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestRecordInfoString() throws BBBSystemException, BBBBusinessException {
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestRecordInfoString");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		return configValue;
	}


	/**
	 * @return the mapQuestSearchString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestSearchString() throws BBBSystemException, BBBBusinessException {
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		return configValue;
	}

	/**
	 * @return the mapQuestSearchString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestSearchStringForLatLng() throws BBBSystemException, BBBBusinessException {
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchStringForLatLng");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		return configValue;
	}

	/**
	 * @return the mapQuestPaginationString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestPaginationString() throws BBBSystemException, BBBBusinessException {
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestPaginationString");
		if(!BBBUtility.isListEmpty(keysList))
		{
			configValue =  keysList.get(0);
		}
		else
		{
			logDebug("Map quest Config Key is empty for " +BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR);
		}
		return configValue;
	}

	/**
	 * @return the pageSize
	 */
	public String getPageSize() {
		return mPageSize;
	}

	/**
	 * @param pPageSize
	 *            the pageSize to set
	 */
	public void setPageSize(String pPageSize) {
		mPageSize = pPageSize;
	}

	/**
	 * @return the mapQuestDirectionsString
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getMapQuestDirectionsString() throws BBBSystemException, BBBBusinessException {
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "mapQuestDirectionsString");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		return configValue;
	}


	/**
	 * @return the staticMapZoom
	 */
	public String getStaticMapZoom() {
		return mStaticMapZoom;
	}

	/**
	 * @param pStaticMapZoom
	 *            the staticMapZoom to set
	 */
	public void setStaticMapZoom(String pStaticMapZoom) {
		mStaticMapZoom = pStaticMapZoom;
	}

	/**
	 * @return the routeMapKey
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getRouteMapKey() throws BBBSystemException, BBBBusinessException {
		String configValue = null;
		List<String> keysList = getCatalogTools().getAllValuesForKey("ThirdPartyURLs", "routeMapKey");
		if(keysList != null && keysList.size()>0)
		{
			configValue =  keysList.get(0);
		}
		return configValue;
	}

	/**
	 * @return the staticMapSize
	 */
	public String getStaticMapSize() {
		return mStaticMapSize;
	}

	/**
	 * @param pStaticMapSize
	 *            the staticMapSize to set
	 */
	public void setStaticMapSize(String pStaticMapSize) {
		mStaticMapSize = pStaticMapSize;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}


	
	/**
	 * Gets the siteid meant for mapquestl 
	 * @param pSiteId
	 * @return String
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public String getStoreType(String pSiteId) throws BBBSystemException, BBBBusinessException {
		if (pSiteId != null) {
			List<String> siteIds = getCatalogTools().getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE, pSiteId);
			if (siteIds == null || siteIds.get(BBBCoreConstants.ZERO).isEmpty() ){
				logError(LogMessageFormatter.formatMessage(null, "No Value found for Key "+pSiteId+" in Config Type "+BBBCoreConstants.MAPQUESTSTORETYPE+" passed to getAllValuesForKey() method", BBBCoreErrorConstants.ACCOUNT_ERROR_1229 ));
				throw new BBBSystemException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
			}
			return siteIds.get(BBBCoreConstants.ZERO);
		}
		logError(LogMessageFormatter.formatMessage(null, "No Value found for Key "+pSiteId+" in Config Type "+BBBCoreConstants.MAPQUESTSTORETYPE+" passed to getAllValuesForKey() method", BBBCoreErrorConstants.ACCOUNT_ERROR_1229 ));
		throw new BBBSystemException (BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE,BBBCatalogErrorCodes.CONFIG_KEYS_NOT_AVAILABLE);
	}
	
	
	/**
	 * This method searches stores on the basis of Zip code / address entered by
	 * user
	 * 
	 * @param searchString
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws InventoryException 
	 */
	public void getStoresBySearchString(String searchString,
			DynamoHttpServletRequest req, DynamoHttpServletResponse res, boolean callFromPLP)
			throws Exception {
			// search should not be based on cookie
			req.getSession().setAttribute(
					SelfServiceConstants.SEARCH_BASED_ON_COOKIE,
					BBBCoreConstants.FALSE);
			req.getSession().setAttribute(BBBCoreConstants.STORE_TYPE , getStoreType(SiteContextManager.getCurrentSiteId()));
			StringBuilder searchStringBuilder = new StringBuilder();
			// search string to get lat/lng from map quest
			searchStringBuilder.append(SelfServiceConstants.LOCATION_EQUALS);
			searchStringBuilder.append(searchString);

			// getting stores based on zip code/address
			StoreDetailsWrapper storeDetailsWrapper = searchStoreByAddress(searchStringBuilder.toString(),
							SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH,
							null);
			// getting store inventory map to display appropriate message based
			// on inventory
			if (storeDetailsWrapper != null
					&& !BBBUtility.isListEmpty(storeDetailsWrapper
							.getStoreDetails())) {
				List<StoreDetails> alStoreDetails = storeDetailsWrapper
						.getStoreDetails();
				if(BBBCoreConstants.TRUE.equalsIgnoreCase(getSupplyBalanceKey()) && !callFromPLP)
				{
				checkProductAvailability(alStoreDetails, req, res,
						"productAvailableViewStore", "outputViewStore",
						"storeDetails", false, "viewStoreEmpty");
				}
				else
				{
					req.setParameter(BBBCoreConstants.STORE_DETAILS , alStoreDetails);
					req.setParameter(BBBCoreConstants.CALL_STORE, BBBCoreConstants.TRUE);
					req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
							.getAttribute(SelfServiceConstants.RADIUSMILES));
					req.serviceLocalParameter(BBBCoreConstants.OUTPUT_VIEW_STORE, req, res);
				}
			} else {
				req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
						.getAttribute(SelfServiceConstants.RADIUSMILES));
				req.serviceLocalParameter("viewStoreEmpty", req, res);
			}
	}

	/**
	 * This method gets favorite store / nearest store to fav store having
	 * inventory
	 * 
	 * @param siteId
	 * @param req
	 * @param res
	 * @param favoriteStoreId
	 * @throws Exception 
	 */
	public void getFavStoreByStoreId(String siteId,
			DynamoHttpServletRequest req, DynamoHttpServletResponse res,
			String favoriteStoreId) throws Exception {
		StoreDetails storeDetails = null;
		List<StoreDetails> stDetails = null;
		// String favoriteStoreId = getFavStoreId(siteId);
		if(BBBCoreConstants.TRUE.equalsIgnoreCase(getLocalStorePDPKey()))
		{
			if (!BBBUtility.isEmpty(favoriteStoreId)) {

				// getting favorite store having stock for the sku.

				storeDetails = fetchFavStoreDetailsForPDP(favoriteStoreId, siteId,
						req, res);
				if (req.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) == null &&
						storeDetails == null) {
					req.serviceLocalParameter(BBBCoreConstants.EMPTY_FAV_STORE, req, res);
				} 
				else if(req.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) != null)
				{
					return;
				}
				else {
					if (storeDetails != null) {
						stDetails = new ArrayList<StoreDetails>();
						stDetails.add(storeDetails);
						req.setParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS, stDetails);
						req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
								.getAttribute(SelfServiceConstants.RADIUSMILES));
						req.serviceLocalParameter(BBBCoreConstants.OUTPUT_FAV_STORE, req, res);
					} 
					else {
						req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
								.getAttribute(SelfServiceConstants.RADIUSMILES));
						req.serviceLocalParameter(BBBCoreConstants.EMPTY_FAV_STORE, req, res);

					}
				}
			}
		}
		else
		{
			req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
					.getAttribute(SelfServiceConstants.RADIUSMILES));
			req.setParameter(BBBCoreConstants.LOCAL_STORE_OFF, BBBCoreConstants.TRUE);
			req.serviceLocalParameter(BBBCoreConstants.OUTPUT_FAV_STORE, req, res);
		}
	}
	
	/**
	 * This method gets favorite store / nearest store to fav store for PLP 
	 * 
	 * @param siteId
	 * @param req
	 * @param res
	 * @param favoriteStoreId
	 * @throws Exception 
	 */
	public void getFavStoreByStoreIdForPLP(String siteId,
			DynamoHttpServletRequest req, DynamoHttpServletResponse res,
			String favoriteStoreId) throws Exception {
		StoreDetails storeDetails = null;
		List<StoreDetails> stDetails = null;
		List<StoreDetails> lStoreDetails = new ArrayList<StoreDetails>();
		// String favoriteStoreId = getFavStoreId(siteId);
			if (!BBBUtility.isEmpty(favoriteStoreId)) {
				
				String storeType = getStoreType(siteId);
				// getting favorite store having stock for the sku.
				
				RepositoryItem[] storeItems = getStoreTools().getStores(
						favoriteStoreId, storeType);
				if(storeItems!=null){
					for (RepositoryItem repositoryItem : storeItems) {
						lStoreDetails = getStoreTools().addStoreDetailsToList(
								repositoryItem, lStoreDetails, null, null,
								favoriteStoreId, ((BBBCatalogToolsImpl)getCatalogTools()).getStoreRepository(), null);
						if(!BBBUtility.isListEmpty(lStoreDetails))
						storeDetails = lStoreDetails.get(0);
					}
				}
					if (storeDetails != null) {
						stDetails = new ArrayList<StoreDetails>();
						stDetails.add(storeDetails);
						req.setParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS, stDetails);
						req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
								.getAttribute(SelfServiceConstants.RADIUSMILES));
						req.serviceLocalParameter(BBBCoreConstants.OUTPUT_FAV_STORE, req, res);
					} 
					else {
						req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
								.getAttribute(SelfServiceConstants.RADIUSMILES));
						req.serviceLocalParameter(BBBCoreConstants.EMPTY_FAV_STORE, req, res);

					}
				//}
			}

	}

	/**
	 * This method holds logic to get store with inventory based on lat/lng
	 * 
	 * @param siteId
	 * @param req
	 * @param res
	 * @param latitude
	 * @param longitude
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws ServletException
	 * @throws IOException
	 * @throws  
	 */
	public void getFavStoreByCoordinates(String siteId,
			DynamoHttpServletRequest req, DynamoHttpServletResponse res,
			String latitude, String longitude, boolean callFromPLP) throws Exception {
			
		if(BBBCoreConstants.TRUE.equalsIgnoreCase(getLocalStorePDPKey()) || callFromPLP)
		{
			List<StoreDetails> alStoreDetails = null;
			StoreDetails storeDetails = null;
			List<StoreDetails> stDetails = new ArrayList<StoreDetails>();
			String storeType = getStoreType(siteId);
			alStoreDetails = getStoreDetailsByLatLng(req, res, latitude,
					longitude , storeType);
			req.getSession().setAttribute(BBBCoreConstants.STORE_TYPE , storeType);
			// Get store with inventory
			if (!BBBUtility.isListEmpty(alStoreDetails) && !callFromPLP ) {
				storeDetails = getStoreWithInventory(
						alStoreDetails, req, res, siteId,storeType);
				
				if (req.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) == null &&
						storeDetails == null) {
					req.serviceLocalParameter(BBBCoreConstants.EMPTY_FAV_STORE, req, res);
				} else if (req
						.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) != null) {
					return;
				}
				else {
					if (storeDetails != null) {
						stDetails = new ArrayList<StoreDetails>();
						stDetails.add(storeDetails);
						req.setParameter(BBBCoreConstants.FAV_STORE_DETAIL, stDetails);
						req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
								.getAttribute(SelfServiceConstants.RADIUSMILES));
						req.serviceLocalParameter(BBBCoreConstants.OUTPUT_FAV_STORE, req, res);
					} else {
						req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
								.getAttribute(SelfServiceConstants.RADIUSMILES));
						req.serviceLocalParameter(BBBCoreConstants.EMPTY_FAV_STORE, req, res);
					}
				}
			}else if (!BBBUtility.isListEmpty(alStoreDetails) && callFromPLP ) {				
						//stDetails.add(storeDetails);
						req.setParameter(BBBCoreConstants.FAV_STORE_DETAIL, alStoreDetails);
						req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
								.getAttribute(SelfServiceConstants.RADIUSMILES));
						req.serviceLocalParameter(BBBCoreConstants.OUTPUT_FAV_STORE, req, res);
					}
				else {
				req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
						.getAttribute(SelfServiceConstants.RADIUSMILES));
				req.serviceLocalParameter(BBBCoreConstants.EMPTY_FAV_STORE, req, res);
			}
		}
		else
		{
			req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
					.getAttribute(SelfServiceConstants.RADIUSMILES));
			req.setParameter(BBBCoreConstants.LOCAL_STORE_OFF, BBBCoreConstants.TRUE);
			req.serviceLocalParameter(BBBCoreConstants.OUTPUT_FAV_STORE, req, res);
		}
	}

	/**
	 * This method get stores near to favorite stores (searched using lat/lng)
	 * within default radius
	 * 
	 * @param latitude
	 * @param longitude
	 * @param req
	 * @param res
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws ServletException
	 * @throws IOException
	 * @throws  
	 */
	public void getNearestStoresByCoordinates(String latitude,
			String longitude, DynamoHttpServletRequest req,
			DynamoHttpServletResponse res, boolean callFromPLP) throws Exception {

		String storeType =  getStoreType(SiteContextManager.getCurrentSiteId());
			List<StoreDetails> alStoreDetails = getStoreDetailsByLatLng(req,
					res, latitude, longitude , storeType);
			
			if (BBBUtility.isListEmpty(alStoreDetails) && !(BBBCoreConstants.TRUE.equals(req.getParameter(SelfServiceConstants.RADIUS_CHANGE))))
			{
				req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES,
						getDefaultRadius());
				alStoreDetails = getStoreDetailsByLatLng(req, res, latitude,
						longitude , storeType);
			}
			if (!BBBUtility.isListEmpty(alStoreDetails) && !callFromPLP) {
				if(BBBCoreConstants.TRUE.equalsIgnoreCase(getSupplyBalanceKey()))
				{
					checkProductAvailability(alStoreDetails, req, res,
							BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE, BBBCoreConstants.OUTPUT_VIEW_STORE,
							BBBCoreConstants.STORE_DETAILS, false, BBBCoreConstants.EMPTY_VIEW_STORE);
				}
				else
				{
					req.setParameter(BBBCoreConstants.STORE_DETAILS , alStoreDetails);
					req.setParameter(BBBCoreConstants.CALL_STORE, BBBCoreConstants.TRUE);
					req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
							.getAttribute(SelfServiceConstants.RADIUSMILES));
					req.serviceLocalParameter(BBBCoreConstants.OUTPUT_VIEW_STORE, req, res);
				}
			}if (!BBBUtility.isListEmpty(alStoreDetails) && callFromPLP) {

					req.setParameter(BBBCoreConstants.STORE_DETAILS , alStoreDetails);
					req.setParameter(BBBCoreConstants.CALL_STORE, BBBCoreConstants.TRUE);
					req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
							.getAttribute(SelfServiceConstants.RADIUSMILES));
					req.serviceLocalParameter(BBBCoreConstants.OUTPUT_VIEW_STORE, req, res);

			} else {
				req.setParameter(BBBCoreConstants.RADIUS, req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES));
				req.serviceLocalParameter(BBBCoreConstants.EMPTY_VIEW_STORE, req, res);
			}

	}

	/**
	 * This method holds logic to get stores near to favorite stores
	 * 
	 * @param storeId
	 * @param req
	 * @param res
	 * @param favStoreId
	 * @throws ServletException
	 * @throws IOException
	 * @throws  
	 * @throws BBBBusinessException 
	 */
	public void getNearestStoresByStoreId(String storeId,
			DynamoHttpServletRequest req, DynamoHttpServletResponse res,
			String favStoreId, boolean callFromPLP) throws Exception {
			String siteId = req.getParameter(BBBCoreConstants.SITE_ID);

			// setting favorite store id in req parameter . Based on this id ,
			// favorite store will be set as first record in the list

			req.setAttribute(BBBCoreConstants.FAVOURITE_STORE_ID, favStoreId);
			// get nearest store based on fav store Id
			List<StoreDetails> alStoreDetails = searchStoresById(storeId, siteId, null);
			if (BBBUtility.isListEmpty(alStoreDetails) && !((BBBCoreConstants.TRUE).equals(req.getParameter(SelfServiceConstants.RADIUS_CHANGE)))) {
				req.getSession().setAttribute(SelfServiceConstants.RADIUSMILES,
						getDefaultRadius());
				alStoreDetails = searchStoresById(
						storeId, siteId, null);
			}
			if (!BBBUtility.isListEmpty(alStoreDetails) && !callFromPLP) {
				if(BBBCoreConstants.TRUE.equalsIgnoreCase(getSupplyBalanceKey()))
				{
					checkProductAvailability(alStoreDetails, req, res,
							BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE, BBBCoreConstants.OUTPUT_VIEW_STORE,
							BBBCoreConstants.STORE_DETAILS, false, BBBCoreConstants.EMPTY_VIEW_STORE);
				}
				else
				{
					req.setParameter(BBBCoreConstants.STORE_DETAILS , alStoreDetails);
					req.setParameter(BBBCoreConstants.CALL_STORE, BBBCoreConstants.TRUE);
					req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
							.getAttribute(SelfServiceConstants.RADIUSMILES));
					req.serviceLocalParameter(BBBCoreConstants.OUTPUT_VIEW_STORE, req, res);
				}
			}if (!BBBUtility.isListEmpty(alStoreDetails) && callFromPLP) {
					req.setParameter(BBBCoreConstants.STORE_DETAILS , alStoreDetails);
					req.setParameter(BBBCoreConstants.CALL_STORE, BBBCoreConstants.TRUE);
					req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
							.getAttribute(SelfServiceConstants.RADIUSMILES));
					req.serviceLocalParameter(BBBCoreConstants.OUTPUT_VIEW_STORE, req, res);

			} else {
				req.setParameter(BBBCoreConstants.RADIUS, req.getSession().getAttribute(SelfServiceConstants.RADIUSMILES));
				req.serviceLocalParameter(BBBCoreConstants.EMPTY_VIEW_STORE, req, res);
			}
	}

	/**
	 * This method get stores by lat/lng
	 * 
	 * @param req
	 * @param res
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public List<StoreDetails> getStoreDetailsByLatLng(
			DynamoHttpServletRequest req, DynamoHttpServletResponse res,
			String latitude, String longitude , String storeType) throws BBBBusinessException,
			BBBSystemException {

		List<StoreDetails> alStoreDetails = searchStoreByLatLng(latitude, longitude , storeType);

		return alStoreDetails;

	}

	/**
	 * This method hold logic to get inventory status of each store
	 * 
	 * @param alStoreDetails
	 * @param req
	 * @param res
	 * @param productAvailable
	 * @param output
	 * @param storeDetails
	 * @param isFromLocalStore
	 * @param emptyOutput
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws InventoryException
	 */
	public void checkProductAvailability(List<StoreDetails> alStoreDetails,
			DynamoHttpServletRequest req, DynamoHttpServletResponse res,
			String productAvailable, String output, String storeDetails,
			boolean isFromLocalStore, String emptyOutput)
			throws ServletException, IOException, InventoryException,
			BBBSystemException, BBBBusinessException {
		String skuId = req.getParameter(BBBCoreConstants.SKUID);
		String registryId = req.getParameter(BBBCoreConstants.REGISTRY_ID);
		String siteId = req.getParameter(BBBCoreConstants.SITE_ID);
		boolean isChangeStore = false;
		if (req.getParameter(CHANGE_CURRENT_STORE) != null) {
			isChangeStore = Boolean.valueOf(req
					.getParameter(CHANGE_CURRENT_STORE));
		}
		long productQty = 0L;
		if (req.getParameter(ORDEREDQTY) != null) {
			productQty = Long.parseLong(req.getParameter(ORDEREDQTY));
		}
		if (!BBBUtility.isListEmpty(alStoreDetails))
			logDebug(" Values return by StoreManager---- "
					+ alStoreDetails.toString());
		Map<String, Integer> productStatusMapForFavStore;

		productStatusMapForFavStore = checkProductAvailability(alStoreDetails,
				siteId, skuId, registryId, isChangeStore, productQty,
				BBBInventoryManager.STORE_STORE, req, isFromLocalStore);

		if (productStatusMapForFavStore != null
				&& productStatusMapForFavStore.size() > 0) {
			req.setParameter(storeDetails, alStoreDetails);
			req.setParameter(productAvailable, productStatusMapForFavStore);
			req.setParameter(BBBCoreConstants.RADIUS, req.getSession()
					.getAttribute(SelfServiceConstants.RADIUSMILES));
			req.serviceLocalParameter(output, req, res);
		} else {
			req.setParameter(
					BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE,
					getLblTxtTemplateManager().getErrMsg(
							BBBCoreConstants.INVENTORY_NOT_AVAILABLE,
							BBBCoreConstants.DEFAULT_LOCALE, null));
			req.setParameter(
					BBBCoreConstants.RADIUS,
					ServletUtil.getCurrentRequest().getSession()
							.getAttribute(SelfServiceConstants.RADIUSMILES));
			req.serviceParameter(
					BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE, req,
					res);
		}
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
			List<StoreDetails> pStoreDetails, String pSiteId, String pSkuId,
			String pRegistryId, boolean pChangeStore, long pReqQty,
			String operation, DynamoHttpServletRequest pRequest,
			boolean isFromLocalStore) throws InventoryException,
			BBBSystemException, BBBBusinessException {
		Map<String, Integer> productAvailStatus = new HashMap<String, Integer>();
		List<String> bopusEligibleStates = null;
		List<String> bopusInEligibleStore = null;
		bopusEligibleStates = getCatalogTools().getBopusEligibleStates(pSiteId);

		String pStoreId = getStoreType(pSiteId);
		bopusInEligibleStore = getCatalogTools().getBopusInEligibleStores(
				pStoreId, pSiteId);

		logDebug("Inside  SearchInStoreDroplet.checkProductAvailability---bopusInEligibleStore List "
				+ bopusInEligibleStore);
		logDebug("Inside  SearchInStoreDroplet.checkProductAvailability---BopusEligibleState List "
				+ bopusEligibleStates);
		
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
			} else if (bopusInEligibleStore != null
					&& bopusInEligibleStore.contains(storeDetails.getStoreId())) {
				// Store is not Bopus Eligible
				productAvailStatus.put(storeDetails.getStoreId(),
						SelfServiceConstants.STORE_PICKUP_NOT_AVAILABLE);
			}
			storeIds.add(storeDetails.getStoreId());

		}
		Map<String, Integer> storeInventoryMap;

		BBBStoreInventoryContainer storeInventoryContainer  =  (BBBStoreInventoryContainer) ServletUtil.getCurrentRequest().resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
		storeInventoryMap = getInventoryManager().getBOPUSProductAvailability(
				pSiteId, pSkuId, storeIds, pReqQty,
				BBBInventoryManager.STORE_STORE, storeInventoryContainer,
				false, pRegistryId, pChangeStore, isFromLocalStore);

		for (String storeId : storeInventoryMap.keySet()) {
			/*
			 * if(storeInventoryMap.get(storeId)==BBBInventoryManager.LIMITED_STOCK
			 * ) { storeInventoryMap.put(storeId,
			 * BBBInventoryManager.AVAILABLE); }
			 */
			Integer boupusStatus = productAvailStatus.get(storeId);
			Integer inventoryStatus = storeInventoryMap.get(storeId);
			if (boupusStatus != null && !inventoryStatus.toString().contains("-")) {
				// merge the No Bopus flag with Inventory
				String mergedData = boupusStatus.toString();
				if (inventoryStatus != null && inventoryStatus!=BBBInventoryManager.DUMMY_STOCK ) {
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

	/**
	 * This method gets default radius if stores are not foung within requested
	 * radius range
	 * 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getDefaultRadius() throws BBBSystemException,
			BBBBusinessException {
		List<String> lRadius = null;
		String radius = BBBCoreConstants.TWENTY_FIVE;
		lRadius = getCatalogTools().getAllValuesForKey(
				BBBCoreConstants.Default_Store_Type,
				BBBCoreConstants.CONFIG_KEY_PDP_RADIUS);

		if (!BBBUtility.isListEmpty(lRadius)) {
			radius = lRadius.get(0);
		} else {
			logDebug("Config key is emptyfor  "
					+ BBBCoreConstants.CONFIG_KEY_PDP_RADIUS);
		}
		return radius;
	}
	
	/**This method returns config key value of Supply Balance
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getSupplyBalanceKey() throws BBBSystemException, BBBBusinessException
	{
		String domOff = "false";
		List<String> lSupplyBalance = getCatalogTools().getAllValuesForKey(
				SelfServiceConstants.FLAG_DRIVEN_FUNCTIONS,
				SelfServiceConstants.SUPPLY_BALANCE_ON);

		if (!BBBUtility.isListEmpty(lSupplyBalance)) {
			domOff = lSupplyBalance.get(0);
		}
		else
		{
			logDebug("Supply Balance Config Key is empty for " +SelfServiceConstants.SUPPLY_BALANCE_ON);
		}
		return domOff;
	}
	
	/**This method returns config key of local store PDP
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getLocalStorePDPKey() throws BBBSystemException, BBBBusinessException
	{
		String localStorePDP = "false";
		List<String> lSupplyBalance = getCatalogTools().getAllValuesForKey(
				SelfServiceConstants.FLAG_DRIVEN_FUNCTIONS,
				SelfServiceConstants.LOCAL_STORE_PDP_FLAG);

		if (!BBBUtility.isListEmpty(lSupplyBalance)) {
			localStorePDP = lSupplyBalance.get(0);
		}
		else
		{
			logDebug("Local Store PDP Config Key is empty for " +SelfServiceConstants.LOCAL_STORE_PDP_FLAG);
		}
		return localStorePDP;
	}
	
	
	/**This method returns config key of local store PDP
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getLocalStoreDbKey() throws BBBSystemException, BBBBusinessException
	{
		logInfo("Method to check Global Flag Fr Db Call - Local Store starts here");
		String localStoreDbKey = "false";
		List<String> lLocalStoreDbKey = getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,BBBCoreConstants.LOCAL_STORE_FETCH);

		if (!BBBUtility.isListEmpty(lLocalStoreDbKey)) {
			localStoreDbKey = lLocalStoreDbKey.get(0);
		}
		else
		{
			logDebug("Local Store PDP Config Key is empty for " +SelfServiceConstants.LOCAL_STORE_PDP_FLAG);
		}
		
		if(isLoggingDebug())
		{
			logDebug("Local store Db Flag "+localStoreDbKey);
		}
		
		logInfo("Method to check Global Flag Fr Db Call - Local Store ends here");
		return localStoreDbKey;
	}
}
