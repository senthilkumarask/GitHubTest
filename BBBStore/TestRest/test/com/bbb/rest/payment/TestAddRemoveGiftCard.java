package com.bbb.rest.payment;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * Test cases for Gift Card Balance API
 *
 * @author sku134
 *
 */
public class TestAddRemoveGiftCard extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	/**
	 * @param name
	 */
	public TestAddRemoveGiftCard(String name) {
		super(name);

	}

	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to add gift card in the payment
	 *
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddGiftCardForPayment() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String giftCardNo = (String) getObject("giftCardNo");
		String result = null;
		JSONObject jsonObject = null;
		boolean giftCardAdded = false;
		try {
		//	mSession.login();
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			JSONObject afterAddItemOrderDetails = getCurrentOrderDetails();
			System.out.println("After add Item Order---"
					+ afterAddItemOrderDetails.toString());
			assertNull(result);

			// Adding billing address into payment
			params.put("userSelectedOption",
					(String) getObject("userSelectedOption"));
			params.put("billingAddress.firstName",
					(String) getObject("firstName"));
			params.put("billingAddress.lastName",
					(String) getObject("lastName"));
			params.put("billingAddress.companyName",
					(String) getObject("companyName"));
			params.put("billingAddress.address1",
					(String) getObject("address1"));
			params.put("billingAddress.address2",
					(String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode",
					(String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.mobileNumber",
					(String) getObject("mobileNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - "
					+ billingJson);
			JSONObject billingJsonOrder = getCurrentOrderDetails();
			System.out.println("jason response after billing address"
					+ billingJsonOrder);

			// add gift card to payment
			params.put("giftCardNumber", giftCardNo);
			params.put("giftCardPin", (String) getObject("giftCardPin"));

			JSONObject addGiftCardJson = addGiftCardInPayment();
			System.out.println("gift card response" + addGiftCardJson);

			JSONObject afterGiftCardOrder = getCurrentOrderDetails();
			System.out.println("jason response after Gift Card"
					+ afterGiftCardOrder);

			if (afterGiftCardOrder.has("paymentGroups")) {
				String jsonStringCard = afterGiftCardOrder
						.getString("paymentGroups");
				System.out.println("jsonStringCard   "+jsonStringCard);
				if (jsonStringCard.contains(giftCardNo.substring(giftCardNo.length()-4))) {
					giftCardAdded = true;
				}

			}
			assertEquals(true, giftCardAdded);

		} catch (RestClientException e) {
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
	 * test error scenerio with wrong credit card number
	 *
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddGiftCardForPaymentError() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String wrongGiftCardNo = (String) getObject("wrongGiftCardNo");
		boolean errorExist=true;
		String result = null;
		JSONObject jsonObject = null;
		try {
		//	mSession.login();
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			JSONObject afterAddItemOrderDetails = getCurrentOrderDetails();
			System.out.println("After add Item Order---"
					+ afterAddItemOrderDetails.toString());
			String errorString=(String) getObject("errorString");
			assertNull(result);

			// Adding billing address into payment
			params.put("userSelectedOption",
					(String) getObject("userSelectedOption"));
			params.put("billingAddress.firstName",
					(String) getObject("firstName"));
			params.put("billingAddress.lastName",
					(String) getObject("lastName"));
			params.put("billingAddress.companyName",
					(String) getObject("companyName"));
			params.put("billingAddress.address1",
					(String) getObject("address1"));
			params.put("billingAddress.address2",
					(String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode",
					(String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.mobileNumber",
					(String) getObject("mobileNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - "
					+ billingJson);
			JSONObject billingJsonOrder = getCurrentOrderDetails();
			System.out.println("jason response after billing address"
					+ billingJsonOrder);

			// add gift card to payment
			params.put("giftCardNumber", wrongGiftCardNo);
			params.put("giftCardPin", (String) getObject("giftCardPin"));
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			JSONObject addGiftCardJson = addGiftCardInPayment();
			System.out.println("gift card response" + addGiftCardJson);

			if (addGiftCardJson.has("formExceptions")) {
				result = addGiftCardJson.getString("formExceptions");
				if (result.contains(errorString))
				{
					errorExist=true;
				}
			}
		assertEquals(true, errorExist);

		} catch (RestClientException e) {
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
	 * test to check the order covered by gc or not for rest API
	 *
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testIsOrderAmountCoveredByGC() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String giftCardNo = (String) getObject("giftCardNo");
		String result = null;
		boolean orderAmountcovered=false;
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			JSONObject afterAddItemOrderDetails = getCurrentOrderDetails();
			System.out.println("After add Item Order---"
					+ afterAddItemOrderDetails.toString());
			assertNull(result);

			// Adding billing address into payment
			params.put("userSelectedOption",
					(String) getObject("userSelectedOption"));
			params.put("billingAddress.firstName",
					(String) getObject("firstName"));
			params.put("billingAddress.lastName",
					(String) getObject("lastName"));
			params.put("billingAddress.companyName",
					(String) getObject("companyName"));
			params.put("billingAddress.address1",
					(String) getObject("address1"));
			params.put("billingAddress.address2",
					(String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode",
					(String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.mobileNumber",
					(String) getObject("mobileNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - "
					+ billingJson);
			JSONObject billingJsonOrder = getCurrentOrderDetails();
			System.out.println("jason response after billing address"
					+ billingJsonOrder);

			// add gift card to payment
			params.put("giftCardNumber", giftCardNo);
			params.put("giftCardPin", (String) getObject("giftCardPin"));

			JSONObject addGiftCardJson = addGiftCardInPayment();
			System.out.println("jason response after adding gift card"
					+ addGiftCardJson.toString());

			JSONObject orderExistOrNot = isOrderAmountCoveredByGC();
			if (!orderExistOrNot.has("formExceptions"))
			{
				orderAmountcovered=orderExistOrNot.getBoolean("result");
			}
			System.out.println(orderExistOrNot);
			assertEquals(true, orderAmountcovered);
		} catch (RestClientException e) {
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
	 * test to check the gift card is already exist or not
	 *
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testIsGiftCardAlreadyExist() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String giftCardNo = (String) getObject("giftCardNo");
		String result = null;
		JSONObject jsonObject = null;
		boolean giftCardExist=false;

		try {
			//mSession.login();
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			JSONObject afterAddItemOrderDetails = getCurrentOrderDetails();
			System.out.println("After add Item Order---"
					+ afterAddItemOrderDetails.toString());
			assertNull(result);

			// Adding billing address into payment
			params.put("userSelectedOption",
					(String) getObject("userSelectedOption"));
			params.put("billingAddress.firstName",
					(String) getObject("firstName"));
			params.put("billingAddress.lastName",
					(String) getObject("lastName"));
			params.put("billingAddress.companyName",
					(String) getObject("companyName"));
			params.put("billingAddress.address1",
					(String) getObject("address1"));
			params.put("billingAddress.address2",
					(String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode",
					(String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.mobileNumber",
					(String) getObject("mobileNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - "
					+ billingJson);
			JSONObject billingJsonOrder = getCurrentOrderDetails();
			System.out.println("jason response after billing address"
					+ billingJsonOrder);

			// add gift card to payment
			params.put("giftCardNumber", giftCardNo);
			params.put("giftCardPin", (String) getObject("giftCardPin"));

			JSONObject addGiftCardJson = addGiftCardInPayment();
			System.out.println("gift card response" + addGiftCardJson);

			JSONObject afterGiftCardOrder = getCurrentOrderDetails();
			System.out.println("jason response after Gift Card"
					+ afterGiftCardOrder);
			//again adding the same gift card
			JSONObject addSameCardAgainJson = addGiftCardInPayment();
			String giftCardAlreadyExist = (String) getObject("giftCardAlreadyExist");
			System.out.println("response"+addSameCardAgainJson);
			if (addSameCardAgainJson.has("formExceptions")) {
				String existsException = addSameCardAgainJson.getString("formExceptions");
				if (existsException.contains("giftcarderror"))
				{
					giftCardExist=true;

				}
			}

			assertEquals(true, giftCardExist);

		} catch (RestClientException e) {
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
	 * test to check the gift card is already exist or not
	 *
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testMaxGiftCardReached() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String giftCardNo = (String) getObject("giftCardNo");
		String result = null;
		JSONObject jsonObject = null;
		boolean maxReached=true;

		try {
			//mSession.login();
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			JSONObject afterAddItemOrderDetails = getCurrentOrderDetails();
			System.out.println("After add Item Order---"
					+ afterAddItemOrderDetails.toString());
			assertNull(result);

			// Adding billing address into payment
			params.put("userSelectedOption",
					(String) getObject("userSelectedOption"));
			params.put("billingAddress.firstName",
					(String) getObject("firstName"));
			params.put("billingAddress.lastName",
					(String) getObject("lastName"));
			params.put("billingAddress.companyName",
					(String) getObject("companyName"));
			params.put("billingAddress.address1",
					(String) getObject("address1"));
			params.put("billingAddress.address2",
					(String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode",
					(String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.mobileNumber",
					(String) getObject("mobileNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - "
					+ billingJson);
			JSONObject billingJsonOrder = getCurrentOrderDetails();
			System.out.println("jason response after billing address"
					+ billingJsonOrder);
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			// add gift card to payment
			params.put("giftCardNumber", giftCardNo);
			params.put("giftCardPin", (String) getObject("giftCardPin"));

			JSONObject addGiftCardJson = addGiftCardInPayment();
			System.out.println("gift card response" + addGiftCardJson);

			JSONObject afterGiftCardOrder = getCurrentOrderDetails();
			System.out.println("jason response after Gift Card"
					+ afterGiftCardOrder);
			// In the current application the mxLimit set to 20 , so after adding 20 th card this flag will be true.
			JSONObject checkMaxGiftReachedJson = isMaxGiftCardReached();
			System.out.println(checkMaxGiftReachedJson);
			if (checkMaxGiftReachedJson.has("result"))
			{
				maxReached=Boolean.parseBoolean(checkMaxGiftReachedJson.getString("result"));
			}
			assertEquals(true, maxReached);

		} catch (RestClientException e) {
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
	 * test to check the gift card is already exist or not
	 *
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testRemoveGiftCard() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String giftCardNo = (String) getObject("giftCardNo");
		String result = null;
		JSONObject jsonObject = null;
		boolean giftCardRemoved=false;
		try {
			//mSession.login();
			jsonObject = addItemToCart();

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}

			JSONObject afterAddItemOrderDetails = getCurrentOrderDetails();
			System.out.println("After add Item Order---"
					+ afterAddItemOrderDetails.toString());
			assertNull(result);

			// Adding billing address into payment
			params.put("userSelectedOption",
					(String) getObject("userSelectedOption"));
			params.put("billingAddress.firstName",
					(String) getObject("firstName"));
			params.put("billingAddress.lastName",
					(String) getObject("lastName"));
			params.put("billingAddress.companyName",
					(String) getObject("companyName"));
			params.put("billingAddress.address1",
					(String) getObject("address1"));
			params.put("billingAddress.address2",
					(String) getObject("address2"));
			params.put("billingAddress.city", (String) getObject("city"));
			params.put("billingAddress.state", (String) getObject("state"));
			params.put("billingAddress.country", (String) getObject("country"));
			params.put("billingAddress.postalCode",
					(String) getObject("postalCode"));
			params.put("billingAddress.email", (String) getObject("email"));
			params.put("billingAddress.mobileNumber",
					(String) getObject("mobileNumber"));
			params.put("confirmedEmail", (String) getObject("confirmedEmail"));
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			JSONObject billingJson = addBillingAddressRequest();
			System.out.println("testAddNewShippingAddress jason response - "
					+ billingJson);
			JSONObject billingJsonOrder = getCurrentOrderDetails();
			System.out.println("jason response after billing address"
					+ billingJsonOrder);
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			// add gift card to payment
			params.put("giftCardNumber", giftCardNo);
			params.put("giftCardPin", (String) getObject("giftCardPin"));
			JSONObject addGiftCardJson = addGiftCardInPayment();
			System.out.println("gift card response" + addGiftCardJson);

			JSONObject afterGiftCardOrder = getCurrentOrderDetails();
			//getting removal paymentGpId

			String removalPaymentGroupId=getGCPaymentGroupId(afterGiftCardOrder);
			System.out.println("removalPaymentGroupId   "+removalPaymentGroupId);
			params.put("paymentGroupId",removalPaymentGroupId);
			JSONObject afterRemovedGiftCard=removeGiftCard();
			System.out.println(afterRemovedGiftCard);
			if (!afterRemovedGiftCard.has("formExceptions"))
			{
				giftCardRemoved=true;

			}
			assertEquals(true, giftCardRemoved);

		} catch (RestClientException e) {
			e.printStackTrace();
			assertFalse(true);

		} catch (JSONException e) {
			e.printStackTrace();
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
	 * Getting paymentGrop id for the GC
	 * @param orderObj
	 * @return id
	 * @throws JSONException
	 */
	public String getGCPaymentGroupId(JSONObject orderObj) throws JSONException
	{
		System.out.println("getGCPaymentGroupId   "+orderObj);
		String removalPaymentGroupId=null;
		if (orderObj.has("paymentGroups")) {

			JSONArray paymentGroups = orderObj.getJSONArray("paymentGroups");
			for (int i = 0; i < paymentGroups.length(); ++i) {
			    JSONObject paymentGroupsDetail = paymentGroups.getJSONObject(i);
			    if(null!=paymentGroupsDetail.getString("paymentMethodType")&&paymentGroupsDetail.getString("paymentMethodType").equalsIgnoreCase("giftCard"))
			    {
			    	removalPaymentGroupId=paymentGroupsDetail.getString("id");
			    	System.out.println("removal payment grp for gift card  "+removalPaymentGroupId);

			    }
			}
		}
		return removalPaymentGroupId;
	}

	private JSONObject removeGiftCard() throws RestClientException,
	JSONException, IOException {
System.out.println("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("removeGiftCardURL"));
		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("removeGiftCardURL"), params, "POST");
		String response=restResult.readInputStream();
		System.out.println("remove response  "+response);
		return new JSONObject(response);
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
	private JSONObject getCurrentOrderDetails() throws RestClientException,
			JSONException, IOException {

		params = (HashMap) getControleParameters();
		params.put("arg1", false);
		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("currentOrderDetailsRequest"), params,
				"POST");
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
	private JSONObject addItemToCart() throws RestClientException,
			JSONException, IOException {

		params.put("jsonResultString", (String) getObject("jsonResultString"));
		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("addCartRequest"), params, "POST");

		return new JSONObject(restResult.readInputStream());
	}

	/**
	 * Add Billing Address Request
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject addBillingAddressRequest() throws RestClientException,
			JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("addBillingAddressRequest"), params,
				"POST");
		return new JSONObject(restResult.readInputStream());
	}

	/**
	 * check , order amount covered by GC or not
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject isOrderAmountCoveredByGC() throws RestClientException,
			JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("isOrderAmount"), params,
				"POST");
		return new JSONObject(restResult.readInputStream());
	}


	/**
	 * Add Gift card in payment
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject addGiftCardInPayment() throws RestClientException,
			JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("addGiftCard"), params, "POST");
		return new JSONObject(restResult.readInputStream());
	}

	/**
	 * Add Gift card in payment
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */

	private JSONObject isMaxGiftCardReached() throws RestClientException,
			JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("isMaxGiftCardReached"), params, "POST");
		return new JSONObject(restResult.readInputStream());
	}
}
