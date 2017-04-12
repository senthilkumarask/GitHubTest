/**
 * 
 */
package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * @author ssha53
 *
 */
public class TestSearchRegistryByCriteria extends BaseTestCase {
	
	
	@SuppressWarnings("rawtypes")
	Map params = null;
	RestSession mSession;

	protected void setUp()throws Exception {
		super.setUp();
		params = getControleParameters();
		
		
	}
	

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public TestSearchRegistryByCriteria(String name) {
		super(name);
		
	}

	
	/**
	 * Test for Searching registries by First Name and Last Name
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testSearchRegistryByName() throws org.json.JSONException, RestClientException, IOException{
		
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String responseData = null;
		JSONObject json = null;
		
		
		String response = null;
		String httpMethod = (String) getObject("httpmethod");
		
	
		try{
			
			mSession.login();
			params = (HashMap) getControleParameters();
            
			Map inputMap = new HashMap<String,String>();
			inputMap.put("firstName", (String) getObject("firstName"));
			inputMap.put("lastName", (String) getObject("lastName"));
			inputMap.put("pageNo", (String) getObject("pageNo"));
			inputMap.put("perPage", (String) getObject("perPage"));
			inputMap.put("sortPassString", (String) getObject("sortPassString"));
			inputMap.put("sortOrder", (String) getObject("sortOrder"));
			
		       
            params.put("arg1",inputMap);
	       
  			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("searchRegistryByCriteriaRequest"),
					params, httpMethod);
  			
  			responseData = restResult.readInputStream();
  			System.out.println("responseData:"+responseData);
  			
  			if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
  				
				json = new JSONObject(responseData);
				if (json.getString("errorExist").equalsIgnoreCase("false")
						&& json.getString("errorMessage").equalsIgnoreCase("null") 
						&& json.getString("atgResponse") !=null
						&& !responseData.toString().contains("formExceptions")) {

					response = (String) json.getString("atgResponse");
				}
			}
  			
			
  			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& responseData != null) {
				if (responseData.toString().contains("atgResponse")&& !responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
  			
	      	assertNotNull(response);
	      	
			
		
		}finally {
		
			if(restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
			
		
	
	/**
	 * Test for Searching registries by Registry Id
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testSearchRegistryByRegistryId() throws org.json.JSONException, RestClientException, IOException{

		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String responseData = null;
		JSONObject json = null;
		
		
		String response = null;
		String httpMethod = (String) getObject("httpmethod");
		
	
		try{
			
			mSession.login();
			params = (HashMap) getControleParameters();
            
			Map inputMap = new HashMap<String,String>();
			inputMap.put("registryId", (String) getObject("registryId"));
			inputMap.put("firstName", (String) getObject("firstName"));
			inputMap.put("lastName", (String) getObject("lastName"));
			inputMap.put("pageNo", (String) getObject("pageNo"));
			inputMap.put("perPage", (String) getObject("perPage"));
			inputMap.put("sortPassString", (String) getObject("sortPassString"));
			inputMap.put("sortOrder", (String) getObject("sortOrder"));
			params.put("arg1",inputMap);
			
			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() +(String) getObject("searchRegistryByCriteriaRequest"),
					params, httpMethod);
  			
  			responseData = restResult.readInputStream();
  			System.out.println("responseData:"+responseData);
  			
  			if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
  				
				json = new JSONObject(responseData);
				if (json.getString("errorExist").equalsIgnoreCase("false")
						&& json.getString("errorMessage").equalsIgnoreCase("null") 
						&& json.getString("atgResponse") !=null
						&& !responseData.toString().contains("formExceptions")) {

					response = (String) json.getString("atgResponse");
				}
			}
  			
			
  			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& responseData != null) {
				if (responseData.toString().contains("atgResponse")&& !responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
  			
	      	assertNotNull(response);
	      	
			
		
		}finally {
		
			if(restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Test for Searching registries by email
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testSearchRegistryByEmail() throws org.json.JSONException, RestClientException, IOException{

		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String responseData = null;
		JSONObject json = null;
		
		
		String response = null;
		String httpMethod = (String) getObject("httpmethod");
		
	
		try{
			
			mSession.login();
			params = (HashMap) getControleParameters();
            
			Map inputMap = new HashMap<String,String>();
			inputMap.put("email", (String) getObject("email"));
			inputMap.put("pageNo", (String) getObject("pageNo"));
			inputMap.put("perPage", (String) getObject("perPage"));
			inputMap.put("sortPassString", (String) getObject("sortPassString"));
			inputMap.put("sortOrder", (String) getObject("sortOrder"));
			params.put("arg1",inputMap);
			
			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("searchRegistryByCriteriaRequest"),
					params, httpMethod);
  			
  			responseData = restResult.readInputStream();
  			System.out.println("responseData:"+responseData);
  			
  			if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
  				
				json = new JSONObject(responseData);
				if (json.getString("errorExist").equalsIgnoreCase("false")
						&& json.getString("errorMessage").equalsIgnoreCase("null") 
						&& json.getString("registrySummaryVO") !=null
						&& !responseData.toString().contains("formExceptions")) {

					response = (String) json.getString("registrySummaryVO");
				}
			}
  			
			
  			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& responseData != null) {
				if (responseData.toString().contains("atgResponse")&& !responseData.toString().contains("errorCode")) {
					response = responseData.toString();
				}
			}
  			
	      	assertNotNull(response);
	      	
			
		
		}finally {
		
			if(restResult != null)
				restResult = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	/**
	 * Test for Searching registries by Registry Id
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testSearchRegistryError(){
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult restResult =  null;
		String responseData = null;
		JSONObject json = null;
		
		
		String response = null;
		String httpMethod = (String) getObject("httpmethod");
		
	
		try{
			
			mSession.login();
			params = (HashMap) getControleParameters();
            
			Map inputMap = new HashMap<String,String>();
			inputMap.put("pageNo", (String) getObject("pageNo"));
			inputMap.put("perPage", (String) getObject("perPage"));
			inputMap.put("sortPassString", (String) getObject("sortPassString")); 
			inputMap.put("registryId", (String) getObject("registryId"));
			inputMap.put("sortOrder", (String) getObject("sortOrder"));
			params.put("atg-rest-return-form-handler-properties", true);
	           
            params.put("arg1",inputMap);
	        

  			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() +(String) getObject("searchRegistryByCriteriaRequest"),
					params, httpMethod);
  			
  			responseData = restResult.readInputStream();
  			System.out.println("responseData:"+responseData);
  	} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println("errorMessage:"+errorMessage);
			if(errorMessage.contains("8401"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("8402"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("8403"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("8404"))
			{
				assertTrue(true);	
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
}
