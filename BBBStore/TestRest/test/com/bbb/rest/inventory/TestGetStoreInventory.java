/**
 * 
 */
package com.bbb.rest.inventory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestComponentHelper;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * @author Prashanth Bhoomula
 *
 */
public class TestGetStoreInventory extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestGetStoreInventory(String name) {
		super(name);
		
	}


	/**
	 * Test for Add to cart - Single Item 
	 * @throws JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetStoreInventory() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession(); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult result =  null;
		
		try {
			
			Object[] objects = new Object[2];
			//objects[0] = getObject("siteId");
			objects[0] = getObject("skuId");
			objects[1] = getObject("storeIds");
			
			result = RestComponentHelper
					.executeMethod(
							(String) getObject("getStoreInventoryRequest"),
							(String) getObject("methodName"), objects, params,
							mSession);
	        String responseData = result.readInputStream();
	        
	        if(responseData != null){
	        	
	        	try {
	        		JSONObject json = new JSONObject(responseData);
	        		int storeInventory = Integer.parseInt(json.getString("storeInventoryStock"));
					assertNotNull(storeInventory);
	        		
				} catch (Exception e) {
					e.printStackTrace();
				}
				
	        }
		}catch (RestClientException e) {
         	String errorMessage=e.getMessage();
         	System.out.println("errorMessage:" + errorMessage);
 			if(errorMessage.contains("8102:Mandatory parameter missing")){
 				assertTrue(true);				
 			}else{
 				assertFalse(true);
 			}	
 		} 		
	}

	private void assertResponse(RestResult pd) throws JSONException,
			IOException {
		JSONObject json = new JSONObject(pd.readInputStream());
        System.out.println("Output :- "+json);
		String result = null;
 			
 		if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 		}
 		assertNull(result);
	}
	
	
}
