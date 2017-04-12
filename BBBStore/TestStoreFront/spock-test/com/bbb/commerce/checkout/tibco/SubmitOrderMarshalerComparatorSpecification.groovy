package com.bbb.commerce.checkout.tibco

import atg.commerce.pricing.DetailedItemPriceInfo
import spock.lang.specification.BBBExtendedSpec

class SubmitOrderMarshalerComparatorSpecification extends BBBExtendedSpec {
	
	def SubmitOrderMarshalerComparator submitOrderMarshalerComparatorMock
	def DetailedItemPriceInfo mockFirstItem = Mock()
	def DetailedItemPriceInfo mockSecondItem = Mock()
	
	def setup(){
		submitOrderMarshalerComparatorMock = new SubmitOrderMarshalerComparator()
	}
	
	def "compare: When DetailedPriceInfo of first item is equal to that of second item"(){
		given:
		int result = 0
		Long firstItemQuantity = 5L
		Long secondItemQuantity = 5L
		
		mockFirstItem.quantity >> firstItemQuantity
		mockSecondItem.quantity >> secondItemQuantity
		
		when:
		result = submitOrderMarshalerComparatorMock.compare(mockFirstItem, mockSecondItem)
		
		then:
		result == 0
	}
	
	def "compare: When DetailedPriceInfo of first item is less than that of second item"(){
		given:
		int result = 0
		Long firstItemQuantity = 5L
		Long secondItemQuantity = 2L
		
		mockFirstItem.quantity >> firstItemQuantity
		mockSecondItem.quantity >> secondItemQuantity
		
		when:
		result = submitOrderMarshalerComparatorMock.compare(mockFirstItem, mockSecondItem)
		
		then:
		result == 1
	}
	
	def "compare: When DetailedPriceInfo of first item is greater than that of second item"(){
		given:
		int result = 0
		Long firstItemQuantity = 5L
		Long secondItemQuantity = 7L
		
		mockFirstItem.quantity >> firstItemQuantity
		mockSecondItem.quantity >> secondItemQuantity
		
		when:
		result = submitOrderMarshalerComparatorMock.compare(mockFirstItem, mockSecondItem)
		
		then:
		result == -1
	}
}
