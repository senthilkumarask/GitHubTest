package com.bbb.commerce.giftregistry.formhandler;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler2 extends BaseTestCase {
	public void testHandleRegistrySearch2() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 
		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setFirstName("sunil");
		 registrySearchVO.setLastName("kumar");
		 
		 giftRegistryFormHandler.resetFormExceptions();
		 
		 if(giftRegistryFormHandler.getSortSeq() 	!=null){
			 registrySearchVO.setSortSeq("NAME");
		 }
		 
		 if(giftRegistryFormHandler.getHidden() !=1){
			 giftRegistryFormHandler.setHidden(1);
		 }
		 
		 if(giftRegistryFormHandler.getSearchRegistryServiceName() ==null){
			 giftRegistryFormHandler.setSearchRegistryServiceName("regSearch");
		 }
		 
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
		 
		 String pSiteId = (String) getObject("siteId");
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		 
		 giftRegistryFormHandler.setRegistrySearchSuccessURL("giftregistry/registry_search_guest.jsp");
		 giftRegistryFormHandler.setRegistrySearchErrorURL("giftregistry/reg_flyout.jsp");
		 
		 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
		 boolean error2 = giftRegistryFormHandler.getFormError();

		 addObjectToAssert("testValue2", error2);

	}
}
