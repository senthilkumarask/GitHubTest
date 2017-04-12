/**
 * 
 */
package com.bbb.rest.profile;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.rest.framework.BaseTestCase;

import atg.core.util.StringUtils;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

/**
 * @author snaya2
 *
 */
public class TestCreateAccount extends BaseTestCase  {

	String email;

	public TestCreateAccount(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for create account success scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCreateAccount() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		String emailString = (String) getObject("email");
		//final String[] emailParts = StringUtils.splitStringAtCharacter(emailString, BBBCoreConstants.AT_THE_RATE);
		email = emailString.substring(0, 8) + (int)(Math.random()*9999) + "@" +emailString.substring(9);
		System.out.println("Email : "+email);
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
	         params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
	         params.put("value.firstName", (String) getObject("firstName"));
	         params.put("value.lastName", (String) getObject("lastName"));
	         params.put("value.phoneNumber", (String) getObject("phoneNumber"));
	         params.put("value.mobileNumber", (String) getObject("mobileNumber"));
	         params.put("sharedCheckBoxEnabled", (Boolean) getObject("sharedCheckBoxEnabled"));
	         params.put("value.password", (String) getObject("password"));
	         params.put("value.confirmPassword", (String) getObject("confirmPassword"));
             params.put("value.email", email);
             params.put("atg-rest-return-form-handler-properties", "true");
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("createAccountRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
			 
 			 if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 				System.out.println("formExceptions="+result);
 			 }
			 
			 assertNull(result);
			// mSession.logout();
			/* mSession.login();
			 params = (HashMap) getControleParameters();
            params.put("value.login", email);
            params.put("value.password", (String) getObject("password"));
            params.put("assoSite", (String) getObject("assoSite"));
            params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			JSONObject json1 = new JSONObject(pd.readInputStream());
			String result1 = null;
	 			
	 			if(json1.has("formExceptions")){
	 				result1 = json1.getString("formExceptions");
	 				System.out.println(json1.toString());
	 			}
			 assertNotNull(result1);
			 mSession.logout();
			 mSession.login();
			 params = (HashMap) getControleParameters();
            params.put("value.login", email);
            params.put("value.password", (String) getObject("password"));
            params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			JSONObject json2 = new JSONObject(pd.readInputStream());
			String result2 = null;
	 			
	 			if(json2.has("formExceptions")){
	 				result2 = json2.getString("formExceptions");
	 				System.out.println(json2.toString());
	 			}
			 assertNull(result2);*/
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
	 * Test for create account error scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCreateAccountError() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			 mSession.login(); 
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
	         params.put("emailOptIn", (Boolean) getObject("emailOptIn"));
	         params.put("value.firstName", (String) getObject("firstName"));
	         params.put("value.lastName", (String) getObject("lastName"));
	         params.put("value.phoneNumber", (String) getObject("phoneNumber"));
	         params.put("value.mobileNumber", (String) getObject("mobileNumber"));
	         params.put("sharedCheckBoxEnabled", (Boolean) getObject("sharedCheckBoxEnabled"));
	         params.put("value.password", (String) getObject("password"));
	         params.put("value.confirmPassword", (String) getObject("confirmPassword"));
             params.put("value.email", (String) getObject("email"));
             params.put("atg-rest-return-form-handler-properties",true);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("createAccountRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;	
			 System.out.println(json.toString());
 			 if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 				System.out.println(result);
 			 }

	 		assertNotNull(result);
	 		
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

	
	
	
	
	
	
	

