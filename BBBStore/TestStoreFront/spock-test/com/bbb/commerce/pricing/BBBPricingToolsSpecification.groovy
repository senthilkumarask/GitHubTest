package com.bbb.commerce.pricing


import atg.commerce.claimable.ClaimableTools
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.GiftlistTools
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroupCommerceItemRelationship
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.ShippingPricingEngine
import atg.commerce.pricing.TaxPriceInfo
import atg.commerce.pricing.UnitPriceBean;
import atg.commerce.promotion.PromotionException
import atg.commerce.states.OrderStates
import atg.core.util.Address
import atg.core.util.Range
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement
import atg.servlet.DynamoHttpServletRequest;
import atg.commerce.order.ShippingGroupImpl

import java.util.ArrayList;
import java.util.List
import java.util.Map;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import atg.commerce.order.ShippingGroup;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.RegionVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.pricing.bean.PriceInfoVO
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.validation.BBBValidationRules
import com.bbb.internationalshipping.vo.BBBInternationalCurrencyDetailsVO
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBOrderPriceInfo
import com.bbb.order.bean.BBBShippingPriceInfo
import com.bbb.order.bean.EcoFeeCommerceItem
import com.bbb.order.bean.GiftWrapCommerceItem
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem
import com.bbb.order.bean.NonMerchandiseCommerceItem
import com.bbb.profile.session.BBBSavedItemsSessionBean
import com.bbb.profile.session.BBBSessionBean
import com.bbb.rest.output.BBBCustomTagComponent
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO

import spock.lang.specification.BBBExtendedSpec;

class BBBPricingToolsSpecification extends BBBExtendedSpec {

	BBBPricingTools pTools

	def  setup(){
		pTools =Spy()
	}

