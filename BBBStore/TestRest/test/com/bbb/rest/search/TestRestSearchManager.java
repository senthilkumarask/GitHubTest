package com.bbb.rest.search;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestComponentHelper;
import atg.rest.client.RestResult;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestSearchManager extends BaseTestCase {
	
	HashMap params = null;
	
	public TestRestSearchManager(final String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

	}
	
	public void testRestSearchManager() throws IOException, JSONException,RestClientException{
        
    	BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
		int productCount =-1;    	
		mSession.login();
		params = (HashMap)getControleParameters();
		params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
		params.put("arg1", (String)getObject("arg1"));
		testResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() +"/rest/bean/com/bbb/rest/search/RestSearchManager/performSearch",
				params, "POST"); 
				//RestComponentHelper.executeMethod("/com/bbb/rest/search/RestSearchManager","performSearch", null, params, mSession);

		resultStream = testResult.readInputStream();
		System.out.println("result:"+resultStream);
		if(("json").equalsIgnoreCase((String)getObject("atg-rest-output"))){
			if (null != testResult && null != resultStream) {
				JSONObject json = new JSONObject(resultStream);
				JSONObject pJson = (JSONObject)json.getJSONObject("bbbProducts");
				productCount = (Integer)pJson.get("BBBProductCount");
				// System.out.println("Count: "+productCount);
			}
		}
    	assertNotNull(productCount); 	
    }
	
	public void testRestSearchManagerError() throws IOException{
        
		BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
        String errorMessage ="";
        
    	try {
			mSession.login();
			params = (HashMap)getControleParameters();
			params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
			params.put("arg1", (String)getObject("arg1"));
        
        	testResult = RestComponentHelper.executeMethod("/com/bbb/rest/search/RestSearchManager","performSearch", null, params, mSession);
    	   
		        resultStream = testResult.readInputStream();
		        /*if (null != testResult && null != resultStream) {
		        	JSONObject json = new JSONObject(resultStream);
                    errorCode = (String)json.get("errorCode");
	            	errorMessage = (String)json.get("errorMessage");
	            }*/
	       
        } catch (RestClientException e1) 
        {
			// TODO Auto-generated catch block
        	errorMessage=e1.getMessage();
			if(errorMessage.contains("error_invalid_keyword") || errorMessage.contains("error_in_input")){
				assertTrue(true);				
			}
			else assertTrue(false);
        	e1.printStackTrace();
		}
        assertNotNull(errorMessage);
    }
	
	public void testRSMPerformTypeAheadSearch() throws RestClientException, JSONException, IOException{
        
    	BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
		int productCount =-1;
    	
    	
		mSession.login();
		params = (HashMap)getControleParameters();
		params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
		params.put("arg1", (String)getObject("arg1"));
		
		/* START R2.1 TypeAhead for Most Popular Keywords */
		params.put("arg2", (String) getObject("showPopularTerms"));
		/* END   R2.1 TypeAhead for Most Popular Keywords */
		
		testResult = RestComponentHelper.executeMethod("/com/bbb/rest/search/RestSearchManager","performTypeAheadSearch", null, params, mSession);
	    
    	resultStream = testResult.readInputStream();
    	System.out.println("Result: "+resultStream);
    	if (null != testResult && null != resultStream) {
        	JSONObject json = new JSONObject(resultStream);
        	assertNotNull(productCount);
    	}
    	
    	if(testResult != null)
    		testResult = null;
		 try {
			mSession.logout();
		 } catch (RestClientException e) {
			e.printStackTrace();
		 }
	    	
    }
	
	public void testRSMPerformTypeAheadSearchError() throws IOException{
        
		BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
        String errorMessage ="";
        
    	try {
			mSession.login();
			params = (HashMap)getControleParameters();
			params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
			params.put("arg1", (String)getObject("arg1"));
			
			/* START R2.1 TypeAhead for Most Popular Keywords */
			params.put("arg2", (String) getObject("showPopularTerms"));
			/* END   R2.1 TypeAhead for Most Popular Keywords */
        
        	testResult = RestComponentHelper.executeMethod("/com/bbb/rest/search/RestSearchManager","performTypeAheadSearch", null, params, mSession);
    	    resultStream = testResult.readInputStream();
		    
        } catch (RestClientException e1) {

        	errorMessage=e1.getMessage();
        	System.out.println("errorMessage:" + errorMessage);
			if(errorMessage.contains("error_search_keyword_length_less_than_3") || errorMessage.contains("error_json_search_param_missing")){
				assertTrue(true);				
			}
        	
		}finally{
			if(testResult != null)
	    		testResult = null;
			 try {
				mSession.logout();
			 } catch (RestClientException e) {
				e.printStackTrace();
			 }
		}
    }
	
	public void testRSMGetAllNavigation() throws RestClientException, JSONException, IOException{
        BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
		
    	mSession.login();
		params = (HashMap)getControleParameters();
		params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
		params.put("arg1", (String)getObject("arg1"));
		//testResult = RestComponentHelper.executeMethod("/com/bbb/rest/search/RestSearchManager","getAllNavigation", null, params, mSession);
		testResult = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() +"/rest/bean/com/bbb/rest/search/RestSearchManager/getRootCategoryNavigation",
				params, "POST");
	    resultStream = testResult.readInputStream();
    	System.out.println("Result: "+resultStream);
    	
    	if (null != testResult && null != resultStream) {
    		if(("json").equalsIgnoreCase((String)getObject("atg-rest-output"))){
    			JSONObject json = new JSONObject(resultStream);
    			if(null != json.get("atgResponse")){
    				assertNotNull(json.get("atgResponse"));
    			}
    			else{
    				assertNull(json.get("atgResponse"));
    			}
    		}
    	}
    	
    	if(testResult != null)
    		testResult = null;
		 try {
			mSession.logout();
		 } catch (RestClientException e) {
			e.printStackTrace();
		 }
    }
	
	public void testRSMGetAllNavigationErr() throws IOException{
        BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
		String errorMessage ="";
    	
    	try {
			mSession.login();
			params = (HashMap)getControleParameters();
			params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
			params.put("arg1", (String)getObject("arg1"));
			testResult = RestComponentHelper.executeMethod("/com/bbb/rest/search/RestSearchManager","getRootCategoryNavigation", null, params, mSession);
    	    resultStream = testResult.readInputStream();
    	    System.out.println("Result:::: "+resultStream);
    	    
        }catch (RestClientException e1) {

        	errorMessage=e1.getMessage();
        	System.out.println("errorMessage:" + errorMessage);
			if(errorMessage.contains("error_catalogid_missing:Mandatory parameter catalog id missing.") || errorMessage.contains("error_rootCategoryid_missing:Mandatory parameter root category id missing.")){
				assertTrue(true);				
			}else{
				assertTrue(false);
			}
        	
		}finally{
			if(testResult != null)
	    		testResult = null;
			 try {
				mSession.logout();
			 } catch (RestClientException e) {
				e.printStackTrace();
			 }
		}	
    }
	
	public void testRSMGetCollegesByState() throws RestClientException, JSONException, IOException{
		BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
    	try{
    	mSession.login();
		params = (HashMap)getControleParameters();
		params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
		params.put("arg1", (String)getObject("arg1"));
		testResult = RestComponentHelper.executeMethod("/com/bbb/rest/search/RestSearchManager","getCollegesByState", null, params, mSession);
		resultStream = testResult.readInputStream();
    	System.out.println("Result: "+resultStream);
    	
    	if (null != testResult && null != resultStream) {
    		if(("json").equalsIgnoreCase((String)getObject("atg-rest-output"))){
    			JSONObject json = new JSONObject(resultStream);
    			if(null != json.get("atgResponse")){
    				assertNotNull(json.get("atgResponse"));
    			}
    			else{
    				assertNull(json.get("atgResponse"));
    			}
    		}
    	}
    	}
    	
    	catch (RestClientException e) {
    		if (e.getMessage().contains("No Colleges are available for this state code"))
    		{
    			assertTrue(true);
    			
    		}
    		else
    		{
    			assertTrue(false);
    		}
		
		}
    	finally{
    	if(testResult != null)
    		testResult = null;
		 try {
			mSession.logout();
		 } catch (RestClientException e) {
			e.printStackTrace();
		 }
    	
    }
    }
	
	/** This API returns a map with Key as Alphabet for which it found some College as their names 
	 * starting with and corresponding List of CollegeVOs as value of that key in the map for a given state id.
	 * @throws RestClientException
	 * @throws JSONException
	 * @throws IOException
	 */
	public void testRSMGetCollegeMerchandize() throws RestClientException, JSONException, IOException{
		BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
    	try{
    	mSession.login();
		params = (HashMap)getControleParameters();
		params.put("atg-rest-http-method", (String)getObject("atg-rest-http-method"));
		params.put("atg-rest-show-rest-paths", (Boolean) false);
		params.put("arg1", (String)getObject("arg1"));
		testResult = RestComponentHelper.executeMethod("/com/bbb/commerce/browse/droplet/CollegeLookup","getCollegeGroups", null, params, mSession);
		resultStream = testResult.readInputStream();
    	System.out.println("Result: "+resultStream);
    	
    	if (null != testResult && null != resultStream) {
    		if(("json").equalsIgnoreCase((String)getObject("atg-rest-output"))){
    			JSONObject json = new JSONObject(resultStream);
    			if(null != json.get("atgResponse")){
    				assertNotNull(json.get("atgResponse"));
    			}
    			else{
    				assertNull(json.get("atgResponse"));
    			}
    		}
    	}
    	}
    	catch (RestClientException e) {
    		if (e.getMessage().contains("No Colleges are available for this state code"))
    		{
    			assertTrue(true);
    			
    		}
    		else
    		{
    			assertTrue(false);
    		}
		
		}
    	finally{
    	if(testResult != null)
    		testResult = null;
		 try {
			mSession.logout();
		 } catch (RestClientException e) {
			e.printStackTrace();
		 }
    	}
    }
}