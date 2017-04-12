package com.bbb.commerce.order.manager

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.PricingTools
import atg.commerce.promotion.PromotionTools
import atg.repository.RepositoryItem
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile

import com.bbb.account.BBBGetCouponsManager
import com.bbb.account.vo.CouponListVo
import com.bbb.account.vo.CouponResponseVo
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBPayPalConstants
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBSystemException
import com.bbb.framework.webservices.vo.ErrorStatus
import com.bbb.repository.RepositoryItemMock

class PromotionLookupManagerSpecification extends BBBExtendedSpec {

	
	def BBBCatalogToolsImpl catalogToolsMock = Mock()
	def BBBGetCouponsManager couponManager = Mock()
	def PromotionTools promotionToolsMock = Mock()
	def BBBCouponUtil couponUtilMock = Mock()
	def Profile profileMock = Mock()
	def BBBOrderImpl orderMock = Mock()
	def PromotionLookupManager promotionLookUpManagerObj
	def BBBRepositoryContactInfo billingAddress = Mock()
	def PricingTools pTools = Mock()
	
	def setup(){
		promotionLookUpManagerObj = new PromotionLookupManager(catalogTools:catalogToolsMock,couponManager:couponManager
			,promotionTools:promotionToolsMock,couponUtil:couponUtilMock,)
	}
	//sasd
	def "school promotion is applied on order "(){
		given:
		orderMock.getCouponMap() >> ["schoolPromo":new RepositoryItemMock()]
		when:
		 boolean applied = promotionLookUpManagerObj.isSchoolPromotion(orderMock)
		then:
		applied == true
	}
	def "school promotion is not applied on order "(){
		given:
		orderMock.getCouponMap() >> null
		when:
		 boolean applied = promotionLookUpManagerObj.isSchoolPromotion(orderMock)
		then:
		applied == false
	}
	def "Invoking coupon Manger to get list coupon applicable for given profile and profile is transient ,not having billing address in order"(){
		given:
		profileMock.isTransient() >> true
		orderMock.getBillingAddress() >> null
		when:
		List<CouponListVo> list = promotionLookUpManagerObj.getCouponList(profileMock,orderMock,
			"BedBathUS", false)
		then:
		list == null
	}
	def "Invoking coupon Manger to get list coupon applicable for given profile and profile is transient "(){
		given:
		profileMock.isTransient() >> true
		billingAddress.getEmail() >> "bbb@test.com"
		billingAddress.getPhoneNumber () >> "111111111111"
		orderMock.getBillingAddress() >> billingAddress
		CouponResponseVo couponResponse = new CouponResponseVo()
		CouponListVo couponDetails = new CouponListVo()
		couponResponse.setCouponList([couponDetails])
		1*couponManager.getCouponsList("bbb@test.com", "111111111111", null, true, true) >> couponResponse
		when:
		List<CouponListVo> list = promotionLookUpManagerObj.getCouponList(profileMock,orderMock,
			"BedBathUS", true)
		then:
		list.size() >= 1
	}
	def "Invoking coupon Manger to get list coupon applicable for given profile and profile is transient and none of the coupons applicable on order "(){
		given:
		profileMock.isTransient() >> true
		billingAddress.getEmail() >> "bbb@test.com"
		billingAddress.getPhoneNumber () >> "111111111111"
		orderMock.getBillingAddress() >> billingAddress
		CouponResponseVo couponResponse = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		couponResponse.setErrorStatus(eStatus)
		couponResponse.setCouponList(null)
		1*couponManager.getCouponsList("bbb@test.com", "111111111111", null, true, true) >> couponResponse
		when:
		List<CouponListVo> list = promotionLookUpManagerObj.getCouponList(profileMock,orderMock,
			"BedBathUS", true)
		then:
		list == null
	}
	def "Invoking coupon Manger to get list coupon applicable for given profile and profile is registerd and none of the coupons applicable on order "(){
		given:
		orderMock.getBillingAddress() >> billingAddress
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com" 
		1*couponManager.getCouponsList("bbb@test.com", null, null, true, true) >> null
		when:
		List<CouponListVo> list = promotionLookUpManagerObj.getCouponList(profileMock,orderMock,
			"BedBathUS", true)
		then:
		list.isEmpty() == true
	}
	def "Invoking coupon Manger to get list coupon applicable for given profile and profile is registerd with wallet id and none of the coupons applicable on order "(){
		given:
		orderMock.getBillingAddress() >> billingAddress
		profileMock.getPropertyValue("walletId") >> "12345"
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		1*couponManager.getCouponsList("bbb@test.com", null, "12345", true, true) >> null
		when:
		List<CouponListVo> list = promotionLookUpManagerObj.getCouponList(profileMock,orderMock,
			"BedBathUS", true)
		then:
		list.isEmpty() == true
	}
	def "Invoking coupon Manger to get list coupon applicable for given profile and profile is transient thows SystemException "(){
		given:
		promotionLookUpManagerObj.setLoggingDebug(true)
		profileMock.isTransient() >> true
		billingAddress.getEmail() >> "bbb@test.com"
		billingAddress.getMobileNumber () >> "111111111111"
		orderMock.getBillingAddress() >> billingAddress
		CouponResponseVo couponResponse = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setDisplayMessage("couppon error Message")
		eStatus.setErrorExists(true)
		couponResponse.setErrorStatus(eStatus)
		couponManager.getCouponsList("bbb@test.com", "111111111111", null, true, true) >> couponResponse
		when:
		List<CouponListVo> list = promotionLookUpManagerObj.getCouponList(profileMock,orderMock,
			"BedBathUS", true)
		then:
		BBBSystemException exc =  thrown()
		exc.message.equalsIgnoreCase("err_order_populateValidPromotions_sysexcep:Error getting coupons from the services, couppon error Message")
	}
	def "validating  promotions list from repository as empty "(){
		when:
		Map pMpa= promotionLookUpManagerObj.fillPromoList("BEDBATH",null)
		then:
		pMpa.isEmpty() 
	}
	
