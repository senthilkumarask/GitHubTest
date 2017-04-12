package com.bbb.commerce.checkout.droplet;

import atg.commerce.order.OrderManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.ecommerce.order.BBBOrder;
import com.sapient.common.tests.BaseTestCase;

/**
 * To test TestBBBCheckoutProductDetailDroplet service method.
 * 
 * @author msiddi
 */

public class TestBBBCheckoutProductDetailDroplet extends BaseTestCase {

	/**
	 * To test the BBBCheckoutProductDetailDroplet
	 * 
	 * @throws Exception
	 */
	public void testBBBCheckoutProductDetailDroplet() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		BBBCheckoutProductDetailDroplet droplet = (BBBCheckoutProductDetailDroplet)getObject("BBBCheckoutProductDetailDroplet");
		
		String catalogRefId = (String) getObject("catalogRefId");
		String productId = (String) getObject("productId");
		String siteId = (String) getObject("siteId");
		pRequest.setParameter("productId", productId);
		pRequest.setParameter("skuId", catalogRefId);
		pRequest.setParameter("siteId", siteId);
		
		
		OrderManager manager = (OrderManager) this.getObject("orderManager");
		BBBOrder order = (BBBOrder) manager.createOrder("6533222");
		pRequest.setParameter("order", order);
		
		droplet.service(pRequest, pResponse);
		
		assertNotNull("productVo is null", pRequest.getParameter("productVO"));
		assertNotNull("SKUDetailVO is null", pRequest.getParameter("pSKUDetailVO"));
		
		String errCatalogRefId = (String) getObject("errCatalogRefId");
		String errProductId = (String) getObject("errProductId");
		pRequest.setParameter("productId", errProductId);
		pRequest.setParameter("skuId", errCatalogRefId);
		
		//clear previous output
		pRequest.removeParameter("productVO");
		pRequest.removeParameter("pSKUDetailVO");
		
		droplet.service(pRequest, pResponse);
		assertNull("productVo is not null", pRequest.getParameter("productVO"));
		assertNull("SKUDetailVO is not null", pRequest.getParameter("pSKUDetailVO"));

	}

	

}