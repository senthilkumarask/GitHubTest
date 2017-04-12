package atg.sitemap



import java.util.Map;
import javax.transaction.TransactionManager

import com.bbb.commerce.checklist.tools.CheckListTools
import atg.adapter.gsa.GSARepository
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContext
import atg.multisite.SiteContextException
import atg.multisite.SiteContextManager
import atg.multisite.SiteManager
import atg.multisite.SiteURLManager
import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.service.webappregistry.WebApp
import spock.lang.specification.BBBExtendedSpec;

import org.junit.Test

import com.bbb.constants.BBBCoreConstants;;

class BBBChecklistCategoryDynamicSitemapGeneratorSpecification extends BBBExtendedSpec{
	def BBBChecklistCategoryDynamicSitemapGenerator testObj
	
	def SiteContextManager siteContextManagerMock = Mock()
	def SitemapGeneratorService sitemapGeneratorServiceMock = Mock()
	def SiteContext siteContextMock = Mock()
	def CheckListTools checkListToolsMock = Mock()
	def RepositoryItem repositoryItemMock = Mock()
	def SiteURLManager siteURLManagerMock= Mock()
	def SitemapTools sitemapToolsMock =Mock()
	def WebApp webAppMock =Mock()
	def SiteManager siteManagerMock = Mock()
	def Repository repositoryMock =Mock()
	def GSARepository GSARepositoryMock = Mock()
	def RepositoryView repositoryViewMock =Mock()
	def QueryBuilder queryBuilderMock =Mock()
	def QueryExpression queryExpressionMock =Mock()
	def Query queryMock =Mock()
	def TransactionManager transactionManagerMock = Mock()
	def Map<String, String> siteToBBBSiteMap=new HashMap()
	def List<RepositoryItem> items = new ArrayList<RepositoryItem>();
	def setup()
	{
		testObj=new BBBChecklistCategoryDynamicSitemapGenerator(checkListTools:checkListToolsMock, transactionManager:transactionManagerMock)
	}

