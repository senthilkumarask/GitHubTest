/**
 * 
 */
package com.bbb.rest.selfservice;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.rest.framework.BaseTestCase;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

/**
 * @author snaya2
 *
 */
public class TestSurvey extends BaseTestCase  {

	public TestSurvey(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
	/**
	 * Test for Request Info success scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testRequestInfo() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			
			 params = (HashMap) getControleParameters();
	         params.put("emailRequired", (String) getObject("emailRequired"));
	         params.put("email", (String) getObject("email"));
	         params.put("everShopped", (String) getObject("everShopped"));
	         params.put("featuresMesssage", (String) getObject("featuresMesssage"));
	         params.put("webSiteMessage", (String) getObject("webSiteMessage"));
	         params.put("otherMessage", (String) getObject("otherMessage"));
	         params.put("userName", (String) getObject("userName"));
             params.put("selectedGender", (String) getObject("selectedGender"));
             params.put("selectedAge", (String) getObject("selectedAge"));
             params.put("location", (String) getObject("location"));            
             params.put("captchaAnswer", (String) getObject("captchaAnswer"));
             params.put("validateCaptcha", (Boolean) (getObject("validateCaptcha")));
             params.put("surveySuccessURL", (String) getObject("surveySuccessURL"));
             params.put("surveyErrorURL", (String) getObject("surveyErrorURL"));
           	 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("surveyRequest"), params,"POST");
			 JSONObject json1 = new JSONObject(pd.readInputStream());
			 String result1 = null;
			 System.out.println(json1);
 			 if(json1.has("formExceptions")){
 				result1 = json1.getString("formExceptions");
 				System.out.println("formExceptions="+result1);
 				assertNotNull(result1);
 			 }
 			 else{
			 assertNull(result1);
 			 }
	 		
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
	
	
	/**
	 * Test for Request Info success scenario
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testRequestInfoError() throws RestClientException, JSONException, IOException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			mSession.login();
			
			 params = (HashMap) getControleParameters();
	         params.put("emailRequired", (String) getObject("emailRequired"));
	         params.put("email", (String) getObject("email"));
	         params.put("everShopped", (String) getObject("everShopped"));
	         params.put("featuresMesssage", (String) getObject("featuresMesssage"));
	         params.put("webSiteMessage", (String) getObject("webSiteMessage"));
	         params.put("otherMessage", (String) getObject("otherMessage"));
	         params.put("userName", (String) getObject("userName"));
            params.put("selectedGender", (String) getObject("selectedGender"));
            params.put("selectedAge", (String) getObject("selectedAge"));
            params.put("location", (String) getObject("location"));            
            params.put("captchaAnswer", (String) getObject("captchaAnswer"));
            params.put("validateCaptcha", (Boolean) (getObject("validateCaptcha")));
            params.put("surveySuccessURL", (String) getObject("surveySuccessURL"));
            params.put("surveyErrorURL", (String) getObject("surveyErrorURL"));
			 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("surveyRequest"), params,"POST");
			 JSONObject json1 = new JSONObject(pd.readInputStream());
			 String result1 = null;
			 System.out.println(json1);
			 if(json1.has("formExceptions")){
				result1 = json1.getString("formExceptions");
				System.out.println("formExceptions="+result1);
			 }
			 
			 assertNotNull(result1);
	 		
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

	
	
	
	
	
	
	

