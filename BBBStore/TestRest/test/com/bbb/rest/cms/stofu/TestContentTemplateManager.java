package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.nucleus.ServiceMap;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestContentTemplateManager extends BaseTestCase {

	Map<String, Object> params;
	RestSession mSession;
	/** ServiceMap that maintains relation between template to template repository component */
	private ServiceMap templateRepositoryMap;
	
	public ServiceMap getTemplateRepositoryMap() {
		return templateRepositoryMap;
	}

	/**
	 * @param templateRepositoryMap the templateRepositoryMap to set
	 */
	public void setTemplateRepositoryMap(ServiceMap templateRepositoryMap) {
		this.templateRepositoryMap = templateRepositoryMap;
	}
	public TestContentTemplateManager(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testContentTemplateDataSuccess() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();
		
		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();		 
		params.put("atg-rest-show-rest-paths", false);

		String templateName = (String) getObject("templateName");	
		String requestJSON = (String) getObject("requestJSON");	
		RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
				getObject("restGetContentAPI"),
				params, new String[] {templateName,requestJSON}, "POST");
		String responseData = pd1.readInputStream();	
				if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			System.out.println(json.get("responseItems"));
			assertNotNull(json.get("responseItems"));

		}
	}
	
	public void testRegistryContentTemplateDataSuccess() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();
		
		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();		 
		params.put("atg-rest-show-rest-paths", false);

		String templateName = (String) getObject("templateName");	
		String requestJSON = (String) getObject("requestJSON");	
		String registryURL= "/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=Baby&registryId=203522339";
		String quote = "\"";
		requestJSON = requestJSON + registryURL + quote + "}";
		RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
				getObject("restGetContentAPI"),
				params, new String[] {templateName,requestJSON}, "POST");
		String responseData = pd1.readInputStream();	
				if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			System.out.println(json.get("promoBox"));
			assertNotNull(json.get("promoBox"));

		}
	}
	
	public void testRecommenderContentTemplateDataSuccess() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();
		
		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();		 
		params.put("atg-rest-show-rest-paths", false);

		String templateName = (String) getObject("templateName");	
		String requestJSON = (String) getObject("requestJSON");		
		RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
				getObject("restGetContentAPI"),
				params, new String[] {templateName,requestJSON}, "POST");
		String responseData = pd1.readInputStream();	
				if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			System.out.println(json.get("promoBox"));
			assertNotNull(json.get("promoBox"));

		}
	}
	
	public void testContentTemplateNotExistErr() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();

		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("atg-rest-show-rest-paths", false);
		String templateName = (String) getObject("templateName");	
		String requestJSON = (String) getObject("requestJSON");


		try {

			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					getObject("restGetContentAPI"),
					params, new String[] {templateName,requestJSON}, "POST");
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
	}
	
	public void testRegistryContentTemplateNotExistErr() throws RestClientException,JSONException, IOException {
		RestSession session = getNewHttpSession();

		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("atg-rest-show-rest-paths", false);
		String templateName = (String) getObject("templateName");	
		String requestJSON = (String) getObject("requestJSON");


		try {

			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					getObject("restGetContentAPI"),
					params, new String[] {templateName,requestJSON}, "POST");
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
	}
	
	
	/*public void testContentTemplateMissingErr() throws RestClientException,JSONException, IOException {
		String templateName=null;
		String requestJSON=null;
		try
		{
			templateName = (String) getObject("templateName");	
			requestJSON = (String) getObject("requestJSON");
		}catch (Exception e) {
			if(e.getMessage().contains("param_template_name_missing") || e.getMessage().contains("could not find object(templateName)"))
			{
				assertTrue(true);	
			}else
			{
				assertFalse(true);
			}
		}

	}*/
	
}