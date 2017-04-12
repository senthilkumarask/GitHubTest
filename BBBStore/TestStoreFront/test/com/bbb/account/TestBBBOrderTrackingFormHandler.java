package com.bbb.account;

import java.util.Map;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBOrderTrackingFormHandler extends BaseTestCase{
	
	public void testHandleTrackOrder() throws Exception {
		Map<String, String> errorMap = null;
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBOrderTrackingFormHandler bbbOrderTrackingFormHandler = (BBBOrderTrackingFormHandler) getObject("bbbOrderTrackingFormHandler");
		BBBOrderTrackingManager orderTrackingManager=(BBBOrderTrackingManager)Nucleus.getGlobalNucleus().resolveName(
	    		"/com/bbb/account/BBBOrderTrackingManager");
		
		
        
		String pEmailId = (String) getObject("emailId");
		String pOrderId = (String) getObject("orderId");
		String pSiteId = (String) getObject("siteId");
		
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		atg.servlet.ServletUtil.setCurrentResponse(this.getResponse());
		
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }
        
		bbbOrderTrackingFormHandler.setEmailId(pEmailId);
		bbbOrderTrackingFormHandler.setOrderId(pOrderId);
		bbbOrderTrackingFormHandler.setOrderTrackingManager(orderTrackingManager);
		
		assertFalse("should give error in form validation", bbbOrderTrackingFormHandler.getFormError());
		assertFalse("should give error in handler calling ", bbbOrderTrackingFormHandler.handleTrackOrder(pRequest, pResponse));
		
		
		errorMap = bbbOrderTrackingFormHandler.getErrorMap();
		assertEquals(0,errorMap.size());
		
		String legacyEmailId = (String) getObject("legacyEmailId");
		String legacyOrderId = (String) getObject("legacyOrderId");
		orderTrackingManager.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		
		bbbOrderTrackingFormHandler.setEmailId(legacyEmailId);
		bbbOrderTrackingFormHandler.setOrderId(legacyOrderId);
		bbbOrderTrackingFormHandler.setOrderTrackingManager(orderTrackingManager);
		
		assertFalse("should give error in form validation", bbbOrderTrackingFormHandler.getFormError());
		assertFalse("should give error in handler calling ", bbbOrderTrackingFormHandler.handleTrackOrder(pRequest, pResponse));
		
		errorMap = bbbOrderTrackingFormHandler.getErrorMap();
		assertEquals(0,errorMap.size());
		
		
		String invalidEmailId = (String) getObject("invalidEmailId");
		String invalidOrderId = (String) getObject("invalidOrderId");
		bbbOrderTrackingFormHandler.setEmailId(invalidEmailId);
		bbbOrderTrackingFormHandler.setOrderId(invalidOrderId);
		assertTrue("should give error in handler calling ", bbbOrderTrackingFormHandler.handleTrackOrder(pRequest, pResponse));
		errorMap = bbbOrderTrackingFormHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
		
		bbbOrderTrackingFormHandler.setEmailId(null);
		assertTrue("should give error in handler calling ", bbbOrderTrackingFormHandler.handleTrackOrder(pRequest, pResponse));
		errorMap = bbbOrderTrackingFormHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
		
		
		pEmailId = (String) getObject("invalidFormatEmail");
		bbbOrderTrackingFormHandler.setEmailId(pEmailId);
		assertTrue("should give error in handler calling ", bbbOrderTrackingFormHandler.handleTrackOrder(pRequest, pResponse));
		errorMap = bbbOrderTrackingFormHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
		
		bbbOrderTrackingFormHandler.setOrderId("");
		assertTrue("should give error in handler calling ", bbbOrderTrackingFormHandler.handleTrackOrder(pRequest, pResponse));
		errorMap = bbbOrderTrackingFormHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
		
		
		pOrderId = (String) getObject("wrongorderId");
		bbbOrderTrackingFormHandler.setOrderId(pOrderId);
		assertTrue("should give error in handler calling ", bbbOrderTrackingFormHandler.handleTrackOrder(pRequest, pResponse));
		errorMap = bbbOrderTrackingFormHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
		
		bbbOrderTrackingFormHandler.setTrackOrderErrorURL(null);
		bbbOrderTrackingFormHandler.getOrderTrackingManager();
		bbbOrderTrackingFormHandler.setErrorMap(null);
		bbbOrderTrackingFormHandler.setTrackOrderSuccessURL(null);
		
	}
	
}
