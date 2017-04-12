package com.bbb.commerce.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;

import com.bbb.commerce.inventory.vo.BBBStoreInventoryVO;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;



/**
 * Interface for BBBStoreInventoryManager.
 * 
 * @author jpadhi
 * @version $Revision: #1 $
 */
public interface BBBStoreInventoryManager {
	/*
	 * public BBBStoreInventoryVO getInventory(String pSiteId, String pSkuId,
	 * String storeId) throws InventoryException;
	 */

	public List<BBBStoreInventoryVO> getInventory(String pSiteId,
			String pSkuId, List<String> storeId , boolean isFromLocalRepository) throws InventoryException;
	public BBBStoreInventoryVO getInventory(String pSkuId, String storeId , boolean isFromLocalInventory) 
			throws InventoryException, BBBBusinessException;	
	public Map<String, Integer> getFavStoreInventory(String pSkuId, String pSiteId, String storeId, boolean isFromLocalRepository) 
			throws InventoryException, BBBBusinessException;
	public HashMap<String, Object> fetchFavStoreInvMultiSkus(
			DynamoHttpServletRequest req, BBBOrder order, String siteId,
			String favoriteStoreId)throws BBBSystemException, BBBBusinessException, InventoryException, Exception;

}
