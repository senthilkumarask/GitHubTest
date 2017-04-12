package com.bbb.commerce.giftregistry.droplet

import atg.repository.RepositoryItem
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class PrintAtHomeDropletSpecification extends BBBExtendedSpec {
	
	PrintAtHomeDroplet testObj
	GiftRegistryTools giftRegistryToolsMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	RepositoryItem repositoryItemMock1 = Mock()
	
	
	def setup(){
		testObj = new PrintAtHomeDroplet(giftRegistryTools:giftRegistryToolsMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.getParameter("id") >> "2232545"
			1 * giftRegistryToolsMock.getPrintAtHomeCardTemplates("BedBathUS") >> [repositoryItemMock,repositoryItemMock1]
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("repoItems", repositoryItemMock)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when repositoryItem returns only one item"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.getParameter("id") >> "2232545"
			1 * giftRegistryToolsMock.getPrintAtHomeCardTemplates("BedBathUS") >> [repositoryItemMock]
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("repoItems", [repositoryItemMock])
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when repositoryItem returns null"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.getParameter("id") >> "2232545"
			1 * giftRegistryToolsMock.getPrintAtHomeCardTemplates("BedBathUS") >> null
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when siteId is null"(){
		given:
			requestMock.getParameter("siteId") >> null
			requestMock.getParameter("id") >> "2232545"
			1 * giftRegistryToolsMock.getThumbnailTemplateDetails("2232545") >> [repositoryItemMock]
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("repoItems", [repositoryItemMock])
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when siteId and repositoryId is null"(){
		given:
			testObj = Spy()
			requestMock.getParameter("siteId") >> null
			requestMock.getParameter("id") >> null
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('BBBBusinessException from SERVICE of PrintAtHomeDroplet.required site id or thumbnail repository id parameter')
			1 * testObj.logDebug('PrintAtHomeDroplet : service : Exit')
			1 * testObj.logDebug('PrintAtHomeDroplet : service : START')
	}

}
