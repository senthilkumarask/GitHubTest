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

public class TestGiftWrapSKUDetails extends BaseTestCase {

	public TestGiftWrapSKUDetails(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;

	/**
	 * Test Add Item From Cart for Transient user.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testGiftWrapSKUTransientUser() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession("BedBathUS");
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		JSONObject jsonObject = null;
		String result = null;
		try {
			mSession.login();
 			params = (HashMap) getControleParameters();
 			params.put("atg-rest-return-form-handler-properties", "true");
 			params.put("jsonResultString", (String) getObject("jsonResultString"));
			jsonObject = addItemToCart();
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);	

			params = (HashMap) getControleParameters();
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));
			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));
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
			jsonObject = addShippingAddressRequest();
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);	
			
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1",  (String) getObject("giftWrapOption"));
			
			jsonObject = getWrapSkuDetails();
			System.out.println(jsonObject);

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNull(result);
			
			
			

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}
	
	public void testGiftWrapSKUMultiShippingGroup() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		JSONObject jsonObject = null;
		String result = null;
		try {
			mSession.login();
 			params = (HashMap) getControleParameters();
 			params.put("atg-rest-return-form-handler-properties", "true");
 			params.put("jsonResultString", (String) getObject("jsonResultString"));
			jsonObject = addItemToCart();
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);	

			params = (HashMap) getControleParameters();
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));
			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));
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
			jsonObject = addShippingAddressRequest();
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);	
			
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1",  (String) getObject("giftWrapOption"));
			
			jsonObject = getWrapSkuDetails();
			System.out.println(jsonObject);

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNull(result);
			
			
			

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}
	
	
	

	/**
	 * Test Add Item From Cart for Transient user.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testGiftWrapSKULoggedInFlow() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			jsonObject = loginRequestRestCall();

			
			params = (HashMap) getControleParameters();
 			params.put("atg-rest-return-form-handler-properties", "true");
 			params.put("jsonResultString", (String) getObject("jsonResultString"));
			jsonObject = addItemToCart();
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);	

			params = (HashMap) getControleParameters();
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));
			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));
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
			jsonObject = addShippingAddressRequest();
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);	
			
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1",  (String) getObject("giftWrapOption"));
			
			jsonObject = getWrapSkuDetails();
			System.out.println(jsonObject);

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNull(result);
			
			

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
	 * Get current order details.
	 * 
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getWrapSkuDetails() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getWrapSkuDetails"),
				params, "POST");
		return new JSONObject(restResult.readInputStream());
	}
	
	/**
	 * Add Shipping Address from Request
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

	
    private JSONObject multiShipFormRequest() throws RestClientException, JSONException, IOException {
		
		//params.put("atg-rest-show-rest-paths", (Boolean) true);
		params.put("atg-rest-return-form-handler-properties", (Boolean) getObject("atg-rest-return-form-handler-properties"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler/multiShippingDisplayAPI", params, "POST");
		String result = restResult.readInputStream();
		System.out.println("multiShipFormRequest - result:" + result);

		return new JSONObject(result);
	}


}
