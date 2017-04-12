package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestEstimatedShippingDates extends BaseTestCase {
	RestSession mSession;
	HashMap params = null;

	public TestRestEstimatedShippingDates(String name) {
		super(name);
	}

	/**
	 * Test for authorised login successfully.
	 * 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testGetExpectedShippingDate() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			String result =null;
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			String shippingMethod =  (String) getObject("shippingMethod");
			
			String state =  (String) getObject("state");
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("getExpectedDate"), params,new Object[] {shippingMethod,state},"POST");
			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			System.out.println(jsonResponseObj);
			String expectedShippingDate = (String) jsonResponseObj.get("atgResponse");
			assertNotNull(expectedShippingDate);
		}finally {
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
	 * Test for authorised login successfully.
	 * 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testGetExpectedShippingDateError() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			String result =null;
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			String shippingMethod =  (String) getObject("shippingMethod");
			String state =  (String) getObject("state");
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("getExpectedDate"), params,new Object[] {shippingMethod,state},"POST");
			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			String expectedShippingDate = (String) jsonResponseObj.get("atgResponse");
			System.out.println(expectedShippingDate);
			assertNotNull(expectedShippingDate);
		}catch (RestClientException e) {
			if((e.getMessage()).contains("2004")){
				assertTrue(true);
			}else assertFalse(true);
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
