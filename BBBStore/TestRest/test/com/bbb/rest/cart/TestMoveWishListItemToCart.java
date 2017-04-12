package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestMoveWishListItemToCart extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestMoveWishListItemToCart(String name) {
		super(name);
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveWishListItemToCart() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		//login user
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData = pd.readInputStream();
		System.out.println("Login Response Data");
		if (responseData != null) {
			System.out.println("responseData: " + responseData);
	     	JSONObject json = new JSONObject(responseData);
	     	System.out.println("Login Status : "+json.toString());
		}
		
		//add item to wishlist
		
		String jsonResultString1=(String) getObject("jsonResultStringAddToWishlist");
		this.addItem(mSession, jsonResultString1);
		
		//get wishlist items
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String responseData2=pd3.readInputStream();
		System.out.println("Get Wishlist Items Response Data");
		assertNotNull(responseData2);
		JSONObject json = new JSONObject(responseData2);
		JSONArray jsonResponseObjArray1 = new JSONArray(json.getString("atgResponse"));
		JSONObject jsonResponseObj = new JSONObject(jsonResponseObjArray1.get(0));
		
		System.out.println("Items : " + jsonResponseObj);

		String id=((JSONObject)jsonResponseObjArray1.get(0)).getString("wishListItemId"); 
		System.out.println("giftlistitem id "+id);
		int originalGiftitemNo=jsonResponseObjArray1.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);
		
		//move to cart
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
        params.put("jsonResultString", (String) getObject("jsonResultStringMoveToCart"));
		params.put("fromWishlist", true);
		params.put("wishlistItemId", id);
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		String responseData3 = pd2.readInputStream();
		System.out.println("Move to Cart Response Data");
		System.out.println("responseData3 : " + responseData3);
		 pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		 responseData2=pd3.readInputStream();
			
		json = new JSONObject(responseData2);
		jsonResponseObjArray1 = new JSONArray(json.getString("atgResponse"));
		jsonResponseObj = new JSONObject(jsonResponseObjArray1.get(0));
			
		int totalGiftitemNo=jsonResponseObjArray1.length();
		//assertEquals(totalGiftitemNo, originalGiftitemNo-1);
		
		System.out.println("Items : " + jsonResponseObj);
		jsonResponseObjArray1.length();
		json = new JSONObject(responseData2);
		 String result = null;
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 				System.out.println("Result: " + result);
 			}
 		 assertNull(result);
         if(pd != null)
			pd = null;
		 try {
			mSession.logout();
		  } catch (RestClientException e) {
			e.printStackTrace();
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
		params.put("atg-rest-return-form-handler-properties", "true");
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveWishListItemToCartError() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		//login user
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData = pd.readInputStream();
		System.out.println("Login Response Data");
		if (responseData != null) {
			System.out.println("responseData: " + responseData);
	     	JSONObject json = new JSONObject(responseData);
	     	System.out.println("Login Status : "+json.toString());
		}
		
		//add item to wishlist
		
		
		String jsonResultString1=(String) getObject("jsonResultStringAddToWishlist");
		this.addItem(mSession, jsonResultString1);
		
		//get wishlist items
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
		String responseData2=pd3.readInputStream();
		System.out.println("Get Wishlist Items Response Data");
		assertNotNull(responseData2);
		JSONObject json = new JSONObject(responseData2);
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("wishListItems"));
		//System.out.println("Items : " + jsonResponseObjArray);
		JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
		System.out.println("jsonChildObj = "+jsonChildObj);
		String id=jsonChildObj.getString("wishListItemId"); 
		System.out.println("giftlistitem id "+id);
		int originalGiftitemNo=jsonResponseObjArray.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);
		
		//move to cart
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
        params.put("jsonResultString", (String) getObject("jsonResultStringMoveToCart"));
		params.put("fromWishlist", true);
		params.put("wishlistItemId", id);
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		String responseData3 = pd2.readInputStream();
		System.out.println("Move to Cart Response Data");
		System.out.println("responseData3 : " + responseData3);
		json = new JSONObject(responseData3);
		
		 String result = null;
 			
 			if(json.has("formExceptions")){
 				
 				result = json.getString("formExceptions");
 				System.out.println("Result: " + result);
 			}
 		 assertNotNull(result);
         if(pd != null)
			pd = null;
		 try {
			mSession.logout();
		  } catch (RestClientException e) {
			e.printStackTrace();
		}
}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveWishListItemToCartNonLoggedInUser() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		//login user
		params = (HashMap) getControleParameters();
		
		//add item to wishlist
		
		String jsonResultString1=(String) getObject("jsonResultStringAddToWishlist");
		try
		{
			this.addItem(mSession, jsonResultString1);
		
		//get wishlist items
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String responseData2=pd3.readInputStream();
		System.out.println("Get Wishlist Items Response Data");
		assertNotNull(responseData2);
		JSONObject json = new JSONObject(responseData2);
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		
		
		System.out.println("Items : " + jsonResponseObjArray);
		String id=((JSONObject)jsonResponseObjArray.get(0)).getString("wishListItemId"); 
		System.out.println("giftlistitem id "+id);
		int originalGiftitemNo=jsonResponseObjArray.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);
		
		//move to cart
		params = (HashMap) getControleParameters();
		params.put("wishlistItemId", id);
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
        params.put("jsonResultString", (String) getObject("jsonResultStringMoveToCart"));
		params.put("fromWishlist", true);		
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		String responseData3 = pd2.readInputStream();
		System.out.println("Move to Cart Response Data");
		System.out.println("responseData3 : " + responseData3);
		json = new JSONObject(responseData3);
		 String result = null;
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 				System.out.println("Result: " + result);
 			}
 		 assertNull(result);
        
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
	

}