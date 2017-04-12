package com.bbb.commerce.pricing

import java.util.Locale
import java.util.Map;
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.logging.LogMessageFormatter;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBItemPriceInfo;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO
import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.DetailedItemPriceTools
import atg.commerce.pricing.FilteredCommerceItem
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingTools
import atg.commerce.pricing.priceLists.Constants;
import atg.commerce.pricing.priceLists.PriceListException
import atg.commerce.pricing.priceLists.PriceListManager
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;

class InCartPriceListCalculatorSpecification  extends BBBExtendedSpec  {

	def RepositoryItem pPricingModel =Mock()
	def RepositoryItem pProfile =Mock()
	def BBBCatalogTools cTools=Mock()
	def PricingTools pTools =Mock()
	def MutableRepository mRep =Mock()
	def PriceListManager manager =Mock()
	def InCartPriceListCalculator customCalc
	def AuxiliaryData aux =Mock()

	int i
	Locale pLocale = Locale.forLanguageTag("en_US")

	def setup(){
		customCalc = Spy()
		customCalc.setCatalogTools(cTools)
		customCalc.setPricingTools(pTools)
		customCalc.setPriceListRepository(mRep)
		customCalc.setPriceListManager(manager)
	}

	def sharedSpecs(){
		BBBDynamicPriceSkuVO dSku= new BBBDynamicPriceSkuVO()
		dSku.setInCartFlag(true)
		SKUDetailVO sku= new SKUDetailVO()
		sku.setDynamicSKUPriceVO(dSku)
		cTools.getSKUDynamicPriceDetails(*_) >> sku
	}


	def"set salePrice of commerceItem as incartprice if in cart flag is true for the sku."(){
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()
		def PricingAdjustment adj = Mock()
		def DetailedItemPriceTools tool =Mock()

		pPriceQuote.setListPrice(30)
		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST , "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCoreConstants.FROM_REIGISTRY_OWNER,true)
		List list = new ArrayList()
		list.add("new")

		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"
		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId() >>"BedBathCanada"
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m
		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>price
		price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		pTools.round(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME)) >> 100
		pTools.round(pItem.getQuantity() * pPriceQuote.getListPrice()) >>60.0
		pPriceQuote.getCurrentPriceDetails().add("old")
		pTools.getDetailedItemPriceTools() >>tool

