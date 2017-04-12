package com.bbb.internationalshipping.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.utils.InternationaShippingCheckoutHelper;
import com.sapient.common.tests.BaseTestCase;


/**
 * Test TestInternationalShippingCheckoutDroplet
 * 
 * @author Sapient Corporation
 * 
 */
public class TestInternationalShippingCheckoutDroplet extends BaseTestCase {
	
	/**
	 * Method to receive the ContextList and CurrencyMap from TestInternationalShippingCheckoutDroplet
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 * @throws Exception
	 */
	
	
	public void testInternationalShippingCheckoutDroplet() throws ServletException, IOException, BBBSystemException, BBBBusinessException {

			InternationalShippingCheckoutDroplet internationalShippingCheckoutDroplet = (InternationalShippingCheckoutDroplet) getObject("internationalShippingCheckoutDroplet");
			DynamoHttpServletRequest pRequest=getRequest();
			DynamoHttpServletResponse pResponse=getResponse();				
			
			pRequest.setParameter("countryCode", (String) getObject("countryCode"));
			pRequest.setParameter("currencyCode", (String) getObject("currencyCode"));
			pRequest.setParameter("countryName", (String) getObject("countryName"));
			pRequest.setParameter("currencyName", (String) getObject("currencyName"));
			
			internationalShippingCheckoutDroplet.setHelper((InternationaShippingCheckoutHelper) getObject("checkoutHelper"));
			internationalShippingCheckoutDroplet.getHelper().setCheckoutParam(pRequest);
			
			internationalShippingCheckoutDroplet.service(pRequest, pResponse);
				System.out.println("Context List : "+internationalShippingCheckoutDroplet.getHelper().getContextList()); 
				System.out.println("Currency Map : "+internationalShippingCheckoutDroplet.getHelper().getCurrencyMap());  
			assertNotNull(internationalShippingCheckoutDroplet.getHelper().getContextList());
			assertNotNull(internationalShippingCheckoutDroplet.getHelper().getCurrencyMap());
			
	
		
	}
	
	
	
}
