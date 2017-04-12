package com.bbb.rest.account;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCanadaStoreDetails extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;

	public TestCanadaStoreDetails(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}

	/**
	 * This method is used test Rest API for Canada Store Details
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testGetCanadaStoreListDetails() throws RestClientException, IOException, JSONException {

		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getCanadaStoreLocatorInfo", params,"POST");
		String responseData = pd0.readInputStream();	
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			JSONArray jsonResponseObjArray = new JSONArray(json.getString("atgResponse"));
			JSONObject jsonChildObj = jsonResponseObjArray.getJSONObject(0);
			String city=jsonChildObj.getString("city");
			assertNotNull(city);
		}
	}
	
	
	
}
