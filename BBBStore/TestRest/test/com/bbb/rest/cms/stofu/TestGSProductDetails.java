package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGSProductDetails extends BaseTestCase {

	public TestGSProductDetails(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

	}

	public void testProductDetails() throws JSONException, IOException,
			RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();

		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String productId = (String) getObject("productId");
		String categoryId = (String) getObject("categoryId");

		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("productDetails"), params,
				new String[] { productId, categoryId }, "POST");
		String responseData = pd1.readInputStream();
		System.out.println(responseData);

		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			JSONObject productRestVO = (JSONObject) json.getJSONObject("productRestVO");
			JSONObject productVO = productRestVO.getJSONObject("productVO");
			String productIdResponce = (String)productVO.get("productId");
			assertNotNull(productIdResponce);
		}

	}

	public void testProductDetailsErr() throws IOException {

		BBBRestSession session = (BBBRestSession) getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String productId = (String) getObject("productId");
		String categoryId = (String) getObject("categoryId");

		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("productDetails"), params,
					new String[] { productId, categoryId }, "POST");
			String responseData = pd1.readInputStream();
			
			if (responseData != null) {
				assertFalse(true);

			}
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("1004")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
	}
}
