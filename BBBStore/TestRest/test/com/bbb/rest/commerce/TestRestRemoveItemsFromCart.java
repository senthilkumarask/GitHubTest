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

public class TestRestRemoveItemsFromCart extends BaseTestCase {

	public TestRestRemoveItemsFromCart(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;

	/**
	 * Test Remove Item From Cart for loggedin user.
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testRemoveItemFromOrderLoggedInUser() throws JSONException, RestClientException, IOException {
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
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			JSONObject json = addItemToCart();
			if (json.has("formExceptions")) {
				result = json.getString("formExceptions");
			}
			assertNull(result);

			// login to profile
			json = loginRequestRestCall();

			result = null;
			if (json.has("formExceptions")) {
				result = json.getString("formExceptions");
			}
			assertNull(result);

			// get current order details to fetch commerceItem
			JSONObject jsonResponseObj = getCurrentOrderDetails();
			System.out.println(jsonResponseObj);

			JSONArray jsonResponseObjArray = new JSONArray(jsonResponseObj.getString("commerceItemVOList"));
			JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
			String commerceItemId = json1.getString("commerceItemId");
			assertNotNull(commerceItemId);

			params.put("removeItemIdParam", commerceItemId);
			params.put("atg-rest-return-form-handler-properties", "true");
			System.out.println("Calling CartModifierFormHandler.removeItemFromCart , input - " + commerceItemId);

			jsonObject = removeItemFromCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			assertNull(result);

		}
		finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Test Remove Item From Cart for Transient user.
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */
	@SuppressWarnings("unchecked")
	public void testRemoveItemFromOrderTransientUser() throws JSONException, RestClientException, IOException {
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
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
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

			// Get commerce Item id that was added
			JSONArray jsonResponseObjArray = new JSONArray(jsonResponseObj.getString("commerceItemVOList"));
			JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
			String commerceItemId = json1.getString("commerceItemId");
			assertNotNull(commerceItemId);

			params.put("removeItemIdParam", commerceItemId);
			params.put("atg-rest-return-form-handler-properties", "true");
			System.out.println("Calling CartModifierFormHandler.removeItemFromCart , input - " + commerceItemId);

			// Remove commerceItem id that was added
			jsonObject = removeItemFromCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			assertNull(result);

			// Get order details after removing a commerce item
			jsonResponseObj = getCurrentOrderDetails();

			itemCount = jsonResponseObj.getString("cartItemCount");
			assertEquals("0", itemCount);

		}
		finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 ** Test Remove Item From Cart failure, item already removed.
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testRemoveItemFromOrderFailure() throws JSONException, RestClientException, IOException {

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
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			// login request
			JSONObject json = loginRequestRestCall();

			if (json.has("formExceptions")) {
				result = json.getString("formExceptions");
			}
			assertNull(result);
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("removeItemIdParam", (String) getObject("removeInvalidItemIdParam"));
			params.put("removeItemFromOrderErrorURL", (String) getObject("removeItemFromOrderErrorURL"));
			// Remove item from the cart
			jsonObject = removeItemFromCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			assertNotNull(result);
			System.out.println("formExceptions=" + result);

		}
		finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
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
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult RestResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");

		return new JSONObject(RestResult.readInputStream());
	}

	/**
	 * Remove Item From Cart
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject removeItemFromCart() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("removeItemRequest"), params,
				"POST");

		return new JSONObject(restResult.readInputStream());
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
		params.put("atg-rest-return-form-handler-properties", "true");
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addCartRequest"), params,
				"POST");

		return new JSONObject(restResult.readInputStream());
	}

}
