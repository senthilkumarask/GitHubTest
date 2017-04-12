package com.bbb.browse

import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.profile.session.BBBSessionBean;
import javax.servlet.ServletOutputStream

import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class to unit test the BBBCaptchaServlet (droplet)
 *
 */
public class BBBCaptchaServletSpecification extends BBBExtendedSpec {
	
	private BBBCaptchaServlet captchaServlet
	
	def setup() {
		
		captchaServlet = new BBBCaptchaServlet()
		
	}

	def "displaying captcha successfully " () {
		
		given : 
		
		BBBSessionBean sessionBeanMock = Mock()
		ServletOutputStream servletOutputStreamMock = Mock()
		
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		responseMock.getOutputStream() >> servletOutputStreamMock
		//sessionBeanMock.setCaptcha(_)
		
		expect : 
		
		captchaServlet.service(requestMock, responseMock)
	}
	
}
