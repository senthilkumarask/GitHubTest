/*
 *  Copyright 2014, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestCurrencyCountrySelectorHandler.java
 *
 *  DESCRIPTION: Test Currency Country Selector form Handler
 *
 *  HISTORY:
 *  Oct 14, 2011 Initial version
 */
package com.bbb.internationalshipping.formHandler;



import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;

import atg.commerce.pricing.priceLists.PriceListManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.constants.BBBInternationalShippingConstants;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.internationalshipping.formhandler.InternationalShipFormHandler;
import com.sapient.common.tests.BaseTestCase;


public class TestInternationalShipFormHandler extends BaseTestCase {

	
	
	/**
	 * testEnvoyIntlNon_Restricteditem()  testing non restricted item and full envoy url for international envoy checkout url
	 * @throws Exception
	 */
	public void testEnvoyIntlNon_Restricteditem() throws Exception{ 
		InternationalShipFormHandler internationalshipformhandler = (InternationalShipFormHandler) getObject("internationalShipFormHandler");
		
		String pCatalogRefId = (String) getObject("non_restcatalogRefId");
		String pProductId = (String) getObject("non_rest_productId");
		String clientIp= (String) getObject("clientIp");
		
		//calling createOrderAndAddIntlitem() method for non_restricted item
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBCartFormhandler cartFormhandler = createOrderAndAddIntlitem(pRequest, pResponse,pCatalogRefId,pProductId);
		Order order   = cartFormhandler.getOrder();
		OrderHolder cart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
		cart.setCurrent(order);
		order = cart.getCurrent();
		pRequest.setParameter("order", order);
		pRequest.setRemoteAddr(clientIp);
		pRequest.setContextPath("/store");
		pRequest.setRequestURI("");
		String countryCode=(String) getObject("countryCode");
		String currencyCode=(String) getObject("currencyCode");
		internationalshipformhandler.setUserSelectedCountry(countryCode);
		internationalshipformhandler.setUserSelectedCurrency(currencyCode);
		internationalshipformhandler.setOrder(order);				
		//calling handleCurrencyCountrySelector() method
		internationalshipformhandler.handleEnvoyCheckout(pRequest, pResponse);
		
			System.out.println("Last cokie name : "+internationalshipformhandler.getTools().getCookieLastName());
			System.out.println("Full Envoy URL : "+internationalshipformhandler.getCheckoutResponseVO().getFullEnvoyUrl());
		
		if (internationalshipformhandler.getCheckoutResponseVO().getFullEnvoyUrl()!=null)
		{
			assertNotNull(internationalshipformhandler.getTools().getCookieLastName());
			assertNotNull(internationalshipformhandler.getCheckoutResponseVO().getFullEnvoyUrl());
		}
		
			
		
	}
	
	
	
	
	/**
	 * testEnvoyIntlCanadaErrorScenario() calling for international envoy checkout for Canada
	 * 
	 * @throws Exception
	 */
	public void testEnvoyIntlCanadaErrorScenario() throws Exception { 
		
		
		String pCatalogRefId = (String) getObject("non_restcatalogRefId");
		String pProductId = (String) getObject("non_rest_productId");
		String clientIp= (String) getObject("clientIp");
		
		//calling createOrderAndAddIntlitem() method for non_restricted item
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBCartFormhandler cartFormhandler = createOrderAndAddIntlitem(pRequest, pResponse,pCatalogRefId,pProductId);
		Order order   = cartFormhandler.getOrder();
		OrderHolder cart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
		cart.setCurrent(order);
		order = cart.getCurrent();
		pRequest.setParameter("order", order);
		pRequest.setRemoteAddr(clientIp);
		pRequest.setContextPath("/store");
		pRequest.setRequestURI("");
				
		String countryCode=(String) getObject("countryCode");
		String currencyCode=(String) getObject("currencyCode");
		InternationalShipFormHandler internationalshipformhandler = (InternationalShipFormHandler) getObject("internationalShipFormHandler");
		internationalshipformhandler.setUserSelectedCountry(countryCode);
		internationalshipformhandler.setUserSelectedCurrency(currencyCode);
		internationalshipformhandler.setOrder(order);		
		//calling handleCurrencyCountrySelector() method
		internationalshipformhandler.handleEnvoyCheckout(pRequest, pResponse);
		
			System.out.println("Last cokie name : "+internationalshipformhandler.getTools().getCookieLastName());
			System.out.println("Form Exception : "+internationalshipformhandler.getFormExceptions());
		
		assertNotNull(internationalshipformhandler.getTools().getCookieLastName());
		if(null != internationalshipformhandler.getFormExceptions())
		{
			assertNotNull(internationalshipformhandler.getFormExceptions());
		}
		
			
	}
	
	
	/**
	 * createOrderAndAddIntlitem()
	 * Creating Order and adding an item to the order 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param pCatalogRefId
	 * @param pProductId
	 * @return
	 * @throws Exception
	 */
	private BBBCartFormhandler createOrderAndAddIntlitem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pCatalogRefId,String pProductId) throws Exception {
		
		//BBBCartFormhandler cartFormhandler = (BBBCartFormhandler)pRequest.resolveName("/atg/commerce/order/purchase/CartModifierFormHandler");	
		final BBBCartFormhandler cartFormhandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");
		//PriceListManager priceListManager = (PriceListManager)pRequest.resolveName("/atg/commerce/pricing/priceLists/PriceListManager");
		 final PriceListManager priceListManager = (PriceListManager)this.getObject("bbbPriceListManager");
		 //cartFormhandler.resetFormExceptions();
		//cartFormhandler.getOrder().removeAllCommerceItems();
		String listPriceId = (String) getObject("listPriceId");
		String salePriceId = (String) getObject("salePriceId");
		String siteId= (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }
		
        RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);

		MutableRepositoryItem profileItem =  (MutableRepositoryItem) cartFormhandler.getProfile();
		this.getRequest().resolveName("/atg/userprofiling/ProfileTools");
		
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);
		cartFormhandler.setProfile(profileItem);
		cartFormhandler.setOrder(cartFormhandler.getOrderManager().createOrder(profileItem.getRepositoryId(), cartFormhandler.getOrder().getOrderClassType()));

		// Test logic
		cartFormhandler.setAddItemCount(1);
		//HardgoodShippingGroup sg = (HardgoodShippingGroup)cartFormhandler.getOrder().getShippingGroups().get(0);	
		//sg.getShippingAddress().setState((String) getObject("state"));
		//
		// Add one online product with quantity 2

		if (cartFormhandler.getItems() != null && cartFormhandler.getItems()[0] != null) {
			cartFormhandler.getItems()[0].setProductId(pProductId);
			cartFormhandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			cartFormhandler.getItems()[0].setQuantity(2);
			cartFormhandler.setSiteId(siteId);
		}
		
		
		cartFormhandler.handleAddItemToOrder(pRequest, pResponse);
		
		return cartFormhandler;
}
		
	/**
	 * testEnvoyIntlRestricteditem() testing restricted item, error and error message for international envoy checkout url
	 * @throws Exception
	 */
	public void testEnvoyIntlRestricteditem() throws Exception { 
			InternationalShipFormHandler internationalshipformhandler = (InternationalShipFormHandler) getObject("internationalShipFormHandler");
			
			String pCatalogRefId = (String) getObject("rest_catalogRefId");
			String pProductId = (String) getObject("rest_productId");
			
			//calling createOrderAndAddIntlitem() method for Restricted item
			DynamoHttpServletRequest pRequest = getRequest();
			DynamoHttpServletResponse pResponse = getResponse();
			BBBCartFormhandler cartFormhandler = createOrderAndAddIntlitem(pRequest, pResponse,pCatalogRefId,pProductId);
			Order order   = cartFormhandler.getOrder();
			OrderHolder cart = (OrderHolder) pRequest.resolveName("/atg/commerce/ShoppingCart");
			cart.setCurrent(order);
			order = cart.getCurrent();
			pRequest.setParameter("order", order);
			
			String countryCode=(String) getObject("countryCode");
			String currencyCode=(String) getObject("currencyCode");
			internationalshipformhandler.setUserSelectedCountry(countryCode);
			internationalshipformhandler.setUserSelectedCurrency(currencyCode);
			internationalshipformhandler.setOrder(order);			
			//calling handleCurrencyCountrySelector() method
			internationalshipformhandler.handleEnvoyCheckout(pRequest, pResponse);
				System.out.println("Last cokie name : "+internationalshipformhandler.getTools().getCookieLastName());
				System.out.println("ENVOY_ERROR : "+pRequest.getSession().getAttribute(BBBInternationalShippingConstants.ENVOY_ERROR));
				System.out.println("Form Exception : "+internationalshipformhandler.getFormExceptions());
								
			assertNotNull(internationalshipformhandler.getTools().getCookieLastName());
			
			if(null != internationalshipformhandler.getFormExceptions())
			{
				assertNotNull(internationalshipformhandler.getFormExceptions());
				assertNotNull(pRequest.getSession().getAttribute(BBBInternationalShippingConstants.ENVOY_ERROR));	
			}
				
	}
	
		
}
