package com.bbb.commerce.order.droplet

import atg.commerce.CommerceException
import atg.commerce.claimable.ClaimableManager
import atg.commerce.claimable.ClaimableTools
import atg.commerce.order.OrderManager
import atg.commerce.pricing.PricingTools
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository
import atg.repository.RepositoryItem
import atg.userprofiling.Profile

import com.bbb.account.vo.CouponListVo
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.droplet.BBBPromotionTools
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.order.manager.PromotionLookupManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.rest.checkout.vo.AppliedCouponListVO
import spock.lang.specification.BBBExtendedSpec

import java.text.DateFormat
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession
import javax.transaction.TransactionManager

class UserCouponWalletDropletSpecification extends BBBExtendedSpec {
	def UserCouponWalletDroplet ucwDroplet
	
	def Profile profileMock = Mock()
	def BBBOrderImpl orderMock =  Mock()
	def BBBPromotionTools promotionToolsMock = Mock()
	def PricingTools pricingToolsMock =Mock()
	def TransactionManager traManagerMock = Mock()
	def RepositoryItem coupon = Mock()
	def RepositoryItem coupon1 = Mock()
	def MutableRepository mRepository = Mock()
	
	def RepositoryItem pCoupon = Mock()
	def RepositoryItem pCoupon1 = Mock()
	
	def RepositoryItem selectedCoupon = Mock()
	def RepositoryItem pPromotion1 = Mock()
	
	def RepositoryItem mediaItem = Mock()
	def OrderManager orderManager = Mock()
	def BBBCatalogTools catalogToolsMock = Mock() 
	def LblTxtTemplateManager txtManager = Mock()
	
	def RepositoryItem pPromotion = Mock()
	
	def PromotionLookupManager proManager = Mock()
	def ClaimableManager claimManagerMock = Mock()
	def ClaimableTools claimableToolsMock = Mock()
	
	
	CouponListVo listVO = new CouponListVo()
	CouponListVo listVO1 = new CouponListVo()
	CouponListVo listVO2 = new CouponListVo()
	CouponListVo listVO3 = new CouponListVo()
	CouponListVo listVO4 = new CouponListVo()
	CouponListVo listVO5 = new CouponListVo()
	
	
	def BBBCouponUtil couponUtilMock = Mock()
	
	def setup (){
	 	//ucwDroplet1 = new UserCouponWalletDroplet(promTools : promotionToolsMock,couponUtil : couponUtilMock,promoManger : proManager)
		ucwDroplet = Spy()
		 ucwDroplet.setPromTools(promotionToolsMock)
		 ucwDroplet.setCouponUtil(couponUtilMock)
		 ucwDroplet.setPromoManger(proManager)
		 ucwDroplet.setCatalogTools(catalogToolsMock)
		 ucwDroplet.setExclusionRuleQuery("")
		 ucwDroplet.setCouponRepository(mRepository)
		 ucwDroplet.setLblTxtTemplateManager(txtManager)

	}
	
