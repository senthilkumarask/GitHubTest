package com.bbb.commerce.inventory;

import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBInventoryManagerImpl  extends BaseTestCase{
	public void testGetProductAvailability() throws Exception {
		
		BBBInventoryManager bbbInventoryManager = (BBBInventoryManagerImpl)getObject("bbbInventoryManager");
		
		String pSkuId1  = (String)getObject("catalogRefId1");//VDC 
		String pSkuId2  = (String)getObject("catalogRefId2");//NON VDC with CA Flag = e
		String pSkuId3  = (String)getObject("catalogRefId3");//NON VDC with CA Flag != e
		String pSiteId = (String)getObject("siteId");
		String qty = (String)getObject("qty");		
		int intQty = Integer.parseInt(qty);

		
		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		//Product Display Page		
		availabilityStatus = bbbInventoryManager.getProductAvailability(pSiteId, pSkuId1, BBBInventoryManager.PRODUCT_DISPLAY, 0);		
		assertEquals("Inventory Status should be "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, availabilityStatus);
		
		availabilityStatus = bbbInventoryManager.getProductAvailability(pSiteId, pSkuId2, BBBInventoryManager.PRODUCT_DISPLAY, 0);
		assertEquals("Inventory Status should be "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, availabilityStatus);
		
		availabilityStatus = bbbInventoryManager.getProductAvailability(pSiteId, pSkuId3, BBBInventoryManager.PRODUCT_DISPLAY, 0);
		assertEquals("Inventory Status should be "+BBBInventoryManager.NOT_AVAILABLE, BBBInventoryManager.NOT_AVAILABLE, availabilityStatus);
				
	}
	
	/*public void testGetNonVDCProductAvailability() throws Exception {
		BBBInventoryManager bbbInventoryManager = (BBBInventoryManagerImpl)getObject("bbbInventoryManager");
		OnlineInventoryManager onlineInventoryManager = (OnlineInventoryManagerImpl)getObject("onlineInventoryManager");
		BBBCatalogTools bbbCatalogTools = (BBBCatalogToolsImpl)getObject("bbbCatalogTools");
		//Non VDC SKU with CAflag != e
		String pSkuId1  = (String)getObject("catalogRefId1");
		//Non VDC SKU with CAflag == e
		String pSkuId2  = (String)getObject("catalogRefId2");
		String pSiteId = (String)getObject("siteId");
		String qty = (String)getObject("qty");		
		int intQty = Integer.parseInt(qty);
		System.out.println("siteId - "+pSiteId+", skuId - "+pSkuId1+" & Qty -"+intQty);
		SKUDetailVO skuDetailVO =  bbbCatalogTools.getSKUDetails(pSiteId, pSkuId1, false);
		String caFlag = skuDetailVO.getEcomFulfillment();
		System.out.println(pSkuId1+ " - SKU caFlag - "+caFlag);
		
		InventoryVO inventoryVO = onlineInventoryManager.getInventory(pSkuId1, pSiteId);
		
		long afs = inventoryVO.getGlobalStockLevel();//0
		long altAFS = inventoryVO.getSiteStockLevel();//0
		long igr = inventoryVO.getGiftRegistryStockLevel();//200
		
		System.out.println("Global Stock Level - "+afs+", Site Stock Level - "+altAFS+" & Inventory Stock Level -"+igr);
		
		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		
		//IGRflag false
		availabilityStatus = bbbInventoryManager.getNonVDCProductAvailability(pSiteId, pSkuId1, caFlag, false);
		System.out.println("1 availabilityStatus "+availabilityStatus);
		assertEquals("Inventory Status should be "+BBBInventoryManager.NOT_AVAILABLE, BBBInventoryManager.NOT_AVAILABLE, availabilityStatus);
		//IGRflag true
		availabilityStatus = bbbInventoryManager.getNonVDCProductAvailability(pSiteId, pSkuId1, caFlag, true);
		System.out.println("2 availabilityStatus "+availabilityStatus);
		assertEquals("Inventory Status should be "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, availabilityStatus);
		
		System.out.println("siteId - "+pSiteId+", skuId - "+pSkuId2+" & Qty -"+intQty);
		skuDetailVO =  bbbCatalogTools.getSKUDetails(pSiteId, pSkuId2, false);
		caFlag = skuDetailVO.getEcomFulfillment();
		System.out.println(pSkuId2+"- SKU caFlag - "+caFlag);
		
		inventoryVO = onlineInventoryManager.getInventory(pSkuId1, pSiteId);
		
		afs = inventoryVO.getGlobalStockLevel();//10
		altAFS = inventoryVO.getSiteStockLevel();//0
		igr = inventoryVO.getGiftRegistryStockLevel();//200
		
		System.out.println("Global Stock Level - "+afs+", Site Stock Level - "+altAFS+" & Inventory Stock Level -"+igr);
		//IGRflag false
		availabilityStatus = bbbInventoryManager.getNonVDCProductAvailability(pSiteId, pSkuId1, "e", false);
		System.out.println("3 availabilityStatus "+availabilityStatus);
		assertEquals("Inventory Status should be "+BBBInventoryManager.NOT_AVAILABLE, BBBInventoryManager.NOT_AVAILABLE, availabilityStatus);
		
		//IGRflag true
		availabilityStatus = bbbInventoryManager.getNonVDCProductAvailability(pSiteId, pSkuId1, "e", true);
		System.out.println("4 availabilityStatus "+availabilityStatus);
		assertEquals("Inventory Status should be "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, availabilityStatus);
	}
	
	public void testGetVDCProductAvailability() throws Exception {
		BBBInventoryManager bbbInventoryManager = (BBBInventoryManagerImpl)getObject("bbbInventoryManager");
		OnlineInventoryManager onlineInventoryManager = (OnlineInventoryManagerImpl)getObject("onlineInventoryManager");
		
		//VDC SKU with requested Qty
		String pSkuId  = (String)getObject("catalogRefId");		
		String pSiteId = (String)getObject("siteId");
		String qty = (String)getObject("qty");		
		long longQty = Long.parseLong(qty);
		System.out.println("siteId - "+pSiteId+", skuId - "+pSkuId+" & Qty -"+longQty);
		
		
		InventoryVO inventoryVO = onlineInventoryManager.getInventory(pSkuId, pSiteId);
		
		long afs = inventoryVO.getGlobalStockLevel();//0
		long altAFS = inventoryVO.getSiteStockLevel();//0
		long igr = inventoryVO.getGiftRegistryStockLevel();//200
		
		System.out.println("Global Stock Level - "+afs+", Site Stock Level - "+altAFS+" & Inventory Stock Level -"+igr);
		
		int availabilityStatus = BBBInventoryManager.AVAILABLE;
		
		//quantity = 0 for product page
		availabilityStatus = bbbInventoryManager.getVDCProductAvailability(pSiteId, pSkuId, 0);
		System.out.println("1 availabilityStatus "+availabilityStatus);
		assertEquals("Inventory Status should be "+BBBInventoryManager.NOT_AVAILABLE, BBBInventoryManager.NOT_AVAILABLE, availabilityStatus);
		//with reqQty
		availabilityStatus = bbbInventoryManager.getVDCProductAvailability(pSiteId, pSkuId, longQty);
		System.out.println("2 availabilityStatus "+availabilityStatus);
		assertEquals("Inventory Status should be "+BBBInventoryManager.NOT_AVAILABLE, BBBInventoryManager.NOT_AVAILABLE, availabilityStatus);
		
		
	}*/

}
