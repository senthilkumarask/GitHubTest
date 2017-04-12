package com.bbb.commerce.giftregistry;
import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * This class provides test case for handling methods for operating on the
 * current customer's gift registries. It can be used to create new gift
 * registries, edit gift registries and add items to the gift registry during
 * the browse and/or shopping process. Gift registry management is the core
 * functionality of this form handler. It controls creating, updating and item
 * management to all of the customer's gift registries. This includes creating,
 * updating, publishing, and deleting gift registries as well as adding items to
 * the registries. This functionality is invoked via the various handleXXX
 * methods of the form handler.
 * 
 * @author sku134
 * 
 */
public class TestGiftRegistryFormHandler extends BaseTestCase {

	/**
	 * testing preCreateRegistry method
	 * @throws Exception
	 */
	 	 public void testPreCreateHandler() throws Exception {
		 
	 		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
	 		EventVO eventVO = new EventVO();
 			eventVO.setBabyGender("M");
 			eventVO.setBabyName("geet");
 			eventVO.setBabyNurseryTheme("this is the total value");
 			eventVO.setEventDate("12/12/2014");
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
 			
 			giftRegistryFormHandler.setRegistryVO(registryVO);
 			if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
 				giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
			}
 			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("baby");
 			giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
			giftRegistryFormHandler.preCreateRegistry(getRequest(), getResponse());
			  addObjectToAssert("isError",  giftRegistryFormHandler.getFormError());
		 
	 }
	 	/**
	 	 * testing preCreateRegistry method
	 	 * @throws Exception
	 	 */
	 	 	 public void testPreCreateHandlerError() throws Exception {
	 		 
	 	 		GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
	 	 		EventVO eventVO = new EventVO();
	 			eventVO.setBabyGender("M");
	 			eventVO.setBabyName("geet");
	 			eventVO.setBabyNurseryTheme("this is the total value");
	 			eventVO.setEventDate("01/01");
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
	 			contactAddress.setAddressLine1("addressLine1");
	 			contactAddress.setAddressLine2("addressLine2");
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
	 			
	 			giftRegistryFormHandler.setRegistryVO(registryVO);
	 			if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
	 				giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
				}
	 			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("baby");
	 			giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
	 			giftRegistryFormHandler.preCreateRegistry(getRequest(), getResponse());
	 			addObjectToAssert("errorSize",  giftRegistryFormHandler.getFormExceptions().size());
	 		 
	 	 }
	 	

	 /**
	  * testing handleCreateRegistry method
	  * @throws Exception
	  */
	/* public void testCreateHandler() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 EventVO eventVO = new EventVO();
			eventVO.setBabyGender('M');
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
			contactAddress.setAddressLine1("addressLine1");
			contactAddress.setAddressLine2("addressLine2");
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
			
			giftRegistryFormHandler.setRegistryVO(registryVO);
			if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
 				giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
			}
 			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("baby");
		//   boolean flag=giftRegistryFormHandler.handleCreateRegistry(getRequest(), getResponse());
		  //addObjectToAssert("testValue", flag);
		
		 
	     
	    }
	  */

}
