package com.bbb.commerce.browse.droplet;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestMobileCategoryOrderDroplet extends BaseTestCase{

	public void testMobileCategoryOrder() throws Exception
	{
		 DynamoHttpServletRequest pRequest = getRequest();
	     DynamoHttpServletResponse pResponse = getResponse();
	     MobileCategoryOrderDroplet mobileCategoryOrderDroplet = (MobileCategoryOrderDroplet) getObject("mobileCategoryOrderDroplet");	
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
			pRequest.setParameter("categoryId",
					(String)getObject("categoryId"));
			atg.servlet.ServletUtil.setCurrentRequest(pRequest);
			atg.servlet.ServletUtil.setCurrentResponse(pResponse);
			
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		mobileCategoryOrderDroplet.service(pRequest, pResponse);
		assertNotNull(pRequest.getParameter("categoryOrder"));
	
	}

}
