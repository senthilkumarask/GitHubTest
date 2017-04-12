package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * Test cases for Gift Card Balance API
 * 
 * @author sku134
 * 
 */
public class TestBridalShowAPI extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	/**
	 * @param name
	 */
	public TestBridalShowAPI(String name) {
		super(name);

	}

	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to get the bridal show for the stateId
	 * 
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testBridalShow() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		boolean showAPIExist = Boolean.FALSE;
		String response = null;
		try {
			
			params.put("arg1", (String) getObject("stateId"));
			params.put(
					"atg-rest-return-form-handler-exceptions",
					(Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			RestResult restResult = mSession.createHttpRequest("http://"
					+ getHost() + ":" + getPort()
					+ (String) getObject("getBridalShowRequest"), params,
					"POST");

			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& restResult != null) {
				response = restResult.readInputStream();
				JSONObject json = new JSONObject(response);
				if (json.toString().contains("toDate")) {
					showAPIExist = Boolean.TRUE;
				}
			
				System.out.println("response from JSON" + json.toString());

			}
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& restResult != null) {
				 response = restResult.readInputStream().toString();
				System.out.println("Xml response: " + response);
				if (response.contains("toDate")) {
					showAPIExist = Boolean.TRUE;
				}

			}
			assertNotNull(response);
			
		}finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}
	/**
	 * test to get the bridal show for the stateId
	 * 
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testBridalShowError() throws IOException, JSONException,
			RestClientException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		params.put("arg1", (String) getObject("invalidstateId"));
		boolean errorExists= Boolean.TRUE;
		try {
			mSession.login();
			params.put(
					"atg-rest-return-form-handler-exceptions",
					(Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			RestResult restResult = mSession.createHttpRequest("http://"
					+ getHost() + ":" + getPort()
					+ (String) getObject("getBridalShowRequest"), params,
					"POST");

			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& restResult != null) {
				JSONObject json = new JSONObject(restResult.readInputStream());
				if (json.toString().contains("null")) {
					errorExists = Boolean.TRUE;
				}
				System.out.println("response from JSON" + json.toString());

			}
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& restResult != null) {
				String xmlResponse = restResult.readInputStream().toString();
				System.out.println("response from JSON" + xmlResponse);
				if (xmlResponse.contains("toDate")) {
					errorExists = Boolean.FALSE;
				}

			}
			assertTrue(errorExists);
		}

		finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}

}
