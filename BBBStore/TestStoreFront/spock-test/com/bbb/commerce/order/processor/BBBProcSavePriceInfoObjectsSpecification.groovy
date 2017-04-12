package com.bbb.commerce.order.processor

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.order.CommerceIdentifier
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.TaxPriceInfo
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem

import com.bbb.commerce.order.BBBOrderManager
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.repository.RepositoryItemMock

class BBBProcSavePriceInfoObjectsSpecification extends BBBExtendedSpec {
	
	def BBBProcSavePriceInfoObjects savePriceInfoObjects
	def setup(){
		savePriceInfoObjects = new BBBProcSavePriceInfoObjects () 
	}

	def "saving price info object with out tax price info"(){
		given:
		savePriceInfoObjects = Spy()
		def BBBOrder order = Mock()
		def CommerceIdentifier cItem = Mock()
		def MutableRepository mRep = Mock()
		def MutableRepositoryItem mItem = Mock()
		def BBBOrderManager orderManager = Mock()
		def TaxPriceInfo tPriceInfo = Mock()
		savePriceInfoObjects.superSavePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager) >> null 
		when:
		MutableRepositoryItem item = savePriceInfoObjects.savePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager)
		then:
		item == null
	}
	def "saving price info object with  tax price info"(){
		given:
		savePriceInfoObjects = Spy()
		savePriceInfoObjects.setShippingItemsTaxPriceInfosPropertyName("shippingItemsTaxPriceInfos")
		def BBBOrder order = Mock()
		def CommerceIdentifier cItem = Mock()
		def MutableRepository mRep = Mock()
		def MutableRepositoryItem mItem = Mock()
		def MutableRepositoryItem mTaxItem = Mock()
		def BBBOrderManager orderManager = Mock()
		def TaxPriceInfo tPriceInfo = Mock()
		TaxPriceInfo orderTaxPriceInfo = new TaxPriceInfo()
		order.getTaxPriceInfo() >> orderTaxPriceInfo
		TaxPriceInfo itemShipTaxPriceInfoRepItem = new TaxPriceInfo()
		def MutableRepositoryItem shipTaxPriceInfoRepItem = Mock()
		mItem.getPropertyValue("shippingItemsTaxPriceInfos") >> ["SG12345":shipTaxPriceInfoRepItem]
		 mRep.createItem("taxPriceInfo") >> mTaxItem
		itemShipTaxPriceInfoRepItem.setAmount(2.0)
		itemShipTaxPriceInfoRepItem.setAmountIsFinal(false)
		itemShipTaxPriceInfoRepItem.setCityTax(5.0)
		itemShipTaxPriceInfoRepItem.setCountryTax(2.0)
		itemShipTaxPriceInfoRepItem.setCountyTax(2.0)
		itemShipTaxPriceInfoRepItem.setCurrencyCode("USD")
		itemShipTaxPriceInfoRepItem.setDiscounted(false)
		itemShipTaxPriceInfoRepItem.setFinalReasonCode("Rcode")
		itemShipTaxPriceInfoRepItem.setDistrictTax(2.0)
		itemShipTaxPriceInfoRepItem.setStateTax(1.0)
		
		TaxPriceInfo shipTaxPriceInfo = new TaxPriceInfo()
		shipTaxPriceInfo.setShippingItemsTaxPriceInfos(["Ci12345":itemShipTaxPriceInfoRepItem])
		orderTaxPriceInfo.setShippingItemsTaxPriceInfos(["SG12345":shipTaxPriceInfo])
		savePriceInfoObjects.superSavePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager) >> mItem
		when:
		MutableRepositoryItem item = savePriceInfoObjects.savePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager)
		then:
		1*shipTaxPriceInfoRepItem.setPropertyValue("shippingItemsTaxPriceInfos", _)
	}
	def "saving price info object with out shipping  tax price info"(){
		given:
		savePriceInfoObjects = Spy()
		savePriceInfoObjects.setShippingItemsTaxPriceInfosPropertyName("shippingItemsTaxPriceInfos")
		def BBBOrder order = Mock()
		def CommerceIdentifier cItem = Mock()
		def MutableRepository mRep = Mock()
		def MutableRepositoryItem mItem = Mock()
		def MutableRepositoryItem mTaxItem = Mock()
		def BBBOrderManager orderManager = Mock()
		def TaxPriceInfo tPriceInfo = Mock()
		TaxPriceInfo orderTaxPriceInfo = new TaxPriceInfo()
		order.getTaxPriceInfo() >> orderTaxPriceInfo
		TaxPriceInfo itemShipTaxPriceInfoRepItem = new TaxPriceInfo()
		def MutableRepositoryItem shipTaxPriceInfoRepItem = Mock()
		mItem.getPropertyValue("shippingItemsTaxPriceInfos") >> null
		 mRep.createItem("taxPriceInfo") >> mTaxItem
		
		TaxPriceInfo shipTaxPriceInfo = new TaxPriceInfo()
		shipTaxPriceInfo.setShippingItemsTaxPriceInfos(["Ci12345":itemShipTaxPriceInfoRepItem])
		orderTaxPriceInfo.setShippingItemsTaxPriceInfos(["SG12345":shipTaxPriceInfo])
		savePriceInfoObjects.superSavePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager) >> mItem
		when:
		MutableRepositoryItem item = savePriceInfoObjects.savePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager)
		then:
		0*shipTaxPriceInfoRepItem.setPropertyValue("shippingItemsTaxPriceInfos", _)
	}
	def "saving price info object with  shipping  tax price info and with commmerce item in map"(){
		given:
		savePriceInfoObjects = Spy()
		savePriceInfoObjects.setShippingItemsTaxPriceInfosPropertyName("shippingItemsTaxPriceInfos")
		def BBBOrder order = Mock()
		def CommerceIdentifier cItem = Mock()
		def MutableRepository mRep = Mock()
		def MutableRepositoryItem mItem = Mock()
		def MutableRepositoryItem mTaxItem = Mock()
		def BBBOrderManager orderManager = Mock()
		def TaxPriceInfo tPriceInfo = Mock()
		TaxPriceInfo orderTaxPriceInfo = new TaxPriceInfo()
		order.getTaxPriceInfo() >> orderTaxPriceInfo
		TaxPriceInfo itemShipTaxPriceInfoRepItem = new TaxPriceInfo()
		def MutableRepositoryItem shipTaxPriceInfoRepItem = Mock()
		mItem.getPropertyValue("shippingItemsTaxPriceInfos") >> ["SG12345":null]
		 mRep.createItem("taxPriceInfo") >> mTaxItem
		
		TaxPriceInfo shipTaxPriceInfo = new TaxPriceInfo()
		shipTaxPriceInfo.setShippingItemsTaxPriceInfos(["Ci12345":itemShipTaxPriceInfoRepItem])
		orderTaxPriceInfo.setShippingItemsTaxPriceInfos(["SG12345":shipTaxPriceInfo])
		savePriceInfoObjects.superSavePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager) >> mItem
		when:
		MutableRepositoryItem item = savePriceInfoObjects.savePriceInfo(order, cItem, mItem, "order", tPriceInfo, mRep, orderManager)
		then:
		0*shipTaxPriceInfoRepItem.setPropertyValue("shippingItemsTaxPriceInfos", _)
	}
	def "saving item detailed item price infos"(){
		given:
		savePriceInfoObjects = Spy()
		def BBBOrder order = Mock()
		def MutableRepository mRep = Mock()
		def MutableRepositoryItem mItem = Mock()
		def BBBOrderManager orderManager = Mock()
		def TaxPriceInfo tPriceInfo = Mock()
		savePriceInfoObjects.superSaveDetailedItemPriceInfos(order, tPriceInfo, mItem, mRep, orderManager) >> {}
		DetailedItemPriceInfo itemPriceInfo = new  DetailedItemPriceInfo()
		savePriceInfoObjects.getPropertyValue(tPriceInfo, _) >> [itemPriceInfo]
		savePriceInfoObjects.getPropertyValue(order,itemPriceInfo, _) >> [mItem]
		when:
		savePriceInfoObjects.saveDetailedItemPriceInfos(order, tPriceInfo, mItem, mRep, orderManager)
		then:
		1*savePriceInfoObjects.writeItemToRepository(order, mRep, mItem)
	}
}
