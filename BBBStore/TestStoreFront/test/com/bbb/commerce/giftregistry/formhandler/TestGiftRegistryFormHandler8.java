package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler8 extends BaseTestCase {
	public void testHandleRegistrySearch8() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setFirstName("Mickael");
		 registrySearchVO.setLastName("John");
		 registrySearchVO.setEmail("sachin@example.com");
		 giftRegistryFormHandler.setHidden(2);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		 
			 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
			  boolean error8 = giftRegistryFormHandler.getFormError();
			  addObjectToAssert("testValue8", error8);
		  }
}
