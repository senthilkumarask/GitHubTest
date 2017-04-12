package com.bbb.selfservice.contactus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nl.captcha.Captcha;
import nl.captcha.Captcha.Builder;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.formhandler.ContactUsFormHandler;
import com.bbb.selfservice.manager.ContactUsManager;
import com.bbb.selfservice.tibco.vo.ContactUsVO;
import com.bbb.selfservice.tools.ContactUsTools;
import com.sapient.common.tests.BaseTestCase;

public class TestContactUsFormHandler extends BaseTestCase{
	
	public void testHandleRequestInfo() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(getRequest());
		DynamoHttpServletResponse pResponse = getResponse();
		ContactUsFormHandler contactUsFormHandler = (ContactUsFormHandler) getObject("contactUsFormHandler");
		contactUsFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		String siteId = "BedBathUS";
		contactUsFormHandler.setSubjectCategory((String)getObject("subjectCategory"));
		contactUsFormHandler.setOrderNumber((String)getObject("orderNumber"));
		contactUsFormHandler.setEmailMessage((String)getObject("emailMessage"));
		contactUsFormHandler.setFirstName((String)getObject("firstName"));
		contactUsFormHandler.setLastName((String)getObject("firstName"));
		contactUsFormHandler.setContactType((String)getObject("contactType"));
		contactUsFormHandler.setEmail((String)getObject("email"));
		contactUsFormHandler.setConfirmEmail((String)getObject("confirmemail"));
		contactUsFormHandler.setPhoneNumber((String)getObject("phoneNumber"));
		contactUsFormHandler.setPhoneExt((String)getObject("phoneExt"));
		contactUsFormHandler.setSelectedTimeZone((String)getObject("selectedTimeZone"));
		contactUsFormHandler.setSelectedTimeCall((String)getObject("selectedTimeCall"));
		contactUsFormHandler.setAmPM((String)getObject("amPM"));
		
		ContactUsVO contactUsVO = new ContactUsVO();
		contactUsVO.setSubjectCategory((String)getObject("subjectCategory"));
		contactUsVO.setEmailMessage((String)getObject("emailMessage"));
		contactUsVO.setContactType((String)getObject("contactType"));
		contactUsVO.setEmail((String)getObject("email"));
		contactUsVO.setFirstName((String)getObject("firstName"));
		contactUsVO.setLastName((String)getObject("lastName"));
		contactUsVO.setGender((String)getObject("gender"));
		contactUsVO.setPhoneNumber((String)getObject("phoneNumber"));
		contactUsVO.setPhoneExt((String)getObject("phoneExt"));
		contactUsVO.setOrderNumber((String)getObject("orderNumber"));
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append((String)getObject("selectedTimeCall"));
		stringBuilder.append(BBBCoreConstants.SPACE);
		stringBuilder.append((String)getObject("amPM"));
		contactUsVO.setTimeCall(stringBuilder.toString());
		contactUsVO.setTimeZone((String)getObject("selectedTimeZone"));
		Calendar date = Calendar.getInstance();
		SimpleDateFormat dateformatter = new SimpleDateFormat(
				BBBCoreConstants.DATE_FORMAT1);
		String pSubmitDate = dateformatter.format(date.getTime());
		contactUsVO.setSubmitDate(pSubmitDate);
		contactUsVO.setSiteFlag(siteId);
				
		assertFalse("should give error in form validation", contactUsFormHandler.getFormError());
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
		Captcha captcha = new Builder(11, 11).build();
		sessionBean.setCaptcha(captcha);
		contactUsFormHandler.setCaptchaAnswer(captcha.getAnswer().toString());
		
		contactUsFormHandler.handleRequestInfo(pRequest, pResponse);
		
		ContactUsManager contactUsManager=(ContactUsManager)Nucleus.getGlobalNucleus().resolveName("/com/bbb/selfservice/ContactUsManager");
		addObjectToAssert("customerCareEmailAddress", contactUsManager.getcustomerCareEmailAddress(siteId));
		assertFalse("Error in Sending Tibco Email", contactUsFormHandler.getFormError());
		
		ContactUsTools contactUsTools  = (ContactUsTools)Nucleus.getGlobalNucleus().resolveName("/com/bbb/selfservice/ContactUsTools");
		contactUsTools.isSendEmailInSeparateThread();
		contactUsTools.isPersistEmails();
		contactUsTools.getTemplateEmailSender();
		contactUsTools.getSiteContext();
	}
}
