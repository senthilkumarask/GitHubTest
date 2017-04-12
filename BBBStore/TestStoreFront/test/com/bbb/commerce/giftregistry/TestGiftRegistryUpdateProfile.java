package com.bbb.commerce.giftregistry;

import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryUpdateProfile extends BaseTestCase {

	public void testService() throws Exception {
		
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryManager");
		
		giftRegistryFormHandler.resetFormExceptions();
		String username0 = (String) getObject("username0");

		EventVO eventVO = new EventVO();
		eventVO.setBabyGender("M");
		eventVO.setBabyName("raj");
		eventVO.setBabyNurseryTheme("this is the total value");
		eventVO.setEventDate("12/12/2012");
		RegistryVO registryVO = new RegistryVO();
		registryVO.setEvent(eventVO);

		RegistrantVO coRegistrantVO = new RegistrantVO();
		coRegistrantVO.setFirstName("Peter");
		coRegistrantVO.setLastName("Pan");
		coRegistrantVO.setEmail("test1@example.com");
		coRegistrantVO.setPrimaryPhone("1212121212");
		coRegistrantVO.setCellPhone("1212121212");

		RegistrantVO primaryRegistrant = new RegistrantVO();
		primaryRegistrant.setFirstName("Raj");
		primaryRegistrant.setLastName("Sakher");
		primaryRegistrant.setEmail(username0);
		primaryRegistrant.setPrimaryPhone("8888121234");
		primaryRegistrant.setCellPhone("1212121212");
		primaryRegistrant.setAddressSelected("differentContact");

		AddressVO contactAddress = new AddressVO();
		contactAddress.setFirstName("firstName");
		contactAddress.setLastName("lastname");
		contactAddress.setAddressLine1("addressLine");
		contactAddress.setAddressLine2("addressLine");
		contactAddress.setCity("city");
		contactAddress.setState("Alaska");
		contactAddress.setZip("12121");

		coRegistrantVO.setContactAddress(contactAddress);
		primaryRegistrant.setContactAddress(contactAddress);

		registryVO.setCoRegistrant(coRegistrantVO);
		registryVO.setPrimaryRegistrant(primaryRegistrant);
		
		ShippingVO shippingVO = new ShippingVO();
		shippingVO.setShippingAddress(contactAddress);
		shippingVO.setFutureshippingAddress(contactAddress);
		
		registryVO.setShipping(shippingVO);
		
		giftRegistryFormHandler.setRegistryVO(registryVO);
		if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
				giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
		}
			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("baby");
			
			
		giftRegistryFormHandler.setCoRegEmailFoundPopupStatus("false");
		//getRequest().setParameter("siteId", "BuyBuyBaby");
		giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		
		giftRegistryFormHandler.setShippingAddress("shipAdrressSameAsRegistrant");
		
		
		giftRegistryFormHandler.handleCreateRegistry(getRequest(), getResponse());
		
		addObjectToAssert("isError",  giftRegistryFormHandler.getFormError());
		  
		

	
//		assertEquals(expected, actual)


		


	    
	}

}
