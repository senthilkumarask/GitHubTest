/*
 *  Copyright 2014, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestInternationalCheckoutManager.java
 *
 *  DESCRIPTION: Test InternationalCheckoutManager
 *
 *  HISTORY:
 *  Oct 14, 2011 Initial version
 */
package com.bbb.internationalshipping.manager;

import java.io.IOException;

import atg.commerce.CommerceException;
import atg.repository.xml.GetException;
import atg.service.idgen.IdGeneratorException;

import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.sapient.common.tests.BaseTestCase;


public class TestInternationalCheckoutManager extends BaseTestCase {

	
	/*************  TestCreateDSOrderId  
	 * @throws CommerceException 	
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException
	 * @throws IdGeneratorException 
	 */
	public void testCreateDSOrderId() throws IOException, BBBSystemException, BBBBusinessException, CommerceException, IdGeneratorException {
		
			InternationalCheckoutManager internationalCheckoutManager = (InternationalCheckoutManager) getObject("internationalCheckoutManager");
			//Creating Order for BBBOrder
			BBBOrderTools orderTools = (BBBOrderTools)getObject("bbbOrderTools");
			String orderType = (String)getObject("orderType");
			BBBOrder order = (BBBOrder)orderTools.createOrder(orderType);
			
			//Adding Commerce Item to Order to get Order type
			BBBCommerceItemManager bbbItemManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");
			String catalogRefId1  = (String)getObject("catalogRefId1");
			String productId1 = (String)getObject("productId1");
			BBBCommerceItem inSessionItem1 = (BBBCommerceItem)bbbItemManager.createCommerceItem(catalogRefId1, productId1, 2);
			bbbItemManager.addItemToOrder(order, inSessionItem1);
			String siteId= (String) getObject("siteId");
			//Calling createDSOrderId method
			String dsOrderId = internationalCheckoutManager.createDSOrderId(getRequest(), order, siteId);
				System.out.println("dsOrderId : "+ dsOrderId);
			assertNotNull(dsOrderId); 
				
			
		
	}
	
	/*************  TestPersistOrderXml  
		 * @throws CommerceException 
		 * @throws GetException 
		 * @throws IOException
		 * @throws BBBSystemException
		 * @throws BBBBusinessException
		 * @throws CommerceException
		 * @throws GetException
		 */
		public void testPersistOrderXml() throws IOException, BBBSystemException, BBBBusinessException, CommerceException, GetException {
				
				
					InternationalCheckoutManager internationalCheckoutManager = (InternationalCheckoutManager) getObject("internationalCheckoutManager");
					BBBOrderTools orderTools = (BBBOrderTools)getObject("bbbOrderTools");
					String orderType = (String)getObject("orderType");
					BBBOrder order = (BBBOrder)orderTools.createOrder(orderType);
					String orderId = order.getId();
					String e4XOrderId = (String) getObject("e4XOrderId");
					String merchantOrderId = (String) getObject("merchantOrderId");
					String countryCode = (String) getObject("countryCode");
					String currencyCode = (String) getObject("currencyCode");
							
					
					internationalCheckoutManager.persistOrderXml(orderId, e4XOrderId,merchantOrderId,countryCode,currencyCode);
						System.out.println("Order XML : "+internationalCheckoutManager.getIntlRepository().getOrderXml(orderId));
					assertNotNull(internationalCheckoutManager.getIntlRepository().getOrderXml(orderId)); 
				
			}
		

		/*************  TestCreateCartCookies  
		 * @throws CommerceException 
		 * @throws IOException
		 * @throws BBBSystemException
		 * @throws BBBBusinessException
		 * @throws CommerceException
		 */
		public void testCreateCartCookies() throws IOException, BBBSystemException, BBBBusinessException, CommerceException {
				
					InternationalCheckoutManager internationalCheckoutManager = (InternationalCheckoutManager) getObject("internationalCheckoutManager");
					//Creating Order for BBBOrder
					BBBOrderTools orderTools = (BBBOrderTools)getObject("bbbOrderTools");
					String orderType = (String)getObject("orderType");
					BBBOrder order = (BBBOrder)orderTools.createOrder(orderType);
					String profile = (String) getObject("profile");
					internationalCheckoutManager.setUserProfile(profile);
					internationalCheckoutManager.createCartCookies(getRequest(), getResponse(), order);
						
					System.out.println("Cookie Name : "+ internationalCheckoutManager.getOrderCookieName());
					assertNotNull(internationalCheckoutManager.getOrderCookieName()); 
								
				
			}
			
}

