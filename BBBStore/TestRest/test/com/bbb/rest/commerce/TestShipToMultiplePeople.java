package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;








import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestShipToMultiplePeople  extends BaseTestCase  {

	public TestShipToMultiplePeople(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	HashMap params = null;
	RestResult restResult = null;


	/**
	 * Test Split current Item
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testSplitCurrentItem()   throws RestClientException, IOException, JSONException {

		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();

			// Get current order details
	        getCurrentOrderDetails();

	        // add item to cart
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addItemToCart();
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);

			// Get CommerceItemShippingInfo objects
			boolean reinitializeContainers = true;
			JSONObject cisiJsonObject = getCIsiItems(reinitializeContainers);
			if (cisiJsonObject.has("formExceptions")) {
				result = cisiJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);
			
			JSONObject atgResponseObj = cisiJsonObject.getJSONObject("atgResponse");
			String index = (String) atgResponseObj.keys().next();
			JSONObject splitJsonObject = splitCurrentItem(index);
			if (splitJsonObject.has("formExceptions")) {
				result = splitJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);
			
			// Get CommerceItemShippingInfo objects
			reinitializeContainers = false;
			cisiJsonObject = getCIsiItems(reinitializeContainers);
			if (cisiJsonObject.has("formExceptions")) {
				result = cisiJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);
			
			atgResponseObj = cisiJsonObject.getJSONObject("atgResponse");
			int  cisiItemCount = atgResponseObj.length();
			
			// since three items were added to the cart, length should 3 after split
			assertTrue(atgResponseObj.length() == 3);
	        
		} finally {
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
	 * Test Split current item error
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testSplitCurrentItemError() throws RestClientException, IOException, JSONException{

		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();

			// Get current order details
	        getCurrentOrderDetails();

	        // add item to cart
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = addItemToCart();
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);

			// Get CommerceItemShippingInfo objects
			boolean reinitializeContainers = true;
			JSONObject cisiJsonObject = getCIsiItems(reinitializeContainers);
			if (cisiJsonObject.has("formExceptions")) {
				result = cisiJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);
			
			JSONObject atgResponseObj = cisiJsonObject.getJSONObject("atgResponse");
			// Passing invalid/blank index for cisiIndex to split to
			String index ="";
			JSONObject splitJsonObject = splitCurrentItem(index);
			if (splitJsonObject.has("formExceptions")) {
				result = splitJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNotNull(result);
			result = null;
			
			// Get CommerceItemShippingInfo objects
			reinitializeContainers = false;
			cisiJsonObject = getCIsiItems(reinitializeContainers);
			if (cisiJsonObject.has("formExceptions")) {
				result = cisiJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);
			
			atgResponseObj = cisiJsonObject.getJSONObject("atgResponse");
			int  cisiItemCount = atgResponseObj.length();
			
			// since split was not performed, length of CISIs should still be one
			assertTrue(cisiItemCount == 1);
	        
		} finally {
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
	private JSONObject loginRequestRestCall() throws RestClientException, JSONException, IOException {

		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
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
	 * Get All CommerceItemShippingInfo objects from the ShippingInfo container
	 * @param reinitializeContainers 
	 * @return
	 * @throws RestClientException
	 * @throws JSONException
	 * @throws IOException
	 */
	private JSONObject getCIsiItems(boolean reinitializeContainers) throws RestClientException, JSONException, IOException {
		params = (HashMap) getControleParameters();
		params.put("arg1", reinitializeContainers);
		RestResult restResult = null;
		JSONObject json = null;
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getCisiItemsRequest"), params, "POST");
		json = new JSONObject(restResult.readInputStream());
		System.out.println(json);
		return json;
	}
	
	/**
	 * Split current item rest call
	 * @param index
	 * @return
	 * @throws RestClientException
	 * @throws JSONException
	 * @throws IOException
	 */
	private JSONObject splitCurrentItem(String index) throws RestClientException, JSONException, IOException {
		params.put("cisiIndex", index);
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("splitCurrentItem"), params, "POST");
		JSONObject jsonObject = new JSONObject(restResult.readInputStream());
		System.out.println(jsonObject);
		return jsonObject;
	}
}