	def"calculateShippingCost,loops through each commerce item in the shipping  group and sums the shippingGroupSubtotal"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 =Mock()
		def CommerceItem c =Mock()
		def CommerceItem c1 =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		rel1.getCommerceItem() >>c1
		c.getCatalogRefId() >>"ref"
		c1.getCatalogRefId() >>"ref1"
		String siteId ="tbs"
		String shippingMethod ="SDD"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel,rel1]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		add.getAddress1() >> "address1"
		add.getPostalCode() >> "123"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isGiftCardItem(siteId,c1.getCatalogRefId()) >> false

		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false
		1*toolImpl.isFreeShipping(siteId,c1.getCatalogRefId(), shippingMethod) >>false

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 500.0
		pTools.getShipItemRelPriceTotal(rel1,"amount") >> 600.0
		pTools.mexicoOrderShippingCalculation(1100.0) >> 1500.0

		hardgoodShippingGroup.getShippingMethod() >>"express"
		1*toolImpl.getRegionDataFromZip("123")>>regionVo
		2*regionVo.getRegionId() >> "Id"
		1*toolImpl.getShippingFee(siteId,shippingMethod, add.getState(),pTools.mexicoOrderShippingCalculation(1100.0), regionVo.getRegionId()) >> 1000.0


		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		0*pTools.logDebug("GiftCart Item Found")
		2*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 1000.0
	}

	def"calculateShippingCost,when region id is null"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)
		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="SDD"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		add.getAddress1() >> "address1"
		add.getPostalCode() >> "123"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false
		pTools.getShipItemRelPriceTotal(rel,"amount") >> 500.0
		pTools.mexicoOrderShippingCalculation(500.0) >> 800.0

		hardgoodShippingGroup.getShippingMethod() >>"express"
		1*toolImpl.getRegionDataFromZip("123")>>null
		1*toolImpl.getShippingFee(siteId,shippingMethod, add.getState(),pTools.mexicoOrderShippingCalculation(500.0), null) >> 800.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 800.0
	}

	def"calculateShippingCost,when postal code is empty"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="SDD"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		add.getAddress1() >> "address1"
		add.getPostalCode() >> ""

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 500.0
		pTools.mexicoOrderShippingCalculation(500.0) >> 800.0
		hardgoodShippingGroup.getShippingMethod() >>"express"
		1*toolImpl.getShippingFee(siteId,shippingMethod, add.getState(),pTools.mexicoOrderShippingCalculation(500.0), null) >> 800.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 800.0
	}

	def"calculateShippingCost,when shipping method is empty"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)
		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod =""

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		add.getAddress1() >> "address1"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 500.0
		pTools.mexicoOrderShippingCalculation(500.0) >> 800.0

		hardgoodShippingGroup.getShippingMethod() >>"express"
		1*toolImpl.getShippingFee(siteId,shippingMethod, add.getState(),pTools.mexicoOrderShippingCalculation(500.0), null) >> 800.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 800.0
	}

	def"calculateShippingCost,when shipping method is not equal to SDD"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		add.getAddress1() >> "address1"
		0*add.getPostalCode() >> ""

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 500.0
		pTools.mexicoOrderShippingCalculation(500.0) >> 800.0
		hardgoodShippingGroup.getShippingMethod() >>"express"
		1*toolImpl.getShippingFee(siteId,shippingMethod, add.getState(),pTools.mexicoOrderShippingCalculation(500.0), null) >> 800.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 800.0
		0*toolImpl.getRegionDataFromZip(_)
	}

	def"calculateShippingCost,when shippingGroupSubtotal is not set"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 0.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
		0*pTools.mexicoOrderShippingCalculation(_)
		0*toolImpl.getRegionDataFromZip(_)
		0*add.getPostalCode()
	}


	def"calculateShippingCost,when BBBSystemException is thrown in isGiftCardItem method"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()

		pTools.setLoggingDebug(true)
		pTools.setLoggingError(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> {throw new BBBSystemException("")}

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		PricingException exc = thrown()
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"calculateShippingCost,when BBBSystemException is thrown while getting shipping fee"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def CommerceItem c1 =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setLoggingError(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="SDD"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		add.getAddress1() >> "address1"
		add.getPostalCode() >> "123"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 500.0
		pTools.mexicoOrderShippingCalculation(500.0) >> 1500.0

		hardgoodShippingGroup.getShippingMethod() >>"express"
		1*toolImpl.getRegionDataFromZip("123") >> {throw new BBBSystemException("")}

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logError("Error getting shipping fee", _);
		1*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
	}

	def"calculateShippingCost,when BBBBusinessException is thrown while getting shipping fee"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def CommerceItem c1 =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()

		pTools.setLoggingDebug(true)
		pTools.setLoggingError(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="SDD"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		add.getAddress1() >> "address1"
		add.getPostalCode() >> "123"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>false
		pTools.getShipItemRelPriceTotal(rel,"amount") >> 500.0
		pTools.mexicoOrderShippingCalculation(500.0) >> 1500.0

		hardgoodShippingGroup.getShippingMethod() >>"express"
		toolImpl.getRegionDataFromZip("123") >> {throw new BBBBusinessException("")}

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logError("Error getting shipping fee", _);
		1*pTools.logDebug("Item is not free shipping")
		1*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
	}

	def"calculateShippingCost,when BBBBusinessException is thrown in isGiftCardItem method"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()

		pTools.setLoggingDebug(true)
		pTools.setLoggingError(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> {throw new BBBBusinessException("")}

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		PricingException exc = thrown()
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"calculateShippingCost,when CommerceItem is a gift item"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"
		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> true
		1*toolImpl.shippingCostForGiftCard("tbs",shippingMethod) >>800.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("There are only GiftCart Items in SG")
		1*pTools.logDebug("GiftCart Item Found")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 800.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"calculateShippingCost,when BBBBusinessException is thrown while getting shippingCostForGiftCard "(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"
		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> true
		1*toolImpl.shippingCostForGiftCard("tbs",shippingMethod) >> {throw new BBBBusinessException(BBBCatalogErrorCodes.NO_GIFT_CARD_FOR_SHIPPING_ID_IN_REPOSITORY)}

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logError("Error getting shipping fee", _);
		1*pTools.logDebug("There are only GiftCart Items in SG")
		1*pTools.logDebug("GiftCart Item Found")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"calculateShippingCost,when BBBSystemException is thrown while getting shippingCostForGiftCard "(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"
		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> true
		1*toolImpl.shippingCostForGiftCard("tbs",shippingMethod) >> {throw new BBBSystemException("")}

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logError("Error getting shipping fee", _);
		1*pTools.logDebug("There are only GiftCart Items in SG")
		1*pTools.logDebug("GiftCart Item Found")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"calculateShippingCost,when CommerceItem is a NonMerchandiseCommerceItem"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def NonMerchandiseCommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"

		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.shippingCostForGiftCard("tbs",shippingMethod) >>800.0

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 0.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("There are only GiftCart Items in SG")
		0*pTools.logDebug("Item is not free shipping")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 800.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"calculateShippingCost,when CommerceItem is a not an NonMerchandiseCommerceItem is free shipping is true"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem c =Mock()
		def Address add =Mock()
		def BBBCatalogToolsImpl toolImpl =Mock()
		def RegionVO regionVo =Mock()

		pTools.setLoggingDebug(true)
		pTools.setCatalogUtil(toolImpl)

		rel.getCommerceItem() >>c
		c.getCatalogRefId() >>"ref"
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingAddress() >>add
		add.getState() >> "state"
		1*toolImpl.isGiftCardItem(siteId,c.getCatalogRefId()) >> false
		1*toolImpl.isFreeShipping(siteId,c.getCatalogRefId(), shippingMethod) >>true
		1*toolImpl.shippingCostForGiftCard("tbs",shippingMethod) >>800.0

		pTools.getShipItemRelPriceTotal(rel,"amount") >> 0.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		1*pTools.logDebug("There are only GiftCart Items in SG")
		0*pTools.logDebug("Item is not free shipping")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 800.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}


	def"when ShippingGroupCommerceItemRelationship is null"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def NonMerchandiseCommerceItem c =Mock()
		def Address add =Mock()
		pTools.setLoggingDebug(true)
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> null

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		0*pTools.logDebug("There are only GiftCart Items in SG")
		0*pTools.logDebug("Item is not free shipping")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"when ShippingGroupCommerceItemRelationship is empty"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def NonMerchandiseCommerceItem c =Mock()
		def Address add =Mock()

		pTools.setLoggingDebug(true)
		String siteId ="tbs"
		String shippingMethod ="normal"

		hardgoodShippingGroup.getCommerceItemRelationships() >> []

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,hardgoodShippingGroup)

		then:
		0*pTools.logDebug("There are only GiftCart Items in SG")
		0*pTools.logDebug("Item is not free shipping")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 0.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"when shippingGroup is null"(){

		given:
		def BBBCatalogToolsImpl imp =Mock()

		pTools.setCatalogUtil(imp)
		pTools.setLoggingDebug(true)
		String siteId ="tbs"
		String shippingMethod ="normal"
		imp.shippingCostForGiftCard(siteId,shippingMethod) >> 500.0

		when:
		double shippingAmount = pTools.calculateShippingCost(siteId,shippingMethod,null)

		then:
		1*pTools.logDebug("There are only GiftCart Items in SG")
		0*pTools.logDebug("Item is not free shipping")
		0*pTools.logDebug("Cart contain items other than GiftCard")
		shippingAmount == 500.0
		0*pTools.mexicoOrderShippingCalculation(_)
	}

	def"mexicoOrderShippingCalculation, calculates shipping charge for mexico orders.converts total amount in peso to usd and then calculates the shipping charge"(){

		given:
		def BBBCustomTagComponent tagComponent =Mock()
		def BBBSessionBean sessionBean =new BBBSessionBean()
		def BBBInternationalCurrencyDetailsVO detailVO = new BBBInternationalCurrencyDetailsVO()
		double shippingGroupSubtotal =500.0

		1*requestMock.getAttribute(BBBInternationalShippingConstants.SESSIONBEAN) >> sessionBean
		pTools.setBbbcustomcomponent(tagComponent)
		pTools.setLoggingDebug(true)
		1*tagComponent.getCurrencyDetailsVO(BBBInternationalShippingConstants.CURRENCY_MEXICO,BBBInternationalShippingConstants.MEXICO_COUNTRY) >> detailVO
		detailVO.setFxRate(200.0)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT ,BBBInternationalShippingConstants.MEXICO_COUNTRY)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, BBBInternationalShippingConstants.CURRENCY_MEXICO)

		when:
		double total =pTools.mexicoOrderShippingCalculation(shippingGroupSubtotal)

		then:
		total ==2.5
		1*pTools.logDebug("Exiting method method mexicoOrderShippingCalculation: shippingGroupSubtotal after conersion in USD ="+ "2.5");
	}

	def"mexicoOrderShippingCalculation, checks if fxRate is zero"(){

		given:
		def BBBCustomTagComponent tagComponent =Mock()
		def BBBSessionBean sessionBean =new BBBSessionBean()
		def BBBInternationalCurrencyDetailsVO detailVO = new BBBInternationalCurrencyDetailsVO()
		double shippingGroupSubtotal =500.0

		1*requestMock.getAttribute(BBBInternationalShippingConstants.SESSIONBEAN) >> sessionBean
		pTools.setBbbcustomcomponent(tagComponent)
		pTools.setLoggingDebug(true)
		1*tagComponent.getCurrencyDetailsVO(BBBInternationalShippingConstants.CURRENCY_MEXICO,BBBInternationalShippingConstants.MEXICO_COUNTRY) >> detailVO
		detailVO.setFxRate(0.0)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT ,BBBInternationalShippingConstants.MEXICO_COUNTRY)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, BBBInternationalShippingConstants.CURRENCY_MEXICO)

		when:
		double total =pTools.mexicoOrderShippingCalculation(shippingGroupSubtotal)

		then:
		total ==500.0
		1*pTools.logDebug("Exiting method method mexicoOrderShippingCalculation: shippingGroupSubtotal after conersion in USD ="+ "500.0");
	}

	def"mexicoOrderShippingCalculation, checks if country is empty"(){

		given:
		def BBBSessionBean sessionBean =new BBBSessionBean()
		def BBBInternationalCurrencyDetailsVO detailVO = new BBBInternationalCurrencyDetailsVO()
		double shippingGroupSubtotal =500.0

		1*requestMock.getAttribute(BBBInternationalShippingConstants.SESSIONBEAN) >> sessionBean
		pTools.setLoggingDebug(true)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT ,"")
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, BBBInternationalShippingConstants.CURRENCY_MEXICO)

		when:
		double total =pTools.mexicoOrderShippingCalculation(shippingGroupSubtotal)

		then:
		total ==500.0
		0*pTools.logDebug("Exiting method method mexicoOrderShippingCalculation: shippingGroupSubtotal after conersion in USD ="+ "500.0");
	}

	def"mexicoOrderShippingCalculation, checks if currency is empty"(){

		given:
		def BBBSessionBean sessionBean =new BBBSessionBean()
		def BBBInternationalCurrencyDetailsVO detailVO = new BBBInternationalCurrencyDetailsVO()
		double shippingGroupSubtotal =500.0

		1*requestMock.getAttribute(BBBInternationalShippingConstants.SESSIONBEAN) >> sessionBean
		pTools.setLoggingDebug(true)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT ,BBBInternationalShippingConstants.MEXICO_COUNTRY)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, "")

		when:
		double total =pTools.mexicoOrderShippingCalculation(shippingGroupSubtotal)

		then:
		total ==500.0
		0*pTools.logDebug("Exiting method method mexicoOrderShippingCalculation: shippingGroupSubtotal after conersion in USD ="+ "500.0");
	}

	def"mexicoOrderShippingCalculation, checks if country is not equal to mexico"(){

		given:
		def BBBSessionBean sessionBean =new BBBSessionBean()
		def BBBInternationalCurrencyDetailsVO detailVO = new BBBInternationalCurrencyDetailsVO()
		double shippingGroupSubtotal =500.0

		1*requestMock.getAttribute(BBBInternationalShippingConstants.SESSIONBEAN) >> sessionBean
		pTools.setLoggingDebug(true)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT ,"US")
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, BBBInternationalShippingConstants.CURRENCY_MEXICO)

		when:
		double total =pTools.mexicoOrderShippingCalculation(shippingGroupSubtotal)

		then:
		total ==500.0
		0*pTools.logDebug("Exiting method method mexicoOrderShippingCalculation: shippingGroupSubtotal after conersion in USD ="+ "500.0");
	}

	def"mexicoOrderShippingCalculation, checks if currency is not equal to mexican currency"(){

		given:
		def BBBSessionBean sessionBean =new BBBSessionBean()
		def BBBInternationalCurrencyDetailsVO detailVO = new BBBInternationalCurrencyDetailsVO()
		double shippingGroupSubtotal =500.0

		1*requestMock.getAttribute(BBBInternationalShippingConstants.SESSIONBEAN) >> null
		1*requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >>sessionBean
		pTools.setLoggingDebug(true)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT ,BBBInternationalShippingConstants.MEXICO_COUNTRY)
		sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, "dollar")

		when:
		double total =pTools.mexicoOrderShippingCalculation(shippingGroupSubtotal)

		then:
		total ==500.0
		0*pTools.logDebug("Exiting method method mexicoOrderShippingCalculation: shippingGroupSubtotal after conersion in USD ="+ "500.0");
	}

	def"calculateSurcharge, loops through each commerce item in the shipping group and sums  the finalSurcharge and finally sets into the  shippingGroupPriceInfo.finalSurcharge property."(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItemRelationship rel1 = Mock()
		def CommerceItem c= Mock()
		def NonMerchandiseCommerceItem c1= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"

		pTools.setCatalogUtil(impl)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel,rel1]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>c
		rel.getQuantity() >> 5
		c.getCatalogRefId() >> "c"
		rel1.getCommerceItem() >>c1

		impl.getSKUSurcharge(siteId, c.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod()) >> 500
		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"shippingSurchargeCap") >> ["1234"]
		BBBUtility.isNumericOnly(["1234"]) >>true

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==1234
	}

	def"calculateSurcharge, when commerce item is null"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItemRelationship rel1 = Mock()
		def CommerceItem c= Mock()
		def NonMerchandiseCommerceItem c1= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"

		pTools.setCatalogUtil(impl)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingMethod() >>"express"
		rel.getCommerceItem() >>null

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"shippingSurchargeCap") >>["abcd"]

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==0
		0*impl.getSKUSurcharge(siteId, c.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod())
	}

	def"calculateSurcharge, when maxSurcahrgeCapList is empty"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItem c= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"
		pTools.setCatalogUtil(impl)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>null
		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"shippingSurchargeCap") >>[]

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==0.0
		0*impl.getSKUSurcharge(siteId, c.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod())
	}

	def"calculateSurcharge, when BBBSystemException is thrown while getting maxSurcahrgeCapList"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItem c= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"
		pTools.setCatalogUtil(impl)
		pTools.setLoggingDebug(true)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>null
		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"shippingSurchargeCap") >>{throw new BBBSystemException("")}

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==0.0
		1*pTools.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : shippingSurchargeCap")
	}

	def"calculateSurcharge, when BBBBusinessException is thrown while getting maxSurcahrgeCapList"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItem c= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"
		pTools.setCatalogUtil(impl)
		pTools.setLoggingDebug(true)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>null
		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"shippingSurchargeCap") >>{throw new BBBBusinessException("")}

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==0.0
		1*pTools.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : shippingSurchargeCap")
	}

	def"calculateSurcharge, when maxSurcahrgeCapList is null"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItem c= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"
		pTools.setCatalogUtil(impl)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>null
		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"shippingSurchargeCap") >>null

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==0.0
		0*impl.getSKUSurcharge(siteId, c.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod())
	}

	def"calculateSurcharge, when final surcharge is less thean shippingSurchargeCap"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItem c= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"
		pTools.setCatalogUtil(impl)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>c
		rel.getQuantity() >> 5
		c.getCatalogRefId() >> "c"

		impl.getSKUSurcharge(siteId, c.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod()) >> 500
		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,"shippingSurchargeCap") >>["3000"]

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==2500
	}

	def"calculateSurcharge,throws BBBSystemException while getting skuSurcharge"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItemRelationship rel1 = Mock()
		def CommerceItem c= Mock()
		def NonMerchandiseCommerceItem c1= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"

		pTools.setCatalogUtil(impl)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel,rel1]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>c
		rel.getQuantity() >> 5
		c.getCatalogRefId() >> "c"
		rel1.getCommerceItem() >>c1

		impl.getSKUSurcharge(siteId, c.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod()) >> {throw new BBBSystemException("")}

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==0.0
		PricingException p =thrown()
	}

	def"calculateSurcharge,throws BBBBusinessException while getting skuSurcharge"(){

		given:
		def HardgoodShippingGroup hardgoodShippingGroup =Mock()
		def CommerceItemRelationship rel = Mock()
		def CommerceItemRelationship rel1 = Mock()
		def CommerceItem c= Mock()
		def NonMerchandiseCommerceItem c1= Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		String siteId ="tbs"

		pTools.setCatalogUtil(impl)

		hardgoodShippingGroup.getCommerceItemRelationships() >> [rel,rel1]
		hardgoodShippingGroup.getShippingMethod() >>"express"

		rel.getCommerceItem() >>c
		rel.getQuantity() >> 5
		c.getCatalogRefId() >> "c"
		rel1.getCommerceItem() >>c1

		impl.getSKUSurcharge(siteId, c.getCatalogRefId(), hardgoodShippingGroup.getShippingMethod()) >> {throw new BBBBusinessException("")}

		when:
		double value=pTools.calculateSurcharge(siteId,hardgoodShippingGroup)

		then:
		value ==0.0
		PricingException p =thrown()
	}

	def "generatePriceBeansForRelationship, when priceInfo is not null "(){

		given:
		def ShippingGroupCommerceItemRelationship pRelationship =Mock()
		def Range range= Mock()
		def CommerceItem cItem =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def DetailedItemPriceInfo det =Mock()
		def UnitPriceBean unitBean=Mock()
		pRelationship.getRange() >> range
		pRelationship.getCommerceItem() >>cItem
		cItem.getPriceInfo() >>priceInfo
		priceInfo.getCurrentPriceDetailsForRange(range) >> [det]
		pTools.generatePriceBeans(priceInfo.getCurrentPriceDetailsForRange(range)) >> [unitBean]

		when:
		List<UnitPriceBean> list =pTools.generatePriceBeansForRelationship(pRelationship)

		then:
		list.contains(unitBean) == true
	}

	def "generatePriceBeansForRelationship, when priceInfo is null "(){

		given:
		def ShippingGroupCommerceItemRelationship pRelationship =Mock()
		def Range range= Mock()
		def CommerceItem cItem =Mock()
		def DetailedItemPriceInfo det =Mock()
		def UnitPriceBean unitBean=Mock()
		def ItemPriceInfo priceInfo =Mock()
		pRelationship.getRange() >> range
		pRelationship.getCommerceItem() >>cItem
		cItem.getPriceInfo() >>null

		when:
		List<UnitPriceBean> list =pTools.generatePriceBeansForRelationship(pRelationship)

		then:
		list.isEmpty() == true
		0*priceInfo.getCurrentPriceDetailsForRange(range)
	}

	def "calculateItemSurchargeInSG, sets the item surcharge"(){

		given:
		def ShippingGroupCommerceItemRelationship pCommerceItemRelationship =Mock()
		def BBBHardGoodShippingGroup group= Mock()
		def BBBCommerceItem bbbItem =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBShippingPriceInfo info =Mock()
		def SKUDetailVO skuVO = new SKUDetailVO()

		skuVO.setShippingSurcharge(100.0)
		info.getFinalSurcharge() >> 200.0

		pTools.setCatalogUtil(impl)

		String pSiteId ="tbs"
		bbbItem.getCatalogRefId() >>"Id"
		pCommerceItemRelationship.getShippingGroup() >>group
		pCommerceItemRelationship.getCommerceItem() >> bbbItem
		group.getPriceInfo() >> info
		group.getShippingMethod() >> "express"

		Map<String,String> map =new HashMap()
		map.putAt(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS, "excludedItems")

		1*impl.getSKUDetails(pSiteId, bbbItem.getCatalogRefId(), false, true, true) >>skuVO
		1*group.getSpecialInstructions() >> map
		1*impl.isFreeShipping(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> false
		1*impl.getSKUSurcharge(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> 500.0

		when:
		double value = pTools.calculateItemSurchargeInSG(pSiteId, pCommerceItemRelationship)

		then:
		value == 500.0
	}

	def "calculateItemSurchargeInSG, when item is not in excluded items list and final surcharge is zero"(){

		given:
		def ShippingGroupCommerceItemRelationship pCommerceItemRelationship =Mock()
		def BBBHardGoodShippingGroup group= Mock()
		def BBBCommerceItem bbbItem =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBShippingPriceInfo info =Mock()
		def SKUDetailVO skuVO = new SKUDetailVO()
		skuVO.setShippingSurcharge(100.0)

		pTools.setCatalogUtil(impl)
		String pSiteId ="tbs"
		bbbItem.getCatalogRefId() >>"Id"
		pCommerceItemRelationship.getShippingGroup() >>group
		pCommerceItemRelationship.getCommerceItem() >> bbbItem
		group.getPriceInfo() >> info
		group.getShippingMethod() >> "express"

		Map<String,String> map =new HashMap()
		map.putAt(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS, "Id")

		1*impl.getSKUDetails(pSiteId, bbbItem.getCatalogRefId(), false, true, true) >>skuVO
		1*group.getSpecialInstructions() >> map
		/*1*impl.isFreeShipping(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> true*/
		1*impl.getSKUSurcharge(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> 500.0

		when:
		double value = pTools.calculateItemSurchargeInSG(pSiteId, pCommerceItemRelationship)

		then:
		value == 500.0
	}

	def "calculateItemSurchargeInSG, when item is not in excluded items list and is free shipping is false"(){

		given:
		def ShippingGroupCommerceItemRelationship pCommerceItemRelationship =Mock()
		def BBBHardGoodShippingGroup group= Mock()
		def BBBCommerceItem bbbItem =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBShippingPriceInfo info =Mock()
		def SKUDetailVO skuVO = new SKUDetailVO()
		skuVO.setShippingSurcharge(100.0)
		info.getFinalSurcharge() >> 200.0

		pTools.setCatalogUtil(impl)
		String pSiteId ="tbs"
		bbbItem.getCatalogRefId() >>"Id"
		pCommerceItemRelationship.getShippingGroup() >>group
		pCommerceItemRelationship.getCommerceItem() >> bbbItem
		group.getPriceInfo() >> info
		group.getShippingMethod() >> "express"

		Map<String,String> map =new HashMap()
		map.putAt(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS, "Id")

		1*impl.getSKUDetails(pSiteId, bbbItem.getCatalogRefId(), false, true, true) >>skuVO
		1*group.getSpecialInstructions() >> map
		1*impl.isFreeShipping(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> false
		1*impl.getSKUSurcharge(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> 500.0

		when:
		double value = pTools.calculateItemSurchargeInSG(pSiteId, pCommerceItemRelationship)

		then:
		value == 500.0
	}

	def "calculateItemSurchargeInSG, whn item is not in the excludedList and shipping surcharge is not zero"(){

		given:
		def ShippingGroupCommerceItemRelationship pCommerceItemRelationship =Mock()
		def BBBHardGoodShippingGroup group= Mock()
		def BBBCommerceItem bbbItem =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBShippingPriceInfo info =Mock()
		def SKUDetailVO skuVO = new SKUDetailVO()
		skuVO.setShippingSurcharge(100.0)
		info.getFinalSurcharge() >> 200.0

		pTools.setCatalogUtil(impl)
		String pSiteId ="tbs"
		bbbItem.getCatalogRefId() >>"Id"
		pCommerceItemRelationship.getShippingGroup() >>group
		pCommerceItemRelationship.getCommerceItem() >> bbbItem
		group.getPriceInfo() >> info
		group.getShippingMethod() >> "express"

		Map<String,String> map =new HashMap()
		map.put(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS, "excludedItems")

		1*impl.getSKUDetails(pSiteId, bbbItem.getCatalogRefId(), false, true, true) >>skuVO
		1*group.getSpecialInstructions() >> map
		1*impl.isFreeShipping(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> true
		0*impl.getSKUSurcharge(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> 500.0

		when:
		double value = pTools.calculateItemSurchargeInSG(pSiteId, pCommerceItemRelationship)

		then:
		value == 0.0
	}

	def "calculateItemSurchargeInSG, whn item is empty"(){

		given:
		def ShippingGroupCommerceItemRelationship pCommerceItemRelationship =Mock()
		def BBBHardGoodShippingGroup group= Mock()
		def BBBCommerceItem bbbItem =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBShippingPriceInfo info =Mock()
		def SKUDetailVO skuVO = new SKUDetailVO()
		info.getFinalSurcharge() >> 200.0

		pTools.setCatalogUtil(impl)
		String pSiteId ="tbs"
		bbbItem.getCatalogRefId() >>"Id"
		pCommerceItemRelationship.getShippingGroup() >>group
		pCommerceItemRelationship.getCommerceItem() >> bbbItem
		group.getPriceInfo() >> info
		group.getShippingMethod() >> "express"

		Map<String,String> map =new HashMap()
		map.put(BBBCheckoutConstants.EXCLUDED_PROMO_ITEMS, "")

		1*impl.getSKUDetails(pSiteId, bbbItem.getCatalogRefId(), false, true, true) >>skuVO
		1*group.getSpecialInstructions() >> map
		0*impl.isFreeShipping(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> true
		0*impl.getSKUSurcharge(pSiteId, bbbItem.getCatalogRefId(), group.getShippingMethod()) >> 500.0

		when:
		double value = pTools.calculateItemSurchargeInSG(pSiteId, pCommerceItemRelationship)

		then:
		value == 0.0
	}

	def "calculateItemSurchargeInSG, when shippinggroup is not an instance of BBBHardGoodShippingGroup"(){

		given:
		def ShippingGroupCommerceItemRelationship pCommerceItemRelationship =Mock()
		def HardgoodShippingGroup group= Mock()
		def BBBCommerceItem bbbItem =Mock()
		String pSiteId ="tbs"
		pCommerceItemRelationship.getShippingGroup() >>group
		pCommerceItemRelationship.getCommerceItem() >> bbbItem

		when:
		double value = pTools.calculateItemSurchargeInSG(pSiteId, pCommerceItemRelationship)

		then:
		value == 0.0
	}

	def "calculateItemSurchargeInSG, when CommerceItem is not an instanceof BBBCommerceItem"(){

		given:
		def ShippingGroupCommerceItemRelationship pCommerceItemRelationship =Mock()
		def BBBHardGoodShippingGroup group= Mock()
		def CommerceItem bbbItem =Mock()
		String pSiteId ="tbs"
		pCommerceItemRelationship.getShippingGroup() >>group
		pCommerceItemRelationship.getCommerceItem() >> bbbItem

		when:
		double value = pTools.calculateItemSurchargeInSG(pSiteId, pCommerceItemRelationship)

		then:
		value == 0.0
	}

	def"getPromotionAdjustment, get a new promotion adjustment"(){

		given:
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItemDescriptor descriptor =Mock()
		PricingAdjustment
		pPricingModel.getItemDescriptor()>>descriptor
		descriptor.getItemDescriptorName() >> "item"
		double discountAmount
		long quantityAdjusted

		when:
		PricingAdjustment price = pTools.getPromotionAdjustment(pPricingModel, discountAmount, quantityAdjusted)

		then:
		price.getAdjustmentDescription() =="item"
	}

	def"getPromotionAdjustment, throws RepositoryException"(){

		given:
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItemDescriptor descriptor =Mock()
		PricingAdjustment
		pPricingModel.getItemDescriptor()>> {throw new RepositoryException("exception thrown")}
		double discountAmount
		long quantityAdjusted

		when:
		PricingAdjustment price = pTools.getPromotionAdjustment(pPricingModel, discountAmount, quantityAdjusted)

		then:
		price.getAdjustmentDescription() ==""
	}

	def"getPromotion, if promotion is of type ATGPromotion"(){

		given:
		def BBBCatalogToolsImpl impl  =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryView view = Mock()
		def ClaimableTools claim =Mock()
		def RepositoryItem rItem =Mock()

		String identifier
		String type =  BBBCoreConstants.ATG_PROMOTION

		pTools.setCatalogUtil(impl)
		1*impl.getCatalogRepository() >> mRep
		1*impl.getClaimableTools() >> claim
		claim.getPromotionsPropertyName() >> "property"
		1*mRep.getView(claim.getPromotionPropertyName()) >> view
		1*pTools.extractDbCall(_, _, view) >> [rItem]

		when:
		RepositoryItem[] rep= pTools.getPromotion(identifier, type)

		then:
		1*pTools.vlogDebug("BBBPricingTools.getPromotion: Items returned {0} after executing query {1} with params {2} ",	_, BBBCoreConstants.GET_PROMOTION_QUERY, _)
		RepositoryItem r =rep.getAt(0)
		r == rItem
	}

	def"getPromotion, if promotion is not  of type ATGPromotion"(){

		given:
		def BBBCatalogToolsImpl impl  =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryView view = Mock()
		def ClaimableTools claim =Mock()
		def RepositoryItem rItem =Mock()

		String identifier ="identifier"
		String type =  "type"

		pTools.setCatalogUtil(impl)
		impl.getPromotions(identifier) >> [rItem]

		when:
		RepositoryItem[] rep= pTools.getPromotion(identifier, type)

		then:
		1*pTools.vlogDebug("BBBPricingTools.getPromotion: Promotions returned {0} by querying with coupon id {1}",_, identifier)
		RepositoryItem r =rep.getAt(0)
		r == rItem
	}

	def"getOrderPriceInfo, sets the orderObject"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBCommerceItem cItem =Mock()
		def EcoFeeCommerceItem cItem1 =Mock()
		def GiftWrapCommerceItem cItem2 =Mock()
		def LTLDeliveryChargeCommerceItem cItem3 =Mock()
		def LTLAssemblyFeeCommerceItem cItem4 =Mock()
		def ItemPriceInfo itemInfo=Mock()
		def ItemPriceInfo itemInfo1=Mock()
		def ItemPriceInfo itemInfo2=Mock()
		def ItemPriceInfo itemInfo3=Mock()
		def ItemPriceInfo itemInfo4=Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def	ShippingGroupImpl ship1 =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()
		def BBBShippingPriceInfo shipInfo1 =Mock()
		def TaxPriceInfo taxInfo = Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> [cItem,cItem1,cItem2,cItem3,cItem4]

		cItem.getStoreId() >>""
		cItem.getQuantity() >> 10
		cItem.getPriceInfo() >> itemInfo
		itemInfo.getAmount() >> 100.0
		itemInfo.getRawTotalPrice() >> 150.0

		cItem1.getStoreId() >> ""
		cItem1.getPriceInfo() >> itemInfo1
		itemInfo1.getAmount() >> 200.0

		cItem2.getPriceInfo() >> itemInfo2
		itemInfo2.getAmount() >> 300.0

		cItem3.getPriceInfo() >> itemInfo3
		itemInfo3.getAmount() >> 350.0
		itemInfo3.getRawTotalPrice() >> 400.0

		cItem4.getPriceInfo() >> itemInfo4
		itemInfo4.getAmount() >> 450.0
		itemInfo4.getRawTotalPrice() >> 500.0

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >> ["1234"]
		pOrder.getShippingGroups() >> [ship,ship1]

		ship.getPriceInfo() >> shipInfo
		shipInfo.getRawShipping() >> 50.0
		shipInfo.getSurcharge() >> 60.0
		shipInfo.getFinalSurcharge() >> 20.0
		shipInfo.getFinalShipping() >> 10.0
		pTools.fillAdjustments(_, ship1) >> {}

		ship1.getPriceInfo() >> shipInfo1
		shipInfo1.getRawShipping() >> 80.0
		shipInfo1.getSurcharge() >> 60.0

		pOrder.getTaxPriceInfo() >> taxInfo
		taxInfo.getAmount() >> 600.0
		taxInfo.getStateTax() >> 20.0
		taxInfo.getCountyTax() >> 30.0

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> ["256"]
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==100.0
		priceInfoVO.getOnlineEcoFeeTotal() ==200.0
		priceInfoVO.getGiftWrapTotal() == 300.0
		priceInfoVO.getRawShippingTotal() == 130.0
		priceInfoVO.getTotalSurcharge() == 120.0
		priceInfoVO.getTotalTax() == 600.0
		priceInfoVO.getShippingStateLevelTax() == 20.0
		priceInfoVO.getShippingCountyLevelTax() == 30.0
		priceInfoVO.getOnlineTotal() ==2210
		priceInfoVO.getOrderPreTaxAmout() ==1610
		priceInfoVO.getTotalAmount() ==2210
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==10
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0
		priceInfoVO.getTotalSavedAmount() == 100.0
		priceInfoVO.getSurchargeSavings() ==40.0
		priceInfoVO.getShippingSavings() ==40.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==true
		priceInfoVO.getMaxDeliverySurcharge() ==256
		priceInfoVO.getTotalDeliverySurcharge() == 400.0
		priceInfoVO.getTotalAssemblyFee() ==500.0

	}

	def"getOrderPriceInfo,when shippingprice info is null"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBCommerceItem cItem =Mock()
		def EcoFeeCommerceItem cItem1 =Mock()
		def CommerceItem cItem2 =Mock()
		def ItemPriceInfo itemInfo=Mock()
		def ItemPriceInfo itemInfo1=Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> [cItem,cItem1,cItem2]

		cItem.getStoreId() >>"storeId"
		cItem.getQuantity() >> 10
		cItem.getPriceInfo() >> itemInfo
		itemInfo.getAmount() >> 100.0
		itemInfo.getRawTotalPrice() >> 150.0

		cItem1.getStoreId() >> "Store"
		cItem1.getPriceInfo() >> itemInfo1
		itemInfo1.getAmount() >> 200.0

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >> ["abcd"]
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> ["0"]
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 100.0
		priceInfoVO.getStoreEcoFeeTotal() ==200.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==300.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==10.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
	}

	def"getOrderPriceInfo, when store amount and eco ee total is not set"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBCommerceItem cItem =Mock()
		def EcoFeeCommerceItem cItem1 =null
		def ItemPriceInfo itemInfo=Mock()
		def ItemPriceInfo itemInfo1=Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> [cItem,cItem1]

		cItem.getStoreId() >>""
		cItem.getQuantity() >> 10
		cItem.getPriceInfo() >> itemInfo
		itemInfo.getAmount() >> 100.0
		itemInfo.getRawTotalPrice() >> 150.0

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >> null
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> ["0"]
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==100.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==100.0
		priceInfoVO.getOrderPreTaxAmout() ==100.0
		priceInfoVO.getTotalAmount() ==100.0
		priceInfoVO.getFreeShipping() == true
		priceInfoVO.getHardgoodShippingGroupItemCount() ==10.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 50.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
	}

	def"getOrderPriceInfo, when commerce item is null"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBCommerceItem cItem =Mock()
		def EcoFeeCommerceItem cItem1 =null
		def ItemPriceInfo itemInfo=Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> [cItem,cItem1]

		cItem.getStoreId() >>""
		cItem.getQuantity() >> 10
		cItem.getPriceInfo() >> itemInfo
		itemInfo.getAmount() >> 100.0
		itemInfo.getRawTotalPrice() >> 150.0

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >> []
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> ["0"]
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==100.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==100.0
		priceInfoVO.getOrderPreTaxAmout() ==100.0
		priceInfoVO.getTotalAmount() ==100.0
		priceInfoVO.getFreeShipping() == true
		priceInfoVO.getHardgoodShippingGroupItemCount() ==10.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 50.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
	}

	def"getOrderPriceInfo,when list of commerce items is null"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> null >>[]

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >> []
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> ["0"]
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==0.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
	}

	def"getOrderPriceInfo,when BBBSystemException is thrown while obtaining maxSurcahrgeCapList"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> null >>[]

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >>{throw new BBBSystemException("") }
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> ["0"]
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==0.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
		1*pTools.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : shippingSurchargeCap")
	}

	def"getOrderPriceInfo,when BBBBusinessException is thrown while obtaining maxSurcahrgeCapList"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> null >>[]

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >>{throw new BBBBusinessException("") }
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> ["0"]
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==0.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
		1*pTools.logDebug("BBBPricingTools.getOrderPriceInfo :: not able to get config key value for : shippingSurchargeCap")
	}

	def"getOrderPriceInfo,when order is null"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(null)

		then:
		priceInfoVO.getOrderPriceInfo() == null
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==0.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
	}

	def"getOrderPriceInfo, when OrderPriceInfo is null"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()

		pOrder.getPriceInfo() >> null

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == null
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==0.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
	}

	def"getOrderPriceInfo,when BBBSystemException is thrown while obtaining thresholdAmount"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> null >>[]

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >> []
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> {throw new BBBSystemException("") }
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==0.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
		1*pTools.logError("Error getting thresholdAmount from config keys", _);
	}

	def"getOrderPriceInfo,when BBBBusinessException is thrown while obtaining thresholdAmount"(){

		given:
		def OrderImpl pOrder =Mock()
		def BBBOrderPriceInfo orderInfo =Mock()
		def BBBCatalogToolsImpl impl  =Mock()
		def BBBHardGoodShippingGroup ship =Mock()
		def BBBShippingPriceInfo shipInfo =Mock()

		pTools.setCatalogUtil(impl)

		pOrder.getPriceInfo() >> orderInfo
		pOrder.getCommerceItems() >> null >>[]

		impl.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "shippingSurchargeCap") >> []
		pOrder.getShippingGroups() >> [ship]

		ship.getPriceInfo() >> null
		pOrder.getTaxPriceInfo() >> null

		impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, "threshold_delivery_amount") >> {throw new BBBBusinessException("") }
		pTools.fillItemAdjustments(_,_) >>{}

		when:
		PriceInfoVO priceInfoVO =pTools.getOrderPriceInfo(pOrder)

		then:
		priceInfoVO.getOrderPriceInfo() == orderInfo
		priceInfoVO.getStoreAmount() == 0.0
		priceInfoVO.getStoreEcoFeeTotal() ==0.0
		priceInfoVO.getOnlinePurchaseTotal() ==0.0
		priceInfoVO.getOnlineEcoFeeTotal() ==0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalSurcharge() == 0.0
		priceInfoVO.getTotalTax() == 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax() == 0.0
		priceInfoVO.getOnlineTotal() ==0.0
		priceInfoVO.getOrderPreTaxAmout() ==0.0
		priceInfoVO.getTotalAmount() ==0.0
		priceInfoVO.getFreeShipping() == false
		priceInfoVO.getHardgoodShippingGroupItemCount() ==0.0
		priceInfoVO.getStorePickupShippingGroupItemCount() ==0.0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getSurchargeSavings() ==0.0
		priceInfoVO.getShippingSavings() ==0.0
		priceInfoVO.isMaxDeliverySurchargeReached() ==false
		priceInfoVO.getMaxDeliverySurcharge() ==0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getTotalAssemblyFee() ==0.0
		1*pTools.logError("Error getting thresholdAmount from config keys", _)
	}

	def"fillAdjustments, populating the PriceInfoVO adjustments"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def PricingAdjustment adj =Mock()
		def RepositoryItem rep =Mock()

		Map<RepositoryItem, Double> shippingAdjustments = new HashMap()
		shippingAdjustments.put(rep, null)

		shippingGroup.getPriceInfo() >> priceInfo
		priceInfo.isDiscounted() >> true
		priceInfo.getAdjustments() >> [adj]
		priceInfoVO.setShippingAdjustments(shippingAdjustments)

		adj.getPricingModel() >>rep
		adj.getTotalAdjustment() >> 200.0

		when:
		pTools.fillAdjustments(priceInfoVO,shippingGroup)

		then:
		priceInfoVO.getShippingAdjustments() == shippingAdjustments
		shippingAdjustments.get(rep) == 200.0
	}

	def"fillAdjustments, when shipping adjustment value is not null"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def PricingAdjustment adj =Mock()
		def RepositoryItem rep =Mock()

		Map<RepositoryItem, Double> shippingAdjustments = new HashMap()
		shippingAdjustments.put(rep, 200.0.doubleValue())

		shippingGroup.getPriceInfo() >> priceInfo
		priceInfo.isDiscounted() >> true
		priceInfo.getAdjustments() >> [adj]
		priceInfoVO.setShippingAdjustments(shippingAdjustments)

		adj.getPricingModel() >>rep
		adj.getTotalAdjustment() >> 200.0

		when:
		pTools.fillAdjustments(priceInfoVO,shippingGroup)

		then:
		priceInfoVO.getShippingAdjustments() == shippingAdjustments
		shippingAdjustments.get(rep) == 400.0
	}

	def"fillAdjustments, when shipping adjustment Map is empty"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def PricingAdjustment adj =Mock()
		Map<RepositoryItem, Double> shippingAdjustments = new HashMap()

		1*shippingGroup.getPriceInfo() >> priceInfo
		1*priceInfo.isDiscounted() >> true
		priceInfoVO.setShippingAdjustments(shippingAdjustments)
		1*priceInfo.getAdjustments() >> [adj]
		adj.getPricingModel() >>null

		when:
		pTools.fillAdjustments(priceInfoVO,shippingGroup)

		then:
		priceInfoVO.getShippingAdjustments() == shippingAdjustments
		shippingAdjustments.isEmpty() == true
	}

	def"fillAdjustments, when PriceInfo is null"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()

		Map<RepositoryItem, Double> shippingAdjustments = new HashMap()

		shippingGroup.getPriceInfo() >> null
		priceInfoVO.setShippingAdjustments(shippingAdjustments)

		when:
		pTools.fillAdjustments(priceInfoVO,shippingGroup)

		then:
		priceInfoVO.getShippingAdjustments() == shippingAdjustments
		shippingAdjustments.isEmpty() == true
	}

	def"fillAdjustments, when item is not discounted"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()

		Map<RepositoryItem, Double> shippingAdjustments = new HashMap()

		shippingGroup.getPriceInfo() >> priceInfo
		priceInfo.isDiscounted() >> false
		priceInfoVO.setShippingAdjustments(shippingAdjustments)

		when:
		pTools.fillAdjustments(priceInfoVO,shippingGroup)

		then:
		priceInfoVO.getShippingAdjustments() == shippingAdjustments
		shippingAdjustments.isEmpty() == true
	}

	def"fillItemAdjustments, iterates through the PricingAdjustments of items and populate the PriceInfoVO adjustments"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def CommerceItem pItem =Mock()
		def DetailedItemPriceInfo price =Mock()
		def PricingAdjustment adj =Mock()
		def RepositoryItem rep =Mock()

		Map<RepositoryItem, Double> itemAdjustments = new HashMap()
		itemAdjustments.put(rep, null)

		pItem.getPriceInfo() >> priceInfo
		priceInfo.getCurrentPriceDetails() >> [price]
		price.getAdjustments() >> [adj]
		adj.getPricingModel() >> rep
		adj.getTotalAdjustment() >> 200.0

		priceInfoVO.setItemAdjustments(itemAdjustments)

		when:
		pTools.fillItemAdjustments(priceInfoVO,pItem)

		then:
		priceInfoVO.getItemAdjustments() == itemAdjustments
		itemAdjustments.get(rep) ==200.0
	}

	def"fillItemAdjustments, when item adjustment value is not null"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def CommerceItem pItem =Mock()
		def DetailedItemPriceInfo price =Mock()
		def PricingAdjustment adj =Mock()
		def RepositoryItem rep =Mock()

		Map<RepositoryItem, Double> itemAdjustments = new HashMap()
		itemAdjustments.put(rep, 200.0.doubleValue())

		pItem.getPriceInfo() >> priceInfo
		priceInfo.getCurrentPriceDetails() >> [price]
		price.getAdjustments() >> [adj]
		adj.getPricingModel() >> rep
		adj.getTotalAdjustment() >> 200.0

		priceInfoVO.setItemAdjustments(itemAdjustments)

		when:
		pTools.fillItemAdjustments(priceInfoVO,pItem)

		then:
		priceInfoVO.getItemAdjustments() == itemAdjustments
		itemAdjustments.get(rep) ==400.0
	}

	def"fillItemAdjustments, pricing model is null"(){

		given:
		def PriceInfoVO priceInfoVO =new PriceInfoVO()
		def ShippingGroup shippingGroup =Mock()
		def ItemPriceInfo priceInfo =Mock()
		def CommerceItem pItem =Mock()
		def DetailedItemPriceInfo price =Mock()
		def PricingAdjustment adj =Mock()
		def RepositoryItem rep =Mock()

		Map<RepositoryItem, Double> itemAdjustments = new HashMap()

		pItem.getPriceInfo() >> priceInfo
		priceInfo.getCurrentPriceDetails() >> [price]
		price.getAdjustments() >> [adj]
		adj.getPricingModel() >> null
		adj.getTotalAdjustment() >> 200.0

		priceInfoVO.setItemAdjustments(itemAdjustments)

		when:
		pTools.fillItemAdjustments(priceInfoVO,pItem)

		then:
		priceInfoVO.getItemAdjustments() == itemAdjustments
		itemAdjustments.isEmpty() == true
	}

	def "fillAllAdjustments, when allPromotions list is not empty"(){

		given:
		def PriceInfoVO priceInfoVO = new PriceInfoVO()
		def Order pOrder =Mock()
		def RepositoryItem rep =Mock()
		def BBBPromotionTools tools = Mock()

		pOrder.getProfileId() >> "Id"
		pTools.setPromotionTools(tools)

		tools.getPromotions(pOrder.getProfileId()) >> [rep]
		tools.getOrderPromotions(pOrder, _, _, _, _, false) >> {}


		when:
		pTools.fillAllAdjustments(priceInfoVO,pOrder)

		then:
		0*pTools.logError("Invalid Profile from Order. The order is not linked to any profile.")
		0*pTools.logError("PromotionException occorred while listing all promotions atached to user's profile", _)
	}

	def "fillAllAdjustments, when allPromotions list is null"(){

		given:
		def PriceInfoVO priceInfoVO = new PriceInfoVO()
		def Order pOrder =Mock()
		def RepositoryItem rep =Mock()
		def BBBPromotionTools tools = Mock()

		pOrder.getProfileId() >> "Id"
		pTools.setPromotionTools(tools)

		tools.getPromotions(pOrder.getProfileId()) >> null
		tools.getOrderPromotions(pOrder, _, _, _, _, false) >> {}


		when:
		pTools.fillAllAdjustments(priceInfoVO,pOrder)

		then:
		0*pTools.logError("Invalid Profile from Order. The order is not linked to any profile.")
		0*pTools.logError("PromotionException occorred while listing all promotions atached to user's profile", _)
	}

	def "fillAllAdjustments, when ProfileId is null"(){

		given:
		def PriceInfoVO priceInfoVO = new PriceInfoVO()
		def Order pOrder =Mock()
		def RepositoryItem rep =Mock()
		def BBBPromotionTools tools = Mock()

		pTools.setLoggingError(true)
		pOrder.getProfileId() >>null
		pTools.setPromotionTools(tools)

		when:
		pTools.fillAllAdjustments(priceInfoVO,pOrder)

		then:
		1*pTools.logError("Invalid Profile from Order. The order is not linked to any profile.")
		0*pTools.logError("PromotionException occorred while listing all promotions atached to user's profile", _)
	}

	def "fillAllAdjustments, when PromotionException  is thrown"(){

		given:
		def PriceInfoVO priceInfoVO = new PriceInfoVO()
		def Order pOrder =Mock()
		def RepositoryItem rep =Mock()
		def BBBPromotionTools tools = Mock()

		pOrder.getProfileId() >> "Id"
		pTools.setPromotionTools(tools)

		tools.getPromotions(pOrder.getProfileId()) >> {throw new PromotionException("")}

		when:
		pTools.fillAllAdjustments(priceInfoVO,pOrder)

		then:
		0*pTools.logError("Invalid Profile from Order. The order is not linked to any profile.")
		1*pTools.logError("PromotionException occorred while listing all promotions atached to user's profile", _)
	}

	def"updatePreviousPricesInCommerceItems, takes the Current Order and sets the previous price  commerce item with saleprice"(){

		given:
		def OrderImpl order =Mock()
		def BBBCommerceItem cItem =Mock()
		def CommerceItem cItem1 =Mock()
		def MutableRepositoryItem mRep =Mock()
		def ItemPriceInfo item =Mock()

		pTools.setLoggingDebug(true)
		order.getCommerceItems() >> [cItem,cItem1]
		cItem.getPriceInfo() >> item
		item.getSalePrice() >> 50.0

		when:
		pTools.updatePreviousPricesInCommerceItems(order)

		then:
		1*pTools.logDebug("Previous Price is: 0.0 Now Setting it to : " + item.getSalePrice())
		1*cItem.setPrevPrice(item.getSalePrice())

	}

	def"updatePreviousPricesInCommerceItems, takes the Current Order and sets the previous price  commerce item with list price"(){

		given:
		def OrderImpl order =Mock()
		def BBBCommerceItem cItem =Mock()
		def MutableRepositoryItem mRep =Mock()
		def ItemPriceInfo item =Mock()

		pTools.setLoggingDebug(true)
		order.getCommerceItems() >> [cItem]
		cItem.getPriceInfo() >> item
		item.getSalePrice() >> 0.0
		item.getListPrice() >> 100.0

		when:
		pTools.updatePreviousPricesInCommerceItems(order)

		then:
		1*pTools.logDebug("Previous Price is: 0.0 Now Setting it to : " + item.getListPrice())
		1*cItem.setPrevPrice(item.getListPrice())

	}

	def"updatePreviousPricesInCommerceItems, when there is no commerce Item"(){

		given:
		def OrderImpl order =Mock()
		def BBBCommerceItem cItem =Mock()

		pTools.setLoggingDebug(true)
		order.getCommerceItems() >> []

		when:
		pTools.updatePreviousPricesInCommerceItems(order)

		then:
		0*pTools.logDebug("Previous Price is: 0.0 Now Setting it to : " + _)
		0*cItem.setPrevPrice(_)

	}

	def"updatePreviousPricesInCommerceItems, when order is not an instance of orderImpl"(){

		given:
		def Order order =Mock()

		pTools.setLoggingDebug(true)

		when:
		pTools.updatePreviousPricesInCommerceItems(order)

		then:
		0*pTools.logDebug("Previous Price is: 0.0 Now Setting it to : " + _)
		0*order.getCommerceItems()

	}

	def"updatePreviousPricesInCommerceItems, when order is null"(){

		given:
		def Order order =Mock()

		pTools.setLoggingDebug(true)

		when:
		pTools.updatePreviousPricesInCommerceItems(null)

		then:
		0*pTools.logDebug("Previous Price is: 0.0 Now Setting it to : " + _)
		0*order.getCommerceItems()


	}

	def"fillAdjustmentsForShipMethod , when shipMethodId id equal to shippingMethod and shippingWithSurcharge is greater than 0"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def RepositoryItem rep =Mock()
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def Collection c =Mock()
		String shipMethodId ="express"

		shippingGroup.getPriceInfo() >> priceInfo
		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  c

		shippingGroup.getShippingMethod()  >> "express"
		priceInfo.getAdjustments() >> [adj,adj1]

		adj.getPricingModel() >> rep
		adj.getTotalAdjustment() >> 200.0

		adj1.getPricingModel() >> null
		adj1.getTotalAdjustment() >> 100.0

		priceInfo.getRawShipping() >> 250.0
		priceInfo.getSurcharge() >>  50.0

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 50.0
		pTools.getTempShippingMethod() == null
		0*priceEngine.priceShippingGroup(_, shippingGroup,c, _,profileItem,_)
		0*shippingGroup.setShippingMethod("express")
		0*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod,when shipMethodId id equal to shippingMethod and shippingWithSurcharge is equal to 0"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def RepositoryItem rep =Mock()
		def PricingAdjustment adj =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def Collection c =Mock()
		String shipMethodId ="express"

		shippingGroup.getPriceInfo() >> priceInfo
		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  c

		shippingGroup.getShippingMethod()  >> "express"
		priceInfo.getAdjustments() >> [adj]

		adj.getPricingModel() >> rep
		adj.getTotalAdjustment() >> 200.0

		priceInfo.getRawShipping() >> 150.0
		priceInfo.getSurcharge() >>  50.0

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 0.0
		pTools.getTempShippingMethod() == null
		0*priceEngine.priceShippingGroup(_, shippingGroup,c, _,profileItem,_)
		0*shippingGroup.setShippingMethod("express")
		0*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod,when shipMethodId id equal to shippingMethod and shippingWithSurcharge is less than  0"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def RepositoryItem rep =Mock()
		def PricingAdjustment adj =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def Collection c =Mock()
		String shipMethodId ="express"

		shippingGroup.getPriceInfo() >> priceInfo
		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  c

		shippingGroup.getShippingMethod()  >> "express"
		priceInfo.getAdjustments() >> [adj]

		adj.getPricingModel() >> rep
		adj.getTotalAdjustment() >> 200.0

		priceInfo.getRawShipping() >> 50.0
		priceInfo.getSurcharge() >>  100.0

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 150.0
		pTools.getTempShippingMethod() == null
		0*priceEngine.priceShippingGroup(_, shippingGroup,c, _,profileItem,_)
		0*shippingGroup.setShippingMethod("express")
		0*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod, shipMethodId is equal to empty "(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def RepositoryItem rep =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def Collection c =Mock()
		String shipMethodId =""

		shippingGroup.getPriceInfo() >> priceInfo
		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  c

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 0.0
		pTools.getTempShippingMethod() == null
		0*priceEngine.priceShippingGroup(_, shippingGroup,c, _,profileItem,_)
		0*shippingGroup.setShippingMethod("express")
		0*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod, when PriceInfo is null "(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def BBBOrder order =Mock()
		String shipMethodId =""

		shippingGroup.getPriceInfo() >> null

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 0.0
		pTools.getTempShippingMethod() == null
	}

	def"fillAdjustmentsForShipMethod, shipMethodId is not  equal to 3g "(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def RepositoryItem rep =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def Collection c =Mock()
		String shipMethodId ="exp"

		shippingGroup.getPriceInfo() >> priceInfo
		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  c

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 0.0
		pTools.getTempShippingMethod() == null
		0*priceEngine.priceShippingGroup(_, shippingGroup,c, _,profileItem,_)
		0*shippingGroup.setShippingMethod("express")
		0*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod, when shipMethodId is equal to 3g and shippingWithSurcharge is 0"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def BBBShippingPriceInfo info =Mock()
		def RepositoryItem rep =Mock()
		def RepositoryItem rep1 =Mock()
		def RepositoryItem coupon =Mock()
		def RepositoryItem item =Mock()
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def Collection c =Mock()
		def BBBPromotionTools tools =Mock()
		String shipMethodId = BBBCoreConstants.SHIP_METHOD_STANDARD_ID
		pTools.setLoggingDebug(true)

		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		shippingGroup.getPriceInfo() >> priceInfo
		shippingGroup.getShippingMethod()  >> "express"

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  [rep,rep1]


		pTools.setPromotionTools(tools)
		tools.getCoupons(rep) >> [coupon]
		coupon.getRepositoryId() >> "123"
		tools.getCoupons(rep1) >> null

		priceEngine.priceShippingGroup(order,shippingGroup,priceEngine.getPricingModels(profileItem), _,profileItem,_) >> info

		info.getAdjustments() >> [adj,adj1]
		adj.getPricingModel() >> item
		adj.getTotalAdjustment() >> 500.0
		info.getRawShipping() >> 300.0
		info.getSurcharge() >> 200.0


		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value ==0.0
		pTools.getTempShippingMethod()   == "express"
		1*pTools.logDebug("Putting coupon "+ coupon.getRepositoryId() + " in the promo map")
		1*shippingGroup.setShippingMethod("express")
		1*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod, when shipMethodId is equal to 3g and shippingWithSurcharge is greater than 0"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def BBBShippingPriceInfo info =Mock()
		def RepositoryItem rep =Mock()
		def RepositoryItem coupon =Mock()
		def RepositoryItem item =Mock()
		def PricingAdjustment adj =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def Collection c =Mock()
		def BBBPromotionTools tools =Mock()
		String shipMethodId = BBBCoreConstants.SHIP_METHOD_STANDARD_ID
		pTools.setLoggingDebug(true)

		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		shippingGroup.getPriceInfo() >> priceInfo
		shippingGroup.getShippingMethod()  >> "express"

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  null

		priceEngine.priceShippingGroup(order,shippingGroup,priceEngine.getPricingModels(profileItem), _,profileItem,_) >> info

		info.getAdjustments() >> [adj]
		adj.getPricingModel() >> item
		adj.getTotalAdjustment() >> 500.0
		info.getRawShipping() >> 550.0
		info.getSurcharge() >> 100.0


		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value ==50.0
		pTools.getTempShippingMethod()   == "express"
		0*pTools.logDebug("Putting coupon "+ null + " in the promo map")
		1*shippingGroup.setShippingMethod("express")
		1*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod, when shipMethodId is equal to 3g and shippingWithSurcharge is less than 0"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def BBBShippingPriceInfo info =Mock()
		def RepositoryItem item =Mock()
		def PricingAdjustment adj =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def BBBPromotionTools tools =Mock()
		String shipMethodId = BBBCoreConstants.SHIP_METHOD_STANDARD_ID
		pTools.setLoggingDebug(true)

		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		shippingGroup.getPriceInfo() >> priceInfo
		shippingGroup.getShippingMethod()  >> "express"

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  null
		priceEngine.priceShippingGroup(order,shippingGroup,priceEngine.getPricingModels(profileItem), _,profileItem,_) >> info

		info.getAdjustments() >> [adj]
		adj.getPricingModel() >> item
		adj.getTotalAdjustment() >> 500.0
		info.getRawShipping() >> 200.0
		info.getSurcharge() >> 100.0

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 400.0
		pTools.getTempShippingMethod()   == "express"
		0*pTools.logDebug("Putting coupon "+ null + " in the promo map")
		1*shippingGroup.setShippingMethod("express")
		1*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod, when shipMethodId is equal to 3g and BBBShippingPriceInfo is null"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def RepositoryItem profileItem =Mock()
		def BBBOrder order =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def BBBPromotionTools tools =Mock()
		String shipMethodId = BBBCoreConstants.SHIP_METHOD_STANDARD_ID
		pTools.setLoggingDebug(true)

		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		shippingGroup.getPriceInfo() >> priceInfo
		shippingGroup.getShippingMethod()  >> "express"

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  null

		priceEngine.priceShippingGroup(order,shippingGroup,priceEngine.getPricingModels(profileItem), _,profileItem,_) >> null

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 0.0
		pTools.getTempShippingMethod()   == "express"
		0*pTools.logDebug("Putting coupon "+ null + " in the promo map")
		1*shippingGroup.setShippingMethod("express")
		1*shippingGroup.setPriceInfo(priceInfo)
	}

	def"fillAdjustmentsForShipMethod, when RepositoryException is thrown"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def BBBOrder order =Mock()
		def RepositoryItem profileItem =Mock()
		String shipMethodId =""
		pTools.setLoggingError(true)

		shippingGroup.getPriceInfo() >> priceInfo
		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> {throw new RepositoryException("")}

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 0.0
		pTools.getTempShippingMethod() == null
		1*pTools.logError("fillAdjustmentsForShipMethod : Repository Exception occorred while updating the Shipping Method prices for Free Standard Shipping")
	}

	def"fillAdjustmentsForShipMethod, when PricingException is thrown"(){

		given:
		def ShippingGroup shippingGroup =Mock()
		def BBBShippingPriceInfo priceInfo =Mock()
		def ShippingPricingEngine priceEngine =Mock()
		def BBBOrder order =Mock()
		def RepositoryItem profileItem =Mock()
		String shipMethodId =BBBCoreConstants.SHIP_METHOD_STANDARD_ID
		pTools.setLoggingError(true)

		shippingGroup.getShippingMethod() >> "express"
		shippingGroup.getPriceInfo() >> priceInfo

		pTools.setShippingPricingEngine(priceEngine)
		priceEngine.getPricingModels(profileItem) >>  null

		order.getProfileId() >> "profile"
		pTools.getProfile(order.getProfileId()) >> profileItem

		priceEngine.priceShippingGroup(order,shippingGroup,priceEngine.getPricingModels(profileItem), _,profileItem,_) >> {throw new PricingException("")}

		when:
		double value = pTools.fillAdjustmentsForShipMethod(shippingGroup,order,shipMethodId)

		then:
		value == 0.0
		pTools.getTempShippingMethod() == "express"
		1*pTools.logError("fillAdjustmentsForShipMethod : Pricing Exception occorred while updating the Shipping Method prices for Free Standard Shipping")
	}

	def"updatePreviousPricesInSavedItems , retrieves the Saved items from Session bean and iterates through each item and set the previous price of each item with list/sale (current) price"() {

		given:
		def GiftlistManager giftListManager =Mock()
		def RepositoryItem profile =Mock()
		def BBBSavedItemsSessionBean bean =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def GiftListVO vo= new GiftListVO()
		def GiftListVO vo1= new GiftListVO()
		def GiftListVO vo2= new GiftListVO()
		def MutableRepositoryItem item =Mock()
		def GiftlistTools gTools =Mock()
		def MutableRepository repository =Mock()

		pTools.setCatalogUtil(impl)
		pTools.setLoggingDebug(true)
		pTools.extractSiteId() >> "tbs"

		vo.setProdID("1")
		vo.setSkuID("skuId1")

		vo1.setProdID("2")
		vo1.setSkuID("skuId2")

		vo2.setProdID("3")
		vo2.setSkuID("skuId3")

		requestMock.resolveName(BBBCoreConstants.SAVEDCOMP) >> bean
		bean.getItems() >> [vo,vo1,vo2]

		impl.isSkuOnSale(vo.getProdID(), _) >> true
		impl.getListPrice(vo.getProdID(), _) >>null
		impl.getSalePrice(vo.getProdID(), _) >> 350.0

		impl.isSkuOnSale(vo1.getProdID(), _) >> true
		impl.getListPrice(vo1.getProdID(), _) >>500.0
		impl.getSalePrice(vo1.getProdID(), _) >> null

		impl.isSkuOnSale(vo2.getProdID(), _) >> false
		impl.getListPrice(vo2.getProdID(), _) >>500.0
		impl.getSalePrice(vo2.getProdID(), _) >> null

		profile.isTransient() >>false >> true
		profile.getRepositoryId() >> "repId"

		giftListManager.getWishlistId(profile.getRepositoryId()) >> "wishId"
		giftListManager.getGiftlistItemId("wishId", _, vo.getProdID(), pTools.extractSiteId()) >> "listItemId"
		giftListManager.getGiftitem("listItemId") >> item

		giftListManager.getGiftlistTools()>> gTools
		gTools.getGiftlistRepository() >> repository

		when:
		pTools.updatePreviousPricesInSavedItems(requestMock, giftListManager, profile)

		then:
		1*pTools.logDebug("Previous Price is: " + 0.0 + " Now Setting it to : " + 350.0)
		1*pTools.logDebug("Previous Price is: " + 0.0 + " Now Setting it to : " + 500.0)
		1*pTools.logDebug("Previous Price is: " + 0.0 + " Now Setting it to : " + 500.0)
		1*item.setPropertyValue(BBBCoreConstants.PREVIOUSPRICE, 350.0)
		1* repository.updateItem(item)
		1*bean.setGiftListVO([vo,vo1,vo2])
	}

	def"updatePreviousPricesInSavedItems, when saved items list is empty"(){

		given:
		def BBBSavedItemsSessionBean bean =Mock()
		def GiftlistManager giftListManager =Mock()
		def RepositoryItem profile =Mock()

		requestMock.resolveName(BBBCoreConstants.SAVEDCOMP) >> bean
		bean.getItems() >> []
		pTools.setLoggingDebug(true)

		when:
		pTools.updatePreviousPricesInSavedItems(requestMock, giftListManager, profile)

		then:
		1*bean.setGiftListVO([])
		0*pTools.logDebug("Previous Price is: " + 0.0 + " Now Setting it to : " + 0.0)
		1*pTools.logDebug("Exit - BBBPricingTools Method Name [updatePreviousPricesInSavedItems]")
	}

	def"isOrderRestrictedForPricing, when state is qouted"(){

		given:
		Order pOrder =Mock()
		OrderStates states =Mock()
		1*pTools.extractOrderStates() >> states
		states.getStateValue(OrderStates.QUOTED) >> 10
		1*pOrder.getState() >> 10
		2*pOrder.getId() >> "Id"

		when:
		boolean value =pTools.isOrderRestrictedForPricing(pOrder)

		then:
		value == true
		pTools.logDebug("Repricing called for submitted order with order id: ["+pOrder.getId()+"] from : "+_);
	}
	
	def"isOrderRestrictedForPricing, when state is submitted"(){

		given:
		Order pOrder =Mock()
		OrderStates states =Mock()
		1*pTools.extractOrderStates() >> states
		states.getStateValue(OrderStates.SUBMITTED) >> 10
		2*pOrder.getState() >> 10
		2*pOrder.getId() >> "Id"

		when:
		boolean value =pTools.isOrderRestrictedForPricing(pOrder)

		then:
		value == true
		pTools.logDebug("Repricing called for submitted order with order id: ["+pOrder.getId()+"] from : "+_);
	}
	
	def"isOrderRestrictedForPricing, when state is neither submitted nor qouted"(){

		given:
		Order pOrder =Mock()
		OrderStates states =Mock()
		1*pTools.extractOrderStates() >> states
		states.getStateValue(OrderStates.SUBMITTED) >> 10
		2*pOrder.getState() >> 20
		1*pOrder.getId() >> "Id"

		when:
		boolean value =pTools.isOrderRestrictedForPricing(pOrder)

		then:
		value == false
		0*pTools.logDebug("Repricing called for submitted order with order id: ["+pOrder.getId()+"] from : "+_);
	}
}
