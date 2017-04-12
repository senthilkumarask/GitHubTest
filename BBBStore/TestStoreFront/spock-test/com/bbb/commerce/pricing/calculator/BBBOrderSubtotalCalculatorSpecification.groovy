package com.bbb.commerce.pricing.calculator

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.order.CommerceItem
import atg.commerce.order.Order
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.PricingTools
import atg.commerce.pricing.TaxPriceInfo
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryItem

import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBOrderPriceInfo
import com.bbb.order.bean.EcoFeeCommerceItem
import com.bbb.order.bean.GiftWrapCommerceItem
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem


class BBBOrderSubtotalCalculatorSpecification extends BBBExtendedSpec {

	def BBBOrderSubtotalCalculator orderCalculator
	def BBBCommerceItem bbbCommerceItems = Mock()
	def Order pOrder =Mock()
	def RepositoryItem pPricingModel = Mock()
	def RepositoryItem pProfile = Mock()
	def TaxPriceInfo taxInfo=Mock()
	def CommerceItem cItem =Mock()
	def ItemPriceInfo priceInfo =Mock()
	def EcoFeeCommerceItem ecoFeeItems =Mock()
	def GiftWrapCommerceItem giftWrapItem = Mock()
	def LTLDeliveryChargeCommerceItem ltlItem =Mock()
	def LTLAssemblyFeeCommerceItem ltlAssembelyItem = Mock()
	def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
	
	
	
	Locale locale 
	Map pMap
	
	
	
	
	def setup(){
		orderCalculator = Spy(BBBOrderSubtotalCalculator)
		orderCalculator.setPricingTools(new BBBPricingTools())
	}
	

	
	def "priceOrder, tests if restore adjustments flag is true"(){
		
		given:
		def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
		pPriceQuote.setAmount(300)
		pOrder.getCommerceItems() >> [cItem]
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pPriceQuote.setRestoreAdjustments(true)  
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap) 
		
