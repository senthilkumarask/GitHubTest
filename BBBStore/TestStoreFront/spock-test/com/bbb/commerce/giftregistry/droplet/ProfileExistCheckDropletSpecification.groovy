package com.bbb.commerce.giftregistry.droplet

import atg.repository.RepositoryItem
import atg.userprofiling.ProfileTools
import com.bbb.account.BBBProfileManager
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class ProfileExistCheckDropletSpecification extends BBBExtendedSpec {
	
	ProfileExistCheckDroplet testObj
	ProfileTools toolsMock = Mock()
	BBBProfileManager managerMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	
	def setup(){
		
		testObj = new ProfileExistCheckDroplet(tools:toolsMock,manager:managerMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			requestMock.getParameter("emailId") >> "bbbCustomer@bbb.com"
			1 * toolsMock.getItemFromEmail("bbbcustomer@bbb.com") >> repositoryItemMock
			1 * repositoryItemMock.getRepositoryId() >> "232545"
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * managerMock.isUserPresentToOtherGroup(repositoryItemMock, "BedBathUS") >> true   
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("doesCoRegistrantExist", "nonSister")		
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when repositoryId is null"(){
		given:
			requestMock.getParameter("emailId") >> "bbbCustomer@bbb.com"
			1 * toolsMock.getItemFromEmail("bbbcustomer@bbb.com") >> repositoryItemMock
			1 * repositoryItemMock.getRepositoryId() >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("doesCoRegistrantExist", "false")
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when repositoryItem is null"(){
		given:
			requestMock.getParameter("emailId") >> "bbbCustomer@bbb.com"
			1 * toolsMock.getItemFromEmail("bbbcustomer@bbb.com") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("doesCoRegistrantExist", "false")
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isUserPresentToOtherGroup is false"(){
		given:
			requestMock.getParameter("emailId") >> "bbbCustomer@bbb.com"
			1 * toolsMock.getItemFromEmail("bbbcustomer@bbb.com") >> repositoryItemMock
			1 * repositoryItemMock.getRepositoryId() >> "232545"
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * managerMock.isUserPresentToOtherGroup(repositoryItemMock, "BedBathUS") >> false
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("doesCoRegistrantExist", "true")
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
		
	}

}
