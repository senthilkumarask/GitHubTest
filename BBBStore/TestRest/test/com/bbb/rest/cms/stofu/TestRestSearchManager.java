package com.bbb.rest.cms.stofu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestSearchManager extends BaseTestCase {

	public TestRestSearchManager(String name) {
		super(name);
		
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void  testPerformSearchErr() throws IOException{
		RestSession session = getNewHttpSession();

		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("atg-rest-show-rest-paths", false);
		try {

			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					getObject("restSearchAPI"),
					params,  "POST");
			String responseData = pd1.readInputStream();	

			if (responseData != null) {
				assertFalse(true);

			}
		} catch (RestClientException e) {		 
			System.out.println(e.getMessage());
		}         
	}
	public void  testPerformSearchSuccess() throws IOException{
		RestSession session = getNewHttpSession();

		String host = getHost();
		Integer port = getPort();   
		session.setScheme("http"); 
		Map<String, Object> params = new HashMap<String, Object>();
		params = getControleParameters();
		params.put("atg-rest-show-rest-paths", false);
		try {

			RestResult pd1 = session.createHttpRequest("http://" + host + ":" + port+ 
					getObject("restSearchAPI"),
					params,  "POST");
			String responseData = pd1.readInputStream();	

			if (responseData != null) {
				assertFalse(true);

			}
		} catch (RestClientException e) {		 
			System.out.println(e.getMessage());
		}         
	}
}
