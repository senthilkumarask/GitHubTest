package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.inventory.InventoryException;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBInventoryManagerImpl;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;

public class TBSNearbyStoreSearchDroplet extends DynamoServlet {
	
	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	/**
	 * mInventoryManager to hold BBBInventoryManagerImpl reference
	 */
	private BBBInventoryManagerImpl mInventoryManager;
	
	
	private List<String> mPageFromList;
	
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
	
	@SuppressWarnings({ "unused", "rawtypes" })
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		vlogDebug("TBSNearbySearchStoreDroplet :: service() method :: START");
		String sku = pRequest.getParameter("sku");
		String siteId = pRequest.getParameter("siteId");
		String pageFrom = pRequest.getParameter("pageFrom");
		String storeId = null;
		if(pRequest.getParameter("storeId")!=null){
			storeId	= pRequest.getParameter("storeId");
		} else {
			storeId	= (String)pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		}
		long productQty = 1L;
		if (pRequest.getParameter(TBSConstants.ORDEREDQTY) != null) {
			productQty = Long.parseLong(pRequest.getParameter(TBSConstants.ORDEREDQTY));
		}
		
		if (!BBBUtility.isEmpty(storeId)) {
			vlogDebug(" Search based on StoreId..." + storeId);
			
			StoreDetails objStoreDetails = null;
			Map latLongMap = null;
			Object changeRadius = null;
			int mStoreCountReturned = 0;
			List<StoreDetails> nearbyStoreDetails = new ArrayList<StoreDetails>();
			
			if(pRequest.getObjectParameter("latLongMap")!=null && !pRequest.getObjectParameter("latLongMap").equals("{}")){
				latLongMap = (Map) pRequest.getObjectParameter("latLongMap");
			}
			if(pRequest.getObjectParameter("changedRadius")!=null){
				changeRadius = pRequest.getObjectParameter("changedRadius");
			}
					
			//invoking the searchNearByStores method for getting the near by stores
			RepositoryItem[] storeList = null;
			RepositoryItem currentStoreItem = null;
			try {
				currentStoreItem = mSearchStoreManager.getStoreRepository().getItem(storeId, TBSConstants.STORE);
				if(latLongMap!=null && changeRadius != null){
					
					storeList = mSearchStoreManager.searchNearByStoresFromLatLong(latLongMap, changeRadius);

				}else{
					
					storeList = mSearchStoreManager.searchNearByStores(storeId);
				}
				RepositoryItem favItem = null;
				Profile profile = (Profile) pRequest.resolveName("/atg/userprofiling/Profile");
				if(!profile.isTransient()){
					Repository repository = profile.getProfileTools().getProfileRepository();
					RepositoryItem profileItem = repository.getItem(profile.getRepositoryId(), "user");
					Map lUserSitesMap = (Map) profileItem.getPropertyValue("userSiteItems");
					if(lUserSitesMap != null && !lUserSitesMap.isEmpty()){
						RepositoryItem userSiteAssoc = (RepositoryItem) lUserSitesMap.get(siteId);
						String favoriteStore = (String) userSiteAssoc.getPropertyValue("favouriteStoreId");
						if(!StringUtils.isEmpty(favoriteStore)){
							favItem = mSearchStoreManager.getStoreRepository().getItem(favoriteStore, TBSConstants.STORE);
						}
					}
					
				}
				if(storeList != null && storeList.length > 0){
					mStoreCountReturned = storeList.length;
					for (RepositoryItem repositoryItem : storeList) {
						//converting the storeItem into StoreDetails object
						if(latLongMap!=null){
							objStoreDetails = mSearchStoreManager.convertStoreItemToStore(repositoryItem, null, latLongMap);
						}else{
							objStoreDetails = mSearchStoreManager.convertStoreItemToStore(repositoryItem, currentStoreItem, null);
						}
						nearbyStoreDetails.add(objStoreDetails);
					}
				}
				if(favItem != null){
					objStoreDetails = mSearchStoreManager.convertStoreItemToStore(favItem, currentStoreItem, null);
					nearbyStoreDetails.add(0, objStoreDetails);
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
			
			if (nearbyStoreDetails.size() > TBSConstants.ZERO) {
				Map<String, Integer> storeInventoryMap = null;
				boolean warehouseFlag = false;
				boolean regionaStoreslFlag = false;
				boolean otherStoresFlag = false;
				try {
					BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer)
							pRequest.resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
					if(StringUtils.isEmpty(pageFrom)){
					storeInventoryMap = mSearchStoreManager.checkProductAvailability(nearbyStoreDetails, siteId, sku, null, false, productQty, 
							BBBInventoryManager.STORE_STORE, storeInventoryContainer, pRequest);
					vlogDebug("storeInventoryMap :: "+storeInventoryMap);
					}
					
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
				if(!nearbyStoreDetails.isEmpty() && nearbyStoreDetails.size() > 0){
					Collections.sort(nearbyStoreDetails);
				}
				if (storeInventoryMap != null && storeInventoryMap.size() > 0) {
					pRequest.setParameter("nearbyStores", nearbyStoreDetails);
					pRequest.setParameter("nearbyStoresInventory", storeInventoryMap);
					pRequest.setParameter("NumberOfStores", mStoreCountReturned);
					pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse); 
				} else if (getPageFromList().contains(pageFrom)) {
					pRequest.setParameter("nearbyStores", nearbyStoreDetails);
					for(int i=0; i<nearbyStoreDetails.size() ; i++){
						if((nearbyStoreDetails.get(i).getStoreId()).equals(storeId)){
							pRequest.setParameter("Zipcode", nearbyStoreDetails.get(i).getPostalCode());
						}
					}
					pRequest.setParameter("NumberOfStores", mStoreCountReturned);
					pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse); 
				} else {
					vlogDebug("Inventory Data not found......");
					pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest, pResponse);
				}
			} else {
				vlogDebug("Inventory Data not found......");
				pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest, pResponse);
			}
		}
		vlogDebug("TBSNearbySearchStoreDroplet :: service() method :: START");

	}
	/**
	 * @return the pageFromList
	 */
	public List<String> getPageFromList() {
		return mPageFromList;
	}
	/**
	 * @param pPageFromList the pageFromList to set
	 */
	public void setPageFromList(List<String> pPageFromList) {
		mPageFromList = pPageFromList;
	}

}
