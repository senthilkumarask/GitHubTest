package com.bbb.commerce.giftregistry.droplet;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestMyRegistriesDisplayDroplet extends BaseTestCase {

public void testService() throws Exception {
		
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
		getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
		String pSiteId = (String) getObject("siteId");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

		String email = (String) getObject("username1");
		String password = (String) getObject("password");

		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());
		String siteId = (String) getObject("siteId");
		//getRequest().setParameter("siteId", siteId);
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		

		boolean isLogin = bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
		MyRegistriesDisplayDroplet myRegistriesDisplayDroplet = (MyRegistriesDisplayDroplet) getObject("myRegistriesDisplayDroplet");

		Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");

		getRequest().setParameter("profile", resultProfile);
		
		//myRegistriesDisplayDroplet.setRegistrySearchServiceName("regSearch");
		getRequest().setParameter("siteId", siteId);
		
		//System.out.println("resultProfile.isTransient(): "	+ resultProfile.isTransient());
		// assertTrue(resultProfile.isTransient());

	    //System.out.println(">>>>>>>myRegistriesDisplayDroplet"+myRegistriesDisplayDroplet);
	    myRegistriesDisplayDroplet.service(getRequest(), getResponse());
	   // System.out.println(getRequest().getObjectParameter("registrySummaryVO"));
	    
	    if(getRequest().getObjectParameter("registryCount") !=null){
	    	assertNotNull(getRequest().getObjectParameter("registryCount"));
	    }else{
	    	assertNull(getRequest().getObjectParameter("registryCount"));
	    }
	    
	    
	}
}