package com.bbb.cms.droplet

import java.util.List;
import java.util.Map

import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.RegistryTemplateVO;
import com.bbb.cms.manager.RegistryTemplateManager
import com.bbb.commerce.browse.droplet.ConfigURLDroplet;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import spock.lang.specification.BBBExtendedSpec;



class RegistryTemplateDropletSpecification extends BBBExtendedSpec {
	
	
	

	def RegistryTemplateManager registryTemplateManagerMock
	def BBBCatalogTools catalogToolsMock 
	
	def RegistryTemplateDroplet testObj
	String regChklstPdfKey="regChklstPdfKey"
	String collgChklstPdfKey="collgChklstPdfKey"
	def setup() {
		catalogToolsMock = Mock()
		registryTemplateManagerMock = Mock()
		testObj= Spy()
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setRegistryTemplateManager(registryTemplateManagerMock)
	}
	
	def "Check when pageName is null" () {
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* requestMock.serviceParameter("empty", requestMock, responseMock);
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
	}
	def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others" () {
		given:
		
		String siteId="BedBathUS"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
		registryTemplateVO.setPageName(pageName)
		registryTemplateVO.setBbbPageName(bbbPageName)
		
		List<String> registryPdfURLList=new ArrayList<String>()
		registryPdfURLList.add(registryPdfURL)
		
		List<String> collgChklstPdfKeyList=new ArrayList<String>()
		collgChklstPdfKeyList.add(collegePdfURL)
		
		List<String> pageAttributesList = new ArrayList<String>();		
		pageAttributesList.add(pageAttributes)
		
