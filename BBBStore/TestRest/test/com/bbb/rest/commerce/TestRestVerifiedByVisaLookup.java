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

public class TestRestVerifiedByVisaLookup extends BaseTestCase {
	RestSession mSession;
	HashMap params = null;
	RestResult restResult = null;

	public TestRestVerifiedByVisaLookup(String name) {
		super(name);
	}
	
	/**
	 * Test for authorised login successfully.
	 * 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testRestVerifiedByVisaLookup() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		RestResult pd1 =  null;
		try {
			String result =null;
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			params.put("atg-rest-return-form-handler-properties", "true");
			
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort() 
							+ (String) getObject("loginRequest"), params,"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			System.out.println(json);
			if(json.has("formExceptions")){
				result = json.getString("formExceptions");
			}
			assertNull(result);
			
			JSONObject jsonObject=null;
			
			// Get current order details
			jsonObject = getCurrentOrderDetails();

	        if(jsonObject.has("formExceptions")){
	        	result = jsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
 			 }
	        assertNull(result);

	        // add item to cart

			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);


            // add shipping address to profile
			params = (HashMap) getControleParameters();
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
			params.put("saveToAccount", (Boolean) getObject("saveToAccount"));
			params.put("singleShippingGroupCheckout", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("saveShippingAddress", (Boolean) getObject("saveShippingAddress"));
			params.put("shipToAddressName", (String) getObject("shipToAddressName"));
			params.put("shippingOption", (String) getObject("shippingOption"));
			params.put("sendShippingConfEmail", (Boolean) getObject("singleShippingGroupCheckout"));
			params.put("address.firstName", (String) getObject("sfirstName"));
			params.put("address.lastName", (String) getObject("slastName"));
			params.put("address.companyName", (String) getObject("scompanyName"));
			params.put("address.address1", (String) getObject("saddress1"));
			params.put("address.address2", (String) getObject("saddress2"));
			params.put("address.city", (String) getObject("scity"));
			params.put("address.state", (String) getObject("sstate"));
			params.put("address.country", (String) getObject("scountry"));
			params.put("address.postalCode", (String) getObject("spostalCode"));
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addShippingAddressRequest();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);

			// add Billing address

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
			params.put("billingAddress.mobileNumber", (String) getObject("phoneNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			params.put("saveProfileFlag", (Boolean) getObject("saveprofileflag"));
			params.put("isOrderAmtCoveredByGC", (String) getObject("isOrderAmtCoveredByGC"));
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addBillingAddressRequest();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);

			// add credit card details

			params = (HashMap) getControleParameters();
			params.put("selectedCreditCardId", (String) getObject("selectedcreditcardid"));
			params.put("creditCardInfo.cardVerificationNumber", (String) getObject("cardverificationnumber"));
			params.put("creditCardInfo.creditCardNumber", (String) getObject("cardnumber"));
			params.put("creditCardInfo.creditCardType", (String) getObject("cardtype"));
			params.put("creditCardInfo.expirationMonth", (String) getObject("expirationmonth"));
			params.put("creditCardInfo.expirationYear", (String) getObject("expirationyear"));
			params.put("creditCardInfo.nameOnCard", (String) getObject("nameoncard"));
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addCreditCardRestCall();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("cardVerNumber", (String) getObject("cardVerNumber"));
			params.put("mobileRequest", (String) getObject("mobileRequest"));
			
			
			pd1 = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("getVerifiedByVisaLookup"), params,"POST");
			jsonObject = new JSONObject(pd1.readInputStream());
			System.out.println(jsonObject);
			assertTrue(true);
			
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
				System.out.println("result ::"+result);
			}
			assertNull(result);


			// remove address from profile
			params = (HashMap) getControleParameters();
	        params.put("addressId", (String) getObject("nickname"));
	        params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject= removeAddressRequest();

			 if(jsonObject.has("formExceptions")){
				 result = jsonObject.getString("formExceptions");
				System.out.println("formExceptions="+result);
			 }

			assertNull(result);

		}finally {
			if(pd != null)
				pd = null;
			if(pd1 != null)
				pd1 = null;
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

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");

		return new JSONObject(restResult.readInputStream());
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
	private JSONObject addCreditCardRestCall() throws RestClientException, JSONException, IOException {

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("paymentGroupFormHandler"), params,
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

		restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + (String) getObject("currentOrderDetailsRequest"), params, "POST");
		JSONObject json = new JSONObject(restResult.readInputStream());
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
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addCartRequest"), params,
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

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addBillingAddressRequest"),
				params, "POST");
		return new JSONObject(restResult.readInputStream());
	}


	/**
	 * Commit Order from Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject commitOrderRequest() throws RestClientException, JSONException, IOException {

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("commitOrderRequest"),
				params, "POST");
		String responseData = restResult.readInputStream();
		System.out.println("responseData :: "+ responseData);
		return new JSONObject(responseData);
	}


	/**
	 * Add Shipping Address from Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject addShippingAddressRequest() throws RestClientException, JSONException, IOException {
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ (String) getObject("addShippingAddressRequest"), params, "POST");

		return new JSONObject(restResult.readInputStream());
	}


	/**
	 * remove Address from Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject removeAddressRequest() throws RestClientException, JSONException, IOException {

		restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("removeAddressInProfileRequest"), params,"POST");

		return new JSONObject(restResult.readInputStream());
	}
}
