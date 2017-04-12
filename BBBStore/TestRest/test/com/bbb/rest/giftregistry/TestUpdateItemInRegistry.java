package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestUpdateItemInRegistry extends BaseTestCase {
	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestUpdateItemInRegistry(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * Method to test update item in registry
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateItemInRegistry() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession("BedBathUS");
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		System.out.println("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"));
		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}

			//new reg id  183330286  call to update gift registry


			String httpMethod = (String) getObject("httpmethod");
			String skuId=(String) getObject("skuId");
			String rowId=(String) getObject("rowId");
			String registryId=(String) getObject("registryId");
			String quantity=(String) getObject("quantity");
			String regItemOldQty=(String)getObject("regItemOldQty");
			String purchasedQuantity=(String)getObject("purchasedQuantity");
			String productId=(String)getObject("productId");
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("skuId",skuId);
			params.put("updateRegistryId",registryId);
			params.put("updateQuantity",quantity);
			params.put("productId",productId);
			params.put("regItemOldQty",regItemOldQty);
			params.put("purchasedQuantity",purchasedQuantity);
			params.put("successURL",(String) getObject("successURL"));
			params.put("errorURL",(String) getObject("errorURL"));
			params.put("rowId",rowId);
			// common fields
			
			RestResult updateRegistry = session.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/updateRegistryItems",
							params, "POST");
			String responseData = updateRegistry.readInputStream();
			System.out.println("responseData" + responseData);
			
			JSONObject json = new JSONObject(responseData);
			System.out.println("update item in registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * Method to test update item in registry
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateItemInRegistryNonLogin() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession("BedBathUS");
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		System.out.println("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"));
		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
	

			//new reg id  183330286  call to update gift registry


			String httpMethod = (String) getObject("httpmethod");
			String skuId=(String) getObject("skuId");
			String rowId=(String) getObject("rowId");
			String registryId=(String) getObject("registryId");
			String quantity=(String) getObject("quantity");
			String regItemOldQty=(String)getObject("regItemOldQty");
			String purchasedQuantity=(String)getObject("purchasedQuantity");
			String productId=(String)getObject("productId");
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("skuId",skuId);
			params.put("updateRegistryId",registryId);
			params.put("updateQuantity",quantity);
			params.put("productId",productId);
			params.put("regItemOldQty",regItemOldQty);
			params.put("purchasedQuantity",purchasedQuantity);
			params.put("successURL",(String) getObject("successURL"));
			params.put("errorURL",(String) getObject("errorURL"));
			params.put("rowId",rowId);
			// common fields
			
			RestResult updateRegistry = session.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/updateRegistryItems",
							params, "POST");
			String responseData = updateRegistry.readInputStream();
			System.out.println("responseData" + responseData);
			
			JSONObject json = new JSONObject(responseData);
			System.out.println("update item in registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}


}
