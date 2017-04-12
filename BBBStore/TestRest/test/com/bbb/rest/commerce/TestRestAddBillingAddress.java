package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestAddBillingAddress extends BaseTestCase {

	public TestRestAddBillingAddress(String name) {
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
	public void testAddNewBillingAddress() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNull(result);

			getCurrentOrderDetails();

			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));

			// Billing address
			params.put("billingAddress.firstName", (String) getObject("firstName"));
			params.put("billingAddress.lastName", (String) getObject("lastName"));
			params.put("billingAddress.companyName", (String) getObject("companyName"));
			params.put("billingAddress.address1", (String) getObject("address1"));
			params.put("billingAddress.address2", (String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode", (String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.phoneNumber", (String) getObject("phoneNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress - " + jsonObject);

			getCurrentOrderDetails();

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void testAddNewBillingAddressLoggedInFlow() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();

			jsonObject = loginRequestRestCall();

			getCurrentOrderDetails();

			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));

			// Billing address
			params.put("billingAddress.firstName", (String) getObject("firstName"));
			params.put("billingAddress.lastName", (String) getObject("lastName"));
			params.put("billingAddress.companyName", (String) getObject("companyName"));
			params.put("billingAddress.address1", (String) getObject("address1"));
			params.put("billingAddress.address2", (String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode", (String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.phoneNumber", (String) getObject("phoneNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			params.put("atg-rest-return-form-handler-properties", "true");
			JSONObject jsonObj = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddressLoggedInFlow - " + jsonObj);

			getCurrentOrderDetails();

		} finally {

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
	public void testAddNewBillingAddressFailure() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();

			JSONObject json = addItemToCart();

			if (json.has("formExceptions")) {
				result = json.getString("formExceptions");
			}
			assertNull(result);

			getCurrentOrderDetails();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));

			// Billing address
			params.put("billingAddress.firstName", (String) getObject("firstName"));
			params.put("billingAddress.lastName", (String) getObject("lastName"));
			params.put("billingAddress.companyName", (String) getObject("companyName"));
			params.put("billingAddress.address1", (String) getObject("address1"));
			params.put("billingAddress.address2", (String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode", (String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.phoneNumber", (String) getObject("phoneNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));

			JSONObject jsonObj = addBillingAddressRequest();
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
		JSONObject json = new JSONObject(restResult.readInputStream());
		System.out.println(json);
		return json;
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
	 * Add Billing Address Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject addBillingAddressRequest() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addBillingAddressRequest"),
				params, "POST");
		return new JSONObject(restResult.readInputStream());
	}

}
