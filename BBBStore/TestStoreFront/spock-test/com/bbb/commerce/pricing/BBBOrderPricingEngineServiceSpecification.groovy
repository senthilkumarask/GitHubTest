package com.bbb.commerce.pricing


import java.util.Locale;
import java.util.Map

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.order.bean.BBBCommerceItem;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.multisite.SiteContextManager;
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;

class BBBOrderPricingEngineServiceSpecification extends BBBExtendedSpec{
	
	
	def PricingContext pricingcontext = Mock()
	def BBBCommerceItem bbbCommerceItem = Mock()
	def BBBOrderPricingEngineService orderService
	def CommerceItem cItem =Mock()
	def RepositoryItem pProfile =Mock()
	
	String pPriceListPropertyName
	boolean pUseDefaultPriceList
	Map pExtraParameters
	Locale pLocale
	
	def setup(){
		def Repository siteRepository = Mock()
		orderService = Spy()
		orderService.setSiteRepository(siteRepository)
	}
	
	def"getPricingCurrencyCode, when site id is not null and Site RepositoryItem is not null"(){
		
		given:
		def Repository siteRepository = Mock()
		def RepositoryItem site =Mock()
		orderService.extractSiteId() >> "tbs"
		orderService.getSiteRepository() >> siteRepository
		siteRepository.getItem(orderService.extractSiteId(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		
		when:
		String str=orderService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		str.equals("currency") == true
	}
	
	def"getPricingCurrencyCode, when site id null"(){
		given:
		
		def RepositoryItem site = Mock()
		orderService.extractSiteId() >> null
		
		when:
		String str=orderService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY)
		str.equals(BBBCheckoutConstants.SITE_CURRENCY_USD) == true
	}
	
	def"getPricingCurrencyCode, when site id not null and Site RepositoryItem is null"(){
		
		given:
		def Repository siteRepository = Mock()
		def RepositoryItem site = Mock()
		orderService.extractSiteId() >> "tbs"
		orderService.getSiteRepository() >> siteRepository
		siteRepository.getItem(orderService.extractSiteId(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> null
		
		when:
		String str=orderService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY)
		str.equals(BBBCheckoutConstants.SITE_CURRENCY_USD) == true
	}
	
	def"getPricingCurrencyCode, RepositoryException is thrown"(){
		
		given:
		def Repository siteRepository = Mock()
		def RepositoryItem site = Mock()
		orderService.extractSiteId() >> "tbs"
		orderService.getSiteRepository() >> { throw new RepositoryException("exception")}
		
		when:
		String str=orderService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY)
		str.equals(BBBCheckoutConstants.SITE_CURRENCY_USD) == true
	}

}
