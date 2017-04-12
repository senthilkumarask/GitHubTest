package com.bbb.rest.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.rest.framework.BaseTestCase;



import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;


public class TestMakeDefaultBillingAddress extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params;
	RestSession mSession;
	
	public TestMakeDefaultBillingAddress(String name){
		super(name);
	}
	
	protected void setUp() {
		params = getControleParameters();
	}

	
	
	
	
	/**
	 * Test case to make default billing address
	 *  from web service based on email id
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testMakeDefaultBillingAddress() throws RestClientException, IOException, JSONException {
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String result = null;
		JSONObject json = null;
		String resultCR = null;
		
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
	 		
	 		assertNull(result);
	 			
	 		//make default billing address
			 params = (HashMap) getControleParameters();
			 params.put("atg-rest-return-form-handler-properties", "true"); 
	         params.put("defaultBillingAddressCode", (String) getObject("defaultBillingAddress"));
	         restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("makeDefaultBillingAddressRequest"), params,"POST");
	         String responseData = restResult.readInputStream();
	         System.out.println("responseData"+ responseData);
	         json = new JSONObject(responseData);
			 
			 
 			 if(json.has("formExceptions")){
 				resultCR = json.getString("formExceptions");
 				System.out.println("formExceptions="+resultCR);
 			 }
			 
			 assertNull(resultCR);
	
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
	 * Test case for error scenario to make default billing address
	 *  from web service based on email id
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testMakeDefaultBillingAddressError() throws RestClientException, IOException, JSONException {
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		
		try{
			
			 mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("defaultBillingAddressCode", (String) getObject("defaultBillingAddress"));
	         restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("makeDefaultBillingAddressRequest"), params,"POST");
		
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		
	
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
	
	
}
