/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestSearchDroplet.java
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Nov 7, 2011  Initial version
*/
package com.bbb.cms.tools;

import atg.repository.RepositoryItem;

import com.bbb.exception.BBBBusinessException;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestCmsTools extends BaseTestCase {
	
	public void testGetStaticTemplateData() throws Exception {
    	
		CmsTools objCmsTools = (CmsTools) getObject("testCmsTools");
    	
		String siteId= (String) getObject("siteId");
    	String pageName= (String) getObject("pageName");
    	   	
    	RepositoryItem staticPageDetail = objCmsTools.getStaticTemplateData(siteId, pageName,"false");
    	
    	assertNotNull(staticPageDetail.getPropertyValue("pageTitle"));
		
    }
	
	public void testGetShippingMethods() throws Exception {
    	
		CmsTools objCmsTools = (CmsTools) getObject("testCmsTools");
    	
		   	
    	RepositoryItem[] shippingMethods = objCmsTools.getShippingMethods("BuyBuyBaby");
    	
    	assertNotNull(shippingMethods[0].getPropertyValue("shipMethodName"));
		
    }
	
	public void testGetAllShippingPriceDetails() throws Exception {
    	
		CmsTools objCmsTools = (CmsTools) getObject("testCmsTools");
		String siteId= (String) getObject("siteId");
		   	
    	RepositoryItem[] shippingMethodPrices = objCmsTools.getAllShippingPriceDetails(siteId);
    	
    	assertNotNull(shippingMethodPrices[0].getPropertyValue("shipMethodCode"));
    	assertNotNull(shippingMethodPrices[0].getPropertyValue("price"));
    }
	
	public void testGetAllShippingPriceDetailsAsc() throws Exception {
    	
		CmsTools objCmsTools = (CmsTools) getObject("testCmsTools");
		String siteId= (String) getObject("siteId");
		   	
    	RepositoryItem[] shippingMethodPrices = objCmsTools.getAllShippingPriceDetails(siteId);
    	
    	assertNotNull(shippingMethodPrices[0].getPropertyValue("shipMethodCode"));
    	assertNotNull(shippingMethodPrices[0].getPropertyValue("price"));
		
    }
	
	public void testGetAllSurchargePrice() throws Exception {
		CmsTools objCmsTools = (CmsTools) getObject("testCmsTools");
		String siteId= (String) getObject("siteId");
		Double skuWeight = (Double)getObject("skuWeight");
    	RepositoryItem[] deliverySurcharge = objCmsTools.getAllSurchargePrice(skuWeight,siteId);
    	assertNotNull(deliverySurcharge[0].getPropertyValue("price"));
}
	
}
