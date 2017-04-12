package com.bbb.cms.manager;

import java.util.Map;

import atg.servlet.DynamoHttpServletRequest;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.sapient.common.tests.BaseTestCase;

public class TestLblTxtManager extends BaseTestCase {

  public void testLableValueService() throws Exception {
    LblTxtTemplateManager manager = (LblTxtTemplateManager) getObject("manager");

    String labelId = (String) getObject("labelId");
    String siteId = (String) getObject("siteId");
    Map<String, String> placeHolder = (Map<String, String>) getObject("placeHolder");
    String label = "";
    DynamoHttpServletRequest request = getRequest();
    if (manager != null) {
      label = manager.getPageLabel(labelId, request.getLocale().getLanguage(), placeHolder, siteId);

    }
    addObjectToAssert("labelValue", label);
  }

  public void testTextAreaService() throws Exception {
    LblTxtTemplateManager manager = (LblTxtTemplateManager) getObject("manager");

    String labelId = (String) getObject("labelId");
    String siteId = (String) getObject("siteId");
    @SuppressWarnings("unchecked")
    Map<String, String> placeHolder = (Map<String, String>) getObject("placeHolder");
    String textArea = "";
    DynamoHttpServletRequest request = getRequest();
    if (manager != null) {
      
      textArea = manager.getPageTextArea(labelId, request.getLocale().getLanguage(), placeHolder, siteId);
    }
    addObjectToAssert("textArea", textArea);
  }
  
  public void testErrorService() throws Exception {
    LblTxtTemplateManager manager = (LblTxtTemplateManager) getObject("manager");

    String labelId = (String) getObject("labelId");
    String siteId = (String) getObject("siteId");
    @SuppressWarnings("unchecked")
    Map<String, String> placeHolder = (Map<String, String>) getObject("placeHolder");
    String errorText = "";
    DynamoHttpServletRequest request = getRequest();
    if (manager != null) {
      
      errorText = manager.getErrMsg(labelId, request.getLocale().getLanguage(), placeHolder, siteId);
    }
    addObjectToAssert("error", errorText);
  }
}
