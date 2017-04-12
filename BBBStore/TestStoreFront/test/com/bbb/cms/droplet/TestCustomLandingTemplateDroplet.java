package com.bbb.cms.droplet;

import java.io.IOException;
import javax.servlet.ServletException;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.cms.vo.CMSResponseVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;


public class TestCustomLandingTemplateDroplet extends BaseTestCase{


	public void testServiceCustomLandingTemplate() throws
	 ServletException, IOException, BBBSystemException
	{
		CustomLandingTemplateDroplet customLandingTemplateDroplet = (CustomLandingTemplateDroplet)getObject("customLandingTemplateDroplet"); //$NON-NLS-1$
		customLandingTemplateDroplet.setLoggingDebug(true);

		String categoryId= (String) getObject("categoryId"); //$NON-NLS-1$
		getRequest().setParameter("categoryId", categoryId); //$NON-NLS-1$
	
		String templateName= (String) getObject("templateName"); //$NON-NLS-1$
		getRequest().setParameter("templateName", templateName);
		String alternateURLRequired= (String) getObject("altURLRequired"); //$NON-NLS-1$
		getRequest().setParameter("altURLRequired", alternateURLRequired);
	
		String siteId= (String) getObject("siteId");
		getRequest().setParameter("siteId", siteId);
		String channelID= (String) getObject("channelID");
		getRequest().setParameter("X-bbb-channel", channelID);
		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		customLandingTemplateDroplet.service(getRequest(), getResponse());
		CMSResponseVO responseVO=(CMSResponseVO)getRequest().getObjectParameter("cmsResponseVO"); //$NON-NLS-1$
		assertNotNull("responseVO is NULL",responseVO); //$NON-NLS-1$
	}
}
	

