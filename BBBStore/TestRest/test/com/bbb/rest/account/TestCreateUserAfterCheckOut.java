package com.bbb.rest.account;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCreateUserAfterCheckOut  extends BaseTestCase  {

	public TestCreateUserAfterCheckOut(String name) {
		super(name);
	}

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	RestResult restResult = null;




	/** Test Create user when user has provided email id that is already registered on the site
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCreateUserWhenEmailIsAlreadyRegistered()   throws RestClientException, IOException, JSONException{

		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		String result = null;
		JSONObject jsonObject = null;

		try {
			//mSession.login();
			String email = (String) getObject("email");
			System.out.println("EMAIL USED TO REGISTER THE NEW USER  "+email);
			// add item to cart

			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties",true);
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
			params.put("atg-rest-return-form-handler-properties",true);
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
			params.put("billingAddress.email", email);
			params.put("billingAddress.mobileNumber", (String) getObject("mobileNumber"));
			params.put("confirmedEmail", email);
			params.put("saveProfileFlag", (Boolean) getObject("saveprofileflag"));
			params.put("isOrderAmtCoveredByGC", (String) getObject("isOrderAmtCoveredByGC"));
			params.put("atg-rest-return-form-handler-properties",true);
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
			params.put("atg-rest-return-form-handler-properties",true);

			jsonObject = addCreditCardRestCall();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
				System.out.println(result);
			}
			assertNull(result);
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties",true);
			jsonObject= commitOrderRequest();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
				System.out.println("result1 ::"+result);
			}
			assertNull(result);


			/*
			 * Call to create user after guest checkout
			 */
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions","true");
			params.put("atg-rest-depth", "3");
			params.put("sharedCheckBoxEnabled", (Boolean) getObject("sharedCheckBoxEnabled"));
			params.put("value.password", (String) getObject("password"));
			params.put("value.confirmPassword", (String) getObject("confirmPassword"));
			params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
			params.put("preRegisterSuccessURL", "atg-rest-ignore-redirect");
			params.put("preRegisterErrorURL", "atg-rest-ignore-redirect");
			params.put("atg-rest-return-form-handler-properties",true);

			//params.put("value.email", email);

			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/atg/userprofiling/ProfileFormHandler/registration", params,"POST");

			//get response
			String response=pd2.readInputStream();
			System.out.println("create user after checkout response "+response  );
			assertNotNull(response);

		}finally {
			if(restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}


	/** Test Create user when user has provided email id that is already registered on the site
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCreateUserInvalidPassword()   throws RestClientException, IOException, JSONException{

		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		String result = null;
		JSONObject jsonObject = null;

		try {
		//	mSession.login();
			String email = (String) getObject("email");
			System.out.println("EMAIL USED TO REGISTER THE NEW USER  "+email);
			// add item to cart

			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties",true);
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
			params.put("atg-rest-return-form-handler-properties",true);
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
			params.put("billingAddress.email", email);
			params.put("billingAddress.mobileNumber", (String) getObject("mobileNumber"));
			params.put("confirmedEmail", email);
			params.put("saveProfileFlag", (Boolean) getObject("saveprofileflag"));
			params.put("isOrderAmtCoveredByGC", (String) getObject("isOrderAmtCoveredByGC"));
			params.put("atg-rest-return-form-handler-properties",true);
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
			params.put("atg-rest-return-form-handler-properties",true);
			jsonObject = addCreditCardRestCall();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
				System.out.println(result);
			}
			assertNull(result);

			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties",true);
			jsonObject= commitOrderRequest();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
				System.out.println("result1 ::"+result);
			}
			assertNull(result);


			/*
			 * Call to create user after guest checkout
			 */
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions","true");
			params.put("atg-rest-depth", "3");
			params.put("sharedCheckBoxEnabled", (Boolean) getObject("sharedCheckBoxEnabled"));
			params.put("value.password", (String) getObject("password"));
			params.put("value.confirmPassword", (String) getObject("confirmPassword"));
			params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
			params.put("preRegisterSuccessURL", "atg-rest-ignore-redirect");
			params.put("preRegisterErrorURL", "atg-rest-ignore-redirect");
			params.put("atg-rest-return-form-handler-properties",true);
			//params.put("value.email", email);

			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/atg/userprofiling/ProfileFormHandler/registration", params,"POST");

			//get response
			String response=pd2.readInputStream();
			System.out.println("create user after checkout response "+response  );
			assertNotNull(response);

		}finally {
			if(restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}


	/** Test Create user after guest checkout
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCreateUserAfterCheckOut()   throws RestClientException, IOException, JSONException{

		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		String result = null;
		JSONObject jsonObject = null;

		try {
			//mSession.login();
			String email = (int)(Math.random()*9999)+(String) getObject("email");
			System.out.println("EMAIL USED TO REGISTER THE NEW USER  "+email);
			// add item to cart

			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties",true);

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
			params.put("atg-rest-return-form-handler-properties",true);

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
			params.put("billingAddress.email", email);
			params.put("billingAddress.mobileNumber", (String) getObject("mobileNumber"));
			params.put("confirmedEmail", email);
			params.put("saveProfileFlag", (Boolean) getObject("saveprofileflag"));
			params.put("isOrderAmtCoveredByGC", (String) getObject("isOrderAmtCoveredByGC"));
			params.put("atg-rest-return-form-handler-properties",true);
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
			params.put("atg-rest-return-form-handler-properties",true);
			jsonObject = addCreditCardRestCall();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
				System.out.println(result);
			}
			assertNull(result);

			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties",true);
			jsonObject= commitOrderRequest();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
				System.out.println("result1 ::"+result);
			}
			assertNull(result);

			/*
			 * Code to get order details
			 */

			params = (HashMap) getControleParameters();
			params.put("arg1", false);
			RestResult	pd = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
					+ (String) getObject("currentOrderDetailsRequest"), params, "POST");
			String getOrderResponse=pd.readInputStream();
			System.out.println(" getOrderResponse  "+getOrderResponse);
			JSONObject jsonResponseObj = new JSONObject(getOrderResponse);
			String itemCount = jsonResponseObj.getString("cartItemCount");
			System.out.println("itemCount   "+itemCount );

			/*
			 * Call to create user after guest checkout
			 */

			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions","true");
			params.put("atg-rest-depth", "3");
			params.put("sharedCheckBoxEnabled", (Boolean) getObject("sharedCheckBoxEnabled"));
			params.put("value.password", (String) getObject("password"));
			params.put("value.confirmPassword", (String) getObject("confirmPassword"));
			params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
			params.put("preRegisterSuccessURL", "atg-rest-ignore-redirect");
			params.put("preRegisterErrorURL", "atg-rest-ignore-redirect");
			params.put("atg-rest-return-form-handler-properties",true);
			//params.put("value.email", "123");

			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/atg/userprofiling/ProfileFormHandler/registration", params,"POST");

			//get response
			String response=pd2.readInputStream();
			System.out.println("create user after checkout response "+response  );
			assertNotNull(response);

		}finally {
			if(restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}




	/** Test create user when invalid order is placed
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public void testCreateUserInvalidOrderCheckout()   throws RestClientException, IOException, JSONException{

		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		String result = null;
		JSONObject jsonObject = null;

		try {
			//mSession.login();
			String email = (int)(Math.random()*9999)+(String) getObject("email");
			params = (HashMap) getControleParameters();
			/*
			 * Call to create user after guest checkout
			 */
			params.put("atg-rest-return-form-handler-exceptions","true");
			params.put("atg-rest-depth", "0");
			params.put("sharedCheckBoxEnabled", (Boolean) getObject("sharedCheckBoxEnabled"));
			params.put("value.password", (String) getObject("password"));
			params.put("value.confirmPassword", (String) getObject("confirmPassword"));
			params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
			params.put("preRegisterSuccessURL", "atg-rest-ignore-redirect");
			params.put("preRegisterErrorURL", "atg-rest-ignore-redirect");
			params.put("atg-rest-return-form-handler-properties",true);
			//params.put("value.email", email);

			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/atg/userprofiling/ProfileFormHandler/registration", params,"POST");

			//get response
			String response=pd2.readInputStream();
			System.out.println("create user after checkout response "+response  );
			assertNotNull(response);
			jsonObject=new  JSONObject(response);
			String formExceptions = jsonObject.getString("formExceptions");
			System.out.println("formExceptions in test case when user has not placed valid order before calling create user ::"+formExceptions);
			assertNotNull(formExceptions);

		}finally {
			if(restResult != null)
				restResult = null;
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
	@SuppressWarnings({ "unchecked", "unused" })
	private JSONObject getCurrentOrderDetails() throws RestClientException, JSONException, IOException {

		params = (HashMap) getControleParameters();
		params.put("arg1", false);
		restResult = mSession.createHttpRequest(
				"http://" + getHost() + ":" + getPort() + (String) getObject("currentOrderDetailsRequest"), params, "POST");
		String currentOrderDetails=restResult.readInputStream();
		JSONObject json = new JSONObject(currentOrderDetails);
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings({ "unused", "unchecked" })
	private JSONObject removeAddressRequest() throws RestClientException, JSONException, IOException {

		restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("removeAddressInProfileRequest"), params,"POST");

		return new JSONObject(restResult.readInputStream());
	}




}
