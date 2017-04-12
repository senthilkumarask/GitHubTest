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
public class TestForgotPassword extends BaseTestCase  {



	public TestForgotPassword(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for Forgot Password success scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testForgotPassword() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
             params.put("value.email", (String) getObject("email"));
             //params.put("atg-rest-return-form-handler-properties",true);
             params.put("forgotPasswordErrorURL", (String) getObject("forgotPasswordErrorURL"));
             params.put("forgotPasswordSuccessURL", (String) getObject("forgotPasswordSuccessURL"));
             params.put("atg-rest-return-form-handler-properties", "true");
             pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("forgotPasswordRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
			 System.out.println(json.toString());
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
	 * Test for Forgot Password Error scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testForgotPasswordError() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
            params.put("value.email", (String) getObject("email"));
            params.put("forgotPasswordErrorURL", (String) getObject("forgotPasswordErrorURL"));
            params.put("forgotPasswordSuccessURL", (String) getObject("forgotPasswordSuccessURL"));
            params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("forgotPasswordRequest"), params,"POST");
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

	}
	
}
