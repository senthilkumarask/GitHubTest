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

public class TestRestStaticContent extends BaseTestCase {
	
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	
	public TestRestStaticContent(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
    @SuppressWarnings({ "unchecked" })
	public void testRestStaticContent() throws  IOException, JSONException, RestClientException{
    	BBBRestSession mSession = (BBBRestSession) getNewHttpSession("BedBathUS");
    	mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
        	mSession.login();
	        params = (HashMap)getControleParameters();
	        params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
	 		params.put("arg1", (String)getObject("arg1"));
	 		params.put("arg2", (String)getObject("arg2"));
	 		params.put("X-bbb-site-id", "BedBathUS");
	        RestResult pd0 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("restStaticContentAPI"), params,"POST");
	        
	        String responseData=pd0.readInputStream();
	        System.out.println("testRestStaticContent Output:" + responseData);
	        if (responseData != null) {
	     		JSONObject json = new JSONObject(responseData);
				String pageName=json.getString("pageName");
				assertNotNull(pageName);
	        }
    }
    
    
    
    
    public void testRestStaticContentError() throws  IOException, JSONException{
    	BBBRestSession mSession = (BBBRestSession) getNewHttpSession("BedBathUS");
    	mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
        try{
        	mSession.login();
	        params = (HashMap)getControleParameters();
	        params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
	 		params.put("arg1", (String)getObject("arg1"));
	 		params.put("arg2", (String)getObject("arg2"));
	        RestResult pd0 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("restStaticContentAPI"), params,"POST");
	        
	        String responseData=pd0.readInputStream();
	        System.out.println("testRestStaticContent Output:" + responseData);
	        JSONObject json = new JSONObject(responseData);
	        String pageName=json.getString("pageName");
			assertTrue(pageName.equals("null"));
	    }catch (RestClientException e) {
         	String errorMessage=e.getMessage();
         	System.out.println(errorMessage);
 			if(errorMessage.contains("value_not_found") || errorMessage.contains("err_input_param_page_name_empty") || errorMessage.contains("error_fetch_static_content_500")){
 				assertTrue(true);				
 			}else{
 				assertFalse(true);
 			}	
 		}
    }
}