		when:
		  
			
			1* testObj.getCurrentSiteId() >> null
			1* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			2* testObj.getRegChklstPdfKey() >> regChklstPdfKey
			2* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			1* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
			1* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
			1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
			1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* requestMock.setParameter("registryPdfURL", registryPdfURL)
			1* requestMock.setParameter("collegePdfURL", collegePdfURL)
			1* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
			1* testObj.logDebug("pageVariation from config keys : " + pageVariation);
			registryTemplateVO.getPageVariation() == pageVariation
			registryTemplateVO.getPageWrapper()== pageWrapper
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
			
	}
	
	def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | getCurrentSiteId() return not null" () {
		given:
		
		String siteId="BedBathUS"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
		registryTemplateVO.setPageName(pageName)
		registryTemplateVO.setBbbPageName(bbbPageName)
		registryTemplateVO.setPageType("pageType")
		List<String> registryPdfURLList=new ArrayList<String>()
		registryPdfURLList.add(registryPdfURL)
		
		List<String> collgChklstPdfKeyList=new ArrayList<String>()
		collgChklstPdfKeyList.add(collegePdfURL)
		
		List<String> pageAttributesList = new ArrayList<String>();
		pageAttributesList.add(pageAttributes)
		
		when:
			
			0* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			1* testObj.getCurrentSiteId() >>siteId 
			2* testObj.getRegChklstPdfKey() >> regChklstPdfKey
			2* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			1* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
			1* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
			1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
			1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* requestMock.setParameter("registryPdfURL", registryPdfURL)
			1* requestMock.setParameter("collegePdfURL", collegePdfURL)
			1* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
			1* testObj.logDebug("pageVariation from config keys : " + pageVariation);
			registryTemplateVO.getPageVariation() == pageVariation
			registryTemplateVO.getPageWrapper()== pageWrapper
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
			
	}
	
	def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | getCurrentSiteId() return not null|BuyBuyBaby" () {
		given:
		
		String siteId="BuyBuyBaby"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
		registryTemplateVO.setPageName(pageName)
		registryTemplateVO.setBbbPageName(bbbPageName)
		registryTemplateVO.setPageType("pageType")
		List<String> registryPdfURLList=new ArrayList<String>()
		registryPdfURLList.add(registryPdfURL)
		
		List<String> collgChklstPdfKeyList=new ArrayList<String>()
		collgChklstPdfKeyList.add(collegePdfURL)
		
		List<String> pageAttributesList = new ArrayList<String>();
		pageAttributesList.add(pageAttributes)
		
		when:
			
			0* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			1* testObj.getCurrentSiteId() >>siteId
			2* testObj.getRegChklstPdfKey() >> regChklstPdfKey
			2* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			1* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
			1* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
			1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
			1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* requestMock.setParameter("registryPdfURL", registryPdfURL)
			1* requestMock.setParameter("collegePdfURL", collegePdfURL)
			1* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
			1* testObj.logDebug("pageVariation from config keys : " + pageVariation);
			registryTemplateVO.getPageVariation() != pageVariation
			registryTemplateVO.getPageWrapper()== pageWrapper
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
			
	}
	
	def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | getCurrentSiteId() return not null|BedBathCanada" () {
		given:
		
		String siteId="BedBathCanada"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
		registryTemplateVO.setPageName(pageName)
		registryTemplateVO.setBbbPageName(bbbPageName)
		registryTemplateVO.setPageType("pageType")
		List<String> registryPdfURLList=new ArrayList<String>()
		registryPdfURLList.add(registryPdfURL)
		
		List<String> collgChklstPdfKeyList=new ArrayList<String>()
		collgChklstPdfKeyList.add(collegePdfURL)
		
		List<String> pageAttributesList = new ArrayList<String>();
		pageAttributesList.add(pageAttributes)
		
		when:
			
			0* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			1* testObj.getCurrentSiteId() >>siteId
			2* testObj.getRegChklstPdfKey() >> regChklstPdfKey
			2* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			1* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
			1* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
			1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
			1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* requestMock.setParameter("registryPdfURL", registryPdfURL)
			1* requestMock.setParameter("collegePdfURL", collegePdfURL)
			1* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
			1* testObj.logDebug("pageVariation from config keys : " + pageVariation);
			registryTemplateVO.getPageVariation() == pageVariation
			registryTemplateVO.getPageWrapper()== pageWrapper
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
			
	}
	
	
	def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | getCurrentSiteId() return not null and pageAttributes is Empty" () {
		given:
		
		String siteId="BedBathUS"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
		registryTemplateVO.setPageName(pageName)
		registryTemplateVO.setBbbPageName(bbbPageName)
		
		List<String> registryPdfURLList=new ArrayList<String>()
		registryPdfURLList.add(registryPdfURL)
		
		List<String> collgChklstPdfKeyList=new ArrayList<String>()
		collgChklstPdfKeyList.add(collegePdfURL)
		
		List<String> pageAttributesList = new ArrayList<String>();
		//pageAttributesList.add(pageAttributes)
		
		when:
			
			0* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			1* testObj.getCurrentSiteId() >>siteId
			2* testObj.getRegChklstPdfKey() >> regChklstPdfKey
			2* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			1* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
			1* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
			1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
			1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			1* requestMock.setParameter("registryPdfURL", registryPdfURL)
			1* requestMock.setParameter("collegePdfURL", collegePdfURL)
			0* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
			0* testObj.logDebug("pageVariation from config keys : " + pageVariation);
			registryTemplateVO.getPageVariation() == pageVariation
			registryTemplateVO.getPageWrapper()!= pageWrapper
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
			
	}
	
	def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | getRegChklstPdfKey return  null" () {
		given:
		
		String siteId="BedBathUS"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
		registryTemplateVO.setPageName(pageName)
		registryTemplateVO.setBbbPageName(bbbPageName)
		
		List<String> registryPdfURLList=new ArrayList<String>()
		registryPdfURLList.add(registryPdfURL)
		
		List<String> collgChklstPdfKeyList=new ArrayList<String>()
		collgChklstPdfKeyList.add(collegePdfURL)
		
		List<String> pageAttributesList = new ArrayList<String>();
		pageAttributesList.add(pageAttributes)
		
		when:
			
			0* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			1* testObj.getCurrentSiteId() >>siteId
			1* testObj.getRegChklstPdfKey() >> null
			1* testObj.getCollgChklstPdfKey() >> null
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			0* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
			0* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
			1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
			1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			0* requestMock.setParameter("registryPdfURL", registryPdfURL)
			0* requestMock.setParameter("collegePdfURL", collegePdfURL)
			1* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
			1* testObj.logDebug("pageVariation from config keys : " + pageVariation);
			registryTemplateVO.getPageVariation() == pageVariation
			registryTemplateVO.getPageWrapper()== pageWrapper
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
			
	}
	
	def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| BBBSystemException during invocation of getContentCatalogConfigration" () {
		given:
		
		String siteId="BedBathUS"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
		registryTemplateVO.setPageName(pageName)
		registryTemplateVO.setBbbPageName(bbbPageName)
		
		List<String> registryPdfURLList=new ArrayList<String>()
		registryPdfURLList.add(registryPdfURL)
		
		List<String> collgChklstPdfKeyList=new ArrayList<String>()
		collgChklstPdfKeyList.add(collegePdfURL)
		
		List<String> pageAttributesList = new ArrayList<String>();
		pageAttributesList.add(pageAttributes)
		
		when:
			
			0* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			1* testObj.getCurrentSiteId() >>siteId
			2* testObj.getRegChklstPdfKey() >> regChklstPdfKey
			2* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			1* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >>  {throw new BBBSystemException("BBBSystemException")} 
			1* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> {throw new BBBSystemException("BBBSystemException")} 
			1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
			1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
			1* requestMock.serviceParameter("output", requestMock, responseMock)
			0* requestMock.setParameter("registryPdfURL", registryPdfURL)
			1* testObj.logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.service() | BBBSystemException ","catalog_1048"), _);
			0* requestMock.setParameter("collegePdfURL", collegePdfURL)
			1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "RegistryTemplateDroplet.service() | BBBSystemException ","catalog_1050"), _);
			1* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
			1* testObj.logDebug("pageVariation from config keys : " + pageVariation);
			registryTemplateVO.getPageVariation() == pageVariation
			registryTemplateVO.getPageWrapper()== pageWrapper
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
			
	}

