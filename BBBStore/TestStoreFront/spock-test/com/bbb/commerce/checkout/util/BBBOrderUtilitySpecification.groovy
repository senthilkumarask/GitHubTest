package com.bbb.commerce.checkout.util

import atg.adapter.secure.GenericSecuredMutableRepositoryItem
import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl
import atg.commerce.pricing.AmountInfo
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.Address
import atg.eai.framework.EAILocalServices;
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;

import java.util.List
import java.util.Map
import java.util.Set

import org.apache.commons.lang.StringUtils;

import com.bbb.commerce.cart.bean.CommerceItemDisplayVO;
import com.bbb.commerce.cart.bean.CommerceItemVO
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.common.PriceInfoDisplayVO;
import com.bbb.commerce.common.ShippingGroupDisplayVO
import com.bbb.commerce.order.BBBCreditCard
import com.bbb.commerce.order.BBBGiftCard;
import com.bbb.commerce.order.Paypal;
import com.bbb.commerce.pricing.bean.PaymentGroupDisplayVO;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBPropertyNameConstants
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBItemPriceInfo
import com.bbb.order.bean.TrackingInfo
import com.bbb.repository.RepositoryItemMock
import com.bbb.utils.BBBUtility;
import com.cybersource.stub.PayPal

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is written to unit test the BBBOrderUtilty
 *
 */
public class BBBOrderUtilitySpecification extends BBBExtendedSpec{

	private BBBOrderUtilty orderUtilitySpy
	private BBBHardGoodShippingGroup hardGoodShippingGroup
	def giftWrapMessage = "Happy Birthday"
	def shippingConfirmationEmail = "tester@yopmail.com"
	def sourceId = "source01"
	def mobileNumber = "9876543210"
	def alternatePhoneNumber = "1234567890"
	def phoneNumber = "2345678901"
	
	def setup() {
	
			orderUtilitySpy = Spy()
			hardGoodShippingGroup = Mock()
	}
	
	/*====================================================================================
	 * populateShippingGrpInfo - test cases - STARTS									 *
	 * 																				     *
	 * Method signature : 																 *
	 * 																					 *
	 * public static ShippingGroupDisplayVO populateShippingGrpInfo(ShippingGroupImpl sg)* 
	 * ===================================================================================
	 */
	
 	def "populateShippingGrpInfo - populating shippingGroup Info successfully - happy flow" () {
		
		given : 
		
		def containsGiftWrap = true
		Date shipOnDate = new Date()
		def containsGiftWrapMessage = true
		

		CommerceItemRelationship commerceRelationShipMock = Mock()
		BBBRepositoryContactInfo repositoryContactInfo = new BBBRepositoryContactInfo()
		TrackingInfo trackingInfo = Mock()
		List<CommerceItemRelationship> commerceItemRelationshipList = new ArrayList<>()
		HashMap specialInstructions = ["CONFIRMATION_EMAIL_ID":shippingConfirmationEmail,"giftMessage":giftWrapMessage]
		GenericSecuredMutableRepositoryItem  mutableRepositoryItemMock = Mock()
		Set<MutableRepositoryItem> shipmentTrackingList = [trackingInfo].toSet()		
		populateContactInfo(repositoryContactInfo, mobileNumber, alternatePhoneNumber)
		
		trackingInfo.getCarrierCode() >> "fedex01"
		trackingInfo.getTrackingNumber() >> "track01"
		
		mutableRepositoryItemMock.getPropertyValue("carrierCode") >>  "fedex01"
		mutableRepositoryItemMock.getPropertyValue("trackingNumber") >>  "track01"
		
		commerceItemRelationshipList.add(commerceRelationShipMock)

		
		
		//populateShippingGroup(shipmentTrackingList, commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, repositoryContactInfo, shippingConfirmationEmail, sourceId)
		populateShippingGroup(commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, repositoryContactInfo, shippingConfirmationEmail, sourceId)
		hardGoodShippingGroup.getTrackingInfos() >> shipmentTrackingList
		
		when : 
		
		ShippingGroupDisplayVO shipGroupDisplayVO = orderUtilitySpy.populateShippingGrpInfo(hardGoodShippingGroup)
		
		then : 
		
		shipGroupDisplayVO.getEmail().equals(shippingConfirmationEmail)
		shipGroupDisplayVO.getGiftWrapMessage().equals(giftWrapMessage)
		shipGroupDisplayVO.getShippingAddress().getAddress1() == "Address 1"
		shipGroupDisplayVO.getAlternatePhoneNumber().equals(alternatePhoneNumber)
		shipGroupDisplayVO.getPhoneNumber().equals(phoneNumber)
		shipGroupDisplayVO.isContainsGiftWrapMessage()
	}

	 def "populateShippingGrpInfo - input shipping group is not HardgoodShippingGroup" () {
		 
		 given :
		 
		 def containsGiftWrap = true
		 Date shipOnDate = new Date()
		 def containsGiftWrapMessage = true
		 def giftWrapMessage = "Happy Birthday"
		 def shippingConfirmationEmail = "tester@yopmail.com"
		 def sourceId = "source01"
		 def mobileNumber = "9876543210"
		 def alternatePhoneNumber = "1234567890"
		 def phoneNumber = "2345678901"
		 CommerceItemRelationship commerceRelationShipMock = Mock()
		 BBBRepositoryContactInfo repositoryContactInfo = Mock()
		 TrackingInfo trackingInfo = Mock()
		 BBBStoreShippingGroup shippingGroupMock = Mock()
		 List<CommerceItemRelationship> commerceItemRelationshipList = new ArrayList<>()
		 HashMap specialInstructions = ["CONFIRMATION_EMAIL_ID":shippingConfirmationEmail,"giftMessage":giftWrapMessage]
		 GenericSecuredMutableRepositoryItem  mutableRepositoryItemMock = Mock()
		 Set<MutableRepositoryItem> shipmentTrackingList = [mutableRepositoryItemMock]
		 
		 trackingInfo.getCarrierCode() >> "fedex01"
		 trackingInfo.getTrackingNumber() >> "track01"
		 
		 mutableRepositoryItemMock.getPropertyValue("carrierCode") >>  "fedex01"
		 mutableRepositoryItemMock.getPropertyValue("trackingNumber") >>  "track01"
		 
		 commerceItemRelationshipList.add(commerceRelationShipMock)
 
		 populateContactInfo(repositoryContactInfo, mobileNumber, alternatePhoneNumber)
				 
		 repositoryContactInfo.getPhoneNumber() >> phoneNumber
		 repositoryContactInfo.getEmail() >> shippingConfirmationEmail
		 
		 //populateShippingGroup(shipmentTrackingList, commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, repositoryContactInfo, shippingConfirmationEmail, sourceId)
		 populateShippingGroup(commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, repositoryContactInfo, shippingConfirmationEmail, sourceId)
		 hardGoodShippingGroup.getTrackingInfos()>> shipmentTrackingList
		 when :
		 
		 ShippingGroupDisplayVO shipGroupDisplayVO = orderUtilitySpy.populateShippingGrpInfo(shippingGroupMock)
		 
		 then :
		 
		 shipGroupDisplayVO.getEmail() == null
		 shipGroupDisplayVO.getGiftWrapMessage() == null
		 shipGroupDisplayVO.getShippingAddress()  == null
		 shipGroupDisplayVO.getAlternatePhoneNumber() == null
		 shipGroupDisplayVO.getPhoneNumber() == null
	 }
	 
	 def "populateShippingGrpInfo - Shipping group is corrupted/invalid (null)" () {
		 
		 given :
		 
		 hardGoodShippingGroup = null
		 
		 when :
		 
		 ShippingGroupDisplayVO shipGroupDisplayVO = orderUtilitySpy.populateShippingGrpInfo(hardGoodShippingGroup)
		 
		 then :
		 
		 shipGroupDisplayVO.getEmail() == null
		 shipGroupDisplayVO.getGiftWrapMessage() == null
		 shipGroupDisplayVO.getShippingAddress()  == null
		 shipGroupDisplayVO.getAlternatePhoneNumber() == null
		 shipGroupDisplayVO.getPhoneNumber() == null
	 }
 
	 def "populateShippingGrpInfo - shipOnDate is corrupted/invalid(null) | shipping address is corrupted/invalid (null) | Tracking details are corrupted/invalid (null)" () {
		 
		 given :
		 
		 def containsGiftWrap = true
		 Date shipOnDate = null
		 def containsGiftWrapMessage = true
		 def giftWrapMessage = "Happy Birthday"
		 def shippingConfirmationEmail = "tester@yopmail.com"
		 def sourceId = "source01"
		 def mobileNumber = "9876543210"
		 def alternatePhoneNumber = "1234567890"
		 def phoneNumber = "2345678901"
		 
		 CommerceItemRelationship commerceRelationShipMock = Mock()
		 BBBRepositoryContactInfo repositoryContactInfo = null
		 TrackingInfo trackingInfo = null
		 List<CommerceItemRelationship> commerceItemRelationshipList = new ArrayList<>()
		 HashMap specialInstructions = ["CONFIRMATION_EMAIL_ID":shippingConfirmationEmail,"giftMessage":giftWrapMessage]
		 Set<MutableRepositoryItem> shipmentTrackingList = null
		 
		 commerceItemRelationshipList.add(commerceRelationShipMock)
 
		 //populateShippingGroup(shipmentTrackingList, commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, repositoryContactInfo, shippingConfirmationEmail, sourceId)
		 populateShippingGroup(commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, null, shippingConfirmationEmail, sourceId)
		 hardGoodShippingGroup.getPropertyValue("shipmentTracking") >> shipmentTrackingList
		 
		 when :
		 
		 ShippingGroupDisplayVO shipGroupDisplayVO = orderUtilitySpy.populateShippingGrpInfo(hardGoodShippingGroup)
		 
		 then :
		 
		 shipGroupDisplayVO.getAlternatePhoneNumber() == null
		 shipGroupDisplayVO.getPhoneNumber() == null
		 shipGroupDisplayVO.getTrackingInfoVOList() == null
		 shipGroupDisplayVO.getEmail().equals(shippingConfirmationEmail)
		 shipGroupDisplayVO.getGiftWrapMessage().equals(giftWrapMessage)
	 }

