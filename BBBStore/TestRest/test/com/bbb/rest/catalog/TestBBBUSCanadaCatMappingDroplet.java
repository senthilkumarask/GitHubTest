
package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.multisite.SiteContextManager;
import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;

import com.bbb.rest.framework.BaseTestCase;

public class TestBBBUSCanadaCatMappingDroplet extends BaseTestCase {

	Map<String, Object> params;
	
	public TestBBBUSCanadaCatMappingDroplet(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		params = getControleParameters();
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();

	}  

	public void testTestBBBUSCanadaCatMappingDropletUS() throws RestClientException,JSONException, IOException {
		String categoryId = (String) getObject("categoryId");
		BBBRestSession session = (BBBRestSession) getNewHttpSession((String)getObject("siteId"));
		params = getControleParameters();
		params.put("X-bbb-site-id", "BedBathUS");
		RestResult pd1;
		pd1 = session.createHttpRequest("http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getUSCanadaCategoryMapping",params, new Object[] {categoryId}, "POST");
		String responseData = pd1.readInputStream();
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			String returnPostURL = (String) json.get("canadaCategoryId");
			 assertNotNull(returnPostURL);
		}
	}

	 

	public void testTestBBBUSCanadaCatMappingDropletCa() throws RestClientException,JSONException, IOException {
		String categoryId = (String) getObject("categoryId");
		BBBRestSession session = (BBBRestSession) getNewHttpSession((String)getObject("siteId"));
		params = getControleParameters();
		params.put("X-bbb-site-id", "BedBathCanada");
		RestResult pd1;
		pd1 = session.createHttpRequest("http://"+ getHost()+ ":"+ getPort()+ "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getUSCanadaCategoryMapping",params, new Object[] {categoryId}, "POST");
		String responseData = pd1.readInputStream();
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			String returnPostURL = (String) json.get("usCategoryId");
			 assertNotNull(returnPostURL);
		}
	}	 

}
