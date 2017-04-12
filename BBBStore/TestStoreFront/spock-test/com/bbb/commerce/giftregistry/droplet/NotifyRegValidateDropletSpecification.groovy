package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class NotifyRegValidateDropletSpecification extends BBBExtendedSpec {
	
	NotifyRegValidateDroplet testObj
	GiftRegistryManager giftRegistryManagerMock = Mock()
	
	def setup() {
		testObj = new NotifyRegValidateDroplet(giftRegistryManager:giftRegistryManagerMock)
	}
	
	def"service method. This TC is the happy flow of service method"(){
		given:
			String eventDate = "15/12/2016"
			String skuId = "12354612"
			requestMock.getParameter("eventDate") >> eventDate
			requestMock.getParameter("skuId") >> skuId
			1 * giftRegistryManagerMock.getNotifyRegistrantMsgType(skuId, eventDate) >> "WelcomeMessage"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("displayMessageType", "WelcomeMessage")
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when eventDate is empty"(){
		given:
			String eventDate = ""
			String skuId = "12354612"
			requestMock.getParameter("eventDate") >> eventDate
			requestMock.getParameter("skuId") >> skuId
			requestMock.getParameter("registryId") >> "2325665"
			1 * giftRegistryManagerMock.getRegistryDate(requestMock, "2325665") >> "1/1/2017"
			1 * giftRegistryManagerMock.getNotifyRegistrantMsgType(skuId, "1/1/2017") >> "WelcomeMessage"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("displayMessageType", "WelcomeMessage")
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			String eventDate = ""
			String skuId = "12354612"
			requestMock.getParameter("eventDate") >> eventDate
			requestMock.getParameter("skuId") >> skuId
			requestMock.getParameter("registryId") >> "2325665"
			1 * giftRegistryManagerMock.getRegistryDate(requestMock, "2325665") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError(' BBBSystemException error occured in NotifyRegValidateDroplet', _)
			1 * testObj.logDebug(' NotifyRegValidateDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start')
			1 * requestMock.setParameter('displayMessageType', null)
			1 * testObj.logDebug(' NotifyRegValidateDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - End')
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			String eventDate = ""
			String skuId = "12354612"
			requestMock.getParameter("eventDate") >> eventDate
			requestMock.getParameter("skuId") >> skuId
			requestMock.getParameter("registryId") >> "2325665"
			1 * giftRegistryManagerMock.getRegistryDate(requestMock, "2325665") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError(' BBBBusinessException error occured in NotifyRegValidateDroplet while fetching event date', _)
			1 * requestMock.setParameter('displayMessageType', null)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when Exception thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			String eventDate = "15/12/2016"
			String skuId = "12354612"
			requestMock.getParameter("eventDate") >> eventDate
			requestMock.getParameter("skuId") >> skuId
			1 * giftRegistryManagerMock.getNotifyRegistrantMsgType(skuId, eventDate) >> {throw new Exception("Mock for Exception")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError(' other exceptions in NotifyRegValidateDroplet', _)
			1 * requestMock.setParameter('displayMessageType', null)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

}
