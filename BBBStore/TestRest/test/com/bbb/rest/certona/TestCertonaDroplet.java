package com.bbb.rest.certona;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCertonaDroplet extends BaseTestCase {
	RestSession session = null;
	private String NO_ERROR = "NO_ERROR";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		session.logout();
		super.tearDown();

	}

	public TestCertonaDroplet(String name) {
		super(name);
	}

	
	/*public void testCertonaDroplet_college_landing() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-input", (String) getObject("atg-rest-input"));
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);		       
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));			
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			inputparam.put("collegeid",(String) getObject("collegeid"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println("Response--------"+responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}*/
	
	public void testCertonaDroplet_error() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			// No scheme name set
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			if (errorMessage.contains("err_null_scheme_name")) {
				assertTrue(true);
			} else {
				assertTrue(false);
			}
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}
	
	/*public void testCertonaDroplet_baby_landing() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}*/
	
	/*public void testCertonaDroplet_bridal_landing() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}*/
	
	public void testCertonaDroplet_product_detail() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			inputparam.put("productId",(String) getObject("productId"));
			inputparam.put("ipaddress",(String) getObject("ipaddress"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}
	
	public void testCertonaDroplet_category_landing() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			inputparam.put("categoryId",(String) getObject("categoryId"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}
	
	
	public void testCertonaDroplet_cart_slot() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			inputparam.put("context",(String) getObject("context"));
			inputparam.put("pageName",(String) getObject("pageName"));
			inputparam.put("ipaddress",(String) getObject("ipaddress"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}
		
	public void testCertonaDroplet_home_page() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			inputparam.put("pageName",(String) getObject("pageName"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}
	
	public void testCertonaDroplet_top_reg_item() {
		

		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");


		RestResult getRegistryDetails = null;
		try {
			session = getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();

			HashMap params = new HashMap<String, String>();

			HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
		
			inputparam.put("scheme",(String) getObject("scheme"));
			inputparam.put("exitemid",(String) getObject("exitemid"));
			inputparam.put("userid",(String) getObject("userid"));
			//inputparam.put("siteId",(String) getObject("siteId"));
			inputparam.put("X-bbb-site-id", "BedBathUS");
			inputparam.put("number",(String) getObject("number"));
			inputparam.put("pageName",(String) getObject("pageName"));
			inputparam.put("giftregid",(String) getObject("giftregid"));
			inputparam.put("registrytype",(String) getObject("registrytype"));
			inputparam.put("ipaddress",(String) getObject("ipaddress"));
			params.put("arg1", inputparam);

			getRegistryDetails = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("certonaDroplet"), params, httpMethod);
			String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);

		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertTrue(false);
		} catch (IOException e) {
			String errorMessage = e.getMessage();
			System.out.println(errorMessage);
			assertFalse(true);
		}
	}

}
