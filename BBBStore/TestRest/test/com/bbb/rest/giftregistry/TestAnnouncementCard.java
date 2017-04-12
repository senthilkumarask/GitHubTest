package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;
/**
 * Test cases for Gift Card Balance API
 * @author njai13
 *
 */
public class TestAnnouncementCard extends BaseTestCase {

	RestSession session;
	@SuppressWarnings("rawtypes")
	HashMap params = null;
	/**
	 * @param name
	 */
	public TestAnnouncementCard(String name) {
		super(name);

	}
	@SuppressWarnings("rawtypes")
	protected void setUp() {
		params = (HashMap) getControleParameters();
	}

	/**
	 * test Announcement Card API
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws RestClientException 
	 */

	@SuppressWarnings("unchecked")
	public void testAnnouncementCards() throws IOException, JSONException, RestClientException{
		session = getNewHttpSession();    
		//Login and establish a session and use that session to all call from same user.
		session.setUseInternalProfileForLogin(false);
		session.setUsername("admin");
		session.setPassword("admin");
		session.setUseHttpsForLogin(false);
		session.login();

		params.put("giftRegSessionBean.registryVO.registryId","153501902");
		params.put("giftRegSessionBean.registryVO.numRegAnnouncementCards","75");
		params.put("errorURL","atg-rest-ignore-redirect");
		params.put("successURL","atg-rest-ignore-redirect");
		params.put("atg-rest-return-form-handler-properties", "true");
		//set params required to make request
		//params.put("atg-rest-return-form-handler-exceptions", (Boolean) getObject("atg-rest-return-form-handler-exceptions"));
		params.put("atg-rest-depth", (String) getObject("atg-rest-depth"));

		//call Announcement Card handler

		RestResult pd2 = session.createHttpRequest("http://" + getHost() +":" + getPort()+"/rest/bean/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler/announcementCardCount", params,"POST");

		//get response
		String response=pd2.readInputStream();
		assertNotNull(response);
		System.out.println(response  );

		//	parse response
		JSONObject json = new JSONObject(response);
		String result=json.getString("result");
		System.out.println("Announcement Card result="+result);
		assertNotNull(result);
	}

}
