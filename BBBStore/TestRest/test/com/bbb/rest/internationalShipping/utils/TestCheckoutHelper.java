package com.bbb.rest.internationalShipping.utils;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCheckoutHelper extends BaseTestCase {

	public TestCheckoutHelper(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	/**
	 * Test for create International Shipping Checkout Helper Success Scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCheckoutHelper() throws RestClientException,
			JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult = null;

		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("testCheckoutHelper"), params, "POST");
			String responseData = restResult.readInputStream();
			System.out.println(responseData);
			
			if (null != responseData) {
				JSONObject json = new JSONObject(responseData);
				if(json.has("shippingLocation")) {
					JSONObject shippingLocationVO = (JSONObject) json.getJSONObject("shippingLocation");
						if(shippingLocationVO.has("countryCode")) {
						String countryCode = (String)shippingLocationVO.get("countryCode");
						assertNotNull(countryCode);
					}
				}
			}
		} finally {
			if (restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}

	
}
