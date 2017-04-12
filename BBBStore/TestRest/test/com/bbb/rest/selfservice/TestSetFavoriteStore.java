package com.bbb.rest.selfservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestSetFavoriteStore extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params = null;
	RestSession mSession;
	
	public TestSetFavoriteStore(String name){
		super(name);
	}
	
	protected void setUp() {
		params = getControleParameters();
	}

	
	
	/**
	 * Test case to set favorite store based on favorite store id in user profile
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testSetFavoriteStore() throws RestClientException, IOException, JSONException {
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String result = null;
		JSONObject json = null;
		
		try{
			
			mSession.login();
			
			//login first for given user profile
			params = (HashMap) getControleParameters();
	        params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));
            params.put("atg-rest-return-form-handler-properties", "true");
            restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("loginRequest"), params,"POST");
			json = new JSONObject(restResult.readInputStream());
			
	 			
	 		if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 		}
	 			
	 		//make default credit card
			
	 		params = (HashMap) getControleParameters();
	        params.put("favStoreId", (String) getObject("favStoreId"));
	        
	        restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("setFavStoreIdRequest"), params,"POST");
			String resultReturn = restResult.readInputStream();
	        System.out.println("resultReturn:"+resultReturn);
	        json = new JSONObject(resultReturn);
			 
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 				System.out.println("formExceptions="+result);
 			 }
			 
			 assertNull(result);
			
		}finally{
			
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
	 * Test case for Error scenario to set favorite store based on favorite store id in user profile
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testSetFavoriteStoreError() throws RestClientException, IOException, JSONException {
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		
		try{
			
			mSession.login();
			params = (HashMap) getControleParameters();
	        params.put("favStoreId", (String) getObject("favStoreId"));
	        restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("setFavStoreIdRequest"), params,"POST");
		
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println("errorMessage:"+errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		
		}finally{
			
			 if(restResult != null)
					restResult = null;
			
			 try {
					mSession.logout();
				 } catch (RestClientException e) {
					e.printStackTrace();
				 }
			
		}
			
	}
	
	
		
}
