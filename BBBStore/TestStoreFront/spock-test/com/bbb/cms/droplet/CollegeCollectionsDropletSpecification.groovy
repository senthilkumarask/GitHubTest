package com.bbb.cms.droplet

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CollectionProductVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import spock.lang.specification.BBBExtendedSpec;

class CollegeCollectionsDropletSpecification extends BBBExtendedSpec {
	
	def BBBCatalogTools catalogToolMock = Mock();
	def String dormRoomCollectionCatId="dormRoomCollectionCatId"
	def String configType ="configType"
	def CollegeCollectionsDroplet testObj
	
	def setup(){
		testObj= Spy()
		testObj.setCatalogTools(catalogToolMock)
		testObj.setConfigType(configType)
		testObj.setDormRoomCollectionCatId(dormRoomCollectionCatId)
		}
	
	def "when siteId and dormRoomId is not null"(){
		
		given:
			String siteId="bedBathUs"
			String dormRoomId="dormRoomId"
			String dormCollection="dormCollection"
			
			List<CollectionProductVO> listCollectionVo = new ArrayList<CollectionProductVO>()
			List<String> getAllValuesForKey= new ArrayList<String>()
			getAllValuesForKey.add(dormRoomId)
			3* testObj.getCurrentSiteId() >> siteId
			1* catalogToolMock.getAllValuesForKey(configType, dormRoomCollectionCatId+siteId) >>getAllValuesForKey
			1* catalogToolMock.getDormRoomCollections(siteId, dormRoomId) >> listCollectionVo
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("starting method CollegeCollectionsDroplet")
		1* testObj.logDebug("Calling getDormRoomCollections for Category : "+dormRoomId+" and site:"+siteId)
		1* testObj.logDebug("COLLEGE_COLLECTIONS will be created : ")
		1* requestMock.setParameter("listCollectionVo",listCollectionVo)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "when siteId and dormRoomId is null"(){
		
		given:
			String siteId="bedBathUs"
			String dormRoomId=null
			String dormCollection="dormCollection"
			
			List<CollectionProductVO> listCollectionVo = new ArrayList<CollectionProductVO>()
			List<String> getAllValuesForKey= new ArrayList<String>()
			getAllValuesForKey.add(dormRoomId)
			3* testObj.getCurrentSiteId() >> siteId
			1* catalogToolMock.getAllValuesForKey(configType, dormRoomCollectionCatId+siteId) >>getAllValuesForKey
			1* catalogToolMock.getDormRoomCollections(siteId, dormRoomId) >> listCollectionVo
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("starting method CollegeCollectionsDroplet")
		1* testObj.logDebug("Calling getDormRoomCollections for Category : "+dormRoomId+" and site:"+siteId)
		1* testObj.logDebug("COLLEGE_COLLECTIONS will be created : ")
		1* requestMock.setParameter("listCollectionVo",listCollectionVo)
		1* requestMock.serviceParameter("empty", requestMock, responseMock)
	}
	def "handle BBBBusinessException"(){
		
		given:
			String siteId="bedBathUs"
			String dormRoomId=null
			String dormCollection="dormCollection"
			
			List<CollectionProductVO> listCollectionVo = new ArrayList<CollectionProductVO>()
			List<String> getAllValuesForKey= new ArrayList<String>()
			getAllValuesForKey.add(dormRoomId)
			1* testObj.getCurrentSiteId() >> siteId
			1* catalogToolMock.getAllValuesForKey(configType, dormRoomCollectionCatId+siteId) >> {throw new BBBBusinessException("BBBBusinessException")}
			0* catalogToolMock.getDormRoomCollections(siteId, dormRoomId) >> listCollectionVo
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("starting method CollegeCollectionsDroplet")
		0* testObj.logDebug("Calling getDormRoomCollections for Category : "+dormRoomId+" and site:"+siteId)
		0* testObj.logDebug("COLLEGE_COLLECTIONS will be created : ")
		0* requestMock.setParameter("listCollectionVo",listCollectionVo)
		0* requestMock.serviceParameter("empty", requestMock, responseMock)
		1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "Collegecollectiondroplet|service|BBBBusinessException","catalog_1032"),_);
		1* requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM,requestMock, responseMock)
	}
	def "handle BBBSystemException"(){
		
		given:
			String siteId="bedBathUs"
			String dormRoomId=null
			String dormCollection="dormCollection"
			
			List<CollectionProductVO> listCollectionVo = new ArrayList<CollectionProductVO>()
			List<String> getAllValuesForKey= new ArrayList<String>()
			getAllValuesForKey.add(dormRoomId)
			testObj.setSiteId(siteId)
			1* catalogToolMock.getAllValuesForKey(configType, dormRoomCollectionCatId+siteId) >> {throw new BBBSystemException("BBBSystemException")}
			0* catalogToolMock.getDormRoomCollections(siteId, dormRoomId) >> listCollectionVo
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("starting method CollegeCollectionsDroplet")
		0* testObj.logDebug("Calling getDormRoomCollections for Category : "+dormRoomId+" and site:"+siteId)
		0* testObj.logDebug("COLLEGE_COLLECTIONS will be created : ")
		0* requestMock.setParameter("listCollectionVo",listCollectionVo)
		0* requestMock.serviceParameter("empty", requestMock, responseMock)
		1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "Collegecollectiondroplet|service|BBBSystemException","catalog_1033"),_);
		1* requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM,requestMock, responseMock)
	}
}