		tool.createInitialDetailedItemPriceInfos(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME),pPriceQuote,pItem,pPricingModel,
				pLocale,pProfile,pExtraParameters,Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION) >> list
		pPriceQuote.getAdjustments().add(adj)

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST))
		1*customCalc.logDebug("rounding item price.total price: "+ pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
		pPriceQuote.getRawTotalPrice() == 60
		pPriceQuote.getAmount() == 100
		pPriceQuote.getCurrentPriceDetails().contains("old")==false
		pPriceQuote.getCurrentPriceDetails().contains("new")==true
		pPriceQuote.getAdjustments().contains(adj)==false
		pPriceQuote.getAdjustments().isEmpty()==false
		pPriceQuote.getCurrentPrice() == 50.0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}

	def"if Item is not an instance of BBBCommerceItem and sale price is not set"(){
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def CommerceItem pItem =Mock()
		pPriceQuote.setListPrice(30)
		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		Map<String, Object> pExtraParameters = new HashMap<>()

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		0*customCalc.logDebug("In cart flag is true")
		pPriceQuote.getCurrentPrice() == 30
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getAmount() == 0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)
	}

	def"if sku is not in cart"(){
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		BBBDynamicPriceSkuVO dSku1= new BBBDynamicPriceSkuVO()
	    SKUDetailVO sku1= new SKUDetailVO()
		customCalc.setLoggingDebug(true)

		pPriceQuote.setListPrice(30)
		dSku1.setInCartFlag(false)
		sku1.setDynamicSKUPriceVO(dSku1)
		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		Map<String, Object> pExtraParameters = new HashMap<>()
			cTools.getSKUDynamicPriceDetails(*_) >> sku1

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		0*customCalc.logDebug("In cart flag is true")
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getAmount() == 0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- InCartPriceListCalculator.priceItem"))
	}


	def"set salePrice of commerceItem as incartprice if in cart flag is true for the sku when site id is canadaTBS"(){	
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def PricingAdjustment adj = Mock()
		def DetailedItemPriceInfo det =Mock()
		def DetailedItemPriceTools tool =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()

		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST , "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()
		List list = new ArrayList()
		list.add(det)


		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"

		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId() >>BBBCoreConstants.SITEBAB_CA_TBS
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m

		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>price
		price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		pTools.round(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME)) >> 100
		pTools.round(pItem.getQuantity() * pPriceQuote.getListPrice()) >>60.0
		pTools.getDetailedItemPriceTools() >>tool
		tool.createInitialDetailedItemPriceInfos(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME),pPriceQuote,pItem,pPricingModel, pLocale,pProfile,pExtraParameters,Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION) >> list
		pPriceQuote.getAdjustments().add(adj)

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST ))
		1*customCalc.logDebug("rounding item price.total price: "+ pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
		pPriceQuote.getRawTotalPrice() == 60
		pPriceQuote.getAmount() == 100
		pPriceQuote.getCurrentPriceDetails().contains(det)==true
		pPriceQuote.getAdjustments().contains(adj)==false
		pPriceQuote.getAdjustments().isEmpty()==false
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}

	def"set salePrice of commerceItem  if in cart flag is true for the sku, country is Mexico and site id is TBS_BedBathUS"(){

		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()
		def DetailedItemPriceTools tool =Mock()
		def DetailedItemPriceInfo det =Mock()
		def PricingAdjustment adj = Mock()
		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCatalogConstants.MEXICO_CODE+ BBBCatalogConstants.IN_CART_PRICELIST, "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCoreConstants.FROM_REIGISTRY_OWNER,true)
		List list = new ArrayList()
		list.add(det)

		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"

		cTools.returnCountryFromSession() >> "MX"
		customCalc.extractSiteId()>> BBBCoreConstants.TBS_BEDBATH_US
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m
		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>price
		price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		pTools.round(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME)) >> 100
		pTools.round(pItem.getQuantity() * pPriceQuote.getListPrice()) >>60.0
		pTools.getDetailedItemPriceTools() >>tool

		tool.createInitialDetailedItemPriceInfos(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME),pPriceQuote,pItem,pPricingModel,
				pLocale,pProfile,pExtraParameters,Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION) >> list
		pPriceQuote.getAdjustments().add(adj)

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCatalogConstants.MEXICO_CODE+ BBBCatalogConstants.IN_CART_PRICELIST))
		1*customCalc.logDebug("rounding item price.total price: "+ pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
		pPriceQuote.getRawTotalPrice() == 60
		pPriceQuote.getAmount() == 100
		pPriceQuote.getCurrentPriceDetails().contains(det)==true
		pPriceQuote.getAdjustments().contains(adj)==false
		pPriceQuote.getAdjustments().isEmpty()==false
		pPriceQuote.getSalePrice() ==50.0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}

	def"set salePrice of commerceItem  if in cart flag is true for the sku, country is Mexico,site id is TBS_BedBathUS and sale price is not zero"(){ //

		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()
		def DetailedItemPriceTools tool =Mock()
		def DetailedItemPriceInfo det =Mock()
		def PricingAdjustment adj = Mock()

		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCatalogConstants.MEXICO_CODE+ BBBCatalogConstants.IN_CART_PRICELIST, "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCoreConstants.FROM_REIGISTRY_OWNER,true)
		List list = new ArrayList()
		list.add(det)

		pPriceQuote.setSalePrice(80)
		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"
		cTools.returnCountryFromSession() >> "MX"
		customCalc.extractSiteId()>> BBBCoreConstants.TBS_BEDBATH_US
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m
		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>price
		price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		pTools.round(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME)) >> 100
		pTools.round(pItem.getQuantity() * pPriceQuote.getListPrice()) >>60.0
		pTools.getDetailedItemPriceTools() >>tool
		tool.createInitialDetailedItemPriceInfos(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME),pPriceQuote,pItem,
				pPricingModel, pLocale,pProfile,pExtraParameters,Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION) >> list
		pPriceQuote.getAdjustments().add(adj)

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCatalogConstants.MEXICO_CODE+ BBBCatalogConstants.IN_CART_PRICELIST))
		1*customCalc.logDebug("rounding item price.total price: "+ pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
		pPriceQuote.getRawTotalPrice() == 60
		pPriceQuote.getAmount() == 100
		pPriceQuote.getCurrentPriceDetails().contains(det)==true
		pPriceQuote.getAdjustments().contains(adj)==false
		pPriceQuote.getAdjustments().isEmpty()==false
		pPriceQuote.getSalePrice() == 50.0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}
	def"set salePrice of commerceItem if in cart flag is true for the sku, country is Mexico and site id is TBS_BuyBuyBaby"(){ //-do

		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()
		def DetailedItemPriceTools tool =Mock()
		def DetailedItemPriceInfo det =Mock()
		def PricingAdjustment adj = Mock()
		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCatalogConstants.MEXICO_CODE+ BBBCatalogConstants.IN_CART_PRICELIST, "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCoreConstants.FROM_REIGISTRY_OWNER,true)
		List list = new ArrayList()
		list.add(det)


		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"
		cTools.returnCountryFromSession() >> "MX"
		customCalc.extractSiteId()>> BBBCoreConstants.SITEBAB_BABY_TBS
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m

		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>price
		price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		pTools.round(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME)) >> 100
		pTools.round(pItem.getQuantity() * pPriceQuote.getListPrice()) >>60.0
		pTools.getDetailedItemPriceTools() >>tool
		tool.createInitialDetailedItemPriceInfos(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME),pPriceQuote,pItem,pPricingModel,
				pLocale,pProfile,pExtraParameters,Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION) >> list
		pPriceQuote.getAdjustments().add(adj)

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCatalogConstants.MEXICO_CODE+ BBBCatalogConstants.IN_CART_PRICELIST))
		1*customCalc.logDebug("rounding item price.total price: "+ pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
		pPriceQuote.getRawTotalPrice() == 60
		pPriceQuote.getAmount() == 100
		pPriceQuote.getCurrentPriceDetails().contains(det)==true
		pPriceQuote.getAdjustments().contains(adj)==false
		pPriceQuote.getAdjustments().isEmpty()==false
		pPriceQuote.getSalePrice() == 50.0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}

	def"set salePrice of commerceItem as incartprice if in cart flag is true for the sku when country is  not Mexico and site id is BedBathUS"(){

		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()
		def DetailedItemPriceTools tool =Mock()
		def DetailedItemPriceInfo det =Mock()
		def PricingAdjustment adj = Mock()

		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_US+ BBBCatalogConstants.IN_CART_PRICELIST, "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCoreConstants.FROM_REIGISTRY_OWNER,true)
		List list = new ArrayList()
		list.add(det)


		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"

		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId()>> BBBCoreConstants.SITE_BAB_US
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m

		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>price
		price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> new Double(50.0)
		pTools.round(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME)) >> 100
		pTools.round(pItem.getQuantity() * pPriceQuote.getListPrice()) >>60.0
		pTools.getDetailedItemPriceTools() >>tool
		tool.createInitialDetailedItemPriceInfos(pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME),pPriceQuote,pItem,
				pPricingModel, pLocale,pProfile,pExtraParameters,Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION) >> list
		pPriceQuote.getAdjustments().add(adj)

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCoreConstants.SITE_BAB_US+ BBBCatalogConstants.IN_CART_PRICELIST))
		1*customCalc.logDebug("rounding item price.total price: "+ pItem.getQuantity()*price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
		pPriceQuote.getRawTotalPrice() == 60
		pPriceQuote.getAmount() == 100
		pPriceQuote.getCurrentPriceDetails().contains(det)==true
		pPriceQuote.getAdjustments().contains(adj)==false
		pPriceQuote.getAdjustments().isEmpty()==false
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}

	def"set salePrice of commerceItem as incartprice if in cart flag is true for the sku when priceListItem.getPrice() returns null"(){ 

		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def AuxiliaryData aux =Mock()

		sharedSpecs()
		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"

		cTools.returnCountryFromSession() >> null
		customCalc.extractSiteId()>> BBBCoreConstants.SITE_BAB_US
		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_US+ BBBCatalogConstants.IN_CART_PRICELIST, "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m
		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>null

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCoreConstants.SITE_BAB_US+ BBBCatalogConstants.IN_CART_PRICELIST))
		0*customCalc.logDebug("rounding item price.total price: "+ _)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)
	}

	def"set salePrice of commerceItem as incartprice if in cart flag is true for the sku when priceListItem is null"(){
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		
		sharedSpecs()
		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId()>> BBBCoreConstants.SITE_BAB_US
		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_US+ BBBCatalogConstants.IN_CART_PRICELIST, "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()

		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m
		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>null
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>null

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCoreConstants.SITE_BAB_US+ BBBCatalogConstants.IN_CART_PRICELIST))
		0*customCalc.logDebug("rounding item price.total price: "+ _)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}

	def"set salePrice of commerceItem as incartprice if in cart flag is true for the sku when inCartPriceListId is null"(){

		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()

		sharedSpecs()
		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId()>> BBBCoreConstants.SITE_BAB_US
		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_US+ BBBCatalogConstants.IN_CART_PRICELIST, null)
		Map<String, Object> pExtraParameters = new HashMap<>()
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		0*customCalc.logDebug(" Price List id is: " + _)
		0*customCalc.logDebug("rounding item price.total price: "+ _)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)
	}
	
	def"check if PriceListException  is thrown"(){
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def PricingAdjustment adj = Mock()
		def DetailedItemPriceInfo det =Mock()
		def DetailedItemPriceTools tool =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()

		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST , "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()

		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"

		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId() >>BBBCoreConstants.SITEBAB_CA_TBS
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m

		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >>priceListItem
		manager.getPrice(priceListItem,aux.getProductId(),pItem.getCatalogRefId()) >>{throw new PriceListException("")}
		pPriceQuote.getAdjustments().add(adj)

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST ))
		0*customCalc.logDebug("rounding item price.total price: "+ _)
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getAmount() == 0
		1* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)

	}
	
	def"check if BBBBusinessException is thrown"(){
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def PricingAdjustment adj = Mock()
		def DetailedItemPriceInfo det =Mock()
		def DetailedItemPriceTools tool =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()

		sharedSpecs()

		Map<String, Object> pExtraParameters = new HashMap<>()

		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"

		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId() >>BBBCoreConstants.SITE_BAB_CA
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> {throw new BBBBusinessException("")}

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		0*customCalc.logDebug(" Price List id is: " + _)
		0*customCalc.logDebug("rounding item price.total price: "+ _)
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getAmount() == 0
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)
		1* customCalc.logError("InCartPriceListCalculator.priceItem BBBBusinessException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
	}
	
	def"check if BBBSystemException is thrown"(){
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def PricingAdjustment adj = Mock()
		def DetailedItemPriceInfo det =Mock()
		def DetailedItemPriceTools tool =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()

		BBBDynamicPriceSkuVO dSku= new BBBDynamicPriceSkuVO()
		dSku.setInCartFlag(true)
		SKUDetailVO sku= new SKUDetailVO()
		sku.setDynamicSKUPriceVO(dSku)
		cTools.getSKUDynamicPriceDetails(*_) >> {throw new BBBSystemException("")}

		Map<String, Object> pExtraParameters = new HashMap<>()

		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		0*customCalc.logDebug("In cart flag is true")
		0*customCalc.logDebug(" Price List id is: " + _)
		0*customCalc.logDebug("rounding item price.total price: "+ _)
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getAmount() == 0
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)
		1* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
	}
	
	def"check if RepositoryException is thrown"(){
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def PricingAdjustment adj = Mock()
		def DetailedItemPriceInfo det =Mock()
		def DetailedItemPriceTools tool =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem priceListItem =Mock()
		def RepositoryItem price =Mock()

		sharedSpecs()

		Map <String, String> m = new HashMap<String, String>()
		m.put(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST , "priceList")
		Map<String, Object> pExtraParameters = new HashMap<>()

		pItem.getQuantity() >>2
		pItem.getCatalogRefId() >> "refId"
		pItem.getAuxiliaryData() >>aux
		aux.getProductId()>>"123"

		cTools.returnCountryFromSession() >> "country"
		customCalc.extractSiteId() >>BBBCoreConstants.SITE_BAB_CA
		cTools.getConfigValueByconfigType(BBBCoreConstants.CONTENT_CATALOG_KEYS) >> m
		mRep.getItem("priceList", BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> {throw new RepositoryException("")}

		when:
		customCalc.priceItem(pPriceQuote, pItem, pPricingModel, pLocale,pProfile,pExtraParameters)

		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - InCartPriceListCalculator.priceItem"))
		1*customCalc.logDebug("Checking in cart flag for sku:" + pItem.getCatalogRefId())
		1*customCalc.logDebug("In cart flag is true")
		1*customCalc.logDebug(" Price List id is: " + m.get(BBBCoreConstants.SITE_BAB_CA + BBBCatalogConstants.IN_CART_PRICELIST))
		0*customCalc.logDebug("rounding item price.total price: "+ _)
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getAmount() == 0
		1* customCalc.logError("InCartPriceListCalculator.priceItem RepositoryException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem BBBSystemException", _)
		0* customCalc.logError("InCartPriceListCalculator.priceItem PriceListException", _)
	}


}
