package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;


public class TestMoveAllWishListItemsToCart extends BaseTestCase{

	
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestMoveAllWishListItemsToCart(String name) {
		super(name);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveAllWishListItemsToCart() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		try{
		params = (HashMap) getControleParameters();
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData = pd.readInputStream();
		System.out.println("Login Response Data");
		System.out.println("responseData: " + responseData);
		
		
		//Move items to wishlist
		String jsonResultString=(String) getObject("jsonResultStringAddToWishlist");
		this.addItem(mSession, jsonResultString);
		String jsonResultString1=(String) getObject("jsonResultStringAddToWishlist1");
		this.addItem(mSession, jsonResultString1);
		
		
		//Move wishlist items to cart
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-properties", true);
		params.put("atg-rest-show-rest-paths", false);
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		String responseData3 = pd2.readInputStream();
		System.out.println("Move to Cart Response Data");
		System.out.println("responseData3 : " + responseData3);
		JSONObject json = new JSONObject(responseData3);
		
		if(json.has("component"))
		{
		    JSONObject errorMap = json.getJSONObject("component").getJSONObject("moveAllItemFailureResult");
		    int length = errorMap.length();
		    assertEquals(0,length);
		}
		
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
		String responseData4 = pd3.readInputStream();
		System.out.println("Items : " + responseData4);
		JSONObject json1 = new JSONObject(responseData4);
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveAllWishListItemsToCartError() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		try{
		params = (HashMap) getControleParameters();
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData = pd.readInputStream();
		System.out.println("Login Response Data");
		System.out.println("responseData: " + responseData);
		
		
		//Move items to wishlist
		String jsonResultString=(String) getObject("jsonResultStringAddToWishlist");
		this.addItem(mSession, jsonResultString);
		String jsonResultString1=(String) getObject("jsonResultStringAddToWishlist1");
		this.addItem(mSession, jsonResultString1);
		
		
		//Move wishlist items to cart
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-properties", true);
		params.put("atg-rest-show-rest-paths", false);
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		String responseData3 = pd2.readInputStream();
		System.out.println("Move to Cart Response Data");
		System.out.println("responseData3 : " + responseData3);
		JSONObject json = new JSONObject(responseData3);
	
		
		if(json.has("component"))
		{
		    JSONObject map = json.getJSONObject("component").getJSONObject("moveAllItemFailureResult");
		    int length = map.length();
		    assertEquals(1,length);
		}
		
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
		String responseData4 = pd3.readInputStream();
		System.out.println("Items : " + responseData4);
		JSONObject json1 = new JSONObject(responseData4);
		JSONArray jsonResponseObjArray = new JSONArray(json1.getString("wishListItems"));
		int itemsInWishList = jsonResponseObjArray.length();
		//assertEquals(1, itemsInWishList);
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
	public void testMoveAllWishListItemsToCartNonLoggedInUser() throws JSONException, RestClientException, IOException{
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
			String jsonResultString1=(String) getObject("jsonResultStringAddToWishlist1");
			this.addItem(mSession, jsonResultString1);
			
			RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			JSONObject json = new JSONObject(pd1.readInputStream());
			System.out.println("Saved Item before move to cart ="+json.toString());
			
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", true);
			params.put("atg-rest-show-rest-paths", false);
			params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
			params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
			String responseData3 = pd2.readInputStream();
			System.out.println("Move to Cart Response Data");
			System.out.println("responseData3 : " + responseData3);
			
			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			JSONObject json3 = new JSONObject(pd3.readInputStream());
			System.out.println("Saved Item after move to cart ="+json3.toString());
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
	 * Method to add wish list item for a user
	 * @param login
	 * @param password
	 * @param jsonResultString
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void addItem(RestSession session, String jsonResultString) throws RestClientException, IOException, JSONException{
		
		params.put("jsonResultString", jsonResultString);
		RestResult pd3 = null;
		
		pd3 = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
		JSONObject json = new JSONObject(pd3.readInputStream());
		System.out.println("add to wishlist json ="+json.toString());
		String result = null;
		if(json.has("formExceptions")){
				result = json.getString("formExceptions");
		}
		assertNull(result); 
		
		
			

	}
}

