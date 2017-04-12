/**
 * 
 */
package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.rest.framework.BaseTestCase;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

/**
 * @author snaya2
 *
 */
public class TestUpdateCartOrder extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestUpdateCartOrder(String name) {
		super(name);
		
	}


	/**
	 * Test for Update Cart
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateItemsToCart() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession(); 
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			//mSession.login();
	 		// add to cart	
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", (String) getObject("jsonResultString"));
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			}
	 			
	 			RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getOrderObject"), params,"GET");
	 			
                String responseData2 = pd1.readInputStream();
                
                if (responseData2 != null) {

                	JSONObject json1 = new JSONObject(responseData2);
                	
                	JSONObject current = (JSONObject)json1.getJSONObject("current");
                	System.out.println("Count:" + current.get("totalCommerceItemCount"));
                	
                	JSONArray commerceItems = current.getJSONArray("commerceItems");
                	params = (HashMap) getControleParameters();
                	params.put("atg-rest-return-form-handler-properties", "true");
        	        StringBuffer sb = new StringBuffer(0);
        	        int count = 22;
                	for (int i = 0; i < commerceItems.length(); i++){
                		JSONObject element = (JSONObject) commerceItems.get(i);
                		String commerceItemId = (String) element.get("id");              			
            			sb = sb.append(commerceItemId+"="+count+";");
            			count++;
                	}
                	System.out.println("sb= "+sb);
                	params.put("updateCartInfoSemiColonSeparated", sb.toString());
                	params.put("setOrderSuccessURL", (String) getObject("setOrderSuccessURL"));
                	params.put("setOrderErrorURL", (String) getObject("setOrderErrorURL"));
                	params.put("atg-rest-return-form-handler-properties", "true");
        			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateCartRequest"), params,"POST");
        			
        			JSONObject json2 = new JSONObject(pd2.readInputStream());
        			 String result2 = null;
        	 			System.out.println(json2);
        	 			if(json2.has("formExceptions")){
        	 				result2 = json2.getString("formExceptions");
        	 			}
        	 			assertNull(result2);
        	 			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getOrderObject"), params,"GET");
                        System.out.println("Getting Order Object....");  
                        
                        String responseData3 = pd3.readInputStream();
                        System.out.println("responseData3:" + responseData3);
                        
                        if (responseData3 != null) {

                        	JSONObject json3 = new JSONObject(responseData3);
                        	
                        	JSONObject current3 = (JSONObject)json3.getJSONObject("current");
                        	System.out.println("commerceItems"+((JSONArray)current3.get("commerceItems")).length()); 
                        	System.out.println("Count:" + current3.get("totalCommerceItemCount"));
                        	assertEquals(current3.get("totalCommerceItemCount"), 45);
                        }
                }
	 		 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * Test for Update Cart Error Scenario
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateItemsToCartError() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession(); 
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			//mSession.login();
	 		// add to cart	
			params = (HashMap) getControleParameters();
			params.put("jsonResultString", (String) getObject("jsonResultString"));
			params.put("atg-rest-return-form-handler-properties", "true");
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			}
	 			
	 			RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getOrderObject"), params,"GET");
	 			
                String responseData2 = pd1.readInputStream();
                
                if (responseData2 != null) {

                	JSONObject json1 = new JSONObject(responseData2);
                	
                	JSONObject current = (JSONObject)json1.getJSONObject("current");
                	System.out.println("Count:" + current.get("totalCommerceItemCount"));
                	
                	JSONArray commerceItems = current.getJSONArray("commerceItems");
                	params = (HashMap) getControleParameters();
        	        StringBuffer sb = new StringBuffer(0);
        	        int count = -22;
                	for (int i = 0; i < commerceItems.length(); i++){
                		JSONObject element = (JSONObject) commerceItems.get(i);
                		String commerceItemId = (String) element.get("id");                		
            			params.put(commerceItemId, count);            			
            			sb = sb.append(commerceItemId+"="+count+";");
            			count++;
                	}
                	System.out.println("sb= "+sb);
                	params.put("updateCartInfoSemiColonSeparated", sb.toString());
                	params.put("setOrderSuccessURL", (String) getObject("setOrderSuccessURL"));
                	params.put("setOrderErrorURL", (String) getObject("setOrderErrorURL"));
                	params.put("atg-rest-return-form-handler-properties", "true");
        			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateCartRequest"), params,"POST");
        			
        			
        			JSONObject json2 = new JSONObject(pd2.readInputStream());
        			 String result2 = null;
        	 			System.out.println(json2);
        	 			if(json2.has("formExceptions")){
        	 				result2 = json2.getString("formExceptions");
        	 			}
        	 			assertNotNull(result2);
        	 			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getOrderObject"), params,"GET");
                        System.out.println("Getting Order Object....");  
                        
                        String responseData3 = pd3.readInputStream();
                        System.out.println("responseData3" + responseData3);
                        if (responseData3 != null) {

                        	JSONObject json3 = new JSONObject(responseData3);
                        	
                        	JSONObject current3 = (JSONObject)json3.getJSONObject("current");
                        	System.out.println("commerceItems"+((JSONArray)current3.get("commerceItems")).length()); 
                        	System.out.println("Count:" + current3.get("totalCommerceItemCount"));
                        	assertEquals(current3.get("totalCommerceItemCount"), 6);
                        }
                        
                }
	 			
	 		 
	 		 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Test for Update Cart
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateLTLItemsToCart() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession(); 
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			//mSession.login();
	 		// add to cart	
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", (String) getObject("jsonResultString"));
			 pd = mSession.createHttpRequest("http://" +  getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			}
	
	 			RestResult pd1 = mSession.createHttpRequest("http://" +  getHost() +":" + getPort()+(String) getObject("getOrderObject"), params,"GET");
	
                String responseData2 = pd1.readInputStream();
	
                if (responseData2 != null) {
	
                	JSONObject json1 = new JSONObject(responseData2);
	
                	JSONObject current = (JSONObject)json1.getJSONObject("current");
                	System.out.println("Count:" + current.get("totalCommerceItemCount"));
                	
                	JSONArray commerceItems = current.getJSONArray("commerceItems");
                	params = (HashMap) getControleParameters();
                	params.put("atg-rest-return-form-handler-properties", "true");
        	        StringBuffer sb = new StringBuffer(0);
        	        int count = 2;
                	for (int i = 0; i < commerceItems.length(); i++){
                		JSONObject element = (JSONObject) commerceItems.get(i);
                		String commerceItemId = (String) element.get("id");  
                		if(!((String)element.get("commerceItemClassType")).equalsIgnoreCase("ltlDeliveryChargeCommerceItem")) {
                			sb = sb.append(commerceItemId+"="+count+";");
}
            			count++;
                	}
                	System.out.println("sb= "+sb);
                	params.put("updateCartInfoSemiColonSeparated", sb.toString());
                	params.put("setOrderSuccessURL", (String) getObject("setOrderSuccessURL"));
                	params.put("setOrderErrorURL", (String) getObject("setOrderErrorURL"));
                	params.put("atg-rest-return-form-handler-properties", "true");
        			RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateCartRequest"), params,"POST");
        			
        			JSONObject json2 = new JSONObject(pd2.readInputStream());
        			 String result2 = null;
        	 			System.out.println(json2);
        	 			if(json2.has("formExceptions")){
        	 				result2 = json2.getString("formExceptions");
        	 			}
        	 			assertNull(result2);
        	 			RestResult pd3 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getOrderObject"), params,"GET");
                        System.out.println("Getting Order Object....");  
                        
                        String responseData3 = pd3.readInputStream();
                        System.out.println("responseData3:" + responseData3);
                        
                        if (responseData3 != null) {
                        	JSONObject json3 = new JSONObject(responseData3);
                        	JSONObject current3 = (JSONObject)json3.getJSONObject("current");
                        	System.out.println("commerceItems"+((JSONArray)current3.get("commerceItems")).length()); 
                        	System.out.println("Count:" + current3.get("totalCommerceItemCount"));
                        	assertEquals(current3.get("totalCommerceItemCount"), 4);
                        }
                }
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
}
