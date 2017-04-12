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
public class TestHandleBridalBook extends BaseTestCase  {

	String email;

	public TestHandleBridalBook(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	
		/**
		 * Test for bridal book request success scenario
		 * @throws RestClientException 
		 * @throws IOException 
		 * @throws JSONException 
		 */		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void testhandleBridalBook() throws RestClientException, JSONException, IOException{
			mSession = getNewHttpSession("BedBathUS");
			testGetCurrentOrderDetailsTransientUser(mSession);
			mSession.setUseHttpsForLogin(false);
			mSession.setUseInternalProfileForLogin(false);
			RestResult pd =  null;
			try {
				// mSession.login(); 
				 params = (HashMap) getControleParameters();
		         params.put("bridalBookVO.type", (String) getObject("type"));
		         params.put("bridalBookVO.firstName", (String) getObject("firstName"));
		         params.put("bridalBookVO.lastName", (String) getObject("lastName"));
		         params.put("bridalBookVO.addressLine1", (String) getObject("addressLine1"));
		         params.put("bridalBookVO.addressLine2", (String) getObject("addressLine2"));
		         params.put("bridalBookVO.city", (String) getObject("city"));
		         params.put("bridalBookVO.state", (String) getObject("state"));
		         params.put("bridalBookVO.zipcode", (String) getObject("zipcode"));
	             params.put("bridalBookVO.phoneNumber", (String) getObject("phoneNumber"));
	             params.put("bridalBookVO.emailAddr", (String) getObject("emailAddr"));
	             params.put("bridalBookVO.dateAsString", (String) getObject("dateAsString"));
	             params.put("bridalBookVO.emailOffer", (Boolean) getObject("emailOffer"));
	             params.put("bridalBookSuccessURL", "atg-rest-ignore-redirect");
	             params.put("bridalBookErrorURL", "atg-rest-ignore-redirect");
				 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("bridalBookRequest"), params,"POST");
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
		 * Test for bridal book request error scenario
		 * @throws RestClientException 
		 * @throws IOException 
		 * @throws JSONException 
		 */		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void testhandleBridalBookError() throws RestClientException, JSONException, IOException{
			mSession = getNewHttpSession();
			testGetCurrentOrderDetailsTransientUser(mSession);
			mSession.setUseHttpsForLogin(false);
			mSession.setUseInternalProfileForLogin(false);
			RestResult pd =  null;
			try {
				 //mSession.login(); 
				 params = (HashMap) getControleParameters();
		         params.put("bridalBookVO.type", (String) getObject("type"));
		         params.put("bridalBookVO.firstName", (String) getObject("firstName"));
		         params.put("bridalBookVO.lastName", (String) getObject("lastName"));
		         params.put("bridalBookVO.addressLine1", (String) getObject("addressLine1"));
		         params.put("bridalBookVO.addressLine2", (String) getObject("addressLine2"));
		         params.put("bridalBookVO.city", (String) getObject("city"));
		         params.put("bridalBookVO.state", (String) getObject("state"));
		         params.put("bridalBookVO.zipcode", (String) getObject("zipcode"));
	             params.put("bridalBookVO.phoneNumber", (String) getObject("phoneNumber"));
	             params.put("bridalBookVO.emailAddr", (String) getObject("emailAddr"));
	             params.put("bridalBookVO.dateAsString", (String) getObject("dateAsString"));
	             params.put("bridalBookVO.emailOffer", (Boolean) getObject("emailOffer"));
	             params.put("bridalBookSuccessURL", "atg-rest-ignore-redirect");
	             params.put("bridalBookErrorURL", "atg-rest-ignore-redirect");
				 pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("bridalBookRequest"), params,"POST");
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

	
	
	
	
	
	
	

