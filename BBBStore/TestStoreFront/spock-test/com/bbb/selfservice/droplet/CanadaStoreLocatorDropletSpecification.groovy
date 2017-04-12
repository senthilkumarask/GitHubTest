package com.bbb.selfservice.droplet

import com.bbb.commerce.catalog.vo.StoreSpecialityVO
import com.bbb.commerce.catalog.vo.StoreVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.manager.CanadaStoreLocatorManager

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec;

class CanadaStoreLocatorDropletSpecification extends BBBExtendedSpec {

	CanadaStoreLocatorDroplet droplet 
	CanadaStoreLocatorManager manager =Mock()
	
	def setup(){
		droplet = new CanadaStoreLocatorDroplet()
		droplet.setCanadaStoreLocatorManager(manager)
	}
	
	def"service,  fetches data of Store Locators for Canada."(){
		
		given:
		StoreVO vo1 = new StoreVO()
		StoreSpecialityVO svo1 = new StoreSpecialityVO()
		StoreSpecialityVO svo2 = new StoreSpecialityVO()
		StoreSpecialityVO svo3 = new StoreSpecialityVO()
		StoreSpecialityVO svo4 = new StoreSpecialityVO()
		
		2*manager.getCanadaStoreLocator() >> [vo1]
		vo1.setStoreSpecialityVO([svo1,svo2,svo3,svo4]) 
		
		svo1.setCodeImage("image")
		svo2.setCodeImage(null)
		svo2.setCodeImage("")
		svo4.setCodeImage("image")
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("canadaStoreLocator", manager.getCanadaStoreLocator())
		1*requestMock.setParameter("imageMap", _)
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
	}
	
	def"service , when list of storeSpecialityVOs is null "(){
		
		given:
		StoreVO vo1 = new StoreVO()
		2*manager.getCanadaStoreLocator() >> [vo1]
		vo1.setStoreSpecialityVO(null)
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("canadaStoreLocator", manager.getCanadaStoreLocator())
		1*requestMock.setParameter("imageMap", [:])
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
	}
	
	def"service, when BBBBusinessException is thrown"(){
		
		given:
		StoreVO vo1 = new StoreVO()
		manager.getCanadaStoreLocator() >> {throw new BBBBusinessException("")}
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(BBBCoreConstants.SYSTEM_ERROR, "err_fetching_canada_stores");
		1*requestMock.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)	
	}
	
	def"service, when BBBSystemException is thrown"(){
		
		given:
		StoreVO vo1 = new StoreVO()
		manager.getCanadaStoreLocator() >> {throw new BBBSystemException("")}
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(BBBCoreConstants.SYSTEM_ERROR, "err_fetching_canada_stores");
		1*requestMock.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
			
	}
	
	def"service, when store list is null"(){
		
		given:
		StoreVO vo1 = new StoreVO()
		1*manager.getCanadaStoreLocator() >> [null]
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("canadaStoreLocator", [null])
		1*requestMock.setParameter("imageMap", _)
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
	}
	
	
}
