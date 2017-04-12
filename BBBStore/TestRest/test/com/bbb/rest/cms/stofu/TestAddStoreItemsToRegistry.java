/**
 * 
 */
package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.rest.framework.BaseTestCase;


/**
 * @author mgup39
 *
 */
public class TestAddStoreItemsToRegistry extends BaseTestCase {

	public TestAddStoreItemsToRegistry(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Test for Add to registry for all valid conditions 
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public void testAddStoreItemsToRegistry() throws RestClientException,
			IOException, JSONException {
		RestResult pd = null;
		RestSession session = getNewHttpSession();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String jsonCollectionObj = (String) getObject("jsonCollectionObj");
		
		pd = session.createHttpRequest("http://"+ getHost()+ ":"+ getPort()+ (String) getObject("addRegistryRequest"),
						params,new String[] { jsonCollectionObj}, "POST");

		String responseData = pd.readInputStream();
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			String result = null;

			if (json.has("BBBExceptions")) {
				result = json.getString("BBBExceptions");
			}
			assertNull(result);
		}

	}
	
	
	/**
	 * Test for Add to registry with invalid sku
	 * @throws RestClientException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public void testAddStoreItemsToRegistrySkuError() throws IOException,
			JSONException {
		RestSession session = getNewHttpSession();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String jsonCollectionObj = (String) getObject("jsonCollectionObj");

		try {
			RestResult pd = session.createHttpRequest("http://" + getHost()
					+ ":" + getPort()
					+ (String) getObject("addRegistryRequest"), params,
					new String[] { jsonCollectionObj }, "POST");
		} catch (RestClientException e) {
			if (e.getMessage().contains("BBBBusinessException")) {
				assertTrue(true);
			}else {
				assertTrue(false);
			}

		}

	}  
	
	
	/**
	 * Test for Add to registry with invalid registry id
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAddStoreItemsToRegistryError() throws IOException, JSONException{
		RestSession session = getNewHttpSession();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String jsonCollectionObj = (String) getObject("jsonCollectionObj");
		
		try {
			RestResult pd = session.createHttpRequest("http://" + getHost()
					+ ":" + getPort()
					+ (String) getObject("addRegistryRequest"), params,
					new String[] { jsonCollectionObj }, "POST");
		} catch (RestClientException e) {
			if (e.getMessage().contains("BBBBusinessException")) {
				assertTrue(true);
			}else {
				assertTrue(false);
			}

		}

	}
	
	/**
	 * Test for Add to registry with invalid quantity
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void testAddStoreItemsToRegistryQuantityError() throws IOException, JSONException{
		RestSession session = getNewHttpSession();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();

		String jsonCollectionObj = (String) getObject("jsonCollectionObj");
		
		try {
			RestResult pd = session.createHttpRequest("http://" + getHost()
					+ ":" + getPort()
					+ (String) getObject("addRegistryRequest"), params,
					new String[] { jsonCollectionObj }, "POST");
		} catch (RestClientException e) {
			if (e.getMessage().contains(BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION)) {
				assertTrue(true);
			} else if (e.getMessage().contains(BBBGiftRegistryConstants.QUANTITY_EXCEPTION)) {
				assertTrue(true);
			} else {
				assertTrue(false);
			}

		}

	}

}
