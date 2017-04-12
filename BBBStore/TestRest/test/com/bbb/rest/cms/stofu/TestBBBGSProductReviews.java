/**
 * 
 */
package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;


/**
 * @author mgup39
 *
 */
public class TestBBBGSProductReviews extends BaseTestCase {

	public TestBBBGSProductReviews(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Test for getting the product Reviews
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public void testGetProductReviews() throws RestClientException,
			IOException, JSONException {
		RestResult pd = null;
		RestSession session = getNewHttpSession();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String productId = (String) getObject("productId");
		String sortingOrder = (String) getObject("sortingOrder");
		
		pd = session.createHttpRequest("http://"+ getHost()+ ":"+ getPort()+ (String) getObject("productReviewRequest"),
						params,new String[] { productId,sortingOrder}, "POST");

		String responseData = pd.readInputStream();
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean hasErrors = (Boolean)json.get("hasErrors");
			assertFalse(hasErrors);
		}

	}
	
	
	/**
	 * Test for getting the product reviews in invalid bazaar voice details condition
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public void testGetProductReviewsError() throws IOException,
			JSONException, RestClientException {
		RestResult pd = null;
		RestSession session = getNewHttpSession();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String productId = (String) getObject("productId");
		String sortingOrder = (String) getObject("sortingOrder");
		
		pd = session.createHttpRequest("http://"+ getHost()+ ":"+ getPort()+ (String) getObject("productReviewRequest"),
						params,new String[] { productId, sortingOrder}, "POST");

		String responseData = pd.readInputStream();
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean hasErrors = (Boolean)json.get("hasErrors");
			assertTrue(hasErrors);
		}

	}  
	
}
