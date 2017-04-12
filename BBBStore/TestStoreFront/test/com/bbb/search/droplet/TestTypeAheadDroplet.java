/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestTypeAheadDroplet.java
 *
 *  DESCRIPTION: Test Type Ahead Droplet
 *
 *  HISTORY:
 *  Dec 7, 2011  Initial version
*/
package com.bbb.search.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.ServletUtil;

import com.bbb.search.bean.result.FacetQueryResults;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Type Ahead Droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestTypeAheadDroplet extends BaseTestCase {
    public void testService() throws ServletException,IOException {
    	TypeAheadDroplet typeAheadDroplet = (TypeAheadDroplet) getObject("typeAheadDroplet");
    	ServletUtil.setCurrentRequest(getRequest());
    	getRequest().setParameter("search","AMP");
    	getRequest().setParameter("siteId",(String) getObject("siteId"));
        typeAheadDroplet.service(getRequest(), getResponse());
        //addObjectToAssert("count",((FacetQueryResults)getRequest().getObjectParameter("FacetQueryResults")).getResults().size());
        assertNotNull("Type Ahead Results are not obtained.",getRequest().getParameter("FacetQueryResults"));
	}
}
