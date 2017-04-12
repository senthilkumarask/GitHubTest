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
 * Test cases for email registry recommendation.
 * @author apan25
 *
 */
public class TestEmailGiftRegistryRecommendation extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestEmailGiftRegistryRecommendation(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to send email registry recommendation
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testEmailGiftRegistryRecommendation() throws IOException, JSONException, RestClientException{
		RestSession session = getNewHttpSession();

		params.put("registryURL",(String)getObject("registryURL"));
		params.put("recipientEmail",(String)getObject("recipientEmail"));
		params.put("senderEmail",(String)getObject("senderEmail"));
		params.put("regFirstName",(String)getObject("regFirstName"));
		params.put("regLastName",(String)getObject("regLastName"));
		params.put("registryName",(String)getObject("registryName"));
		params.put("message",(String)getObject("message"));
		params.put("eventType",(String)getObject("eventType"));
		params.put("registryId",(String)getObject("registryId"));

		//set params required to make request
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		//call email a friend handler

		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("emailRegistry"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println("Response: " + response);

		//	parse response
		JSONObject json = new JSONObject(response);
		Boolean result = (Boolean) json.get("result");
		System.out.println("Email Gift Registry Recommendation message json="+result);
		assertTrue("Result is false", result);
	}


	/**
	 * test to send email registry recommendation exception scenario
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testEmailGiftRegistryRecommendationException() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();

		//set params required to make request
		params.put("registryURL",(String)getObject("registryURL"));
		params.put("recipientEmail",(String)getObject("recipientEmail"));
		params.put("senderEmail",(String)getObject("senderEmail"));
		params.put("regFirstName",(String)getObject("regFirstName"));
		params.put("regLastName",(String)getObject("regLastName"));
		params.put("registryName",(String)getObject("registryName"));
		params.put("message",(String)getObject("message"));
		params.put("eventType",(String)getObject("eventType"));
		params.put("registryId",(String)getObject("registryId"));

		//set params required to make request
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("emailRegistry"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

		//	parse response
		JSONObject json = new JSONObject(response);
		String formExceptions = json.getString("formExceptions");
		System.out.println("Email Registry Recommendation formExceptions json= "+formExceptions);
		assertNotNull(formExceptions);

	}

}
