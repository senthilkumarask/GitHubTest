package com.bbb.rest.cart;

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
 * @author hbandl
 *
 */
public class TestPriceItem extends BaseTestCase {

	RestSession mSession;
	Map params = null;
	/**
	 * @param name
	 */
	public TestPriceItem(String name) {
		super(name);
		
	}

	protected void setUp() {
		params = getControleParameters();
	}
	
	/**
	 * Test for API to fetch price item on the basis of sku id 
	 * @throws IOException
	 * @throws JSONException 
	 *  
	 */
	public void testPriceItem() throws IOException, JSONException{
		
		mSession = getNewHttpSession(); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult result =  null;
		
		try {
			
			Map<String, String> products = new HashMap<String, String>();
	        products.put((String)getObject("product1"), (String)getObject("quantity1"));
	        products.put((String)getObject("product2"), (String)getObject("quantity2"));
	        
	        result = RestComponentHelper.executeMethod((String) getObject("addPriceItemRequest"), "getPriceItems", new Object[] {products}, params, mSession);
	        String responseData = result.readInputStream();
	        //System.out.println("responseData:" + responseData);
	        
	        if(responseData != null){
	        	
	        	try {
	        		JSONObject json = new JSONObject(responseData);
		      		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		      		JSONObject jsonResponse = jsonResponseObjArray.getJSONObject(0);
					String listPrice = jsonResponse.getString("listPrice");
					System.out.println("listPrice:" + listPrice);
					assertNotNull(listPrice);
	        		
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
	
	/**
	 * Test for API to fetch price item on the basis of sku id 
	 * @throws IOException
	 * @throws JSONException 
	 *  
	 */
	public void testPriceItemError() throws IOException, JSONException{
		
		mSession = getNewHttpSession(); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult result =  null;
		
		try {
			
			Map<String, String> products = null;
	        
	        result = RestComponentHelper.executeMethod((String) getObject("addPriceItemRequest"), "getPriceItems", new Object[] {products}, params, mSession);
	        String responseData = result.readInputStream();
	        //System.out.println("responseData:" + responseData);
	        
	        assertTrue(false);
	        
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
	
}
