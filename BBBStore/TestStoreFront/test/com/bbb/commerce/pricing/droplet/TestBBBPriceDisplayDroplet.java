/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.pricing.droplet/TestBBBPriceDisplayDroplet.java.TestBBBPriceDisplayDroplet $$
 * @updated $DateTime: Nov 8, 2011 5:14:19 PM
 */
package com.bbb.commerce.pricing.droplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.ProfileTools;

import com.bbb.account.droplet.BBBEncryptionDroplet;
import com.bbb.commerce.cart.droplet.BBBCartDisplayDroplet;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBPriceDisplayDroplet extends BaseTestCase {
	private static final String PARAM_PRICE_OBJECT = "priceObject";
	private static final String OUTPUT_PARAM_PRICEINFOVO = "priceInfoVO";
	private static final String PARAM_ORDER_OBJECT = "orderObject";
	private static final String SHIPPINGGROUP = "shippingGroup";
	private static final String LTL_SKU_ID = "ltlSkuId";
	private static final String EXPECTED_DELIVERY_DATE = "expectedDeliveryDate";
	
	public void testServiceForCommerceItem() throws Exception {

		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		BBBCartFormhandler formHandler = (BBBCartFormhandler)getObject("bbbCartFormHandler");
		
		//Add all parametrs in profile level
		MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
        
		//MutableRepositoryItem profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("andrew@example.com");
        
		PriceListManager priceListManager = (PriceListManager)getObject("bbbPriceListManager");
		String listPriceId = (String)getObject("listPriceId");
		String salePriceId = (String)getObject("salePriceId");
		RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);
		
		CustomCatalogTools catalogTools = (CustomCatalogTools)getObject("bbbCatalogTools");
		List catalogIds = new ArrayList();
		String catalogId = (String)getObject("catalogId");
		catalogIds.add(catalogId);
		if(catalogTools.getCatalogsForRepository(null, catalogIds) != null){
			RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(null, catalogIds)[0];
			profileItem.setPropertyValue("catalog", catalogItem);
			
		}
		
		formHandler.setProfile(profileItem);
		
		formHandler.setOrder(formHandler.getOrderManager().createOrder(profileItem.getRepositoryId(), formHandler.getOrder().getOrderClassType()));
		//formHandler.getOrder().removeAllCommerceItems();
		/*List commerceItems = formHandler.getOrder().getCommerceItems();
		formHandler.getOrderManager().getCommerceItemManager().removeAllCommerceItemsFromOrder(formHandler.getOrder());
		for (Iterator iterator = commerceItems.iterator(); iterator.hasNext();) {
		    CommerceItem object = (CommerceItem) iterator.next();
            formHandler.getOrderManager().getCommerceItemManager().removeAllRelationshipsFromCommerceItem(formHandler.getOrder(), object.getId());
            
        }*/
		//Test logic
		formHandler.setAddItemCount(1);		
		
		// Test Scenario 1
		// Add one online product with quantity 2
		
		String pCatalogRefId = (String)getObject("catalogRefId");	
		String pProductId = (String)getObject("productId");
		long pQuantity = 2;		
		
		if(formHandler.getItems() != null && formHandler.getItems()[0] != null){
			formHandler.getItems()[0].setProductId(pProductId);
			formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			formHandler.getItems()[0].setQuantity(pQuantity);
			formHandler.setSiteId("BedBathUS");
		}
		formHandler.handleAddItemToOrder(pRequest, pResponse);
		
		BBBPriceDisplayDroplet priceDisplayDroplet = (BBBPriceDisplayDroplet) getObject("bbbPriceDisplayDroplet");
		//BBBCommerceItemManager bbbManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");
		
		if(formHandler.getOrder().getCommerceItemCount() > 0){
			BBBCommerceItem bbbItem = (BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0);		
			assertNotNull(bbbItem);
		
			
			
			
			pRequest.setParameter(PARAM_PRICE_OBJECT, bbbItem);
			priceDisplayDroplet.service(pRequest, pResponse);
			
			PriceInfoVO priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);
			
			assertEquals("Item quantity should match", priceInfoVO.getUndiscountedItemsCount(), bbbItem.getQuantity());
			assertTrue("No discounts should be applied", priceInfoVO.getTotalSavedAmount() < 0.001);
			
			pRequest.setParameter(PARAM_PRICE_OBJECT, formHandler.getOrder());
            priceDisplayDroplet.service(pRequest, pResponse);
            priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);
            
            assertEquals("No store total should be applied", priceInfoVO.getTotalSavedAmount(), 0.0);
            assertEquals("order online total should be match", priceInfoVO.getOnlinePurchaseTotal(), ((BBBOrderPriceInfo) formHandler.getOrder().getPriceInfo()).getOnlineSubtotal());
			/*System.out.println(((BBBShippingPriceInfo)((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo()).getFinalShipping());
			System.out.println(((BBBShippingPriceInfo)((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo()).getFinalSurcharge());
			System.out.println(((BBBShippingPriceInfo)((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo()).getAmount());*/
			
			HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);
            double shippingAmount = ((BBBShippingPriceInfo)hardgoodShippingGroup.getPriceInfo()).getFinalShipping();
            pRequest.setParameter(PARAM_PRICE_OBJECT, hardgoodShippingGroup);
            priceDisplayDroplet.service(pRequest, pResponse);
            priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);
			assertTrue(Math.abs(priceInfoVO.getRawShippingTotal() - shippingAmount) < 0.001);
			

			// This is to test if there is only gift card item in the cart
			
			/**
			String pCatalogRefId_gift = (String)getObject("catalogRefId_gift");	
			String pProductId_gift = (String)getObject("productId_gift");
			long pQuantity_gift = 2;
			if(formHandler.getItems() != null && formHandler.getItems()[0] != null){
				formHandler.getItems()[0].setProductId(pProductId_gift);
				formHandler.getItems()[0].setCatalogRefId(pCatalogRefId_gift);
				formHandler.getItems()[0].setQuantity(pQuantity_gift);			
			}
			boolean result_gift = formHandler.handleAddItemToOrder(pRequest, pResponse);
			assertEquals("Only Gift Cart Item", "0.0", ((BBBShippingPriceInfo)((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo()).getAmount());*/
		}
	}
	
	public void testServiceForLTLCommerceItem() throws Exception {

		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
        ServletUtil.setCurrentRequest(getRequest());
        
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
}
        
		BBBCartFormhandler formHandler = (BBBCartFormhandler)getObject("bbbCartFormHandler");
		
		//Add all parametrs in profile level
		MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
        
		//MutableRepositoryItem profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("andrew@example.com");
        
		PriceListManager priceListManager = (PriceListManager)getObject("bbbPriceListManager");
		String listPriceId = (String)getObject("listPriceId");
		String salePriceId = (String)getObject("salePriceId");
		RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);
		
		CustomCatalogTools catalogTools = (CustomCatalogTools)getObject("bbbCatalogTools");
		List catalogIds = new ArrayList();
		String catalogId = (String)getObject("catalogId");
		catalogIds.add(catalogId);
		if(catalogTools.getCatalogsForRepository(null, catalogIds) != null){
			RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(null, catalogIds)[0];
			profileItem.setPropertyValue("catalog", catalogItem);
			
		}
		
		formHandler.setProfile(profileItem);
		
		formHandler.setOrder(formHandler.getOrderManager().createOrder(profileItem.getRepositoryId(), formHandler.getOrder().getOrderClassType()));
		//Test logic
		formHandler.setAddItemCount(1);		
		
		// Test Scenario 1
		// Add one online product with quantity 20
		
		String pCatalogRefId = (String)getObject("catalogRefId");	
		String pProductId = (String)getObject("productId");
		long pQuantity = 80;
		
		if(formHandler.getItems() != null && formHandler.getItems()[0] != null){
			formHandler.getItems()[0].setProductId(pProductId);
			formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			formHandler.getItems()[0].setQuantity(pQuantity);
			formHandler.getItems()[0].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,"LT");
		
			formHandler.setSiteId("BuyBuyBaby");
		}
		formHandler.handleAddItemToOrder(pRequest, pResponse);
		
		BBBPriceDisplayDroplet priceDisplayDroplet = (BBBPriceDisplayDroplet) getObject("bbbPriceDisplayDroplet");
		
		if(formHandler.getOrder().getCommerceItemCount() > 0){
			BBBCommerceItem bbbItem = (BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0);
			assertNotNull(bbbItem);
			bbbItem.setCatalogRefId(pCatalogRefId);
			
			pRequest.setParameter(PARAM_PRICE_OBJECT, bbbItem);
			pRequest.setParameter(PARAM_ORDER_OBJECT, formHandler.getOrder());
			priceDisplayDroplet.service(pRequest, pResponse);
			
			PriceInfoVO priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);
			priceInfoVO.getDeliverySurcharge();
			assertTrue(priceInfoVO.getDeliverySurcharge() > 0.0);
			assertTrue(priceInfoVO.getDeliverySurchargeSaving() > 0.0);
			pRequest.removeParameter("PARAM_ORDER_OBJECT");
			pRequest.setParameter(PARAM_PRICE_OBJECT, formHandler.getOrder());
            priceDisplayDroplet.service(pRequest, pResponse);
            
            priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);
            assertTrue(priceInfoVO.getTotalDeliverySurcharge() > 0.0);
            
			HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup)
					((BBBShippingGroupCommerceItemRelationship)bbbItem.getShippingGroupRelationships().get(0)).getShippingGroup();

			double shippingAmount = ((BBBShippingPriceInfo)hardgoodShippingGroup.getPriceInfo()).getFinalShipping();
            pRequest.setParameter(PARAM_PRICE_OBJECT, hardgoodShippingGroup);
            priceDisplayDroplet.service(pRequest, pResponse);
            priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);
			assertTrue(priceInfoVO.getTotalDeliverySurcharge() > 0.0);
			assertTrue(priceInfoVO.getDeliverySurchargeSaving() > 0.0);
			
			BBBCartDisplayDroplet bbbCartDisplayDroplet = (BBBCartDisplayDroplet)Nucleus.getGlobalNucleus().resolveName("/atg/commerce/order/droplet/BBBCartDisplayDroplet");
			pRequest.removeParameter("PARAM_ORDER_OBJECT");
			pRequest.removeParameter("PARAM_PRICE_OBJECT");
			pRequest.setParameter(LTL_SKU_ID, pCatalogRefId);
			pRequest.setParameter(SHIPPINGGROUP, hardgoodShippingGroup);
			bbbCartDisplayDroplet.service(pRequest, pResponse);
			String deliveryDate = (String)pRequest.getObjectParameter(EXPECTED_DELIVERY_DATE);
			assertEquals(true, deliveryDate.length()>0);
		}
	}
}
