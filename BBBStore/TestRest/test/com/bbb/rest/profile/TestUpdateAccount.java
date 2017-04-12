/**
 * 
 */
package com.bbb.rest.profile;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.rest.framework.BaseTestCase;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

/**
 * @author snaya2
 *
 */
public class TestUpdateAccount extends BaseTestCase  {

	String email;

	public TestUpdateAccount(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for update account success scenario
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testUpdateAccount() throws JSONException, IOException, RestClientException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			//login profile
			params = (HashMap) getControleParameters();
	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
            params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));
            params.put("atg-rest-return-form-handler-properties",true);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("loginRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			}
	 			
	 		//update profile
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
	         params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
	         params.put("value.firstName", (String) getObject("firstName"));
	         params.put("value.lastName", (String) getObject("lastName"));
	         params.put("value.phoneNumber", (String) getObject("phoneNumber"));
	         params.put("value.mobileNumber", (String) getObject("mobileNumber"));
             params.put("value.email", (String) getObject("email"));
             params.put("updateErrorURL",(String)getObject("updateErrorURL"));
             params.put("updateSuccessURL",(String)getObject("updateSuccessURL"));
             params.put("atg-rest-return-form-handler-properties", "true");
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateAccountRequest"), params,"POST");
			 JSONObject json1 = new JSONObject(pd.readInputStream());
			 String result1 = null;
			 
 			 if(json1.has("formExceptions")){
 				result1 = json1.getString("formExceptions");
 				System.out.println("formExceptions="+result1);
 			 }
			 
			 assertNull(result1);
	 		
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
		 * Test for update account error scenario
		 * @throws RestClientException 
		 */		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void testUpdateAccountError() throws RestClientException{
			mSession = getNewHttpSession();
			mSession.setUseHttpsForLogin(false);
			mSession.setUseInternalProfileForLogin(false);
			RestResult pd =  null;
			try {
				mSession.login();
				
		 		//update profile
				try {
					params = (HashMap) getControleParameters();
			         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			         params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
			         params.put("value.firstName", (String) getObject("firstName"));
			         params.put("value.lastName", (String) getObject("lastName"));
			         params.put("value.phoneNumber", (String) getObject("phoneNumber"));
			         params.put("value.mobileNumber", (String) getObject("mobileNumber"));
		             params.put("value.email", (String) getObject("email"));
		             params.put("updateErrorURL",(String)getObject("updateErrorURL"));
		             params.put("atg-rest-return-form-handler-properties", "true");
		             params.put("updateSuccessURL",(String)getObject("updateSuccessURL"));
					 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateAccountRequest"), params,"POST");
										
				} catch (RestClientException e) {
					String errorMessage=e.getMessage();
	                System.out.println(errorMessage);
                    if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
                            assertTrue(true);                               
                    }else{
                            assertFalse(true);
                    } 
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

	
	
	
	
	
	
	

