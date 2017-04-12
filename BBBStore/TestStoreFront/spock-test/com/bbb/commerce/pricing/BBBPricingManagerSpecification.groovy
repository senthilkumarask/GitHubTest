package com.bbb.commerce.pricing


import atg.apache.xml.serialize.SieveEncodingInfo
import atg.commerce.order.CommerceItem
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.TaxPriceInfo
import atg.commerce.pricing.UnitPriceBean
import atg.commerce.pricing.priceLists.PriceDroplet
import atg.commerce.pricing.priceLists.PriceListException
import atg.commerce.pricing.priceLists.PriceListManager
import atg.multisite.SiteManager
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.ServletUtil;
import com.bbb.commerce.pricing.bean.ItemPriceVO;
import com.bbb.commerce.pricing.bean.PriceInfoVO
import java.util.Map
import atg.core.util.Range
import javax.servlet.ServletException;
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.commerce.order.BBBCommerceItemManager
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBShippingPriceInfo;

import spock.lang.specification.BBBExtendedSpec;

class BBBPricingManagerSpecification extends BBBExtendedSpec {

	BBBPricingManager bbbManager
	
	def setup(){
		bbbManager= new BBBPricingManager()
		bbbManager.setLoggingDebug(true)
	}
	
	def"getPriceItems, when total price is derived from list price"(){
		
		given:
		def PriceDroplet priceDroplet =Mock()
		def RepositoryItem listRepItem =Mock()
		def RepositoryItem saleRepItem =Mock()
		def RepositoryItem userProfile =Mock()
		
		Map<String, String> items  =new HashMap()
		items.put("2", "40")
		
		1*requestMock.resolveName("/atg/commerce/pricing/priceLists/PriceDroplet") >>priceDroplet
        requestMock.getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE)>> listRepItem >> listRepItem >> null
		1*listRepItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(100.0)
		//saleRepItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> 50.0
		
