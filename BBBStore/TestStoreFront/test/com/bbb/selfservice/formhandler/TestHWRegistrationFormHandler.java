package com.bbb.selfservice.formhandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants.BABY_BOOK_REQUEST_TYPE;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.tibco.vo.HWRegistrationVO;
import com.sapient.common.tests.BaseTestCase;

public class TestHWRegistrationFormHandler extends BaseTestCase {

	public void testHandleHWRegistrationRequest() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		HWRegistrationFormHandler hwRegistrationFormHandler = (HWRegistrationFormHandler) getObject("hwRegistrationFormHandler");

		hwRegistrationFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		String siteId = "BedBathUS";

		HWRegistrationVO hwRegistrationVO = new HWRegistrationVO();
		hwRegistrationVO.setEmailAddr((String) getObject("emailAddr"));
		hwRegistrationVO.setFirstName((String) getObject("firstName"));
		hwRegistrationVO.setLastName((String) getObject("lastName"));
		hwRegistrationVO.setAddressLine1((String) getObject("addressLine1"));
		hwRegistrationVO.setAddressLine2((String) getObject("addressLine2"));
		hwRegistrationVO.setCity((String) getObject("city"));
		hwRegistrationVO.setState((String) getObject("state"));
		hwRegistrationVO.setZipcode((String) getObject("zipcode"));
		hwRegistrationVO.setEmailOffer((Boolean) getObject("emailOffer"));
		hwRegistrationVO.setSiteId(siteId);
		hwRegistrationVO.setType(BABY_BOOK_REQUEST_TYPE.TYPE_BABY_BOOK_REGISTRATION);
		hwRegistrationFormHandler.setHwRegistrationVO(hwRegistrationVO);
		assertFalse("should give error in form validation",	hwRegistrationFormHandler.getFormError());
		hwRegistrationFormHandler.handleHWRegistrationRequest(pRequest,	pResponse);
		assertFalse("should give error in tibco service calling", hwRegistrationFormHandler.isSuccessMessage());
	}

}
