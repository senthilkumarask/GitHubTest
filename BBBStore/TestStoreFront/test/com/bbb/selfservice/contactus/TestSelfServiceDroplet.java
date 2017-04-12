package com.bbb.selfservice.contactus;
import java.util.ArrayList;
import java.util.List;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.droplet.ContactUSCategoryDroplet;
import com.bbb.selfservice.droplet.ContactUSTimeZoneDroplet;
import com.sapient.common.tests.BaseTestCase;

public class TestSelfServiceDroplet extends BaseTestCase {
	
	public void testService() throws Exception
	{
		//ContactUSCategoryDroplet 
		ContactUSCategoryDroplet contactUSCategoryDroplet  = (ContactUSCategoryDroplet) getObject("contactUSCategoryDroplet");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		String pSiteId = (String) getObject("siteId");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		contactUSCategoryDroplet.setLoggingDebug(true);
		contactUSCategoryDroplet.service(getRequest(), getResponse());
		
		List<String> subjectCategoryTypes =(ArrayList<String>) getRequest().getObjectParameter("subjectCategoryTypes");
		
		assertTrue(subjectCategoryTypes != null);
		assertFalse(subjectCategoryTypes == null);
		
	    String subjectValue = 	(String)  getObject("subject");
	    String subjectInValue = (String)  getObject("subjectInvalid");
		
	    if(subjectCategoryTypes !=null){
		 assertTrue(subjectCategoryTypes.contains(subjectValue));
		 assertFalse(subjectCategoryTypes.contains(subjectInValue));
		}
	    
	    //ContactUSTimeZoneDroplet
	    ContactUSTimeZoneDroplet contactUStzDroplet= (ContactUSTimeZoneDroplet) getObject("contactUSTimeZoneDroplet");
	    contactUStzDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
	    contactUStzDroplet.service(getRequest(), getResponse());
	    assertNotNull(getRequest().getParameter("timeZoneTypes"));
	    
	    
	    //GetCollegeProductDroplet
//	    GetCollegeProductDroplet getColProdDroplet=(GetCollegeProductDroplet)Nucleus.getGlobalNucleus().resolveName(
//	    		"com/bbb/selfservice/GetCollegeProductDroplet");
//	    getRequest().setParameter("collegeId", "10");
//	    getRequest().setParameter("siteId", "BuyBuyBaby");
//
//	    getColProdDroplet.service(getRequest(), getResponse());
//	    assertNotNull(getRequest().getParameter("getVO"));
	    
	    
	}
	
	

}
