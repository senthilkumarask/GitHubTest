package com.bbb.rest.seo;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;


public class TestSEOTag extends BaseTestCase{

	
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestSEOTag(String name) {
		super(name);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testSeoTag() throws JSONException, RestClientException, IOException{
		
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		
		try{
		params = (HashMap) getControleParameters();
		params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		params.put("arg1", (String)getObject("arg1"));
		
		RestResult pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getSeoTagRequest"), params,"POST");
		String responseData = pd.readInputStream();
		System.out.println("ResponseData: " + responseData);
		assertNotNull(responseData);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
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

