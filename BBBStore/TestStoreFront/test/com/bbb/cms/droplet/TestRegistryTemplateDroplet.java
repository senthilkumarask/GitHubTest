package com.bbb.cms.droplet;

import com.bbb.cms.RegistryTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestRegistryTemplateDroplet extends BaseTestCase{

		
		public void testRegistryTemplateService() throws Exception
		{
			RegistryTemplateDroplet registryTemplateDroplet = (RegistryTemplateDroplet) getObject("registryTemplateDroplet");
			registryTemplateDroplet.setLoggingDebug(true);

			String siteId = (String) getObject("siteId");
			String pageName = (String) getObject("pageName");
			int brandSize = 0;
			
			getRequest().setParameter("siteId", siteId);
			getRequest().setParameter("pageName", pageName);
			
			registryTemplateDroplet.service(getRequest(), getResponse());
			
			RegistryTemplateVO registryTemplateVO = (RegistryTemplateVO) getRequest().getObjectParameter("RegistryTemplateVO");
			
			addObjectToAssert("pageTitle", registryTemplateVO.getPageTitle());
		    addObjectToAssert("pageHeaderCopy", registryTemplateVO.getPageHeaderCopy());
		    if(registryTemplateVO.getBrands()!= null) {
		    	brandSize = registryTemplateVO.getBrands().size();
		    }
		    addObjectToAssert("brandImagesSize", brandSize);
		    
			System.out.println("Page Title : " +registryTemplateVO.getPageTitle());
			System.out.println("Page Header Copy Null : " +registryTemplateVO.getPageHeaderCopy().isEmpty());
			System.out.println("Brand List Size : " +brandSize);
		}


}
