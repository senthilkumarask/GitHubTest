package com.bbb.commerce.droplet;

import java.io.IOException;
import java.rmi.ServerError;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.Address;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.GenericHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.paypal.vo.PayPalAddressVerifyVO;
import com.sapient.common.tests.BaseTestCase;

/**
 * 
 * @author asi162 (Amandeep Singh Dhammu)
 * 
 */

public class TestPayPalDroplet extends BaseTestCase {
	/**
	 * 
	 * Checks the failure scenario for International and PO failure
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 */

	public void testPayPalDropletInternationalandPOfailure()
			throws ServletException, IOException, CommerceException, BBBSystemException {
		
		OrderManager orderManager = (OrderManager) this.getObject("orderManager");
		final BBBCartFormhandler bbbCartFormhandler = (BBBCartFormhandler) this
				.getObject("bbbCartFormHandler");
		
		final PayPalDroplet payPalDroplet = (PayPalDroplet) this
				.getObject("payPalDroplet");
		
		
//		 setting initial data
		setInitialData(bbbCartFormhandler);
		
		
//		 Validate address set to TRUE
		this.getRequest().setParameter(BBBPayPalConstants.VALIDATE_ADDRESS,
				"TRUE");
//		QAS verified set to FALSE
		this.getRequest()
				.setParameter(BBBPayPalConstants.QAS_VERIFIED, "FALSE");

		this.getRequest().setParameter(BBBCoreConstants.TOKEN,
				"EC-69P57731VL3403331");
		
		payPalDroplet.setOrder((BBBOrder) bbbCartFormhandler.getOrder());
		
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		payPalDroplet.service(this.getRequest(), this.getResponse());
		String value = this.getRequest().getParameter(BBBCoreConstants.SUCCESS);
		String redirectUrl = this.getRequest().getParameter(BBBPayPalConstants.REDIRECT_URL);
		
		if(value != null){
			assertTrue(value.equals(BBBCoreConstants.FALSE));
		}else{
			assertNotNull(redirectUrl);
		}
		
		orderManager.removeOrder(payPalDroplet.getOrder().getId());

	}

	/**
	 * This method check the failure of shipping restriction
	 *  
	 * @throws IOException
	 * @throws ServletException
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 * 
	 */
	public void testPayPalDropletShippingRestriction()
			throws ServletException, IOException, CommerceException, BBBSystemException {
		
		OrderManager orderManager = (OrderManager) this.getObject("orderManager");
		final BBBCartFormhandler bbbCartFormhandler = (BBBCartFormhandler) this
				.getObject("bbbCartFormHandler");
		
		
		final PayPalDroplet payPalDroplet = (PayPalDroplet) this
				.getObject("payPalDroplet");
//		 setting initial data
		setInitialData(bbbCartFormhandler);
		
//		 validate address is set to true
		this.getRequest().setParameter(BBBPayPalConstants.VALIDATE_ADDRESS,
				"TRUE");
		
//		 qas verified is set to true	
		this.getRequest().setParameter(BBBPayPalConstants.QAS_VERIFIED, "TRUE");

		this.getRequest().setParameter(BBBCoreConstants.TOKEN,
				"EC-69P57731VL3403332");

		payPalDroplet.setOrder((BBBOrder) bbbCartFormhandler.getOrder());
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		payPalDroplet.service(this.getRequest(), this.getResponse());
		
		System.out.println("The parameter value is should be false... the value is " + this.getRequest().getParameter(BBBCoreConstants.SUCCESS));
		
		String value = this.getRequest().getParameter(BBBCoreConstants.SUCCESS);
		String redirectUrl = this.getRequest().getParameter(BBBPayPalConstants.REDIRECT_URL);
		
		if(value != null){
			assertTrue(value.equals(BBBCoreConstants.FALSE));
		}else{
			assertNotNull(redirectUrl);
		}
		
		orderManager.removeOrder(payPalDroplet.getOrder().getId());
	}
	
