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
package com.bbb.internationalshipping.integration.poFileProcessing;

import atg.commerce.CommerceException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.jaxb.internationalshipping.pofile.OrderFeed;
import com.bbb.internationalshipping.fulfillment.poservice.IntlPOFileUnMarshaller;
import com.bbb.internationalshipping.vo.pofileprocessing.BBBInternationalOrderPOFileVO;
import com.sapient.common.tests.BaseTestCase;


public class TestIntlPOFileUnMarshaller extends BaseTestCase {

	
	
	public void testUnmarshalPOFile() throws BBBSystemException, BBBBusinessException, CommerceException
	{
		IntlPOFileUnMarshaller orderPOFileUnMarshaller= (IntlPOFileUnMarshaller) getObject("orderPOFileUnMarshaller");
		String xmlString = (String) getObject("xmlString");
		System.setProperty("weblogic.Name", "bbb_prod");
		OrderFeed orderPOFileVO = orderPOFileUnMarshaller.unmarshalPOFile(xmlString,null,"BuyBuyBaby");
		
			System.out.println("Address Type  : "+orderPOFileVO.getOrder().get(0).getMarketing().getAddress().get(0).getType());
			System.out.println("Address Type  : "+orderPOFileVO.getOrder().get(0).getMarketing().getAddress().get(1).getType());
			System.out.println("Credit Card Type  : "+orderPOFileVO.getOrder().get(0).getCreditCard().getType());
			System.out.println("Order Id  : "+orderPOFileVO.getOrder().get(0).getOrderId());
			System.out.println("Exchange Order Id  : "+orderPOFileVO.getOrder().get(0).getOrderId().getE4XOrderId());
	 	
		if(orderPOFileVO != null)
	 	{
			assertNotNull(orderPOFileVO.getOrder().get(0).getOrderId());
			assertNotNull(orderPOFileVO.getOrder().get(0).getOrderId().getE4XOrderId());
	 		assertNotNull(orderPOFileVO.getOrder().get(0).getMarketing().getAddress().get(0).getType());
	 		assertNotNull(orderPOFileVO.getOrder().get(0).getMarketing().getAddress().get(1).getType());
	 		assertNotNull(orderPOFileVO.getOrder().get(0).getCreditCard().getType());
	 	}
	}
	
	
}

