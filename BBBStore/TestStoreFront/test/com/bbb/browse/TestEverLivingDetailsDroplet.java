package com.bbb.browse;



import java.io.IOException;
import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.droplet.EverLivingDetailsDroplet;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.sapient.common.tests.BaseTestCase;

public class TestEverLivingDetailsDroplet extends BaseTestCase{

	public void testProcessProductVO() throws  ServletException, IOException
	{
		EverLivingDetailsDroplet everLivingDetailsDroplet = (EverLivingDetailsDroplet) getObject("everLivingDetailsDroplet");
		 DynamoHttpServletRequest pRequest = getRequest();
	     DynamoHttpServletResponse pResponse = getResponse();
	
	     pRequest.setParameter("id","prod60026");
	     pRequest.setParameter("siteId","BuyBuyBaby");
	     pRequest.setParameter("color","RED");
	     atg.servlet.ServletUtil.setCurrentRequest(pRequest);
		 atg.servlet.ServletUtil.setCurrentResponse(pResponse);
		everLivingDetailsDroplet.service(pRequest, pResponse);
		ProductVO productVO=(ProductVO)getRequest().getObjectParameter("productVO");
		
		assertNotNull(productVO);
		
		
		//assertNotNull(R);
		pRequest.setParameter("id","prod60026");
		pRequest.setParameter("siteId","BuyBuyBaby");
		pRequest.setParameter("skuId","sku60075");
		everLivingDetailsDroplet.service(pRequest,pResponse);
		productVO=(ProductVO)getRequest().getObjectParameter("productVO");
		
		assertNotNull(getRequest().getParameter("productVO"));
	

	}
	
	public void testProcessCollectionVO() throws  ServletException, IOException
	{
		EverLivingDetailsDroplet everLivingDetailsDroplet = (EverLivingDetailsDroplet) getObject("everLivingDetailsDropletCollection");	
		 DynamoHttpServletRequest pRequest = getRequest();
	     DynamoHttpServletResponse pResponse = getResponse();
	
	     pRequest.setParameter("id"," prod60026");
	     pRequest.setParameter("siteId","BuyBuyBaby");
	     atg.servlet.ServletUtil.setCurrentRequest(pRequest);
		 atg.servlet.ServletUtil.setCurrentResponse(pResponse);
		everLivingDetailsDroplet.service(pRequest, pResponse);
	
		CollectionProductVO collectionVO=(CollectionProductVO)getRequest().getObjectParameter("collectionVO");
		assertNotNull(getRequest().getParameter("collectionVO"));
	}
	
}
