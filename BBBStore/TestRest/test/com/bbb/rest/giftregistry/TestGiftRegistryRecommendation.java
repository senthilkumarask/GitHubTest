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
 * Test cases for Gift Registry recommendation Pending tab.
 * @author apanssha5325
 *
 */
public class TestGiftRegistryRecommendation extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestGiftRegistryRecommendation(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to to get Gift Registry recommendation
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testGiftRegistryRecommendation() throws IOException, JSONException, RestClientException{
		RestSession session = getNewHttpSession();

		params.put("arg1",(String)getObject("registryId"));
		params.put("arg2",(String)getObject("tabId"));
		params.put("arg3",(String)getObject("sortOption"));
		params.put("arg4",(String)getObject("pPageSize"));
		params.put("arg5",(String)getObject("pPageNum"));
		params.put("arg6",(String)getObject("eventTypeCode"));
		
		//set params required to make request
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		//call email a friend handler
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("getRecommendations"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);

		//	parse response
		JSONObject json = new JSONObject(response);
		Object result = (Object) json.get("atgResponse");

		if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && result != null) {
				json = new JSONObject(result);
			if (json.has("atgResponse")
					&& json.getString("atgResponse") != null
					&& !json.getString("atgResponse").equalsIgnoreCase(
							"null")
					&& !result.toString().contains("formExceptions")) {

				response = (String) json.getString("atgResponse");
			}
		}
      	assertNotNull(result);
	}
	
	
	/**
	 * Test to get Gift registry recommendation pending tab exception scenario
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testGiftRegistryRecommendationException() throws IOException, JSONException, RestClientException{
		
		RestSession session = getNewHttpSession();
		params.put("arg2",(String)getObject("tabId"));
		params.put("arg3",(String)getObject("sortOption"));
		params.put("arg4",(String)getObject("pPageSize"));
		params.put("arg5",(String)getObject("pPageNum"));
		params.put("arg6",(String)getObject("eventTypeCode"));
		
		//set params required to make request
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		RestResult result = null;
		try{
			result = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("getRecommendations"), params,"POST");
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			if(errorMessage.contains("4002")){
				assertTrue(true);				
			}
			if(errorMessage.contains("3008")){
				assertTrue(true);				
			}
			if(errorMessage.contains("2003")){
				assertTrue(true);				
			}	
		}finally{
			
			 if(result != null)
				 result = null;
			 try {
				 session.logout();
				 } catch (RestClientException e) {
					e.printStackTrace();
				 }
		}
	}
	
}
