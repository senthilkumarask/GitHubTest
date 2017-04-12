package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCategoryLevel extends BaseTestCase {

	public TestCategoryLevel(String name) {
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

	/**
	 * Test case for category level
	 * 
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testIsFirstLevelCategory() throws RestClientException, IOException, JSONException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("arg1", (String) getObject("categoryId"));
		RestResult pd1 = session
				.createHttpRequest("http://" + host + ":" + port + (String) getObject("getFirstLevelCategoryRequest"), params, "POST");
		String responseData = pd1.readInputStream();
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			boolean isFirstLevelCat = (Boolean) json.get("atgResponse");
			assertTrue(isFirstLevelCat);
		}
	}

	/**
	 * Error Test case for category level
	 */
	public void testIsFirstLevelCategoryError() {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		String errorMessage = null;

		try {
			params.put("arg1", (String) getObject("categoryId"));
			session.createHttpRequest("http://" + host + ":" + port + (String) getObject("getFirstLevelCategoryRequest"), params, "POST");
		} catch (RestClientException e1) {
			errorMessage = e1.getMessage();
			if (errorMessage.contains("1001:1001")) {
				assertTrue(true);
			} else {
				assertTrue(false);
			}

		}

	}

}
