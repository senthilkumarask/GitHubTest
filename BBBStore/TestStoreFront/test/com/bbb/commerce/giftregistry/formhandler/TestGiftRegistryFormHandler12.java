package com.bbb.commerce.giftregistry.formhandler;

import atg.userprofiling.Profile;
import atg.commerce.order.OrderImpl;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler12 extends BaseTestCase {

	public void testHandleRegistrySearch12() throws Exception {
		GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
			
			BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		 	bbbProfileFormHandler.setExpireSessionOnLogout(false);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			((OrderImpl)bbbProfileFormHandler.getOrder()).invalidateOrder();
			
			// first logout the existing user
			if(!bbbProfileFormHandler.getProfile().isTransient()){
				bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			}
			 

			 Profile profile = (Profile) getRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
			 profile.setPropertyValue("id", "DC13750002");
			 giftRegistryFormHandler.setProfile(profile);
			 
			 // login the user
			BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
			getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
			String pSiteId = (String) getObject("siteId");
			bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

			String email = (String) getObject("username1");
			String password = (String) getObject("password");

			bbbProfileFormHandler.getValue().put("login", email);
			bbbProfileFormHandler.getValue().put("password", password);
			atg.servlet.ServletUtil.setCurrentRequest(getRequest());
			

			boolean isLogin = bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
		 

		 giftRegistryFormHandler.resetFormExceptions();
		 giftRegistryFormHandler.setProductId("1017198602");
		 giftRegistryFormHandler.setQuantity("1");
		 giftRegistryFormHandler.setRegistryId("166635887");
		 giftRegistryFormHandler.setSkuIds("17198602");			 
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		 giftRegistryFormHandler.handleAddItemToGiftRegistryFromCetona (getRequest(), getResponse());
		 boolean error12 = giftRegistryFormHandler.getFormError();
		 addObjectToAssert("testValue12", error12);
		 //logout the user
		 bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		  }
}
