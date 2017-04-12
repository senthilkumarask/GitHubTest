package com.bbb.commerce.order.purchase

import atg.commerce.order.ElectronicShippingGroup
import atg.commerce.order.HardgoodShippingGroup
import atg.core.util.Address;
import com.bbb.commerce.common.BBBAddressImpl
import spock.lang.specification.BBBExtendedSpec

class BBBShippingGroupContainerServiceSpecification extends BBBExtendedSpec {

	def BBBShippingGroupContainerService sgcService
	def HardgoodShippingGroup hgsg = Mock() 
	def ElectronicShippingGroup elsg = Mock()
	BBBAddressImpl address = new BBBAddressImpl()
	Address address1 = Mock()
	
	def setup(){
	 sgcService  = new BBBShippingGroupContainerService()
	}
	
	def"getNewShippingGroupName. TC to get the identifier  "(){
		given:
		  	def ArrayList list = [] 	
			1*hgsg.getShippingAddress() >> address
			address.setIdentifier("identi")
		when:
			String value = sgcService.getNewShippingGroupName(hgsg, list)
		then:
		value == "identi"
	}
	
	def"getNewShippingGroupName. TC when shipping address type is not BBBAddress "(){
		given:
			  def ArrayList list = []
			1*hgsg.getShippingAddress() >> address1
			address.setIdentifier("identi")
		when:
			String value = sgcService.getNewShippingGroupName(hgsg, list)
		then:
		value.isEmpty()  == false
	}
	
	def"getNewShippingGroupName. TC when shipping address  is null "(){
		given:
			  def ArrayList list = []
			1*hgsg.getShippingAddress() >> null
			address.setIdentifier("identi")
		when:
			String value = sgcService.getNewShippingGroupName(hgsg, list)
		then:
		value.isEmpty()  == false
	}
	
	def"getNewShippingGroupName. TC when shipping group type is not HardgoodShippingGroup "(){
		given:
			  def ArrayList list = []
			elsg.getId() >> "sd1"
		when:
			String value = sgcService.getNewShippingGroupName(elsg, list)
		then:
		value == "sd1"
	}
	
	/******************************************getShippingGroupName ******************************/
	
	def"getShippingGroupName. TC to get the identifier  "(){
		given:
			  def ArrayList list = []
			1*hgsg.getShippingAddress() >> address
			address.setIdentifier("identi")
		when:
			String value = sgcService.getShippingGroupName(hgsg, list)
		then:
		value == "identi"
	}
	
	def"getShippingGroupName. TC when shipping address type is not BBBAddress "(){
		given:
			  def ArrayList list = []
			1*hgsg.getShippingAddress() >> address1
			address.setIdentifier("identi")
		when:
			String value = sgcService.getShippingGroupName(hgsg, list)
		then:
		value.isEmpty()  == false
	}
	
	def"getShippingGroupName. TC when shipping address  is null "(){
		given:
			  def ArrayList list = []
			1*hgsg.getShippingAddress() >> null
			address.setIdentifier("identi")
		when:
			String value = sgcService.getShippingGroupName(hgsg, list)
		then:
		value.isEmpty()  == false
	}
	
	def"getShippingGroupName. TC when shipping group type is not HardgoodShippingGroup "(){
		given:
			  def ArrayList list = []
			elsg.getId() >> "sd1"
		when:
			String value = sgcService.getShippingGroupName(elsg, list)
		then:
		value == "sd1"
	}
	
	
}
