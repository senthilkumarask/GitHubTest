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
public class TestUnsubscribeOOSEmail extends BaseTestCase  {

	public TestUnsubscribeOOSEmail(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for UnSubscribe OOS email success scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testUnsubscribeOOSEmail() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession("BuyBuyBaby");
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
	 			
			 params = (HashMap) getControleParameters();
	         params.put("catalogRefId", (String) getObject("catalogRefId"));
	         params.put("productId", (String) getObject("productId"));
	         params.put("productName", "K-Cup-reg-Gloria-Jean-39-s-Hazelnut-Coffee-for-Keurig-reg-Brewers-Set-of-18");
	         params.put("emailAddress", (String) getObject("emailAddress"));
	         params.put("successURL",(String) getObject("successURL"));
			 params.put("errorURL",(String) getObject("errorURL"));
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("unsubscribeOOSEmailRequest"), params,"POST");
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
	 * Test for UnSubscribe OOS email error scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testUnsubscribeOOSEmailError() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession("BuyBuyBaby");
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
	 			
			 params = (HashMap) getControleParameters();
	         params.put("catalogRefId", (String) getObject("catalogRefId"));
	         params.put("emailAddress", (String) getObject("emailAddress"));
	         params.put("successURL",(String) getObject("successURL"));
			 params.put("errorURL",(String) getObject("errorURL"));
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("unsubscribeOOSEmailRequest"), params,"POST");
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

	
	
	
	
	
	
	

