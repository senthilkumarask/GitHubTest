package com.bbb.commerce.pricing

import java.util.ArrayList;
import java.util.List
import java.util.Locale;
import java.util.Map

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException
import atg.commerce.order.OrderHolder
import atg.commerce.order.InvalidParameterException;
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.DetailedItemPriceTools
import atg.commerce.pricing.FilteredCommerceItem
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import atg.commerce.pricing.PricingContext
import atg.commerce.pricing.PricingTools
import atg.commerce.pricing.priceLists.Constants;
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.order.bean.BBBItemPriceInfo;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

class CustomPriceListCalculatorSpecification  extends BBBExtendedSpec {

	def PricingContext pricingcontext = Mock()
	FilteredCommerceItem fItem  = Mock()
	def BBBCommerceItem bbbCommerceItem = Mock()
	def CustomPriceListCalculator customCalc
	def CommerceItem pItem =Mock()
	def RepositoryItem pPricingModel =Mock()
	def RepositoryItem pProfile =Mock()
	def BBBCatalogTools cTools=Mock()
	def PricingTools pTools =Mock()
	Locale local = Locale.forLanguageTag("en_US")

	int i
	Locale pLocale
	
	
	def setup(){
		customCalc = Spy()
		customCalc.setCatalogTools(cTools)
		customCalc.setPricingTools(pTools)
	}
	
	
	def"priceItem,sets priceinfo for LTLDeliveryChargeCommerceItem "(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLDeliveryChargeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		
		ShipMethodVO  shipVo = new ShipMethodVO()
		shipVo.setShipMethodId("express")
		shipVo.setDeliverySurcharge(20.0)
		
		ShipMethodVO  shipVo1 = new ShipMethodVO()
		shipVo1.setShipMethodId("Normal")
		shipVo1.setDeliverySurcharge(30.0)
		BBBShippingGroupCommerceItemRelationship bbbRelation =new  BBBShippingGroupCommerceItemRelationship()
		
		List<ShipMethodVO> list = new ArrayList<ShipMethodVO>()
		list.add(shipVo)
		list.add(shipVo1)
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >>"refId"
		cTools.getLTLEligibleShippingMethods(cItem.getCatalogRefId(), customCalc.extractSiteID(), requestMock.getLocale().toString()) >>list
		
		ltl.getShippingGroupRelationships() >> [bbbRelation]
		bbbRelation.setShippingGroup(hg)
		hg.getShippingMethod()>>"express"
		ltl.getQuantity() >> 2.0
		pTools.round(ltl.getQuantity()*shipVo.getDeliverySurcharge())>> 40.0
		
		List l = new ArrayList()
		l.add("old")
		List newList = new ArrayList()
		newList.add("new")
		
		def PricingAdjustment adj = Mock()
		pPriceQuote.getAdjustments().add(adj)
		
		pPriceQuote.setCurrentPriceDetails(l)
	
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		pPriceQuote.setSalePrice(10)
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("delivery item is present");
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("shipMethod: express quantity: 2.0 delivery charge: " + shipVo.getDeliverySurcharge())
		1*customCalc.logDebug("rounding item price.total price: " + ltl.getQuantity()*shipVo.getDeliverySurcharge())
		pPriceQuote.getRawTotalPrice() == 40.0
		pPriceQuote.getListPrice() == 20.0
		l.contains("old") == false
		l.contains("new") == true
		pPriceQuote.getAdjustments().contains(adj) == false
		pPriceQuote.getAdjustments().isEmpty() == false
		pPriceQuote.getCurrentPrice()==10.0
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"when commerce item retrieved from order is null"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLDeliveryChargeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
	    order.getCommerceItem("relation") >>null
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		
		ShipMethodVO  shipVo = new ShipMethodVO()
		shipVo.setShipMethodId("express")
		shipVo.setDeliverySurcharge(20.0)
		BBBShippingGroupCommerceItemRelationship bbbRelation =new  BBBShippingGroupCommerceItemRelationship()
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		ltl.getQuantity() >> 2.0
		pTools.round(ltl.getQuantity()*shipVo.getDeliverySurcharge())>> 40.0
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("delivery item is present");
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + null);
		0*customCalc.logDebug("shipMethod: express quantity: 2.0 delivery charge: " + shipVo.getDeliverySurcharge())
		0*customCalc.logDebug("rounding item price.total price: " + ltl.getQuantity()*shipVo.getDeliverySurcharge())
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getCurrentPrice()==0.0
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"when ship method and ltl ship methodId is different"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLDeliveryChargeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		
		ShipMethodVO  shipVo = new ShipMethodVO()
		shipVo.setShipMethodId("express")
		shipVo.setDeliverySurcharge(0.0)
		BBBShippingGroupCommerceItemRelationship bbbRelation =new  BBBShippingGroupCommerceItemRelationship()
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local 
		cItem.getCatalogRefId() >>"refId"
		cTools.getLTLEligibleShippingMethods(cItem.getCatalogRefId(), customCalc.extractSiteID(), requestMock.getLocale().toString()) >>[shipVo]
		
