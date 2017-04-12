package com.bbb.commerce.pricing.calculator

import java.util.Locale;
import java.util.Map
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship
import com.bbb.commerce.order.droplet.CommerceItemCheckDroplet;
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.order.bean.BBBShippingPriceInfo
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.repository.RepositoryItemMock
import com.sun.xml.rpc.processor.modeler.j2ee.xml.pathType;
import com.bbb.commerce.catalog.BBBCatalogTools;
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.PricingTools
import atg.commerce.pricing.ShippingPriceInfo
import atg.commerce.pricing.priceLists.PriceListException
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import spock.lang.specification.BBBExtendedSpec;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

class SubTotalRangeCalculatorSpecification extends BBBExtendedSpec {


	def SubTotalRangeCalculator rangeCalculator
	def Order pOrder = Mock()
	def ShippingPriceInfo pPriceQoute =Mock()
	def ShippingGroup pShippingGroup = Mock()
	def RepositoryItem pPricingModel = Mock()
	def RepositoryItem pProfile = Mock()
	def BBBCommerceItem bbbCommerceItem = Mock()
	def BBBCatalogTools catalogTools =  Mock()
	def HardgoodShippingGroup hardGoodMock = Mock()
	def BBBPricingTools pTools = Mock()
	BBBShippingGroupCommerceItemRelationship rel  = new BBBShippingGroupCommerceItemRelationship()



	Locale locale
	Map pMap



	def setup(){
		rangeCalculator = new SubTotalRangeCalculator()
		rangeCalculator.setPricingTools(pTools)
		rangeCalculator.setCatalogUtil(catalogTools)
	}

	def"priceShippingGroup, Tests if CommerceItem is an Instance of BBBCommerceItem"(){

		given:
		pShippingGroup.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)

		when:
		rangeCalculator.priceShippingGroup(pOrder,pPriceQoute, pShippingGroup, pPricingModel, locale, pProfile, pMap)