	def "generateSitemap, passing getSitesIncludedForGeneration as 'BedBathUS' "(){
		given:
		testObj = Spy()
		testObj.setLoggingDebug(true)
		testObj.setSitesIncludedForGeneration(['BedBathUS'])
		1*sitemapGeneratorServiceMock.getSiteContextManager() >> siteContextManagerMock
		2*siteContextManagerMock.getSiteContext('BedBathUS') >> siteContextMock
		testObj.setSitemapFilePrefixString('checklistCategorySitemap') 
		testObj.callSuperpreGenerateSitemapUrls(*_)>>{} 
		testObj.generateSitemapUrls(*_) >>{}
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)//calling the test methods
	   then:
	   1* siteContextManagerMock.pushSiteContext(siteContextMock)
	   1* testObj.setSitemapFilePrefix(_)
	   1* testObj.preGenerateSitemapUrls(_,_)
	   
	}
	def "generateSitemap, pass getSitesIncludedForGeneration as null "(){
		given:
		
		testObj.setSitesIncludedForGeneration(null)
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)//calling the test methods
	   then:
	   0* siteContextManagerMock.pushSiteContext(siteContextMock)
	   0* testObj.preGenerateSitemapUrls(_,_)
	   
	}
	def "generateSitemap, passing getSitesIncludedForGeneration as 'BedBathCanada' and throw SiteContextException"(){
		given:
		testObj = Spy()
		testObj.setSitesIncludedForGeneration(['BedBathCanada'])
		1*sitemapGeneratorServiceMock.getSiteContextManager() >> siteContextManagerMock
		siteContextManagerMock.getSiteContext('BedBathCanada') >> {throw new SiteContextException("mock SiteContextException")}
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)
	   then:
	     1 * testObj.logError(_)
		 0* siteContextManagerMock.pushSiteContext(siteContextMock)
		 0* testObj.preGenerateSitemapUrls(_,_)
		 
	}
	def "generateSitemap passing pSitemapGeneratorService as 'BedBathCanada'and set loggingdebug as true "(){
		given:
		testObj = Spy()
		testObj.setLoggingDebug(true)
		testObj.setSitesIncludedForGeneration(['BedBathCanada'])
		1*sitemapGeneratorServiceMock.getSiteContextManager() >> siteContextManagerMock
		siteContextManagerMock.getSiteContext('BedBathCanada') >> {throw new SiteContextException("mock SiteContextException")}
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)
	   then:
	     1* testObj.logError(_)
		 2* testObj.logError(_,_)
		 0* siteContextManagerMock.pushSiteContext(siteContextMock)
		 0* testObj.preGenerateSitemapUrls(_,_)
		 
	}
	def "generateSitemap, passing getSitesIncludedForGeneration as 'BedBathUS' and getSitemapFilePrefixString as null and site as null"(){
		given:
		testObj = Spy()
		testObj.setLoggingDebug(true)
		testObj.getSitesIncludedForGeneration() >>>[['BedBathUS'],[null],['BedBathUS']]
		sitemapGeneratorServiceMock.getSiteContextManager() >> siteContextManagerMock
		siteContextManagerMock.getSiteContext('BedBathUS') >> siteContextMock
		testObj.setSitemapFilePrefixString(null)
		testObj.callSuperpreGenerateSitemapUrls(*_)>>{}
		testObj.generateSitemapUrls(*_) >>{}
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)
	   then:
	   0* testObj.setSitemapFilePrefix(_)
	   1* testObj.preGenerateSitemapUrls(_,_)
	   
	}
	def "generateSitemap, passing getSitesIncludedForGeneration as 'BedBathUS' and site as null"(){
		given:
		testObj = Spy()
		testObj.isLoggingDebug >>>[true,true,false,true,false]
		testObj.getSitesIncludedForGeneration() >>>[['BedBathUS'],[null],['BedBathUS']]
		1*sitemapGeneratorServiceMock.getSiteContextManager() >> siteContextManagerMock
		siteContextManagerMock.getSiteContext(_) >> siteContextMock
		testObj.setSitemapFilePrefixString('checklistCategorySitemap') 
		testObj.callSuperpreGenerateSitemapUrls(*_)>>{}
		1*testObj.generateSitemapUrls(*_) >>{}
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)
	   then:
	   0* testObj.setSitemapFilePrefix(_)
	   1* testObj.preGenerateSitemapUrls(_,_)
	   
	}
	def "generateSitemap passing getSitesIncludedForGeneration as null in 3rd execution in preGenerateSitemapUrls method"(){
		given:
		testObj = Spy()
		testObj.getSitesIncludedForGeneration() >>>[['BedBathUS'],['BedBathUS'],[]]
		sitemapGeneratorServiceMock.getSiteContextManager() >> siteContextManagerMock
		siteContextManagerMock.getSiteContext('BedBathUS') >> siteContextMock
		testObj.generateSitemapUrls(*_) >>{}
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)//calling the test methods
	   then:
	   1* siteContextManagerMock.pushSiteContext(siteContextMock)
	}
	def "generateSitemap, passing getSitesIncludedForGeneration as 'BedBathUS'and pass LoggingDebug is false in preGenerateSitemapUrls method"(){
		given:
		testObj = Spy()
		testObj.setLoggingDebug(false)
		testObj.setSitesIncludedForGeneration(['BedBathUS'])
		sitemapGeneratorServiceMock.getSiteContextManager() >> siteContextManagerMock
		siteContextManagerMock.getSiteContext('BedBathUS') >> siteContextMock
		testObj.setSitemapFilePrefixString('checklistCategorySitemap') 
		testObj.callSuperpreGenerateSitemapUrls(*_)>>{}
		testObj.generateSitemapUrls(*_) >>{}
	   when:
		testObj.generateSitemap(sitemapGeneratorServiceMock)//calling the test methods
	   then:
	   1* siteContextManagerMock.pushSiteContext(siteContextMock)
	   1* testObj.preGenerateSitemapUrls(_,_)
	   
	}
	//***************************************************************************
                     //         getSitemapURL() method -Start
	//***************************************************************************
	
	def "getSitemapURL passing all values"(){
		given:
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> 'Wedding'
		testObj.getCheckListTools().formatUrlParam(*_) >> 'Wedding'
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>'/Wedding/FineDining'
		testObj.setWebApp(webAppMock)
		webAppMock.getContextRoot() >> ""
		testObj.setUrlPrefix("/")
		testObj.setLoggingDebug(true)
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, repositoryItemMock, sitemapGeneratorServiceMock)
		then:
		results == "/Wedding/FineDining"
		//1*sitemapToolsMock.addPrefixToUrl(_, _)
	}
	
	def "getSitemapURL, we pass site as null"(){
		given:
		
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> null
		testObj.getCheckListTools().formatUrlParam(*_) >> 'Wedding'
		testObj.setNeedServletPathOnSitemapURL(true)
		testObj.setWebApp(webAppMock)
		webAppMock.getContextRoot() >> "/"
		testObj.setUrlPrefix("bedbathUs\'")
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, null, sitemapGeneratorServiceMock)
		then:
		results == "bedbathUs&#39;/Checklist/Wedding"
		0*siteURLManagerMock.getProductionSiteBaseURL(*_)
		
	}
	def "getSitemapURL, we pass null vlaue for pSite and pRepositoryItem"(){
		given:
		testObj.getCheckListTools().formatUrlParam(*_) >> 'Wedding'
		testObj.setWebApp(webAppMock)
		1*webAppMock.getContextRoot() >> ""
		testObj.setUrlPrefix("/")
		when:
		def results = testObj.getSitemapURL(null, null, sitemapGeneratorServiceMock)
		then:
		results == "/Checklist/Wedding"
		
	}
	def "getSitemapURL, we pass null vlaue for pRepositoryItem"(){
		given:
		testObj.getCheckListTools().formatUrlParam(*_) >> 'Wedding'
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>'/checklist/Wedding'
		testObj.setWebApp(webAppMock)
		webAppMock.getContextRoot() >> ""
		testObj.setUrlPrefix("/")
		when:
		def results = testObj.getSitemapURL(null, repositoryItemMock, sitemapGeneratorServiceMock)
		then:
		results == "/checklist/Wedding"
		//0*repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
	}
	def "getSitemapURL,finalUrl start with character"(){
		given:
		testObj.getCheckListTools().formatUrlParam(*_) >> 'Wedding'
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>'Checklist/Wedding'
		testObj.setUrlPrefix("/")
		when:
		def results = testObj.getSitemapURL(null, repositoryItemMock, sitemapGeneratorServiceMock)
		then:
		results == "Checklist/Wedding"
		0* webAppMock.getContextRoot()
	}
	def "getSitemapURL throw Exception"(){
		given:
		testObj=Spy()
		testObj.setCheckListTools(checkListToolsMock)
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> 'Wedding'
		testObj.getCheckListTools().formatUrlParam(*_) >> 'FineDining'
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>{throw new Exception("mock Exception")}
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, repositoryItemMock, sitemapGeneratorServiceMock)
		then:
		results == null
		1* testObj.logError(_)
	}
	
	def "getSitemapURL throw Exception and set LoggingDebug as true"(){
		given:
		testObj=Spy()
		testObj.setCheckListTools(checkListToolsMock)
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> 'Wedding'
		testObj.getCheckListTools().formatUrlParam(*_) >> 'FineDining'
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>{throw new Exception("mock Exception")}
		testObj.setLoggingDebug(true)
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, repositoryItemMock, sitemapGeneratorServiceMock)
		then:
		results == null
		1* testObj.logError(_)
		2* testObj.logError(_,_)
	}
	//***************************************************************************
	 //         getSitemapURL() method -Stop
	//***************************************************************************
	//***************************************************************************
	 //         generateSitemapUrls() method -Start
	//***************************************************************************
	def "generateSitemapUrls,pass all param values"(){
		given:
		mockObjects()
		testObj.isLoggingDebug()>>true
		siteManagerMock.getSite(*_)>>repositoryItemMock
		repositoryViewMock.executeQuery(*_)>>>[[repositoryItemMock,null],null]
		testObj.getSitemapURL(*_) >>'/Checklist/FineDining'
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>items
		repositoryItemMock.getRepositoryId() >>'BedBathUS'
		4*repositoryItemMock.getPropertyValue(BBBCoreConstants.CATEGORY_URL) >> '/FineDining'
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>'/Checklist/FineDining'
		testObj.setWebApp(webAppMock)
		webAppMock.getContextRoot() >> ""
		testObj.setUrlPrefix("/")
		testObj.setPriority(133.23)
		sitemapToolsMock.generateSitemapUrlXml(_,_, _, _)>> '/fine'
		sitemapGeneratorServiceMock.getMaxUrlsPerSitemap()>> 0
		sitemapToolsMock.validateSitemapSize(*_)>>false
		checkListToolsMock.childCategoriesForChecklistCategory(*_) >>> [[repositoryItemMock],[null]]
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>'/Dinnerware'
		3*checkListToolsMock.formatUrlParam(*_)>>'/Dinnerware'
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		3* sitemapToolsMock.appendSitemapHeader(*_)
	}

	
	def "generateSitemapUrls,pass null value for pSiteId"(){
		given:
		testObj=Spy()
		mockObjects()
		siteManagerMock.getSite(*_) >>repositoryItemMock
		repositoryViewMock.executeQuery(*_)>>> [repositoryItemMock,null]
		testObj.getSitemapURL(*_) >>'/Checklist/FineDining'
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>null
		testObj.generateSEOUrlForChildCategory(*_) >>{}
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, null)
		then:
		0* siteManagerMock.getSite("BedBathUS")
		//1*checkListToolsMock.childCategoriesForChecklist(repositoryItemMock)
	}
	def "generateSitemapUrls,we return empty list for executeQuery" (){
		given:
		testObj=Spy()
		siteManagerMock.getSite(*_) >>repositoryItemMock
		mockObjects()
		repositoryViewMock.executeQuery(*_)>>[]
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		1* siteManagerMock.getSite("BedBathUS")
		0*checkListToolsMock.childCategoriesForChecklist(repositoryItemMock)
	}
	def "generateSitemapUrls,throw RepositoryException" (){
		given:
		testObj=Spy()
		items.add(repositoryItemMock)
		siteToBBBSiteMap.put('BedBathUS','BedBathUS')
		testObj.setSourceRepository(GSARepositoryMock)
		GSARepositoryMock.getView(*_) >> {throw new RepositoryException("mock RepositoryException")}
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		 1 * testObj.logError(_)
		 1*sitemapGeneratorServiceMock.getSitemapTools() >> sitemapToolsMock
	}
	def "generateSitemapUrls,throw RepositoryException and set LoggingDebug as true" (){
		given:
		testObj=Spy()
		testObj.setLoggingDebug(true)
		items.add(repositoryItemMock)
		siteToBBBSiteMap.put('BedBathUS','BedBathUS')
		testObj.setSourceRepository(GSARepositoryMock)
		GSARepositoryMock.getView(*_) >> {throw new RepositoryException("mock RepositoryException")}
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		 1 * testObj.logError(_)
		 1*sitemapGeneratorServiceMock.getSitemapTools() >> sitemapToolsMock
		 2 * testObj.logError(_,_)
	}
	def "generateSitemapUrls,throw TransactionDemarcationException" (){
		given:
		testObj=Spy()
		items.add(repositoryItemMock)
		siteToBBBSiteMap.put('BedBathUS','BedBathUS')
		testObj.setSourceRepository(GSARepositoryMock)
		GSARepositoryMock.getView(*_) >> repositoryViewMock
		sitemapGeneratorServiceMock.getSitemapTools() >> sitemapToolsMock
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getSiteManager() >> siteManagerMock
		siteManagerMock.getSite(*_) >>repositoryItemMock
		testObj.setSiteToBBBSiteMap(siteToBBBSiteMap)
		testObj.getSiteToBBBSiteMap().get('BedBathUS')
		repositoryViewMock.getQueryBuilder() >> queryBuilderMock
		queryBuilderMock.createPropertyQueryExpression(*_) >> queryExpressionMock
		queryBuilderMock.createConstantQueryExpression(*_) >> queryExpressionMock
		queryBuilderMock.createPatternMatchQuery(*_) >> queryMock
		testObj.getTransactionManager() >> {throw new TransactionDemarcationException("mock TransactionDemarcationException")}
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		1* siteManagerMock.getSite("BedBathUS")
		1 * testObj.logError(_)
		0*checkListToolsMock.childCategoriesForChecklist(repositoryItemMock)
	}
	def "generateSitemapUrls,throw TransactionDemarcationException and set LoggingDebug as true" (){
		given:
		testObj=Spy()
		testObj.setLoggingDebug(true)
		items.add(repositoryItemMock)
		siteToBBBSiteMap.put('BedBathUS','BedBathUS')
		testObj.setSourceRepository(GSARepositoryMock)
		GSARepositoryMock.getView(*_) >> repositoryViewMock
		sitemapGeneratorServiceMock.getSitemapTools() >> sitemapToolsMock
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getSiteManager() >> siteManagerMock
		siteManagerMock.getSite(*_) >>repositoryItemMock
		testObj.setSiteToBBBSiteMap(siteToBBBSiteMap)
		testObj.getSiteToBBBSiteMap().get('BedBathUS')
		repositoryViewMock.getQueryBuilder() >> queryBuilderMock
		queryBuilderMock.createPropertyQueryExpression(*_) >> queryExpressionMock
		queryBuilderMock.createConstantQueryExpression(*_) >> queryExpressionMock
		queryBuilderMock.createPatternMatchQuery(*_) >> queryMock
		testObj.getTransactionManager() >> {throw new TransactionDemarcationException("mock TransactionDemarcationException")}
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		1* siteManagerMock.getSite("BedBathUS")
		1 * testObj.logError(_)
		2 * testObj.logError(_,_)
		0*checkListToolsMock.childCategoriesForChecklist(repositoryItemMock)
	}
	
	def "generateSitemapUrls,we pass siteid as baby in generateSEOUrlForChildCategory method"(){
		given:
		testObj.isLoggingDebug()>>true
		siteManagerMock.getSite(*_)>>repositoryItemMock
		mockObjects()
		repositoryViewMock.executeQuery(*_)>>>[[repositoryItemMock,null],null]
		testObj.getSitemapURL(*_) >>'/Checklist/FineDining'
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>items
		repositoryItemMock.getRepositoryId() >>'BuyBuyBaby'
		4*repositoryItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL) >> '/FineDining'
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>'/Checklist/FineDining'
		testObj.setPriority(133.23)
		2*sitemapToolsMock.generateSitemapUrlXml(_,_, _, _)>> '/fine'
		2*sitemapGeneratorServiceMock.getMaxUrlsPerSitemap()>> 0
		sitemapToolsMock.validateSitemapSize(_,_,_)>>true
		2*checkListToolsMock.childCategoriesForChecklistCategory(*_) >>> [[repositoryItemMock],[null]]
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>null
		checkListToolsMock.formatUrlParam(*_)>>'/Dinnerware'
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		3* sitemapToolsMock.appendSitemapHeader(*_)
		//2*repositoryItemMock.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL)
	}
	
	def "generateSitemapUrls,we pass siteid as BedBathCanada in generateSEOUrlForChildCategory method"(){
		given:
		testObj.isLoggingDebug()>>true
		siteManagerMock.getSite(*_)>>repositoryItemMock
		mockObjects()
		repositoryViewMock.executeQuery(*_)>>>[[repositoryItemMock,null],null]
		testObj.getSitemapURL(*_) >>'/Checklist/FineDining'
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>items
		repositoryItemMock.getRepositoryId() >>'BedBathCanada'
		4*repositoryItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL) >> '/FineDining'
		parameterObjects()
		sitemapToolsMock.validateSitemapSize(*_)>>true
		2*checkListToolsMock.childCategoriesForChecklistCategory(*_) >>> [[repositoryItemMock],[null]]
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>null
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		1* sitemapToolsMock.appendSitemapHeader(*_)
		//2*repositoryItemMock.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL)
	}
	def "generateSitemapUrls,we pass siteid as BedBathUS in generateSEOUrlForChildCategory method and execute else"(){
		given:
		testObj.isLoggingDebug()>>true
		siteManagerMock.getSite(*_)>>repositoryItemMock
		mockObjects()
		repositoryViewMock.executeQuery(*_)>>>[[repositoryItemMock,null],null]
		testObj.getSitemapURL(*_) >>'/Checklist/FineDining'
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>items
		repositoryItemMock.getRepositoryId() >>'BedBathUS'
		testObj.setWebApp(webAppMock)
		webAppMock.getContextRoot() >> ""
		testObj.setUrlPrefix("/")
		parameterObjects()
		sitemapToolsMock.validateSitemapSize(*_)>>false
		checkListToolsMock.childCategoriesForChecklistCategory(*_) >> null
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>null
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		2* sitemapToolsMock.appendSitemapHeader(*_)
	}
	def "generateSitemapUrls,we pass siteid as baby in generateSEOUrlForChildCategory method and we pass checklistSEOUrl as null"(){
		given:
		testObj=Spy()
		testObj.isLoggingDebug()>>true
		siteManagerMock.getSite(*_)>>repositoryItemMock
		mockObjects()
		repositoryViewMock.executeQuery(*_)>>>[[repositoryItemMock,null],null]
		testObj.getSitemapURL(*_) >>{}
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>items
		repositoryItemMock.getRepositoryId() >>>[null,'BuyBuyBaby']
		parameterObjects()
		1*sitemapToolsMock.validateSitemapSize(*_)>>false
		1*checkListToolsMock.childCategoriesForChecklistCategory(*_) >> null
		2*repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>'/Dinnerware'
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		2* sitemapToolsMock.appendSitemapHeader(*_)
		0* webAppMock.getContextRoot()
	}
	def "generateSitemapUrls,we pass site as null in generateSEOUrlForChildCategory and  generateSiteMapUrlXMLs method "(){
		given:
		items.add(repositoryItemMock)
		testObj.isLoggingDebug()>>true
		siteManagerMock.getSite(*_)>>null
		mockObjects()
		repositoryViewMock.executeQuery(*_)>>>[[repositoryItemMock,null],null]
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>items
		repositoryItemMock.getRepositoryId() >>'BedBathCanada'
		parameterObjects()
		sitemapToolsMock.validateSitemapSize(*_)>>false
		2*checkListToolsMock.childCategoriesForChecklistCategory(*_) >> null
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>'/Dinnerware'
	
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		3* sitemapToolsMock.appendSitemapHeader(*_)
		1*siteManagerMock.getSite(*_)
	}
	def "generateSitemapUrls,we pass site as null and getSitemapURL as null in generateSEOUrlForChildCategory and  generateSiteMapUrlXMLs method  "(){
		given:
		testObj=Spy()
		testObj.isLoggingDebug()>>true
		siteManagerMock.getSite(*_)>>null
		mockObjects()
		repositoryViewMock.executeQuery(*_)>>>[[repositoryItemMock,null],null]
		testObj.getSitemapURL(*_) >> null
		testObj.getCheckListTools() >> checkListToolsMock
		checkListToolsMock.childCategoriesForChecklist(repositoryItemMock) >>items
		repositoryItemMock.getRepositoryId() >>'BedBathCanada'
		1*sitemapToolsMock.validateSitemapSize(*_)>>false
		1*checkListToolsMock.childCategoriesForChecklistCategory(*_) >> null
		repositoryItemMock.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)>>'/Dinnerware'
		parameterObjects()
		when:
		testObj.generateSitemapUrls("checkList", sitemapGeneratorServiceMock, "BedBathUS")
		then:
		2* sitemapToolsMock.appendSitemapHeader(*_)
	}

	public parameterObjects() {
		siteURLManagerMock.getProductionSiteBaseURL(*_) >>'/Checklist/FineDining'
		testObj.setPriority(133.23)
		sitemapToolsMock.generateSitemapUrlXml(_,_, _, _)>> '/fine'
		sitemapGeneratorServiceMock.getMaxUrlsPerSitemap()>> 1
		checkListToolsMock.formatUrlParam(*_)>>'/Dinnerware'
	}
	public mockObjects() {
		items.add(repositoryItemMock)
		siteToBBBSiteMap.put('BedBathUS','BedBathUS')
		testObj.setSiteToBBBSiteMap(siteToBBBSiteMap)
		testObj.getSiteToBBBSiteMap().get('BedBathUS')
		testObj.setSourceRepository(GSARepositoryMock)
		GSARepositoryMock.getView(*_) >> repositoryViewMock
		sitemapGeneratorServiceMock.getSitemapTools() >> sitemapToolsMock
		sitemapGeneratorServiceMock.getSiteURLManager() >> siteURLManagerMock
		siteURLManagerMock.getSiteManager() >> siteManagerMock
		repositoryViewMock.getQueryBuilder() >> queryBuilderMock
		queryBuilderMock.createPropertyQueryExpression(*_) >> queryExpressionMock
		queryBuilderMock.createConstantQueryExpression(*_) >> queryExpressionMock
		queryBuilderMock.createPatternMatchQuery(*_) >> queryMock
		testObj.getTransactionManager() >> transactionManagerMock
	}
}



