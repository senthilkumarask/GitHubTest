package com.bbb.commerce.giftregistry.droplet

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.cms.ManageRegistryChecklistVO;
import com.bbb.cms.manager.ContentTemplateManager
import com.bbb.cms.vo.CMSResponseVO
import com.bbb.commerce.checklist.manager.CheckListManager
import com.bbb.commerce.checklist.vo.NonRegistryGuideVO
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean
import com.bbb.repository.RepositoryItemMock
import com.bbb.utils.BBBUtility;

import javax.servlet.ServletException
import javax.servlet.http.Cookie

import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author kmagud
 *
 */
class ManageRegistryChecklistDropletSpecification extends BBBExtendedSpec {
	
	ManageRegistryChecklistDroplet testObj
	ContentTemplateManager contentTemplateManagerMock = Mock()
	GiftRegistryManager giftRegistryManagerMock = Mock()
	CheckListManager checkListManagerMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	CMSResponseVO cmsResponseVOMock = Mock()
	NonRegistryGuideVO nonRegistryGuideVOMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new ManageRegistryChecklistDroplet(contentTemplateManager:contentTemplateManagerMock,giftRegistryManager:giftRegistryManagerMock,
			checkListManager:checkListManagerMock)
	}
	
	////////////////////////////////////////TestCases for service --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) ///////////
	
	def"service method. This TC is when guideType is empty and regSummaryVO has value"(){
		given:
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> ""
			BBBSessionBean sessionBeanMock = new BBBSessionBean() 
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("2345654", _) >> registrySummaryVOMock
			RegistryTypes registryTypesMock = new RegistryTypes()
			registrySummaryVOMock.setRegistryType(registryTypesMock)
			registryTypesMock.setRegistryTypeName("subTypeCode")
			sessionBeanMock.setActivateGuideInRegistryRibbon(FALSE)
			
			RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
			RepositoryItemMock repositoryItem2 = new RepositoryItemMock()
			RepositoryItem[] repositoryItemList = [repositoryItem1,repositoryItem2]
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItem1.setProperties([links:[repositoryItemMock,repositoryItemMock1]])
			repositoryItem2.setProperties([links:[]])
			repositoryItemMock.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINK) >> "/url/bannerLink"
			repositoryItemMock.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_TEXT) >> "bannerLink"
			repositoryItemMock1.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINK) >> null
			repositoryItemMock1.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_TEXT) >> null
			
			CMSResponseVO cmsResponseVOMock = new CMSResponseVO(responseItems:repositoryItemList)
			1 * contentTemplateManagerMock.getContent(BBBCoreConstants.MANAGE_CHECKLIST_LINK, {registryType:"subTypeCode"}) >> cmsResponseVOMock 
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.CHANNEL, "DesktopWeb")
			1 * requestMock.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK, _)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, registrySummaryVOMock)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
			
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
	}
	
	def"service method. This TC is when guideType has value and fromAjax is true"(){
		given:
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> "guideType"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
			Cookie cookieMock = Mock()
			1 * checkListManagerMock.createorUpdateRegistryGuideCookie("guideType", requestMock, false) >> cookieMock
			NonRegistryGuideVO nonRegistryGuideVOMock1 = Mock()
			1 * nonRegistryGuideVOMock.getGuideTypeCode() >> "guide"
			1 * nonRegistryGuideVOMock1.getGuideTypeCode() >> "guideType"
 			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVOMock,nonRegistryGuideVOMock1])
			cmsResponseVOMock.getResponseItems() >> null
			1 * contentTemplateManagerMock.getContent(BBBCoreConstants.MANAGE_CHECKLIST_LINK, {registryType:"guideType"}) >> cmsResponseVOMock
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.CHANNEL, "DesktopWeb")
			1 * requestMock.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK, _)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null)
			1 * requestMock.setParameter(BBBCoreConstants.SELECTED_GUIDE_VO, nonRegistryGuideVOMock1)
			1 * responseMock.addCookie(cookieMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			sessionBeanMock.getValues().get("selectedGuideType").equals("guideType")
			
			testObj.getNonRegistryGuideVOs().equals([nonRegistryGuideVOMock,nonRegistryGuideVOMock1])
			testObj.isActivateGuideInRegistryRibbon().equals(TRUE)
			testObj.getSelectedGuideType().equals("guideType")
			
	}
	
	def"service method. This TC is when guideType and registryId is empty"(){
		given:
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> ""
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> ""
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, requestMock, responseMock)
	}
	
	def"service method. This TC is when nonRegistryGuideVOs is empty and responseVO is null"(){
		given:
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> "guideType"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
			Cookie cookieMock = Mock()
			1 * checkListManagerMock.createorUpdateRegistryGuideCookie("guideType", requestMock, false) >> cookieMock
			 sessionBeanMock.getValues().put("guideVoList",[])
			1 * contentTemplateManagerMock.getContent(BBBCoreConstants.MANAGE_CHECKLIST_LINK, {registryType:"guideType"}) >> null
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.CHANNEL, "DesktopWeb")
			1 * requestMock.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK, _)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null)
			1 * responseMock.addCookie(cookieMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			sessionBeanMock.getValues().get("selectedGuideType").equals("guideType")
			
			testObj.getNonRegistryGuideVOs().equals([])
			testObj.isActivateGuideInRegistryRibbon().equals(TRUE)
			testObj.getSelectedGuideType().equals("guideType")
			
	}
	
	def"service method. This TC is when nonRegistryGuideVOs is empty and ajax is FALSE"(){
		given:
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> "guideType"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> FALSE
			1 * contentTemplateManagerMock.getContent(BBBCoreConstants.MANAGE_CHECKLIST_LINK, {registryType:"guideType"}) >> null
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.CHANNEL, "DesktopWeb")
			1 * requestMock.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK, _)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			
	}
	
	def"service method. This TC is when RepositoryException thrown in getContent method"(){
		given:
			testObj = Spy()
			testObj.setContentTemplateManager(contentTemplateManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> ""
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("2345654", _) >> registrySummaryVOMock
			RegistryTypes registryTypesMock = new RegistryTypes()
			registrySummaryVOMock.setRegistryType(registryTypesMock)
			registryTypesMock.setRegistryTypeName("subTypeCode")
			sessionBeanMock.setActivateGuideInRegistryRibbon(TRUE)
			1 * contentTemplateManagerMock.getContent(BBBCoreConstants.MANAGE_CHECKLIST_LINK, {registryType:"subTypeCode"}) >> {throw new RepositoryException("Mock for RepositoryException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logDebug('Repository Exception ManageRegistryChecklistDroplet Stack Trace:atg.repository.RepositoryException: Mock for RepositoryException')
			1 * testObj.logError('Repository Exception ManageRegistryChecklistDropletMock for RepositoryException')
			1 * testObj.logDebug('Entering ManageRegistryChecklistDroplet : service with registryType : subTypeCode')
			1 * requestMock.setParameter(BBBCoreConstants.CHANNEL, "DesktopWeb")
			1 * requestMock.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK, _)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, registrySummaryVOMock)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
			
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown in getContent method and fromAjax is false"(){
		given:
			testObj = Spy()
			testObj.setContentTemplateManager(contentTemplateManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> ""
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> FALSE
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("2345654", _) >> registrySummaryVOMock
			RegistryTypes registryTypesMock = new RegistryTypes()
			registrySummaryVOMock.setRegistryType(registryTypesMock)
			registryTypesMock.setRegistryTypeName("subTypeCode")
			sessionBeanMock.setActivateGuideInRegistryRibbon(TRUE)
			1 * contentTemplateManagerMock.getContent(BBBCoreConstants.MANAGE_CHECKLIST_LINK, {registryType:"subTypeCode"}) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logDebug('BBBSystem Exception ManageRegistryChecklistDroplet Stack Trace:com.bbb.exception.BBBSystemException: Mock for BBBSystemException')
			1 * testObj.logError('BBBSystem Exception ManageRegistryChecklistDropletMock for BBBSystemException')
			1 * testObj.logDebug('Entering ManageRegistryChecklistDroplet : service with registryType : subTypeCode')
			1 * requestMock.setParameter(BBBCoreConstants.CHANNEL, "DesktopWeb")
			1 * requestMock.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK, _)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, registrySummaryVOMock)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
			
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
	}
	
	def"service method. This TC is when BBBBusinessException thrown in getContent method"(){
		given:
			testObj = Spy()
			testObj.setContentTemplateManager(contentTemplateManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> ""
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("2345654", _) >> registrySummaryVOMock
			RegistryTypes registryTypesMock = new RegistryTypes()
			registrySummaryVOMock.setRegistryType(registryTypesMock)
			registryTypesMock.setRegistryTypeName("subTypeCode")
			sessionBeanMock.setActivateGuideInRegistryRibbon(TRUE)
			1 * contentTemplateManagerMock.getContent(BBBCoreConstants.MANAGE_CHECKLIST_LINK, {registryType:"subTypeCode"}) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logDebug('BBBBusiness Exception ManageRegistryChecklistDroplet Stack Trace:com.bbb.exception.BBBBusinessException: Mock for BBBBusinessException')
			1 * testObj.logError('BBBBusiness Exception ManageRegistryChecklistDropletMock for BBBBusinessException')
			1 * testObj.logDebug('Entering ManageRegistryChecklistDroplet : service with registryType : subTypeCode')
			1 * requestMock.setParameter(BBBCoreConstants.CHANNEL, "DesktopWeb")
			1 * requestMock.setParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK, _)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, registrySummaryVOMock)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
			
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown in getRegistryInfo method"(){
		given:
			testObj = Spy()
			testObj.setContentTemplateManager(contentTemplateManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> ""
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
			1 * giftRegistryManagerMock.getRegistryInfo("2345654", _) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logDebug('Error Stack Trace:com.bbb.exception.BBBSystemException: Mock for BBBSystemException')
			1 * testObj.logError('BBBSystem Exception ManageRegistryChecklistDropletMock for BBBSystemException')
	}
	
	def"service method. This TC is when BBBBusinessException thrown in getRegistryInfo method"(){
		given:
			testObj = Spy()
			testObj.setContentTemplateManager(contentTemplateManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBCoreConstants.REGISTRY_ID) >> "2345654"
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >> ""
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getParameter("fromAjax") >> TRUE
			1 * giftRegistryManagerMock.getRegistryInfo("2345654", _) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logDebug('Error Stack Trace:com.bbb.exception.BBBBusinessException: Mock for BBBBusinessException')
			1 * testObj.logError('BBBBusiness Exception ManageRegistryChecklistDropletMock for BBBBusinessException')
	}
	
	////////////////////////////////////////TestCases for service --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getManageRegistryRibbon --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public ManageRegistryChecklistVO getManageRegistryRibbon(String registryId,String guideType,String fromAjax) ///////////

	def"getManageRegistryRibbon. This TC is the Happy flow of getManageRegistryRibbon method"(){
		given:
			String registryId = "232131231"
			String guideType = "guideType"
			String fromAjax = "true"
			ManageRegistryChecklistVO manageRegistryChecklistVOMock = new ManageRegistryChecklistVO()
			requestMock.getObjectParameter(BBBCoreConstants.MANAGE_CHECKLIST_LINK) >> manageRegistryChecklistVOMock
			testObj.setActivateGuideInRegistryRibbon(TRUE)
			testObj.setNonRegistryGuideVOs([nonRegistryGuideVOMock])
			testObj.setSelectedGuideType("guideType")
		
		when:
			ManageRegistryChecklistVO results = testObj.getManageRegistryRibbon(registryId, guideType, fromAjax)
		then:
			results.isActivateGuideInRegistryRibbon().equals(TRUE)
			results.getNonRegistryGuideVOs().equals([nonRegistryGuideVOMock])
			results.getSelectedGuideType().equals("guideType")
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, guideType)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_AJAX, fromAjax)
			1 * requestMock.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, requestMock, responseMock)
	}
	
	def"getManageRegistryRibbon. This TC is when ServletException thrown"(){
		given:
			testObj = Spy()
			String registryId = "232131231"
			String guideType = "guideType"
			String fromAjax = "true"
			1 * testObj.service(requestMock, responseMock) >> {throw new ServletException("Mock for ServletException")}
		
		when:
			ManageRegistryChecklistVO results = testObj.getManageRegistryRibbon(registryId, guideType, fromAjax)
		then:
			results == null
			1 * testObj.logError('ManageRegistryChecklistDroplet:ServletException: Mock for ServletException')
			1 * testObj.logDebug('ManageRegistryChecklistDroplet:ServletException Stack Trace:javax.servlet.ServletException: Mock for ServletException')
			1 * testObj.logDebug('ManageRegistryChecklistDroplet : getManageRegistryRibbon() method with input parameters registryId:232131231 guideType:guideType fromAjax:true')
			1 * testObj.logDebug('Calling the service method of ManageRegistryChecklistDroplet')
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, guideType)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_AJAX, fromAjax)
	}
	
	def"getManageRegistryRibbon. This TC is when IOException thrown"(){
		given:
			testObj = Spy()
			String registryId = "232131231"
			String guideType = "guideType"
			String fromAjax = "true"
			1 * testObj.service(requestMock, responseMock) >> {throw new IOException("Mock for IOException")}
		
		when:
			ManageRegistryChecklistVO results = testObj.getManageRegistryRibbon(registryId, guideType, fromAjax)
		then:
			results == null
			1 * testObj.logError('ManageRegistryChecklistDroplet:IOException: Mock for IOException')
			1 * testObj.logDebug('ManageRegistryChecklistDroplet:IOException Stack Trace:java.io.IOException: Mock for IOException')
			1 * testObj.logDebug('ManageRegistryChecklistDroplet : getManageRegistryRibbon() method with input parameters registryId:232131231 guideType:guideType fromAjax:true')
			1 * testObj.logDebug('Calling the service method of ManageRegistryChecklistDroplet')
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, guideType)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_AJAX, fromAjax)
	}
	
	////////////////////////////////////////TestCases for getManageRegistryRibbon --> ENDS//////////////////////////////////////////////////////////
	
}
