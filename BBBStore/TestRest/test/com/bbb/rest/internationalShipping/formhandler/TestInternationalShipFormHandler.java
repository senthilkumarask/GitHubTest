package com.bbb.rest.internationalShipping.formhandler;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestInternationalShipFormHandler extends BaseTestCase {

	public TestInternationalShipFormHandler(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	
	/**
	 * Test for Cart Validation
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCartValidate() throws RestClientException,
			JSONException, IOException {
		
		mSession = getNewHttpSession(); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult =  null;
		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 JSONObject json = new JSONObject(restResult.readInputStream());
		 String result = null;
		 
		 System.out.println("result ::: "+json);
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 			}
 	      
 			params.put("userSelectedCountry", (String) getObject("userSelectedCountry"));
			params.put("userSelectedCurrency", (String) getObject("userSelectedCurrency"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("successURL","atg-rest-ignore-redirect");
			params.put("errorURL","atg-rest-ignore-redirect");
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("handleCurrencyCountrySelector"), params, "POST");
			
			String responseData = restResult.readInputStream();
			System.out.println(responseData);
			
			if (null != responseData) {
				JSONObject json1 = new JSONObject(responseData);
				assertNotNull(json1);
					
				}
		 try {
			mSession.logout();
		  } catch (RestClientException e) {
			e.printStackTrace();
		}
        
	}
	
	
	
	/**
	 * Test for Envoy Cart Validation
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testEnvoyCartValidate() throws RestClientException,
			JSONException, IOException {
		
		mSession = getNewHttpSession(); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult restResult =  null;
		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		restResult = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		 JSONObject json = new JSONObject(restResult.readInputStream());
		 String result = null;
		 
		 System.out.println("result ::: "+json);
 			
 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 			}
 	      
 			params.put("userSelectedCountry", (String) getObject("userSelectedCountry"));
			params.put("userSelectedCurrency", (String) getObject("userSelectedCurrency"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("successURL","atg-rest-ignore-redirect");
			params.put("errorURL","atg-rest-ignore-redirect");
			restResult = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("handleCurrencyCountrySelector"), params, "POST");
			
			String responseData = restResult.readInputStream();
			System.out.println(responseData);
			
			if (null != responseData) {
				JSONObject json1 = new JSONObject(responseData);
				assertNotNull(json1);
					
				}
		 try {
			mSession.logout();
		  } catch (RestClientException e) {
			e.printStackTrace();
		}
        
	}
}
