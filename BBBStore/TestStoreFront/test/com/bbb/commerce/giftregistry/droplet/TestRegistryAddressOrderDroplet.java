package com.bbb.commerce.giftregistry.droplet;

import java.util.Map;

import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileManager;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestRegistryAddressOrderDroplet extends BaseTestCase {

	public void testService() throws Exception {
		RegistryAddressOrderDroplet registryAddressOrderDroplet = (RegistryAddressOrderDroplet) getObject ("getRegistryAddressOrderDroplet");

		//Login
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		assertTrue(bbbProfileFormHandler.getProfile().isTransient());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);
		

		BBBProfileManager manager = (BBBProfileManager)getObject("bbbProfileManager");
		String pSiteId = (String) getObject("siteId");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());
		String email = (String) getObject("email");
		String password = (String) getObject("password");
		manager.updatePassword("vagar50@test.com", "Sapient!2");
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		bbbProfileFormHandler.getValue().put("login", "vagar50@test.com");
		bbbProfileFormHandler.getValue().put("password", password);
		bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
		
				
		Profile profile = bbbProfileFormHandler.getProfile();
		
		if( profile !=null && !profile.isTransient() ){
			Map addresses = (Map)bbbProfileFormHandler.getProfile().getPropertyValue("secondaryAddresses");
			getRequest().setParameter("defaultId",null);
			getRequest().setParameter("sortByKeys", "true");
			getRequest().setParameter("map", addresses);
			
			registryAddressOrderDroplet.service(getRequest(), getResponse());
			
			
		    Object sortedArray = (Object)(getRequest().getObjectParameter("sortedArray"));
		    assertNotNull(sortedArray);
		    
		}
		
		bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
	}
}
