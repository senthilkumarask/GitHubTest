/**
 * 
 */
package com.bbb.commerce.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.inventory.vo.InventoryFeedVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author alakra
 * 
 */
public class InventoryTestCase extends BaseTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInsert() {

	}

	public void testPerformInventoryFeedCheck() {
		// InventoryManager mgr = (InventoryManager)
		// Nucleus.getGlobalNucleus().resolveName("/atg/commerce/inventory/InventoryManager");
		try {
			OnlineInventoryManagerImpl mgr = (OnlineInventoryManagerImpl) getObject("Inventorymanager");
			String feedStatus=(String) getObject("feedStatus");
			if (mgr != null) {
				assertNotNull("Checking if inventory feed has any updates",
						mgr.getInventoryFeedUpdates(feedStatus));

				List<InventoryFeedVO> pInventoryFeeds = new ArrayList<InventoryFeedVO>();
				InventoryFeedVO feed = new InventoryFeedVO();
				feed.setID("f01:BuyBuyBaby:inv1001");
				feed.setFeedStatus(BBBCoreConstants.INVENTORY_FEED_COMPLETE);
				pInventoryFeeds.add(feed);
				mgr.updateInventoryFeed(pInventoryFeeds);
				assertNotNull("Checking if inventory feed update works");
			} else {
				System.out.println("Mgr is not resolved");
			}
		} catch (BBBSystemException e) {
			e.printStackTrace();
		}
	}

	public void testGetAltAfs() throws BBBBusinessException, BBBSystemException {
		OnlineInventoryManagerImpl mgr = (OnlineInventoryManagerImpl) getObject("Inventorymanager");
		String siteId = (String) getObject("siteId");
		String skuId = (String) getObject("skuId");
		mgr.setLoggingDebug(true);
		Long altAf = mgr.getAltAfs(skuId, siteId);
		System.out.println("altAf----" + altAf);
		assertNotNull("ALTAFS is null for skuid " + skuId, altAf);

	}

	public void testGetIgr() throws BBBBusinessException, BBBSystemException {
		OnlineInventoryManagerImpl mgr = (OnlineInventoryManagerImpl) getObject("Inventorymanager");
		String siteId = (String) getObject("siteId");
		String skuId = (String) getObject("skuId");
		mgr.setLoggingDebug(true);
		Long Igr = mgr.getIgr(skuId, siteId);
		System.out.println("Igr----" + Igr);
		assertNotNull("Igr is null for skuid " + skuId, Igr);

	}

	public void testGetAfs() throws BBBBusinessException, BBBSystemException {
		OnlineInventoryManagerImpl mgr = (OnlineInventoryManagerImpl) getObject("Inventorymanager");
		String siteId = (String) getObject("siteId");
		String skuId = (String) getObject("skuId");
		mgr.setLoggingDebug(true);
		Long afs = mgr.getAfs(skuId, siteId);
		System.out.println("afs----" + afs);
		assertNotNull("afs is null for skuid " + skuId, afs);

	}

	public void testGetInventory() throws BBBBusinessException,
			BBBSystemException {
		OnlineInventoryManagerImpl mgr = (OnlineInventoryManagerImpl) getObject("Inventorymanager");
		String siteId = (String) getObject("siteId");
		String skuId = (String) getObject("skuId");
		mgr.setLoggingDebug(true);
		InventoryVO inventory = mgr.getInventory(skuId, siteId);

		System.out.println("inventory----" + inventory.getInventoryID());
		assertNotNull("inventory is null for skuid " + skuId, inventory);
		assertNotNull("inventory id is null for skuid " + skuId,
				inventory.getInventoryID());

	}

	public void testGetInventoryList() throws BBBBusinessException,
			BBBSystemException {
		OnlineInventoryManagerImpl mgr = (OnlineInventoryManagerImpl) getObject("Inventorymanager");
		String siteId = (String) getObject("siteId");
		String[] skuId = { "sku10014", "sku10013" };
		mgr.setLoggingDebug(true);
		Map<String, InventoryVO> inventory = mgr.getInventory(skuId, siteId);

		System.out.println("inventory----"
				+ inventory.get("sku10013").getInventoryID());
		assertNotNull("inventory is null for skuid " + skuId, inventory);
		assertNotNull("inventory is null for skuid " + skuId,
				inventory.get("sku10013").getInventoryID());
	}

	public void testDecrementSkuInventory() throws BBBBusinessException,
			BBBSystemException {
		InventoryToolsImpl it = (InventoryToolsImpl) getObject("InventoryTools");
		OnlineInventoryManagerImpl mgr = (OnlineInventoryManagerImpl) getObject("InventoryManager");
		List<InventoryVO> inventoryVOs = it.getSKUInventory(getInventoryVOS());
		it.setLoggingDebug(true);
		String sku_id = inventoryVOs.get(0).getSkuID();
		Long globalStock = inventoryVOs.get(0).getGlobalStockLevel();
		Long siteStock = inventoryVOs.get(1).getSiteStockLevel();
		Long grStock = inventoryVOs.get(2).getGiftRegistryStockLevel();

		mgr.decrementInventoryStock(getInventoryVOS());
		List<InventoryVO> inventoryVOsUpdated = it
				.getSKUInventory(getInventoryVOS());
		System.out.println("Global Stock Level berfore updated:" + globalStock);
		System.out.println("Global Stock Level after updated:"
				+ inventoryVOsUpdated.get(0).getGlobalStockLevel());
		System.out.println("Site Stock Level berfore updated:" + siteStock);
		System.out.println("Site Stock Level after updated:"
				+ inventoryVOsUpdated.get(1).getSiteStockLevel());
		System.out.println("Gift Registry berfore updated:" + grStock);
		System.out.println("Gift Registry after updated:"
				+ inventoryVOsUpdated.get(2).getGiftRegistryStockLevel());

		assertNotSame("The Quantity has been Updated for the global site::::",
				globalStock, inventoryVOsUpdated.get(0).getGlobalStockLevel());
		assertNotSame(
				"The Quantity has been Updated if the Quantity is zero for Global site and ordered quantity is greater than site quantity:::::",
				siteStock, inventoryVOsUpdated.get(1).getSiteStockLevel());
		assertNotSame(
				"The Quantity has been Updated if the Quantity is zero for site and global Inventories:::::::::::",
				grStock, inventoryVOsUpdated.get(2).getGiftRegistryStockLevel());
	}

	public InventoryVO[] getInventoryVOS() {

		InventoryVO[] inventoryVOs = new InventoryVO[3];
		inventoryVOs[0] = new InventoryVO();

		inventoryVOs[0].setSiteID("BedBathUS");
		inventoryVOs[0].setSkuID("sku40037");
		inventoryVOs[0].setOrderedQuantity(10L);
		inventoryVOs[1] = new InventoryVO();

		inventoryVOs[1].setSiteID("BuyBuyBaby");
		inventoryVOs[1].setSkuID("sku30005");
		inventoryVOs[1].setOrderedQuantity(20L);
		inventoryVOs[2] = new InventoryVO();
		inventoryVOs[2].setSiteID("BedBathUS");
		inventoryVOs[2].setSkuID("sku40006");
		inventoryVOs[2].setOrderedQuantity(30L);

		return inventoryVOs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
