package com.bbb.commerce.checkout.tibco;


import java.util.HashMap;

import atg.commerce.order.OrderManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBSubmitOrderHandler extends BaseTestCase{
	

	public void testProcessSubmitOrder() throws Exception {
	
		DynamoHttpServletRequest pRequest = getRequest();
		BBBSubmitOrderHandler submitOrderHandler = (BBBSubmitOrderHandler) getObject("submitOrderHandler");
		
		PricingRequestDocument wsRequest;
	    BBBOrderImpl order = null;
	        
	    String siteId = (String)getObject("siteId");
	    wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
	        
	    Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
	    order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
	    order.setSiteId(siteId);
	        
		submitOrderHandler.processSubmitOrder(order, pRequest);
		assertTrue(true);
	}
	
	
	/*public void testSubmitInventoryMesssage()throws Exception {
		
		BBBSubmitOrderHandler submitOrderHandler = (BBBSubmitOrderHandler) getObject("submitOrderHandler");
		
		PricingRequestDocument wsRequest;
	    BBBOrderImpl order = null;
	        
	    String siteId = (String)getObject("siteId");
	    wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
	        
	    Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
	    order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
	    order.setSiteId(siteId);
	    
	    submitOrderHandler.submitInventoryMesssage(order);
	    assertTrue(true);
	}*/
	
	
	public void testSendMail()throws Exception {
	
	DynamoHttpServletRequest pRequest = getRequest();	
    ServletUtil.setCurrentRequest(pRequest);
    BBBSubmitOrderHandler submitOrderHandler = (BBBSubmitOrderHandler) getObject("submitOrderHandler");
	OrderManager orderMan = (OrderManager) getObject("ordermanager");
	PricingRequestDocument wsRequest;
    BBBOrderImpl order = null;
    String siteId = (String)getObject("siteId");
    wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
    Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
    order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
    order.setSiteId(siteId);
    BBBRepositoryContactInfo address = ((BBBOrderTools) orderMan.getOrderTools()).createBillingAddress();
    address.setEmail("test1@sapient.com");
    address.setPhoneNumber("1234567890");
    order.setBillingAddress(address);
    
    submitOrderHandler.sendMail(order, pRequest);
    assertTrue(true);
}

}
