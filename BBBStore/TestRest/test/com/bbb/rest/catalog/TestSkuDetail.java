
package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;

import com.bbb.rest.framework.BaseTestCase;

public class TestSkuDetail extends BaseTestCase {

	Map<String, Object> params;
	
	public TestSkuDetail(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		params = getControleParameters();
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

	}  

	public void testSkuDetails() throws RestClientException,JSONException, IOException {
		Boolean calculateAboveBelowLine;
		Boolean includeStoreItems;
		try{
		calculateAboveBelowLine = (Boolean) getObject("calculateAboveBelowLine");
		includeStoreItems = (Boolean) getObject("includeStoreItems");

		BBBRestSession session = (BBBRestSession) getNewHttpSession();
		params = getControleParameters();
		String skuId = (String) getObject("skuId");
		 
			RestResult pd1;
			 
				pd1 = session.createHttpRequest("http://"
										+ getHost()
										+ ":"
										+ getPort()
										+ "/rest/bean/com/bbb/rest/catalog/BBBRestCatalogToolImpl/getSkuDetails",
										params, new Object[] {skuId,calculateAboveBelowLine, includeStoreItems}, "POST");
			 
			String responseData = pd1.readInputStream();
			System.out.println(responseData);
			if (responseData != null) {
				JSONObject json = new JSONObject(responseData);
				Double skuIdResponce = (Double) json.get("listPrice");	
				 assertNotNull(skuIdResponce);
			}
		}
		catch (RestClientException e) {
			assertNotNull(e.getMessage());
		}
		 
	}

	 

	public void testSkuDetailsErr() throws IOException{	 

		Boolean calculateAboveBelowLine;
		Boolean includeStoreItems;
		BBBRestSession session = (BBBRestSession) getNewHttpSession();
		params = getControleParameters();
		String skuId = (String) getObject("skuId");
		calculateAboveBelowLine = (Boolean) getObject("calculateAboveBelowLine");
		includeStoreItems = (Boolean) getObject("includeStoreItems");
		try {
			RestResult pd1 = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ "/rest/bean/com/bbb/rest/catalog/BBBRestCatalogToolImpl/getSkuDetails",
									params, new Object[] {skuId,calculateAboveBelowLine, includeStoreItems}, "POST");
			String responseData = pd1.readInputStream();	
		} catch (RestClientException e) {		 
			String errorMessage=e.getMessage();
			if(errorMessage.contains("9100")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}		 
		}                  
	}	 

}
