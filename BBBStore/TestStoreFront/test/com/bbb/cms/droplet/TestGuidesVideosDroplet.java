package com.bbb.cms.droplet;

import java.io.IOException;

import javax.servlet.ServletException;


import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestGuidesVideosDroplet  extends BaseTestCase{
	
	public void testGuidesVideosService() throws BBBBusinessException,
	BBBSystemException, ServletException, IOException
	{
		GuidesVideosDroplet guidesVideosDroplet = (GuidesVideosDroplet) getObject("guidesVideosDroplet");
		guidesVideosDroplet.setLoggingDebug(true);
		
		guidesVideosDroplet.service(getRequest(), getResponse());
		
		String updatedVideosList =  (String) getRequest().getObjectParameter("finalVideoList");
		
		addObjectToAssert("list", updatedVideosList.isEmpty());
		//addObjectToAssert("repositoryDisplayName", staticTemplateData.getItemDisplayName());
	
	}

}
