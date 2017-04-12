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


public class TestEmailCart extends BaseTestCase{

	
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestEmailCart(String name) {
		super(name);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testEmailCart() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession();
		testGetCurrentOrderDetailsTransientUser(mSession);
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		//mSession.login();
		
		try{
		params = (HashMap) getControleParameters();
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		params.put("atg-rest-return-form-handler-properties", false);
		params.put("atg-rest-show-rest-paths", false);
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		String cartResponseData = pd.readInputStream();
		System.out.println("cartResponseData : " + cartResponseData);
		
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-properties", false);
		params.put("recipientEmail",(String)getObject("recipientEmail"));
		RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("emailCartRequest"), params,"POST");
		String responseData = pd1.readInputStream();
		System.out.println("Email Cart Response Data");
		System.out.println("responseData : " + responseData);
		JSONObject json = new JSONObject(responseData);
		if(json.has("formExceptions")){
			String result = json.getString("formExceptions");
				System.out.println("Result: " + result);
				assertNotNull(result);
			}
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

