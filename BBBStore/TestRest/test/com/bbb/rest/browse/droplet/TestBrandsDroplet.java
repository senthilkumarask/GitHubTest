package com.bbb.rest.browse.droplet;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;


public class TestBrandsDroplet extends BaseTestCase{
	private static final String HTTP = "http://";
	private static final String TRUE = "true";
	private static final String ATG_REST_RETURN_FORM_HANDLER_PROPERTIES = "atg-rest-return-form-handler-properties";
	private static final String CREATE_BRANDS_DROPLET_REQUEST = "createBrandsDropletRequest";
	private static final String FORM_EXCEPTIONS = "formExceptions";
	public final static String PARAMETER_ALPHABET_BRANDLIST_MAP = "alphabetBrandListMap";
	public final static String PARAMETER_FEATURED_BRANDS = "featuredBrands";
	
	public TestBrandsDroplet(String name) {
		super(name);
	}

	RestSession mSession;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testAllBrandResults()  throws RestClientException, JSONException, IOException {
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);		
		RestResult pd =  null;
		mSession.login();
		try {
			params = (HashMap) getControleParameters();
            params.put(ATG_REST_RETURN_FORM_HANDLER_PROPERTIES, TRUE);
			 
			pd = mSession.createHttpRequest(HTTP + getHost() +":" + "7003"+(String) getObject(CREATE_BRANDS_DROPLET_REQUEST), params,"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			String result = null;
			System.out.println(json);
			if(json.has(FORM_EXCEPTIONS)){
	 			result = json.getString(FORM_EXCEPTIONS);
	 			System.out.println("formExceptions="+result);
	 		}
			assertNull(result);
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testBrandDetails() throws Exception
	{
		mSession = getNewHttpSession();
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);		
		RestResult pd =  null;
		mSession.login();
		try {
			params = (HashMap) getControleParameters();            
            params.put(ATG_REST_RETURN_FORM_HANDLER_PROPERTIES, TRUE);
			 
			pd = mSession.createHttpRequest(HTTP + getHost() +":" + "7003"+(String) getObject(CREATE_BRANDS_DROPLET_REQUEST), params,"POST");
			JSONObject json = new JSONObject(pd.readInputStream());
			String result = null;
			System.out.println(json);
			
			if(json.has(FORM_EXCEPTIONS)){
	 			result = json.getString(FORM_EXCEPTIONS);
	 			System.out.println("formExceptions="+result);
	 		}
			assertNull(result);
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				mSession.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}
}
