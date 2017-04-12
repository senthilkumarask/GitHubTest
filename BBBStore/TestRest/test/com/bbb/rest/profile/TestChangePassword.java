/**
 * 
 */
package com.bbb.rest.profile;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * @author snaya2
 *
 */
public class TestChangePassword extends BaseTestCase  {



	public TestChangePassword(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for change Password success scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testChangePassword() throws RestClientException, JSONException, IOException{

		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		String email = (int)(Math.random()*9999)+(String) getObject("email");
		System.out.println("Email : "+email);
		try {
			//create user
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
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("createAccountRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
			 
 			 if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 				System.out.println("formExceptions="+result);
 			 }
			 
			 assertNull(result);
			 mSession.logout();
			 //login
			 mSession.login();
			 params = (HashMap) getControleParameters();
			  params.put("value.login", email);
	          params.put("value.password", (String) getObject("password"));
			pd = mSession.createHttpRequest("http://" + (String) getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			JSONObject json1 = new JSONObject(pd.readInputStream());
			String result1 = null;
	 			
	 			if(json1.has("formExceptions")){
	 				result1 = json1.getString("formExceptions");
	 				System.out.println(json1.toString());
	 			}
			 assertNull(result1);
			
			 
			 //change password
			 params = (HashMap) getControleParameters();
		
             params.put("value.oldpassword", (String) getObject("password"));
             params.put("value.password", (String) getObject("password")+"invalid");
             params.put("value.confirmpassword", (String) getObject("password")+"invalid");
             pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("changePasswordRequest"), params,"POST");             
			 json = new JSONObject(pd.readInputStream());
			 result = null;
			 System.out.println(json.toString());
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(json.toString());
	 			}
			 assertNull(result);
			 mSession.logout();
			 //relogin
			 mSession.login();
			 params = (HashMap) getControleParameters();
            params.put("value.login", email);
            params.put("value.password", (String) getObject("password")+"invalid");
			pd = mSession.createHttpRequest("http://" + (String) getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			json1 = new JSONObject(pd.readInputStream());
			result1 = null;
	 			
	 			if(json1.has("formExceptions")){
	 				result1 = json1.getString("formExceptions");
	 				System.out.println(json1.toString());
	 			}
			 assertNull(result1);
			
			 try{
				params = (HashMap) getControleParameters();
	            params.put("value.oldpassword", (String) getObject("password")+"wesee");
	            params.put("value.password", (String) getObject("password"));
	            params.put("value.confirmpassword", (String) getObject("password")+"invlais");
	            params.put("value.login", (String) getObject("email"));
				pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("changePasswordRequest"), params,"POST");
				json = new JSONObject(pd.readInputStream());
				result = null;
		 			
		 			if(json.has("formExceptions")){
		 				result = json.getString("formExceptions");
		 				System.out.println(json.toString());
		 			}
		 			assertTrue(result.contains("err_profile_password_confirmpassword_notequal"));
			 }catch (Exception e) {
				 	System.out.println(e);
				 	assertTrue(true);
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
	
		/*
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
             params.put("value.email", (String) getObject("email"));
             params.put("value.oldpassword", (String) getObject("password"));
             params.put("value.password", (String) getObject("password")+"invalid");
             params.put("value.confirmpassword", (String) getObject("password")+"invalid");
             pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("changePasswordRequest"), params,"POST");             
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
			 System.out.println(json.toString());
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(json.toString());
	 			}
			 assertNull(result);			 
			 params.put("value.oldpassword", (String) getObject("password")+"invalid");
             params.put("value.password", (String) getObject("password"));
             params.put("value.confirmpassword", (String) getObject("password"));
             pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("changePasswordRequest"), params,"POST");
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

	*/}
	
	
	/**
	 * Test for change Password Error scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
/*	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testChangePasswordError() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("value.email", (String) getObject("email"));
            params.put("value.oldpassword", (String) getObject("password")+"wesee");
            params.put("value.password", (String) getObject("password"));
            params.put("value.confirmpassword", (String) getObject("password")+"invlais");
            params.put("value.login", (String) getObject("email"));
			pd = mSession.createHttpRequest("http://" + (String) getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			JSONObject json1 = new JSONObject(pd.readInputStream());
			String result1 = null;
	 			
	 			if(json1.has("formExceptions")){
	 				result1 = json1.getString("formExceptions");
	 				System.out.println(json1.toString());
	 			}
			 assertNull(result1);
			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("changePasswordRequest"), params,"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(json.toString());
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

	}*/
	
}
