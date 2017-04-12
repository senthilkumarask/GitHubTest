package com.bbb.commerce.catalog.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.selfservice.vo.SchoolVO;
import com.sapient.common.tests.BaseTestCase;

/**
 * To test TestSchoolLookupDroplet service method.
 * 
 * @author skuma7
 */

public class TestSchoolLookupDroplet extends BaseTestCase {

	private static final String DROPLET = "bbbSchoolLookupDroplet";
	private static final String SCHOOL_ID = "schoolId";

	/**
	 * To test the service method of SchoolLookupDroplet 
	 * 
	 * @throws Exception
	 */
	public void testServiceForSchoolLookupDroplet() throws Exception{

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		SchoolLookupDroplet schoolLookupDroplet = (SchoolLookupDroplet) getObject(DROPLET);	
		String schoolId = (String) getObject(SCHOOL_ID);
		
		// Setting request parameters
		pRequest.setParameter(BBBCoreConstants.SCHOOL_ID, schoolId);

		// Calling droplet service method		
		schoolLookupDroplet.service(pRequest, pResponse);		
		SchoolVO schoolVO = (SchoolVO)pRequest.getObjectParameter(BBBCoreConstants.SCHOOL_VO);		
//		assertNotNull(schoolVO);
		addObjectToAssert("schoolName", schoolVO.getSchoolName());
		addObjectToAssert("addrLine1", schoolVO.getAddrLine1()); 	
		addObjectToAssert("zip", schoolVO.getZip()); 	
	
	}	
}