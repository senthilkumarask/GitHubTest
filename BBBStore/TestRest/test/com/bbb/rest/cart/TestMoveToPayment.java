package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;


public class TestMoveToPayment extends BaseTestCase{

	
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestMoveToPayment(String name) {
		super(name);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testMoveToPayment() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		try{
		params = (HashMap) getControleParameters();
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		String moveToCartResponse = pd.readInputStream();
		System.out.println("moveToCartResponse: " + moveToCartResponse);
		
		RestResult pd1 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/atg/commerce/order/purchase/CartModifierFormHandler/repriceOrder", params,"POST");
		String responseData = pd1.readInputStream();
		System.out.println("Reprice order Response data : " + responseData);
		JSONObject json = new JSONObject(responseData);
		
		assertNotNull(responseData);
		}
		finally{ 
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		 
		}
	}
	
}

