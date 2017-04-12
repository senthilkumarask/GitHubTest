package com.bbb.commerce.browse.droplet

import spock.lang.specification.BBBExtendedSpec;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCmsConstants;
import com.bbb.framework.cache.BBBObjectCache

/**
 * 
 * @author kmagud
 *
 */
class CollectionParentDropletSpecification extends BBBExtendedSpec{
	
	def CollectionParentDroplet testObj
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBObjectCache objectCacheMock = Mock()
	
	def setup(){
		
		testObj = new CollectionParentDroplet(catalogTools : catalogToolsMock, objectCache : objectCacheMock)
	}
	
	def"service method. This TC is the Happy flow of the service method"(){
		given:
			testObj.setCatalogTools(catalogToolsMock)
			1 * requestMock.getParameter("productId") >> "prod12345"
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			1 * catalogToolsMock.getParentProductId("prod12345", "BedBathUS") >> "collectionParentProductId"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("collectionParentProductId", "collectionParentProductId")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when siteId is null and getParentProductId returns null"(){
		given:
			1 * requestMock.getParameter("productId") >> "prod12345"
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> null
			1 * catalogToolsMock.getParentProductId("prod12345", null) >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter("empty", requestMock, responseMock)
			0 * requestMock.setParameter("collectionParentProductId", "collectionParentProductId")
			0 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when productId is not defined"(){
		given:
			1 * requestMock.getParameter("productId") >> null
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			0 * catalogToolsMock.getParentProductId("prod12345", "BedBathUS") >> "collectionParentProductId"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
			0 * requestMock.serviceParameter("empty", requestMock, responseMock)
			0 * requestMock.setParameter("collectionParentProductId", "collectionParentProductId")
			0 * requestMock.serviceParameter("output", requestMock, responseMock)
	}

}
