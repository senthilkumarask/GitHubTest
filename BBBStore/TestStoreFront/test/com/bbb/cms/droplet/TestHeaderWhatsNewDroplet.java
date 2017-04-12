package com.bbb.cms.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestHeaderWhatsNewDroplet  extends BaseTestCase{
	
	public void testWhatsNewService() throws BBBBusinessException,
	BBBSystemException, ServletException, IOException
	{
		HeaderWhatsNewDroplet headerWhatsNewDroplet = (HeaderWhatsNewDroplet) getObject("headerWhatsNewDroplet");
		headerWhatsNewDroplet.setLoggingDebug(true);

		String siteId = (String) getObject("siteId");
		getRequest().setParameter("site", siteId);
		headerWhatsNewDroplet.service(getRequest(), getResponse());
		String categoryId = (String) getRequest().getObjectParameter("categoryId");
		addObjectToAssert("categoryId", categoryId);
		
		System.out.println("Category Id : " +categoryId);
	}

}
