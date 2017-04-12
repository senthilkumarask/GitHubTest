package com.bbb.cms.droplet

import java.util.LinkedHashSet
import java.util.Map
import javax.servlet.ServletException

import com.bbb.cms.manager.BridalShowManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCmsConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter
import com.bbb.repository.RepositoryItemMock

import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import spock.lang.specification.BBBExtendedSpec

class BridalShowDetailDropletSpecification extends BBBExtendedSpec {
	
	def BridalShowDetailDroplet testObj
	
	def  Repository bridalShowTemplateRepositoryMock  = Mock()	
	def  BridalShowManager bridalShowManagerMock = Mock()
	def  BBBCatalogTools catalogToolMock= Mock()
	
	def  String bridalShowViewDirection ="bridalShowViewDirection"
	
	
	def setup(){
		
	testObj = Spy()
	
	testObj.setCatalogTools(catalogToolMock)
	testObj.getCatalogTools() >> catalogToolMock
	
	testObj.setBridalShowTemplateRepository(bridalShowTemplateRepositoryMock)
	testObj.getBridalShowTemplateRepository() >> bridalShowTemplateRepositoryMock
	
	testObj.setBridalShowManager(bridalShowManagerMock)
	testObj.getBridalShowTemplateRepository() >> bridalShowManagerMock
	
	
	
	}
	
	def "testService when state is null"(){
		given:
		String siteId="BedBathUs"
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> null
		1* testObj.getCurrentSiteId() >> siteId
		1* testObj.logDebug("input parameters is null or empty stateId=" + null + " siteId= " + siteId)
		1* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		
		expect :
		testObj.service(requestMock,responseMock)
	}
	
	def "testService when siteId is null"(){
		given:
		String siteId=null
		String stateId="ALL"
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
		1* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		1* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		
		expect :
		testObj.service(requestMock,responseMock)
	}
	def "testService when siteId  is in request in not null and StateId is not null"(){
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>fromDate
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> toDate
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> name
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> time
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>address
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>phone
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
		
		
		1* requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* requestMock.getParameter(BBBCmsConstants.SITE_ID) >> siteId
		
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
	
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
		
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
	
		2* testObj.getBridalShowViewDirection() >> bridalShowViewDirection
		1* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >> bridalShowViewDirectionList
		
	
		
		when :
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		 1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
		 1* requestMock.setParameter("errMsg", null)
	}
	
