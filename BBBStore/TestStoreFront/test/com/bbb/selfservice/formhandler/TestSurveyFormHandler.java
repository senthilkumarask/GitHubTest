package com.bbb.selfservice.formhandler;

import nl.captcha.Captcha;
import nl.captcha.Captcha.Builder;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestSurveyFormHandler extends BaseTestCase{
	
	public void testHandleRequestInfo() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		SurveyFormHandler surveyFormHandler = (SurveyFormHandler) getObject("surveyFormHandler");
		surveyFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		surveyFormHandler.setEmail((String)getObject("emailAddr"));
		surveyFormHandler.setUserName((String)getObject("userName"));
		surveyFormHandler.setSelectedGender((String)getObject("gender"));
		surveyFormHandler.setSelectedAge((String)getObject("age"));
		surveyFormHandler.setLocation((String)getObject("location"));
		surveyFormHandler.setEmailRequired((String)getObject("emailRequired"));
		surveyFormHandler.setOtherMessage((String)getObject("otherMessage"));
		surveyFormHandler.setWebSiteMessage((String)getObject("webSiteoMessage"));
		surveyFormHandler.setFeaturesMesssage((String)getObject("featureMessage"));
		surveyFormHandler.setEverShopped((String)getObject("everShopped"));
		
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
		Captcha captcha = new Builder(11, 11).build();
		sessionBean.setCaptcha(captcha);
		surveyFormHandler.setCaptchaAnswer(captcha.getAnswer().toString());

		surveyFormHandler.handleRequestInfo(pRequest, pResponse);
		
		assertTrue("should give error in tibco service calling", surveyFormHandler.isSuccessMessage());
		
	}
	public void testHandleInvalidRequestInfo() throws Exception {
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		ServletUtil.setCurrentRequest(this.getRequest());
		String siteId = "BuyBuyBaby";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		SurveyFormHandler surveyFormHandler = (SurveyFormHandler) getObject("surveyFormHandler");
		surveyFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		surveyFormHandler.setEmail((String)getObject("emailAddr"));
		surveyFormHandler.setUserName((String)getObject("userName"));
		surveyFormHandler.setSelectedGender((String)getObject("gender"));
		surveyFormHandler.setSelectedAge((String)getObject("age"));
		surveyFormHandler.setLocation((String)getObject("location"));
		surveyFormHandler.setEmailRequired((String)getObject("emailRequired"));
		surveyFormHandler.setOtherMessage((String)getObject("otherMessage"));
		surveyFormHandler.setWebSiteMessage((String)getObject("webSiteoMessage"));
		surveyFormHandler.setFeaturesMesssage((String)getObject("featureMessage"));
		surveyFormHandler.setEverShopped((String)getObject("everShopped"));
		
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
		Captcha captcha = new Builder(11, 11).build();
		sessionBean.setCaptcha(captcha);
		surveyFormHandler.setCaptchaAnswer("Test");

		surveyFormHandler.handleRequestInfo(pRequest, pResponse);
		
		assertFalse("should give error in tibco service calling", surveyFormHandler.isSuccessMessage());
		
	}
	
}
