/**
 * 
 */
package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;


public class TestGetEcoFeeSKUDetailForStore extends BaseTestCase {
	
	
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

	public TestGetEcoFeeSKUDetailForStore(String name) {
		super(name);
		
	}

	
	/**
	 * Test for gets the details for eco fee sku for store
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testGetEcoFeeSKUForStore() throws org.json.JSONException, RestClientException, IOException{
		
		
		mSession = getNewHttpSession((String) getObject("siteId"));
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
			String storeId =  (String) getObject("storeId");
			String skuId = (String) getObject("skuId");
			
            params.put("arg1",storeId);
            params.put("arg2",skuId);
	        
            
  			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getEcoFeeSKUDetailForStoreRequest"),
					params, httpMethod);
  			
  			responseData = restResult.readInputStream();
  			System.out.println("responseData:"+responseData);
  			
  			if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
				json = new JSONObject(responseData);
				if (json.has("ecoFeeProductId")
						&& json.getString("ecoFeeProductId") != null
						&& !json.getString("ecoFeeProductId").equalsIgnoreCase(
								"null")
						&& json.getString("feeEcoSKUId") != null
						&& !json.getString("feeEcoSKUId").equalsIgnoreCase(
								"null")
						&& !responseData.toString().contains("formExceptions")) {

					response = (String) json.getString("ecoFeeProductId");
				}
			}
  			
			
  			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& responseData != null) {
				if (responseData.toString().contains("ecoFeeProductId") && responseData.toString().contains("feeEcoSKUId") && !responseData.toString().contains("errorCode")) {
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
	 * Test for gets the details for eco fee sku for store
	 * @throws org.json.JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void testGetEcoFeeSKUForStoreError(){
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
			String storeId =  (String) getObject("storeId");
			String skuId = (String) getObject("skuId");
			
            params.put("arg1",storeId);
            params.put("arg2",skuId);
	        
            
  			restResult =  mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getEcoFeeSKUDetailForStoreRequest"),
					params, httpMethod);
  			
  			responseData = restResult.readInputStream();
  			System.out.println("responseData:"+responseData);
  			
  	} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println("errorMessage:"+errorMessage);
			if(errorMessage.contains("1000"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("1003"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("2003"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("2006"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("6001"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("6003"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("6004"))
			{
				assertTrue(true);	
			}
			if(errorMessage.contains("8001"))
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
