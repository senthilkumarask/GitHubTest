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
package com.bbb.internationalshipping.order;

import java.io.IOException;

import atg.commerce.CommerceException;
import atg.repository.RepositoryException;

import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;


public class TestBBBInternationalOrder extends BaseTestCase {

	
	/*************  testAddInternationalOrder  
	 * @throws RepositoryException 
	 * @throws CommerceException 
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws CommerceException
	 */
	public void testAddInternationalOrder() throws IOException, BBBSystemException, BBBBusinessException, RepositoryException, CommerceException {
		
			BBBOrderTools bbbOrderTools = (BBBOrderTools) getObject("bbbOrderTools");
			String orderType = (String)getObject("orderType");
			BBBOrder order = (BBBOrder)bbbOrderTools.createOrder(orderType);
				System.out.println("Order Id :"+order.getId());
								
			String internationalOrderId = (String) getObject("internationalOrderId");
			String internationalState = (String) getObject("internationalState");
			String currencyCode = (String) getObject("currencyCode");
			String countryCode = (String) getObject("countryCode");
			
			boolean success = bbbOrderTools.addInternationalOrder(order, internationalOrderId, internationalState, currencyCode, countryCode);
			
				System.out.println("Successfully added : " +success);
	
			assertNotNull(success);
	}
	
	
	
}

