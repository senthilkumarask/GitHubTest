package com.bbb.commerce.browse.droplet;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestMinimalProductDetailDroplet extends BaseTestCase{

	public void testMinimalProductDetail() throws Exception
	{
		 DynamoHttpServletRequest pRequest = getRequest();
	     DynamoHttpServletResponse pResponse = getResponse();
	     MinimalProductDetailDroplet minimalProductDetailDroplet = (MinimalProductDetailDroplet) getObject("minimalProductDetailDroplet");	
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
			pRequest.setParameter("id",
					(String)getObject("id"));
			atg.servlet.ServletUtil.setCurrentRequest(pRequest);
			atg.servlet.ServletUtil.setCurrentResponse(pResponse);
			
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		minimalProductDetailDroplet.service(pRequest, pResponse);
		assertNotNull(pRequest.getParameter("productVo"));
	
	}

}
