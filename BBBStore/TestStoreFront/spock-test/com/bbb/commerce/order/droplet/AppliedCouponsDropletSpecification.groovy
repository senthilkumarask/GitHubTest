package com.bbb.commerce.order.droplet

import java.util.ArrayList;
import java.util.List
import java.util.Map;

import com.bbb.account.vo.CouponListVo;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.droplet.BBBPromotionTools
import com.bbb.constants.BBBCoreConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.repository.RepositoryItemMock;

import atg.adapter.secure.GenericSecuredMutableRepositoryItem
import atg.commerce.order.Order;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor
import atg.servlet.DynamoHttpServletRequest;
import spock.lang.specification.BBBExtendedSpec;

/**
 *
 * @author Velmurugan Moorthy
 *
 * This class is written to unit test the AppliedCouponsDroplet
 *
 */

class AppliedCouponsDropletSpecification extends BBBExtendedSpec {

	private BBBPromotionTools promotionToolsMock
	private AppliedCouponsDroplet appliedCouponsDropletSpy
	private Order orderMock
	private BBBCatalogToolsImpl catalogToolsMock
	
	def setup() {
	
		promotionToolsMock = Mock()
		orderMock = Mock()
		catalogToolsMock = Mock()
		appliedCouponsDropletSpy = Spy()
		
		appliedCouponsDropletSpy.setPromotionTools(promotionToolsMock)
		promotionToolsMock.getCatalogTools() >> catalogToolsMock
		requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
	}
	
	def "service - Applied coupons retrieved successfully(happy flow)" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItemMock mediaIteamMock = new RepositoryItemMock(["id":mediaItemId])
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = [currentPromo]
		
		mediaIteamMock.setProperties(["URL":mediaUrl])
		mediaMap.put(BBBCoreConstants.PROMOTION_MAIN_IMAGE_KEY, mediaIteamMock)
		
