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
package com.bbb.search.droplet;

import com.sapient.common.tests.BaseTestCase;
import com.bbb.cms.droplet.PageTabsOrderingDroplet;
import com.bbb.search.droplet.SearchDroplet;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestPageTabsOrderingDroplet extends BaseTestCase {
    public void testService() throws Exception {
    	PageTabsOrderingDroplet sDroplet = (PageTabsOrderingDroplet) getObject("pageTabsOrderingDroplet");
        getRequest().setParameter("pageName","Product Details Page");
        sDroplet.service(getRequest(), getResponse());
        System.out.println("Page Tab Details: "+getRequest().getObjectParameter("pageTab"));
        assertNotNull("Page Tab Details are obtained:: ",getRequest().getObjectParameter("pageTab"));
    }
}
