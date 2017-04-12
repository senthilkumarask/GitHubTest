package com.bbb.commerce.giftregistry.droplet;

import com.sapient.common.tests.BaseTestCase;

public class TestPOBoxValidateDroplet extends BaseTestCase {
	
	public void testService() throws Exception {
		POBoxValidateDroplet poBoxValidateDroplet = (POBoxValidateDroplet) getObject("TestPOBoxValidateDroplet");
		String address = "PO BOX 1234";
		getRequest().setParameter("address", address);
		poBoxValidateDroplet.service(getRequest(), getResponse());
		boolean isValid = (Boolean)getRequest().getObjectParameter("isValid");
		assertEquals(true, isValid);
	}
}
