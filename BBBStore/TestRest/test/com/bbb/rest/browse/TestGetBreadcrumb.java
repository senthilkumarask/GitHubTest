package com.bbb.rest.browse;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetBreadcrumb extends BaseTestCase  {

	public TestGetBreadcrumb(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for create breadcrumb
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetBreadcrumb() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);		
		RestResult pd =  null;

		try {
			mSession.login();
			
		
			params = (HashMap) getControleParameters();            
            params.put("arg1", (String)getObject("arg1"));
            params.put("atg-rest-return-form-handler-properties", "true");
			 
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("createBreadcrumbRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
			 System.out.println(json);
			 
			 if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("formExceptions="+result);
	 			 }
				 
				 assertNull(result);
	 			 						 	
	 		
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
		 * Test for create breadcrumb error scenario
		 */		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetBreadcrumbError() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			
			params = (HashMap) getControleParameters();            
            params.put("arg1", (String)getObject("arg1"));
            params.put("atg-rest-return-form-handler-properties", "true");
            pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("createBreadcrumbRequest"), params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
			 System.out.println(json);
			 
			 if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("formExceptions="+result);
	 			 }
				 
			 assertNull(result);
	 		
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
