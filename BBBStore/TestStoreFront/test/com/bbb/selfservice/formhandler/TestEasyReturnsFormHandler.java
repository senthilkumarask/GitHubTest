package com.bbb.selfservice.formhandler;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestEasyReturnsFormHandler extends BaseTestCase {

	public void testHandleGenerateLabel() throws Exception {
		
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		ServletUtil.setCurrentRequest(this.getRequest());
		EasyReturnsFormHandler easyFormHandler = (EasyReturnsFormHandler) getObject("easyReturnsFormHandler");		
		easyFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}		
		easyFormHandler.setFirstName("XXXX");
		easyFormHandler.setLastName("YYYY");
		easyFormHandler.setCompany("Bed Bath Beyond");
		easyFormHandler.setAddress1("31 Parker Rd");
		easyFormHandler.setShipfromcity("Edison");
		easyFormHandler.setState("NJ");
		easyFormHandler.setPostalcode("08820");
		easyFormHandler.setNumboxes(1);
		easyFormHandler.setEmailaddress("test@Stest1.com");
		easyFormHandler.setBasePhoneERF1("732");
		easyFormHandler.setBasePhoneERF2("096");
		easyFormHandler.setBasePhoneERF3("5841");
		easyFormHandler.setShiptorma("BBB123455");
		easyFormHandler.handleGenerateLabel(pRequest,pResponse);
		boolean error = easyFormHandler.getFormError();
		assertTrue(error);
	}

}
