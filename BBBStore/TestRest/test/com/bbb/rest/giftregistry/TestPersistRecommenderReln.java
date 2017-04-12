package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestPersistRecommenderReln extends BaseTestCase {

	Map<String, Object> params;
	RestSession mSession;
	
	public TestPersistRecommenderReln(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testPersistRecommenderReln() throws RestClientException,JSONException, IOException {
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
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

		String registryId = (String) getObject("registryId");	
		String token = (String) getObject("token");	
		String isFromFb = (String) getObject("isFromFb");	
		pd= mSession.createHttpRequest("http://" + getHost() + ":" + getPort()+ 
		getObject("restGetContentAPI"),params, new String[] {registryId,token,isFromFb}, "POST");
		String responseData = pd.readInputStream();	
		if (responseData != null) {
				JSONObject json1 = new JSONObject(responseData);
				assertNotNull(json1);
				System.out.println(json1.getString("registryInfo"));
			}
	
	}
	catch (RestClientException e) {
		System.out.println(e.getMessage());
		if("1001".contains(e.getMessage())){
			assertFalse(true);
		}
	} finally {
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