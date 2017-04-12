package com.bbb.internationalshipping.manager;

import com.bbb.exception.BBBBusinessException;
import com.bbb.internationalshipping.vo.orderconfirmation.BBBInternationalShippingOrderConfResponse;
import com.sapient.common.tests.BaseTestCase;


public class TestInternationalOrderConfirmationManager extends BaseTestCase {

	
	
	/**
	 * Test Order Confirmation API.
	 */
	public void testOrderConfirmation() {
		
			InternationalOrderConfirmationManager confirmationManager = (InternationalOrderConfirmationManager) getObject("internationalOrderConfirmationManager");
			
			String merchantOrderId = (String)getObject("merchantOrderId");
			String orderId = (String)getObject("orderId");
			
			BBBInternationalShippingOrderConfResponse orderConfResponse;
			orderConfResponse = confirmationManager.orderConfirmation(merchantOrderId, orderId);
			
			if(orderConfResponse.isWebServiceError()) {
				System.out.println("Order Confirmation Already done");
				assertTrue("Order Confirmation Already done", orderConfResponse.isWebServiceError());
			} else {
				System.out.println("Flag for Order Confirmation API " + orderConfResponse.isConfirmed());
				assertTrue("Order Confirmation Flag is true", orderConfResponse.isConfirmed());
			}
	}		
}

