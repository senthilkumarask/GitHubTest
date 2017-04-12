package com.bbb.rest.internationalShipping.utils;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.rest.framework.BaseTestCase;

public class TestBuildContext extends BaseTestCase {

	public TestBuildContext(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	/**
	 * Test for create Build Context From IP Success Scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testBuildContextFromIPSuccess() throws RestClientException,
			JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult = null;

		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("arg1", (String) getObject("arg1"));
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("buildContextFromIP"), params, "POST");
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

	/**
	 * Test for create Build Context From IP Error Scenario
	 * @throws RestClientException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testBuildContextFromIPError() throws 
			JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult = null;

		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("arg1", (String) getObject("arg1"));
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("buildContextFromIP"),
					params, "POST");
			String responseData = restResult.readInputStream();
			System.out.println(responseData);
			
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1004)) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		finally {
			if (restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Test for create Build Context on CountryCode IP Success Scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testBuildContextOnCountryCodeSuccess() throws RestClientException,
			JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult = null;

		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("arg1", (String) getObject("arg1"));
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("buildContextOnCountryCode"), params, "POST");
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

	/**
	 * Test for create Build Context on CountryCode Error Scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testBuildContextOnCountryCodeError() throws RestClientException,
			JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult = null;

		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("arg1", (String) getObject("arg1"));
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("buildContextOnCountryCode"),
					params, "POST");
			String responseData = restResult.readInputStream();
			System.out.println(responseData);
			
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1005)) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		finally {
			if (restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Test for create Build Context ALL Scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testBuildContextAllSuccess() throws RestClientException,
			JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult = null;

		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("buildContextALL"), params, "POST");
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
