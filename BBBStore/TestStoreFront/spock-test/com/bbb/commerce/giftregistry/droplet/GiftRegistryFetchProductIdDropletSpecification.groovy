package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter

import com.bbb.exception.BBBException
import spock.lang.specification.BBBExtendedSpec

class GiftRegistryFetchProductIdDropletSpecification extends BBBExtendedSpec {
	
	GiftRegistryFetchProductIdDroplet droplet = new GiftRegistryFetchProductIdDroplet()
	BBBCatalogTools mCatalogTools = Mock()
	
	def setup(){
		droplet.setCatalogTools(mCatalogTools)
	}
	
	def "service method happy path"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.SKU_ID) >> "skuId"
			1*mCatalogTools.getParentProductForSku("skuId") >> "prodId"
			0*mCatalogTools.getParentProductForSku("skuId", true) >> "productId"
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("inStore", false)
			1*requestMock.setParameter(BBBGiftRegistryConstants.PRODUCTID, "prodId")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with empty productId"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.SKU_ID) >> "skuId"
			1*mCatalogTools.getParentProductForSku("skuId") >> ""
			1*mCatalogTools.getParentProductForSku("skuId", true) >> "productId"
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("inStore", true)
			1*requestMock.setParameter(BBBGiftRegistryConstants.PRODUCTID, "productId")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with BBBSystemException thrown"(){
		given:
			droplet = Spy()
			droplet.setCatalogTools(mCatalogTools)
			requestMock.getParameter(BBBGiftRegistryConstants.SKU_ID) >> "skuId"
			1*mCatalogTools.getParentProductForSku("skuId") >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "BBBSystemException from SERVICE of GiftRegistryFetchProductIdDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),_)
			1*requestMock.setParameter("errorMsg",_)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with BBBBusinessException thrown"(){
		given:
			droplet = Spy()
			droplet.setCatalogTools(mCatalogTools)
			requestMock.getParameter(BBBGiftRegistryConstants.SKU_ID) >> "skuId"
			1*mCatalogTools.getParentProductForSku("skuId") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "BBBBusinessException from SERVICE of GiftRegistryFetchProductIdDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),_)
			1*requestMock.setParameter("errorMsg",_)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
}
