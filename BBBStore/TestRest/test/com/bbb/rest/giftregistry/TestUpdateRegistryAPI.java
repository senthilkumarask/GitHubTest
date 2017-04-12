package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestUpdateRegistryAPI extends BaseTestCase {
	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestUpdateRegistryAPI(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * Junit to test update Wedding registry
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateRegistry() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = session.createHttpRequest("http://" + (String) getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}

			//new reg id  183330286  call to update gift registry

			params.put("atg-rest-return-form-handler-exceptions","true");

			String username = (String) getObject("login");
			String password = (String) getObject("password");
			String httpMethod = (String) getObject("httpmethod");
			String eventDate = (String) getObject("eventdate");
			String showerDate = (String) getObject("showerdate");
			String eventType = (String) getObject("eventtype");
			System.out.println("username "+username +" password "+password);
			String guestCount = (String) getObject("guestcount");
			String registryId= (String) getObject("registryId");
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


			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			// common fields
			params.put("registryVO.event.eventDate", eventDate);
			params.put("registryEventType", eventType);
			params.put("sessionBean.registryTypesEvent", eventType);
			params.put("registryVO.event.guestCount", guestCount);
			params.put("registryVO.event.showerDate", showerDate);
			params.put("registryVO.registryId", registryId);
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
			params.put("successURL",(String) getObject("successURL"));
			params.put("errorURL",(String) getObject("errorURL"));
			RestResult updateRegistry = session.createHttpRequest(
					"http://"
							+  (String) getHost()
							+ ":"
							+ getPort()
							+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/updateRegistry",
							params, "POST");
			String responseData = updateRegistry.readInputStream();
			System.out.println("responseData" + responseData);

			JSONObject json = new JSONObject(responseData);
			System.out.println("update registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}



	/**
	 * Junit to test update Wedding registry with form exception
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateRegistryException() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {
			//call login
			session.login();
			params.put("atg-rest-return-form-handler-exceptions", "true");
			params.put("value.login", (String) getObject("login"));
			params.put("value.password", (String) getObject("password"));
			RestResult pd2 = session.createHttpRequest("http://" + (String) getHost() +":" + getPort()+(String) getObject("loginRequest"), params,"POST");
			String responseData2 = pd2.readInputStream();
			if (responseData2 != null) {
				JSONObject json = new JSONObject(responseData2);
				System.out.println("Login Status : "+json.toString());
			}

			//new reg id  183330286  call to update gift registry

			params.put("atg-rest-return-form-handler-exceptions","true");

			String username = (String) getObject("login");
			String password = (String) getObject("password");
			String httpMethod = (String) getObject("httpmethod");
			String eventDate = (String) getObject("eventdate");
			String showerDate = (String) getObject("showerdate");
			String eventType = (String) getObject("eventtype");
			System.out.println("username "+username +" password "+password);
			String guestCount = (String) getObject("guestcount");
			String registryId= (String) getObject("registryId");
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
			//String coFirstName = (String) getObject("cofirstname");
			//String coLastName = (String) getObject("colastname");
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


			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			// common fields
			params.put("registryVO.event.eventDate", eventDate);
			params.put("registryEventType", eventType);
			params.put("sessionBean.registryTypesEvent", eventType);
			params.put("registryVO.event.guestCount", guestCount);
			params.put("registryVO.event.showerDate", showerDate);
			params.put("registryVO.registryId", registryId);
			params.put("registryVO.primaryRegistrant.firstName", regFirstName);
			params.put("registryVO.primaryRegistrant.lastName", regLastName);
			params.put("registryVO.primaryRegistrant.email", regEmail);
			params.put("registryVO.primaryRegistrant.primaryPhone",regPrimaryPhone);
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

			// Co reg info omit last and first name of co registrant to test for form exception
			//params.put("registryVO.coRegistrant.firstName", coFirstName);
			//params.put("registryVO.coRegistrant.lastName", coLastName);
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
			params.put("successURL",(String) getObject("successURL"));
			params.put("errorURL",(String) getObject("errorURL"));
			RestResult updateRegistry = session.createHttpRequest(
					"http://"
							+ (String) getHost()
							+ ":"
							+ getPort()
							+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/updateRegistry",
							params, "POST");
			String responseData = updateRegistry.readInputStream();
			System.out.println("responseData" + responseData);

			JSONObject json = new JSONObject(responseData);

			System.out.println("formhandler formExceptions  "+json.getString("formExceptions"));
			assertNotNull(json.getString("formExceptions")); 
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Junit to test update Wedding registry
	 * @throws RestClientException
	 * @throws IOException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void testUpdateRegistryNonLogin() throws RestClientException, IOException, JSONException{
		session = getNewHttpSession();
		session.setUseHttpsForLogin(false);
		session.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		try {

			session.login();	
			//new reg id  183330286  call to update gift registry

			params.put("atg-rest-return-form-handler-exceptions","true");

	
			String httpMethod = (String) getObject("httpmethod");
			String eventDate = (String) getObject("eventdate");
			String showerDate = (String) getObject("showerdate");
			String eventType = (String) getObject("eventtype");

			String guestCount = (String) getObject("guestcount");
			String registryId= (String) getObject("registryId");
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


			params.put("atg-rest-http-method", httpMethod);
			params.put("atg-rest-return-form-handler-exceptions", true);
			params.put("atg-rest-return-form-handler-properties",true);
			// common fields
			params.put("registryVO.event.eventDate", eventDate);
			params.put("registryEventType", eventType);
			params.put("sessionBean.registryTypesEvent", eventType);
			params.put("registryVO.event.guestCount", guestCount);
			params.put("registryVO.event.showerDate", showerDate);
			params.put("registryVO.registryId", registryId);
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

			RestResult updateRegistry = session.createHttpRequest(
					"http://"
							+  (String) getObject("host")
							+ ":"
							+ getPort()
							+ "/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/updateRegistry",
							params, "POST");
			String responseData = updateRegistry.readInputStream();
			System.out.println("responseData" + responseData);

			JSONObject json = new JSONObject(responseData);
			System.out.println("update registry json ="+json.toString());
			System.out.println("formhandler return  "+json.getString("result"));
			assertNotNull(json.getString("result")); 

		}catch (RestClientException e) {
			String errorMessage=e.getMessage();
			System.out.println(errorMessage);
			if(errorMessage.contains("UNAUTHORIZED ACCESS:Profile not logged in")){
				assertTrue(true);				
			}else{
				assertFalse(true);
			}	
		}
		finally {
			if(pd != null)
				pd = null;
			try {
				session.logout();
			} catch (RestClientException e) {
				e.printStackTrace();
			}
		}

	}


}
