package com.bbb.commerce.inventory;

import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;

import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * Interface for InventoryManager.
 *  
 * @author jpadhi
 * @version $Revision: #1 $
 */
public interface BBBInventoryManager {
	
	public static final int AVAILABLE = 0;
	public static final int NOT_AVAILABLE = 1;
	public static final int LIMITED_STOCK = 2;
	public static final int DUMMY_STOCK = -7;
	public static final String RETRIEVE_CART = "retrieve";
	public static final String ADD_ITEM = "addItem";
	public static final String ADD_ITEM_FROM_REG = "addItemFromReg";
	public static final String UPDATE_CART = "update";
	//public static final String REGISTRY_PRODUCT = "registryProduct";
	public static final String PRODUCT_DISPLAY = "productDisplay";
	public static final String STORE_ONLINE = "storeToOnline";
	public static final String ONLINE_STORE = "onlineToStore";
	public static final String STORE_STORE = "storeToStore";
	public static final String WEB = "WEB_AVAILABLE";
	public static final String STORE = "STORE_AVAILABLE";
	public static final String WEB_STOCK = "WEB_STOCK";
	public static final String STORE_STOCK = "STORE_STOCK";
	public static final Integer AVAILABLE_BOOLEAN = 0;
	public static final Integer NON_AVAILABLE_BOOLEAN = 1;
	
	public int getProductAvailability(String pSiteId, String pSkuId, String operation, long qty) throws BBBBusinessException,  BBBSystemException;
	public int getEverLivingProductAvailability(String pSiteId, String pSkuId, String operation) throws BBBBusinessException,  BBBSystemException;
	public int getProductAvailability(String pSiteId, String pSkuId, String operation, boolean throwExc) throws BBBBusinessException,  BBBSystemException;
	public int getVDCProductAvailability(String pSiteId, String pSkuId, long pReqQty, boolean pUseCache) throws InventoryException;
	public int getNonVDCProductAvailability(String pSiteId, String pSkuId, String caFlag, String operation, boolean pUseCache, long reqQty) throws InventoryException;
	public Map<String,Integer> getBOPUSProductAvailability(String pSiteId, String pSkuId,List<String> pStoreIds, long pReqQty, String operation,  BBBStoreInventoryContainer storeInventoryContainer, boolean useCachedInventory, String pRegistryId, boolean pChangeStore, boolean isFromLocalStore) throws InventoryException;
	public void invalidateItemInventoryCache(String siteId, String catalogRefId);
	public boolean checkUncachedInventory(BBBOrder order);
	public boolean uncachedInventoryCheck();
	public Map<String,String> getWebProductInventoryStatus(List<String> pSkuId) throws BBBBusinessException, BBBSystemException;
	public int getATGInventoryForTBS(String pSiteId, String pSkuId, String operation, long qty) throws BBBBusinessException,  BBBSystemException;
	public int getProductAvailability(String pSiteId, String pSkuId, String operation, long qty, SKUDetailVO skuVO) throws BBBBusinessException, BBBSystemException;
}
