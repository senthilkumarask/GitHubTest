package com.bbb.commerce.checklist.droplet

import atg.repository.RepositoryItem
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.checklist.manager.CheckListManager
import com.bbb.commerce.checklist.tools.CheckListTools
import com.bbb.commerce.checklist.vo.CheckListVO
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.BBBLocalCacheContainer


import spock.lang.specification.BBBExtendedSpec

class StaticCheckListDropletSpecification extends BBBExtendedSpec {
	StaticCheckListDroplet testObj
	CheckListTools checkListToolsMock=Mock()
	CheckListManager checkListManagerMock =Mock()
	BBBCatalogTools bbbCatalogToolsMock =Mock()
	BBBLocalCacheContainer cacheContainerMock= Mock()
	GiftRegistryTools giftRegistryToolsMock=Mock()
	CheckListVO checkListVOMock =Mock()
	RepositoryItem repositoryItemMock= Mock()
	def setup(){
		testObj=new StaticCheckListDroplet(checkListTools:checkListToolsMock,checkListManager:checkListManagerMock,mCatalogTools:bbbCatalogToolsMock,cacheContainer:cacheContainerMock,giftRegistryTools:giftRegistryToolsMock)
	}
	def "service- passing data with checklistType as 'BRD' and getSiteflag == siteIdValue"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "BRD"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("BRD") >> "registry"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			0*giftRegistryToolsMock.getRegistryTypeCode(_, _) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			1*bbbCatalogToolsMock.getRegistryTypeName(_,_) >> "Weding"
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
		1* requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		1*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,_)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'wedding' and getSiteflag = null"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "Wedding"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("Wedding") >> "registry"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*bbbCatalogToolsMock.getRegistryTypeName(_,_) >> "Weding"
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
		1* requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		1*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE, 'Wedding')
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'wedding' and getSiteflag != siteIdValue"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "Wedding"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("Wedding") >> "registry"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathCa"
			0*checkListVOMock.isCheckListDisabled() >> false
			0*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*bbbCatalogToolsMock.getRegistryTypeName(_,_) >> "Wedding"
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		0* requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		0*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE, 'Wedding')
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'BRD' and cacheContainer is null then Siteflag == siteIdValue and throw BBBSystemException"(){
		given:
			usingSpy()
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "BRD"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("BRD") >> "registry"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			0*giftRegistryToolsMock.getRegistryTypeCode(_, _) >> "BRD"
			1*cacheContainerMock.get("BRD") >> null
			checkListToolsMock.fetchCheckListRepositoryItem("BRD") >> [repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathUS"
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			bbbCatalogToolsMock.getRegistryTypeName(_,_) >> {throw new BBBSystemException("mock BBBSystemException")}
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
		1*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		1*testObj.logError(_)
		1*testObj.logDebug("BBBSystem Exception StaticCheckListDroplet Stack Trace",_);
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, requestMock, responseMock)
	}

	def "service- passing data with checklistType as 'BRD' and cacheContainer is null then Siteflag == null"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "BRD"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("BRD") >> "registry"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			0*giftRegistryToolsMock.getRegistryTypeCode(_, _) >> "BRD"
			1*cacheContainerMock.get("BRD") >> null
			checkListToolsMock.fetchCheckListRepositoryItem("BRD") >> null
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		0*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	def "service- passing data with checklistType as 'BRD' and cacheContainer is null then Siteflag != siteIdValue"(){
		given:
			testObj=Spy()
			testObj.setCheckListTools(checkListToolsMock)
			testObj.setCheckListManager(checkListManagerMock)
			testObj.setCacheContainer(null)
			testObj.setCatalogTools(bbbCatalogToolsMock)
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "BRD"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("BRD") >> "registry"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			0*giftRegistryToolsMock.getRegistryTypeCode(_, _) >> "BRD"
			checkListToolsMock.fetchCheckListRepositoryItem("BRD") >> [repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathCa"
			0*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		0*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		0*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,_)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	protected usingSpy() {
		testObj=Spy()
		testObj.setCheckListTools(checkListToolsMock)
		testObj.setCheckListManager(checkListManagerMock)
		testObj.setCacheContainer(cacheContainerMock)
		testObj.setCatalogTools(bbbCatalogToolsMock)
	}
	
	def "service- passing data with checklistType as 'guide' and getSiteflag = null"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> "NONREGISTRY"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("guide") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
		1* requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		1*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,"")
		1*requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, "guide")
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'guide' and getSiteflag == siteIdValue and throw BBBBusinessException"(){
		given:
			usingSpy()
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> "NONREGISTRY"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("guide") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			checkListManagerMock.getRegistryCheckList(_, _, _) >> {throw new BBBBusinessException("mock BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
		1*testObj.logError(_)
		1*testObj.logDebug("usiness Exception from service of StaticCheckListDroplet Stack Trace",_)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, requestMock, responseMock)
	}

	def "service- passing data with checklistType as 'guide' and getSiteflag != siteIdValue"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> "NONREGISTRY"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("guide") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathCa"
			0*checkListVOMock.isCheckListDisabled() >> false
			0*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		0* requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		0*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,"")
		0*requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, "guide")
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'guide' and cacheContainer is null then getSiteflag == siteIdValue"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> "NONREGISTRY"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			cacheContainerMock.get("guide") >> null
			checkListToolsMock.fetchCheckListRepositoryItem("guide") >> [repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathUS"
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
		1* requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		1*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,"")
		1*requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, "guide")
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'guide' and cacheContainer is null then getSiteflag = null"(){
		given:
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> "NONREGISTRY"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			cacheContainerMock.get("guide") >> null
			checkListToolsMock.fetchCheckListRepositoryItem("guide") >>null
			0*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			0*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		0*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		0*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,"")
		0*requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, "guide")
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'guide' and cacheContainer is null then getSiteflag != siteIdValue"(){
		given:
			testObj=Spy()
			testObj.setCheckListTools(checkListToolsMock)
			testObj.setCheckListManager(checkListManagerMock)
			testObj.setCacheContainer(null)
			testObj.setCatalogTools(bbbCatalogToolsMock)
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> "NONREGISTRY"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			checkListToolsMock.fetchCheckListRepositoryItem("guide") >>[repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathCa"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		0*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		0*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,"")
		0*requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, "guide")
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'guide' and throw BBBBusinessException"(){
		given:
			usingSpy()
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> {throw new BBBBusinessException("mock BBBBusinessException")}
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> false
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		1*testObj.logError(_)
		1*testObj.logDebug("BBBSystem Exception StaticCheckListDroplet Stack Trace",_);
		0*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		0*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,"")
		0*requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, "guide")
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, requestMock, responseMock)
	}
	
	def "service- passing data with checklistType as 'guide' and throw BBBSystemException"(){
		given:
			usingSpy()
			requestMock.getParameter(BBBCoreConstants.CHECKLIST_TYPE) >> "guide"
			checkListToolsMock.checkRegistryTypeFromSubTypeCode("guide") >> {throw new BBBSystemException("mock BBBSystemException")}
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >> ["BedBathUS"]
			checkListToolsMock.showCheckList() >> false
		when:
			testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		1*testObj.logError(_)
		1*testObj.logDebug("BBBSystem Exception StaticCheckListDroplet Stack Trace",_);
		0*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVOMock)
		0*requestMock.setParameter(BBBCoreConstants.REGISTRY_TYPE,"")
		0*requestMock.setParameter(BBBCoreConstants.GUIDE_TYPE, "guide")
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, requestMock, responseMock)
	}
}

