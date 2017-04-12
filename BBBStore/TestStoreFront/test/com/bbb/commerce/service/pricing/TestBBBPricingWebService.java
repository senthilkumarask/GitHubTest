/**
 * 
 */
package com.bbb.commerce.service.pricing;

import atg.servlet.ServletUtil;

import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.bedbathandbeyond.atg.PricingResponse;
import com.bedbathandbeyond.atg.PricingResponseDocument;
import com.bedbathandbeyond.atg.ShippingGroup;
import com.sapient.common.tests.BaseTestCase;

/**
 * 
 *
 */
public class TestBBBPricingWebService extends BaseTestCase {

	public void testPricingService() throws Exception {
		ServletUtil.setCurrentRequest(getRequest());
		BBBPricingWebService pricingObject = (BBBPricingWebService) getObject("bbbPriceObject");

		PricingRequestDocument wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));

		PricingResponse response = pricingObject.priceOrder(wsRequest.getPricingRequest());

		assertTrue("Shipping Price is zero, " + response.getResponse().getOrderPrice().getTotalShipping(), + response.getResponse().getOrderPrice().getTotalShipping().doubleValue() > 0.0);
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString1"));

        response = pricingObject.priceOrder(wsRequest.getPricingRequest());

        ShippingGroup[] shippingGroupArray = response.getResponse().getShippingGroups().getShippingGroupArray();
        assertTrue("1, LAst item price not right, " + shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(4).getFinalPrice().doubleValue(), shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(4).getFinalPrice().doubleValue() == 15.0);
        
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString2"));

        response = pricingObject.priceOrder(wsRequest.getPricingRequest());

        shippingGroupArray = response.getResponse().getShippingGroups().getShippingGroupArray();
        assertTrue("2, Last item price not right, " + shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(3).getFinalPrice().doubleValue(), shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(3).getFinalPrice().doubleValue() == 20.0);
        
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString3"));

		response = pricingObject.priceOrder(wsRequest.getPricingRequest());

		shippingGroupArray = response.getResponse().getShippingGroups().getShippingGroupArray();
        assertTrue("3, LAst item price not right, " + shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(2).getFinalPrice().doubleValue(), shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(2).getFinalPrice().doubleValue() == 20.0);
        
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString4"));

        response = pricingObject.priceOrder(wsRequest.getPricingRequest());

        shippingGroupArray = response.getResponse().getShippingGroups().getShippingGroupArray();
        assertTrue("eco fee item price not right, " + shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(2).getEcoFee().doubleValue(), shippingGroupArray[shippingGroupArray.length - 1].getItemList().getItemArray(2).getEcoFee().doubleValue() == 2.0);
        
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString5"));

        response = pricingObject.priceOrder(wsRequest.getPricingRequest());

        shippingGroupArray = response.getResponse().getShippingGroups().getShippingGroupArray();
        assertTrue("gift wrap fee item price not right, " + shippingGroupArray[0].getShippingPrice().getGiftWrapFee().doubleValue(), shippingGroupArray[0].getShippingPrice().getGiftWrapFee().doubleValue() > 0.0);
        assertTrue("Surcharge fee price not right, " + shippingGroupArray[1].getItemList().getItemArray(0).getSurcharge().doubleValue(), shippingGroupArray[1].getItemList().getItemArray(0).getSurcharge().doubleValue() >= 0.0);
        
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString6"));
        PricingServiceImpl pSImpl = new PricingServiceImpl();
        PricingResponseDocument performPricing = pSImpl.performPricing(wsRequest);
        

        shippingGroupArray = performPricing.getPricingResponse().getResponse().getShippingGroups().getShippingGroupArray();
        assertTrue("Promotion applied not right, " + shippingGroupArray[0].getItemList().getItemArray(0).getAdjustmentList().getAdjustmentArray(0).getDiscountAmount().doubleValue(), shippingGroupArray[0].getItemList().getItemArray(0).getAdjustmentList().getAdjustmentArray(0).getDiscountAmount().doubleValue() > 0.0);
        
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString7"));
        try{
            performPricing = pSImpl.performPricing(wsRequest);
            assertTrue("Exception should be thrown", false);
        } catch (SoapFault e) {            
        }
        
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString8"));

		response = pricingObject.priceOrder(wsRequest.getPricingRequest());

		assertTrue("Total Delivery Surcharge for LTL Order is greater than zero, " + response.getResponse().getOrderPrice().getTotalDeliverySurcharge(), + response.getResponse().getOrderPrice().getTotalDeliverySurcharge().doubleValue() > 0.0);
		assertTrue("Total Delivery Surcharge for LTL Order is greater than zero, " + response.getResponse().getOrderPrice().getDeliveryThreshold(), + response.getResponse().getOrderPrice().getDeliveryThreshold().doubleValue() > 0.0);
		assertTrue("Delivery Surcharge for LTL item is greater than zero, " + response.getResponse().getShippingGroups().getShippingGroupArray(0).getItemList().getItemArray(0).getDeliverySurcharge(), + response.getResponse().getShippingGroups().getShippingGroupArray(0).getItemList().getItemArray(0).getDeliverySurcharge().doubleValue() > 0.0);
		assertTrue("Delivery Surcharge saving for LTL item is greater than zero, " + response.getResponse().getShippingGroups().getShippingGroupArray(0).getItemList().getItemArray(0).getDeliverySurchargeSaving(), + response.getResponse().getShippingGroups().getShippingGroupArray(0).getItemList().getItemArray(0).getDeliverySurchargeSaving().doubleValue() > 0.0);
        
	}

}
