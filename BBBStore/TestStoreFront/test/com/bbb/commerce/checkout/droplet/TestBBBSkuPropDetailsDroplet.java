package com.bbb.commerce.checkout.droplet;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.sapient.common.tests.BaseTestCase;

/**
 * To test TestBBBSkuPropDetailsDroplet service method.
 * 
 * @author Rajesh
 */

public class TestBBBSkuPropDetailsDroplet extends BaseTestCase {

	/**
	 * To test the TestIsProductSKUShippingDroplet
	 * 
	 * @throws Exception
	 */
	public void testService() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		BBBSkuPropDetailsDroplet skuPropDetailsDroplet = (BBBSkuPropDetailsDroplet)getObject("BBBSkuPropDetailsDroplet");
		BBBCartFormhandler cartFormhandler = createorderAndAdditem(pRequest, pResponse);
		Order order   = cartFormhandler.getOrder();
		pRequest.setParameter("order", order);
		skuPropDetailsDroplet.service(pRequest, pResponse);
		
		assertNull(pRequest.getParameter("skuDetails"));
		assertNull(pRequest.getParameter("skuDetailsMap"));
	}
	
	
	/**
	 *  len
	 * This method creates an order and add a commerce item to it.
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @return BBBCartFormhandler
	 * 
	 * @throws Exception
	 */

	private BBBCartFormhandler createorderAndAdditem(
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws Exception {
		

		BBBCartFormhandler cartFormhandler = (BBBCartFormhandler)pRequest.resolveName("/atg/commerce/order/purchase/CartModifierFormHandler");
		
		PriceListManager priceListManager = (PriceListManager)pRequest.resolveName("/atg/commerce/pricing/priceLists/PriceListManager");

		String listPriceId = (String) getObject("listPriceId");
		String salePriceId = (String) getObject("salePriceId");
		String siteId = (String) getObject("siteId");

		RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);

		MutableRepositoryItem profileItem =  (MutableRepositoryItem) cartFormhandler.getProfile();
		
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);
		
		
		cartFormhandler.setProfile(profileItem);
		cartFormhandler.setSiteId(siteId);

		// Test logic
		cartFormhandler.setAddItemCount(1);
		
		

		// Add one online product with quantity 2

		String pCatalogRefId = (String) getObject("catalogRefId");
		String pProductId = (String) getObject("productId");

		if (cartFormhandler.getItems() != null && cartFormhandler.getItems()[0] != null) {
			cartFormhandler.getItems()[0].setProductId(pProductId);
			cartFormhandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			cartFormhandler.getItems()[0].setQuantity(2);
		}
		HardgoodShippingGroup sg = (HardgoodShippingGroup)cartFormhandler.getOrder().getShippingGroups().get(0);	
		sg.getShippingAddress().setState((String) getObject("state"));
		cartFormhandler.setShippingGroup(sg);

		cartFormhandler.handleAddItemToOrder(pRequest, pResponse);
		return cartFormhandler;
	}


	

}