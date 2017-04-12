package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestGetCurrentOrderDetails extends BaseTestCase {


	public TestRestGetCurrentOrderDetails(String name) {
		super(name);
	}

	RestSession mSession;
	HashMap params = null;


	/**
	 * Test for authorised login successfully
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testGetCurrentOrderDetailsTransientUser() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();

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
	@SuppressWarnings("unchecked")
	public void testGetCurrentOrderDetailsLoggedInUser() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
            params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));

			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");

			JSONObject json = new JSONObject(pd.readInputStream());
			String result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 			}

			 assertNull(result);


			 params.put("arg1", true);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()
						+ (String) getObject("currentOrderDetailsRequest"), params, "POST");
			 JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			 System.out.println(jsonResponseObj);

			 String transientFlag =  jsonResponseObj.getString("transientFlag");
			 assertEquals("false", transientFlag);

			 String itemCount =  jsonResponseObj.getString("cartItemCount");
			 assertTrue((Integer.parseInt(itemCount)) >=1);
			 //assertEquals("22", itemCount);

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
	 * Test for authorised login successfully
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testGetATGOrderDetails() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			
			
			
            params.put("value.login", (String) getObject("login"));
            params.put("value.password", (String) getObject("password"));

			pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");

			JSONObject json = new JSONObject(pd.readInputStream());
			String result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 			}

			 assertNull(result);

		
			
			params.put("arg1", (String) getObject("orderId"));
			pd = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
					+ (String) getObject("getATGOrderRequest"), params, "POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			System.out.println(jsonResponseObj);

			String itemCount = jsonResponseObj.getString("cartItemCount");
			//assertEquals("1", itemCount);
			assertTrue(Integer.parseInt((itemCount))>=1);

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

	@SuppressWarnings("unchecked")
	public void testGetATGOrderDetailsFailure() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		try{
			mSession.login();
			@SuppressWarnings("unused")
			JSONObject jsonResponseObj = null;
			params = (HashMap) getControleParameters();
			 try{
				 params.put("arg1", (String) getObject("orderId1"));
				 jsonResponseObj = getATGOrderDetails();
				 assertFalse("No Exception occurred for wrong order id", true);
			 }catch (RestClientException t) {
					System.out.println("testGetATGOrderDetailsFailure : Inside catch block");
					assertEquals(true, StringUtils.contains(t.getMessage(), "err_get_atg_order_details_1001"));
			}
			 try{
				 params.put("arg1", (String) getObject("orderId2"));
				 jsonResponseObj = getATGOrderDetails();
				 assertFalse("No Exception occurred for wrong order id", true);
			 }catch (RestClientException t) {
				 System.out.println("testGetATGOrderDetailsFailure : Inside catch block");
				 assertEquals(true, StringUtils.contains(t.getMessage(), "err_get_atg_order_details_1001"));

			}


			}finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

	private JSONObject getATGOrderDetails() throws RestClientException, JSONException, IOException {
		RestResult restResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
				+ (String) getObject("getATGOrderRequest"), params, "POST");

		return new JSONObject(restResult.readInputStream());
	}

}
