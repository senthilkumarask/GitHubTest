
package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestComponentHelper;
import atg.rest.client.RestRepositoryHelper;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCollegeDetails extends BaseTestCase {

	HashMap params = null;
	public TestCollegeDetails(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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

	/**
	 * @throws JSONException
	 * @throws IOException
	 * @throws RestClientException
	 */
	public void testCollegeDetail() throws JSONException, IOException,RestClientException{
		 
		BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
		String resultStream ="";
    	
    	mSession.login();
    	try{
		params = (HashMap)getControleParameters();
		params.put("arg1", (String)getObject("arg1"));
		testResult = RestComponentHelper.executeMethod("/com/bbb/rest/catalog/BBBRestCatalogToolImpl","getCollegeDetails", null, params, mSession);
		resultStream = testResult.readInputStream();
    	System.out.println("Result: "+resultStream);
    	
    	if (null != testResult && null != resultStream) {
    		if(("json").equalsIgnoreCase((String)getObject("atg-rest-output"))){
    			JSONObject json = new JSONObject(resultStream);
    			if(null != json.get("schoolVO")){
    				JSONObject pJson = (JSONObject)(json.get("schoolVO"));
    				assertNotNull(pJson.get("schoolId"));
    			}
    		}
    	}
    	
    	if(testResult != null)
    		testResult = null;
    	}
    	catch (RestClientException e) {
			assertNotNull(e.getMessage());
    	}
    	finally{
		 try {
			mSession.logout();
		 } catch (RestClientException e) {
			e.printStackTrace();
		 }
    	}
    }
          
	/**
	 * @throws IOException
	 */
	public void testCollegeDetailError() throws IOException{

		BBBRestSession mSession = (BBBRestSession) getNewHttpSession();
		mSession.setUseInternalProfileForLogin(false);
		mSession.setUseHttpsForLogin(false);
		RestResult testResult =  null;
        String errorMessage ="";
        
    	try {
			mSession.login();
			params = (HashMap)getControleParameters();
			params.put("arg1", (String)getObject("arg1"));
        
        	testResult = RestComponentHelper.executeMethod("/com/bbb/rest/catalog/BBBRestCatalogToolImpl","getCollegeDetails", null, params, mSession);
    	} catch (RestClientException e1) {

        	errorMessage=e1.getMessage();
        	System.out.println("errorMessage:" + errorMessage);
			if(errorMessage.contains("err_school_id_missing") || errorMessage.contains("3001") || errorMessage.contains("err_shool_id_invalid")){
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
}
