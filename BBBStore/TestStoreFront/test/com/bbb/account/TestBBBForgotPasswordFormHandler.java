package com.bbb.account;

import java.util.Map;


import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.BBBPropertyManager;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBForgotPasswordFormHandler extends BaseTestCase{
	
	public void testForgotPassword() throws Exception {
		Map<String, String> errorMap = null;
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBForgotPasswordHandler bbbForgotPasswordHandler = (BBBForgotPasswordHandler) getObject("bbbForgotPasswordHandler");
		BBBPropertyManager pmgr = (BBBPropertyManager)getObject("bbbPropertyManager");
		bbbForgotPasswordHandler.setPmgr(pmgr);
		String pSiteId = (String) getObject("siteId");
		bbbForgotPasswordHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		String invalidemailId = (String) getObject("invalidemailId");

		bbbForgotPasswordHandler.getValue().put("email", invalidemailId);
		bbbForgotPasswordHandler.getErrorMap().clear();
		bbbForgotPasswordHandler.getFormExceptions().clear();
		assertTrue("should give error in handler calling ", bbbForgotPasswordHandler.handleForgotPassword(pRequest, pResponse));
		errorMap = bbbForgotPasswordHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
		
		errorMap = null;
		bbbForgotPasswordHandler.getFormExceptions().clear();
		bbbForgotPasswordHandler.getValue().remove("email");
		String pEmailId = (String) getObject("emailId");
		
		bbbForgotPasswordHandler.getValue().put("email", pEmailId);
		
		assertFalse("should give error in form validation", bbbForgotPasswordHandler.getFormError());
		assertTrue("should give error in handler calling ", bbbForgotPasswordHandler.handleForgotPassword(pRequest, pResponse));
		BBBProfileManager manger = (BBBProfileManager) getObject("bbbProfileManager");
		manger.updatePassword(pEmailId, "Sapient!2");
		
		
	}
	
	public void testForgotPasswordError() throws Exception {
		Map<String, String> errorMap = null;
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBForgotPasswordHandler bbbForgotPasswordHandler = (BBBForgotPasswordHandler) getObject("bbbForgotPasswordHandler");
		
		String pEmailId = (String) getObject("emailId");
		String pSiteId = (String) getObject("siteId");
		bbbForgotPasswordHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		bbbForgotPasswordHandler.getErrorMap().clear();
		bbbForgotPasswordHandler.getFormExceptions().clear();
		bbbForgotPasswordHandler.getValue().remove("email");
		bbbForgotPasswordHandler.getValue().put("email", pEmailId);
		assertTrue("should give error in handler calling ", bbbForgotPasswordHandler.handleForgotPassword(pRequest, pResponse));
		errorMap = bbbForgotPasswordHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
	}
	
	public void testinvalidEmail() throws Exception {
		Map<String, String> errorMap = null;
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBForgotPasswordHandler bbbForgotPasswordHandler = (BBBForgotPasswordHandler) getObject("bbbForgotPasswordHandler");
		String pEmailId = (String) getObject("emailId");
		String pSiteId = (String) getObject("siteId");
		bbbForgotPasswordHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		bbbForgotPasswordHandler.getErrorMap().clear();
		bbbForgotPasswordHandler.getFormExceptions().clear();
		bbbForgotPasswordHandler.getValue().remove("email");
		bbbForgotPasswordHandler.getValue().put("email", pEmailId);
		assertTrue("should give error in handler calling ", bbbForgotPasswordHandler.handleForgotPassword(pRequest, pResponse));
		errorMap = bbbForgotPasswordHandler.getErrorMap();
		assertTrue(errorMap.size() > 0);
		bbbForgotPasswordHandler.getEmailAddr();
		bbbForgotPasswordHandler.setEmailAddr(null);
		bbbForgotPasswordHandler.setErrorMap(null);
		bbbForgotPasswordHandler.getPmgr();
		
		bbbForgotPasswordHandler.getManager();
		bbbForgotPasswordHandler.setLoggingDebug(true);
	}

}