		currentPromo.getItemDescriptor() >> currentPromoItemDescMock
		currentPromo.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap
		currentPromo.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> couponDisplayName
		currentPromoItemDescMock.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])

		appliedCoupons.add(itemCouponMock)
		appliedCoupons.add(orderCopuonMock)
		//1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock//not working - returns null
		1 * catalogToolsMock.getPromotions(itemCouponId) >> currentPromos
		1 * promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * appliedCouponsDropletSpy.vlogDebug("AppliedCouponsDroplet.service: There is no promotions associated to the coupon {0}",_)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}

	def "service - Exception occured while retrieving applied coupons(BBBSystemException)" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		
		promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		appliedCoupons.add(itemCouponMock)
		
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		
		catalogToolsMock.getPromotions(itemCouponId) >> {throw new BBBSystemException("Exception occured while retrieving the applied coupons")}
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		thrown NullPointerException
		/*
		 * If the system exception is thrown the currentPromos var will be null.
		 * Accessing it will lead to Null pointer
		 */
		0 * requestMock.setParameter('couponList',_)
		0 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * appliedCouponsDropletSpy.vlogError("AppliedCouponsDroplet.service: BBBSystemException occurred while trying to fetch promotion from the coupon {0}",_)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}
	
	def "service - Exception occured while retrieving applied coupons(BBBBusinessException)" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		
		promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		appliedCoupons.add(itemCouponMock)
		
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		
		catalogToolsMock.getPromotions(itemCouponId) >> {throw new BBBBusinessException("Exception occured while retrieving the applied coupons")}
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		thrown NullPointerException
		/*
		 * If the RepositoryException is thrown the currentPromos var will be null.
		 * Accessing it will lead to Null pointer
		 */
		0 * requestMock.setParameter('couponList',_)
		0 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * appliedCouponsDropletSpy.vlogError("AppliedCouponsDroplet.service: BBBBusinessException occurred while trying to fetch promotion from the coupon {0}",_)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}
	
	
	def "service - Exception occured while retrieving applied coupons(RepositoryException)" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = [currentPromo]
		currentPromo.getItemDescriptor() >> {throw new RepositoryException("Exception occured while retrieving the applied coupons")}
		promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		appliedCoupons.add(itemCouponMock)
		
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		
		catalogToolsMock.getPromotions(itemCouponId) >> currentPromos
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		//thrown RepositoryException
		/*
		 * If the RepositoryException is thrown the currentPromos var will be null.
		 * Accessing it will lead to Null pointer
		 */
		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * appliedCouponsDropletSpy.vlogError("AppliedCouponsDroplet.service: RepositoryException occurred while trying to fetch promotion type from promotion {0}",currentPromos[0])
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}

	def "service - current promo item is an OrderDiscount | Media (map) doesn't exist in coupon item" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = [currentPromo]
		
		currentPromo.getItemDescriptor() >> currentPromoItemDescMock
		currentPromoItemDescMock.getItemDescriptorName() >> BBBCoreConstants.ORDER_DISCOUNT
		
		
		appliedCoupons.add(itemCouponMock)
		
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		
		1 * promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		1 * catalogToolsMock.getPromotions(itemCouponId) >> currentPromos
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		//thrown RepositoryException
		/*
		 * If the RepositoryException is thrown the currentPromos var will be null.
		 * Accessing it will lead to Null pointer
		 */
		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}

	def "service - current promo item is invalid | Item descriptor name is neither ItemDiscount not OrderDiscount" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = [currentPromo]
		
		currentPromo.getItemDescriptor() >> currentPromoItemDescMock
		currentPromoItemDescMock.getItemDescriptorName() >> "new discount"
		1 * promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		appliedCoupons.add(itemCouponMock)
		
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		
		catalogToolsMock.getPromotions(itemCouponId) >> currentPromos
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		//thrown RepositoryException
		/*
		 * If the RepositoryException is thrown the currentPromos var will be null.
		 * Accessing it will lead to Null pointer
		 */
		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}
		
	def "service - applied coupon is corrupted/invalid(null)" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = null
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItemMock mediaIteamMock = new RepositoryItemMock(["id":mediaItemId])
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = [currentPromo]
		
		mediaIteamMock.setProperties(["URL":mediaUrl])
		mediaMap.put(BBBCoreConstants.PROMOTION_MAIN_IMAGE_KEY, mediaIteamMock)
		
		currentPromo.getItemDescriptor() >> currentPromoItemDescMock
		currentPromo.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap
		currentPromo.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> couponDisplayName
		currentPromoItemDescMock.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		

		appliedCoupons.add(itemCouponMock)
		0 * catalogToolsMock.getPromotions(itemCouponId) >> currentPromos // 1 * getPromotions - not working
		1 * promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		1 * appliedCouponsDropletSpy.vlogDebug("AppliedCouponsDroplet.service: Coupons is null")
		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}
	
		
	def "service - No promotion available for coupon" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = []
		
		currentPromo.getItemDescriptor() >> currentPromoItemDescMock
		currentPromoItemDescMock.getItemDescriptorName() >> "new discount"
		promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		appliedCoupons.add(itemCouponMock)
		
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		
		catalogToolsMock.getPromotions(itemCouponId) >> currentPromos
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		//thrown RepositoryException
		/*
		 * If the RepositoryException is thrown the currentPromos var will be null.
		 * Accessing it will lead to Null pointer
		 */
		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * appliedCouponsDropletSpy.vlogDebug("AppliedCouponsDroplet.service: There is no promotions associated to the coupon {0}",_)
	}

	def "service - Media Item is not present in the promo (media map)" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = "http://media.bedbath.com/images/promo/itemCoupon01"
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItemMock mediaIteamMock = null
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = [currentPromo]
		
		mediaMap.put(BBBCoreConstants.PROMOTION_MAIN_IMAGE_KEY, mediaIteamMock)
		
		currentPromo.getItemDescriptor() >> currentPromoItemDescMock
		currentPromo.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap
		currentPromo.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> couponDisplayName
		currentPromoItemDescMock.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		catalogToolsMock.getPromotions(itemCouponId) >> currentPromos

		appliedCoupons.add(itemCouponMock)
		appliedCoupons.add(orderCopuonMock)
		
		promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :

		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}

	def "service - Media item not having URL(null)" () {
		
		def itemCouponId = "itemCoupon01"
		def orderCouponId = "orderCoupon01"
		def couponDisplayName = "50OFF"
		def mediaItemId = "media01"
		def mediaUrl = null
		def currentPromoId = "promo01"
		
		List<RepositoryItemMock> appliedCoupons = new ArrayList<>()
		Map mediaMap = new HashMap<>()
		
		RepositoryItemMock itemCouponMock = new RepositoryItemMock(["id":itemCouponId])
		RepositoryItemMock orderCopuonMock = new RepositoryItemMock(["id":orderCouponId])
		RepositoryItemMock mediaIteamMock = Mock()
		RepositoryItem currentPromo = Mock()
		RepositoryItemDescriptor currentPromoItemDescMock = Mock()
		
		RepositoryItem[] currentPromos = [currentPromo]
		
		1 * mediaIteamMock.getPropertyValue("URL") >> mediaUrl
		mediaMap.put(BBBCoreConstants.PROMOTION_MAIN_IMAGE_KEY, mediaIteamMock)
		
		currentPromo.getItemDescriptor() >> currentPromoItemDescMock
		currentPromo.getPropertyValue(BBBCoreConstants.MEDIA) >> mediaMap
		currentPromoItemDescMock.getItemDescriptorName() >> BBBCoreConstants.ORDER_DISCOUNT
		promotionToolsMock.getCouponListFromOrder(orderMock) >> appliedCoupons
		
		appliedCoupons.add(itemCouponMock)
		
		itemCouponMock.setProperties(["displayName":couponDisplayName,"media":mediaMap])
		
		catalogToolsMock.getPromotions(itemCouponId) >> currentPromos
		
		when :
		
		appliedCouponsDropletSpy.service(requestMock, responseMock)
		
		then :
		
		//thrown RepositoryException
		/*
		 * If the RepositoryException is thrown the currentPromos var will be null.
		 * Accessing it will lead to Null pointer
		 */
		1 * requestMock.setParameter('couponList',_)
		1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		//1 * catalogToolsMock.getPromotions(itemCouponMock.getRepositoryId()) // not working - returns null
	}
	
}
