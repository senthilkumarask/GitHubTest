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

public class TestRestAddRemoveGiftMessage extends BaseTestCase {

	public TestRestAddRemoveGiftMessage(String name) {
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
	public void testRemoveGiftMessagesLoggedInFlow() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		JSONObject jsonObject = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();

			jsonObject = loginRequestRestCall();

			jsonObject = getCurrentOrderDetails();
			System.out.println("Before RemoveGiftMessages Request-->:" + jsonObject);

			// Get gift message input string
			String giftMessageInputString = getInputString(jsonObject);
			params.put("giftMessageInput", giftMessageInputString);

			params.put("atg-rest-return-form-handler-properties", "true");
			// add remove  gift message request
			jsonObject = addRemoveGiftMessage();
			System.out.println(jsonObject);

			jsonObject = getCurrentOrderDetails();
			System.out.println("After RemoveGiftMessages Request-->:" + jsonObject);
			JSONArray jsonArrayShpGrp = jsonObject.getJSONArray("shippingGroups");
			JSONObject shpGrpObj = jsonArrayShpGrp.getJSONObject(0);
			String shiGrp1Msg = shpGrpObj.getString("giftWrapMessage");
			assertEquals(shiGrp1Msg, "null");

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
	public void testAddGiftMessagesLoggedInFlow() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			params = (HashMap) getControleParameters();

			jsonObject = loginRequestRestCall();

			jsonObject = getCurrentOrderDetails();
			System.out.println("Before AddRemoveGiftMessages Request-->:" + jsonObject);

			// Get gift message input string
			String giftMessageInputString = getInputString(jsonObject);
			params.put("giftMessageInput", giftMessageInputString);
			params.put("atg-rest-return-form-handler-properties", "true");
			// add remove  gift message request
			jsonObject = addRemoveGiftMessage();
			System.out.println(jsonObject);

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNull(result);

			jsonObject = getCurrentOrderDetails();
			System.out.println("After AddRemoveGiftMessages Request-->:" + jsonObject);

			JSONArray jsonArrayShpGrp = jsonObject.getJSONArray("shippingGroups");
			JSONObject shpGrpObj = jsonArrayShpGrp.getJSONObject(0);

			String shiGrp1Msg = shpGrpObj.getString("giftWrapMessage");

			assertEquals(shiGrp1Msg, (String) getObject("giftMessage1"));

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
	public void testAddGiftMessageTransientUser() throws JSONException, RestClientException, IOException {
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
			JSONObject json = addItemToCart();
			if (json.has("formExceptions")) {
				result = json.getString("formExceptions");
			}
			assertNull(result);
			JSONObject jsonResponseObj = getCurrentOrderDetails();
			System.out.println(jsonResponseObj);
			System.out.println("Before testAddGiftMessageTransientUser" + jsonResponseObj);

			JSONArray jsonResponseObjArray = new JSONArray(jsonResponseObj.getString("shippingGroups"));
			JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
			String shippingGrpId = json1.getString("id");
			assertNotNull(shippingGrpId);

			params.put("giftMessageInput", shippingGrpId + ":" + (String) getObject("giftMessage1") + ":" + (String) getObject("giftWrapFlag1")+":" + (String) getObject("giftWrapPackFlag1"));
			params.put("atg-rest-return-form-handler-properties", "true");
			// add remove  gift message request
			jsonObject = addRemoveGiftMessage();
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNull(result);

			jsonResponseObj = getCurrentOrderDetails();
			System.out.println("After testAddGiftMessageTransientUser" + jsonResponseObj);

			JSONArray jsonArrayShpGrp = jsonResponseObj.getJSONArray("shippingGroups");
			JSONObject shpGrpObj = jsonArrayShpGrp.getJSONObject(0);

			String shiGrp1Msg = shpGrpObj.getString("giftWrapMessage");

			assertEquals(shiGrp1Msg, (String) getObject("giftMessage1"));




		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}



	@SuppressWarnings("unchecked")
	public void testGiftWrapSKUTransientUser() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;
		try {
			//mSession.login();
			jsonObject = getWrapSkuDetails();
			System.out.println("After testGiftWrapSKUTransientUser" + jsonObject);
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
	 * Test Add Remove Gift Message Failure.
	 *
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddRemoveGiftMessagesFailure() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
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

			// Get gift message input string
			String giftMessageInputString = ":;";
			params.put("giftMessageInput", giftMessageInputString);

			// add remove  gift message request
			jsonObject = addRemoveGiftMessage();
			System.out.println(jsonObject);

			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNotNull(result);

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}

	/**
	 * Creates Input String.
	 *
	 * @param jsonObject
	 * @return
	 * @throws JSONException
	 */
	private String getInputString(JSONObject jsonObject) throws JSONException {
		JSONArray jsonArray = jsonObject.getJSONArray("shippingGroups");
		String ShippingGrp1 = jsonArray.getString(0);
		String shippingGrpId2 = null;
		JSONObject jsonObjectSG1 = new JSONObject(ShippingGrp1);
		String giftMessageInputString = null;
		if(jsonArray.length() >1){
			String ShippingGrp2 = jsonArray.getString(1);
			JSONObject jsonObjectSG2 = new JSONObject(ShippingGrp2);
			shippingGrpId2 = jsonObjectSG2.getString("id");
			String shippingGrpId1 = jsonObjectSG1.getString("id");

			System.out.println("shippingGrpId1" + shippingGrpId1 + " shippingGrpId2" + shippingGrpId2);
			giftMessageInputString = shippingGrpId1 + ":" + (String) getObject("giftMessage1") + ":" + (String) getObject("giftWrapFlag1")+ ":" + (String) getObject("giftWrapPackFlag1") + ";"
					+ shippingGrpId2 + ":" + (String) getObject("giftMessage2") + ":" + (String) getObject("giftWrapFlag2")+":" + (String) getObject("giftWrapPackFlag2");
		} else {
			String shippingGrpId1 = jsonObjectSG1.getString("id");
			giftMessageInputString = shippingGrpId1 + ":" + (String) getObject("giftMessage1") + ":" + (String) getObject("giftWrapFlag1")+ ":" + (String) getObject("giftWrapPackFlag1") + ";";
		}



		return giftMessageInputString;

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
	 * Get current order details.
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private JSONObject addRemoveGiftMessage() throws RestClientException, JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addRemoveGiftMessage"),
				params, "POST");
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


}
