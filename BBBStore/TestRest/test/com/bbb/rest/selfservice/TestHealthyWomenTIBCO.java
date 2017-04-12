package com.bbb.rest.selfservice;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;
/**
 * Test cases for Healthy Women API
 * @author njai13
 *
 */
public class TestHealthyWomenTIBCO extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestHealthyWomenTIBCO(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to send request for healthy women
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testHealthyWomenRegistrationTIBCO() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		params.put("hwRegistrationVO.emailAddr",(String)getObject("emailAddr"));
		params.put("hwRegistrationVO.firstName",(String)getObject("firstName"));
		params.put("hwRegistrationVO.lastName",(String)getObject("lastName"));
		params.put("hwRegistrationVO.addressLine1",(String)getObject("addressLine1"));
		params.put("hwRegistrationVO.addressLine2",(String)getObject("addressLine2"));
		params.put("hwRegistrationVO.city",(String)getObject("city"));
		params.put("hwRegistrationVO.state",(String)getObject("state"));
		params.put("hwRegistrationVO.zipcode",(String)getObject("zipcode"));
		/*
		 * if you would like to receive more information from HealthyWomen, please fill out the following form and we will pass it along. 
		 */
		params.put("hwRegistrationVO.emailOffer",(Boolean)getObject("emailOffer"));



		//set params required to make request
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));



		//call Healthy women request handler
		params.put("hWSuccessURL",(String)getObject("hWSuccessURL"));
		params.put("hWErrorURL",(String)getObject("hWErrorURL"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("HWReg"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

		//	parse response
		JSONObject json = new JSONObject(response);
		String result=json.getString("result");
		System.out.println("Healthy Women result="+result);
		assertNotNull(result);
	}
	
	/**
	 * test for form exception scenarios in healthy women request
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testHealthyWomenRegistrationTIBCOException() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		
		/*
		 * f you would like to receive more information from HealthyWomen, please fill out the following form and we will pass it along. 
		 */
		params.put("hwRegistrationVO.emailOffer",(Boolean)getObject("emailOffer"));
		params.put("hwRegistrationVO.zipcode","ddd");


		//set params required to make request
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));



		//call Healthy women request handler
		params.put("hWSuccessURL",(String)getObject("hWSuccessURL"));
		params.put("hWErrorURL",(String)getObject("hWErrorURL"));
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String)getObject("HWReg"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

		//		parse response
			JSONObject json = new JSONObject(response);
		
			JSONArray jsonResponseObjArray = new JSONArray(json.getString("formExceptions"));

			int exceptionlength =jsonResponseObjArray.length();
		
			System.out.println("Healthy Women no of exceptions="+exceptionlength);
			assertEquals(exceptionlength,3);
	}
}
