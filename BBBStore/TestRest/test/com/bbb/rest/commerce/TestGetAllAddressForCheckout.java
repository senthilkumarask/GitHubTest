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

public class TestGetAllAddressForCheckout extends BaseTestCase {

	public TestGetAllAddressForCheckout(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;


	/**
	 * Test get All address from the current order
	 *
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testGetAllAddressForCheckout() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession("BedBathUS");
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		JSONObject jsonObject = null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			addItemToCart();

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
			params.put("X-bbb-site-id", "BedBathUS");

			params.put("atg-rest-return-form-handler-properties", "true");

			jsonObject  = new JSONObject(addBillingAddressRequest().readInputStream());

			System.out.println("testAddNewShippingAddress - " + jsonObject);

			getCurrentOrderDetails();
			String resultStr=getAllAddressForCheckout().readInputStream();
			System.out.println("----"+resultStr.toString());

			String addressFname="tester";
			boolean resultFlag=false;
			if (resultStr.contains(addressFname))
			{
				resultFlag=true;
			}

			assertTrue(resultFlag);
		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
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
	public void testGetAllAddressForCheckoutLoggedInFlow() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			loginRequestRestCall();
			System.out.println(new JSONObject(getCurrentOrderDetails().readInputStream()));
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));
			params.put("atg-rest-return-form-handler-properties", "true");
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
			addBillingAddressRequest();
			System.out.println(new JSONObject(getCurrentOrderDetails().readInputStream()));
			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));

			// Shipping address
			params.put("address.firstName", (String) getObject("shpfirstName"));
			params.put("address.lastName", (String) getObject("shplastName"));
			params.put("address.companyName", (String) getObject("shpcompanyName"));
			params.put("address.address1", (String) getObject("shpaddress1"));
			params.put("address.address2", (String) getObject("shpaddress2"));
			params.put("address.city", (String) getObject("shpcity"));
			params.put("address.state", (String) getObject("shpstate"));
			params.put("address.country", (String) getObject("shpcountry"));
			params.put("address.postalCode", (String) getObject("shppostalCode"));
			params.put("atg-rest-return-form-handler-properties", "true");
			addShippingAddressRequest();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			 addItemToCart();
			String resultStr=getAllAddressForCheckout().readInputStream();
			System.out.println("----"+resultStr.toString());
			String address="dd";
			boolean resultFlag=false;
			if (resultStr.contains(address))
			{
				resultFlag=true;
			}

			assertTrue(resultFlag);

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
	private RestResult loginRequestRestCall() throws RestClientException, JSONException, IOException {

		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");

		return restResult;
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
	private RestResult getCurrentOrderDetails() throws RestClientException, JSONException, IOException {

		params = (HashMap) getControleParameters();
		params.put("arg1", false);
		RestResult restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + (String) getObject("currentOrderDetailsRequest"), params, "POST");
		return restResult;
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
	private RestResult getAllAddressForCheckout() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + (String) getObject("getAllAddRequest"), params, "POST");
		return restResult;
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
	private RestResult addItemToCart() throws RestClientException, JSONException, IOException {

		params.put("jsonResultString", (String) getObject("jsonResultString"));
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addCartRequest"), params,
				"POST");

		return  restResult;
	}

	/**
	 * Add Billing Address Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private RestResult addBillingAddressRequest() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addBillingAddressRequest"),
				params, "POST");
		return restResult;
	}

	/**
	 * Add Shipping Address Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private RestResult addShippingAddressRequest() throws RestClientException, JSONException, IOException {
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ (String) getObject("addShippingAddressRequest"), params, "POST");

		return  restResult;
	}



}
