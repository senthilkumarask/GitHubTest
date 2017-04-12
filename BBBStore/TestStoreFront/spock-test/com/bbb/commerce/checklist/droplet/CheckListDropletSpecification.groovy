package com.bbb.commerce.checklist.droplet

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.CheckListProgressVO
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.BBBLocalCacheContainer
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;

import atg.repository.RepositoryItem
import atg.servlet.ServletUtil;
import spock.lang.specification.BBBExtendedSpec

class CheckListDropletSpecification extends BBBExtendedSpec {
	CheckListDroplet testObj
	CheckListTools checkListToolsMock=Mock()
	CheckListManager checkListManagerMock =Mock()
	BBBCatalogTools bbbCatalogToolsMock =Mock()
	BBBLocalCacheContainer cacheContainerMock= Mock()
	GiftRegistryTools giftRegistryToolsMock=Mock()
	CheckListVO checkListVOMock =Mock()
	BBBSessionBean bbbSessionBeanMock =Mock()
	RepositoryItem repositoryItemMock= Mock()
	CheckListProgressVO checkListProgressVOMock =Mock() 
	def setup(){
		testObj=new CheckListDroplet(checkListTools:checkListToolsMock,checkListManager:checkListManagerMock,catalogTools:bbbCatalogToolsMock,cacheContainer:cacheContainerMock,
			giftRegistryTools:giftRegistryToolsMock,sessionBean:bbbSessionBeanMock)
		
	}
		
