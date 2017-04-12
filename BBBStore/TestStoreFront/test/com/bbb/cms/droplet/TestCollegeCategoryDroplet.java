package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestCollegeCategoryDroplet extends BaseTestCase{



	@SuppressWarnings("unchecked")
	public void testServiceCollegeCategorySubcategories() throws BBBBusinessException,
	BBBSystemException, ServletException, IOException
	{
		CollegeCategoryDroplet collegeCategoryDroplet = (CollegeCategoryDroplet)getObject("collegeCategoryDroplet");
		collegeCategoryDroplet.setLoggingDebug(true);

		String siteId= (String) getObject("siteId");
		String categoryId= (String) getObject("categoryId");
		getRequest().setParameter("id", categoryId);
		getRequest().setParameter("siteId", siteId);
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		collegeCategoryDroplet.service(getRequest(), getResponse());
		CategoryVO subcategories=(CategoryVO)getRequest().getObjectParameter("collegeCategories");
		assertNotNull("Collge Categories are NULL",subcategories);
	}
}
