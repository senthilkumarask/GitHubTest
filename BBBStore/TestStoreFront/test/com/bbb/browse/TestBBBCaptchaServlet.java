package com.bbb.browse;



import java.io.IOException;

import javax.servlet.ServletException;

import nl.captcha.Captcha;

import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBCaptchaServlet extends BaseTestCase{

	public void testBBBCaptchaServlet() throws  ServletException, IOException
	{
		BBBCaptchaServlet captchaServlet = (BBBCaptchaServlet) getObject("bbbCaptchaServlet");	
	
		captchaServlet.service(getRequest(), getResponse());
		BBBSessionBean bbbSessionBean = (BBBSessionBean) getRequest().resolveName("/com/bbb/profile/session/SessionBean");
		Captcha captcha = bbbSessionBean.getCaptcha();
		assertNotNull(captcha);
	}
}
