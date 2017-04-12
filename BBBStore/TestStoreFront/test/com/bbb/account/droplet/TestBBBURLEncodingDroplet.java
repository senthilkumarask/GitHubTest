/**
 * @author rsain4
 * @version Id: //com.bbb.commerce.order/TestBBBOrderManager.java.TestBBBOrderManager $$
 * @updated $DateTime: Nov 18, 2011 12:46:29 PM
 */
package com.bbb.account.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

/**
 * @author rajesh saini
 * 
 */
public class TestBBBURLEncodingDroplet extends BaseTestCase {

	public void testBBBURLEncodingDroplet() throws Exception {

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();
		BBBURLEncodingDroplet encodingDroplet = (BBBURLEncodingDroplet) getObject("encodingDroplet");
		encodingDroplet.setLoggingDebug(true);
		encodingDroplet.getEncodingType();

		String URL = (String) getObject("URL");
		String encodingType = (String) getObject("encodingType");
		req.setParameter("encodingType", encodingType);
		req.setParameter("URL", URL);

		encodingDroplet.service(req, res);
		assertNotNull(req.getParameter("encodedURL"));
		
		encodingType = null;
		req.setParameter("encodingType", encodingType);
		encodingDroplet.service(req, res);
		assertNotNull(req.getParameter("encodedURL"));
		

	}

}
