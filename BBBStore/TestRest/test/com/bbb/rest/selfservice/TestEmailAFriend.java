package com.bbb.rest.selfservice;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;
/**
 * Test cases Tell A friend request
 * @author njai13
 *
 */
public class TestEmailAFriend extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestEmailAFriend(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to send email a friend request
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testEmailAFriend() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();


		params.put("recipientName",(String)getObject("recipientName"));
		params.put("recipientEmail",(String)getObject("recipientEmail"));
		params.put("senderEmail",(String)getObject("senderEmail"));
		params.put("currentSite",(String)getObject("currentSite"));
		params.put("currentPageURL",(String)getObject("currentPageURL"));
		params.put("message",(String)getObject("message"));
		//set params required to make request
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));

		//call email a friend handler

		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("emailAFriend"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

		//	parse response
		JSONObject json = new JSONObject(response);
		String result=json.getString("result");
		assertNotNull(result);

	}


	/**
	 * test to send email a friend request
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testEmailAFriendException() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();



		params.put("recipientName",(String)getObject("recipientName"));
		//params.put("recipientEmail",(String)getObject("recipientEmail"));


		//set params required to make request
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));

		//call email a friend handler

		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("emailAFriend"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

		//	parse response
		JSONObject json = new JSONObject(response);
		String formExceptions=json.getString("formExceptions");
		assertNotNull(formExceptions);

	}

}
