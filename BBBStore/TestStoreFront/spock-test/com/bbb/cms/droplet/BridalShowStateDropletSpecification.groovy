package com.bbb.cms.droplet

import com.bbb.cms.manager.BridalShowManager
import com.bbb.constants.BBBCmsConstants;

import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import spock.lang.specification.BBBExtendedSpec;;

class BridalShowStateDropletSpecification extends BBBExtendedSpec {
	
	def BridalShowManager bridalShowManagerMock = Mock()
	def Repository bridalShowTemplateRepositoryMock= Mock()
	def BridalShowStateDroplet testObj

	def setup(){
		testObj = Spy()
		testObj.setBridalShowManager(bridalShowManagerMock)
		testObj.setBridalShowTemplateRepository(bridalShowTemplateRepositoryMock)
	}
	
	def "When SiteId id not null in request parameter"(){
		
		given:
			String siteId="BedBathUs"
			String stateId1="stateId1"
			String stateId2="stateId2"
			String stateId1Desc="stateId1Desc"
			String stateId2Desc="stateId2Desc"
			
			String bridalItemId="bridalItemId1"
			
			RepositoryView bridalShowTemplateViewMock= Mock()
			QueryBuilder queryBuilderMock= Mock()
			QueryExpression toDateExpressionMock= Mock()
			QueryExpression currentDateExpressionMock= Mock()
			Query query0_Mock=Mock()
			Query query1_Mock=Mock()
			Query querySiteandTimeQueryMock=Mock()
			
			
			
			
			RepositoryItem stateItem1_Mock= Mock()
			stateItem1_Mock.getPropertyValue(BBBCmsConstants.DESCRIP) >>stateId1
			stateItem1_Mock.getRepositoryId() >>stateId1
			
			RepositoryItem stateItem2_Mock= Mock()
			stateItem2_Mock.getPropertyValue(BBBCmsConstants.DESCRIP) >>stateId2
			stateItem2_Mock.getRepositoryId() >>stateId2
			
			RepositoryItem bridalItemMock= Mock()
			bridalItemMock.getRepositoryId() >> bridalItemId
			Set<RepositoryItem> stateItemSet= new HashSet<RepositoryItem>() 
			stateItemSet.add(stateItem1_Mock)
			stateItemSet.add(stateItem2_Mock)
			bridalItemMock.getPropertyValue(BBBCmsConstants.STATE_ID) >>stateItemSet 
			
			RepositoryItem[] bridalItemArray=[bridalItemMock]
			
			1* requestMock.getParameter(BBBCmsConstants.SITE_ID) >> siteId
			1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
			1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
			1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateExpressionMock
			1* queryBuilderMock.createConstantQueryExpression(_) >> currentDateExpressionMock
			1* bridalShowManagerMock.multiValueQuery(queryBuilderMock, BBBCmsConstants.SITE_ID, siteId) >>query0_Mock
			1* queryBuilderMock.createComparisonQuery(toDateExpressionMock,currentDateExpressionMock, QueryBuilder.GREATER_THAN_OR_EQUALS) >>query1_Mock
			1* queryBuilderMock.createAndQuery(_) >>querySiteandTimeQueryMock
			1* bridalShowTemplateViewMock.executeQuery(querySiteandTimeQueryMock) >>bridalItemArray
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1* testObj.logDebug("BridalShowStateDroplet : siteId= " + siteId)
			1* testObj.logDebug("Result set of applicable states " +bridalItemArray)
			1* testObj.logDebug(("no of states in which show is present :  "+bridalItemArray.length))
			1* requestMock.setParameter(BBBCmsConstants.STATE_MAP, _)
			1* requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock,responseMock)
	}
	
	def "When SiteId id not null in request parameter and stateMap is null"(){
		
		given:
			String siteId="BedBathUs"
			String stateId1="stateId1"
			String stateId2="stateId2"
			String stateId1Desc="stateId1Desc"
			String stateId2Desc="stateId2Desc"
			
			String bridalItemId="bridalItemId1"
			
			RepositoryView bridalShowTemplateViewMock= Mock()
			QueryBuilder queryBuilderMock= Mock()
			QueryExpression toDateExpressionMock= Mock()
			QueryExpression currentDateExpressionMock= Mock()
			Query query0_Mock=Mock()
			Query query1_Mock=Mock()
			Query querySiteandTimeQueryMock=Mock()
			
			RepositoryItem bridalItemMock= Mock()
			bridalItemMock.getRepositoryId() >> bridalItemId
			Set<RepositoryItem> stateItemSet= new HashSet<RepositoryItem>()
			
			bridalItemMock.getPropertyValue(BBBCmsConstants.STATE_ID) >>stateItemSet
			
			RepositoryItem[] bridalItemArray=[bridalItemMock]
			
			1* requestMock.getParameter(BBBCmsConstants.SITE_ID) >> siteId
			1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
			1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
			1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateExpressionMock
			1* queryBuilderMock.createConstantQueryExpression(_) >> currentDateExpressionMock
			1* bridalShowManagerMock.multiValueQuery(queryBuilderMock, BBBCmsConstants.SITE_ID, siteId) >>query0_Mock
			1* queryBuilderMock.createComparisonQuery(toDateExpressionMock,currentDateExpressionMock, QueryBuilder.GREATER_THAN_OR_EQUALS) >>query1_Mock
			1* queryBuilderMock.createAndQuery(_) >>querySiteandTimeQueryMock
			1* bridalShowTemplateViewMock.executeQuery(querySiteandTimeQueryMock) >>bridalItemArray
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1* testObj.logDebug("BridalShowStateDroplet : siteId= " + siteId)
			1* testObj.logDebug("Result set of applicable states " +bridalItemArray)
			1* testObj.logDebug(("no of states in which show is present :  "+bridalItemArray.length))
			1* requestMock.serviceParameter(BBBCmsConstants.EMPTY,requestMock, responseMock);
			1* testObj.logDebug("No results in states Map");
	}
	
	def "When SiteId id not null in request parameter and bridalArray is null"(){
		
		given:
			String siteId="BedBathUs"
			String stateId1="stateId1"
			String stateId2="stateId2"
			String stateId1Desc="stateId1Desc"
			String stateId2Desc="stateId2Desc"
			
			String bridalItemId="bridalItemId1"
			
			RepositoryView bridalShowTemplateViewMock= Mock()
			QueryBuilder queryBuilderMock= Mock()
			QueryExpression toDateExpressionMock= Mock()
			QueryExpression currentDateExpressionMock= Mock()
			Query query0_Mock=Mock()
			Query query1_Mock=Mock()
			Query querySiteandTimeQueryMock=Mock()
			
			RepositoryItem bridalItemMock= Mock()
			bridalItemMock.getRepositoryId() >> bridalItemId
			Set<RepositoryItem> stateItemSet= new HashSet<RepositoryItem>()
			
			bridalItemMock.getPropertyValue(BBBCmsConstants.STATE_ID) >>stateItemSet
			
			RepositoryItem[] bridalItemArray=[bridalItemMock]
			
			1* requestMock.getParameter(BBBCmsConstants.SITE_ID) >> siteId
			1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
			1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
			1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateExpressionMock
			1* queryBuilderMock.createConstantQueryExpression(_) >> currentDateExpressionMock
			1* bridalShowManagerMock.multiValueQuery(queryBuilderMock, BBBCmsConstants.SITE_ID, siteId) >>query0_Mock
			1* queryBuilderMock.createComparisonQuery(toDateExpressionMock,currentDateExpressionMock, QueryBuilder.GREATER_THAN_OR_EQUALS) >>query1_Mock
			1* queryBuilderMock.createAndQuery(_) >>querySiteandTimeQueryMock
			1* bridalShowTemplateViewMock.executeQuery(querySiteandTimeQueryMock) >>null
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1* testObj.logDebug("BridalShowStateDroplet : siteId= " + siteId)
			0* testObj.logDebug("Result set of applicable states " +bridalItemArray)
			0* testObj.logDebug(("no of states in which show is present :  "+bridalItemArray.length))
			1* requestMock.serviceParameter(BBBCmsConstants.EMPTY,requestMock, responseMock);
			1* testObj.logDebug("No results in states Map");
	}
	
	def "When SiteId id not null in request parameter and bridalArray is Empty"(){
		
		given:
			String siteId="BedBathUs"
			String stateId1="stateId1"
			String stateId2="stateId2"
			String stateId1Desc="stateId1Desc"
			String stateId2Desc="stateId2Desc"
			
			String bridalItemId="bridalItemId1"
			
			RepositoryView bridalShowTemplateViewMock= Mock()
			QueryBuilder queryBuilderMock= Mock()
			QueryExpression toDateExpressionMock= Mock()
			QueryExpression currentDateExpressionMock= Mock()
			Query query0_Mock=Mock()
			Query query1_Mock=Mock()
			Query querySiteandTimeQueryMock=Mock()
			
			RepositoryItem bridalItemMock= Mock()
			bridalItemMock.getRepositoryId() >> bridalItemId
			Set<RepositoryItem> stateItemSet= new HashSet<RepositoryItem>()
			
			bridalItemMock.getPropertyValue(BBBCmsConstants.STATE_ID) >>stateItemSet
			
			RepositoryItem[] bridalItemArray=[]
			
			1* requestMock.getParameter(BBBCmsConstants.SITE_ID) >> siteId
			1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >> bridalShowTemplateViewMock
			1* bridalShowTemplateViewMock.getQueryBuilder() >> queryBuilderMock
			1* queryBuilderMock.createPropertyQueryExpression(BBBCmsConstants.TO_DATE) >> toDateExpressionMock
			1* queryBuilderMock.createConstantQueryExpression(_) >> currentDateExpressionMock
			1* bridalShowManagerMock.multiValueQuery(queryBuilderMock, BBBCmsConstants.SITE_ID, siteId) >>query0_Mock
			1* queryBuilderMock.createComparisonQuery(toDateExpressionMock,currentDateExpressionMock, QueryBuilder.GREATER_THAN_OR_EQUALS) >>query1_Mock
			1* queryBuilderMock.createAndQuery(_) >>querySiteandTimeQueryMock
			1* bridalShowTemplateViewMock.executeQuery(querySiteandTimeQueryMock) >>bridalItemArray
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1* testObj.logDebug("BridalShowStateDroplet : siteId= " + siteId)
			1* testObj.logDebug("Result set of applicable states " +bridalItemArray)
			0* testObj.logDebug(("no of states in which show is present :  "+bridalItemArray.length))
			1* requestMock.serviceParameter(BBBCmsConstants.EMPTY,requestMock, responseMock);
			1* testObj.logDebug("No results in states Map");
	}
	def "When SiteId id null in request parameter and RepositoryException occured"(){
		
		given:
			String siteId="BedBathUs"
			String stateId1="stateId1"
			String stateId2="stateId2"
			String stateId1Desc="stateId1Desc"
			String stateId2Desc="stateId2Desc"
			
			String bridalItemId="bridalItemId1"
			
			1* requestMock.getParameter(BBBCmsConstants.SITE_ID) >> null
			1* testObj.getCurrentSiteId() >>siteId 
			1* bridalShowTemplateRepositoryMock.getView(BBBCmsConstants.SHOW_TEMPLATE) >>   {throw new RepositoryException("RepositoryException")}
			
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1* testObj.logDebug("State Repository not found");			
	}
	def "When SiteId id is Empty"(){
		
		given:
			String siteId=""
			String stateId1="stateId1"
			String stateId2="stateId2"
			String stateId1Desc="stateId1Desc"
			String stateId2Desc="stateId2Desc"
			
			String bridalItemId="bridalItemId1"
			
			1* requestMock.getParameter(BBBCmsConstants.SITE_ID) >> null
			1* testObj.getCurrentSiteId() >>siteId
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			0* testObj.logDebug("State Repository not found");
			0* testObj.logDebug("BridalShowStateDroplet : siteId= " + siteId)
	}
	def "check setter/Getter"(){
		given:
		String state="test_state"
		testObj.setState(state)
		expect: 
		state == testObj.getState()
		null == testObj.getCurrentSiteId()
	}
}
