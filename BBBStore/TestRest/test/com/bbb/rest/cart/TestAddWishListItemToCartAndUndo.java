package com.bbb.rest.cart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestAddWishListItemToCartAndUndo  extends BaseTestCase {
	
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	public TestAddWishListItemToCartAndUndo(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testAddWishListItemToCartAndUndo() throws RestClientException, JSONException, IOException{
	
		//for transient user add 3 items to cart
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		pd = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		JSONObject json = new JSONObject(pd.readInputStream());
		System.out.println("result ::: "+json);
		String result = null;
		ArrayList<String> commerceItemId = new ArrayList<String>();
		if(json.has("component")){
		    JSONObject map = json.getJSONObject("component").getJSONObject("order");
		    JSONArray commerceItemVOList = map.getJSONArray("commerceItemVOList");
		    for (int i=0; i<commerceItemVOList.length(); i++) {
		        JSONObject item = commerceItemVOList.getJSONObject(i);
		        commerceItemId.add(item.getString("commerceItemId"));
		    }
		}
		
		//for transient user move all 3 items one by one from cart to saved items
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-properties", true);
		params.put("atg-rest-show-rest-paths", false);
		params.put("moveItemsFromCartLoginURL",(String)getObject("moveItemsFromCartLoginURL"));
		params.put("moveItemsFromCartErrorURL",(String)getObject("moveItemsFromCartErrorURL"));
		params.put("moveItemsFromCartSuccessURL",(String)getObject("moveItemsFromCartSuccessURL"));
		params.put("storeId",(String)getObject("storeId"));
		params.put("quantity",(Long)getObject("quantity1"));
		String commItem=commerceItemId.get(0);
		params.put("currentItemId",commItem);
		RestResult pd1 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/atg/commerce/gifts/GiftlistFormHandler/moveItemsFromCart", params,"POST");
		json = new JSONObject(pd1.readInputStream());
		params.put("quantity",(Long)getObject("quantity2"));
		commItem=commerceItemId.get(1);
		params.put("currentItemId",commItem);
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/atg/commerce/gifts/GiftlistFormHandler/moveItemsFromCart", params,"POST");
		json = new JSONObject(pd2.readInputStream());
		params.put("quantity",(Long)getObject("quantity3"));
		commItem=commerceItemId.get(2);
		params.put("currentItemId",commItem);
		RestResult pd3 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/atg/commerce/gifts/GiftlistFormHandler/moveItemsFromCart", params,"POST");
		json = new JSONObject(pd3.readInputStream());
		assertNotNull(pd1);
		assertNotNull(pd2);
		assertNotNull(pd3);
		
		//for transient user get all items in saved items
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
        params.put("atg-rest-depth",(String)getObject("atg-rest-depth"));
		RestResult pd4 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/profile/session/BBBSavedItemsSessionBean/getItems", params,"POST");
		json = new JSONObject(pd4.readInputStream());
		System.out.println("result ::: "+json);
		
		//for transient user move 2nd item to cart from saved item
		JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
		System.out.println("Items : " + jsonResponseObjArray);
		String id2=((JSONObject)jsonResponseObjArray.get(1)).getString("wishListItemId"); 
		String skuid=((JSONObject)jsonResponseObjArray.get(1)).getString("skuID");
		System.out.println("giftlistitem id "+id2);
		int originalGiftitemNo=jsonResponseObjArray.length();
		System.out.println("originalGiftitemNo  "+originalGiftitemNo);
		params = (HashMap) getControleParameters();
		params.put("wishlistItemId", id2);
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("fromWishlist", true);		
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("atg-rest-return-form-handler-properties", "true");
		int count=2;
		params.put("countNo",count);
		long qtyAdded = 5;
		params.put("quantity",qtyAdded);
		RestResult pd5 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("moveWishListItemToCart"), params,"POST");
		json = new JSONObject(pd5.readInputStream());
		String undoComItemId=null;
		String countNo=	null;
		String quantity=null;
		if(json.has("component")){
		    JSONObject map = json.getJSONObject("component");
		    undoComItemId=map.getString("commerceItemId");
		    countNo=map.getString("countNo");
		    quantity=map.getString("quantity");
		}
		String responseData = pd5.readInputStream();
		System.out.println("Move to Cart Response Data");
		System.out.println("responseData3 : " + responseData);
		
 		
 		//undo
 		 params = (HashMap) getControleParameters();
         params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
 		params.put("jsonResultString", (String) getObject("jsonResultString1"));
 		params.put("quantity",quantity);
 		params.put("countNo",countNo);
 		params.put("undoComItemId",undoComItemId);
 		params.put("undoOpt",true);
 		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
 		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
 		params.put("moveItemsFromCartLoginURL",(String)getObject("addItemToOrderErrorURL"));
 		params.put("moveItemsFromCartErrorURL",(String)getObject("addItemToOrderErrorURL"));
 		params.put("atg-rest-return-form-handler-properties", "true"); 
 		pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("WishListRequest"), params,"POST");
 		json = new JSONObject(pd1.readInputStream());
 		ArrayList<String> skuIdList = new ArrayList<String>();
 		if(json.has("component")){
			JSONObject map = json.getJSONObject("component").getJSONObject("wishListItems");
			JSONArray commerceItemVOList = map.getJSONArray("wishListItems");
			for (int i=0; i<commerceItemVOList.length(); i++) {
				JSONObject item = commerceItemVOList.getJSONObject(i);
				skuIdList.add(item.getString("skuID"));
		    }
		}
		String undoSkuId=skuIdList.get(count-1);
		assertEquals(skuid, undoSkuId);
 		
 		
	}
}
