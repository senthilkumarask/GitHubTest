/**
 * 
 */
package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.rest.framework.BaseTestCase;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

/**
 * @author snaya2
 *
 */
public class TestAddToCartOrder extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestAddToCartOrder(String name) {
		super(name);
		
	}

	
	
	public void testGetCurrentOrderDetailsTransientUser() throws RestClientException, JSONException, IOException{

		RestResult pd =  null;
		try {


			params = (HashMap) getControleParameters();
			params.put("arg1", true);
			pd = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
					+ (String) getObject("currentOrderDetailsRequest"), params, "POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			System.out.println(jsonResponseObj);

			String transientFlag = jsonResponseObj.getString("transientFlag");
			assertEquals("true", transientFlag);

			String itemCount = jsonResponseObj.getString("cartItemCount");
			assertEquals("0", itemCount);

		}
		finally {
			if(pd != null)
				pd = null;
//			try {
//				mSession.logout();
//			} catch (RestClientException e) {
//				e.printStackTrace();
//			}
		}

	}
	

	/**
	 * Test for Add to cart - Single Item 
	 * @throws JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAddSingleItemToCart() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession(); 
		RestResult pd =  null;
//		mSession.login();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 JSONObject json = new JSONObject(pd.readInputStream());
		 String result = null;
		 
		 System.out.println("result ::: "+json);
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 			}
 		 assertNull(result);
         if(pd != null)
			pd = null;
		 try {
			mSession.logout();
		  } catch (RestClientException e) {
			e.printStackTrace();
		}
		

	}
	
	
	/**
	 * Test Error Scenario for Add to cart - Single Item with Invalid SKU
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAddSingleItemToCartInvalidSKU() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
//		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 JSONObject json = new JSONObject(pd.readInputStream());
		 System.out.println("result ::: "+json);
		 String result = null;
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 			}
 		 assertNotNull(result);
		 System.out.println("formExceptions="+json.getString("formExceptions"));
     	if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}

	}
	
	
	/**
	 * Test for Add to cart - Multiple Item 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAddMultipleItemToCart() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
//		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 JSONObject json = new JSONObject(pd.readInputStream());
		 System.out.println("result ::: "+json);
		 String result = null;
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 			}
 		 assertNull(result);
  		if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}

	}
	
	
	/**
	 * Test for Merge cart after Login 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMergeItemToCart() throws RestClientException, IOException, JSONException{
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
//		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString1"));
		 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 
		 params = (HashMap) getControleParameters();
         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
         params.put("value.login", (String) getObject("login"));
         params.put("value.password", (String) getObject("password"));
         
         RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
         
         String responseData2 = pd2.readInputStream();
         
         if (responseData2 != null) {

         	JSONObject json = new JSONObject(responseData2);
         	System.out.println("Login Status : "+json.toString());
    
         }
         
         params = (HashMap) getControleParameters();
         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		 params.put("jsonResultString", (String) getObject("jsonResultString2"));
		 params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		 params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		 params.put("atg-rest-return-form-handler-properties", "true"); 
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 JSONObject json = new JSONObject(pd3.readInputStream());
		System.out.println("add to cart json ="+json.toString());
		String result = null;
		 			
		if(json.has("formExceptions")){
			result = json.getString("formExceptions");
		}
		 assertNull(result);
		
		if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		

	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAddLtlSingleItemToCart() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession(); 
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
//		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 JSONObject json = new JSONObject(pd.readInputStream());
		 String result = null;
	
		 System.out.println("result ::: "+json);
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
}
 		 assertNull(result);
         if(pd != null)
			pd = null;
		 try {
			mSession.logout();
		  } catch (RestClientException e) {
			e.printStackTrace();
		}
		

	}
	
	
}
