package com.bbb.commerce.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.vo.InventoryStatusVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderHolder;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
public class TBSInventoryManagerImpl extends BBBInventoryManagerImpl {
	
	private BopusInventoryService mInventoryService;
	
	private String mSpecialShipMsg;
	
	private TBSSearchStoreManager mSearchStoreManager;
	
	private String lShiptime=null;
	/**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}
	/**
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}
	/**
	 * @return the specialShipMsg
	 */
	public String getSpecialShipMsg() {
		return mSpecialShipMsg;
	}
	/**
	 * @param pSpecialShipMsg the specialShipMsg to set
	 */
	public void setSpecialShipMsg(String pSpecialShipMsg) {
		mSpecialShipMsg = pSpecialShipMsg;
	}
	/**
	 * @return the inventoryService
	 */
	public BopusInventoryService getInventoryService() {
		return mInventoryService;
	}
	/**
	 * @param pInventoryService the inventoryService to set
	 */
	public void setInventoryService(BopusInventoryService pInventoryService) {
		mInventoryService = pInventoryService;
	}

	/**
	 * This method return the inventory count for BOPUS item In case of System
	 * Exception if operation is updateCart then return AVAILABLE In case of
	 * System Exception if operation is not updateCart then return NOT AVAILABLE
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param pStoreId
	 * @param pRequestedQuantity
	 * @param operation
	 * 
	 * @return InventoryStatusVO
	 * @exception InventoryException
	 * 
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public InventoryStatusVO getTbsBOPUSProductAvailability(String pSiteId,
			String pSkuId, List<String> pStoreIds, long pRequestedQuantity,
			String operation, BBBStoreInventoryContainer storeInventoryContainer,
			boolean useCachedInventory, String pRegistryId, boolean pChangeStore) throws InventoryException {
		
	    BBBPerformanceMonitor.start("TBSInventoryManagerImpl getBOPUSProductAvailability");
		Map<String, Integer> storeIdInventoryMap = storeInventoryContainer.getStoreIdInventoryMap();
		//For left over storeIds if not found in session bean
		List<String> newStoreIdList = new ArrayList<String>();
		Map<String, Integer> inventories = new HashMap<String, Integer>();
		InventoryStatusVO statusVO = new InventoryStatusVO();
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		OrderHolder cart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
		BBBOrder order = (BBBOrder) cart.getCurrent();

		Map<String, Integer> inventoryStatusMap = new HashMap<String, Integer>();
		List<CommerceItem> commerceItemList = null;
		try {
			commerceItemList = order.getCommerceItemsByCatalogRefId(pSkuId);
		} catch (CommerceItemNotFoundException exception) {
			logDebug("CommerceItemNotFoundException for SKUID : " + pSkuId + exception);
		} catch (InvalidParameterException exception) {
			logError("InvalidParameterException for SKUID :" + pSkuId + exception);
		}
		try {
			ThresholdVO skuThresholdVO = getCatalogTools().getSkuThreshold(pSiteId, pSkuId);

			//use this as the Key to storeInventoryMap
			String inventoryMapKey = null;
			if(useCachedInventory && storeIdInventoryMap != null){
				for(String storeId: pStoreIds){
					long totalRequestedQuantity = pRequestedQuantity;
					long skuQuantityInOrder = 0L;
					if(BBBInventoryManager.STORE_STORE.equalsIgnoreCase(operation)){
						if (commerceItemList != null && !commerceItemList.isEmpty()) {
							for (CommerceItem commerceItem : commerceItemList) {
								if (commerceItem instanceof BBBCommerceItem) {
									final BBBCommerceItem bbbItem = (BBBCommerceItem) commerceItem;
									if (bbbItem.getStoreId() != null && storeId == bbbItem.getStoreId()) {
										skuQuantityInOrder += commerceItem.getQuantity();
									}
								}
							}
						}
						
						if(!pChangeStore){
							totalRequestedQuantity += skuQuantityInOrder;
						}
					}
									
					//append store Id and skuid separated by Pipe Symbol
					inventoryMapKey = storeId + BBBCoreConstants.PIPE_SYMBOL + pSkuId;
					if(storeIdInventoryMap.containsKey(inventoryMapKey)){						
						Integer invCount = storeIdInventoryMap.get(inventoryMapKey);
						
						// Get inventory Status and update the inventoryStatusMap
						inventoryStatusMap.put(storeId, getInventoryStatus(invCount, totalRequestedQuantity, skuThresholdVO));
						inventories.put(storeId, invCount);
					}else{
						newStoreIdList.add(storeId);
					}
				}
			}else{
				newStoreIdList.addAll(pStoreIds);
			}
			Map<String, Integer> inventoriesFromNewStore = new HashMap<String, Integer>();
			boolean inventoryFound = false;
			Integer inventoryStock = 0;
			if(newStoreIdList.size() > 0){
				inventoriesFromNewStore = getBopusService().getInventoryForBopusItem(pSkuId, newStoreIdList , false);
				if (!BBBUtility.isMapNullOrEmpty(inventoriesFromNewStore)) {
					inventories.putAll(inventoriesFromNewStore);
				}
			}
			if (null != inventories && !inventories.isEmpty()) {
				inventoryFound = true;
			}
			for (int i = 0; i < newStoreIdList.size(); i++)
			{
				if(inventoryFound && inventories.containsKey(newStoreIdList.get(i))){
					inventoryStock = inventories.get(newStoreIdList.get(i));
				} else {
					inventoryStock = 0;
				}
				long totalReqQuantity = pRequestedQuantity;
				long totalSkuQuantityInOrder = 0;
				// update the storeInventoryMap in session scope
				inventoryMapKey = newStoreIdList.get(i) + BBBCoreConstants.PIPE_SYMBOL + pSkuId;
				storeIdInventoryMap.put(inventoryMapKey, inventoryStock);

				if(BBBInventoryManager.STORE_STORE.equalsIgnoreCase(operation)){
					if (commerceItemList != null && !commerceItemList.isEmpty()) {
						for (CommerceItem commerceItem : commerceItemList) {
							if (commerceItem instanceof BBBCommerceItem) {
								final BBBCommerceItem bbbItem = (BBBCommerceItem) commerceItem;
								if (bbbItem.getStoreId() != null && newStoreIdList.get(i).equalsIgnoreCase(bbbItem.getStoreId())) {
									totalSkuQuantityInOrder += bbbItem.getQuantity();
								}
							}
						}
					}

					if (!pChangeStore) {
						totalReqQuantity += totalSkuQuantityInOrder;
					}
				}
				// Get inventory Status and update the inventoryStatusMap
				inventoryStatusMap.put(newStoreIdList.get(i), getInventoryStatus(inventoryStock, totalReqQuantity, skuThresholdVO));
			}
			statusVO.getInventoryStatusMap().putAll(inventoryStatusMap);
			statusVO.getInventoryMap().putAll(inventories);
						
			return statusVO;

		} catch (BBBSystemException e) {
			
			throw new InventoryException(e);
		} catch (BBBBusinessException e) {
			
			throw new InventoryException(e);
		} finally {
		    BBBPerformanceMonitor.end("TBSInventoryManagerImpl", "getBOPUSProductAvailability");
		}

	}
	

	/**
	 * This method return the inventory count for item in the entire network.
	 *
	 * @param pSiteId the site id
	 * @param pSkuId the sku id
	 * @param pOperation the operation
	 * @return the product availability
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	@Override
	public int getProductAvailability(String pSiteId, String pSkuId,
			String pOperation, long qty) throws BBBBusinessException, BBBSystemException {
		
		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		
		if(pSiteId.equals("TBS_BedBathUS") ) {
			pSiteId = "BedBathUS";
		}
		else if(pSiteId.equals("TBS_BuyBuyBaby") ) {
			pSiteId = "BuyBuyBaby";			
		}
		else if(pSiteId.equals("TBS_BedBathCanada") ) {
			pSiteId = "BedBathCanada";			
		}
		availabilityStatus = super.getProductAvailability(pSiteId, pSkuId, pOperation, qty);
		
		return availabilityStatus;
	}
	
	/* (non-Javadoc)
	 * @see com.bbb.commerce.inventory.BBBInventoryManagerImpl#getNonVDCProductAvailability(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public int getNonVDCProductAvailability(String pSiteId, String pSkuId,
			String pCaFlag, String pOperation, boolean pUseCache, long reqQty)
					throws InventoryException {

		logDebug("TBSInventoryManagerImpl : getNonVDCProductAvailability() : starts");

		if(pSiteId.equals("TBS_BedBathUS") ) {
			pSiteId = "BedBathUS";
		}
		else if(pSiteId.equals("TBS_BuyBuyBaby") ) {
			pSiteId = "BuyBuyBaby";			
		}
		else if(pSiteId.equals("TBS_BedBathCanada") ) {
			pSiteId = "BedBathCanada";			
		}
		RepositoryItem skuItem = null;
		Set<RepositoryItem> skuAttrRelation = null;
		RepositoryItem skuAttribute = null;
		String skuAttrId = null;
		SKUDetailVO skuvo = null;
		
		try {
			skuItem = getCatalogTools().getSkuRepositoryItem(pSkuId);
			skuvo = getCatalogTools().getSKUDetails(pSiteId, pSkuId);
		} catch (BBBBusinessException e) {
			throw new InventoryException(e);
		} catch (BBBSystemException e) {
			throw new InventoryException(e);
		}

		if(skuItem != null){
			skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
		}
		if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
			logDebug("skuAttrRelation :: "+skuAttrRelation );
			for (RepositoryItem skuAttrReln : skuAttrRelation) {
				skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
				if(skuAttribute != null){
					skuAttrId = skuAttribute.getRepositoryId();
				}
				if(!StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) || skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE))){
					getSearchStoreManager().getShipTime(pSkuId, reqQty, pSiteId, (String)ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));
					return BBBInventoryManager.AVAILABLE;
				}
			}
		}
				
		boolean domCallFlag = false;
		try {
			domCallFlag = getCatalogTools().getValueForConfigKey(TBSConstants.CONFIG_TYPE_FLAG_DRIVEN_FUNCTIONS, TBSConstants.CONFIG_KEY_DOM_CALL_FLA, domCallFlag);
		} catch (BBBSystemException e) {
			logError("BBBInventoryManagerImpl : getNonVDCProductAvailability() Exception occurred in fetching config key: " + TBSConstants.CONFIG_KEY_DOM_CALL_FLA);
		} catch (BBBBusinessException e) {
			logError("BBBInventoryManagerImpl : getNonVDCProductAvailability() Exception occurred in fetching config key: " + TBSConstants.CONFIG_KEY_DOM_CALL_FLA);
		}
		
		int availability = BBBInventoryManager.AVAILABLE;
		
		/* If domCallFlag flag is true, it will check onlineInventory first. 
		 * If not available in online inventory then only it will make DOM call otherwise not.
		 * This performance change is made for story BBB-745.
		 * If domCallFlag flag is false, it will keep executing existing code. 
		*/
		if(domCallFlag){	
			logDebug("TBSInventoryManagerImpl : getNonVDCProductAvailability() : flag for avoiding DOM call is true.");

			try{
				availability = super.getNonVDCProductAvailability(pSiteId, pSkuId, pCaFlag, pOperation, pUseCache, reqQty);
			} catch (InventoryException e) {
				logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1021 +
						": BBBInventoryManagerImpl : getNonVDCProductAvailability() Exception occurred in getting Inventory information  for sku " + pSkuId);
				availability = BBBInventoryManager.NOT_AVAILABLE;
			}
			
