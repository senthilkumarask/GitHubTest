package com.bbb.account;

import atg.userprofiling.ProfileTools;

import com.sapient.common.tests.BaseTestCase;

/**
 * @author Lokesh
 * 
 */
public class TestBBBSharedProfile extends BaseTestCase {

	public void testIsUserMulitpleGroups() throws Exception {
		String pSiteId = (String) getObject("siteId");
		String pSiteCA = (String) getObject("siteCA");
		String pEmailId = (String) getObject("emailId");		
		BBBProfileManager pm=(BBBProfileManager) getRequest().resolveName("/com/bbb/account/BBBProfileManager");
		ProfileTools profileTools=(ProfileTools)getRequest().resolveName("/atg/userprofiling/ProfileTools");
		profileTools.getItemFromEmail(pEmailId);
		assertTrue("The site belongs to the group",pm.isUserPresentToOtherGroup(profileTools.getItemFromEmail(pEmailId),pSiteCA));
		assertFalse("The site belongs to the group",pm.isUserPresentToOtherGroup(profileTools.getItemFromEmail(pEmailId),pSiteId));
	}
//	public void testaddSiteToProfile() throws Exception {
////		String pSiteId = (String) getObject("siteId");
////		String pEmailId = (String) getObject("emailId");		
////		String pSiteCA = (String) getObject("siteCA");
////		
////		BBBProfileManager pm=(BBBProfileManager) getRequest().resolveName("/com/bbb/account/BBBProfileManager");
////		ProfileTools profileTools=(ProfileTools)getRequest().resolveName("/atg/userprofiling/ProfileTools");
////		profileTools.getItemFromEmail(pEmailId);
////		assertTrue("The site belongs to the group",pm.addSiteToProfile(, );
//		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler)
//		getRequest().resolveName("/atg/userprofiling/ProfileFormHandler");
//		
//		bbbProfileFormHandler.getValue().put("email", "test124@123.com");
//		bbbProfileFormHandler.getValue().put("password", "S@pient7");
//		bbbProfileFormHandler.getValue().put("confirmPassword", "S@pient7");
//		bbbProfileFormHandler.getValue().put("firstName", "hooha");
//		bbbProfileFormHandler.getValue().put("lastName", "singh");
//		
//		
//		bbbProfileFormHandler.handleCreate(getRequest(), getResponse());
//		 
//		System.out.println("exceptions"+bbbProfileFormHandler.getFormExceptions());
//	}
		 
}
