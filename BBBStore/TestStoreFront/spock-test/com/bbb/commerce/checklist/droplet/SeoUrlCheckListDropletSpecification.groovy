package com.bbb.commerce.checklist.droplet;

import static org.junit.Assert.*
import org.junit.Test
import spock.lang.specification.BBBExtendedSpec;
import com.bbb.commerce.checklist.droplet.SeoUrlCheckListDroplet
import com.bbb.commerce.giftregistry.utility.BBBGiftRegistryUtils

class SeoUrlCheckListDropletSpecification extends BBBExtendedSpec {

	def SeoUrlCheckListDroplet testObj;
	def BBBGiftRegistryUtils giftRegistryUtilsMock= Mock()
	
	def setup(){
		testObj = new SeoUrlCheckListDroplet(giftRegistryUtils : giftRegistryUtilsMock)
	}
	
	
	def "service:TC to check if the SEOURL for the checklist category is invoked"(){
		given: 
		1*giftRegistryUtilsMock.populateChecklistSEOUrl(requestMock,null) >> "seoURL"
			
		when: 
		testObj.service(requestMock, responseMock)		
		
		then:
		1*requestMock.setParameter("seoURL", "seoURL")
		1*requestMock.serviceLocalParameter("output", requestMock,responseMock)
	}
	
	

}
