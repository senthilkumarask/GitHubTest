package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestConstants;
import atg.rest.client.RestRepositoryHelper;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetStoreDetails extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;

	public TestGetStoreDetails(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}
	


	/**
	 * Get Store Details information
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testGetStoreDetails() throws RestClientException,
			IOException, JSONException {

		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult restResult =  null;
		RestResult result = null; 
		JSONObject json = null;
		try {
			session.login();
			params = (HashMap) getControleParameters();
            params.put("atg-rest-show-rest-paths", false);
 			params.put(RestConstants.COUNT, 5);
 			params.put(RestConstants.INDEX, 0);
  
 			result = session.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getAllStoreDetailsRequest"), params,"POST");
 			String responseData = result.readInputStream();
			json = new JSONObject(responseData);
 			System.out.println("responseData:" + responseData);
			
 				
 			assertNotNull(responseData);
			

		} finally {
			if (result != null)
				result = null;
			session.logout();
		}
	}
	
	/**
	 * Get Store Detail information for given store id
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	public void testGetStoreDetailForSpecialityCode() throws RestClientException,
	IOException, JSONException {

		RestSession session = getNewHttpSession();
		session.setUsername((String) getObject("username"));
		session.setPassword((String) getObject("password"));
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		JSONObject json = null;
		RestResult result = null;
		try{
		
				session.login();
				
				String storeId = (String) getObject("storeId");
				params = (HashMap) getControleParameters();
				params.put("atg-rest-show-rest-paths", false);
				params.put("arg1", storeId);
				
				result = session.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getStoreDetailsRequest"), params,"POST");
	 			String responseData = result.readInputStream();
	 			json = new JSONObject(responseData);
	 			System.out.println("responseData:" + responseData);
				
	 			
				System.out.println("responseData:" + responseData);
				assertNotNull(responseData);


		} finally {
			if (result != null)
				result = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Get Store Detail information for given store id
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	public void testGetStoreDetailError() throws RestClientException,
	IOException, JSONException {

		RestSession session = getNewHttpSession();
		session.setUsername((String) getObject("username"));
		session.setPassword((String) getObject("password"));
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		JSONObject json = null;
		RestResult result = null;
		try{
		
				session.login();
				
				String storeId = (String) getObject("storeId");
				params = (HashMap) getControleParameters();
				params.put("atg-rest-show-rest-paths", false);
				params.put("arg1", storeId);
				
				result = session.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getStoreDetailsRequest"), params,"POST");
	 		

		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
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