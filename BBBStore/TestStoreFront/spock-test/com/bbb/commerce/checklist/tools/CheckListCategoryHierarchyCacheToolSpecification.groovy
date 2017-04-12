package com.bbb.commerce.checklist.tools

import atg.adapter.gsa.GSAItem
import atg.multisite.SiteContextException
import atg.multisite.SiteContextImpl
import atg.multisite.SiteContextManager
import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryView
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.checklist.vo.CheckListSeoUrlHierarchy
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.cache.BBBObjectCache
import com.bbb.search.endeca.EndecaSearchUtil
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.utils.BBBConfigRepoUtils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import spock.lang.specification.BBBExtendedSpec

class CheckListCategoryHierarchyCacheToolSpecification extends BBBExtendedSpec {
	
	def CheckListCategoryHierarchyCacheTool testObj
	def Repository repositoryMock = Mock()
	def EndecaSearchUtil endecaSearchUtilMock = Mock()
	def BBBCatalogTools bbbCatalogToolsMock = Mock()
	def CheckListTools checkListToolsMock = Mock()
	def	SiteContextManager siteContextManagerMock = Mock()
	def SiteContextImpl siteContextImplMock = Mock()
	def BBBObjectCache bbbObjectCacheMock= Mock()
	def CheckListSeoUrlHierarchy checkListSeoUrlHierarchyMock= Mock()
	def RepositoryItemDescriptor repositoryItemDescriptorMock= Mock()
	def QueryBuilder queryBuilderMock= Mock()
	def QueryExpression queryExpressionMock = Mock()
	def RepositoryView repositoryViewMock = Mock()
	def RepositoryItem repositoryItemMock =Mock()
	def Query queryMock = Mock()
	def GSAItem gsaItemMock=Mock()
	def ExecutorService executorServiceMock= Mock()
	def Executors executorsMock=Mock()
	def Runnable runnableMock=Mock()
	def setup(){
		testObj = new CheckListCategoryHierarchyCacheTool(registryCheckListRepository:repositoryMock,bbbCatalogTools:bbbCatalogToolsMock,
			endecaSearchUtil:endecaSearchUtilMock,checkListTools:checkListToolsMock,siteContextManager:siteContextManagerMock,objectCache:bbbObjectCacheMock	)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass data upto c2 level without urls and disabled&deleted as false"(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE) >> "BA1">>"BA1"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >> true
		checkListToolsMock.formatUrlParam(_)>>"Wedding">>"FineDining">>"Dinnerware"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>[gsaItemMock]
		
		3*gsaItemMock.getRepositoryId()>>"DC31">>"DC301"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining">>"Dinnerware"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)>>true
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)>>false
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)>>false
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)>>""
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)>>""
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)>>""
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)>>[gsaItemMock]
		
		testObj.setDefaultPoolSize("4")
		bbbCatalogToolsMock.getConfigKeyValue('ContentCatalogKeys','CheckListCategoryIdThreadPoolSize',testObj.getDefaultPoolSize()) >> "1"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
		
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		
		endecaSearchUtilMock.getCatalogId(_,_)>>"DC31"
		
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == true
		3*siteContextManagerMock.pushSiteContext(*_)
		3*siteContextManagerMock.popSiteContext(*_)
	}

	
	def "populateCheckListHierarchyAndLoadInCache- we pass upto c2 level with urls and disabled&deleted as true"(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE)>>"BA1">>"COL"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED) >> false
		checkListToolsMock.formatUrlParam(_)>>"Wedding">>"FineDining">>"Dinnerware"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>[gsaItemMock]
		
		3*gsaItemMock.getRepositoryId()>>"DC31">>"DC301"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"">>""
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)>>false
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)>>true
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)>>true
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)>>"www.amazon.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)>>"www.gmail.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)>>"www.google.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)>>[gsaItemMock]
		
		
		bbbCatalogToolsMock.getConfigKeyValue(_, _,_) >> "3"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
				
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		endecaSearchUtilMock.getCatalogId(_,_)>>"DC31"
		testObj.setDetailLog(true)
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == true
		3*siteContextManagerMock.pushSiteContext(*_)
		3*siteContextManagerMock.popSiteContext(*_)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass upto c2 level with urls and disabled as false & deleted as true"(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		checkListToolsMock.formatUrlParam(_)>>"Wedding">>"FineDining">>"Dinnerware"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>[gsaItemMock]
		
		3*gsaItemMock.getRepositoryId()>>"DC31">>"DC301"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining">>"Dinnerware"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)>>true
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)>>false
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)>>true
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)>>"www.amazon.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)>>"www.gmail.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)>>"www.google.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)>>[gsaItemMock]
		
	
		bbbCatalogToolsMock.getConfigKeyValue(_, _,_) >> "3"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
				
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		endecaSearchUtilMock.getCatalogId(_,_)>>"DC31"
		testObj.setDetailLog(true)
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == true
		3*siteContextManagerMock.pushSiteContext(*_)
		3*siteContextManagerMock.popSiteContext(*_)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass upto c2 level with urls and disabled as true & deleted as false"(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		checkListToolsMock.formatUrlParam(_)>>"Wedding">>"FineDining">>"Dinnerware"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>[gsaItemMock]
		
		3*gsaItemMock.getRepositoryId()>>"DC31">>"DC301"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining">>"Dinnerware"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)>>true
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)>>true
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)>>false
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)>>"www.amazon.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)>>"www.gmail.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)>>"www.google.com"
		3*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)>>[gsaItemMock]
		
		
		bbbCatalogToolsMock.getConfigKeyValue(_, _,_) >> "2"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
		
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		endecaSearchUtilMock.getCatalogId(_,_)>>""
		testObj.setDetailLog(true)
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		3*siteContextManagerMock.pushSiteContext(*_)
		3*siteContextManagerMock.popSiteContext(*_)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass c2 as empty & c1 level with urls and disabled as true & deleted as false"(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		checkListToolsMock.formatUrlParam(_)>>"Wedding">>"FineDining"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>[gsaItemMock]
		
		1*gsaItemMock.getRepositoryId()>>"DC31"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)>>true
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)>>true
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)>>false
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)>>"www.amazon.com"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)>>"www.gmail.com"
	
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)>>"www.google.com"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)>>[]
		
		
		bbbCatalogToolsMock.getConfigKeyValue(_, _,_) >> "1"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
				
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		endecaSearchUtilMock.getCatalogId(_,_)>>{throw new Exception("mock Exception")}
		testObj.setDetailLog(true)
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		1*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass c2 as null & c1 level with urls and disabled as true & deleted as false"(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		checkListToolsMock.formatUrlParam(_)>>"Wedding">>"FineDining"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>[gsaItemMock]
		
		1*gsaItemMock.getRepositoryId()>>"DC31"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)>>true
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)>>true
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)>>false
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)>>"www.amazon.com"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)>>"www.gmail.com"
	
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)>>"www.google.com"
		1*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)>>null
		
		
		bbbCatalogToolsMock.getConfigKeyValue(_, _,_) >> "1"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
		siteContextManagerMock.getSite(BBBCoreConstants.SITE_BAB_US)>>{throw new SiteContextException("mock SiteContextException")}
		
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		endecaSearchUtilMock.getCatalogId(_,_)>>"DC31"
		testObj.setDetailLog(true)
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		0*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass c1 as empty "(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		checkListToolsMock.formatUrlParam(_)>>"Wedding"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>[]
		
		bbbCatalogToolsMock.getConfigKeyValue(_, _,_) >> "3"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
		
		
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		endecaSearchUtilMock.getCatalogId(_,_)>>"DC31"
		testObj.setDetailLog(true)
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		0*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
		0*gsaItemMock.getRepositoryId()
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass c1 as null "(){
		given:
		populateChecklist()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>"FineDining"
		repositoryItemMock.getRepositoryId() >> "DC31"
		checkListToolsMock.formatUrlParam(_)>>"Wedding"
		repositoryItemMock.getPropertyValue(BBBCoreConstants.CHECK_LIST_CATEGORIES)>>null
		
		bbbCatalogToolsMock.getConfigKeyValue(_, _,_) >> "3"
		executorsMock.newFixedThreadPool(_) >>executorServiceMock
		executorServiceMock.submit(runnableMock)
		
		BBBConfigRepoUtils.setBbbCatalogTools(bbbCatalogToolsMock)
		bbbCatalogToolsMock.getAllValuesForKey("ObjectCacheKeys", "CHECKLIST_PLP_SEO_URL_CACHE_NAME")>>["sdsdsss"]
		endecaSearchUtilMock.getCatalogId(_,_)>>"DC31"
		testObj.setDetailLog(true)
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		0*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
		0*gsaItemMock.getRepositoryId()
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.SHOW_ON_CHECKLIST)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DISABLED)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.IS_DELETED)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL)
		0*gsaItemMock.getPropertyValue(BBBCoreConstants.CHILD_CHECKLIST_CATEGORY_ID)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass checklist empty"(){
		given:
		testObj=Spy()
		testObj.setRegistryCheckListRepository(repositoryMock)
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECK_LIST_TYPE)>>repositoryItemDescriptorMock
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECKLIST) >> repositoryItemDescriptorMock
		repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>>queryBuilderMock
		queryBuilderMock.createPropertyQueryExpression(BBBCoreConstants.TYPE_NAME)>>queryExpressionMock
		queryBuilderMock.createConstantQueryExpression("REGISTRY")>>queryExpressionMock
		queryBuilderMock.createComparisonQuery(*_) >> queryMock
		queryBuilderMock.createUnconstrainedQuery()>>queryMock
		repositoryViewMock.executeQuery(queryMock)>>[]
		
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		0*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
		1*testObj.logError("populateCheckListHierarchyAndLoadInCache exception", _)
		0*repositoryItemMock.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE)
	}
	def "populateCheckListHierarchyAndLoadInCache- we pass checklist null"(){
		given:
		testObj=Spy()
		testObj.setRegistryCheckListRepository(repositoryMock)
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECKLIST) >> repositoryItemDescriptorMock
		repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>>queryBuilderMock
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECK_LIST_TYPE)>>repositoryItemDescriptorMock
		queryBuilderMock.createPropertyQueryExpression(BBBCoreConstants.TYPE_NAME)>>queryExpressionMock
		queryBuilderMock.createConstantQueryExpression("REGISTRY")>>queryExpressionMock
		queryBuilderMock.createComparisonQuery(*_) >> queryMock
		queryBuilderMock.createUnconstrainedQuery()>>queryMock
		repositoryViewMock.executeQuery(queryMock)>>null
		
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		0*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
		1*testObj.logError("populateCheckListHierarchyAndLoadInCache exception", _)
		0*repositoryItemMock.getPropertyValue(BBBCoreConstants.SUBTYPE_CODE)
	}
	def "populateCheckListHierarchyAndLoadInCache- throw RepositoryException"(){
		given:
		testObj=Spy()
		testObj.setRegistryCheckListRepository(repositoryMock)
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECK_LIST_TYPE)>>repositoryItemDescriptorMock
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECKLIST) >> repositoryItemDescriptorMock
		repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>>queryBuilderMock
		queryBuilderMock.createPropertyQueryExpression(BBBCoreConstants.TYPE_NAME)>>queryExpressionMock
		queryBuilderMock.createConstantQueryExpression("REGISTRY")>>queryExpressionMock
		queryBuilderMock.createComparisonQuery(*_) >> queryMock
		queryBuilderMock.createUnconstrainedQuery()>>{throw new RepositoryException("mock RepositoryException")}
		repositoryViewMock.executeQuery(queryMock)>>[]
		
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		0*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
		1*testObj.logError("RepositoryException occured in populateCheckListHierarchy ",_)
		1*testObj.logError("populateCheckListHierarchyAndLoadInCache BBBSystemException", _);
	}
	def "populateCheckListHierarchyAndLoadInCache- throw Exception"(){
		given:
		testObj=Spy()
		testObj.setRegistryCheckListRepository(repositoryMock)
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECK_LIST_TYPE)>>repositoryItemDescriptorMock
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECKLIST) >> repositoryItemDescriptorMock
		repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>>queryBuilderMock
		queryBuilderMock.createPropertyQueryExpression(BBBCoreConstants.TYPE_NAME)>>queryExpressionMock
		queryBuilderMock.createConstantQueryExpression("REGISTRY")>>queryExpressionMock
		queryBuilderMock.createComparisonQuery(*_) >> queryMock
		queryBuilderMock.createUnconstrainedQuery()>>queryMock
		repositoryViewMock.executeQuery(queryMock)>>{throw new Exception("mock Exception")}
		
		when:
		def resultset = testObj.populateCheckListHierarchyAndLoadInCache()
		then:
		resultset == false
		0*siteContextManagerMock.pushSiteContext(*_)
		0*siteContextManagerMock.popSiteContext(*_)
		1*testObj.logError("populateCheckListHierarchyAndLoadInCache BBBSystemException", _)
		1*testObj.logError("Exception occured in populateCheckListHierarchy ", _)
	}
	
	protected populateChecklist() {
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECK_LIST_TYPE)>>repositoryItemDescriptorMock
		repositoryMock.getItemDescriptor(BBBCoreConstants.CHECKLIST) >> repositoryItemDescriptorMock
		repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>>queryBuilderMock
		queryBuilderMock.createPropertyQueryExpression(BBBCoreConstants.TYPE_NAME)>>queryExpressionMock
		queryBuilderMock.createConstantQueryExpression("REGISTRY")>>queryExpressionMock
		queryBuilderMock.createComparisonQuery(*_) >> queryMock
		queryBuilderMock.createUnconstrainedQuery()>>queryMock
		repositoryViewMock.executeQuery(queryMock)>>[repositoryItemMock]
		
	}

}
