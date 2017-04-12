package com.bbb.commerce.checkout.formhandler;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.Address;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.GenericHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BBBVBVSessionBean;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.droplet.PayPalDroplet;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.PaypalStatusImpl;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * 
 * @author asi162
 * 
 */

public class TestBBBCommitOrderFormHandler extends BaseTestCase {

	private static final String AUTH_CODE_FROM_MOCK = "99860555KP847051C";

	/**
	 * This method test the functionality of committing order by PayPal payment
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 */

	public void testhandleCommitOrderByPayPal() throws ServletException, IOException, CommerceException, BBBSystemException {

		// This 3 line snippet adds header to the request
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,
				"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(
				genericHttpServletRequest);

		ServletUtil.setCurrentRequest(this.getRequest());

		final BBBCartFormhandler bbbCartFormhandler = (BBBCartFormhandler) this
				.getObject("bbbCartFormHandler");
		BBBOrderManager orderManager = (BBBOrderManager) this.getObject("orderManager");
		bbbCartFormhandler.resetFormExceptions();
		bbbCartFormhandler.getOrder().removeAllCommerceItems();		
		BBBOrder order = (BBBOrder) orderManager.createOrder((String) this.getObject("orderId"));
		OrderHolder holder = (OrderHolder) this.getObject("shoppingCart");
		holder.setCurrent(order);
		
		final PriceListManager priceListManager = (PriceListManager) this
				.getObject("bbbPriceListManager");
		final String listPriceId = (String) this.getObject("listPriceId");
		final String salePriceId = (String) this.getObject("salePriceId");
		final RepositoryItem listPriceListItem = priceListManager
				.getPriceList(listPriceId);
		final RepositoryItem salePriceListItem = priceListManager
				.getPriceList(salePriceId);
		final MutableRepositoryItem profileItem = (MutableRepositoryItem) bbbCartFormhandler
				.getProfile();
		final DynamoHttpServletRequest pRequest = this.getRequest();
		final DynamoHttpServletResponse pResponse = this.getResponse();

