package com.bbb.browse;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.droplet.GiftCardListDroplet;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftCardListDroplet extends BaseTestCase {
	
	public void testService() throws ServletException, IOException
	{
		GiftCardListDroplet giftCardListDroplet = (GiftCardListDroplet) getObject("giftCardListDroplet");	
		String siteId = (String) getObject("siteId");
		getRequest().setParameter("siteId",siteId);
		giftCardListDroplet.service(getRequest(), getResponse());
	
		List<ProductVO> productVOList=(List<ProductVO>) getRequest().getObjectParameter("productVOList");
		
		assertNotNull(productVOList);	

	}
}