	 def "populateShippingGrpInfo - Tracking details(getTrackingInfos) are corrupted/invalid (empty)" () {
		 /*
		  * TrackingInfos is empty/null will be treated same.
		  * Because, the return value of getTrackingInfos will be null for both scenarios
		  */
		 given :
		 
		 def containsGiftWrap = true
		 Date shipOnDate = null
		 def containsGiftWrapMessage = true
		 def giftWrapMessage = "Happy Birthday"
		 def shippingConfirmationEmail = "tester@yopmail.com"
		 def sourceId = "source01"
		 def mobileNumber = "9876543210"
		 def alternatePhoneNumber = "1234567890"
		 def phoneNumber = "2345678901"
		 
		 CommerceItemRelationship commerceRelationShipMock = Mock()
		 BBBRepositoryContactInfo repositoryContactInfo = Mock()
		 TrackingInfo trackingInfo = null
		 List<CommerceItemRelationship> commerceItemRelationshipList = new ArrayList<>()
		 HashMap specialInstructions = ["CONFIRMATION_EMAIL_ID":shippingConfirmationEmail,"giftMessage":giftWrapMessage]
		 GenericSecuredMutableRepositoryItem  mutableRepositoryItemMock = Mock()
		 Set<MutableRepositoryItem> shipmentTrackingList = new HashSet<>()
		 
		 mutableRepositoryItemMock.getPropertyValue("carrierCode") >>  "fedex01"
		 mutableRepositoryItemMock.getPropertyValue("trackingNumber") >>  "track01"
		 
		 commerceItemRelationshipList.add(commerceRelationShipMock)
 
		 //populateShippingGroup(shipmentTrackingList, commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, repositoryContactInfo, shippingConfirmationEmail, sourceId)
		 populateShippingGroup(commerceItemRelationshipList, specialInstructions, containsGiftWrap, shipOnDate, containsGiftWrapMessage, giftWrapMessage, repositoryContactInfo, shippingConfirmationEmail, sourceId)
		 hardGoodShippingGroup.getPropertyValue("shipmentTracking") >> shipmentTrackingList
		 hardGoodShippingGroup.getTrackingInfos() >> new TreeSet<TrackingInfo>()
		 
		 when :
		 
		 ShippingGroupDisplayVO shipGroupDisplayVO = orderUtilitySpy.populateShippingGrpInfo(hardGoodShippingGroup)
		 
		 then :
		 
		 shipGroupDisplayVO.getAlternatePhoneNumber() == null
		 shipGroupDisplayVO.getPhoneNumber() == null
		 shipGroupDisplayVO.getTrackingInfoVOList() == null
		 shipGroupDisplayVO.getEmail().equals(shippingConfirmationEmail)
		 shipGroupDisplayVO.getGiftWrapMessage().equals(giftWrapMessage)
	 }
	