			if(availability == BBBInventoryManager.AVAILABLE){
				DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
				request.setParameter(TBSConstants.NEAR_BY_STORE_LINK, true);
				request.setParameter(TBSConstants.TIME_FRAME, "0001");
			}			
			if(skuvo != null && !skuvo.isVdcSku() && !skuvo.isLtlItem() && availability == BBBInventoryManager.NOT_AVAILABLE){				
				//for /store, if this call is happening from PDP page then it will be 0(zero), as we need the exact value for /tbs for PDP
				//default value setting as 1
				if(reqQty == 0){
					reqQty = 1;
				}
				lShiptime = getSearchStoreManager().getShipTime(pSkuId, reqQty, pSiteId, (String)ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));
				if (!StringUtils.isBlank(lShiptime) && lShiptime == "0004"){
					availability = BBBInventoryManager.NOT_AVAILABLE;
				}else{
					availability = BBBInventoryManager.AVAILABLE;
				}
			}
		} else {	
			logDebug("TBSInventoryManagerImpl : getNonVDCProductAvailability() : flag for avoiding DOM call is false.");

			if(skuvo != null && !skuvo.isVdcSku() && !skuvo.isLtlItem()){
				//for /store, if this call is happening from PDP page then it will be 0(zero), as we need the exact value for /tbs for PDP
				//default value setting as 1
				if(reqQty == 0){
					reqQty = 1;
				}
				lShiptime = getSearchStoreManager().getShipTime(pSkuId, reqQty, pSiteId, (String)ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));
			}
			availability = super.getNonVDCProductAvailability(pSiteId, pSkuId, pCaFlag, pOperation,
					pUseCache, reqQty);
			
			if(skuvo != null && !skuvo.isVdcSku() && !skuvo.isLtlItem()){
				
				if (availability == BBBInventoryManager.NOT_AVAILABLE) {
					//for /store, if this call is happening from PDP page then it will be 0(zero), as we need the exact value for /tbs for PDP
					//default value setting as 1
					/*if(reqQty == 0){
						reqQty = 1;
					}
					lShiptime = getSearchStoreManager().getShipTime(pSkuId, reqQty, pSiteId, (String)ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER));*/
				
					if (!StringUtils.isBlank(lShiptime) && lShiptime == "0004"){
						availability = BBBInventoryManager.NOT_AVAILABLE;
					}else{
						availability = BBBInventoryManager.AVAILABLE;
					}
				}
			}
		}	
		logDebug("TBSInventoryManagerImpl : getNonVDCProductAvailability() : ends");

		return availability;
	}
	
	
	/**
	 * This method return the inventory status for VDC item
	 * 
	 * @param pSiteId
	 * @param pSkuId
	 * @param pReqQty
	 * @return status
	 * @exception InventoryException
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int getVDCProductAvailability(String pSiteId, String pSkuId,
			long pReqQty, boolean pUseCache) throws InventoryException {
		BBBPerformanceMonitor.start("BBBInventoryManager getVDCProductAvailability");
		
		RepositoryItem skuItem = null;
		Set<RepositoryItem> skuAttrRelation = null;
		RepositoryItem skuAttribute = null;
		String skuAttrId = null;
		
		try {
			skuItem = getCatalogTools().getSkuRepositoryItem(pSkuId);
		} catch (BBBBusinessException e) {
			throw new InventoryException(e);
		} catch (BBBSystemException e) {
			throw new InventoryException(e);
		}

		if(skuItem != null){
			skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
		}
		if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
			logDebug("skuAttrRelation :: "+skuAttrRelation );
			for (RepositoryItem skuAttrReln : skuAttrRelation) {
				skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
				if(skuAttribute != null){
					skuAttrId = skuAttribute.getRepositoryId();
				}
				if(!StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) || skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE))){
					return BBBInventoryManager.AVAILABLE;
				}
			}
		}
		int availability = super.getVDCProductAvailability(pSiteId, pSkuId, pReqQty, pUseCache);
		return availability;
	}
	

	/**
	 * This method was overridden to make the inventory available in case of
	 * available inventory and requested qty are same. 
	 */
	public int getInventoryStatus(int inventory, long pReqQty,
			ThresholdVO skuThresholdVO) {
		BBBPerformanceMonitor.start("BBBInventoryManager getInventoryStatus");
		int inventoryStatus = BBBInventoryManager.AVAILABLE;
		if (skuThresholdVO != null) {
			if (inventory - pReqQty >= skuThresholdVO.getThresholdAvailable()) {
				inventoryStatus = BBBInventoryManager.AVAILABLE;
			} else if (inventory - pReqQty >= skuThresholdVO
					.getThresholdLimited()) {
				inventoryStatus = BBBInventoryManager.LIMITED_STOCK;
			} else {
				inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("skuThresholdVO from catalog in null");
			}
			if (inventory - pReqQty >= 0) {
				inventoryStatus = BBBInventoryManager.AVAILABLE;
			} else {
				inventoryStatus = BBBInventoryManager.NOT_AVAILABLE;
			}

		}

		BBBPerformanceMonitor.end("BBBInventoryManager getInventoryStatus");
		return inventoryStatus;
	}
	
	@Override
	public int getATGInventoryForTBS(String pSiteId, String pSkuId,
			String operation, long qty) throws BBBBusinessException, BBBSystemException {
		BBBPerformanceMonitor.start("TBSInventoryManagerImpl getATGInventoryForTBS");
		
		logDebug("TBSInventoryManagerImpl : getATGInventoryForTBS() starts");
		logDebug("Input parameters : pSiteId " + pSiteId + " ,pSkuId "
				+ pSkuId + " & operation " + operation);

		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		SKUDetailVO skuDetailVO = null;
		try {
			logDebug("Calling CatalogTools getSKUDetails() method : Input Parameters are pSiteId - "
					+ pSiteId + " ,pSkuId - " + pSkuId	+ " & calculateAboveBelowLine - " + false);
			
			skuDetailVO = getCatalogTools().getSKUDetails(pSiteId, pSkuId, false);
			logDebug("response skuDetailVO from CatalogTools getSKUDetails() method :"	+ skuDetailVO);
	
			String caFlag = skuDetailVO.getEcomFulfillment();
	
			if (skuDetailVO.isVdcSku()) {
				availabilityStatus = super.getVDCProductAvailability(pSiteId, pSkuId,
						qty, BBBCoreConstants.CACHE_ENABLED);
			} else {
				availabilityStatus = super.getNonVDCProductAvailability(pSiteId,
						pSkuId, caFlag, operation, BBBCoreConstants.CACHE_ENABLED, qty);
			}
		} catch (InventoryException e) {
			if(isLoggingDebug()){
			logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1021 +
					": TBSInventoryManagerImpl : getATGInventoryForTBS() Exception occurred in getting Inventory information  for sku " + pSkuId);
			}
			availabilityStatus = BBBInventoryManager.NOT_AVAILABLE;
		}

		logDebug("TBSInventoryManagerImpl : getATGInventoryForTBS() ends.");
		logDebug("Output - availabilityStatus " + availabilityStatus);

		BBBPerformanceMonitor.end("TBSInventoryManagerImpl getATGInventoryForTBS");
		return availabilityStatus;
	}
	
}