package com.bbb.cms.email;

import java.util.Locale;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import atg.servlet.RequestLocale;

import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestEmailAPageFormHandler extends BaseTestCase{
	
	public void testHandleSendMailSuccess() throws Exception
	{
		EmailAPageFormHandler emailAPageFormHandler = (EmailAPageFormHandler) getObject("emailAPageFormHandler");
		Captcha captcha =new Captcha.Builder(300, 100)
		.addText()
		.addBackground(new GradiatedBackgroundProducer())
		.gimp()
		.addNoise()
		.addBorder()
		.build();
		emailAPageFormHandler.setLoggingDebug(true);
		emailAPageFormHandler.setRecipientEmail("apanwar2@sapient.com");
		emailAPageFormHandler.setRecipientName("TEST");
		emailAPageFormHandler.setSenderEmail("apanwar2@sapient.com");
		emailAPageFormHandler.setErrorURL("Error page");
		emailAPageFormHandler.setSuccessURL("success");
		emailAPageFormHandler.setCaptchaAnswer(captcha.getAnswer());
		emailAPageFormHandler.setPageTitle("www.abc.com/RegistryChecklist");
		emailAPageFormHandler.setSiteId("BedBathUS");
//		getRequest().getSession().setAttribute("simpleCaptcha", captcha);
		BBBSessionBean sessionBean = (BBBSessionBean) getRequest().resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		sessionBean.setCaptcha(captcha);
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		emailAPageFormHandler.handleSend(getRequest(), getResponse());
		boolean result = emailAPageFormHandler.getFormError();
		assertFalse(result);
	}
}
