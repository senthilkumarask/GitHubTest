package com.bbb.commerce.pricing

import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.DetailedItemPriceTools
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingTools
import atg.commerce.pricing.priceLists.Constants;
import atg.repository.RepositoryItem;
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.EcoFeeCommerceItem
import spock.lang.specification.BBBExtendedSpec;

class CustomCalculatorSpecification extends BBBExtendedSpec {

	CustomCalculator customCalc
	
	def RepositoryItem pPrice= Mock()
	def RepositoryItem pPricingModel =Mock()
	def RepositoryItem pProfile =Mock()
	def PricingTools pTools =Mock()
	
	Locale pLocale
	Map pExtraParameters
	
	
   def setup(){
	customCalc =Spy()
	customCalc.setPricingTools(pTools)
	
 }
   
   def"priceItem, when pItem is an instance of commerce Item, current price details list is not empty and adjustments list is not empty"(){
	   given:
	   def CommerceItem pItem =Mock()
	   def DetailedItemPriceTools priceTool =Mock()
	   def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
	   
	   List newDetail = new ArrayList()
	   newDetail.add("new")
	   
	   pTools.round(_) >> 30
	   pTools.getDetailedItemPriceTools() >>priceTool
	   pPriceQuote.getCurrentPriceDetails().add("old")
	   priceTool.createInitialDetailedItemPriceInfos(_,pPriceQuote,pItem,pPricingModel, pLocale, pProfile, pExtraParameters,Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newDetail
	   pPriceQuote.getAdjustments().add("adj")
	   pItem.getQuantity() >> 2
	 
	   when:
	   customCalc. priceItem(pPrice,pPriceQuote, pItem, pPricingModel,  pLocale, pProfile, pExtraParameters)
	   
	   then:
	   1*customCalc.logDebug("rounding item price.")
	   pPriceQuote.getCurrentPriceDetails().contains("old")== false
	   pPriceQuote.getCurrentPriceDetails().contains("new") == true  
	   pPriceQuote.getAdjustments().contains("adj")== false
	   pPriceQuote.getAdjustments().isEmpty() == false
	   pPriceQuote.getRawTotalPrice() == 30.0
	   pPriceQuote.getAmount() == 30.0
	   
   }
   
   def"priceItem, when pItem is an instance of commerce Item, current price details list is empty and adjustments list is not empty"(){
	   
	   given:
	   def CommerceItem pItem =Mock()
	   def DetailedItemPriceTools priceTool =Mock()
	   def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
	   
	   List newDetail = new ArrayList()
	   newDetail.add("new")
	   
	   pTools.getDetailedItemPriceTools() >>priceTool
	   priceTool.createInitialDetailedItemPriceInfos(_,pPriceQuote,pItem,pPricingModel, pLocale, pProfile, pExtraParameters,Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newDetail
	   pPriceQuote.getAdjustments()add("adj")
	   pItem.getQuantity() >> 2
	   pTools.round(_) >> 30
	
	   when:
	   customCalc. priceItem(pPrice,pPriceQuote, pItem, pPricingModel,  pLocale, pProfile, pExtraParameters)
	   
	   then:
	   1*customCalc.logDebug("rounding item price.")
	   pPriceQuote.getCurrentPriceDetails().contains("new") == true
	   pPriceQuote.getAdjustments().contains("adj")== false
	   pPriceQuote.getAdjustments().isEmpty() == false   
	   pPriceQuote.getRawTotalPrice() == 30.0
	   pPriceQuote.getAmount() == 30.0
   }
   
   def"priceItem, when pItem is an instance of commerce Item, current price details list is not empty and adjustments list is empty"(){
	   
	   given:
	   def CommerceItem pItem =Mock()
	   def DetailedItemPriceTools priceTool =Mock()
	   def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
	   
	   List newDetail = new ArrayList()
	   newDetail.add("new")
	   
	   pTools.getDetailedItemPriceTools() >>priceTool
	   pTools.round(_)>>30
	   pPriceQuote.getCurrentPriceDetails().add("old")
	   priceTool.createInitialDetailedItemPriceInfos(_,pPriceQuote,pItem,pPricingModel, pLocale, pProfile, pExtraParameters,Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newDetail
	   pItem.getQuantity() >> 2
	   
	   when:
	   customCalc. priceItem(pPrice,pPriceQuote, pItem, pPricingModel,  pLocale, pProfile, pExtraParameters)
	   
	   then:
	   1*customCalc.logDebug("rounding item price.")
	   pPriceQuote.getCurrentPriceDetails().contains("old")==false
	   pPriceQuote.getCurrentPriceDetails().contains("new") == true
	   pPriceQuote.getAdjustments().isEmpty() == false
	   pPriceQuote.getRawTotalPrice() == 30.0
	   pPriceQuote.getAmount() == 30.0
	   
   }
   
   def"priceItem, when pItem is not an instance of commerce Item"(){
	   
	   given:
	   def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
	   
	   when:
	   customCalc. priceItem(pPrice,pPriceQuote, null, pPricingModel,  pLocale, pProfile, pExtraParameters)
	   
	   then:
	   0*customCalc.logDebug("rounding item price.")
	   pPriceQuote.getRawTotalPrice() == 0.0
	   pPriceQuote.getAmount() == 0.0
	   
   }
   
}
