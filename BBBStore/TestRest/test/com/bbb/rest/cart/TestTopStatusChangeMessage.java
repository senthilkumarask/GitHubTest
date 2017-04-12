/**
 *
 */
package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;
import com.bbb.utils.BBBUtility;


/**
 * @author Ayush Gupta
 *
 */
public class TestTopStatusChangeMessage extends BaseTestCase  {


	public TestTopStatusChangeMessage(String name) {
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
	public void testCartItemMessages() throws RestClientException, JSONException, IOException{
		Boolean result =  false;
		RestSession session = getNewHttpSession((String) getObject("SiteId"));

	    //Login and establish a session and use that session to all call from same user.
	    session.setUseInternalProfileForLogin(false);

	    //session.setUsername((String)getObject("login"));
	    //session.setPassword((String) getObject("password"));
	    session.setUseHttpsForLogin(false);
	    session.login();
	    params = (HashMap) getControleParameters();

	    params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			//System.out.println("Login Status : "+json.toString());
		}

		try{
			pd2 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("checkCartItemMessage"), params,"POST");
			JSONObject json = new JSONObject(pd2.readInputStream());

	 		//System.out.println("checkCartItemMessage json ="+json.toString());
			//System.out.println("Messages returned from method "+json.getString("atgResponse"));
			if(!json.getString("atgResponse").equalsIgnoreCase("null"))
				result = true;


			assertTrue(result);


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
	public void testSaveItemMessages() throws RestClientException, JSONException, IOException{
		Boolean result =  false;
		RestSession session = getNewHttpSession((String) getObject("SiteId"));

	    //Login and establish a session and use that session to all call from same user.
	    session.setUseInternalProfileForLogin(false);

	    //session.setUsername((String)getObject("login"));
	    //session.setPassword((String) getObject("password"));
	    session.setUseHttpsForLogin(false);
	    session.login();
	    params = (HashMap) getControleParameters();

	    params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			//System.out.println("Login Status : "+json.toString());
		}

		try{
			pd2 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("checkSavedItemMessages"), params,"POST");
			JSONObject json = new JSONObject(pd2.readInputStream());

			//System.out.println("checkSavedItemMessages json ="+json.toString());
			//System.out.println("Messages returned from method "+json.getString("atgResponse"));
			if(!json.getString("atgResponse").equalsIgnoreCase("null"))
				result = true;


			assertTrue(result);


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
	 * Test for unAuthorised login
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMergeSaveCartItemMessages() throws RestClientException, JSONException, IOException{
		Boolean result =  false;
		RestSession session = getNewHttpSession((String) getObject("SiteId"));

	    //Login and establish a session and use that session to all call from same user.
	    session.setUseInternalProfileForLogin(false);

	    //session.setUsername((String)getObject("login"));
	    //session.setPassword((String) getObject("password"));
	    session.setUseHttpsForLogin(false);
	    session.login();
	    params = (HashMap) getControleParameters();

	    params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			//System.out.println("Login Status : "+json.toString());
		}

		try{
			pd2 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("mergeSaveCartItemMessage"), params,"POST");
			JSONObject json = new JSONObject(pd2.readInputStream());

			//System.out.println("mergeSaveCartItemMessage json ="+json.toString());
			//System.out.println("Messages returned from method "+json.getString("atgResponse"));
			if(!json.getString("atgResponse").equalsIgnoreCase("null"))
				result = true;


			assertTrue(result);



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
