package com.bbb.commerce.checkout.droplet

import atg.commerce.order.CommerceItem
import atg.commerce.order.CreditCard
import atg.commerce.order.ElectronicShippingGroup
import atg.commerce.order.Order
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.core.util.Address
import atg.repository.Repository
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec

class BBBSkuPropDetailsDropletSpecification extends BBBExtendedSpec {
    def BBBSkuPropDetailsDroplet spdDroplet
	def BBBCatalogTools catalogToolsMock = Mock()
	def Repository repositoryMock = Mock()
	def Order orderMock = Mock()
	def BBBHardGoodShippingGroup hgdsMock = Mock()
	def Address shippingAddressMock = Mock()
	def ShippingGroupCommerceItemRelationship sgcirMock = Mock()
	def CommerceItem commerceItemMock = Mock()
	def SKUDetailVO skuDetailVo = new SKUDetailVO()
	def ElectronicShippingGroup elsGroup = Mock()
	def setup(){
	spdDroplet = new BBBSkuPropDetailsDroplet(catalogTools : catalogToolsMock, orderRepository : repositoryMock )
}

	def"sercvice.Tc to set skuDetails and  skuProdStatus in request object   "(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		hgdsMock.getCommerceItemRelationships() >> [sgcirMock]
		// seting state value
		shippingAddressMock.getState() >> "CA"
		
		// setting C.Itme to relationship Mock
		commerceItemMock.getCatalogRefId() >> "sku123"
		sgcirMock.getCommerceItem() >> commerceItemMock
		
		//getting skuDetail
		skuDetailVo.setDisplayName("Towle")
		catalogToolsMock.getSKUDetails("USBed", "sku123", false) >> skuDetailVo
		
		//getting skuProdMap 
		catalogToolsMock.getSkuPropFlagStatus("sku123") >> ["sku1" : "prod89"]
		when:
		spdDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuDetails", ['sku123':'Towle']);
		1*requestMock.setParameter("skuProdStatus", ['sku123':["sku1" : "prod89"]]);

	}	
	
	def"sercvice.Tc to set empty as open parameter in request object when skuProdMap is empty   "(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		hgdsMock.getCommerceItemRelationships() >> [sgcirMock]
		// seting state value
		shippingAddressMock.getState() >> "CA"
		
		// setting C.Itme to relationship Mock
		commerceItemMock.getCatalogRefId() >> "sku123"
		sgcirMock.getCommerceItem() >> commerceItemMock
		
		//getting skuDetail
		skuDetailVo.setDisplayName("Towle")
		catalogToolsMock.getSKUDetails("USBed", "sku123", false) >> skuDetailVo
		
		//getting skuProdMap
		catalogToolsMock.getSkuPropFlagStatus("sku123") >> [:]
		when:
		spdDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("empty", requestMock, responseMock);

	}
	
	def"sercvice.Tc when Shiiping Group is not HardGoodShippingGroup"(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [elsGroup]
		// seting state value
		when:
		spdDroplet.service(requestMock, responseMock)
		
		then:
		0*catalogToolsMock.getSKUDetails("USBed", "sku123", false)

	}
	
	def"sercvice.Tc when Shiiping address'state is not CA"(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		
		// seting state value
		shippingAddressMock.getState() >> "NA"

		when:
		spdDroplet.service(requestMock, responseMock)
		
		then:
		0*catalogToolsMock.getSKUDetails("USBed", "sku123", false)

	}
	
	def"sercvice.Tc when order is null"(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> null
		
		
		when:
		spdDroplet.service(requestMock, responseMock)
		
		then:
		0*catalogToolsMock.getSKUDetails("USBed", _, _)

	}
	
	def"sercvice.Tc to skuId is null   "(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		hgdsMock.getCommerceItemRelationships() >> [sgcirMock]
		// seting state value
		shippingAddressMock.getState() >> "CA"
		
		// setting C.Itme to relationship Mock
		commerceItemMock.getCatalogRefId() >> null
		sgcirMock.getCommerceItem() >> commerceItemMock
		
		//getting skuDetail
		skuDetailVo.setDisplayName("Towle")
		catalogToolsMock.getSKUDetails("USBed", _, false) >> skuDetailVo
		
		when:
		spdDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("skuDetails", _);

	}
	
