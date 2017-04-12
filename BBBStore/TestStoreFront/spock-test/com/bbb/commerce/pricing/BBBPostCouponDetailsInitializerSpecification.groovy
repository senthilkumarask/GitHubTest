package com.bbb.commerce.pricing

import java.util.Locale;
import java.util.Map

import com.bbb.constants.BBBCoreConstants
import com.bbb.framework.performance.BBBPerformanceMonitor
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;

import atg.commerce.order.CommerceItem
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingContext;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor
import spock.lang.specification.BBBExtendedSpec;

class BBBPostCouponDetailsInitializerSpecification extends BBBExtendedSpec{


	def PricingContext pricingcontext = Mock()
	def BBBCommerceItem bbbCommerceItem = Mock()
	def BBBPostCouponDetailsInitializer postCoupon
	def CommerceItem pItem =Mock()
	def RepositoryItem pPricingModel =Mock()
	def RepositoryItem pProfile =Mock()
	def SiteContextManager siteManager = Mock()
	def ItemPriceInfo pPriceQuote =Mock()

	String pPriceListPropertyName
	boolean pUseDefaultPriceList
	Locale pLocale

	def setup(){
		postCoupon = Spy()
		postCoupon.setLoggingDebug(true)
	}

	def"priceItem, when repository id is equal to promotion id and item descriptor name is equal to item discount"(){

		given:
		
		def RepositoryItem promo = Mock()
		def RepositoryItemDescriptor descriptor = Mock()

		Map<RepositoryItem,String> promoMap = new HashMap()
		promoMap.put(promo, "promo1")

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap)

		pPricingModel.getRepositoryId() >> "promo1"
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "Item Discount"
		postCoupon.setLoggingDebug(false)

		when:
		postCoupon.priceItem(pPriceQuote,pItem, pPricingModel, pLocale, pProfile, pExtraParameters)

		then:
		Map m =pExtraParameters.get(BBBCoreConstants.PROMO_MAP, promoMap)
		m.containsKey(promo) == false
		m.containsValue("promo1") == false


	}

	def"priceItem, when promoMap is null"(){

		given:
		Map<RepositoryItem,String> promoMap
		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, null)

		when:
		postCoupon.priceItem(pPriceQuote,pItem, pPricingModel, pLocale, pProfile, pExtraParameters)

		then:
		Map m = pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		m == null
		1*postCoupon.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBCouponDetailsInitializer.priceItem :: value of map " + promoMap))
	}

	def"priceItem, when pPricingModel is null"(){
		
		given:
		Map<RepositoryItem,String> promoMap = new HashMap<>()
		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap)
		
		when:
		postCoupon.priceItem(pPriceQuote,pItem, null, pLocale, pProfile, pExtraParameters)
		
		then:
		Map m=pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		m.isEmpty() == true
		1*postCoupon.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBCouponDetailsInitializer.priceItem :: value of map {}"))
		
	}
	
	def"priceItem, when repository id is  not equal to promotion id"(){
		
		given:
		def RepositoryItem promo = Mock()
		def RepositoryItemDescriptor descriptor = Mock()
		
		Map<RepositoryItem,String> promoMap = new HashMap()
		promoMap.put(promo, "promotion")
		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap)
		pPricingModel.getRepositoryId() >> "promo1"
		pPricingModel.getItemDescriptor() >> descriptor
		
		when:
		postCoupon.priceItem(pPriceQuote,pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		Map m=pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		m.containsKey(promo)== true
		m.containsValue("promotion")== true
		// 1*postCoupon.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBCouponDetailsInitializer.priceItem :: value of map " + promoMap))
		
	}
	
	def"priceItem, when repository id is equal to promotion id and item descriptor name is  not equal to item discount"(){
		
		given:
		def RepositoryItem promo = Mock()
		def RepositoryItemDescriptor descriptor = Mock()
		
		Map<RepositoryItem,String> promoMap = new HashMap()
		promoMap.put(promo, "promo1")
		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap)
		pPricingModel.getRepositoryId() >> "promo1"
		pPricingModel.getItemDescriptor() >> descriptor
		descriptor.getItemDescriptorName() >> "Item"
		
		when:
		postCoupon.priceItem(pPriceQuote,pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		Map m=pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		m.containsKey(promo)== true
		m.containsValue("promo1")== true
		// 1*postCoupon.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBCouponDetailsInitializer.priceItem :: value of map " + promoMap))	
	}
	
	def"priceItem, when Repository Exception is thrown while getting item descriptor name"(){
		
		given:
		def RepositoryItem promo = Mock()
		
		Map<RepositoryItem,String> promoMap = new HashMap()
		promoMap.put(promo, "promo1")
		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap)
		pPricingModel.getRepositoryId() >> "promo1"
		pPricingModel.getItemDescriptor() >>{throw new RepositoryException("exception")}
		
		when:
		postCoupon.priceItem(pPriceQuote,pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		Map m=pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		m.containsKey(promo)== true
		m.containsValue("promo1")== true
		// 1*postCoupon.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBCouponDetailsInitializer.priceItem :: value of map " + promoMap))
		
	}
	
	def"priceItem, when Repository Exception is thrown while getting item descriptor name, log debug is set to false"(){
		
		given:
		def RepositoryItem promo = Mock()
		Map<RepositoryItem,String> promoMap = new HashMap()
		promoMap.put(promo, "promo1")
		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap)
		
		pPricingModel.getRepositoryId() >> "promo1"
		pPricingModel.getItemDescriptor() >>{throw new RepositoryException("exception")}
		postCoupon.setLoggingDebug(false)
		
		when:
		postCoupon.priceItem(pPriceQuote,pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		Map m=pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		m.containsKey(promo)== true
		m.containsValue("promo1")== true
		// 1*postCoupon.logDebug(LogMessageFormatter.formatMessage(null,"End- BBBCouponDetailsInitializer.priceItem :: value of map " + promoMap))
		
	}


}
