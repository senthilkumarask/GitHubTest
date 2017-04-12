package com.bbb.commerce.checkout.droplet

import atg.commerce.order.CommerceItem
import atg.commerce.order.ElectronicShippingGroup
import atg.commerce.order.Order
import atg.commerce.order.ShippingGroupCommerceItemRelationship;

import atg.core.util.Address;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec

class DisplayShippingRestrictionsDropletSpecification extends BBBExtendedSpec {
	def DisplayShippingRestrictionsDroplet dsrdObject
	def Order orderMock = Mock()
    def BBBHardGoodShippingGroup hgsgMock =  Mock()
	def ShippingGroupCommerceItemRelationship sgcirMock = Mock()
	def CommerceItem commerceItemMcok = Mock()
	def Address addressMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def SKUDetailVO skuVoMock = new SKUDetailVO()
	def ImageVO imgVO = new ImageVO()
	def ElectronicShippingGroup elsgMock = Mock()
	def StateVO stateVO = new StateVO()
	def setup(){
		dsrdObject = new DisplayShippingRestrictionsDroplet(catalogTools : catalogToolsMock)
	
	}
	def "service. TC to get he sku restriction detail" (){
		given:
		setShippingAddressMock()
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		
		//requestMock.getObjectParameter("skuId") >> skuId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [hgsgMock]
		hgsgMock.getShippingAddress() >>addressMock
		 
		hgsgMock.getCommerceItemRelationships() >> [sgcirMock]
		sgcirMock.getCommerceItem() >> commerceItemMcok
		catalogToolsMock.isShippingZipCodeRestrictedForSku(skuId, siteId, "250001") >> true
		
		//////getSkuRestrictionDetails method
		1*catalogToolsMock.getRestrictedSkuDetails(skuId, "250001") >> "skuRegionName"
		1*catalogToolsMock.getSKUDetails(siteId, skuId, false) >> skuVoMock
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
	}
	
	def "service. TC to get he sku restriction detail. when shipping address is empty " (){
		given:
		//setShippingAddressMock()
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		
		//requestMock.getObjectParameter("skuId") >> skuId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [hgsgMock]
		hgsgMock.getShippingAddress() >>addressMock
		 
		hgsgMock.getCommerceItemRelationships() >> [sgcirMock]
		sgcirMock.getCommerceItem() >> commerceItemMcok
		catalogToolsMock.isShippingZipCodeRestrictedForSku(skuId, siteId, _) >> true
		
		//////getSkuRestrictionDetails method
		1*catalogToolsMock.getRestrictedSkuDetails(skuId, null) >> "skuRegionName"
		1*catalogToolsMock.getSKUDetails(siteId, skuId, false) >> skuVoMock
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
	}
	
	def "service. TC to get empty oparam. when isRestricted is false gets from isShippingZipCodeRestrictedForSku() method" (){
		given:
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		
		//requestMock.getObjectParameter("skuId") >> skuId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [hgsgMock]
		hgsgMock.getShippingAddress() >>addressMock
		 
		hgsgMock.getCommerceItemRelationships() >> [sgcirMock]
		sgcirMock.getCommerceItem() >> commerceItemMcok
		1*catalogToolsMock.isShippingZipCodeRestrictedForSku(skuId, siteId, _) >> false
		
		//////getSkuRestrictionDetails method
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("empty", requestMock, responseMock);
	}
	
	def "service. TC to get empty oparam. when shipping group is not BBBHardGoodShippingGroup " (){
		given:
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [elsgMock]
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("empty", requestMock, responseMock);
	}
	
	def "service. TC to get empty oparam. when order is null " (){
		given:
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		//orderMock.getSiteId() >> siteId
		requestMock.getObjectParameter("order") >> null
				
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("mapSkuRestrictedZip", _);
		0*requestMock.serviceParameter("empty", requestMock, responseMock);
	}
	
	////////////////////////// getShippingRestrictionDetails /////////
	def "service. TC to get skuShipRestrictionsVO oparam.  " (){
		given:
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		stateVO.setStateName("us")
		
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getZipCodesRestrictedForSku(skuId) >> ["rst" : "25001"]
		1*catalogToolsMock.getNonShippableStatesForSku(null, skuId) >> [stateVO, stateVO]
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuShipRestrictionsVO", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
	}
	
	def "service. TC to get TC to get skuShipRestrictionsVO oparam. state name is null in stateVo " (){
		given:

		String skuId = "skuId"
		String siteId = "siteId"
		stateVO.setStateName(null)
		
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getZipCodesRestrictedForSku(skuId) >> ["rst" : "25001"]
		1*catalogToolsMock.getNonShippableStatesForSku(null, skuId) >> [stateVO]
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuShipRestrictionsVO", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
	}
	
