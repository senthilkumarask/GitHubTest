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

public class TestRestAddCreditCardToOrder extends BaseTestCase {

	public TestRestAddCreditCardToOrder(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;



	 /** Test add new credit card into logged in user
	 * @throws JSONException 
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddNewCreditCartToOrderLoggedIn() throws RestClientException, JSONException, IOException  {

		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;


		try {
			//mSession.login();
			params = (HashMap) getControleParameters();

			//login user
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
	        jsonObject = loginRequestRestCall();


	        params.put("atg-rest-return-form-handler-properties", "true");
	        if(jsonObject.has("formExceptions")){
	        	result = jsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
 			 }
	        assertNull(result);
	     // add item to cart

	     params = (HashMap) getControleParameters();
	     params.put("atg-rest-return-form-handler-properties", "true");
	     jsonObject = addItemToCart();


	        // Get current order details
	        jsonObject = getCurrentOrderDetails();

	        if(jsonObject.has("formExceptions")){
	        	result = jsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
 			 }
	        assertNull(result);
	        System.out.println(jsonObject);
	        
	        JSONArray jsonResponseObjArray = new JSONArray(jsonObject.getString("commerceItemVOList"));
			JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
			String commerceItemId = json1.getString("commerceItemId");
			assertNotNull(commerceItemId);

			params.put("removeItemIdParam", commerceItemId);
			params.put("atg-rest-return-form-handler-properties", "true");
			System.out.println("Calling CartModifierFormHandler.removeItemFromCart , input - " + commerceItemId);

			jsonObject = removeItemFromCart();

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
			String creditLastfourDigit =null;
			System.out.println(jsonObject);
			String expCardLastDigit="1111";
			JSONObject jsonOrderCredtCard=getCurrentOrderDetails();
			if (jsonOrderCredtCard.has("paymentGroups"))
					{
				String jsonStringCard=jsonOrderCredtCard.getString("paymentGroups");
				if (jsonStringCard.contains(expCardLastDigit))
				    {
					creditLastfourDigit=expCardLastDigit;
				    }
				}
			assertEquals(expCardLastDigit, creditLastfourDigit);

			}
			catch (RestClientException e) {
				assertFalse(true);

			} catch (JSONException e) {
				assertFalse(true);

			}
			catch (IOException e) {
				assertFalse(true);
			}

		finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}


	/**
	 * Test add new credit card into order
	 * @throws JSONException 
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddNewCreditCartToOrder() throws RestClientException, JSONException, IOException  {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		String creditCardNumber=null;
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

			String expCardNumber= (String) getObject("cardnumber");
			params.put("saveProfileFlag", (Boolean) getObject("saveprofileflag"));
			params.put("isOrderAmtCoveredByGC", (String) getObject("isOrderAmtCoveredByGC"));
			// credit card details
			params.put("selectedCreditCardId", (String) getObject("selectedcreditcardid"));
			params.put("creditCardInfo.cardVerificationNumber", (String) getObject("cardverificationnumber"));
			params.put("creditCardInfo.creditCardNumber", (String) getObject("cardnumber"));
			params.put("creditCardInfo.creditCardType", (String) getObject("cardtype"));
			params.put("creditCardInfo.expirationMonth", (String) getObject("expirationmonth"));
			params.put("creditCardInfo.expirationYear", (String) getObject("expirationyear"));
			params.put("creditCardInfo.nameOnCard", (String) getObject("nameoncard"));
			// Billing address
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
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
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - " + billingJson);
			JSONObject afterCreditDetail=addCreditCardRestCall();
			System.out.println("addCreditCardRestCall jason response - " + afterCreditDetail);
			JSONObject billingJsonOrder=getCurrentOrderDetails();
			System.out.println("jason response after billing address"+billingJsonOrder);
			JSONObject jsonOrderCredtCard=getCurrentOrderDetails();
			if (jsonOrderCredtCard.has("paymentGroups"))
					{
				String jsonStringCard=jsonOrderCredtCard.getString("paymentGroups");
				System.out.println("jsonStringCard  "+jsonStringCard);
				if (jsonStringCard.contains("XXXXXXXXXXXX1111"))
				    {
					creditCardNumber=expCardNumber;
				    }


			assertTrue(jsonStringCard.contains("XXXXXXXXXXXX1111"));
					}

			}
			catch (RestClientException e) {
				assertFalse(true);

			} catch (JSONException e) {
				assertFalse(true);

			} catch (IOException e) {
				assertFalse(true);
			}

		finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}


	/**
	 * Test credit card with wrong cvv number
	 * @throws JSONException 
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddNewCreditCartToOrderError() throws RestClientException, JSONException, IOException  {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		String error="Please enter a valid CVV code";
		JSONObject jsonObject = null;
		try {
		//	mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			//params.put("atg-rest-return-form-handler-exceptions", true);
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNull(result);

			params.put("saveProfileFlag", (Boolean) getObject("saveprofileflag"));
			params.put("isOrderAmtCoveredByGC", (String) getObject("isOrderAmtCoveredByGC"));
			// credit card details
			params.put("selectedCreditCardId", (String) getObject("selectedcreditcardid"));
			//params.put("creditCardInfo.cardVerificationNumber", (String) getObject("cardverificationnumber"));
			params.put("creditCardInfo.creditCardNumber", (String) getObject("cardnumber"));
			params.put("creditCardInfo.creditCardType", (String) getObject("cardtype"));
			params.put("creditCardInfo.expirationMonth", (String) getObject("expirationmonth"));
			params.put("creditCardInfo.expirationYear", (String) getObject("expirationyear"));
			params.put("creditCardInfo.nameOnCard", (String) getObject("nameoncard"));
			// Billing address
			params.put("userSelectedOption", (String) getObject("userSelectedOption"));
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
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - " + billingJson);
			JSONObject afterCreditDetail=addCreditCardRestCall();
			System.out.println("addCreditCardRestCall jason response - " + afterCreditDetail);
			JSONObject billingJsonOrder=getCurrentOrderDetails();
			System.out.println("jason response after billing address"+billingJsonOrder);

			if (afterCreditDetail.has("formExceptions")) {
				result = afterCreditDetail.getString("formExceptions");

				if (result.contains(error))
				{
					assertTrue(true);
				}
				else
				{
					assertTrue(false);
				}
			}
			else
			{
				assertTrue(false);
			}
			}

			catch (RestClientException e) {
				assertFalse(true);

			} catch (JSONException e) {
				assertFalse(true);

			} catch (IOException e) {
				assertFalse(true);
			}

		finally {

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

		RestResult RestResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("paymentGroupFormHandler"), params,
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
