package com.bbb.commerce.giftregistry.formhandler;



import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandlerDoColink extends BaseTestCase
{
	
	
	public void testDoCoLink() throws Exception{
		
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		String pSiteId = (String) getObject("siteId");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());
		String email = (String) getObject("email");
		String password = (String) getObject("password");
		BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
		bbbProfileFormHandler.setProfileTools(bbbProfileTools);
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		bbbProfileFormHandler.doCoRegLinking(email, getRequest(), getResponse(), pSiteId);
		assertNotNull(bbbProfileFormHandler.getFormExceptions().size());
			
	}
}
