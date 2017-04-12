package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestChangeShippingGroup extends BaseTestCase {

	public TestRestChangeShippingGroup(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;

	/**
	 * To test the success scenarios for online to Bopus & Bopus to online flow
	 *
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testOnlineToBopusAndBopusToOnlineSuccess() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		String result = null;
		JSONArray jsonResponseObjArray = null;
		JSONObject jsonShpGrp = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			JSONObject jsonResponse = addItemToCart();
			checkResponseForFormException(result, jsonResponse);

			JSONObject jsonResponseObj = getCurrentOrderDetails();
			System.out.println("Before OnlineToBopus request" + jsonResponseObj);

			String shipGrpType = getShippingGroupTypeFromJsonResponse(jsonResponseObj);
			assertEquals("hardgoodShippingGroup", shipGrpType);

			// set parameters for Online to Bopus request
			String commerceItemId = getCommerceItemIdFromJson(jsonResponseObj);
			params.put("commerceItemId", commerceItemId);
			params.put("newQuantity", (String) getObject("newQuantity"));
			params.put("storeId", (String) getObject("storeId"));

			// Online to bopus request
			params.put("successURL", (String) getObject("successURL"));
			params.put("errorURL", (String) getObject("errorURL"));
			params.put("systemErrorURL", (String) getObject("systemErrorURL"));
			jsonResponse = processOnlineToBopusRequest();
			checkResponseForFormException(result, jsonResponse);

			jsonResponse = getCurrentOrderDetails();
			System.out.println("After OnlineToBopus request" + jsonResponse);
			jsonResponseObjArray = new JSONArray(jsonResponse.getString("shippingGroups"));
			jsonShpGrp = jsonResponseObjArray.getJSONObject(0);
			String changedshipGrpType = jsonShpGrp.getString("shippingGroupType");
			assertEquals("storeShippingGroup", changedshipGrpType);

			// set parameters for Bopus to Online request
			String oldStoreId = jsonShpGrp.getString("id");
			String newCommerceItemId = getCommerceItemIdFromJson(jsonResponse);
			params = (HashMap) getControleParameters();
			params.put("commerceItemId", newCommerceItemId);
			params.put("newQuantity", (String) getObject("newQuantity"));
			params.put("oldShippingId", oldStoreId);
			params.put("successURL", (String) getObject("successURL"));
			params.put("errorURL", (String) getObject("errorURL"));
			params.put("systemErrorURL", (String) getObject("systemErrorURL"));
			params.put("atg-rest-return-form-handler-properties", "true");
			// Bopus to Online request
			jsonResponse = processBopustoOnlineRequest();
			checkResponseForFormException(result, jsonResponse);

			// Get order details after Bopus to Online request
			jsonResponse = getCurrentOrderDetails();

			changedshipGrpType = getShippingGroupTypeFromJsonResponse(jsonResponseObj);
			assertEquals("hardgoodShippingGroup", changedshipGrpType);

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout faield", true);
			}
		}

	}

	/**
	 * To test the failure scenarios for online to Bopus & Bopus to online flow
	 *
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testOnlineToBopusAndBopusToOnlineFailure() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		String result = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();

			JSONObject jsonResponse = addItemToCart();
			checkResponseForFormException(result, jsonResponse);

			JSONObject jsonResponseObj = getCurrentOrderDetails();
			System.out.println("Before OnlineToBopus request" + jsonResponseObj);

			String shipGrpType = getShippingGroupTypeFromJsonResponse(jsonResponseObj);
			assertEquals("hardgoodShippingGroup", shipGrpType);

			// set parameters for Online to Bopus request
			String commerceItemId = getCommerceItemIdFromJson(jsonResponseObj);

			// Online to bopus request
			params.put("successURL", (String) getObject("successURL"));
			params.put("errorURL", (String) getObject("errorURL"));
			params.put("systemErrorURL", (String) getObject("systemErrorURL"));
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonResponse = processOnlineToBopusRequest();
			checkResponseForFormExceptionFailure(result, jsonResponse);

			params = (HashMap) getControleParameters();

			// Bopus to Online request
			params.put("successURL", (String) getObject("successURL"));
			params.put("errorURL", (String) getObject("errorURL"));
			params.put("systemErrorURL", (String) getObject("systemErrorURL"));
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonResponse = processBopustoOnlineRequest();
			checkResponseForFormExceptionFailure(result, jsonResponse);

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout faield", true);
			}
		}

	}

	/**
	 * Check for form exceptions.
	 *
	 * @param result
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void checkResponseForFormExceptionFailure(String result, JSONObject jsonResponse) throws JSONException {
		if (jsonResponse.has("formExceptions")) {
			result = jsonResponse.getString("formExceptions");
		}

		assertEquals(true, result.contains("Invalid Input"));

	}

	/**
	 * Check for form exceptions.
	 *
	 * @param result
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void checkResponseForFormException(String result, JSONObject jsonResponse) throws JSONException {
		if (jsonResponse.has("formExceptions")) {
			result = jsonResponse.getString("formExceptions");
		}
		assertNull(result);
	}

	/**
	 * To get Commerce Item Id from Json Object.
	 *
	 * @param jsonResponseObj
	 * @return
	 * @throws JSONException
	 */
	private String getCommerceItemIdFromJson(JSONObject jsonResponseObj) throws JSONException {
		JSONArray jsonResponseObjArray;
		JSONObject jsonCommItem;
		jsonResponseObjArray = new JSONArray(jsonResponseObj.getString("commerceItemVOList"));
		jsonCommItem = jsonResponseObjArray.getJSONObject(0);
		String commerceItemId = jsonCommItem.getString("commerceItemId");
		return commerceItemId;
	}

	/**
	 * To get ShippingGroupType from Json Object.
	 *
	 * @param jsonResponseObj
	 * @return
	 * @throws JSONException
	 */
	private String getShippingGroupTypeFromJsonResponse(JSONObject jsonResponseObj) throws JSONException {
		JSONArray jsonResponseObjArray = new JSONArray(jsonResponseObj.getString("shippingGroups"));
		JSONObject jsonShpGrp = jsonResponseObjArray.getJSONObject(0);
		String shipGrpType = jsonShpGrp.getString("shippingGroupType");
		return shipGrpType;
	}

	/**
	 * Get current order details.
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getCurrentOrderDetails() throws RestClientException, JSONException, IOException {

		params = (HashMap) getControleParameters();
		params.put("arg1", false);

		RestResult restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + (String) getObject("currentOrderDetailsRequest"), params, "POST");
		return new JSONObject(restResult.readInputStream());
	}

	/**
	 * Add item to cart.
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject addItemToCart() throws RestClientException, JSONException, IOException {

		params.put("jsonResultString", (String) getObject("jsonResultString"));
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addCartRequest"), params,
				"POST");

		return new JSONObject(restResult.readInputStream());
	}

	/**
	 * Add item to cart.
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject processOnlineToBopusRequest() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + (String) getObject("getOnlineToBopusRequestUrl"), params, "POST");

		String result = restResult.readInputStream();
		System.out.println("processOnlineToBopusRequest - result:" + result);

		return new JSONObject(result);
	}

	/**
	 * Add item to cart.
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject processBopustoOnlineRequest() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + (String) getObject("getBopustoOnlineRequestUrl"), params, "POST");
		String result = restResult.readInputStream();
		System.out.println("processBopustoOnlineRequest - result:" + result);

		return new JSONObject(result);
	}

}
