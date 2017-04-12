/*
 *  Copyright 2014, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestInternationalOrderXmlRepoTools.java
 *
 *  DESCRIPTION: Test InternationalOrderXmlRepoTools
 *
 *  HISTORY:
 *  Oct 14, 2011 Initial version
 */
package com.bbb.internationalshipping.utils;

import java.io.IOException;

import atg.commerce.CommerceException;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;


public class TestInternationalOrderXmlRepoTools extends BaseTestCase {

	
	
	
	/*************  TestAddOrderXml  
	 * @throws CommerceException *************/
		
	public void testAddOrderXml() throws IOException, BBBSystemException, BBBBusinessException, CommerceException {
		
			InternationalOrderXmlRepoTools internationalOrderXmlRepoTools = (InternationalOrderXmlRepoTools) getObject("internationalOrderXmlRepoTools");
			double orderId1 = Math.random();
			String orderId=new Double(orderId1).toString();
			orderId=orderId.substring(3,13);
			String orderId2 = (String) getObject("orderId2");
			String E4XOrderId = (String) getObject("E4XOrderId");
			String orderXML = (String) getObject("orderXML");
			BBBOrderTools bbbOrderTools = (BBBOrderTools) getObject("bbbOrderTools");
			String orderType = (String)getObject("orderType");

			String countryCode = (String)getObject("countryCode");
			String currencyCode = (String)getObject("currencyCode");
			
			BBBOrder order = (BBBOrder)bbbOrderTools.createOrder(orderType);
				System.out.println("Order Id :"+order.getId());
			String bbborder =order.getId();
				
			// Adding Order in BBB_INTL_ORDER table with random number 10 digit Order Id
			internationalOrderXmlRepoTools.addOrderXml(orderId, E4XOrderId, orderXML,bbborder,countryCode,currencyCode);
				System.out.println("Order added with Order Id : "+orderId);
				System.out.println("");
			
			// Adding Order in BBB_INTL_ORDER table with Order Id = "123456"
				internationalOrderXmlRepoTools.addOrderXml(orderId, E4XOrderId, orderXML,bbborder,countryCode,currencyCode);
				System.out.println("Order added with Order Id : "+orderId2);
				System.out.println("");
			
			//Deleting Order from BBB_INTL_ORDER table for Order Id = "123456"
			testRemoveOrderXml(orderId2);
				System.out.println("Order removed of Order Id : "+orderId2);
				System.out.println("");
			
			//Fetching Order XML from BBB_INTL_ORDER table for Order Id
			String orderXml = testGetOrderXml(orderId);
				System.out.println("Order XML : \""+orderXml +"\"for Order Id : "+orderId);
				System.out.println("");
				
			assertNotNull(internationalOrderXmlRepoTools.getInternationalOrderRepository());
	
	}
	
	
		
	/*************  TestRemoveOrderXml Is called in the testAddOrderXml methods *************/
		private void testRemoveOrderXml(String orderId) throws IOException, BBBSystemException, BBBBusinessException {
				
				InternationalOrderXmlRepoTools internationalOrderXmlRepoTools = null;	
				try
				{
					internationalOrderXmlRepoTools = (InternationalOrderXmlRepoTools) getObject("internationalOrderXmlRepoTools");
					internationalOrderXmlRepoTools.removeOrderXml(orderId);
				}
				catch (Exception e) 
				{
					String errorMessage = e.getMessage();
					System.out.println("TestRemoveOrderXml Exeption :"+ errorMessage);
				}
		}
		
		/*************  TestGetOrderXml Is called in the testAddOrderXml methods*************/
		private String testGetOrderXml(String orderId) throws IOException, BBBSystemException, BBBBusinessException {
			
			InternationalOrderXmlRepoTools internationalOrderXmlRepoTools = null;
			String orderXML=null;
			try
			{
				internationalOrderXmlRepoTools = (InternationalOrderXmlRepoTools) getObject("internationalOrderXmlRepoTools");
				orderXML =internationalOrderXmlRepoTools.getOrderXml(orderId);
			}
			catch (Exception e) 
			{
				String errorMessage = e.getMessage();
				System.out.println("testGetOrderXml Exeption :"+ errorMessage);
			}
			return orderXML;
	}
		
}

