package com.bbb.commerce.giftregistry.webservices.test;

import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.constants.BBBWebServiceConstants;
import com.sapient.common.tests.BaseTestCase;

public class WSTestCreateGiftRegistry extends BaseTestCase{
	
	public void testWSServiceCreate() throws Exception {
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryManager");
			
		EventVO eventVO = new EventVO();
			eventVO.setBabyGender("M");
			eventVO.setBabyName("geet");
			eventVO.setBabyNurseryTheme("this is the total value");
			eventVO.setEventDate("12/12/2100");
			RegistryVO registryVO = new RegistryVO();
			registryVO.setEvent(eventVO);

			RegistrantVO coRegistrantVO = new RegistrantVO();
			coRegistrantVO.setFirstName("Peter");
			coRegistrantVO.setLastName("Pan");
			coRegistrantVO.setEmail("test@test.com");
			coRegistrantVO.setPrimaryPhone("1212121212");
			coRegistrantVO.setCellPhone("1212121212");

			RegistrantVO primaryRegistrant = new RegistrantVO();
			primaryRegistrant.setFirstName("Raj");
			primaryRegistrant.setLastName("Sakher");
			primaryRegistrant.setEmail("raj@example.com");
			primaryRegistrant.setPrimaryPhone("1212121234");
			primaryRegistrant.setCellPhone("1212121212");

			AddressVO contactAddress = new AddressVO();
			contactAddress.setFirstName("firstName");
			contactAddress.setLastName("lastname");
			contactAddress.setAddressLine1("addressLine");
			contactAddress.setAddressLine2("addressLine");
			contactAddress.setCity("city");
			contactAddress.setZip("12121");

			coRegistrantVO.setContactAddress(contactAddress);
			primaryRegistrant.setContactAddress(contactAddress);

			registryVO.setCoRegistrant(coRegistrantVO);
			registryVO.setPrimaryRegistrant(primaryRegistrant);
			
			ShippingVO shippingVO = new ShippingVO();
			shippingVO.setShippingAddress(contactAddress);
			shippingVO.setFutureshippingAddress(contactAddress);
			
			registryVO.setShipping(shippingVO);
			registryVO.setServiceName("regSearch");
	
			RegistryResVO registryResVO=giftRegistryFormHandler.getGiftRegistryManager().createRegistry(registryVO);
		addObjectToAssert("isError", registryResVO.getServiceErrorVO().isErrorExists());
		
		
	       
	}
}