def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| BBBBusinessException during invocation of getContentCatalogConfigration" () {
	given:
	
	String siteId="BedBathUS"
	 
	String collegePdfURL="collegePdfURL"
	String registryPdfURL="registryPdfURL"
	
	

	String pageName="Others"
	String bbbPageName="bbbPageName"
	String pageTitle="pageTitle"
	String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
	String pageHeaderCopy="pageHeaderCopy"
	String promoImageURL="promoImageURL"
	String promoImageAltText="promoImageAltText"
	String pageCopy="pageCopy"
	String pageType="pageType"
	
	String pageWrapper="pageWrapper"
	String pageVariation="br"
	
	String headTagContent="headTagContent"
	String bodyEndTagContent="bodyEndTagContent"
	
	
	String pageAttributes=pageWrapper+","+pageVariation
	
	
	RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
	registryTemplateVO.setPageName(pageName)
	registryTemplateVO.setBbbPageName(bbbPageName)
	
	List<String> registryPdfURLList=new ArrayList<String>()
	registryPdfURLList.add(registryPdfURL)
	
	List<String> collgChklstPdfKeyList=new ArrayList<String>()
	collgChklstPdfKeyList.add(collegePdfURL)
	
	List<String> pageAttributesList = new ArrayList<String>();
	pageAttributesList.add(pageAttributes)
	
	when:
		
		0* requestMock.getParameter("siteId") >> siteId
		2* requestMock.getParameter("pageName") >> pageName
		1* testObj.getCurrentSiteId() >>siteId
		2* testObj.getRegChklstPdfKey() >> regChklstPdfKey
		2* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
		1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
		1* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >>  {throw new BBBBusinessException("BBBBusinessException")}
		1* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >>{throw new BBBBusinessException("BBBBusinessException")}
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> pageAttributesList
		testObj.service(requestMock, responseMock)
		
		
	then:
		1* testObj.logDebug("starting method RegistryTemplateDroplet")
		1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
		1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		0* requestMock.setParameter("registryPdfURL", registryPdfURL)
		1* testObj.logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.service() | BBBBusinessException ","catalog_1049"), _);
		0* requestMock.setParameter("collegePdfURL", collegePdfURL)
		1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "RegistryTemplateDroplet.service() | BBBBusinessException ","catalog_1051"), _);
		1* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
		1* testObj.logDebug("pageVariation from config keys : " + pageVariation);
		registryTemplateVO.getPageVariation() == pageVariation
		registryTemplateVO.getPageWrapper()== pageWrapper
		1* testObj.logDebug("Existing method RegistryTemplateDroplet")
		
}
def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | BBBBusinessException during invocation of getAllValuesForKey" () {
	given:
	
	String siteId="BedBathUS"
	 
	String collegePdfURL="collegePdfURL"
	String registryPdfURL="registryPdfURL"
	
	

	String pageName="Others"
	String bbbPageName="bbbPageName"
	String pageTitle="pageTitle"
	String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
	String pageHeaderCopy="pageHeaderCopy"
	String promoImageURL="promoImageURL"
	String promoImageAltText="promoImageAltText"
	String pageCopy="pageCopy"
	String pageType="pageType"
	
	String pageWrapper="pageWrapper"
	String pageVariation="br"
	
	String headTagContent="headTagContent"
	String bodyEndTagContent="bodyEndTagContent"
	
	
	String pageAttributes=pageWrapper+","+pageVariation
	
	
	RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
	registryTemplateVO.setPageName(pageName)
	registryTemplateVO.setBbbPageName(bbbPageName)
	registryTemplateVO.setPageType("College")
	
	List<String> registryPdfURLList=new ArrayList<String>()
	registryPdfURLList.add(registryPdfURL)
	
	List<String> collgChklstPdfKeyList=new ArrayList<String>()
	collgChklstPdfKeyList.add(collegePdfURL)
	
	List<String> pageAttributesList = new ArrayList<String>();
	pageAttributesList.add(pageAttributes)
	
	when:
		
		0* requestMock.getParameter("siteId") >> siteId
		2* requestMock.getParameter("pageName") >> pageName
		1* testObj.getCurrentSiteId() >>siteId
		1* testObj.getRegChklstPdfKey() >> null
		1* testObj.getCollgChklstPdfKey() >> null
		1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
		0* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
		0* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> {throw new BBBBusinessException("BBBBusinessException")}
		testObj.service(requestMock, responseMock)
		
		
	then:
		1* testObj.logDebug("starting method RegistryTemplateDroplet")
		1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
		1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		0* requestMock.setParameter("registryPdfURL", registryPdfURL)
		0* requestMock.setParameter("collegePdfURL", collegePdfURL)
		0* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
		0* testObj.logDebug("pageVariation from config keys : " + pageVariation);
		1* testObj.logDebug("No Value found for PagethemeKeys " + bbbPageName);
		registryTemplateVO.getPageVariation() != pageVariation
		registryTemplateVO.getPageWrapper() != pageWrapper
		1* testObj.logDebug("Existing method RegistryTemplateDroplet")
		
   }
