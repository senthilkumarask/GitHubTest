/**
 *
 */
package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.bbb.rest.framework.BaseTestCase;

import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

/**
 * @author snaya2
 *
 */
public class TestAddItemToRegistry extends BaseTestCase {

	public TestAddItemToRegistry(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}



	HashMap params = null;



	/**
	 * Test for Add to registry
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAddItemToRegistry() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        //session.setUsername((String)getObject("login"));
        //session.setPassword((String) getObject("password"));
        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}



	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			 String responseData = pd.readInputStream();
				System.out.println(responseData);
				if (responseData != null) {
	 			 JSONObject json = new JSONObject(responseData);
				 String result = null;

		 			if(json.has("formExceptions")){
		 				result = json.getString("formExceptions");
		 			}
		 			assertNotNull(result);
		 			session.logout();
				}


	}


	/**
	 * Test for Add to registry with invalid sku
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAddItemToRegistryErr() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

		 try{
			session.login();
			params = (HashMap) getControleParameters();
	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			 String responseData = pd.readInputStream();
				System.out.println("Response:" + responseData);
				if (responseData != null) {
	 			 JSONObject json = new JSONObject(responseData);
				 String result = null;

		 			if(json.has("formExceptions")){
		 				result = json.getString("formExceptions");
		 			}
		 			assertNotNull(result);
		 			session.logout();
				}
		 } catch (RestClientException e) {
				String errorMessage=e.getMessage();
                System.out.println(errorMessage);
                if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
                        assertTrue(true);
                }else{
                        assertFalse(true);
                }
			}
	}

	/**
	 * Test for Add item to registry for Recommender.
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAddItemToRegistryRecommender() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("recommededFlag", (String) getObject("recommededFlag"));
		params.put("registryId", (String) getObject("registryId"));
		params.put("skuId", (String) getObject("skuId"));
		params.put("comment", (String) getObject("comment"));
		params.put("recommendedQuantity", (String) getObject("recommendedQuantity"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			//get response
			String response=pd.readInputStream();
			assertNotNull(response);
			System.out.println("Response: " + response);

			//	parse response
			JSONObject json = new JSONObject(response);
			Boolean result = (Boolean) json.get("result");
			System.out.println("Email Gift Registry Recommendation message json="+result);
			assertTrue("Result is false", result);


	}

	/**
	 * Accept Recommendation from Pending Tab.
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAcceptRecommendationRegistry() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("recommededFlag", (String) getObject("recommededFlag"));
		params.put("registryId", (String) getObject("registryId"));
		params.put("skuId", (String) getObject("skuId"));
		params.put("comment", (String) getObject("comment"));
		params.put("recommendedQuantity", (String) getObject("recommendedQuantity"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			//get response
			String response=pd.readInputStream();
			assertNotNull(response);
			System.out.println("Response: " + response);

			//	parse response
			JSONObject json = new JSONObject(response);
			Boolean result = (Boolean) json.get("result");
			System.out.println("Email Gift Registry Recommendation message json="+result);
			assertTrue("Result is false", result);
	}

	/**
	 * Accept Recommendation from Pending Tab negative scenario.
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAcceptRecommendationFromPendingTabErr() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("recommendedQuantity", (String) getObject("recommendedQuantity"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			//get response
			String response=pd.readInputStream();
			assertNotNull(response);
			System.out.println("Response: " + response);

			//	parse response
			JSONObject json = new JSONObject(response);
			Boolean result = (Boolean) json.get("result");
			System.out.println("Email Gift Registry Recommendation message json="+result);
			assertFalse("Result is false", result);
	}


	/**
	 * Accept Recommendation from Declined Tab.
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAcceptRecommendationFromDeclinedTab() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			//get response
			String response=pd.readInputStream();
			assertNotNull(response);
			System.out.println("Response: " + response);

			//	parse response
			JSONObject json = new JSONObject(response);
			Boolean result = (Boolean) json.get("result");
			System.out.println("Email Gift Registry Recommendation message json="+result);
			assertTrue("Result is false", result);
	}

	/**
	 * Accept Recommendation from Declined Tab negative scenario.
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAcceptRecommendationFromDeclinedTabErr() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("recommendedQuantity", (String) getObject("recommendedQuantity"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			//get response
			String response=pd.readInputStream();
			assertNotNull(response);
			System.out.println("Response: " + response);

			//	parse response
			JSONObject json = new JSONObject(response);
			Boolean result = (Boolean) json.get("result");
			System.out.println("Email Gift Registry Recommendation message json="+result);
			assertFalse("Result is false", result);
	}

	/**
	 * This method is used to unit test decline recommendation from
	 * pending tab for positive scenario.
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testDeclineRecommendationFromPendingTab() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			//get response
			String response=pd.readInputStream();
			assertNotNull(response);
			System.out.println("Response: " + response);

			//	parse response
			JSONObject json = new JSONObject(response);
			Boolean result = (Boolean) json.get("result");
			System.out.println("Email Gift Registry Recommendation message json="+result);
			assertTrue("Result is false", result);
	}

	/**
	 * This method is used to unit test decline recommendation from
	 * pending tab for negative scenario.
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testDeclineRecommendationFromPendingTabErr() throws RestClientException, IOException, JSONException{
		RestResult pd =  null;
		RestSession session = getNewHttpSession();

        //Login and establish a session and use that session to all call from same user.
        session.setUseInternalProfileForLogin(false);

        session.setUseHttpsForLogin(false);
        session.login();
        params = (HashMap) getControleParameters();

        params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

	        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jasonCollectionObj", (String) getObject("jasonCollectionObj"));
			params.put("atg-rest-return-form-handler-properties", "true");
			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort() +
						"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/addItemToGiftRegistry",params, "POST");

			//get response
			String response=pd.readInputStream();
			assertNotNull(response);
			System.out.println("Response: " + response);

			//	parse response
			JSONObject json = new JSONObject(response);
			Boolean result = (Boolean) json.get("result");
			System.out.println("Email Gift Registry Recommendation message json="+result);
			assertFalse("Result is false", result);
	}

}
