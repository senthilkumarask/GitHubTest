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

public class TestRemoveAddressInProfile extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params = null;
	RestSession mSession;

	public TestRemoveAddressInProfile(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}
	


	/**
	 * Create Remove Address for a logged in user
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testRemoveAddress() throws RestClientException,
			IOException, JSONException {

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
		
	 		params = (HashMap) getControleParameters();
	        
	       
			params.put("addressId", (String) getObject("nickname"));
			params.put("useShippingAddressAsDefault", (Boolean) getObject("useShippingAddressAsDefault"));
			params.put("useBillingAddressAsDefault", (Boolean) getObject("useBillingAddressAsDefault"));
			 params.put("atg-rest-return-form-handler-properties", "true"); 

  			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("removeAddressInProfileRequest"), params,"POST");
  			String resultOut = restResult.readInputStream();
  			System.out.println(" ResultOut:"+resultOut);
  			json = new JSONObject(resultOut);
			 
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
	 * Getting error remove address for a not logged in user
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testRemoveAddressWithOutLoginError() throws RestClientException,
			IOException, JSONException {

		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String result = null;
		JSONObject json = null;
		String resultCR = null;
		
		try{
			
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("addressId", (String) getObject("nickname"));
			params.put("useShippingAddressAsDefault", (Boolean) getObject("useShippingAddressAsDefault"));
			params.put("useBillingAddressAsDefault", (Boolean) getObject("useBillingAddressAsDefault"));
			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("removeAddressInProfileRequest"), params,"POST");
			
		
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