package com.bbb.commerce.pricing

import java.util.HashSet
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.bbb.commerce.pricing.calculator.BBBOrderSubtotalCalculator
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem
import com.bbb.repositorywrapper.RepositoryItemWrapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.bbb.ecommerce.order.BBBOrderImpl;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.FilteredCommerceItem
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.TaxPriceInfo
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor
import spock.lang.specification.BBBExtendedSpec

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;

class BBBCIAttributesBasedFilterSpecification  extends BBBExtendedSpec {
	
	def BBBCIAttributesBasedFilter attributesFilter
	def BBBCommerceItem bbbCommerceItems = Mock()
	def RepositoryItem pItem = Mock()
	def RepositoryItemDescriptor pDescriptor = Mock()
	def Order pOrder =Mock()
	def RepositoryItem pPricingModel = Mock()
	def TaxPriceInfo taxInfo=Mock()
	def CommerceItem cItem =Mock()
	def BBBOrderPriceInfo pPriceQuote =  new BBBOrderPriceInfo()
	def PricingContext pricingContext =Mock()
	def BBBCoreConstants bbcore= Mock()
	def BBBCatalogTools cTools = Mock()
	
	
	Locale locale
	List filteredItems = new ArrayList()
	Map detailsPendingActingAsQualifier
	Map detailsRangesToReceiveDiscount
	int filterContext

	
	def setup(){
		
		attributesFilter = new BBBCIAttributesBasedFilter()
		attributesFilter.setCatalogTools(cTools)
		
	}
	
