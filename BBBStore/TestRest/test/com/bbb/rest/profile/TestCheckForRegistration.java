package com.bbb.rest.profile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCheckForRegistration extends BaseTestCase {
	
	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;
	
	public TestCheckForRegistration(String name) {
		super(name);
	}
	
	protected void setUp() {
		params = getControleParameters();
	}
	
    @SuppressWarnings({ "unchecked" })
    public void testProfileAvailabilityCheck() throws JSONException, IOException, RestClientException{
    	session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);
        session.setUseHttpsForLogin(false);
        RestResult testResult;
        
			session.login();
			params.put("arg1", (String)getObject("arg1"));
			params.put("X-bbb-site-id", (String)getObject("siteId"));
			testResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("checkForRegistrationRequest"), params,"POST");
			
			JSONObject json = new JSONObject(testResult.readInputStream());
			 String result = null;	
			 if(json.has("atgResponse")){
				result = json.getString("atgResponse");
			 }
			 assertEquals((String)getObject("expectedResult"), result);
       
	}
	
    
    @SuppressWarnings({ "unchecked" })
    public void testCheckForRegistration() throws JSONException, IOException, RestClientException{
    	session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);
        session.setUseHttpsForLogin(false);
        RestResult testResult;
        
			session.login();
			params.put("arg1", (String)getObject("arg1"));
			testResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("checkForRegistrationRequest"), params,"POST");
			String responseData = testResult.readInputStream();	
			System.out.println(responseData);
//			if (responseData != null) {
//<atgResponse>profile_already_exist</atgResponse>
//				JSONObject json = new JSONObject(responseData);
//			 
//			}
			
			assertNotNull(testResult.readInputStream());        
       
	}

    @SuppressWarnings({ "unchecked" })
    public void testCheckForRegistrationError() throws JSONException, IOException{
    	session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);
        session.setUseHttpsForLogin(false);
        RestResult testResult;
        
        try {
			session.login();
			params.put("arg1", (String)getObject("arg1"));
			testResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("checkForRegistrationRequest"), params,"POST");
			String responseData = testResult.readInputStream();	
			System.out.println(responseData);
			if (responseData != null) {

				JSONObject json = new JSONObject(responseData);
			 
			}
			
			assertNotNull(testResult.readInputStream());
        
        } catch (RestClientException e) {
        	String errorMessage=e.getMessage();
        	System.out.println(errorMessage);
			if(errorMessage.contains("err_profile_not_found:Profile not found")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}
     
}