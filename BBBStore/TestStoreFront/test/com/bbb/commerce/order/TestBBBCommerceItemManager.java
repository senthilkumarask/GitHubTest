/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order/TestBBBCommerceItemManager.java.TestBBBCommerceItemManager $$
 * @updated $DateTime: Nov 4, 2011 11:01:24 AM
 */
package com.bbb.commerce.order;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.states.CommerceItemStates;

import com.bbb.order.bean.BBBCommerceItem;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBCommerceItemManager  extends BaseTestCase{
	public void testShouldMergeItems() throws Exception {
		
		BBBCommerceItemManager bbbManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");
		
		String catalogRefId  = (String)getObject("catalogRefId");
		String productId = (String)getObject("productId");
		String qty = (String)getObject("qty");
		int intQty = Integer.parseInt(qty);
		BBBCommerceItem pExistingItem = (BBBCommerceItem)bbbManager.createCommerceItem(catalogRefId, productId, intQty);
		BBBCommerceItem pNewItem = (BBBCommerceItem)bbbManager.createCommerceItem(catalogRefId, productId, intQty);
						
		pExistingItem.setStoreId("store 1");
		pExistingItem.setRegistryId("Reg 1");		
				
		pNewItem.setStoreId("store 1");
		pNewItem.setRegistryId("Reg 1");	
		
		
		boolean doMerge = bbbManager.shouldMergeItems(pExistingItem, pNewItem);
		assertTrue("Can not merge the items as shouldMergeItems() returned false ",doMerge);
	}
	
	/*public void testCompareStoreId() throws Exception {
		
	}
	
	public void testCompareRegistryId() throws Exception {
		
	}*/
}
