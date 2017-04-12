
package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;
import com.bbb.rest.framework.BaseTestCase;

public class TestProductDetail extends BaseTestCase {

	public TestProductDetail(String name) {
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

		public void testProductDetails() throws JSONException, IOException,RestClientException{
			 
			RestSession session = getNewHttpSession();
			String host = getHost();
			Integer port = getPort();   
			
			session.setScheme("http"); 
			try{
			Map<String, Object> params = new HashMap<String, Object>();
	 		params = getControleParameters();		 
	 	     
		    String productId = (String) getObject("productId");	     
				RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
						"/rest/bean/com/bbb/rest/catalog/BBBRestCatalogToolImpl/getProductDetails",
						params, new String[] {productId}, "POST");
			 	String responseData = pd1.readInputStream();	
				System.out.println(responseData);
				if (responseData != null) {
	 			 JSONObject json = new JSONObject(responseData);
	 		     JSONObject productVO = (JSONObject)json.getJSONObject("productVO");
	 			 String productIdResponce = (String)productVO.get("productId"); 			 
	 			 assertNotNull(productIdResponce);				 
				}
		}
			catch (RestClientException e) {
				assertNotNull(e.getMessage());
			}
		}
	public void testProductDetailsErr() throws JSONException,IOException{

		BBBRestSession session = (BBBRestSession) getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		 
		String productId = (String) getObject("productId");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ "/rest/bean/com/bbb/rest/catalog/BBBRestCatalogToolImpl/getProductDetails", params, new String[] {productId}, "POST");
			String responseData = pd1.readInputStream();	

			if (responseData != null) {
				JSONObject json = new JSONObject(responseData);
	 		     Boolean errorExist = (Boolean)json.get("errorExist");
				assertTrue(errorExist);

			}
		} catch (RestClientException e) {		 
			String errorMessage=e.getMessage();
			if(errorMessage.contains("1004")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}		 
		}                 
	}	 
	
	public void testGetProductDetailsByUPC() throws JSONException,IOException{
		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort(); 
		session.setScheme("http"); 
		RestResult pd1;
		Map<String, Object> params = new HashMap<String, Object>();
 		params = getControleParameters();	
 		 String upcCode = (String) getObject("upcCode");
 		  String lon = (String) getObject("lon");	    
 		  String lat = (String) getObject("lat");
 		 try {
 		   pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					"/rest/bean/com/bbb/rest/catalog/BBBRestCatalogToolImpl/getProductDetailsByUPC",
					params, new String[] {upcCode,lon,lat}, "POST");
		 	String responseData = pd1.readInputStream();	
			System.out.println(responseData);
			if (responseData != null) {
			 JSONObject json = new JSONObject(responseData);
			 assertNotNull(json);			 
			}
 		 }
 		catch(Exception e){
			//throw new Exception ("SCANNED_SKU_NOT_IN_THE_SYSTEM","SKU_NOT_AVAILABLE_FOR_SCANNED_ITEM")
			String errorMessage= e.getMessage();
			if(errorMessage.contains("8009")){
				assertTrue(true);
			}else{
				assertFalse(true);
			}
			System.out.println("e.getMessage()--->"+ e.getMessage());
		}
			
				
	}
}