		ServletUtil.setCurrentUserProfile(userProfile)
		2*userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> "profile"
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
		/*1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		1*bbbManager.logDebug("Calling price droplet for fetching list price of :" + "2")
		1*bbbManager.logDebug("Setting list price details")*/
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		2*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, "2")
		1*requestMock.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME))
		ItemPriceVO item =list.get(0)
		item.getSkuId() =="2"
		item.getQuantity() ==40
		item.getTotalPrice() == 4000.0
		/*1*bbbManager.logDebug("BBBPricingManager.getPriceItems : END")*/
		
	}
	
	def"getPriceItems, when total price is derived from sale price"(){
		
		given:
		def PriceDroplet priceDroplet =Mock()
		def RepositoryItem listRepItem =Mock()
		def RepositoryItem saleRepItem =Mock()
		def RepositoryItem userProfile =Mock()
		
		Map<String, String> items  =new HashMap()
		items.put("2", "40")
		
		1*requestMock.resolveName("/atg/commerce/pricing/priceLists/PriceDroplet") >>priceDroplet
		requestMock.getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE)>> null >> saleRepItem >> saleRepItem
		1*saleRepItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		
		ServletUtil.setCurrentUserProfile(userProfile)
		2*userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> "profile"
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
		/*1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		1*bbbManager.logDebug("Calling price droplet for fetching sale price of :" + "2")
		1*bbbManager.logDebug("Setting sale price details")*/
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		2*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, "2")
		1*requestMock.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME))
		ItemPriceVO item =list.get(0)
		item.getSkuId() =="2"
		item.getQuantity() ==40
		item.getTotalPrice() == 2000.0
		/*1*bbbManager.logDebug("BBBPricingManager.getPriceItems : END")*/
		
	}
	
	def"getPriceItems, when sale price is not null, and quantity is less than zero"(){
		
		given:
		def PriceDroplet priceDroplet =Mock()
		def RepositoryItem listRepItem =Mock()
		def RepositoryItem saleRepItem =Mock()
		def RepositoryItem userProfile =Mock()
		
		Map<String, String> items  =new HashMap()
		items.put("2", "-1")
		
		1*requestMock.resolveName("/atg/commerce/pricing/priceLists/PriceDroplet") >>priceDroplet
		requestMock.getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE)>> null >> saleRepItem >> saleRepItem
		1*saleRepItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		
		ServletUtil.setCurrentUserProfile(userProfile)
		2*userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> "profile"
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
	/*	1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		1*bbbManager.logDebug("Calling price droplet for fetching sale price of :" + "2")
		1*bbbManager.logDebug("Setting sale price details")*/
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		2*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, "2")
		1*requestMock.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME))
		ItemPriceVO item =list.get(0)
		item.getSkuId() =="2"
		item.getQuantity() ==0
		item.getTotalPrice() == 0.0
		/*1*bbbManager.logDebug("BBBPricingManager.getPriceItems : END")*/
		
	}
	
	def"getPriceItems, when sale price is not null, and quantity is empty"(){
		
		given:
		def PriceDroplet priceDroplet =Mock()
		def RepositoryItem saleRepItem =Mock()
		def RepositoryItem userProfile =Mock()
		
		Map<String, String> items  =new HashMap()
		items.put("2", "")
		
		1*requestMock.resolveName("/atg/commerce/pricing/priceLists/PriceDroplet") >>priceDroplet
		requestMock.getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE)>> null >> saleRepItem >> saleRepItem
		1*saleRepItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		
		ServletUtil.setCurrentUserProfile(userProfile)
		2*userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> "profile"
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
		/*1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		1*bbbManager.logDebug("Calling price droplet for fetching sale price of :" + "2")
		1*bbbManager.logDebug("Setting sale price details")*/
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		2*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, "2")
		1*requestMock.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME))
		ItemPriceVO item =list.get(0)
		item.getSkuId() =="2"
		item.getQuantity() ==0
		item.getTotalPrice() == 0.0
		/*1*bbbManager.logDebug("BBBPricingManager.getPriceItems : END")*/
		
	}
	
	def"getPriceItems, when NumberFormatException is thrown while converting quantity from string to integer"(){
		
		given:
		def PriceDroplet priceDroplet =Mock()
		def RepositoryItem saleRepItem =Mock()
		def RepositoryItem userProfile =Mock()
		
		Map<String, String> items  =new HashMap()
		items.put("2", "abcd")
		
		1*requestMock.resolveName("/atg/commerce/pricing/priceLists/PriceDroplet") >>priceDroplet
		requestMock.getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE)>> null >> saleRepItem >> saleRepItem
		1*saleRepItem.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		
		ServletUtil.setCurrentUserProfile(userProfile)
		2*userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> "profile"
		
		Integer.parseInt(items.get("2")) >> {throw new NumberFormatException("")}
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
	/*	1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		1*bbbManager.logDebug("Calling price droplet for fetching sale price of :" + "2")
		1*bbbManager.logDebug("Setting sale price details")*/
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		2*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, "2")
		1*requestMock.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, userProfile.getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME))
		Exception exc = thrown()
		/*list ==null	*/
	}
	
	def"getPriceItems, when ServletException is thrown while calling service method of droplet"(){
		
		given:
		def PriceDroplet priceDroplet =Mock()
		bbbManager =Spy()
		
		Map<String, String> items  =new HashMap()
		items.put("2", "40")
		
		1*requestMock.resolveName("/atg/commerce/pricing/priceLists/PriceDroplet") >>priceDroplet
		
		priceDroplet.service(requestMock, responseMock) >> {throw new ServletException("")}
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
		1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		1*bbbManager.logDebug("Calling price droplet for fetching list price of :" + "2")
		0*bbbManager.logDebug("Setting list price details")
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		//1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, "2")
		/*list ==null*/
		BBBSystemException exc = thrown()
	}
	
	def"getPriceItems, when IOException is thrown while calling service method of droplet"(){
		
		given:
		def PriceDroplet priceDroplet =Mock()
		bbbManager =Spy()
		
		Map<String, String> items  =new HashMap()
		items.put("2", "40")
		
		1*requestMock.resolveName("/atg/commerce/pricing/priceLists/PriceDroplet") >>priceDroplet
		priceDroplet.service(requestMock, responseMock) >> {throw new IOException("")}
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
		1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		1*bbbManager.logDebug("Calling price droplet for fetching list price of :" + "2")
		0*bbbManager.logDebug("Setting list price details")
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		1*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, "2")
		list ==null
		BBBSystemException exc = thrown()
	}
	
	def"getPriceItems, when items list is empty"(){
		
		given:
		bbbManager =Spy()
		Map<String, String> items  =new HashMap()
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
		1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		0*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		BBBBusinessException exc = thrown()
		0*bbbManager.logDebug("Calling price droplet for fetching sale price of :" + "_")
		0*bbbManager.logDebug("Setting sale price details")
		0*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, _)
		0*requestMock.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, _)
		list ==null
	}
	
	def"getPriceItems, when items list is null"(){
		
		given:
		bbbManager =Spy()
		Map<String, String> items = null
		
		when:
		List<ItemPriceVO> list= bbbManager.getPriceItems(items)
		
		then:
		1*bbbManager.logDebug("BBBPricingManager.getPriceItems : START")
		0*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null)
		BBBBusinessException exc = thrown()
		0*bbbManager.logDebug("Calling price droplet for fetching sale price of :" + "_")
		0*bbbManager.logDebug("Setting sale price details")
		0*requestMock.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, _)
		0*requestMock.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, _)
		list ==null
	}
	
	def" getListPriceBySite,sets the List price of the SKU based on site price List "(){
	
		given:
		bbbManager =Spy()
		def RepositoryItem siteRep =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem price =Mock()
		def SiteManager site =Mock()
		def PriceListManager manager =Mock()
			
		bbbManager.setSiteManager(site)
		bbbManager.setLoggingError(true)
		bbbManager.setPriceListManager(manager)
		String siteName ="tbs"
		String productId ="product"
		String skuId ="skuId"
		site.getSite(siteName) >> siteRep
		siteRep.getRepositoryId() >> "Id"
		manager.getPriceListForSite(siteRep, "defaultListPriceList")>>priceList
		manager.getPrice(priceList, productId, skuId)>>price
		price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		
		when:
		Double listPrice =bbbManager.getListPriceBySite( siteName, productId, skuId)
		
		then:
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite start")
		0*bbbManager.logError("Price List not configured for site" + siteRep.getRepositoryId());
		1*bbbManager.logDebug("Listprice of SKU : " + skuId + " is : " + price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite end")
		listPrice == 50.0	
	}
	
	def" getListPriceBySite, when price list is null"(){
		
		given:
		bbbManager=Spy()
		def RepositoryItem siteRep =Mock()
		def RepositoryItem priceList =Mock()
		def SiteManager site =Mock()
		def PriceListManager manager =Mock()
	
		bbbManager.setSiteManager(site)
		bbbManager.setLoggingError(true)
		bbbManager.setPriceListManager(manager)
		String siteName ="tbs"
		String productId ="product"
		String skuId ="skuId"
		site.getSite(siteName) >> siteRep
		1*siteRep.getRepositoryId() >> "Id"
		1*manager.getPriceListForSite(siteRep, "defaultListPriceList")>>null
		
		when:
		Double listPrice =bbbManager.getListPriceBySite( siteName, productId, skuId)
		
		then:
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite start")
		0*bbbManager.logError("Price List not configured for site" + siteRep.getRepositoryId());
		1*bbbManager.logDebug("Listprice of SKU : " + skuId + " is : " + 0.0)
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite end")
		listPrice == 0.0
	}
	
	def"getListPriceBySite, when price is null"(){
		
		given:
		bbbManager =Spy()
		def RepositoryItem siteRep =Mock()
		def RepositoryItem priceList =Mock()
		def SiteManager site =Mock()
		def PriceListManager manager =Mock()
	
		bbbManager.setSiteManager(site)
		bbbManager.setLoggingError(true)
		bbbManager.setPriceListManager(manager)
		String siteName ="tbs"
		String productId ="product"
		String skuId ="skuId"
		site.getSite(siteName) >> siteRep
		1*siteRep.getRepositoryId() >> "Id"
		1*manager.getPriceListForSite(siteRep, "defaultListPriceList")>>priceList
		1*manager.getPrice(priceList, productId, skuId)>>null
		
		when:
		Double listPrice =bbbManager.getListPriceBySite( siteName, productId, skuId)
		
		then:
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite start")
		0*bbbManager.logError("Price List not configured for site" + siteRep.getRepositoryId());
		1*bbbManager.logDebug("Listprice of SKU : " + skuId + " is : " + 0.0)
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite end")
		listPrice == 0.0
	}
	
	def"getListPriceBySite, when RepositoryException is thrown"(){
		
		given:
		bbbManager =Spy()
		def RepositoryItem siteRep =Mock()
		def RepositoryItem priceList =Mock()
		def SiteManager site =Mock()
		def PriceListManager manager =Mock()
	
		bbbManager.setSiteManager(site)
		bbbManager.setLoggingError(true)
		bbbManager.setPriceListManager(manager)
		String siteName ="tbs"
		String productId ="product"
		String skuId ="skuId"
		1*site.getSite(siteName) >> siteRep
		2*siteRep.getRepositoryId() >> "Id"
		1*manager.getPriceListForSite(siteRep, "defaultListPriceList")>> {throw new RepositoryException("")}
		
		when:
		Double listPrice =bbbManager.getListPriceBySite( siteName, productId, skuId)
		
		then:
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite start")
		1*bbbManager.logError("Price List not configured for site" + siteRep.getRepositoryId());
		1*bbbManager.logDebug("Listprice of SKU : " + skuId + " is : " + 0.0)
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite end")
		listPrice == 0.0
	}
	
	def"getListPriceBySite, when RepositoryException is thrown while obtaining siteRepositoryItem"(){
		
		given:
		bbbManager=Spy()
		def RepositoryItem siteRep =Mock()
		def RepositoryItem priceList =Mock()
		def SiteManager site =Mock()
		def PriceListManager manager =Mock()
	
		bbbManager.setSiteManager(site)
		bbbManager.setLoggingError(true)
		bbbManager.setPriceListManager(manager)
		String siteName ="tbs"
		String productId ="product"
		String skuId ="skuId"
		site.getSite(siteName) >> {throw new RepositoryException("")}
		
		when:
		Double listPrice =bbbManager.getListPriceBySite( siteName, productId, skuId)
		
		then:
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite start")
		0*bbbManager.logError("Price List not configured for site" + siteRep.getRepositoryId());
		1*bbbManager.logDebug("Listprice of SKU : " + skuId + " is : " + 0.0)
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite end")
		listPrice == 0.0
	}
	
	def"getListPriceBySite, when PriceListException is thrown"(){
		
		given:
		bbbManager =Spy()
		def RepositoryItem siteRep =Mock()
		def RepositoryItem priceList =Mock()
		def SiteManager site =Mock()
		def PriceListManager manager =Mock()
	
		bbbManager.setSiteManager(site)
		bbbManager.setLoggingError(true)
		bbbManager.setPriceListManager(manager)
		String siteName ="tbs"
		String productId ="product"
		String skuId ="skuId"
		site.getSite(siteName) >> siteRep
		1*siteRep.getRepositoryId() >> "Id"
		1*manager.getPriceListForSite(siteRep, "defaultListPriceList")>> priceList
		1*manager.getPrice(priceList, productId, skuId) >>  {throw new PriceListException("")}
	    
		
		when:
		Double listPrice =bbbManager.getListPriceBySite( siteName, productId, skuId)
		
		then:
		1*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite start")
		0*bbbManager.logError("Price List not configured for site" + siteRep.getRepositoryId());
		BBBSystemException exc = thrown()
		0*bbbManager.logDebug("Listprice of SKU : " + skuId + " is : " + null)
		0*bbbManager.logDebug("BVFeedTools Method : getListPriceBySite end")
		listPrice == null
	}
	
	def"getItemPriceInfo, takes the BBBCommerceItem and get its priceInfo object and sets into the PriceInfoVO "(){
		
		given:
		def BBBCommerceItem item =Mock()
		def ItemPriceInfo itemInfo =Mock()
		def BBBPricingTools pTools = Mock()
		def UnitPriceBean u1 = Mock()
		def UnitPriceBean u2 = Mock()
	
		bbbManager.setPricingTools(pTools)
		item.getPriceInfo() >> itemInfo
		
		item.getQuantity() >> 10.0
		itemInfo.getRawTotalPrice() >> 50.0
		itemInfo.getAmount() >> 100.0
		itemInfo.getListPrice() >> 120.0
		itemInfo.getSalePrice() >> 80.0
		itemInfo.getOrderDiscountShare() >> 10
		pTools.round(_, 2) >> 91.66 >> 33.33
		
		List list = new ArrayList()
		List list1 = new ArrayList()
		list1.add("1")
		
		itemInfo.getCurrentPriceDetails() >> list
		1*pTools.generatePriceBeans(itemInfo.getCurrentPriceDetails()) >> [u1,u2]
		1*u1.getQuantity() >>5
		
		1*u1.getPricingModels() >> null
		2*u2.getPricingModels() >> list1
		
		when:
		PriceInfoVO priceVO = bbbManager.getItemPriceInfo(item) 
		
		then:
		priceVO.getRawAmount() == 50.0
		priceVO.getTotalAmount()==100.0
		priceVO.getUnitListPrice()==120.0
		priceVO.getTotalSavedAmount()==1100.0
		priceVO.getUnitSalePrice() == 80.0
		priceVO.getTotalSavedPercentage() == 91.66
		priceVO.getUnitSavedPercentage() == 33.33
		priceVO.getUnitSavedAmount() == 40.0
		priceVO.getTotalDiscountShare() == 10.0
		priceVO.getUndiscountedItemsCount() == 5
		priceVO.getPriceBeans() ==[u1,u2]
		priceVO.getItemPromotionVOList() == []
		
	}
	
	def"getItemPriceInfo, takes the BBBCommerceItem and get its priceInfo object and sets into the PriceInfo1VO "(){
		
		given:
		def BBBCommerceItem item =Mock()
		def ItemPriceInfo itemInfo =Mock()
		def BBBPricingTools pTools = Mock()
		def UnitPriceBean u1 = Mock()
	
		bbbManager.setPricingTools(pTools)
		item.getPriceInfo() >> itemInfo
		item.getQuantity() >> 10.0
		itemInfo.getRawTotalPrice() >> 50.0
		itemInfo.getAmount() >> 100.0
		itemInfo.getListPrice() >> 120.0
		itemInfo.getSalePrice() >> 0.0
		itemInfo.getOrderDiscountShare() >> 10
		
		List list = new ArrayList()
		List list1 = new ArrayList()
		itemInfo.getCurrentPriceDetails() >> list
		1*pTools.generatePriceBeans(itemInfo.getCurrentPriceDetails()) >> [u1]
		1*u1.getQuantity() >>5
		2*u1.getPricingModels() >> list1
		
		when:
		PriceInfoVO priceVO = bbbManager.getItemPriceInfo(item)
		
		then:
		priceVO.getRawAmount() == 50.0
		priceVO.getTotalAmount()==100.0
		priceVO.getUnitListPrice()==120.0
		priceVO.getTotalSavedAmount()==0.0
		priceVO.getUnitSalePrice() == 0.0
		priceVO.getTotalSavedPercentage() == 0.00
		priceVO.getUnitSavedPercentage() == 0.00
		priceVO.getUnitSavedAmount() == 0.0
		priceVO.getTotalDiscountShare() == 10.0
		priceVO.getUndiscountedItemsCount() == 5
		priceVO.getPriceBeans() ==[u1]
		priceVO.getItemPromotionVOList() == []		
	}
	
	def"getItemPriceInfo,when BBBCommerceItem is null"(){
		
		given:
		def ItemPriceInfo itemInfo =Mock()
		def BBBPricingTools pTools = Mock()
		def UnitPriceBean u1 = Mock()
		
		when:
		PriceInfoVO priceVO = bbbManager.getItemPriceInfo(null)
		
		then:
		priceVO.getRawAmount() == 0.0
		priceVO.getTotalAmount()==0.0
		priceVO.getUnitListPrice()==0.0
		priceVO.getTotalSavedAmount()==0.0
		priceVO.getUnitSalePrice() == 0.0
		priceVO.getTotalSavedPercentage() == 0.00
		priceVO.getUnitSavedPercentage() == 0.00
		priceVO.getUnitSavedAmount() == 0.0
		priceVO.getTotalDiscountShare() == 0.0
		priceVO.getUndiscountedItemsCount() ==0
		priceVO.getPriceBeans() ==null
		priceVO.getItemPromotionVOList() == []	
		
	}
	
	def"getShippingPriceInfo, takes shipping group object and get its priceInfo object and sets into the PriceInfoVO"(){
		
		given:
		def BBBHardGoodShippingGroup shippingGroup =Mock()
		def OrderImpl pOrder =Mock()
		def BBBShippingGroupCommerceItemRelationship rel =Mock()
		def BBBShippingGroupCommerceItemRelationship rel1 =Mock()
		def BBBCommerceItem c =Mock()
		def CommerceItem c1 = Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()
		def Range range =Mock()
		def UnitPriceBean u1 =Mock()
		def TaxPriceInfo taxInfo =Mock()
		def TaxPriceInfo shippingTaxInfo =Mock()
		def BBBShippingPriceInfo shippingPriceInfo =Mock()
		def BBBCommerceItemManager manager = Mock()
		bbbManager.setPricingTools(pTools)
		
		2*shippingGroup.getCommerceItemRelationships() >> [rel, rel1] 
		2*shippingGroup.getId() >> "Id"
		
		rel.getCommerceItem()  >> c
		c.getPriceInfo() >> item
		rel.getQuantity() >> 10
		rel.getRange() >>range
		rel.getRawtotalByAverage() >> 200.0
		rel.getAmountByAverage() >> 100.0
		item.getListPrice() >> 50.0
		
		rel1.getCommerceItem() >> c1

		1*pTools.generatePriceBeans(item.getCurrentPriceDetailsForRange(range)) >> [u1]
		1*pTools.fillAdjustments(_, shippingGroup) >> {}
		1*u1.getUnitPrice() >> 50.0
		1*u1.getQuantity() >> 5
		
		Map<String, Object> m = new HashMap<String, Object>()
		m.put(shippingGroup.getId(), shippingTaxInfo)
		
		shippingTaxInfo.getAmount() >> 30.0
		shippingTaxInfo.getCountyTax() >> 20.0
		shippingTaxInfo.getStateTax() >> 10.0
		
		pOrder.getTaxPriceInfo() >>taxInfo
		taxInfo.getShippingItemsTaxPriceInfos() >> m
		
	    shippingGroup.getPriceInfo() >>shippingPriceInfo
	    shippingPriceInfo.getSurcharge() >> 80.0
	    shippingPriceInfo.getFinalShipping() >> 70.0
		shippingPriceInfo.getRawShipping() >> 110.0
		shippingPriceInfo.getSurcharge() >> 50.0
		
		c.isLtlItem() >> true
		c.getId() >>"c"
		bbbManager.setCommerceItemManager(manager)
		PriceInfoVO ciPriceInfoVo = new PriceInfoVO()
		ciPriceInfoVo.setDeliverySurcharge(150.0)
		ciPriceInfoVo.setDeliverySurchargeSaving(140.0)
		ciPriceInfoVo.setAssemblyFee(130.0)
		1*manager.getLTLItemPriceInfo("c", _, pOrder)  >> ciPriceInfoVo
	
		
		when:
		PriceInfoVO priceInfoVO =bbbManager.getShippingPriceInfo(shippingGroup, pOrder)
		
		then:
		priceInfoVO.getShippingLevelTax() == 30.0
		priceInfoVO.getShippingCountyLevelTax()== 20.0
		priceInfoVO.getShippingStateLevelTax() == 10.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getEcoFeeTotal() == 0.0
		priceInfoVO.getItemCount() == 10
		priceInfoVO.getTotalSavedAmount() == 100.0
		priceInfoVO.getShippingGroupItemsTotal()== 250.0
		priceInfoVO.getTotalSurcharge() ==80.0
		priceInfoVO.getTotalShippingAmount() == 70.0
		priceInfoVO.getRawShippingTotal() == 110.0
		priceInfoVO.getTotalAmount() == 570.0
		priceInfoVO.getSurchargeSavings() == 0.0
		priceInfoVO.getShippingSavings() == 40.0
		priceInfoVO.getFinalShippingCharge() == 70.0
		priceInfoVO.getTotalDeliverySurcharge() == 150.0
		priceInfoVO.getDeliverySurchargeSaving() == 140.0
		priceInfoVO.getAssemblyFee() == 130.0
		
		
	}
	
	def"getShippingPriceInfo,when commerce item is not an ltl item, shippingPriceInfo is null and shippingTaxInfo is null"(){
		
		given:
		def BBBHardGoodShippingGroup shippingGroup =Mock()
		def OrderImpl pOrder =Mock()
		def BBBShippingGroupCommerceItemRelationship rel =Mock()
		def BBBShippingGroupCommerceItemRelationship rel1 =Mock()
		def BBBCommerceItem c =Mock()
		def CommerceItem c1 = Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()
		def Range range =Mock()
		def UnitPriceBean u1 =Mock()
		def TaxPriceInfo taxInfo =Mock()
		def TaxPriceInfo shippingTaxInfo =Mock()
		def BBBShippingPriceInfo shippingPriceInfo =Mock()
		def BBBCommerceItemManager manager = Mock()
		bbbManager.setPricingTools(pTools)
		
		2*shippingGroup.getCommerceItemRelationships() >> [rel, rel1]
		
		rel.getCommerceItem()  >> c
		c.getPriceInfo() >> item
		rel.getQuantity() >> 10
		rel.getRange() >>range
		rel.getRawtotalByAverage() >> 200.0
		rel.getAmountByAverage() >> 100.0
		item.getListPrice() >> 50.0
		
		rel1.getCommerceItem() >> c1

		1*pTools.generatePriceBeans(item.getCurrentPriceDetailsForRange(range)) >> [u1]
		1*pTools.fillAdjustments(_, shippingGroup) >> {}
		1*u1.getUnitPrice() >> 50.0
		1*u1.getQuantity() >> 5
		
		pOrder.getTaxPriceInfo() >>taxInfo
		taxInfo.getShippingItemsTaxPriceInfos() >> null
		
		shippingGroup.getPriceInfo() >>null
		shippingPriceInfo.getSurcharge() >> 0.0
		shippingPriceInfo.getFinalShipping() >> 0.0
		shippingPriceInfo.getRawShipping() >> 0.0
		shippingPriceInfo.getSurcharge() >> 50.0
		c.isLtlItem() >> false
		
		when:
		PriceInfoVO priceInfoVO =bbbManager.getShippingPriceInfo(shippingGroup, pOrder)
		
		then:
		priceInfoVO.getShippingLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax()== 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getEcoFeeTotal() == 0.0
		priceInfoVO.getItemCount() == 10
		priceInfoVO.getTotalSavedAmount() == 100.0
		priceInfoVO.getShippingGroupItemsTotal()== 250.0
		priceInfoVO.getTotalSurcharge() ==0.0
		priceInfoVO.getTotalShippingAmount() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalAmount() == 250.0
		priceInfoVO.getSurchargeSavings() == 0.0
		priceInfoVO.getShippingSavings() == 0.0
		priceInfoVO.getFinalShippingCharge() == 0.0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getDeliverySurchargeSaving() == 0.0
		priceInfoVO.getAssemblyFee() == 0.0
		
		
	}
	
	def"getShippingPriceInfo,when shipping group is not an instance of BBBHardGoodShippingGroup, item count is zero"(){
		
		given:
		def ShippingGroup shippingGroup =Mock()
		def OrderImpl pOrder =Mock()
		def BBBShippingGroupCommerceItemRelationship rel =Mock()
		def BBBShippingGroupCommerceItemRelationship rel1 =Mock()
		def BBBCommerceItem c =Mock()
		def CommerceItem c1 = Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()
		def Range range =Mock()
		def UnitPriceBean u1 =Mock()
		def TaxPriceInfo taxInfo =Mock()
		def TaxPriceInfo shippingTaxInfo =Mock()
		def BBBShippingPriceInfo shippingPriceInfo =Mock()
		def BBBCommerceItemManager manager = Mock()
		bbbManager.setPricingTools(pTools)
		
		1*shippingGroup.getCommerceItemRelationships() >> [rel, rel1]
		
		rel.getCommerceItem()  >> c
		c.getPriceInfo() >> item
		rel.getQuantity() >> 0
		rel.getRange() >>range
		rel.getRawtotalByAverage() >> 200.0
		rel.getAmountByAverage() >> 100.0
		item.getListPrice() >> 50.0
		
		rel1.getCommerceItem() >> c1

		1*pTools.generatePriceBeans(item.getCurrentPriceDetailsForRange(range)) >> [u1]
		1*pTools.fillAdjustments(_, shippingGroup) >> {}
		1*u1.getUnitPrice() >> 50.0
		1*u1.getQuantity() >> 5
		
		Map<String, Object> m = new HashMap<String, Object>()
		m.put(shippingGroup.getId(), null)
		
		pOrder.getTaxPriceInfo() >>taxInfo
		taxInfo.getShippingItemsTaxPriceInfos() >> m
		
		shippingGroup.getPriceInfo() >>null
		shippingPriceInfo.getSurcharge() >> 0.0
		shippingPriceInfo.getFinalShipping() >> 0.0
		shippingPriceInfo.getRawShipping() >> 0.0
		shippingPriceInfo.getSurcharge() >> 50.0
			
		when:
		PriceInfoVO priceInfoVO =bbbManager.getShippingPriceInfo(shippingGroup, pOrder)
		
		then:
		priceInfoVO.getShippingLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax()== 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getEcoFeeTotal() == 0.0
		priceInfoVO.getItemCount() == 0
		priceInfoVO.getTotalSavedAmount() == 100.0
		priceInfoVO.getShippingGroupItemsTotal()== 250.0
		priceInfoVO.getTotalSurcharge() ==0.0
		priceInfoVO.getTotalShippingAmount() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalAmount() == 250.0
		priceInfoVO.getSurchargeSavings() == 0.0
		priceInfoVO.getShippingSavings() == 0.0
		priceInfoVO.getFinalShippingCharge() == 0.0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getDeliverySurchargeSaving() == 0.0
		priceInfoVO.getAssemblyFee() == 0.0
		
		
	}
	
	def"getShippingPriceInfo,when commerce item is an instance of GiftWrapCommerceItem"(){
		
		given:
		def ShippingGroup shippingGroup =Mock()
		def OrderImpl pOrder =Mock()
		def BBBShippingGroupCommerceItemRelationship rel =Mock()
		def BBBShippingGroupCommerceItemRelationship rel1 =Mock()
		def BBBCommerceItem c =Mock()
		def GiftWrapCommerceItem c1 = Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()

		bbbManager.setPricingTools(pTools)
		1*shippingGroup.getCommerceItemRelationships() >> [rel, rel1]
		
		rel.getCommerceItem()  >> c
		c.getPriceInfo() >> null
		rel1.getCommerceItem() >> c1
		c1.getPriceInfo() >> item
		item.getAmount() >> 60.0
		pTools.fillAdjustments(_, shippingGroup) >> {}
		pOrder.getTaxPriceInfo() >>null
		shippingGroup.getPriceInfo() >>null
		
		when:
		PriceInfoVO priceInfoVO =bbbManager.getShippingPriceInfo(shippingGroup, pOrder)
		
		then:
		priceInfoVO.getShippingLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax()== 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getGiftWrapTotal() == 60.0
		priceInfoVO.getEcoFeeTotal() == 0.0
		priceInfoVO.getItemCount() == 0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getShippingGroupItemsTotal()== 0.0
		priceInfoVO.getTotalSurcharge() ==0.0
		priceInfoVO.getTotalShippingAmount() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalAmount() == 60.0
		priceInfoVO.getSurchargeSavings() == 0.0
		priceInfoVO.getShippingSavings() == 0.0
		priceInfoVO.getFinalShippingCharge() == 0.0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getDeliverySurchargeSaving() == 0.0
		priceInfoVO.getAssemblyFee() == 0.0
		
		
	}
	
	def"getShippingPriceInfo,when commerceitem is an instance of EcoFeeCommerceItem"(){
		
		given:
		def ShippingGroup shippingGroup =Mock()
		def OrderImpl pOrder =Mock()
		def BBBShippingGroupCommerceItemRelationship rel =Mock()
		def BBBShippingGroupCommerceItemRelationship rel1 =Mock()
		def GiftWrapCommerceItem c =Mock()
		def EcoFeeCommerceItem c1 = Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()
		bbbManager.setPricingTools(pTools)
		
		1*shippingGroup.getCommerceItemRelationships() >> [rel, rel1]
		
		rel.getCommerceItem()  >> null
		rel1.getCommerceItem() >> c1
		c1.getPriceInfo() >> item
		item.getAmount() >> 60.0

		pTools.fillAdjustments(_, shippingGroup) >> {}
		pOrder.getTaxPriceInfo() >>null
		shippingGroup.getPriceInfo() >>null

		when:
		PriceInfoVO priceInfoVO =bbbManager.getShippingPriceInfo(shippingGroup, pOrder)
		
		then:
		priceInfoVO.getShippingLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax()== 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getEcoFeeTotal() == 60.0
		priceInfoVO.getItemCount() == 0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getShippingGroupItemsTotal()== 0.0
		priceInfoVO.getTotalSurcharge() ==0.0
		priceInfoVO.getTotalShippingAmount() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalAmount() == 60.0
		priceInfoVO.getSurchargeSavings() == 0.0
		priceInfoVO.getShippingSavings() == 0.0
		priceInfoVO.getFinalShippingCharge() == 0.0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getDeliverySurchargeSaving() == 0.0
		priceInfoVO.getAssemblyFee() == 0.0
		
		
	}
	
	def"getShippingPriceInfo,when order is null"(){
		
		given:
		def ShippingGroup shippingGroup =Mock()
		def OrderImpl pOrder =Mock()
		def BBBShippingGroupCommerceItemRelationship rel =Mock()
		def EcoFeeCommerceItem c =Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()
		bbbManager.setPricingTools(pTools)
		
		1*shippingGroup.getCommerceItemRelationships() >> [rel]
		
		rel.getCommerceItem()  >> c
		c.getPriceInfo() >> item
		item.getAmount() >> 60.0

		pTools.fillAdjustments(_, shippingGroup) >> {}
		shippingGroup.getPriceInfo() >>null

		when:
		PriceInfoVO priceInfoVO =bbbManager.getShippingPriceInfo(shippingGroup, null)
		
		then:
		priceInfoVO.getShippingLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax()== 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getEcoFeeTotal() == 60.0
		priceInfoVO.getItemCount() == 0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getShippingGroupItemsTotal()== 0.0
		priceInfoVO.getTotalSurcharge() ==0.0
		priceInfoVO.getTotalShippingAmount() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalAmount() == 60.0
		priceInfoVO.getSurchargeSavings() == 0.0
		priceInfoVO.getShippingSavings() == 0.0
		priceInfoVO.getFinalShippingCharge() == 0.0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getDeliverySurchargeSaving() == 0.0
		priceInfoVO.getAssemblyFee() == 0.0
	}
	
	
	def"getShippingPriceInfo,when shipping group is null"(){
		
		given:
		def OrderImpl pOrder =Mock()
		
		when:
		PriceInfoVO priceInfoVO =bbbManager.getShippingPriceInfo(null, pOrder)
		
		then:
		priceInfoVO.getShippingLevelTax() == 0.0
		priceInfoVO.getShippingCountyLevelTax()== 0.0
		priceInfoVO.getShippingStateLevelTax() == 0.0
		priceInfoVO.getGiftWrapTotal() == 0.0
		priceInfoVO.getEcoFeeTotal() == 0.0
		priceInfoVO.getItemCount() == 0
		priceInfoVO.getTotalSavedAmount() == 0.0
		priceInfoVO.getShippingGroupItemsTotal()== 0.0
		priceInfoVO.getTotalSurcharge() ==0.0
		priceInfoVO.getTotalShippingAmount() == 0.0
		priceInfoVO.getRawShippingTotal() == 0.0
		priceInfoVO.getTotalAmount() == 0.0
		priceInfoVO.getSurchargeSavings() == 0.0
		priceInfoVO.getShippingSavings() == 0.0
		priceInfoVO.getFinalShippingCharge() == 0.0
		priceInfoVO.getTotalDeliverySurcharge() == 0.0
		priceInfoVO.getDeliverySurchargeSaving() == 0.0
		priceInfoVO.getAssemblyFee() == 0.0
	}
	
	def"getShippingGroupCommerceItemPriceInfo , retrieves the bean and populates the priceInfoVO object"(){
		
		given:
		def ShippingGroupCommerceItemRelationship shippingGroupCommerceItemRelationship =Mock()
		def Range r = Mock()
		def CommerceItem c =Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()
		def UnitPriceBean u1 =Mock()
		def UnitPriceBean u2 =Mock()
		
		List list = new ArrayList()
		List list1 = new ArrayList()
		list1.add("1")
		
		u1.getUnitPrice() >> 100.0
		u1.getQuantity()  >> 10
		u1.getPricingModels() >>list 
		u2.getUnitPrice() >> 200.0
		u2.getQuantity()  >> 15
		u2.getPricingModels() >> list1
		
		bbbManager.setPricingTools(pTools)
		shippingGroupCommerceItemRelationship.getCommerceItem() >> c
		c.getPriceInfo() >>item
		shippingGroupCommerceItemRelationship.getRange() >> r
		pTools.generatePriceBeans(item.getCurrentPriceDetailsForRange(r)) >>[u1,u2]
		
		when:
		PriceInfoVO priceInfoVO=bbbManager.getShippingGroupCommerceItemPriceInfo(shippingGroupCommerceItemRelationship)
		
		then:
		priceInfoVO.getUndiscountedItemsCount() == 10
		priceInfoVO.getPriceBeans() == [u1,u2]
		priceInfoVO.getShippingGroupItemTotal() == 4000.0
		
	}
	
	def"getShippingGroupCommerceItemPriceInfo , when pricingModel retrieved from priceBean is null"(){
		
		given:
		def ShippingGroupCommerceItemRelationship shippingGroupCommerceItemRelationship =Mock()
		def Range r = Mock()
		def CommerceItem c =Mock()
		def ItemPriceInfo item =Mock()
		def BBBPricingTools pTools =Mock()
		def UnitPriceBean u1 =Mock()
		
		u1.getUnitPrice() >> 100.0
		u1.getQuantity()  >> 10
		u1.getPricingModels() >>null
		
		bbbManager.setPricingTools(pTools)
		shippingGroupCommerceItemRelationship.getCommerceItem() >> c
		c.getPriceInfo() >>item
		shippingGroupCommerceItemRelationship.getRange() >> r
		pTools.generatePriceBeans(item.getCurrentPriceDetailsForRange(r)) >>[u1]
		
		when:
		PriceInfoVO priceInfoVO=bbbManager.getShippingGroupCommerceItemPriceInfo(shippingGroupCommerceItemRelationship)
		
		then:
		priceInfoVO.getUndiscountedItemsCount() == 10.0
		priceInfoVO.getPriceBeans() == [u1]
		priceInfoVO.getShippingGroupItemTotal() == 1000.0
		
	}
	
	
	
	
}