	def"service . TC to check online coupone list "(){
		given:
		    def Set s1 = [pPromotion]
			def Set s2 = [pPromotion1]
			def Set s3 = []
		    String siteId = "BedBathCanada"
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2005-04-06 09:01:10")
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("siteId") >> siteId
			requestMock.getObjectParameter("cartCheck") >> "cartCheck"
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			1*pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> "mail@fj.com"
			profileMock.isTransient() >> true
			orderMock.getCouponMap() >> ["cou1" : coupon]
			1*orderMock.getCouponListVo() >> [listVO,listVO1,listVO2,listVO3,listVO4, listVO5]
			1*couponUtilMock.applySchoolPromotion(["cou1" : coupon], profileMock, orderMock) >> ["schoolPromo" : coupon ,"schoolPromo1" : coupon1]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon, null, pCoupon1,pCoupon,pCoupon,pCoupon,pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "BedBathCanada"
			
			1*coupon.getPropertyValue("displayName") >> "itemfree"
			1*coupon.getPropertyValue("description") >> "ogof"
			1*coupon.getPropertyValue("endUsable") >> ts2
			1*coupon1.getPropertyValue("displayName") >> "itemfree"
			1*coupon1.getPropertyValue("description") >> "ogof"
			1*coupon1.getPropertyValue("endUsable") >> ""
	
			
			listVO.setEntryCd("schoolPromo")
			listVO.setRedemptionChannel("redimeChanel")  
			listVO.setUniqueCouponCd("uniqueCoupon")   
			listVO.setExpiryDate("2012/07/10 14:58:00.000000") 
			listVO.setRedemptionCount("2")
			listVO.setRedemptionLimit("3")
			listVO.setLastRedemptionDate("01/03/2015")
			
			listVO1.setEntryCd("schoolPromo")
			listVO1.setRedemptionChannel("redimeChanel")
			listVO1.setUniqueCouponCd("uniqueCoupon")
			listVO1.setExpiryDate("2012/07/10 14:58:00.000000") 
			listVO1.setRedemptionCount("2")
			listVO1.setRedemptionLimit("")
			listVO1.setLastRedemptionDate("01/03/2015")
			
			listVO2.setEntryCd("schoolPromo")
			listVO2.setRedemptionChannel("redimeChanel")
			listVO2.setUniqueCouponCd("uniqueCoupon")
			listVO2.setExpiryDate("2012/07/10 14:58:00.000000") 
			listVO2.setRedemptionCount("2")
			listVO2.setRedemptionLimit(null)
			listVO2.setLastRedemptionDate("01/03/2015")
	
			
			listVO3.setEntryCd("schoolPromo")
			listVO3.setRedemptionChannel("redimeChanel")
			listVO3.setUniqueCouponCd("uniqueCoupon")
			listVO3.setExpiryDate("2012/07/10 14:58:00.000000") 
			listVO3.setRedemptionCount("")
			listVO3.setRedemptionLimit("3")
			listVO3.setLastRedemptionDate("01/03/2015")
			
			listVO4.setEntryCd("schoolPromo")
			listVO4.setRedemptionChannel("redimeChanel")
			listVO4.setUniqueCouponCd("uniqueCoupon")
			listVO4.setExpiryDate("2012/07/10 14:58:00.000000")  
			listVO4.setRedemptionCount(null)
			listVO4.setRedemptionLimit("3")
			listVO4.setLastRedemptionDate("01/03/2015")
            
			listVO5.setEntryCd("Promo")
			


			
			12*profileMock.getPropertyValue("schoolPromotions") >> "schoolPromo" >> "schoolPromo" >> "schoolPromo" >> "schoolPromo" >> "schoolPromo" >> null >> "schoolPromo"
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			
			proManager.getCouponUtil() >> couponUtilMock
			couponUtilMock.getClaimableManager() >> claimManagerMock
			claimManagerMock.getClaimableTools() >> claimableToolsMock
			4*claimableToolsMock.getPromotionsPropertyName() >> "promotion"
			4*pCoupon.getPropertyValue("promotion") >>  s1 >> s2 >> s3 >> null
			1*pPromotion.getRepositoryId() >> "schoolPromo"
			1*pPromotion1.getRepositoryId() >> "select"
			
			pCoupon.getRepositoryId() >> "profieCoupon"
			
			1*promotionToolsMock.fetchExclusionText("schoolPromo", siteId) >> "exclusive"
			coupon.getPropertyValue("id") >> "coup1"
			1*coupon.getPropertyValue("media") >> ["mainImage" : mediaItem]
			1*coupon1.getPropertyValue("media") >> ["mainImage" : mediaItem]
			
			3*mediaItem.getPropertyValue( "URL") >> "url" >> null
			orderMock.getSchoolCoupon() >>"co"
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			1*pricingToolsMock.getOrderManager() >> orderManager
			
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", []);
		1*requestMock.setParameter("useAnywhereCouponList", []);
        1*requestMock.serviceParameter("output", requestMock, responseMock);
        1*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
		1*orderManager.updateOrder(orderMock)
	}
	
