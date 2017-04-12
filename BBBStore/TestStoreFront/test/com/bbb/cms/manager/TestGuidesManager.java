package com.bbb.cms.manager;

 

import java.util.Iterator;
import java.util.List;

import com.bbb.cms.GuidesTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestGuidesManager extends BaseTestCase {

  public void testGuidesService() throws Exception {
    GuidesTemplateManager manager = (GuidesTemplateManager) getObject("manager");

    String imageAltText=null;
    String title=null;
  
    
    String contentType = (String) getObject("contentType");
    String siteId = (String) getObject("siteId");
    String guidesCategory = (String) getObject("guidesCategory");

    List<GuidesTemplateVO> lstGuidesTemplate=null;
    //DynamoHttpServletRequest request = getRequest();
    if (manager != null) {
      lstGuidesTemplate = manager.getGuidesTemplateData(contentType,guidesCategory,siteId);
    }

    Iterator it=lstGuidesTemplate.iterator();
    while(it.hasNext()){
      GuidesTemplateVO guidesTemplateVO =(GuidesTemplateVO)it.next();
      imageAltText=guidesTemplateVO.getImageAltText();
      title=guidesTemplateVO.getTitle();
    }

    addObjectToAssert("imageAltText",imageAltText);
    addObjectToAssert("title",title);
      
    addObjectToAssert("lstGuidesSize", lstGuidesTemplate.size());
  }
  
  public void testGuidesLongDescService() throws Exception {
    GuidesTemplateManager manager = (GuidesTemplateManager) getObject("manager");
    String guideId = (String) getObject("guideId");
   GuidesTemplateVO guidesTemplateVO=null;
   
    if (manager != null) {
      guidesTemplateVO = manager.getGuidesLongDescription(guideId);
    }
    addObjectToAssert("title",guidesTemplateVO.getTitle());
    addObjectToAssert("longDescription", guidesTemplateVO.getLongDescription());
  }
  
  
  
  
  
}
