/**
 * 
 */
package com.bbb.payment.droplet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import atg.commerce.pricing.PricingTools;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author msiddi
 * 
 */
public class TestBBBPaymentGroupDroplet extends BaseTestCase {

	@SuppressWarnings("unchecked")
	public void testPaymentGroupDroplet() throws Exception {

		DynamoHttpServletRequest request = getRequest();
		DynamoHttpServletResponse response = getResponse();

		Profile profile = (Profile) request.resolveName("/atg/userprofiling/Profile");
		PricingRequestDocument wsRequest;
		BBBOrderImpl order = null;
		BBBCartFormhandler formHandler = (BBBCartFormhandler) getObject("bbbCartFormHandler");
		PricingTools pricingTools = formHandler.getPromoTools().getPricingTools();

		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));

		order = (BBBOrderImpl) getPricingWSMapper().transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
		/*
		 * Here we collect all global promotion for each type of pricing engine
		 * and later we merge this promotion with request promotion
		 */
		Collection<RepositoryItem> pShippingPricingModels = pricingTools.getShippingPricingEngine().getPricingModels(
				profile);
		Collection<RepositoryItem> pItemPricingModels = pricingTools.getItemPricingEngine().getPricingModels(profile);
		Collection<RepositoryItem> pOrderPricingModels = pricingTools.getOrderPricingEngine().getPricingModels(profile);
		/*
		 * Here we extract promotion by promotion id or coupon id according to
		 * the type passed in request.
		 */
		Map<String, Collection<RepositoryItem>> pricingModels = getPricingWSMapper().populatePromotions(order,
				wsRequest.getPricingRequest(), profile);

		/*
		 * Here we merge global, profile promotions and the promotions present
		 * in the pricing service request and pass each type of promotion list
		 * to appropriate pricing engine through priceOrderSubtotalShipping
		 * method of PricingTools class.
		 */
		pShippingPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.SHIPPING_PROMOTIONS));
		pItemPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.ITEM_PROMOTIONS));
		pOrderPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.ORDER_PROMOTIONS));

		pricingTools.priceOrderSubtotalShipping(order, pItemPricingModels, pShippingPricingModels, pOrderPricingModels,
				pricingTools.getDefaultLocale(), profile, new HashMap<String, Object>());
        
		String pinNumber = (String) getObject("pinNo");
		String cardNumber = (String) getObject("giftCardNo");
		String balanceAmt = (String) getObject("balanceAmt");
		BBBGiftCard gcPaymentGroup = ((BBBPaymentGroupManager) formHandler.getPaymentGroupManager()).createGiftCardPaymentGroup(order, balanceAmt,
				cardNumber, pinNumber);
		((BBBPaymentGroupManager) formHandler.getPaymentGroupManager()).addPaymentGroupToOrder(order, gcPaymentGroup);

		BBBPaymentGroupDroplet paymentGroupDroplet = (BBBPaymentGroupDroplet) getObject("bbbPaymentGroupDroplet");
		request.setParameter("order", order);

		// call droplet's service method for service type - GiftCardDetailService
		request.setParameter(BBBCheckoutConstants.SERVICE_TYPE, BBBCheckoutConstants.GIFT_DET_SERVICE);
		paymentGroupDroplet.service(request, response);
		assertEquals("COVEREDBYGC value is not as expected", "99.0", request.getParameter(BBBCheckoutConstants.COVEREDBYGC));
		assertNotNull("Gift card is null", request.getParameter(BBBCheckoutConstants.GIFTCARDS));
		System.out.println("ORDERTOTAL" +  request.getParameter(BBBCheckoutConstants.ORDER_TOTAL));
		assertEquals("ORDERTOTAL is not as expected", "599.8", request.getParameter(BBBCheckoutConstants.ORDER_TOTAL));

		// call droplet's service method for service type - GetPaymentGroupStatusOnLoad
		request.setParameter(BBBCheckoutConstants.SERVICE_TYPE, BBBCheckoutConstants.GET_PG_ST_ON_LOAD);
		paymentGroupDroplet.service(request, response);
		assertTrue("Not a giftCard", Boolean.valueOf(request.getParameter(BBBCheckoutConstants.IS_GIFTCARDS)));
		assertFalse("Order Amount Covered", Boolean.valueOf(request.getParameter(BBBCheckoutConstants.IS_OD_AMT_COV)));
		assertFalse("Max Gift Card Added", Boolean.valueOf(request.getParameter(BBBCheckoutConstants.IS_MAX_GC_AD)));

	}

	private BBBPricingWSMapper getPricingWSMapper() {
		return (BBBPricingWSMapper) getObject("bbbPriceObject");
	}

}
