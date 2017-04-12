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

public class TestRestAddShippingAddress extends BaseTestCase {

	public TestRestAddShippingAddress(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;

	/**
	 * Test Remove Item From Cart for Transient user.
	 *
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddNewShippingAddress() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		String result = null;
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");

			JSONObject json = addItemToCart();

			if (json.has("formExceptions")) {
				result = json.getString("formExceptions");
			}
			assertNull(result);

			JSONObject jsonResponseObj = getCurrentOrderDetails();

			System.out.println(jsonResponseObj);

			String itemCount = jsonResponseObj.getString("cartItemCount");
			assertEquals("1", itemCount);

			JSONArray jsonResponseObjArray = new JSONArray(jsonResponseObj.getString("shippingGroups"));
			JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
			String shippingGrpId = json1.getString("id");
			assertNotNull(shippingGrpId);

			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));

			// Shipping address
			params.put("address.firstName", (String) getObject("firstName"));
			params.put("address.lastName", (String) getObject("lastName"));
			params.put("address.companyName", (String) getObject("companyName"));
			params.put("address.address1", (String) getObject("address1"));
			params.put("address.address2", (String) getObject("address2"));
			params.put("address.city", (String) getObject("city"));
			params.put("address.state", (String) getObject("state"));
			params.put("address.country", (String) getObject("country"));
			params.put("address.postalCode", (String) getObject("postalCode"));
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonResponseObj = addShippingAddressRequest();
			System.out.println("testAddNewShippingAddress - " +  jsonResponseObj);

			jsonResponseObj = getCurrentOrderDetails();
			System.out.println(jsonResponseObj);

		} finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout faield", true);
			}
		}

	}



	@SuppressWarnings("unchecked")
	public void testAddNewShippingAddressLoggedInFlow() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		String result = null;
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();


			jsonObject = loginRequestRestCall();

			JSONObject jsonResponseObj = getCurrentOrderDetails();
			System.out.println(jsonResponseObj);

			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));

			// Shipping address
			params.put("address.firstName", (String) getObject("firstName"));
			params.put("address.lastName", (String) getObject("lastName"));
			params.put("address.companyName", (String) getObject("companyName"));
			params.put("address.address1", (String) getObject("address1"));
			params.put("address.address2", (String) getObject("address2"));
			params.put("address.city", (String) getObject("city"));
			params.put("address.state", (String) getObject("state"));
			params.put("address.country", (String) getObject("country"));
			params.put("address.postalCode", (String) getObject("postalCode"));
			params.put("atg-rest-return-form-handler-properties", "true");
			JSONObject jsonObj = addShippingAddressRequest();
			System.out.println("testAddNewShippingAddressLoggedInFlow - " +  jsonObj);


			jsonResponseObj = getCurrentOrderDetails();
			System.out.println(jsonResponseObj);

		} finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout faield", true);
			}
		}

	}

	/**
	 * Test Remove Item From Cart for Transient user.
	 *
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */
	@SuppressWarnings("unchecked")
	public void testAddNewShippingAddressFailure() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		String result = null;
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();

			JSONObject json = addItemToCart();

			if (json.has("formExceptions")) {
				result = json.getString("formExceptions");
			}
			assertNull(result);

			JSONObject jsonResponseObj = getCurrentOrderDetails();

			System.out.println(jsonResponseObj);

			String itemCount = jsonResponseObj.getString("cartItemCount");
			assertEquals("1", itemCount);

			JSONArray jsonResponseObjArray = new JSONArray(jsonResponseObj.getString("shippingGroups"));
			JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
			String shippingGrpId = json1.getString("id");
			assertNotNull(shippingGrpId);

			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));

			// Shipping address
			params.put("address.firstName", (String) getObject("firstName"));
			params.put("address.lastName", (String) getObject("lastName"));
			params.put("address.companyName", (String) getObject("companyName"));
			params.put("address.address1", (String) getObject("address1"));
			params.put("address.address2", (String) getObject("address2"));
			params.put("address.city", (String) getObject("city"));
			params.put("address.state", (String) getObject("state"));
			params.put("address.country", (String) getObject("country"));
			params.put("address.postalCode", (String) getObject("postalCode"));
			params.put("atg-rest-return-form-handler-properties", "true");
			JSONObject jsonObj = addShippingAddressRequest();
			System.out.println("testAddNewShippingAddressFailure=" + jsonObj);

			result = null;
			if (jsonObj.has("formExceptions")) {
				result = jsonObj.getString("formExceptions");
			}

			assertNotNull(result);
			System.out.println("formExceptions=" + result);

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}

	/**
	 * login call
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject loginRequestRestCall() throws RestClientException, JSONException, IOException {

		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult RestResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");

		return new JSONObject(RestResult.readInputStream());
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
	 * Add Shipping Address Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject addShippingAddressRequest() throws RestClientException, JSONException, IOException {
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ (String) getObject("addShippingAddressRequest"), params, "POST");

		return new JSONObject(restResult.readInputStream());
	}

}
