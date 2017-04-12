/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order/TestBBBOrderManager.java.TestBBBOrderManager $$
 * @updated $DateTime: Nov 18, 2011 12:46:29 PM
 */
package com.bbb.commerce.order;

import java.util.List;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;

import com.bbb.account.BBBProfileTools;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBOrderManager   extends BaseTestCase{
	
	public void testMergeOrders() throws Exception {		
		
		BBBOrderManager bbbOrderManager = (BBBOrderManager)getObject("bbbOrderManager");
		BBBCommerceItemManager bbbItemManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");
		BBBShippingGroupManager bbbSGManager = (BBBShippingGroupManager)getObject("bbbShippingGroupManager");
		BBBProfileTools profieTools = (BBBProfileTools)getObject("bbbProfileTools");
		
		String catalogRefId1  = (String)getObject("catalogRefId1");
		String catalogRefId2  = (String)getObject("catalogRefId2");
		String productId1 = (String)getObject("productId1");	//xprod1014
		String productId2 = (String)getObject("productId2");	//xprod1008
		String storeId1 = (String)getObject("storeId1");	//1032
		String storeId2 = (String)getObject("storeId2");	//384
		
		String registryId1 = (String)getObject("registryId1");
		String registryId2 = (String)getObject("registryId2");
		
		String email = (String)getObject("email");
		String pwd = (String)getObject("pwd");	
		RepositoryItem profile = profieTools.getItemFromEmail(email);		
		
		//Add 4 items to the inSession Order
		
		//Product - xprod1014
		BBBCommerceItem inSessionItem1 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 2);
		
		//Product - xprod1008, Store - 1032, Registry - 1
		BBBCommerceItem inSessionItem2 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId2, productId2, 1);						
		inSessionItem2.setStoreId(storeId1);
		inSessionItem2.setRegistryId(registryId1);		
		
		
		//Product - xprod1014, Store - 384, Registry - 1 
		BBBCommerceItem inSessionItem3 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 1);						
		inSessionItem3.setStoreId(storeId2);
		inSessionItem3.setRegistryId(registryId1);
		
		//Product - xprod1014, Store - 384, Registry - 2 
		BBBCommerceItem inSessionItem4 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 1);						
		inSessionItem4.setStoreId(storeId2);
		inSessionItem4.setRegistryId(registryId2);
		
		Order inSessionOrder = bbbOrderManager.createOrder( profile.getRepositoryId());
			
		bbbItemManager.addItemToOrder(inSessionOrder, inSessionItem1);
		bbbItemManager.addItemToOrder(inSessionOrder, inSessionItem2);
		bbbItemManager.addItemToOrder(inSessionOrder, inSessionItem3);
		bbbItemManager.addItemToOrder(inSessionOrder, inSessionItem4);
		
		ShippingGroup sg0 = bbbSGManager.getFirstNonGiftHardgoodShippingGroup(inSessionOrder);
		bbbItemManager.addItemQuantityToShippingGroup(inSessionOrder, inSessionItem1.getId(), sg0.getId(), inSessionItem1.getQuantity());
		
		ShippingGroup sg1 = bbbSGManager.getStorePickupShippingGroup(storeId1, inSessionOrder);
		bbbItemManager.addItemQuantityToShippingGroup(inSessionOrder, inSessionItem2.getId(), sg1.getId(), inSessionItem2.getQuantity());
		
		ShippingGroup sg2 = bbbSGManager.getStorePickupShippingGroup(storeId2, inSessionOrder);
		bbbItemManager.addItemQuantityToShippingGroup(inSessionOrder, inSessionItem3.getId(), sg2.getId(), inSessionItem3.getQuantity());
		bbbItemManager.addItemQuantityToShippingGroup(inSessionOrder, inSessionItem4.getId(), sg2.getId(), inSessionItem4.getQuantity());
		
		List<BBBCommerceItem> inSessionItems = inSessionOrder.getCommerceItems();
		assertEquals("CommerceItems size should be 4 ", 4, inSessionItems.size());
		List<ShippingGroup> sgList =  inSessionOrder.getShippingGroups();
		assertEquals("Shippinggroup size should be 3 ", 3, sgList.size());
		
		
		//Add 4 items to the saved Order
		
		//Product xprod1014
		BBBCommerceItem savedItem1 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 1);
		//Product - xprod1008, Store - 1032, Registry - 1
		BBBCommerceItem savedItem2 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId2, productId2, 1);						
		savedItem2.setStoreId(storeId1);
		savedItem2.setRegistryId(registryId1);	
		//Product - xprod1014, Store - 384, Registry - 1
		BBBCommerceItem savedItem3 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 2);						
		savedItem3.setStoreId(storeId2);
		savedItem3.setRegistryId(registryId1);
		//Product - xprod1014, Store - 25, Registry - 2
		BBBCommerceItem savedItem4 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 1);	
		String storeId3 = (String)getObject("storeId3");	//25
		savedItem4.setStoreId(storeId3);
		savedItem4.setRegistryId(registryId2);
		
		Order savedOrder = bbbOrderManager. createOrder(profile.getRepositoryId());
			
		bbbItemManager.addItemToOrder(savedOrder, savedItem1);
		bbbItemManager.addItemToOrder(savedOrder, savedItem2);
		bbbItemManager.addItemToOrder(savedOrder, savedItem3);
		bbbItemManager.addItemToOrder(savedOrder, savedItem4);
		
		bbbSGManager.getStorePickupShippingGroup(storeId2, savedOrder);
		bbbSGManager.getStorePickupShippingGroup(storeId3, savedOrder);
		
		ShippingGroup sg3 = bbbSGManager.getStorePickupShippingGroup(storeId1, savedOrder);
		bbbItemManager.addItemQuantityToShippingGroup(savedOrder, savedItem2.getId(), sg3.getId(), savedItem2.getQuantity());
		
		ShippingGroup sg4 = bbbSGManager.getStorePickupShippingGroup(storeId2, savedOrder);
		bbbItemManager.addItemQuantityToShippingGroup(savedOrder, savedItem3.getId(), sg4.getId(), savedItem3.getQuantity());
		
		ShippingGroup sg5 = bbbSGManager.getStorePickupShippingGroup(storeId3, savedOrder);
		bbbItemManager.addItemQuantityToShippingGroup(savedOrder, savedItem4.getId(), sg5.getId(), savedItem4.getQuantity());
		
		inSessionOrder.setSiteId("BedBathUS");
		savedOrder.setSiteId("BedBathUS");
		bbbOrderManager.mergeOrders(inSessionOrder, savedOrder);
		
		List<BBBCommerceItem> mergedItems = savedOrder.getCommerceItems();
		assertEquals("CommerceItems size should be 5 ", 5, mergedItems.size());
		assertEquals("Shippinggroup size should be 4 ", 4, savedOrder.getShippingGroupCount());
		
		
		
		//assertEquals("The merge order should create 5 commerce items", 5, mergedItems.size());*/
		
		/*BBBProfileFormHandler b2cProfileFormHandler = (BBBProfileFormHandler)getObject("b2cProfileFormHandler");
		b2cProfileFormHandler.getValueMap().put("login", email);
		b2cProfileFormHandler.getValueMap().put("password", pwd);
		SiteContext pSiteContext = (SiteContext)resolveName("/atg/multisite/SiteContext");
		
		
		Site s = pSiteContext.getSite();
		b2cProfileFormHandler.setSiteContext(pSiteContext);
		
		b2cProfileFormHandler.handleLogin(getRequest(), getResponse());*/
	

	}
	
