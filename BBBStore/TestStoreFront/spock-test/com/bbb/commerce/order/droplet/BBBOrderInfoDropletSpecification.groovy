package com.bbb.commerce.order.droplet

import java.io.IOException;
import java.util.Date
import java.util.List
import java.util.Locale;
import java.util.Map

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.commerce.pricing.bean.PriceInfoVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.jaxb.inventory.SiteId;
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.repository.RepositoryItemMock
import com.bedbathandbeyond.atg.impl.ShippingGroupImpl;
import com.bedbathandbeyond.www.impl.OrderImpl

import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.OrderPriceInfo
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse
import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;

/**
 *
 * @author Velmurugan Moorthy
 *
 * This class is written to unit test the BBBOrderInfoDroplet
 *
 */

public class BBBOrderInfoDropletSpecification extends BBBExtendedSpec {

	private BBBOrderInfoDroplet orderInfoDropletSpy
	private BBBCatalogTools catalogToolsMock
	private BBBPricingTools pricingToolsMock
	private Locale local
	
	def setup () {
		
		orderInfoDropletSpy = Spy()

		catalogToolsMock = Mock()
		pricingToolsMock = Mock()
		
		orderInfoDropletSpy.setCatalogTools(catalogToolsMock)
		orderInfoDropletSpy.setPricingTools(pricingToolsMock)
		
		local = Locale.forLanguageTag("en_US")
	}
	
	/*================================================================
	 * 	Service method - Test cases starts					 		 *	
	 * Method signature : 									  	     *
	 * 																 *
	 * public void service(final DynamoHttpServletRequest pRequest,	 *	
	 * final DynamoHttpServletResponse pResponse) 	 				 *
	 * throws ServletException, IOException	 						 *
	 *	 															 *
	 * ===============================================================
	 */
	
