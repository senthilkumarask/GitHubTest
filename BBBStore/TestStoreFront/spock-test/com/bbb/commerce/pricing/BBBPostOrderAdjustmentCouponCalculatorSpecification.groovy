package com.bbb.commerce.pricing

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map

import javax.swing.tree.DefaultMutableTreeNode.PostorderEnumeration;

import com.bbb.constants.BBBCoreConstants
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;

import atg.commerce.order.CommerceItem
import atg.commerce.order.Order;
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.FilteredCommerceItem
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.PricingContext
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor
import spock.lang.specification.BBBExtendedSpec;

class BBBPostOrderAdjustmentCouponCalculatorSpecification extends BBBExtendedSpec{
	
	def OrderPriceInfo pPriceQuote = Mock()
	def RepositoryItem pPricingModel =Mock()
	def RepositoryItem pProfile =Mock()
	def Order pOrder =Mock()
	def RepositoryItemDescriptor descriptor = Mock()
	BBBPostOrderAdjustmentCouponCalculator postOrder
	Locale pLocale


def setup(){
	postOrder = Spy()
}

def"tests if sets the coupon in the item adjustments after getting the value from the skuMap from pExtraParameters"(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	def PricingAdjustment itemAdjustments =Mock()
	def RepositoryItem promotion =Mock()
	
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	cItem.getCatalogRefId() >> "123"
	
	def RepositoryItem promotion1 =Mock()
	def PricingAdjustment adjustments =Mock()      // for last loop
	detailedInfo.getAdjustments() >> [adjustments]
	adjustments.getPricingModel() >> promotion1
	promotion1.getItemDescriptor() >>descriptor
	descriptor.getItemDescriptorName() >>"Item Discount"
	promotion1.getRepositoryId() >> "pro1Id"
	
	itemInfo.getAdjustments() >> [itemAdjustments]  // for second last loop
	itemAdjustments.getPricingModel() >> promotion
	promotion.getItemDescriptor() >>descriptor    
	descriptor.getItemDescriptorName() >>"Item Discount"
	promotion.getRepositoryId() >> "proId"
	
	
	def RepositoryItem promoKey =Mock()
	def RepositoryItem promoKey1 =Mock()
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	promomap.put(promoKey, promotion.getRepositoryId())
 	promomap.put(promoKey1, promotion1.getRepositoryId())
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",cItem.getCatalogRefId(),_)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",promotion.getRepositoryId(), _)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, promotion.getRepositoryId())
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
    1*itemAdjustments.setCoupon(promoKey)
	1*adjustments.setCoupon(promoKey1);
}

def"tests if skumap is null"(){
	
	given:
	def CommerceItem cItem =Mock()
	pOrder.getCommerceItems() >>[cItem]
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, null)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Starts");
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_,_)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
}

def"tests if skumap does not contains catalog refId of commerce Item "(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]

	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Starts");
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_,_)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
}

def"tests if promotion repository item is null "(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> null
	detailedInfo.getAdjustments() >> null
	
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
}


def"tests if promotion repositry is not null and ItemDescriptor name is not equal to item discount"(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	def RepositoryItem promotion =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> promotion
	promotion.getItemDescriptor() >>descriptor
	descriptor.getItemDescriptorName() >>"Item"
	promotion.getRepositoryId() >> "proId"
	detailedInfo.getAdjustments() >> null
	
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
}

def"tests if promotion repositry is not null  and coupon in the adjustment is not the mapping is found in the skuMap"(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	def RepositoryItem promotion =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> promotion
	promotion.getItemDescriptor() >>descriptor
	descriptor.getItemDescriptorName() >>"Item Discount"
	promotion.getRepositoryId() >> "123"
	detailedInfo.getAdjustments() >> null
	
    def RepositoryItem promoKey =Mock()
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	promomap.put(promoKey, "1234")
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	0*itemAdjustments.setCoupon(promoKey)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
}

