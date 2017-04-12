package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestImportRegistry extends BaseTestCase {
	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestImportRegistry(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * Junit to test import Wedding registry from Legacy system
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testImportRegistry() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		System.out.println("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"));
		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}

			//call import registry
			String httpMethod = (String) getObject("httpmethod");
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("registryPassword","Sapient123");
			params.put("registryId","183991756");
			params.put("importEventDate", (String)getObject("eventDate"));
			params.put("importEventType", (String)getObject("eventType"));


			RestResult importRegistry = session.createHttpRequest("http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/importRegistry",params, "POST");
			String responseData = importRegistry.readInputStream();
			System.out.println("responseData for import registry" + responseData);

			JSONObject json = new JSONObject(responseData);
			System.out.println("import registry in registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Junit to test import Wedding registry from Legacy system
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testImportRegistryNonLogin() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;

		try {

			session.login();


			//call import registry
			String httpMethod = (String) getObject("httpmethod");
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("registryPassword","Sapient123");
			params.put("registryId","183991756");
			params.put("importEventDate", (String)getObject("eventDate"));
			params.put("importEventType", (String)getObject("eventType"));


			RestResult importRegistry = session.createHttpRequest("http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/importRegistry",params, "POST");
			String responseData = importRegistry.readInputStream();
			System.out.println("responseData for import registry" + responseData);

			JSONObject json = new JSONObject(responseData);
			System.out.println("import registry in registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}



}
