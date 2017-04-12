/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestSampleDroplte.java
 *
 *  DESCRIPTION: Test sample droplet
 *
 *  HISTORY:
 *  Oct 14, 2011  Initial version
 */
package com.bbb.selfservice.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

/**
 * Test Sample droplet
 * 
 * @author Sapient Corporation
 * 
 */
public class TestGetCollegeProductDroplet extends BaseTestCase {

	public void testService() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		GetCollegeProductDroplet productDroplet = (GetCollegeProductDroplet) getObject("getCollegeProductDroplet");

		String collegeId = (String) getObject("collegeId");
		String siteId = (String) getObject("siteId");
		pRequest.setParameter("collegeId", collegeId);
		pRequest.setParameter("siteId", siteId);
		
		productDroplet.service(pRequest, pResponse);
		assertNotNull(pRequest.getParameter("productList"));

	}
}
