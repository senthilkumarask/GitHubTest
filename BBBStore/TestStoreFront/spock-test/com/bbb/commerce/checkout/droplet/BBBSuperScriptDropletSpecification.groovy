package com.bbb.commerce.checkout.droplet

import atg.commerce.order.OrderImpl

import java.util.HashMap;
import java.util.Map;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.commerce.pricing.bean.PriceInfoVO
import com.bbb.constants.BBBCoreConstants;

import spock.lang.specification.BBBExtendedSpec;

class BBBSuperScriptDropletSpecification extends BBBExtendedSpec {
    def BBBSuperScriptDroplet ssDroplet
	def OrderImpl orderMock = Mock()
	def BBBPricingTools pricingToolsMock = Mock()
	def PriceInfoVO priceInfoVo = new PriceInfoVO()
	def LblTxtTemplateManager messageHelperMock = Mock()
	Locale local = Locale.forLanguageTag("en_US")
	
	def setup(){
		ssDroplet = new BBBSuperScriptDroplet(pricingTools : pricingToolsMock, messageHandler : messageHelperMock)
    }
	
	def "service . TC not to add SuperScriptMap in request object when priceInfoVO is null " (){
		given:
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> null
		when:
		ssDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("SuperScriptMap", _)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service . TC to add placeHolderMap to request object when OnlineEcoFeeTotal is greater then 0 "(){
		given:
		requestMock.getLocale() >> local
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVo
		//OnlineEcoFeeTotal
		priceInfoVo.setOnlineEcoFeeTotal(1) 
		messageHelperMock.getPageLabel("lbl_footnote_ecofee", _, null) >> "lable"
		
		when:
		ssDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("SuperScriptMap", ['ecofeeFootNoteCount':'1'])
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def"service . TC to add placeHolderMap to request object when GiftWrapTotal is greater then 0 "(){
		given:
		requestMock.getLocale() >> local
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVo
		//GiftWrapTotal
		priceInfoVo.setGiftWrapTotal(3)
		messageHelperMock.getPageLabel("lbl_footnote_giftWrap", _, null) >> "lable"
		
		when:
		ssDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("SuperScriptMap", ['giftWrapFootNoteCount':'1'])
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def"service . TC to add placeHolderMap to request object on FreeShipping  "(){
		given:
		requestMock.getLocale() >> local
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVo
		//FreeShipping
		priceInfoVo.setFreeShipping(true)
		messageHelperMock.getPageLabel("lbl_footnote_shipping", _, null) >> "lable"
		
		when:
		ssDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("SuperScriptMap", ['shippingFootNoteCount':'1'])
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def"service . TC to add placeHolderMap to request object on FreeShipping , GiftWrapTotal is greater then 0 ,OnlineEcoFeeTotal is greater then 0  "(){
		given:
		requestMock.getLocale() >> local
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVo
		//FreeShipping
		priceInfoVo.setFreeShipping(true)
		priceInfoVo.setGiftWrapTotal(3)
		priceInfoVo.setOnlineEcoFeeTotal(1)
		messageHelperMock.getPageLabel("lbl_footnote_shipping", _, null) >> "lable"
		messageHelperMock.getPageLabel("lbl_footnote_giftWrap", _, null) >> "lable"
		messageHelperMock.getPageLabel("lbl_footnote_ecofee", _, null) >> "lable"
		
		
		when:
		ssDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("SuperScriptMap", ['shippingFootNoteCount':'3','giftWrapFootNoteCount':'2','ecofeeFootNoteCount':'1'])
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
//////////////////////////////////////test case when pageLabel is null /////////////////////////  	

	def"service . TC to add empty placeHolderMap to request object when OnlineEcoFeeTotal is greater then 0 and pageLable is null "(){
		given:
		requestMock.getLocale() >> local
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVo
		//OnlineEcoFeeTotal
		priceInfoVo.setOnlineEcoFeeTotal(1)
		messageHelperMock.getPageLabel("lbl_footnote_ecofee", _, null) >> null
		
		when:
		ssDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("SuperScriptMap", [:])
		
	}
	
	def"service . TC to add empty placeHolderMap to request object when GiftWrapTotal is greater then 0 and pageLable is null "(){
		given:
		requestMock.getLocale() >> local
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVo
		//GiftWrapTotal
		priceInfoVo.setGiftWrapTotal(3)
		messageHelperMock.getPageLabel("lbl_footnote_giftWrap", _, null) >> null
		
		when:
		ssDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("SuperScriptMap", [:])
		
	}
	
	def"service . TC to add empty placeHolderMap to request object on free shipping and pageLable is null  "(){
		given:
		requestMock.getLocale() >> local
		requestMock.getObjectParameter("order") >> orderMock
		pricingToolsMock.getOrderPriceInfo(orderMock) >> priceInfoVo
		//FreeShipping
		priceInfoVo.setFreeShipping(true)
		messageHelperMock.getPageLabel("lbl_footnote_shipping", _, null) >> null
		
		when:
		ssDroplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("SuperScriptMap", [:])
		
	}
	
}
