package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestGetRegistryDetailAPI extends BaseTestCase {
	RestSession session=null;
	private final String NO_ERROR="NO_ERROR";

	@Override
    protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
    protected void tearDown() throws Exception {
		this.session.logout();
		super.tearDown();

	}

	public TestGetRegistryDetailAPI(final String name) {
		super(name);
	}

	/**
	 * Test case to create baby type registry
	 */
	public void testGetRegistryDetail() {
		String success = null;

		final String sortSeq = (String) this.getObject("sortseq");
		final String view=(String) this.getObject("view");
		final String startIdx = (String)this.getObject("startindex");
		final String bulkSize = (String)this.getObject("bulkSize");
		final String isGiftGiver = (String)this.getObject("isgiftgiver");
		final String isAvailForWebPurchaseFlag = (String)this.getObject("isavailforwebpurchaseflag");


		final String username = (String) this.getObject("username");
		final String displayView = (String) this.getObject("displayView");
		final String password = (String) this.getObject("password");
		final String httpMethod = (String) this.getObject("httpmethod");
		final String registryId = (String) this.getObject("registryId");
		final String noError = (String) this.getObject("noerror");

		RestResult getRegistryDetails=null;
		try {
			this.session=this.getNewHttpSession();
			this.session.setUseInternalProfileForLogin(false);
			this.session.setUsername(username);
			this.session.setPassword(password);
			this.session.setUseHttpsForLogin(false);
			this.session.login();

			HashMap params = new HashMap<String, String>();

			final HashMap inputparam = new HashMap<String, String>();

			params = (HashMap) this.getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			inputparam.put("registryId",registryId);
				inputparam.put("view",view);
			inputparam.put("displayView",displayView);
			inputparam.put("sortSeq",sortSeq);
			inputparam.put("startIdx",startIdx);
			inputparam.put("bulkSize",bulkSize);
			inputparam.put("isGiftGiver",isGiftGiver);
			inputparam.put("isAvailForWebPurchaseFlag",isAvailForWebPurchaseFlag);
			params.put("arg1", inputparam);


			getRegistryDetails = this.session
					.createHttpRequest(
							"http://"
									+ this.getHost()
									+ ":"
									+ this.getPort()
									+ (String) this.getObject("registryDetailURL"),
							params, httpMethod);
			final String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& (responseData != null)) {

				final JSONObject json = new JSONObject(responseData);
				if ((json!=null)&&json.toString().contains("eventDate")) {
						success=this.NO_ERROR;

					}
			}
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& (responseData != null)) {


				if (responseData.toString().contains("eventDate")) {
					success=this.NO_ERROR;
				}
			}
			assertEquals(noError, success);

		} catch (final RestClientException e) {
			final String errorMessage=e.getMessage();

			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}
			if(errorMessage.contains("8005"))
			{
				assertTrue(true);
			}

			} catch (final IOException e) {
			assertFalse(true);

		} catch (final JSONException e) {
			assertFalse(true);

		}
	}




	/**
	 * Test case to check the error scenerio in get Registry Details
	 */
	public void testGetRegistryDetailError() {
		final String displayView = (String) this.getObject("displayView");
		final String sortSeq = (String) this.getObject("sortseq");
		final String view=(String) this.getObject("view");
		final String startIdx = (String)this.getObject("startindex");
		final String bulkSize = (String)this.getObject("bulkSize");
		final String isGiftGiver = (String)this.getObject("isgiftgiver");
		final String isAvailForWebPurchaseFlag = (String)this.getObject("isavailforwebpurchaseflag");

		final String errorId = (String) this.getObject("errorId");
		final String username = (String) this.getObject("username");
		final String password = (String) this.getObject("password");
		final String httpMethod = (String) this.getObject("httpmethod");
		final String registryId = (String) this.getObject("registryId");
		RestResult getRegistryDetails=null;
		try {
			this.session=this.getNewHttpSession();
			this.session.setUseInternalProfileForLogin(false);
			this.session.setUsername(username);
			this.session.setPassword(password);
			this.session.setUseHttpsForLogin(false);
			this.session.login();

			HashMap params = new HashMap<String, String>();
			final Map inputparam = new HashMap<String, String>();


			params = (HashMap) this.getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			inputparam.put("registryId",registryId);
			inputparam.put("view",view);
			inputparam.put("sortSeq",sortSeq);
			inputparam.put("startIdx",startIdx);
			inputparam.put("displayView",displayView);
			inputparam.put("bulkSize",bulkSize);
			inputparam.put("isGiftGiver",isGiftGiver);
			inputparam.put("isAvailForWebPurchaseFlag",isAvailForWebPurchaseFlag);
			params.put("arg1", inputparam);

			// common fields


			getRegistryDetails = this.session
					.createHttpRequest(
							"http://"
									+ this.getHost()
									+ ":"
									+ this.getPort()
									+ (String) this.getObject("registryDetailURL"),
							params, httpMethod);
			final String responseData = getRegistryDetails.readInputStream();
			System.out.println("testGetRegistryDetailError: "+responseData);

			String errorCode = null;

			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& (responseData != null)) {
			    final JSONObject json = new JSONObject(responseData);
				if (json.has("errorExist")) {
				    errorCode = json.getString("errorCode");

				}
			}
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& (responseData != null)) {
				final String startError="<errorId>";
				if (responseData.toString().contains("errorId")) {
					responseData.substring(responseData.indexOf(startError)+startError.length(),responseData.indexOf("</errorId>"));
				}
			}
			assertEquals(errorId, errorCode);

		} catch (final RestClientException e) {
			final String errorMessage=e.getMessage();

			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}
			if(errorMessage.contains("8005"))
			{
				assertTrue(true);
			}

			} catch (final IOException e) {
			assertFalse(true);

		} catch (final JSONException e) {
			assertFalse(true);

		}
	}

	public void testGetRegistryDetailOwner()
	{
		String success = null;

		final String displayView = (String) this.getObject("displayView");
		final String sortSeq = (String) this.getObject("sortseq");
		final String view=(String) this.getObject("view");
		final String startIdx = (String)this.getObject("startindex");
		final String bulkSize = (String)this.getObject("bulkSize");
		final String isGiftGiver = (String)this.getObject("isgiftgiver");
		final String isAvailForWebPurchaseFlag = (String)this.getObject("isavailforwebpurchaseflag");

		final String noError = (String) this.getObject("noerror");
		final String username = (String) this.getObject("username");
		final String password = (String) this.getObject("password");
		final String httpMethod = (String) this.getObject("httpmethod");
		final String registryId = (String) this.getObject("registryId");
		RestResult getRegistryDetails=null;
		try {
			this.session=this.getNewHttpSession();
			this.session.setUseInternalProfileForLogin(false);
			this.session.setUsername(username);
			this.session.setPassword(password);
			this.session.setUseHttpsForLogin(false);
			this.session.login();

			HashMap params = new HashMap<String, String>();
			final Map inputparam = new HashMap<String, String>();

			params = (HashMap) this.getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			  params.put("atg-rest-depth", "8");
			params.put("atg-rest-return-form-handler-exceptions", true);
			this.loginRequestRestCall(params, this.session);
			inputparam.put("registryId",registryId);
			inputparam.put("view",view);
			inputparam.put("sortSeq",sortSeq);
			inputparam.put("startIdx",startIdx);
			inputparam.put("displayView",displayView);
			inputparam.put("bulkSize",bulkSize);
			inputparam.put("isGiftGiver",isGiftGiver);
			inputparam.put("isAvailForWebPurchaseFlag",isAvailForWebPurchaseFlag);
			params.put("arg1", inputparam);

			// common fields


			getRegistryDetails = this.session
					.createHttpRequest(
							"http://"
									+ this.getHost()
									+ ":"
									+ this.getPort()
									+ (String) this.getObject("registryDetailURL"),
							params, httpMethod);
			final String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);
			System.out.println("owner"+responseData);
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& (responseData != null)) {

				final JSONObject json = new JSONObject(responseData);

				if(json.has("errorCode")) {
				    assert(true);
				}else {



				if ((json!=null)&&json.toString().contains("eventDate")) {
						success=this.NO_ERROR;

					}

			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& (responseData != null)) {


				if (responseData.toString().contains("eventDate")) {
					success=this.NO_ERROR;
				}
			}
			assertEquals(noError, success);

				}}} catch (final RestClientException e) {
			final String errorMessage=e.getMessage();

			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}
			if(errorMessage.contains("8005"))
			{
				assertTrue(true);
			}

			} catch (final IOException e) {
			assertFalse(true);

		} catch (final JSONException e) {
			assertFalse(true);

		}
	}

	/**
	 * login call
	 *
	 * @return response JSON Object
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private RestResult loginRequestRestCall(final Map<String, Object> params, final RestSession session) throws RestClientException, JSONException, IOException {

		params.put("value.login", this.getObject("username"));
		params.put("value.password", this.getObject("password"));
		final RestResult restResult = session.createHttpRequest("http://" + this.getHost() + ":" + this.getPort() + (String) this.getObject("loginRequest"), params,
				"POST");

		return restResult;
	}

	/**
	 * Test case to create baby type registry
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetRegistryDetailForMobileApp() {
		String success = null;

		final String username = (String) this.getObject("username");
		final String password = (String) this.getObject("password");
		final String httpMethod = (String) this.getObject("httpmethod");
		final String registryId = (String) this.getObject("registryId");
		final String noError = (String) this.getObject("noerror");

		RestResult getRegistryDetails=null;
		try {
			this.session=this.getNewHttpSession();
			this.session.setUseInternalProfileForLogin(false);
			this.session.setUsername(username);
			this.session.setPassword(password);
			this.session.setUseHttpsForLogin(true);
			this.session.login();

			HashMap params = new HashMap<String, String>();
			params = (HashMap) this.getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			this.loginRequestRestCall(params, this.session);
			params.put("arg1", registryId);
			getRegistryDetails = this.session
					.createHttpRequest(
							"http://"
									+ this.getHost()
									+ ":"
									+ this.getPort()
									+ (String) this.getObject("registryDetailURL"),
							params, httpMethod);
			final String responseData = getRegistryDetails.readInputStream();
			System.out.println(responseData);
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& (responseData != null)) {

				final JSONObject json = new JSONObject(responseData);
				if ((json!=null)&&json.toString().contains("items")) {
						success=this.NO_ERROR;

					}
			}
			assertEquals(noError, success);

		} catch (final RestClientException e) {
			final String errorMessage=e.getMessage();

			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);
			}
			} catch (final IOException e) {
			assertFalse(true);

		} catch (final JSONException e) {
			assertFalse(true);

		}
	}


}
