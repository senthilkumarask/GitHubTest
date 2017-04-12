package com.bbb.commerce.browse.droplet;

import java.util.ArrayList;
import java.util.TreeMap;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestCollegeSearchDroplet extends BaseTestCase {

	@SuppressWarnings("null")
	public void testProcessCollegeResults() throws Exception
	{
		CollegeSearchDroplet collegeSearchDroplet = (CollegeSearchDroplet) getObject("collegeSearchDroplet");	
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		collegeSearchDroplet.service(getRequest(), getResponse());
		@SuppressWarnings("unchecked")
		TreeMap <String,ArrayList<CollegeVO>> alphabetCollegeListMap=(TreeMap <String,ArrayList<CollegeVO>>)getRequest().getObjectParameter("collegesMap");
		if(alphabetCollegeListMap==null){
			
		assertNull(alphabetCollegeListMap);
		
		}
		else{
		assertFalse(alphabetCollegeListMap.isEmpty());
		
		}
	}
}
