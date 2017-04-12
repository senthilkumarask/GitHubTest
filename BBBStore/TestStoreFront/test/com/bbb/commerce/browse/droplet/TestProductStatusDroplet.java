package com.bbb.commerce.browse.droplet;



import java.io.IOException;

import javax.servlet.ServletException;

import com.sapient.common.tests.BaseTestCase;

public class TestProductStatusDroplet extends BaseTestCase {

	public void testProductStatusDropletActiveProduct() throws  ServletException, IOException {
		ProductStatusDroplet productStatusDroplet = (ProductStatusDroplet) getObject("productStatusDroplet");	
		getRequest().setParameter("productId","prod60012");
		productStatusDroplet.service(getRequest(), getResponse());
		Boolean isProductActive = (Boolean)getRequest().getObjectParameter("isProductActive");
		assertNotNull(isProductActive);
		assertTrue(isProductActive);
	}
	
	public void testProductStatusDropletInActiveProduct() throws  ServletException, IOException {
		ProductStatusDroplet productStatusDroplet = (ProductStatusDroplet) getObject("productStatusDroplet");	
		getRequest().setParameter("productId","prod60026");
		productStatusDroplet.service(getRequest(), getResponse());
		Boolean isProductActive = (Boolean)getRequest().getObjectParameter("isProductActive");
		assertNotNull(isProductActive);
		assertFalse(isProductActive);
	}
}
