package com.bbb.cms.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;

import com.bbb.cms.GuidesTemplateVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestGuidesLongDescDroplet  extends BaseTestCase{
	
	public void testGuidesLongDescService() throws BBBBusinessException,
	BBBSystemException, ServletException, IOException
	{
		GuidesLongDescDroplet guidesLongDescDroplet = (GuidesLongDescDroplet) getObject("guidesLongDescDroplet");
		guidesLongDescDroplet.setLoggingDebug(true);

		String guideId = (String) getObject("guideId");
		getRequest().setParameter("guideId", guideId);
		guidesLongDescDroplet.service(getRequest(), getResponse());
		
		 GuidesTemplateVO guidesLongDesc =  (GuidesTemplateVO) getRequest().getObjectParameter("guidesLongDesc");
		 addObjectToAssert("GuideTitle", guidesLongDesc.getTitle());
		 addObjectToAssert("GuideLongDesc", guidesLongDesc.getLongDescription());
		
	}

}
