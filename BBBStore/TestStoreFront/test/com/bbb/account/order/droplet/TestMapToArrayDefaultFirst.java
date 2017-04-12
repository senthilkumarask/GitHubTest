package com.bbb.account.order.droplet;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.util.MapToArrayDefaultFirst;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

public class TestMapToArrayDefaultFirst extends BaseTestCase {
	public void testService() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBMapToArrayDefaultFirst mapToArrayDefaultFirst = (BBBMapToArrayDefaultFirst) getObject("mapToArrayDefaultFirst");

		String defaultId2 = (String) getObject("defaultId2");
		String defaultId = (String) getObject("defaultId");

		pRequest.setParameter("defaultId2", defaultId2);
		pRequest.setParameter("defaultId", defaultId);

		Map map = new HashMap<String, String>();
		map.put(101, "Rajesh");
		map.put(102, "Kumar");
		map.put(103, "Saini");
		
		pRequest.setParameter("map", map);
		mapToArrayDefaultFirst.service(pRequest, pResponse);
		pRequest.setParameter("map", map);
		mapToArrayDefaultFirst.getSortedArray(new HashMap<String, String>(), defaultId, defaultId2, false, pRequest);

	}
}