		then:
		pPriceQuote.isRestoreAdjustments() == false;
		pPriceQuote.getOnlineSubtotal() == 300
		
	}
	
	def "priceOrder, tests if restore adjustments flag is false "(){
		
		given:
		def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
		pOrder.getCommerceItems() >> [cItem]
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pPriceQuote.setRestoreAdjustments(false)
		pPriceQuote.setAmount(300)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.isRestoreAdjustments()== true
		pPriceQuote.getOnlineSubtotal() == 300
		
	}
	
	def "priceOrder, tests if restore adjustments flag is true and getTaxPriceInfo recieves non null value"(){
		given:
		def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
		pPriceQuote.setAmount(300)
		pOrder.getCommerceItems() >> [cItem]
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >>{}
		pPriceQuote.setRestoreAdjustments(true) 
		pOrder.getTaxPriceInfo() >> taxInfo
		taxInfo.getAmount()>>200
		
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.isRestoreAdjustments()== false
		pPriceQuote.getOnlineSubtotal() == 300
		pPriceQuote.getTax() == 200
		
	}
	
	def "priceOrder, tests if restore adjustments flag is true and getTaxPriceInfo recieves null value"(){
		given:
		def BBBOrderPriceInfo pPriceQuote =   new BBBOrderPriceInfo()
		pPriceQuote.setAmount(300)
		pPriceQuote.getAdjustments() >> ["205"]
		pOrder.getCommerceItems() >> [cItem]
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap)  >>{}
		pPriceQuote.setRestoreAdjustments(true) 
		pOrder.getTaxPriceInfo() >> null
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.isRestoreAdjustments()== false
		pPriceQuote.getOnlineSubtotal() == 300
		pPriceQuote.getTax() >> 0
	}
	
	def "priceOrder, tests if item is an instance of BBBCommerceItems and storeID recieves a non null value"(){
		
		given:
		bbbCommerceItems.getStoreId()>>"BBBStore"
		bbbCommerceItems.getPriceInfo()>>priceInfo
		pPriceQuote.setAmount(500)
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [bbbCommerceItems]
		priceInfo.getAmount() >> 200
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getStoreSubtotal() == 200
		pPriceQuote.getOnlineSubtotal() == 300
	}
	
	def "priceOrder, tests if item is an instance of BBBCommerceItems and storeID recieves a empty value"(){
		
		given:
	    orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [bbbCommerceItems]
		bbbCommerceItems.getStoreId() >> ""
		bbbCommerceItems.getPriceInfo() >> priceInfo
		pPriceQuote.setAmount(500)
		priceInfo.getAmount() >> 200
			
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getStoreSubtotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	
	def "priceOrder, tests if item is not an instance of BBBCommerceItems and storeID recieves a non null value"(){
		
		given:
		def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [cItem]
		bbbCommerceItems.getStoreId() >> "BBBStore"
		bbbCommerceItems.getPriceInfo() >> priceInfo
		pPriceQuote.setAmount(500)
		priceInfo.getAmount() >> 200
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getStoreSubtotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is an instance of BBBCommerceItems and storeID recieves a null value"(){
		
		given:
		def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [bbbCommerceItems]
		bbbCommerceItems.getStoreId() >> null
		bbbCommerceItems.getPriceInfo() >> priceInfo
		pPriceQuote.setAmount(500)
		priceInfo.getAmount() >> 200
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getStoreSubtotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is an instance of ecoFeeItems and getTaxPriceInfo recieves a non null value"(){ //check
		given:
		ecoFeeItems.getPriceInfo() >> priceInfo
		pPriceQuote.setAmount(500)
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [ecoFeeItems]
		priceInfo.getAmount() >> 200
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getEcoFee() == 200
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is an instance of ecoFeeItems and getTaxPriceInfo recieves a null value"(){
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [ecoFeeItems]
		ecoFeeItems.getPriceInfo() >> null
		pPriceQuote.setAmount(500)
	
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getEcoFee() == 0
		0*ecoFeeItems.getPriceInfo().getAmount()
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is not an instance of ecoFeeItems and getTaxPriceInfo recieves a non null value"(){
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [cItem]
		cItem.getPriceInfo() >> priceInfo
		pPriceQuote.setAmount(500)
		priceInfo.getAmount() >> 200
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getEcoFee() == 0
		0*ecoFeeItems.getPriceInfo().getAmount()
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is an instance of giftWrapItem and getTaxPriceInfo recieves a non null value"(){
		given:
		def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [giftWrapItem]
		giftWrapItem.getPriceInfo() >> priceInfo
		priceInfo.getAmount() >> 200
		pPriceQuote.setAmount(500)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getGiftWrapSubtotal() == 200
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is an instance of giftWrapItem and getTaxPriceInfo recieves a null value"(){
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [giftWrapItem]
		giftWrapItem.getPriceInfo() >> null
		pPriceQuote.setAmount(500)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		0*giftWrapItem.getPriceInfo().getAmount()
		pPriceQuote.getGiftWrapSubtotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	
	def "priceOrder, tests if item is not an instance of giftWrapItem and getTaxPriceInfo recieves a non null value"(){
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [cItem]
		cItem.getPriceInfo() >> priceInfo
		pPriceQuote.setAmount(500)
		priceInfo.getAmount() >> 200
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getOnlineSubtotal() == 500
		0*giftWrapItem.getPriceInfo().getAmount()
		pPriceQuote.getGiftWrapSubtotal() == 0
	}
	
	def "priceOrder, tests if item is an instance of ltlItem and getTaxPriceInfo recieves a non null value"(){
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [ltlItem]
		ltlItem.getPriceInfo() >> priceInfo
		pPriceQuote.setAmount(500)
		priceInfo.getAmount() >> 200
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		pPriceQuote.getOnlineSubtotal() == 500
		pPriceQuote.getDeliverySurchargeTotal() == 200
	
	}
	
	def "priceOrder, tests if item is an instance of ltlItem and getTaxPriceInfo recieves a null value"(){
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [ltlItem]
		ltlItem.getPriceInfo() >> null
		pPriceQuote.setAmount(500)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		0*ltlItem.getPriceInfo().getAmount()
		pPriceQuote.getDeliverySurchargeTotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is not an instance of ltlItem and getTaxPriceInfo recieves a non null value"(){
		given:
		
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [cItem]
		cItem.getPriceInfo() >> priceInfo
		priceInfo.getAmount() >> 200
		pPriceQuote.setAmount(500)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		0*ltlItem.getPriceInfo().getAmount()
		pPriceQuote.getDeliverySurchargeTotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is an instance of ltlAssembelyItem and getTaxPriceInfo recieves a non null value"(){ //check
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [ltlAssembelyItem]
		ltlAssembelyItem.getPriceInfo() >> priceInfo
		priceInfo.getAmount()>>200
		pPriceQuote.setAmount(500)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
	    pPriceQuote.getAssemblyFeeTotal() == 200
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder, tests if item is an instance of ltlAssembelyItem and getTaxPriceInfo recieves a null value"(){
		given:
		
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [ltlAssembelyItem]
		ltlAssembelyItem.getPriceInfo() >>null
		pPriceQuote.setAmount(500)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		0*ltlAssembelyItem.getPriceInfo().getAmount()
		pPriceQuote.getAssemblyFeeTotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}
	
	def "priceOrder,tests if item is not an instance of ltlAssembelyItem and getTaxPriceInfo recieves a non null value"(){
		given:
		orderCalculator.superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, locale, pProfile, pMap) >> {}
		pOrder.getCommerceItems() >> [cItem]
		cItem.getPriceInfo() >> priceInfo
		priceInfo.getAmount() >> 200
		pPriceQuote.setAmount(500)
		
		when:
		orderCalculator.priceOrder(pPriceQuote,pOrder,pPricingModel,locale,pProfile,pMap)
		
		then:
		0*ltlAssembelyItem.getPriceInfo().getAmount()
		pPriceQuote.getAssemblyFeeTotal() == 0
		pPriceQuote.getOnlineSubtotal() == 500
	}

	
	
}
