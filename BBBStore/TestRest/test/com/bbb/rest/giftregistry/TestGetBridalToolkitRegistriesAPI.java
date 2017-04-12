package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetBridalToolkitRegistriesAPI extends BaseTestCase {
	
	
	@SuppressWarnings("rawtypes")
	Map params = null;
	RestSession mSession;

	protected void setUp()throws Exception {
		super.setUp();
		params = getControleParameters();
		
		
	}
	

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public TestGetBridalToolkitRegistriesAPI(String name) {
		super(name);
		
	}

	
	/**
	 * Test for GetBridalToolkitRegistries 
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testGetBridalToolkitRegistries() throws org.json.JSONException, RestClientException, IOException{
		
		String siteId =  (String) getObject("siteId");
		mSession = getNewHttpSession(siteId);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String responseData = null;
		JSONObject json = null;
		String result = null;
		
		String response = null;
		String httpMethod = (String) getObject("httpmethod");
		mSession.login();
	
		try{
			
			
			
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
	 		
			
			params = (HashMap) getControleParameters();
         	restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getBridalToolkitRegistriesRequest"),
					params, httpMethod);
  			
  			responseData = restResult.readInputStream();
  			System.out.println("responseData:"+responseData);
  			
  			if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
  				json = new JSONObject(responseData);
				if (json.has("atgResponse")
						&& json.getString("atgResponse") != null
						&& !json.getString("atgResponse").equalsIgnoreCase(
								"null")
						&& !responseData.toString().contains("formExceptions")) {

					response = (String) json.getString("atgResponse");
				}
			}
  			
			
  			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& responseData != null) {
				if (responseData.toString().contains("atgResponse")&& !responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
  			
	      	assertNotNull(response);
	      	
			
		
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
	 * Test for GetBridalToolkitRegistries 
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testGetBridalToolkitRegistriesError() throws org.json.JSONException, RestClientException, IOException{
		String siteId =  (String) getObject("siteId");
		mSession = getNewHttpSession(siteId);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult =  null;
		String httpMethod = (String) getObject("httpmethod");
		mSession.login();
		try{
			
		
			params = (HashMap) getControleParameters();
            
  			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getBridalToolkitRegistriesRequest"),
					params, httpMethod);
  		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println("errorMessage :"+errorMessage);
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
