/**
 * 
 */
package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * @author sku134
 * 
 */
public class TestGetAllAppliedCoupons extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	/**
	 * @param name
	 */
	public TestGetAllAppliedCoupons(String name) {
		super(name);

	}

	/**
	 * Test to get applied coupons
	 * 
	 * @throws JSONException
	 * @throws RestClientException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetApplyCoupon() throws JSONException, RestClientException,
			IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		String response=null;
		boolean result=false;
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("atg-rest-return-form-handler-exceptions", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult restResult1 = mSession.createHttpRequest("http://"
					+ getHost() + ":" + getPort()
					+ (String) getObject("loginRequest"), params, "POST");
			System.out.println(restResult1.readInputStream());
			params.put("arg1", null);
			params.put("arg2", null);
			params.put("arg3", true);
			RestResult restResult = mSession.createHttpRequest("http://"
					+ getHost() + ":" + getPort()
					+ (String) getObject("applyCouponRequest"), params, "POST");
		//	response=getAppliedCoupons().readInputStream().toString();
			System.out.println("response "+response);
			/*if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")) {
				JSONObject jsonObjectCoupons = new JSONObject(
						getAppliedCoupons().readInputStream());
				response=jsonObjectCoupons.toString();
				System.out.println("response " + response);

			} else {
				response=getAppliedCoupons().readInputStream().toString();
				System.out.println("response "+response);
			}*/
			
			//  below condition is used because of user may or may not have applied coupon available.
			if (response!=null)
			{
			result=true;
			}
			assertTrue(result);
		} finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}
	
	/**
	 * Test to get applied coupons error scenario
	 * 
	 * @throws JSONException
	 * @throws RestClientException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetApplyCouponError() throws JSONException, RestClientException,
			IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		try {
			mSession.login();
			params = (HashMap) getControleParameters();
				if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")) {
				JSONObject jsonObjectCoupons = new JSONObject(
						getAppliedCoupons().readInputStream());
				System.out.println("response " + jsonObjectCoupons);

			} else {
				System.out.println("response "
						+ getAppliedCoupons().readInputStream());
			}
		}	catch(RestClientException re)
				{
			String errorMessage=re.getMessage();
         	System.out.println("errorMessage:" + errorMessage);
 			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
 				assertTrue(true);				
 			}else{
 				assertFalse(true);
 			}	
					
				}
		 finally {

			try {
				mSession.logout();
			} catch (RestClientException e) {
				assertFalse("Logout failed", true);
			}
		}

	}

	/**
	 * login call
	 * 
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private RestResult loginRequestRestCall() throws RestClientException,
			JSONException, IOException {

		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("loginRequest"), params, "POST");

		return restResult;
	}

	/**
	 * get applied coupons
	 * 
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	private RestResult getAppliedCoupons() throws RestClientException,
			JSONException, IOException {

		RestResult restResult = mSession.createHttpRequest("http://"
				+ getHost() + ":" + getPort()
				+ (String) getObject("applyCouponRequest"), params, "POST");
		return restResult;
	}

}
