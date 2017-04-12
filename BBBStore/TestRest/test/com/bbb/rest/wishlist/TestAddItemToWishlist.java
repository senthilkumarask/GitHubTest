package com.bbb.rest.wishlist;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.rest.framework.BaseTestCase;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

public class TestAddItemToWishlist extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestAddItemToWishlist(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}
	/**
	 * Test for non logged  in user.Form handler returns false
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	public void testTransientUser() throws JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jsonResultString", (String) getObject("jsonResultString"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
			JSONObject json = new JSONObject(pd3.readInputStream());
			System.out.println("add to cart json ="+json.toString());
			String result = null;
			if(json.has("formExceptions")){
				result = json.getString("formExceptions");
			}
			assertNull(result);
			pd3 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getItem"), params,"POST");
			json = new JSONObject(pd3.readInputStream());
			System.out.println("add to cart json ="+json.toString());
			result = null;
			if(json.has("formExceptions")){
				result = json.getString("formExceptions");
			}
			assertNotNull(json.getString("atgResponse"));
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
	 * test to add single sku or 1 collection product
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings("unchecked")
	public void testAddSingleSkuToWishlist() throws RestClientException, IOException, JSONException{
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
			params.put("jsonResultString", (String) getObject("jsonResultString"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd3 = mSession.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
			JSONObject json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 	
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
	 * test case to get wish list items for a user
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testGetWishList() throws JSONException, IOException, RestClientException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
		
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
			 params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd3 = mSession.createHttpRequest("http://" + getHost()+ ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
			String response=pd3.readInputStream();
			System.out.println("get WishList json =");
			System.out.println(response);
			assertNotNull(response);
			JSONObject json = new JSONObject(response);
			JSONArray jsonResponseObjArray1 = new JSONArray(json.getString("atgResponse"));
			
			

			assertNotNull((JSONObject)jsonResponseObjArray1.get(0));

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
	 * Test to check that exception is returned in the case a non logged in user tries to
	 * access get wish list items service
	 */

	@SuppressWarnings({ "unchecked", "unused" })
	public void testGetWishListNonLoggedInUser(){
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
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
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

}
