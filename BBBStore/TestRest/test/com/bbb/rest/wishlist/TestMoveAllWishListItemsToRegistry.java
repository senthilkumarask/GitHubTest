package com.bbb.rest.wishlist;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestMoveAllWishListItemsToRegistry extends BaseTestCase {
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestMoveAllWishListItemsToRegistry(String name) {
		super(name);

	}
	/**
	 * test to move all wish list items to registry for success scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveAllWishListItemsToRegistry() throws RestClientException, IOException, JSONException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		try {
			params = (HashMap) getControleParameters();
			//login user
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String loginresponse = pd.readInputStream();
			if (loginresponse != null) {
				JSONObject json = new JSONObject(loginresponse);
				System.out.println("Login Status : "+json.toString());
			}
			
			//add items to wish list
			String jsonResultString=(String) getObject("jsonResultStringMoveToWishList1");
			this.addItem(mSession, jsonResultString);
			String jsonResultString1=(String) getObject("jsonResultStringMoveToWishList2");
			this.addItem(mSession, jsonResultString1);
			
			//Move all wish list items to registry
			params.put("atg-rest-return-form-handler-properties", true);
			params.put("atg-rest-show-rest-paths", false);
			params.put("registryId", (String) getObject("registryId"));
			RestResult pd1 = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("moveAllWishListItemsToRegistry"), params,"POST");
			String moveResponse =pd1.readInputStream();
			System.out.println("Move Response: "+moveResponse);
			JSONObject json = new JSONObject(moveResponse);
			
			if(json.has("component"))
			{
			    JSONObject errorMap = json.getJSONObject("component").getJSONObject("moveAllWishListItemsFailureResult");
			    int length = errorMap.length();
			    assertEquals(0,length);
			}
			
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			String responseData = pd2.readInputStream();
			System.out.println("Items : " + responseData);
			JSONObject json1 = new JSONObject(responseData);
			 if(json1.has("wishListItems"))
			{
				String result = json1.getString("wishListItems");
				System.out.println("Result " + result);
				assertNotNull(result);
			}
			}
			finally{ 
				try {
					mSession.logout();
				} catch (RestClientException e) {
					e.printStackTrace();
				}
			 
			}

	}
	
	/**
	 * test to move all wish list items to registry for error scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveAllWishListItemsToRegistryError() throws RestClientException, IOException, JSONException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		try {
			params = (HashMap) getControleParameters();
			
			//login user
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String loginResponse = pd.readInputStream();
			if (loginResponse != null) {
				JSONObject json = new JSONObject(loginResponse);
				System.out.println("Login Status : "+json.toString());
			}
			
			//add items to wish list
			String jsonResultString=(String) getObject("jsonResultStringMoveToWishList1");
			this.addItem(mSession, jsonResultString);
			String jsonResultString1=(String) getObject("jsonResultStringMoveToWishList2");
			this.addItem(mSession, jsonResultString1);
			
			//Move all wish list items to registry
			params.put("atg-rest-return-form-handler-properties", true);
			params.put("atg-rest-show-rest-paths", false);
			params.put("registryId", (String) getObject("registryId"));
			RestResult pd1 = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("moveAllWishListItemsToRegistry"), params,"POST");
			String moveResponse =pd1.readInputStream();
			System.out.println("Move Response: "+moveResponse);
			JSONObject json = new JSONObject(moveResponse);
			
			if(json.has("component"))
			{
			    JSONObject errorMap = json.getJSONObject("component").getJSONObject("moveAllWishListItemsFailureResult");
			    int length = errorMap.length();
			    assertEquals(2,length);
			}
			
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			String responseData = pd2.readInputStream();
			System.out.println("Items : " + responseData);
			JSONObject json1 = new JSONObject(responseData);
			JSONArray jsonResponseObjArray = new JSONArray(json1.getString("wishListItems"));
			int itemsInWishList = jsonResponseObjArray.length();
			assertEquals(2, itemsInWishList);
			}
			finally{ 
				try {
					mSession.logout();
				} catch (RestClientException e) {
					e.printStackTrace();
				}
			 
			}

	}
	
	/**
	 * Method to add item to wishlist
	 * @param session
	 * @param jsonResultString
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private void addItem(RestSession session, String jsonResultString) throws RestClientException, IOException, JSONException{ 
		params.put("jsonResultString", jsonResultString);
		RestResult pd = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
		String addResponse = pd.readInputStream();
		System.out.println("Add Response: " + addResponse);
		JSONObject json = new JSONObject(addResponse);
		String result = null;
		if(json.has("formExceptions")){
				result = json.getString("formExceptions");
		}
		assertNull(result);	
	}
	
	/**
	 * test to move all wish list items to registry for non-logged in user
	 * @throws JSONException
	 * @throws RestClientException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveAllWishListItemsToRegistryNonLoggedInUser() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		try
		{
			params = (HashMap) getControleParameters();
			
			//Move all wish list items to registry
			params.put("atg-rest-return-form-handler-properties", true);
			params.put("atg-rest-show-rest-paths", false);
			params.put("registryId", (String) getObject("registryId"));
			RestResult pd = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("moveAllWishListItemsToRegistry"), params,"POST");
			String moveResponse =pd.readInputStream();
			System.out.println("Move Response: "+moveResponse);
		}
		catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally{ 
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		 
		}
	}
}