		ltl.getShippingGroupRelationships() >> [bbbRelation]
		bbbRelation.setShippingGroup(hg)
		hg.getShippingMethod()>>"expediate"
		ltl.getQuantity() >> 2.0
		pTools.round(ltl.getQuantity()*shipVo.getDeliverySurcharge())>> 0.0
		
		List l = new ArrayList()

		List newList = new ArrayList()
		newList.add("new")
		
		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("delivery item is present");
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("shipMethod: expediate quantity: 2.0 delivery charge: " + shipVo.getDeliverySurcharge())
		1*customCalc.logDebug("rounding item price.total price: " + ltl.getQuantity()*shipVo.getDeliverySurcharge())
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getListPrice()==0.0
		pPriceQuote.getCurrentPrice()==0.0
		l.contains("new")==true
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"tests if BBBSystemException is thrown"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLDeliveryChargeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		
		ShipMethodVO  shipVo = new ShipMethodVO()
		shipVo.setShipMethodId("express")
		shipVo.setDeliverySurcharge(0.0)
		BBBShippingGroupCommerceItemRelationship bbbRelation =new  BBBShippingGroupCommerceItemRelationship()
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >>"refId"
		cTools.getLTLEligibleShippingMethods(cItem.getCatalogRefId(), customCalc.extractSiteID(), requestMock.getLocale().toString()) >>{throw new BBBSystemException("")}
		
		ltl.getShippingGroupRelationships() >> [bbbRelation]
		bbbRelation.setShippingGroup(hg)
		hg.getShippingMethod()>>"expediate"
		ltl.getQuantity() >> 2.0
		pTools.round(ltl.getQuantity()*shipVo.getDeliverySurcharge())>> 0.0
		
		List l = new ArrayList()
		List newList = new ArrayList()
		newList.add("new")
		
		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("delivery item is present");
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("shipMethod: expediate quantity: 2.0 delivery charge: " + shipVo.getDeliverySurcharge())
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getListPrice()==0.0
		pPriceQuote.getCurrentPrice()==0.0
		l.contains("new")==true
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"tests if BBBBusinessException is thrown"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLDeliveryChargeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		
		ShipMethodVO  shipVo = new ShipMethodVO()
		shipVo.setShipMethodId("express")
		shipVo.setDeliverySurcharge(0.0)
		BBBShippingGroupCommerceItemRelationship bbbRelation =new  BBBShippingGroupCommerceItemRelationship()
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >>"refId"
		cTools.getLTLEligibleShippingMethods(cItem.getCatalogRefId(), customCalc.extractSiteID(), requestMock.getLocale().toString()) >>{throw new BBBBusinessException("")}
		
		ltl.getShippingGroupRelationships() >> [bbbRelation]
		bbbRelation.setShippingGroup(hg)
		hg.getShippingMethod()>>"expediate"
		ltl.getQuantity() >> 2.0
		pTools.round(ltl.getQuantity()*shipVo.getDeliverySurcharge())>> 0.0
		
		List l = new ArrayList()
		List newList = new ArrayList()
		newList.add("new")
		
		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("delivery item is present");
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("shipMethod: expediate quantity: 2.0 delivery charge: " + shipVo.getDeliverySurcharge())
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getListPrice()==0.0
		pPriceQuote.getCurrentPrice()==0.0
		l.contains("new")==true
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"priceItem,sets priceinfo for LTLAssemblyCommerceItem"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLAssemblyFeeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		def PricingAdjustment adj=Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.REPRICE_SHOPPING_CART_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >> "refId"
		ltl.getQuantity() >> 2.0
		
		cTools.getAssemblyCharge(customCalc.extractSiteID(), cItem.getCatalogRefId()) >> 20.0
		pTools.round(ltl.getQuantity()*cTools.getAssemblyCharge(customCalc.extractSiteID(),cItem.getCatalogRefId()))>> 40.0
		
		def DetailedItemPriceInfo detail=Mock()
		def DetailedItemPriceInfo detail1=Mock()
		
		List <DetailedItemPriceInfo> l = new ArrayList<DetailedItemPriceInfo>()
		l.add(detail)
		
	    List <DetailedItemPriceInfo> newList = new ArrayList<DetailedItemPriceInfo>()
		newList.add(detail1)
		
