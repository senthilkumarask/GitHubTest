package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestForgotRegistryPassword extends BaseTestCase {
	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestForgotRegistryPassword(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * Junit to test forgot registry password service for registries created on legacy system
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testForgotRegistryPassword() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession("BedBathUS");
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		System.out.println("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"));
		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			String forgetPasswordRegistryId=(String) getObject("forgetPasswordRegistryId");

			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("forgetPasswordRegistryId",forgetPasswordRegistryId);
	
			
			RestResult forgotRegPass = session.createHttpRequest(
					"http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/forgetRegistryPassword",params, "POST");
			String responseData = forgotRegPass.readInputStream();
			System.out.println("responseData" + responseData);
			
			JSONObject json = new JSONObject(responseData);
			System.out.println("forgot password for registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Junit to test forgot registry password of registry created in legacy system
	 * the JUNIT covers the case when registry id is not found at web service end
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testForgotRegistryPasswordException() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession("BedBathUS");
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		System.out.println("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"));
		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			String forgetPasswordRegistryId=(String) getObject("forgetPasswordRegistryId");

			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("forgetPasswordRegistryId",forgetPasswordRegistryId);
	
			
			RestResult forgotRegPass = session.createHttpRequest(
					"http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/forgetRegistryPassword",params, "POST");
			String responseData = forgotRegPass.readInputStream();
			System.out.println("responseData" + responseData);
			
			JSONObject json = new JSONObject(responseData);
			System.out.println("forgot password for registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Junit to test forgot registry password service for registries created on legacy system when user is not logged in user
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testForgotRegistryPasswordNonLogin() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession("BedBathUS");
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			String forgetPasswordRegistryId=(String) getObject("forgetPasswordRegistryId");

			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("forgetPasswordRegistryId",forgetPasswordRegistryId);
		
			RestResult forgotRegPass = session.createHttpRequest(
					"http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/forgetRegistryPassword",params, "POST");
			String responseData = forgotRegPass.readInputStream();
			System.out.println("responseData" + responseData);
			
			JSONObject json = new JSONObject(responseData);
			System.out.println("forgot password for registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

}
