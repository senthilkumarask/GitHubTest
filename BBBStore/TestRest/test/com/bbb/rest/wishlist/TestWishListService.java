package com.bbb.rest.wishlist;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestWishListService extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestWishListService(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to update a gift list item id with random quantity
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings("unchecked")
	public void testUpdateGiftlistItems() throws IOException, JSONException, RestClientException{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		mSession.login();
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd1.readInputStream();
		if (responseData2 != null) {
			JSONObject json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

		Long quantity=	(long)(Math.random()*100);
		String currentItemId=(String) getObject("currentItemId");
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("currentItemId",currentItemId );
		params.put("quantityToUpdate",quantity);
		params.put("atg-rest-depth", "2");
		System.out.println("quantity to update "+quantity);
		  params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateWishListRequest"), params,"POST");
		JSONObject json = new JSONObject(pd2.readInputStream());
		System.out.println("update to wishlist json ="+json.toString());
		System.out.println("formhandler return  "+json.getString("result"));
		assertNotNull(json.getString("result"));

		//check if quantity is correctly updated
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response=pd3.readInputStream();
		JSONObject json1 = new JSONObject(response);
		JSONArray jsonResponseObjArray = new JSONArray(json1.getString("atgResponse"));
		for(int i=0;i<jsonResponseObjArray.length();i++){
			JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(i);
			String giftListItemId=jsonChildObj.getString("wishListItemId");
			String repItem=jsonChildObj.getString("giftRepositoryItem");;
			JSONObject json2 = new JSONObject(repItem);
			Long quantityDesired=json2.getLong("quantityDesired");;
			System.out.println("giftListItemId "+giftListItemId+" currentItemId "+currentItemId+" quantityDesired "+quantityDesired+" quantity to update "+quantity);
			if(giftListItemId.equalsIgnoreCase(currentItemId)){
				assertEquals(quantity,quantityDesired );
			}
		}
	}


	public void testUpdateGiftlistItemsTransient() throws IOException, JSONException, RestClientException{
		String jsonResultString=(String) getObject("jsonResultString");
		RestSession	session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		session.login();

		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", jsonResultString);
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd3 = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");

		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response=pd3.readInputStream();
		System.out.println(" get gift item before add "+response);
		assertNotNull(response);

		JSONObject json = new JSONObject(response);
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
		String id=jsonChildObj.getString("wishListItemId");
		int originalGiftitemNo=jsonResponseObjArray.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);

		/**
		 * Update item to update quantity
		 */
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("currentItemId", id);
		params.put("quantityToUpdate", "0");
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth1"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd4 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateWishListRequest"), params,"POST");
		response=pd4.readInputStream();
		System.out.println("update item  "+response);
		JSONObject json2 = new JSONObject(response);
		String updateResponse=json2.getString("result");
		System.out.println("updateResponse  "+updateResponse);
		assertNotNull(updateResponse);

		/**
		 * get wishlist items after setting 1 item quantity as 0
		 */
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response3= pd3.readInputStream();
		System.out.println(" get gift item after update "+response3);
		assertNotNull(response3);
		JSONObject json3 = new JSONObject(response3);
		JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
		int giftitemNoAfterRemove=jsonResponseObjArray3.length();
		System.out.println("giftitemNoAfterRemove "+giftitemNoAfterRemove);
		//check that 1 item quantity has been updated as 0
		assertEquals(giftitemNoAfterRemove, (originalGiftitemNo-1));
	}

	/**
	 * test to update a gift item with quantity zero
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings({ "unchecked", "unused" })
	public void testUpdateGiftlistItemsForQtyZero() throws JSONException, IOException, RestClientException{

		String login=(String) getObject("login");
		String password=(String) getObject("password");
		 String jsonResultString=(String) getObject("jsonResultString");
		 this.addItem(login, password, jsonResultString);
		  jsonResultString=(String) getObject("jsonResultString1");
			 this.addItem(login, password, jsonResultString);

			 RestSession	session = getNewHttpSession();
			 session.setUseHttpsForLogin(false);
			 session.setUseInternalProfileForLogin(false);
				RestResult pd =  null;
				session.login();
		System.out.println("login email "+login);
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("value.login", login);
		params.put("value.password", password);
		  params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd1 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd1.readInputStream();
		if (responseData2 != null) {
			JSONObject jsonObj = new JSONObject(responseData2);
			System.out.println("Login Status : "+jsonObj.toString());
		}

		RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response=pd3.readInputStream();
		System.out.println(" get gift item before add "+response);
		assertNotNull(response);


		/**
		 * get gift list items to get original no of items
		 */

		 pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		 response=pd3.readInputStream();
		System.out.println(" get gift item before update "+response);
		assertNotNull(response);
		JSONObject json = new JSONObject(response);
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
		String giftRepositoryItem=jsonChildObj.getString("giftRepositoryItem");
		JSONObject item = new JSONObject(giftRepositoryItem);
		String giftListItemId = item.getString("id");
		int originalGiftitemNo=jsonResponseObjArray.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);

		/**
		 * Update item to update quantity
		 */
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("currentItemId", giftListItemId);
		params.put("quantityToUpdate", (String) getObject("quantityToUpdate"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth1"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd4 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateWishListRequest"), params,"POST");
		response=pd4.readInputStream();
		System.out.println("update item  "+response);
		JSONObject json2 = new JSONObject(response);
		String updateResponse=json2.getString("result");
		System.out.println("updateResponse  "+updateResponse);
		assertNotNull(updateResponse);

		/**
		 * get wishlist items after setting 1 item quantity as 0
		 */
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response3= pd3.readInputStream();
		System.out.println(" get gift item after update "+response3);
		assertNotNull(response3);
		JSONObject json3 = new JSONObject(response3);
		JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
		int giftitemNoAfterRemove=jsonResponseObjArray3.length();
		System.out.println("giftitemNoAfterRemove "+giftitemNoAfterRemove);
		//check that 1 item quantity has been updated as 0
		assertEquals(giftitemNoAfterRemove, (originalGiftitemNo-1));
	}


	public void testUpdateGiftlistItemsForQtyZeroTransient() throws JSONException, IOException, RestClientException{



		String jsonResultString=(String) getObject("jsonResultString");


		RestSession	session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		session.login();

		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", jsonResultString);
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd3 = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");

		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response=pd3.readInputStream();
		System.out.println(" get gift item before add "+response);
		assertNotNull(response);

		JSONObject json = new JSONObject(response);
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
		String id=jsonChildObj.getString("wishListItemId");
		int originalGiftitemNo=jsonResponseObjArray.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);

		/**
		 * Update item to update quantity
		 */
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("currentItemId", id);
		params.put("quantityToUpdate", "0");
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth1"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd4 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("updateWishListRequest"), params,"POST");
		response=pd4.readInputStream();
		System.out.println("update item  "+response);
		JSONObject json2 = new JSONObject(response);
		String updateResponse=json2.getString("result");
		System.out.println("updateResponse  "+updateResponse);
		assertNotNull(updateResponse);

		/**
		 * get wishlist items after setting 1 item quantity as 0
		 */
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response3= pd3.readInputStream();
		System.out.println(" get gift item after update "+response3);
		assertNotNull(response3);
		JSONObject json3 = new JSONObject(response3);
		JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
		int giftitemNoAfterRemove=jsonResponseObjArray3.length();
		System.out.println("giftitemNoAfterRemove "+giftitemNoAfterRemove);
		//check that 1 item quantity has been updated as 0
		assertEquals(giftitemNoAfterRemove, (originalGiftitemNo-1));
	}
	/**
	 * Method to add wish list item for a user
	 * @param login
	 * @param password
	 * @param jsonResultString
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void addItem(String login,String password ,String jsonResultString) throws RestClientException, IOException, JSONException{


		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", login);
			params.put("value.password", password);
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("jsonResultString", jsonResultString);
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd3 = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
			JSONObject json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
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
	 * test to update a gift item with quantity zero
	 * @throws IOException
	 * @throws JSONException
	 * @throws RestClientException
	 */

	@SuppressWarnings({ "unchecked", "unused" })
	public void testRemoveGiftlistItems() throws JSONException, IOException, RestClientException{

		String login=(String) getObject("login");
		String password=(String) getObject("password");
		 String jsonResultString=(String) getObject("jsonResultString");
		 this.addItem(login, password, jsonResultString);
		  jsonResultString=(String) getObject("jsonResultString1");
			 this.addItem(login, password, jsonResultString);
			 jsonResultString=(String) getObject("jsonResultString2");
			 this.addItem(login, password, jsonResultString);
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		mSession.login();
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		 params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd1.readInputStream();
		if (responseData2 != null) {
			JSONObject jsonObj = new JSONObject(responseData2);
			System.out.println("Login Status : "+jsonObj.toString());
		}

		//add item
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");


		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString1"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		 pd2 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");
		System.out.println("add item2: "+pd2.readInputStream());
		/**
		 * get gift list items to get original no of items
		 */

		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response=pd3.readInputStream();

		assertNotNull(response);
		JSONObject json = new JSONObject(response);
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
		String id=jsonChildObj.getString("wishListItemId");
		System.out.println("giftlistitem id "+id);
		System.out.println("wish list items before remove  ");
		System.out.println(jsonResponseObjArray);


		int originalGiftitemNo=jsonResponseObjArray.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);

		/**
		 * remove item from wish list
		 */
		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("itemIdToRemove", id);
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth1"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		//System.out.println(" removing sku "+skuIdToRemove +" with item id "+giftListItemId);
		RestResult pd4 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("removeWishListRequest"), params,"POST");
		String responseData3 = pd4.readInputStream();
		System.out.println("Move to Cart Response Data");
		System.out.println("responseData3 : " + responseData3);
		json = new JSONObject(responseData3);
		 String result = null;

 			if(json.has("formExceptions")){
 				result = json.getString("formExceptions");
 				System.out.println("Result: " + result);
 			}
 		 assertNull(result);

		/**
		 * get wishlist items after setting 1 item quantity as 0
		 */
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		  params.put("atg-rest-return-form-handler-properties", "true");
		pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		String response3= pd3.readInputStream();
		assertNotNull(response3);
		JSONObject json3 = new JSONObject(response3);
		JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
		int giftitemNoAfterRemove=jsonResponseObjArray3.length();
		System.out.println("wish list items after remove ");
		System.out.println(jsonResponseObjArray3);
		System.out.println("giftitemNoAfterRemove "+giftitemNoAfterRemove);
		//check that 1 item has been removed
		assertEquals(giftitemNoAfterRemove, (originalGiftitemNo-1));
	}


	public void testRemoveGiftlistItemsTransient() throws JSONException, IOException, RestClientException{

		 String jsonResultString=(String) getObject("jsonResultString");

		 RestSession	session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		session.login();



		/**
		 * Update item to update quantity
		 */

		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", jsonResultString);
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd3 = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");

		JSONObject json = new JSONObject(pd3.readInputStream());
		System.out.println("add to wishlist json ="+json.toString());
		System.out.println("formhandler return  "+json.getString("result"));
		assertNotNull(json.getString("result"));
		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");

		json = new JSONObject(pd3.readInputStream());
		System.out.println("add to wishlist json ="+json.toString());
		System.out.println("formhandler return  "+json.getString("atgResponse"));
		assertNotNull(json.getString("atgResponse"));
		JSONArray jsonResponseObjArray3 = new JSONArray(json.getString("atgResponse"));
		int giftitemNo=jsonResponseObjArray3.length();

		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		System.out.println("Items : " + jsonResponseObjArray);
		String itemIdToRemove=((JSONObject)jsonResponseObjArray.get(0)).getString("wishListItemId");
		params.put("itemIdToRemove",itemIdToRemove);
		params.put("atg-rest-return-form-handler-properties", "true");
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth1"));
		params.put("atg-rest-return-form-handler-properties", "true");

		RestResult pd4 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("removeItemsFromGiftlist"), params,"POST");
		String response=pd4.readInputStream();
		System.out.println("update item  "+response);
		JSONObject json2 = new JSONObject(response);
		String updateResponse=json2.getString("result");
		System.out.println("updateResponse  "+updateResponse);
		assertNotNull(updateResponse);

		/**
		 * get wishlist items after setting 1 item quantity as 0
		 */
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
		String response3= pd3.readInputStream();
		System.out.println(" get gift item after update "+response3);
		assertNotNull(response3);
		JSONObject json3 = new JSONObject(response3);
		jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
		int giftitemNoAfterRemove=jsonResponseObjArray3.length();
		System.out.println("giftitemNoAfterRemove "+giftitemNoAfterRemove);
		assertEquals(giftitemNo-1, giftitemNoAfterRemove);
		//check that 1 item quantity has been updated as 0

	}
	
	/**
	 * test to confirm that a non logged in user cannot access Remove wish list service
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void testRemoveWishListNonLoggedInUser(){
		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("removeWishListRequest"), params,"POST");
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

	public void testMergeGiftlistItems() throws JSONException, IOException, RestClientException{

		 String jsonResultString=(String) getObject("jsonResultString");

		 RestSession	session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		session.login();

		/**
		 * Update item to update quantity
		 */

		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", jsonResultString);
		params.put("atg-rest-return-form-handler-properties", "true");
		RestResult pd3 = session.createHttpRequest("http://" + getHost()+":" + getPort()+(String) getObject("addWishListRequest"), params,"POST");

		JSONObject json = new JSONObject(pd3.readInputStream());
		System.out.println("add to wishlist json ="+json.toString());
		System.out.println("formhandler return  "+json.getString("result"));
		assertNotNull(json.getString("result"));
		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");

		json = new JSONObject(pd3.readInputStream());
		System.out.println("add to wishlist json ="+json.toString());
		System.out.println("formhandler return  "+json.getString("atgResponse"));
		assertNotNull(json.getString("atgResponse"));


		params = (HashMap) getControleParameters();
		params.put("value.login", (String) getObject("login"));
		params.put("value.password", (String) getObject("password"));
		params.put("atg-rest-return-form-handler-properties", true);
		params.put("atg-rest-return-form-handler-exceptions", true);
		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
		String responseData2 = pd2.readInputStream();
		if (responseData2 != null) {
			json = new JSONObject(responseData2);
			System.out.println("Login Status : "+json.toString());
		}

		/**
		 * get wishlist items after setting 1 item quantity as 0
		 */
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));
		pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
		String response3= pd3.readInputStream();
		System.out.println(" get gift item after update "+response3);
		assertNotNull(response3);
		JSONObject json3 = new JSONObject(response3);
		JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
		int giftitemNoAfterRemove=jsonResponseObjArray3.length();
		System.out.println("giftitemNoAfterRemove "+giftitemNoAfterRemove);
		//check that 1 item quantity has been updated as 0

	}

	public void testMoveFromCartTrasient() throws JSONException, IOException{
		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("atg-rest-depth","2");
			RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
			String response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);
			int currentListofItem = 0;
			JSONObject json3 = new JSONObject(response3);
			if(!(json3.getString("atgResponse") != null)){
				JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
				currentListofItem=jsonResponseObjArray3.length();
			}

			String jsonResultOrderString=(String) getObject("jsonResultOrderString");
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", jsonResultOrderString);
			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("addCartRequest"), params,"POST");
			JSONObject json = new JSONObject(pd3.readInputStream());
			String result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("Result: " + result);
	 			}
	 		 assertNull(result);
	 		params = (HashMap) getControleParameters();
	 		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1", false);

			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort()	+ (String) getObject("currentOrderDetailsRequest"), params, "POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			JSONArray arr = jsonResponseObj.getJSONArray("commerceItemVOList");
			String commerceId = ((JSONObject)arr.get(0)).getString("commerceItemId");
			System.out.println(jsonResponseObj);

			params = (HashMap) getControleParameters();
	 		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("currentItemId", commerceId);
			params.put("moveItemsFromCartLoginURL", (String) getObject("moveItemsFromCartLoginURL"));
			params.put("moveItemsFromCartErrorURL", (String) getObject("moveItemsFromCartErrorURL"));

			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("moveWishListRequest"), params,"POST");

			json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertTrue(json.getBoolean("result"));

			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
			response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);
			json3 = new JSONObject(response3);
			JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
			int currentListofItem2=jsonResponseObjArray3.length();
			assertEquals(currentListofItem+1,currentListofItem2);
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

	public void testMoveFromCartLoggedIn() throws JSONException, IOException{
		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			  params.put("atg-rest-return-form-handler-properties", "true");
			RestResult pd1 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd1.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			params = (HashMap) getControleParameters();
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("atg-rest-depth","2");
			RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
			String response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);
			int currentListofItem = 0;
			JSONObject json3 = new JSONObject(response3);
			if(!(json3.getString("atgResponse") != null)){
				JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
				currentListofItem=jsonResponseObjArray3.length();
			}
			params = (HashMap) getControleParameters();
			String jsonResultOrderString=(String) getObject("jsonResultOrderString");
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", jsonResultOrderString);
			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("addCartRequest"), params,"POST");
			JSONObject json = new JSONObject(pd3.readInputStream());
			String result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("Result: " + result);
	 			}
	 		 assertNull(result);
	 		params = (HashMap) getControleParameters();
	 		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("arg1", false);

			pd = session.createHttpRequest("http://" + getHost() + ":" + getPort()	+ (String) getObject("currentOrderDetailsRequest"), params, "POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			JSONArray arr = jsonResponseObj.getJSONArray("commerceItemVOList");
			String commerceId = ((JSONObject)arr.get(0)).getString("commerceItemId");
			System.out.println(jsonResponseObj);

			params = (HashMap) getControleParameters();
	 		params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("currentItemId", commerceId);
			params.put("quantity", 1);
			params.put("moveItemsFromCartSuccessURL", (String) getObject("moveItemsFromCartLoginURL"));
			params.put("moveItemsFromCartLoginURL", (String) getObject("moveItemsFromCartLoginURL"));
			params.put("moveItemsFromCartErrorURL", (String) getObject("moveItemsFromCartErrorURL"));

			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("moveWishListRequest"), params,"POST");

			json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertTrue(json.getBoolean("result"));

			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
			response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);
			json3 = new JSONObject(response3);
			JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
			int currentListofItem2=jsonResponseObjArray3.length();
			assertTrue(currentListofItem<currentListofItem2);
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


	public void testMoveWishListItemsToCartTrasient() throws JSONException, IOException{
		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			String jsonResultOrderString=(String) getObject("jsonResultString");
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", jsonResultOrderString);
			RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
			String response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);
			int currentListofItem = 0;
			JSONObject json3 = new JSONObject(response3);
			if(!(json3.getString("atgResponse") != null)){
				JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
				currentListofItem=jsonResponseObjArray3.length();
			}



			params = (HashMap) getControleParameters();
			jsonResultOrderString=(String) getObject("jsonResultOrderString");
			params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", jsonResultOrderString);
			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("addCartRequest"), params,"POST");


			String jsonResultString=(String) getObject("jsonResultString");
			params.put("jsonResultString", jsonResultString);
			params.put("moveItemsFromCartLoginURL", (String) getObject("moveItemsFromCartLoginURL"));
			params.put("moveItemsFromCartErrorURL", (String) getObject("moveItemsFromCartErrorURL"));
			JSONObject json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result"));
			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("moveWishListRequest"), params,"POST");

			json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result"));

			json = new JSONObject(pd3.readInputStream());
			String result = null;

	 			if(json.has("formExceptions")){
	 				result = json.getString("formExceptions");
	 				System.out.println("Result: " + result);
	 			}
	 		 assertNull(result);
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result"));

			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("atg-rest-return-form-handler-properties", "true");

			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getItem"), params,"POST");
			response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);
			json3 = new JSONObject(response3);
			JSONArray jsonResponseObjArray3 = new JSONArray(json3.getString("atgResponse"));
			int currentListofItem2=jsonResponseObjArray3.length();

			assertEquals(currentListofItem+1,currentListofItem2);
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

	public void testMoveWishListItemsToCart() throws JSONException, IOException{
		RestSession session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			session.login();
			params = (HashMap) getControleParameters();
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			params.put("atg-rest-return-form-handler-properties", true);
			params.put("atg-rest-return-form-handler-exceptions", true);
			RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}
			params = (HashMap) getControleParameters();
			String jsonResultOrderString=(String) getObject("jsonResultString");
			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("jsonResultString", jsonResultOrderString);
			RestResult pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String)getObject("addWishListRequest"), params,"POST");


			String jsonResultString=(String) getObject("jsonResultString");
			params.put("jsonResultString", jsonResultString);
			JSONObject json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result"));
			params.put("moveItemsFromCartLoginURL", (String) getObject("moveItemsFromCartLoginURL"));
			params.put("moveItemsFromCartErrorURL", (String) getObject("moveItemsFromCartErrorURL"));
			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("moveWishListRequest"), params,"POST");

			json = new JSONObject(pd3.readInputStream());
			System.out.println("add to wishlist json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result"));

			params.put("atg-rest-return-form-handler-properties", "true");
			params.put("atg-rest-return-form-handler-properties", "true");

			pd3 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("getWishListItems"), params,"POST");
			String response3= pd3.readInputStream();
			System.out.println(" get gift item after update "+response3);
			assertNotNull(response3);
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
