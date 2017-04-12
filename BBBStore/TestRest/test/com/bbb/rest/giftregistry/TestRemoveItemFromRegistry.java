package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRemoveItemFromRegistry extends BaseTestCase {
	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestRemoveItemFromRegistry(String name) {
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
	public void testRemoveItemFromRegistry() throws RestClientException, IOException, JSONException{
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

			//call add item to registry
			params.put("atg-rest-return-form-handler-exceptions","true");
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));		 
		
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST"); 
			String responseData = pd.readInputStream();	
			System.out.println("add item response "+responseData);

			//remove item from registry
			String regItemOldQty=(String)getObject("regItemOldQty");
			String httpMethod = (String) getObject("httpmethod");
			String skuId=(String) getObject("skuId");
			String rowId=(String) getObject("rowId");

			String productId=(String)getObject("productId");
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("skuId",skuId);
			params.put("regItemOldQty",regItemOldQty);
			params.put("updateRegistryId",(String) getObject("registryId"));

			params.put("productId",productId);

			params.put("rowId",rowId);
			// common fields
			params.put("successURL",(String)getObject("successURL"));
			params.put("errorURL",(String)getObject("errorURL"));
			
			RestResult updateRegistry = session.createHttpRequest(
					"http://"
							+ getHost()
							+ ":"
							+ getPort()
							+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/removeRegistryItems",
							params, "POST");
			String removeData = updateRegistry.readInputStream();
			System.out.println("responseData" + removeData);

			JSONObject json = new JSONObject(removeData);
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
	public void testRemoveItemFromRegistryNonLogin() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession("BedBathUS");
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;

		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
			//remove item from registry
			String regItemOldQty=(String)getObject("regItemOldQty");
			String httpMethod = (String) getObject("httpmethod");
			String skuId=(String) getObject("skuId");
			String rowId=(String) getObject("rowId");

			String productId=(String)getObject("productId");
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			params.put("skuId",skuId);
			params.put("regItemOldQty",regItemOldQty);
			params.put("updateRegistryId",(String) getObject("registryId"));

			params.put("productId",productId);

			params.put("rowId",rowId);
			// common fields
			params.put("successURL",(String)getObject("successURL"));
			params.put("errorURL",(String)getObject("errorURL"));
			RestResult updateRegistry = session.createHttpRequest(
					"http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/removeRegistryItems",params, "POST");
			String removeData = updateRegistry.readInputStream();
			System.out.println("responseData" + removeData);

			JSONObject json = new JSONObject(removeData);
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
