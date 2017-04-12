package com.bbb.browse;



import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.droplet.ProductDetailDroplet;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.sapient.common.tests.BaseTestCase;

public class TestProductDetailDroplet extends BaseTestCase{

	public void testProcessProductVO() throws  ServletException, IOException
	{
		ProductDetailDroplet productDetailDroplet = (ProductDetailDroplet) getObject("productDetailDroplet");	
	
		getRequest().setParameter("id","prod60012");
		getRequest().setParameter("siteId","BuyBuyBaby");
		getRequest().setParameter("color","RED");
		productDetailDroplet.service(getRequest(), getResponse());
		ProductVO productVO=(ProductVO)getRequest().getObjectParameter("productVO");
		
		assertNotNull(productVO);
		
		
		//assertNotNull(R);
		getRequest().setParameter("id","prod60012");
		getRequest().setParameter("siteId","BuyBuyBaby");
		getRequest().setParameter("skuId","sku60016");
		productDetailDroplet.service(getRequest(), getResponse());
		productVO=(ProductVO)getRequest().getObjectParameter("productVO");
		
		assertNotNull(productVO);
	
		//LTL Product is LTL
		productVO=(ProductVO)getRequest().getObjectParameter("productVO");
		SKUDetailVO skuvo =(SKUDetailVO)getRequest().getObjectParameter("pSKUDetailVO");
		System.out.println(productVO.isLtlProduct());
		System.out.println(skuvo.isLtlItem());
		assertTrue(productVO.isLtlProduct());
		assertTrue(skuvo.isLtlItem());
	}
	
	public void testProcessCollectionVO() throws  ServletException, IOException
	{
		ProductDetailDroplet productDetailDroplet = (ProductDetailDroplet) getObject("productDetailDropletCollection");	
	
		getRequest().setParameter("id","prod60036");
		getRequest().setParameter("siteId","BuyBuyBaby");
		productDetailDroplet.service(getRequest(), getResponse());
	
		CollectionProductVO collectionVO=(CollectionProductVO)getRequest().getObjectParameter("collectionVO");
		assertNotNull(collectionVO);
		//assertEquals("prod60039", collectionVO.getChildProducts().get(0).getProductId());
		//assertEquals("prod60038", collectionVO.getChildProducts().get(1).getProductId());
		//assertEquals("prod60037", collectionVO.getChildProducts().get(2).getProductId());
		//assertNotNull(productVO);
		
		//LTL Collection is LTL
		assertTrue(collectionVO.isLtlProduct());


	}
	
}
