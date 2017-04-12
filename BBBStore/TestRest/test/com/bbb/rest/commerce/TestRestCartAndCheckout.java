package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestCartAndCheckout extends BaseTestCase {

	public TestRestCartAndCheckout(String name) {
		super(name); 
	}

	/**
	 * To test SingleShipping Success scenario.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testShippingMethodsSingleShippingSuccess() throws RestClientException, JSONException, IOException {

		HashMap params = (HashMap) getControleParameters();
		RestSession mSession = null;

		try {

			mSession = loginRequest(params, mSession);
			JSONObject json = getShippingMethods(mSession);
			System.out.println("Output : " + json);

			JSONArray jsonResponseObjArray = new JSONArray(json.getString("shipMethodsVORest"));
			JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
			String skuId = jsonChildObj.getString("skuId");
			String operationMode = json.getString("shippingOperation");
			assertEquals("Error operation mode is not singleShipping", (String) getObject("arg1"), operationMode);
			assertEquals("Sku Id should be null", "null", skuId);

		} finally {
			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout Failed", true);
			}
		}

	}

	/**
	 * To test MultiShipping Success scenario.
	 * 
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testShippingMethodsMultiShippingSuccess() throws RestClientException, JSONException, IOException {

		HashMap params = (HashMap) getControleParameters();
		RestSession mSession = null;

		try {

			mSession = getNewHttpSession();
			testGetCurrentOrderDetailsTransientUser(mSession);
			mSession.setUseHttpsForLogin(false);
			mSession.setUseInternalProfileForLogin(false);
			RestResult pd = null;
			//mSession.login();
			params.put("jsonResultString", (String) getObject("jsonResultString"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addCartRequest"), params, "POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			System.out.println("add to cart - response" + json);

			json = getShippingMethods(mSession);
			System.out.println("Get Methods Output : " + json);

			JSONArray jsonResponseObjArray = new JSONArray(json.getString("shipMethodsVORest"));
			JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
			String skuId = jsonChildObj.getString("skuId");
			String operationMode = json.getString("shippingOperation");
			assertEquals("Sku Id should be null", "16830275", skuId);
			assertEquals("Error operation mode is not multiShipping", (String) getObject("arg1"), operationMode);

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout Failed", true);
			}
		}

	}

	/**
	 * To test ShippingMethods failure scenario.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */
	@SuppressWarnings("unchecked")
	public void testShippingMethodsFailure() throws JSONException, IOException, RestClientException {

		HashMap params = (HashMap) getControleParameters();
		RestSession mSession = null;

		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		//mSession.login();
		try {

			JSONObject json = getShippingMethods(mSession);
			System.out.println("testShippingMethodsFailure " + json);

			// No exception occurred - assert should fail
			assertFalse("No Exception occurred for wrong shipping mode", true);

		} catch (RestClientException t) {
			System.out.println("testShippingMethodsFailure : Inside catch block");
			assertEquals(true, StringUtils.contains(t.getMessage(), "err_ship_methods_1001"));

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}

	// To test the get states API.

	@SuppressWarnings("unchecked")
	public void testGetStatesList() throws JSONException, IOException, RestClientException {

		HashMap params = (HashMap) getControleParameters();
		RestResult restResult = null;

		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		params.put("atg-rest-output", "json");
		params.put("arg1", (String) getObject("arg1"));

		RestSession mSession = getNewHttpSession();

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getStateList", params, "POST");

		JSONObject json = new JSONObject(restResult.readInputStream());
		System.out.println("Output : " + json);

		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
		String firstStateCode = json1.getString("stateCode");

		assertEquals("State code is incorrect", "AA", firstStateCode);

	}

	/**
	 * Get Shipping methods.
	 * 
	 * @param mSession
	 * @return
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getShippingMethods(RestSession mSession) throws RestClientException, JSONException, IOException {
		HashMap params;
		params = (HashMap) getControleParameters();
		params.put("arg1", (String) getObject("arg1"));
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ "/rest/bean/com/bbb/common/manager/ShippingMethodManager/getShippingMethodsFromCurrentOrder", params, "POST");
		return new JSONObject(restResult.readInputStream());
	}

	/**
	 * Login request
	 * 
	 * @param mSession
	 * @param params
	 * @throws RestClientException
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings("unchecked")
	private RestSession loginRequest(Map params, RestSession mSession) throws RestClientException, JSONException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		
		//mSession.login();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-return-form-handler-properties", "true");
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params, "POST");

		return mSession;
	}
	
	
	// To get college state excluding Military state 
	
	@SuppressWarnings("unchecked")
	public void testGetCollegeStatesList() throws JSONException, IOException, RestClientException {

		HashMap params = (HashMap) getControleParameters();
		RestResult restResult = null;

		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		params.put("atg-rest-output", "json");
		params.put("arg1", (String) getObject("arg1"));

		RestSession mSession = getNewHttpSession();

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getCollegeStateList", params, "POST");

		JSONObject json = new JSONObject(restResult.readInputStream());
		System.out.println("Output : " + json);

		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
		String firstStateCode = json1.getString("stateCode");

		assertEquals("State code is incorrect", "AL", firstStateCode);

	}
	
	// To get college list corresponding to state  
	
	@SuppressWarnings("unchecked")
	public void testGetCollegesByState() throws JSONException, IOException, RestClientException {

		HashMap params = (HashMap) getControleParameters();
		RestResult restResult = null;

		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		params.put("atg-rest-output", "json");
		params.put("arg1", (String) getObject("arg1"));

		RestSession mSession = getNewHttpSession();

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getCollegesByState", params, "POST");

		JSONObject json = new JSONObject(restResult.readInputStream());
		System.out.println("Output : " + json);

		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
		String collegeId = json1.getString("collegeId");

		assertEquals("State code is incorrect", "10011", collegeId);

	}


}
