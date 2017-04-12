package com.bbb.commerce.pricing

import java.util.ArrayList;
import java.util.List;
import java.util.Map

import atg.commerce.order.CommerceItem
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.OrderPriceInfo
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.definition.MatchingObject
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import spock.lang.specification.BBBExtendedSpec;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCheckoutConstants;;

class BBBQualifierSpecification extends BBBExtendedSpec{
	
	
	def PricingContext pPricingContext =Mock()
	def BBBCatalogTools cTools =Mock()
	def RepositoryItem rItem= Mock()
	RepositoryItemDescriptor descriptor =Mock()
	
	int filterContext
	Map detailsPendingActingAsQualifier
	Map detailsRangesToReceiveDiscount
	BBBQualifier bbbQual
	
	def setup(){
		bbbQual = Spy()
}
	
	def"findQualifyingShipping, check if threshold amount is set in pExtraParametersMap"(){
		given:
		def MatchingObject match=Mock()
		def OrderPriceInfo orderInfo = new OrderPriceInfo()
		
		Map<String, Object> pExtraParametersMap = new HashMap<>()
		pExtraParametersMap.put(BBBCheckoutConstants.THRESHOLD_AMOUNT, new Double(120))
		
		1*bbbQual.extractSuperFindQualifyingShipping(pPricingContext, pExtraParametersMap) >> {match}
		
		pPricingContext.getPricingModel() >> rItem
		rItem.getItemDescriptor() >>descriptor
		descriptor.getItemDescriptorName() >>"closenessQualifier"
		pPricingContext.getOrderPriceQuote()>> orderInfo
		bbbQual.setLoggingDebug(true)
		
		when:
		match=bbbQual.findQualifyingShipping(pPricingContext, pExtraParametersMap)
		
		then:
		orderInfo.getAmount() == 0  //check with imran
		1*bbbQual.logDebug("findQualifyingShipping() method : thresholdAmount to be set in Order's priceInfo  - "+pExtraParametersMap.get(BBBCheckoutConstants.THRESHOLD_AMOUNT))
		1*bbbQual.logInfo('findQualifyingShipping() method : MatchingObject object null')	
	}
	
	def"findQualifyingShipping, check if pricing model is null"(){
		
		given:
		def MatchingObject match=Mock()
		def OrderPriceInfo orderInfo = new OrderPriceInfo()
		def FilteredCommerceItem fItem =Mock()
		def CommerceItem cItem=Mock()
		def ItemPriceInfo itemInfo=Mock()
		Map<String, Object> pExtraParametersMap = new HashMap<>()
			
		1*bbbQual.extractSuperFindQualifyingShipping(pPricingContext, pExtraParametersMap) >> {match}
		pPricingContext.getPricingModel() >> null
	    pPricingContext.getItems() >> ["123"]
		pPricingContext.getOrderPriceQuote()>> orderInfo
		bbbQual.setLoggingDebug(true)
		itemInfo.getAmount() >> 0
		
		when:
		match=bbbQual.findQualifyingShipping(pPricingContext, pExtraParametersMap)
		
		then:
		1*bbbQual.logDebug("findQualifyingShipping() method : Items quantity before applying filters - "+pPricingContext.getItems().size())
		1*bbbQual.logDebug("findQualifyingShipping() method : thresholdAmount in Order's priceInfo before applying filters   - "+orderInfo.getAmount())
		1*bbbQual.logDebug("findQualifyingShipping() method : thresholdAmount to be set in Order's priceInfo  - "+itemInfo.getAmount())
		1*bbbQual.doFilters(1, pPricingContext, pExtraParametersMap, null, _, _);
		pExtraParametersMap.get(BBBCheckoutConstants.THRESHOLD_AMOUNT) ==0.0
		orderInfo.getAmount() == 0
	}
	
