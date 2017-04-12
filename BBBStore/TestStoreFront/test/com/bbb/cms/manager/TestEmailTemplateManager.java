package com.bbb.cms.manager;

import com.bbb.cms.EmailTemplateVO;
import com.bbb.email.EmailTemplateManager;
import java.util.HashMap;
import com.sapient.common.tests.BaseTestCase;

public class TestEmailTemplateManager extends BaseTestCase{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testEmailService() throws Exception {
		EmailTemplateManager manager = (EmailTemplateManager) getObject("manager");

	    String emailType = (String) getObject("emailType");
	    String siteId = (String) getObject("siteId"); 
	    
	    HashMap placeHolderValues = new HashMap();
	    placeHolderValues.put("emailType", emailType);
	    placeHolderValues.put("frmData_siteId", siteId);
	    
	    EmailTemplateVO mEmailTemplateVO=null;
	    if (manager != null) {
	      mEmailTemplateVO = manager.getEmailTemplateData(placeHolderValues);
	    }
	   
	    addObjectToAssert("emailHeader", mEmailTemplateVO.getEmailHeader());
	    addObjectToAssert("emailBody", mEmailTemplateVO.getEmailBody());
	    addObjectToAssert("emailFooter", mEmailTemplateVO.getEmailFooter());

	  }
}
