package com.bbb.account.order.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

public class TestTrackingInfoDroplet extends BaseTestCase {
	public void testService() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		TrackingInfoDroplet trackingInfoDroplet = (TrackingInfoDroplet) getObject("trackingInfoDroplet");
		trackingInfoDroplet.service(pRequest, pResponse);
		trackingInfoDroplet.getShippingCarriers();
		trackingInfoDroplet.getCatalogTools();
		assertNotNull(pRequest.getParameter("carrierURL"));

	}
}
