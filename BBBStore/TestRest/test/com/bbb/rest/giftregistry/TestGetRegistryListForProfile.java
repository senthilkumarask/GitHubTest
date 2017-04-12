package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetRegistryListForProfile extends BaseTestCase {
	
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */public TestGetRegistryListForProfile(String name) {
		super(name);
	}

	/**
	 * Test case to get registry by profile ID
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetRegistryListForProfile() throws IOException, JSONException, RestClientException {
		
		String response = null;
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		try{
		params = (HashMap) getControleParameters();
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String loginResponseData = pd.readInputStream();
		System.out.println("Login Response Data");
		System.out.println("responseData: " + loginResponseData);
			RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/com/bbb/rest/giftregistry/RestAPIManager/getRegistryListForProfile", params,"POST");
			String responseData = pd1.readInputStream();
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& responseData != null) {

				JSONObject json = new JSONObject(responseData);
				if (json.has("atgResponse")
						&& json.getString("atgResponse") != null
						&& !json.getString("atgResponse").equalsIgnoreCase(
								"null")
						&& !responseData.toString().contains("formExceptions")) {

					response = (String) json.getString("atgResponse");
				}
			}
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& responseData != null) {
				if (responseData.toString().contains("atgResponse")&&!responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
			
	      	assertNotNull(response);

		} catch (RestClientException e) {
			try {
				mSession.logout();
			} catch (RestClientException ex) {
				ex.printStackTrace();
			}
		} 
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetRegistryListForProfileError() throws IOException, JSONException, RestClientException {
		
		String response = null;
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		try{
		params = (HashMap) getControleParameters();
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/com/bbb/rest/giftregistry/RestAPIManager/getRegistryListForProfile", params,"POST");
		String responseData = pd1.readInputStream();
		if (params.get("atg-rest-output").toString()
				.equalsIgnoreCase("json")
				&& responseData != null) {

			JSONObject json = new JSONObject(responseData);
			if (json.has("atgResponse")
					&& json.getString("atgResponse") != null
					&& !json.getString("atgResponse").equalsIgnoreCase(
							"null")
					&& !responseData.toString().contains("formExceptions")) {

				response = (String) json.getString("atgResponse");
			}
		}
		if (params.get("atg-rest-output").toString()
				.equalsIgnoreCase("xml")
				&& responseData != null) {
			if (responseData.toString().contains("atgResponse")&&!responseData.toString().contains("errorCode")) {
				response = responseData.toString();
			}
		}
		
      	assertNotNull(response);

		} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally{ 
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		 
		}
	}	
	
}
