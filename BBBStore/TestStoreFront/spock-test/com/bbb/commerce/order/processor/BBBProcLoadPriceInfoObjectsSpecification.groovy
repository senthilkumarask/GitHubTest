/**
 * 
 */
package com.bbb.commerce.order.processor

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.order.OrderTools
import atg.commerce.pricing.TaxPriceInfo
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryItemDescriptor

import com.bbb.commerce.order.BBBOrderManager
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.repository.RepositoryItemMock


class BBBProcLoadPriceInfoObjectsSpecification extends BBBExtendedSpec {
	
	def BBBProcLoadPriceInfoObjects loadPriceInfo 
	def setup()	{
		loadPriceInfo= new BBBProcLoadPriceInfoObjects(shippingItemsTaxPriceInfosPropertyName:"shippingItemsTaxPriceInfos");
	}
	def "copying values from shipping repository item to Tax princeinfo Object"(){
	given:
	TaxPriceInfo tPrifceInfo = new TaxPriceInfo()
	RepositoryItemMock itemMock = new RepositoryItemMock()
	Map dataMap = ["amount":2.doubleValue(),"amountIsFinal":false,"cityTax":5.doubleValue(),"countryTax":3.doubleValue(),"countyTax":3.doubleValue(),"currencyCode":"USD","discounted":false,
		"finalReasonCode":"RCode","shippingItemsTaxPriceInfos":null,"stateTax":2.3.doubleValue()]
	itemMock.setProperties(dataMap)
	when:
	tPrifceInfo = loadPriceInfo.readTaxPriceInfoProperties(tPrifceInfo,itemMock)
	then:
		tPrifceInfo.getAmount() == 2.0
		tPrifceInfo.isAmountIsFinal() == false
		tPrifceInfo.getCityTax() == 5.0
		tPrifceInfo.getCountryTax() == 3.0
		tPrifceInfo.getCountyTax() == 3.0
		tPrifceInfo.getCurrencyCode().equals("USD") 
		tPrifceInfo.getFinalReasonCode().equalsIgnoreCase("RCode")
		tPrifceInfo.getStateTax() == 2.3
		
	}
	def "copying values from shipping repository item to Tax princeinfo Object with district tax"(){
		given:
		TaxPriceInfo tPrifceInfo = new TaxPriceInfo()
		RepositoryItemMock itemMock = new RepositoryItemMock()
		Map dataMap = ["amount":2.doubleValue(),"amountIsFinal":false,"cityTax":5.doubleValue(),"countryTax":3.doubleValue(),"countyTax":3.doubleValue(),"currencyCode":"USD","discounted":false,
			"finalReasonCode":"RCode","shippingItemsTaxPriceInfos":null,"stateTax":2.3.doubleValue(),"districtTax":2.1.doubleValue()]
		itemMock.setProperties(dataMap)
		when:
		tPrifceInfo = loadPriceInfo.readTaxPriceInfoProperties(tPrifceInfo,itemMock)
		then:
			tPrifceInfo.getAmount() == 2.0
			tPrifceInfo.isAmountIsFinal() == false
			tPrifceInfo.getCityTax() == 5.0
			tPrifceInfo.getCountryTax() == 3.0
			tPrifceInfo.getCountyTax() == 3.0
			tPrifceInfo.getCurrencyCode().equals("USD")
			tPrifceInfo.getFinalReasonCode().equalsIgnoreCase("RCode")
			tPrifceInfo.getStateTax() == 2.3
			tPrifceInfo.getDistrictTax() == 2.1
			
		}
	def "ProcLoadPriceInfoObjects is performing tax for shippping price info as null"(){
		given:
		def BBBOrderManager orderMaanager = Mock()
		def BBBOrder order = Mock()
		def MutableRepositoryItem mItem = Mock()
		//mItem.getPropertyValue("")
		expect:
		loadPriceInfo.loadTaxPriceInfo(order,mItem,orderMaanager,false)
	}
	def "ProcLoadPriceInfoObjects is performing tax for shippping price info "(){
		given:
		loadPriceInfo = Spy()
		String[] loadProperties= new String[0]
		def BBBOrderManager orderMaanager = Mock()
		def BBBOrder order = Mock()
		def MutableRepositoryItem mItem = Mock()
		def MutableRepositoryItem mTaxPriceItem = Mock()
		mItem.getPropertyValue("taxPriceInfo") >> mTaxPriceItem
		RepositoryItemMock itemShipTaxPriceInfoRepItem = new RepositoryItemMock()
		RepositoryItemMock shipTaxPriceInfoRepItem = new RepositoryItemMock()
		Map itemDataMap = ["amount":2.doubleValue(),"amountIsFinal":false,"cityTax":5.doubleValue(),"countryTax":3.doubleValue(),"countyTax":3.doubleValue(),"currencyCode":"USD","discounted":false,
			"finalReasonCode":"RCode","shippingItemsTaxPriceInfos":null,"stateTax":2.3.doubleValue(),"districtTax":2.1.doubleValue()]
		itemShipTaxPriceInfoRepItem.setProperties(itemDataMap)
		mTaxPriceItem.getPropertyValue("shippingItemsTaxPriceInfos") >> ["SG12345":shipTaxPriceInfoRepItem]
		shipTaxPriceInfoRepItem.setProperties(["shippingItemsTaxPriceInfos":["Ci12345":itemShipTaxPriceInfoRepItem]])
		TaxPriceInfo orderTaxPriceInfo = new TaxPriceInfo()
		TaxPriceInfo shipTaxPriceInfo = new TaxPriceInfo()
		orderTaxPriceInfo.setShippingItemsTaxPriceInfos(["SG12345":shipTaxPriceInfo])
		order.getTaxPriceInfo() >> orderTaxPriceInfo
		loadPriceInfo.setLoadProperties(loadProperties)
		loadPriceInfo.setShippingItemsTaxPriceInfosPropertyName("shippingItemsTaxPriceInfos")
		loadPriceInfo.superLoadTaxPriceInfo(order,mItem,orderMaanager,false) >>  {}
		when:
		loadPriceInfo.loadTaxPriceInfo(order,mItem,orderMaanager,false)
		then:
		shipTaxPriceInfo.getShippingItemsTaxPriceInfos().containsKey("Ci12345")
	}
	def "ProcLoadPriceInfoObjects is performing tax for shippping price info  map has null"(){
		given:
		loadPriceInfo = Spy()
		String[] loadProperties= new String[0]
		def BBBOrderManager orderMaanager = Mock()
		def BBBOrder order = Mock()
		def MutableRepositoryItem mItem = Mock()
		def MutableRepositoryItem mTaxPriceItem = Mock()
		mItem.getPropertyValue("taxPriceInfo") >> mTaxPriceItem
		RepositoryItemMock itemShipTaxPriceInfoRepItem = new RepositoryItemMock()
		RepositoryItemMock shipTaxPriceInfoRepItem = new RepositoryItemMock()
		mTaxPriceItem.getPropertyValue("shippingItemsTaxPriceInfos") >> null
		TaxPriceInfo shipTaxPriceInfo = new TaxPriceInfo()
		loadPriceInfo.setLoadProperties(loadProperties)
		loadPriceInfo.setShippingItemsTaxPriceInfosPropertyName("shippingItemsTaxPriceInfos")
		loadPriceInfo.superLoadTaxPriceInfo(order,mItem,orderMaanager,false) >>  {}
		when:
		loadPriceInfo.loadTaxPriceInfo(order,mItem,orderMaanager,false)
		then:
		shipTaxPriceInfo.getShippingItemsTaxPriceInfos().containsKey("Ci12345") == false
	}
}
