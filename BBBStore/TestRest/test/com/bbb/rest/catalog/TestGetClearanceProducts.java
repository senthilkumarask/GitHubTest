package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetClearanceProducts extends BaseTestCase {
	
	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;
	
	public TestGetClearanceProducts(String name) {
		super(name);
	}
	
	protected void setUp() {
		params = getControleParameters();
	}
	
    @SuppressWarnings({ "unchecked" })
    public void testGetClearanceProducts() throws JSONException, IOException, RestClientException{
    	session = getNewHttpSession("BedBathUS");
		//Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);
        session.setUseHttpsForLogin(false);
        RestResult testResult;
        
			session.login();
			System.out.println("http://" + getHost() + ":" + getPort() + (String)getObject("getClearanceProductsRequest"));
			testResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("getClearanceProductsRequest"), params,"POST");
			String responseData = testResult.readInputStream();	
			JSONArray atgResponse = null;
			System.out.println(responseData);
			if (responseData != null) {
				JSONObject json = new JSONObject(responseData);
				atgResponse = (JSONArray) json.get("atgResponse");
			}
						
			assertNotSame(0, atgResponse.length());        
       
	}

    
     
}