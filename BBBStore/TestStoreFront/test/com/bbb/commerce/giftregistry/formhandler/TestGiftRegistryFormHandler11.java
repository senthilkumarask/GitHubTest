package com.bbb.commerce.giftregistry.formhandler;

import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler11 extends BaseTestCase {
	public void testHandleRegistrySearch11() throws Exception {
	GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

	 giftRegistryFormHandler.resetFormExceptions();
	 giftRegistryFormHandler.handleClearSessionBeanData(getRequest(), getResponse());
	 
	 if(giftRegistryFormHandler.getRegistryTypes() ==null){
		 giftRegistryFormHandler.setRegistryTypes("");
	 }
	 if(giftRegistryFormHandler.getRegistryEventType() ==null){
		 giftRegistryFormHandler.setRegistryEventType("BRD");
	 }
	 	 
	 giftRegistryFormHandler.handleRegistryTypes (getRequest(), getResponse());
	 boolean error11 = giftRegistryFormHandler.getFormError();
	 addObjectToAssert("testValue11", error11);
	  }
}
