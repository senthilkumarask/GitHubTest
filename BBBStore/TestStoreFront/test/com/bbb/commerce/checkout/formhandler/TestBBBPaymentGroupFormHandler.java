package com.bbb.commerce.checkout.formhandler;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderHolder;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.commerce.checkout.BBBCreditCardContainer;
import com.bbb.commerce.checkout.droplet.GetCreditCardsForPayment;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.order.bean.BBBCommerceItem;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBPaymentGroupFormHandler extends BaseTestCase{
	
	public void testHandlePayment() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBPaymentGroupFormHandler paymentGroupFormHandler = (BBBPaymentGroupFormHandler) getObject("paymentGroupFormHandler");
		
		MutableRepository repository = (MutableRepository)resolveName("/atg/commerce/order/OrderRepository");
		//paymentGroupFormHandler.setVerificationNumber((String)getObject("verificationNumber"));
		paymentGroupFormHandler.setSelectedCreditCardId("New");
		paymentGroupFormHandler.getCreditCardInfo().setCreditCardType((String)getObject("cardType"));
		paymentGroupFormHandler.getCreditCardInfo().setCreditCardNumber((String)getObject("cardNumber"));
		paymentGroupFormHandler.getCreditCardInfo().setCardVerificationNumber((String)getObject("verificationNumber"));
		paymentGroupFormHandler.getCreditCardInfo().setExpirationMonth((String)getObject("expirationMonth"));
		paymentGroupFormHandler.getCreditCardInfo().setExpirationYear((String)getObject("expirationYear"));
		paymentGroupFormHandler.getCreditCardInfo().setNameOnCard((String)getObject("nameOnCard"));
		paymentGroupFormHandler.setSaveProfileFlag(false);
		
		
		//MutableRepository repository = (MutableRepository)paymentGroupFormHandler.getProfilePath().getRepository();
		
		MutableRepositoryItem item = repository.createItem("bbbAddress");
		
		
		//MutableRepositoryItem item = (MutableRepositoryItem)Nucleus.getGlobalNucleus().resolveName("");
		
		BBBRepositoryContactInfo mBillingAddress = new BBBRepositoryContactInfo(item);
		mBillingAddress.setFirstName("test");
		mBillingAddress.setLastName("user");
		mBillingAddress.setAddress1("1855, Saint Fransis street");
		mBillingAddress.setAddress2("Metropoliatan 1307");
		mBillingAddress.setCity("Reston");
		mBillingAddress.setEmail("test1@sapient.com");
		mBillingAddress.setPhoneNumber("5714360771");
		mBillingAddress.setPostalCode("20190");
		mBillingAddress.setState("VA");
		repository.addItem(item);
		
		String pCatalogRefId = (String)getObject("catalogRefId");
		String pProductId = (String)getObject("productId");
		BBBOrderImpl order = (BBBOrderImpl)paymentGroupFormHandler.getOrder();
		BBBCommerceItemManager itemManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");
		
		CommerceItem ciItem = (BBBCommerceItem) itemManager.createCommerceItem(pCatalogRefId, pProductId, 1);
		ciItem.getAuxiliaryData().setSiteId(order.getSiteId());
		itemManager.addItemToOrder(order, ciItem);	
		
		/*BBBRepositoryContactInfo item = order.getBillingAddress();
		item.setFirstName("test");
		item.setMiddleName("1");
		item.setLastName("user");
		item.setAddress1("1855, Saint Fransis street");
		item.setAddress2("Metropoliatan 1307");
		item.setCity("Reston");
		//mBillingAddress.setEmail("test@test.com");
		item.setPhoneNumber("5714360771");
		item.setPostalCode("20190");
		item.setState("VA");*/
		
		
		order.setBillingAddress(mBillingAddress);
		//order.updateVersion();
		//paymentGroupFormHandler.getOrderManager().updateOrder(order);
		pRequest.setRequestURI("/store/sdfasdfsd/asdfasd");
		ServletUtil.setCurrentRequest(pRequest);
		paymentGroupFormHandler.handlePayment(pRequest, pResponse);

		BBBCreditCard creditCard = (BBBCreditCard) paymentGroupFormHandler.getCurrentPaymentGroup();
		
		assertEquals(creditCard.getCreditCardType(), (String)getObject("cardType"));
		assertEquals(creditCard.getCreditCardNumber(), (String)getObject("cardNumber"));
		assertEquals(creditCard.getCardVerificationNumber(), (String)getObject("verificationNumber"));
		assertEquals(creditCard.getExpirationMonth(), (String)getObject("expirationMonth"));
		assertEquals(creditCard.getExpirationYear(), (String)getObject("expirationYear"));
		assertEquals(creditCard.getNameOnCard(), (String)getObject("nameOnCard"));
		
		
		String DROPLET = "GetCreditCardsForPayment";
		
		GetCreditCardsForPayment creditCardsForPayment = (GetCreditCardsForPayment) getObject(DROPLET);
		
		OrderHolder holder = (OrderHolder)resolveName("/atg/commerce/ShoppingCart");
		
		// Setting request parameters
		getRequest().setParameter("Profile", (Profile)resolveName("/atg/userprofiling/Profile"));
		getRequest().setParameter("Order", order);
		getRequest().setParameter("CreditCardContainer", (BBBCreditCardContainer)resolveName("/com/bbb/commerce/checkout/BBBCreditCardContainer"));
		

		// Calling droplet service method
		creditCardsForPayment.service(pRequest, pResponse);
		assertNotNull(pRequest.getParameter("selectedId"));
		
		
	}
	
	
	public void atestHandlePaymentFailScenarios() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBPaymentGroupFormHandler paymentGroupFormHandler = (BBBPaymentGroupFormHandler) getObject("paymentGroupFormHandler");
		
		MutableRepository repository = (MutableRepository)resolveName("/atg/commerce/order/OrderRepository");
		//paymentGroupFormHandler.setVerificationNumber((String)getObject("verificationNumber"));
		paymentGroupFormHandler.setSelectedCreditCardId("New");
		BasicBBBCreditCardInfo creditCardInfo = new BasicBBBCreditCardInfo();
		
		creditCardInfo.setCreditCardType((String)getObject("cardType"));
		creditCardInfo.setCreditCardNumber((String)getObject("cardNumber"));
		creditCardInfo.setCardVerificationNumber((String)getObject("verificationNumberInvalid"));
		creditCardInfo.setExpirationMonth((String)getObject("expirationMonth"));
		creditCardInfo.setExpirationYear((String)getObject("expirationYear"));
		creditCardInfo.setNameOnCard((String)getObject("nameOnCard"));
		paymentGroupFormHandler.setSaveProfileFlag(false);
		paymentGroupFormHandler.setCreditCardInfo(creditCardInfo);
		paymentGroupFormHandler.getCreditCardContainer().
		addCreditCardToContainer(paymentGroupFormHandler.getSelectedCreditCardId(), creditCardInfo);
		paymentGroupFormHandler.getCreditCardContainer().setMaxCreditCardRetryCount(1);
		paymentGroupFormHandler.setVerificationNumber((String)getObject("verificationNumber"));
		
		
		MutableRepositoryItem item = repository.createItem("bbbAddress");
		
		BBBRepositoryContactInfo mBillingAddress = new BBBRepositoryContactInfo(item);
		mBillingAddress.setFirstName("test");
		mBillingAddress.setLastName("user");
		mBillingAddress.setAddress1("1855, Saint Fransis street");
		mBillingAddress.setAddress2("Metropoliatan 1307");
		mBillingAddress.setCity("Reston");
		mBillingAddress.setEmail("test1@sapient.com");
		mBillingAddress.setPhoneNumber("5714360771");
		mBillingAddress.setPostalCode("20190");
		mBillingAddress.setState("VA");
		
		repository.addItem(item);

		BBBOrderImpl order = (BBBOrderImpl)paymentGroupFormHandler.getOrder();
		order.setBillingAddress(mBillingAddress);
		paymentGroupFormHandler.handlePayment(pRequest, pResponse);
		
		assertTrue("Form Error Expected", paymentGroupFormHandler.getFormError());
		assertNotNull("Catalog tool component not initialized", paymentGroupFormHandler.getCatalogTools());
		assertNotNull("Profile is null", paymentGroupFormHandler.getProfilePath());
		paymentGroupFormHandler.resetFormExceptions();
		creditCardInfo.setCardVerificationNumber((String)getObject("verificationNumber"));
		paymentGroupFormHandler.setSelectedCreditCardId("Old");
		paymentGroupFormHandler.getCreditCardContainer().addCreditCardToContainer(paymentGroupFormHandler.getSelectedCreditCardId(),
				creditCardInfo);
		
		paymentGroupFormHandler.handlePayment(pRequest, pResponse);
		
		assertEquals("Credit Card Year Max Limit is not correct" ,5, paymentGroupFormHandler.getCreditCardYearMaxLimit());
		
	}
	
	public void atestHandleRemovePaymentGroupErrorScenario() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBPaymentGroupFormHandler paymentGroupFormHandler = (BBBPaymentGroupFormHandler) getObject("paymentGroupFormHandler");
		paymentGroupFormHandler.setPaymentGroupId((String)getObject("paymentGroupId"));
		paymentGroupFormHandler.handleRemovePaymentGroup(pRequest, pResponse);
		assertTrue("Form Error was expected", paymentGroupFormHandler.getFormError());
		
	}
}
