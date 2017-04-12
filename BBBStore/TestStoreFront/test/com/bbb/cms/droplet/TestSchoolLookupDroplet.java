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


import com.bbb.commerce.catalog.droplet.SchoolLookupDroplet;
import com.bbb.selfservice.vo.SchoolVO;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test SchoolLookup  droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestSchoolLookupDroplet extends BaseTestCase {
	 
	public void testSchoolLookupDroplet() throws Exception {
    	
		SchoolLookupDroplet objSchoolLookupDroplet = (SchoolLookupDroplet) getObject("schoolLookupDroplet");
		objSchoolLookupDroplet.setLoggingDebug(true);
    	String schoolId= (String) getObject("schoolId");
    	getRequest().setParameter("schoolId", schoolId);
		
    	objSchoolLookupDroplet.service(getRequest(), getResponse());
    	SchoolVO schoolVO =  (SchoolVO)getRequest().getObjectParameter("SchoolVO");
		
    	assertNotNull(schoolVO);
    	assertNotNull(schoolVO.getSchoolName());
		
    }
}
