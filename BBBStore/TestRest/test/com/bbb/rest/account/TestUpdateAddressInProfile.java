package com.bbb.rest.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestRepositoryHelper;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestUpdateAddressInProfile extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params = null;
	RestSession mSession;

	public TestUpdateAddressInProfile(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}
	


	/**
	 * Update Address for a logged in user
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateAddress() throws RestClientException,
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
	        
	        params.put("editValue.firstName", (String) getObject("firstName"));
			params.put("editValue.lastName", (String) getObject("lastName"));
			params.put("editValue.companyName", (String) getObject("companyName"));
			params.put("editValue.address1",(String) getObject("address1"));
			params.put("editValue.address2", (String) getObject("address2"));
			params.put("editValue.city", (String) getObject("city"));
			params.put("editValue.state",(String) getObject("state"));
			params.put("editValue.postalCode", (String) getObject("postalCode"));
			params.put("editValue.nickname", (String) getObject("nickname"));
			params.put("useShippingAddressAsDefault", (Boolean) getObject("useShippingAddressAsDefault"));
			params.put("useBillingAddressAsDefault", (Boolean) getObject("useBillingAddressAsDefault"));
			params.put("updateAddressErrorURL", (String) getObject("updateAddressErrorURL"));
			params.put("updateAddressSuccessURL", (String) getObject("updateAddressSuccessURL"));
    		params.put("atg-rest-return-form-handler-properties", "true"); 

			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("updateAddressInProfileRequest"), params,"POST");
			json = new JSONObject(restResult.readInputStream());
			 
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
	 * Getting validation Error during update address for a logged in user
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateAddressError() throws RestClientException,
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
	        params.put("editValue.firstName", (String) getObject("firstName"));
			params.put("editValue.lastName", (String) getObject("lastName"));
			params.put("editValue.companyName", (String) getObject("companyName"));
			params.put("editValue.address1",(String) getObject("address1"));
			params.put("editValue.address2", (String) getObject("address2"));
			params.put("editValue.city", (String) getObject("city"));
			params.put("editValue.state",(String) getObject("state"));
			params.put("editValue.postalCode", (String) getObject("postalCode"));
			params.put("editValue.nickname", (String) getObject("nickname"));
			params.put("useShippingAddressAsDefault", (Boolean) getObject("useShippingAddressAsDefault"));
			params.put("useBillingAddressAsDefault", (Boolean) getObject("useBillingAddressAsDefault"));
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("updateAddressErrorURL", (String) getObject("updateAddressErrorURL"));
			params.put("updateAddressSuccessURL", (String) getObject("updateAddressSuccessURL"));
    		 params.put("atg-rest-return-form-handler-properties", "true"); 

			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("updateAddressInProfileRequest"), params,"POST");
			
			String resultError = restResult.readInputStream();
			System.out.println("resultError:"+resultError);
			
			json = new JSONObject(resultError);
			 
			 if(json.has("formExceptions")){
				resultCR = json.getString("formExceptions");
				System.out.println("formExceptions="+resultCR);
			 }
			 
			assertNotNull(resultCR);
		
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
	 * Getting error to update address for a not logged in user
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateAddressWithOutLoginError() throws RestClientException,
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
	        params.put("editValue.firstName", (String) getObject("firstName"));
			params.put("editValue.lastName", (String) getObject("lastName"));
			params.put("editValue.companyName", (String) getObject("companyName"));
			params.put("editValue.address1",(String) getObject("address1"));
			params.put("editValue.address2", (String) getObject("address2"));
			params.put("editValue.city", (String) getObject("city"));
			params.put("editValue.state",(String) getObject("state"));
			params.put("editValue.postalCode", (String) getObject("postalCode"));
			params.put("editValue.nickname", (String) getObject("nickname"));
			params.put("useShippingAddressAsDefault", (Boolean) getObject("useShippingAddressAsDefault"));
			params.put("useBillingAddressAsDefault", (Boolean) getObject("useBillingAddressAsDefault"));
			params.put("updateAddressErrorURL", (String) getObject("updateAddressErrorURL"));
			params.put("updateAddressSuccessURL", (String) getObject("updateAddressSuccessURL"));
			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("updateAddressInProfileRequest"), params,"POST");
			
		
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