package com.bbb.internationalshipping.integration.checkoutrequest

import java.util.List
import javax.xml.bind.JAXBException

import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.DomesticShippingOptionV2
import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.PriceBookType;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBBasketItemsVO
import com.bbb.internationalshipping.vo.checkoutrequest.BBBBasketTotalVO
import com.bbb.internationalshipping.vo.checkoutrequest.BBBDisplayVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBDomesticBasketVO
import com.bbb.internationalshipping.vo.checkoutrequest.BBBDomesticShippingMethodVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBInternationalCheckoutRequestVO
import com.bbb.internationalshipping.vo.checkoutrequest.BBBOrderPropertiesVO;
import com.bbb.internationalshipping.vo.checkoutrequest.BBBPricingVO
import com.bbb.internationalshipping.vo.checkoutrequest.BBBSessionDetailsVO
import com.itextpdf.text.xml.XMLUtil;

import spock.lang.specification.BBBExtendedSpec;

/**
 * @author Velmurugan Moorthy
 *
 * The class is to test the marshaller (BBBInternationalCheckoutMarshaller) class 
 * Input - International Checkout requestVo and creates a XML request for the Https POST International Checkout API call. 
 *
 */

class BBBInternationalCheckoutMarshallerSpecification extends BBBExtendedSpec {

	BBBInternationalCheckoutMarshaller internationalCheckoutMarshallerMock
	
	def setup () {
		
		internationalCheckoutMarshallerMock = Spy()
		//internationalCheckoutMarshallerMock = Mock()
	}
	
	def "prepareInternationalOrderRequest - international order request is created successfully - happy flow" () {
		
		
		given : 
		
		def buyerCurrency = "USD"
		
		BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
		BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
		BBBDomesticBasketVO domesticBasketVoMock = Mock()
		BBBBasketItemsVO bbbBasketItemVoMock = Mock()
		BBBDisplayVO bbbDisplayVOMock = Mock()
		BBBPricingVO pricingVoMock = Mock()
	 	BBBBasketTotalVO basketTotalVOMock = Mock()
		BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
		BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
		List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
		
		populateDisplayVO(bbbDisplayVOMock)
		populatePricingVoMock(pricingVoMock)
		populateBasketTotalVO(basketTotalVOMock)
		populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
		popualteOrderPropertiesVoMock(orderPropertiesVOMock)
		
		populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
		
		bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
		
		populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
		
		domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
		domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
		
		populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
		
		when :

		def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, "false")
		
		then : 
		
