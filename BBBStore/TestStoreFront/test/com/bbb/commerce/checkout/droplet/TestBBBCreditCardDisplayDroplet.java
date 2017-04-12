package com.bbb.commerce.checkout.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

/**
 * To test TestBBBCreditCardDisplayDroplet service method.
 * 
 * @author vagra4
 */

public class TestBBBCreditCardDisplayDroplet extends BaseTestCase {

	/**
	 * To test the perOrder flow of shipping method Droplet - Single shipping
	 * 
	 * @throws Exception
	 */
	public void testBBBCreditCardDisplayDroplet() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		BBBCreditCardDisplayDroplet droplet = (BBBCreditCardDisplayDroplet)getObject("BBBCreditCardDisplayDroplet");
		
		pRequest.setParameter("creditCardNo", "4111111111111234");
		droplet.service(pRequest, pResponse);
		System.out.println("maskedCreditCardNo: " + pRequest.getParameter("maskedCreditCardNo"));
		
		assertNotNull("maskedCreditCardNo is null", pRequest.getParameter("maskedCreditCardNo"));
		
		pRequest.setParameter("creditCardNo", "411111111115678");
		droplet.service(pRequest, pResponse);
		System.out.println("maskedCreditCardNo: " + pRequest.getParameter("maskedCreditCardNo"));
		
		assertNotNull("maskedCreditCardNo is null", pRequest.getParameter("maskedCreditCardNo"));

	}

	

}