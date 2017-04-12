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

import java.util.Map;
import java.util.Set;

import com.bbb.constants.BBBCmsConstants;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestBridalShowDetailDroplet extends BaseTestCase {
    @SuppressWarnings("unchecked")
	public void testBridalShowDetail() throws Exception {
    	
    	BridalShowDetailDroplet objBridalShowDetailDroplet = (BridalShowDetailDroplet) getObject("bridalShowDetailDroplet");
    	String siteId= (String) getObject("siteId");
    	String stateId= (String) getObject("stateId");
    	
    	getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("stateId", stateId);
		
		objBridalShowDetailDroplet.service(getRequest(), getResponse());
		Set<Map<String, Object>> bridalDetails = (Set<Map<String, Object>>)getRequest().getObjectParameter("stateItem");
		
		assertTrue(bridalDetails.size() > 0);
		
		for (Map<String, Object> bridalDetailMap : bridalDetails) {
			assertNotNull(bridalDetailMap.get(BBBCmsConstants.NAME));			
		}
    }
}
