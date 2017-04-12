package com.bbb.selfservice.formhandler;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.constants.BBBCoreConstants.BRIDAL_REQUEST_TYPE;
import com.bbb.constants.BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;



import com.bbb.selfservice.tibco.vo.BridalBookVO;
import com.bbb.selfservice.tibco.vo.TellAFriendVO;
import com.sapient.common.tests.BaseTestCase;

public class TestBridalBookFormHandler extends BaseTestCase{
	
	public void testHandleBridalBookRequest() throws Exception {
		
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		ServletUtil.setCurrentRequest(this.getRequest());
		BridalBookFormHandler bridalBookFormHandler = (BridalBookFormHandler) getObject("bridalBookFormHandler");
		System.out.println("ERROR!!!"+bridalBookFormHandler.getFormError());
		bridalBookFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		
		BridalBookVO  bridalBookVO = new BridalBookVO();
		
		bridalBookVO.setEmailAddr((String)getObject("emailAddr"));
		bridalBookVO.setFirstName((String)getObject("firstName"));
		bridalBookVO.setLastName((String)getObject("lastName"));
		bridalBookVO.setAddressLine1((String)getObject("addressLine1"));
		bridalBookVO.setAddressLine2((String)getObject("addressLine2"));
		bridalBookVO.setCity((String)getObject("city"));
		bridalBookVO.setState((String)getObject("state"));
		bridalBookVO.setZipcode((String)getObject("zipcode"));
		bridalBookVO.setPhoneNumber((String)getObject("phoneNumber"));
		bridalBookVO.setEmailOffer((Boolean)getObject("emailOffer"));
		bridalBookVO.setEventDate((Date)getObject("eventDate"));
		bridalBookVO.setSiteId(siteId);
		bridalBookVO.setType(BRIDAL_REQUEST_TYPE.TYPE_BRIDAL_BOOK_REGISTRATION);
		bridalBookFormHandler.setBridalBookVO(bridalBookVO);
		assertFalse("should give error in form validation", bridalBookFormHandler.getFormError());
		bridalBookFormHandler.handleBridalBookRequest(pRequest, pResponse);
		assertTrue("should give error in tibco service calling", bridalBookFormHandler.isSuccessMessage());
		
		bridalBookVO.setEmailAddr("");
		bridalBookVO.setFirstName("");
		bridalBookVO.setLastName("");
		bridalBookVO.setAddressLine1("");
		bridalBookVO.setAddressLine2("");
		bridalBookVO.setCity("");
		bridalBookVO.setState("");
		bridalBookVO.setZipcode("");
		bridalBookVO.setPhoneNumber("");
		bridalBookVO.setEmailOffer(false);
		bridalBookVO.setEventDate(new Date());
		bridalBookFormHandler.handleBridalBookRequest(pRequest, pResponse);
		boolean error = bridalBookFormHandler.getFormError();
		assertTrue(error);
		
		bridalBookFormHandler.getFormExceptions().clear();
		bridalBookVO.setEmailAddr("jiji@ff.com");
		bridalBookVO.setFirstName("q");
		bridalBookVO.setLastName("w");
		bridalBookVO.setAddressLine1("4");
		bridalBookVO.setAddressLine2("r");
		bridalBookVO.setCity("f");
		bridalBookVO.setState("g");
		bridalBookVO.setZipcode("d");
		bridalBookVO.setPhoneNumber("s");
		bridalBookVO.setEmailOffer(false);
		bridalBookVO.setEventDate(new Date());
		bridalBookFormHandler.handleBridalBookRequest(pRequest, pResponse);
		error = bridalBookFormHandler.getFormError();
		assertTrue(error);
		bridalBookFormHandler.getFormExceptions().clear();
	}

	
	public void testHandleTellAFriend() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BridalBookFormHandler bridalBookFormHandler = (BridalBookFormHandler) getObject("bridalBookFormHandler");
		bridalBookFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		String siteId = "BedBathUS";
		ServletUtil.setCurrentRequest(this.getRequest());
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		TellAFriendVO tellAFriendVO = new TellAFriendVO();
		tellAFriendVO.setSenderFirstName((String)getObject("senderFirstName"));
		tellAFriendVO.setSenderLastName((String)getObject("senderLastName"));
		tellAFriendVO.setSenderEmailAddr((String)getObject("senderEmailAddr"));
		tellAFriendVO.setRecipientFirstName((String)getObject("recipientFirstName"));
		tellAFriendVO.setRecipientLastName((String)getObject("recipientLastName"));
		tellAFriendVO.setRecipientEmailAddr((String)getObject("recipientEmailAddr"));
		tellAFriendVO.setSiteId(siteId);
		tellAFriendVO.setType(TELLAFRIEND_REQUEST_TYPE.TYPE_TELL_A_FRIEND);
		bridalBookFormHandler.setTellAFriendVO(tellAFriendVO);
		assertFalse("should give error in form validation for tell a friend", bridalBookFormHandler.getFormError());
		bridalBookFormHandler.handleTellAFriend(pRequest, pResponse);
//		assertFalse("should give error in tibco service calling for tell a friend", bridalBookFormHandler.isSuccessMessage());
		bridalBookFormHandler.getFormExceptions().clear();
	}

	
	public void testSendEmail() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		
		BridalBookFormHandler bridalBookFormHandler = (BridalBookFormHandler) getObject("bridalBookFormHandler");
		bridalBookFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		String siteId = "BedBathUS";
		ServletUtil.setCurrentRequest(this.getRequest());
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		TemplateEmailInfoImpl emailInfo = bridalBookFormHandler.getEmailInfo();
		
		TellAFriendVO tellAFriendVO = new TellAFriendVO();
		tellAFriendVO.setSenderFirstName((String)getObject("senderFirstName"));
		tellAFriendVO.setSenderLastName((String)getObject("senderLastName"));
		tellAFriendVO.setSenderEmailAddr((String)getObject("senderEmailAddr"));
		tellAFriendVO.setRecipientFirstName((String)getObject("recipientFirstName"));
		tellAFriendVO.setRecipientLastName((String)getObject("recipientLastName"));
		tellAFriendVO.setRecipientEmailAddr((String)getObject("recipientEmailAddr"));
		tellAFriendVO.setSiteId(siteId);
		tellAFriendVO.setType(TELLAFRIEND_REQUEST_TYPE.TYPE_TELL_A_FRIEND);
		tellAFriendVO.setEmailCopy((Boolean)getObject("emailCopy"));
		String templateUrl = (String)getObject("templateUrl");
		emailInfo.setTemplateURL(templateUrl);
		
		String mailSubject = (String)getObject("mailSubject");
		
		Map<String,String> emailParams = new HashMap<String,String>();
		emailParams.put("senderFirstName", tellAFriendVO.getSenderFirstName());
		emailParams.put("senderLastName", tellAFriendVO.getSenderLastName());
		emailParams.put("senderEmail", tellAFriendVO.getSenderEmailAddr());
		emailParams.put("recipientFirstName", tellAFriendVO.getRecipientFirstName());
		emailParams.put("recipientLastName", tellAFriendVO.getRecipientLastName());
		emailParams.put("recipientEmail", tellAFriendVO.getRecipientEmailAddr());
		emailParams.put("templateUrl", templateUrl);
		emailParams.put("mailSubject", mailSubject);
		
		emailInfo.setTemplateParameters(emailParams);
		emailInfo.setMailingId(tellAFriendVO.getSenderEmailAddr());
		emailInfo.setMessageTo(tellAFriendVO.getRecipientEmailAddr());
		emailInfo.setSiteId(siteId);
		
		
		assertFalse("should give error in form validation for tell a friend", bridalBookFormHandler.getFormError());
		bridalBookFormHandler.handleTellAFriend(pRequest, pResponse);

		
		
		tellAFriendVO.setSenderFirstName("");
		tellAFriendVO.setSenderLastName("");
		tellAFriendVO.setSenderEmailAddr("");
		tellAFriendVO.setRecipientFirstName("");
		tellAFriendVO.setRecipientLastName("");
		tellAFriendVO.setRecipientEmailAddr("");
		tellAFriendVO.setSiteId(siteId);
		tellAFriendVO.setType(TELLAFRIEND_REQUEST_TYPE.TYPE_TELL_A_FRIEND);
		tellAFriendVO.setEmailCopy((Boolean)getObject("emailCopy"));
		
		emailInfo.setTemplateParameters(emailParams);
		emailInfo.setMailingId(tellAFriendVO.getSenderEmailAddr());
		emailInfo.setMessageTo(tellAFriendVO.getRecipientEmailAddr());
		emailInfo.setSiteId(siteId);
		bridalBookFormHandler.setTellAFriendVO(tellAFriendVO);
		bridalBookFormHandler.handleTellAFriend(pRequest, pResponse);
//		assertFalse("should give error in tibco service calling for tell a friend", bridalBookFormHandler.isSuccessMessage());
		boolean error = bridalBookFormHandler.getFormError();
		assertTrue(error);
		bridalBookFormHandler.getFormExceptions().clear();
		
	}
	
}
