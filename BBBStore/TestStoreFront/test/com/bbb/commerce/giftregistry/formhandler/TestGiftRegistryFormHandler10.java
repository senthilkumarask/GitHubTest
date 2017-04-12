package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler10 extends BaseTestCase {
	public void testHandleRegistrySearch10() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setRegistryId("!@#$%");
		 giftRegistryFormHandler.setHidden(2);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));				
			 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
			  boolean error10 = giftRegistryFormHandler.getFormError();
			  addObjectToAssert("testValue10", error10);
		  }
}
