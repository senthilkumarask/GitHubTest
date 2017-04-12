package com.bbb.cms.manager;

 

import com.bbb.cms.LandingTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestLandingManager extends BaseTestCase {

  public void testLandingService() throws Exception {
    LandingTemplateManager manager = (LandingTemplateManager) getObject("manager");

    String pageName = (String) getObject("pageName");
    //String siteId = (String) getObject("siteId");
    String categoryId = (String) getObject("categoryId");
    String siteId = (String) getObject("siteId");
    
    
    LandingTemplateVO mLandingTemplateVO=null;
    //DynamoHttpServletRequest request = getRequest();
    if (manager != null) {
      mLandingTemplateVO = manager.getLandingTemplateData(pageName,categoryId,siteId);
    }
    mLandingTemplateVO.getHeroImages().size();
   
    addObjectToAssert("pageTitle", mLandingTemplateVO.getPageTitle());
     
    addObjectToAssert("heroImagesSize", mLandingTemplateVO.getHeroImages().size());
  }
}
