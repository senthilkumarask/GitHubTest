package com.bbb.browse;

import java.util.Locale;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import atg.servlet.GenericHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

import com.sapient.common.tests.BaseTestCase;
import com.bbb.browse.EmailAFriendFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.session.BBBSessionBean;

public class TestEmailAFriendFormHandler extends BaseTestCase{
	
	public void testHandleSendMailSuccess() throws Exception
	{
		EmailAFriendFormHandler emailAFriendFormHandler = (EmailAFriendFormHandler) getObject("emailAFriendFormHandler");
		Captcha captcha =new Captcha.Builder(300, 100)
		.addText()
		.addBackground(new GradiatedBackgroundProducer())
		.gimp()
		.addNoise()
		.addBorder()
		.build();
		emailAFriendFormHandler.setLoggingDebug(true);
		emailAFriendFormHandler.setRecipientEmail("agupta41@sapient.com");
		emailAFriendFormHandler.setRecipientName("TEST");
		emailAFriendFormHandler.setSenderEmail("agupta41@sapient.com");
		emailAFriendFormHandler.setErrorURL("Error page"); // error url should not be empty else next call will always return true
		emailAFriendFormHandler.setSuccessURL("success");
		emailAFriendFormHandler.setCaptchaAnswer(captcha.getAnswer());
		getRequest().getSession().setAttribute("simpleCaptcha", captcha);
		emailAFriendFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.REFERRER,"CurrentPage");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(genericHttpServletRequest);

		ServletUtil.setCurrentRequest(this.getRequest());
		emailAFriendFormHandler.getTemplateUrl();
		emailAFriendFormHandler.setCurrentSite("BuyBuyBaby");
		emailAFriendFormHandler.setCurrentPageURL("CurrentPage");
		emailAFriendFormHandler.getProductIdParamName();
		emailAFriendFormHandler.getSubjectParamName();
		emailAFriendFormHandler.getLocaleParamName();
		emailAFriendFormHandler.getSuccessURL();
		
		emailAFriendFormHandler.setServerName("");
		emailAFriendFormHandler.setContextPath("/");
		emailAFriendFormHandler.setSubject("subject");
		emailAFriendFormHandler.getSuccessURL();
		emailAFriendFormHandler.setSuccessURL("");
		emailAFriendFormHandler.setErrorURL("");
		emailAFriendFormHandler.setSenderName("Rajesh");
		emailAFriendFormHandler.getSiteId();
		emailAFriendFormHandler.setSiteId("BuyBuyBaby");
		emailAFriendFormHandler.setMessage("message");
		emailAFriendFormHandler.getProfileTools();
		emailAFriendFormHandler.getCatalogTools();
		emailAFriendFormHandler.getProfile();
		emailAFriendFormHandler.getSubjectParamName();
		emailAFriendFormHandler.setSubjectParamName("");
		emailAFriendFormHandler.getActionResult();
		
		
		BBBSessionBean sessionBean = (BBBSessionBean) getRequest().resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		sessionBean.setCaptcha(captcha);
		emailAFriendFormHandler.collectParams(getRequest());
		emailAFriendFormHandler.handleSend(getRequest(), getResponse());
		boolean result = emailAFriendFormHandler.getFormError();
		assertFalse(result); // result is true if there is no form error
		emailAFriendFormHandler.processException(new Throwable(), "123", getRequest(),getResponse());
	}
}
