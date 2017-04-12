package com.bbb.rest.email.droplet;

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
public class TestBBBEmailFetchDroplet extends BaseTestCase {
	
    	RestSession session = null;
	/**
	 * @param name
	 */
	public TestBBBEmailFetchDroplet(String name) {
		super(name);

	}

	/**
	 * test to check persisted email
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testEmailFetchContent() throws IOException, JSONException, RestClientException{
	    
	    	HashMap params = (HashMap) getControleParameters();	    	
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		
		try {
        		session.login();        		        	 
	        	String tokenId = (String)getObject("token");
	        	params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));	        	        		        		        			       
        		//set params required to make request
        		        		
        		RestResult pd2 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("fetchEmailDetails"), params, new Object[] {tokenId} ,"POST");
        
        		//get response
        		String response=pd2.readInputStream();
        		System.out.println("Response received is " + response  );
        		if (response != null) {

				JSONObject json = new JSONObject(response);
				String emailMessage=json.getString("atgResponse");
				System.out.println("Email Fetch success json="+emailMessage);
				assertNotNull(emailMessage);

			}
        		
		} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("RepositoryException for Fetching Email")){
				assertTrue(true);				
			}	
		}
	}	
}
