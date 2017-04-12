package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler3 extends BaseTestCase {
	public void testHandleRegistrySearch3() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setFirstName("Mickael");
		 registrySearchVO.setLastName("John");
		 giftRegistryFormHandler.setHidden(1);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
				
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		 
		 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
		 
		 boolean formError = giftRegistryFormHandler.getFormError();

		 addObjectToAssert("testValue3", formError);
			//  boolean error3 = giftRegistryFormHandler.getFormError();
	}
}
