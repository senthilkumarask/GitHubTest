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

public class TestAddCartItemToWishlist extends BaseTestCase {

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;

	/**
	 * @param name
	 */
	public TestAddCartItemToWishlist(String name) {
		super(name);

	}

	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test to add item from cart to wishlist
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */

	@SuppressWarnings("unchecked")
	public void testCartItemToWishList() throws RestClientException,
			IOException, JSONException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		try {
			mSession.login();
			// login API
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = mSession.createHttpRequest("http://" + getHost()
					+ ":" + getPort() + (String) getObject("loginRequest"),
					params, "POST");
			String responseData2 = pd2.readInputStream();
			params.put("jsonResultString",
					(String) getObject("jsonResultString"));
			params.put("moveItemsFromCartSuccessURL",
					(String) getObject("moveItemsFromCartSuccessURL"));
			params.put("moveItemsFromCartErrorURL",
					(String) getObject("moveItemsFromCartErrorURL"));
			params.put("moveItemsFromCartLoginURL",
					(String) getObject("moveItemsFromCartLoginURL"));
			params.put("productDetailsRedirectUrl",
					(String) getObject("productDetailsRedirectUrl"));
			pd = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("addCartRequest"), params,
					"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			System.out.println("result ::: " + json);

				// Get Current Order Detail
			params = (HashMap) getControleParameters();
			params.put("arg1", false);

			pd = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort()
					+ (String) getObject("currentOrderDetailsRequest"), params,
					"POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			String quantity = null;

			JSONArray shippingGroup = (JSONArray) jsonResponseObj
					.get("shippingGroups");
			if (null != shippingGroup.get(0)) {
				JSONObject shpJson = (JSONObject) (shippingGroup.get(0));
				JSONArray itemVOList = (JSONArray) shpJson
						.get("commerceItemRelationshipVOList");
				if (null != itemVOList.get(0)) {
					JSONObject itemArray = (JSONObject) (itemVOList.get(0));
					quantity = (String) itemArray.get("quantity");
					String singleId = (String) itemArray.get("commerceItemId");
					if (null != quantity && null != singleId) {
						params.put("atg-rest-return-form-handler-exceptions", true);
						params.put("atg-rest-method","POST");
						params.put("itemIdToMove", singleId);
						params.put("quantity", quantity);
						params.put("moveItemsFromCartSuccessURL",
								(String) getObject("moveItemsFromCartSuccessURL"));
						params.put("moveItemsFromCartErrorURL",
								(String) getObject("moveItemsFromCartErrorURL"));
						params.put("moveItemsFromCartLoginURL",
								(String) getObject("moveItemsFromCartLoginURL"));
						params.put("productDetailsRedirectUrl",
								(String) getObject("productDetailsRedirectUrl"));

						// Calling Remove item from wishlist webservice
						RestResult pd3 = mSession.createHttpRequest("http://"
								+ getHost() + ":" + getPort()
								+ (String) getObject("addToWishListRequest"),
								params, "POST");
						JSONObject json2 = new JSONObject(pd3.readInputStream());
						System.out.println("Out put JSON ="
								+ json2.toString());
						assertNotNull(json2.toString());

						// Get Current Order Detail
						params = (HashMap) getControleParameters();
						params.put("arg1", false);

						pd = mSession.createHttpRequest("http://" + getHost() + ":"
								+ getPort()
								+ (String) getObject("currentOrderDetailsRequest"), params,
								"POST");

						JSONObject finalJson = new JSONObject(pd.readInputStream());
						System.out.println("Json After move item"+finalJson);
					}
				}
			}
		} finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * test to add item from cart to wishlist
	 *
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */

	@SuppressWarnings("unchecked")
	public void testCartItemToWishListTransient() throws RestClientException,
			IOException, JSONException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd = null;
		try {
			mSession.login();
			// login API

			params.put("jsonResultString",
					(String) getObject("jsonResultString"));
			params.put("moveItemsFromCartSuccessURL",
					(String) getObject("moveItemsFromCartSuccessURL"));
			params.put("moveItemsFromCartErrorURL",
					(String) getObject("moveItemsFromCartErrorURL"));
			params.put("moveItemsFromCartLoginURL",
					(String) getObject("moveItemsFromCartLoginURL"));
			params.put("productDetailsRedirectUrl",
					(String) getObject("productDetailsRedirectUrl"));
			pd = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort() + (String) getObject("addCartRequest"), params,
					"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			System.out.println("After adding Result" + json);

			// Get Current Order Detail
			params = (HashMap) getControleParameters();
			params.put("arg1", false);

			pd = mSession.createHttpRequest("http://" + getHost() + ":"
					+ getPort()
					+ (String) getObject("currentOrderDetailsRequest"), params,
					"POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			System.out.println("Json After adding item"+jsonResponseObj);
			String quantity = null;

			JSONArray shippingGroup = (JSONArray) jsonResponseObj
					.get("shippingGroups");
			if (null != shippingGroup.get(0)) {
				JSONObject shpJson = (JSONObject) (shippingGroup.get(0));
				JSONArray itemVOList = (JSONArray) shpJson
						.get("commerceItemRelationshipVOList");
				if (null != itemVOList.get(0)) {
					JSONObject itemArray = (JSONObject) (itemVOList.get(0));
					quantity = (String) itemArray.get("quantity");
					String singleId = (String) itemArray.get("commerceItemId");
					if (null != quantity && null != singleId) {
						params.put("atg-rest-return-form-handler-exceptions", true);
						params.put("atg-rest-method","POST");
						params.put("itemIdToMove", singleId);
						params.put("quantity", quantity);
						params.put("moveItemsFromCartSuccessURL",
								(String) getObject("moveItemsFromCartSuccessURL"));
						params.put("moveItemsFromCartErrorURL",
								(String) getObject("moveItemsFromCartErrorURL"));
						params.put("moveItemsFromCartLoginURL",
								(String) getObject("moveItemsFromCartLoginURL"));
						params.put("productDetailsRedirectUrl",
								(String) getObject("productDetailsRedirectUrl"));

						// Calling Remove item from wishlist webservice
						RestResult pd3 = mSession.createHttpRequest("http://"
								+ getHost() + ":" + getPort()
								+ (String) getObject("addToWishListRequest"),
								params, "POST");
						JSONObject json2 = new JSONObject(pd3.readInputStream());
						System.out.println("add to wishlist json ="
								+ json2.toString());
						assertNotNull(json2.getString("result"));

						// Get Current Order Detail
						params = (HashMap) getControleParameters();
						params.put("arg1", false);

						pd = mSession.createHttpRequest("http://" + getHost() + ":"
								+ getPort()
								+ (String) getObject("currentOrderDetailsRequest"), params,
								"POST");

						JSONObject finalJson = new JSONObject(pd.readInputStream());
						System.out.println("order will be same because it will redirect to login page"+finalJson);

					}
				}
			}
		} finally {
			if (pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}
}
