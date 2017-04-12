package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestBBBGSEmailSender extends BaseTestCase {

	public TestBBBGSEmailSender(String name) {
		super(name);

	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

	}

	public void testSendFeedbackEmail() throws JSONException, IOException,
			RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		String recipientList = (String) getObject("recipientList");
		String message = (String) getObject("message");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendFeedbackEmail"), params,
				new String[] { recipientList, message }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendFeedbackEmailRecipientErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		String recipientList = (String) getObject("recipientList");
		String message = (String) getObject("message");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendFeedbackEmail"), params,
					new String[] { recipientList, message }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2007")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendFeedbackEmailMessageErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		String recipientList = (String) getObject("recipientList");
		String message = (String) getObject("message");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendFeedbackEmail"), params,
					new String[] { recipientList, message }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2003")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendPDPEmail() throws JSONException, IOException,
			RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String productId = (String) getObject("productId");
		String productAvailabilityFlag = (String) getObject("productAvailabilityFlag");
		String skuId = (String) getObject("skuId");
		String recipientList = (String) getObject("recipientList");
		String reviewRating = (String) getObject("reviewRating");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendPDPEmail"), params,
				new String[] { productId, productAvailabilityFlag, skuId,
						recipientList, reviewRating }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendPDPEmailRecipientErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String productId = (String) getObject("productId");
		String productAvailabilityFlag = (String) getObject("productAvailabilityFlag");
		String skuId = (String) getObject("skuId");
		String recipientList = (String) getObject("recipientList");
		String reviewRating = (String) getObject("reviewRating");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendPDPEmail"), params,
					new String[] { productId, productAvailabilityFlag, skuId,
							recipientList, reviewRating }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2007")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendPDPEmailProductIdErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String productId = (String) getObject("productId");
		String productAvailabilityFlag = (String) getObject("productAvailabilityFlag");
		String skuId = (String) getObject("skuId");
		String recipientList = (String) getObject("recipientList");
		String reviewRating = (String) getObject("reviewRating");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendPDPEmail"), params,
					new String[] { productId, productAvailabilityFlag, skuId,
							recipientList, reviewRating }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2001")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableRegistryCartEmailForTable() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendTableRegistryCartEmail"),
				params, new String[] { jsonResultString, recipientList,
						emailType }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendTableRegistryCartEmailForRegistry()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendTableRegistryCartEmail"),
				params, new String[] { jsonResultString, recipientList,
						emailType }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendTableRegistryCartEmailForCartFF1()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendTableRegistryCartEmail"),
				params, new String[] { jsonResultString, recipientList,
						emailType }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendTableRegistryCartEmailForCartFF2()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendTableRegistryCartEmail"),
				params, new String[] { jsonResultString, recipientList,
						emailType }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendTableRegistryCartEmailRecipientErr()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableRegistryCartEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2007")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableRegistryCartEmailEmailTypeErr()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableRegistryCartEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2002")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableRegistryCartEmailJsonStringErr()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableRegistryCartEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2008")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableRegistryCartEmailJsonStringKeyErr()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("emailType");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableRegistryCartEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2009")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableCheckListEmail() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("tableName");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendTableCheckListEmail"), params,
				new String[] { jsonResultString, recipientList, emailType },
				"POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendTableCheckListEmailRecipientErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("tableName");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableCheckListEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2007")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableCheckListEmailJsonStringErr()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("tableName");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableCheckListEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2008")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableCheckListEmailTableNameErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("tableName");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableCheckListEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2010")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendTableCheckListEmailJsonStringKeyErr()
			throws JSONException, IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		String emailType = (String) getObject("tableName");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendTableCheckListEmail"),
					params, new String[] { jsonResultString, recipientList,
							emailType }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2009")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendCompareEmailForFF1() throws JSONException, IOException,
			RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendCompareEmail"), params,
				new String[] { jsonResultString, recipientList }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendCompareEmailForFF2() throws JSONException, IOException,
			RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		RestResult pd1 = session.createHttpRequest("http://" + host + ":"
				+ port + (String) getObject("sendCompareEmail"), params,
				new String[] { jsonResultString, recipientList }, "POST");
		String responseData = pd1.readInputStream();
		assertNotNull(responseData);
		System.out.println(responseData);
		if (responseData != null) {
			JSONObject json = new JSONObject(responseData);
			Boolean result = (Boolean) json.get("atgResponse");
			assertTrue(result);
		}

	}

	public void testSendCompareEmailRecipientErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendCompareEmail"), params,
					new String[] { jsonResultString, recipientList }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2007")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendCompareEmailJsonStringErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendCompareEmail"), params,
					new String[] { jsonResultString, recipientList }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2008")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

	public void testSendCompareEmailJsonStringKeyErr() throws JSONException,
			IOException, RestClientException {

		RestSession session = getNewHttpSession();
		String host = getHost();
		Integer port = getPort();
		session.setScheme("http");
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("X-bbb-site-id", (String) getObject("site-id"));
		params.put("X-bbb-store", (String) getObject("store-id"));
		params.put("X-bbb-channel", (String) getObject("channel"));
		String jsonResultString = (String) getObject("jsonResultString");
		String recipientList = (String) getObject("recipientList");
		try {
			RestResult pd1 = session.createHttpRequest("http://" + host + ":"
					+ port + (String) getObject("sendCompareEmail"), params,
					new String[] { jsonResultString, recipientList }, "POST");
			String responseData = pd1.readInputStream();
			assertNotNull(responseData);
			System.out.println(responseData);
			assertFalse(true);
		} catch (RestClientException e) {
			String errorMessage = e.getMessage();
			if (errorMessage.contains("2009")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}

	}

}
