package com.bbb.rest.account;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetCouponService extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;

	public TestGetCouponService(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}

	/**
	 * Test case to get coupon information from web service based on email id
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testGetCoupons() throws RestClientException, IOException, JSONException {
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();
		String email= (String)getObject("arg1");
		String mobile= "";
		params.put("arg1", email);
		params.put("arg2", mobile);
		params.put("arg3", "SC123");
		params.put("arg4", new Boolean (true));
		params.put("arg5", new Boolean (true));
		RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/account/BBBGetCouponsManager/getUserCoupons", params,"POST");
		String responseData = pd0.readInputStream();	
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			System.out.println("json "+json);
			JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
			for(int i = 0; i < jsonResponseObjArray.length(); i++){
				JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(i);
				String description=jsonChildObj.getString("description");
				assertNotNull(description);
			}
		}
	}
	
	/**
	 * Test case to get coupon information based on school id input
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */

	@SuppressWarnings("unchecked")
	public void testGetCouponsBySchoolId() throws RestClientException, IOException, JSONException {

		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();
		String schoolId= (String)getObject("arg3");
		String mobile= "";
		params.put("arg1", "");
		params.put("arg2", mobile);
		params.put("arg3", schoolId);
		params.put("arg4", new Boolean (true));
		params.put("arg5", new Boolean (true));
		RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/account/BBBGetCouponsManager/getUserCoupons", params,"POST");
		String responseData = pd0.readInputStream();	
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
			JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
			String description=jsonChildObj.getString("description"); 
			assertNotNull(description);
		}
	}
	
	/**
	 * test case to check error scenario where all parameters are null
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */

	@SuppressWarnings("unchecked")
	public void testGetCouponsAllInputNull() throws RestClientException, IOException, JSONException {
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		try {
			session.login();

			String schoolId= "";
			String mobile= "";
			params.put("arg1", "");
			params.put("arg2", mobile);
			params.put("arg3", schoolId);
			params.put("arg4", new Boolean (true));
			params.put("arg5", new Boolean (true));
			RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/account/BBBGetCouponsManager/getUserCoupons", params,"POST");
			String responseData = pd0.readInputStream();	
			System.out.println(responseData);
		} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("error_retrieving_valid_input:All inputs email-mobile-schoolid are null cannot retrieve data")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		} 
	}
	
	
	/**
	 * Test case to get coupon information from web service based on email id
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	
	@SuppressWarnings("unchecked")
	public void testGetCouponsByEmailOnly() throws RestClientException, IOException, JSONException {
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();
		String email= (String)getObject("arg1");
		String mobile= "";
		params.put("arg1", email);
		params.put("arg2", mobile);
		RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/account/BBBGetCouponsManager/getUserCoupons", params,"POST");
		String responseData = pd0.readInputStream();	
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			System.out.println("json "+json);
			JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
			for(int i = 0; i < jsonResponseObjArray.length(); i++){
				JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(i);
				String description=jsonChildObj.getString("description");
				assertNotNull(description);
			}
		}
	}
}