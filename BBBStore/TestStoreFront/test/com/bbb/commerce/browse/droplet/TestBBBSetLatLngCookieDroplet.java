package com.bbb.commerce.browse.droplet;

import com.sapient.common.tests.BaseTestCase;
import com.bbb.commerce.browse.droplet.BBBSetLatLngCookieDroplet;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TestBBBSetLatLngCookieDroplet extends BaseTestCase {

	public void testLatLngCookie() throws Exception {

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();
		BBBSetLatLngCookieDroplet droplet = (BBBSetLatLngCookieDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/commerce/browse/droplet/BBBSetLatLngCookieDroplet");
		String inputStr = (String)getObject("inputString");
		String inputStr1 = (String)getObject("inputString1");
		req.setParameter("storeIdFromURL",inputStr );
		req.setParameter("siteId",inputStr1);
		droplet.service(req, res);
		assertNotNull(req.getParameter("latLngFromPLP"));
	}
	
}
