/**
 * 
 */
package com.bbb.rest.cart;

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
 * @author Prashanth Bhoomula
 *
 */
public class TestApplyCouponToOrder extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestApplyCouponToOrder(String name) {
		super(name);
		
	}


	/**
	 * Test for Add to cart - Single Item 
	 * @throws JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testApplyCoupon() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession(); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		Map<String, String> couponList = new HashMap<String, String>();
		params.put("couponList.0", (String) getObject("couponCode"));		
		//params.put("checkoutState.checkoutFailureURLs.CART", (String) getObject("failurePageURL"));
		params.put("atg-rest-return-form-handler-properties", "true");
		pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		assertResponse(pd);
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-properties", "true");
 		pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("applyCouponRequest"), params,"POST");
 		assertResponse(pd);
         if(pd != null)
			pd = null;
		 try {
			mSession.logout();
		  } catch (RestClientException e) {
			e.printStackTrace();
		}
		

	}

	private void assertResponse(RestResult pd) throws JSONException,
			IOException {
		JSONObject json = new JSONObject(pd.readInputStream());
		String result = null;
		System.out.println("Check values :- "+json);
 			
 		if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 		}
 		assertNull(result);
	}
	
	
}