	 /*
	  * populateShippingGrpInfo - test cases - STARTS
	  *
	  */
	 
	
	 /*====================================================================================
	  * populatePriceInfo - test cases - STARTS						  					  *
	  * 																				  *
	  * Method signature : 																  *
	  * 																				  *
	  * public static PriceInfoDisplayVO populatePriceInfo(PriceInfoVO priceInfoVO,		  * 
	  * String siteId,BBBOrderImpl order, AmountInfo priceInfo)				   			  *
	  * ===================================================================================
	  */
	 
	 
	 def "populatePriceInfo - populating price info successfully (happy flow)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00 
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 PricingAdjustment secondaryPricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId]) 
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 RepositoryItemMock secondaryCouponItemMock = new RepositoryItemMock(["id":secondaryCouponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 adjustmentList.add(secondaryPricingAdjustment)		 
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 secondaryPricingAdjustment.getPricingModel()  >> secondaryPricingModelMock
		 secondaryPricingAdjustment.getCoupon() >> secondaryCouponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when : 
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 
	 }

	 def "populatePriceInfo - Exception while populating price info data (NumberFormatException)" () {
		 
		 
		 PriceInfoVO priceInfoVO = Mock()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing02"
		 def couponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 
		 priceInfoVO.getShippingGroupItemsTotal() >> {throw new NumberFormatException("Exception in input number data")}
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 
		 when :
		 
		 def PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 thrown NumberFormatException
		 resultPriceInfoDisplay == null
		0 * resultPriceInfoDisplay.setShippingGroupItemsTotal(_)
		0 * resultPriceInfoDisplay.setShippingGroupItemTotal(_)
		0 * resultPriceInfoDisplay.setUndiscountedItemsCount(_)
		0 * resultPriceInfoDisplay.setItemCount(_)
		0 * resultPriceInfoDisplay.setTotalShippingAmount(_)
		0 * resultPriceInfoDisplay.setEcoFeeTotal(_)
		0 * resultPriceInfoDisplay.setTotalSavedPercentage(_)
		0 * resultPriceInfoDisplay.setShippingLevelTax(_)
		0 * resultPriceInfoDisplay.setStoreAmount(_)
		0 * resultPriceInfoDisplay.setStoreEcoFeeTotal(_)
		0 * resultPriceInfoDisplay.setOnlinePurchaseTotal(_)
		0 * resultPriceInfoDisplay.setOnlineEcoFeeTotal(_)
		0 * resultPriceInfoDisplay.setGiftWrapTotal(_)
		0 * resultPriceInfoDisplay.setRawShippingTotal(_)
		0 * resultPriceInfoDisplay.setTotalSurcharge(_)
		0 * resultPriceInfoDisplay.setTotalTax(_)
		0 * resultPriceInfoDisplay.setOnlineTotal(_)
		0 * resultPriceInfoDisplay.setOrderPreTaxAmount(_)
		0 * resultPriceInfoDisplay.setTotalAmount(_)
		0 * resultPriceInfoDisplay.setFreeShipping(_)
		0 * resultPriceInfoDisplay.setHardgoodShippingGroupItemCount(_)
		0 * resultPriceInfoDisplay.setStorePickupShippingGroupItemCount(_)
		0 * resultPriceInfoDisplay.setTotalSavedAmount(_)
		0 * resultPriceInfoDisplay.setFinalShippingCharge(_)
		 
	 }
	 
	 
	 def "populatePriceInfo - BedBathCanada site | state tax and country tax is set ( tax > 0) " () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = BBBCoreConstants.SITE_BAB_CA
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing02"
		 def couponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setShippingCountyLevelTax(16)
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 resultPriceInfoDisplay != null
	     //2 * resultPriceInfoDisplay.setShippingStateLevelTax(_)// not working
		 
	 }
	 
	 def "populatePriceInfo - BedBathCanada site | state tax and country tax is not set ( tax is 0) " () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = BBBCoreConstants.SITE_BAB_CA
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing02"
		 def couponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 0.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 priceInfoVO.setShippingStateLevelTax(0)
		 
		 priceInfoVO.setOnlineTotal(giftCardAmount)
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 resultPriceInfoDisplay != null
		 //2 * resultPriceInfoDisplay.setShippingStateLevelTax(_)// not working
		 
	 }
	 
	 def "populatePriceInfo - TBS - BedBathCanada site (SITE_TBS_BAB_CA) | state tax is free and country tax is set" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = TBSConstants.SITE_TBS_BAB_CA
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing02"
		 def couponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setShippingStateLevelTax(16)
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 resultPriceInfoDisplay != null
		 //2 * resultPriceInfoDisplay.setShippingStateLevelTax(_)// not working
		 
	 }

	 def "populatePriceInfo - TBS - BedBathCanada site (SITE_TBS_BAB_CA) | state tax and country tax is set ( tax > 0) " () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = TBSConstants.SITE_TBS_BAB_CA
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing02"
		 def couponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setShippingCountyLevelTax(16)
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 resultPriceInfoDisplay != null
		 //2 * resultPriceInfoDisplay.setShippingStateLevelTax(_)// not working
		 
	 }

	 def "populatePriceInfo - siteID is not set (null)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = null
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 PricingAdjustment secondaryPricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 RepositoryItemMock secondaryCouponItemMock = new RepositoryItemMock(["id":secondaryCouponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 adjustmentList.add(secondaryPricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 secondaryPricingAdjustment.getPricingModel()  >> secondaryPricingModelMock
		 secondaryPricingAdjustment.getCoupon() >> secondaryCouponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
	 }
	 
	 def "populatePriceInfo - ItemPriceInfo is invalid (not an instance of ItemPriceInfo)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 AmountInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 
		 priceBeansList.add(unitPriceBean)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 
		 when :
		 
		 resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 0 * resultPriceInfoDisplay.setPromotionCount(_);
		 0 * resultPriceInfoDisplay.setPromoPriceBeansDisplayVO(_);
		 
	 }
	 
	 def "populatePriceInfo - ItemPriceInfo is not set/invalid (null)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = null
		 UnitPriceBean unitPriceBean = Mock()
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 
		 priceBeansList.add(unitPriceBean)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 
		 when :
		 
		 resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 0 * resultPriceInfoDisplay.setPromotionCount(_);
		 0 * resultPriceInfoDisplay.setPromoPriceBeansDisplayVO(_);
		 
	 }
	 
	 def "populatePriceInfo - Pricing models are not set (null)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 PricingAdjustment secondaryPricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = null
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 RepositoryItemMock secondaryCouponItemMock = new RepositoryItemMock(["id":secondaryCouponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 adjustmentList.add(secondaryPricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 secondaryPricingAdjustment.getCoupon() >> secondaryCouponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 thrown NullPointerException // Line#299 - accessing pricing model without null check
		 0 * resultPriceInfoDisplay.setItemAdjustments(_)
		 0 * resultPriceInfoDisplay.setShippingAdjustments(_)
		 0 * resultPriceInfoDisplay.setRawAmount(_)
		 0 * resultPriceInfoDisplay.setItemPromotionVOList(_)
		 0 * resultPriceInfoDisplay.setTotalDiscountShare(_)
		 0 * resultPriceInfoDisplay.setUnitSavedPercentage(_)
		 0 * resultPriceInfoDisplay.setUnitSavedAmount(_)
		 0 * resultPriceInfoDisplay.setUnitListPrice(_)
		 0 * resultPriceInfoDisplay.setUnitSalePrice(_)
		 
	 }
	 
	 def "populatePriceInfo - No coupons exist(null) | Pricing Models are not set (empty)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 PricingAdjustment secondaryPricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = null
		 RepositoryItemMock secondaryCouponItemMock = new RepositoryItemMock(["id":secondaryCouponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 /*pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)*/
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 adjustmentList.add(secondaryPricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 secondaryPricingAdjustment.getPricingModel()  >> secondaryPricingModelMock
		 //secondaryPricingAdjustment.getCoupon() >> secondaryCouponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 
	 }
	 
	 
	 def "populatePriceInfo - price details(PriceInfoVO) are corrupted/invalid (null) " () {
		 
		 
		 PriceInfoVO priceInfoVO = null
		 String siteId = "BBB_US"
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 PriceInfoDisplayVO resultPriceInfoDisplay
		 
		 when :
		 
		 resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
			0 * resultPriceInfoDisplay.setShippingGroupItemsTotal(_)
			0 * resultPriceInfoDisplay.setShippingGroupItemTotal(_)
			0 * resultPriceInfoDisplay.setUndiscountedItemsCount(_)
			0 * resultPriceInfoDisplay.setItemCount(_)
			0 * resultPriceInfoDisplay.setTotalShippingAmount(_)
			0 * resultPriceInfoDisplay.setEcoFeeTotal(_)
			0 * resultPriceInfoDisplay.setTotalSavedPercentage(_)
			0 * resultPriceInfoDisplay.setShippingLevelTax(_)
			0 * resultPriceInfoDisplay.setStoreAmount(_)
			0 * resultPriceInfoDisplay.setStoreEcoFeeTotal(_)
			0 * resultPriceInfoDisplay.setOnlinePurchaseTotal(_)
			0 * resultPriceInfoDisplay.setOnlineEcoFeeTotal(_)
			0 * resultPriceInfoDisplay.setGiftWrapTotal(_)
			0 * resultPriceInfoDisplay.setRawShippingTotal(_)
			0 * resultPriceInfoDisplay.setTotalSurcharge(_)
			0 * resultPriceInfoDisplay.setTotalTax(_)
			0 * resultPriceInfoDisplay.setOnlineTotal(_)
			0 * resultPriceInfoDisplay.setOrderPreTaxAmount(_)
			0 * resultPriceInfoDisplay.setTotalAmount(_)
			0 * resultPriceInfoDisplay.setFreeShipping(_)
			0 * resultPriceInfoDisplay.setHardgoodShippingGroupItemCount(_)
			0 * resultPriceInfoDisplay.setStorePickupShippingGroupItemCount(_)
			0 * resultPriceInfoDisplay.setTotalSavedAmount(_)
			0 * resultPriceInfoDisplay.setFinalShippingCharge(_)
	 }
	 
	 def "populatePriceInfo - PriceInfoBean corrupted/invalid (null)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 def PriceInfoDisplayVO resultPriceInfoDisplay
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 
		 List<UnitPriceBean> priceBeansList = null
	
		 populatePriceInfoVOBasic(priceInfoVO)
		 when :
		 
		 resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 0 * resultPriceInfoDisplay.setPromotionCount(_)
		 0 * resultPriceInfoDisplay.setPromoPriceBeansDisplayVO(_)
		 
	 }

	 
	 def "populatePriceInfo - PriceInfoBean corrupted/invalid (empty)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 def PriceInfoDisplayVO resultPriceInfoDisplay
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 priceInfoVO.setPriceBeans(priceBeansList)
	
		 populatePriceInfoVOBasic(priceInfoVO)
		 when :
		 
		 resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 0 * resultPriceInfoDisplay.setPromotionCount(_);
		 0 * resultPriceInfoDisplay.setPromoPriceBeansDisplayVO(_);
		 
	 }
	 
	 def "populatePriceInfo - No adjustments exists(null) in DetailedItemPriceInfo" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing02"
		 def couponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = null
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 /*pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock*/
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 resultPriceInfoDisplay.getPromoPriceBeansDisplayVO().isEmpty()
		 
	 }
	 
	 def "populatePriceInfo - Pricing model name(ItemDisplayName) is not set (empty)" () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 PricingAdjustment secondaryPricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock =  Mock()
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 RepositoryItemMock secondaryCouponItemMock = new RepositoryItemMock(["id":secondaryCouponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 primaryPricingModelMock.getItemDisplayName() >> ""
		  
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 adjustmentList.add(secondaryPricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 secondaryPricingAdjustment.getPricingModel()  >> secondaryPricingModelMock
		 secondaryPricingAdjustment.getCoupon() >> secondaryCouponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 PriceInfoDisplayVO resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 
	 }
	 
	 def "populatePriceInfo - Payment groups are not set (empty) " () {
		 
		 
		 PriceInfoVO priceInfoVO = new PriceInfoVO()
		 String siteId = "BBB_US"
		 def primaryPricingModelId = "pricing01"
		 def secondaryPricingModelId = "pricing01"
		 def couponId = "coupon01"
		 def secondaryCouponId = "coupon01"
		 def quantity = 10
		 def unitPrice = 10.00
		 def unitSalePrice = 15.00
		 def giftCardAmount = 16.00
		 PriceInfoDisplayVO resultPriceInfoDisplay
		 
		 BBBOrderImpl order = Mock()
		 ItemPriceInfo priceInfo = Mock()
		 UnitPriceBean unitPriceBean = Mock()
		 PricingAdjustment pricingAdjustment = Mock()
		 PricingAdjustment secondaryPricingAdjustment = Mock()
		 DetailedItemPriceInfo detailedPriceItemInfoMock = Mock()
		 RepositoryItemMock primaryPricingModelMock = new RepositoryItemMock(["id":primaryPricingModelId])
		 RepositoryItemMock secondaryPricingModelMock = new RepositoryItemMock(["id":secondaryPricingModelId])
		 RepositoryItemMock couponItemMock = new RepositoryItemMock(["id":couponId])
		 RepositoryItemMock secondaryCouponItemMock = new RepositoryItemMock(["id":secondaryCouponId])
		 BBBGiftCard giftCardPayment = Mock()
		 Paypal paypalPayment = Mock()
		 
		 List<UnitPriceBean> priceBeansList = new ArrayList<>()
		 List<DetailedItemPriceInfo> detailedItemPriceInfoList = new ArrayList<>()
		 List<PricingAdjustment> adjustmentList = new ArrayList<>()
		 List<RepositoryItem> pricingModels = new ArrayList<>()
		 List<Object> paymentGroups = new ArrayList<>()
		 
		 
		 unitPriceBean.getQuantity() >> quantity
		 unitPriceBean.getUnitPrice() >> unitPrice
		 unitPriceBean.getPricingModels() >> pricingModels
		 
		 pricingModels.add(primaryPricingModelMock)
		 pricingModels.add(secondaryPricingModelMock)
		 
		 priceBeansList.add(unitPriceBean)
		 detailedItemPriceInfoList.add(detailedPriceItemInfoMock)
		 adjustmentList.add(pricingAdjustment)
		 adjustmentList.add(secondaryPricingAdjustment)
		 
		 detailedPriceItemInfoMock.getAdjustments() >> adjustmentList
		 
		 populatePriceInfoVOBasic(priceInfoVO)
		 
		 priceInfoVO.setPriceBeans(priceBeansList)
		 priceInfoVO.setUnitSalePrice(unitSalePrice)
		 priceInfo.getCurrentPriceDetails() >> detailedItemPriceInfoList
		 
		 pricingAdjustment.getPricingModel() >> primaryPricingModelMock
		 pricingAdjustment.getCoupon() >> couponItemMock
		 
		 secondaryPricingAdjustment.getPricingModel()  >> secondaryPricingModelMock
		 secondaryPricingAdjustment.getCoupon() >> secondaryCouponItemMock
		 
		 giftCardPayment.getAmount() >> giftCardAmount
		 /*paymentGroups.add(giftCardPayment)
		 paymentGroups.add(paypalPayment)*/
		 
		 order.getPaymentGroups() >> paymentGroups
		 
		 when :
		 
		 resultPriceInfoDisplay = orderUtilitySpy.populatePriceInfo(priceInfoVO, siteId, order, priceInfo)
		 
		 then :
		 
		 isPriceInfoPopulated(resultPriceInfoDisplay, priceInfoVO, giftCardAmount)
		 0 * resultPriceInfoDisplay.setAmountWithoutGiftCard(_)
	 }
	 
	 /*
	  * Logging debug can't be enabled as it's from OOTB static method.
	  * Logging debug Can't be mocked, as we use spy for test class 
	  */
	 
	 
	 /*====================================================================================
	  * populatePriceInfo - test cases - ENDS										      *	  
	  * ===================================================================================
	  */
	  
	 
	 /*====================================================================================
	  * populateCommerceItemDisplayVO - test cases - STARTS						  		  *
	  * 																				  *
	  * Method signature : 																  *
	  * 																				  *
	  * public static void populateCommerceItemDisplayVO(CommerceItemVO commerceItemVO,   *
	  *  CommerceItemDisplayVO commItemDisplayVO) {									      *
	  * ===================================================================================
	  */
	 
	 
	 def "populateCommerceItemDisplayVO - CommerceItem info data populating successfully- happy flow" () {
		 
		 given :
		 
		 def skuId     = "bbbsku01"
		 def productId = "bbbprod01"
		 def seoUrl = "store/product/glasses"
		 def storeId = "store01"
		 def referenceNumber = "ref01"
		 def fullImagePath = "images.bedbath.com/images/image01"
		 def stockAvailability = 10
		 def commerceRepoItemId = "cItem01"
		 def displayName = "cotton pillow"
		 def thumbnailImage = "image.bedbath.com/images/thumbnail/imgsku01"
		 def smallImage = "image.bedbath.com/images/small/imgsku01"
		 
		 BBBCommerceItem commerceItemMock = Mock()
		 AuxiliaryData auxiliaryDataMock = Mock()
		 RepositoryItemMock skuRepoItemMock = new RepositoryItemMock(["id":skuId])
		 RepositoryItemMock productRepoItemMock = new RepositoryItemMock(["id":productId])
		 //GenericSecuredMutableRepositoryItem commerceRepoItemMock = new RepositoryItemMock(["id":commerceRepoItemId])
		 GenericSecuredMutableRepositoryItem commerceRepoItemMock = Mock()
		 SKUDetailVO skuVO = new SKUDetailVO()
		 CommerceItemVO commerceItemVO = new CommerceItemVO()
		 CommerceItemDisplayVO commerceItemDisplayVO = new CommerceItemDisplayVO()

		 populateCommerceItemMock(commerceItemMock, storeId, referenceNumber, fullImagePath)
		
		 populateSkuVO(skuVO, skuId)

		 commerceRepoItemMock.getPropertyValue("productId") >> productId
		 commerceRepoItemMock.getPropertyValue("seoUrl") >> seoUrl
		
		 commerceItemMock.getRepositoryItem() >> commerceRepoItemMock
		 commerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
		 
		 commerceItemVO.setBBBCommerceItem(commerceItemMock)
		 commerceItemVO.setStockAvailability(stockAvailability)
		 commerceItemVO.setSkuDetailVO(skuVO)
		 
		 skuRepoItemMock.setProperties(["displayName":displayName,"thumbnailImage":thumbnailImage,"smallImage":smallImage])
		 productRepoItemMock.setProperties(["seoUrl":seoUrl])
		 
		 auxiliaryDataMock.getCatalogRef() >> skuRepoItemMock
		 auxiliaryDataMock.getProductRef() >> productRepoItemMock
		 
		 when : 
		 
		 orderUtilitySpy.populateCommerceItemDisplayVO(commerceItemVO, commerceItemDisplayVO)
		 
		 then : 
		 
		 commerceItemDisplayVO.getProductSeoUrl().equals(seoUrl)
		 commerceItemDisplayVO.getProductId().equals(productId)
		 commerceItemDisplayVO.getSkuId().equals(skuId)
		 commerceItemDisplayVO.getStoreId().equals(storeId)
		 commerceItemDisplayVO.isBopusAllowed() == true
	 }

	 def "populateCommerceItemDisplayVO - CommerceItem and commerce display item are corrupted/invalid (null)" () {
		 
		 given :
		 
		 def skuId     = "bbbsku01"
		 def productId = "bbbprod01"
		 def seoUrl = "store/product/glasses"
		 def storeId = "store01"
		 def referenceNumber = "ref01"
		 def fullImagePath = "images.bedbath.com/images/image01"
		 def stockAvailability = 10
		 def commerceRepoItemId = "cItem01"
		 def displayName = "cotton pillow"
		 def thumbnailImage = "image.bedbath.com/images/thumbnail/imgsku01"
		 def smallImage = "image.bedbath.com/images/small/imgsku01"
		 
		 BBBCommerceItem commerceItemMock = Mock()
		 AuxiliaryData auxiliaryDataMock = Mock()
		 RepositoryItemMock skuRepoItemMock = new RepositoryItemMock(["id":skuId])
		 RepositoryItemMock productRepoItemMock = new RepositoryItemMock(["id":productId])
		 GenericSecuredMutableRepositoryItem commerceRepoItemMock = Mock()
		 SKUDetailVO skuVO = new SKUDetailVO()
		 CommerceItemVO commerceItemVO = null
		 CommerceItemDisplayVO commerceItemDisplayVO = new CommerceItemDisplayVO()

		 populateCommerceItemMock(commerceItemMock, storeId, referenceNumber, fullImagePath)
		
		 populateSkuVO(skuVO, skuId)

		 commerceRepoItemMock.getPropertyValue("productId")
		 commerceRepoItemMock.getPropertyValue("seoUrl")
		
		 commerceItemMock.getRepositoryItem() >> commerceRepoItemMock
		 commerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
		 
		 skuRepoItemMock.setProperties(["displayName":displayName,"thumbnailImage":thumbnailImage,"smallImage":smallImage])
		 productRepoItemMock.setProperties(["seoUrl":seoUrl])
		 
		 auxiliaryDataMock.getCatalogRef() >> skuRepoItemMock
		 auxiliaryDataMock.getProductRef() >> productRepoItemMock
		 
		 when :
		 
		 orderUtilitySpy.populateCommerceItemDisplayVO(null, commerceItemDisplayVO)
		 
		 then :
		 
		 commerceItemDisplayVO.getCommerceItemId() == null
	 }

	 def "populateCommerceItemDisplayVO - commerce display item is corrupted/invalid (null)" () {
		 
		 given :
		 
		 def skuId     = "bbbsku01"
		 def productId = "bbbprod01"
		 def seoUrl = "store/product/glasses"
		 def storeId = "store01"
		 def referenceNumber = "ref01"
		 def fullImagePath = "images.bedbath.com/images/image01"
		 def stockAvailability = 10
		 def commerceRepoItemId = "cItem01"
		 def displayName = "cotton pillow"
		 def thumbnailImage = "image.bedbath.com/images/thumbnail/imgsku01"
		 def smallImage = "image.bedbath.com/images/small/imgsku01"
		 
		 BBBCommerceItem commerceItemMock = Mock()
		 AuxiliaryData auxiliaryDataMock = Mock()
		 RepositoryItemMock skuRepoItemMock = new RepositoryItemMock(["id":skuId])
		 RepositoryItemMock productRepoItemMock = new RepositoryItemMock(["id":productId])
		 //GenericSecuredMutableRepositoryItem commerceRepoItemMock = new RepositoryItemMock(["id":commerceRepoItemId])
		 GenericSecuredMutableRepositoryItem commerceRepoItemMock = Mock()
		 SKUDetailVO skuVO = new SKUDetailVO()
		 CommerceItemVO commerceItemVO = new CommerceItemVO()
		 CommerceItemDisplayVO commerceItemDisplayVO = null

		 populateCommerceItemMock(commerceItemMock, storeId, referenceNumber, fullImagePath)
		
		 populateSkuVO(skuVO, skuId)

		 commerceRepoItemMock.getPropertyValue("productId")
		 commerceRepoItemMock.getPropertyValue("seoUrl")
		
		 commerceItemMock.getRepositoryItem() >> commerceRepoItemMock
		 commerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
		 
		  commerceItemVO.setBBBCommerceItem(commerceItemMock)
		  commerceItemVO.setStockAvailability(stockAvailability)
		  commerceItemVO.setSkuDetailVO(skuVO)
		 
		 skuRepoItemMock.setProperties(["displayName":displayName,"thumbnailImage":thumbnailImage,"smallImage":smallImage])
		 productRepoItemMock.setProperties(["seoUrl":seoUrl])
		 
		 auxiliaryDataMock.getCatalogRef() >> skuRepoItemMock
		 auxiliaryDataMock.getProductRef() >> productRepoItemMock
		 
		 when :
		 
		 orderUtilitySpy.populateCommerceItemDisplayVO(commerceItemVO, commerceItemDisplayVO)
		 
		 then :
		 
		 commerceItemDisplayVO == null
		 0 * commerceItemDisplayVO.setStoreId(storeId)
	 }

	 def "populateCommerceItemDisplayVO - Product in commerce item is corrupted/invalid(null)" () {
		 
		 given :
		 
		 def skuId     = "bbbsku01"
		 def productId = "bbbprod01"
		 def seoUrl = "store/product/glasses"
		 def storeId = "store01"
		 def referenceNumber = "ref01"
		 def fullImagePath = "images.bedbath.com/images/image01"
		 def stockAvailability = 10
		 def commerceRepoItemId = "cItem01"
		 def displayName = "cotton pillow"
		 def thumbnailImage = "image.bedbath.com/images/thumbnail/imgsku01"
		 def smallImage = "image.bedbath.com/images/small/imgsku01"
		 
		 BBBCommerceItem commerceItemMock = Mock()
		 AuxiliaryData auxiliaryDataMock = Mock()
		 RepositoryItemMock skuRepoItemMock = new RepositoryItemMock(["id":skuId])
		 RepositoryItemMock productRepoItemMock = null
		 GenericSecuredMutableRepositoryItem commerceRepoItemMock = Mock()
		 SKUDetailVO skuVO = new SKUDetailVO()
		 CommerceItemVO commerceItemVO = new CommerceItemVO()
		 CommerceItemDisplayVO commerceItemDisplayVO = new CommerceItemDisplayVO()

		 populateCommerceItemMock(commerceItemMock, storeId, referenceNumber, fullImagePath)
		
		 populateSkuVO(skuVO, skuId)

		 commerceRepoItemMock.getPropertyValue("productId") >> productId
		 commerceRepoItemMock.getPropertyValue("seoUrl") >> seoUrl
		
		 commerceItemMock.getRepositoryItem() >> commerceRepoItemMock
		 commerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
		 
		 commerceItemVO.setBBBCommerceItem(commerceItemMock)
		 commerceItemVO.setStockAvailability(stockAvailability)
		 commerceItemVO.setSkuDetailVO(skuVO)
		 
		 skuRepoItemMock.setProperties(["displayName":displayName,"thumbnailImage":thumbnailImage,"smallImage":smallImage])
		 
		 auxiliaryDataMock.getCatalogRef() >> skuRepoItemMock
		 auxiliaryDataMock.getProductRef() >> productRepoItemMock
		 
		 when :
		 
		 orderUtilitySpy.populateCommerceItemDisplayVO(commerceItemVO, commerceItemDisplayVO)
		 
		 then :
		 
		 commerceItemDisplayVO.getProductSeoUrl() == null
		 commerceItemDisplayVO.getProductId().equals(productId)
		 commerceItemDisplayVO.getSkuId().equals(skuId)
		 commerceItemDisplayVO.getStoreId().equals(storeId)
		 commerceItemDisplayVO.isBopusAllowed() == true
	 }
	 	 
	 
	 /*====================================================================================
	  * populateCommerceItemDisplayVO - test cases - ENDS					   			  *
	  * ===================================================================================
	  */
	 
	 
	 /*========================================================================================
	  * populateOrderDetails - test cases - STARTS						  		 	          *
	  * 																				  	  *
	  * Method signature : 																      *
	  * 																				      *
	  * public static BBBOrderVO populateOrderDetails(BBBOrderImpl order, BBBOrderVO orderVO) *
	  * =======================================================================================
	  */
	 
	  def "populateOrderDetails - order details populated - successfully" () {
		  
		  given : 
		  
		  def orderId = "order01"
		  def transientProfile = true
		  def mobileNumber = "9876543210"
		  def alternatePhoneNumber = "1234567890"
		  def phoneNumber = "2345678901"
		  def country = "USA"
		  def date = new Date()
		  def stateDetail = "submitted"
		  def expressCheckout = true
		  def subStatus = "In transit"
		  def emailSignUp = true 
		  
		  BBBOrderVO orderVo = new BBBOrderVO()
		  BBBOrderImpl orderMock = Mock()
		  BBBRepositoryContactInfo repositoryContactInfo = Mock()
		  BBBCommerceItem commerceItemMock = Mock()
		  
		  List<CommerceItem> commerceItems = new ArrayList<>()
		  List<ShippingGroupDisplayVO> shippingGroups = new ArrayList<>()
		  
		  commerceItems.add(commerceItemMock)
		  
		  populateContactInfo(repositoryContactInfo, mobileNumber, alternatePhoneNumber)
		  
		  populateOrderMock(orderMock, commerceItems, repositoryContactInfo, country, orderId, transientProfile, shippingGroups, date, stateDetail, expressCheckout, subStatus, emailSignUp)
		  
		  when : 
		   
		  BBBOrderVO resultOrder =  orderUtilitySpy.populateOrderDetails(orderMock, orderVo)
		  
		  then :
		  
		  resultOrder.getOrderId().equals(orderId) 
		  resultOrder.isEmailSignUp() == emailSignUp
		  resultOrder.getBillingAddress().getCountry() == country
	  }

	  def "populateOrderDetails - Canadian order | state details corrupted/invalid (empty)" () {
		  
		  given :
		  
		  def orderId = "order01"
		  def transientProfile = true
		  def mobileNumber = "9876543210"
		  def alternatePhoneNumber = "1234567890"
		  def phoneNumber = "2345678901"
		  def country = "Canada"
		  def date = new Date()
		  def stateDetail = ""
		  def expressCheckout = true
		  def subStatus = "In transit"
		  def emailSignUp = true
		  
		  BBBOrderVO orderVo = new BBBOrderVO()
		  BBBOrderImpl orderMock = Mock()
		  BBBRepositoryContactInfo repositoryContactInfo = Mock()
		  BBBCommerceItem commerceItemMock = Mock()
		  
		  List<CommerceItem> commerceItems = new ArrayList<>()
		  List<ShippingGroupDisplayVO> shippingGroups = new ArrayList<>()
		  
		  commerceItems.add(commerceItemMock)
		  
		  populateContactInfo(repositoryContactInfo, mobileNumber, alternatePhoneNumber)
		  
		  populateOrderMock(orderMock, commerceItems, repositoryContactInfo, country, orderId, transientProfile, shippingGroups, date, stateDetail, expressCheckout, subStatus, emailSignUp)
		  
		  when :
		   
		  BBBOrderVO resultOrder =  orderUtilitySpy.populateOrderDetails(orderMock, orderVo)
		  
		  then :
		  
		  resultOrder.getOrderId().equals(orderId)
		  resultOrder.isEmailSignUp() == emailSignUp
		  resultOrder.getBillingAddress().getCountry() == country
	  }
	  
	  def "populateOrderDetails - orderVO is corrupted/invalid (null)" () {
		  
		  given :
		 
		  BBBOrderVO orderVo = null
		  BBBOrderImpl orderMock = Mock()
		  
		  when :
		   
		  BBBOrderVO resultOrder =  orderUtilitySpy.populateOrderDetails(orderMock, orderVo)
		  
		  then :
		  
		  resultOrder == null
		  
	  }
	  
	  def "populateOrderDetails - order object is corrupted/invalid (null)" () {
		  
		  given :
		  
		  BBBOrderVO orderVo = new BBBOrderVO()
		  BBBOrderImpl orderMock = null
		  
		  when :
		   
		  BBBOrderVO resultOrder =  orderUtilitySpy.populateOrderDetails(orderMock, orderVo)
		  
		  then :
		  
		  resultOrder.getOrderId() == null
		  resultOrder.isEmailSignUp() == false
		  resultOrder.getBillingAddress() == null
		  
	  }
	  
	  def "populateOrderDetails - shipping address is invalid/corrupted (null)" () {
		  
		  given :
		  
		  def orderId = "order01"
		  def transientProfile = true
		  def mobileNumber = "9876543210"
		  def alternatePhoneNumber = "1234567890"
		  def phoneNumber = "2345678901"
		  def country = "USA"
		  def date = new Date()
		  def stateDetail = "submitted"
		  def expressCheckout = true
		  def subStatus = "In transit"
		  def emailSignUp = true
		  
		  BBBOrderVO orderVo = new BBBOrderVO()
		  BBBOrderImpl orderMock = Mock()
		  BBBRepositoryContactInfo repositoryContactInfo = null
		  BBBCommerceItem commerceItemMock = Mock()
		  
		  List<CommerceItem> commerceItems = new ArrayList<>()
		  List<ShippingGroupDisplayVO> shippingGroups = new ArrayList<>()
		  
		  commerceItems.add(commerceItemMock)
		  
		  //populateContactInfo(repositoryContactInfo, mobileNumber, alternatePhoneNumber)
		  
		  populateOrderMock(orderMock, commerceItems, repositoryContactInfo, country, orderId, transientProfile, shippingGroups, date, stateDetail, expressCheckout, subStatus, emailSignUp)
		  
		  when :
		   
		  BBBOrderVO resultOrder =  orderUtilitySpy.populateOrderDetails(orderMock, orderVo)
		  
		  then :
		  
		  resultOrder.getOrderId().equals(orderId)
		  resultOrder.isEmailSignUp() == emailSignUp
		  resultOrder.getBillingAddress() == null
	  }
	  
	  def "populateOrderDetails - Country not present in billing address" () {
		  
		  given :
		  
		  def orderId = "order01"
		  def transientProfile = true
		  def mobileNumber = "9876543210"
		  def alternatePhoneNumber = "1234567890"
		  def phoneNumber = "2345678901"
		  def country = null
		  def date = new Date()
		  def stateDetail = "submitted"
		  def expressCheckout = true
		  def subStatus = "In transit"
		  def emailSignUp = true
		  
		  BBBOrderVO orderVo = new BBBOrderVO()
		  BBBOrderImpl orderMock = Mock()
		  BBBRepositoryContactInfo repositoryContactInfo = Mock()
		  BBBCommerceItem commerceItemMock = Mock()
		  
		  List<CommerceItem> commerceItems = new ArrayList<>()
		  List<ShippingGroupDisplayVO> shippingGroups = new ArrayList<>()
		  
		  commerceItems.add(commerceItemMock)
		  
		  populateContactInfo(repositoryContactInfo, mobileNumber, alternatePhoneNumber)
		  
		  populateOrderMock(orderMock, commerceItems, repositoryContactInfo, country, orderId, transientProfile, shippingGroups, date, stateDetail, expressCheckout, subStatus, emailSignUp)
		  
		  when :
		   
		  BBBOrderVO resultOrder =  orderUtilitySpy.populateOrderDetails(orderMock, orderVo)
		  
		  then :
		  
		  resultOrder.getOrderId().equals(orderId)
		  resultOrder.isEmailSignUp() == emailSignUp
		  resultOrder.getBillingAddress().getCountry() == country
	  }

	  def "populateOrderDetails - order submitted date is corrupted/invalid (null)" () {
		  
		  given :
		  
		  def orderId = "order01"
		  def transientProfile = true
		  def mobileNumber = "9876543210"
		  def alternatePhoneNumber = "1234567890"
		  def phoneNumber = "2345678901"
		  def country = "USA"
		  def date = null
		  def stateDetail = "submitted"
		  def expressCheckout = true
		  def subStatus = "In transit"
		  def emailSignUp = true
		  
		  BBBOrderVO orderVo = new BBBOrderVO()
		  BBBOrderImpl orderMock = Mock()
		  BBBRepositoryContactInfo repositoryContactInfo = Mock()
		  BBBCommerceItem commerceItemMock = Mock()
		  
		  List<CommerceItem> commerceItems = new ArrayList<>()
		  List<ShippingGroupDisplayVO> shippingGroups = new ArrayList<>()
		  
		  commerceItems.add(commerceItemMock)
		  
		  populateContactInfo(repositoryContactInfo, mobileNumber, alternatePhoneNumber)
		  
		  populateOrderMock(orderMock, commerceItems, repositoryContactInfo, country, orderId, transientProfile, shippingGroups, date, stateDetail, expressCheckout, subStatus, emailSignUp)
		  
		  when :
		   
		  BBBOrderVO resultOrder =  orderUtilitySpy.populateOrderDetails(orderMock, orderVo)
		  
		  then :
		  
		  resultOrder.getOrderId().equals(orderId)
		  resultOrder.isEmailSignUp() == emailSignUp
		  resultOrder.getBillingAddress().getCountry() == country
		  resultOrder.getSubmittedDate() == date
	  }
	  
	  
	 
	 /*==============================================
	  * populateOrderDetails - test cases - ENDS	*
	  * =============================================
	  */

	  /*=======================================================================================
	   * populateCreditCardPaymentGrp - test cases - STARTS						  		 	  *
	   * 																				  	  *
	   * Method signature : 															      *
	   * 																				      *
	   * public static PaymentGroupDisplayVO populateCreditCardPaymentGrp(			          *
	   * PaymentGroupDisplayVO paymentVO, BBBCreditCard paymentGrp) 			              *
	   * ======================================================================================
	   */
	  
	  def "populateCreditCardPaymentGrp - credit card payment group details populated successfully (happy flow)" () {
		  
		  given : 
		  
		  PaymentGroupDisplayVO paymentVO = new PaymentGroupDisplayVO()
		  BBBCreditCard paymentGrp = Mock()
		  
		  def amountCredited = 10.00
		  def amountAuthorized = 10.00
		  def amountDebited = 10.00
		  def currencyCode = "USD"
		  def paymentMethod = "creditCard"
		  def id = "pg01"
		  def stateAsUserResource = "Newyork"
		  
		  populatePaymentGroup(paymentGrp, amountCredited, amountAuthorized, amountDebited, currencyCode, id, paymentMethod, stateAsUserResource)
		  
		  when : 
		  
		  PaymentGroupDisplayVO paymentGroupDisplayVo = orderUtilitySpy.populateCreditCardPaymentGrp(paymentVO, paymentGrp)
		  
		  then : 
		  
		  paymentGroupDisplayVo.getAmountCredited() == amountCredited
		  paymentGroupDisplayVo.getAmountDebited() == amountDebited
		  paymentGroupDisplayVo.getAmountAuthorized() == amountAuthorized
		  paymentGroupDisplayVo.getCurrencyCode().equals(currencyCode)
	  }

	  /*=======================================================================================
	   * populateCreditCardPaymentGrp - test cases - ENDS						  		 	  *
	   * ======================================================================================
	   */

	  	  
	  /*=======================================================================================
	   * populatePayPalPaymentGrp - test cases - STARTS						  		 	  	  *
	   * 																				  	  *
	   * Method signature : 															      *
	   * 																				      *
	   * public static PaymentGroupDisplayVO populatePayPalPaymentGrp ( 					  *
	   * PaymentGroupDisplayVO paymentVO, Paypal paymentGrp)							  	  * 
	   * ======================================================================================
	   */
	
	  
	  def "populatePayPalPaymentGrp - Paypal payment group populated successfully (happy flow)" () {
		  
		  given :
		  
		  PaymentGroupDisplayVO paymentVO = new PaymentGroupDisplayVO()
		  Paypal paymentGrpMock = Mock()
		  
		  def paymentMethod = "paypal"
		  def payerEmail = "tester@yopmail.com"
		  
		  paymentGrpMock.getPaymentMethod() >> paymentMethod 
		  paymentGrpMock.getPayerEmail() >> payerEmail
		  
		  when : 
		  
		 PaymentGroupDisplayVO populatedPaymentGrpDisplayVO = orderUtilitySpy.populatePayPalPaymentGrp(paymentVO, paymentGrpMock)
		  
		  then : 
		  
		  populatedPaymentGrpDisplayVO.getPayerEmail().equals(payerEmail)
		  populatedPaymentGrpDisplayVO.getPaymentMethodType().equals(paymentMethod)
	  }
	  
	
	  /*===============================================
	   * populatePayPalPaymentGrp - test cases - ENDS *
	   * ==============================================
	   */
	  	 
	  
	  /*=======================================================================================
	   * populateGiftCardPaymentGrp - test cases - STARTS						  		 	  *
	   * 																				  	  *
	   * Method signature : 															      *
	   * 																				      *
	   * public static PaymentGroupDisplayVO populateGiftCardPaymentGrp (					  *  
	   * PaymentGroupDisplayVO paymentVO, BBBGiftCard paymentGrp) {							  *
	   * ======================================================================================
	   */
	  
	  
	  def "populateGiftCardPaymentGrp - populating gift card payment group successfully (happy flow)" () {
		 
		  
		  given :
		  
		  PaymentGroupDisplayVO paymentVO = new PaymentGroupDisplayVO()
		  BBBGiftCard paymentGrpMock = Mock()
		  
		  def amountCredited = 10.00
		  def amount = 30.00
		  def amountAuthorized = 30.00
		  def amountDebited = 15.00
		  def balance = 100.00
		  def currencyCode = "USD"
		  def paymentMethodId = "GC01"
		  def paymentMethod = "GiftCard"
		  def stateAsUserResource = "Florida"
		  def cardNumber = "4111111111111111"
		  
		  populateGiftCardPaymentGroup(paymentGrpMock, amountCredited, amount, amountAuthorized, amountDebited, balance, currencyCode, paymentMethodId, paymentMethod, stateAsUserResource, cardNumber)
		  
		  
		  when : 
		  
		  PaymentGroupDisplayVO populatedPaymentGrpDisplayVO = orderUtilitySpy.populateGiftCardPaymentGrp(paymentVO, paymentGrpMock)
		  
		  then : 
		  
		  populatedPaymentGrpDisplayVO.getAmountCredited() == amountCredited
		  populatedPaymentGrpDisplayVO.getAmount() == amount
		  populatedPaymentGrpDisplayVO.getAmountAuthorized() == amountAuthorized
		  populatedPaymentGrpDisplayVO.getBalanceAmtGiftCard() == balance
		  populatedPaymentGrpDisplayVO.getCurrencyCode() == currencyCode
		  populatedPaymentGrpDisplayVO.getPaymentMethodType() == paymentMethod
		  // can't verify the gift card number as it'll be encrypted
	  }
	  
	  def "populateGiftCardPaymentGrp - cardNumber corrupted/invalid(empty) while populating gift card payment group" () {
		  
		   
		   given :
		   
		   PaymentGroupDisplayVO paymentVO = new PaymentGroupDisplayVO()
		   BBBGiftCard paymentGrpMock = Mock()
		   
		   def amountCredited = 10.00
		   def amount = 30.00
		   def amountAuthorized = 30.00
		   def amountDebited = 15.00
		   def balance = 100.00
		   def currencyCode = "USD"
		   def paymentMethodId = "GC01"
		   def paymentMethod = "GiftCard"
		   def stateAsUserResource = "Florida"
		   def cardNumber = ""
		   
		   populateGiftCardPaymentGroup(paymentGrpMock, amountCredited, amount, amountAuthorized, amountDebited, balance, currencyCode, paymentMethodId, paymentMethod, stateAsUserResource, cardNumber)
		   
		   
		   when :
		   
		   PaymentGroupDisplayVO populatedPaymentGrpDisplayVO = orderUtilitySpy.populateGiftCardPaymentGrp(paymentVO, paymentGrpMock)
		   
		   then :
		   
		   populatedPaymentGrpDisplayVO.getGiftCardNumber() == null
		   populatedPaymentGrpDisplayVO.getAmountCredited() == amountCredited
		   populatedPaymentGrpDisplayVO.getAmount() == amount
		   populatedPaymentGrpDisplayVO.getAmountAuthorized() == amountAuthorized
		   populatedPaymentGrpDisplayVO.getBalanceAmtGiftCard() == balance
		   populatedPaymentGrpDisplayVO.getCurrencyCode() == currencyCode
	   }

	 
	  
	  /*=======================================================================================
	   * populateGiftCardPaymentGrp - test cases - ENDS						  		 	  	  *
	   * ======================================================================================
	   */

	  /*=======================================================================================
	   * setCanadaMultipleShippingTax - test cases - STARTS						  		 	  *
	   * 																				  	  *
	   * Method signature : 															      *
	   * 																				      *
	   * public static void setCanadaMultipleShippingTax ( 									  *
	   * List<ShippingGroupDisplayVO> shippingGroups, 										  *
	   * PriceInfoDisplayVO priceInfoDisplayVO ) 											  *
	   * 							  														  *
	   * ======================================================================================
	   */
	  
	  def "setCanadaMultipleShippingTax - setting canada multiple shipping tax successfully (happy flow)" () {
		  
		  given :
		  
		  List<ShippingGroupDisplayVO> shippingGroups = new ArrayList<>()
		  PriceInfoDisplayVO priceInfoDisplayVO = new PriceInfoDisplayVO()
		  PriceInfoDisplayVO tempPriceInfoDisplayVO = new PriceInfoDisplayVO()
		  ShippingGroupDisplayVO shipGroupDisplayVO = new ShippingGroupDisplayVO()
		  
		  double totalStateTax = 15.0;
		  double totalCountyTax = 15.0;
		  
		  tempPriceInfoDisplayVO.setShippingStateLevelTax(totalStateTax)
		  tempPriceInfoDisplayVO.setShippingCountyLevelTax(totalCountyTax)
		  
		  shipGroupDisplayVO.setShippingPriceInfoDisplayVO(tempPriceInfoDisplayVO)
		  shippingGroups.add(shipGroupDisplayVO)
		  
		  when : 
		  
		  orderUtilitySpy.setCanadaMultipleShippingTax(shippingGroups, priceInfoDisplayVO)
		  
		  then :
		  
		  priceInfoDisplayVO.getShippingStateLevelTax() == totalStateTax
		  priceInfoDisplayVO.getShippingCountyLevelTax() == totalCountyTax
	  }
	  
	  def "setCanadaMultipleShippingTax - shipping groups invalid/corrupted (empty)" () {
		  
		  given :
		  
		  List<ShippingGroupDisplayVO> shippingGroups = new ArrayList<>()
		  PriceInfoDisplayVO priceInfoDisplayVO = new PriceInfoDisplayVO()
		  PriceInfoDisplayVO tempPriceInfoDisplayVO = new PriceInfoDisplayVO()

		  when :
		  
		  orderUtilitySpy.setCanadaMultipleShippingTax(shippingGroups, priceInfoDisplayVO)
		  
		  then :
		  
		  priceInfoDisplayVO.getShippingStateLevelTax() == 0.0
		  priceInfoDisplayVO.getShippingCountyLevelTax() == 0.0
	  }
	  
	  
	  /*=======================================================================================
	   * setCanadaMultipleShippingTax - test cases - ENDS						  		 	  *
	   * ======================================================================================
	   */
	

	  
	  
	  		  
	 
	 /*
	  * Data populating methods - STARTS
	  *
	  */

	private populateContactInfo(BBBRepositoryContactInfo repositoryContactInfo, String mobileNumber, String alternatePhoneNumber) {
		
		MutableRepositoryItem mutableRepositoryItemMock = Mock()
		RepositoryItemDescriptor repositoryItemDescriptorMock = Mock()
		repositoryContactInfo.mRepositoryItem = mutableRepositoryItemMock

		mutableRepositoryItemMock.getItemDescriptor() >> repositoryItemDescriptorMock

		repositoryItemDescriptorMock.hasProperty(BBBPropertyNameConstants.MOBILE_NUMBER) >> true
		mutableRepositoryItemMock.getPropertyValue(BBBPropertyNameConstants.MOBILE_NUMBER) >> mobileNumber
		repositoryItemDescriptorMock.hasProperty("address1") >> true
		mutableRepositoryItemMock.getPropertyValue("address1") >> "Address 1"
		repositoryItemDescriptorMock.hasProperty(BBBPropertyNameConstants.ALTERNATE_PHONE_NUMBER) >> true
		mutableRepositoryItemMock.getPropertyValue(BBBPropertyNameConstants.ALTERNATE_PHONE_NUMBER) >> alternatePhoneNumber
		repositoryItemDescriptorMock.hasProperty("phoneNumber") >> true
		mutableRepositoryItemMock.getPropertyValue("phoneNumber") >> phoneNumber
		repositoryItemDescriptorMock.hasProperty("emailaddress") >> true
		mutableRepositoryItemMock.getPropertyValue("emailaddress") >> shippingConfirmationEmail
	}
	
	private populateShippingGroup(List commerceItemRelationshipList, HashMap specialInstructions, boolean containsGiftWrap, Date shipOnDate, boolean containsGiftWrapMessage, String giftWrapMessage, BBBRepositoryContactInfo repositoryContactInfo, String shippingConfirmationEmail, String sourceId) {
		//hardGoodShippingGroup.getPropertyValue("shipmentTracking") >> shipmentTrackingList
		hardGoodShippingGroup.getCommerceItemRelationships() >> commerceItemRelationshipList
		hardGoodShippingGroup.getSpecialInstructions() >> specialInstructions
		hardGoodShippingGroup.isContainsGiftWrap() >> containsGiftWrap
		hardGoodShippingGroup.getShipOnDate() >> shipOnDate
		hardGoodShippingGroup.getContainsGiftWrapMessage() >> containsGiftWrapMessage
		hardGoodShippingGroup.getGiftWrapMessage() >> giftWrapMessage
		hardGoodShippingGroup.getShippingAddress() >> repositoryContactInfo
		hardGoodShippingGroup.getShippingConfirmationEmail() >> shippingConfirmationEmail
		hardGoodShippingGroup.getSourceId() >> sourceId
	}
	
	private populateSkuVO(SKUDetailVO skuVO, String skuId) {
		def giftWrapEligible = true
		def commaSepNonShipableStates = "HI,AK"
		def shippingSurCharge = 10.00
		def description = "Sample description"
		def color = "red"
		def skuSize = "10"
		def upc = "upc01"
		def bopusAllowed = true
		def hasRebate = true
		def onSale = true
		def ecoFeeEligible = true
		def ltlItem = true
		def webOfferedItem = true
		def intlRestricted = true
		def disableFlag = true
		List<ShipMethodVO> shipMethodVOList = new ArrayList<>()
		ShipMethodVO shipMethodVo = new ShipMethodVO()
		RepositoryItemMock skuRepositoryItemMock = new RepositoryItemMock(["id":skuId]) 
		skuVO.setSkuRepositoryItem(skuRepositoryItemMock)
		skuRepositoryItemMock.setProperties(["bopusExclusion":bopusAllowed])
		//skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> bopusAllowed
		
		shipMethodVOList.add(shipMethodVo)
		
		skuVO.setEligibleShipMethods(shipMethodVOList)
		skuVO.setGiftWrapEligible(giftWrapEligible)
		skuVO.setCommaSepNonShipableStates(commaSepNonShipableStates)
		skuVO.setSkuId(skuId)
		skuVO.setShippingSurcharge(shippingSurCharge)
		skuVO.setDescription(description)
		skuVO.setColor(color)
		skuVO.setSize(skuSize)
		skuVO.setUpc(upc)
		skuVO.setBopusAllowed(bopusAllowed)
		skuVO.setHasRebate(hasRebate)
		skuVO.setOnSale(onSale)
		skuVO.setIsEcoFeeEligible(ecoFeeEligible)
		skuVO.setLtlItem(ltlItem)
		skuVO.setWebOfferedFlag(webOfferedItem)
		skuVO.setIntlRestricted(intlRestricted)
		skuVO.setDisableFlag(disableFlag)
	}

	private populateCommerceItemMock(BBBCommerceItem commerceItemMock, String storeId, String referenceNumber, String fullImagePath) {
		commerceItemMock.getStoreId()  >> storeId
		commerceItemMock.getReferenceNumber() >> referenceNumber
		commerceItemMock.getFullImagePath() >> fullImagePath
	}

	private populateOrderMock(BBBOrderImpl orderMock, List commerceItems, BBBRepositoryContactInfo repositoryContactInfo, String country, String orderId, boolean transientProfile, List shippingGroups, Date date, String stateDetail, boolean expressCheckout, String subStatus, boolean emailSignUp) {
		orderMock.getCommerceItems() >> commerceItems
		repositoryContactInfo.getCountry() >> country
		orderMock.getId() >> orderId
		orderMock.isTransient() >> transientProfile
		orderMock.getBillingAddress() >> repositoryContactInfo
		orderMock.getShippingGroups() >> shippingGroups
		orderMock.getSubmittedDate() >> date
		orderMock.getStateDetail() >> stateDetail
		orderMock.isExpressCheckOut() >> expressCheckout
		orderMock.getSubStatus() >> subStatus
		orderMock.getPropertyValue("emailSignUp") >> emailSignUp
	}

	private populatePaymentGroup(BBBCreditCard paymentGrp, BigDecimal amountCredited, BigDecimal amountAuthorized, BigDecimal amountDebited, String currencyCode, String id, String paymentMethod, String stateAsUserResource) {
		paymentGrp.getAmountCredited() >> amountCredited
		paymentGrp.getAmountAuthorized() >> amountAuthorized
		paymentGrp.getAmountDebited() >> amountDebited
		paymentGrp.getCurrencyCode() >> currencyCode
		paymentGrp.getId() >> id
		paymentGrp.getPaymentMethod() >> paymentMethod
		paymentGrp.getStateAsUserResource() >> stateAsUserResource
	}

	private populateGiftCardPaymentGroup(BBBGiftCard paymentGrpMock, double amountCredited, double amount, double amountAuthorized, double amountDebited, double balance, String currencyCode, String paymentMethodId, String paymentMethod, String stateAsUserResource, String cardNumber) {
		paymentGrpMock.getAmountCredited() >> amountCredited
		paymentGrpMock.getAmount() >> amount
		paymentGrpMock.getAmountAuthorized() >> amountAuthorized
		paymentGrpMock.getAmountDebited() >> amountDebited
		paymentGrpMock.getPropertyValue("balance") >> balance
		paymentGrpMock.getCurrencyCode() >> currencyCode
		paymentGrpMock.getId() >> paymentMethodId
		paymentGrpMock.getPaymentMethod() >> paymentMethod
		paymentGrpMock.getStateAsUserResource() >> stateAsUserResource
		paymentGrpMock.getPropertyValue("cardNumber") >> cardNumber
	}

	private populatePriceInfoVOBasic(PriceInfoVO priceInfoVO) {
		
		populatePriceInfoTotal(priceInfoVO) 
		populatePriceInfoVOCount(priceInfoVO)
		populatePriceInfoVOAmount(priceInfoVO)
		populatePriceInfoVOCharges(priceInfoVO)
		
		priceInfoVO.getShippingGroupItemsTotal() >> 12
		priceInfoVO.setShippingLevelTax(14)
		priceInfoVO.setTotalTax(15)
		
		priceInfoVO.setFreeShipping(false)
		priceInfoVO.setDeliverySurchargeProrated(3.00) 
		priceInfoVO.setMaxDeliverySurchargeReached(true)
		
		priceInfoVO.setDeliverySurchargeSaving(15.00)
		priceInfoVO.setAssemblyFee(16.00)
		priceInfoVO.setTotalAssemblyFee(17.00)
		
		priceInfoVO.setMaxDeliverySurcharge(18.00)
		priceInfoVO.setDeliverySurcharge(19.00)
		
		priceInfoVO.setTotalDeliverySurcharge(20.00)
		
	}

	private populatePriceInfoVOCharges(PriceInfoVO priceInfoVO) {
		priceInfoVO.setTotalSurcharge(5)
		priceInfoVO.setFinalShippingCharge(15)
		priceInfoVO.setSurchargeSavings(8)
		priceInfoVO.setShippingSavings(7)
		priceInfoVO.setTotalSavedPercentage(8.33)
	}

	private populatePriceInfoVOAmount(PriceInfoVO priceInfoVO) {
		priceInfoVO.setTotalShippingAmount(150)
		priceInfoVO.setStoreAmount(10)
		priceInfoVO.setOrderPreTaxAmout(150)
		priceInfoVO.setTotalAmount(160)
		priceInfoVO.setTotalSavedAmount(15)
	}

	private populatePriceInfoVOCount(PriceInfoVO priceInfoVO) {
		priceInfoVO.setUndiscountedItemsCount(3)
		priceInfoVO.setItemCount(10)
		priceInfoVO.setHardgoodShippingGroupItemCount(2)
		priceInfoVO.setStorePickupShippingGroupItemCount(3)
	}

	private populatePriceInfoTotal(PriceInfoVO priceInfoVO) {
		priceInfoVO.setShippingGroupItemsTotal(10.00)
		priceInfoVO.setShippingGroupItemTotal(2.00)
		priceInfoVO.setEcoFeeTotal(1.24)
		priceInfoVO.setOnlineTotal(160)
		priceInfoVO.setStoreEcoFeeTotal(15)
		priceInfoVO.setOnlinePurchaseTotal(150)
		priceInfoVO.setOnlineEcoFeeTotal(15)
		priceInfoVO.setGiftWrapTotal(5)
		priceInfoVO.setRawShippingTotal(150)
	}

	private isPriceInfoPopulated(PriceInfoDisplayVO resultPriceInfoDisplay, PriceInfoVO priceInfoVO, BigDecimal giftCardAmount) {
		resultPriceInfoDisplay.getShippingGroupItemsTotal() == priceInfoVO.getShippingGroupItemsTotal()
		resultPriceInfoDisplay.getShippingGroupItemTotal()  == priceInfoVO.getShippingGroupItemTotal()
		resultPriceInfoDisplay.getUndiscountedItemsCount() == priceInfoVO.getUndiscountedItemsCount()
		resultPriceInfoDisplay.getItemCount() == priceInfoVO.getItemCount()
		resultPriceInfoDisplay.getTotalShippingAmount() == priceInfoVO.getTotalShippingAmount()
		resultPriceInfoDisplay.getEcoFeeTotal()   == priceInfoVO.getEcoFeeTotal()
		resultPriceInfoDisplay.getTotalSavedPercentage()  == priceInfoVO.getTotalSavedPercentage()
		resultPriceInfoDisplay.getShippingLevelTax() == priceInfoVO.getShippingLevelTax()
		resultPriceInfoDisplay.getStoreAmount() == priceInfoVO.getStoreAmount()
		resultPriceInfoDisplay.getStoreEcoFeeTotal() == priceInfoVO.getStoreEcoFeeTotal()
		resultPriceInfoDisplay.getOnlinePurchaseTotal() == priceInfoVO.getOnlinePurchaseTotal()
		resultPriceInfoDisplay.getOnlineEcoFeeTotal() == priceInfoVO.getOnlineEcoFeeTotal()
		resultPriceInfoDisplay.getGiftWrapTotal() == priceInfoVO.getGiftWrapTotal()
		resultPriceInfoDisplay.getRawShippingTotal() == priceInfoVO.getRawShippingTotal()
		resultPriceInfoDisplay.getTotalSurcharge() == priceInfoVO.getTotalSurcharge()
		resultPriceInfoDisplay.getTotalTax() == priceInfoVO.getTotalTax()
		resultPriceInfoDisplay.getOnlineTotal() == priceInfoVO.getOnlineTotal()
		resultPriceInfoDisplay.getOrderPreTaxAmount() == priceInfoVO.getOrderPreTaxAmout()
		resultPriceInfoDisplay.getTotalAmount() == priceInfoVO.getTotalAmount()
		resultPriceInfoDisplay.getFreeShipping() == priceInfoVO.getFreeShipping()
		resultPriceInfoDisplay.getHardgoodShippingGroupItemCount() == priceInfoVO.getHardgoodShippingGroupItemCount()
		resultPriceInfoDisplay.getStorePickupShippingGroupItemCount() == priceInfoVO.getStorePickupShippingGroupItemCount()
		resultPriceInfoDisplay.getTotalSavedAmount() == priceInfoVO.getTotalSavedAmount()
		resultPriceInfoDisplay.getFinalShippingCharge() == priceInfoVO.getFinalShippingCharge()
		resultPriceInfoDisplay.getUnitSalePrice() == priceInfoVO.getUnitSalePrice()
		resultPriceInfoDisplay.getTotalGiftCardAmt() == giftCardAmount
		resultPriceInfoDisplay.getDeliverySurcharge() == priceInfoVO.getDeliverySurcharge()
		resultPriceInfoDisplay.getDeliverySurchargeProrated() == priceInfoVO.getDeliverySurchargeProrated()
		resultPriceInfoDisplay.getDeliverySurchargeSaving() == priceInfoVO.getDeliverySurchargeSaving()
		resultPriceInfoDisplay.getAssemblyFee() == priceInfoVO.getAssemblyFee()
		resultPriceInfoDisplay.getTotalAssemblyFee() == priceInfoVO.getTotalAssemblyFee()
		resultPriceInfoDisplay.isMaxDeliverySurchargeReached() == priceInfoVO.isMaxDeliverySurchargeReached()
		resultPriceInfoDisplay.getMaxDeliverySurcharge() != priceInfoVO.getMaxDeliverySurcharge()
		resultPriceInfoDisplay.getTotalDeliverySurcharge() == priceInfoVO.getTotalDeliverySurcharge()
	}


		
	/*
	 * Data populating methods - ENDS
	 *
	 */
	
}