	def "testService when siteId and StateId is not null"(){
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>fromDate
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> toDate
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> name
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> time
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>address
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>phone		
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
		
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
		
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
	
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
		
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
	
		2* testObj.getBridalShowViewDirection() >> bridalShowViewDirection
		1* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >> bridalShowViewDirectionList
		
	
		
		when :
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		 1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
		 1* requestMock.setParameter("errMsg", null)
	}
	def "testService when siteId and StateId is not null when BBBSystemException in the invocation of getContentCatalogConfigration"(){
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>fromDate
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> toDate
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> name
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> time
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>address
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>phone
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
		 
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
	
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
		
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
		
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
	
		2* testObj.getBridalShowViewDirection() >> bridalShowViewDirection
		1* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >>  {throw new BBBSystemException("BBBSystemException")}
	
		1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
		
		
		when :
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "BridalShowDetailDroplet|service|BBBSystemException","catalog_1027"),_)
		 1* requestMock.setParameter("errMsg", "BBBSystemException:catalog_1027")
		 
	}
	
	def "testService when siteId and StateId is not null when BBBBusinessException in the invocation of getContentCatalogConfigration"(){
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>fromDate
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> toDate
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> name
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> time
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>address
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>phone
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
	

		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
		
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
		
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
		
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
	
		2* testObj.getBridalShowViewDirection() >> bridalShowViewDirection
		1* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >>  {throw new BBBBusinessException("BBBBusinessException")}
		
		1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		
		
		
		when:
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "BridalShowDetailDroplet|service|BBBBusinessException","catalog_1028"),_)
		 1* requestMock.setParameter("errMsg", "BBBBusinessException:catalog_1028")
		 1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
		 
	}
	
	
	def "testService when siteId != null,StateId != null when there is not items for siteId and stateId "(){
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		
		RepositoryItem[] bridalShowTemplate=null
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
		
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
	
		
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >>> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
	
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
		
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
		
		
		when:
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("No results applicable for the shows")
		 1* requestMock.setParameter("errMsg", null)
		 1* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
	}
	
	
	def "testService when siteId , StateId is not null and StateId !='ALL' "(){
		given:
		String siteId="BedBathUs"
		String stateId="NotALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		Query query_2_multiValueQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>fromDate
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> toDate
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> name
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> time
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>address
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>phone
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
		
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
		
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
	
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.STATE_ID, stateId) >> query_2_multiValueQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
		
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
		
		2* testObj.getBridalShowViewDirection() >> bridalShowViewDirection
		1* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >> bridalShowViewDirectionList
		
		when :
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)		
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* requestMock.setParameter("errMsg", null)
		 1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		 1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
		 
	}
	
	def "testService when siteId , StateId is not null and bridalShowViewDirection is null "(){
		given:
		String siteId="BedBathUs"
		String stateId="NotALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		Query query_2_multiValueQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>fromDate
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> toDate
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> name
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> time
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>address
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>phone
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
		
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
		
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
		
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.STATE_ID, stateId) >> query_2_multiValueQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
	
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
		
		1* testObj.getBridalShowViewDirection() >> null //bridalShowViewDirection
		0* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >> bridalShowViewDirectionList
		
		
		
		
		when :
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* requestMock.setParameter("errMsg", null)
		 1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		 1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
	}
	
	
	def "testService when siteId and StateId is not null and  check null on property of repositoryItem"(){
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
			
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>null
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> null
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> null
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> null
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>null
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>null
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
	
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
		
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
		
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
	
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
		
		2* testObj.getBridalShowViewDirection() >> bridalShowViewDirection
		1* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >> bridalShowViewDirectionList
		
		1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
		
		
		when:
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* requestMock.setParameter("errMsg", null)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
	}
	
	def "testService when RepositoryException during doing Repository Operantion"(){
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> { throw new RepositoryException("RepositoryException") }
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		
		1* testObj.getCurrentSiteId() >> siteId
		
		
		when :
		 testObj.service(requestMock,responseMock)
		 
		 then:
		 1* requestMock.setParameter("errMsg", "RepositoryException:catalog_1029")
		 1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "BridalShowDetailDroplet|service|RepositoryException","catalog_1029"),_)
	}
	 
	def "test getBridalShowsAPI with invocation of service method "(){
		
		given: 		
		String siteId="BedBathUs"
		String stateId="ALL"
		String bridalItemId="bridalItem_1"
		String name="name"
		String address="address"
		String phone="123456780"
		String direction="direction"
		
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, - 10)
		java.util.Date now = calendar.getTime()
		java.sql.Timestamp fromDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, +10)
		now = calendar.getTime()
		java.sql.Timestamp toDate = new java.sql.Timestamp(now.getTime())
		
		calendar = Calendar.getInstance()
		now = calendar.getTime()
		java.sql.Timestamp time = new java.sql.Timestamp(now.getTime())
		
		
		
		RepositoryView bridalShowTemplateViewMock = Mock()
		
		QueryBuilder queryBuilderMock=Mock()
		
		QueryExpression toDateQueryExp=Mock()
		QueryExpression valueQueryExp=Mock()
		
		Query query_0_multiValueQuery = Mock()
		Query query_1_ComparisonQuery = Mock()
		
		Query querySiteandTimeMock= Mock()
		
		
		RepositoryItemMock bridalItem = Mock(["id":bridalItemId])
		bridalItem.getRepositoryId() >> bridalItem
		bridalItem.getPropertyValue(BBBCmsConstants.FROM_DATE) >>fromDate
		bridalItem.getPropertyValue(BBBCmsConstants.TO_DATE) >> toDate
		bridalItem.getPropertyValue(BBBCmsConstants.NAME) >> name
		bridalItem.getPropertyValue(BBBCmsConstants.TIME) >> time
		bridalItem.getPropertyValue(BBBCmsConstants.ADDRESS) >>address
		bridalItem.getPropertyValue(BBBCmsConstants.PHONE) >>phone
		RepositoryItem[] bridalShowTemplate=[bridalItem]
		
		List<String> bridalShowViewDirectionList = new ArrayList<String>()
		bridalShowViewDirectionList.add(direction)
		
		
		
		requestMock.getParameter(BBBCmsConstants.STATE_ID) >> stateId
		1* testObj.getCurrentSiteId() >> siteId
		
		0* requestMock.serviceParameter(BBBCmsConstants.EMPTY, requestMock,responseMock)
		1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
		1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
		1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateQueryExp
		1* queryBuilderMock.createConstantQueryExpression(*_) >> valueQueryExp
	
		1* bridalShowManagerMock.multiValueQuery(queryBuilderMock,BBBCmsConstants.SITE_ID, siteId) >> query_0_multiValueQuery
		1* queryBuilderMock.createComparisonQuery(toDateQueryExp, valueQueryExp,QueryBuilder.GREATER_THAN_OR_EQUALS) >> query_1_ComparisonQuery
		1* queryBuilderMock.createAndQuery(*_) >> querySiteandTimeMock
		
		
		1* bridalShowTemplateViewMock.executeQuery(*_) >>bridalShowTemplate
	
		2* testObj.getBridalShowViewDirection() >> bridalShowViewDirection
		1* catalogToolMock.getContentCatalogConfigration(getBridalShowViewDirection()) >> bridalShowViewDirectionList
		
		when :
		 testObj.getBridalShowsAPI(stateId)
		 
		 then:
		 1* testObj.logDebug("Starting method getBridalShowsAPI for rest module")
		 1* requestMock.setParameter("stateId",stateId)
		 1* testObj.service(requestMock, responseMock)
		 0* testObj.logDebug("input parameters is null or empty stateId=" + stateId + " siteId= " + siteId)
		 1* testObj.logDebug("BridalShowDetailDroplet:selected state id "+stateId)
		 1* testObj.logDebug("The query to be executed to get  show data "+querySiteandTimeMock)
		 1* testObj.logDebug("no of shows for state "+stateId +" is :"+bridalShowTemplate.length)
		 1* requestMock.setParameter(BBBCmsConstants.STATE_ITEM, *_)
		 1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
		 1* requestMock.setParameter("errMsg", null)
		 1* requestMock.getObjectParameter(BBBCmsConstants.STATE_ITEM)
		 1* requestMock.getParameter("errMsg")
		 1* testObj.logDebug("End method getBridalShowsAPI for rest module")
	}
	
	def "test getBridalShowsAPI when errMsg is not null "(){
		
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
	    1* requestMock.getParameter("errMsg") >> "RepositoryException:catalog_1029"
		1* testObj.service(requestMock, responseMock) >> null
		when:
		testObj.getBridalShowsAPI(stateId)
		
		then:
		 BBBSystemException exception =thrown()
		
	}
	
	def "test getBridalShowsAPI when IOException occured "(){
		
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		1* requestMock.setParameter("stateId", stateId) >> { throw new IOException("IOException") }
		 
		when:
		testObj.getBridalShowsAPI(stateId)
		
		then:
		 BBBSystemException exception =thrown()
		
	}
	def "test getBridalShowsAPI when ServletException occured "(){
		
		given:
		String siteId="BedBathUs"
		String stateId="ALL"
		1* requestMock.setParameter("stateId", stateId) >> { throw new ServletException("ServletException") }
		 
		when:
		testObj.getBridalShowsAPI(stateId)
		
		then:
		 BBBSystemException exception =thrown()
		
	}
}
