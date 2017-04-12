package com.bbb.personalstore.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants;
import com.sapient.common.tests.BaseTestCase;

/**
 * This is a Sapunit to test personal Store context Cookie Droplet, which contains methods to
 * test the Last Viewed Cookie.
 * 
 * @author rjain40
 * 
 */
public class TestContextCookieDroplet extends BaseTestCase {
	
	/**
	 * This method tests the Last Viewed cookie creation
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	public void testLastViewedCookie() throws ServletException, IOException {
		ContextCookieDroplet contextCookieDroplet = (ContextCookieDroplet) getObject("TestContextCookieDroplet");
		// Cookie lVCookie = null;

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();

		final String productId = (String) getObject("productId");
		getRequest().setParameter(BBBCoreConstants.PRODUCTID, productId);
		
		atg.servlet.ServletUtil.setCurrentRequest(req);
		contextCookieDroplet.service(req, res);
		Boolean isError = (Boolean) getRequest().getObjectParameter("isError");
		assertNull(isError);
		req.setParameter("isError", null);
		req.setParameter(BBBCoreConstants.PRODUCTID, null);
	}
	
	/**
	 * This method tests the Last Viewed cookie creation with error when product Id does not exists
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	public void testLastViewedCookie_Error() throws ServletException, IOException {
		ContextCookieDroplet contextCookieDroplet = (ContextCookieDroplet) getObject("TestContextCookieDroplet");

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();

		atg.servlet.ServletUtil.setCurrentRequest(req);
		contextCookieDroplet.service(req, res);
		Boolean isError = (Boolean) getRequest().getObjectParameter("isError");
		assertTrue(isError);
		req.setParameter("isError", null);
	}
}
