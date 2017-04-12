/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order.purchase/TestBBBPurchaseProcessHelper.java.TestBBBPurchaseProcessHelper $$
 * @updated $DateTime: Nov 5, 2011 9:59:29 AM
 */
package com.bbb.commerce.order.purchase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.userprofiling.Profile;

import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.shipping.BBBShippingInfoBean;
import com.bbb.commerce.pricing.BBBNonMerchandiseCommerceItemFilter;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBPurchaseProcessHelper extends BaseTestCase {
	
	public void testGetShippingGroupForItem() throws Exception {
		BBBPurchaseProcessHelper purchaseHelper = (BBBPurchaseProcessHelper)getObject("bbbPurchaseProcessHelper");
		OrderTools orderTools = (OrderTools)getObject("orderTools");
		BBBShippingGroupManager bbbSGManager = (BBBShippingGroupManager)getObject("bbbShippingGroupManager");
		String siteId = (String)getObject("siteId");
		getRequest().setParameter("siteId", siteId);
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }
		Order pOrder = orderTools.createOrder("default");
		pOrder.setSiteId(siteId);
		String storeId1 = (String)getObject("storeId");
		AddCommerceItemInfo pItemInfo = new AddCommerceItemInfo();
		pItemInfo.setCatalogRefId((String)getObject("catalogRefId"));
		pItemInfo.setProductId((String)getObject("productId"));
		String qtyStr = (String)getObject("quantity");
		pItemInfo.setQuantity(Integer.parseInt(qtyStr));
		
		ShippingGroup pShippingGroup = bbbSGManager.createShippingGroup();	
		//bbbSGManager.addShippingGroupToOrder(pOrder, pShippingGroup);
		pItemInfo.getValue().put("storeId","");
		ShippingGroup tempShippingGroup = purchaseHelper.getShippingGroupForItem(pOrder, pItemInfo, pShippingGroup, null);
		
		assertEquals("getShippingGroupForItem method created new Shipping group which was not expected ", ((ShippingGroup)pOrder.getShippingGroups().get(0)).getId(), tempShippingGroup.getId());
		
		
		pItemInfo.getValue().put("storeId", storeId1);
		ShippingGroup tempShippingGroup1 = purchaseHelper.getShippingGroupForItem(pOrder, pItemInfo, pShippingGroup, null);
		assertNotSame("getShippingGroupForItem method did not create new StoreShipping group for store pickup item ", ((ShippingGroup)pOrder.getShippingGroups().get(0)).getId(), tempShippingGroup1.getId());
		
		ShippingGroup tempShippingGroup2 =  purchaseHelper.getShippingGroupForItem(pOrder, pItemInfo, pShippingGroup, null);
		assertEquals("getShippingGroupForItem method again created new StoreShippingGroup but did not pick the already present StoreShippingGroup in order which was not expected ", tempShippingGroup1.getId(), tempShippingGroup2.getId());
	
	}
	
	
	public void testCheckInventory() throws Exception {
		BBBPurchaseProcessHelper purchaseHelper = (BBBPurchaseProcessHelper)getObject("bbbPurchaseProcessHelper");
		OrderTools orderTools = (OrderTools)getObject("orderTools");
		BBBCommerceItemManager bbbManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");
		BBBStoreInventoryContainer storeInventoryContainer = (BBBStoreInventoryContainer)getRequest().
				resolveName("/com/bbb/commerce/common/BBBStoreInventoryContainer");
		
		String pSkuId1 = (String)getObject("catalogRefId1");	
		String pSkuId2 = (String)getObject("catalogRefId2");
		String pSkuId3 = (String)getObject("catalogRefId3");
		
		String pProductId = (String)getObject("productId");
		String pStoreId = 	null; 	
		String pSiteId = (String)getObject("siteId");
		String strQty = (String)getObject("quantity");
		String globalStock = (String)getObject("globalStock");
		long pItemQty = Long.parseLong(strQty);
		long pGlobalStockQty = Long.parseLong(globalStock);
		
		Order pOrder = orderTools.createOrder("default");				
				
		
		int available = purchaseHelper.checkInventory(pSiteId, pSkuId1, pStoreId, pOrder, pItemQty, BBBInventoryManager.ADD_ITEM, storeInventoryContainer, BBBInventoryManager.AVAILABLE);
		assertEquals("checkInventory should return "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, available);
		
		available = purchaseHelper.checkInventory(pSiteId, pSkuId2, pStoreId, pOrder, pItemQty, BBBInventoryManager.STORE_ONLINE, storeInventoryContainer, BBBInventoryManager.AVAILABLE);
		assertEquals("checkInventory should return "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, available);
		
		available = purchaseHelper.checkInventory(pSiteId, pSkuId3, pStoreId, pOrder, pItemQty, BBBInventoryManager.STORE_ONLINE, storeInventoryContainer, BBBInventoryManager.AVAILABLE);
		assertEquals("checkInventory should return "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, available);
		
		available = purchaseHelper.checkInventory(pSiteId, pSkuId3, pStoreId, pOrder, pItemQty, BBBInventoryManager.RETRIEVE_CART, storeInventoryContainer, BBBInventoryManager.AVAILABLE);
		assertEquals("checkInventory should return "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, available);
		
		
		BBBCommerceItem pNewItem = (BBBCommerceItem)bbbManager.createCommerceItem(pSkuId1, pProductId, pGlobalStockQty);
		pOrder.addCommerceItem(pNewItem);
		
		long rolledUpQty = purchaseHelper.getRollupQuantities(pStoreId, pSkuId3, (BBBOrderImpl)pOrder, pItemQty, null);
		available = purchaseHelper.checkInventory(pSiteId, pSkuId3, pStoreId, pOrder, rolledUpQty, BBBInventoryManager.ADD_ITEM, storeInventoryContainer, BBBInventoryManager.AVAILABLE);
		assertEquals("checkInventory should return "+BBBInventoryManager.AVAILABLE, BBBInventoryManager.AVAILABLE, available);
	
	}
	
	

	public void testManageAddOrRemoveGiftWrapToShippingGroup() throws Exception {

		BBBPurchaseProcessHelper purchaseHelper = (BBBPurchaseProcessHelper) getObject("bbbPurchaseProcessHelper");		
		BBBCommerceItemManager itemManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");		
		OrderHolder shoppingCart = (OrderHolder)getObject("shoppingCart");		
		String pSiteId = (String)getObject("siteId");
		OrderManager orderManager = (OrderManager)getObject("ordermanager");
		

		boolean flagGiftWrapItem = false;
		
		purchaseHelper.getTransactionManager().begin();
		Order order = orderManager.createOrder(((Profile) getRequest().resolveName("/atg/userprofiling/Profile")).getRepositoryId());
		
		String pCatalogRefId = (String)getObject("catalogRefId");
		String pProductId = (String)getObject("productId");
		long pQuantity = 1;
		int initialCount = order.getCommerceItemCount();
		String nonGiftWrapItem = null;

		CommerceItem item = (BBBCommerceItem) itemManager.createCommerceItem(pCatalogRefId, pProductId, 1);
		item.getAuxiliaryData().setSiteId(order.getSiteId());
		itemManager.addItemToOrder(order, item);		
		
		ShippingGroup shippingGroup =  purchaseHelper.getShippingGroupManager().getFirstNonGiftHardgoodShippingGroup(order);
		itemManager.addItemQuantityToShippingGroup(order, item.getId(), shippingGroup.getId(), 1);
		
		orderManager.updateOrder(order);
		System.out.println("Commerce Item Created: " + order.getCommerceItems().get(initialCount));
		System.out.println("Item in the Cart: " + order.getCommerceItems().size());
		
		int initialNonGiftItems = order.getCommerceItemCount();
		
		BBBShippingInfoBean shippingInfoBean = new BBBShippingInfoBean();
		shippingInfoBean.setGiftingFlag(true);
		shippingInfoBean.setGiftMessage("Happy Birthday");
		shippingInfoBean.setGiftWrap(true);
		
		boolean flag = purchaseHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, shippingGroup, pSiteId, shippingInfoBean);
		System.out.println("See the manageGiftWrap flag" + flag);
		List<CommerceItem> list = order.getCommerceItems();
		System.out.println("Item in the Cart: " + list.size());
		
		BBBNonMerchandiseCommerceItemFilter nonMerchandiseFilter = (BBBNonMerchandiseCommerceItemFilter) getObject("bbbNonMerchandiseCommerceItemFilter");
		List<FilteredCommerceItem> filterItemList = new ArrayList<FilteredCommerceItem>();
		  
		assertEquals("CommerceItem count should be 1 ", initialNonGiftItems + 1, order.getCommerceItemCount());
		
		List commerce = new ArrayList();
		for (CommerceItem commerceItem : list) {
			
			FilteredCommerceItem filteredItem = new FilteredCommerceItem(commerceItem);
			filterItemList.add(filteredItem);
			
			System.out.println(commerceItem.getCatalogRefId());
			System.out.println(commerceItem.getCommerceItemClassType());
			if (commerceItem.getCommerceItemClassType().equalsIgnoreCase("giftWrapCommerceItem")) {
				flagGiftWrapItem = true;
			} else {
				commerce.add(commerceItem.getId());
			}
		}
		
		//add a Eco fee type commerce item to filterItemList
		//EcoFeeCommerceItem ecoCommerceItem = new EcoFeeCommerceItem();
		FilteredCommerceItem filteredItemObj = new FilteredCommerceItem(createEcoFeeCommItem());
		filterItemList.add(filteredItemObj);
		
		assertEquals("The filtered items should have 3 commerce items", 3, filterItemList.size());
		nonMerchandiseFilter.filterItems(1, null, null, null, null, filterItemList);
        assertEquals("The filtered items should not have non merchandise commerce items", initialNonGiftItems, filterItemList.size());
			
		// Scenario to add gift wrap
		assertEquals("GiftWrap Item Added Successfully", flagGiftWrapItem, true);
		
		for (Object object : commerce) {
		    itemManager.removeItemFromOrder(order, object.toString());
        }
		
		orderManager.updateOrder(order);
		purchaseHelper.getTransactionManager().commit();
		System.out.println("Order Items Count Now : " + order.getCommerceItems().size());
		
		
		
		// Scenario to remove shipping group having only giftwrap item
		assertEquals("GiftWrap Item Removed Successfully", order.getCommerceItemCount(), 0);
		
}

	private CommerceItem createEcoFeeCommItem() {

		EcoFeeCommerceItem ecoFeeCommItem = new EcoFeeCommerceItem();
		ecoFeeCommItem.setRepositoryItem(new MutableRepositoryItem() {

			@Override
			public String getItemDisplayName() {
				return null;
			}

			@Override
			public boolean isTransient() {
				return false;
			}

			@Override
			public String getRepositoryId() {
				return null;
			}

			@Override
			public Repository getRepository() {
				return null;
			}

			@Override
			public Object getPropertyValue(String s) {
				return null;
			}

			@Override
			public RepositoryItemDescriptor getItemDescriptor()
					throws RepositoryException {
				return null;
			}

			@Override
			public Collection<String> getContextMemberships()
					throws RepositoryException {
				return null;
			}

			@Override
			public void setPropertyValue(String s, Object obj) {
				
			}
		});

		return ecoFeeCommItem;
	}
 
 }
