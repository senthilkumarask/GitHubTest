package com.bbb.commerce.pricing

import java.util.List
import java.util.Locale;
import java.util.Map

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.order.bean.BBBCommerceItem;
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext
import atg.multisite.Site
import atg.multisite.SiteContext
import atg.multisite.SiteContextManager;
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;

class BBBTaxPricingEngineServiceSpecification extends BBBExtendedSpec {

	
	
	def PricingContext pricingcontext = Mock()
	def BBBCommerceItem bbbCommerceItem = Mock()
	def CommerceItem cItem =Mock()
	def RepositoryItem pProfile =Mock()
	
	BBBTaxPricingEngineService taxService
	
	String pPriceListPropertyName
	boolean pUseDefaultPriceList
	Map pExtraParameters
	Locale pLocale
	
	def setup(){
		taxService =Spy()
	}
	
	def"getPricingCurrencyCode, when site id is not null and Site RepositoryItem is not null"(){
		
		given:
		def Repository siteRepository = Mock()
		def RepositoryItem site =Mock()
		taxService.extractSiteID() >> "tbs"
		taxService.getSiteRepository() >> siteRepository
		siteRepository.getItem(taxService.extractSiteID(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		
		when:
		String str=taxService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		str.equals("currency") == true
	}
	
	def"getPricingCurrencyCode, when site id null"(){
		given:
		
		def RepositoryItem site = Mock()
		taxService.extractSiteID() >> null
		
		when:
		String str=taxService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY)
		str.equals(BBBCheckoutConstants.SITE_CURRENCY_USD) == true
	}
	
	def"getPricingCurrencyCode, when site id not null and Site RepositoryItem is null"(){
		
		given:
		def Repository siteRepository = Mock()
		def RepositoryItem site = Mock()
		taxService.extractSiteID() >> "tbs"
		taxService.getSiteRepository() >> siteRepository
		siteRepository.getItem(taxService.extractSiteID(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> null
		
		when:
		String str=taxService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY)
		str.equals(BBBCheckoutConstants.SITE_CURRENCY_USD) == true
	}
	
	def"getPricingCurrencyCode, RepositoryException is thrown"(){
		
		given:
		def Repository siteRepository = Mock()
		def RepositoryItem site = Mock()
		taxService.extractSiteID() >> "tbs"
		taxService.getSiteRepository() >> { throw new RepositoryException("exception")}
		
		when:
		String str=taxService.getPricingCurrencyCode(pProfile,pPriceListPropertyName, pUseDefaultPriceList, pExtraParameters, pLocale)
		
		then:
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY)
		str.equals(BBBCheckoutConstants.SITE_CURRENCY_USD) == true
	}
}
