package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.commerce.giftregistry.tool.RecommendationRegistryProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class RecommendationInfoDisplayDropletSpecification extends BBBExtendedSpec {
	
	RecommendationInfoDisplayDroplet testObj
	GiftRegistryRecommendationManager giftRegistryRecommendationManagerMock = Mock()
	GiftRegistryTools giftRegistryToolsMock = Mock()
	
	def setup(){
		testObj = new RecommendationInfoDisplayDroplet(giftRegistryRecommendationManager:giftRegistryRecommendationManagerMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			String registryId = "23565232";	String tabId = "8"; String sortOption = "BestSeller"
			String pageSize = "12"; String pageNum = "1"; String eventTypeCode = "event8"
			String fromViewRegistryOwner = "true"; String fromRecommenderTab = null
			
			this.populateGetParameters(requestMock, registryId, tabId, sortOption, pageSize, pageNum, eventTypeCode, fromViewRegistryOwner, fromRecommenderTab)
			
			//getBadgeCount Public Method Coverage
			giftRegistryRecommendationManagerMock.getGiftRegistryTools() >> giftRegistryToolsMock
			1 * giftRegistryToolsMock.getRecommendationCount(registryId) >> [3532321,55452252]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("recommendationCount", 3532321)
			1 * requestMock.setParameter("recommendationProductSize", 55452252)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
		
	}
	
	def"service method. This TC is when fromViewRegistryOwner is false and fromRecommenderTab is true"(){
		given:
			String registryId = "23565232";	String tabId = "3"; String sortOption = "BestSeller"
			String pageSize = "12"; String pageNum = "1"; String eventTypeCode = "event8"
			String fromViewRegistryOwner = "false"; String fromRecommenderTab = "true"
			
			this.populateGetParameters(requestMock, registryId, tabId, sortOption, pageSize, pageNum, eventTypeCode, fromViewRegistryOwner, fromRecommenderTab)
			1 * giftRegistryRecommendationManagerMock.getEmailOptInValue(registryId) >> 5
			
			//getRecommendationTabDetails Private Method Coverage
			RecommendationRegistryProductVO recommendationRegistryProductVOMock = Mock()
			1 * giftRegistryRecommendationManagerMock.getRegistryRecommendationItemsForTab(registryId, tabId, "",eventTypeCode,requestMock) >> [recommendationRegistryProductVOMock] 
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("recommendationProduct", [recommendationRegistryProductVOMock])
			1 * requestMock.setParameter("emailOptInValue", 5)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when fromViewRegistryOwner is null, sortOption and recommendationProduct is empty"(){
		given:
			String registryId = "23565232";	String tabId = "5"; String sortOption = ""
			String pageSize = "12"; String pageNum = "1"; String eventTypeCode = "event8"
			String fromViewRegistryOwner = null; String fromRecommenderTab = "false"
			
			this.populateGetParameters(requestMock, registryId, tabId, sortOption, pageSize, pageNum, eventTypeCode, fromViewRegistryOwner, fromRecommenderTab)
			
			//getRecommendationTabDetails Private Method Coverage
			1 * giftRegistryRecommendationManagerMock.getRegistryRecommendationItemsForTab(registryId, tabId, "recommender",eventTypeCode,requestMock) >> []
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("recommendationProduct", [])
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)	
	}
	
	def"service method. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			String registryId = "23565232";	String tabId = "5"; String sortOption = "BestSeller"
			String pageSize = "12"; String pageNum = "1"; String eventTypeCode = "event8"
			String fromViewRegistryOwner = null; String fromRecommenderTab = "false"
			
			this.populateGetParameters(requestMock, registryId, tabId, sortOption, pageSize, pageNum, eventTypeCode, fromViewRegistryOwner, fromRecommenderTab)
			
			//getRecommendationTabDetails Private Method Coverage
			1 * giftRegistryRecommendationManagerMock.getRegistryRecommendationItemsForTab(registryId, tabId, "BestSeller",eventTypeCode,requestMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('Error fetching recommendation for recommender tab', _)
			1 * testObj.logDebug('RegistryId= 23565232tabId= 5sortOption= BestSellerPageSize= 12pageNum1eventTypeCode:event8fromViewRegistryOwnerfalse')
			1 * testObj.logDebug('Inside the RecommendationInfoDisplayDroplet')
	}
	
	def"service method. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			String registryId = "23565232";	String tabId = "3"; String sortOption = ""
			String pageSize = "12"; String pageNum = "1"; String eventTypeCode = "event8"
			String fromViewRegistryOwner = null; String fromRecommenderTab = "false"
			
			this.populateGetParameters(requestMock, registryId, tabId, sortOption, pageSize, pageNum, eventTypeCode, fromViewRegistryOwner, fromRecommenderTab)
			
			//getRecommendationTabDetails Private Method Coverage
			1 * giftRegistryRecommendationManagerMock.getRegistryRecommendationItemsForTab(registryId, tabId, "",eventTypeCode,requestMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('Error fetching recommendation for recommender tab', _)
			1 * testObj.logDebug('RegistryId= 23565232tabId= 3sortOption= PageSize= 12pageNum1eventTypeCode:event8fromViewRegistryOwnerfalse')
			1 * testObj.logDebug('Inside the RecommendationInfoDisplayDroplet')
	}

	private populateGetParameters(atg.servlet.DynamoHttpServletRequest requestMock, String registryId, String tabId, String sortOption, String pageSize, String pageNum, String eventTypeCode, String fromViewRegistryOwner, String fromRecommenderTab) {
		requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> registryId
		requestMock.getParameter(BBBGiftRegistryConstants.TAB_ID) >> tabId
		requestMock.getParameter(BBBGiftRegistryConstants.SORT_OPTION) >> sortOption
		requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> pageSize
		requestMock.getParameter(BBBCoreConstants.PAGENUMBER) >> pageNum
		requestMock.getParameter("eventTypeCode") >> eventTypeCode
		requestMock.getParameter("fromViewRegistryOwner") >> fromViewRegistryOwner
		requestMock.getParameter("fromRecommenderTab") >> fromRecommenderTab
	}
}