def"tests if RepositoryException is thrown"(){ 
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	def RepositoryItem promotion =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> promotion
	promotion.getItemDescriptor() >>{throw new RepositoryException("")}
	detailedInfo.getAdjustments() >> null
	
	def RepositoryItem promoKey =Mock()
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	promomap.put(promoKey, "1234")
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	0*itemAdjustments.setCoupon(promoKey)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogError("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion {0}",_);
	Exception msg = thrown()
	msg.getMessage().equalsIgnoreCase("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion: Mock for type 'RepositoryItem' named 'promotion'") 
}

def"tests if pricing model obtained from DetailedItemPriceInfo is  null"(){
	
	given:
	
	
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> null
	
	def RepositoryItem promotion1 = Mock()
	def PricingAdjustment Adjustments =Mock()
	detailedInfo.getAdjustments() >> [Adjustments]
	Adjustments.getPricingModel() >>null
	
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	0*Adjustments.setCoupon(_)
	0*itemAdjustments.setCoupon(_)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
}

def"tests if pricing model obtained from DetailedItemPriceInfo is not null and the corresponding Item decriptor name is not Item Discount"(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> null
	
	def RepositoryItem promotion1 = Mock()
	def PricingAdjustment Adjustments =Mock()
	detailedInfo.getAdjustments() >> [Adjustments]
	Adjustments.getPricingModel() >>promotion1
	promotion1.getItemDescriptor() >> descriptor
	descriptor.getItemDescriptorName() >> "Item"
	
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	0*Adjustments.setCoupon(promotion1)
	0*itemAdjustments.setCoupon(_)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
}

def"tests if pricing model obtained from DetailedItemPriceInfo is not null  and does not sets coupon in the adjustment if the mapping is not found in the skuMap"(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> null
	
	def RepositoryItem promotion1 = Mock()
	def PricingAdjustment Adjustments =Mock()
	detailedInfo.getAdjustments() >> [Adjustments]
	Adjustments.getPricingModel() >>promotion1
	promotion1.getItemDescriptor() >> descriptor
	descriptor.getItemDescriptorName() >> "Item Discount"
	promotion1.getRepositoryId() >>"123"
	
	def RepositoryItem promoKey = Mock()
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	promomap.put(promoKey, "1234")
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder(pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	0*Adjustments.setCoupon(promotion1)
	0*itemAdjustments.setCoupon(_)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _)
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Copy promo Map : {0}",_);
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends")
}


def"tests if RepositoryException is thrown1"(){
	
	given:
	def CommerceItem cItem =Mock()
	def ItemPriceInfo itemInfo =Mock()
	def DetailedItemPriceInfo detailedInfo =Mock()
	
	pOrder.getCommerceItems() >>[cItem]
	cItem.getPriceInfo() >>itemInfo
	cItem.getCatalogRefId() >> "123"
	itemInfo.getCurrentPriceDetails() >> [detailedInfo]
	
	def PricingAdjustment itemAdjustments =Mock()
	def RepositoryItem promotion =Mock()
	itemInfo.getAdjustments() >> [itemAdjustments]
	itemAdjustments.getPricingModel() >> null
	
	def RepositoryItem promotion1 = Mock()
	def PricingAdjustment Adjustments =Mock()
	detailedInfo.getAdjustments() >> [Adjustments]
	Adjustments.getPricingModel() >>promotion1
	promotion1.getItemDescriptor() >> {throw new RepositoryException("")}
	
	LinkedHashMap<RepositoryItem, String> promomap = new HashMap()
	
	Map<String, LinkedHashMap<RepositoryItem, String>> skumap = new HashMap()
	skumap.put(cItem.getCatalogRefId(), promomap)
	
	Map<String,Object> pExtraParameters = new HashMap()
	pExtraParameters.put(BBBCoreConstants.SKU_MAP, skumap)
	
	when:
	postOrder.priceOrder( pPriceQuote, pOrder, pPricingModel,  pLocale, pProfile, pExtraParameters)
	
	then:
	0*itemAdjustments.setCoupon(_)
	0*Adjustments.setCoupon(promotion1)
	1*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",_, _);
	0*postOrder.vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",_, _);
	1*postOrder.vlogError("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion {0}",_);
	Exception msg = thrown()
	msg.getMessage().equalsIgnoreCase("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion: Mock for type 'RepositoryItem' named 'promotion1'")
}

	
}