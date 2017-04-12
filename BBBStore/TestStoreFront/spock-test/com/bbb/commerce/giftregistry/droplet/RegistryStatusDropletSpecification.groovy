package com.bbb.commerce.giftregistry.droplet

import atg.multisite.Site
import atg.multisite.SiteContext

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class RegistryStatusDropletSpecification extends BBBExtendedSpec {
	
	RegistryStatusDroplet testObj
	GiftRegistryTools giftRegistryToolsMock = Mock()
	SiteContext siteContextMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	Site siteMock = Mock()
	
	def setup(){
		
		testObj = new RegistryStatusDroplet(giftRegistryTools:giftRegistryToolsMock,siteContext:siteContextMock,catalogTools:catalogToolsMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			requestMock.getLocalParameter("registryId") >> "23256532"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * giftRegistryToolsMock.getRegistryStatus("23256532","true") >> "Y"
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("regPublic", true)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
	}
	
	def"service method. This TC is when registryStatus is not 'Y'"(){
		given:
			requestMock.getLocalParameter("registryId") >> "23256532"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * giftRegistryToolsMock.getRegistryStatus("23256532","true") >> "N"
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("regPublic", false)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
	}
	
	def"service method. This TC is when registryStatus is null"(){
		given:
			requestMock.getLocalParameter("registryId") >> "23256532"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * giftRegistryToolsMock.getRegistryStatus("23256532","true") >> null
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("regPublic", true)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setGiftRegistryTools(giftRegistryToolsMock)
			testObj.setSiteContext(siteContextMock)
			requestMock.getLocalParameter("registryId") >> "23256532"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * giftRegistryToolsMock.getRegistryStatus("23256532","true") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('registry status error', _)
			1 * requestMock.setParameter("regPublic", true)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setGiftRegistryTools(giftRegistryToolsMock)
			testObj.setSiteContext(siteContextMock)
			requestMock.getLocalParameter("registryId") >> "23256532"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * giftRegistryToolsMock.getRegistryStatus("23256532","true") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('registry status error', _)
			1 * requestMock.setParameter("regPublic", true)
			1 * requestMock.serviceParameter(BBBCmsConstants.OUTPUT, requestMock, responseMock)
	}

}
