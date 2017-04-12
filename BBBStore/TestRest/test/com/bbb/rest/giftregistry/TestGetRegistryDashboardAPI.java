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

/**
 * 
 * @author ssha53
 *
 */
public class TestGetRegistryDashboardAPI extends BaseTestCase {
	RestSession session=null;
	private String NO_ERROR="NO_ERROR";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		session.logout();
		super.tearDown();

	}

	public TestGetRegistryDashboardAPI(String name) {
		super(name);
	}

	/**
	 * Test case to get registry details for Dashboard. 
	 */
	@SuppressWarnings("unchecked")
	public void testGetRegistryDashboard() {
       String success = null;
		
		String noError = (String) getObject("noerror");
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		RestResult getRegistryDetails=null;
		try {
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			loginRequestRestCall(params, session);
		
			getRegistryDetails = session.createHttpRequest("http://" + getHost()+ ":"+ getPort()+ (String) getObject("registryDashboardURL"),
							params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& responseData != null) {

				JSONObject json = new JSONObject(responseData);
				if (json!=null&&json.toString().contains("eventDate")) {
						success=NO_ERROR;
					
					}
			}
			if (params.get("atg-rest-output").toString().equalsIgnoreCase("xml") && responseData != null) {
				
				if (responseData.toString().contains("eventDate")) {
					success=NO_ERROR;
				}
			}
			assertEquals(noError, success);

		} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}
			if(errorMessage.contains("901"))
			{
				assertTrue(true);	
			}
			
			} catch (IOException e) {
			assertFalse(true);

		} catch (JSONException e) {
			assertFalse(true);

		}
	}



	
	/**
	 * Test case to check the error scenerio in get Registry Details
	 */
	@SuppressWarnings("unchecked")
	public void testGetRegistryDashboardError() {
		String exception = null;

		String errorId = (String) getObject("errorId");
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		RestResult getRegistryDetails=null;
		try {
			session=getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();
		
			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
		
			// common fields
			getRegistryDetails = session.createHttpRequest("http://"+ getHost() + ":" + (String) getObject("registryDashboardURL"),params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println("testGetRegistryDetailError: "+responseData);
			if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {

				JSONObject json = new JSONObject(responseData);
				if (json.has("registryResVO")) {
					JSONObject registryResVO =  json.getJSONObject("registryResVO");
					if (registryResVO.has("serviceErrorVO")) {
					JSONObject serviceErrorVO =  registryResVO.getJSONObject("serviceErrorVO");
					if (serviceErrorVO.has("errorId"))
					{
						exception=serviceErrorVO.getString("errorId");
					}
					}
				}
			}
			if (params.get("atg-rest-output").toString().equalsIgnoreCase("xml") && responseData != null) {
				String startError="<errorId>";
				if (responseData.toString().contains("errorId")) {
					exception = responseData.substring(responseData.indexOf(startError)+startError.length(),responseData.indexOf("</errorId>"));
				}
			}
			assertEquals(errorId,exception);

		} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			
			System.out.println(errorMessage);
			if(errorMessage.contains("Error 500--Internal Server Error")){
				assertTrue(true);
			}
			if(errorMessage.contains("901"))
			{
				assertTrue(true);	
			}
			
			} catch (IOException e) {
			assertFalse(true);

		} catch (JSONException e) {
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
	private RestResult loginRequestRestCall(Map<String, Object> params, RestSession session) throws RestClientException, JSONException, IOException {

		params.put("value.login", (String) getObject("username"));
		params.put("value.password", (String) getObject("password"));
		RestResult restResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");
		return restResult;
	}
	

}
