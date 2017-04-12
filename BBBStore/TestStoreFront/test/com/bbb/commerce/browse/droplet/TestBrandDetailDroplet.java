package com.bbb.commerce.browse.droplet;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBrandDetailDroplet extends BaseTestCase{

	public void testProcessBrandResults() throws Exception
	{
		 DynamoHttpServletRequest pRequest = getRequest();
	     DynamoHttpServletResponse pResponse = getResponse();
	     BrandDetailDroplet brandDetailDroplet = (BrandDetailDroplet) getObject("brandDetailDroplet");	
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
			pRequest.setParameter("origBrandName",
					(String)getObject("origBrandName"));
			pRequest.setParameter("keywordName",
					(String)getObject("keywordName"));
			atg.servlet.ServletUtil.setCurrentRequest(pRequest);
			atg.servlet.ServletUtil.setCurrentResponse(pResponse);
			
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		brandDetailDroplet.service(pRequest, pResponse);
		assertNotNull(pRequest.getParameter("seoUrl"));
		assertNotNull(pRequest.getParameter("sortOptionVO"));
		assertEquals((String)getObject("origBrandName"), pRequest.getParameter("brandName"));
	
	
	}


	

}
