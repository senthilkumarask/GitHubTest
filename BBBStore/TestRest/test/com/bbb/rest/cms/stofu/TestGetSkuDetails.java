package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.nucleus.ServiceMap;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetSkuDetails extends BaseTestCase {

	Map<String, Object> params;
	RestSession mSession;
	
	public TestGetSkuDetails(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testGetSkuDetailsSuccess() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();		 
		params.put("atg-rest-show-rest-paths", false);
		String skuId = (String) getObject("arg1");
	
		RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
				getObject("GetSkuDetailsAPI"),
				params, new String[] {skuId},"POST");
		System.out.println(pd1.readInputStream());
		String responseData = pd1.readInputStream();	
		//assertNotNull(responseData);
		JSONObject json = new JSONObject(responseData);
		assertNotNull(json.getString("atgResponse"));

		
	}
	/*public void testMultiSKUInventoryErr() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();

		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("atg-rest-show-rest-paths", false);
		String skuIds = (String) getObject("skuIds");


		try {

			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					getObject("MultiSKUInventoryAPI"),
					params, new String[] {skuIds}, "POST");
			String responseData = pd1.readInputStream();	

			if (responseData != null) {
				assertFalse(true);

			}
		} catch (RestClientException e) {		 
			String errorMessage=e.getMessage();
			if(errorMessage.contains("template_not_exist")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}		 
		}         
	}*/
	
}