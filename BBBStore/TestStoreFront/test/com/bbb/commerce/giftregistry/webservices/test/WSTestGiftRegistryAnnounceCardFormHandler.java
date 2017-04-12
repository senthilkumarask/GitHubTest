package com.bbb.commerce.giftregistry.webservices.test;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * This class provides test case for handling methods for requestign
 * announcement cards  for a user's gift registry
 * 
 * This functionality is invoked via the handleAnnouncementCardCount
 * methods of the form handler.
 * 
 * @author ikhan2
 * 
 */
public class WSTestGiftRegistryAnnounceCardFormHandler extends BaseTestCase {

	/**
	 * testing preCreateRegistry method
	 * @throws Exception
	 */
	 	 public void testAnnounceCard() throws Exception {
		 
	 		GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
	 		
	 		giftRegistryFormHandler.getFormExceptions().clear();
	 			 		
	 		RegistryVO  registryVO= giftRegistryFormHandler.getRegistryVO();
	 		registryVO.setRegistryId("153501902");
	 		String pSiteId = (String) getObject("siteId");
	 		giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
	 		registryVO.getRegistryType().setRegistryTypeName("baby");
	 		registryVO.getPrimaryRegistrant().setFirstName("Class");
	 		registryVO.getCoRegistrant().setFirstName("Sarah");
	 		registryVO.setNumRegAnnouncementCards(25);
						
	 		GiftRegSessionBean sessionBean= giftRegistryFormHandler.getGiftRegSessionBean();
	 		if( sessionBean!= null){
	 			sessionBean.setResponseHolder(registryVO);
	 		}

			giftRegistryFormHandler.handleAnnouncementCardCount(getRequest(), getResponse());
			
			addObjectToAssert("isError",  giftRegistryFormHandler.getFormError());
			
			giftRegistryFormHandler.getFormExceptions().clear();
	 	}
	 
}
