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

public class TestProfileInfo extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;

	public TestProfileInfo(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}
	


	/**
	 * Getting Profile information for a logged in user
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testProfileInforUser() throws RestClientException,
			IOException, JSONException {

		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult result = null; 
		try {
			String response = null;
			
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername((String) getObject("username"));
			session.setPassword((String) getObject("password"));
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-show-rest-paths", false);
			params.put("atg-rest-http-method", "POST");
			loginRequestRestCall(params, session);
			
			result = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getProfileObject"), params,"POST");
			String responseData = result.readInputStream();
			String name = (String) getObject("firstName");
			System.out.println("responseData:" + responseData);
			if (responseData != null) {
				JSONObject json2;
				json2 = new JSONObject(responseData);
				String firstName = json2.getString("firstName");
				assertEquals(name, firstName);
			}

		} finally {
			if (result != null)
				result = null;
			session.logout();
		}
	}
	
	/**
	 * Getting profile information for a guest user.
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	public void testProfileInfoforUserError() throws JSONException {

		String testResult = (String) getObject("testResult");
		RestSession session = getNewHttpSession();
		session.setUsername((String) getObject("username"));
		session.setPassword((String) getObject("password"));
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult result = null;
		
		try {
			try {
				session.login();
				params = (HashMap) getControleParameters();
				params.put("atg-rest-show-rest-paths", false);
				result = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getProfileObject"), params,"POST");
				String responseData = result.readInputStream();
				assertEquals(testResult, responseData);
				
			} catch (RestClientException e) {
				String errorMessage=e.getMessage();
				System.out.println(errorMessage);
				if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
					assertTrue(true);
				}else{
					assertFalse(true);
				}
			} catch (IOException e) {
				assertTrue(false);	
			}


		} finally {
			if (result != null)
				result = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * login call
	 * 
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private RestResult loginRequestRestCall(Map<String, Object> params, RestSession session) throws RestClientException, JSONException, IOException {

		params.put("value.login", (String) getObject("username"));
		params.put("value.password", (String) getObject("password"));
		RestResult restResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");
		return restResult;
	}

}