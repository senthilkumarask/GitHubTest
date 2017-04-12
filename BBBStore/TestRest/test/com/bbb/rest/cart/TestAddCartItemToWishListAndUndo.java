package com.bbb.rest.cart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.repository.RepositoryItem;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestAddCartItemToWishListAndUndo extends BaseTestCase {
	
	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	
	public TestAddCartItemToWishListAndUndo(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAddCartItemToWishListAndUndo() throws RestClientException, JSONException, IOException{
		
		//for transient user add items to cart
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd1 =  null;
		mSession.login();
		params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString"));
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		JSONObject json = new JSONObject(pd1.readInputStream());
		System.out.println("result ::: "+json);
		String result = null;
		ArrayList<String> commerceItemId = new ArrayList<String>();
		ArrayList<String> skuIdList = new ArrayList<String>();
		if(json.has("component")){
		    JSONObject map = json.getJSONObject("component").getJSONObject("order");
		    JSONArray commerceItemVOList = map.getJSONArray("commerceItemVOList");
		    for (int i=0; i<commerceItemVOList.length(); i++) {
		        JSONObject item = commerceItemVOList.getJSONObject(i);
		        commerceItemId.add(item.getString("commerceItemId"));
		        skuIdList.add(item.getString("skuId"));
		    }
		}
		
		//for transient user move item at count position from cart to saved items
		params = (HashMap) getControleParameters();
		params.put("atg-rest-return-form-handler-properties", true);
		params.put("atg-rest-show-rest-paths", false);
		params.put("moveItemsFromCartLoginURL",(String)getObject("moveItemsFromCartLoginURL"));
		params.put("moveItemsFromCartErrorURL",(String)getObject("moveItemsFromCartErrorURL"));
		params.put("moveItemsFromCartSuccessURL",(String)getObject("moveItemsFromCartSuccessURL"));
		params.put("quantity",(Long)getObject("quantity"));
		int count=(Integer) getObject("countNo");
		String commItem=commerceItemId.get(count-1);
		String skuId=skuIdList.get(count-1);
		params.put("currentItemId",commItem);
		params.put("countNo",count);
		params.put("storeId",(String)getObject("storeId"));
		RestResult pd2 = mSession.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/atg/commerce/gifts/GiftlistFormHandler/moveItemsFromCart", params,"POST");
		json = new JSONObject(pd2.readInputStream());
		String quantity=null;
		String countNo=null;
		String storeId=null;
		String itemMoveFromCartID=null;
		if(json.has("component")){
		    JSONObject map = json.getJSONObject("component");
		    quantity=map.getString("quantity");
		    countNo=map.getString("countNo");
		    storeId= map.getString("storeId");
		    itemMoveFromCartID=map.getString("itemMoveFromCartID");
		    assertNotNull(quantity);
		    assertNotNull(countNo);
		    assertNotNull(itemMoveFromCartID);
		}
		
		//for transient user undo move item at count position from cart to saved items
	    params = (HashMap) getControleParameters();
        params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("jsonResultString", (String) getObject("jsonResultString1"));
		params.put("quantity",quantity);
		params.put("countNo",countNo);
		params.put("storeId",storeId);
		params.put("undoCheck",true);
		params.put("fromCart",true);
		params.put("fromWishlist",true);
		params.put("wishlistItemId",itemMoveFromCartID);
		params.put("addItemToOrderSuccessURL",(String)getObject("addItemToOrderSuccessURL"));
		params.put("addItemToOrderErrorURL",(String)getObject("addItemToOrderErrorURL"));
		params.put("atg-rest-return-form-handler-properties", "true"); 
		pd1 = mSession.createHttpRequest("http://" + getHost() +":" + getPort()+(String) getObject("addCartRequest"), params,"POST");
		json = new JSONObject(pd1.readInputStream());
		System.out.println("result ::: "+json);
		result = null;
		if(json.has("component")){
			JSONObject map = json.getJSONObject("component").getJSONObject("order");
			JSONArray commerceItemVOList = map.getJSONArray("commerceItemVOList");
			for (int i=0; i<commerceItemVOList.length(); i++) {
				JSONObject item = commerceItemVOList.getJSONObject(i);
				skuIdList.add(item.getString("skuId"));
		    }
		}
		String undoSkuId=skuIdList.get(count-1);
		
		//check the skuid before move to saved item and after undo 
		assertEquals(skuId, undoSkuId);
	}
}
