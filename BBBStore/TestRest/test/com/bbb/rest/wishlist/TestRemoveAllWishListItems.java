package com.bbb.rest.wishlist;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRemoveAllWishListItems extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	
	/**
	 * @param name
	 */
	public TestRemoveAllWishListItems(String name) {
		super(name);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testRemoveAllWishListItems() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		try{
		//login user
			params = (HashMap) getControleParameters();
			params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData = pd.readInputStream();
			System.out.println("Login Response Data");
			System.out.println("responseData: " + responseData);
			
			//Move items to wishlist
			System.out.println("Add Item To WishList Response Data");
			
			String jsonResultString=(String) getObject("jsonResultStringAddToWishlist");
			this.addItem(mSession, jsonResultString);
			jsonResultString=(String) getObject("jsonResultStringAddToWishlist1");
			this.addItem(mSession, jsonResultString);
			jsonResultString=(String) getObject("jsonResultStringAddToWishlist2");
			this.addItem(mSession, jsonResultString);
			jsonResultString=(String) getObject("jsonResultStringAddToWishlist3");
			this.addItem(mSession, jsonResultString);
			
			//Remove All items from wishlist
			params.put("atg-rest-return-form-handler-properties", true);
			params.put("atg-rest-show-rest-paths", false);
			RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("removeWishListRequest"), params,"POST");
			String responseData1 = pd1.readInputStream();
			System.out.println("Remove Items Response Data");
			System.out.println("Response Data: " + responseData1);
			JSONObject json1 = new JSONObject(responseData1);
			
			if(json1.has("component"))
			{
			    JSONObject errorMap = json1.getJSONObject("component").getJSONObject("removeAllWishListItemsFailureResult");
			    int length = errorMap.length();
			    assertEquals(0,length);
			}
			
			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			String responseData4 = pd3.readInputStream();
			System.out.println("Items : " + responseData4);
			JSONObject json2 = new JSONObject(responseData4);
			if(json2.has("wishListItems"))
			{
				String result = json2.getString("wishListItems");
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testRemoveAllWishListItemsNonLoggedInUser() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		//login user
		params = (HashMap) getControleParameters();
		
		try
		{
		//Move items to wishlist
		
		String jsonResultString=(String) getObject("jsonResultStringAddToWishlist");
		this.addItem(mSession, jsonResultString);
		jsonResultString=(String) getObject("jsonResultStringAddToWishlist1");
		this.addItem(mSession, jsonResultString);
		jsonResultString=(String) getObject("jsonResultStringAddToWishlist2");
		this.addItem(mSession, jsonResultString);
		jsonResultString=(String) getObject("jsonResultStringAddToWishlist3");
		this.addItem(mSession, jsonResultString);
		
		//Remove All items from wishlist
		params.put("atg-rest-return-form-handler-properties", true);
		params.put("atg-rest-show-rest-paths", false);
		RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("removeWishListRequest"), params,"POST");
		String responseData1 = pd1.readInputStream();
		System.out.println("Remove Items Response Data");
		System.out.println("Response Data: " + responseData1);
		
		 try {
				mSession.logout();
			  } catch (RestClientException e) {
				e.printStackTrace();
			}
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
	}
	/**
	 * Method to add wish list item for a user
	 * @param session
	 * @param jsonResultString
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void addItem(RestSession session, String jsonResultString) throws RestClientException, IOException, JSONException{
		
		params.put("jsonResultString", jsonResultString);
		RestResult pd = null;
		
		pd = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
		String responseData = pd.readInputStream();
		
		System.out.println("Response Data: " + responseData);
		JSONObject json = new JSONObject(responseData);
		String result = null;
		if(json.has("formExceptions")){
				result = json.getString("formExceptions");
		}
		assertNull(result); 
	}
}
