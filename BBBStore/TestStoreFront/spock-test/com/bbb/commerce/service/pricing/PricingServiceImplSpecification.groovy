package com.bbb.commerce.service.pricing

import org.apache.xmlbeans.XmlValidationError

import spock.lang.specification.BBBExtendedSpec

import com.bedbathandbeyond.atg.Currency
import com.bedbathandbeyond.atg.MessageError
import com.bedbathandbeyond.atg.MessageHeader
import com.bedbathandbeyond.atg.PricingError
import com.bedbathandbeyond.atg.PricingRequest
import com.bedbathandbeyond.atg.PricingRequestDocument
import com.bedbathandbeyond.atg.PricingResponse
import com.bedbathandbeyond.atg.PricingResponseDocument

class PricingServiceImplSpecification extends BBBExtendedSpec {

	PricingServiceImpl impl
	Calendar date = Calendar.getInstance()
	com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
	com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)

	def setup(){
		impl = new PricingServiceImpl()
	}

	def"performPricing, responseDocument is returned"(){

		given:
		impl = Spy()
		com.bedbathandbeyond.atg.PricingRequestDocument pRequestDocument = Mock()
		BBBPricingWebService ws =Mock()
		PricingRequest pricingRequest =Mock()
		1*impl.extractNucleus() >> ws
		pRequestDocument.validate(_) >> false
		1*pRequestDocument.getPricingRequest() >> pricingRequest
		1*ws.priceOrder(pricingRequest) >> com.bedbathandbeyond.atg.PricingResponse.Factory.newInstance()

		when:
		PricingResponseDocument doc =impl.performPricing(pRequestDocument)

		then:
		doc != null
		doc.getPricingResponse() != null
		0*ws.logError("Webservice error while performing Pricing", _)
	}

	
	def"performPricing, when soap fault exception is not an instance of BBBSystemException"(){

		given:
		impl = Spy()
		com.bedbathandbeyond.atg.PricingRequestDocument pRequestDocument = Mock()
		BBBPricingWebService ws =Mock()
		PricingRequest pricingRequest =Mock()
		PricingResponse pricingResponse = Mock()
		MessageHeader requestHeader = Mock()

		1*impl.extractNucleus() >> ws
		ws.isLoggingError() >> true
		1*pRequestDocument.validate(_) >> true
		2*pRequestDocument.getPricingRequest() >> pricingRequest
		1*ws.priceOrder(pricingRequest) >> pricingResponse
		1*pricingRequest.getHeader() >> requestHeader

		1*requestHeader.getOrderId() >> "orderId"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> date
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"

		when:
		PricingResponseDocument doc =impl.performPricing(pRequestDocument)

		then:
		doc == null
		1*ws.logError("Webservice error while performing Pricing", _)
		com.bbb.commerce.service.pricing.SoapFault e = thrown()
		MessageError msgError =e.getFaultMessage().getPricingError().getError()
		msgError.getCode() == "Server.Sys"
		e.getFaultMessage().getPricingError().getError() !=null
		e.getFaultMessage().getPricingError().getError().getDescription() != null
	}

	def"performPricing, when soap fault exception is an instance of BBBSystemException"(){ // check with narinder

		given:
		impl = Spy()
		PricingRequestDocument pRequestDocument = Mock()
		BBBPricingWebService ws =Mock()
		PricingRequest pricingRequest =Mock()
		PricingResponse pricingResponse =Mock()
		MessageHeader requestHeader = Mock()

		1*impl.extractNucleus() >> ws
		ws.isLoggingError() >> true
		XmlValidationError valid = Mock()
		valid.getErrorCode() >> "errorCode"
		impl.createValidatioErrors() >> [valid]
		1*pRequestDocument.validate(_) >> false
		1*pRequestDocument.getPricingRequest() >> pricingRequest
		0*ws.priceOrder(pricingRequest) >> pricingResponse

		1*pricingRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		1*requestHeader.getSiteId() >> null
		1*requestHeader.getOrderDate() >> date
		1*requestHeader.getCurrencyCode() >> null
		1*requestHeader.getCallingAppCode() >> null

		when:
		PricingResponseDocument doc =impl.performPricing(pRequestDocument)

		then:
		doc == null
		1*ws.logError("Webservice error while performing Pricing", _)
		com.bbb.commerce.service.pricing.SoapFault e = thrown()
		MessageError msgError =e.getFaultMessage().getPricingError().getError()
		msgError.getCode() == "Server.Biz"
		e.getFaultMessage().getPricingError().getError() !=null
		e.getFaultMessage().getPricingError().getError().getDescription() != null
	}


}
