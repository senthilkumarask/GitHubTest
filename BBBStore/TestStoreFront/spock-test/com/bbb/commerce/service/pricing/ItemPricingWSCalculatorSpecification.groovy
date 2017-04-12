package com.bbb.commerce.service.pricing

import java.util.List;
import java.util.Locale;
import java.util.Map
import com.bbb.commerce.pricing.BBBPricingTools;

import com.bbb.constants.BBBCheckoutConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bedbathandbeyond.atg.Adjustment
import com.bedbathandbeyond.atg.AdjustmentList
import com.bedbathandbeyond.atg.Item;
import atg.core.util.Range;
import atg.commerce.order.CommerceItem
import atg.commerce.order.Order
import atg.commerce.order.ShippingGroupCommerceItemRelationship
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.PricingTools
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;

class ItemPricingWSCalculatorSpecification extends BBBExtendedSpec {

	def BBBPricingTools pTools = Mock()

	ItemPricingWSCalculator calc

	def setup(){
		calc = new ItemPricingWSCalculator()
		calc.setPricingTools(pTools)
	}

	def"priceItems, calls the service and populates the ItemPriceInfo object"(){
		
		given:
		RepositoryItem pPricingModel = Mock()
		RepositoryItem pProfile = Mock()
		Order pOrder =Mock()
		ShippingGroupCommerceItemRelationship rel =Mock()
		ShippingGroupCommerceItemRelationship rel1 =Mock()
		ShippingGroupCommerceItemRelationship rel2 =Mock()
		CommerceItem citem =Mock()
		CommerceItem citem1 =Mock()
		CommerceItem citem2 =Mock()
		Range range =Mock()
		Range range1 =Mock()
		Range range2 =Mock()
		Item item =Mock()
		Item item1 =Mock()
		Item item2 =Mock()
		ItemPriceInfo bbbItemPrice =  Mock()
		ItemPriceInfo bbbItemPrice1 = Mock()
		ItemPriceInfo bbbItemPrice2 = Mock()
		AdjustmentList adjList =Mock()

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		itemInfoMap.put(rel, item)
		itemInfoMap.put(rel1, item1)
		itemInfoMap.put(rel2, item2)

		rel.getCommerceItem() >> citem
		rel1.getCommerceItem() >>citem1
		rel2.getCommerceItem() >>citem2
		rel.getRange() >> range
		rel1.getRange() >> range1
		rel2.getRange() >> range2

		pItems.add(0, citem)
		pItems.add(1, citem1)
		pItems.add(2, citem2)
		pPriceQuotes.add(0,bbbItemPrice)
		pPriceQuotes.add(1,bbbItemPrice1)
		pPriceQuotes.add(2,bbbItemPrice2)

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, true)
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)

		item.getPrice() >> 100.0
		item.getQuantity() >> 2
		item.getAdjustmentList() >> adjList
		citem.getQuantity() >> 10
		item1.getPrice() >> 200.0
		citem1.getQuantity() >> 10
		item2.getPrice() >> 300.0
		citem2.getQuantity() >> 10

		2*pTools.round(item.getPrice().doubleValue()*citem.getQuantity()) >> 1000.0
		2*pTools.round(item1.getPrice().doubleValue()*citem1.getQuantity()) >> 2000.0
		2*pTools.round(item2.getPrice().doubleValue()*citem2.getQuantity()) >> 3000.0

		2*pTools.round(item.getPrice(), 2) >> 100.0
		1*pTools.round(item1.getPrice(), 2) >> 200.0
		1*pTools.round(item2.getPrice(), 2) >> 300.0

		//for private 1
		DetailedItemPriceInfo det = new DetailedItemPriceInfo()
		PricingAdjustment adj = new PricingAdjustment()
		bbbItemPrice.getCurrentPriceDetailsForRange(range)  >> [det]
		bbbItemPrice1.getCurrentPriceDetailsForRange(range1) >>[]
		bbbItemPrice2.getCurrentPriceDetailsForRange(range2) >> null
		det.setQuantity(15)
		det.getAdjustments().add(adj)
		3*pTools.round(item.getPrice()*det.getQuantity(), 2) >> 1500.0

		//for private 2
		Adjustment adjArr =Mock()
		Adjustment adjArr1 =Mock()
		Adjustment adjArr2 =Mock()
		RepositoryItem rItem =Mock()
		PricingAdjustment pAdj = Mock()

		adjList.getAdjustmentArray() >> [adjArr,adjArr1,adjArr2]

		adjArr.getAtgPromotionId() >> ""
		adjArr.getCouponCode() >> "123"
		adjArr.getPromotionType() >> "type"
		1*pTools.getPromotion("123", "type") >> [rItem]
		adjArr.getDiscountAmount() >> 50.0
		1*pTools.getPromotionAdjustment(rItem, 0.0, 2) >>pAdj
		pAdj.getTotalAdjustment() >> 500.0
		1*pTools.round(2000.0,2) >> 2000.0

		2*adjArr1.getAtgPromotionId() >> "atgId"
		1*adjArr1.getPromotionType() >> "type"
		1*pTools.getPromotion("atgId", "type") >> null

		2*adjArr2.getAtgPromotionId() >> "atgId2"
		1*adjArr2.getPromotionType() >> "type"
		1*pTools.getPromotion("atgId2", "type") >> []

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		1*bbbItemPrice.setRawTotalPrice(pTools.round(item.getPrice().doubleValue()*citem.getQuantity()))
		1*bbbItemPrice1.setRawTotalPrice(pTools.round(item1.getPrice().doubleValue()*citem1.getQuantity()))
		1*bbbItemPrice2.setRawTotalPrice(pTools.round(item2.getPrice().doubleValue()*citem2.getQuantity()))
		1*bbbItemPrice.setListPrice(pTools.round(item.getPrice(), 2))
		1*bbbItemPrice1.setListPrice(_)
		1*bbbItemPrice2.setListPrice(_)
		1*bbbItemPrice.setSalePrice(0.0)
		1*bbbItemPrice.setOnSale(false)
		1*bbbItemPrice1.setSalePrice(0.0)
		1*bbbItemPrice1.setOnSale(false)
		1*bbbItemPrice2.setSalePrice(0.0)
		1*bbbItemPrice2.setOnSale(false)
		det.getAmount() == 2000.0
		1*bbbItemPrice.setAmount(1500.0)
		det.isDiscounted() == true
		det.getAdjustments().contains(pAdj) == true
	}


	def"priceItems, when getAdjustmentList is null "(){

		given:
		def RepositoryItem pPricingModel = Mock()
		def RepositoryItem pProfile = Mock()
		def Order pOrder =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem citem =Mock()
		def Range range =Mock()
		def Item item =Mock()
		def ItemPriceInfo bbbItemPrice =  Mock()
		def AdjustmentList adjList =Mock()

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		itemInfoMap.put(rel, item)

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, true)
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)

		rel.getCommerceItem() >> citem
		rel.getRange() >> range

		pItems.add(0, citem)
		pPriceQuotes.add(0,bbbItemPrice)

		item.getPrice() >> 100.0
		item.getQuantity() >> 2
		item.getAdjustmentList() >> null
		citem.getQuantity() >> 10

		2*pTools.round(item.getPrice().doubleValue()*citem.getQuantity()) >> 1000.0
		2*pTools.round(item.getPrice(), 2) >> 100.0

		//for private 1
		def DetailedItemPriceInfo det = new DetailedItemPriceInfo()
		def PricingAdjustment adj = new PricingAdjustment()
		bbbItemPrice.getCurrentPriceDetailsForRange(range)  >> [det]
		det.setQuantity(15)
		det.getAdjustments().add(adj)
		3*pTools.round(item.getPrice()*det.getQuantity(), 2) >> 1500.0

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		1*bbbItemPrice.setRawTotalPrice(pTools.round(item.getPrice().doubleValue()*citem.getQuantity()))
		1*bbbItemPrice.setListPrice(pTools.round(item.getPrice(), 2))
		1*bbbItemPrice.setSalePrice(0.0)
		1*bbbItemPrice.setOnSale(false)
		det.getAmount() == 1500.0
		1*bbbItemPrice.setAmount(1500.0)
	}

	def"priceItem, getAdjustmentArray is null1"(){

		given:
		def RepositoryItem pPricingModel = Mock()
		def RepositoryItem pProfile = Mock()
		def Order pOrder =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem citem =Mock()
		def Range range =Mock()
		def Item item =Mock()
		def ItemPriceInfo bbbItemPrice =  Mock()
		def AdjustmentList adjList =Mock()

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		itemInfoMap.put(rel, item)

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, true)
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)

		rel.getCommerceItem() >> citem
		rel.getRange() >> range

		pItems.add(0, citem)
		pPriceQuotes.add(0,bbbItemPrice)

		item.getPrice() >> 100.0
		item.getQuantity() >> 2
		item.getAdjustmentList() >> adjList
		adjList.getAdjustmentArray()  >> null
		citem.getQuantity() >> 10

		2*pTools.round(item.getPrice().doubleValue()*citem.getQuantity()) >> 1000.0
		2*pTools.round(item.getPrice(), 2) >> 100.0

		//for private 1
		def DetailedItemPriceInfo det = new DetailedItemPriceInfo()
		def PricingAdjustment adj = new PricingAdjustment()
		bbbItemPrice.getCurrentPriceDetailsForRange(range)  >> [det]
		det.setQuantity(15)
		det.getAdjustments().add(adj)
		3*pTools.round(item.getPrice()*det.getQuantity(), 2) >> 1500.0

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		1*bbbItemPrice.setRawTotalPrice(pTools.round(item.getPrice().doubleValue()*citem.getQuantity()))
		1*bbbItemPrice.setListPrice(pTools.round(item.getPrice(), 2))
		1*bbbItemPrice.setSalePrice(0.0)
		1*bbbItemPrice.setOnSale(false)
		det.getAmount() == 1500.0
		1*bbbItemPrice.setAmount(1500.0)
	}

	def"priceItem, IS_PARTIALLY_SHIPPED flag is false"(){

		given:
		def RepositoryItem pPricingModel = Mock()
		def RepositoryItem pProfile = Mock()
		def Order pOrder =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem citem =Mock()
		def Range range =Mock()
		def Item item =Mock()
		def ItemPriceInfo bbbItemPrice =  Mock()
		def AdjustmentList adjList =Mock()

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		itemInfoMap.put(rel, item)

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, false)
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)

		rel.getCommerceItem() >> citem
		rel.getRange() >> range

		pItems.add(0, citem)
		pPriceQuotes.add(0,bbbItemPrice)

		item.getPrice() >> 100.0
		item.getQuantity() >> 2
		citem.getQuantity() >> 10

		2*pTools.round(item.getPrice().doubleValue()*citem.getQuantity()) >> 1000.0
		2*pTools.round(item.getPrice(), 2) >> 100.0

		//for private 1
		def DetailedItemPriceInfo det = new DetailedItemPriceInfo()
		def PricingAdjustment adj = new PricingAdjustment()
		bbbItemPrice.getCurrentPriceDetailsForRange(range)  >> [det]
		det.setQuantity(15)
		det.getAdjustments().add(adj)
		3*pTools.round(item.getPrice()*det.getQuantity(), 2) >> 1500.0

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		1*bbbItemPrice.setRawTotalPrice(pTools.round(item.getPrice().doubleValue()*citem.getQuantity()))
		1*bbbItemPrice.setListPrice(pTools.round(item.getPrice(), 2))
		1*bbbItemPrice.setSalePrice(0.0)
		1*bbbItemPrice.setOnSale(false)
		det.getAmount() == 1500.0
		1*bbbItemPrice.setAmount(1500.0)
	}

	def"priceItem , when ShippingGroupCommerceItemRelationship List  is null"(){

		given:
		def RepositoryItem pPricingModel = Mock()
		def RepositoryItem pProfile = Mock()
		def Order pOrder =Mock()
		def CommerceItem citem =Mock()
		def Item item =Mock()
		def ItemPriceInfo bbbItemPrice =  Mock()

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, null)

		pItems.add(0, citem)
		pPriceQuotes.add(0,bbbItemPrice)

		0*item.getQuantity() >> 2
		0*citem.getQuantity() >> 10

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		0*bbbItemPrice.setRawTotalPrice(_)
		0*bbbItemPrice.setListPrice(_)
		0*bbbItemPrice.setSalePrice(0.0)
		0*bbbItemPrice.setOnSale(false)
		0*bbbItemPrice.setAmount(_)
	}

	def"priceItem, when RepositoryException is thrown"(){

		given:
		def RepositoryItem pPricingModel = Mock()
		def RepositoryItem pProfile = Mock()
		def Order pOrder =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem citem =Mock()
		def Range range =Mock()
		def Item item =Mock()
		def ItemPriceInfo bbbItemPrice =  Mock()
		def AdjustmentList adjList =Mock()
		calc =Spy()
		calc.setPricingTools(pTools)

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		itemInfoMap.put(rel, item)

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, true)
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)

		rel.getCommerceItem() >> citem
		rel.getRange() >> range

		pItems.add(0, citem)
		pPriceQuotes.add(0,bbbItemPrice)

		item.getPrice() >> 100.0
		item.getQuantity() >> 2
		item.getAdjustmentList() >> adjList
		citem.getQuantity() >> 10

		2*pTools.round(item.getPrice().doubleValue()*citem.getQuantity()) >> 1000.0
		2*pTools.round(item.getPrice(), 2) >> 100.0

		//for private 1
		def DetailedItemPriceInfo det = new DetailedItemPriceInfo()
		def PricingAdjustment adj = new PricingAdjustment()
		bbbItemPrice.getCurrentPriceDetailsForRange(range)  >> [det]
		det.setQuantity(15)
		det.getAdjustments().add(adj)
		3*pTools.round(item.getPrice()*det.getQuantity(), 2) >> 1500.0

		//for private 2
		def Adjustment adjArr =Mock()
		def Adjustment adjArr1 =Mock()
		def Adjustment adjArr2 =Mock()
		def RepositoryItem rItem =Mock()
		PricingAdjustment pAdj = Mock()

		adjList.getAdjustmentArray()  >> [adjArr]
		adjArr.getAtgPromotionId() >> ""
		adjArr.getCouponCode() >> "123"
		adjArr.getPromotionType() >> "type"
		pTools.getPromotion("123", "type") >> {throw new RepositoryException("")}

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		1*bbbItemPrice.setRawTotalPrice(pTools.round(item.getPrice().doubleValue()*citem.getQuantity()))
		1*bbbItemPrice.setListPrice(pTools.round(item.getPrice(), 2))
		1*bbbItemPrice.setSalePrice(0.0)
		1*bbbItemPrice.setOnSale(false)
		det.getAmount() == 1500.0
		1*bbbItemPrice.setAmount(1500.0)
		1*calc.logError("Pricing exception while performing pricing operation - ItemPricingWSCalculator", _)
	}

	def"priceItem, when BBBSystemException is thrown"(){

		given:
		def RepositoryItem pPricingModel = Mock()
		def RepositoryItem pProfile = Mock()
		def Order pOrder =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem citem =Mock()
		def Range range =Mock()
		def Item item =Mock()
		def ItemPriceInfo bbbItemPrice =  Mock()
		def AdjustmentList adjList =Mock()
		calc =Spy()
		calc.setPricingTools(pTools)

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		itemInfoMap.put(rel, item)

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, true)
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)

		rel.getCommerceItem() >> citem
		rel.getRange() >> range

		pItems.add(0, citem)
		pPriceQuotes.add(0,bbbItemPrice)

		item.getPrice() >> 100.0
		item.getQuantity() >> 2
		item.getAdjustmentList() >> adjList
		citem.getQuantity() >> 10

		2*pTools.round(item.getPrice().doubleValue()*citem.getQuantity()) >> 1000.0
		2*pTools.round(item.getPrice(), 2) >> 100.0

		//for private 1
		def DetailedItemPriceInfo det = new DetailedItemPriceInfo()
		def PricingAdjustment adj = new PricingAdjustment()
		bbbItemPrice.getCurrentPriceDetailsForRange(range)  >> [det]
		det.setQuantity(15)
		det.getAdjustments().add(adj)
		3*pTools.round(item.getPrice()*det.getQuantity(), 2) >> 1500.0

		//for private 2
		def Adjustment adjArr =Mock()
		def Adjustment adjArr1 =Mock()
		def Adjustment adjArr2 =Mock()
		def RepositoryItem rItem =Mock()
		PricingAdjustment pAdj = Mock()

		adjList.getAdjustmentArray()  >> [adjArr]
		adjArr.getAtgPromotionId() >> ""
		adjArr.getCouponCode() >> "123"
		adjArr.getPromotionType() >> "type"
		pTools.getPromotion("123", "type") >> {throw new BBBSystemException("")}

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		1*bbbItemPrice.setRawTotalPrice(pTools.round(item.getPrice().doubleValue()*citem.getQuantity()))
		1*bbbItemPrice.setListPrice(pTools.round(item.getPrice(), 2))
		1*bbbItemPrice.setSalePrice(0.0)
		1*bbbItemPrice.setOnSale(false)
		det.getAmount() == 1500.0
		1*bbbItemPrice.setAmount(1500.0)
		1*calc.logError("BBBSystemException while performing pricing operation - ItemPricingWSCalculator", _)
	}

	def"priceItem, when BBBBusinessException is thrown"(){

		given:
		def RepositoryItem pPricingModel = Mock()
		def RepositoryItem pProfile = Mock()
		def Order pOrder =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem citem =Mock()
		def Range range =Mock()
		def Item item =Mock()
		def ItemPriceInfo bbbItemPrice =  Mock()
		def AdjustmentList adjList =Mock()
		calc =Spy()
		calc.setPricingTools(pTools)

		List pPriceQuotes = new ArrayList()
		List pItems =  new ArrayList()
		Locale pLocale

		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		itemInfoMap.put(rel, item)

		Map<String,Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED, true)
		pExtraParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)

		rel.getCommerceItem() >> citem
		rel.getRange() >> range

		pItems.add(0, citem)
		pPriceQuotes.add(0,bbbItemPrice)

		item.getPrice() >> 100.0
		item.getQuantity() >> 2
		item.getAdjustmentList() >> adjList
		citem.getQuantity() >> 10

		2*pTools.round(item.getPrice().doubleValue()*citem.getQuantity()) >> 1000.0
		2*pTools.round(item.getPrice(), 2) >> 100.0

		//for private 1
		def DetailedItemPriceInfo det = new DetailedItemPriceInfo()
		def PricingAdjustment adj = new PricingAdjustment()
		bbbItemPrice.getCurrentPriceDetailsForRange(range)  >> [det]
		det.setQuantity(15)
		det.getAdjustments().add(adj)
		3*pTools.round(item.getPrice()*det.getQuantity(), 2) >> 1500.0

		//for private 2
		def Adjustment adjArr =Mock()
		def Adjustment adjArr1 =Mock()
		def Adjustment adjArr2 =Mock()
		def RepositoryItem rItem =Mock()
		PricingAdjustment pAdj = Mock()

		adjList.getAdjustmentArray()  >> [adjArr]
		adjArr.getAtgPromotionId() >> ""
		adjArr.getCouponCode() >> "123"
		adjArr.getPromotionType() >> "type"
		1*pTools.getPromotion("123", "type") >> {throw new BBBBusinessException("")}

		when:
		calc.priceItems(pPriceQuotes,pItems,pPricingModel, pLocale, pProfile,pOrder,pExtraParameters)

		then:
		1*bbbItemPrice.setRawTotalPrice(pTools.round(item.getPrice().doubleValue()*citem.getQuantity()))
		1*bbbItemPrice.setListPrice(pTools.round(item.getPrice(), 2))
		1*bbbItemPrice.setSalePrice(0.0)
		1*bbbItemPrice.setOnSale(false)
		det.getAmount() == 1500.0
		1*bbbItemPrice.setAmount(1500.0)
		1*calc.logError("BBBBusinessException while performing pricing operation - ItemPricingWSCalculator", _)
	}



}