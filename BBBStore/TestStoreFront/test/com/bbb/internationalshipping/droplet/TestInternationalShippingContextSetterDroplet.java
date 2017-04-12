/*
 *  Copyright 2014, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestInternationalShippingContextSetterDroplet.java
 *
 *  DESCRIPTION: Test Shipping Context Setter Droplet
 *
 *  HISTORY:
 *  Nov 7, 2011  Initial version
 */
package com.bbb.internationalshipping.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;


/**
 * Test TestInternationalShippingContextSetterDroplet
 * 
 * @author Sapient Corporation
 * 
 */
public class TestInternationalShippingContextSetterDroplet extends BaseTestCase {
	
	/**
	 * Method to receive the Country Code from InternationalShippingContextSetterDroplet
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testInternationalShippingContextSetterDropletSuccess() throws ServletException, IOException {

			InternationalShippingContextSetterDroplet internationalShippingDroplet = (InternationalShippingContextSetterDroplet) getObject("internationalShippingDroplet");
			BBBSessionBean sessionBean =(BBBSessionBean)getObject("sessionBean");
		
			String countryCode = (String) getObject("countryCode");
			sessionBean.getValues().put("defaultCountryCodeFromIP", countryCode);
			internationalShippingDroplet.setSessionBean(sessionBean);
			
			internationalShippingDroplet.service(getRequest(), getResponse());
				System.out.println("Country Code : "+getRequest().getParameter("countryCode"));
			assertNotNull(getRequest().getParameter("countryCode"));
		
	}
		
}
