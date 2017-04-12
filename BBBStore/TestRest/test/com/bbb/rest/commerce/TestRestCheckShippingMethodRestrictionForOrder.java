package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestCheckShippingMethodRestrictionForOrder extends BaseTestCase {
	RestSession mSession;
	HashMap params = null;

	public TestRestCheckShippingMethodRestrictionForOrder(String name) {
		super(name);
	}

	/**
	 * Test for authorised login successfully.
	 * 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testCheckShippingMethodForSkus() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			String result =null;
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			String shippingMethod =  (String) getObject("shippingMethod");
			String skus =  (String) getObject("skuIds");
			//String sku2 = (String) getObject("sku2");
			//String sku3 = (String) getObject("sku3");
		//	skusList.add(sku2);
		//	skusList.add(sku3);
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("isShippingMethodValidForSkus"), params,new Object[] {skus,shippingMethod},"POST");
			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			boolean validShippingMethod = (Boolean) jsonResponseObj.get("atgResponse");
			System.out.println(validShippingMethod);
			assertNotNull(validShippingMethod);
		}finally {
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