def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | BBBSystemException during invocation of getAllValuesForKey" () {
	given:
	
	String siteId="BedBathUS"
	 
	String collegePdfURL="collegePdfURL"
	String registryPdfURL="registryPdfURL"
	
	

	String pageName="Others"
	String bbbPageName="bbbPageName"
	String pageTitle="pageTitle"
	String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
	String pageHeaderCopy="pageHeaderCopy"
	String promoImageURL="promoImageURL"
	String promoImageAltText="promoImageAltText"
	String pageCopy="pageCopy"
	String pageType="pageType"
	
	String pageWrapper="pageWrapper"
	String pageVariation="br"
	
	String headTagContent="headTagContent"
	String bodyEndTagContent="bodyEndTagContent"
	
	
	String pageAttributes=pageWrapper+","+pageVariation
	
	
	RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
	registryTemplateVO.setPageName(pageName)
	registryTemplateVO.setBbbPageName(bbbPageName)
	registryTemplateVO.setPageType("College")
	List<String> registryPdfURLList=new ArrayList<String>()
	registryPdfURLList.add(registryPdfURL)
	
	List<String> collgChklstPdfKeyList=new ArrayList<String>()
	collgChklstPdfKeyList.add(collegePdfURL)
	
	List<String> pageAttributesList = new ArrayList<String>();
	pageAttributesList.add(pageAttributes)
	
	when:
		
		0* requestMock.getParameter("siteId") >> siteId
		2* requestMock.getParameter("pageName") >> pageName
		1* testObj.getCurrentSiteId() >>siteId
		1* testObj.getRegChklstPdfKey() >> null
		1* testObj.getCollgChklstPdfKey() >> null
		1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
		0* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
		0* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> {throw new BBBSystemException("BBBSystemException")}
		testObj.service(requestMock, responseMock)
		
		
	then:
		1* testObj.logDebug("starting method RegistryTemplateDroplet")
		1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
		1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		0* requestMock.setParameter("registryPdfURL", registryPdfURL)
		0* requestMock.setParameter("collegePdfURL", collegePdfURL)
		0* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
		0* testObj.logDebug("pageVariation from config keys : " + pageVariation);
		1* testObj.logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.setPageTheme() | BBBSystemException ","catalog_1049"), _)
		registryTemplateVO.getPageVariation() != pageVariation
		registryTemplateVO.getPageWrapper() != pageWrapper
		1* testObj.logDebug("Existing method RegistryTemplateDroplet")
		
   }
