package com.bbb.selfservice.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.droplet.ListPriceSalePriceDroplet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestListPriceSalePriceDroplet extends BaseTestCase {

	public void testService() throws BBBBusinessException,
			BBBSystemException, ServletException, IOException {
	
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		pRequest.setParameter("productId", (String) getObject("productId"));
		pRequest.setParameter("skuId", (String) getObject("skuId"));

		ListPriceSalePriceDroplet droplet = (ListPriceSalePriceDroplet) getObject("ListPriceSalePriceDroplet");
		droplet.service(pRequest, pResponse);
		assertTrue(getRequest().getObjectParameter("salePrice") !=null);
		assertTrue(getRequest().getObjectParameter("listPrice") !=null);
	}

}
