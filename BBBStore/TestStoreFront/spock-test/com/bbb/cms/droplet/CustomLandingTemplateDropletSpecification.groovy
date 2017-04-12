package com.bbb.cms.droplet

import java.util.Map;
import javax.servlet.ServletException

import com.bbb.cms.manager.ContentTemplateManager
import com.bbb.cms.vo.CLPResponseVO;
import com.bbb.cms.vo.CMSResponseVO
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;
import com.bbb.repository.RepositoryItemMock;
import com.bbb.utils.BBBUtility

import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;

class CustomLandingTemplateDropletSpecification extends BBBExtendedSpec {
	
	def ContentTemplateManager contentTemplateMgrMock = Mock()
	
	def BBBCatalogTools catalogToolMock = Mock()
	
	def CustomLandingTemplateDroplet testObj
	
	def setup(){		
		testObj=Spy()
		
		testObj.setCatalogTools(catalogToolMock)
		testObj.setContentTemplateMgr(contentTemplateMgrMock)
	}
	
	def"when clpName and categoryId is not null and Site is BedBathUs"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
		 	 String name = "name"
		 	 String altURLRequired = "true"
		 	 String omnitureFlag = "true"
			 String siteId="BedBathUs"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = true
			 String canadaAlternateURL="http://bbby.com/canadaAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
		 	 requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 1* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			1* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"when clpName and categoryId is not null ,name is null and Site is BedBathUs && altURLRequired is null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = null
			  String altURLRequired = null
			  String omnitureFlag = "true"
			 String siteId="BedBathUs"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = true
			 String canadaAlternateURL="http://bbby.com/canadaAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 0* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 0* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 0* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			0* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			0* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"when clpName and categoryId is not null and Site is BedBathUs && altURLRequired is false"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "false"
			  String omnitureFlag = "true"
			 String siteId="BedBathUs"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = true
			 String canadaAlternateURL="http://bbby.com/canadaAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 0* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 0* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 0* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			0* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			0* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"when clpName and categoryId is not null and Site is BedBathUs && canadaAlternateURL is null and omnitureFlag is null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = null
			 String siteId="BedBathUs"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = true
			 String canadaAlternateURL=null
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 0* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 0* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 0* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 1* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			0* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			0* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			0* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			0* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"when clpName and categoryId is not null and Site is BedBathCanada && usAlternateURL is null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathCanada"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistUS = true
			 String usAlternateURL=null
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,usCategoryId) >> templateExistUS
			 1* contentTemplateMgrMock.createCanonicalURL(usCategoryId, templateName) >> usAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - usAlternateURL :: " + usAlternateURL)
			0* requestMock.setParameter ("usAlternateURL", usAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"when clpName and categoryId is not null and Site is BedBathCanada && templateExistUS is false"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathCanada"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistUS = false
			 String usAlternateURL=null
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			 requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,usCategoryId) >> templateExistUS
			 0* contentTemplateMgrMock.createCanonicalURL(usCategoryId, templateName) >> usAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - usAlternateURL :: " + usAlternateURL)
			0* requestMock.setParameter ("usAlternateURL", usAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	
	def"when clpName and categoryId is not null and Site is BedBathUS && canadaCategoryId is null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathUS"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId=null
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistUS = false
			 String usAlternateURL=null
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			 requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 0* contentTemplateMgrMock.checkTemplateForId(templateName,usCategoryId) >> templateExistUS
			 0* contentTemplateMgrMock.createCanonicalURL(usCategoryId, templateName) >> usAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - usAlternateURL :: " + usAlternateURL)
			0* requestMock.setParameter ("usAlternateURL", usAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"when clpName and categoryId is not null and Site is BedBathCanada && usAlternateURL  and usCategoryId is null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathCanada"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId=null
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistUS = true
			 String usAlternateURL=null
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 0* contentTemplateMgrMock.checkTemplateForId(templateName,usCategoryId) >> templateExistUS
			 0* contentTemplateMgrMock.createCanonicalURL(usCategoryId, templateName) >> usAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - usAlternateURL :: " + usAlternateURL)
			0* requestMock.setParameter ("usAlternateURL", usAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}

	def"when clpName and categoryId is not null and Site is BedBathUs && omnitureFlag is false && templateExistCanada does not exist &&  alternateURL is null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "false"
			 String siteId="BedBathUs"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = false
			 String canadaAlternateURL="http://bbby.com/canadaAlternateURL/123"
			 String alternateURL=null
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 0* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 0* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 0* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 0* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			0* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			0* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			0* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			0* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			0* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}

	
	def"when clpName and categoryId is not null and Site is BedBathUs && categoryL1Name is null "(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathUs"
			 
			 String l1CategoryName=null
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = true
			 String canadaAlternateURL="http://bbby.com/canadaAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 1* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, null)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			1* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}

	
	
	
	def"when clpName and categoryId is not null and Site is BedBathCanada |when parentCategoryMap, categoryVO are null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathCanada"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistUS = true
			 String usAlternateURL="http://bbby.com/usAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = null
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO= null
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 
			 
			 
			 RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,usCategoryId) >> templateExistUS
			 1* contentTemplateMgrMock.createCanonicalURL(usCategoryId, templateName) >> usAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, "")
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, "")
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, "")
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - usAlternateURL :: " + usAlternateURL)
			1* requestMock.setParameter ("usAlternateURL", usAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"when clpName and categoryId is not null and Site is BedBathCanada && when category is null in parentCategoryMap && categoryL3Name equals to categoryL1Name "(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathCanada"
			 
			 String l1CategoryName="l1l3HaveSameName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l1l3HaveSameName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistUS = true
			 String usAlternateURL="http://bbby.com/usAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", null)
			 parentCategoryMap.put("1", l1_category)
			 
			 RepositoryItem resposeItem1= Mock()
			 
		
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			 responseVO.setResponseItems(resposeItem)
			
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 1* contentTemplateMgrMock.checkTemplateForId(templateName,usCategoryId) >> templateExistUS
			 1* contentTemplateMgrMock.createCanonicalURL(usCategoryId, templateName) >> usAlternateURL
			 1* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, "")
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, "")
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - usAlternateURL :: " + usAlternateURL)
			1* requestMock.setParameter ("usAlternateURL", usAlternateURL)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			1* requestMock.setParameter ("alternateURL", alternateURL)
			1* requestMock.setParameter ("clpName", clpName)
			1* requestMock.setParameter ("clpTitle", clpTitle)
			1* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			1* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	
	def"when clpName and categoryId is not null and Site is BedBathUs && responseVO dont have ResponseItems"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathUs"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = true
			 String canadaAlternateURL="http://bbby.com/canadaAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = new CMSResponseVO()
			// responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 0* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 0* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 0* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			0* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			0* requestMock.setParameter ("alternateURL", alternateURL)
			0* requestMock.setParameter ("clpName", clpName)
			0* requestMock.setParameter ("clpTitle", clpTitle)
			0* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			0* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() :: Empty Response");
			1* requestMock.serviceParameter (BBBCoreConstants.EMPTY, requestMock, responseMock)
	}
	
	
	def"when clpName and categoryId is not null and Site is BedBathUs && responseVO is null"(){
		
		given:
			 String categoryId = "categoryId"
			 String templateName = "templateName"
			  String name = "name"
			  String altURLRequired = "true"
			  String omnitureFlag = "true"
			 String siteId="BedBathUs"
			 
			 String l1CategoryName="l1CategoryName"
			 String l2CategoryName="l2CategoryName"
			 String l3CategoryName="l3CategoryName"
			 
			 String canadaCategoryId="canadaCategoryId"
			 String usCategoryId="usCategoryId"
			 
			 String bannerContent="bannerContent"
			 String cssFilePath="cssFilePath"
			 String jsFilePath="jsFilePath"
			 
			 
			 
			 boolean templateExistCanada = true
			 String canadaAlternateURL="http://bbby.com/canadaAlternateURL/123"
			 String alternateURL="http://bbby.com/alternateURL/123"
			  
			 String clpName="clpName"
			  String clpTitle ="clpTitle"
			  
			 Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>()
			 
			 CategoryVO l1_category=  new CategoryVO()
			 CategoryVO l2_category=  new CategoryVO()
			 CategoryVO categoryVO=  new CategoryVO()
			 
			 l1_category.setCategoryName(l1CategoryName)
			 l2_category.setCategoryName(l2CategoryName)
			 categoryVO.setCategoryName(l3CategoryName)
			 
			 parentCategoryMap.put("0", l2_category)
			 parentCategoryMap.put("1", l1_category)
			 
			RepositoryItem resposeItem1= Mock()
		 
			 
			 resposeItem1.getPropertyValue(canadaCategoryId) >> canadaCategoryId
			 resposeItem1.getPropertyValue(usCategoryId) >> usCategoryId
			 resposeItem1.getPropertyValue(clpName) >> clpName
			 resposeItem1.getPropertyValue(clpTitle) >> clpTitle
			 
			 RepositoryItem[] resposeItem= [resposeItem1]
			 CMSResponseVO responseVO = null
			// responseVO.setResponseItems(resposeItem)
			 
			 
			 categoryVO.setBannerContent(bannerContent)
			 categoryVO.setCssFilePath(cssFilePath)
			 categoryVO.setJsFilePath(jsFilePath)
				 
			 
			  requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
			 requestMock.getParameter(BBBCoreConstants.TEMPLATE_NAME) >> templateName
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.getParameter("alternateURLRequired") >> altURLRequired
			 requestMock.getParameter("omnitureFlag") >> omnitureFlag
			  
			 1* testObj.getCurrentSiteId() >> siteId
			 1* catalogToolMock.getParentCategory(categoryId, siteId) >> parentCategoryMap
			 1* contentTemplateMgrMock.getContent(templateName, "{associatedCategory:"+ categoryId + "}") >> responseVO
			 1* catalogToolMock.getCategoryDetail(siteId, categoryId,false) >> categoryVO
			 1* catalogToolMock.getBccManagedCategory(categoryVO)
			 0* contentTemplateMgrMock.checkTemplateForId(templateName,canadaCategoryId) >> templateExistCanada
			 0* contentTemplateMgrMock.createCanonicalURL(canadaCategoryId, templateName) >> canadaAlternateURL
			 0* contentTemplateMgrMock.createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel()) >>alternateURL
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId )
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input name = " + name)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
			1* testObj.logDebug("CustomLandingTemplate.service() siteId :: " + siteId )
			1* requestMock.setParameter(BBBCoreConstants.CHANNEL, _)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL1, l1CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL2, l2CategoryName)
			1* requestMock.setParameter (BBBCoreConstants.CATEGORYL3, l3CategoryName)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL)
			0* requestMock.setParameter ("canadaAlternateURL", canadaAlternateURL)
			0* testObj.logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired)
			0* requestMock.setParameter ("alternateURL", alternateURL)
			0* requestMock.setParameter ("clpName", clpName)
			0* requestMock.setParameter ("clpTitle", clpTitle)
			0* requestMock.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO)
			0* requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() :: Empty Response");
			1* requestMock.serviceParameter (BBBCoreConstants.EMPTY, requestMock, responseMock)
	}
	
	
	
	def"when clpName and categoryId is not null and Site is BedBathUs and there is BBBSystemException"(){
		
		given:
			 String categoryId = "categoryId"
			 String name = "name"
			 
			 requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
		
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.setParameter(BBBCoreConstants.CHANNEL, BBBUtility.getChannel()) >> { throw new BBBSystemException("BBBSystemException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "System Exception from service of CustomLandingTemplateDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1024),_)
			1* requestMock.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CUSTOM_LANDING)
			1* requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1* responseMock.setStatus(404)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - end")
	}
	
	
	def"when clpName and categoryId is not null and Site is BedBathUs and there is BBBBusinessException"(){
		
		given:
			 String categoryId = "categoryId"
			 String name = "name"
			 
			 requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
		
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.setParameter(BBBCoreConstants.CHANNEL, BBBUtility.getChannel()) >> { throw new BBBBusinessException("BBBBusinessException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "Business Exception from service of CustomLandingTemplateDroplet ", BBBCoreErrorConstants.BROWSE_ERROR_1025),_)
			1* requestMock.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CUSTOM_LANDING)
			1* requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1* responseMock.setStatus(404)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - end")
	}
	
	def"when clpName and categoryId is not null and Site is BedBathUs and there is RepositoryException"(){
		
		given:
			 String categoryId = "categoryId"
			 String name = "name"
			 
			 requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
		
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.setParameter(BBBCoreConstants.CHANNEL, BBBUtility.getChannel()) >> { throw new RepositoryException("RepositoryException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "System Exception from " +
					"service of CustomLandingTemplateDroplet ", BBBCoreErrorConstants.BROWSE_ERROR_1024),_);
			1* requestMock.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CUSTOM_LANDING);
			1* requestMock.serviceLocalParameter(BBBCoreConstants.ERROR,requestMock, responseMock);
			1* responseMock.setStatus(404)
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() - end")
	}
	
	def"when clpName and categoryId ,name is null and Site is BedBathUs"(){
		
		given:
			 String categoryId = null
			 String name = null
			 
			 requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> categoryId
		
			 requestMock.getParameter(BBBCoreConstants.PARAM_NAME) >> name
			 requestMock.setParameter(BBBCoreConstants.CHANNEL, BBBUtility.getChannel()) >> { throw new RepositoryException("RepositoryException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("CustomLandingTemplateDroplet.service() :: Error in Response")
			1* requestMock.serviceParameter (BBBCoreConstants.ERROR, requestMock, responseMock)
	}
	
	
	def"check getCustomLandingTemplate"(){
		
		given:
				String categoryId="categoryId"
				String templateName="templateName"
				String altURLRequired="altURLRequired"
				String clpName="clpName"
				
				CMSResponseVO responseVO = new CMSResponseVO()
			 
				 requestMock.getObjectParameter(BBBCoreConstants.PARAM_RESPONSE_VO) >> responseVO
			
		when:
				CLPResponseVO clpResponseVO = testObj.getCustomLandingTemplate(categoryId, templateName,altURLRequired,clpName)
		then:
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - categoryId :: " + categoryId)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - templateName :: " + templateName)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - altURLRequired :: " + altURLRequired)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - clpName :: " + clpName)
				1* requestMock.setParameter(BBBCoreConstants.PARAM_CATEGORY_ID, categoryId);
				1* requestMock.setParameter(BBBCoreConstants.TEMPLATE_NAME, templateName);
				1* requestMock.setParameter("alternateURLRequired", altURLRequired);
				1* requestMock.setParameter(BBBCoreConstants.PARAM_NAME, clpName)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - start")
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - end")
				clpResponseVO.getCmsResponseVO() == responseVO 
	}
	
	def"check getCustomLandingTemplate when ServletException in service method"(){
		
		given:
				String categoryId="categoryId"
				String templateName="templateName"
				String altURLRequired="altURLRequired"
				String clpName="clpName"
				
				CMSResponseVO responseVO1 = new CMSResponseVO()
			 
				1* requestMock.getObjectParameter(BBBCoreConstants.PARAM_RESPONSE_VO) >> responseVO1
				1* testObj.service(requestMock, responseMock) >> { throw new ServletException("ServletException")}
				1* requestMock.getObjectParameter("error") >> "error"
			
		when:
				CLPResponseVO responseVO = testObj.getCustomLandingTemplate(categoryId, templateName,altURLRequired,clpName)
		then:
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - categoryId :: " + categoryId)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - templateName :: " + templateName)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - altURLRequired :: " + altURLRequired)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - clpName :: " + clpName)
				1* requestMock.setParameter(BBBCoreConstants.PARAM_CATEGORY_ID, categoryId);
				1* requestMock.setParameter(BBBCoreConstants.TEMPLATE_NAME, templateName);
				1* requestMock.setParameter("alternateURLRequired", altURLRequired);
				1* requestMock.setParameter(BBBCoreConstants.PARAM_NAME, clpName)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - start")
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - end")
				2* testObj.logError(_)
				responseVO.getCmsResponseVO() == responseVO1
				responseVO.isErrorExist() == true
				responseVO.getErrorCode() == BBBCatalogErrorCodes.CLP_NOT_AVAILABLE_IN_REPO
				
	}
	
	def"check getCustomLandingTemplate when IOException in service method"(){
		
		given:
				String categoryId="categoryId"
				String templateName="templateName"
				String altURLRequired="altURLRequired"
				String clpName="clpName"
				String alternateURL="http://bbbby/alternateURL/123"
				CMSResponseVO responseVO1 = new CMSResponseVO()
			 
				1* requestMock.getObjectParameter(BBBCoreConstants.PARAM_RESPONSE_VO) >> responseVO1
				1* testObj.service(requestMock, responseMock) >> { throw new IOException("IOException")}
				1* requestMock.getObjectParameter("error") >> "error"
				2* requestMock.getObjectParameter("alternateURL") >> alternateURL
			
		when:
				CLPResponseVO responseVO = testObj.getCustomLandingTemplate(categoryId, templateName,altURLRequired,clpName)
		then:
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - categoryId :: " + categoryId)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - templateName :: " + templateName)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - altURLRequired :: " + altURLRequired)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - clpName :: " + clpName)
				1* requestMock.setParameter(BBBCoreConstants.PARAM_CATEGORY_ID, categoryId);
				1* requestMock.setParameter(BBBCoreConstants.TEMPLATE_NAME, templateName);
				1* requestMock.setParameter("alternateURLRequired", altURLRequired);
				1* requestMock.setParameter(BBBCoreConstants.PARAM_NAME, clpName)
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - start")
				1* testObj.logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - end")
				2* testObj.logError(_)
				responseVO.getCmsResponseVO() == responseVO1
				responseVO.isErrorExist() == true
				responseVO.getErrorCode() == BBBCatalogErrorCodes.CLP_NOT_AVAILABLE_IN_REPO
				responseVO.getAlternateURL() ==alternateURL
				
	}
	
	
}
