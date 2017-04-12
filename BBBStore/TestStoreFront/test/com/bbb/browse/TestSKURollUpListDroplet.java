package com.bbb.browse;



import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.droplet.SKURollUpListDroplet;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.sapient.common.tests.BaseTestCase;

public class TestSKURollUpListDroplet extends BaseTestCase{

	public void testProcessSKURollup() throws  ServletException, IOException
	{
		SKURollUpListDroplet skuRollUpListDroplet = (SKURollUpListDroplet) getObject("skuRollUpListDroplet");	
	
		getRequest().setParameter("id","prod60016");
		getRequest().setParameter("siteId","BuyBuyBaby");
		getRequest().setParameter("prodSize","King");
		getRequest().setParameter("prodColor","Black");
		getRequest().setParameter("prodFinish","");
		skuRollUpListDroplet.service(getRequest(), getResponse());
	
		SKUDetailVO pSKUDetailVO=(SKUDetailVO) getRequest().getObjectParameter("SKUDetailsVO");
		
		assertNotNull(pSKUDetailVO);
		assertEquals("sku60038", pSKUDetailVO.getSkuId());
		assertEquals("Product SKU with different color and size - Black - King", pSKUDetailVO.getDisplayName());
		
		//assertNotNull(productVO);
		
	

	}

	public void testProcessLTLSKURollup() throws  ServletException, IOException
	{
		SKURollUpListDroplet skuRollUpListDroplet = (SKURollUpListDroplet) getObject("skuRollUpListDroplet");	

		getRequest().setParameter("id","prod60012");
		getRequest().setParameter("siteId","BuyBuyBaby");
		getRequest().setParameter("prodColor","RED");
		getRequest().setParameter("prodSize","");
		getRequest().setParameter("prodFinish","");

		skuRollUpListDroplet.service(getRequest(), getResponse());

		SKUDetailVO pSKUDetailVO=(SKUDetailVO) getRequest().getObjectParameter("SKUDetailsVO");
		
		assertNotNull(pSKUDetailVO);
		assertEquals("sku60016", pSKUDetailVO.getSkuId());
}




}