	def "fecthing promotions for BedBathUSSiteCode a paypal transient user"(){
		given:
		orderMock.getSiteId() >> "BedBathUS"
		RepositoryItemMock rItem = new RepositoryItemMock()
		catalogToolsMock.getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS) >> ["BedBathUSSiteCode":"BedBathUS"]
		catalogToolsMock.getContentCatalogConfigration(BBBPayPalConstants.COUPONS_US) >> ["true"]
		RepositoryItemMock premItem = new RepositoryItemMock()
		RepositoryItemMock premItem1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>  []
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue("walletId") >> "12345"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111"
		CouponResponseVo couponRes = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("")
		cListVo.setRedemptionLimit("")
		cListVo.setExpiryDate(getTodaysdayDateString())
		cListVo.setRedemptionChannel("2")
		cListVo.setEntryCd("CODE")
		CouponListVo cListVo1 = new CouponListVo()
		cListVo1.setRedemptionCount("9")
		cListVo1.setRedemptionLimit("10")
		cListVo1.setExpiryDate(getTodaysdayDateString())
		cListVo1.setRedemptionChannel("2")
		cListVo1.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		2*catalogToolsMock.getPromotions("CODE") >> [itemMock].toArray()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() + (1000 * 60 * 60 * 24))).getTime())])
		couponRes.setCouponList([cListVo,cListVo1])
		couponRes.setErrorStatus(eStatus)
		1*couponManager.getCouponsList("bbb@test.com", "11111111", "12345", true, false) >> couponRes
		1*couponUtilMock.applySchoolPromotion(_,profileMock, orderMock) >> ["CODE":premItem]
		promotionToolsMock.getPricingTools() >> pTools
		when:
		Map pMap= promotionLookUpManagerObj.getCouponMap(orderMock,profileMock)
		then:
		pMap.containsKey("CODE")
		0*promotionToolsMock.removePromotion(profileMock, _, false)
		1*orderMock.setCouponListVo(_)
	}
	def "fecthing promotions for BedBathUSSiteCode a paypal transient user no promotions"(){
		given:
		orderMock.getSiteId() >> "BedBathUS"
		RepositoryItemMock rItem = new RepositoryItemMock()
		catalogToolsMock.getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS) >> ["BedBathUSSiteCode":"BedBathUS"]
		catalogToolsMock.getContentCatalogConfigration(BBBPayPalConstants.COUPONS_US) >> ["true"]
		RepositoryItemMock premItem = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>  [premItem]
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue("walletId") >> "12345"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111"
		CouponResponseVo couponRes = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		RepositoryItemMock itemMock =new RepositoryItemMock()
		catalogToolsMock.getPromotions("CODE") >> [itemMock].toArray()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() + (1000 * 60 * 60 * 24))).getTime())])
		couponRes.setCouponList(null)
		couponRes.setErrorStatus(eStatus)
		1*couponManager.getCouponsList("bbb@test.com", "11111111", "12345", true, false) >> couponRes
		1*couponUtilMock.applySchoolPromotion(_,profileMock, orderMock) >> ["CODE":premItem]
		promotionToolsMock.getPricingTools() >> pTools
		when:
		Map pMap= promotionLookUpManagerObj.getCouponMap(orderMock,profileMock)
		then:
		pMap.containsKey("CODE")
		1*promotionToolsMock.removePromotion(profileMock, premItem, false)
	}
	def "fecthing promotions for BuyBuyBabySiteCode a paypal transient user"(){
		
		given:
		orderMock.getSiteId() >> "BuyBuyBaby"
		RepositoryItemMock rItem = new RepositoryItemMock()
		catalogToolsMock.getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS) >> ["BuyBuyBabySiteCode":"BuyBuyBaby"]
		catalogToolsMock.getContentCatalogConfigration(BBBPayPalConstants.COUPONS_BABY) >> ["false"]
		promotionToolsMock.getPricingTools() >> pTools
		RepositoryItemMock premItem = new RepositoryItemMock()
		RepositoryItemMock premItem1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>  []
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue("walletId") >> "12345"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111"
		CouponResponseVo couponRes = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("")
		cListVo.setRedemptionLimit("")
		cListVo.setExpiryDate(getTodaysdayDateString())
		cListVo.setRedemptionChannel("2")
		cListVo.setEntryCd("CODE")
		CouponListVo cListVo1 = new CouponListVo()
		cListVo1.setRedemptionCount("9")
		cListVo1.setRedemptionLimit("10")
		cListVo1.setExpiryDate(getTodaysdayDateString())
		cListVo1.setRedemptionChannel("2")
		cListVo1.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		0*catalogToolsMock.getPromotions("CODE") >> [itemMock].toArray()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() + (1000 * 60 * 60 * 24))).getTime())])
		couponRes.setCouponList([cListVo,cListVo1])
		couponRes.setErrorStatus(eStatus)
		0*couponManager.getCouponsList("bbb@test.com", "11111111", "12345", true, false) >> couponRes
		0*couponUtilMock.applySchoolPromotion(_,profileMock, orderMock) >> ["CODE":premItem]
		promotionToolsMock.getPricingTools() >> pTools
		when:
		Map pMap= promotionLookUpManagerObj.getCouponMap(orderMock,profileMock)
		then:
		pMap == null
		0*promotionToolsMock.removePromotion(profileMock, _, false)
		0*orderMock.setCouponListVo(_)
		
	}
	def "fecthing promotions for BuyBuyBabySiteCode a paypal transient user pricing exception "(){
		
		given:
		orderMock.getSiteId() >> "BuyBuyBaby"
		RepositoryItemMock rItem = new RepositoryItemMock()
		catalogToolsMock.getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS) >> ["BuyBuyBabySiteCode":"BuyBuyBaby"]
		catalogToolsMock.getContentCatalogConfigration(BBBPayPalConstants.COUPONS_BABY) >> ["true"]
		promotionToolsMock.getPricingTools() >> pTools
		RepositoryItemMock premItem = new RepositoryItemMock()
		RepositoryItemMock premItem1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>  [premItem,premItem1]
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue("walletId") >> "12345"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111"
		CouponResponseVo couponRes = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("")
		cListVo.setRedemptionLimit("")
		cListVo.setExpiryDate(getTodaysdayDateString())
		cListVo.setRedemptionChannel("2")
		cListVo.setEntryCd("CODE")
		CouponListVo cListVo1 = new CouponListVo()
		cListVo1.setRedemptionCount("9")
		cListVo1.setRedemptionLimit("10")
		cListVo1.setExpiryDate(getTodaysdayDateString())
		cListVo1.setRedemptionChannel("2")
		cListVo1.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		0*catalogToolsMock.getPromotions("CODE") >> [itemMock].toArray()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() + (1000 * 60 * 60 * 24))).getTime())])
		couponRes.setCouponList([cListVo,cListVo1])
		couponRes.setErrorStatus(eStatus)
		0*couponManager.getCouponsList("bbb@test.com", "11111111", "12345", true, false) >> couponRes
		0*couponUtilMock.applySchoolPromotion(_,profileMock, orderMock) >> ["CODE":premItem]
		promotionToolsMock.getPricingTools() >> pTools
		1*pTools.priceOrderSubtotalShipping(orderMock, null, _, _, null) >> {throw new PricingException()}
		when:
		Map pMap= promotionLookUpManagerObj.getCouponMap(orderMock,profileMock)
		then:
		2*promotionToolsMock.removePromotion(profileMock, _, false)
		0*orderMock.setCouponListVo(_)
		
	}
	def "fecthing promotions for with out site id a paypal transient user"(){
		given:
		orderMock.getBillingAddress() >> billingAddress
		orderMock.getSiteId() >> "BedBathUS"
		RepositoryItemMock rItem = new RepositoryItemMock()
		catalogToolsMock.getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS) >> [:]
		RepositoryItemMock premItem = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>  [premItem]
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111"
		CouponResponseVo couponRes = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(true)
		couponRes.setErrorStatus(eStatus)
		1*couponManager.getCouponsList("bbb@test.com", "11111111", null, true, false) >> couponRes
		0*couponUtilMock.applySchoolPromotion(_,profileMock, orderMock) >> ["CODE":premItem]
		promotionToolsMock.getPricingTools() >> pTools
		when:
		Map pMap= promotionLookUpManagerObj.getCouponMap(orderMock,profileMock)
		then:
		pMap == null
	}
	def "fecthing promotions for BedBathCanadaSiteCode a paypal transient user"(){
		given:
		orderMock.getSiteId() >> "BedBathCanada"
		RepositoryItemMock rItem = new RepositoryItemMock()
		1*catalogToolsMock.getConfigValueByconfigType(BBBCatalogConstants.CONTENT_CATALOG_KEYS) >> ["BedBathCanadaSiteCode":"BedBathCanada"]
		1*catalogToolsMock.getContentCatalogConfigration(BBBPayPalConstants.COUPONS_CANADA) >> ["true"]
		RepositoryItemMock premItem = new RepositoryItemMock()
		profileMock.isTransient() >> true
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>  [premItem]
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue("walletId") >> "12345"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111"
		orderMock.getBillingAddress() >> billingAddress
		promotionToolsMock.getPricingTools() >> pTools
		when:
		Map pMap= promotionLookUpManagerObj.getCouponMap(orderMock,profileMock)
		then:
		pMap == null
	}
	def "fecthing promotions for shipping address as empty because email is is not availalbe"(){
		when:
		Map shipMap = promotionLookUpManagerObj.populateSPCValidPromotions(profileMock,orderMock,"US")
		then:
		shipMap.isEmpty()
		1 * profileMock.isTransient()
		1 * profileMock.getPropertyValue('email')
		1 * profileMock.getPropertyValue('walletId')
	}
	def "fecthing promotions for shipping address and register customer"(){
		given:
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue("walletId") >> "12345"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111" 
		CouponResponseVo resCoupon = new CouponResponseVo()
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("11")
		cListVo.setRedemptionLimit("10")
		cListVo.setExpiryDate(getTodaysdayDateString())
		cListVo.setRedemptionChannel("2")
		cListVo.setEntryCd("CODE")
		resCoupon.setCouponList([cListVo])
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		1*couponManager.getCouponsList("bbb@test.com", "11111111" , "12345", true, false) >> resCoupon
		when:
		Map shipMap = promotionLookUpManagerObj.populateSPCValidPromotions(profileMock,orderMock,"US")
		then:
		shipMap.isEmpty()
	}
	def "fecthing promotions for shipping address and register customer throws error"(){
		given:
		profileMock.isTransient() >> true
		orderMock.getShippingAddress() >> billingAddress
		billingAddress.getEmail() >> "bbb@test.com"
		billingAddress.getPhoneNumber() >> "11111111"
		CouponResponseVo resCoupon = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(true)
		resCoupon.setErrorStatus(eStatus)
		1*couponManager.getCouponsList("bbb@test.com", "11111111" , null , true, false) >> resCoupon
		when:
		Map shipMap = promotionLookUpManagerObj.populateSPCValidPromotions(profileMock,orderMock,"US")
		then:
		shipMap.isEmpty()
	}
	def "fecthing promotions for shipping address and register customer no promotions availabe"(){
		given:
		orderMock.getShippingAddress() >> billingAddress
		profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@test.com"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "11111111"
		CouponResponseVo resCoupon = new CouponResponseVo()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		resCoupon.setErrorStatus(eStatus)
		1*couponManager.getCouponsList("bbb@test.com", "11111111" , null , true, false) >> resCoupon
		when:
		Map shipMap = promotionLookUpManagerObj.populateSPCValidPromotions(profileMock,orderMock,"US")
		then:
		shipMap.isEmpty()
	}
	def "fecthing promotion list based exipry date returns empty"(){
		when:
		Map promoMap = promotionLookUpManagerObj.fillPromoList("BedBathUS",null)
		then:
		promoMap.isEmpty()
	}
	def "fecthing promotion list based exipry date"(){
		given:
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("9")
		cListVo.setRedemptionLimit("10")
		cListVo.setExpiryDate(getTodaysdayDateString())
		cListVo.setRedemptionChannel(null)
		cListVo.setEntryCd("CODE")
		1*catalogToolsMock.getPromotions("CODE") >> null
		when:
		Map promoMap = promotionLookUpManagerObj.fillPromoList("BedBathUS",[cListVo])
		then:
		promoMap.isEmpty()
	}
	def "validating promotion is life long valid"(){
		given:
		RepositoryItemMock itemMock =new RepositoryItemMock()
		Date today = new Date();
		itemMock.setProperties(["endUsable":null])
		when:
		boolean expry = promotionLookUpManagerObj.validPromo(itemMock,"BedBathUS")
		then:
		expry == true
	}
	def "validating promotion will never expiry"(){
		given:
		RepositoryItemMock itemMock =new RepositoryItemMock()
		Date today = new Date();
		itemMock.setProperties(["endUsable":""])
		when:
		boolean expry = promotionLookUpManagerObj.validPromo(itemMock,"BedBathUS")
		then:
		expry == true
	}
	def "validating promotion length is not greater then zero"(){
		given:
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("9")
		cListVo.setRedemptionLimit("10")
		cListVo.setExpiryDate(getTodaysdayDateString())
		cListVo.setRedemptionChannel("2")
		cListVo.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() - (1000 * 60 * 60 * 24))).getTime())])
		List<RepositoryItem> listItems = new ArrayList<RepositoryItem>(0)
		1*catalogToolsMock.getPromotions("CODE") >> listItems.toArray()
		Map restulMap = [:]
		when:
		Map pMap = promotionLookUpManagerObj.fillPromotionList("BedBathUs", restulMap, [cListVo])
		then:
		restulMap.isEmpty()
	}
	def "validating promotion length is not greater then zero and expiry date has nul"(){
		given:
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("9")
		cListVo.setRedemptionLimit("10")
		cListVo.setExpiryDate(null)
		cListVo.setRedemptionChannel("1")
		cListVo.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() - (1000 * 60 * 60 * 24))).getTime())])
		List<RepositoryItem> listItems = new ArrayList<RepositoryItem>(0)
		0*catalogToolsMock.getPromotions("CODE") >> listItems.toArray()
		Map restulMap = [:]
		when:
		Map pMap = promotionLookUpManagerObj.fillPromotionList("BedBathUs", restulMap, [cListVo])
		then:
		restulMap.isEmpty()
	}
	def "validating promotion length is not greater then zero and expiry date has yesterday"(){
		given:
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("9")
		cListVo.setRedemptionLimit("10")
		cListVo.setExpiryDate(getYesterdayDateString())
		cListVo.setRedemptionChannel("1")
		cListVo.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() - (1000 * 60 * 60 * 24))).getTime())])
		List<RepositoryItem> listItems = new ArrayList<RepositoryItem>(0)
		0*catalogToolsMock.getPromotions("CODE") >> listItems.toArray()
		Map restulMap = [:]
		when:
		Map pMap = promotionLookUpManagerObj.fillPromotionList("BedBathUs", restulMap, [cListVo])
		then:
		restulMap.isEmpty()
	}
	def "validating promotion length is  greater then zero and list as null"(){
		given:
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("9")
		cListVo.setRedemptionLimit("10")
		cListVo.setExpiryDate(getTodaysdayDateString())
		cListVo.setRedemptionChannel("2")
		cListVo.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() - (1000 * 60 * 60 * 24))).getTime())])
		List<RepositoryItem> listItems = new ArrayList<RepositoryItem>()
		listItems.add(null)
		1*catalogToolsMock.getPromotions("CODE") >> listItems.toArray()
		Map restulMap = [:]
		when:
		Map pMap = promotionLookUpManagerObj.fillPromotionList("BedBathUs", restulMap, [cListVo])
		then:
		restulMap.isEmpty()
	}
	def "validating promotion is expried with one day"(){
		given:
		promotionLookUpManagerObj = Spy()
		promotionLookUpManagerObj.setCatalogTools(catalogToolsMock)
		CouponListVo cListVo = new CouponListVo()
		cListVo.setRedemptionCount("9")
		cListVo.setRedemptionLimit("10")
		cListVo.setExpiryDate("hgjghjg")
		cListVo.setRedemptionChannel("2")
		cListVo.setEntryCd("CODE")
		RepositoryItemMock itemMock =new RepositoryItemMock()
		Date today = new Date();
		itemMock.setProperties(["endUsable":new Timestamp((new Date(today.getTime() - (1000 * 60 * 60 * 24))).getTime())])
		1*catalogToolsMock.getPromotions("CODE") >> [itemMock].toArray()
		Map restulMap = [:]
		when:
		Map pMap = promotionLookUpManagerObj.fillPromotionList("BedBathUs", restulMap, [cListVo])
		then:
		restulMap.isEmpty()
		1*promotionLookUpManagerObj.logError(_,_)
	}
	private String getYesterdayDateString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return dateFormat.format(cal.getTime());
}
	private String getTodaysdayDateString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return dateFormat.format(cal.getTime());
}
	
}
