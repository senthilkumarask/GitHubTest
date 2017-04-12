package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler6 extends BaseTestCase {
	public void testHandleRegistrySearch6() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setFirstName("Mickael123");
		 registrySearchVO.setLastName("John");
		 giftRegistryFormHandler.setHidden(2);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		 
			 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
			  boolean error6 = giftRegistryFormHandler.getFormError();
			  addObjectToAssert("testValue6", error6);
		  }
}