def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others and pageType Reg| BBBSystemException during invocation of getAllValuesForKey" () {
	given:
	
	String siteId="BedBathUS"
	 
	String collegePdfURL="collegePdfURL"
	String registryPdfURL="registryPdfURL"
	
	

	String pageName="Others"
	String bbbPageName="bbbPageName"
	String pageTitle="pageTitle"
	String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
	String pageHeaderCopy="pageHeaderCopy"
	String promoImageURL="promoImageURL"
	String promoImageAltText="promoImageAltText"
	String pageCopy="pageCopy"
	String pageType="pageType"
	
	String pageWrapper="pageWrapper"
	String pageVariation="br"
	
	String headTagContent="headTagContent"
	String bodyEndTagContent="bodyEndTagContent"
	
	
	String pageAttributes=pageWrapper+","+pageVariation
	
	
	RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
	registryTemplateVO.setPageName(pageName)
	registryTemplateVO.setBbbPageName(bbbPageName)
	registryTemplateVO.setPageType("Registry")
	List<String> registryPdfURLList=new ArrayList<String>()
	registryPdfURLList.add(registryPdfURL)
	
	List<String> collgChklstPdfKeyList=new ArrayList<String>()
	collgChklstPdfKeyList.add(collegePdfURL)
	
	List<String> pageAttributesList = new ArrayList<String>();
	pageAttributesList.add(pageAttributes)
	
	when:
		
		0* requestMock.getParameter("siteId") >> siteId
		2* requestMock.getParameter("pageName") >> pageName
		1* testObj.getCurrentSiteId() >>siteId
		1* testObj.getRegChklstPdfKey() >> null
		1* testObj.getCollgChklstPdfKey() >> null
		1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
		0* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
		0* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName) >> {throw new BBBSystemException("BBBSystemException")}
		testObj.service(requestMock, responseMock)
		
		
	then:
		1* testObj.logDebug("starting method RegistryTemplateDroplet")
		1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
		1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		0* requestMock.setParameter("registryPdfURL", registryPdfURL)
		0* requestMock.setParameter("collegePdfURL", collegePdfURL)
		0* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
		0* testObj.logDebug("pageVariation from config keys : " + pageVariation);
		1* testObj.logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.setPageTheme() | BBBSystemException ","catalog_1049"), _)
		registryTemplateVO.getPageVariation() == pageVariation
		registryTemplateVO.getPageWrapper() != pageWrapper
		1* testObj.logDebug("Existing method RegistryTemplateDroplet")
		
   }

