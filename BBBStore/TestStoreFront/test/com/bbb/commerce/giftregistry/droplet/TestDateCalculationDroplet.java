package com.bbb.commerce.giftregistry.droplet;

import java.util.Locale;

import atg.servlet.ServletUtil;

import com.sapient.common.tests.BaseTestCase;

public class TestDateCalculationDroplet extends BaseTestCase {

	@SuppressWarnings("static-access")
	public void testServiceFutureDate() throws Exception {
		DateCalculationDroplet dateCalculationDroplet = (DateCalculationDroplet) getObject("TestDateCalculationDroplet");

		getRequest().setParameter("eventDate", "12/12/2012");
		getRequest().setParameter("convertDateFlag", "true");
		getRequest().getLocale().setDefault(Locale.US);
		ServletUtil.setCurrentRequest(getRequest());
		dateCalculationDroplet.service(getRequest(), getResponse());
		addObjectToAssert("testValue1", getRequest().getParameter("check"));
	}
	@SuppressWarnings("static-access")
	public void testServicePastDate() throws Exception {
		DateCalculationDroplet dateCalculationDroplet = (DateCalculationDroplet) getObject("TestDateCalculationDroplet");

		getRequest().setParameter("eventDate", "12/12/2015");
		getRequest().setParameter("convertDateFlag", "true");
		getRequest().getLocale().setDefault(Locale.US);
		ServletUtil.setCurrentRequest(getRequest());
		dateCalculationDroplet.service(getRequest(), getResponse());
		addObjectToAssert("testValue1", getRequest().getParameter("check"));
	}
}
