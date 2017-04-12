package com.bbb.cms.manager;

 

import atg.repository.RepositoryItem;

import com.sapient.common.tests.BaseTestCase;

public class TestStaticTemplateManager extends BaseTestCase {

  public void testStaticJspService() throws Exception {
	  StaticTemplateManager manager = (StaticTemplateManager) getObject("manager");

	  RepositoryItem staticTemplate =null;
    
    String siteId = (String) getObject("siteId");
    String pageName = (String) getObject("pageName");
   
    if (manager != null) {
      staticTemplate  = manager.getStaticTemplateData(siteId, pageName, "false");
    }

    String property = null;
    if(staticTemplate != null){
    	property = (String)staticTemplate.getPropertyValue("pageTitle");
    }
    
	addObjectToAssert("staticTemplateResults", property);
    
  }
  
}