	def "service-pass- registry type and siteFlag == siteIdValue and getchecklistFlag is true"(){
		given:
			testObj=Spy()
			testObj.setCheckListTools(checkListToolsMock)
			testObj.setCheckListManager(checkListManagerMock)
			testObj.setCacheContainer(null)
			testObj.setCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryTools(giftRegistryToolsMock)
			testObj.setSessionBean(bbbSessionBeanMock)
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"BRD"
			1*requestMock.getParameter(BBBCoreConstants.GET_CHECKLIST_FLAG)>> true
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			checkListToolsMock.fetchCheckListRepositoryItem("BRD") >> [repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathUS"
			
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and getchecklistFlag is false"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"BRD"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK) >> true
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			checkListToolsMock.fetchCheckListRepositoryItem("BRD") >> null
			0*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			0*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathUS"
			bbbSessionBeanMock.setChecklistVO(checkListVOMock)
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and  as null and siteFlag != siteIdValue and getchecklistFlag is false "(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"BRD"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK) >> false
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			checkListToolsMock.fetchCheckListRepositoryItem("BRD") >>  [repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathCa"
			bbbSessionBeanMock.setChecklistVO(checkListVOMock)
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def "service-pass- registry type and siteFlag == siteIdValue and staticChecklist is true "(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "true"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO, checkListVOMock)
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categories"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag != siteIdValue and staticChecklist is false and pass- registry type all categoriesandchecklistVO on session is checkListVOMock"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathCA"
			0*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValue and pass- registry type all categoriesandchecklistVO on session is null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			//requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "true"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			1*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
		
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass-guidetype andsiteFlag == siteIdValue and staticChecklist is true "(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "true"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("Thanksgiving") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO, checkListVOMock)
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- guide type and siteFlag == null and staticChecklist is false and pass- registry type all categories"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"false"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("Thanksgiving") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			checkListVOMock.getRegistryId() >> "520256677"
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def "service-pass- guide type and siteFlag != siteIdValue and staticChecklist is true and pass- registry type all categories"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "true"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("Thanksgiving") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathCA"
			0*checkListVOMock.isCheckListDisabled() >> false
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def "service-pass- guide type and siteFlag == siteIdValue; getCacheContainer is null;staticChecklist is false and pass- registry type all categories"(){
		given:
			testObj=Spy()
			testObj.setCheckListTools(checkListToolsMock)
			testObj.setCheckListManager(checkListManagerMock)
			testObj.setCacheContainer(null)
			testObj.setCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryTools(giftRegistryToolsMock)
			testObj.setSessionBean(bbbSessionBeanMock)				
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"false"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			
			checkListToolsMock.fetchCheckListRepositoryItem("Thanksgiving") >> [repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathUS"
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >>  {throw new BBBBusinessException("mock BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*testObj.logError(_)
			1*testObj.logDebug("usiness Exception from service of CheckListDroplet Stack Trace",_)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, requestMock, responseMock)
			
	}
	
	def "service-pass- guide type and siteFlag == null; getCacheContainer is null;staticChecklist is true and pass- registry type all categories"(){
		given:
			
			requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "true"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"false"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>""
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>""
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			cacheContainerMock.get("Thanksgiving") >> null
			checkListToolsMock.fetchCheckListRepositoryItem("Thanksgiving") >> null
			0*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			0*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathUS"
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListVOMock.getRegistryId() >>"520256677"
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def "service-pass- guide type and siteFlag != siteIdValue; getCacheContainer is null;staticChecklist is false and pass- registry type all categories"(){
		given:
			testObj=Spy()
			testObj.setCheckListTools(checkListToolsMock)
			testObj.setCheckListManager(checkListManagerMock)
			testObj.setCacheContainer(null)
			testObj.setCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryTools(giftRegistryToolsMock)
			testObj.setSessionBean(bbbSessionBeanMock)
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"false"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			cacheContainerMock.get("Thanksgiving") >> null
			checkListToolsMock.fetchCheckListRepositoryItem("Thanksgiving") >> [repositoryItemMock]
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >>false
			1*repositoryItemMock.getPropertyValue(BBBCoreConstants.SITE_FLAG) >> "BedBathCA"
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >>  {throw new BBBSystemException("mock BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
			1*testObj.logError(_)
			1*testObj.logDebug("BBBSystem Exception ChecklistDroplet Stack Trace",_)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, requestMock, responseMock)
			
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nulland c3 as empty"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			1*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass-guidetype andsiteFlag == siteIdValueand staticChecklist is true "(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>null
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("Thanksgiving") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			0*requestMock.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO, checkListVOMock)
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc3 as emptyand IS_CHECKLIST_SELECTED as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>null
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc3 as emptyand REGISTRY_ID as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>null
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"12"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc3 as emptyand TOTAL_C3_QUANTITY_ADDED as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>null
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"12"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc3 as emptyand TOTAL_C3_SUGGESTED as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>null
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"12"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc3 as emptyand NO_OF_C1 as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>null
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"12"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc3 as emptyand AVERAGE_PERCENTAGE as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>null
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"12"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
		
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc3 as emptyand C3_ADDED_QUANTITY as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			//requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "true"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"12"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>null
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			//bbbSessionBeanMock.setChecklistVO(checkListVOMock)
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc2,c3 as empty"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>""
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			//bbbSessionBeanMock.setChecklistVO(checkListVOMock)
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag ==  isiteIdValueandpass- registry type all categoriesandchecklistVO on session is nullandc1,c3 as empty"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.MANUAL_C3_CHECK)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>""
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC320"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> "BedBathUS"
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getRegistryCheckList(_, _, _) >> checkListVOMock
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesandc3 as empty"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			1*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
		bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesandc1,c3 as empty"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>""
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesandc3 as emptyandIS_CHECKLIST_SELECTED as null"(){
		given:
			requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>null
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesandc3 asemptyand REGISTRY_ID as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>null
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>""
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesand TOTAL_C3_QUANTITY_ADDED as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>null
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesand TOTAL_C3_SUGGESTED as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>null
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesand NO_OF_C1 as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>null
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"0"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesand AVERAGE_PERCENTAGE as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>null
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>"1"
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- registry type and siteFlag == null and staticChecklist is false and pass- registry type all categoriesand C3_ADDED_QUANTITY as null"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>""
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>null
			
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			1*giftRegistryToolsMock.getRegistryTypeCode(_,_) >> "BRD"
			2*cacheContainerMock.get("BRD") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			
			0*checkListManagerMock.getUpdatedProgressOnManualCheck(*_) >> checkListProgressVOMock
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			checkListToolsMock.populateCheckListVOAfterManualCheck(checkListProgressVOMock, checkListVOMock, true) >> checkListVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			1*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,null);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def "service-pass- guide type and siteFlag == null and staticChecklist is false  and pass- registry type all categories"(){
		given:
			1*requestMock.getParameter(BBBCoreConstants.GUIDE_TYPE) >>"Thanksgiving"
			1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
			1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"false"
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
			
			1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>null
			1*requestMock.getParameter(BBBCoreConstants.CAT1_ID)>>"DC31"
			1*requestMock.getParameter(BBBCoreConstants.CAT2_ID)>>"DC098"
			1*requestMock.getParameter(BBBCoreConstants.CAT3_ID)>>"DC13"
			
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED) >>"1"
			1*requestMock.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED)>>"2"
			1*requestMock.getParameter(BBBCoreConstants.NO_OF_C1)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE)>>"1"
			1*requestMock.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED)>>"true"
			1*requestMock.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY)>>null
			bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
			checkListToolsMock.showCheckList() >> true
			2*cacheContainerMock.get("Thanksgiving") >> checkListVOMock
			checkListVOMock.getSiteFlag() >> null
			1*checkListVOMock.isCheckListDisabled() >> false
			checkListVOMock.getRegistryId() >> "520256677"
			bbbSessionBeanMock.getChecklistVO()>>checkListVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, false)
			0*requestMock.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVOMock);
			1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def "service-pass- registry type and showCheckList as false"(){
	 given:testObj=Spy()
		 testObj.setCheckListTools(checkListToolsMock)
		 testObj.setCheckListManager(checkListManagerMock)
		 testObj.setCatalogTools(bbbCatalogToolsMock)
		 testObj.setGiftRegistryTools(giftRegistryToolsMock)
		 testObj.setSessionBean(bbbSessionBeanMock)
		 1*requestMock.getParameter(BBBCoreConstants.REGISTRY_TYPE) >>"Wedding"
		 1*requestMock.getParameter(BBBCoreConstants.STATIC_CHECKLIST) >> "false"
		 1*requestMock.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY)>>"true"
		 1*requestMock.getParameter(BBBCoreConstants.REGISTRY_ID)>>"520256677"
		 bbbCatalogToolsMock.getAllValuesForKey(_, _) >>["BedBathUS"]
		 checkListToolsMock.showCheckList() >> false
		 bbbSessionBeanMock.setChecklistVO(checkListVOMock)
		 1*checkListManagerMock.getRegistryCheckList(_, _, _) >>checkListVOMock
		 
	 when:
		 testObj.service(requestMock, responseMock)
	 then:
		 1*requestMock.setParameter(BBBCoreConstants.IS_DISABLED, true)
		 1*requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
 }
		
}
