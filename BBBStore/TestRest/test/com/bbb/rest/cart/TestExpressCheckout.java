/**
 * 
 */
package com.bbb.rest.cart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
public class TestExpressCheckout extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestExpressCheckout(String name) {
		super(name);
		
	}

	/**
	 * Test for Add to cart - Single Item 
	 * @throws JSONException 
	 * @throws RestClientException 
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testExpressCheckout() throws JSONException, RestClientException, IOException{
		mSession = getNewHttpSession(); 
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		Map<String, String> couponList = new HashMap<String, String>();
		
		pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		assertResponse(pd);
 		pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("expressCheckouRequest"), params,"POST");
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
 			
 		if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 		}
 		assertNull(result);
	}
	
}
