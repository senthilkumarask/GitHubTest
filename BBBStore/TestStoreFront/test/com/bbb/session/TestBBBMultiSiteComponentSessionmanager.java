package com.bbb.session;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

import atg.servlet.DynamoHttpServletRequest;

import com.bbb.commerce.checkout.droplet.BBBBillingAddressDroplet;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBMultiSiteComponentSessionmanager extends BaseTestCase {

	public void testBBBMultiSiteComponentSessionmanager() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		BBBMultiSiteComponentSessionmanager manager = new BBBMultiSiteComponentSessionmanager();
		manager.saveSessionData(pRequest);
		manager.setBackingUpSessions(true);
		
		HttpSession httpSession =pRequest.getSession();
		manager.putSession("PName", httpSession);
		manager.removeSession("45678");

	}
}