def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=NotOthers | BBBSystemException during invocation of getAllValuesForKey" () {
	given:
	
	String siteId="BedBathUS"
	 
	String collegePdfURL="collegePdfURL"
	String registryPdfURL="registryPdfURL"
	
	

	String pageName="NotOthers"
	String bbbPageName="bbbPageName"
	String pageTitle="pageTitle"
	String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
	String pageHeaderCopy="pageHeaderCopy"
	String promoImageURL="promoImageURL"
	String promoImageAltText="promoImageAltText"
	String pageCopy="pageCopy"
	String pageType="pageType"
	
	String pageWrapper="pageWrapper"
	String pageVariation="br"
	
	String headTagContent="headTagContent"
	String bodyEndTagContent="bodyEndTagContent"
	
	
	String pageAttributes=pageWrapper+","+pageVariation
	
	
	RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
	registryTemplateVO.setPageName(pageName)
	registryTemplateVO.setBbbPageName(bbbPageName)
	registryTemplateVO.setPageType("Registry")
	List<String> registryPdfURLList=new ArrayList<String>()
	registryPdfURLList.add(registryPdfURL)
	
	List<String> collgChklstPdfKeyList=new ArrayList<String>()
	collgChklstPdfKeyList.add(collegePdfURL)
	
	List<String> pageAttributesList = new ArrayList<String>();
	pageAttributesList.add(pageAttributes)
	
	when:
		
		0* requestMock.getParameter("siteId") >> siteId
		2* requestMock.getParameter("pageName") >> pageName
		1* testObj.getCurrentSiteId() >>siteId
		1* testObj.getRegChklstPdfKey() >> null
		1* testObj.getCollgChklstPdfKey() >> null
		1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
		0* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
		0* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, pageName) >> {throw new BBBSystemException("BBBSystemException")}
		testObj.service(requestMock, responseMock)
		
		
	then:
		1* testObj.logDebug("starting method RegistryTemplateDroplet")
		1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
		1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		0* requestMock.setParameter("registryPdfURL", registryPdfURL)
		0* requestMock.setParameter("collegePdfURL", collegePdfURL)
		0* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
		0* testObj.logDebug("pageVariation from config keys : " + pageVariation);
		1* testObj.logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.setPageTheme() | BBBSystemException ","catalog_1049"), _)
		registryTemplateVO.getPageVariation() == pageVariation
		registryTemplateVO.getPageWrapper() != pageWrapper
		1* testObj.logDebug("Existing method RegistryTemplateDroplet")
		
   }
