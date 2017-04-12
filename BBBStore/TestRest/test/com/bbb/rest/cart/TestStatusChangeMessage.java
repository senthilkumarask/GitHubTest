/**
 *
 */
package com.bbb.rest.cart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
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
public class TestStatusChangeMessage extends BaseTestCase  {


	public TestStatusChangeMessage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;


	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}
/**
	 * Test for unAuthorised login
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testNoStatusChangeMessage() throws RestClientException, JSONException, IOException{
		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("atg-rest-depth","2");
			RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
			String response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);

			String jsonResultOrderString=(String) getObject("jsonResultOrderString");
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", jsonResultOrderString);
			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("addCartRequest"), params,"POST");
			JSONObject json = new JSONObject(pd3.readInputStream());
			String result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("Result: " + result);
	 			}

	 			assertNull(result);


	 		params = (HashMap) getControleParameters();
	 		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1", false);
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort()	+ (String) getObject("currentOrderDetailsRequest"), params, "POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			JSONArray arr = jsonResponseObj.getJSONArray("commerceItemVOList");
			String commerceId = ((JSONObject)arr.get(0)).getString("commerceItemId");
			System.out.println(jsonResponseObj);

			List<String> commId = new ArrayList<String>();
			commId.add(commerceId);
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1", commId);
			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("checkCartItemMessage"), params,"POST");
			json = new JSONObject(pd3.readInputStream());
			result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("Result: " + result);
	 			}

	 			assertNull(result);

		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}else{
				assertFalse(true);
			}
		}
		finally {}
	}


	/**
	 * Test for No Status Change message is shown
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testStatusChangeMessageLoggedIn() throws RestClientException, JSONException, IOException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

	    //Login and establish a session and use that session to all call from same user.
	    session.setUseInternalProfileForLogin(false);

	    //session.setUsername((String)getObject("login"));
	    //session.setPassword((String) getObject("password"));
	    session.setUseHttpsForLogin(false);
	    session.login();
	    params = (HashMap) getControleParameters();

	    params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

		try{
	 		params = (HashMap) getControleParameters();
	 		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1", false);
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort()	+ (String) getObject("currentOrderDetailsRequest"), params, "POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			JSONArray arr = jsonResponseObj.getJSONArray("commerceItemVOList");
			String commerceId = ((JSONObject)arr.get(0)).getString("commerceItemId");
			System.out.println(jsonResponseObj);

			List<String> commId = new ArrayList<String>();
			commId.add(commerceId);
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1", commId);
			RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("checkCartItemMessage"), params,"POST");
			JSONObject json = new JSONObject(pd3.readInputStream());
			String result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("Result: " + result);
	 			}

	 			assertNull(result);

		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}else{
				assertFalse(true);
			}
		}
		finally {}
	}


}
