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

import java.util.SortedMap;
import java.util.TreeMap;

import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestBridalShowStateDroplet extends BaseTestCase {
	@SuppressWarnings("unchecked")
	public void testBridalShowStateDetail() throws Exception {
    	
		BridalShowStateDroplet objBridalShowStateDroplet = (BridalShowStateDroplet) getObject("bridalShowStateDroplet");
    	String siteId= (String) getObject("siteId");    	
    	getRequest().setParameter("siteId", siteId);
		
		
		objBridalShowStateDroplet.service(getRequest(), getResponse());
		SortedMap<String, String> stateMap =  (TreeMap<String, String>)getRequest().getObjectParameter("stateMap");
		
		assertTrue(stateMap.size() > 0);		
		
		for (String key : stateMap.keySet()) {
			assertNotNull(stateMap.get(key));			
		}
    }
}
