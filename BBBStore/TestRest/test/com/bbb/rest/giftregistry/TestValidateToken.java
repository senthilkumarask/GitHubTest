package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestValidateToken extends BaseTestCase {

	Map<String, Object> params;
	RestSession mSession;
	
	public TestValidateToken(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testValidateToken() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();		
		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();		 
		params.put("atg-rest-show-rest-paths", false);

		String registryId = (String) getObject("registryId");	
		String token = (String) getObject("token");	
		
		RestResult pd= session.createHttpRequest("http://" + host + ":" + port+ 
		getObject("restGetContentAPI"),params, new String[] {registryId,token}, "POST");
		String responseData = pd.readInputStream();	
		if (responseData != null) {
				JSONObject json = new JSONObject(responseData);
				assertNotNull(json.getString("atgResponse"));
				System.out.println(json.getString("atgResponse"));
			}
	}	
}