		pPriceQuote.getAdjustments().add(adj)
		
		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		pPriceQuote.setSalePrice(10.0)
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("assembly item is present");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("rounding item price.")
		pPriceQuote.getRawTotalPrice() == 40.0
		pPriceQuote.getListPrice() == 20.0
		pPriceQuote.getAmount() == 40.0
		pPriceQuote.getCurrentPrice()==10.0
		l.contains(detail)==false
		l.contains(detail1)==true
		pPriceQuote.getAdjustments().contains(adj) ==false
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"priceItem,checks if CommerceItem is neither LTLdelivery nor LTL Assembly Item LTL Assembly Item and sale price is greater than zero"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def BBBCommerceItem ltl =Mock()
		
		customCalc.setLoggingDebug(true)
		requestMock.getLocale() >> local
		customCalc.extractSiteID() >> "tbs"
		pPriceQuote.setListPrice(20.0)
		pPriceQuote.setSalePrice(10.0)
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		pPriceQuote.getCurrentPrice() == 10.0
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		0*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		0*customCalc.logDebug("assembly item is present");
		0*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))	
	}
	
	def"priceItem,tests if BBBBusinessException is thrown when commerce item is an instance of LTL assembely Item"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLAssemblyFeeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		def PricingAdjustment adj=Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.REPRICE_SHOPPING_CART_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >> "refId"
		ltl.getQuantity() >> 2.0
		
		cTools.getAssemblyCharge(customCalc.extractSiteID(), cItem.getCatalogRefId()) >>{throw new BBBBusinessException("")}
		
		def DetailedItemPriceInfo detail=Mock()
		def DetailedItemPriceInfo detail1=Mock()
		
		List <DetailedItemPriceInfo> l = new ArrayList<DetailedItemPriceInfo>()
		
		List <DetailedItemPriceInfo> newList = new ArrayList<DetailedItemPriceInfo>()
		newList.add(detail1)
		
		pPriceQuote.getAdjustments().add(adj)
		
		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("assembly item is present");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("rounding item price.")
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getListPrice() == 0
		pPriceQuote.getAmount() == 0
		pPriceQuote.getCurrentPrice()==0
		l.contains(detail1)==true
		pPriceQuote.getAdjustments().contains(adj) ==false
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"priceItem,tests if BBBSystemException is thrown when commerce item is an instance of LTL assembely Item"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def LTLAssemblyFeeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		def PricingAdjustment adj=Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		pExtraParameters.put(BBBCheckoutConstants.REPRICE_SHOPPING_CART_ORDER,order)
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >> "refId"
		ltl.getQuantity() >> 2.0
		
		cTools.getAssemblyCharge(customCalc.extractSiteID(), cItem.getCatalogRefId()) >>{throw new BBBSystemException("")}
		
		def DetailedItemPriceInfo detail=Mock()
		def DetailedItemPriceInfo detail1=Mock()
		
		List <DetailedItemPriceInfo> l = new ArrayList<DetailedItemPriceInfo>()
		
		List <DetailedItemPriceInfo> newList = new ArrayList<DetailedItemPriceInfo>()
		newList.add(detail1)
		
		pPriceQuote.getAdjustments().add(adj)
		
		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("assembly item is present");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("rounding item price.")
		pPriceQuote.getRawTotalPrice() == 0
		pPriceQuote.getListPrice() == 0
		pPriceQuote.getAmount() == 0
		l.contains(detail1)==true
		pPriceQuote.getAdjustments().contains(adj) ==false
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"when pItem is instanceof LTLAssemblyFeeCommerceItem , and detail list is empty"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLAssemblyFeeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		def PricingAdjustment adj =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		cart.getCurrent() >>order
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >> "refId"
		ltl.getQuantity() >> 2.0
		
		cTools.getAssemblyCharge(customCalc.extractSiteID(), cItem.getCatalogRefId()) >> 20.0
		pTools.round(ltl.getQuantity()*cTools.getAssemblyCharge(customCalc.extractSiteID(),cItem.getCatalogRefId()))>> 40.0
		
		def DetailedItemPriceInfo detail1=Mock()
		List <DetailedItemPriceInfo> l = new ArrayList<DetailedItemPriceInfo>()
		List <DetailedItemPriceInfo> newList = new ArrayList<DetailedItemPriceInfo>()
		newList.add(detail1)
		
		pPriceQuote.getAdjustments().add(adj)
		

		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		pPriceQuote.setSalePrice(10.0)
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("assembly item is present");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("rounding item price.")
		pPriceQuote.getRawTotalPrice() == 40.0
		pPriceQuote.getListPrice() == 20.0
		pPriceQuote.getAmount() == 40.0
		l.contains(detail1)==true
		pPriceQuote.getAdjustments().contains(adj)==false
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"when pItem is instanceof LTLAssemblyFeeCommerceItem , and adjustment list is empty"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLAssemblyFeeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>cItem
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		cart.getCurrent() >>order
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >> "refId"
		ltl.getQuantity() >> 2.0
		
		cTools.getAssemblyCharge(customCalc.extractSiteID(), cItem.getCatalogRefId()) >> 20.0
		pTools.round(ltl.getQuantity()*cTools.getAssemblyCharge(customCalc.extractSiteID(),cItem.getCatalogRefId()))>> 40.0
		
		def DetailedItemPriceInfo detail1=Mock()
		def DetailedItemPriceInfo detail=Mock()
		
		List <DetailedItemPriceInfo> l = new ArrayList<DetailedItemPriceInfo>()
		l.add(detail)
		List <DetailedItemPriceInfo> newList = new ArrayList<DetailedItemPriceInfo>()
		newList.add(detail1)
		
		pPriceQuote.setCurrentPriceDetails(l)
		pTools.getDetailedItemPriceTools() >>priceTool
		priceTool.createInitialDetailedItemPriceInfos(_, pPriceQuote, ltl, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION) >> newList
		
		pPriceQuote.setSalePrice(10.0)
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("assembly item is present");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + cItem);
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		1*customCalc.logDebug("rounding item price.")
		pPriceQuote.getRawTotalPrice() == 40.0
		pPriceQuote.getListPrice() == 20.0
		pPriceQuote.getAmount() == 40.0
		pPriceQuote.getCurrentPrice()==10.0
		l.contains(detail)==false
		l.contains(detail1)==true
		pPriceQuote.getAdjustments().isEmpty()==false
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}
	
	def"tests if CommerceItemNotFoundException is thrown "(){

		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLAssemblyFeeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>{throw new CommerceItemNotFoundException("")}
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		cart.getCurrent() >>order
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >> "refId"
		ltl.getQuantity() >> 2.0
		
		cTools.getAssemblyCharge(customCalc.extractSiteID(), cItem.getCatalogRefId()) >> 20.0
		pTools.round(ltl.getQuantity()*cTools.getAssemblyCharge(customCalc.extractSiteID(),cItem.getCatalogRefId()))>> 40.0
		
		pTools.getDetailedItemPriceTools() >>priceTool
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("assembly item is present");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + null);
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		0*customCalc.logDebug("rounding item price.")
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getListPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		pPriceQuote.getCurrentPrice()==0.0
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem" ))
	}
	
	def"tests if InvalidParameterException is thrown "(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def LTLAssemblyFeeCommerceItem ltl =Mock()
		def OrderHolder cart =Mock()
		def CommerceItem cItem =Mock()
		def BBBHardGoodShippingGroup hg = Mock()
		def DetailedItemPriceTools priceTool =Mock()
		
		ltl.getLtlCommerceItemRelation() >>"relation"
		
		def BBBOrderImpl order = Mock()
		order.getId()>>"Id"
		order.getCommerceItem("relation") >>{throw new InvalidParameterException("")}
		
		Map<String, Object> pExtraParameters = new HashMap<>()
		
		customCalc.setLoggingDebug(true)
		requestMock.resolveName("/atg/commerce/ShoppingCart") >>cart
		cart.getCurrent() >>order
			
		customCalc.extractSiteID()>>"tbs"
		requestMock.getLocale() >> local
		cItem.getCatalogRefId() >> "refId"
		ltl.getQuantity() >> 2.0
		
		cTools.getAssemblyCharge(customCalc.extractSiteID(), cItem.getCatalogRefId()) >> 20.0
		pTools.round(ltl.getQuantity()*cTools.getAssemblyCharge(customCalc.extractSiteID(),cItem.getCatalogRefId()))>> 40.0
		
		pTools.getDetailedItemPriceTools() >>priceTool
		
		when:
		customCalc.priceItem(pPriceQuote, ltl, pPricingModel, pLocale, pProfile,pExtraParameters)
		
		then:
		1* customCalc.logDebug(LogMessageFormatter.formatMessage(null,"Start - CustomPriceListCalculator.priceItem"))
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: Start");
		1*customCalc.logDebug("assembly item is present");
		1*customCalc.logDebug("ltlCommerceItemForDeliveryItem: " + ltl.getLtlCommerceItemRelation());
		1*customCalc.logDebug("Order id: " + order.getId());
		1*customCalc.logDebug("ltlCommerceItem: " + null);
		1*customCalc.logDebug("CustomPriceListCalculator.getCommerceItemForCustomLTLCommerceItem:: End");
		0*customCalc.logDebug("rounding item price.")
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getListPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		pPriceQuote.getCurrentPrice()==0.0
		1*customCalc.logDebug(LogMessageFormatter.formatMessage(null,"End- CustomPriceListCalculator.priceItem"))
	}

}
