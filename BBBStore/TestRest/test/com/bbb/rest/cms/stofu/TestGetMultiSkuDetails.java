package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.nucleus.ServiceMap;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetMultiSkuDetails extends BaseTestCase {

	Map<String, Object> params;
	RestSession mSession;
	
	public TestGetMultiSkuDetails(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testGetMultiSkuDetailsSuccess() throws RestClientException,JSONException, IOException {
		
		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();		 
		params.put("atg-rest-show-rest-paths", false);
		params.put("arg1",(String)getObject("arg1"));
		RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
				getObject("GetMultiSkuDetailsAPI"),
				params, "POST");
	
		String responseData = new String(pd1.readInputStream());	
		JSONObject json = new JSONObject(responseData);
		assertNotNull(json.getString("atgResponse"));

		
	}
	public void testGetMultiSkuDetailsErr() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();

		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("atg-rest-show-rest-paths", false);
		params.put("arg1",(String)getObject("arg1"));
		try {

			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					getObject("GetMultiSkuDetailsAPI"),
					params, "POST");
			String responseData = pd1.readInputStream();	

			if (responseData != null) {
				assertFalse(false);

			}
		} catch (RestClientException e) {		 
			String errorMessage=e.getMessage();
			if(errorMessage.contains("9100")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}		 
		}         
	}
	
}