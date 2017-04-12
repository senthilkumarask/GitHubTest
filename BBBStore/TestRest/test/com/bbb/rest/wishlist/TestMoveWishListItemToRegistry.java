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

public class TestMoveWishListItemToRegistry extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestMoveWishListItemToRegistry(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}


	/**
	 * test to move wish list item to registry
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings("unchecked")
	public void testMoveWishListItemToRegistry() throws RestClientException, IOException, JSONException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			
			//add item
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jsonResultString", (String) getObject("jsonResultString"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			 pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");

			
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jsonResultString", (String) getObject("jsonResultString1"));
			 pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
			System.out.println("add item2: "+pd2.readInputStream());
			/**
			 * get gift list items to get original no of items
			 */

			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			String response=pd3.readInputStream();

			assertNotNull(response);
			JSONObject json = new JSONObject(response);
			JSONArray jsonResponseObjArray = new JSONArray(json.getString("wishListItems"));
			JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
			String id=jsonChildObj.getString("wishListItemId"); 
			System.out.println("giftlistitem id "+id);
			System.out.println("number of wish list items before move to registry "+jsonResponseObjArray.length());
			System.out.println(jsonResponseObjArray);
			
			/**
			 * request call to move the wish list item to registry
			 */
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("wishListItemId",id);
			params.put("registryId", (String) getObject("registryId"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			 pd3 = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("moveWishListItemToRegistry"), params,"POST");
			 String moveResponse=pd3.readInputStream();
			 System.out.println(" response of move item from wishlist to registry "+moveResponse);
			 json = new JSONObject(moveResponse);
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 	
			
			/**
			 * get gift list items to get no of items after move to registry
			 */

			 pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			 response=pd3.readInputStream();

			assertNotNull(response);
			 json = new JSONObject(response);
			 if(!json.getString("wishListItems").equalsIgnoreCase("null") && json.getString("wishListItems")!= null){
				 jsonResponseObjArray = new JSONArray(json.getString("wishListItems"));
				 System.out.println("number of wish list items after move to registry "+jsonResponseObjArray.length());
			 }
			 else{
				 System.out.println("number of wish list items after move to registry "+json.getString("wishListItems")); 
			 }
			
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * test to move wish list item to registry
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings("unchecked")
	public void testMoveWishListItemToRegistryException() throws RestClientException, IOException, JSONException{
		mSession = getNewHttpSession("BedBathUS");
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		RestResult pd3 =  null;
		JSONObject json=null;

		try {
			mSession.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				 json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			
		
			
			/**
			 * request call to move the wish list item to registry
			 */
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("wishListItemId","ddddd");
			params.put("registryId", (String) getObject("registryId"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			 pd3 = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("moveWishListItemToRegistry"), params,"POST");
			 String moveResponse=pd3.readInputStream();
			 System.out.println(" response of move item from wishlist to registry "+moveResponse);
			 json = new JSONObject(moveResponse);
			System.out.println("formhandler formExceptions  "+json.getString("formExceptions"));
			assertNotNull(json.getString("formExceptions")); 	
			
			
			
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * test to move wish list item to registry
	 * exception scenario is that wishlist item sent is invalid
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings("unchecked")
	public void testInvalidRegistryId() throws RestClientException, IOException, JSONException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jsonResultString", (String) getObject("jsonResultString1"));
			 pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
			System.out.println("add item2: "+pd2.readInputStream());
		
			/**
			 * get gift list items to get original no of items
			 */

			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			String response=pd3.readInputStream();

			assertNotNull(response);
			JSONObject json = new JSONObject(response);
			JSONArray jsonResponseObjArray = new JSONArray(json.getString("wishListItems"));
			JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
			String id=jsonChildObj.getString("wishListItemId"); 
			System.out.println("giftlistitem id "+id);
			System.out.println("number of wish list items before move to registry "+jsonResponseObjArray.length());
			System.out.println(jsonResponseObjArray);
			
			/**
			 * request call to move the wish list item to registry
			 */
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("wishListItemId",id);
			params.put("registryId", (String) getObject("registryId"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			 pd3 = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("moveWishListItemToRegistry"), params,"POST");
			 String moveResponse=pd3.readInputStream();
			 System.out.println(" response of move item from wishlist to registry "+moveResponse);
			 json = new JSONObject(moveResponse);
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 	
			
			/**
			 * get gift list items to get no of items after move to registry
			 */

			 pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/wishlist/BBBWishlistManager/getWishListItems", params,"POST");
			 response=pd3.readInputStream();

			assertNotNull(response);
			 json = new JSONObject(response);
			 jsonResponseObjArray = new JSONArray(json.getString("wishListItems"));
			System.out.println("number of wish list items after move to registry "+jsonResponseObjArray.length());
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * test to confirm that a non logged in user cannot access update wish list service
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked" })
	public void testMoveWishListItemToRegistryNonLoggedInUser() throws IOException, JSONException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd3 =  null;
		try {
			mSession.login();
			/**
			 * request call to move the wish list item to registry
			 */
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("wishListItemId",(String) getObject("wishListItemId"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			params.put("registryId", (String) getObject("registryId"));
			 pd3 = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("moveWishListItemToRegistry"), params,"POST");
			 String moveResponse=pd3.readInputStream();
			 System.out.println(" response of move item from wishlist to registry "+moveResponse);
			 JSONObject json = new JSONObject(moveResponse);
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 	
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally {
			if(pd3 != null)
				pd3 = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}



}
