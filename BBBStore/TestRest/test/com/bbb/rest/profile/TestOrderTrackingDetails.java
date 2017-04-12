/**
 * 
 */
package com.bbb.rest.profile;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * @author snaya2
 *
 */
public class TestOrderTrackingDetails extends BaseTestCase  {



	public TestOrderTrackingDetails(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for Order Tracking success scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testOrderTrackingDetails() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-properties", (Boolean) getObject("atg-rest-return-form-handler-properties"));
             params.put("emailId", (String) getObject("email"));
             params.put("orderId", (String) getObject("orderId"));
             //params.put("atg-rest-return-form-handler-properties",true);
             params.put("trackOrderErrorURL", (String) getObject("trackOrderErrorURL"));
             params.put("trackOrderSuccessURL", (String) getObject("trackOrderSuccessURL"));
           	 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("orderTrackingDetailsRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 System.out.println(json);
			 JSONObject component = json.getJSONObject("component");
			 String result = component.getString("BBBTrackOrderVO");
	 		 System.out.println(result);
			 assertNotNull(result);
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
	 * Test for Order Tracking Error scenario
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testOrderTrackingDetailsError() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-properties", (Boolean) getObject("atg-rest-return-form-handler-properties"));
             params.put("emailId", (String) getObject("email"));
             params.put("orderId", (String) getObject("orderId"));
             //params.put("atg-rest-return-form-handler-properties",true);
             params.put("trackOrderErrorURL", (String) getObject("trackOrderErrorURL"));
             params.put("trackOrderSuccessURL", (String) getObject("trackOrderSuccessURL"));
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("orderTrackingDetailsRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 System.out.println(json);
			 JSONObject component = json.getJSONObject("component");
			 String result = component.getString("BBBTrackOrderVO");
	 		 System.out.println(result);
			 assertEquals(result, "null");
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