public void testMergeOrdersForLTLItems() throws Exception {
		
		BBBOrderManager bbbOrderManager = (BBBOrderManager)getObject("bbbOrderManager");
		BBBCommerceItemManager bbbItemManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");
		BBBShippingGroupManager bbbSGManager = (BBBShippingGroupManager)getObject("bbbShippingGroupManager");
		BBBProfileTools profieTools = (BBBProfileTools)getObject("bbbProfileTools");
		
		String catalogRefId1  = (String)getObject("catalogRefId1");
		String catalogRefId2  = (String)getObject("catalogRefId2");
		String productId1 = (String)getObject("productId1");	//xprod1014
		String productId2 = (String)getObject("productId2");	//xprod1008
		Address pAddress = new Address();
		pAddress.setAddress1("address1");
		pAddress.setAddress1("address2");
		pAddress.setCity("city");
		pAddress.setFirstName("fname");
		pAddress.setFirstName("lname");
		pAddress.setPostalCode("10001");
		pAddress.setCountry("US");
		String email = (String)getObject("email");
		RepositoryItem profile = profieTools.getItemFromEmail(email);		
		String pSiteId = (String) getObject("siteId");
				SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
}
		
		BBBCommerceItem inSessionItem1 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 2);
		BBBCommerceItem inSessionItem2 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId2, productId2, 1);						
		inSessionItem1.setLtlItem(true);
		inSessionItem2.setLtlItem(true);
		Order inSessionOrder = bbbOrderManager.createOrder(profile.getRepositoryId());
		inSessionOrder.setSiteId("BuyBuyBaby");
		bbbItemManager.addItemToOrder(inSessionOrder, inSessionItem1);
		bbbItemManager.addItemToOrder(inSessionOrder, inSessionItem2);
		
		ShippingGroup sg0 = bbbSGManager.getFirstNonGiftHardgoodShippingGroup(inSessionOrder);
		sg0.setShippingMethod("LR");
		bbbItemManager.addItemQuantityToShippingGroup(inSessionOrder, inSessionItem1.getId(), sg0.getId(), inSessionItem1.getQuantity());
		bbbOrderManager.createDeliveryAssemblyCommerceItems(inSessionItem1, sg0, inSessionOrder);
		
		ShippingGroup sg1 = bbbSGManager.createHardGoodShippingGroup(inSessionOrder, pAddress, "LT");
		bbbItemManager.addItemQuantityToShippingGroup(inSessionOrder, inSessionItem2.getId(), sg1.getId(), inSessionItem2.getQuantity());
		bbbOrderManager.createDeliveryAssemblyCommerceItems(inSessionItem2, sg1, inSessionOrder);
		
		List<BBBCommerceItem> inSessionItems = inSessionOrder.getCommerceItems();
		assertEquals("CommerceItems size should be 4 ", 4, inSessionItems.size());
		List<ShippingGroup> sgList =  inSessionOrder.getShippingGroups();
		assertEquals("Shippinggroup size should be 2 ", 2, sgList.size());
		
		//Product xprod1014
		BBBCommerceItem savedItem1 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 1);
		BBBCommerceItem savedItem2 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId2, productId2, 1);						
		savedItem1.setLtlItem(true);
		savedItem2.setLtlItem(true);
		Order savedOrder = bbbOrderManager.createOrder(profile.getRepositoryId());
		savedOrder.setSiteId("BuyBuyBaby");
		bbbItemManager.addItemToOrder(savedOrder, savedItem1);
		bbbItemManager.addItemToOrder(savedOrder, savedItem2);
		
		ShippingGroup sg2 = bbbSGManager.getFirstNonGiftHardgoodShippingGroup(savedOrder);
		sg2.setShippingMethod("LR");
		bbbItemManager.addItemQuantityToShippingGroup(savedOrder, savedItem1.getId(), sg2.getId(), savedItem1.getQuantity());
		bbbOrderManager.createDeliveryAssemblyCommerceItems(savedItem1, sg2, savedOrder);
		
		ShippingGroup sg3 = bbbSGManager.createHardGoodShippingGroup(savedOrder, pAddress, "LC");
		bbbItemManager.addItemQuantityToShippingGroup(savedOrder, savedItem2.getId(), sg3.getId(), savedItem2.getQuantity());
		bbbOrderManager.createDeliveryAssemblyCommerceItems(savedItem2, sg3, savedOrder);
		
		bbbOrderManager.mergeOrders(inSessionOrder, savedOrder);
		
		List<BBBCommerceItem> mergedItems = savedOrder.getCommerceItems();
		assertEquals("CommerceItems size should be 6 ", 6, mergedItems.size());
		assertEquals("Shippinggroup size should be 4 ", 3, savedOrder.getShippingGroupCount());
	}
}
