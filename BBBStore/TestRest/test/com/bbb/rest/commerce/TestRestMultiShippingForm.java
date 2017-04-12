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

public class TestRestMultiShippingForm extends BaseTestCase {

	public TestRestMultiShippingForm(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;

	/**
	 * To test the success scenarios for online to Bopus & Bopus to online flow
	 *
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testMultiShipFormSuccess() throws JSONException, RestClientException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);

		String result = null;
		JSONArray jsonResponseObjArray = null;
		JSONObject jsonShpGrp = null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			JSONObject jsonResponse = addItemToCart();
			checkResponseForFormException(result, jsonResponse);

			JSONObject jsonResponseObj = getCurrentOrderDetails();
			System.out.println("Before OnlineToBopus request" + jsonResponseObj);

			jsonResponseObj = multiShipFormRequest();

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout faield", true);
			}
		}

	}


	/**
	 * Check for form exceptions.
	 *
	 * @param result
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void checkResponseForFormExceptionFailure(String result, JSONObject jsonResponse) throws JSONException {
		if (jsonResponse.has("formExceptions")) {
			result = jsonResponse.getString("formExceptions");
		}

		assertEquals(true, result.contains("Invalid Input"));

	}

	/**
	 * Check for form exceptions.
	 *
	 * @param result
	 * @param jsonResponse
	 * @throws JSONException
	 */
	private void checkResponseForFormException(String result, JSONObject jsonResponse) throws JSONException {
		if (jsonResponse.has("formExceptions")) {
			result = jsonResponse.getString("formExceptions");
		}
		assertNull(result);
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



	/**
	 * Add item to cart.
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
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
