package com.bbb.account;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBStoreSessionDroplet extends BaseTestCase
{
	
	public void testService() throws Exception {
	
	DynamoHttpServletRequest request = getRequest();
	DynamoHttpServletResponse response = getResponse();
	
	BBBStoreSessionDroplet bbbStoreSessionDroplet = (BBBStoreSessionDroplet) getObject("bbbStoreSessionDroplet");
		
	BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
	bbbProfileFormHandler.setExpireSessionOnLogout(false);
	getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
	BBBProfileManager manger = (BBBProfileManager) getObject("bbbProfileManager");
	
	String pSiteId = (String) getObject("siteId");
	bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

	String email = (String) getObject("email");
	String password = (String) getObject("password");
	
	manger.updatePassword(email, "Sapient!2");
	bbbProfileFormHandler.getFormExceptions().clear();
	if(!bbbProfileFormHandler.getProfile().isTransient()){
		 bbbProfileFormHandler.handleLogout(request, response);
	}
	getRequest().setRemoteAddr("123.123.123.123");
	assertTrue(bbbProfileFormHandler.getProfile().isTransient());
	bbbProfileFormHandler.setOrder(null);
	bbbProfileFormHandler.setBBBOrder(null);
	bbbProfileFormHandler.getValue().put("login", email);
	bbbProfileFormHandler.getValue().put("password", password);
	atg.servlet.ServletUtil.setCurrentRequest(getRequest());	
	
	bbbProfileFormHandler.getErrorMap().clear();
	bbbProfileFormHandler.getFormExceptions().clear();
	bbbProfileFormHandler.setFormErrorVal(false);
	getRequest().setParameter("userCheckingOut","asdf");
	System.out.println("before TestBBBStoreSessionDroplet.bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
	bbbProfileFormHandler.handleLoginUser(request, response);
	System.out.println("after TestBBBStoreSessionDroplet.bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
	
    assertFalse(bbbProfileFormHandler.getProfile().isTransient());	
    request.setParameter(BBBCoreConstants.CURR_SITE, pSiteId);
    bbbStoreSessionDroplet.setProfile(bbbProfileFormHandler.getProfile());
	bbbStoreSessionDroplet.service(request,response);
	assertNotNull(getRequest().getParameter(BBBCoreConstants.URL));
	
	bbbProfileFormHandler.handleLogout(request, response);
	assertTrue(bbbProfileFormHandler.getProfile().isTransient());
	System.out.println("after logout TestBBBStoreSessionDroplet.bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
	bbbStoreSessionDroplet.getProfile();	
	

	
	
	
	
  }
}
