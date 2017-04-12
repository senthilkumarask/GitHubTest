package com.bbb.rest.cms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestComponentHelper;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestStaticContent extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;

	public TestStaticContent(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}

	@SuppressWarnings({ "unchecked" })
	public void testRegistryContent() throws JSONException, IOException{
		session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		RestResult testResult;

		try {
			session.login();
			params.put("arg1", (String)getObject("arg1"));
			testResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/cms/manager/RegistryTemplateManager/getRegistryTemplateContent", params,"POST");
			String responseData = testResult.readInputStream();	
			System.out.println(responseData);
			if (responseData != null) {

				JSONObject json = new JSONObject(responseData);
				String pageName=json.getString("pageName");
				assertNotNull(pageName);

			}

			assertNotNull(testResult.readInputStream());

		} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("value_not_found:no result found for page")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void testStaticContent() throws  IOException, JSONException{
		session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		try{
			session.login();

			params.put("arg1", (String)getObject("arg1"));
			RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/cms/manager/StaticTemplateManager/getStaticContent", params,"POST");

			String responseData=pd0.readInputStream();
			System.out.println("testStaticContent Output:" + responseData);

			assertNotNull(responseData);

			if (responseData != null) {

				JSONObject json = new JSONObject(responseData);
				String pageName=json.getString("pageName");
				assertNotNull(pageName);

			}

		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("value_not_found:Value not found")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void getGuide() throws IOException, JSONException {
		session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		try{
			session.login();

			params.put("arg1", (String)getObject("arg1"));
			RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/cms/manager/GuidesTemplateManager/getGuidesContent", params,"POST");
			String responseData = pd0.readInputStream();
			System.out.println("getGuide Output:" + responseData);

			assertNotNull(responseData);
			if (responseData != null) {

				JSONObject json = new JSONObject(responseData);
				String description = json.getString("longDescription");
				assertNotNull(description);

			}

		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			if(errorMessage.contains("value_not_found:no result found for guideId")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}

	@SuppressWarnings({  "unchecked" })
	public void getAllGuide() throws  IOException, JSONException {
		session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		try{
			session.login();

			params.put("arg1", (String)getObject("arg1"));
			params.put("arg2", (String)getObject("arg2"));

			RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/cms/manager/GuidesTemplateManager/getGuidesTemplateData", params,"POST");
			String responseData=pd0.readInputStream();

			assertNotNull(responseData);
			System.out.println("Output: " + responseData);
			if (responseData != null) {

				JSONObject json = new JSONObject(responseData);
				JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
				JSONObject json1 = jsonResponseObjArray.getJSONObject(0);
				String description = json1.getString("longDescription");
				assertNotNull(description);

			}

		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			if(errorMessage.contains("err_input_param_null:Input param can not be null")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void getConfigKeys() throws RestClientException, IOException{
		session = getNewHttpSession();
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		params.put("arg1", (String)getObject("arg1"));
		params.put("arg2", (String)getObject("arg2"));

		RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getAllValuesForKey", params,"POST");
		System.out.println("getConfigKeys Output:" + pd0.readInputStream());
		assertNotNull(pd0.readInputStream());

	}


	@SuppressWarnings({ "unchecked" })
	public void getConfigType() throws RestClientException, IOException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		params.put("arg1", (String)getObject("arg1"));

		RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getConfigValueByconfigType", params,"POST");
		System.out.println("getConfigType Output:" + pd0.readInputStream());
		assertNotNull(pd0.readInputStream());

	}


	@SuppressWarnings({ "unchecked" })
	public void getConfigTypeNullInput() throws RestClientException, IOException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		try{
			session.login();

			params.put("arg1", null);

			RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getConfigValueByconfigType", params,"POST");
			System.out.println("getConfigType Output:" + pd0.readInputStream());
			assertNotNull(pd0.readInputStream());
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("catalog_config_null:Config Type cannot be null")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}



	@SuppressWarnings("unchecked")
	public void getLabel() throws IOException, JSONException {

		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);

		try{
			session.login();

			String key= (String)getObject("arg2");
			String language= (String)getObject("arg3");
			int type=(Integer)getObject("arg1");
			params.put("arg1", type);
			params.put("arg2", key);
			params.put("arg3", language);


			RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/cms/manager/LblTxtTemplateManager/getAllLabel", params,"POST");
			String responseData = pd0.readInputStream();
			System.out.println("Output:" + responseData);
			assertNotNull(responseData);
			if (responseData != null) {

				JSONObject json = new JSONObject(responseData);
				assertNotNull(json.getString("atgResponse"));

			}


		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("err_input_param_null:required input parameter is null")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}



	@SuppressWarnings("unchecked")
	public void getMultipleLabel() throws IOException, JSONException, RestClientException {

		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();
		String key= (String)getObject("arg2");
		String language= (String)getObject("arg3");
		String type=(String)getObject("arg1");

		Map<String, String> labelKeyVal = new HashMap<String, String>();
		labelKeyVal.put(key, type);
		labelKeyVal.put("err_gift_reg_siteflag_usertoken_error", "3");

		RestResult  result = RestComponentHelper.executeMethod("/com/bbb/cms/manager/LblTxtTemplateManager", "getMultipleLabelValues", new Object[] {labelKeyVal,language}, params, session);

		String responseData = result.readInputStream();
		System.out.println("Output:  " + responseData);
		assertNotNull(responseData);
		if (responseData != null) {

			JSONObject json = new JSONObject(responseData);
			assertNotNull(json.getString("atgResponse"));

		}
	}
	
	

	@SuppressWarnings("unchecked")
	public void getMultipleLabelException() throws IOException, JSONException {

		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		try{
		session.login();


		Map<String, String> labelKeyVal =null;


		RestResult  result = RestComponentHelper.executeMethod("/com/bbb/cms/manager/LblTxtTemplateManager", "getMultipleLabelValues", new Object[] {labelKeyVal,""}, params, session);

		String responseData = result.readInputStream();
		System.out.println("Output:  " + responseData);
		assertNotNull(responseData);
		if (responseData != null) {

			JSONObject json = new JSONObject(responseData);
			assertNotNull(json.getString("atgResponse"));

		}
	}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("err_input_param_null:required input parameter is null")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
	}
}