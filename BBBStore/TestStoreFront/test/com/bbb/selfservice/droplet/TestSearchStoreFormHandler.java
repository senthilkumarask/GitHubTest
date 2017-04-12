package com.bbb.selfservice.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.common.StoreLocator;
import com.bbb.selfservice.formhandler.SearchStoreFormHandler;
import com.sapient.common.tests.BaseTestCase;

public class TestSearchStoreFormHandler extends BaseTestCase {

	public void testSearchStoreFormHandler() throws BBBBusinessException,
			BBBSystemException, ServletException, IOException {
	
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		SearchStoreFormHandler searchStoreFormHandler = (SearchStoreFormHandler)getObject("searchStoreFormHandler"); 
		searchStoreFormHandler.setSiteContext("BuyBuyBaby");
		StoreLocator storeLoc = new StoreLocator();
		storeLoc.setSearchType((String) getObject("searchType"));
		storeLoc.setPostalCode((String) getObject("postal"));
		searchStoreFormHandler.setStoreLocator(storeLoc);
		assertFalse("should give error in form validation", searchStoreFormHandler.getFormError());
		assertTrue("should give error in mapquest service calling", searchStoreFormHandler.handleSearchStore(pRequest, pResponse));
		
		
		storeLoc.setAddress((String) getObject("address"));
		storeLoc.setCity((String) getObject("city"));
		storeLoc.setPagenumber(1);
		storeLoc.setRadius(5);
		storeLoc.setState((String) getObject("state"));
		storeLoc.setPostalCode(null);
		searchStoreFormHandler.setStoreLocator(storeLoc);
		assertFalse("should give error in form validation", searchStoreFormHandler.getFormError());
		assertTrue("should give error in mapquest service calling", searchStoreFormHandler.handleSearchStore(pRequest, pResponse));
		
		searchStoreFormHandler.getFormExceptions().clear();
		
		storeLoc.setPostalCode("@#$#$#$");
		searchStoreFormHandler.setStoreLocator(storeLoc);
		assertTrue("should give error in mapquest service calling", searchStoreFormHandler.handleSearchStore(pRequest, pResponse));
		assertTrue("should give error in form validation", searchStoreFormHandler.getFormError());
		searchStoreFormHandler.getFormExceptions().clear();
		
		storeLoc.setState("@#$#$#$");
		searchStoreFormHandler.setStoreLocator(storeLoc);
		assertTrue("should give error in mapquest service calling", searchStoreFormHandler.handleSearchStore(pRequest, pResponse));
		assertTrue("should give error in form validation", searchStoreFormHandler.getFormError());
		searchStoreFormHandler.getFormExceptions().clear();
		
		searchStoreFormHandler.setStoreLocator(null);
		assertTrue("should give error in mapquest service calling", searchStoreFormHandler.handleSearchStore(pRequest, pResponse));
		assertTrue("should give error in form validation", searchStoreFormHandler.getFormError());
		searchStoreFormHandler.getFormExceptions().clear();
		
		
		getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
		String email = (String) getObject("email");
		String password = (String) getObject("password");
		assertTrue(bbbProfileFormHandler.getProfile().isTransient());
		
		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		pRequest.setQueryString("favouriteStoreId="+(String) getObject("favStore"));
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());
		
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		
		System.out.println("before TestSearchStoreFormHandler.bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
		
		pRequest.setParameter("userCheckingOut","true");
		boolean isLogin = bbbProfileFormHandler.handleLogin(pRequest, pResponse);
		
		System.out.println("after TestSearchStoreFormHandler.bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
		assertFalse(bbbProfileFormHandler.getProfile().isTransient());
		pRequest.setRequestURI("/");
	    assertFalse("should give error in mapquest service calling", searchStoreFormHandler.handleModifyFavStore(pRequest, pResponse));
	    
	    if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
	    assertTrue(bbbProfileFormHandler.getProfile().isTransient());
	    System.out.println("after logout TestBBBStoreSessionDroplet.bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());

		/*
		 * assertTrue("ERRORS in Logging out ", bbbProfileFormHandler
		 * .getFormExceptions().size() == 0);
		 */
	}

}
