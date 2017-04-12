package com.bbb.commerce.inventory;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.inventory.vo.InventoryFeedVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public interface OnlineInventoryManager{
	
	/**
	 * Returns the inventory tools instance
	 * @return Inventory Tools
	 */
	public InventoryTools getInventoryTools();
	
	/**
	 * Sets the inventory tools instance
	 * @param pInventoryTools
	 */
	public void setInventoryTools(InventoryTools pInventoryTools);
	
	/**
	 * Given inventory details (sku, site, [store]), returns the uncached inventory details
	 * @param pInventoryVO
	 * @return Inventory details
	 */
	public InventoryVO getInventory(String skuId,String siteId) throws BBBSystemException ,BBBBusinessException;
	
	/**
	 * Given inventory details (sku, site, [store]), returns the cached inventory details
	 * @param pInventoryVO
	 * @return Inventory details
	 */
	public InventoryVO getCachedInventory(String skuId, String siteId) throws BBBSystemException, BBBBusinessException;
	
	/**
	 * Given a list of inventory details (sku, site, [store]), returns a map of Inventory VOs with sku id as the key
	 * @param pInventoryVO
	 * @return Map of Inventory VOs with sku id as the key
	 */
	public Map<String, InventoryVO> getInventory(String[] skuId,String siteId) throws BBBSystemException,BBBBusinessException;
	
	/**
	 * Returns the feed updates to inventory
	 * @return List of InventoryVO inventory feed updates
	 */
	public List<InventoryFeedVO> getInventoryFeedUpdates(String status) throws BBBSystemException;
	
	/**
	 * Invalidate Cache for Inventory repository
	 * 
	 */
	public void invalidateInventoryCache() throws BBBSystemException;
	
	/**
	 * Updates the inventory feed with the provided values
	 * @param pInventoryFeed
	 */
	public void updateInventoryFeed(List<InventoryFeedVO> pInventoryFeeds) throws BBBSystemException;
	
	/**
	 * Given inventory detail, removes the inventory item from cache
	 * @param pInventoryFeed
	 */
	public void invalidateInventoryCache(InventoryFeedVO pInventoryFeed) throws BBBSystemException;

	public Long getAltAfs(String skuId,String siteId) throws BBBBusinessException, BBBSystemException;

	public Long getAfs(String skuId,String siteId) throws BBBBusinessException, BBBSystemException;

	public Long getIgr(String skuId,String siteId) throws BBBBusinessException, BBBSystemException;

	/**
	 * @throws BBBSystemException
	 */
	public void decrementInventoryStock(final InventoryVO[] pInventoryVOs) throws BBBSystemException;

	public String getMaxStockSku(List<String> skuId, String siteId)
			throws BBBBusinessException, BBBSystemException;

	public void invalidateItemInventoryCache(String pSkuId, String pSiteId) throws BBBBusinessException, BBBSystemException;
	
}
