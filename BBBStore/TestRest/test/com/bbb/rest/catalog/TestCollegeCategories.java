package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestCollegeCategories extends BaseTestCase {

	@SuppressWarnings("rawtypes")
	Map params;
	RestSession session;

	public TestCollegeCategories(String name) {
		super(name);
	}

	protected void setUp() {
		params = getControleParameters();
	}

	/**
	 * This method is used test Rest API college categories
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testCollegeCategories() throws RestClientException, IOException, JSONException {

		session = getNewHttpSession("BedBathUS");    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getCollegeCategories", params,"POST");
		String responseData = pd0.readInputStream();	
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			String categoryId=json.getString("categoryId");
			System.out.println(" categoryId "+categoryId);
			assertNotNull(categoryId);

		}
	}

	/**
	 * This method is used test Rest API college categories exception scenario
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void testCollegeCategoriesException() throws  IOException, JSONException {

		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		try{
			session.login();

			RestResult pd0 = session.createHttpRequest("http://" + getHost() + ":" + getPort() + "/rest/bean/com/bbb/commerce/catalog/BBBCatalogTools/getCollegeCategories", params,"POST");
		}
		catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("1001")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally {

			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}

}
