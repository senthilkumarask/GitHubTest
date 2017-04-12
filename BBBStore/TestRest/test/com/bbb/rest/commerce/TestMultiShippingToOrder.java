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

public class TestMultiShippingToOrder  extends BaseTestCase  {

	public TestMultiShippingToOrder(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	HashMap params = null;
	RestResult restResult = null;



	 /** Test AddMultiShippingToOrder
	 *
	 * @throws JSONExceptiontestcr
	 * @throws IOException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testAddMultiShippingToOrder()   throws RestClientException, IOException, JSONException {

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
	        params.put("value.login", (String) getObject("login"));
	        params.put("value.password", (String) getObject("password"));
	        jsonObject = loginRequestRestCall();

	        params.put("atg-rest-return-form-handler-properties", "true");
	        if(jsonObject.has("formExceptions")){
	        	result = jsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
 			 }
	        assertNull(result);
			
			
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
			JSONObject cisiJsonObject = getCIsiItems();
			if (cisiJsonObject.has("formExceptions")) {
				result = cisiJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);
			
			// Add new address
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			jsonObject = restAddNewAddress();
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			    System.out.println(result);
			}
			assertNull(result);

			// Get All Addresses
	        JSONObject allAddressJson = getAllAddresses();
	        if (allAddressJson.getJSONObject("newAddrContainer").has("addressMap")) {
				result = allAddressJson.getJSONObject("newAddrContainer").getString("addressMap");
			    System.out.println(allAddressJson);
			}
			assertNotNull(result);
			
	        Iterator<String> addressKeys = allAddressJson.getJSONObject("newAddrContainer").getJSONObject("addressMap").keys();
	        String shipGroupName = addressKeys.next();
	        
	        String multiResult = null;
	        JSONObject multishipJsonObject = addMultishipToOrder(cisiJsonObject, shipGroupName);
	        if(multishipJsonObject.has("formExceptions")) {
	        	multiResult = multishipJsonObject.getString("formExceptions");
 				System.out.println("formExceptions=" + result);
 				System.out.println(multishipJsonObject);
 			 }
	        assertNull(multiResult);
	        
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
	 *  Test AddMultiShippingToOrder Error
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAddMultiShippingToOrderError() throws RestClientException, IOException, JSONException{

		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String result = null;
		JSONObject jsonObject = null;

		try {
			//mSession.login();
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
			JSONObject cisiJsonObject = getCIsiItems();
			if (cisiJsonObject.has("formExceptions")) {
				result = cisiJsonObject.getString("formExceptions");
 				System.out.println("formExceptions="+result);
			}
			assertNull(result);
			
			// Sending blank/invalid shippingGroup Name
	        String shipGroupName = "";
	        
	        JSONObject multishipJsonObject = addMultishipToOrder(cisiJsonObject, shipGroupName);
	        if(multishipJsonObject.has("formExceptions")) {
	        	result = multishipJsonObject.getString("formExceptions");
 				System.out.println("formExceptions=" + result);
 				System.out.println(multishipJsonObject);
 			 }
			assertNotNull(result);

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
	 * @return
	 * @throws RestClientException
	 * @throws JSONException
	 * @throws IOException
	 */
	private JSONObject getCIsiItems() throws RestClientException, JSONException, IOException {
		params = (HashMap) getControleParameters();
		params.put("arg1", true);
		RestResult restResult = null;
		JSONObject json = null;
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getCisiItemsRequest"), params, "POST");
		json = new JSONObject(restResult.readInputStream());
		System.out.println(json);
		return json;
	}
	
	/**
	 *  Add new Address to Address container
	 * @return
	 * @throws RestClientException
	 * @throws JSONException
	 * @throws IOException
	 */
	 private JSONObject restAddNewAddress() throws RestClientException, JSONException, IOException {
		params = (HashMap) getControleParameters();
		params.put("address.firstName", getObject("firstName"));
		params.put("address.lastName", getObject("lastName"));
		params.put("address.address1", getObject("address1"));
		params.put("address.address2", getObject("address2"));
		params.put("address.city", getObject("city"));
		params.put("address.state", getObject("state"));
		params.put("address.postalCode", getObject("postalCode"));
		params.put("cisiIndex", "0");
		params.put("address.companyName", getObject("companyName"));
		params.put("address.country", getObject("country"));
		params.put("address.phoneNumber", getObject("phoneNumber"));
		params.put("address.alternatePhoneNumber", "");
		params.put("address.email", getObject("email"));
		RestResult restResult = null;
		JSONObject json = null;
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("restAddNewAddress"), params, "POST");
		json = new JSONObject(restResult.readInputStream());
		System.out.println(json);
		return json;
	}
	/**
	 * Get all addresses from the address container
	 * @return
	 * @throws RestClientException
	 * @throws JSONException
	 * @throws IOException
	 */
	private JSONObject getAllAddresses() throws RestClientException, JSONException, IOException {
		params = (HashMap) getControleParameters();
		RestResult restResult = null;
		JSONObject json = null;
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getMultishipAddresses"), params, "POST");
		json = new JSONObject(restResult.readInputStream());
		System.out.println(json);
		return json;
	}

	
	/**
	 * Create shipInfo Json String
	 * @param index
	 * @param shipGroupName
	 * @param atgResponseObj
	 * @return
	 * @throws JSONException 
	 */
	private String createShipInfoJsonString(String index, String shipGroupName, JSONObject atgResponseObj) throws JSONException {
		String jsonString = null;
			String commerceItemId = atgResponseObj.getJSONObject(index).getJSONObject("commerceItem").getString("id");
			jsonString = "{\"atg-rest-class-type\":\"com.bbb.common.vo.CommerceItemShipInfoVO\"," 
					+ "\"shippingMethod\": \"" + getObject("shippingOption3g") + "\",\"commerceItemId\": \"" + commerceItemId + "\",\"shippingGroupName\": \"" + shipGroupName + "\"}";
		return jsonString;
	}

	 /**
	  *  Add multishipping information to order
	  * @param cisiJsonObject
	  * @param shipGroupName
	  * @return
	  * @throws JSONException
	  * @throws RestClientException
	  * @throws IOException
	  */
	private JSONObject addMultishipToOrder(JSONObject cisiJsonObject, String shipGroupName) throws JSONException, RestClientException, IOException {
	    params = (HashMap) getControleParameters();
		JSONObject atgResponseObj = null;
		atgResponseObj = cisiJsonObject.getJSONObject("atgResponse");
		Iterator<String> indexIter = atgResponseObj.keys();
    	for (;indexIter.hasNext();) {
			String index = indexIter.next();
			String shipInfoItem = createShipInfoJsonString(index, shipGroupName, atgResponseObj);
			params.put("sGCommerceItemShipInfoVO." + index, shipInfoItem);
		}
    	
		RestResult restResult = null;
		JSONObject json = null;
		restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("addMultipleShippingToOrder"), params, "POST");
		json = new JSONObject(restResult.readInputStream());
		System.out.println(json);
		return json;
	}



}