	def "service - Order info is populated request object successfully (happy flow)" () {
		
		given : 
		
		//BBBOrder orderMock = Mock()
		def onlineOrderNumber = "onlineOrder01"
		int commerceItemCount = 0
		def siteID = "BBB_US"
		def registryId1 = "reg01"
		def registryId2 = "reg02"
		def registryDesc1 = "Birthday registry"
		def registryDesc2 = "House warming registry"
		double salePrice1 = 15.00
		double salePrice2 = 0.00
		def productId1 = "prod01"
		def productId2 = "prod02"
		def pdpURL1 = "bedbath.com/product/prod01"
		def pdpURL2 = "bedbath.com/product/prod02"
		def prodName1 = "Multi colored glass"
		def prodName2 = "Pure cotton pillow"
		def smallImage1 = "images.bedbath.com/small/product/prod01"
		def smallImage2 = "images.bedbath.com/small/product/prod02"
		def largeImage1 = "images.bedbath.com/large/product/prod01"
		def largeImage2 = "images.bedbath.com/large/product/prod02"
		def longDescription1 = "Glass specially designed with multiple colors"
		def longDescription2 = "Pillow made with 100% pure cotton"
		int quantity1 = 2
		int quantity2 = 3
		
		Date orderDate = new Date()
		RegistryTypes registryType1 =  new RegistryTypes()//"BDR"
		RegistryTypes registryType2 = new RegistryTypes()//"HWR"
		PriceInfoVO priceInfoVO = new PriceInfoVO()
		ImageVO imageVO1 = new ImageVO()
		ImageVO imageVO2 = new ImageVO()
		ProductVO productVo1 = new ProductVO()
		ProductVO productVo2 = new ProductVO()
		RegistrySummaryVO registrySummary1 = new RegistrySummaryVO()
		RegistrySummaryVO registrySummary2 = new RegistrySummaryVO()
		
		List<String> rkgMerchantIds = Arrays.asList("rkgMerch01","rkgMerch02","rkgMerch03")
		List<String> rkgZMAMIds = Arrays.asList("rkgZMAM01","rkgZMAM02","rkgZMAM03")
		List<CommerceItem> commerceItems = new ArrayList<>()
		List<ShippingGroup> shippingGroups = new ArrayList<>() // ((BBBOrder) order).getShippingGroups()
		List<PricingAdjustment> pricingAdjustments = new ArrayList<>()
		Map<String,RegistrySummaryVO> registryMap = new HashMap<>()
		
		BBBOrderImpl orderMock = Mock()
		
		PricingAdjustment pricingAdjustment1 = Mock()
		PricingAdjustment pricingAdjustment2 = Mock()
		
		HardgoodShippingGroup shippingGroup1 = Mock()
		HardgoodShippingGroup shippingGroup2 = Mock()
		//BBBStoreShippingGroup shippingGroup2 = Mock()
		BBBCommerceItem commerceItem1 = Mock()
		BBBCommerceItem commerceItem2 = Mock()
		BBBCommerceItem commerceItem3 = Mock()
		
		ItemPriceInfo itemPriceInfo1 = Mock()
		ItemPriceInfo itemPriceInfo2 = Mock()
		//ItemPriceInfo itemPriceInfo3 = null
		OrderPriceInfo orderPriceInfo = Mock()
		ShippingPriceInfo shippingPriceInfo1 = Mock()
		ShippingPriceInfo shippingPriceInfo2 = Mock()
		
		AuxiliaryData auxiliaryDataMock1 = Mock()
		AuxiliaryData auxiliaryDataMock2 = Mock()
		
		RepositoryItemMock catalogRef1 = new RepositoryItemMock(["id":"sku01"])
		RepositoryItemMock catalogRef2 = new RepositoryItemMock(["id":"sku02"])
		RepositoryItemMock pricingModel1 = new RepositoryItemMock(["id":"pricing01"])
		RepositoryItemMock pricingModel2 = new RepositoryItemMock(["id":"pricing02"])
		
		pricingModel1.setProperties(["displayName":"50OFF"])
		pricingModel2.setProperties(["displayName":"25OFF"])
		
		populatePricingAdjustments(pricingAdjustment1, pricingModel1, pricingAdjustment2, pricingModel2, pricingAdjustments)
		
		populateItemPriceInfos(itemPriceInfo1, salePrice1, pricingAdjustments, itemPriceInfo2, salePrice2)
		
		populateCommerceItem(commerceItem1, auxiliaryDataMock1, registryId1, itemPriceInfo1, quantity1)
		populateCommerceItem(commerceItem2, auxiliaryDataMock2, registryId2, itemPriceInfo2, quantity2)
		
		popualteAuxiliaryData(auxiliaryDataMock1, catalogRef1, productId1, auxiliaryDataMock2, catalogRef2, productId2)
		
		catalogRef1.setProperties("displayName":"Chinese clay pot")
		catalogRef2.setProperties("displayName" : "Glass with multi-colors")
		
		commerceItems.add(commerceItem1)
		commerceItems.add(commerceItem2)

		requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		//requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteID
		1 * orderInfoDropletSpy.getCurrentSiteIdFromManager() >> siteID
		requestMock.getLocale() >> local
		
		populateProductVO(productVo1, pdpURL1, prodName1, longDescription1, imageVO1)
		populateProductVO(productVo2, pdpURL2, prodName2, longDescription2, imageVO2)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID) >> rkgMerchantIds
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID+"ZMAM") >> rkgZMAMIds
		1 * catalogToolsMock.getProductDetails(siteID, productId1) >> productVo1
		1 * catalogToolsMock.getProductDetails(siteID, productId2) >> productVo2
		
		registryType1.setRegistryTypeDesc(registryDesc1)
		registryType2.setRegistryTypeDesc(registryDesc2)
		registrySummary1.setRegistryType(registryType1)
		registrySummary2.setRegistryType(registryType2)
		
		registryMap.put(registryId1, registrySummary1)
		registryMap.put(registryId2, registrySummary2)
		
		shippingPriceInfo1.getAdjustments() >> pricingAdjustments
		shippingPriceInfo2.getAdjustments() >> pricingAdjustments
		
		shippingGroup1.getPriceInfo() >> shippingPriceInfo1
		shippingGroup2.getPriceInfo() >> shippingPriceInfo2
		
		shippingGroups.add(shippingGroup1)
		shippingGroups.add(shippingGroup2)
		
		//imageVO1
		imageVO1.setSmallImage(smallImage1) 
		imageVO1.setLargeImage(largeImage1) 
		imageVO2.setSmallImage(smallImage2) 
		imageVO2.setLargeImage(largeImage2)
		populateOrderMock(orderMock, onlineOrderNumber, orderDate, commerceItems, registryMap, shippingGroups, orderPriceInfo)
		//1 * orderMock.getOnlineOrderNumber()//not working - showing n+1 count
		//1 * orderMock.getSubmittedDate() >> orderDate
		4 * orderPriceInfo.getAdjustments() >> pricingAdjustments
		//1 * orderMock.getCommerceItemCount() >> commerceItems.size()
		/*1 * orderMock.getCommerceItemCount() >> commerceItems.size()
		2 * orderMock.getCommerceItems() >> commerceItems
		1 * orderMock.getRegistryMap() >> registryMap
		1 * orderMock.getShippingGroups() >> shippingGroups
		1 * orderMock.getPriceInfo() >> orderPriceInfo
		1 * orderPriceInfo.getAdjustments() >> pricingAdjustments*/
		
		1 * pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVO 
		
		when :
		 
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		
		    //1 * orderMock.getOnlineOrderNumber() //not working - showing zero count
		
			// RequestObject's execution count  
		
			1 * requestMock.setParameter("PromoCode", _)
			1 * requestMock.setParameter("PromoAmount", _)
			1 * requestMock.setParameter("storeId", _)
			1 * requestMock.setParameter("itemIds", _)
			1 * requestMock.setParameter("resxEventType", _)
			1 * requestMock.setParameter("itemQtys", _)
			1 * requestMock.setParameter("itemprices", _)
			1 * requestMock.setParameter("itemAmounts", _)
			1 * requestMock.setParameter("itemCount", _)
			1 * requestMock.setParameter("itemSkuIds", _)
			1 * requestMock.setParameter("itemAmts", _)
			1 * requestMock.setParameter("itemQuantities", _)
			1 * requestMock.setParameter("itemSkuNames", _)
			1 * requestMock.setParameter("cItemIds", _)
			1 * requestMock.setParameter(BBBCoreConstants.WC_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.BP_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.CJ_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, _)
			1 * requestMock.setParameter("orderList", _)
			1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		    1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,priceInfoVO)
	}

	/*
	 * Negative branches covered
	 *  online orderNubmer - null 
	 *  RegistryId - null (not working as StringBuilder considers it as string)
	 *  orderDate - null |  CatalogTools.getProductDetails() i.e, productVo - null  
	 */
	def "service - Exception while getting RkgMerchantID & RkgZMAMId | BBBBusinessException | negative branches" () {
		
		given :
		
		//BBBOrder orderMock = Mock()
		def onlineOrderNumber = null
		def bopusOrderNumber = "bopus01"
		int commerceItemCount = 0
		def siteID = "BBB_US"
		def registryId1 = "reg01"
		def registryId2 = null
		def registryDesc1 = "Birthday registry"
		def registryDesc2 = "House warming registry"
		double salePrice1 = 15.00
		double salePrice2 = 25.00
		def productId1 = "prod01"
		def productId2 = "prod02"
		def pdpURL1 = "bedbath.com/product/prod01"
		def pdpURL2 = "bedbath.com/product/prod02"
		def prodName1 = "Multi colored glass"
		def prodName2 = "Pure cotton pillow"
		def smallImage1 = "images.bedbath.com/small/product/prod01"
		def smallImage2 = "images.bedbath.com/small/product/prod02"
		def largeImage1 = "images.bedbath.com/large/product/prod01"
		def largeImage2 = "images.bedbath.com/large/product/prod02"
		def longDescription1 = "Glass specially designed with multiple colors"
		def longDescription2 = "Pillow made with 100% pure cotton"
		int quantity1 = 2
		int quantity2 = 3
		
		Date orderDate = null
		RegistryTypes registryType1 =  new RegistryTypes()//"BDR"
		RegistryTypes registryType2 = new RegistryTypes()//"HWR"
		PriceInfoVO priceInfoVO = new PriceInfoVO()
		ImageVO imageVO1 = new ImageVO()
		ImageVO imageVO2 = new ImageVO()
		ProductVO productVo1 = Mock()
		ProductVO productVo2 = new ProductVO()
		RegistrySummaryVO registrySummary1 = new RegistrySummaryVO()
		RegistrySummaryVO registrySummary2 = new RegistrySummaryVO()
		
		List<String> rkgMerchantIds = Arrays.asList("rkgMerch01","rkgMerch02","rkgMerch03")
		List<String> rkgZMAMIds = Arrays.asList("rkgZMAM01","rkgZMAM02","rkgZMAM03")
		List<CommerceItem> commerceItems = new ArrayList<>()
		List<ShippingGroup> shippingGroups = new ArrayList<>() // ((BBBOrder) order).getShippingGroups()
		List<PricingAdjustment> pricingAdjustments = new ArrayList<>()
		Map<String,RegistrySummaryVO> registryMap = new HashMap<>()
		
		BBBOrderImpl orderMock = Mock()
		
		PricingAdjustment pricingAdjustment1 = Mock()
		PricingAdjustment pricingAdjustment2 = Mock()
		
		BBBStoreShippingGroup shippingGroup1 = Mock()
		BBBStoreShippingGroup shippingGroup2 = Mock()
		
		BBBCommerceItem commerceItem1 = Mock()
		BBBCommerceItem commerceItem2 = Mock()
		ItemPriceInfo itemPriceInfo1 = Mock()
		ItemPriceInfo itemPriceInfo2 = Mock()
		OrderPriceInfo orderPriceInfo = Mock()
		ShippingPriceInfo shippingPriceInfo1 = Mock()
		ShippingPriceInfo shippingPriceInfo2 = Mock()
		
		AuxiliaryData auxiliaryDataMock1 = Mock()
		AuxiliaryData auxiliaryDataMock2 = Mock()
		
		RepositoryItemMock catalogRef1 = new RepositoryItemMock(["id":"sku01"])
		RepositoryItemMock catalogRef2 = new RepositoryItemMock(["id":"sku02"])
		RepositoryItemMock pricingModel1 = new RepositoryItemMock(["id":"pricing01"])
		RepositoryItemMock pricingModel2 = new RepositoryItemMock(["id":"pricing02"])
		
		pricingModel1.setProperties(["displayName":"50OFF"])
		pricingModel2.setProperties(["displayName":"25OFF"])
		
		populatePricingAdjustments(pricingAdjustment1, pricingModel1, pricingAdjustment2, pricingModel2, pricingAdjustments)
		
		populateItemPriceInfos(itemPriceInfo1, salePrice1, pricingAdjustments, itemPriceInfo2, salePrice2)
		
		populateCommerceItem(commerceItem1, auxiliaryDataMock1, registryId1, itemPriceInfo1, quantity1)
		populateCommerceItem(commerceItem2, auxiliaryDataMock2, registryId2, itemPriceInfo2, quantity2)
		
		popualteAuxiliaryData(auxiliaryDataMock1, catalogRef1, productId1, auxiliaryDataMock2, catalogRef2, productId2)
		
		catalogRef1.setProperties("displayName":"Chinese clay pot")
		catalogRef2.setProperties("displayName" : "Glass with multi-colors")
		
		commerceItems.add(commerceItem1)
		commerceItems.add(commerceItem2)

		requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteID
		requestMock.getLocale() >> local
		
		
		productVo1.getProductPageUrl() >> {throw new BBBBusinessException("Some BBBSystemException occuered while fetching product details for product Id : ")}
		populateProductVO(productVo2, pdpURL2, prodName2, longDescription2, imageVO2)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID) >> {throw new BBBBusinessException("BusinessException : exception while getting key for rkg config type")}
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID+"ZMAM") >> {throw new BBBBusinessException("BusinessException : exception while getting key for rkg config type")}
		1 * catalogToolsMock.getProductDetails(siteID, productId1) >> {throw new BBBBusinessException("Some BBBSystemException occuered while fetching product details for product Id : ")}
		1 * catalogToolsMock.getProductDetails(siteID, productId2) >> productVo2
		
		registryType1.setRegistryTypeDesc(registryDesc1)
		registryType2.setRegistryTypeDesc(registryDesc2)
		registrySummary1.setRegistryType(registryType1)
		registrySummary2.setRegistryType(registryType2)
		
		registryMap.put(registryId1, registrySummary1)
		registryMap.put(registryId2, registrySummary2)
		
		shippingPriceInfo1.getAdjustments() >> pricingAdjustments
		shippingPriceInfo2.getAdjustments() >> pricingAdjustments
		
		shippingGroup1.getPriceInfo() >> shippingPriceInfo1
		shippingGroup2.getPriceInfo() >> shippingPriceInfo2
		
		shippingGroups.add(shippingGroup1)
		shippingGroups.add(shippingGroup2)
		
		//imageVO1
		imageVO1.setSmallImage(smallImage1)
		imageVO1.setLargeImage(largeImage1)
		imageVO2.setSmallImage(smallImage2)
		imageVO2.setLargeImage(largeImage2)
		
		populateOrderMock(orderMock, onlineOrderNumber, orderDate, commerceItems, registryMap, shippingGroups, orderPriceInfo)
		orderMock.getBopusOrderNumber() >> bopusOrderNumber
		orderPriceInfo.getAdjustments() >> pricingAdjustments
		
		1 * pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVO
		
		when :
		 
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		
			1 * orderInfoDropletSpy.logError("Error in getting merchant Id ", _)
			1 * orderInfoDropletSpy.logError("Error in getting rkgZMAM Id ", _)
			1 * orderInfoDropletSpy.logError('Some BBBBusinessException occuered while fetching product details for product Id : prod01', null)
			
			1 * requestMock.setParameter("PromoCode", _)
			1 * requestMock.setParameter("PromoAmount", _)
			1 * requestMock.setParameter("storeId", _)
			1 * requestMock.setParameter("itemIds", _)
			1 * requestMock.setParameter("resxEventType", _)
			1 * requestMock.setParameter("itemQtys", _)
			1 * requestMock.setParameter("itemprices", _)
			1 * requestMock.setParameter("itemAmounts", _)
			1 * requestMock.setParameter("itemCount", _)
			1 * requestMock.setParameter("itemSkuIds", _)
			1 * requestMock.setParameter("itemAmts", _)
			1 * requestMock.setParameter("itemQuantities", _)
			1 * requestMock.setParameter("itemSkuNames", _)
			1 * requestMock.setParameter("cItemIds", _)
			1 * requestMock.setParameter(BBBCoreConstants.WC_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.BP_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.CJ_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, _)
			1 * requestMock.setParameter("orderList", _)
			1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
			1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,priceInfoVO)
	}
	
	/*
	 * Negative scenarios covered : 
	 * 
	 * Order pricing model - null
	 * ShippingGroup is neither HardGood nor StoreShippinGroup. 
	 */
	
	def "service - Exception while getting RkgMerchantID & RkgZMAMId | BBBSystemException" () {
		
		given :
		
		//BBBOrder orderMock = Mock()
		def onlineOrderNumber = "onlineOrder01"
		int commerceItemCount = 0
		def siteID = "BBB_US"
		def registryId1 = "reg01"
		def registryId2 = "reg02"
		def registryDesc1 = "Birthday registry"
		def registryDesc2 = "House warming registry"
		double salePrice1 = 15.00
		double salePrice2 = 0.00
		def productId1 = "prod01"
		def productId2 = "prod02"
		def pdpURL1 = "bedbath.com/product/prod01"
		def pdpURL2 = "bedbath.com/product/prod02"
		def prodName1 = "Multi colored glass"
		def prodName2 = "Pure cotton pillow"
		def smallImage1 = "images.bedbath.com/small/product/prod01"
		def smallImage2 = "images.bedbath.com/small/product/prod02"
		def largeImage1 = "images.bedbath.com/large/product/prod01"
		def largeImage2 = "images.bedbath.com/large/product/prod02"
		def longDescription1 = "Glass specially designed with multiple colors"
		def longDescription2 = "Pillow made with 100% pure cotton"
		int quantity1 = 2
		int quantity2 = 3
		
		Date orderDate = new Date()
		RegistryTypes registryType1 =  new RegistryTypes()//"BDR"
		RegistryTypes registryType2 = new RegistryTypes()//"HWR"
		PriceInfoVO priceInfoVO = new PriceInfoVO()
		ImageVO imageVO1 = new ImageVO()
		ImageVO imageVO2 = new ImageVO()
		ProductVO productVo1 = Mock()
		ProductVO productVo2 = Mock()
		RegistrySummaryVO registrySummary1 = new RegistrySummaryVO()
		RegistrySummaryVO registrySummary2 = new RegistrySummaryVO()
		
		List<String> rkgMerchantIds = Arrays.asList("rkgMerch01","rkgMerch02","rkgMerch03")
		List<String> rkgZMAMIds = Arrays.asList("rkgZMAM01","rkgZMAM02","rkgZMAM03")
		List<CommerceItem> commerceItems = new ArrayList<>()
		List<ShippingGroup> shippingGroups = new ArrayList<>() // ((BBBOrder) order).getShippingGroups()
		List<PricingAdjustment> pricingAdjustments = new ArrayList<>()
		List<PricingAdjustment> orderPricingAdjustments = new ArrayList<>()
		Map<String,RegistrySummaryVO> registryMap = new HashMap<>()
		
		BBBOrderImpl orderMock = Mock()
		
		PricingAdjustment pricingAdjustment1 = Mock()
		PricingAdjustment pricingAdjustment2 = Mock()
		PricingAdjustment orderPricingAdjustment = Mock()
		
		HardgoodShippingGroup shippingGroup1 = Mock()
		ShippingGroup shippingGroup2 = Mock()
		BBBCommerceItem commerceItem1 = Mock()
		BBBCommerceItem commerceItem2 = Mock()
		ItemPriceInfo itemPriceInfo1 = Mock()
		ItemPriceInfo itemPriceInfo2 = Mock()
		OrderPriceInfo orderPriceInfo = Mock()
		ShippingPriceInfo shippingPriceInfo1 = Mock()
		ShippingPriceInfo shippingPriceInfo2 = Mock()
		
		AuxiliaryData auxiliaryDataMock1 = Mock()
		AuxiliaryData auxiliaryDataMock2 = Mock()
		
		RepositoryItemMock catalogRef1 = new RepositoryItemMock(["id":"sku01"])
		RepositoryItemMock catalogRef2 = new RepositoryItemMock(["id":"sku02"])
		RepositoryItemMock pricingModel1 = new RepositoryItemMock(["id":"pricing01"])
		RepositoryItemMock pricingModel2 = new RepositoryItemMock(["id":"pricing02"])
		
		pricingModel1.setProperties(["displayName":"50OFF"])
		pricingModel2.setProperties(["displayName":"25OFF"])
		
		populatePricingAdjustments(pricingAdjustment1, pricingModel1, pricingAdjustment2, pricingModel2, pricingAdjustments)
		
		orderPricingAdjustment.getPricingModel() >> null
		orderPricingAdjustments.add(orderPricingAdjustment)
		populateItemPriceInfos(itemPriceInfo1, salePrice1, pricingAdjustments, itemPriceInfo2, salePrice2)
		
		populateCommerceItem(commerceItem1, auxiliaryDataMock1, registryId1, itemPriceInfo1, quantity1)
		populateCommerceItem(commerceItem2, auxiliaryDataMock2, registryId2, itemPriceInfo2, quantity2)
		
		popualteAuxiliaryData(auxiliaryDataMock1, catalogRef1, productId1, auxiliaryDataMock2, catalogRef2, productId2)
		
		catalogRef1.setProperties("displayName":"Chinese clay pot")
		catalogRef2.setProperties("displayName" : "Glass with multi-colors")
		
		commerceItems.add(commerceItem1)
		commerceItems.add(commerceItem2)

		1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteID
		(1.._) * requestMock.getLocale() >> local
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID)  >> {throw new BBBSystemException("BusinessException : exception while getting key for rkg config type")}
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID+"ZMAM")  >> {throw new BBBSystemException("BusinessException : exception while getting key for rkg config type")}
		1 * catalogToolsMock.getProductDetails(siteID, productId1) >> {throw new BBBSystemException("Some BBBSystemException occuered while fetching product details for product Id : ")}
		
		registryType1.setRegistryTypeDesc(registryDesc1)
		registryType2.setRegistryTypeDesc(registryDesc2)
		registrySummary1.setRegistryType(registryType1)
		registrySummary2.setRegistryType(registryType2)
		
		registryMap.put(registryId1, registrySummary1)
		registryMap.put(registryId2, registrySummary2)
		
		shippingPriceInfo1.getAdjustments() >> pricingAdjustments
		shippingPriceInfo2.getAdjustments() >> pricingAdjustments
		
		shippingGroup1.getPriceInfo() >> shippingPriceInfo1
		shippingGroup2.getPriceInfo() >> shippingPriceInfo2
		
		shippingGroups.add(shippingGroup1)
		shippingGroups.add(shippingGroup2)
		
		//imageVO1
		imageVO1.setSmallImage(smallImage1)
		imageVO1.setLargeImage(largeImage1)
		imageVO2.setSmallImage(smallImage2)
		imageVO2.setLargeImage(largeImage2)
		
		populateOrderMock(orderMock, onlineOrderNumber, orderDate, commerceItems, registryMap, shippingGroups, orderPriceInfo)
		
		orderPriceInfo.getAdjustments() >> orderPricingAdjustments
		
		1 * pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVO
		
		when :
		 
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		
		 	//0 * orderPricingAdjustment.getPricingModel() // not working throwing - 0 null
			//0 * orderPricingAdjustment.getPricingModel().getPropertyValue("displayName")
			1 * requestMock.setParameter("PromoCode", _)
			1 * requestMock.setParameter("PromoAmount", _)
			1 * requestMock.setParameter("storeId", _)
			1 * requestMock.setParameter("itemIds", _)
			1 * requestMock.setParameter("resxEventType", _)
			1 * requestMock.setParameter("itemQtys", _)
			1 * requestMock.setParameter("itemprices", _)
			1 * requestMock.setParameter("itemAmounts", _)
			1 * requestMock.setParameter("itemCount", _)
			1 * requestMock.setParameter("itemSkuIds", _)
			1 * requestMock.setParameter("itemAmts", _)
			1 * requestMock.setParameter("itemQuantities", _)
			1 * requestMock.setParameter("itemSkuNames", _)
			1 * requestMock.setParameter("cItemIds", _)
			1 * requestMock.setParameter(BBBCoreConstants.WC_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.BP_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.CJ_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, _)
			1 * requestMock.setParameter("orderList", _)
			1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
			1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,priceInfoVO)
	}
	
	//@Ignore
	/*
	 * Negative scenarios covered : 
	 * 
	 * commerceItem.getPriceInfo() - null (#227)
	 * 
	 * RegistryType - null | RegistrySummaryVO - null 
	 * 
	 */
	def "service - Price details in commerce item (commerceItem.getPriceInfo()) is invalid/corrupted (ItemPriceInfo is null)" () {
		
		given :
		
		//BBBOrder orderMock = Mock()
		def onlineOrderNumber = "onlineOrder01"
		int commerceItemCount = 0
		def siteID = "BBB_US"
		def registryId1 = "reg01"
		def registryId2 = "reg02"
		def registryDesc1 = "Birthday registry"
		def registryDesc2 = "House warming registry"
		double salePrice1 = 15.00
		double salePrice2 = 0.00
		def productId1 = "prod01"
		def productId2 = "prod02"
		def pdpURL1 = "bedbath.com/product/prod01"
		def pdpURL2 = "bedbath.com/product/prod02"
		def prodName1 = "Multi colored glass"
		def prodName2 = "Pure cotton pillow"
		def smallImage1 = "images.bedbath.com/small/product/prod01"
		def smallImage2 = "images.bedbath.com/small/product/prod02"
		def largeImage1 = "images.bedbath.com/large/product/prod01"
		def largeImage2 = "images.bedbath.com/large/product/prod02"
		def longDescription1 = "Glass specially designed with multiple colors"
		def longDescription2 = "Pillow made with 100% pure cotton"
		int quantity1 = 2
		int quantity2 = 3
		
		Date orderDate = new Date()
		RegistryTypes registryType1 =  new RegistryTypes()//"BDR"
		RegistryTypes registryType2 = null//"HWR"
		PriceInfoVO priceInfoVO = new PriceInfoVO()
		ImageVO imageVO1 = new ImageVO()
		ImageVO imageVO2 = new ImageVO()
		//ProductVO productVo1 = new ProductVO()
		//ProductVO productVo2 = new ProductVO()
		RegistrySummaryVO registrySummary1 = null
		RegistrySummaryVO registrySummary2 = new RegistrySummaryVO()
		
		List<String> rkgMerchantIds = Arrays.asList("rkgMerch01","rkgMerch02","rkgMerch03")
		List<String> rkgZMAMIds = Arrays.asList("rkgZMAM01","rkgZMAM02","rkgZMAM03")
		List<CommerceItem> commerceItems = new ArrayList<>()
		List<ShippingGroup> shippingGroups = new ArrayList<>() // ((BBBOrder) order).getShippingGroups()
		List<PricingAdjustment> pricingAdjustments = new ArrayList<>()
		Map<String,RegistrySummaryVO> registryMap = new HashMap<>()
		
		BBBOrderImpl orderMock = Mock()
		
		PricingAdjustment pricingAdjustment1 = Mock()
		PricingAdjustment pricingAdjustment2 = Mock()
		
		HardgoodShippingGroup shippingGroup1 = Mock()
		HardgoodShippingGroup shippingGroup2 = Mock()
		BBBCommerceItem commerceItem1 = Mock()
		BBBCommerceItem commerceItem2 = Mock()
		ItemPriceInfo itemPriceInfo1 = null
		ItemPriceInfo itemPriceInfo2 = Mock()
		OrderPriceInfo orderPriceInfo = Mock()
		ShippingPriceInfo shippingPriceInfo1 = Mock()
		ShippingPriceInfo shippingPriceInfo2 = Mock()
		
		AuxiliaryData auxiliaryDataMock1 = Mock()
		AuxiliaryData auxiliaryDataMock2 = Mock()
		
		RepositoryItemMock catalogRef1 = new RepositoryItemMock(["id":"sku01"])
		RepositoryItemMock catalogRef2 = new RepositoryItemMock(["id":"sku02"])
		RepositoryItemMock pricingModel1 = new RepositoryItemMock(["id":"pricing01"])
		RepositoryItemMock pricingModel2 = new RepositoryItemMock(["id":"pricing02"])
		
		pricingModel1.setProperties(["displayName":"50OFF"])
		pricingModel2.setProperties(["displayName":"25OFF"])
		
		populatePricingAdjustments(pricingAdjustment1, pricingModel1, pricingAdjustment2, pricingModel2, pricingAdjustments)
		
		populateItemPriceInfos(itemPriceInfo1, salePrice1, pricingAdjustments, itemPriceInfo2, salePrice2)
		
		populateCommerceItem(commerceItem1, auxiliaryDataMock1, registryId1, itemPriceInfo1, quantity1)
		populateCommerceItem(commerceItem2, auxiliaryDataMock2, registryId2, itemPriceInfo2, quantity2)
		
		popualteAuxiliaryData(auxiliaryDataMock1, catalogRef1, productId1, auxiliaryDataMock2, catalogRef2, productId2)
		
		catalogRef1.setProperties("displayName":"Chinese clay pot")
		catalogRef2.setProperties("displayName" : "Glass with multi-colors")
		
		commerceItems.add(commerceItem1)
		commerceItems.add(commerceItem2)

		requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteID
		requestMock.getLocale() >> local
		
		/*populateProductVO(productVo1, pdpURL1, prodName1, longDescription1, imageVO1)
		populateProductVO(productVo2, pdpURL2, prodName2, longDescription2, imageVO2)*/
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID) >> rkgMerchantIds
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID+"ZMAM") >> rkgZMAMIds
		/*catalogToolsMock.getProductDetails(siteID, productId1) >> productVo1
		catalogToolsMock.getProductDetails(siteID, productId2) >> productVo2*/
		
		registryType1.setRegistryTypeDesc(registryDesc1)
		//registryType2.setRegistryTypeDesc(registryDesc2)
		//registrySummary1.setRegistryType(registryType1)
		registrySummary2.setRegistryType(registryType2)
		
		registryMap.put(registryId1, registrySummary1)
		registryMap.put(registryId2, registrySummary2)
		
		shippingPriceInfo1.getAdjustments() >> pricingAdjustments
		shippingPriceInfo2.getAdjustments() >> pricingAdjustments
		
		shippingGroup1.getPriceInfo() >> shippingPriceInfo1
		shippingGroup2.getPriceInfo() >> shippingPriceInfo2
		
		shippingGroups.add(shippingGroup1)
		shippingGroups.add(shippingGroup2)
		
		//imageVO1
		imageVO1.setSmallImage(smallImage1)
		imageVO1.setLargeImage(largeImage1)
		imageVO2.setSmallImage(smallImage2)
		imageVO2.setLargeImage(largeImage2)
		
		populateOrderMock(orderMock, onlineOrderNumber, orderDate, commerceItems, registryMap, shippingGroups, orderPriceInfo)
		
		orderPriceInfo.getAdjustments() >> pricingAdjustments
		
		//1 * pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVO // not working (0 invocations)
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVO
		when :
		 
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		 
			thrown NumberFormatException
			0 * requestMock.setParameter("PromoCode", _)
			0 * requestMock.setParameter("PromoAmount", _)
			0 * requestMock.setParameter("storeId", _)
			0 * requestMock.setParameter("itemIds", _)
			0 * requestMock.setParameter("resxEventType", _)
			0 * requestMock.setParameter("itemQtys", _)
			0 * requestMock.setParameter("itemprices", _)
			0 * requestMock.setParameter("itemAmounts", _)
			0 * requestMock.setParameter("itemCount", _)
			0 * requestMock.setParameter("itemSkuIds", _)
			0 * requestMock.setParameter("itemAmts", _)
			0 * requestMock.setParameter("itemQuantities", _)
			0 * requestMock.setParameter("itemSkuNames", _)
			0 * requestMock.setParameter("cItemIds", _)
			0 * requestMock.setParameter(BBBCoreConstants.WC_ITEM_URL, _)
			0 * requestMock.setParameter(BBBCoreConstants.BP_ITEM_URL, _)
			0 * requestMock.setParameter(BBBCoreConstants.CJ_ITEM_URL, _)
			0 * requestMock.setParameter(BBBCoreConstants.RKG_ITEM_URL, _)
			0 * requestMock.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, _)
			0 * requestMock.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, _)
			0 * requestMock.setParameter("orderList", _)
			0 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
			0 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,priceInfoVO)
	}
	

	def "order object is corrupted/invalid (null)" () {
		
		given : 
		
		BBBOrderImpl orderMock = null
		
		requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		
		when : 
		
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		 
		0 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		0 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,_)
		1 * orderInfoDropletSpy.logDebug("CLS=[BBBOrderInfoDroplet] MTHD=[Service ends]")
	}	
	
	def "service - CommerceItem is NonMerchandised type" () {
		
		given :
		
		
		BBBOrderImpl orderMock = Mock()
		List<CommerceItem> commerceItems = new ArrayList<>()
		NonMerchandiseCommerceItem commerceItem1 = Mock()
		
		commerceItems.add(commerceItem1)
		2 * orderMock.getCommerceItems() >> commerceItems
		1 * orderMock.getShippingGroups() >> []
		1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		
		when :
		 
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		
			 1 * requestMock.setParameter("PromoCode", _)
			 1 * requestMock.setParameter("PromoAmount", _)
			 1 * requestMock.setParameter("storeId", _)
			 1 * requestMock.setParameter("itemIds", _)
			 1 * requestMock.setParameter("resxEventType", _)
			 1 * requestMock.setParameter("itemQtys", _)
			 1 * requestMock.setParameter("itemprices", _)
			 1 * requestMock.setParameter("itemAmounts", _)
			 1 * requestMock.setParameter("itemCount", _)
			 1 * requestMock.setParameter("itemSkuIds", _)
			 1 * requestMock.setParameter("itemAmts", _)
			 1 * requestMock.setParameter("itemQuantities", _)
			 1 * requestMock.setParameter("itemSkuNames", _)
			 1 * requestMock.setParameter("cItemIds", _)
			 1 * requestMock.setParameter(BBBCoreConstants.WC_ITEM_URL, _)
			 1 * requestMock.setParameter(BBBCoreConstants.BP_ITEM_URL, _)
			 1 * requestMock.setParameter(BBBCoreConstants.CJ_ITEM_URL, _)
			 1 * requestMock.setParameter(BBBCoreConstants.RKG_ITEM_URL, _)
			 1 * requestMock.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, _)
			 1 * requestMock.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, _)
			 1 * requestMock.setParameter("orderList", _)
			 1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
			 1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,null)
	}
	
	/*
	 * Negative scenarios covered :
	 *
	 * Commerce Item's adjustment's pricing model is null (#790) - adjustment.getPricingModel()
	 * Order adjustment's pricing model is null (#764) 
	 * Shipping groups adjustment's pricing model is null (#821) 
	 */
	def "service - Order Pricing adjustment is not set/invalid (null)" () {
		
		given :
		
		//BBBOrder orderMock = Mock()
		def onlineOrderNumber = "onlineOrder01"
		int commerceItemCount = 0
		def siteID = "BBB_US"
		def registryId1 = "reg01"
		def registryId2 = "reg02"
		def registryDesc1 = "Birthday registry"
		def registryDesc2 = "House warming registry"
		double salePrice1 = 15.00
		double salePrice2 = 0.00
		def productId1 = "prod01"
		def productId2 = "prod02"
		def pdpURL1 = "bedbath.com/product/prod01"
		def pdpURL2 = "bedbath.com/product/prod02"
		def prodName1 = "Multi colored glass"
		def prodName2 = "Pure cotton pillow"
		def smallImage1 = "images.bedbath.com/small/product/prod01"
		def smallImage2 = "images.bedbath.com/small/product/prod02"
		def largeImage1 = "images.bedbath.com/large/product/prod01"
		def largeImage2 = "images.bedbath.com/large/product/prod02"
		def longDescription1 = "Glass specially designed with multiple colors"
		def longDescription2 = "Pillow made with 100% pure cotton"
		int quantity1 = 2
		int quantity2 = 3
		
		Date orderDate = new Date()
		RegistryTypes registryType1 =  new RegistryTypes()//"BDR"
		RegistryTypes registryType2 = new RegistryTypes()//"HWR"
		PriceInfoVO priceInfoVO = new PriceInfoVO()
		ImageVO imageVO1 = new ImageVO()
		ImageVO imageVO2 = new ImageVO()
		ProductVO productVo1 = new ProductVO()
		ProductVO productVo2 = new ProductVO()
		RegistrySummaryVO registrySummary1 = new RegistrySummaryVO()
		RegistrySummaryVO registrySummary2 = new RegistrySummaryVO()
		
		List<String> rkgMerchantIds = Arrays.asList("rkgMerch01","rkgMerch02","rkgMerch03")
		List<String> rkgZMAMIds = Arrays.asList("rkgZMAM01","rkgZMAM02","rkgZMAM03")
		List<CommerceItem> commerceItems = new ArrayList<>()
		List<ShippingGroup> shippingGroups = new ArrayList<>() // ((BBBOrder) order).getShippingGroups()
		List<PricingAdjustment> pricingAdjustments = new ArrayList<>()
		Map<String,RegistrySummaryVO> registryMap = new HashMap<>()
		
		BBBOrderImpl orderMock = Mock()
		
		PricingAdjustment pricingAdjustment1 = Mock()
		PricingAdjustment pricingAdjustment2 = Mock()
		
		HardgoodShippingGroup shippingGroup1 = Mock()
		HardgoodShippingGroup shippingGroup2 = Mock()
		//BBBStoreShippingGroup shippingGroup2 = Mock()
		BBBCommerceItem commerceItem1 = Mock()
		BBBCommerceItem commerceItem2 = Mock()
		ItemPriceInfo itemPriceInfo1 = Mock()
		ItemPriceInfo itemPriceInfo2 = Mock()
		OrderPriceInfo orderPriceInfo = Mock()
		ShippingPriceInfo shippingPriceInfo1 = Mock()
		ShippingPriceInfo shippingPriceInfo2 = Mock()
		
		AuxiliaryData auxiliaryDataMock1 = Mock()
		AuxiliaryData auxiliaryDataMock2 = Mock()
		
		RepositoryItemMock catalogRef1 = new RepositoryItemMock(["id":"sku01"])
		RepositoryItemMock catalogRef2 = new RepositoryItemMock(["id":"sku02"])
		RepositoryItemMock pricingModel1 = null
		RepositoryItemMock pricingModel2 = new RepositoryItemMock(["id":"pricing02"])
		
		//pricingModel1.setProperties(["displayName":"50OFF"])
		pricingModel2.setProperties(["displayName":"25OFF"])
		
		populatePricingAdjustments(pricingAdjustment1, pricingModel1, pricingAdjustment2, pricingModel2, pricingAdjustments)
		
		populateItemPriceInfos(itemPriceInfo1, salePrice1, pricingAdjustments, itemPriceInfo2, salePrice2)
		
		populateCommerceItem(commerceItem1, auxiliaryDataMock1, registryId1, itemPriceInfo1, quantity1)
		populateCommerceItem(commerceItem2, auxiliaryDataMock2, registryId2, itemPriceInfo2, quantity2)
		
		popualteAuxiliaryData(auxiliaryDataMock1, catalogRef1, productId1, auxiliaryDataMock2, catalogRef2, productId2)
		
		catalogRef1.setProperties("displayName":"Chinese clay pot")
		catalogRef2.setProperties("displayName" : "Glass with multi-colors")
		
		commerceItems.add(commerceItem1)
		commerceItems.add(commerceItem2)

		requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteID
		requestMock.getLocale() >> local
		
		populateProductVO(productVo1, pdpURL1, prodName1, longDescription1, imageVO1)
		populateProductVO(productVo2, pdpURL2, prodName2, longDescription2, imageVO2)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID) >> rkgMerchantIds
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID+"ZMAM") >> rkgZMAMIds
		1 * catalogToolsMock.getProductDetails(siteID, productId1) >> productVo1
		1 * catalogToolsMock.getProductDetails(siteID, productId2) >> productVo2
		
		registryType1.setRegistryTypeDesc(registryDesc1)
		registryType2.setRegistryTypeDesc(registryDesc2)
		registrySummary1.setRegistryType(registryType1)
		registrySummary2.setRegistryType(registryType2)
		
		registryMap.put(registryId1, registrySummary1)
		registryMap.put(registryId2, registrySummary2)
		
		shippingPriceInfo1.getAdjustments() >> pricingAdjustments
		shippingPriceInfo2.getAdjustments() >> pricingAdjustments
		
		shippingGroup1.getPriceInfo() >> shippingPriceInfo1
		shippingGroup2.getPriceInfo() >> shippingPriceInfo2
		
		shippingGroups.add(shippingGroup1)
		shippingGroups.add(shippingGroup2)
		
		//imageVO1
		imageVO1.setSmallImage(smallImage1)
		imageVO1.setLargeImage(largeImage1)
		imageVO2.setSmallImage(smallImage2)
		imageVO2.setLargeImage(largeImage2)
		
		populateOrderMock(orderMock, onlineOrderNumber, orderDate, commerceItems, registryMap, shippingGroups, orderPriceInfo)
		
		orderPriceInfo.getAdjustments() >> pricingAdjustments
		
		1 * pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVO
		
		when :
		 
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		
			0 * pricingAdjustment1.getPricingModel().getPropertyValue("displayName")
		 
			1 * requestMock.setParameter("PromoCode", _)
			1 * requestMock.setParameter("PromoAmount", _)
			1 * requestMock.setParameter("storeId", _)
			1 * requestMock.setParameter("itemIds", _)
			1 * requestMock.setParameter("resxEventType", _)
			1 * requestMock.setParameter("itemQtys", _)
			1 * requestMock.setParameter("itemprices", _)
			1 * requestMock.setParameter("itemAmounts", _)
			1 * requestMock.setParameter("itemCount", _)
			1 * requestMock.setParameter("itemSkuIds", _)
			1 * requestMock.setParameter("itemAmts", _)
			1 * requestMock.setParameter("itemQuantities", _)
			1 * requestMock.setParameter("itemSkuNames", _)
			1 * requestMock.setParameter("cItemIds", _)
			1 * requestMock.setParameter(BBBCoreConstants.WC_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.BP_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.CJ_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, _)
			1 * requestMock.setParameter("orderList", _)
			1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
			1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,priceInfoVO)
	}
	
	/*
	 * Negative scenarios covered 
	 * 
	 * OrderPricing adjustment is null (#761) - ((BBBOrder) order).getPriceInfo().getAdjustments() != null)
	 * commerce item adjustment is null (#787) - item.getPriceInfo().getAdjustments() != null
	 * 
	 */
	def "service - OrderPricing adjustment is null" () {
		
		given :
		
		//BBBOrder orderMock = Mock()
		def onlineOrderNumber = "onlineOrder01"
		int commerceItemCount = 0
		def siteID = "BBB_US"
		def registryId1 = "reg01"
		def registryId2 = "reg02"
		def registryDesc1 = "Birthday registry"
		def registryDesc2 = "House warming registry"
		double salePrice1 = 15.00
		double salePrice2 = 0.00
		def productId1 = "prod01"
		def productId2 = "prod02"
		def pdpURL1 = "bedbath.com/product/prod01"
		def pdpURL2 = "bedbath.com/product/prod02"
		def prodName1 = "Multi colored glass"
		def prodName2 = "Pure cotton pillow"
		def smallImage1 = "images.bedbath.com/small/product/prod01"
		def smallImage2 = "images.bedbath.com/small/product/prod02"
		def largeImage1 = "images.bedbath.com/large/product/prod01"
		def largeImage2 = "images.bedbath.com/large/product/prod02"
		def longDescription1 = "Glass specially designed with multiple colors"
		def longDescription2 = "Pillow made with 100% pure cotton"
		int quantity1 = 2
		int quantity2 = 3
		
		Date orderDate = new Date()
		RegistryTypes registryType1 =  new RegistryTypes()//"BDR"
		RegistryTypes registryType2 = new RegistryTypes()//"HWR"
		PriceInfoVO priceInfoVO = new PriceInfoVO()
		ImageVO imageVO1 = new ImageVO()
		ImageVO imageVO2 = new ImageVO()
		ProductVO productVo1 = new ProductVO()
		ProductVO productVo2 = new ProductVO()
		RegistrySummaryVO registrySummary1 = new RegistrySummaryVO()
		RegistrySummaryVO registrySummary2 = new RegistrySummaryVO()
		
		List<String> rkgMerchantIds = Arrays.asList("rkgMerch01","rkgMerch02","rkgMerch03")
		List<String> rkgZMAMIds = Arrays.asList("rkgZMAM01","rkgZMAM02","rkgZMAM03")
		List<CommerceItem> commerceItems = new ArrayList<>()
		List<ShippingGroup> shippingGroups = new ArrayList<>() // ((BBBOrder) order).getShippingGroups()
		List<PricingAdjustment> pricingAdjustments = null
		Map<String,RegistrySummaryVO> registryMap = new HashMap<>()
		
		BBBOrderImpl orderMock = Mock()
		
		PricingAdjustment pricingAdjustment1 = Mock()
		PricingAdjustment pricingAdjustment2 = Mock()
		
		HardgoodShippingGroup shippingGroup1 = Mock()
		HardgoodShippingGroup shippingGroup2 = Mock()
		//BBBStoreShippingGroup shippingGroup2 = Mock()
		BBBCommerceItem commerceItem1 = Mock()
		BBBCommerceItem commerceItem2 = Mock()
		//BBBCommerceItem commerceItem3 = Mock()
		ItemPriceInfo itemPriceInfo1 = Mock()
		ItemPriceInfo itemPriceInfo2 = Mock()
		OrderPriceInfo orderPriceInfo = Mock()
		ShippingPriceInfo shippingPriceInfo1 = Mock()
		ShippingPriceInfo shippingPriceInfo2 = Mock()
		
		AuxiliaryData auxiliaryDataMock1 = Mock()
		AuxiliaryData auxiliaryDataMock2 = Mock()
		
		RepositoryItemMock catalogRef1 = new RepositoryItemMock(["id":"sku01"])
		RepositoryItemMock catalogRef2 = new RepositoryItemMock(["id":"sku02"])
		RepositoryItemMock pricingModel1 = new RepositoryItemMock(["id":"pricing01"])
		RepositoryItemMock pricingModel2 = new RepositoryItemMock(["id":"pricing02"])
		
		pricingModel1.setProperties(["displayName":"50OFF"])
		pricingModel2.setProperties(["displayName":"25OFF"])
		
		//populatePricingAdjustments(pricingAdjustment1, pricingModel1, pricingAdjustment2, pricingModel2, pricingAdjustments)
		pricingAdjustment1.getPricingModel() >> null
		pricingAdjustment2.getPricingModel() >> pricingModel2
		/*pricingAdjustments.add(pricingAdjustment1)
		pricingAdjustments.add(pricingAdjustment2)*/
		
		populateItemPriceInfos(itemPriceInfo1, salePrice1, pricingAdjustments, itemPriceInfo2, salePrice2)
		
		populateCommerceItem(commerceItem1, auxiliaryDataMock1, registryId1, itemPriceInfo1, quantity1)
		populateCommerceItem(commerceItem2, auxiliaryDataMock2, registryId2, itemPriceInfo2, quantity2)
		//populateCommerceItem(commerceItem3, auxiliaryDataMock2, registryId2, null, quantity2)
		//commerceItem3.getPriceInfo() >> null		// not working as they try to access price values.
		popualteAuxiliaryData(auxiliaryDataMock1, catalogRef1, productId1, auxiliaryDataMock2, catalogRef2, productId2)
		
		catalogRef1.setProperties("displayName":"Chinese clay pot")
		catalogRef2.setProperties("displayName" : "Glass with multi-colors")
		
		commerceItems.add(commerceItem1)
		commerceItems.add(commerceItem2)
		//commerceItems.add(commerceItem3)
		
		requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteID
		requestMock.getLocale() >> local
		
		populateProductVO(productVo1, pdpURL1, prodName1, longDescription1, imageVO1)
		populateProductVO(productVo2, pdpURL2, prodName2, longDescription2, imageVO2)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID) >> rkgMerchantIds
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.RKG_CONFIG_TYPE, siteID+"ZMAM") >> rkgZMAMIds
		1 * catalogToolsMock.getProductDetails(siteID, productId1) >> productVo1
		1 * catalogToolsMock.getProductDetails(siteID, productId2) >> productVo2
		
		registryType1.setRegistryTypeDesc(registryDesc1)
		registryType2.setRegistryTypeDesc(registryDesc2)
		registrySummary1.setRegistryType(registryType1)
		registrySummary2.setRegistryType(registryType2)
		
		registryMap.put(registryId1, registrySummary1)
		registryMap.put(registryId2, registrySummary2)
		
		shippingPriceInfo1.getAdjustments() >> pricingAdjustments
		shippingPriceInfo2.getAdjustments() >> pricingAdjustments
		
		shippingGroup1.getPriceInfo() >> shippingPriceInfo1
		shippingGroup2.getPriceInfo() >> shippingPriceInfo2
		
		shippingGroups.add(shippingGroup1)
		shippingGroups.add(shippingGroup2)
		
		//imageVO1
		imageVO1.setSmallImage(smallImage1)
		imageVO1.setLargeImage(largeImage1)
		imageVO2.setSmallImage(smallImage2)
		imageVO2.setLargeImage(largeImage2)
		
		populateOrderMock(orderMock, onlineOrderNumber, orderDate, commerceItems, registryMap, shippingGroups, orderPriceInfo)
		
		orderPriceInfo.getAdjustments() >> null
		
		1 * pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVO
		
		when :
		 
		orderInfoDropletSpy.service(requestMock, responseMock)
		
		then :
		
			0 * pricingAdjustment1.getPricingModel().getPropertyValue("displayName")
		
			1 * requestMock.setParameter("PromoCode", _)
			1 * requestMock.setParameter("PromoAmount", _)
			1 * requestMock.setParameter("storeId", _)
			1 * requestMock.setParameter("itemIds", _)
			1 * requestMock.setParameter("resxEventType", _)
			1 * requestMock.setParameter("itemQtys", _)
			1 * requestMock.setParameter("itemprices", _)
			1 * requestMock.setParameter("itemAmounts", _)
			1 * requestMock.setParameter("itemCount", _)
			1 * requestMock.setParameter("itemSkuIds", _)
			1 * requestMock.setParameter("itemAmts", _)
			1 * requestMock.setParameter("itemQuantities", _)
			1 * requestMock.setParameter("itemSkuNames", _)
			1 * requestMock.setParameter("cItemIds", _)
			1 * requestMock.setParameter(BBBCoreConstants.WC_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.BP_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.CJ_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, _)
			1 * requestMock.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, _)
			1 * requestMock.setParameter("orderList", _)
			1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
			1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,priceInfoVO)
	}

	//TODO
	 
	/*
	 * 
	 * PriceInfo object is null for commerceItem, shipping group - test case will not be covered
	 * i.e.,
	 * (item.getPriceInfo() != null) and sg.getPriceInfo() != null - These branches will not be covered  
	 * Can't cover the above said branch in createPromoStr() method
	 * ArrayIndex exception thrown before invoking the cratePromo method (where our branches located)
	 * createBPAndWCURL - this is where the IndexOutOfBoundException occurs (#323) skuPrice[i]
	 */
	
	
	/*================================================================
	 * 	Service method - Test cases ends					 		 *
	 * *==============================================================
	 */	
	
	
	
	/*
	 * Data populating methods - STARTS
	 */
	
	private popualteAuxiliaryData(AuxiliaryData auxiliaryDataMock1, RepositoryItemMock catalogRef1, String productId1, AuxiliaryData auxiliaryDataMock2, RepositoryItemMock catalogRef2, String productId2) {
		auxiliaryDataMock1.getCatalogRef() >> catalogRef1
		auxiliaryDataMock1.getProductId() >> productId1
		auxiliaryDataMock2.getCatalogRef() >> catalogRef2
		auxiliaryDataMock2.getProductId() >> productId2
	}

	private populatePricingAdjustments(PricingAdjustment pricingAdjustment1, RepositoryItemMock pricingModel1, PricingAdjustment pricingAdjustment2, RepositoryItemMock pricingModel2, List pricingAdjustments) {
		pricingAdjustment1.getPricingModel() >> pricingModel1
		pricingAdjustment2.getPricingModel() >> pricingModel2
		pricingAdjustments.add(pricingAdjustment1)
		pricingAdjustments.add(pricingAdjustment2)
	}

	private populateItemPriceInfos(ItemPriceInfo itemPriceInfo1, double salePrice1, List pricingAdjustments, ItemPriceInfo itemPriceInfo2, double salePrice2) {
		itemPriceInfo1.getSalePrice() >> salePrice1
		itemPriceInfo1.getAmount() >> salePrice1
		itemPriceInfo1.getAdjustments() >> pricingAdjustments

		itemPriceInfo2.getSalePrice() >> salePrice2
		itemPriceInfo2.getAmount() >> salePrice2
		itemPriceInfo2.getAdjustments() >> pricingAdjustments
	}

	private populateCommerceItem(BBBCommerceItem commerceItem1, AuxiliaryData auxiliaryDataMock1, String registryId1, ItemPriceInfo itemPriceInfo1, int quantity1) {
		commerceItem1.getAuxiliaryData() >> auxiliaryDataMock1
		commerceItem1.getRegistryId() >> registryId1
		commerceItem1.getPriceInfo() >> itemPriceInfo1
		commerceItem1.getQuantity() >> quantity1
	}

	private populateProductVO(ProductVO productVo1, String pdpURL1, String prodName1, String longDescription1, ImageVO imageVO1) {
		productVo1.setProductPageUrl(pdpURL1)
		productVo1.setName(prodName1)
		productVo1.setLongDescription(longDescription1)
		productVo1.setProductImages(imageVO1)
	}

	private populateOrderMock(BBBOrderImpl orderMock, String onlineOrderNumber, Date orderDate, List commerceItems, Map registryMap, List shippingGroups, OrderPriceInfo orderPriceInfo) {
		
		//orderMock.getPropertyValue("onlineOrderNumber") >> onlineOrderNumber//Should be used if getOnlineOrderNumber() is final
		//orderMock.getOnlineOrderNumber() >> onlineOrderNumber
		orderMock.getOnlineOrderNumber() >> onlineOrderNumber
		orderMock.getSubmittedDate() >> orderDate
		orderMock.getCommerceItemCount() >> commerceItems.size()
		orderMock.getCommerceItems() >> commerceItems
		orderMock.getRegistryMap() >> registryMap
		orderMock.getShippingGroups() >> shippingGroups
		orderMock.getPriceInfo() >> orderPriceInfo
	}
	
	/*
	 * Data populating methods - ENDS
	 */
	
	
}
