package com.bbb.rest.selfservice;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
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
public class TestTellAFriend extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestTellAFriend(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to send tell a friend request
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testTellAFriendBabyBook() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		params.put("tellAFriendVO.senderFirstName",(String)getObject("senderFirstName"));
		params.put("tellAFriendVO.senderLastName",(String)getObject("senderLastName"));
		params.put("tellAFriendVO.senderEmailAddr",(String)getObject("senderEmailAddr"));
		params.put("tellAFriendVO.recipientFirstName",(String)getObject("recipientFirstName"));
		params.put("tellAFriendVO.recipientLastName",(String)getObject("recipientLastName"));
		params.put("tellAFriendVO.recipientEmailAddr",(String)getObject("recipientEmailAddr"));



		//set params required to make request
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));

		//call tell a friend handler
		 params.put("babyBookErrorURL", (String) getObject("babyBookErrorURL"));
         params.put("babyBookSuccessURL", (String) getObject("babyBookSuccessURL"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/com/bbb/selfservice/BabyBookFormHandler/requestTellAFriend", params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

	//	parse response
		JSONObject json = new JSONObject(response);
		String result=json.getString("result");
		System.out.println("TellAFriendBabyBook result="+result);
		assertNotNull(result);
	}
	
	
	/**
	 *  test to send tell a friend request with exceptions scenario
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testTellAFriendFormException() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();



		//set params required to make request
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));



		//call tell a friend handler
		 params.put("babyBookErrorURL", (String) getObject("babyBookErrorURL"));
         params.put("babyBookSuccessURL", (String) getObject("babyBookSuccessURL"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/com/bbb/selfservice/BabyBookFormHandler/requestTellAFriend", params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

	//	parse response
		JSONObject json = new JSONObject(response);
	
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("formExceptions"));

		String exception =jsonResponseObjArray.getString(0);
		System.out.println("TellAFriendBabyBook result="+exception);
		boolean trueB=exception.contains("First Name must be between 1 and 40 characters.");
		assertTrue(trueB);
	}
	
}
