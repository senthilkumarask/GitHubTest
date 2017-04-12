/**
 * 
 */
package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.Date;
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
public class TestHandleBabyBook extends BaseTestCase  {

	String email;

	public TestHandleBabyBook(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
		/**
		 * Test for baby book request success scenario
		 * @throws RestClientException 
		 * @throws IOException 
		 * @throws JSONException 
		 */		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void testhandleBabyBook() throws RestClientException, JSONException, IOException{
			mSession = getNewHttpSession("BuyBuyBaby");
			testGetCurrentOrderDetailsTransientUser(mSession);
			mSession.setUseHttpsForLogin(false);
			mSession.setUseInternalProfileForLogin(false);
			RestResult pd =  null;
			try {
				// mSession.login(); 
				 params = (HashMap) getControleParameters();
		         params.put("babyBookVO.type", (String) getObject("type"));
		         params.put("babyBookVO.firstName", (String) getObject("firstName"));
		         params.put("babyBookVO.lastName", (String) getObject("lastName"));
		         params.put("babyBookVO.addressLine1", (String) getObject("addressLine1"));
		         params.put("babyBookVO.addressLine2", (String) getObject("addressLine2"));
		         params.put("babyBookVO.city", (String) getObject("city"));
		         params.put("babyBookVO.state", (String) getObject("state"));
		         params.put("babyBookVO.zipcode", (String) getObject("zipcode"));
	             params.put("babyBookVO.phoneNumber", (String) getObject("phoneNumber"));
	             params.put("babyBookVO.emailAddr", (String) getObject("emailAddr"));
	             params.put("babyBookVO.dateAsString", (String) getObject("dateAsString"));
	             params.put("babyBookVO.emailOffer", (Boolean) getObject("emailOffer"));
	             params.put("babyBookSuccessURL","atg-rest-ignore-redirect");
	             params.put("babyBookErrorURL","atg-rest-ignore-redirect");
				 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("babyBookRequest"), params,"POST");
				 JSONObject json = new JSONObject(pd.readInputStream());
				 String result = null;	
				 System.out.println(json.toString());
	 			 if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			 }
	 			
		 		assertNull(result);
	 			 
		 		
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
		 * Test for baby book request error scenario
		 * @throws RestClientException 
		 * @throws IOException 
		 * @throws JSONException 
		 */		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void testhandleBabyBookError() throws RestClientException, JSONException, IOException{
			mSession = getNewHttpSession("BuyBuyBaby");
			testGetCurrentOrderDetailsTransientUser(mSession);
			mSession.setUseHttpsForLogin(false);
			mSession.setUseInternalProfileForLogin(false);
			RestResult pd =  null;
			try {
				// mSession.login(); 
				 params = (HashMap) getControleParameters();
		         params.put("babyBookVO.type", (String) getObject("type"));
		         params.put("babyBookVO.firstName", (String) getObject("firstName"));
		         params.put("babyBookVO.lastName", (String) getObject("lastName"));
		         params.put("babyBookVO.addressLine1", (String) getObject("addressLine1"));
		         params.put("babyBookVO.addressLine2", (String) getObject("addressLine2"));
		         params.put("babyBookVO.city", (String) getObject("city"));
		         params.put("babyBookVO.state", (String) getObject("state"));
		         params.put("babyBookVO.zipcode", (String) getObject("zipcode"));
	             params.put("babyBookVO.phoneNumber", (String) getObject("phoneNumber"));
	             params.put("babyBookVO.emailAddr", (String) getObject("emailAddr"));
	             params.put("babyBookVO.dateAsString", (String) getObject("dateAsString"));
	             params.put("babyBookVO.emailOffer", (Boolean) getObject("emailOffer"));
	             params.put("babyBookSuccessURL","atg-rest-ignore-redirect");
	             params.put("babyBookErrorURL","atg-rest-ignore-redirect");
				 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("babyBookRequest"), params,"POST");
				 JSONObject json = new JSONObject(pd.readInputStream());
				 String result = null;	
				 System.out.println(json.toString());
	 			 if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println(result);
	 			 }

		 		assertNotNull(result);
		 		
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

	
	
	
	
	
	
	

