package com.bbb.payment.giftcard;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.commerce.order.PaymentGroup;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.droplet.DropletException;
import atg.json.JSONException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.constants.BBBCheckoutConstants;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBGiftCardFormHandler extends BaseTestCase {

	public void testBBBGiftCardFormHandler() throws PricingException {

		DynamoHttpServletRequest request = getRequest();
		DynamoHttpServletResponse response = getResponse();

		BBBGiftCardFormHandler giftCardHandler = (BBBGiftCardFormHandler) getObject("giftCardFormHandler");
		String pinNumber = (String)getObject("pinNo");
		String cardNumber = (String)getObject("giftCardNo");
		String siteID = (String)getObject("siteId");
		
		try {
			giftCardHandler.setGiftCardNumber(cardNumber);
			giftCardHandler.setGiftCardPin(pinNumber);
			giftCardHandler.setSiteID(siteID);
			
			giftCardHandler.handleBalance(request, response);
			System.out.println("response: "+ response.getContentType());
			assertEquals("application/json", (String)response.getContentType());
			
			giftCardHandler.setGiftCardNumber(cardNumber);
			giftCardHandler.setGiftCardPin(pinNumber);
			giftCardHandler.setSiteID(siteID);
			
			PricingTools pricingTools = (PricingTools) getObject("pricingTools");
			 pricingTools.priceOrderSubtotalShipping(giftCardHandler.getOrder(), null,
                 null, null,
                 pricingTools.getDefaultLocale(), (RepositoryItem) getRequest().resolveName("/atg/userprofiling/Profile"), new HashMap<String, Object>());
			
			giftCardHandler.handleCreateGiftCard(request, response);
			if(giftCardHandler.getFormError()){
				Vector vec = giftCardHandler.getFormExceptions();
				for (Object object : vec) {
					DropletException exc = (DropletException)object;
					System.out.println("##############################     error message during sapeunit: " + exc.getMessage());
				}
				assertTrue("Form exception during Gift card paymebnt group creation", false);
			}
			List<PaymentGroup> paymentGrp = ((BBBPaymentGroupManager)giftCardHandler.getPaymentGroupManager()).
			getPaymentGroups(giftCardHandler.getOrder(), "giftCard");
			
			assertNotNull("No Giftcard payment group created, NULL ", paymentGrp);
			assertTrue("No Giftcard payment group created, EMPTY ", paymentGrp.size() > 0);
			Integer i = 0;
			for (PaymentGroup paymentGroup : paymentGrp) {
				if(paymentGroup instanceof BBBGiftCard){
					i++;
				}
			}
			
			assertTrue("All the payment groups are not of type BBBGiftCard ", i.equals(paymentGrp.size()));
			
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
