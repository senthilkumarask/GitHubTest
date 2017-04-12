package com.bbb.rest.cms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestHomePageContent extends BaseTestCase {
	
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	
	public TestRestHomePageContent(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
    @SuppressWarnings({ "unchecked" })
	public void testRestHomePageContent() throws  IOException, JSONException, RestClientException{
    	BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
    	mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
	        RestResult pd = null;
	        try {
	        	mSession.login();
		        params = (HashMap)getControleParameters();
		        params.put("X-bbb-site-id", "BedBathUS");
	        	pd = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("restHomePageContentAPI"), params,"POST");
	        
		        String responseData=pd.readInputStream();
		        System.out.println("restHomePageContentAPI Output:" + responseData);
		        if (responseData != null) {
		     		JSONObject json = new JSONObject(responseData);
					 String result = json.getString("templateId");
			 		 System.out.println(result);
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