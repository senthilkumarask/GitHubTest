package com.bbb.cms.manager;

import com.bbb.cms.RegistryTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestRegistryManager extends BaseTestCase {

  public void testRegistryService() throws Exception {
	RegistryTemplateManager manager = (RegistryTemplateManager) getObject("manager");

    String pageName = (String) getObject("pageName");
    String siteId = (String) getObject("siteId");
    int brandSize =0;

    
    
    RegistryTemplateVO mRegistryTemplateVO=null;
    if (manager != null) {
      mRegistryTemplateVO = manager.getRegistryTemplateData(pageName,siteId);
    }
    
//    mRegistryTemplateVO.getBrands().size();
   
    addObjectToAssert("pageTitle", mRegistryTemplateVO.getPageTitle());
    addObjectToAssert("pageHeaderCopy", mRegistryTemplateVO.getPageHeaderCopy());
    if(mRegistryTemplateVO.getBrands()!= null) {
    	brandSize = mRegistryTemplateVO.getBrands().size();
    }
  addObjectToAssert("brandImagesSize", brandSize);
  }
}
