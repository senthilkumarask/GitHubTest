package com.bbb.rest.account;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestHandleContactUsRequest extends BaseTestCase {

	public TestRestHandleContactUsRequest(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;

	/**
	 ** Test Remove Item From Cart failure, item already removed.
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings("unchecked")
	public void testContactUsViaEmailRequestSuccess() throws RestClientException, JSONException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		String result = null;
		JSONObject jsonObject = null;

		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("subjectCategory", (String) getObject("subjectCategory"));
			params.put("firstName", (String) getObject("firstName"));
			params.put("lastName", (String) getObject("lastName"));
			params.put("emailMessage", (String) getObject("emailMessage"));
			params.put("contactType", (String) getObject("contactType"));
			params.put("email", (String) getObject("email"));
			params.put("confirmEmail", (String) getObject("confirmEmail"));
			
			jsonObject = handleContactUsREST();
			System.out.println(jsonObject);
			
		} 
		finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 ** Test Remove Item From Cart failure, item already removed.
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings("unchecked")
	public void testContactUsViaPhoneRequestSuccess() throws RestClientException, JSONException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		String result = null;
		JSONObject jsonObject = null;

		try {
			
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("subjectCategory", (String) getObject("subjectCategory"));
			params.put("firstName", (String) getObject("firstName"));
			params.put("lastName", (String) getObject("lastName"));
			params.put("emailMessage", (String) getObject("emailMessage"));
			
      		params.put("contactType", (String) getObject("contactType"));
			params.put("phoneNumber", (String) getObject("phoneNumber"));
			params.put("phoneExt", (String) getObject("phoneExt"));
			params.put("selectedTimeZone", (String) getObject("selectedTimeZone"));
			params.put("selectedTimeCall", (String) getObject("selectedTimeCall"));
			params.put("amPM", (String) getObject("amPM"));
			
			jsonObject = handleContactUsREST();
			System.out.println(jsonObject);
		} 
		finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 ** Test Remove Item From Cart failure, item already removed.
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	@SuppressWarnings({ "unchecked", "null" })
	public void testContactUsRequestFailure() throws RestClientException, JSONException, IOException {
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		String result = null;
		JSONObject jsonObject = null;

		try {

			mSession.login();
			
			params = (HashMap) getControleParameters();
			params.put("captchaAnswer", (String) getObject("captchaAnswer"));
			jsonObject = handleContactUsREST();
			
			System.out.println(jsonObject);
			if (jsonObject.has("formExceptions")) {
				result = jsonObject.getString("formExceptions");
			}
			assertNotNull(result);

		}
		finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}


	/**
	 * handle Contact Us REST
	 *
	 * @return JSONObject
	 * @throws RestClientException
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	private JSONObject handleContactUsREST() throws RestClientException, JSONException, IOException {

		params.put("validateCaptcha", (Boolean) (getObject("validateCaptcha")));
		params.put("contactUsSuccessURL", (String) getObject("contactUsSuccessURL"));
		params.put("contactUsErrorURL", (String) getObject("contactUsErrorURL"));

		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ "/rest/bean/com/bbb/selfservice/ContactUsFormHandler/requestInfo", params, "POST");

		String response = restResult.readInputStream();
		System.out.println("response:" + response);
		return new JSONObject(response);
	}

}