		requestXml.contains(bbbDisplayVOMock.getColor())
		requestXml.contains(pricingVoMock.getItemDiscount().toString())
		requestXml.contains(basketTotalVOMock.getOrderDiscount().toString())
		requestXml.contains(domesticShippingMethodVoMock.getDeliveryPromiseMaximum().toString())
		requestXml.contains(orderPropertiesVOMock.getCurrencyQuoteId().toString())
		
	}

	def "prepareInternationalOrderRequest - international order (Mexican order with duties) request is created successfully" () {
		
		
		given :
		
		def buyerCurrency = "MXN"
		def mexicanOrderSwitch = BBBInternationalShippingConstants.DUTIES
		BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
		BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
		BBBDomesticBasketVO domesticBasketVoMock = Mock()
		BBBBasketItemsVO bbbBasketItemVoMock = Mock()
		BBBDisplayVO bbbDisplayVOMock = Mock()
		BBBPricingVO pricingVoMock = Mock()
		 BBBBasketTotalVO basketTotalVOMock = Mock()
		BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
		BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
		List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
		
		populateDisplayVO(bbbDisplayVOMock)
		populatePricingVoMock(pricingVoMock)
		populateBasketTotalVO(basketTotalVOMock)
		populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
		popualteOrderPropertiesVoMock(orderPropertiesVOMock)
		
		populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
		
		bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
		
		populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
		
		domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
		domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
		
		populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
		
		when :

		def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, mexicanOrderSwitch)
		
		then :
		
		requestXml.contains(bbbDisplayVOMock.getColor())
		requestXml.contains(pricingVoMock.getItemDiscount().toString())
		requestXml.contains(basketTotalVOMock.getOrderDiscount().toString())
		requestXml.contains(domesticShippingMethodVoMock.getDeliveryPromiseMaximum().toString())
		requestXml.contains(orderPropertiesVOMock.getCurrencyQuoteId().toString())
		
	}

	def "prepareInternationalOrderRequest - international order (Mexican order with taxes) request is created successfully" () {
		
		
		given :
		
		def buyerCurrency = "MXN"
		def mexicanOrderSwitch = BBBInternationalShippingConstants.TAXES
		BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
		BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
		BBBDomesticBasketVO domesticBasketVoMock = Mock()
		BBBBasketItemsVO bbbBasketItemVoMock = Mock()
		BBBDisplayVO bbbDisplayVOMock = Mock()
		BBBPricingVO pricingVoMock = Mock()
		 BBBBasketTotalVO basketTotalVOMock = Mock()
		BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
		BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
		List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
		
		populateDisplayVO(bbbDisplayVOMock)
		populatePricingVoMock(pricingVoMock)
		populateBasketTotalVO(basketTotalVOMock)
		populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
		popualteOrderPropertiesVoMock(orderPropertiesVOMock)
		
		populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
		
		bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
		
		populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
		
		domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
		domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
		
		populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
		
		when :

		def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, mexicanOrderSwitch)
		
		then :
		requestXml.contains(bbbSessionDetailsVOMock.getBuyerCurrency()) 
		requestXml.contains(bbbDisplayVOMock.getColor())
		requestXml.contains(pricingVoMock.getItemDiscount().toString())
		requestXml.contains(basketTotalVOMock.getOrderDiscount().toString())
		requestXml.contains(domesticShippingMethodVoMock.getDeliveryPromiseMaximum().toString())
		requestXml.contains(orderPropertiesVOMock.getCurrencyQuoteId().toString())
		
	}

	def "prepareInternationalOrderRequest - international order (Mexican order with duties and taxes) request is created successfully" () {

				given :
		
		def buyerCurrency = "MXN"
		def mexicanOrderSwitch = BBBInternationalShippingConstants.DUTIES_AND_TAXES
		BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
		BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
		BBBDomesticBasketVO domesticBasketVoMock = Mock()
		BBBBasketItemsVO bbbBasketItemVoMock = Mock()
		BBBDisplayVO bbbDisplayVOMock = Mock()
		BBBPricingVO pricingVoMock = Mock()
		 BBBBasketTotalVO basketTotalVOMock = Mock()
		BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
		BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
		List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
		
		populateDisplayVO(bbbDisplayVOMock)
		populatePricingVoMock(pricingVoMock)
		populateBasketTotalVO(basketTotalVOMock)
		populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
		popualteOrderPropertiesVoMock(orderPropertiesVOMock)
		
		populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
		
		bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
		
		populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
		
		domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
		domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
		
		populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
		
		when :

		def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, mexicanOrderSwitch)
		
		then :
		
		requestXml.contains(bbbSessionDetailsVOMock.getBuyerCurrency())
		requestXml.contains(bbbDisplayVOMock.getColor())
		requestXml.contains(pricingVoMock.getItemDiscount().toString())
		requestXml.contains(basketTotalVOMock.getOrderDiscount().toString())
		requestXml.contains(domesticShippingMethodVoMock.getDeliveryPromiseMaximum().toString())
		requestXml.contains(orderPropertiesVOMock.getCurrencyQuoteId().toString())
		
	}

	def "prepareInternationalOrderRequest - international order (Mexican order with invalid order switch) request is created successfully" () {
		
				given :
				
				def buyerCurrency = "MXN"
				def mexicanOrderSwitch = ""
				BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
				BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
				BBBDomesticBasketVO domesticBasketVoMock = Mock()
				BBBBasketItemsVO bbbBasketItemVoMock = Mock()
				BBBDisplayVO bbbDisplayVOMock = Mock()
				BBBPricingVO pricingVoMock = Mock()
				 BBBBasketTotalVO basketTotalVOMock = Mock()
				BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
				BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
				List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
				
				populateDisplayVO(bbbDisplayVOMock)
				populatePricingVoMock(pricingVoMock)
				populateBasketTotalVO(basketTotalVOMock)
				populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
				popualteOrderPropertiesVoMock(orderPropertiesVOMock)
				
				populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
				
				bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
				
				populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
				
				domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
				domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
				
				populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
				
				when :
		
				def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, mexicanOrderSwitch)
				
				then :
				
				requestXml.contains(bbbSessionDetailsVOMock.getBuyerCurrency())
				requestXml.contains(bbbDisplayVOMock.getColor())
				requestXml.contains(pricingVoMock.getItemDiscount().toString())
				requestXml.contains(basketTotalVOMock.getOrderDiscount().toString())
				requestXml.contains(domesticShippingMethodVoMock.getDeliveryPromiseMaximum().toString())
				requestXml.contains(orderPropertiesVOMock.getCurrencyQuoteId().toString())
				
		}
		
		
	def "prepareInternationalOrderRequest - international order request creation failed - (JAXBException Exception)" () {
		
		given :
		
		def buyerCurrency = "USD"
		
		BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
		BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
		BBBDomesticBasketVO domesticBasketVoMock = Mock()
		BBBBasketItemsVO bbbBasketItemVoMock = Mock()
		BBBDisplayVO bbbDisplayVOMock = Mock()
		BBBPricingVO pricingVoMock = Mock()
		 BBBBasketTotalVO basketTotalVOMock = Mock()
		BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
		BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
		List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
		
		populateDisplayVO(bbbDisplayVOMock)
		populatePricingVoMock(pricingVoMock)
		populateBasketTotalVO(basketTotalVOMock)
		populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
		popualteOrderPropertiesVoMock(orderPropertiesVOMock)
		
		populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
		
		bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
		
		populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
		
		domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
		domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
		
		populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
		internationalCheckoutMarshallerMock.getMarshaller() >> {throw new JAXBException("BBBInternationalCheckoutMarshaller.prepareInternationalOrderRequest() | BBBBusinessException ")}
		
		when :

		def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, "false") 
		
		then :
		
		thrown BBBBusinessException
	}

	def "prepareInternationalOrderRequest - buyer currency is not set (empty)" () {
		
		
		given :
		
		def buyerCurrency = ""
		
		BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
		BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
		BBBDomesticBasketVO domesticBasketVoMock = Mock()
		BBBBasketItemsVO bbbBasketItemVoMock = Mock()
		BBBDisplayVO bbbDisplayVOMock = Mock()
		BBBPricingVO pricingVoMock = Mock()
		 BBBBasketTotalVO basketTotalVOMock = Mock()
		BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
		BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
		List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
		
		populateDisplayVO(bbbDisplayVOMock)
		populatePricingVoMock(pricingVoMock)
		populateBasketTotalVO(basketTotalVOMock)
		populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
		popualteOrderPropertiesVoMock(orderPropertiesVOMock)
		
		populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
		
		bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
		
		populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
		
		domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
		domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
		
		populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
		
		when :

		def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, "false")
		
		then :
		
		requestXml.contains(bbbDisplayVOMock.getColor())
		requestXml.contains(pricingVoMock.getItemDiscount().toString())
		requestXml.contains(basketTotalVOMock.getOrderDiscount().toString())
		requestXml.contains(domesticShippingMethodVoMock.getDeliveryPromiseMaximum().toString())
		requestXml.contains(orderPropertiesVOMock.getCurrencyQuoteId().toString())
		
	}

	def "prepareInternationalOrderRequest - order property LcpRuleId is not set(null)" () {
		
		given :
		
		def buyerCurrency = "USD"
		
		BBBSessionDetailsVO bbbSessionDetailsVOMock = Mock ()
		BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock = Mock()
		BBBDomesticBasketVO domesticBasketVoMock = Mock()
		BBBBasketItemsVO bbbBasketItemVoMock = Mock()
		BBBDisplayVO bbbDisplayVOMock = Mock()
		BBBPricingVO pricingVoMock = Mock()
		 BBBBasketTotalVO basketTotalVOMock = Mock()
		BBBDomesticShippingMethodVO domesticShippingMethodVoMock = Mock()
		BBBOrderPropertiesVO orderPropertiesVOMock = Mock()
		List<BBBBasketItemsVO> bbbBasketItemsVOMock = new ArrayList<>()
		
		populateDisplayVO(bbbDisplayVOMock)
		populatePricingVoMock(pricingVoMock)
		populateBasketTotalVO(basketTotalVOMock)
		populateDomesticShippingMethodVoMock(domesticShippingMethodVoMock)
		
		orderPropertiesVOMock.setCurrencyQuoteId(1001)
		orderPropertiesVOMock.setMerchantOrderId("merch01")
		orderPropertiesVOMock.setMerchantOrderRef("merch01ord01")
		
		populateBasketItemVoMock(bbbBasketItemVoMock, bbbDisplayVOMock, pricingVoMock)
		
		bbbBasketItemsVOMock.add(bbbBasketItemVoMock)
		
		populateSessionDetailsVoMock(bbbSessionDetailsVOMock, buyerCurrency)
		
		domesticBasketVoMock.setBbbBasketTotalVO(basketTotalVOMock)
		domesticBasketVoMock.setBbbBasketItemsVO(bbbBasketItemsVOMock)
		
		populateIntlCheckoutRequestVoMock(internationalCheckoutRequestVOMock, bbbSessionDetailsVOMock, domesticBasketVoMock, domesticShippingMethodVoMock, orderPropertiesVOMock)
		
		when :

		def requestXml = internationalCheckoutMarshallerMock.prepareInternationalOrderRequest(internationalCheckoutRequestVOMock, "false")
		
		then :
		requestXml.contains("lcpRuleId") == false
		requestXml.contains(bbbDisplayVOMock.getColor())
		requestXml.contains(pricingVoMock.getItemDiscount().toString())
		requestXml.contains(basketTotalVOMock.getOrderDiscount().toString())
		requestXml.contains(domesticShippingMethodVoMock.getDeliveryPromiseMaximum().toString())
		requestXml.contains(orderPropertiesVOMock.getCurrencyQuoteId().toString())
		
	}

	
	
	/*
	 * Following methods are only for populating the data required for test cases 
	 */
	private populateIntlCheckoutRequestVoMock(BBBInternationalCheckoutRequestVO internationalCheckoutRequestVOMock, BBBSessionDetailsVO bbbSessionDetailsVOMock, BBBDomesticBasketVO domesticBasketVoMock, BBBDomesticShippingMethodVO domesticShippingMethodVoMock, BBBOrderPropertiesVO orderPropertiesVOMock) {
		
		internationalCheckoutRequestVOMock.setBbbSessionDetailsVO(bbbSessionDetailsVOMock)
		internationalCheckoutRequestVOMock.setBbbDomesticBasketVO(domesticBasketVoMock)
		internationalCheckoutRequestVOMock.setBbbDomesticShippingMethodVO(domesticShippingMethodVoMock)
		internationalCheckoutRequestVOMock.setBbbOrderPropertiesVO(orderPropertiesVOMock)
	}

	private populateSessionDetailsVoMock(BBBSessionDetailsVO bbbSessionDetailsVOMock, String buyerCurrency) {
		
		bbbSessionDetailsVOMock.setBuyerCurrency(buyerCurrency)
		bbbSessionDetailsVOMock.setCheckoutBasketUrl("bedbath.com/checkout/basket")
		bbbSessionDetailsVOMock.setCheckoutFailureUrl("bedbath.com/cart")
		bbbSessionDetailsVOMock.setCheckoutPendingUrl("bedbath.com/checkout/pending")
		bbbSessionDetailsVOMock.setCheckoutUSCartStartPageUrl("bedbath.com/us/cart")
		bbbSessionDetailsVOMock.setCheckoutSuccessUrl("bedbath.com/checkout/success")
		bbbSessionDetailsVOMock.setPayPalReturnUrl("bedbath.com/success")
		bbbSessionDetailsVOMock.setPayPalHeaderLogoUrl("paypal.com/logo.png")
		bbbSessionDetailsVOMock.setPayPalCancelUrl("bedbath.com/checkout/failure")
		bbbSessionDetailsVOMock.setBuyerIpAddress("160.132.133.12")
		bbbSessionDetailsVOMock.setBuyerSessionId("buyerSession01")
		
	}

	private popualteOrderPropertiesVoMock(BBBOrderPropertiesVO orderPropertiesVOMock) {
		orderPropertiesVOMock.setCurrencyQuoteId(1001)
		orderPropertiesVOMock.setLcpRuleId("1002")
		orderPropertiesVOMock.setMerchantOrderId("merch01")
		orderPropertiesVOMock.setMerchantOrderRef("merch01ord01")
	}

	private populateDomesticShippingMethodVoMock(BBBDomesticShippingMethodVO domesticShippingMethodVoMock) {
		domesticShippingMethodVoMock.setDeliveryPromiseMaximum(3)
		domesticShippingMethodVoMock.setDeliveryPromiseMinimum(2)
		domesticShippingMethodVoMock.setDomesticHandlingPrice(3.35)
		domesticShippingMethodVoMock.setDomesticShippingPrice(2.25)
		domesticShippingMethodVoMock.setExtraInsurancePrice(10)
	}

	private populateBasketTotalVO(BBBBasketTotalVO basketTotalVOMock) {
		basketTotalVOMock.setOrderDiscount(6.75)
		basketTotalVOMock.setTotalPrice(110.73)
		basketTotalVOMock.setTotalProductExtraShipping(2.25)
		basketTotalVOMock.setTotalProductExtraHandling(3.45)
		basketTotalVOMock.setTotalSalePrice(110.73)
	}

	private populateBasketItemVoMock(BBBBasketItemsVO bbbBasketItemVoMock, BBBDisplayVO bbbDisplayVOMock, BBBPricingVO pricingVoMock) {
		bbbBasketItemVoMock.setBbbDisplayVO(bbbDisplayVOMock)
		bbbBasketItemVoMock.setBbbPricingVO(pricingVoMock)

		bbbBasketItemVoMock.setQuantity(10)
	}

	private populatePricingVoMock(BBBPricingVO pricingVoMock) {
		pricingVoMock.setItemDiscount(10.0)
		pricingVoMock.setListPrice(125.73)
		pricingVoMock.setProductExtraHandling(3.45)
		pricingVoMock.setProductExtraShipping(2.25)
		pricingVoMock.setSalePrice(110.73)
	}

	private populateDisplayVO(BBBDisplayVO bbbDisplayVOMock) {
		
		bbbDisplayVOMock.setColor("red")
		bbbDisplayVOMock.setDescription("Dark red")
		bbbDisplayVOMock.setName("bbbDisplay01")
		bbbDisplayVOMock.setProductUrl("/product/cambridge-300-thread-count-cotton-sheet-set/3278127")
		bbbDisplayVOMock.setImageUrl("https://s7d9.scene7.com/is/image/BedBathandBeyond/7000123278127m")
		bbbDisplayVOMock.setSize("8")
		
	}
	
}
