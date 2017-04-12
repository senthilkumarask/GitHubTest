/**
 * 
 */
package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONArray;
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
public class TestGetShippingPolicies extends BaseTestCase  {



	public TestGetShippingPolicies(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for unAuthorised login 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetShippingMethodDetails() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
			 params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
	         params.put("atg-rest-show-rest-paths", false);	      
			
             //params.put("atg-rest-return-form-handler-properties",true);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) (String) getObject("getShippingMethodDetails"), params,"POST");
			 String responseData = pd.readInputStream();
			 System.out.println(responseData);
			 JSONObject json = new JSONObject(responseData);
			 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			assertTrue(false);
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
	
	public void testGetShippingPriceTableDetail() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-depth", 6);
	         params.put("atg-rest-show-rest-paths", false);
	         
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
	         HashMap inputparam = new HashMap<String, String>();
			
			
             //params.put("atg-rest-return-form-handler-properties",true);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) (String) getObject("getShippingPriceTableDetail"), params,"POST");
			 String responseData = pd.readInputStream();
			 System.out.println(responseData);
			 //JSONObject json = new JSONObject(responseData);
			 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			assertTrue(false);
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
