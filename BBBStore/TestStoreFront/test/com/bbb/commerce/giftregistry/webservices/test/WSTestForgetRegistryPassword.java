package com.bbb.commerce.giftregistry.webservices.test;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class WSTestForgetRegistryPassword extends BaseTestCase{
	
	public void testForgetRegistryPasswordWSService() throws Exception {
		 GiftRegistryFormHandler forgetRegistryPasswordFormHandler = (GiftRegistryFormHandler) getObject("forgetRegistryPasswordFormHandler");
		 String username = (String) getObject("username");
		 Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 resultProfile.setDataSource(profileTool.getItemFromEmail(username));
		 forgetRegistryPasswordFormHandler.setForgetPasswordRegistryId((String)getObject("registryId"));
		// forgetRegistryPasswordFormHandler.setForgetPasswordEmailId(username);
		 forgetRegistryPasswordFormHandler.setForgetRegistryPasswordServiceName((String) getObject("servicename"));
		 //getRequest().setParameter("siteId",((String)getObject("siteId")));
		 forgetRegistryPasswordFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(((String)getObject("siteId"))));
		 boolean isNoError=forgetRegistryPasswordFormHandler.handleforgetRegistryPassword( getRequest(),  getResponse());
		assertNotNull(isNoError);
	       
	}
	
}
