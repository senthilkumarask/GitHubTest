package com.bbb.commerce.catalog.droplet

import java.util.Map

import atg.repository.RepositoryItem

import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter
import com.bbb.selfservice.droplet.ProcessCouponDroplet

import spock.lang.specification.BBBExtendedSpec

class GetPromotionDetailsDropletSpecification extends BBBExtendedSpec {

	GetPromotionDetailsDroplet droplet
	ProcessCouponDroplet processCouponDroplet
	String language
	BBBPromotionTools mPromTools
	
	def setup(){
		droplet = Spy()
		processCouponDroplet = Mock()
		language = "en"
		mPromTools = Mock()
		
		droplet.setProcessCouponDroplet(processCouponDroplet)
		droplet.setLanguage(language)
		droplet.setPromTools(mPromTools)
	}
	
	def "service method happy path"(){
		given:
			requestMock.getLocalParameter(BBBCoreConstants.PROMOTION_ID) >> "promoId"
			requestMock.getLocalParameter(BBBCoreConstants.COUPON_ID) >> "couponId"
			RepositoryItem promotion = Mock()
			1*mPromTools.getPromotionById(_) >> promotion
			Map mediaMap = new HashMap()
			promotion.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap
			RepositoryItem mainImgItem = Mock()
			RepositoryItem lrgImgItem = Mock()
			mediaMap.put("mainImage", mainImgItem)
			mediaMap.put("large", lrgImgItem)
			mainImgItem.getPropertyValue(BBBCoreConstants.URL) >> "mainImgUrl"
			lrgImgItem.getPropertyValue(BBBCoreConstants.URL) >> "lrgImgUrl"
			promotion.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "promoDescription"
			requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> null
			mPromTools.fetchExclusionText(_,_) >> "tAndc"
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logDebug("Input Paramter SchoolId :promoId")
			1*droplet.logDebug("Input Paramter COUPON_ID :couponId")
			0*droplet.logDebug("Promotion dosn't belong to this site")
			1*requestMock.setParameter("mainImgUrl", "mainImgUrl")
			1*requestMock.setParameter("lrgImgUrl", "lrgImgUrl")
			1*requestMock.setParameter("promoDesription", _);
			1*requestMock.setParameter("tAndc", "tAndc");
			1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock);
	}
	
	def "service method with prmotionId,couponId is null and BBBSystemException is thrown"(){
		given:
			requestMock.getLocalParameter(BBBCoreConstants.PROMOTION_ID) >> null
			requestMock.getLocalParameter(BBBCoreConstants.COUPON_ID) >> null
			RepositoryItem promotion = Mock()
			1*mPromTools.getPromotionById(_) >> promotion
			Map mediaMap = new HashMap()
			promotion.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap
			mediaMap.put("mainImage", null)
			mediaMap.put("large", null)
			promotion.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "promoDescription"
			1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "BBBCanada"
			1*mPromTools.fetchExclusionText(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logDebug("promotionId Id is Null")
			1*droplet.logDebug("couponId Id is Null")
			0*droplet.logDebug("Promotion dosn't belong to this site")
			0*requestMock.setParameter("mainImgUrl", "mainImgUrl")
			0*requestMock.setParameter("lrgImgUrl", "lrgImgUrl")
			1*requestMock.setParameter("promoDesription", _)
			1*requestMock.setParameter("tAndc", _)
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "err_promotion_details_error" , BBBCoreErrorConstants.ACCOUNT_ERROR_1118),_)
			1*requestMock.setParameter("systemerror", "err_mycoupons_system_error")
			2*requestMock.serviceParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with mediaMap as null"(){
		given:
			requestMock.getLocalParameter(BBBCoreConstants.PROMOTION_ID) >> null
			requestMock.getLocalParameter(BBBCoreConstants.COUPON_ID) >> null
			RepositoryItem promotion = Mock()
			1*mPromTools.getPromotionById(_) >> promotion
			promotion.getPropertyValue(BBBCoreConstants.MEDIA) >> null
			promotion.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "promoDescription"
			1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "BBBCanada"
			1*mPromTools.fetchExclusionText(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logDebug("promotionId Id is Null")
			1*droplet.logDebug("couponId Id is Null")
			0*droplet.logDebug("Promotion dosn't belong to this site")
			0*requestMock.setParameter("mainImgUrl", "mainImgUrl")
			0*requestMock.setParameter("lrgImgUrl", "lrgImgUrl")
			1*requestMock.setParameter("promoDesription", _)
			1*requestMock.setParameter("tAndc", _)
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "err_promotion_details_error" , BBBCoreErrorConstants.ACCOUNT_ERROR_1118),_)
			1*requestMock.setParameter("systemerror", "err_mycoupons_system_error")
			2*requestMock.serviceParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with promotion as null"(){
		given:
			requestMock.getLocalParameter(BBBCoreConstants.PROMOTION_ID) >> null
			requestMock.getLocalParameter(BBBCoreConstants.COUPON_ID) >> null
			1*mPromTools.getPromotionById(_) >> null
			0*mPromTools.fetchExclusionText(_,_) >> "tAndC"
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logDebug("promotionId Id is Null")
			1*droplet.logDebug("couponId Id is Null")
			1*droplet.logDebug("Promotion dosn't belong to this site")
			3*requestMock.serviceParameter("empty", requestMock, responseMock)
	}
}