	/**
	 * This method test the coupon associated with profile
	 * and redirects to coupon page
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException 
	 * @throws BBBSystemException 
	 */
	public void testPayPalCouponPageRedirect() throws ServletException, IOException, CommerceException, BBBSystemException{
		
		OrderManager orderManager = (OrderManager) this.getObject("orderManager");
		final BBBCartFormhandler bbbCartFormhandler = (BBBCartFormhandler) this
				.getObject("bbbCartFormHandler");

		final PayPalDroplet payPalDroplet = (PayPalDroplet) this
				.getObject("payPalDroplet");
//		 setting initial data
		setInitialData(bbbCartFormhandler);
		
//		 validate address is set to true
		this.getRequest().setParameter(BBBPayPalConstants.VALIDATE_ADDRESS,
				"TRUE");
		
//		 qas verified is set to true	
		this.getRequest().setParameter(BBBPayPalConstants.QAS_VERIFIED, "TRUE");

		this.getRequest().setParameter(BBBCoreConstants.TOKEN,
				"EC-69P57731VL3403333");

		payPalDroplet.setOrder((BBBOrder) bbbCartFormhandler.getOrder());
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		payPalDroplet.service(this.getRequest(), this.getResponse());
		
		String value = this.getRequest().getParameter(BBBCoreConstants.SUCCESS);
		String redirectUrl = this.getRequest().getParameter(BBBPayPalConstants.REDIRECT_URL);
		
		if(value != null){
			String redirectState = this.getRequest().getParameter(BBBPayPalConstants.REDIRECT_STATE);		
			assertTrue(redirectState.equalsIgnoreCase(BBBPayPalConstants.COUPONS));
		//	assertTrue(value.equals(BBBCoreConstants.FALSE));
		}else{
			assertNotNull(redirectUrl);
		}
		
		orderManager.removeOrder(payPalDroplet.getOrder().getId());
		
	}


	/**
	 * This method sets the initial data
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException 
	 */
	
	public void setInitialData(BBBCartFormhandler bbbCartFormhandler) throws ServletException,
			IOException, CommerceException {
// 		This 3 line snippet sets the header in the request.
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,
				"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(
				genericHttpServletRequest);
		ServletUtil.setCurrentRequest(this.getRequest());
		
		
		bbbCartFormhandler.resetFormExceptions();
		bbbCartFormhandler.getOrder().removeAllCommerceItems();
		
		
				
		final DynamoHttpServletRequest pRequest = this.getRequest();
		final DynamoHttpServletResponse pResponse = this.getResponse();
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
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);
		profileItem.setPropertyValue("email", "bhagya.koteru@bedbath.com");	
		bbbCartFormhandler.setAddItemCount(1);
		bbbCartFormhandler.setOrder(getTestOrder());
		HardgoodShippingGroup sg = (HardgoodShippingGroup) bbbCartFormhandler
				.getOrder().getShippingGroups().get(0);
		final String state = (String) this.getObject("state");
		
//		Address set in the hardgoodshipping		 
		Address address = new Address();
		address.setFirstName("Aman");
		address.setLastName("Singh");
		address.setAddress1("131 Dartmouth Street");
		address.setAddress2("3rd Floor");
		address.setCity("Boston");
		address.setState(state);
		address.setCountry("US");
		address.setPostalCode("02116");
		sg.setShippingAddress(address);
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
		System.out.println("adding item to order");
// 		adding item to the order		
		bbbCartFormhandler.handleAddItemToOrder(pRequest, pResponse);
		
		System.out.println("after adding item to order");
		assertTrue(bbbCartFormhandler.getFormExceptions().size() == 0);
	}

	private BBBOrder getTestOrder() throws CommerceException {
		OrderManager orderManager = (OrderManager) this.getObject("orderManager");
		
		BBBOrder order = (BBBOrder) orderManager.createOrder((String) this.getObject("orderId"));
		OrderHolder holder = (OrderHolder) this.getObject("shoppingCart");
		holder.setCurrent(order);
		return order;
	}	
	
}
