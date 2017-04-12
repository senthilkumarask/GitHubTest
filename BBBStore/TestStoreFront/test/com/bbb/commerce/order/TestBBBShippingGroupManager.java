/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order/TestBBBPurchaseProcessHelper.java.TestBBBPurchaseProcessHelper $$
 * @updated $DateTime: Nov 4, 2011 3:56:41 PM
 */
package com.bbb.commerce.order;

import java.util.ArrayList;
import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;

import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBShippingGroupManager extends BaseTestCase {
	
	public void testGetStorePickupShippingGroup() throws Exception{
		
		//BBBShippingGroupManager bbbSGManager = (BBBShippingGroupManager)resolveName("/atg/commerce/order/ShippingGroupManager");
		BBBShippingGroupManager bbbSGManager = (BBBShippingGroupManager)getObject("bbbShippingGroupManager");
		//OrderTools orderTools = (OrderTools)resolveName("/atg/commerce/order/OrderTools");
		OrderTools orderTools = (OrderTools)getObject("orderTools");
		Order pOrder = orderTools.createOrder("default");		
		String storeId1 = (String)getObject("storeId1");
		String storeId2 = (String)getObject("storeId2");
		
		assertEquals("Order has more than 1 default HardgoodShippingGroup", 1, pOrder.getShippingGroupCount());
		
		BBBStoreShippingGroup storeSG = (BBBStoreShippingGroup)bbbSGManager.getStorePickupShippingGroup(storeId1, pOrder);		
		addObjectToAssert("param1", storeSG.getStoreId());
		assertEquals("getStorePickupShippingGroup call has not created new StoreShippingGroup", 2, pOrder.getShippingGroupCount());
				
		BBBStoreShippingGroup storeSG2 = (BBBStoreShippingGroup)orderTools.createShippingGroup("storeShippingGroup");		
		storeSG2.setStoreId(storeId2);		
		assertEquals("createShippingGroup has added new Shippinggroup to order", 2, pOrder.getShippingGroupCount());
		
		pOrder.addShippingGroup(storeSG2);		
		assertEquals("addShippingGroup has not added new Shippinggroup to order", 3, pOrder.getShippingGroupCount());
		
		BBBStoreShippingGroup storeSG3 = (BBBStoreShippingGroup)bbbSGManager.getStorePickupShippingGroup(storeId2, pOrder);
		addObjectToAssert("param2", storeSG3.getStoreId());
		assertEquals("getStorePickupShippingGroup has again added new storeShippingGroup", 3, pOrder.getShippingGroupCount());
		
		pOrder = null;
		try{
			BBBStoreShippingGroup storeSG4 = (BBBStoreShippingGroup)bbbSGManager.getStorePickupShippingGroup(storeId2, pOrder);			
			fail("NullPointerException was expected to be thrown as order is null");
		}catch (NullPointerException e) {
			
		}
		
	}
	
	
}
