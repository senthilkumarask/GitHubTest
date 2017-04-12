package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetRegistryInfoForProfileId extends BaseTestCase {
	RestSession session=null;
	String SYSTEM_EXCEPTION="err_regsearch_sys_exception";
	Map params;

	protected void setUp() throws Exception {
		params = getControleParameters();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		session.logout();
		super.tearDown();

	}

	public TestGetRegistryInfoForProfileId(String name) {
		super(name);
	}

	/**
	 * Test case to get registry by profile ID
	 * @throws IOException
	 * @throws JSONException
	 */

	public void testGetRegistryInfoForProfile() throws IOException, JSONException {
		String response = null;
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		RestResult getRegistryInfoByProfileId = null;

		try {
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			loginRequestRestCall(params,session);

			getRegistryInfoByProfileId = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ "/rest/bean/com/bbb/rest/giftregistry/RestAPIManager/getRegistryInfoForProfileID",
							params, httpMethod);
			String responseData = getRegistryInfoByProfileId.readInputStream();
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
			}
			else{
				assertTrue(false);
			}
		}
	}
	/**
	 * Test case to get registry by profile ID with error Scenarios
	 * @throws JSONException
	 */
	public void testGetRegistryInfoForProfileError() throws JSONException {
		String response = null;
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");

		try {
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			params.put("atg-rest-http-method", httpMethod);
			loginRequestRestCall(params,session);
			RestResult getRegistryInfoByProfileId = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ "/rest/bean/com/bbb/rest/giftregistry/RestAPIManager/getRegistryInfoForProfileID",
							params, httpMethod);
			String responseData = getRegistryInfoByProfileId.readInputStream();
			System.out.println(responseData);
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
				if (responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
			//because of wrong profile id webservice will return nothing in the output
			assertNull(response);

		} catch (RestClientException e) {
			String errorMessage=e.getMessage();

			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}

			} catch (IOException e) {
			assertFalse(true);

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
	private JSONObject loginRequestRestCall(Map<String, Object> params, RestSession session) throws RestClientException, JSONException, IOException {

		params.put("value.login", (String) getObject("username"));
		params.put("value.password", (String) getObject("password"));
		RestResult RestResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");

		return new JSONObject(RestResult.readInputStream());
	}

	/**
	 * Test case to get registry by profile ID with error Scenarios
	 * @throws JSONException
	 */
	public void testGetRegistryRecommendationItemsForTab() throws JSONException {
		String response = null;
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		try {
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			params.put("atg-rest-http-method", httpMethod);
			params.put("arg1", "203521617");
			params.put("arg2", "0");
			params.put("arg3", "date");
			params.put("arg4", "0");
			params.put("arg5", "0");
			params.put("arg6", "BIR");
			params.put("X-bbb-site-id", "BedBathUS");
			RestResult getRegistryInfoByProfileId = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ "/rest/bean/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager/getRegistryRecommendationItemsForTab",
							params, httpMethod);
			String responseData = getRegistryInfoByProfileId.readInputStream();
			System.out.println(responseData);
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
				if (responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
			assertNotNull(response);
		} catch (RestClientException e) {
			String errorMessage=e.getMessage();

			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}

			} catch (IOException e) {
			assertFalse(true);

			}
	}

	/**
	 * Test case to get declined items for declined tab positive scenario.
	 *
	 * @throws JSONException
	 */


	public void testGetRegRecommItemsForDeclinedTab() throws JSONException {
		String response = null;
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		try {
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			params.put("atg-rest-http-method", httpMethod);
			params.put("arg1",  (String) getObject("registryId"));
			params.put("arg2",  (String) getObject("tabId"));
			params.put("arg3",  (String) getObject("sortOption"));
			params.put("arg4",  (String) getObject("pPageSize"));
			params.put("arg5",  (String) getObject("pPageNum"));
			params.put("arg6",  (String) getObject("eventTypeCode"));
			params.put("X-bbb-site-id", "BedBathUS");
			RestResult getRegistryInfoByProfileId = session
					.createHttpRequest(
							"http://" + getHost() + ":" + getPort() + (String) getObject("getRegistryRecommendationItemsForTab"),
							params, httpMethod);
			String responseData = getRegistryInfoByProfileId.readInputStream();
			System.out.println(responseData);
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
				if (responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
			assertTrue("Empty Registry product VO", response.contains("recommendedQuantity"));
		} catch (RestClientException e) {
			String errorMessage=e.getMessage();

			System.out.println(errorMessage);

			} catch (IOException e) {
			assertFalse(true);

			}
	}


	/**
	 * Test case to get declined items for declined tab error scenario.
	 *
	 * @throws JSONException
	 */
	public void testGetRegRecommItemsForDeclinedTabErr() throws JSONException {
		String response = null;
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		try {
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			params.put("atg-rest-http-method", httpMethod);
			params.put("arg1",  (String) getObject("registryId"));
			params.put("arg2",  (String) getObject("tabId"));
			params.put("arg3",  (String) getObject("sortOption"));
			params.put("arg4",  (String) getObject("pPageSize"));
			params.put("arg5",  (String) getObject("pPageNum"));
			params.put("arg6",  (String) getObject("eventTypeCode"));
			params.put("X-bbb-site-id", "BedBathUS");
			RestResult getRegistryInfoByProfileId = session
					.createHttpRequest(
							"http://" + getHost() + ":" + getPort()
									+ (String) getObject("getRegistryRecommendationItemsForTab"),
							params, httpMethod);
			String responseData = getRegistryInfoByProfileId.readInputStream();
			System.out.println(responseData);
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
				if (responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
			assertFalse("Registry product VO not empty", response.contains("recommendedQuantity"));
		} catch (RestClientException e) {
			String errorMessage=e.getMessage();

			System.out.println(errorMessage);

			} catch (IOException e) {
			assertFalse(true);

			}
	}
	public void testSetEmailOption() {

		String response = null;
		String httpMethod = (String) getObject("httpmethod");
		String username = (String) getObject("username");
		String password = (String) getObject("password");

		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			params.put("atg-rest-http-method", httpMethod);
			params.put("arg1", "203524312");
			params.put("arg2", "0");
			RestResult data = session.createHttpRequest("http://"
										+ getHost()
										+ ":"
										+ getPort()
										+ "/rest/bean/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager/setEmailOptInChange", params, httpMethod);

			String responseData = data.readInputStream();
			System.out.println(responseData);
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

			assertNotNull(response);
		} catch (RestClientException e) {

			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}

		} catch (IOException e) {
			assertFalse(true);
		} catch (JSONException e) {
			assertFalse(true);
		}

	}




}