	def "service. TC to get TC to get skuShipRestrictionsVO oparam. when stateVO List " (){
		given:
		String skuId = "skuId"
		String siteId = "siteId"
		stateVO.setStateName(null)
		
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getZipCodesRestrictedForSku(skuId) >> ["rst" : "25001"]
		1*catalogToolsMock.getNonShippableStatesForSku(null, skuId) >> null
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuShipRestrictionsVO", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
	}
	
	
	
	///////////////////////////exception scenario //////////////////////////////////////////////
	def "service. TC on  BBBBusinessException  while getting ShippingZipCodeRestrictedForSku" (){
		given:
		setShippingAddressMock()
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		
		//requestMock.getObjectParameter("skuId") >> skuId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [hgsgMock]
		hgsgMock.getShippingAddress() >>addressMock
		 
		hgsgMock.getCommerceItemRelationships() >> [sgcirMock]
		sgcirMock.getCommerceItem() >> commerceItemMcok
		catalogToolsMock.isShippingZipCodeRestrictedForSku(skuId, siteId, "250001") >> {throw new BBBBusinessException("") }
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("empty", requestMock, responseMock);
	}
	
	def "service. TC on  BBBSystemException  while getting ShippingZipCodeRestrictedForSku" (){
		given:
		setShippingAddressMock()
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		
		//requestMock.getObjectParameter("skuId") >> skuId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [hgsgMock]
		hgsgMock.getShippingAddress() >>addressMock
		 
		hgsgMock.getCommerceItemRelationships() >> [sgcirMock]
		sgcirMock.getCommerceItem() >> commerceItemMcok
		catalogToolsMock.isShippingZipCodeRestrictedForSku(skuId, siteId, "250001") >> {throw new BBBSystemException("") }
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("empty", requestMock, responseMock);
	}
	
	def "service. TC on BBBBusinessException while geting ZipCodesRestrictedForSku " (){
		given:
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		stateVO.setStateName("us")
		
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getZipCodesRestrictedForSku(skuId) >> {throw new BBBBusinessException("") }
		0*catalogToolsMock.getNonShippableStatesForSku(null, skuId) >> [stateVO, stateVO]
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuShipRestrictionsVO", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
	}

	def "service. TC on BBBSystemException while geting ZipCodesRestrictedForSku " (){
		given:
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		stateVO.setStateName("us")
		
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getZipCodesRestrictedForSku(skuId) >> {throw new BBBSystemException("") }
		0*catalogToolsMock.getNonShippableStatesForSku(null, skuId) >> [stateVO, stateVO]
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuShipRestrictionsVO", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
	}

	def "service. TC BBBSystemException while getting RestrictedSkuDetails " (){
		given:
		setShippingAddressMock()
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		
		//requestMock.getObjectParameter("skuId") >> skuId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [hgsgMock]
		hgsgMock.getShippingAddress() >>addressMock
		 
		hgsgMock.getCommerceItemRelationships() >> [sgcirMock]
		sgcirMock.getCommerceItem() >> commerceItemMcok
		catalogToolsMock.isShippingZipCodeRestrictedForSku(skuId, siteId, "250001") >> true
		
		//////getSkuRestrictionDetails method
		1*catalogToolsMock.getRestrictedSkuDetails(skuId, "250001") >> {throw new BBBSystemException("exception")}
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		0*catalogToolsMock.getSKUDetails(siteId, skuId, false) 
		
	}
	
	def "service. TC BBBBusinessException while getting RestrictedSkuDetails " (){
		given:
		setShippingAddressMock()
		setSkuDetailVo()
		String skuId = "skuId"
		String siteId = "siteId"
		commerceItemMcok.getCatalogRefId() >> skuId
		orderMock.getSiteId() >> siteId
		
		//requestMock.getObjectParameter("skuId") >> skuId
		requestMock.getObjectParameter("order") >> orderMock
		
		orderMock.getShippingGroups() >> [hgsgMock]
		hgsgMock.getShippingAddress() >>addressMock
		 
		hgsgMock.getCommerceItemRelationships() >> [sgcirMock]
		sgcirMock.getCommerceItem() >> commerceItemMcok
		catalogToolsMock.isShippingZipCodeRestrictedForSku(skuId, siteId, "250001") >> true
		
		//////getSkuRestrictionDetails method
		1*catalogToolsMock.getRestrictedSkuDetails(skuId, "250001") >> {throw new BBBBusinessException("exception")}
		
		when:
		dsrdObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("mapSkuRestrictedZip", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		0*catalogToolsMock.getSKUDetails(siteId, skuId, false)
		
	}
	
	private void setShippingAddressMock(){
		addressMock.getAddress1() >> "1 street"
		addressMock.getAddress2() >> "cross"
		addressMock.getCity()  >> "new york"
		addressMock.getState()  >> "state"
		addressMock.getPostalCode() >> "250001"
	}

	private void setSkuDetailVo(){
		imgVO.setMediumImage("img")  
		skuVoMock.setLongDescription("the long description")  
		skuVoMock.setSkuImages(imgVO)  
		skuVoMock.setDisplayName("skuDisplayName")  
	}
}