	def"service . TC when site id is BedBathCanada  "(){
		given:
			String siteId = "BedBathCanada"
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2023-04-06 09:01:10")
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("siteId") >> siteId
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			requestMock.getSession() >> sessionMock
			sessionMock.getAttribute("couponMailId")>> ""
			profileMock.isTransient() >> true
			proManager.getCouponList(profileMock, orderMock, _, true) >> [listVO,listVO1]
			
			orderMock.getCouponMap() >> ["cou1" : coupon]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> ["schoolPromo" : coupon ,"schoolPromo1" : coupon1]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "TBS_BedBathCanada"
			
			coupon.getPropertyValue("displayName") >> "itemfree"
			coupon.getPropertyValue("description") >> "ogof"
			coupon.getPropertyValue("endUsable") >> ts2
			coupon1.getPropertyValue("displayName") >> "itemfree"
			coupon1.getPropertyValue("description") >> "ogof"
			coupon1.getPropertyValue("endUsable") >> null
	
			
			listVO.setEntryCd("schoolPromo")
			listVO.setRedemptionChannel("2")
			listVO.setUniqueCouponCd("uniqueCoupon")
			listVO.setExpiryDate("2022/07/10 14:58:00.000000")
			listVO.setRedemptionCount("2")
			listVO.setRedemptionLimit("3")
			listVO.setLastRedemptionDate("01/03/2015")
			
			listVO1.setEntryCd("schoolPromo1")
			listVO1.setExpiryDate("2022/07/10 14:58:00.000000")
			
			
			profileMock.getPropertyValue("schoolPromotions") >> null
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			
		
			promotionToolsMock.fetchExclusionText("schoolPromo", siteId) >> "exclusive"
			coupon.getPropertyValue("id") >> "coup1"
			coupon.getPropertyValue("media") >> ["mainImage" : null]
			coupon1.getPropertyValue("media")  >> null
			
			orderMock.getSchoolCoupon() >> "co"
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getOrderManager() >> orderManager
			orderManager.updateOrder(orderMock) >> {throw new CommerceException("exception")}
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", _);
		1*requestMock.setParameter("useAnywhereCouponList", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.serviceParameter("outputEnd", requestMock, responseMock)
		1*requestMock.setParameter("errorMsg","1000012")
	}
	
	def"service . TC when site id is BedBathCana "(){
		given:
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2023-04-06 09:01:10")
			
		    requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			requestMock.getSession() >> sessionMock
			sessionMock.getAttribute("couponMailId")>> ""
			profileMock.isTransient() >> false
			proManager.getCouponList(profileMock, orderMock, _, true) >> [listVO,listVO1]
			
			orderMock.getCouponMap() >> ["cou1" : coupon]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> ["schoolPromo" : coupon]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "us"
			
			coupon.getPropertyValue("displayName") >> "itemfree"
			coupon.getPropertyValue("description") >> "ogof"
			coupon.getPropertyValue("endUsable") >> ""
	
			
			listVO.setEntryCd("schoolPromo")
			listVO.setRedemptionChannel("1")
			listVO.setUniqueCouponCd("uniqueCoupon")
			listVO.setExpiryDate("")
			listVO.setRedemptionCount("2")
			listVO.setRedemptionLimit("3")
			listVO.setLastRedemptionDate("01/03/2015")
			
			listVO1.setEntryCd("schoolPromo12")
			
			
			profileMock.getPropertyValue("schoolPromotions") >> null
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			coupon.getPropertyValue("id") >> "coup1"
			
			orderMock.getSchoolCoupon() >> ""
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getOrderManager() >> orderManager
			orderManager.updateOrder(orderMock) >> {}
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", _);
		1*requestMock.setParameter("useAnywhereCouponList", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
	def"service . TC when wsExpirationDate is not  null "(){
		given:
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2023-04-06 09:01:10")
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> ""
			profileMock.isTransient() >> true
			proManager.getCouponList(profileMock, orderMock, _, true) >> [listVO,listVO1]
			
			orderMock.getCouponMap() >> [:]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> ["schoolPromo" : coupon]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "us"
			
			coupon.getPropertyValue("displayName") >> "itemfree"
			coupon.getPropertyValue("description") >> "ogof"
			coupon.getPropertyValue("endUsable") >> ""
	
			
			listVO.setEntryCd("schoolPromo")
			listVO.setRedemptionChannel("3")
			listVO.setUniqueCouponCd("uniqueCoupon")
			listVO.setExpiryDate("2022/07/10 14:58:00.000000")
			listVO.setRedemptionCount("2")
			listVO.setRedemptionLimit("3")
			listVO.setLastRedemptionDate("null")
			
			listVO1.setEntryCd("schoolPromo12")
			
			
			profileMock.getPropertyValue("schoolPromotions") >> null
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			coupon.getPropertyValue("id") >> "coup1"
			
			orderMock.getSchoolCoupon() >> ""
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getOrderManager() >> orderManager
			orderManager.updateOrder(orderMock) >> {}
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", _);
		1*requestMock.setParameter("useAnywhereCouponList", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
	def"service . TC when wsExpirationDate is null "(){
		given:
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2023-04-06 09:01:10")
			
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> ""
			profileMock.isTransient() >> true
			proManager.getCouponList(profileMock, orderMock, _, true) >> [listVO,listVO1]
			
			orderMock.getCouponMap() >> [:]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> ["schoolPromo" : coupon]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "us"
			
			coupon.getPropertyValue("displayName") >> "itemfree"
			coupon.getPropertyValue("description") >> "ogof"
			coupon.getPropertyValue("endUsable") >> ts2
	
			
			listVO.setEntryCd("schoolPromo")
			listVO.setRedemptionChannel("5")
			listVO.setUniqueCouponCd("uniqueCoupon")
			listVO.setExpiryDate("")
			listVO.setRedemptionCount("2")
			listVO.setRedemptionLimit("3")
			listVO.setLastRedemptionDate("01/03/2015 00:00:00")
			
			listVO1.setEntryCd("schoolPromo12")
			
			
			profileMock.getPropertyValue("schoolPromotions") >> null
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			coupon.getPropertyValue("id") >> "coup1"
			
			orderMock.getSchoolCoupon() >> ""
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getOrderManager() >> orderManager
			orderManager.updateOrder(orderMock) >> {}
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", _);
		1*requestMock.setParameter("useAnywhereCouponList", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
	def"service . TC when wsExpirationDate and endUsableDate  are same "(){
		given:
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2022-07-10 00:00:00")
			
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> ""
			profileMock.isTransient() >> true
			proManager.getCouponList(profileMock, orderMock, _, true) >> [listVO,listVO1]
			
			orderMock.getCouponMap() >> [:]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> ["schoolPromo" : coupon]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "us"
			
			coupon.getPropertyValue("displayName") >> "itemfree"
			coupon.getPropertyValue("description") >> "ogof"
			coupon.getPropertyValue("endUsable") >> ts2
	
			
			listVO.setEntryCd("schoolPromo")
			listVO.setRedemptionChannel("5")
			listVO.setUniqueCouponCd("uniqueCoupon")
			listVO.setExpiryDate("2022/07/10 00:00:00.000000")
			listVO.setRedemptionCount("3")
			listVO.setRedemptionLimit("2")
			listVO.setLastRedemptionDate("")
			
			listVO1.setEntryCd("schoolPromo12")
			
			
			profileMock.getPropertyValue("schoolPromotions") >> null
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			coupon.getPropertyValue("id") >> "coup1"
			
			orderMock.getSchoolCoupon() >> ""
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getOrderManager() >> orderManager
			orderManager.updateOrder(orderMock) >> {}
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", _);
		1*requestMock.setParameter("useAnywhereCouponList", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
	def"service . TC when list is empty "(){
		given:
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2022-07-10 00:00:00")
			
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> "co"
			profileMock.isTransient() >> true
			proManager.getCouponList(profileMock, orderMock, _, true) >> []
			
			orderMock.getCouponMap() >> [:]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> ["schoolPromo" : coupon]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "us"
			
			coupon.getPropertyValue("displayName") >> "itemfree"
			coupon.getPropertyValue("description") >> "ogof"
			coupon.getPropertyValue("endUsable") >> ts2
	
		
			profileMock.getPropertyValue("schoolPromotions") >> null
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			coupon.getPropertyValue("id") >> "coup1"
			
			orderMock.getSchoolCoupon() >> ""
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getOrderManager() >> orderManager
			orderManager.updateOrder(orderMock) >> {}
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", _);
		1*requestMock.setParameter("useAnywhereCouponList", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
	def"service . TC  TC when list is null "(){
		given:
			java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2022-07-10 00:00:00")
			
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> ""
			profileMock.isTransient() >> true
			proManager.getCouponList(profileMock, orderMock, _, true) >> null
			
			orderMock.getCouponMap() >> [:]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> ["schoolPromo" : coupon]
			
			1*promotionToolsMock.getCouponListFromOrder(orderMock) >> [pCoupon]
			//process coupon start
			1*profileMock.getPropertyValue("selectedPromotionsList") >> [selectedCoupon]
			ucwDroplet.getSiteId() >> "us"
			
			coupon.getPropertyValue("displayName") >> "itemfree"
			coupon.getPropertyValue("description") >> "ogof"
			coupon.getPropertyValue("endUsable") >> ts2
	
			profileMock.getPropertyValue("schoolPromotions") >> null
			pCoupon.getRepositoryId() >> "cou"
			pCoupon1.getRepositoryId() >> "schoolPromo"
			coupon.getPropertyValue("id") >> "coup1"
			
			orderMock.getSchoolCoupon() >> ""
			
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getOrderManager() >> orderManager
			orderManager.updateOrder(orderMock) >> {}
			
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("onlineCouponList", _);
		1*requestMock.setParameter("useAnywhereCouponList", _);
		1*requestMock.serviceParameter("output", requestMock, responseMock);
		1*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
	
	def"service . TC  promotion list is empty "(){
		given:
			
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> "co"
			profileMock.isTransient() >> false
			proManager.getCouponList(profileMock, orderMock, _, true) >> null
			
			orderMock.getCouponMap() >> [:]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> [:]
			1*profileMock.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> []
			ucwDroplet.getSiteId() >> "us"
		
		when:
			ucwDroplet.service(requestMock, responseMock)
			then:
			0*requestMock.setParameter("onlineCouponList", _);
			0*requestMock.setParameter("useAnywhereCouponList", _);
			1*requestMock.serviceParameter("empty", requestMock, responseMock);
			0*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
	def"service . TC order is null "(){
		given:
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> null
			
		
		when:
			ucwDroplet.service(requestMock, responseMock)
			then:
	        requestMock.setParameter("errorMsg","Profile Null")	
			
	}
	
	def"service . TC when profile is null "(){
		given:
			
			requestMock.getObjectParameter("profile") >> null
			requestMock.getObjectParameter("order") >> null
			
		
		when:
			ucwDroplet.service(requestMock, responseMock)
			then:
			requestMock.setParameter("errorMsg","Profile Null")
			
	}
	
	def"service . TC  promotion list is null "(){
		given:
			
			
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> "co"
			profileMock.isTransient() >> ""
			proManager.getCouponList(profileMock, orderMock, _, true) >> null
			
			orderMock.getCouponMap() >> [:]
			1*couponUtilMock.applySchoolPromotion(null, profileMock, orderMock) >> null
			
		
		when:
		ucwDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("onlineCouponList", _);
		0*requestMock.setParameter("useAnywhereCouponList", _);
		0*requestMock.serviceParameter("empty", requestMock, responseMock);
		0*requestMock.serviceParameter("outputEnd", requestMock, responseMock);
	}
	
/************************************ exception Scenario *******************************/
	def"service . TC  for BBBBusinessException "(){
		given:
			
			ucwDroplet.setPromTools(promotionToolsMock)
			ucwDroplet.setCouponUtil(couponUtilMock)
			ucwDroplet.setPromoManger(proManager)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> "co"
			profileMock.isTransient() >> false
			proManager.getCouponList(profileMock, orderMock, _, true) >> {throw new BBBBusinessException("exception")}
			orderMock.getCouponMap() >> [:]
			
		
		when:
			ucwDroplet.service(requestMock, responseMock)
			then:
		    1*requestMock.setParameter("errorMsg","2009")
	}
	
	def"service . TC  for BBBSystemException "(){
		given:
			
			ucwDroplet.setPromTools(promotionToolsMock)
			ucwDroplet.setCouponUtil(couponUtilMock)
			ucwDroplet.setPromoManger(proManager)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			requestMock.getObjectParameter("cartCheck") >> ""
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> traManagerMock
			
			sessionMock.getAttribute("couponMailId")>> "co"
			profileMock.isTransient() >> false
			proManager.getCouponList(profileMock, orderMock, _, true) >> {throw new BBBSystemException("exception")}
			orderMock.getCouponMap() >> [:]
			
		
		when:
			ucwDroplet.service(requestMock, responseMock)
			then:
			1*requestMock.setParameter("errorMsg","2009")
	}
	
	def"service . TC  for TransactionDemarcationException "(){
		given:
			
			ucwDroplet.setPromTools(promotionToolsMock)
			ucwDroplet.setCouponUtil(couponUtilMock)
			ucwDroplet.setPromoManger(proManager)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getObjectParameter("order") >> orderMock
			promotionToolsMock.getPricingTools() >> pricingToolsMock
			pricingToolsMock.getTransactionManager() >> null
			
			sessionMock.getAttribute("couponMailId")>> "co"
			profileMock.isTransient() >> false
			
		
		when:
			ucwDroplet.service(requestMock, responseMock)
			then:
			1*requestMock.setParameter("errorMsg","100001")
	}
	
	def "changeDateFormat."(){
		given:
		ucwDroplet = new UserCouponWalletDroplet(promTools : promotionToolsMock,couponUtil : couponUtilMock,promoManger : proManager)
		AppliedCouponListVO vo = new AppliedCouponListVO()
		DateFormat displayFormat =  new SimpleDateFormat("dd/MM/yyyy")
		DateFormat wsFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
		vo.setExpiryDate(null)
		when:
		AppliedCouponListVO value = ucwDroplet.changeDateFormat(vo, displayFormat, wsFormatter)
		then:
		value != null
		
	}	
	
}
