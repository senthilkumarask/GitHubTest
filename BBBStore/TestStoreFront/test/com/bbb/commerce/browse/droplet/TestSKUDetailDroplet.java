package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.sapient.common.tests.BaseTestCase;

public class TestSKUDetailDroplet extends BaseTestCase {


	public void testService() throws Exception {
		SKUDetailDroplet skuDetailDroplet = (SKUDetailDroplet) getObject("sKUDetailDroplet");

		String siteId = (String) getObject("siteId");
		String skuId = (String) getObject("skuId");

		getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("skuId", skuId);
		getRequest().setParameter("pSKUDetailVO", null);

		skuDetailDroplet.service(getRequest(), getResponse());
		assertNotNull(getRequest().getParameter("pSKUDetailVO"));

	}
	public void testServiceInvalid()  {
		SKUDetailDroplet skuDetailDroplet = (SKUDetailDroplet) getObject("sKUDetailDroplet");

		String siteId = (String) getObject("siteId");

		getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("skuId", null);
		getRequest().setParameter("pSKUDetailVO", null);

		try {
			skuDetailDroplet.service(getRequest(), getResponse());
		} catch (ServletException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		assertNull(getRequest().getParameter("pSKUDetailVO"));

	}

}
