package com.bbb.commerce.order.formhandler;

import java.util.HashMap;
import java.util.Locale;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;





import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBShippingGroupFormhandler extends BaseTestCase {

	public void testHandleChangeToStorePickup()  throws Exception{
		
		DynamoHttpServletRequest pRequest =  getRequest();
        DynamoHttpServletResponse pResponse = getResponse();
        BBBShippingGroupFormhandler bbbShippingGroupFormhandler = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormhandler");  
        RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
        ServletUtil.setCurrentRequest(getRequest());
        PricingRequestDocument wsRequest;
	    BBBOrderImpl order = null;
	        
	    String siteId = (String)getObject("siteId");
	    wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
	        
	    Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
	    order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
	    order.setSiteId(siteId);
	    bbbShippingGroupFormhandler.setOrder(order);
        boolean handleReturn = bbbShippingGroupFormhandler.handleChangeToStorePickup(pRequest, pResponse);
        assertTrue(handleReturn);
        
	}
	
	
	public void testHandleChangeToShipOnline() throws Exception{
		
		DynamoHttpServletRequest pRequest =  getRequest();
        DynamoHttpServletResponse pResponse = getResponse();
        BBBShippingGroupFormhandler bbbShippingGroupFormhandler = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormhandler");  
        RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
        ServletUtil.setCurrentRequest(getRequest());
        PricingRequestDocument wsRequest;
	    BBBOrderImpl order = null;
	        
	    String siteId = (String)getObject("siteId");
	    wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
	        
	    Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
	    order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
	    order.setSiteId(siteId);
	    bbbShippingGroupFormhandler.setOrder(order);
	    boolean handleReturn = bbbShippingGroupFormhandler.handleChangeToShipOnline(pRequest, pResponse);
        assertTrue(handleReturn);
	}
	
	public void testHandleGiftMessaging() throws Exception{
		
		DynamoHttpServletRequest pRequest =  getRequest();
        DynamoHttpServletResponse pResponse = getResponse();
        BBBShippingGroupFormhandler bbbShippingGroupFormhandler = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormhandler");  
        RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
        ServletUtil.setCurrentRequest(getRequest());
        PricingRequestDocument wsRequest;
	    BBBOrderImpl order = null;
	        
	    String siteId = (String)getObject("siteId");
	    wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
	        
	    Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
	    order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
	    order.setSiteId(siteId);
	    bbbShippingGroupFormhandler.setOrder(order);
	    String failureUrl = bbbShippingGroupFormhandler. getCheckoutProgressStates().getFailureURL();
	    assertNotNull(failureUrl);
	    if(failureUrl == null){
	    boolean handleReturn = bbbShippingGroupFormhandler.handleGiftMessaging(pRequest, pResponse);
        assertTrue(handleReturn);
        }
	}
	
}
