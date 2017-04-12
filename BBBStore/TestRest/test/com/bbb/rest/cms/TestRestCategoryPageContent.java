package com.bbb.rest.cms;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestCategoryPageContent extends BaseTestCase {
	
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	
	public TestRestCategoryPageContent(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
    @SuppressWarnings({ "unchecked" })
	public void testCategoryPageContent() throws  IOException, JSONException, RestClientException{
    	BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
    	mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		mSession.login();
	        RestResult pd = null;
	        try {
	        	
	        	String catId = (String)getObject("catId");
		        params = (HashMap)getControleParameters();
	        	pd = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("restCategoryPageContentAPI"), params,new Object[] {catId},"POST");
		        String responseData=pd.readInputStream();
		        System.out.println("restCategoryPageContentAPI Output:" + responseData);
		        if (responseData != null) {
		     		JSONObject json = new JSONObject(responseData);
					 String result = json.getString("templateId");
			 		 System.out.println(result);
					 assertNotNull(result);
		        }
	        }
	        catch(RestClientException e)
	        {
	        	System.out.println("Exception: " + e.getMessage());
	        	assertNotNull(e.getMessage());
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