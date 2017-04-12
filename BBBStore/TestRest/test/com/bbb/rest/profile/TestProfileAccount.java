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
public class TestProfileAccount extends BaseTestCase  {



	public TestProfileAccount(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for unAuthorised login 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testUnAuthorisedLogin() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
             params.put("value.login", (String) getObject("wrongLogin"));
             params.put("value.password", (String) getObject("wrongPassword"));
             params.put("atg-rest-return-form-handler-properties", "true");
             //params.put("atg-rest-return-form-handler-properties",true);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("loginRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(json.toString());
	 			}
			 assertNotNull(result);
			 System.out.println("formExceptions="+result);
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
	 * Test for authorised login successfully
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAuthorisedLogin() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
            params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));
            params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(json.toString());
	 				
	 			}
	 			
	 				assertNull(result);
	 			
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
	 * Test for login in a existing session 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testExistingSessionLogin() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
            params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));
            params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			params = (HashMap) getControleParameters();
	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
            params.put("value.login", (String) getObject("wrongLogin"));
            params.put("value.password", (String) getObject("wrongPassword"));
            params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(json.toString());
	 			}
			 assertNotNull(result);
			 System.out.println("formExceptions="+result);
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
