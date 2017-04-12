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

import com.bbb.constants.BBBCoreConstants.BABY_BOOK_REQUEST_TYPE;
import com.bbb.constants.BBBCoreConstants.TELLAFRIEND_REQUEST_TYPE;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;



import com.bbb.selfservice.tibco.vo.BabyBookVO;
import com.bbb.selfservice.tibco.vo.TellAFriendVO;
import com.sapient.common.tests.BaseTestCase;

public class TestBabyBookFormHandler extends BaseTestCase{
	
	public void testHandleBabyBookRequest() throws Exception {
		
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BabyBookFormHandler babyBookFormHandler = (BabyBookFormHandler) getObject("babyBookFormHandler");
		babyBookFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		String siteId = "BuyBuyBaby";
		ServletUtil.setCurrentRequest(this.getRequest());
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		BabyBookVO  babyBookVO = new BabyBookVO();
		
		babyBookVO.setEmailAddr((String)getObject("emailAddr"));
		babyBookVO.setFirstName((String)getObject("firstName"));
		babyBookVO.setLastName((String)getObject("lastName"));
		babyBookVO.setAddressLine1((String)getObject("addressLine1"));
		babyBookVO.setAddressLine2((String)getObject("addressLine2"));
		babyBookVO.setCity((String)getObject("city"));
		babyBookVO.setState((String)getObject("state"));
		babyBookVO.setZipcode((String)getObject("zipcode"));
		babyBookVO.setPhoneNumber((String)getObject("phoneNumber"));
		babyBookVO.setEmailOffer((Boolean)getObject("emailOffer"));
		babyBookVO.setEventDate((Date)getObject("eventDate"));
		babyBookVO.setSiteId(siteId);
		babyBookVO.setType(BABY_BOOK_REQUEST_TYPE.TYPE_BABY_BOOK_REGISTRATION);
		babyBookFormHandler.setBabyBookVO(babyBookVO);
		assertFalse("should give error in form validation", babyBookFormHandler.getFormError());
		babyBookFormHandler.handleBabyBookRequest(pRequest, pResponse);
		//assertFalse("should give error in tibco service calling", babyBookFormHandler.isSuccessMessage());
		
		babyBookVO.setEmailAddr("");
		babyBookVO.setFirstName("");
		babyBookVO.setLastName("");
		babyBookVO.setAddressLine1("");
		babyBookVO.setAddressLine2("");
		babyBookVO.setCity("");
		babyBookVO.setState("");
		babyBookVO.setZipcode("");
		babyBookVO.setPhoneNumber("");
		babyBookVO.setEmailOffer(false);
		babyBookVO.setEventDate(new Date());
		babyBookFormHandler.handleBabyBookRequest(pRequest, pResponse);
		boolean error = babyBookFormHandler.getFormError();
		assertTrue(error);
		
		babyBookFormHandler.getFormExceptions().clear();
		babyBookVO.setEmailAddr("jiji@ff.com");
		babyBookVO.setFirstName("q");
		babyBookVO.setLastName("w");
		babyBookVO.setAddressLine1("4");
		babyBookVO.setAddressLine2("r");
		babyBookVO.setCity("f");
		babyBookVO.setState("g");
		babyBookVO.setZipcode("d");
		babyBookVO.setPhoneNumber("s");
		babyBookVO.setEmailOffer(false);
		babyBookVO.setEventDate(new Date());
		babyBookFormHandler.handleBabyBookRequest(pRequest, pResponse);
		error = babyBookFormHandler.getFormError();
		assertTrue(error);
		babyBookFormHandler.getFormExceptions().clear();
	}

	
	public void testHandleTellAFriend() throws Exception {
		Map<String, String> errorMap = null;
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BabyBookFormHandler babyBookFormHandler = (BabyBookFormHandler) getObject("babyBookFormHandler");
		babyBookFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		String siteId = "BuyBuyBaby";
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
		babyBookFormHandler.setTellAFriendVO(tellAFriendVO);
		assertFalse("should give error in form validation for tell a friend", babyBookFormHandler.getFormError());
		babyBookFormHandler.handleTellAFriend(pRequest, pResponse);
		//assertFalse("should give error in tibco service calling for tell a friend", babyBookFormHandler.isSuccessMessage());
		babyBookFormHandler.getFormExceptions().clear();
	}

	
public void testHandleSendEmail() throws Exception {
		Map<String, String> errorMap = null;
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		
		BabyBookFormHandler babyBookFormHandler = (BabyBookFormHandler) getObject("babyBookFormHandler");
		babyBookFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		String siteId = "BuyBuyBaby";
		ServletUtil.setCurrentRequest(this.getRequest());
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		TemplateEmailInfoImpl emailInfo = babyBookFormHandler.getEmailInfo();
		
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
		
		
		assertFalse("should give error in form validation for tell a friend", babyBookFormHandler.getFormError());
		babyBookFormHandler.handleTellAFriend(pRequest, pResponse);
		//assertFalse("should give error in tibco service calling for tell a friend", babyBookFormHandler.isSuccessMessage());
		
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
		babyBookFormHandler.setTellAFriendVO(tellAFriendVO);
		babyBookFormHandler.handleTellAFriend(pRequest, pResponse);
		//assertFalse("should give error in tibco service calling for tell a friend", babyBookFormHandler.isSuccessMessage());
		boolean error = babyBookFormHandler.getFormError();
		assertTrue(error);
		babyBookFormHandler.getFormExceptions().clear();
		
	}
	
}
