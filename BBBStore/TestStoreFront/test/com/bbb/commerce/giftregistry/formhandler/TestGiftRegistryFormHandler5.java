package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler5 extends BaseTestCase {
	public void testHandleRegistrySearch5() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 giftRegistryFormHandler.setHidden(2);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		 
			 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
			  boolean error5 = giftRegistryFormHandler.getFormError();
			  addObjectToAssert("testValue5", error5);
		  }
}