	def"sercvice.Tc for BBBSystemException "(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		hgdsMock.getCommerceItemRelationships() >> [sgcirMock]
		// seting state value
		shippingAddressMock.getState() >> "CA"
		
		// setting C.Itme to relationship Mock
		commerceItemMock.getCatalogRefId() >> "sku123"
		sgcirMock.getCommerceItem() >> commerceItemMock
		
		//getting skuDetail
		skuDetailVo.setDisplayName("Towle")
		catalogToolsMock.getSKUDetails("USBed", "sku123", false) >> {throw new BBBSystemException("exception") }
		
		//getting skuProdMap
		catalogToolsMock.getSkuPropFlagStatus("sku123") >> ["sku1" : "prod89"]
		when:
		spdDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuDetails", [:]);
		1*requestMock.setParameter("skuProdStatus", ['sku123':["sku1" : "prod89"]]);

	}
	
	def"sercvice.Tc for BBBBusinessException while getting skuDetail  "(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		hgdsMock.getCommerceItemRelationships() >> [sgcirMock]
		// seting state value
		shippingAddressMock.getState() >> "CA"
		
		// setting C.Itme to relationship Mock
		commerceItemMock.getCatalogRefId() >> "sku123"
		sgcirMock.getCommerceItem() >> commerceItemMock
		
		//getting skuDetail
		skuDetailVo.setDisplayName("Towle")
		catalogToolsMock.getSKUDetails("USBed", "sku123", false) >> {throw new BBBBusinessException("exception") }
		
		//getting skuProdMap
		catalogToolsMock.getSkuPropFlagStatus("sku123") >> ["sku1" : "prod89"]
		when:
		spdDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("skuDetails", [:]);
		1*requestMock.setParameter("skuProdStatus", ['sku123':["sku1" : "prod89"]]);

	}
	
	def"sercvice.Tc for BBBBusinessException while getting skuProdMap  "(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		hgdsMock.getCommerceItemRelationships() >> [sgcirMock]
		// seting state value
		shippingAddressMock.getState() >> "CA"
		
		// setting C.Itme to relationship Mock
		commerceItemMock.getCatalogRefId() >> "sku123"
		sgcirMock.getCommerceItem() >> commerceItemMock
		
		//getting skuDetail
		skuDetailVo.setDisplayName("Towle")
		catalogToolsMock.getSKUDetails("USBed", "sku123", false) >> skuDetailVo
		
		//getting skuProdMap
		catalogToolsMock.getSkuPropFlagStatus("sku123") >> {throw new BBBBusinessException("exception") }
		when:
		spdDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("skuDetails", ['sku123':'Towle']);
		0*requestMock.setParameter("skuProdStatus", [:]);

	}
	
	def"sercvice.Tc for BBBSystemException while getting skuProdMap  "(){
		given:
		orderMock.getSiteId() >> "USBed"
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgdsMock]
		hgdsMock.getShippingAddress() >> shippingAddressMock
		hgdsMock.getCommerceItemRelationships() >> [sgcirMock]
		// seting state value
		shippingAddressMock.getState() >> "CA"
		
		// setting C.Itme to relationship Mock
		commerceItemMock.getCatalogRefId() >> "sku123"
		sgcirMock.getCommerceItem() >> commerceItemMock
		
		//getting skuDetail
		skuDetailVo.setDisplayName("Towle")
		catalogToolsMock.getSKUDetails("USBed", "sku123", false) >> skuDetailVo
		
		//getting skuProdMap
		catalogToolsMock.getSkuPropFlagStatus("sku123") >> {throw new BBBSystemException("exception") }
		when:
		spdDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("skuDetails", ['sku123':'Towle']);
		0*requestMock.setParameter("skuProdStatus", [:]);

	}
}
