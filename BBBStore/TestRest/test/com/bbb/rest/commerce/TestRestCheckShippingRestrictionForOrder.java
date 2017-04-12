package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class TestRestCheckShippingRestrictionForOrder extends BaseTestCase {
	RestSession mSession;
	HashMap params = null;

	public TestRestCheckShippingRestrictionForOrder(String name) {
		super(name);
	}

	/**
	 * Test for authorised login successfully.
	 * 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */

	public void testGetSkusShippingStatus() throws RestClientException, JSONException, IOException{
		mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			String result =null;
			mSession.login();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			String state =  (String) getObject("state");
			String sku1 =  (String) getObject("sku1");
			String sku2 = (String) getObject("sku2");
			String sku3 = (String) getObject("sku3");
			List<String> skusList = new ArrayList<String>();
			skusList.add(sku1);
			skusList.add(sku2);
			skusList.add(sku3);
			pd = mSession.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ (String) getObject("getShippingRestriction"), params, new Object[] {skusList,state},"POST");
			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			JSONObject jsonResponseObj1= (JSONObject) jsonResponseObj.get("atgResponse");
			boolean skuStatus = (Boolean) jsonResponseObj1.get(sku2);
			System.out.println("Sku status  : " +skuStatus);
			assertNotNull(skuStatus);
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
