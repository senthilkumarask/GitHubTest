package com.bbb.browse;



import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.browse.droplet.BreadcrumbDroplet;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBreadcrumbDroplet extends BaseTestCase{

	public void testBreadcrumbDropletProduct() throws  ServletException, IOException, BBBSystemException
	{
		BreadcrumbDroplet breadcrumbDroplet = (BreadcrumbDroplet) getObject("breadcrumbDropletProduct");	
	
		getRequest().setParameter("categoryId","cat20007");
		getRequest().setParameter("productId","prod10013");
		String pSiteId = (String) getObject("siteId");
		getRequest().setParameter("siteId",pSiteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
               siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
               throw new BBBSystemException("Exception" + siteContextException);
        }

		breadcrumbDroplet.service(getRequest(), getResponse());
	
		Map<String, CategoryVO> breadCrumb = (Map<String, CategoryVO>)getRequest().getObjectParameter("breadCrumb");
		
		assertNotNull(breadCrumb);
	}
	
	
	public void testBreadcrumbDropletCategory() throws  ServletException, IOException
	{
		BreadcrumbDroplet breadcrumbDroplet = (BreadcrumbDroplet) getObject("breadcrumbDropletCategory");	
		
//		getRequest().setParameter("categoryId","4294967266");
		getRequest().setParameter("categoryId","cat20009");
		breadcrumbDroplet.service(getRequest(), getResponse());
	
		Map<String, CategoryVO> breadCrumb = (Map<String, CategoryVO>)getRequest().getObjectParameter("breadCrumb");
		
		assertNotNull(breadCrumb);
		
	

	}
	
	public void testBreadcrumbDropletPrimaryCategory() throws  ServletException, IOException
	{
		BreadcrumbDroplet breadcrumbDroplet = (BreadcrumbDroplet) getObject("breadcrumbDropletPrimaryCategory");	
		
//		getRequest().setParameter("categoryId","4294967266");
		getRequest().setParameter("productId","125273");
		getRequest().setParameter("siteId","BuyBuyBaby");
		breadcrumbDroplet.service(getRequest(), getResponse());
	    
		Map<String, CategoryVO> breadCrumb = (Map<String, CategoryVO>)getRequest().getObjectParameter("breadCrumb");
		
		assertNotNull(breadCrumb);
		
	

	}
			
	
}
