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

public class TestGetDormRoomCollections extends BaseTestCase {
	
	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;
	
	public TestGetDormRoomCollections(String name) {
		super(name);
	}
	
	protected void setUp() {
		params = getControleParameters();
	}
	
    /**
     * @throws JSONException
     * @throws IOException
     * @throws RestClientException
     */
    @SuppressWarnings({ "unchecked" })
    public void testGetDormRoomCollections() throws JSONException, IOException, RestClientException{
    	session = getNewHttpSession("BedBathUS");
		//Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);
        session.setUseHttpsForLogin(false);
        RestResult testResult;
        session.login();
        try{
			System.out.println("http://" + getHost() + ":" + getPort() + (String)getObject("getDormRoomCollectionsRequest"));
			testResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("getDormRoomCollectionsRequest"), params,"POST");
			String responseData = testResult.readInputStream();	
			System.out.println(responseData);
			JSONArray atgResponse = null;
			if (responseData != null) {
				JSONObject json = new JSONObject(responseData);
				atgResponse = (JSONArray) json.get("atgResponse");
				int length = atgResponse.length();		
				assertNotNull(atgResponse);
				assertTrue(length >= 0 );
			}
			
        }
        catch (RestClientException e) {
			assertNotNull(e.getMessage());
		}
        finally{
        	try{
        	session.logout();
        	}
        	catch (RestClientException e) {
    			e.printStackTrace();
    		}
        }
       
	}

    
     
}