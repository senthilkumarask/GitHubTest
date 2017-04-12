package com.bbb.rest.mapquest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestSearchInStore extends BaseTestCase {
	RestSession session = null;
	private String NO_ERROR = "NO_ERROR";
	@SuppressWarnings("rawtypes")
	Map params = null;


	public TestSearchInStore(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	public void testSearchInStorePostal() throws JSONException, RestClientException, IOException {
				
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		JSONObject json = null; 
		RestResult restResult = null;
		String response = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			
			Map<String, String> inputparam = new HashMap<String, String>();
			
			inputparam.put("radius", (String) getObject("radius"));
			inputparam.put("searchString", (String) getObject("searchString"));
			inputparam.put("pageKey", (String) getObject("pageKey"));
			inputparam.put("pageNumber", (String) getObject("pageNumber"));
			inputparam.put("pageSize", (String) getObject("pageSize"));
			inputparam.put("skuID", (String) getObject("skuId"));
			inputparam.put("orderedQty", (String) getObject("orderedQty"));
			params = (HashMap) getControleParameters();
			params.put("arg1", inputparam);
			params.put("atg-rest-http-method", "POST");
			params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));	
			
			restResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("searchInStore"), params, "POST");
			
			String responseData = restResult.readInputStream();
			System.out.println("responseData:"+responseData);
			
            if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
				
            	json = new JSONObject(responseData);
            	if (json.has("objStoreDetails")) {
					response = (String) json.getString("objStoreDetails");
					json = new JSONObject(response);
					if(json.has("storeDetails")){
						response = (String) json.getString("storeDetails");
						
					}else if(json.has("storeAddressSuggestion")){
						response = (String) json.getString("storeAddressSuggestion");
					}
				}
            }
           
            
            assertNotNull(response);
          
		}finally {
			
				if(restResult != null)
					restResult = null;
				try {
					session.logout();
				} catch (RestClientException e) {
					e.printStackTrace();
				}
		}
		
		
	}
	

	public void testSearchInStoreByError() throws JSONException, RestClientException, IOException  {
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		
		RestResult restResult = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			
			Map<String, String> inputparam = new HashMap<String, String>();
			inputparam.put("radius", (String) getObject("radius"));
			inputparam.put("searchString", (String) getObject("searchString"));
			inputparam.put("pageKey", (String) getObject("pageKey"));
			inputparam.put("pageNumber", (String) getObject("pageNumber"));
			inputparam.put("pageSize", (String) getObject("pageSize"));
			inputparam.put("skuID", (String) getObject("skuId"));
			inputparam.put("orderedQty", (String) getObject("orderedQty"));
			params = (HashMap) getControleParameters();
			params.put("arg1", inputparam);
			params.put("atg-rest-http-method", "POST");
			params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));	
			
			restResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("searchInStore"), params, "POST");
			
			String responseData = restResult.readInputStream();
			System.out.println("responseData:"+responseData);
			
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("err_store_mandatory_input")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		    
		}finally {
			
				if(restResult != null)
					restResult = null;
				try {
					session.logout();
				} catch (RestClientException e) {
					e.printStackTrace();
				}
		}

	}
  		
  		
  	
	public void testSearchStorePageKey() throws JSONException, RestClientException, IOException  {
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		JSONObject json = null; 
		RestResult restResult = null;
		String response = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			
			Map<String, String> inputparam = new HashMap<String, String>();
			
			inputparam.put("radius", (String) getObject("radius"));
			inputparam.put("searchString", (String) getObject("searchString"));
			inputparam.put("pageKey", (String) getObject("pageKey"));
			inputparam.put("pageNumber", (String) getObject("pageNumber"));
			inputparam.put("pageSize", (String) getObject("pageSize"));
			inputparam.put("skuID", (String) getObject("skuId"));
			inputparam.put("orderedQty", (String) getObject("orderedQty"));
			params = (HashMap) getControleParameters();
			params.put("arg1", inputparam);
			params.put("atg-rest-http-method", "POST");
			params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));	
			
			restResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("searchInStore"), params, "POST");
			String pageKey = null;
			String responseData = restResult.readInputStream();
			System.out.println("responseData:"+responseData);
			
            if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
				
            	json = new JSONObject(responseData);
				if (json.has("objStoreDetails")) {
					response = (String) json.getString("objStoreDetails");
					json = new JSONObject(response);
					if(json.has("storeDetails")){
						if(json.has("pageKey") && !json.isNull("pageKey")){
							pageKey = (String) json.get("pageKey");
						}
						response = (String) json.getString("storeDetails");
						
					}else if(json.has("storeAddressSuggestion") && !json.isNull("storeAddressSuggestion")){
						response = (String) json.getString("storeAddressSuggestion");
					}
				}
            }
           
          
            assertNotNull(response);
          
            if(pageKey != null){
            inputparam = new HashMap<String, String>();
			inputparam.put("radius", (String) getObject("radius"));
			inputparam.put("searchString", (String) getObject("searchString"));
			inputparam.put("pageKey", pageKey);
			inputparam.put("pageNumber", (String) getObject("pageNumber"));
			inputparam.put("pageSize", (String) getObject("pageSize"));
			inputparam.put("skuID", (String) getObject("skuId"));
			inputparam.put("orderedQty", (String) getObject("orderedQty"));
			params = (HashMap) getControleParameters();
			params.put("arg1", inputparam);
			params.put("atg-rest-http-method", "POST");
			params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));	
			
			restResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("searchInStore"), params, "POST");
			
			responseData = restResult.readInputStream();
			System.out.println("responseData:"+responseData);
			
			 if (params.get("atg-rest-output").toString().equalsIgnoreCase("json") && responseData != null) {
					
	            	json = new JSONObject(responseData);
	            	if (json.has("objStoreDetails")) {
						response = (String) json.getString("objStoreDetails");
						json = new JSONObject(response);
						if(json.has("storeDetails")){
							response = (String) json.getString("storeDetails");
							
						}else if(json.has("storeAddressSuggestion")){
							response = (String) json.getString("storeAddressSuggestion");
						}
					}
	            }
			 assertNotNull(response);
			 
            }
		}finally {
			
				if(restResult != null)
					restResult = null;
				try {
					session.logout();
				} catch (RestClientException e) {
					e.printStackTrace();
				}
		}
	}

	
	
	
	

}