def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=NotOthers | BBBBusinessException during invocation of getAllValuesForKey" () {
	given:
	
	String siteId="BedBathUS"
	 
	String collegePdfURL="collegePdfURL"
	String registryPdfURL="registryPdfURL"
	
	

	String pageName="NotOthers"
	String bbbPageName="bbbPageName"
	String pageTitle="pageTitle"
	String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
	String pageHeaderCopy="pageHeaderCopy"
	String promoImageURL="promoImageURL"
	String promoImageAltText="promoImageAltText"
	String pageCopy="pageCopy"
	String pageType="pageType"
	
	String pageWrapper="pageWrapper"
	String pageVariation="br"
	
	String headTagContent="headTagContent"
	String bodyEndTagContent="bodyEndTagContent"
	
	
	String pageAttributes=pageWrapper+","+pageVariation
	
	
	RegistryTemplateVO registryTemplateVO = new RegistryTemplateVO()
	registryTemplateVO.setPageName(pageName)
	registryTemplateVO.setBbbPageName(bbbPageName)
	registryTemplateVO.setPageType("Registry")
	
	List<String> registryPdfURLList=new ArrayList<String>()
	registryPdfURLList.add(registryPdfURL)
	
	List<String> collgChklstPdfKeyList=new ArrayList<String>()
	collgChklstPdfKeyList.add(collegePdfURL)
	
	List<String> pageAttributesList = new ArrayList<String>();
	pageAttributesList.add(pageAttributes)
	
	when:
		
		0* requestMock.getParameter("siteId") >> siteId
		2* requestMock.getParameter("pageName") >> pageName
		1* testObj.getCurrentSiteId() >>siteId
		1* testObj.getRegChklstPdfKey() >> null
		1* testObj.getCollgChklstPdfKey() >> null
		1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
		0* catalogToolsMock.getContentCatalogConfigration(regChklstPdfKey) >> registryPdfURLList
		0* catalogToolsMock.getContentCatalogConfigration(collgChklstPdfKey) >> collgChklstPdfKeyList
		1* catalogToolsMock.getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, pageName) >> {throw new BBBBusinessException("BBBBusinessException")}
		testObj.service(requestMock, responseMock)
		
		
	then:
		1* testObj.logDebug("starting method RegistryTemplateDroplet")
		1* testObj.logDebug("Received RegistryTemplateVO : "+registryTemplateVO)
		1* requestMock.setParameter("RegistryTemplateVO", registryTemplateVO)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		0* requestMock.setParameter("registryPdfURL", registryPdfURL)
		0* requestMock.setParameter("collegePdfURL", collegePdfURL)
		0* testObj.logDebug("pageWrapper from config keys  : " + pageWrapper);
		0* testObj.logDebug("pageVariation from config keys : " + pageVariation);
		1* testObj.logDebug("No Value found for PagethemeKeys " + pageName);
		registryTemplateVO.getPageVariation() == pageVariation
		registryTemplateVO.getPageWrapper() != pageWrapper
		1* testObj.logDebug("Existing method RegistryTemplateDroplet")
		
   }



		def "Check when pageName ,siteId , regPageNum  and RegistryTemplateData is not null| pageName=Others | RegistryTemplateData is null" () {
		given:
		
		String siteId="BedBathUS"
		 
		String collegePdfURL="collegePdfURL"
		String registryPdfURL="registryPdfURL"
		
		
	
		String pageName="Others"
		String bbbPageName="bbbPageName"
		String pageTitle="pageTitle"
		String pageHeaderFeaturedContent="pageHeaderFeaturedContent"
		String pageHeaderCopy="pageHeaderCopy"
		String promoImageURL="promoImageURL"
		String promoImageAltText="promoImageAltText"
		String pageCopy="pageCopy"
		String pageType="pageType"
		
		String pageWrapper="pageWrapper"
		String pageVariation="br"
		
		String headTagContent="headTagContent"
		String bodyEndTagContent="bodyEndTagContent"
		
		
		String pageAttributes=pageWrapper+","+pageVariation
		
		
		RegistryTemplateVO registryTemplateVO = null
		
		when:
			
			0* requestMock.getParameter("siteId") >> siteId
			2* requestMock.getParameter("pageName") >> pageName
			1* testObj.getCurrentSiteId() >>siteId
			0* testObj.getRegChklstPdfKey() >> regChklstPdfKey
			0* testObj.getCollgChklstPdfKey() >> collgChklstPdfKey
			1* registryTemplateManagerMock.getRegistryTemplateData(pageName, siteId) >> registryTemplateVO
			1* testObj.logDebug("Received RegistryTemplateVO as Null: ");
			1* requestMock.serviceParameter("empty", requestMock, responseMock);
			
			testObj.service(requestMock, responseMock)
			
			
		then:
			1* testObj.logDebug("starting method RegistryTemplateDroplet")
			1* testObj.logDebug("Existing method RegistryTemplateDroplet")
	}
	
   def "test setter/Getter"(){
	   given:
	   String regChklstPdfKey="RegChklstPdfKey"
	   String collgChklstPdfKey="collgChklstPdfKey"
	   
	   testObj.setRegChklstPdfKey(regChklstPdfKey)
	   testObj.setCollgChklstPdfKey(collgChklstPdfKey)
	   testObj.setRegistryTemplateManager(registryTemplateManagerMock)
	   testObj.setCatalogTools(catalogToolsMock)
	   expect:
	    regChklstPdfKey== testObj.getRegChklstPdfKey()
		collgChklstPdfKey== testObj.getCollgChklstPdfKey()
		registryTemplateManagerMock == testObj.getRegistryTemplateManager()
		catalogToolsMock == testObj.getCatalogTools()
   }
}
