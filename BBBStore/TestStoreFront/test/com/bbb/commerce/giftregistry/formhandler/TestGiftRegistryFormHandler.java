package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler extends BaseTestCase {
	 /**
	  * testing handleCreateRegistry method
	  * @throws Exception
	  */
	 public void testHandleRegistrySearch1() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setFirstName("");
		 registrySearchVO.setLastName("");
		 giftRegistryFormHandler.setHidden(1);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
		 
		 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
		  boolean error1 = giftRegistryFormHandler.getFormError();
		  addObjectToAssert("testValue1", error1);
		  
	}
}
