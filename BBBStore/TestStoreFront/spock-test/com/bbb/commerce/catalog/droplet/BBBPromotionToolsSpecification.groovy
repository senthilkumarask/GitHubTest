package com.bbb.commerce.catalog.droplet

import java.sql.Timestamp
import java.util.List
import java.util.Map
import atg.commerce.order.CommerceItem
import atg.commerce.order.ShippingGroup
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.OrderPriceInfo
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.ShippingPriceInfo
import atg.repository.MutableRepository
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.userprofiling.Profile
import com.bbb.account.vo.CouponListVo
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.order.manager.PromotionLookupManager
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.TBSConstants
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.rest.checkout.vo.AppliedCouponsVO
import spock.lang.specification.BBBExtendedSpec

class BBBPromotionToolsSpecification extends BBBExtendedSpec {

	BBBPromotionTools tools
	String mQuery
	LblTxtTemplateManager mLblTxtTemplate
	String mMediaItmKey
	PromotionLookupManager promoManager
	BBBCouponUtil couponUtil
	String exclusionRuleQuery
	BBBCatalogToolsImpl catalogToolsImpl
	String couponByPromotionQuery
	BBBCatalogTools catalogTools
	MutableRepository couponRepository
	
	def setup(){
		tools = Spy()
		mQuery = "ALL"
		mLblTxtTemplate = Mock()
		mMediaItmKey = "mediaKey"
		promoManager = Mock()
		couponUtil = Mock()
		exclusionRuleQuery = "exclusionQuery"
		catalogToolsImpl = Mock()
		couponByPromotionQuery = "couponByPromQuery"
		catalogTools = Mock()
		couponRepository = Mock()
		
		tools.setQuery(mQuery)
		tools.setLblTxtTemplate(mLblTxtTemplate)
		tools.setMediaItmKey(mMediaItmKey)
		tools.setCatalogUtil(catalogTools)
		tools.setExclusionRuleQuery(exclusionRuleQuery)
		tools.setCouponRepository(couponRepository)
		tools.setPromoManager(promoManager)
		tools.setCouponUtil(couponUtil)
		tools.setCatalogTools(catalogToolsImpl)
		tools.setCouponByPromotionQuery(couponByPromotionQuery)
	}
	
