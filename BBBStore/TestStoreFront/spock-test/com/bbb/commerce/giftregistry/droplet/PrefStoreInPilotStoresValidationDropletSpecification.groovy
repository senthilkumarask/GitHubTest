package com.bbb.commerce.giftregistry.droplet

import atg.multisite.SiteContextManager
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class PrefStoreInPilotStoresValidationDropletSpecification extends BBBExtendedSpec {

	PrefStoreInPilotStoresValidationDroplet testObj
	GiftRegistryManager giftRegistryManagerMock = Mock()
	SiteContextManager siteContextManagerMock = Mock()
	
	def setup(){
		
		testObj = new PrefStoreInPilotStoresValidationDroplet(giftRegistryManager:giftRegistryManagerMock,siteContextManager:siteContextManagerMock)
	}
	
	def"service Method. This TC is the Happy flow of service method"(){
		given:
			String storeId = "22325"
			String registryType = "Wedding"
			requestMock.getParameter("prefredStoreId") >> storeId
			requestMock.getParameter("registryTypeCode") >> registryType
			1 * giftRegistryManagerMock.canScheuleAppointment(storeId, registryType, _) >> true
		
		when:
			testObj.service(requestMock	, responseMock)
		then:
			1 * requestMock.setParameter("isUserAllowedToScheduleAStoreAppointment", true)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
}
