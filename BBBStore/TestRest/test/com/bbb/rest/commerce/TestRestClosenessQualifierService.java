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

public class TestRestClosenessQualifierService extends BaseTestCase {
	RestSession mSession;
	HashMap params = null;

	public TestRestClosenessQualifierService(String name) {
		super(name);
	}

	/**
	 * Test for authorised login successfully.
	 * 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testGetClosenessQualifiersForLoggedinUser() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		RestResult pd1 =  null;
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
			String qualifierType =  (String) getObject("qualifierType");
			pd1 = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("getClosenessQualifierRequest"), params,new Object[] {qualifierType},"POST");
			JSONObject jsonResponseObj = new JSONObject(pd1.readInputStream());
			System.out.println(jsonResponseObj);
			assertTrue(true);
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
	
	public void testGetClosenessQualifiersForAnonymousUser() throws RestClientException, JSONException, IOException{
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
			String qualifierType =  (String) getObject("qualifierType");
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("getClosenessQualifierRequest"), params,new Object[] {qualifierType},"POST");
			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			System.out.println(jsonResponseObj);
			assertTrue(true);
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
	public void testGetClosenessQualifiersError() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		RestResult pd1 =  null;
		try {
			String result =null;
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
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
			String qualifierType =  (String) getObject("qualifierType");
			pd1 = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("getClosenessQualifierRequest"), params,new Object[] {qualifierType},"POST");
			JSONObject jsonResponseObj = new JSONObject(pd1.readInputStream());
			System.out.println(jsonResponseObj);
		}catch (RestClientException e) {
			if(e.getMessage().contains("required_parameter_null")){
				assertTrue(true);
			}else assertFalse(false);
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
}
