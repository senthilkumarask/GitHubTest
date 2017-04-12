package com.bbb.cms.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestStaticTemplateDroplet  extends BaseTestCase{
	
	public void testStaticTemplateService() throws BBBBusinessException,
	BBBSystemException, ServletException, IOException
	{
		StaticTemplateDroplet staticTemplateDroplet = (StaticTemplateDroplet) getObject("staticTemplateDroplet");
		staticTemplateDroplet.setLoggingDebug(true);

		String siteId = (String) getObject("siteId");
		String pageName = (String) getObject("pageName");
		
		getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("pageName", pageName);
		
		staticTemplateDroplet.service(getRequest(), getResponse());
		
		RepositoryItem staticTemplateData = (RepositoryItem) getRequest().getObjectParameter("staticTemplateData");
		
		addObjectToAssert("repositoryId", staticTemplateData.getRepositoryId());
		addObjectToAssert("repositoryDisplayName", staticTemplateData.getItemDisplayName());
	
	}

}
