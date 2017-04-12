/**
 * 
 */
package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

/**
 * @author snaya2
 *
 */
public class TestPopulateState extends BaseTestCase  {



	public TestPopulateState(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for unAuthorised login 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetState() throws RestClientException, JSONException, IOException{
		mSession = (RestSession) getNewHttpSession();
		mSession.setScheme("http"); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			 params = (HashMap) getControleParameters();
			 params.put("arg1", (String) getObject("country"));
             //params.put("atg-rest-return-form-handler-properties",true);
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort() + "/rest/bean/com/bbb/commerce/common/BBBPopulateStatesDroplet/getStateVO", params,"POST");
			 JSONObject json = new JSONObject(pd.readInputStream());
			 String result = null;
	 			
	 			if(json.has("atgResponse")){
	 				result = json.getString("atgResponse");
	 				System.out.println(json.toString());
	 			}
			 assertNotNull(result);			
		}
		finally {
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
