package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftWrapGreetingsDroplet extends BaseTestCase {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGiftWrapGreetingsDroplet() {

        DynamoHttpServletRequest request = getRequest();
        DynamoHttpServletResponse response = getResponse();
        String siteID = (String)getObject("siteId");
        
        GiftWrapGreetingsDroplet droplet = (GiftWrapGreetingsDroplet)getObject("giftWrapGreetingsDroplet");
        
        request.setParameter("siteId", siteID);
        
        try {
			droplet.service(request, response);
			Map<String, String> map = (Map<String, String>)request.getObjectParameter("giftWrapMessages");
			
			assertNotNull("@@@@@@  giftWrapMessages are null ", map);
			assertTrue("@@@@@@  giftWrapMessages Map is empty ", map.size()>0);
			
			Set<Entry<String, String>>  entrySet = map.entrySet();
			
			System.out.println("Giftwrap  messages -------->>>>");
			for (Entry<String, String> entry : entrySet) {
				System.out.println(entry.getKey() +" : "+ entry.getValue() );
			}
			
			
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
