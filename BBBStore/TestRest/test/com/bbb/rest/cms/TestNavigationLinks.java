package com.bbb.rest.cms;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestNavigationLinks extends BaseTestCase {
	
	public TestNavigationLinks(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testNavigationLinks() throws  IOException, JSONException, RestClientException{
    	mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
	        RestResult pd = null;
	        try {
	        	
		        params = (HashMap)getControleParameters();
		        String url = "http://" + getHost() + ":" + getPort() + (String)getObject("restNavigationLinksAPI");
		        
	        	pd = mSession.createHttpRequest(url, params,"POST");
	        
		        String responseData=pd.readInputStream();
		        System.out.println("restNavigationLinksAPI Output:" + responseData);
		       if (responseData != null) {
		     		JSONObject json = new JSONObject(responseData);
					 String result = json.getString("navLinkId");
			 		 System.out.println("Result:" + result);
					 assertNotNull(result);
		        }
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