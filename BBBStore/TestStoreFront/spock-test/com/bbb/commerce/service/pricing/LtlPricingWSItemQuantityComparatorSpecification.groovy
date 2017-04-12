package com.bbb.commerce.service.pricing

import spock.lang.specification.BBBExtendedSpec;
import com.bedbathandbeyond.atg.Item;

class LtlPricingWSItemQuantityComparatorSpecification extends BBBExtendedSpec  {

	LtlPricingWSItemQuantityComparator ltlPricing
	
	def setup(){
		ltlPricing = new LtlPricingWSItemQuantityComparator()		
	}
	
	def"compare delivery charge in ShipMethodVO when item1 quantity is less than item2 quantity"(){
		
		given:
		def Item item1 =Mock()
		def Item item2 =Mock()
		
		1*item1.getQuantity() >>10
		1*item2.getQuantity() >>20
		
		when:
		int value =ltlPricing.compare(item1, item2)
		
		then:
		value ==1
	}
	
	
	def"compare delivery charge in ShipMethodVO when item1 quantity is greater than item2 quantity"(){
		
		given:
		def Item item1 =Mock()
		def Item item2 =Mock()
		
		1*item1.getQuantity() >>20
		1*item2.getQuantity() >>10
		
		when:
		int value =ltlPricing.compare(item1, item2)
		
		then:
		value == -1
	}
	
	def"compare delivery charge in ShipMethodVO when item1 quantity is equal to item2 quantity"(){
		
		given:
		def Item item1 =Mock()
		def Item item2 =Mock()
		
		1*item1.getQuantity() >>10
		1*item2.getQuantity() >>10
		
		when:
		int value =ltlPricing.compare(item1, item2)
		
		then:
		value == 0
	}
}
