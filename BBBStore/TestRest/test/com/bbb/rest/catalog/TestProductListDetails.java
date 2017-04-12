
package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestProductListDetails extends BaseTestCase {

	public TestProductListDetails(String name) {
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

	public void testProductListDetails() {
	
		RestSession session=getNewHttpSession();
		String host = getHost();
		Integer port = getPort();	    
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
 		params = getControleParameters();	
 		String errorMessage = null;
 	    
 		try { 			 
 			params.put("arg1", (String) getObject("productId"));
			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					"/rest/bean/com/bbb/rest/catalog/BBBRestCatalogToolImpl/getProductListDetails",
					params, "POST");
		 	String responseData = pd1.readInputStream();	
			System.out.println(responseData);
			/*if (responseData != null) {
 			 JSONObject json = new JSONObject(responseData);
 		     JSONObject productVORest = (JSONObject)json.getJSONObject("productVORest");
 			 String productIdResponce = (String)productVORest.get("productId"); 			 
 			 assertNotNull(productIdResponce);			 */
			//}
		}catch (IOException e1) {
			errorMessage=e1.getMessage();
	    	e1.printStackTrace();
		} catch (RestClientException e1)  {
			errorMessage=e1.getMessage();
			e1.printStackTrace();
		}
	    assertNull(errorMessage);
 	}
	
	public void testProductListDetailsErr(){

		RestSession session=getNewHttpSession();
		String host = getHost();
		Integer port = getPort();	    
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
 		params = getControleParameters();		 		 
	    String errorMessage = null;
	    
	    try 
	    { 			 
			params.put("arg1", (String) getObject("productId"));
			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					"/rest/bean/com/bbb/rest/catalog/BBBRestCatalogToolImpl/getProductListDetails",
					params, "POST");
			String responseData = pd1.readInputStream();
		}
	    catch (IOException e1) {
			errorMessage=e1.getMessage();
	    	if(errorMessage.contains("1004")){
				assertTrue(true);				
			}
	    	else assertTrue(false);
	    	e1.printStackTrace();
		} catch (RestClientException e1)  {
			errorMessage=e1.getMessage();
			if(errorMessage.contains("1004")){
				assertTrue(true);				
			}else assertTrue(false);
	    	e1.printStackTrace();
		}
	    assertNotNull(errorMessage);
	}	 
}