		pRequest.setContextPath("/store");
		pRequest.setRequestURI("/store/cart/cart.jsp");
		pRequest.setServletPath("/cart/cart.jsp");

		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);

		// Validate address set to TRUE

		pRequest.setParameter(BBBPayPalConstants.VALIDATE_ADDRESS, "TRUE");
		// QAS verified set to FALSE

		pRequest.setParameter(BBBPayPalConstants.QAS_VERIFIED, "FALSE");
		bbbCartFormhandler.setOrder(order);
		bbbCartFormhandler.setAddItemCount(1);
		BBBHardGoodShippingGroup sg = (BBBHardGoodShippingGroup) bbbCartFormhandler
				.getOrder().getShippingGroups().get(0);

		// Address set in the hardgoodshipping
		Address address = new Address();
		address.setFirstName("Aman");
		address.setLastName("Singh");
		address.setAddress1("131 Dartmouth Street");
		address.setAddress2("3rd Floor");
		address.setCity("Boston");
		address.setState("MA");
		address.setCountry("US");
		address.setPostalCode("02116");
		sg.setShippingAddress(address);
		bbbCartFormhandler.setShippingGroup(sg);

		bbbCartFormhandler.getOrder().setSiteId("BuyBuyBaby");
		
		// Currency code set to USD
		bbbCartFormhandler.getOrder().getTaxPriceInfo().setCurrencyCode("USD");

		String pCatalogRefId = (String) this.getObject("catalogRefIdZip");
		if ((bbbCartFormhandler.getItems() != null)
				&& (bbbCartFormhandler.getItems()[0] != null)) {
			// final String pProductId = (String)
			// this.getObject("productIdZip");
			// bbbCartFormhandler.getItems()[0].setProductId(pProductId);
			bbbCartFormhandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			bbbCartFormhandler.getItems()[0].setQuantity(2);
			bbbCartFormhandler.setSiteId("BuyBuyBaby");
		}

		bbbCartFormhandler.setProfile(profileItem);
		// additem to order called
		bbbCartFormhandler.handleAddItemToOrder(pRequest, pResponse);
		assertTrue(bbbCartFormhandler.getFormExceptions().size() == 0);

		bbbCartFormhandler.setFromCart(true);
		bbbCartFormhandler.setSiteId((String) this.getObject("siteId"));
		// checkout with paypal called
		final String siteId = "BuyBuyBaby";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		bbbCartFormhandler.handleCheckoutWithPaypal(pRequest, pResponse);
		if(bbbCartFormhandler.getErrorMap() == null || bbbCartFormhandler.getErrorMap().size() == 0){
			assertTrue(bbbCartFormhandler.getPaypalToken() != null);
			
			pRequest.setParameter(BBBCoreConstants.TOKEN, "EC-69P57731VL3403332");
			// pRequest.setParameter(BBBCoreConstants.TOKEN,
			// bbbCartFormhandler.getPaypalToken());
	
			// pay pal droplet's service called
			
			final PayPalDroplet payPalDroplet = (PayPalDroplet) this
					.getObject("payPalDroplet");
			
			payPalDroplet.setOrder((BBBOrder) bbbCartFormhandler.getOrder());
			payPalDroplet.service(pRequest, pResponse);
			String value = this.getRequest().getParameter(BBBCoreConstants.SUCCESS);
			String redirectUrl = this.getRequest().getParameter(BBBPayPalConstants.REDIRECT_URL);
			
			if(value != null){
				assertTrue(value.equals(BBBCoreConstants.TRUE));
				BBBCommitOrderFormHandler bbbCommitOrderFormHandler = (BBBCommitOrderFormHandler) this
						.getObject("bbbCommitOrderFormHandler");
				
				// commit order calledpayPalDroplet.getOrder()
				bbbCommitOrderFormHandler.setOrder(payPalDroplet.getOrder());
				bbbCommitOrderFormHandler.getCheckoutState().setCurrentLevel("REVIEW");
				holder.setCurrent(payPalDroplet.getOrder());
				bbbCommitOrderFormHandler.handleCommitOrder(pRequest, pResponse);
				assertTrue(bbbCommitOrderFormHandler.getFormExceptions().size() == 0);
		
				String orderState = ((BBBOrderImpl) bbbCommitOrderFormHandler
						.getOrder()).getStateAsString();
		
				assertTrue(orderState.equalsIgnoreCase("SUBMITTED"));
		
				BBBOrderImpl bbbOrder = (BBBOrderImpl) bbbCommitOrderFormHandler
						.getOrder();
				String authCode = getAuthCodeFromOrder(bbbOrder);
		
				assertEquals(AUTH_CODE_FROM_MOCK, authCode);
			}else{
				assertNotNull(redirectUrl);
			}
		}
		else{
			assertNotNull(bbbCartFormhandler.getErrorMap());
		}
		orderManager.removeOrder(bbbCartFormhandler.getOrder().getId());
		

	}

	/**
	 * This method returns the transaction id
	 * 
	 * @param bbbOrder
	 * @return authcode
	 */

	private String getAuthCodeFromOrder(BBBOrderImpl bbbOrder) {
		String authCode = null;
		// get List of Payment Groups
		// Iterate and find PayPal Payment Group
		// Get Auth Code from Payment Group.
		// break and set value in authCode to return
		PaymentGroup group = (PaymentGroup) bbbOrder.getPaymentGroups().get(0);

		@SuppressWarnings("unchecked")
		List<PaypalStatusImpl> authorizationStatus = group
				.getAuthorizationStatus();

		PaypalStatusImpl impl = authorizationStatus.get(0);

		authCode = impl.getTransId();

		return authCode;
	}

	/**
	 * This method test the handleVerifiedByVisaLookup method in BBBCommitOrderFormHandler
	 * @throws CommerceException 
	 * 
	 */
	public void testhandleVerifiedByVisaLookup() throws ServletException, IOException, BBBSystemException, CommerceException {

		BBBCommitOrderFormHandler bbbCommitOrderFormHandler = (BBBCommitOrderFormHandler) this
				.getObject("bbbCommitOrderFormHandler");
		
		BBBOrderManager orderManager = (BBBOrderManager) this.getObject("orderManager");
		
// 		Setting initial data
		setInitialData();

// 		Adding billing address to order
		setBillingAddressToOrder();

// 		Adding credit card to the order
		setCreditCardToOrder();


		bbbCommitOrderFormHandler.handleVerifiedByVisaLookup(this.getRequest(),
				this.getResponse());
		assertTrue(bbbCommitOrderFormHandler.getFormExceptions().size() == 0);
		
		orderManager.removeOrder(bbbCommitOrderFormHandler.getOrder().getId());

	}
	
	/**
	 * This method tests the handleVerifiedByVisaAuth method in BBBCommitOrderFormHandler
	 * 
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws BBBSystemException 
	 * @throws CommerceException 
	 */

	public void testhandleVerifiedByVisaAuth() throws ServletException, IOException, BBBSystemException, CommerceException {
		
		BBBCommitOrderFormHandler bbbCommitOrderFormHandler = (BBBCommitOrderFormHandler) this
				.getObject("bbbCommitOrderFormHandler");
		
		BBBOrderManager orderManager = (BBBOrderManager) this.getObject("orderManager");
		
			
//		Setting initial data
		setInitialData();
		
//		Adding Billing address to order
		setBillingAddressToOrder();
		
//		Adding Credit Card to the order
		setCreditCardToOrder();
	
// 		setting response pay load and token in the session bean
		bbbCommitOrderFormHandler.setPaRes((String) this.getObject("payLoad"));		
		BBBVBVSessionBean bbbvbvSessionBean = new BBBVBVSessionBean();
		BBBVerifiedByVisaVO bBBVerifiedByVisaVO = new BBBVerifiedByVisaVO();
		bBBVerifiedByVisaVO.setToken("cCAuthenticate");
		bbbvbvSessionBean.setbBBVerifiedByVisaVO(bBBVerifiedByVisaVO);
		bbbCommitOrderFormHandler.setVbvSessionBean(bbbvbvSessionBean);	
		
//		Calling VBV Auth	
		
		CheckoutProgressStates states = new CheckoutProgressStates();
		states.setCurrentLevel("REVIEW");
		bbbCommitOrderFormHandler.setCheckoutState(states);
		bbbCommitOrderFormHandler.handleVerifiedByVisaAuth(this.getRequest(), this.getResponse());
		
		
		assertTrue(bbbCommitOrderFormHandler.getFormExceptions().size() == 0 );
		
		orderManager.removeOrder(bbbCommitOrderFormHandler.getOrder().getId());
	}
	
	/**
	 * This method checks failure scenario for NYY
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 */
	public void testFailureScenarioVBVFirst() throws CommerceException, ServletException, IOException, BBBSystemException{
		BBBCommitOrderFormHandler bbbCommitOrderFormHandler = (BBBCommitOrderFormHandler) this
				.getObject("bbbCommitOrderFormHandler");
		
		BBBOrderManager orderManager = (BBBOrderManager) this.getObject("orderManager");
		
			
//		Setting initial data
		setInitialData();
		
//		Adding Billing address to order
		setBillingAddressToOrder();
		
//		Adding Credit Card to the order
		setCreditCardToOrder();
		
		
// 		calling VBV lookup
		
		bbbCommitOrderFormHandler.handleVerifiedByVisaLookup(this.getRequest(), this.getResponse());
		if(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorNo() == null){
			assertTrue((((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getEnrolled()).equalsIgnoreCase("N"));
			assertTrue(bbbCommitOrderFormHandler.getFormExceptions().size() == 0);
//	 		setting response pay load and token in the session bean
			bbbCommitOrderFormHandler.setPaRes((String) this.getObject("payLoad"));		
			BBBVBVSessionBean bbbvbvSessionBean = new BBBVBVSessionBean();
			BBBVerifiedByVisaVO bBBVerifiedByVisaVO = new BBBVerifiedByVisaVO();
			bBBVerifiedByVisaVO.setToken("cCAuthenticate");
			bbbvbvSessionBean.setbBBVerifiedByVisaVO(bBBVerifiedByVisaVO);
			bbbCommitOrderFormHandler.setVbvSessionBean(bbbvbvSessionBean);	
			
//			Calling VBV Auth
			CheckoutProgressStates states = new CheckoutProgressStates();
			states.setCurrentLevel("REVIEW");
			bbbCommitOrderFormHandler.setCheckoutState(states);
			bbbCommitOrderFormHandler.handleVerifiedByVisaAuth(this.getRequest(), this.getResponse());
			if(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorNo() == null){
				assertTrue((((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getEci()).equalsIgnoreCase("06"));
			}
			else{
				assertNotNull(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorDesc());
			}
		}
		else{
			assertNotNull(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorDesc());
		}
		
		orderManager.removeOrder(bbbCommitOrderFormHandler.getOrder().getId());
		
	}
	
	
	/**
	 * This method test the failure scenario of VBV for YNY
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 */
	public void testFailureScenarioVBVSecond() throws CommerceException, ServletException, IOException, BBBSystemException{
		
		BBBCommitOrderFormHandler bbbCommitOrderFormHandler = (BBBCommitOrderFormHandler) this
				.getObject("bbbCommitOrderFormHandler");
		BBBOrderManager orderManager = (BBBOrderManager) this.getObject("orderManager");
		
		
//		Setting initial data
		setInitialData();
		
//		Adding Billing address to order
		setBillingAddressToOrder();
		
//		Adding Credit Card to the order
		setCreditCardToOrder();
		
		
// 		calling VBV lookup
		
		bbbCommitOrderFormHandler.handleVerifiedByVisaLookup(this.getRequest(), this.getResponse());
		if(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorNo() == null){
			assertTrue((((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getEnrolled()).equalsIgnoreCase("Y"));
			assertTrue(bbbCommitOrderFormHandler.getFormExceptions().size() == 0);
//	 		setting response pay load and token in the session bean
			bbbCommitOrderFormHandler.setPaRes((String) this.getObject("payLoad"));		
			BBBVBVSessionBean bbbvbvSessionBean = new BBBVBVSessionBean();
			BBBVerifiedByVisaVO bBBVerifiedByVisaVO = new BBBVerifiedByVisaVO();
			bBBVerifiedByVisaVO.setToken("cCAuthenticate");
			bbbvbvSessionBean.setbBBVerifiedByVisaVO(bBBVerifiedByVisaVO);
			bbbCommitOrderFormHandler.setVbvSessionBean(bbbvbvSessionBean);	
			
//			Calling VBV Auth
			CheckoutProgressStates states = new CheckoutProgressStates();
			states.setCurrentLevel("REVIEW");
			bbbCommitOrderFormHandler.setCheckoutState(states);
			bbbCommitOrderFormHandler.handleVerifiedByVisaAuth(this.getRequest(), this.getResponse());
			if(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorNo() == null){
				assertTrue((((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getPAResStatus()).equalsIgnoreCase("N"));
			}
			else{
				assertNotNull(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorDesc());
			}
		}
		else{
			assertNotNull(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorDesc());
		}
		orderManager.removeOrder(bbbCommitOrderFormHandler.getOrder().getId());
	}
	
	/**
	 * This method checks the failure scenario of VBV for YYN
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 * 
	 */
	public void testFailureScenarioVBVThird() throws CommerceException, ServletException, IOException, BBBSystemException{
		
		BBBCommitOrderFormHandler bbbCommitOrderFormHandler = (BBBCommitOrderFormHandler) this
				.getObject("bbbCommitOrderFormHandler");
		BBBOrderManager orderManager = (BBBOrderManager) this.getObject("orderManager");
		
		
//		Setting initial data
		setInitialData();
		
//		Adding Billing address to order
		setBillingAddressToOrder();
		
//		Adding Credit Card to the order
		setCreditCardToOrder();
		
		
// 		calling VBV lookup
		
		bbbCommitOrderFormHandler.handleVerifiedByVisaLookup(this.getRequest(), this.getResponse());
		if(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorNo() == null){
			assertTrue((((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getEnrolled()).equalsIgnoreCase("Y"));
			assertTrue(bbbCommitOrderFormHandler.getFormExceptions().size() == 0);
//	 		setting response pay load and token in the session bean
			bbbCommitOrderFormHandler.setPaRes((String) this.getObject("payLoad"));		
			BBBVBVSessionBean bbbvbvSessionBean = new BBBVBVSessionBean();
			BBBVerifiedByVisaVO bBBVerifiedByVisaVO = new BBBVerifiedByVisaVO();
			bBBVerifiedByVisaVO.setToken("cCAuthenticate");
			bbbvbvSessionBean.setbBBVerifiedByVisaVO(bBBVerifiedByVisaVO);
			bbbCommitOrderFormHandler.setVbvSessionBean(bbbvbvSessionBean);	
			
//			Calling VBV Auth
			CheckoutProgressStates states = new CheckoutProgressStates();
			states.setCurrentLevel("REVIEW");
			bbbCommitOrderFormHandler.setCheckoutState(states);
			bbbCommitOrderFormHandler.handleVerifiedByVisaAuth(this.getRequest(), this.getResponse());
			if(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorNo() == null){
				assertTrue((((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getSignatureVerification()).equalsIgnoreCase("N"));
			}
			else{
				assertNotNull(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorDesc());
			}
		}
		else{
			assertNotNull(((BBBOrder)bbbCommitOrderFormHandler.getOrder()).getErrorDesc());
		}
		orderManager.removeOrder(bbbCommitOrderFormHandler.getOrder().getId());
	
	}
	
	/**
	 * This method sets the credit card information to the order
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */

	private void setCreditCardToOrder() throws ServletException, IOException {

//		Adding Credit Card to Order
		
		BBBPaymentGroupFormHandler paymentGroupFormHandler = (BBBPaymentGroupFormHandler) getObject("paymentGroupFormHandler");
		paymentGroupFormHandler.setSelectedCreditCardId("New");
		BasicBBBCreditCardInfo creditCardInfo = new BasicBBBCreditCardInfo();
		
		creditCardInfo.setCreditCardType("Visa");
		creditCardInfo.setCreditCardNumber("4111111111111111");
		creditCardInfo.setCardVerificationNumber("123");
		creditCardInfo.setExpirationMonth("07");
		creditCardInfo.setExpirationYear("2019");
		creditCardInfo.setNameOnCard("Visa card");
		paymentGroupFormHandler.setSaveProfileFlag(false);
		paymentGroupFormHandler.setCreditCardInfo(creditCardInfo);
		paymentGroupFormHandler.getCreditCardContainer()
				.addCreditCardToContainer(
						paymentGroupFormHandler.getSelectedCreditCardId(),
						creditCardInfo);
		paymentGroupFormHandler.getCreditCardContainer()
				.setMaxCreditCardRetryCount(2);
		paymentGroupFormHandler.setVerificationNumber("123");
		paymentGroupFormHandler.handlePayment(this.getRequest(),
				this.getResponse());
		

	}

	/**
	 * This method sets the billing address to order
	 * 
	 * @throws BBBSystemException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void setBillingAddressToOrder() throws BBBSystemException,
			ServletException, IOException {

		/**
		 * Adding Billing Address to Order
		 */
		final BBBBillingAddressFormHandler billingAddrForm = (BBBBillingAddressFormHandler) this
				.getObject("billingAddrForm");
		
		final BBBOrder order = (BBBOrder) billingAddrForm.getOrder();
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		final BBBAddress mBillingAddress = new BBBAddressImpl();
		mBillingAddress.setFirstName("FirstName");
		mBillingAddress.setLastName("lastName");
		mBillingAddress.setAddress1("address1billing");
		mBillingAddress.setAddress2("address2billing");
		mBillingAddress.setCity("citybilling");
		mBillingAddress.setEmail("test_vbv@bbb.com");
		mBillingAddress.setMobileNumber("1234567890");
		mBillingAddress.setPostalCode("12345");
		mBillingAddress.setState("state");
		mBillingAddress.setCountry("US");
		order.setSiteId(siteId);
		billingAddrForm.setConfirmedEmail("test_vbv@bbb.com");
		billingAddrForm.setUserSelectedOption("NEW");
		billingAddrForm.setBillingAddress(mBillingAddress);
		billingAddrForm.handleSaveBillingAddress(this.getRequest(),
				this.getResponse());

	}

	/**
	 * This method sets the initial data
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 */

	private void setInitialData() throws ServletException,
			IOException, CommerceException, BBBSystemException {
//		 This 3 line snippet sets header in the request
		
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,
				"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(
				genericHttpServletRequest);
		final DynamoHttpServletRequest pRequest = this.getRequest();
		final DynamoHttpServletResponse pResponse = this.getResponse();
		ServletUtil.setCurrentRequest(pRequest);
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		final BBBBillingAddressFormHandler billingAddrForm = (BBBBillingAddressFormHandler) this
				.getObject("billingAddrForm");
		BBBCommitOrderFormHandler bbbCommitOrderFormHandler = (BBBCommitOrderFormHandler) this
				.getObject("bbbCommitOrderFormHandler");
		bbbCommitOrderFormHandler.resetFormExceptions();
	
		
		BBBPaymentGroupFormHandler paymentGroupFormHandler = (BBBPaymentGroupFormHandler) getObject("paymentGroupFormHandler");
		OrderManager orderManager = (OrderManager) this.getObject("orderManager");
		
		BBBOrder order = null;
		order = (BBBOrder) orderManager.createOrder((String) this.getObject("orderId"));
		OrderHolder holder = (OrderHolder) this.getObject("shoppingCart");
		holder.setCurrent(order);
				
		final BBBCartFormhandler bbbCartFormhandler = (BBBCartFormhandler) this
				.getObject("bbbCartFormHandler");
		bbbCartFormhandler.resetFormExceptions();
		bbbCartFormhandler.setOrder(order);
		billingAddrForm.setOrder(order);
		bbbCommitOrderFormHandler.setOrder(order);
		paymentGroupFormHandler.setOrder(order);
		
		
		// Currency code set to USD
		bbbCartFormhandler.getOrder().getTaxPriceInfo().setCurrencyCode("USD");

		
		final MutableRepositoryItem profileItem = (MutableRepositoryItem) bbbCartFormhandler
				.getProfile();
		final PriceListManager priceListManager = (PriceListManager) this
				.getObject("bbbPriceListManager");
		final String listPriceId = (String) this.getObject("listPriceId");
		final String salePriceId = (String) this.getObject("salePriceId");
		final RepositoryItem listPriceListItem = priceListManager
				.getPriceList(listPriceId);
		final RepositoryItem salePriceListItem = priceListManager
				.getPriceList(salePriceId);
		pRequest.setRequestURI("/store/cart/cart.jsp");
		pRequest.setServletPath("/cart/cart.jsp");

//		Adding item to order
		
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);

		System.out.println("Shipping Restrictions");
		bbbCartFormhandler.setAddItemCount(1);
		HardgoodShippingGroup sg = (HardgoodShippingGroup) bbbCartFormhandler
				.getOrder().getShippingGroups().get(0);
		
		bbbCartFormhandler.setShippingGroup(sg);
		String pCatalogRefId = (String) this.getObject("catalogRefIdZip");
		if ((bbbCartFormhandler.getItems() != null)
				&& (bbbCartFormhandler.getItems()[0] != null)) {
			final String pProductId = (String) this.getObject("productIdZip");
			bbbCartFormhandler.getItems()[0].setProductId(pProductId);
			bbbCartFormhandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			bbbCartFormhandler.getItems()[0].setQuantity(2);
			bbbCartFormhandler.setSiteId("BedBathUS");
		}
		bbbCartFormhandler.setProfile(profileItem);

		bbbCartFormhandler.handleAddItemToOrder(pRequest, pResponse);

//		Adding Shipping Address to Order
		
		final BBBHardGoodShippingGroup hardgoodShippingGroup = (BBBHardGoodShippingGroup) (sg);
		final BBBRepositoryContactInfo atgShippingAddress = (BBBRepositoryContactInfo) hardgoodShippingGroup
				.getShippingAddress();
		atgShippingAddress.setFirstName((String) this.getObject("FirstName"));
		atgShippingAddress.setLastName("user");
		atgShippingAddress.setAddress1("30 West Monroe");
		atgShippingAddress.setAddress2("12th Floor");
		atgShippingAddress.setCity("Chicago");
		atgShippingAddress.setState("IL");
		atgShippingAddress.setCountry("US");
		atgShippingAddress.setPostalCode("60603");
		hardgoodShippingGroup.setShippingAddress(atgShippingAddress);
		
	}

}
