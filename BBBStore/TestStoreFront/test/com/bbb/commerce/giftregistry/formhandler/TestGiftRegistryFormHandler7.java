package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler7 extends BaseTestCase {
	public void testHandleRegistrySearch7() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setFirstName("Mickaelllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll");
		 registrySearchVO.setLastName("John");
		 giftRegistryFormHandler.setHidden(1);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);

		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
			 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
			  boolean error7 = giftRegistryFormHandler.getFormError();
			  addObjectToAssert("testValue7", error7);
		  }
}
