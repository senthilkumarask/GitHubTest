package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFetchProductIdDroplet extends BaseTestCase {
	
	public void testService() throws ServletException, IOException
	{
		GiftRegistryFetchProductIdDroplet fetchProductIdDroplet = (GiftRegistryFetchProductIdDroplet) getObject ("giftRegistryFetchProductIdDroplet");
		getRequest().setParameter("skuId", "sku40037");
		fetchProductIdDroplet.service(getRequest(), getResponse());
		String productId = (String) getRequest().getParameter("productId");
		assertNotNull(productId);
		
	}
}
