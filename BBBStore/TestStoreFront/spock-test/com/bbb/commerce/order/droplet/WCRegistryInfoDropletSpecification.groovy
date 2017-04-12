package com.bbb.commerce.order.droplet

import atg.commerce.order.CommerceItem
import atg.commerce.order.ShippingGroup
import atg.commerce.order.ShippingGroupCommerceItemRelationship

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.order.bean.BBBCommerceItem

import spock.lang.specification.BBBExtendedSpec

class WCRegistryInfoDropletSpecification extends BBBExtendedSpec {

	def WCRegistryInfoDroplet wcriDroplet
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBPricingTools pTools = Mock()
	def BBBOrder orderMock = Mock()
	def ShippingGroup sgroup1 = Mock()
	def ShippingGroup sgroup2 = Mock()
	
	def CommerceItem commerceItem = Mock()
	def BBBCommerceItem bbbcommerceItem1 = Mock()
	def BBBCommerceItem bbbcommerceItem2 = Mock()
	def BBBCommerceItem bbbcommerceItem3 = Mock()
	def BBBCommerceItem bbbcommerceItem4 = Mock()
	
	
	
	def ShippingGroupCommerceItemRelationship sgciRelation1 = Mock()
	def ShippingGroupCommerceItemRelationship sgciRelation2 = Mock()
	def ShippingGroupCommerceItemRelationship sgciRelation3 = Mock()
	def ShippingGroupCommerceItemRelationship sgciRelation4 = Mock()
	
	
	
	
	def setup(){
		wcriDroplet = new WCRegistryInfoDroplet(catalogTools : catalogToolsMock, pricingTools : pTools)
	}
	
	def "service. to check registry ids" (){
		given:
			requestMock.getObjectParameter("order") >> orderMock
			1*orderMock.getShippingGroups() >> [sgroup1, sgroup1]
			2*sgroup1.getCommerceItemRelationships() >> [sgciRelation1, sgciRelation2, sgciRelation3, sgciRelation4]
			
			2*sgciRelation1.getCommerceItem() >> bbbcommerceItem1
			2*sgciRelation2.getCommerceItem() >> bbbcommerceItem2
			2*sgciRelation3.getCommerceItem() >> bbbcommerceItem3
			2*sgciRelation4.getCommerceItem() >> commerceItem
			
			
			bbbcommerceItem1.getRegistryId() >> "registry1"
			bbbcommerceItem2.getRegistryId() >> "registry2"
			bbbcommerceItem2.getRegistryId() >> null
			
			
		when:
			wcriDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("registryIds", "registry1,registry2,registry1,registry2")
			1*requestMock.serviceParameter("output", requestMock, responseMock);
		
	}
	
	def "service. TC when registryid list is empty " (){
		given:
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [sgroup1]
		sgroup1.getCommerceItemRelationships() >> [sgciRelation1]
		sgciRelation4.getCommerceItem() >> commerceItem
		
        when:
		wcriDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.serviceParameter("empty", requestMock, responseMock);
						
	}
	
	def "service. TC when order is null" (){
		given:
		requestMock.getObjectParameter("order") >> null
		when:
		wcriDroplet.service(requestMock, responseMock)
		
		then:
		0*requestMock.serviceParameter("empty", requestMock, responseMock)
		0*requestMock.serviceParameter("output", requestMock, responseMock)
	}
}
