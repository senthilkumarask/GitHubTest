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

import java.util.List;

import com.bbb.cms.GuidesTemplateVO;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestPaginationDroplet extends BaseTestCase {
	@SuppressWarnings("unchecked")
	public void testPaginationDroplet() throws Exception {
    	
		GuidesTemplateDroplet objGuidesTemplateDroplet = (GuidesTemplateDroplet) getObject("guidesTemplateDroplet");
    	String contentType= (String) getObject("contentType");   
    	String guidesCategory= (String) getObject("guidesCategory");
    	String siteId= (String) getObject("siteId");
    	getRequest().setParameter("contentType", contentType);
    	getRequest().setParameter("guidesCategory", guidesCategory);
    	getRequest().setParameter("siteId", siteId);
		
    	objGuidesTemplateDroplet.service(getRequest(), getResponse());
    	List<GuidesTemplateVO> lstGuidesTemplate =  (List<GuidesTemplateVO>)getRequest().getObjectParameter("lstGuidesTemplate");
		
		PaginationDroplet objPaginationDroplet = (PaginationDroplet) getObject("paginationDroplet");
    	
		String pageNo= (String) getObject("pageNo");
    	String perPage= (String) getObject("perPage");
    	String seeAll= (String) getObject("seeAll");
    	List<GuidesTemplateVO> guideList= lstGuidesTemplate;
    	
    	getRequest().setParameter("pageNo", pageNo);
    	getRequest().setParameter("perPage", perPage);
    	getRequest().setParameter("seeAll", seeAll);
    	getRequest().setParameter("guideList", guideList);
    	
    	objPaginationDroplet.service(getRequest(), getResponse());
    	
    	List<GuidesTemplateVO> guideReturnList = (List<GuidesTemplateVO>)getRequest().getObjectParameter("guideReturnList");
    	long pageCount = (Long)getRequest().getObjectParameter("pageCount");
		
    	
    	assertNotNull(guideReturnList);
    	assertTrue(pageCount > 0);
		
    }
}
