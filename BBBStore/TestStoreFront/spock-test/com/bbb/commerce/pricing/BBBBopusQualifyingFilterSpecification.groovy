package com.bbb.commerce.pricing

import java.util.List;
import java.util.Map;

import atg.commerce.order.CommerceItem
import atg.commerce.pricing.FilteredCommerceItem
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import spock.lang.specification.BBBExtendedSpec;
import com.bbb.order.bean.BBBCommerceItem;



class BBBBopusQualifyingFilterSpecification  extends BBBExtendedSpec {

	def PricingContext pricingcontext = Mock()
    FilteredCommerceItem fItem  = Mock()
	def BBBCommerceItem bbbCommerceItem = Mock()
	def BBBBopusQualifyingFilter bopusFilter
	def CommerceItem cItem =Mock()
	
	int i
	Map map
	Map map1
	Map map2
	List<FilteredCommerceItem> list 
	
	
	def setup(){
		bopusFilter = new BBBBopusQualifyingFilter()
	}
	
	
	def"filterItems,  Tests when List is not null and FilteredCommerceItem is an instance of BBBCommerceItem and StoreId is not empty"(){
		
		given:
		list = new ArrayList<FilteredCommerceItem>()
		list.add(fItem)
		fItem.getWrappedItem() >> bbbCommerceItem
		bbbCommerceItem.getStoreId() >> "123"
				
		when:
		bopusFilter.filterItems(i, pricingcontext, map, map1,  map2, list)
		
		then:
		list.isEmpty() == true
		
	}
	
	def"filterItems,  Tests when List is  null"(){
		
		given:
		
		
		when:
		bopusFilter.filterItems(i, pricingcontext, map, map1,  map2, list)
		
		then:
		list == null
		
	}
	
	def"filterItems,  Tests when List is not null, FilteredCommerceItem is not an instance of BBBCommerceItem and StoreId is not empty"(){
		
		given:
		list =new ArrayList<FilteredCommerceItem>()
		list.add(fItem)
		fItem.getWrappedItem() >> cItem
		bbbCommerceItem.getStoreId() >> "123"
				
		when:
		bopusFilter.filterItems(i, pricingcontext, map, map1,  map2, list)
		
		then:
		list.isEmpty() == false
		
	}
	
	def"filterItems,  Tests when List is not null,FilteredCommerceItem is an instance of BBBCommerceItem and StoreId is empty"(){
		
		given:
		list =new ArrayList<FilteredCommerceItem>()
		list.add(fItem)
		fItem.getWrappedItem() >> bbbCommerceItem
		bbbCommerceItem.getStoreId() >> ""
				
		when:
		bopusFilter.filterItems(i, pricingcontext, map, map1,  map2, list)
		
		then:
		list.isEmpty() == false
		
	}
	
	
}
