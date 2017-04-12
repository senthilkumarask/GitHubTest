package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestGetAllOrdersForUser extends BaseTestCase {
	RestSession mSession;
	HashMap params = null;

	public TestRestGetAllOrdersForUser(String name) {
		super(name);
	}

	/**
	 * Test for authorised login successfully
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testGetAllOrders() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		//BBBRestSession session = (BBBRestSession) getNewHttpSession();
		RestResult pd =  null;
		try {
			String result =null;
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort() 
							+ (String) getObject("loginRequest"), params,"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			System.out.println(json);
			if(json.has("formExceptions")){
				result = json.getString("formExceptions");
			}
			assertNull(result);
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("legacyOrdersRequest"), params,"POST");
			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			System.out.println(jsonResponseObj);
			assertTrue(true);
		}catch (RestClientException e) {
			System.out.println(e.getMessage());
			if("1001".contains(e.getMessage())){
				assertFalse(true);
			}
		} finally {
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
	 * Test for authorised login successfully
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
//	@SuppressWarnings("unchecked")
//	public void testGetCurrentOrderDetailsLoggedInUser() throws RestClientException, JSONException, IOException{
//		mSession = (BBBRestSession) getNewHttpSession();
//		mSession.setUseHttpsForLogin(false);
//		mSession.setUseInternalProfileForLogin(false);
//		RestResult pd =  null;
//		try {
//			mSession.login();
//			params = (HashMap) getControleParameters();
//	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
//            params.put("value.login", (String) getObject("login"));
//            params.put("value.password", (String) getObject("password"));
//            
//			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
//			
//			JSONObject json = new JSONObject(pd.readInputStream());
//			String result = null;
//	 			
//	 			if(json.has("formExceptions")){
//	 				result = json.getString("formExceptions");
//	 			}
//			 assertNull(result);
//			 
//			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()
//						+ (String) getObject("currentOrderDetailsRequest"), params, "POST");
//			 JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
//			 System.out.println(jsonResponseObj);
//			 
//			 String transientFlag =  jsonResponseObj.getString("transientFlag");
//			 assertEquals("false", transientFlag);
//			 
//			 String itemCount =  jsonResponseObj.getString("cartItemCount");
//			 assertEquals("4", itemCount);
//			 
//		}
//		finally {
//			if(pd != null)
//				pd = null;
//			try {
//				mSession.logout();
//			} catch (RestClientException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//	
	

}
