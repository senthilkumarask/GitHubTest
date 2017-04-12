/**
 * 
 */
package com.bbb.commerce.inventory;

import java.util.List;

import atg.repository.Repository;

import com.bbb.commerce.inventory.vo.InventoryFeedVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author alakra
 * 
 */
public interface InventoryTools {

	/**
	 * Returns the inventory repository instance
	 * 
	 * @return Inventory Repository
	 */
	public Repository getInventoryRepository();

	/**
	 * Sets the inventory repository instance
	 * 
	 * @param pInventoryRepository
	 */
	public void setInventoryRepository(Repository pInventoryRepository);

	/**
	 * Returns the feed updates to inventory
	 * 
	 * @return List of InventoryVO inventory feed updates
	 */
	public List<InventoryFeedVO> getInventoryFeedUpdates(String status)
			throws BBBSystemException;
	
	
	/**
	 * Invokes invalidate cache for Inventory Repository.
	 * 
	 * @throws BBBSystemException
	 */
	public void invalidateInventoryCache() throws BBBSystemException;

	/**
	 * Given inventory detail, removes the inventory item from cache
	 * 
	 * @param pInventoryFeed
	 */
	public void removeItemFromCache(InventoryFeedVO pInventoryFeed)
			throws BBBSystemException;

	/**
	 * Updates the inventory feed with the provided values
	 * 
	 * @param pInventoryFeed
	 */
	public void updateInventoryFeed(List<InventoryFeedVO> pInventoryFeed)
			throws BBBSystemException;

	/**
	 * This method will get the list of InventoryVO's from the Inventory
	 * repository
	 * 
	 * @param inventory
	 *            is list of InventoryVO[]'s
	 * @return List of InventoryVO Objects
	 * @throws BBBBusinessException
	 */
	public List<InventoryVO> getSKUInventory(InventoryVO[] pInventory)
			throws BBBSystemException;

	/**
	 * Updates the SKU's Stock Level in the inventory for the sites provided in
	 * the InventoryVO objects
	 * 
	 * @param inventory
	 * @throws BBBSystemException
	 */
	public void updateSKUInventory(List<InventoryVO> pInventory)
			throws BBBSystemException;

	/**
	 * Updates the SKU's Stock Level in the inventory for all sites provided in
	 * the InventoryVO objects
	 * 
	 * @param inventory
	 * @throws BBBSystemException
	 */
	public void updateSKUInventoryForAllSites(List<InventoryVO> updatedInventoryVOs)
			throws BBBSystemException;

}
