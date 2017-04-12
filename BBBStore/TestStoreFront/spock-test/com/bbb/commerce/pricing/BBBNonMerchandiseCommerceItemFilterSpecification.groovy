package com.bbb.commerce.pricing

import java.util.List;
import java.util.Map;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem
import spock.lang.specification.BBBExtendedSpec;;

class BBBNonMerchandiseCommerceItemFilterSpecification extends BBBExtendedSpec {
	
	def PricingContext pricingContext = Mock()
	def RepositoryItem pProfile =Mock()
	def FilteredCommerceItem fItems  = Mock()
	BBBNonMerchandiseCommerceItemFilter itemFilter 
	
	int filterContext
	Map extraParametersMap
	Map detailsPendingActingAsQualifier
	Map detailsRangesToReceiveDiscount
	
	
	def setup(){
		itemFilter = Spy()
	}
	
	def"filterItems, when filteredItems List is not empty and wrappedItem is an instance of NonMerchandiseCommerceItem"(){
		
		given:
		
		def NonMerchandiseCommerceItem item = Mock()
		List<FilteredCommerceItem> filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItems)
		fItems.getWrappedItem()>> item
		
		when:
		itemFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,
			filteredItems)
		
		then:
		itemFilter.logDebug("Leaving BBBNonMerchandiseCommerceItemFilter.filterItems because filteredItems is a null list");
		filteredItems.isEmpty() == true
	}
	
	def"filterItems, when filteredItems List is null"(){
		
		given:
		
		when:
		itemFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,null)
		
		then:
		1*itemFilter.logDebug("Leaving BBBNonMerchandiseCommerceItemFilter.filterItems because filteredItems is a null list");
	}
	
	def"filterItems, when filteredItems List is empty"(){
		
		given:
		List<FilteredCommerceItem> filteredItems = new ArrayList<FilteredCommerceItem>()
		
		when:
		itemFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*itemFilter.logDebug("Leaving BBBNonMerchandiseCommerceItemFilter.filterItems because filteredItems is a null list");
	}
	
	def"filterItems, when filteredItems List is not null and wrappedItem is not an instance of NonMerchandiseCommerceItem"(){
		
		given:
		def CommerceItem item = Mock()
		List<FilteredCommerceItem> filteredItems = new ArrayList<FilteredCommerceItem>()
		filteredItems.add(fItems)
		fItems.getWrappedItem()>> item
		
		when:
		itemFilter.filterItems(filterContext,pricingContext,extraParametersMap,detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*itemFilter.logDebug("Leaving BBBNonMerchandiseCommerceItemFilter.filterItems because filteredItems is a null list");
		filteredItems.isEmpty() == false
	}

}
