package com.bbb.commerce.giftregistry.formhandler;

import java.util.Locale;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.ServletUtil;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileManager;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandler1 extends BaseTestCase {
	 /**
	  * testing handleCreateRegistry method
	  * @throws Exception
	  */
	 public void testHandleRegistrySearch1() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 giftRegistryFormHandler.resetFormExceptions();
		 RegistrySearchVO registrySearchVO = new RegistrySearchVO ();
		 registrySearchVO.setFirstName("");
		 registrySearchVO.setLastName("");
		 giftRegistryFormHandler.setHidden(1);
		 giftRegistryFormHandler.setRegistrySearchVO(registrySearchVO);
		 
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		 
		 giftRegistryFormHandler.handleRegistrySearch(getRequest(), getResponse());
		  boolean error1 = giftRegistryFormHandler.getFormError();
		  addObjectToAssert("testValue1", error1);
		  
	  }
	 
	 /**
	  * Test updating wedding registry
	  * 
	  * @throws Exception
	  */
	 public void testUpdateWeddingRegistry() throws Exception {
		 
		 //Wedding registry Id = 153587758
		 	GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 	BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		 	bbbProfileFormHandler.setExpireSessionOnLogout(false);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			if(!bbbProfileFormHandler.getProfile().isTransient()){
				bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			}
			
			BBBProfileManager manager = (BBBProfileManager)getObject("bbbProfileManager");
			String pSiteId = (String) getObject("siteId");
			bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
			atg.servlet.ServletUtil.setCurrentRequest(getRequest());
			String email = (String) getObject("email");
			String password = (String) getObject("password");
			manager.updatePassword(email, password);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			bbbProfileFormHandler.getValue().put("login", email);
			bbbProfileFormHandler.getValue().put("password", password);
			bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
			giftRegistryFormHandler.resetFormExceptions();

			RegistryVO registryVO = new RegistryVO();
			
			//registryVO.setRegistryId("153587758");
			registryVO.setRegistryId("153737219"); 
			//fill event details
			EventVO eventVO = new EventVO();
			eventVO.setEventDate("12/12/2015");
			eventVO.setShowerDate("12/11/2015");
			eventVO.setGuestCount("12");
			
			registryVO.setEvent(eventVO);
			
			//co registrant
			RegistrantVO coRegistrantVO = new RegistrantVO();
			coRegistrantVO.setFirstName("Abhilash");
			coRegistrantVO.setLastName("Khajuria");
			coRegistrantVO.setEmail("sunil3185@gmail.com");
			coRegistrantVO.setPrimaryPhone("9878728826");
			coRegistrantVO.setCellPhone("9582417586");

			//primary registrant
			RegistrantVO primaryRegistrant = new RegistrantVO();
			primaryRegistrant.setFirstName("Umang");
			primaryRegistrant.setLastName("Goel");
			primaryRegistrant.setEmail("test1@example.com");
			primaryRegistrant.setPrimaryPhone("9834528898");
			primaryRegistrant.setCellPhone("9865743867");
			primaryRegistrant.setAddressSelected("differentContact");


			AddressVO contactAddress = new AddressVO();
			contactAddress.setFirstName("firstName");
			contactAddress.setLastName("lastname");
			contactAddress.setAddressLine1("addressLine");
			contactAddress.setAddressLine2("addressLine");
			contactAddress.setCity("city");
			contactAddress.setState("CO");
			contactAddress.setZip("07083");

			coRegistrantVO.setContactAddress(contactAddress);
			primaryRegistrant.setContactAddress(contactAddress);

			//set this address as newPrimaryRegAddress
			giftRegistryFormHandler.setRegContactAddress("newPrimaryRegAddress");
			
			
			registryVO.setCoRegistrant(coRegistrantVO);
			registryVO.setPrimaryRegistrant(primaryRegistrant);
			
			ShippingVO shippingVO = new ShippingVO();
			
			//set this address as newShippingAddress
			shippingVO.setShippingAddress(contactAddress);
			giftRegistryFormHandler.setShippingAddress("newShippingAddress");

			//set this address as newShippingAddress
			giftRegistryFormHandler.setFutureShippingDateSelected("true");
			giftRegistryFormHandler.setFutureShippingAddress("newFutureShippingAddress");
			shippingVO.setFutureshippingAddress(contactAddress);
			registryVO.getShipping().setFutureShippingDate("07/12/2015");

			
			registryVO.setShipping(shippingVO);
			
			giftRegistryFormHandler.setRegistryVO(registryVO);
			if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
					giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
			}
		
			//wedding type registry
			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("BRD");
				
				
			giftRegistryFormHandler.setCoRegEmailFoundPopupStatus("true");
			//getRequest().setParameter("siteId", "BuyBuyBaby");
			giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
			
			/*giftRegistryFormHandler.setShippingAddress("shipAdrressSameAsRegistrant");*/
			giftRegistryFormHandler.setErrorURL("registry_creation_form.jsp");
			giftRegistryFormHandler.setSuccessURL("registry_creation_confirmation.jsp");
			giftRegistryFormHandler.setShippingAddressFromWS(null);
			giftRegistryFormHandler.setFutureShippingAddressFromWS(null);
			giftRegistryFormHandler.setRegistrantAddressFromWS(null);
			
			giftRegistryFormHandler.setCoRegEmailNotFoundPopupStatus("false");
			getRequest().getLocale().setDefault(Locale.US);
			ServletUtil.setCurrentRequest(getRequest());
			giftRegistryFormHandler.handleUpdateRegistry(getRequest(), getResponse());
			
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			addObjectToAssert("isError",  giftRegistryFormHandler.getFormError());
		  
	  }
	
	 /**
	  * Test updating Baby registry
	  * 
	  * @throws Exception
	  */
	 public void testUpdateBabyRegistry() throws Exception {
		 //Baby registry id = 153551906
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 	BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		 	bbbProfileFormHandler.setExpireSessionOnLogout(false);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			if(!bbbProfileFormHandler.getProfile().isTransient()){
				bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			}
			
			BBBProfileManager manager = (BBBProfileManager)getObject("bbbProfileManager");
			String siteId = (String) getObject("siteId");
			getRequest().setParameter("siteId", siteId);
			SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
			try {
			  siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
			} catch (SiteContextException siteContextException) {
			  throw new BBBSystemException("Exception" + siteContextException);
			}

			bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
			atg.servlet.ServletUtil.setCurrentRequest(getRequest());
			String email = (String) getObject("email");
			String password = (String) getObject("password");
			manager.updatePassword(email, password);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			bbbProfileFormHandler.getValue().put("login", email);
			bbbProfileFormHandler.getValue().put("password", password);
			bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
			
			giftRegistryFormHandler.resetFormExceptions();
			//String username0 = (String) getObject("username0");

			EventVO eventVO = new EventVO();
			eventVO.setBabyGender("B");
			eventVO.setBabyName("raj");
			eventVO.setBabyNurseryTheme("this is the total value");
			eventVO.setEventDate("12/12/2018");
			
			
			RegistryVO registryVO = new RegistryVO();
			registryVO.setEvent(eventVO);
			registryVO.setRegistryId("153551906");

			RegistrantVO coRegistrantVO = new RegistrantVO();
			coRegistrantVO.setFirstName("Abhilash");
			coRegistrantVO.setLastName("Khajuria");
			coRegistrantVO.setEmail("sunil3185@gmail.com");
			coRegistrantVO.setPrimaryPhone("9865743867");
			coRegistrantVO.setCellPhone("9834528898");

			RegistrantVO primaryRegistrant = new RegistrantVO();
			primaryRegistrant.setFirstName("Umang");
			primaryRegistrant.setLastName("Goel");
			primaryRegistrant.setEmail("test1@example.com");
			primaryRegistrant.setPrimaryPhone("9878728826");
			primaryRegistrant.setCellPhone("9416047335");
			primaryRegistrant.setAddressSelected("differentContact");

			AddressVO contactAddress = new AddressVO();
			contactAddress.setFirstName("firstName");
			contactAddress.setLastName("lastname");
			contactAddress.setAddressLine1("addressLine");
			contactAddress.setAddressLine2("addressLine");
			contactAddress.setCity("city");
			contactAddress.setState("CO");
			contactAddress.setZip("07083");


			coRegistrantVO.setContactAddress(contactAddress);
			primaryRegistrant.setContactAddress(contactAddress);

			//set this address as newPrimaryRegAddress
			giftRegistryFormHandler.setRegContactAddress("newPrimaryRegAddress");
			
			
			registryVO.setCoRegistrant(coRegistrantVO);
			registryVO.setPrimaryRegistrant(primaryRegistrant);
			
			ShippingVO shippingVO = new ShippingVO();
			
			//set this address as newShippingAddress
			shippingVO.setShippingAddress(contactAddress);
			giftRegistryFormHandler.setShippingAddress("newShippingAddress");

			//set this address as newShippingAddress
			giftRegistryFormHandler.setFutureShippingDateSelected("true");
			giftRegistryFormHandler.setFutureShippingAddress("newFutureShippingAddress");
			shippingVO.setFutureshippingAddress(contactAddress);
			registryVO.getShipping().setFutureShippingDate("07/02/2015");

			
			registryVO.setShipping(shippingVO);
			
			giftRegistryFormHandler.setRegistryVO(registryVO);
			if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
					giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
			}
		
			//wedding type registry
			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("BA1");
				
				
			giftRegistryFormHandler.setCoRegEmailFoundPopupStatus("true");
			//getRequest().setParameter("siteId", "BuyBuyBaby");
			giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
			
			/*giftRegistryFormHandler.setShippingAddress("shipAdrressSameAsRegistrant");*/
			getRequest().getLocale().setDefault(Locale.US);
			ServletUtil.setCurrentRequest(getRequest());
			
			giftRegistryFormHandler.handleUpdateRegistry(getRequest(), getResponse());
			
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			addObjectToAssert("isError",  giftRegistryFormHandler.getFormError());
		  
	  }

	 /**
	  * Test updating college registry
	  * 
	  * @throws Exception
	  */
	 public void testUpdateCollegeRegistry() throws Exception {
		 //College registry id = 153588274; 
		 
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 	BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		 	bbbProfileFormHandler.setExpireSessionOnLogout(false);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			if(!bbbProfileFormHandler.getProfile().isTransient()){
				bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			}
			
			BBBProfileManager manager = (BBBProfileManager)getObject("bbbProfileManager");
			String pSiteId = (String) getObject("siteId");
			bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
			atg.servlet.ServletUtil.setCurrentRequest(getRequest());
			String email = (String) getObject("email");
			String password = (String) getObject("password");
			manager.updatePassword(email, password);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			bbbProfileFormHandler.getValue().put("login", email);
			bbbProfileFormHandler.getValue().put("password", password);
			bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
			
			giftRegistryFormHandler.resetFormExceptions();
			

			EventVO eventVO = new EventVO();
			eventVO.setEventDate("12/12/2015");
			eventVO.setCollege("Hindu");
			RegistryVO registryVO = new RegistryVO();
			registryVO.setEvent(eventVO);
			registryVO.setRegistryId("153588274");
			
			RegistrantVO coRegistrantVO = new RegistrantVO();
			coRegistrantVO.setFirstName("Abhilash");
			coRegistrantVO.setLastName("Khajuria");
			coRegistrantVO.setEmail("sunil3185@gmail.com");
			coRegistrantVO.setPrimaryPhone("9896275750");
			coRegistrantVO.setCellPhone("9896375649");

			RegistrantVO primaryRegistrant = new RegistrantVO();
			primaryRegistrant.setFirstName("Umang");
			primaryRegistrant.setLastName("Goel");
			primaryRegistrant.setEmail("test1@example.com");
			primaryRegistrant.setPrimaryPhone("9417277598");
			primaryRegistrant.setCellPhone("9418475286");
			primaryRegistrant.setAddressSelected("differentContact");

			
			AddressVO contactAddress = new AddressVO();
			contactAddress.setFirstName("firstName");
			contactAddress.setLastName("lastname");
			contactAddress.setAddressLine1("addressLine");
			contactAddress.setAddressLine2("addressLine");
			contactAddress.setCity("city");
			contactAddress.setState("KS");
			contactAddress.setZip("12121");

			coRegistrantVO.setContactAddress(contactAddress);
			primaryRegistrant.setContactAddress(contactAddress);

			//set this address as newPrimaryRegAddress
			giftRegistryFormHandler.setRegContactAddress("newPrimaryRegAddress");
			
			
			registryVO.setCoRegistrant(coRegistrantVO);
			registryVO.setPrimaryRegistrant(primaryRegistrant);
			
			ShippingVO shippingVO = new ShippingVO();
			
			//set this address as newShippingAddress
			shippingVO.setShippingAddress(contactAddress);
			giftRegistryFormHandler.setShippingAddress("newShippingAddress");

			//set this address as newShippingAddress
			giftRegistryFormHandler.setFutureShippingDateSelected("true");
			giftRegistryFormHandler.setFutureShippingAddress("newFutureShippingAddress");
			shippingVO.setFutureshippingAddress(contactAddress);
			registryVO.getShipping().setFutureShippingDate("07/02/2015");

			
			registryVO.setShipping(shippingVO);
			
			giftRegistryFormHandler.setRegistryVO(registryVO);
			if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
					giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
			}
		
			//wedding type registry
			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("COL");
				
				
			giftRegistryFormHandler.setCoRegEmailFoundPopupStatus("true");
			//getRequest().setParameter("siteId", "BuyBuyBaby");
			giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
			
			/*giftRegistryFormHandler.setShippingAddress("shipAdrressSameAsRegistrant");*/
			
			
			giftRegistryFormHandler.handleUpdateRegistry(getRequest(), getResponse());
			getRequest().getLocale().setDefault(Locale.US);
			ServletUtil.setCurrentRequest(getRequest());
			addObjectToAssert("isError",  giftRegistryFormHandler.getFormError());
			
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			giftRegistryFormHandler.resetFormExceptions();
		  
	  }	 
	 
	 
	 public void testAddItemToGiftRegistry() throws Exception {
		 //College registry id = 153588274; 
		 
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		 	bbbProfileFormHandler.setExpireSessionOnLogout(false);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			if(!bbbProfileFormHandler.getProfile().isTransient()){
				bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			}
			
			BBBProfileManager manager = (BBBProfileManager)getObject("bbbProfileManager");
			String pSiteId = (String) getObject("siteId");
			bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
			atg.servlet.ServletUtil.setCurrentRequest(getRequest());
			String email = (String) getObject("email");
			String password = (String) getObject("password");
			manager.updatePassword(email, password);
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
			bbbProfileFormHandler.getValue().put("login", email);
			bbbProfileFormHandler.getValue().put("password", password);
			bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
			giftRegistryFormHandler.resetFormExceptions();
			
			EventVO eventVO = new EventVO();
			eventVO.setEventDate("12/12/2013");
			eventVO.setCollege("Hindu");
			RegistryVO registryVO = new RegistryVO();
			registryVO.setEvent(eventVO);
			registryVO.setRegistryId("153588274");
			
			RegistrantVO coRegistrantVO = new RegistrantVO();
			coRegistrantVO.setFirstName("Peter");
			coRegistrantVO.setLastName("Pan");
			coRegistrantVO.setEmail("test1@example.com");
			coRegistrantVO.setPrimaryPhone("9865734865");
			coRegistrantVO.setCellPhone("9419897345");

			RegistrantVO primaryRegistrant = new RegistrantVO();
			primaryRegistrant.setFirstName("Raj");
			primaryRegistrant.setLastName("Sakher");
			primaryRegistrant.setEmail("sunil3185@gmail.com");
			primaryRegistrant.setPrimaryPhone("8978670546");
			primaryRegistrant.setCellPhone("9874537586");
			primaryRegistrant.setAddressSelected("differentContact");

			
			AddressVO contactAddress = new AddressVO();
			contactAddress.setFirstName("firstName");
			contactAddress.setLastName("lastname");
			contactAddress.setAddressLine1("addressLine");
			contactAddress.setAddressLine2("addressLine");
			contactAddress.setCity("city");
			contactAddress.setState("CO");
			contactAddress.setZip("07083");

			coRegistrantVO.setContactAddress(contactAddress);
			primaryRegistrant.setContactAddress(contactAddress);

			//set this address as newPrimaryRegAddress
			giftRegistryFormHandler.setRegContactAddress("newPrimaryRegAddress");
			
			
			registryVO.setCoRegistrant(coRegistrantVO);
			registryVO.setPrimaryRegistrant(primaryRegistrant);
			
			ShippingVO shippingVO = new ShippingVO();
			
			//set this address as newShippingAddress
			shippingVO.setShippingAddress(contactAddress);
			giftRegistryFormHandler.setShippingAddress("newShippingAddress");

			//set this address as newShippingAddress
			giftRegistryFormHandler.setFutureShippingDateSelected("true");
			giftRegistryFormHandler.setFutureShippingAddress("newFutureShippingAddress");
			shippingVO.setFutureshippingAddress(contactAddress);
			registryVO.getShipping().setFutureShippingDate("07/02/2015");

			
			registryVO.setShipping(shippingVO);
			
			giftRegistryFormHandler.setRegistryVO(registryVO);
			if (giftRegistryFormHandler.getSessionBean().getRegistryTypesEvent() != null) {
					giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent(null) ;
			}
		
			//wedding type registry
			giftRegistryFormHandler.getSessionBean().setRegistryTypesEvent("COL");
				
				
			giftRegistryFormHandler.setCoRegEmailFoundPopupStatus("true");
			//getRequest().setParameter("siteId", "BuyBuyBaby");
			giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
			
			giftRegistryFormHandler.setShippingAddress("shipAdrressSameAsRegistrant");
			
			getRequest().getLocale().setDefault(Locale.US);
			ServletUtil.setCurrentRequest(getRequest());
			giftRegistryFormHandler.handleUpdateRegistry(getRequest(), getResponse());
			
			addObjectToAssert("isError",  giftRegistryFormHandler.getFormError());
			
			giftRegistryFormHandler.resetFormExceptions();
		  
	  }	 
	 
}

