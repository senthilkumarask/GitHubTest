package com.bbb.commerce.pricing

import java.util.ArrayList;
import java.util.LinkedHashMap
import java.util.List;
import java.util.Locale;
import java.util.Map

import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.constants.BBBCoreConstants;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.ItemPricingEngine
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;

class BBBCouponDetailsInitializerSpecification extends BBBExtendedSpec{
	
	def ItemPriceInfo pPriceQuote =Mock()
	def CommerceItem pItem =Mock()
	def RepositoryItem pPricingModel =Mock()
	def RepositoryItem pProfile= Mock()
	def BBBItemPricingEngineService itemEngine =Mock()
	def BBBOrderPricingEngineService orderEngine =Mock()
	def BBBShippingPricingEngineService shippingEngine = Mock()
	BBBCouponDetailsInitializer bbbCouponIniitializer
	
	Locale pLocale
	
	def setup(){
		bbbCouponIniitializer =Spy()
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		
	}
	
	def"priceItem,when active promotions is not null and coupons list is not empty and allGlobalPromotions is empty"(){
	given:

	def RepositoryItem activePromo= Mock()
	def RepositoryItem promotion= Mock()
	def RepositoryItem coupons= Mock() 
	
	def RepositoryItem globalItemPromotions=Mock()
	
	Map <String,Object>pExtraParameters = new HashMap<String,Object>()
	Map<RepositoryItem, String> promoMap = new LinkedHashMap<RepositoryItem, String>()
	pProfile.getPropertyValue("activePromotions") >> [activePromo]
	activePromo.getPropertyValue("promotion") >>promotion
	activePromo.getPropertyValue("coupons") >> [coupons]
	promotion.getRepositoryId() >> "promotionId"
	
	itemEngine.getGlobalPromotions() >>[]
	orderEngine.getGlobalPromotions() >>[]
	shippingEngine.getGlobalPromotions() >>[]
	
	List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
	allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
	allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
	allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
	
	when:
	bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
	
	then:
	1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
	1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Complete list of global promotions is {0}",allGlobalPromotions);
	pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
	Map test =  pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
	test.containsKey(coupons)
	test.containsValue("promotionId")	
	}
	
	
	def"priceItem,when active promotions is not null ,promoMap is not null, couponsListn and promotion repositoryItem is not null, and allGlobalPromotions is not empty"(){ // -check--
		given:
	
		def RepositoryItem activePromo= Mock()
		def RepositoryItem promotion= Mock()
		def RepositoryItem coupons= Mock()
		
		def RepositoryItem globalPromotion=Mock()
		def BBBPromotionTools promotionTools = Mock()
		
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		bbbCouponIniitializer.setPromotionTools(promotionTools)
		
		Map <String,Object>pExtraParameters = new HashMap<String,Object>()
		Map<RepositoryItem, String> promoMap = new LinkedHashMap<RepositoryItem, String>()
		pProfile.getPropertyValue("activePromotions") >> [activePromo]
		activePromo.getPropertyValue("promotion") >>promotion
		activePromo.getPropertyValue("coupons") >> [coupons]
		promotion.getRepositoryId() >> "promotionId"
		globalPromotion.getRepositoryId() >> "123"
		
		itemEngine.getGlobalPromotions() >>[globalPromotion]
		orderEngine.getGlobalPromotions() >>[]
		shippingEngine.getGlobalPromotions() >>[]
		
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
		
		promotionTools.getCoupons(_) >> [globalPromotion]
			
		when:
		bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		0*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Complete list of global promotions is {0}",allGlobalPromotions);
		pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
		Map test =  pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		test.containsKey(coupons)
		test.containsKey(globalPromotion)
		}
	
	def"priceItem, when active promotions is not null, promotion repository item is null and allGlobalPromotions is empty"(){
		
		given:
		def RepositoryItem activePromo= Mock()
		def RepositoryItem coupons= Mock()
		def RepositoryItem globalPromotion=Mock()
		def BBBPromotionTools promotionTools = Mock()
		
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		bbbCouponIniitializer.setPromotionTools(promotionTools)
		
		Map <String,Object>pExtraParameters = new HashMap<String,Object>()
		Map<RepositoryItem, String> promoMap = new LinkedHashMap<RepositoryItem, String>()
		pProfile.getPropertyValue("activePromotions") >> [activePromo]
		activePromo.getPropertyValue("promotion") >>null
		
		itemEngine.getGlobalPromotions() >>[]
		orderEngine.getGlobalPromotions() >>[]
		shippingEngine.getGlobalPromotions() >>[]
		
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
		
		when:
		bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Complete list of global promotions is {0}",allGlobalPromotions);
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
		pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
		Map map = pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		map.containsKey(coupons) == false
		0*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: value of promo map is {0}", promoMap)
		
		}
	
	def"priceItem, when active promotions is null, promotion repository item is null and allGlobalPromotions is empty"(){
		
		given:
		def RepositoryItem activePromo= Mock()
		def RepositoryItem coupons= Mock()
		def RepositoryItem globalPromotion=Mock()
		def BBBPromotionTools promotionTools = Mock()
		
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		bbbCouponIniitializer.setPromotionTools(promotionTools)
		
		Map <String,Object>pExtraParameters = new HashMap<String,Object>()
		Map<RepositoryItem, String> promoMap = new LinkedHashMap<RepositoryItem, String>()
		pProfile.getPropertyValue("activePromotions") >> null
		activePromo.getPropertyValue("promotion") >>null
		
		itemEngine.getGlobalPromotions() >>[]
		orderEngine.getGlobalPromotions() >>[]
		shippingEngine.getGlobalPromotions() >>[]
		
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
		
		when:
		bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Complete list of global promotions is {0}",allGlobalPromotions);
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
		pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
		Map map = pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		//map.containsKey(coupons) == false
		0*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: value of promo map is {0}", promoMap)
		
		}
	
