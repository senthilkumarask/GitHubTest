package com.bbb.commerce.order.scheduler;

import java.util.List;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.repository.RepositoryItem;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.order.bean.BBBCommerceItem;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBTibcoOrderMessageSchedulerJob extends BaseTestCase {

	public void testTibcoOrderMessageSchedulerJob() throws Exception {

		BBBTibcoOrderMessageSchedulerJob tibcoOrderMessageSchedulerJob = (BBBTibcoOrderMessageSchedulerJob) getObject("tibcoOrderMessageSchedulerJob");
		tibcoOrderMessageSchedulerJob.setSchedulerEnabled(true);
		tibcoOrderMessageSchedulerJob.getOrderBatchSize();
		tibcoOrderMessageSchedulerJob.getOrderSubmittedDurationToSearch();
		tibcoOrderMessageSchedulerJob.isSchedulerEnabled();
		tibcoOrderMessageSchedulerJob.getOrderSubmittedTime();
		tibcoOrderMessageSchedulerJob.getJobSchedule();

		tibcoOrderMessageSchedulerJob.getOrderHelper();
		tibcoOrderMessageSchedulerJob.getOrderManager();
		
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
	}
}
