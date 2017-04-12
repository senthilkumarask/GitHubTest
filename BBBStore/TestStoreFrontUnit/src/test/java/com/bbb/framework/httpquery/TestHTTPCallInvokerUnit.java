package com.bbb.framework.httpquery;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.bean.LockAPIBean;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BaseTestCase;

@RunWith(PowerMockRunner.class) 
@PowerMockIgnore({"javax.net.ssl.*", "javax.management.*", "javax.security.auth.*"})
public class TestHTTPCallInvokerUnit extends BaseTestCase {


	@Mock HttpClient mockHttpClient;
	@Mock BBBCatalogTools bbbCatalogTools;
	@Spy HTTPCallInvoker httpCallInvoker = new HTTPCallInvoker();
	@Spy List<String> list = new ArrayList<String>();
	@Spy LockAPIBean viewbean = new LockAPIBean();
	@Mock HttpResponse httpResponse;
	@Test
	public void testInvokeforPUT() throws Exception {
				
		
		viewbean.setStatus("success");
		
		list.add("test");
		String httpType = "PUT";
		HashMap<String, String> headerParam = new HashMap<String, String>();
		headerParam.put("X-ClientID", "4146a237e41426d4c98330813db16e42804f68cf8f51fb4e");
		headerParam.put("X-ApiKey", "to7ZarcbE5kzw0KATjhtF6NoINifI5TG");
		
		HashMap<String, String> param = new HashMap<String, String>();
		
		param.put("status", "locked");
		String url="http://api.dev.bbby.katori.com/api/customizations/5544d94371951262268b4568/lock";
		
				
		ArgumentCaptor<HttpUriRequest> argCapForUriRequest = ArgumentCaptor.forClass(HttpUriRequest.class);
		ArgumentCaptor<Class> argCapForClass = ArgumentCaptor.forClass(Class.class);
		doReturn(bbbCatalogTools).when(httpCallInvoker).getCatalogTools();
		when(bbbCatalogTools.getAllValuesForKey(anyString(),anyString())).thenReturn(list);
		when(bbbCatalogTools.getAllValuesForKey(anyString(),anyString()).get(0)).thenReturn(null);
		httpCallInvoker.setDefaultConnectionTimeout(5000);
		httpCallInvoker.setDefaultSocketTimeout(5000);
		httpCallInvoker.setDefaultMaxTotalConnections(10);
		httpCallInvoker.setDefaultMaxHostConnectionsPerHost(50);
		httpCallInvoker.invokeToGetJson(LockAPIBean.class , headerParam, url, param, httpType);
		
		verify(httpCallInvoker).invokeToGetJson(argCapForClass.capture(), argCapForUriRequest.capture());
		
		assertEquals("4146a237e41426d4c98330813db16e42804f68cf8f51fb4e", argCapForUriRequest.getValue().getHeaders("X-ClientID")[0].getValue());
		assertEquals("to7ZarcbE5kzw0KATjhtF6NoINifI5TG", argCapForUriRequest.getValue().getHeaders("X-ApiKey")[0].getValue());
		assertEquals("PUT", argCapForUriRequest.getValue().getMethod());
		
		
	}
	
	
	
	public void testInvokeforGet() throws Exception{
		
		String url="http://api.dev.bbby.katori.com/api/customizations/531107cbfc3807403eeee6a4/summary";
		list.add("test");
		HashMap<String, String> headerParam = new HashMap<String, String>();
		headerParam.put("X-ClientID", "4146a237e41426d4c98330813db16e42804f68cf8f51fb4e");
		headerParam.put("X-ApiKey", "to7ZarcbE5kzw0KATjhtF6NoINifI5TG");
		HashMap<String, String> param = new HashMap<String, String>();
			
		String httpType = "GET";
		ArgumentCaptor<HttpUriRequest> argCapForUriRequest = ArgumentCaptor.forClass(HttpUriRequest.class);
		ArgumentCaptor<Class> argCapForClass = ArgumentCaptor.forClass(Class.class);
		doReturn(bbbCatalogTools).when(httpCallInvoker).getCatalogTools();
		when(bbbCatalogTools.getAllValuesForKey(anyString(),anyString())).thenReturn(list);
		when(bbbCatalogTools.getAllValuesForKey(anyString(),anyString()).get(0)).thenReturn(null);
		httpCallInvoker.setDefaultConnectionTimeout(5000);
		httpCallInvoker.setDefaultSocketTimeout(5000);
		httpCallInvoker.setDefaultMaxTotalConnections(50);
		httpCallInvoker.setDefaultMaxHostConnectionsPerHost(50);
		httpCallInvoker.invokeToGetJson(LockAPIBean.class , headerParam, url, param, httpType);
		
		verify(httpCallInvoker).invokeToGetJson(argCapForClass.capture(), argCapForUriRequest.capture());
		
		assertEquals("GET", argCapForUriRequest.getValue().getMethod());
		
		
	}
	
