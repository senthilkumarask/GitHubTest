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
package com.bbb.cms.droplet;

import com.bbb.cms.HomePageTemplateVO;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestHomePageTemplateDroplet extends BaseTestCase {
	public void testHomePageTemplate() throws Exception {
    	
		HomePageTemplateDroplet objHomePageTemplateDroplet = (HomePageTemplateDroplet) getObject("homePageTemplateDroplet");
    	String siteId= (String) getObject("siteId");
    	getRequest().setParameter("siteId", siteId);
		
    	objHomePageTemplateDroplet.service(getRequest(), getResponse());
    	HomePageTemplateVO homePageTemplateVO =  (HomePageTemplateVO)getRequest().getObjectParameter("homePageTemplateVO");
		System.out.println(homePageTemplateVO.toString());
    	assertNotNull(homePageTemplateVO);
    	assertNotNull(homePageTemplateVO.getPromoBoxFirst());
		
    }
}
