package com.bbb.rest.profile;

import java.io.IOException;
import java.util.HashMap;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestProfileLogout extends BaseTestCase {
	RestSession session = null;

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

	}

	public TestProfileLogout(String name) {
		super(name);
	}

	/**
	 * Test case to create Birthday type registry
	 */
	HashMap params = null;
	/**
	 * check user logout
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testCheckLogout() throws RestClientException, IOException, JSONException {

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String method = (String) getObject("httpmethod");
		String transientString = (String) getObject("isTransient");
		RestResult restResult = null;

		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUseHttpsForLogin(false);
			session.login();
			HashMap params = new HashMap<String, String>();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", method);
		    params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", username);
			params.put("value.password", password);

			RestResult pd2 = session.createHttpRequest("http://" + getHost()
					+ ":" + getPort()
					+ (String) getObject("loginRequest"),
					params, method);
			
			params.put("expireSessionOnLogout", false);
			restResult = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ (String) getObject("logoutRequest"),
							params, method);
			
			RestResult logoutSuccess = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ (String) getObject("checkTransient"),
							params, method);
			
			String responseData = logoutSuccess.readInputStream();
			
			String isTransient = null;
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& responseData != null) {
				JSONObject json = new JSONObject(responseData);
				if (json.has("atgResponse")) {
					isTransient = json.getString("atgResponse");
				}
				if (params.get("atg-rest-output").toString()
						.equalsIgnoreCase("xml")
						&& responseData != null) {
					if (responseData.toString().contains("true")) {
						isTransient = transientString;
					}
				}
				assertEquals(transientString, isTransient);
			}
		} finally {
			if (restResult != null)
				restResult = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}

	public void testCheckLogoutError() throws RestClientException, JSONException, IOException {

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String method = (String) getObject("httpmethod");
		String transientString = (String) getObject("isTransient");
		RestResult restResult = null;

		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUseHttpsForLogin(false);
			session.login();
			HashMap params = new HashMap<String, String>();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", method);
		    params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", username);
			params.put("value.password", password);

			RestResult pd2 = session.createHttpRequest("http://" + getHost()
					+ ":" + getPort()
					+ (String) getObject("loginRequest"),
					params, method);
			
			RestResult logoutError = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ (String) getObject("checkTransient"),
							params, method);
			
			String responseData = logoutError.readInputStream();
			
			String isTransient = null;
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& responseData != null) {
				JSONObject json = new JSONObject(responseData);
				if (json.has("atgResponse")) {
					isTransient = json.getString("atgResponse");
				}
				if (params.get("atg-rest-output").toString()
						.equalsIgnoreCase("xml")
						&& responseData != null) {
					if (responseData.toString().contains("true")) {
						isTransient = transientString;
					}
				}
				assertEquals(transientString, isTransient);
			}
		}  finally {
			if (restResult != null)
				restResult = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
}
