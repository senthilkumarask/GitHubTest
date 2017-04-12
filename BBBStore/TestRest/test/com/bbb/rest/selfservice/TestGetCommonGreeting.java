/**
 * 
 */
package com.bbb.rest.selfservice;

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
 * @author abhilash
 *
 */
public class TestGetCommonGreeting extends BaseTestCase  {



	public TestGetCommonGreeting(String name) {
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
	public void testGetGiftWrapMsg() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
             //params.put("atg-rest-return-form-handler-properties",true);
	         HashMap inputparam = new HashMap<String, String>();
	         inputparam.put("siteId","BuyBuyBaby");
	         params.put("arg1", inputparam);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getCommonGreeting"), params,"POST");
			 String responseData = pd.readInputStream();
			 System.out.println(responseData);
			 JSONObject json = new JSONObject(responseData);
			 
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
	
	public void testErrGetGiftWrapMsg() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
	         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
             //params.put("atg-rest-return-form-handler-properties",true);
	         HashMap inputparam = new HashMap<String, String>();
	         inputparam.put("siteId","sdfasdf");
	         params.put("arg1", inputparam);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + (String) getObject("getCommonGreeting"), params,"POST");
			 String responseData = pd.readInputStream();
			 System.out.println(responseData);
			 JSONObject json = new JSONObject(responseData);
			 assertTrue(false); 
		} catch (Exception e) {
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("Internal Error!! BBBSystemException in GiftWrapGreetingsDroplet.getGiftWrapMsg()"));
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
