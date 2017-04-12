/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestBBBGetRootCategoryDroplet.java
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Nov 7, 2011  Initial version
 */
package com.bbb.cms.droplet;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * Test TestBBBGetRootCategoryDroplet
 * 
 * @author Sapient Corporation
 * 
 */
public class TestBBBGetRootCategoryDroplet extends BaseTestCase {
	public void testBBBGetRootCategoryDroplet() throws Exception {

		BBBGetRootCategoryDroplet rootCategoryDroplet = (BBBGetRootCategoryDroplet) getObject("rootCategoryDroplet");
		
		String childCategory = (String) getObject("childCategory");
		String siteId = (String) getObject("siteId");

		getRequest().setParameter("childCategory", childCategory);
		getRequest().setParameter("siteId", siteId);

		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		
		rootCategoryDroplet.service(getRequest(), getResponse());
		assertNotNull(rootCategoryDroplet.getCatalogTools());
		assertEquals(getRequest().getParameter("rootCategory"), "cat20002");
	}
}
