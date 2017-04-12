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
 * Test cases Tell A friend request
 * @author njai13
 *
 */
public class TestEmailRegistry extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestEmailRegistry(String name) {
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
	public void testEmailRegistry() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		params.put("registryURL",(String)getObject("registryURL"));
		params.put("recipientEmail",(String)getObject("recipientEmail"));
		params.put("message",(String)getObject("message"));
		params.put("senderEmail",(String)getObject("senderEmail"));
		params.put("eventType",(String)getObject("eventType"));
		
		params.put("eventDate",(String)getObject("eventDate"));
		params.put("subject",(String)getObject("subject"));
		params.put("regFirstName",(String)getObject("regFirstName"));
		params.put("regLastName",(String)getObject("regLastName"));
		params.put("title",(String)getObject("title"));
		params.put("coRegFirstName",(String)getObject("coRegFirstName"));
		params.put("registryId",(String)getObject("registryId"));
		params.put("registryName",(String)getObject("registryName"));
		params.put("daysToGo",(Long)getObject("daysToGo"));
		params.put("coRegLastName",(String)getObject("coRegLastName"));
        params.put("successURL","atg-rest-ignore-redirect");
        params.put("errorURL","atg-rest-ignore-redirect");

		//set params required to make request
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		params.put("atg-rest-return-form-handler-properties", "true");
		//call email a friend handler

		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("emailRegistry"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println("Response: " + response);

		//	parse response
		String result = null;
		JSONObject json = new JSONObject(response);
		if(json.has("errorMessages"))
		{
			 result=json.getString("errorMessages");
			 System.out.println("Email Registry error message json="+result);
				assertNotNull(result);
		}
		else{
		assertNull(result);
		}
		
	}


	/**
	 * test to send email registry request exception scenario
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testEmailRegistryException() throws IOException, JSONException, RestClientException{
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

		//call email a friend handler
		params.put("successURL","atg-rest-ignore-redirect");
        params.put("errorURL","atg-rest-ignore-redirect");
        params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("emailRegistry"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

		//	parse response
		JSONObject json = new JSONObject(response);
		String errorMessages=json.getString("errorMessages");
		System.out.println("Email Registry errorMessages json="+errorMessages);
		assertNotNull(errorMessages);

	}

}
