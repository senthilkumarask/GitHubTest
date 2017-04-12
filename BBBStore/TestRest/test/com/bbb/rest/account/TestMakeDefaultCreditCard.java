package com.bbb.rest.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestMakeDefaultCreditCard extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params = null;
	RestSession mSession;
	
	public TestMakeDefaultCreditCard(String name){
		super(name);
	}
	
	protected void setUp() {
		params = getControleParameters();
	}

	
	
	/**
	 * Test case to make default credit card from web service based on email id
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testMakeDefaultCreditCard() throws RestClientException, IOException, JSONException {
		
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
            restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("loginRequest"), params,"POST");
			json = new JSONObject(restResult.readInputStream());
			
	 			
	 		if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 		}
	 			
	 		//make default credit card
			
	 		params = (HashMap) getControleParameters();
	        params.put("value.defaultCreditCard", (String) getObject("defaultCreditCard"));
	        params.put("atg-rest-return-form-handler-properties", "true"); 
	        restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("makeDefaultCreditCardRequest"), params,"POST");
			json = new JSONObject(restResult.readInputStream());
			System.out.println("formExceptions="+json);
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
	 * Test case for Error scenario to make default credit card from web service based on email id
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testMakeDefaultCreditCardError() throws RestClientException, IOException, JSONException {
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		
		try{
			
			mSession.login();
			params = (HashMap) getControleParameters();
	        params.put("value.defaultCreditCard", (String) getObject("defaultCreditCard"));
	        restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("makeDefaultCreditCardRequest"), params,"POST");
		
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
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
