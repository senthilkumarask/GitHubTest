package com.bbb.commerce.pricing

import java.util.List;
import java.util.Map;
import com.bbb.commerce.catalog.BBBCatalogTools;
import atg.commerce.order.CommerceItem
import atg.commerce.order.Order
import atg.commerce.pricing.FilteredCommerceItem
import atg.commerce.pricing.PricingContext;
import spock.lang.specification.BBBExtendedSpec;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

class BBBGiftCardFilterSpecification extends BBBExtendedSpec{
	
	def PricingContext pricingContext =Mock()
	def BBBCatalogTools cTools =Mock()
	
	int filterContext
	Map extraParametersMap
	Map detailsPendingActingAsQualifier
	Map detailsRangesToReceiveDiscount
	BBBGiftCardFilter bbbFilter
	
	
	def setup(){
		bbbFilter = Spy()	
		bbbFilter.setCatalogTools(cTools)
	}
	
	def"tests if itme should be removed if it is a gift card item"(){
		
		given:
		def CommerceItem cItem = Mock()
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		
		pricingContext.getOrder() >> order
		order.getSiteId() >> "123"
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> true
		
		List<FilteredCommerceItem>filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItem)
		
		when:
		bbbFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +filteredItems + " pricingContext -" + pricingContext)
		filteredItems.isEmpty() == true
	}
	
	def"tests if item should not be removed if it is not a gift card item"(){
		
		given:
		def CommerceItem cItem = Mock()
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		
		pricingContext.getOrder() >> order
		order.getSiteId() >> "123"
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> false
		
		List<FilteredCommerceItem>filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItem)
		
		when:
		bbbFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +filteredItems + " pricingContext -" + pricingContext)
		filteredItems.isEmpty() == false
	}
	
	def"tests if pricing context is null"(){
		
		given:
		
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		
		List<FilteredCommerceItem>filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItem)
		
		when:
		bbbFilter.filterItems(filterContext,null,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +filteredItems + " pricingContext -" + null)
	}
	
	def"tests if order is null"(){
		
		given:
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		pricingContext.getOrder() >> null
		List<FilteredCommerceItem>filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItem)
		
		when:
		bbbFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +filteredItems + " pricingContext -" + pricingContext)
	}
	
	def"tests if filtered items is null"(){
		
		given:
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		pricingContext.getOrder() >> order
		
		when:
		bbbFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,null)
		
		then:
		1*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +null + " pricingContext -" + pricingContext)
	}
	
	def"tests if filtered items is empty"(){
		
		given:
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		pricingContext.getOrder() >> order
		List<FilteredCommerceItem>filteredItems = new ArrayList<FilteredCommerceItem>()
		
		
		when:
		bbbFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +filteredItems + " pricingContext -" + pricingContext)
	}
	
	def"tests if BBBSystemException is thrown"(){
		
		given:
		def CommerceItem cItem = Mock()
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		
		pricingContext.getOrder() >> order
		order.getSiteId() >> "123"
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >>"refId" 
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> {throw  new BBBSystemException("exception")}
		
		List<FilteredCommerceItem>filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItem)
		
		when:
		bbbFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +filteredItems + " pricingContext -" + pricingContext)
		filteredItems.isEmpty() == false
		//1*bbbFilter. logError("BBBSystem Exception occured while identifying a gift card item ",  cItem.getCatalogRefId())
	}
	
	def"tests if BBBBusinessException is thrown"(){
		
		given:
		def CommerceItem cItem = Mock()
		def Order order = Mock()
		def FilteredCommerceItem fItem = Mock()
		
		pricingContext.getOrder() >> order
		order.getSiteId() >> "123"
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isGiftCardItem(order.getSiteId(), cItem.getCatalogRefId()) >> {throw  new BBBBusinessException("exception")}
		
		List<FilteredCommerceItem>filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItem)
		
		when:
		bbbFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier,detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*bbbFilter.logDebug("Leaving BBBGiftCardFilter.filterItems because of invalid parameters : filteredItems - " +filteredItems + " pricingContext -" + pricingContext)
		filteredItems.isEmpty() == false
		//1*bbbFilter. logError("BBBSystem Exception occured while identifying a gift card item ",  cItem.getCatalogRefId())
	}
}
