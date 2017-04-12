package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.apache.xmlbeans.XmlException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftWrapCheckDroplet extends BaseTestCase {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGiftWrapCheckDroplet() {

        DynamoHttpServletRequest request = getRequest();
        DynamoHttpServletResponse response = getResponse();

        Profile profile = (Profile) request.resolveName("/atg/userprofiling/Profile");
        PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
        try {
            wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
            order = (BBBOrderImpl) getPricingWSMapper().transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());          
    
            GiftWrapCheckDroplet gwcd = (GiftWrapCheckDroplet)getObject("giftWrapCheckDroplet");
            
            request.setParameter("order", order);
            request.setParameter("siteId", (String)getObject("siteId"));
            request.setParameter("shippingGroup", order.getShippingGroups().get(0));
            gwcd.service(request, response);
            
           String giftWrapFlag = request.getParameter("giftWrapFlag");
           String giftWrapPrice = request.getParameter("giftWrapPrice");
           String nonGiftWrapSkus = request.getParameter("nonGiftWrapSkus");
            
           System.out.println("***** giftWrapFlag: " + giftWrapFlag);
           System.out.println("***** giftWrapPrice: " + giftWrapPrice);
           System.out.println("***** nonGiftWrapSkus: " + nonGiftWrapSkus);
           
            request.setParameter("order", order);
            request.setParameter("siteId", (String)getObject("siteId"));
            request.setParameter("giftWrapOption", "multi");
            gwcd.service(request, response);
            
            System.out.println("***** giftWrapPrice: " + request.getParameter("giftWrapPrice"));
            System.out.println("***** giftWrapMap: " + request.getParameter("giftWrapMap"));
            
            assertNotNull("###  giftWrapFlag is null " , giftWrapFlag);
            assertNotNull("###  giftWrapPrice is null " , giftWrapPrice);
            assertNotNull("###  giftWrapMap is null " , request.getParameter("giftWrapMap"));
            
           
        } catch (XmlException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (BBBBusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BBBSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        

    }



    private BBBPricingWSMapper getPricingWSMapper() {
        return (BBBPricingWSMapper) getObject("bbbPriceObject");
    }

}