	@Test
	public void testInvokePostSuccess() throws BBBSystemException,
			BBBBusinessException, ClientProtocolException, IOException {
		final String targetURL = "https://sandbox.borderfree.com/checkout/checkoutAPI-v2.srv";
		final Map<String, String> headerParams = new HashMap<String, String>();
		headerParams.put(BBBCoreConstants.HTTP_INVKR_AUTH,
				"Basic YmJiX2FwaV9zdGc6K09TVGdleFQ=");
		headerParams.put(BBBCoreConstants.HTTP_INVKR_CNT_TYPE, "text/xml");
		final String requestXml = readRequestXML();
		list.add(null);
		httpCallInvoker.setDefaultConnectionTimeout(5000);
		httpCallInvoker.setDefaultSocketTimeout(5000);
		httpCallInvoker.setDefaultMaxTotalConnections(50);
		httpCallInvoker.setDefaultMaxHostConnectionsPerHost(50);

		doReturn(bbbCatalogTools).when(httpCallInvoker).getCatalogTools();
		when(bbbCatalogTools.getAllValuesForKey(anyString(), anyString()))
				.thenReturn(list);
		doReturn(httpResponse).when(mockHttpClient).execute(
				(HttpPost) anyObject());

		String responseXML = httpCallInvoker.invokeIntlCartSubmission(targetURL, requestXml,
				headerParams);
		System.out.println(responseXML);
		assertTrue(responseXML.contains("<?xml"));
		assertTrue(responseXML.contains("<envoyInitialParams>"));
	}

	@Test
	public void testInvokePostError() throws BBBSystemException,
			BBBBusinessException, ClientProtocolException, IOException {
		final String targetURL = "https://sandbox.borderfree.com/checkout/checkoutAPI-v2.srv";
		final Map<String, String> headerParams = new HashMap<String, String>();
		headerParams.put(BBBCoreConstants.HTTP_INVKR_AUTH,
				"Basic YmJiX2FwaV9zdGc6K09TVGdleFQ=");
		headerParams.put(BBBCoreConstants.HTTP_INVKR_CNT_TYPE, "text/xml");
		final String requestXml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?> <message> </message>";
		list.add(null);
		httpCallInvoker.setDefaultConnectionTimeout(5000);
		httpCallInvoker.setDefaultSocketTimeout(5000);
		httpCallInvoker.setDefaultMaxTotalConnections(50);
		httpCallInvoker.setDefaultMaxHostConnectionsPerHost(50);

		doReturn(bbbCatalogTools).when(httpCallInvoker).getCatalogTools();
		when(bbbCatalogTools.getAllValuesForKey(anyString(), anyString()))
				.thenReturn(list);
		doReturn(httpResponse).when(mockHttpClient).execute(
				(HttpPost) anyObject());

		String responseXML = httpCallInvoker.invokeIntlCartSubmission(targetURL, requestXml,
				headerParams);
		assertTrue(responseXML.contains("<?xml"));
		assertTrue(responseXML.contains("<errors>"));
	}

	private String readRequestXML() {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"src/test/resources/com/bbb/test/data/envoy.xml"));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		char[] buf = new char[5076];
		int numRead = 0;
		try {
			while ((numRead = reader.read(buf)) != -1) {

				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[5076];
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		try {
			reader.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return fileData.toString();
	}
}
