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
package com.bbb.utils;

import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestBBBLogBuildNumber extends BaseTestCase {
    @SuppressWarnings("unchecked")
	public void testService() throws Exception {
    	
    	BBBLogBuildNumber logBuildNumber = (BBBLogBuildNumber) getObject("logBuildNumber");
    	String siteId= (String) getObject("siteId");
    	String stateId= (String) getObject("stateId");
    	
    	getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("stateId", stateId);
		
		logBuildNumber.service(getRequest(), getResponse());
		//assertTrue(bridalDetails.size() > 0);
		
    }
}
