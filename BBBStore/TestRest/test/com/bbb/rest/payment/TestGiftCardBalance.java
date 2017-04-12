package com.bbb.rest.payment;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;
/**
 * Test cases for Gift Card Balance API
 * @author njai13
 *
 */
public class TestGiftCardBalance extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestGiftCardBalance(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to get gift card balance 
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testGetGiftCardBalance() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();
		String pinNumber = (String)getObject("pinNo");
		String cardNumber = (String)getObject("giftCardNo");

		//set params required to make request
		params.put("atg-rest-return-form-handler-properties",true);
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		params.put("giftCardNumber", cardNumber);
		params.put("giftCardPin", pinNumber);

		//call get gift card balance API
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getGiftCardBalance"), params,"POST");

		//get response
		String response=pd2.readInputStream();
		System.out.println("response in giftcard "+response);
		assertNotNull(response);
		
		//parse response
		if (params.get("atg-rest-output").toString()
				.equalsIgnoreCase("json")
				&& response != null) {
			System.out.println("getGiftcardBalance json balance="+response);
			JSONObject json = new JSONObject(response);
			String component=json.getString("component");
			JSONObject balanceJson = new JSONObject(component);
			if (balanceJson.has("balanceBean"))
			{
			assertTrue(true);
			}
			else
			{
				assertTrue(false);
			}
			}
		else
		{
			System.out.println(response);
			assertNotNull(response);
		}
	}

	/**
	 * check for error scenario by setting empty gift card number set
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testGetGiftCardBalanceError() throws IOException, JSONException{
		try{
			session = getNewHttpSession();    
			//Login and establish a session and use that session to all call from same user.
			session.setUseInternalProfileForLogin(false);
			session.setUsername("admin");
			session.setPassword("admin");
			session.setUseHttpsForLogin(false);
			session.login();
			String pinNumber = (String)getObject("pinNo");
			String cardNumber = (String)getObject("giftCardNo");
			params.put("atg-rest-return-form-handler-properties",true);
			//set params required to make request
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
			params.put("giftCardNumber", cardNumber);
			params.put("giftCardPin", pinNumber);

			//call get gift card balance API
			RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getGiftCardBalance"), params,"POST");

			//get response
			String response=pd2.readInputStream();
			assertNotNull(response);
			System.out.println("response in giftcard error "+response);
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& response != null) {
				System.out.println("getGiftcardBalance json balance="+response);
				JSONObject json = new JSONObject(response);
				String component=json.getString("component");
				JSONObject balanceJson = new JSONObject(component);
				if (balanceJson.has("balanceBean"))
				{
					String balanceBean=balanceJson.getString("balanceBean");
					JSONObject balanceVal=new JSONObject(balanceBean);
					if (balanceVal.has("errorMessage"))
					{
						assertEquals(balanceVal.get("errorMessage"), "Unable to fetch balance.  Please try again later.");
					}
					else
					{
						assertTrue(false);
					}
					
				}
				
				}
			else
			{
				System.out.println(response);
				assertNotNull(response);
			}
			
			
		}catch(RestClientException e){
			System.out.println("testGetGiftCardBalanceError error message "+e.getMessage());
		}
	}


	/**
	 * check for error scenario when user has used by all the attempts of entering invalid card details
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testGetGiftCardBalanceMaxAttempt() throws IOException, JSONException{
		try{
			session = getNewHttpSession();    
			//Login and establish a session and use that session to all call from same user.
			session.setUseInternalProfileForLogin(false);
			session.setUsername("admin");
			session.setPassword("admin");
			session.setUseHttpsForLogin(false);
			session.login();
			String pinNumber = (String)getObject("pinNo");
			String cardNumber = (String)getObject("giftCardNo");
			params.put("atg-rest-return-form-handler-properties",true);
			//set params required to make request
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
			params.put("giftCardNumber", cardNumber);
			params.put("giftCardPin", pinNumber);
			int i=0;
			for(i=0;i<=4;i++){
				System.out.println(i+" i loop");
				//call get gift card balance API
				RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("getGiftCardBalance"), params,"POST");

				//get response
				String response=pd2.readInputStream();
				assertNotNull(response);
				//parse response
			
				if(i<3){
					
					if (params.get("atg-rest-output").toString()
							.equalsIgnoreCase("json")
							&& response != null) {
						System.out.println("getGiftcardBalance json balance="+response);
						JSONObject jsonResponse = new JSONObject(response);
						String component=jsonResponse.getString("component");
						JSONObject balanceJson = new JSONObject(component);
						if (balanceJson.has("balanceBean"))
						{
							String balanceBean=balanceJson.getString("balanceBean");
							JSONObject balanceVal=new JSONObject(balanceBean);
							if (balanceVal.has("errorMsg"))
							{
								assertEquals(balanceVal.get("errorMsg"), "Unable to fetch balance.  Please try again later.");
							}
						}
						
						}
					else
					{
						assertNotNull(response);
						System.out.println(response);
					}

				}
				else{
					if (params.get("atg-rest-output").toString()
							.equalsIgnoreCase("json")
							&& response != null) {
						System.out.println("getGiftcardBalance json balance="+response);
						JSONObject jsonResponse = new JSONObject(response);
						String component=jsonResponse.getString("component");
						JSONObject balanceJson = new JSONObject(component);
						if (balanceJson.has("balanceBean"))
						{
							String balanceBean=balanceJson.getString("balanceBean");
							JSONObject balanceVal=new JSONObject(balanceBean);
							if (balanceVal.has("errorMsg"))
							{
								assertEquals(balanceVal.get("errorMsg"), "Maximum Invalid Attempt reached");
							}
						}
						
						}
					else
					{
						assertNotNull(response);
					System.out.println("getGiftcardBalance json url="+response);
					}
					break;
				}
			
			}
		}catch(RestClientException e){

			System.out.println("testGetGiftCardBalanceError error message "+e.getMessage());
		}
	}
	
	
}
