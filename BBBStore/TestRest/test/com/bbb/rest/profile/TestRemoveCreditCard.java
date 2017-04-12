/**
 * 
 */
package com.bbb.rest.profile;

import java.io.IOException;
import java.util.HashMap;

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
public class TestRemoveCreditCard extends BaseTestCase  {

	public TestRemoveCreditCard(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for remove credit card success scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testRemoveCreditCard() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			
			//login
			params = (HashMap) getControleParameters();
            params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));
            params.put("atg-rest-return-form-handler-properties", "true");
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("loginRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			}
	 		
	 			//add credit card	
	 			
				 params = (HashMap) getControleParameters();
		         params.put("editValue.creditCardType", (String) getObject("creditCardType"));
		         params.put("editValue.nickname", (String) getObject("nickname"));
		         params.put("editValue.creditCardNumber", (String) getObject("creditCardNumber"));
		         params.put("editValue.expirationMonth", (String) getObject("expirationMonth"));
		         params.put("editValue.expirationYear", (String) getObject("expirationYear"));
		         params.put("editValue.nameOnCard", (String) getObject("nameOnCard"));
		         params.put("billAddrValue.newNickname", (String) getObject("newNickname"));
	             params.put("billAddrValue.makeBilling", (String) getObject("makeBilling"));
	             params.put("billAddrValue.firstName", (String) getObject("firstName"));
	             params.put("billAddrValue.lastName", (String) getObject("lastName"));
	             params.put("billAddrValue.companyName", (String) getObject("companyName"));
	             params.put("billAddrValue.address1", (String) getObject("address1"));
	             params.put("billAddrValue.address2", (String) getObject("address2"));
	             params.put("billAddrValue.city", (String) getObject("city"));
	             params.put("billAddrValue.state", (String) getObject("state"));
	             params.put("billAddrValue.country", (String) getObject("country"));
	             params.put("billAddrValue.postalCode", (String) getObject("postalCode"));
	             params.put("makePreferredSet", (Boolean) getObject("makePreferredSet"));
	             params.put("atg-rest-return-form-handler-properties", "true");
				 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("createCreditCardRequest"), params,"POST");
				 JSONObject json1 = new JSONObject(pd.readInputStream());
				 String result1 = null;
	 			 if(json1.has("formExceptions")){
	 				result1 = json1.getString("formExceptions");
	 				System.out.println("formExceptions="+result1);
	 			 }
	 		
	 		
	 		//remove credit card	
	 			
			 params = (HashMap) getControleParameters();
	         params.put("removeCard", (String) getObject("removeCard"));
	         params.put("atg-rest-return-form-handler-properties", "true");
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("removeCreditCardRequest"), params,"POST");
			 JSONObject json2 = new JSONObject(pd.readInputStream());
			 String result2 = null;
			 System.out.println(json2);
 			 if(json2.has("formExceptions")){
 				result2 = json2.getString("formExceptions");
 				System.out.println("formExceptions="+result2);
 			 }
			 
			 assertNull(result2);
	 		
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
	 * Test for remove credit card error scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testRemoveCreditCardError() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			
			//login
			params = (HashMap) getControleParameters();
            params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));
            params.put("atg-rest-return-form-handler-properties", "true");
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("loginRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	 			
	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			}
	 			
	 		//remove credit card	
	 			
			 params = (HashMap) getControleParameters();
	         params.put("removeCard", (String) getObject("nickname"));
	         params.put("atg-rest-return-form-handler-properties", "true");
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("removeCreditCardRequest"), params,"POST");
			 JSONObject json2 = new JSONObject(pd.readInputStream());
			 String result2 = null;
			 System.out.println(json2);
 			 if(json2.has("formExceptions")){
 				result2 = json2.getString("formExceptions");
 				System.out.println("formExceptions="+result2);
 			 }
			 
			 assertNotNull(result2);
	 		
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

	
	
	
	
	
	
	

