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

public class TestCreateWeddingRegistryClient extends BaseTestCase {
	RestSession session=null;
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		session.logout();
		super.tearDown();

	}

	public TestCreateWeddingRegistryClient(String name) {
		super(name);
	}
	
	/**
	 * Test case to create Wedding type registry
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */
	public void testCreateWeddingReg() throws IOException, JSONException, RestClientException {
		String exception = null;
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		String eventDate = (String) getObject("eventdate");
		String showerDate = (String) getObject("showerdate");
		String eventType = (String) getObject("eventtype");

		String guestCount = (String) getObject("guestcount");

		String regFirstName = (String) getObject("regfirstname");
		String regLastName = (String) getObject("reglastname");
		String regEmail = (String) getObject("regemail");
		String regPrimaryPhone = (String) getObject("regeprimaryphone");
		String regCellPhone = (String) getObject("regcellphone");

		// primary Reg add info
		String regAddFlag = (String) getObject("regaddflag");
		String regAddressLine1 = (String) getObject("regaddressLine1");
		String regAddressLine2 = (String) getObject("regaddressLine2");
		String regCity = (String) getObject("regcity");
		String regState = (String) getObject("regstate");
		String regzip = (String) getObject("regzip");

		// CO Reg info (required only in some cases)
		String coEmailFound = (String) getObject("coemailfound");
		String coFirstName = (String) getObject("cofirstname");
		String coLastName = (String) getObject("colastname");
		String coEmail = (String) getObject("coemail");

		// <!-- Shp add info -->
		String shpAddFlag = (String) getObject("shpaddflag");
		String optinWeddingOrBump = (String) getObject("optinweddingorbump");
		String shpFirstName = (String) getObject("shpfirstname");
		String shpLastName = (String) getObject("shplastname");
		String shpAddressLine1 = (String) getObject("shpaddressline1");
		String shpAddressLine2 = (String) getObject("shpaddressline2");
		String shpCity = (String) getObject("shpcity");
		String shpState = (String) getObject("shpstate");
		String shpZip = (String) getObject("shpzip");

		// future Shp add info

		String futureShpAdded = (String) getObject("futureshpadded");
		String fuShpAddFlag = (String) getObject("fushpaddflag");
		String fuShpFirstName = (String) getObject("fushpaddfirstname");
		String fuShpLastName = (String) getObject("fushpaddlastname");
		String fuShpAddressLine1 = (String) getObject("fushpaddressline1");
		String fuShpAddressLine2 = (String) getObject("fushpaddressline2");
		String fuShpCity = (String) getObject("fushpcity");
		String fuShpState = (String) getObject("fushpstate");
		String fuShpDate = (String) getObject("fushpdate");
		String fuShpZip = (String) getObject("fushpzip");

		String refStoreContactMethod = (String) getObject("refstorecontactmethod");
		String prefStoreNum = (String) getObject("prefstorenum");

		
			session= getNewHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.login();
			
			HashMap params = new HashMap<String, String>();
			params = (HashMap) getControleParameters();
			
			JSONObject json = loginRequestRestCall(params, session);
			System.out.println("json" +  json);
			
			params = (HashMap) getControleParameters();
			
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			// common fields
			params.put("registryVO.event.eventDate", eventDate);
			params.put("registryEventType", eventType);

			params.put("registryVO.event.guestCount", guestCount);
			params.put("registryVO.event.showerDate", showerDate);

			params.put("registryVO.primaryRegistrant.firstName", regFirstName);
			params.put("registryVO.primaryRegistrant.lastName", regLastName);
			params.put("registryVO.primaryRegistrant.email", regEmail);
			params.put("registryVO.primaryRegistrant.primaryPhone",
					regPrimaryPhone);
			params.put("registryVO.primaryRegistrant.cellPhone", regCellPhone);
			// params.put("registryVO.primaryRegistrant.babyMaidenName",regMaidenName);

			// primary Reg info

			params.put("regContactAddress", regAddFlag);
			params.put(
					"registryVO.primaryRegistrant.contactAddress.addressLine1",
					regAddressLine1);
			params.put(
					"registryVO.primaryRegistrant.contactAddress.addressLine2",
					regAddressLine2);
			params.put("registryVO.primaryRegistrant.contactAddress.city",
					regCity);
			params.put("registryVO.primaryRegistrant.contactAddress.state",
					regState);
			params.put("registryVO.primaryRegistrant.contactAddress.zip",
					regzip);

			// Co reg info
			params.put("registryVO.coRegistrant.firstName", coFirstName);
			params.put("registryVO.coRegistrant.lastName", coLastName);
			params.put("registryVO.coRegistrant.email", coEmail);
			params.put("coRegEmailFoundPopupStatus", coEmailFound);
			// Shipping info

			params.put("shippingAddress", shpAddFlag);
			params.put("registryVO.shipping.shippingAddress.firstName",
					shpFirstName);
			params.put("registryVO.shipping.shippingAddress.lastName",
					shpLastName);
			params.put("registryVO.shipping.shippingAddress.addressLine1",
					shpAddressLine1);
			params.put("registryVO.shipping.shippingAddress.addressLine2",
					shpAddressLine2);
			params.put("registryVO.shipping.shippingAddress.city", shpCity);
			params.put("registryVO.shipping.shippingAddress.state", shpState);
			params.put("registryVO.shipping.shippingAddress.zip", shpZip);
			params.put("registryVO.optInWeddingOrBump", optinWeddingOrBump);

			// Future Shipping address start

			params.put("futureShippingDateSelected", futureShpAdded);
			params.put("futureShippingAddress", fuShpAddFlag);
			params.put("registryVO.shipping.futureshippingAddress.firstName",
					fuShpFirstName);
			params.put("registryVO.shipping.futureShippingDate", fuShpDate);
			params.put("registryVO.shipping.futureshippingAddress.lastName",
					fuShpLastName);
			params.put(
					"registryVO.shipping.futureshippingAddress.addressLine1",
					fuShpAddressLine1);
			params.put(
					"registryVO.shipping.futureshippingAddress.addressLine2",
					fuShpAddressLine2);
			params.put("registryVO.shipping.futureshippingAddress.city",
					fuShpCity);
			params.put("registryVO.shipping.futureshippingAddress.state",
					fuShpState);
			params.put("registryVO.shipping.futureshippingAddress.zip",
					fuShpZip);
			params.put("registryVO.refStoreContactMethod",
					refStoreContactMethod);
			params.put("registryVO.prefStoreNum", prefStoreNum);
			
			RestResult createWeddingRegistryRes = session
					.createHttpRequest(
							"http://"
									+ getHost()
									+ ":"
									+ getPort()
									+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/createRegistry",
							params, "POST");
			String responseData = createWeddingRegistryRes.readInputStream();
			System.out.println("responseData" + responseData);
			
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& responseData != null) {

				JSONObject json1 = new JSONObject(responseData);
				if (json.has("formExceptions")) {
					exception = (String) json.getString("formExceptions");
				}
			}
			

			assertNull(exception);

	}
	/**
	 * Test case to check the error scenerio in Wedding type registry
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public void testCreateWeddingRegError() throws IOException, JSONException {
		String exception = null;
		String username = (String) getObject("username");
		String password = (String) getObject("password");
		String httpMethod = (String) getObject("httpmethod");
		String eventDate = (String) getObject("eventdate");
		String showerDate = (String) getObject("showerdate");
		String eventType = (String) getObject("eventtype");

		String guestCount = (String) getObject("guestcount");
		String regFirstName = (String) getObject("regfirstname");
		String regLastName = (String) getObject("reglastname");
		String regEmail = (String) getObject("regemail");
		String regPrimaryPhone = (String) getObject("regeprimaryphone");
		String regCellPhone = (String) getObject("regcellphone");

		// primary Reg add info
		String regAddFlag = (String) getObject("regaddflag");
		String regAddressLine1 = (String) getObject("regaddressLine1");
		String regAddressLine2 = (String) getObject("regaddressLine2");
		String regCity = (String) getObject("regcity");
		String regState = (String) getObject("regstate");
		String regzip = (String) getObject("regzip");

		// CO Reg info (required only in some cases)
		String coEmailFound = (String) getObject("coemailfound");
		String coFirstName = (String) getObject("cofirstname");
		String coLastName = (String) getObject("colastname");
		String coEmail = (String) getObject("coemail");

		// <!-- Shp add info -->
		String shpAddFlag = (String) getObject("shpaddflag");
		String optinWeddingOrBump = (String) getObject("optinweddingorbump");
		String shpFirstName = (String) getObject("shpfirstname");
		String shpLastName = (String) getObject("shplastname");
		String shpAddressLine1 = (String) getObject("shpaddressline1");
		String shpAddressLine2 = (String) getObject("shpaddressline2");
		String shpCity = (String) getObject("shpcity");
		String shpState = (String) getObject("shpstate");
		String shpZip = (String) getObject("shpzip");

		// future Shp add info

		String futureShpAdded = (String) getObject("futureshpadded");
		String fuShpAddFlag = (String) getObject("fushpaddflag");
		String fuShpFirstName = (String) getObject("fushpaddfirstname");
		String fuShpLastName = (String) getObject("fushpaddlastname");
		String fuShpAddressLine1 = (String) getObject("fushpaddressline1");
		String fuShpAddressLine2 = (String) getObject("fushpaddressline2");
		String fuShpCity = (String) getObject("fushpcity");
		String fuShpState = (String) getObject("fushpstate");
		String fuShpDate = (String) getObject("fushpdate");
		String fuShpZip = (String) getObject("fushpzip");

		String refStoreContactMethod = (String) getObject("refstorecontactmethod");
		String prefStoreNum = (String) getObject("prefstorenum");

		try {
			session=getHttpSession();
			session.setUseInternalProfileForLogin(false);
			session.setUsername(username);
			session.setPassword(password);
			session.setUseHttpsForLogin(false);
			session.setHost((String) getObject("host"));
			session.login();
			
			HashMap params = new HashMap<String, String>();
			params = (HashMap) getControleParameters();
			
			loginRequestRestCall(params, session);
			
			params = new HashMap<String, String>();
			params = (HashMap) getControleParameters();
			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);

			// common fields
			params.put("registryVO.event.eventDate", eventDate);
			params.put("registryEventType", eventType);

			params.put("registryVO.event.guestCount", guestCount);
			params.put("registryVO.event.showerDate", showerDate);

			// baby fields
			params.put("registryVO.registryType.registryTypeName", eventType);
			params.put("registryVO.primaryRegistrant.firstName", regFirstName);
			params.put("registryVO.primaryRegistrant.lastName", regLastName);
			params.put("registryVO.primaryRegistrant.email", regEmail);
			params.put("registryVO.primaryRegistrant.primaryPhone",
					regPrimaryPhone);
			params.put("registryVO.primaryRegistrant.cellPhone", regCellPhone);

			// primary Reg info

			params.put("regContactAddress", regAddFlag);
			params.put(
					"registryVO.primaryRegistrant.contactAddress.addressLine1",
					regAddressLine1);
			params.put(
					"registryVO.primaryRegistrant.contactAddress.addressLine2",
					regAddressLine2);
			params.put("registryVO.primaryRegistrant.contactAddress.city",
					regCity);
			params.put("registryVO.primaryRegistrant.contactAddress.state",
					regState);
			params.put("registryVO.primaryRegistrant.contactAddress.zip",
					regzip);

			// Co reg info

			params.put("registryVO.coRegistrant.firstName", coFirstName);
			params.put("registryVO.coRegistrant.lastName", coLastName);
			params.put("registryVO.coRegistrant.email", coEmail);
			params.put("coRegEmailFoundPopupStatus", coEmailFound);

			// Shipping info

			params.put("shippingAddress", shpAddFlag);
			params.put("registryVO.shipping.shippingAddress.firstName",
					shpFirstName);
			params.put("registryVO.shipping.shippingAddress.lastName",
					shpLastName);
			params.put("registryVO.shipping.shippingAddress.addressLine1",
					shpAddressLine1);
			params.put("registryVO.shipping.shippingAddress.addressLine2",
					shpAddressLine2);
			params.put("registryVO.shipping.shippingAddress.city", shpCity);
			params.put("registryVO.shipping.shippingAddress.state", shpState);
			params.put("registryVO.shipping.shippingAddress.zip", shpZip);
			params.put("registryVO.optInWeddingOrBump", optinWeddingOrBump);

			// Future Shipping address start

			params.put("futureShippingDateSelected", futureShpAdded);
			params.put("futureShippingAddress", fuShpAddFlag);
			params.put("registryVO.shipping.futureshippingAddress.firstName",
					fuShpFirstName);
			params.put("registryVO.shipping.futureShippingDate", fuShpDate);
			params.put("registryVO.shipping.futureshippingAddress.lastName",
					fuShpLastName);
			params.put(
					"registryVO.shipping.futureshippingAddress.addressLine1",
					fuShpAddressLine1);
			params.put(
					"registryVO.shipping.futureshippingAddress.addressLine2",
					fuShpAddressLine2);
			params.put("registryVO.shipping.futureshippingAddress.city",
					fuShpCity);
			params.put("registryVO.shipping.futureshippingAddress.state",
					fuShpState);
			params.put("registryVO.shipping.futureshippingAddress.zip",
					fuShpZip);
			params.put("registryVO.refStoreContactMethod",
					refStoreContactMethod);
			params.put("registryVO.prefStoreNum", prefStoreNum);

			RestResult createWeddingRegistryRes = session
					.createHttpRequest(
							"http://"
									+ (String) getObject("host")
									+ ":"
									+ getPort()
									+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/createRegistry",
							params, "POST");
			String responseData = createWeddingRegistryRes.readInputStream();
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("json")
					&& responseData != null) {

				JSONObject json = new JSONObject(responseData);
				if (json.has("formExceptions")) {
					exception = (String) json.getString("formExceptions");
				}
			}
			if (params.get("atg-rest-output").toString()
					.equalsIgnoreCase("xml")
					&& responseData != null) {
				if (responseData.toString().contains("formExceptions")) {
					exception = responseData.toString();
				}
			}

			assertNotNull(exception);

		} catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println("errorMessage:" + errorMessage);
			if(errorMessage.contains("409")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}		 
			

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
	private JSONObject loginRequestRestCall(Map<String, Object> params, RestSession session) throws RestClientException, JSONException, IOException {

		params.put("value.login", (String) getObject("profileLogin"));
		params.put("value.password", (String) getObject("profilePwd"));
		RestResult RestResult = session.createHttpRequest("http://" + getHost() + ":" + getPort() + (String) getObject("loginRequest"), params,
				"POST");

		return new JSONObject(RestResult.readInputStream());
	}
	
}