	def"tests if pricing context is not null and pricingContext.getPricingModel() is not null"(){
		given:
		attributesFilter = Spy()
		Map<String, Object> extraParametersMap = new HashMap<>()
		extraParametersMap.put(bbcore.PROMO_MAP, null)
		pricingContext.getPricingModel() >>pItem
		pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "Item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because pricingContext is not valid : pricingContext - "
						+ pricingContext);
		1*attributesFilter.vlogDebug("Leaving BBBCIAttributesBasedFilter.filterItems: because promotion map is null or gobal promotion has no rules to execute")

	}
	
	def"tests if pricing context is not null and pricingContext.getPricingModel() is null"(){
		given:
		attributesFilter = Spy()
		Map<String, Object> extraParametersMap = new HashMap<>()
		extraParametersMap.put(bbcore.PROMO_MAP, null)
		pricingContext.getPricingModel() >>null

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because pricingContext is not valid : pricingContext - "
						+ pricingContext);

	}
	
	def"tests if pricing context is null and pricingContext.getPricingModel() is  null"(){
		given:
		attributesFilter= Spy()
		Map<String, Object> extraParametersMap = new HashMap<>()

		when:
		attributesFilter.filterItems(filterContext, null,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because pricingContext is not valid : pricingContext - null")

	}
	
	def"tests if Item descriptor name is equal to closeness qualifier"(){
		given:
		attributesFilter = Spy()
		Map<String, Object> extraParametersMap = new HashMap<>()
		pricingContext.getPricingModel() >>pItem
		pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "closenessQualifier"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Start : BBBCIAttributesBasedFilter.filterItems");
		0*attributesFilter.vlogDebug("Leaving BBBCIAttributesBasedFilter.filterItems: because promotion map is null or gobal promotion has no rules to execute")

	}
	
	
	
	def"tests if promotion map is not null, repository name is not equal to Item discount and coupon code is null"(){
		given:
		Map<RepositoryItem, String> promoMap = new HashMap<>()
	    promoMap.put(pItem, "123")
		Map<String, Object> extraParametersMap = new HashMap<>()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, null)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
        pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "Item"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		promoMap.isEmpty() == false

	}
	
	
	
	def"tests if promotion map is null"(){
		
		given:
		attributesFilter = Spy(BBBCIAttributesBasedFilter)
		attributesFilter.setCatalogTools(cTools)
     	Map<String, Object> extraParametersMap = new HashMap<>()
     	extraParametersMap.put(bbcore.PROMO_MAP, null )
		pricingContext.getPricingModel() >>pItem
		pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "Item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.vlogDebug("Leaving BBBCIAttributesBasedFilter.filterItems: because promotion map is null or gobal promotion has no rules to execute")

	}
	
	def"tests if repository name is equal to Item Discount and coupon code is null"(){
		
		given:
		Map<RepositoryItem, String> promoMap = new HashMap<>()
		promoMap.put(pItem, "123")	
		Map<String, Object> extraParametersMap = new HashMap<>()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, null)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "Item Discount"
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		promoMap.isEmpty() == true
	
	}
	
	def"tests if repository name is not equal to item discount and coupon code is not null"(){
		
		given:
		Map<RepositoryItem, String> promoMap = new HashMap<>()
		promoMap.put(pItem, "123")
		Map<String, Object> extraParametersMap = new HashMap<>()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "Item"
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		promoMap.isEmpty() == false
	
	}
	
	
	
	def"tests if repository name is equal to Item Descriptor and coupon code is not null"(){
		given:
		Map<RepositoryItem, String> promoMap = new HashMap<>()
		promoMap.put(pItem, "123")
		Map<String, Object> extraParametersMap = new HashMap<>()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50Off")
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		promoMap.isEmpty() == false

	}
	
	
	def"tests if coupon code is empty, ItemDescriptorName is equal to Item Descriptor and coupon code is not null"(){ 
		given:
		attributesFilter = Spy(BBBCIAttributesBasedFilter)
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap<>()
		promoMap.put(pItem, "123")
		Map<String, Object> extraParametersMap = new HashMap<>()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor()>>pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("** Duplicate call for Qualifier for Coupon  : 50off")
		extraParametersMap.get(bbcore.COUPON_CODE) == null
	}
	
	
	def"tests if coupon code is not empty, ItemDescriptorName is not equal to Item discount and coupon code is not null"(){  
		given:
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> map = new HashMap()
		map.put("1", "abc")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", map)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		extraParametersMap.get(bbcore.COUPON_CODE) == "50off"
		0*attributesFilter.logDebug("** Duplicate call for Qualifier for Coupon  : " + extraParametersMap.get(bbcore.COUPON_CODE));

	}
	
	def"tests if coupon code is not empty, ItemDescriptorName is equal to Item discount and coupon code is null"(){  
		given:
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> map = new HashMap()
		map.put("1", "abc")
		
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, null)
		extraParametersMap.put("couponListParam", map)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("** Duplicate call for Qualifier for Coupon  : " + extraParametersMap.get(bbcore.COUPON_CODE));
		extraParametersMap.get(bbcore.COUPON_CODE) == null
	}
	
	
	def"tests if coupon list is not null and coupon list does not contains coupon code"(){ //check assertion with narinder
		
		given:
		attributesFilter =Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" , "abc")
		
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		//pItem.getRepositoryId() >> "123" not setting repository id
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("** Duplicate call for Qualifier for Coupon  : " + extraParametersMap.get(bbcore.COUPON_CODE));
		
	}
	
	def"tests if coupon list is not null and coupon list contains coupon code"(){ //check assertion with narinder
		
		given:
		attributesFilter =Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put(bbcore.COUPON_CODE , "50off")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		//pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("** Duplicate call for Qualifier for Coupon  : 50off")
		1*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list")
		extraParametersMap.get(bbcore.COUPON_CODE) == null
		
	}
	
	def"tests if coupon code is not empty"(){ 
		given:
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		//promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		//pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because coupon code is not valid")
	}
	
	def"tests if coupon code is empty when prmotion id and repository id are not same"(){ 
		given:
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "1234"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because coupon code is not valid")
	}
	
	def"tests when coupon code is not empty, filtered items list is empty and applied coupon is true and LogCouponDetails is true "(){
		given:
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		//promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put(bbcore.COUPON_CODE , "50off")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		//pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		cTools.isLogCouponDetails() >> true

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because coupon code is not valid")
		1*attributesFilter.logInfo("The coupon : 50off cannot be applied to any commerce item. The items have already been filetered out before applying exclusion/inclusion rules.");
		1*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
	}
	
	def"tests if coupon code is not empty, filtered items list is empty and appliedCouponflag is false and LogCouponDetails is true"(){
		given:
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		//promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		//pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		cTools.isLogCouponDetails() >> true

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because coupon code is not valid")
		0*attributesFilter.logInfo("The coupon : 50off cannot be applied to any commerce item. The items have already been filetered out before applying exclusion/inclusion rules.");
		1*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
	}
	
	def"tests when filtered items list is empty and appliedCouponflag is true and LogCouponDetails is false"(){
		given:
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		//promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put(bbcore.COUPON_CODE , "50off")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		//pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		cTools.isLogCouponDetails() >> false

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because coupon code is not valid")
		0*attributesFilter.logInfo("The coupon : 50off cannot be applied to any commerce item. The items have already been filetered out before applying exclusion/inclusion rules.");
		1*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
	}
	
	def"tests when filtered items list is not empty"(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		//promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		//pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)


		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because coupon code is not valid")
		0*attributesFilter.logInfo("The coupon : 50off cannot be applied to any commerce item. The items have already been filetered out before applying exclusion/inclusion rules.");
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
	}
	
	def"tests when filtered items list is not empty and isSkuExcluded method returns true and LogCouponDetails is true "(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> true
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
	//	promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
	//	pItem.getRepositoryId() >> "1234"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		cTools.isLogCouponDetails() >> true
		


		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == true
		1*attributesFilter.logInfo("The coupon : 50off cannot be applied to any commerce Item. The items has been filtered out due to exclusion rule");
	}
	
	def"tests when filtered items list is not empty and isSkuExcluded method returns true and LogCouponDetails is false "(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> true
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
	//	promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
	//	pItem.getRepositoryId() >> "1234"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		cTools.isLogCouponDetails() >> false
		


		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == true
		0*attributesFilter.logInfo("The coupon : 50off cannot be applied to any commerce Item. The items has been filtered out due to exclusion rule");
	}
	
	
	def"tests when filtered items list is not empty  and isSkuExcluded method returns false and LogCouponDetails is false  "(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
	//	promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
	//	pItem.getRepositoryId() >> "1234"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
	
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == false
		0*attributesFilter.logInfo("The coupon : 50off cannot be applied to any commerce Item. The items has been filtered out due to exclusion rule");
	}
	
	def"tests if createSkuLevelMapForCoupons method is called when isUserCoupon is true and is skuMap contains the entry of the commerce item "(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		def RepositoryItem c= Mock()
		
		Map<String,String> couponlist = new HashMap()
		fItem.getWrappedItem() >> cItem
		fItem.getCatalogRefId() >> "fRefID"
		cItem.getCatalogRefId() >> "refId"
		pItem.getRepositoryId() >> "123"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy(BBBCIAttributesBasedFilter)
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		LinkedHashMap<RepositoryItem, String> skuMapValue = new HashMap()
	    Map <String,LinkedHashMap<RepositoryItem, String>> skuMap = ["fRefID": skuMapValue]
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, null)
		extraParametersMap.put("couponListParam", couponlist)
		extraParametersMap.put(bbcore.SKU_MAP,skuMap)
		pricingContext.getPricingModel() >>pItem
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
	
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == false
		1*attributesFilter.logDebug("** createSkuLevelMapForCoupons : Start");
		skuMapValue.get(pItem) =="123"
		//skuMap.get(fItem.getCatalogRefId()) == "123"
	//	extraParametersMap.get(bbcore.SKU_MAP) ==  (Object)skuMap
	}
	
	
	def"tests if createSkuLevelMapForCoupons method is called when isUserCoupon is true and skuMap does not contains the entry of the commerce item "(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		def RepositoryItem c= Mock()
		
		Map<String,String> couponlist = new HashMap()
		fItem.getWrappedItem() >> cItem
		fItem.getCatalogRefId() >> "fRefID"
		cItem.getCatalogRefId() >> "refId"
		pItem.getRepositoryId() >> "123"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy(BBBCIAttributesBasedFilter)
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		LinkedHashMap<RepositoryItem, String> skuMapValue = new HashMap()
	    Map <String,LinkedHashMap<RepositoryItem, String>> skuMap =  new HashMap()//["fRefID": null]
		skuMap.put("fRefID", null)
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, null)
		extraParametersMap.put("couponListParam", couponlist)
		extraParametersMap.put(bbcore.SKU_MAP,skuMap)
		pricingContext.getPricingModel() >>pItem
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
	
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == false
		1*attributesFilter.logDebug("** createSkuLevelMapForCoupons : Start");
		//skuMapValue.get(pItem) =="123" //-check why not working
		//skuMap.get(fItem.getCatalogRefId()) == "skuMapValue"
	}
	
	def"tests if createSkuLevelMapForCoupons method is called when isUserCoupon is true, skuMap does contains the entry of the commerce item "(){//--modify with imran
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		def RepositoryItem c= Mock()
		
		Map<String,String> couponlist = new HashMap()
		fItem.getWrappedItem() >> cItem
		fItem.getCatalogRefId() >> "fRefID"
		cItem.getCatalogRefId() >> "refId"
		pItem.getRepositoryId() >> "123"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy(BBBCIAttributesBasedFilter)
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		LinkedHashMap<RepositoryItem, String> skuMapValue = new HashMap()
		Map <String,LinkedHashMap<RepositoryItem, String>> skuMap =  new HashMap()
		//skuMap.put("fRefID", skuMapValue)
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, null)
		extraParametersMap.put("couponListParam", couponlist)
		extraParametersMap.put(bbcore.SKU_MAP,null)
		pricingContext.getPricingModel() >>pItem
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
	
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == false
		1*attributesFilter.logDebug("** createSkuLevelMapForCoupons : Start");
		extraParametersMap.containsKey("skuMap")
		
		//skuMap.get(fItem.getCatalogRefId()) == "skuMapValue"
	}
	
	def"tests if createSkuLevelMapForCoupons is not called when couponRepositoryItem is null and ItemDescriptor name is item discount "(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy(BBBCIAttributesBasedFilter)
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item discount"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
	
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == false
		0*attributesFilter.createSkuLevelMapForCoupons(pricingContext, extraParametersMap, filteredItems, false,pItem) 
	}
	
	def"tests if createSkuLevelMapForCoupons is not called couponRepositoryItem is not null and ItemDescriptor name is not item discount "(){
		given:
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy(BBBCIAttributesBasedFilter)
		attributesFilter.setCatalogTools(cTools)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
	
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because filteredItems is a null list");
		1*attributesFilter.logDebug("** filteredItems : " + filteredItems);
		filteredItems.isEmpty() == false
		0*attributesFilter.createSkuLevelMapForCoupons(pricingContext, extraParametersMap, filteredItems, false,pItem)
	}
	
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and skuId is not empty "(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		def RepositoryItem jdaItem =Mock()
		def RepositoryItem jdaSubItem =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "refId"
		sku.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaItem
		sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubItem
		jdaItem.getRepositoryId() >> "jdaID"
		jdaSubItem.getRepositoryId() >> "jdasubID"
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [123]")
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		1*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + jdaItem.getRepositoryId()+ ", jdaSubDeptId=" +  jdaSubItem.getRepositoryId() + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is false,isLogCouponDetails is true,couponRuleItem is not null and skuId is not empty "(){ //-change accordingly
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"45") 
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		cTools.isLogCouponDetails() >> true
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		def RepositoryItem jdaItem =Mock()
		def RepositoryItem jdaSubItem =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "refId"
		sku.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaItem
		sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubItem
		jdaItem.getRepositoryId() >> "jdaID"
		jdaSubItem.getRepositoryId() >> "jdasubID"
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "site"
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [123]")
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		//1*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								//.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + jdaItem.getRepositoryId()+ ", jdaSubDeptId=" +  jdaSubItem.getRepositoryId() + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,isLogCouponDetails is false,couponRuleItem is not null and skuId is not empty "(){ //-change accordingly
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		cTools.isLogCouponDetails() >> false
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		def RepositoryItem jdaItem =Mock()
		def RepositoryItem jdaSubItem =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "refId"
		sku.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaItem
		sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubItem
		jdaItem.getRepositoryId() >> "jdaID"
		jdaSubItem.getRepositoryId() >> "jdasubID"
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "site"
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [123]")
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		//1*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								//.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + jdaItem.getRepositoryId()+ ", jdaSubDeptId=" +  jdaSubItem.getRepositoryId() + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	def"checks if checkInclusionItems is called and site Id is empty"(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		def RepositoryItem jdaItem =Mock()
		def RepositoryItem jdaSubItem =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "refId"
		sku.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaItem
		sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubItem
		jdaItem.getRepositoryId() >> "jdaID"
		jdaSubItem.getRepositoryId() >> "jdasubID"
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		def SiteContextManager sitem =Mock()
		sitem.getCurrentSiteId()>> "site1"
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [123]")
		1*attributesFilter.logDebug("Passing parameters for sku inclusion : promotionCode=" + pItem.getRepositoryId() + ", siteId=" + sitem.getCurrentSiteId());
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		1*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + jdaItem.getRepositoryId()+ ", jdaSubDeptId=" +  jdaSubItem.getRepositoryId() + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is null "(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> null
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
	//	couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "refId"
	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		0*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [123]")
		0*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		0*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + sku
								.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME)+ ", jdaSubDeptId=" +  sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	def"check if BBBBusinessException is thrown in filterItems Method "(){
		
		given:
		attributesFilter = Spy()
		Map<String, Object> extraParametersMap = new HashMap()
		pricingContext.getPricingModel() >>{throw new BBBBusinessException("")}
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Start : BBBCIAttributesBasedFilter.filterItems")
		0*attributesFilter.vlogDebug("Leaving BBBCIAttributesBasedFilter.filterItems: because promotion map is null or gobal promotion has no rules to execute")
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because pricingContext is not valid : pricingContext - "+ pricingContext)
	}
	
	def"check if RepositoryException in filterItems method "(){
		
		given:
		attributesFilter = Spy()
		Map<String, Object> extraParametersMap = new HashMap()
		pricingContext.getPricingModel() >>pItem
		pItem.getItemDescriptor() >> {throw new RepositoryException("")}
		
		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("Start : BBBCIAttributesBasedFilter.filterItems")
		0*attributesFilter.vlogDebug("Leaving BBBCIAttributesBasedFilter.filterItems: because promotion map is null or gobal promotion has no rules to execute")
		0*attributesFilter.logDebug("Leaving BBBCIAttributesBasedFilter.filterItems because pricingContext is not valid : pricingContext - "+ pricingContext)
	}
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is empty "(){ 
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>()
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> []
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
	//	couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "refId"
	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		0*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [123]")
		0*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		0*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + sku
								.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME)+ ", jdaSubDeptId=" +  sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and skuId is empty"(){
		
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> ""
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true	

		when:	
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		0*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [123]")
		0*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		0*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + sku
								.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME)+ ", jdaSubDeptId=" +  sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and skuRepositoryItem is null"(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>null
		cTools.isLogCouponDetails()>> true
	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		1*attributesFilter.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		0*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		0*attributesFilter.logInfo("SKU Properties to be checked for inclusion : VendorId=" + sku
								.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) +", jdaDeptId=" + sku
								.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME)+ ", jdaSubDeptId=" +  sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) + ", jdaClass=" + sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME))
		filteredItems.isEmpty() == false
	}
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and jdaDeptItem is null"(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		def RepositoryItem jdasubitemItem =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		sku.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdasubitemItem
		jdasubitemItem.getRepositoryId() >> "jdaRepository"
		sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "class"

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		0*attributesFilter.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
	    1*attributesFilter.logDebug("SKU Properties to be checked for inclusion : VendorId=" + sku.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME)
								+ ", jdaDeptId=" + sku.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) + ", jdaSubDeptId=" + jdasubitemItem.getRepositoryId() + ", jdaClass="
								+ sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME));
		1*attributesFilter.logDebug("Inclusion Rule NOT Applicable hence removing the filterItem from the list for  sku "+ cItem.getCatalogRefId());
		filteredItems.isEmpty() == true
	}
	
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and jdaSubDeptItem is null"(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
        def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		def RepositoryItem jdaItem =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> null
		sku.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaItem
		jdaItem.getRepositoryId() >> "jdaRepository"
		sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "class"

	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		0*attributesFilter.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
	    1*attributesFilter.logDebug("SKU Properties to be checked for inclusion : VendorId=" + sku.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME)
								+ ", jdaDeptId=" + jdaItem.getRepositoryId() + ", jdaSubDeptId=" + sku.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) + ", jdaClass="
								+ sku.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME));
		1*attributesFilter.logDebug("Inclusion Rule NOT Applicable hence removing the filterItem from the list for  sku "+ cItem.getCatalogRefId());
		filteredItems.isEmpty() == true
	}
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and ruleSkuId is empty"(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> ""
	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		0*attributesFilter.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		1*attributesFilter.logDebug("Inclusive Coupon Rule properties for coupon "	+ couponRuleItem.getRepositoryId() + " are : SKU=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID)	+ ", VendorId=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID) + ", jdaDeptId=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID) + ", jdaSubDeptId="
								+ couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID) + ", jdaClass=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS));
		1*attributesFilter.logDebug("Inclusion Rule NOT Applicable hence removing the filterItem from the list for  sku "+ cItem.getCatalogRefId());
		filteredItems.isEmpty() == true
	}
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and ruleSkuId is not equal to skuId"(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "ref"
	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		0*attributesFilter.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		1*attributesFilter.logDebug("Inclusive Coupon Rule properties for coupon "	+ couponRuleItem.getRepositoryId() + " are : SKU=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID)	+ ", VendorId=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID) + ", jdaDeptId=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID) + ", jdaSubDeptId="
								+ couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID) + ", jdaClass=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS));
		1*attributesFilter.logDebug("Inclusion Rule NOT Applicable hence removing the filterItem from the list for  sku "+ cItem.getCatalogRefId());
		filteredItems.isEmpty() == true
	}
	
	
	def"checks if checkInclusionItems is called when isAppliedCoupon is true,couponRuleItem is not null and ruleSkuId is  equal to 0"(){
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cItem.getCatalogRefId() >> "refId"
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>sku
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "0"
	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		0*attributesFilter.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1*attributesFilter.logDebug("Checking the coupon inclusion rule for coupon id : " + pItem.getRepositoryId()
								+ " for the sku id" + cItem.getCatalogRefId());
		1*attributesFilter.logDebug("Inclusive Coupon Rule properties for coupon "	+ couponRuleItem.getRepositoryId() + " are : SKU=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID)	+ ", VendorId=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_VENDOR_ID) + ", jdaDeptId=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_DEPT_ID) + ", jdaSubDeptId="
								+ couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SUB_DEPT_ID) + ", jdaClass=" + couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_CLASS));
		1*attributesFilter.logDebug("Inclusion Rule NOT Applicable hence removing the filterItem from the list for  sku "+ cItem.getCatalogRefId());
		filteredItems.isEmpty() == true
		1*attributesFilter.logInfo("The coupon : "+ pItem.getRepositoryId()+ " cannot be applied to any commerce Item as items has been filtered out due to inclusion rule");
	}
	
	def"tests if exception is thrown in checkInclusionItems method"(){
		
		given:
		def MutableRepository mRep =Mock()
		def BBBOrderImpl orderimpl = Mock()
		pricingContext.getOrder() >> orderimpl
		Map<String , Set<CommerceItem>> promoExcelMap = new HashMap()
		orderimpl.getExcludedPromotionMap() >> promoExcelMap
		def FilteredCommerceItem fItem= Mock()
		fItem.getWrappedItem() >> cItem
		cTools.isSkuExcluded(cItem.getCatalogRefId(), "50off", false) >> false
		attributesFilter = Spy()
		attributesFilter.setCatalogTools(cTools)
		attributesFilter.setCatalogRepository(mRep)
		Map<RepositoryItem, String> promoMap = new HashMap()
		promoMap.put(pItem, "123")
		Map<String,String> couponlist = new HashMap()
		couponlist.put("1" ,"123")
		Map<String, Object> extraParametersMap = new HashMap()
		extraParametersMap.put(bbcore.PROMO_MAP, promoMap)
		extraParametersMap.put(bbcore.COUPON_CODE, "50off")
		extraParametersMap.put("couponListParam", couponlist)
		pricingContext.getPricingModel() >>pItem
		pItem.getRepositoryId() >> "123"
		pItem.getItemDescriptor() >> pDescriptor
		pDescriptor.getItemDescriptorName() >> "item"
		filteredItems.add(fItem)
		Set excludedPromoItems = new HashSet<CommerceItem>();
		cItem.getCatalogRefId() >> "refId"
		
		def RepositoryItem couponRuleItem= Mock()
		def RepositoryItem sku =Mock()
		cTools.executeRQLQuery(*_) >> [couponRuleItem]
		mRep.getItem(cItem.getCatalogRefId(), BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException ("exception")}
		cTools.isLogCouponDetails()>> true
		couponRuleItem.getPropertyValue(BBBCatalogConstants.COUPON_RULES_SKU_ID) >> "0"
	
		

		when:
		attributesFilter.filterItems(filterContext, pricingContext,extraParametersMap, detailsPendingActingAsQualifier, detailsRangesToReceiveDiscount,filteredItems)
		
		then:
		1*attributesFilter.logDebug("BBBCIAttributesBasedFilter class method Name checkInclusionItems() : Start");
		1*attributesFilter.logDebug("Found [1] Inclusion Rule(s) for this Coupon : [" + pItem.getRepositoryId() + "]")
		0*attributesFilter.logDebug("SKU item is not in the Repository for sku id " + cItem.getCatalogRefId())
		1* attributesFilter.logError("BBBCIAttributesBasedFilter Method Name checkInclusionItems() : RepositoryException ");
	}
	

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