	def"priceItem, when active promotions is not null, coupons list is null and allGlobalPromotions is empty"(){
		
		given:
		def RepositoryItem activePromo= Mock()
		def RepositoryItem coupons= Mock()
		def RepositoryItem globalPromotion=Mock()
		def RepositoryItem promotion= Mock()
		def BBBPromotionTools promotionTools = Mock()
		
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		bbbCouponIniitializer.setPromotionTools(promotionTools)
		
		Map <String,Object>pExtraParameters = new HashMap<String,Object>()
		Map<RepositoryItem, String> promoMap = new LinkedHashMap<RepositoryItem, String>()
		pProfile.getPropertyValue("activePromotions") >> [activePromo]
		activePromo.getPropertyValue("promotion") >>promotion
		activePromo.getPropertyValue("coupons") >> null
		
		itemEngine.getGlobalPromotions() >>[]
		orderEngine.getGlobalPromotions() >>[]
		shippingEngine.getGlobalPromotions() >>[]
		
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
		
		when:
		bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Complete list of global promotions is {0}",allGlobalPromotions);
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
		pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
		Map map = pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		map.containsKey(coupons) == false
		0*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: value of promo map is {0}", promoMap)
		
		}
	
	def"priceItem, when active promotions is null,and allGlobalPromotions is empty"(){
		given:
		def RepositoryItem activePromo= Mock()
		def RepositoryItem promotion= Mock()
		def RepositoryItem coupons= Mock()
		def RepositoryItem globalPromotion=Mock()
		def BBBPromotionTools promotionTools = Mock()
		
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		bbbCouponIniitializer.setPromotionTools(promotionTools)
		
		Map <String,Object>pExtraParameters = new HashMap<String,Object>()
		Map<RepositoryItem, String> promoMap =null
		pProfile.getPropertyValue("activePromotions") >> null
		
		itemEngine.getGlobalPromotions() >>[]
		orderEngine.getGlobalPromotions() >>[]
		shippingEngine.getGlobalPromotions() >>[]
		
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
		
		when:
		bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Complete list of global promotions is {0}",allGlobalPromotions);
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
		pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
		0*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: value of promo map is {0}", promoMap)
		
		}

	
	def"priceItem,tests if repository exception is thrown when active promotions is empty,and allGlobalPromotions is not empty"(){ 
		
		given:
		def RepositoryItem activePromo= Mock()
		def RepositoryItem promotion= Mock()
		def RepositoryItem coupons= Mock()
		def RepositoryItem globalPromotion=Mock()
		def BBBPromotionTools promotionTools = Mock()
		
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		bbbCouponIniitializer.setPromotionTools(promotionTools)
		
		Map <String,Object>pExtraParameters = new HashMap<String,Object>()
		Map<RepositoryItem, String> promoMap =null
		pProfile.getPropertyValue("activePromotions") >> []
		
		itemEngine.getGlobalPromotions() >>[globalPromotion]
		orderEngine.getGlobalPromotions() >>[]
		shippingEngine.getGlobalPromotions() >>[]
		
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
		
		promotionTools.getCoupons(_) >> {throw new RepositoryException("exception")}
		
		
		when:
		bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		0*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
		1*bbbCouponIniitializer.vlogError("BBBCouponDetailsInitializer.priceItem: RepositoryException occured while trying to get coupon items from ClaimableRepository using promotion {0}",globalPromotion);
		pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
		1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: value of promo map is {0}", promoMap)
		
		}
	
	def"priceItem, when active promotions is empty,and promotionTools.getCoupons is not empty"(){
		
		given:
		def RepositoryItem activePromo= Mock()
		def RepositoryItem promotion= Mock()
		def RepositoryItem coupons= Mock()
		def RepositoryItem globalPromotion=Mock()
		def BBBPromotionTools promotionTools = Mock()
		
		bbbCouponIniitializer.setItemPricingEngine(itemEngine)
		bbbCouponIniitializer.setOrderPricingEngine(orderEngine)
		bbbCouponIniitializer.setShippingPricingEngine(shippingEngine)
		bbbCouponIniitializer.setPromotionTools(promotionTools)
		
		Map <String,Object>pExtraParameters = new HashMap<String,Object>()
		Map<RepositoryItem, String> promoMap =  null//new HashMap<RepositoryItem,String>()
		//promoMap.put(globalPromotion, globalPromotion.getRepositoryId())
		pProfile.getPropertyValue("activePromotions") >> []
		globalPromotion.getRepositoryId() >> "123"
		
		itemEngine.getGlobalPromotions() >>[globalPromotion]
		orderEngine.getGlobalPromotions() >>[]
		shippingEngine.getGlobalPromotions() >>[]
		
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(itemEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(orderEngine.getGlobalPromotions())
		allGlobalPromotions.addAll(shippingEngine.getGlobalPromotions())
		
		promotionTools.getCoupons(_) >> [globalPromotion]
		
		when:
		bbbCouponIniitializer.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters)
		
		then:
		0*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
		0*bbbCouponIniitializer.vlogError("BBBCouponDetailsInitializer.priceItem: RepositoryException occured while trying to get coupon items from ClaimableRepository using promotion {0}",globalPromotion);
		pExtraParameters.containsKey(BBBCoreConstants.PROMO_MAP) == true
		Map map = pExtraParameters.get(BBBCoreConstants.PROMO_MAP)
		map.containsKey(globalPromotion)
		map.containsValue(globalPromotion.getRepositoryId())
	//	1*bbbCouponIniitializer.vlogDebug("BBBCouponDetailsInitializer.priceItem: value of promo map is {0}", promoMap)
		
		}
	
	
	
	

}
