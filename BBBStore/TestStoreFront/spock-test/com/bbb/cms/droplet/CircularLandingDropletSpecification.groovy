package com.bbb.cms.droplet

import spock.lang.specification.BBBExtendedSpec

import com.bbb.cms.LandingTemplateVO;
import com.bbb.cms.manager.LandingTemplateManager
import com.bbb.constants.BBBCmsConstants

class CircularLandingDropletSpecification  extends BBBExtendedSpec {
	
	def LandingTemplateManager landingTemplateManagerMock= Mock()
	def CircularLandingDroplet testObj
	
	def setup(){
		testObj = Spy()
		testObj.setLandingTemplateManager(landingTemplateManagerMock)
	}

	def "when pageName and siteId are null in reguest parameter"(){
		given:
			String pageName= null;
			requestMock.getLocalParameter(BBBCmsConstants.PAGENAME) >> pageName
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("starting method CircularLandingDroplet")
		1* testObj.logDebug("Input Paramters : " + pageName)
		1* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock, responseMock)
		1* testObj.logDebug("Exiting method CircularLandingDroplet")
	}
	
	def "when pageName and siteId are not null in reguest parameter"(){
		given:
			String pageName= "homePage";
			String siteId="BedBathUs"
			String categoryId = BBBCmsConstants.NOT_REQUIRED
			LandingTemplateVO landingTemplateVO = new LandingTemplateVO()
			landingTemplateVO.setPageName(pageName)
			 	requestMock.getLocalParameter(BBBCmsConstants.PAGENAME) >> pageName
				requestMock.getLocalParameter(BBBCmsConstants.SITE_ID) >> siteId
			1* 	landingTemplateManagerMock.getLandingTemplateData(pageName, categoryId, siteId) >>landingTemplateVO
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("starting method CircularLandingDroplet")
		1* testObj.logDebug("Input Paramters : " + pageName)
		1* testObj.logDebug("Calling LandingTemplateManager : ")
		1* testObj.logDebug("Received LandingTemplateVO : " + landingTemplateVO)
		1* requestMock.setParameter(BBBCmsConstants.LANDING_TEMPLATE_VO, landingTemplateVO);
		1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT,requestMock, responseMock)
		1* testObj.logDebug("Exiting method CircularLandingDroplet")
	}
	def "when pageName and siteId are not null in reguest parameter and landingTemplateVO is null "(){
		given:
			String pageName= "homePage";
			String siteId="BedBathUs"
			String categoryId = BBBCmsConstants.NOT_REQUIRED
			LandingTemplateVO landingTemplateVO = null
				 requestMock.getLocalParameter(BBBCmsConstants.PAGENAME) >> pageName
				requestMock.getLocalParameter(BBBCmsConstants.SITE_ID) >> siteId
			1* 	landingTemplateManagerMock.getLandingTemplateData(pageName, categoryId, siteId) >>landingTemplateVO
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("starting method CircularLandingDroplet")
		1* testObj.logDebug("Input Paramters : " + pageName)
		1* testObj.logDebug("Calling LandingTemplateManager : ")
		1* testObj.logDebug("Received LandingTemplateVO as Null: ");
		1* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock, responseMock)
		1* testObj.logDebug("Exiting method CircularLandingDroplet")
	}
}
