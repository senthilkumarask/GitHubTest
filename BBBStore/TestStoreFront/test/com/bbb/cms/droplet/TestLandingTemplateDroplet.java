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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;

import com.bbb.cms.HomePageTemplateVO;
import com.bbb.cms.LandingTemplateVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.integration.SearchManager;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestLandingTemplateDroplet extends BaseTestCase {
	@SuppressWarnings("unchecked")
	public void testLandingTemplate() throws Exception {
    	
		LandingTemplateDroplet objLandingTemplateDroplet = (LandingTemplateDroplet) getObject("landingTemplateDroplet");
    	String siteId= (String) getObject("siteId");
    	String pageName= (String) getObject("pageName");
    	getRequest().setParameter("siteId", siteId);
    	getRequest().setParameter("pageName", pageName);
    	
    	objLandingTemplateDroplet.service(getRequest(), getResponse());
    	LandingTemplateVO landingTemplateVO =  (LandingTemplateVO)getRequest().getObjectParameter("LandingTemplateVO");
		
    	assertNotNull(landingTemplateVO);
    	assertNotNull(landingTemplateVO.getHeroImages());
		
    }
}
