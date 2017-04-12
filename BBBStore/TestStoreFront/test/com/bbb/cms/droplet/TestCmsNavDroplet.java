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

import javax.servlet.ServletException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestCmsNavDroplet extends BaseTestCase {
    public void testCmsNavService() throws ServletException,IOException,BBBBusinessException,BBBSystemException {
    	
    	CmsNavFlyoutDroplet CmsNavFlyoutDroplet = (CmsNavFlyoutDroplet) getObject("cmsNavFlyoutDroplet");
    	CmsNavFlyoutDroplet.setLoggingDebug(true);
    	getRequest().setParameter("siteId", (String)getObject("siteId"));
		getRequest().setParameter("CatalogId", (String)getObject("CatalogId"));
		CmsNavFlyoutDroplet.service(getRequest(), getResponse());
    }
}