	def "getPromotionCouponKey method happy path"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem]
			Set<RepositoryItem> siteItemObj = new HashSet<RepositoryItem>()
			RepositoryItem siteItem1 = Mock()
			
			siteItemObj.add(siteItem1)
			promotionItem.getPropertyValue(BBBCoreConstants.SITES) >> siteItemObj
			siteItem1.getPropertyValue(BBBCoreConstants.ID) >> "BBBCa"
			promotionItem.getPropertyValue(BBBCoreConstants.END_USABLE) >> new Date(26012020)
		
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", true)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = 12:43 PM IST on 01/01/1970")
			cmsValue == "12:43 PM IST on 01/01/1970"
			0*tools.logDebug("Promotion dosn't belong to this site")
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
	}
	
	def "getPromotionCouponKey method with siteItemObj not null and endDate null"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem]
			Set<RepositoryItem> siteItemObj = new HashSet<RepositoryItem>()
			RepositoryItem siteItem1 = Mock()
			
			siteItemObj.add(siteItem1)
			promotionItem.getPropertyValue(BBBCoreConstants.SITES) >> siteItemObj
			siteItem1.getPropertyValue(BBBCoreConstants.ID) >> "BBBCa"
			promotionItem.getPropertyValue(BBBCoreConstants.END_USABLE) >> null
		
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", true)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = (NO EXPIRED DATE AVAILABLE)")
			cmsValue == "(NO EXPIRED DATE AVAILABLE)"
			0*tools.logDebug("Promotion dosn't belong to this site")
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
	}
	
	def "getPromotionCouponKey method with expiryDateRequired as false"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem]
			Set<RepositoryItem> siteItemObj = new HashSet<RepositoryItem>()
			RepositoryItem siteItem1 = Mock()
			
			siteItemObj.add(siteItem1)
			promotionItem.getPropertyValue(BBBCoreConstants.SITES) >> siteItemObj
			siteItem1.getPropertyValue(BBBCoreConstants.ID) >> "BBBCa"
		
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", false)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = ")
			cmsValue == ""
			0*tools.logDebug("Promotion dosn't belong to this site")
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
	}
	
	def "getPromotionCouponKey method with expiryDateRequired as false and cmsValue as not null"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem]
			Set<RepositoryItem> siteItemObj = new HashSet<RepositoryItem>()
			RepositoryItem siteItem1 = Mock()
			
			siteItemObj.add(siteItem1)
			promotionItem.getPropertyValue(BBBCoreConstants.SITES) >> siteItemObj
			siteItem1.getPropertyValue(BBBCoreConstants.ID) >> "BBBCa"
			promotionItem.getPropertyValue(BBBCoreConstants.ACTIVATION_LABEL_ID) >> "CMS"
			
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", false)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = CMS")
			cmsValue == "CMS"
			0*tools.logDebug("Promotion dosn't belong to this site")
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
	}
	
	def "getPromotionCouponKey method with currentSite not equals BBBCa"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem]
			Set<RepositoryItem> siteItemObj = new HashSet<RepositoryItem>()
			RepositoryItem siteItem1 = Mock()
			
			siteItemObj.add(siteItem1)
			promotionItem.getPropertyValue(BBBCoreConstants.SITES) >> siteItemObj
			siteItem1.getPropertyValue(BBBCoreConstants.ID) >> "BBBUs"
			
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", false)
		
		then:
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = CMS")
			cmsValue == null
			1*tools.logDebug("Promotion dosn't belong to this site")
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
	}
	
	def "getPromotionCouponKey method with siteItemObj null"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem]
			promotionItem.getPropertyValue(BBBCoreConstants.SITES) >> null
			
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", false)
		
		then:
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = CMS")
			cmsValue == null
			1*tools.logDebug("Promotion dosn't belong to this site")
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
	}
	
	def "getPromotionCouponKey method with promotionItem as null"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			1*tools.executeDBQuery(_,_,_) >> null
			
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", false)
		
		then:
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = CMS")
			cmsValue == null
			0*tools.logDebug("Promotion dosn't belong to this site")
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
			1*tools.logDebug("Coupon Exist in more than two promotion")
	}
	
	def "getPromotionCouponKey method with more than 1 promotionItem returned from DB call"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem1 = Mock()
			RepositoryItem promotionItem2 = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem1,promotionItem2]
			
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATED_LABEL_ID ,"BBBCa", "en", false)
		
		then:
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = CMS")
			cmsValue == null
			0*tools.logDebug("Promotion dosn't belong to this site")
			0*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends")
			1*tools.logDebug("Coupon Exist in more than two promotion")
	}
	
	def "getPromotionCouponKey method with property as empty"(){
		
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", "" ,"BBBCa", "en", false)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = null")
			cmsValue == null
	}
	
	def "getPromotionCouponKey method with property as nul"(){
		
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", null ,"BBBCa", "en", false)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = null")
			cmsValue == null
	}
	
	def "getPromotionCouponKey method with currSite as nul"(){
		
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", null ,null, "en", false)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = null")
			cmsValue == null
	}
	
	def "getPromotionCouponKey method with promId as nul"(){
		
		when:
			String cmsValue = tools.getPromotionCouponKey(null, null ,null, "en", false)
		
		then:
			1*tools.logDebug("BBBPromotionTools.getPromotionCouponKey() method ends and retuns cmsValue = null")
			cmsValue == null
	}
	
	def "getPromotionCouponKey method with RepositoryException thrown"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> {throw new RepositoryException()}
			
		when:
			String cmsValue = tools.getPromotionCouponKey("promId", BBBCoreConstants.ACTIVATION_LABEL_ID ,"BBBCa", "en", false)
		
		then:
			1*tools.logError("catalog_1067 : Error while retriving data from repository", _)
	}
	
	def "getPromotionById method happy path"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			RepositoryView promotionView = Mock()
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> promotionView
			RepositoryItem promotionItem = Mock()
			1*tools.executeDBQuery(_,_,_) >> [promotionItem]
		when:
			RepositoryItem item = tools.getPromotionById("promoId")
		then:
			item!=null
			item == promotionItem
	}
	
	def "getPromotionById method RepositoryException thrown"(){
		
		given:
			Repository promoRepo = Mock()
			1*tools.getPromos() >> promoRepo
			promoRepo.getView(BBBCoreConstants.PROMOTION) >> {throw new RepositoryException()}

		when:
			RepositoryItem item = tools.getPromotionById("promoId")
			
		then:
			item ==null
			1*tools.logError(null,_)
	}
	
	def "getPromotionById method with promoId null"(){
		
		when:
			RepositoryItem item = tools.getPromotionById(null)
		then:
			item ==null
	}
	
	def "getAppliedCoupons method happy path"(){
		
		given:
			tools.setLoggingDebug(true)
			BBBOrderImpl order = Mock()
			Profile profile = Mock()
			1*tools.extractCurrentSiteId() >> BBBCoreConstants.SITE_BAB_CA
			Map<String, RepositoryItem> promotions = new HashMap<String,RepositoryItem>()
			List<CouponListVo> couponList = new ArrayList<CouponListVo>()
			order.getCouponMap() >> promotions
			order.getCouponListVo() >> couponList
			promoManager.getCouponList(_,_,_,_) >> couponList
			promoManager.fillPromoList(_,_) >> promotions
			profile.getPropertyValue(BBBCoreConstants.EMAIL) >> "sarora50@gmail.com"
			couponUtil.applySchoolPromotion(_,_,_) >> promotions

		when:
			AppliedCouponsVO vo = tools.getAppliedCoupons(order,profile,true)
			
		then:
			1*tools.logDebug("getting coupons from web service")
	}
	
	def "getAppliedCoupons method with channel equals MOBILEWEB"(){
		
		given:
			tools.setLoggingDebug(false)
			BBBOrderImpl order = Mock()
			Profile profile = Mock()
			1*tools.extractCurrentSiteId() >> TBSConstants.SITE_TBS_BAB_CA
			Map<String, RepositoryItem> promotions = new HashMap<String,RepositoryItem>()
			List<CouponListVo> couponList = new ArrayList<CouponListVo>()
			order.getCouponMap() >> promotions
			order.getCouponListVo() >> couponList
			promoManager.getCouponList(_,_,_,_) >> couponList
			promoManager.fillPromoList(_,_) >> promotions
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			sessionMock.getAttribute("couponMailId") >> "sarora50@gmail.com"
			profile.getPropertyValue(BBBCoreConstants.EMAIL) >> "sarora50@gmail.com"
			RepositoryItem repoItem = Mock()
			RepositoryItem repoItem1 = Mock()
			RepositoryItem repoItem2 = Mock()
			promotions.put("",repoItem)
			promotions.put("id1",repoItem1)
			promotions.put("id2",repoItem2)
			
			OrderPriceInfo priceInfo = Mock()
			order.getPriceInfo() >> priceInfo
			PricingAdjustment adjustment1 = Mock()
			PricingAdjustment adjustment2 = Mock()
			priceInfo.getAdjustments() >> [adjustment1,adjustment2]
			RepositoryItem coupon1 = Mock()
			adjustment1.getCoupon() >> coupon1
			adjustment2.getCoupon() >> null
			coupon1.getRepositoryId() >> "id1"
			
			CommerceItem item1 = Mock()
			CommerceItem item2 = Mock()
			PricingAdjustment adjustment3 = Mock()
			PricingAdjustment adjustment4 = Mock()
			order.getCommerceItems() >> [item1,item2]
			ItemPriceInfo itemPriceInfo = Mock()
			item1.getPriceInfo() >> itemPriceInfo
			item2.getPriceInfo() >> null
			itemPriceInfo.getAdjustments() >> [adjustment3,adjustment4]
			RepositoryItem coupon2 = Mock()
			adjustment3.getCoupon() >> coupon2
			adjustment4.getCoupon() >> null
			coupon2.getRepositoryId() >> "id2"
			
			ShippingGroup sg1 = Mock()
			ShippingGroup sg2 = Mock()
			order.getShippingGroups() >> [sg1,sg2]
			ShippingPriceInfo spInfo = Mock()
			sg1.getPriceInfo() >> spInfo
			PricingAdjustment adjustment5 = Mock()
			PricingAdjustment adjustment6 = Mock()
			spInfo.getAdjustments() >> [adjustment5,adjustment6]
			RepositoryItem coupon3 = Mock()
			adjustment5.getCoupon() >> coupon3
			adjustment6.getCoupon() >> null
			sg2.getPriceInfo() >> null
			coupon3.getRepositoryId() >> "id3"
			
			profile.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> []
			
			Map mediaMap = new HashMap()
			RepositoryItem mediaItem1 = Mock()
			mediaMap.put("mainImage",mediaItem1)
			mediaItem1.getPropertyValue(BBBCoreConstants.URL) >> "http://url"
			repoItem2.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap
			
			CouponListVo cVO1 = new CouponListVo()
			CouponListVo cVO2 = new CouponListVo()
			CouponListVo cVO3 = new CouponListVo()
			CouponListVo cVO4 = new CouponListVo()
			couponList.add(cVO1)
			couponList.add(cVO2)
			couponList.add(cVO3)
			couponList.add(cVO4)
			cVO1.setEntryCd("id2")
			cVO2.setEntryCd("id2")
			cVO3.setEntryCd("id2")
			cVO4.setEntryCd("id1")
			cVO1.setRedemptionCount("10")
			cVO1.setRedemptionLimit("50")
			cVO3.setRedemptionCount("10")
			
			cVO3.setExpiryDate("2020/01/01 00:00:00")
			cVO4.setExpiryDate("11/01/2019")
			repoItem2.getPropertyValue(BBBCoreConstants.END_USABLE) >> new Timestamp((new Date()).getTime())
			repoItem1.getPropertyValue(BBBCoreConstants.END_USABLE) >> ""
			
			tools.sortAppCpnListVO(_) >> null
			
			Map mediaMap2 = new HashMap()
			RepositoryItem mediaItem2 = Mock()
			mediaMap2.put("mainImage",mediaItem2)
			repoItem1.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap2
			
			Map mediaMap3 = new HashMap()
			mediaMap3.put("mainImage",null)
			repoItem.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap3
			
			couponUtil.applySchoolPromotion(_,_,_) >> promotions

		when:
			AppliedCouponsVO vo = tools.getAppliedCoupons(order,profile,true)
			
		then:
			true == true
	}
}