		then:
		1*bbbCommerceItem.isLtlItem()
	}

	def"priceShippingGroup, Tests if CommerceItem is not an Instance of BBBCommerceItem"(){

		given:
		def EcoFeeCommerceItem ecoFeeItems =Mock()
		pShippingGroup.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(ecoFeeItems)

		when:
		rangeCalculator.priceShippingGroup(pOrder,pPriceQoute, pShippingGroup, pPricingModel, locale, pProfile, pMap)

		then:
		0*bbbCommerceItem.isLtlItem()
	}

	def"priceShippingGroup, Tests if Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , and Shipping method is not null"(){

		given:
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> true
		hardGoodMock.getShippingMethod() >> "store"

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		1*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)
		1*pTools.calculateSurcharge(pOrder.getSiteId(), hardGoodMock)
		//  1*((BBBShippingPriceInfo)pQoute).setRawShipping(_)
		//1*pQoute.setSurcharge(20.0)
		//1*pQoute.setFinalSurcharge(pTools.round())

	}



	def"priceShippingGroup, Tests if Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , and Shipping method is null"(){

		given:
		ShipMethodVO shipVo =new ShipMethodVO()
		shipVo.setShipMethodId("express")
		catalogTools.getDefaultShippingMethod(pOrder.getSiteId()) >> shipVo
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> true
		hardGoodMock.getShippingMethod() >> null >>shipVo.getShipMethodId()


		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		//1*pTools.calculateShippingCost(_, hardGoodMock.getShippingMethod(), hardGoodMock)
		1*hardGoodMock.setShippingMethod(shipVo.getShipMethodId());
		//	1*pTools.calculateSurcharge(_, hardGoodMock)

	}

	def"priceShippingGroup, Tests if Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , and Shipping method is empty"(){

		given:
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		def HardgoodShippingGroup hardGoodMock = Mock()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> true
		hardGoodMock.getShippingMethod() >> ""

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		0*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)
		0*pTools.calculateSurcharge(pOrder.getSiteId(), hardGoodMock)
		//pQoute.getRawShipping() == 0
		//pQoute.getSurcharge() == 0

	}

	//def"Tests if Shipping group is an instance of HardgoodShippingGroup, item is not an LTLItem , and Shipping method is not null"(){
	//
	//	given:
	//	BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
	//	def HardgoodShippingGroup hardGoodMock = Mock()
	//	hardGoodMock.getCommerceItemRelationships() >> [rel]
	//	rel.setCommerceItem(bbbCommerceItem)
	//	bbbCommerceItem.isLtlItem() >> false
	//	hardGoodMock.getShippingMethod() >> "express"
	//
	//	when:
	//	rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)
	//
	//	then:
	//	1*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)
	//	1*pTools.calculateSurcharge(pOrder.getSiteId(), hardGoodMock)
	//
	//}

	def"priceShippingGroup, Tests if Shipping group is an instance of HardgoodShippingGroup, item is not an LTLItem , and Shipping method is empty"(){

		given:
		ShipMethodVO shipVo =new ShipMethodVO()
		shipVo.setShipMethodId("123")
		def HardgoodShippingGroup hardGoodMock = Mock()
		catalogTools.getDefaultShippingMethod(pOrder.getSiteId()) >> shipVo
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> false
		hardGoodMock.getShippingMethod() >> "" >> "" >> "">>shipVo.getShipMethodId()

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		1*hardGoodMock.setShippingMethod(shipVo.getShipMethodId())
		//1*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)

	}

	def"priceShippingGroup, Tests if Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , Shipping method is not empty and Shipping method is not equal to hardgoodShippingGroup "(){

		given:
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> true
		hardGoodMock.getShippingMethod() >> "store"

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		1*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)
		1*pTools.calculateSurcharge(pOrder.getSiteId(), hardGoodMock)
		//1*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)

	}


	def"priceShippingGroup, Tests if PricingException is thrown when Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , Shipping method is not empty and Shipping method is not equal to hardgoodShippingGroup "(){

		given:
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> true
		hardGoodMock.getShippingMethod() >> "store"
		pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock) >> {throw new PricingException("exception")}

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		0*pTools.calculateSurcharge(pOrder.getSiteId(), hardGoodMock)
		//1*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)

	}

	def"priceShippingGroup, Tests if Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , Shipping method is not empty and Shipping method is equal to hardgoodShippingGroup "(){

		given:
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		ShipMethodVO shipVo =new ShipMethodVO()
		shipVo.setShipMethodId("123")
		catalogTools.getDefaultShippingMethod(pOrder.getSiteId()) >> shipVo
		hardGoodMock.getShippingMethod() >> "hardgoodShippingGroup" >>"hardgoodShippingGroup">>"hardgoodShippingGroup">>"hardgoodShippingGroup">>shipVo.getShipMethodId()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> true

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		1*hardGoodMock.setShippingMethod(shipVo.getShipMethodId())

	}



	def"priceShippingGroup, Tests if BBBBusinessException is thrown when Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , and Shipping method is null"(){

		given:
		ShipMethodVO shipVo =new ShipMethodVO()
		shipVo.setShipMethodId("123")
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		def HardgoodShippingGroup hardGoodMock = Mock()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> false
		hardGoodMock.getShippingMethod() >> null
		catalogTools.getDefaultShippingMethod(pOrder.getSiteId()) >> {throw new BBBBusinessException("exception")}

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		0*hardGoodMock.setShippingMethod(shipVo.getShipMethodId())
		0*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)
		0*pTools.calculateSurcharge(pOrder.getSiteId(), hardGoodMock)

	}



	def"priceShippingGroup, Tests if BBBSystemException is thrown when Shipping group is an instance of HardgoodShippingGroup, item is an LTLItem , and Shipping method is null"(){

		given:
		ShipMethodVO shipVo =new ShipMethodVO()
		shipVo.setShipMethodId("123")
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		def HardgoodShippingGroup hardGoodMock = Mock()
		hardGoodMock.getCommerceItemRelationships() >> [rel]
		rel.setCommerceItem(bbbCommerceItem)
		bbbCommerceItem.isLtlItem() >> false
		hardGoodMock.getShippingMethod() >> null
		catalogTools.getDefaultShippingMethod(pOrder.getSiteId()) >> {throw new BBBSystemException("exception")}

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		0*hardGoodMock.setShippingMethod(shipVo.getShipMethodId())
		0*pTools.calculateShippingCost(pOrder.getSiteId(), hardGoodMock.getShippingMethod(), hardGoodMock)
		0*pTools.calculateSurcharge(pOrder.getSiteId(), hardGoodMock)

	}

	def"priceShippingGroup, Tests if CommerceItem is an Instance of ecoFeeItems"(){

		given:
		def ItemPriceInfo info =Mock()
		def EcoFeeCommerceItem ecoFeeItems =Mock()
		def HardgoodShippingGroup hardGoodMock = Mock()
		def CommerceItemRelationship crelation = Mock()
		ecoFeeItems.getPriceInfo() >>info
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		hardGoodMock.getCommerceItemRelationships() >> [rel]>> [crelation]
		rel.setCommerceItem(bbbCommerceItem)
		crelation.getCommerceItem() >> ecoFeeItems
		bbbCommerceItem.isLtlItem() >> true
		hardGoodMock.getShippingMethod() >> "store"

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		1*ecoFeeItems.getPriceInfo().getAmount()

	}

	def"priceShippingGroup, Tests if CommerceItem is not an Instance of ecoFeeItems"(){

		given:
		def ItemPriceInfo info =Mock()
		def EcoFeeCommerceItem ecoFeeItems =Mock()
		def CommerceItemRelationship crelation = Mock()
		def HardgoodShippingGroup hardGoodMock = Mock()
		BBBShippingPriceInfo pQoute = new BBBShippingPriceInfo()
		hardGoodMock.getCommerceItemRelationships() >> [rel]>> [crelation]
		rel.setCommerceItem(bbbCommerceItem)
		crelation.getCommerceItem() >> bbbCommerceItem
		bbbCommerceItem.isLtlItem() >> true
		hardGoodMock.getShippingMethod() >> "store"

		when:
		rangeCalculator.priceShippingGroup(pOrder,pQoute, hardGoodMock, pPricingModel, locale, pProfile, pMap)

		then:
		0*ecoFeeItems.getPriceInfo().getAmount()

	}

	def"haveItemsToShip, tests when shipping group is an instance of BBBStoreShippingGroup"(){

		given:
		def BBBStoreShippingGroup shippingGroup =Mock()

		when:
		rangeCalculator.haveItemsToShip(shippingGroup)

		then:
		rangeCalculator.haveItemsToShip(shippingGroup) == false



	}

	def"haveItemsToShip, tests when shipping group is null"(){

		given:
		rangeCalculator =Spy(SubTotalRangeCalculator)

		when:
		rangeCalculator.haveItemsToShip(null)

		then:
		1*rangeCalculator.extractedSuperHaveItemsToShip(null)



	}

	def"haveItemsToShip, tests when shipping group is not an instance of BBBShippingGroup"(){

		given:
		rangeCalculator =Spy(SubTotalRangeCalculator)

		when:
		rangeCalculator.haveItemsToShip(pShippingGroup)

		then:
		1*rangeCalculator.extractedSuperHaveItemsToShip(pShippingGroup)



	}
}