	def"findQualifyingShipping, check if item descriptor name is not equal to closeness qualifier "(){
		
		given:
		def MatchingObject match=Mock()
		def OrderPriceInfo orderInfo = new OrderPriceInfo()
		def FilteredCommerceItem fItem =Mock()
		def CommerceItem cItem=Mock()
		def ItemPriceInfo itemInfo=Mock()
		Map<String, Object> pExtraParametersMap = new HashMap<>()
			
		1*bbbQual.extractSuperFindQualifyingShipping(pPricingContext, pExtraParametersMap) >> {match}
		pPricingContext.getPricingModel() >> rItem
		rItem.getItemDescriptor() >>descriptor
		descriptor.getItemDescriptorName() >>"closeness"
		pPricingContext.getItems() >> ["123"]
		pPricingContext.getOrderPriceQuote()>> orderInfo
		bbbQual.setLoggingDebug(true)
		itemInfo.getAmount() >> 0
		
		when:
		match=bbbQual.findQualifyingShipping(pPricingContext, pExtraParametersMap)
		
		then:
		1*bbbQual.logDebug("findQualifyingShipping() method : Items quantity before applying filters - "+pPricingContext.getItems().size())
		1*bbbQual.logDebug("findQualifyingShipping() method : thresholdAmount in Order's priceInfo before applying filters   - "+orderInfo.getAmount())
		1*bbbQual.logDebug("findQualifyingShipping() method : thresholdAmount to be set in Order's priceInfo  - "+itemInfo.getAmount())
		1*bbbQual.doFilters(1, pPricingContext, pExtraParametersMap, null, _, _);
		pExtraParametersMap.get(BBBCheckoutConstants.THRESHOLD_AMOUNT) ==0.0
		orderInfo.getAmount() == 0
	}
	
	def"findQualifyingShipping, check if threshold amount is not set in pExtraParametersMap "(){
		
		given:
		def MatchingObject match=Mock()
		def OrderPriceInfo orderInfo = new OrderPriceInfo()
		def FilteredCommerceItem fItem =Mock()
		def CommerceItem cItem=Mock()
		def ItemPriceInfo itemInfo=Mock()
		Map<String, Object> pExtraParametersMap = new HashMap<>()
		pExtraParametersMap.put(BBBCheckoutConstants.THRESHOLD_AMOUNT, null)
			
		1*bbbQual.extractSuperFindQualifyingShipping(pPricingContext, pExtraParametersMap) >> {match}
		pPricingContext.getPricingModel() >> rItem
		rItem.getItemDescriptor() >>descriptor
		descriptor.getItemDescriptorName() >>"closenessQualifier"
		pPricingContext.getItems() >> ["123"]
		pPricingContext.getOrderPriceQuote()>> orderInfo
		bbbQual.setLoggingDebug(true)
		itemInfo.getAmount() >> 0
		
		when:
		match=bbbQual.findQualifyingShipping(pPricingContext, pExtraParametersMap)
		
		then:
		1*bbbQual.logDebug("findQualifyingShipping() method : Items quantity before applying filters - "+pPricingContext.getItems().size())
		1*bbbQual.logDebug("findQualifyingShipping() method : thresholdAmount in Order's priceInfo before applying filters   - "+orderInfo.getAmount())
		1*bbbQual.logDebug("findQualifyingShipping() method : thresholdAmount to be set in Order's priceInfo  - "+itemInfo.getAmount())
		1*bbbQual.doFilters(1, pPricingContext, pExtraParametersMap, null, _, _);
		pExtraParametersMap.get(BBBCheckoutConstants.THRESHOLD_AMOUNT) ==0.0
		orderInfo.getAmount() == 0
	}
	
	def"findQualifyingShipping, check if RepositoryException is thrown"(){
		given:
		def MatchingObject match=Mock()
		def OrderPriceInfo orderInfo = new OrderPriceInfo()
		Map<String, Object> pExtraParametersMap = new HashMap<>()
		0*bbbQual.extractSuperFindQualifyingShipping(pPricingContext, pExtraParametersMap) >> {match}
		
		pPricingContext.getPricingModel() >> rItem
		rItem.getItemDescriptor() >>{throw new RepositoryException("")}
		pPricingContext.getOrderPriceQuote()>> orderInfo
		bbbQual.setLoggingDebug(true)
		
		when:
		match=bbbQual.findQualifyingShipping(pPricingContext, pExtraParametersMap)
		
		then:
		1*bbbQual.vlogError('BBBQualifier.findQualifyingShipping: Repository exception occured while trying to get itemdescriptor name from promotion {0}', _)
		Exception msg = thrown()
	}
	
	
	
}
