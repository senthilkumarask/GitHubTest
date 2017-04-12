package com.bbb.personalstore.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.droplet.BBBChangeAnchorTagUrlDroplet;
import com.bbb.framework.BBBSiteContext;
import com.bbb.personalstore.vo.PersonalStoreResponseVO;
import com.bbb.utils.BBBUtility;
import com.sapient.common.tests.BaseTestCase;

/**
 * This is a Sapunit to test personal Store Droplet, which contains methods to
 * test the output for logged-in and guest user
 * 
 * @author rjain40
 * 
 */

public class TestPersonalStoreDroplet extends BaseTestCase {

	// Test The personal Store Droplet for guest User
	public void testPersonalStoreDroplet_GuestUser() throws ServletException, IOException {

		PersonalStoreDroplet personalStoreDroplet = (PersonalStoreDroplet) getObject("TestPersonalStoreDroplet");
		String siteId = (String) getObject("siteId");

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();
		
		final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		if (!bbbProfileFormHandler.getProfile().isTransient()) {
			bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
		}
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		Profile profile = bbbProfileFormHandler.getProfile();
		req.setParameter("isError",null);
		getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("userProfile", profile);
		atg.servlet.ServletUtil.setCurrentRequest(req);
		personalStoreDroplet.service(req, res);
		PersonalStoreResponseVO personalStoreResponseVO = (PersonalStoreResponseVO) req.getObjectParameter("personalStoreDetails");
		Boolean isError = (Boolean) req.getObjectParameter("isError");
		//Check if any exception exists
		assertNull(isError);
		
		//Check if VO is not null
		assertNotNull(personalStoreResponseVO);
		getRequest().setParameter("isError", null);
		getRequest().setParameter("personalStoreDetails", null);

	}

	// Test The personal Store Droplet for Logged-In User
	public void testPersonalStoreDroplet_LoggedInUser() throws ServletException, IOException {
		PersonalStoreDroplet personalStoreDroplet = (PersonalStoreDroplet) getObject("TestPersonalStoreDroplet");
		String siteId = (String) getObject("siteId");

		final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		if (!bbbProfileFormHandler.getProfile().isTransient()) {
			bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
		}
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		
		//Login
		final String email = (String) this.getObject("email");
		final String password = (String) this.getObject("password");
		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());

		Profile profile = bbbProfileFormHandler.getProfile();
		getRequest().setParameter("isError", null);
		getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("userProfile", profile);
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		personalStoreDroplet.service(getRequest(), getResponse());
		PersonalStoreResponseVO personalStoreResponseVO = (PersonalStoreResponseVO) getRequest().getObjectParameter("personalStoreDetails");
		Boolean isError = (Boolean) getRequest().getObjectParameter("isError");
		//Check if any exception exists
		assertNull(isError);
		
		//Check if VO is not null
		assertNotNull(personalStoreResponseVO);

		getRequest().setParameter("isError", null);
		getRequest().setParameter("personalStoreDetails", null);
	}

}
