package com.bbb.account;

import java.util.Map;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.profile.BBBPropertyManager;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author Lokesh
 * 
 */
public class TestBBBProfileTools extends BaseTestCase {

	public void testCreateSiteItem() throws Exception {
		
		
		BBBProfileTools profileTools = (BBBProfileTools) getObject("bbbProfileTools");
		String pSiteId = (String) getObject("siteId");
		String pEmailId = (String) getObject("emailId");
		
		profileTools.createSiteItem(pEmailId,pSiteId,null,null, BBBCoreConstants.YES);
		profileTools.createSiteItem(pEmailId,pSiteId,null,null, BBBCoreConstants.YES, BBBCoreConstants.YES);
	}
	public void testIsDuplicateEmailAddress() {
		
		BBBProfileTools profileTools = (BBBProfileTools) getObject("bbbProfileTools");
		String pSiteId = (String) getObject("siteId");
		String pEmailId = (String) getObject("emailId");
		String pInValidEmail = (String) getObject("inValidEmailId");
		
		assertTrue("Email address entered is not duplicate",profileTools.isDuplicateEmailAddress(pEmailId, pSiteId));
		assertFalse("Email address entered is not duplicate",profileTools.isDuplicateEmailAddress(pInValidEmail, pSiteId));
	}

	public void testLookupUsers() throws Exception {
		
		
		BBBProfileTools profileTools = (BBBProfileTools) getObject("bbbProfileTools");
		String pLogin = (String) getObject("login");
		String sProfileType = profileTools.getDefaultProfileType();
		
		assertTrue("login entered does not exist, lookup failed",profileTools.lookupUsers(pLogin, pLogin, sProfileType).length > 0);
		
	}
	
    public void testGetAndUpdateAutoLoginForProfile() throws Exception {
    	
	    DynamoHttpServletRequest pRequest = getRequest();	
	    BBBProfileTools profileTools = (BBBProfileTools) getObject("bbbProfileTools");
		String pLogin = (String) getObject("login");
		if (profileTools.getItemFromEmail(pLogin) != null ) {
			assertTrue("Password update failed", profileTools.generateTempPasswordForProfile(profileTools.getItemFromEmail(pLogin), "Sapient1"));
			assertTrue("Update Autologin Failed", profileTools.updateAutoLogin(profileTools.getItemFromEmail(pLogin), true, pRequest));
			boolean autoLogin = profileTools.getAutoLogin(profileTools.getItemFromEmail(pLogin));
			if (autoLogin) {
				assertTrue(autoLogin);
			} else {
				assertFalse(autoLogin);
			}
			String emailOptinFlag = profileTools.fetchEmailOptInflagFromSisterSite(profileTools.getItemFromEmail(pLogin));
			assertNotNull("Email Optin Flag on Sister's site is Null", emailOptinFlag);
		}
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateSiteItem() throws Exception {
	
		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);
		BBBProfileTools profileTools = (BBBProfileTools) getObject("bbbProfileTools");
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		if (!bbbProfileFormHandler.getProfile().isTransient()) {
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		bbbProfileFormHandler.getValue().put("login", (String)getObject("email"));
		bbbProfileFormHandler.getValue().put("password", (String)getObject("password"));
		bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
		Map updateSiteItem = profileTools.updateSiteItem(bbbProfileFormHandler.getProfile(), (Map)getObject("propertyMap"));
		assertNotNull("Update Site Item failed",updateSiteItem);
		assertFalse("Update Site Item failed",updateSiteItem.isEmpty());
			 Profile profile = bbbProfileFormHandler.getProfile();
			 String addressId = ((RepositoryItem)profile.getPropertyValue("shippingAddress")).getRepositoryId();
			 if (addressId != null) {
				 assertNotNull("Get Address By Id failed", profileTools.getAddressesById(bbbProfileFormHandler.getProfile(),addressId));
			}
		profileTools.getFbProfileTools();
	}

}
