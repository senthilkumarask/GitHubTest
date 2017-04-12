package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetProfileRegistryList extends BaseTestCase {
	RestSession mSession;
	HashMap params = null;

	public TestGetProfileRegistryList(String name) {
		super(name);
	}

	@SuppressWarnings("rawtypes")
	/**
	 * set up required parameters
	 */
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * Test case to get registry list associated with the profile 
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */

	public void testGetRegistryProfileListLoggedInFlow() throws RestClientException,
			JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		boolean successFlag = false;
		mSession.login();
		try {
			
			loginRequestRestCall();
			RestResult restResult = getProfileRegistries();

			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& restResult != null) {
				JSONObject json = new JSONObject(restResult.readInputStream());
				System.out.println("response from JSON" + json.toString());
				if (json.toString().contains("eventDate")) {
					successFlag = true;
				}

			} else {
				String xmlResponse = restResult.readInputStream().toString();
				System.out.println(xmlResponse);
				if (xmlResponse.contains("eventDate")) {
					successFlag = true;
				}

			}
			assertTrue(successFlag);

		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}
	}

	/**
	 * test case for not logged in user.
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */

	public void testGetRegistryProfileList() throws
			JSONException, IOException, RestClientException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		try {
			
			RestResult restResult = getProfileRegistries();

			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& restResult != null) {
				JSONObject json = new JSONObject(restResult.readInputStream());
				System.out.println("response from JSON" + json.toString());
				if (json.toString().contains("eventDate")) {
				}

			} else {
				String xmlResponse = restResult.readInputStream().toString();
				System.out.println(xmlResponse);
				if (xmlResponse.contains("eventDate")) {
				}

			}
		}
			catch (RestClientException e) {
				if (e.getMessage().contains("UNAUTHORIZED ACCESS"))
						{
					assertTrue(true);
						}
				else{
					assertTrue(false);
				}
			}

		 finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
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
	private RestResult loginRequestRestCall() throws RestClientException,
			JSONException, IOException {

		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("loginRequest"), params, "POST");

		return restResult;
	}

	private RestResult getProfileRegistries() throws RestClientException,
			JSONException, IOException {
		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("profileRegistryListRequest"), params,
				"POST");
		return restResult;
	}

}
