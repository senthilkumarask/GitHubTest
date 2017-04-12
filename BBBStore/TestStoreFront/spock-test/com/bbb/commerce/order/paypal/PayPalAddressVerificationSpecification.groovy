package com.bbb.commerce.order.paypal

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.order.CommerceItemRelationship
import atg.core.util.Address
import atg.multisite.SiteContextManager

import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper
import com.bbb.commerce.vo.PayPalInputVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBPayPalConstants
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.validation.BBBValidationRules
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.EcoFeeCommerceItem
import com.bbb.paypal.BBBAddressPPVO
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO
import com.bbb.paypal.vo.PayPalAddressVerifyVO
import com.bbb.utils.BBBUtility

class PayPalAddressVerificationSpecification extends BBBExtendedSpec {
	
	def PayPalAddressVerification addressVerificationObj
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBPurchaseProcessHelper shippingHelperMock = Mock()
	def BBBCheckoutManager checkoutManagerMock = Mock()
	def BBBOrderImpl orderMock = Mock()
	def Properties shippingMethodMapMock
	def List states
	def LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	Map propertyMap= [	"firstName":"First Name",
	"lastName":"Last Name",
	"address1":"Street Address",
	"city":"City",
	"state":"State",
	"country":"Country",
	"postalCode":"Zip Code",
	"phoneNumber":"Phone Number",
	"company":"Company",
	"address2":"Street 2 Address",
	"address3":"Apartment Number",
	"countryAndState":"Country and Sate combination",
	"phoneNumber":"Phone Number",
	"email":"Email",
	"alternatePhoneNumber":"Alternate Phone Number"]
	def Map<String,String> addressVerifyRedirectUrl = [ "SHIPPING":"/checkout/shipping/shipping.jsp","SHIPPING_SINGLE":"/checkout/shipping/singleShipping.jsp",
     "REVIEW":"/checkout/preview/review_cart.jsp", "COUPONS":"/checkout/coupons/coupon.jsp", "SP_SHIPPING":"/checkout/checkout_single.jsp","SP_SHIPPING_SINGLE":"/checkout/checkout_single.jsp",
	  "SP_REVIEW":"/checkout/singlePage/preview/review_cart.jsp", "SP_COUPONS":"/checkout/checkout_single.jsp", "CART":"/cart/cart.jsp"]
	def BBBPayPalServiceManager paypalServiceManagerMock = Mock()
	
	def setup(){
		addressVerificationObj = new PayPalAddressVerification(catalogTools:catalogToolsMock,shippingHelper:shippingHelperMock,checkoutManager:checkoutManagerMock,paypalServiceManager:paypalServiceManagerMock,lblTxtTemplateManager:lblTxtTemplateManagerMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
	}
	def "validating shipping address for paypal user failed verfication "(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO() 
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> "Error in Shipping Page"
		0*paypalServiceManagerMock.addressInOrder(orderMock)
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, null)
		then:
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	} 
	def "validating shipping address for paypal user is NONPOBox asddress "(){
		given:
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setAddress1("43206 parsons ave")
		shipAddress.setIsNonPOBoxAddress(false)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalVo = new PayPalInputVO()
		resVo.setShippingAddress(shipAddress)
		def BBBCommerceItem citem = Mock()
		def EcoFeeCommerceItem cecoitem = Mock()
		orderMock.getCommerceItems() >> [citem,cecoitem]
		1*checkoutManagerMock.canItemShipToAddress("BedBathUS",_, _) >> true
		orderMock.getSiteId() >> "BedBathUS"
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, paypalVo)
		then:
		addressVerifyVO.getRedirectUrl() == null
		addressVerifyVO.getAddressErrorList().size() == 0
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating shipping address for paypal user is NONPOBox asddress  and have shippping group"(){
		given:
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setAddress1("43206 parsons ave")
		shipAddress.setIsNonPOBoxAddress(false)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalVo = new PayPalInputVO()
		resVo.setShippingAddress(shipAddress)
		def BBBCommerceItem citem = Mock()
		orderMock.getCommerceItems() >> [citem]
		1*paypalServiceManagerMock.addressInOrder(orderMock) >> true
		1*checkoutManagerMock.canItemShipToAddress("BedBathUS",_, _) >> true
		orderMock.getSiteId() >> "BedBathUS"
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, paypalVo)
		then:
		addressVerifyVO.getRedirectUrl() == null
		addressVerifyVO.getAddressErrorList().size() == 0
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating shipping address for paypal user is NONPOBox asddress and item canot be ship to given address"(){
		given:
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setAddress1("43206 parsons ave")
		shipAddress.setIsNonPOBoxAddress(false)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalVo = new PayPalInputVO()
		resVo.setShippingAddress(shipAddress)
		def BBBCommerceItem citem = Mock()
		orderMock.getCommerceItems() >> [citem]
		1*checkoutManagerMock.canItemShipToAddress("BedBathUS",_, _) >> false
		orderMock.getSiteId() >> "BedBathUS"
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, paypalVo)
		then:
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	
	def "validating shipping address for paypal user isPOBox asddress and item can not ship to user selected address "(){
		given:
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setAddress1("POB 65502")
		shipAddress.setPostalCode("85728")
		shipAddress.setIsNonPOBoxAddress(true)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalVo = new PayPalInputVO()
		resVo.setShippingAddress(shipAddress)
		def BBBCommerceItem citem = Mock()
		citem.getCatalogRefId() >> "1234"
		orderMock.getCommerceItems() >> [citem]
		1*checkoutManagerMock.canItemShipToAddress("BedBathUS",_, _) >> false
		0*catalogToolsMock.isShippingZipCodeRestrictedForSku(_,_,_) 
		orderMock.getSiteId() >> "BedBathUS"
		catalogToolsMock.isShippingZipCodeRestrictedForSku("1234", "BedBathUS", "85728") >> true
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, paypalVo)
		then:
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for paypal user isPOBox asddress "(){
		given:
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setAddress1("POB 65502")
		shipAddress.setPostalCode("85728")
		shipAddress.setIsNonPOBoxAddress(true)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalVo = new PayPalInputVO()
		resVo.setShippingAddress(shipAddress)
		def BBBCommerceItem citem = Mock()
		citem.getCatalogRefId() >> "1234"
		orderMock.getCommerceItems() >> [citem]
		checkoutManagerMock.canItemShipToAddress("BedBathUS",_, _) >> true
		orderMock.getSiteId() >> "BedBathUS"
		catalogToolsMock.isShippingZipCodeRestrictedForSku("1234", "BedBathUS", "85728") >> true
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, paypalVo)
		then:
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for paypal user isPOBox asddress  throws System Exception"(){
		given:
		addressVerificationObj =  Spy()
		addressVerificationObj.setCheckoutManager(checkoutManagerMock)
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setAddress1("POB 65502")
		shipAddress.setPostalCode("85728")
		shipAddress.setIsNonPOBoxAddress(true)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalVo = new PayPalInputVO()
		resVo.setShippingAddress(shipAddress)
		def BBBCommerceItem citem = Mock()
		citem.getCatalogRefId() >> "1234"
		orderMock.getCommerceItems() >> [citem]
		checkoutManagerMock.canItemShipToAddress("BedBathUS",_, _) >> {throw new BBBSystemException("Mock of System Exception")} 
		orderMock.getSiteId() >> "BedBathUS"
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, paypalVo)
		then:
		1*addressVerificationObj.logError(_)
	}
	def "validating shipping address for paypal user isPOBox asddress  throws Business Exception"(){
		given:
		addressVerificationObj =  Spy()
		addressVerificationObj.setCheckoutManager(checkoutManagerMock)
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setPostalCode("85728")
		shipAddress.setIsNonPOBoxAddress(true)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalVo = new PayPalInputVO()
		resVo.setShippingAddress(shipAddress)
		def BBBCommerceItem citem = Mock()
		citem.getCatalogRefId() >> "1234"
		orderMock.getCommerceItems() >> [citem]
		checkoutManagerMock.canItemShipToAddress("BedBathUS",_, _) >> {throw new BBBBusinessException("Mock of Business Exception")}
		orderMock.getSiteId() >> "BedBathUS"
		when:
		boolean valid = addressVerificationObj.validatePayPalShippingAddress(resVo, orderMock, addressVerifyVO, paypalVo)
		then:
		1*addressVerificationObj.logError(_)
	}
	def "validating shipping address for international  user and does not have shipping address"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		orderMock.getSiteId() >> "BedBathUS"
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalOrPOError() == false
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for international  user and site BedBathUS"(){
		given:
		addressVerificationObj = Spy()
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("IN")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "BedBathUS"
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalOrPOError() == true
		addressVerifyVO.isInternationalError() == true
		addressVerifyVO.getRedirectUrl().equals("/cart/cart.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for international  user and site TBS_BuyBuyBaby"(){
		given:
		addressVerificationObj = Spy()
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("IN")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "TBS_BuyBuyBaby"
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalOrPOError() == true
		addressVerifyVO.isInternationalError() == true
		addressVerifyVO.getRedirectUrl().equals("/cart/cart.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for international  user and site BuyBuyBaby"(){
		given:
		addressVerificationObj = Spy()
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("IN")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "BuyBuyBaby"
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalOrPOError() == true
		addressVerifyVO.isInternationalError() == true
		addressVerifyVO.getRedirectUrl().equals("/cart/cart.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for international  user and site BedBathCanada"(){
		given:
		addressVerificationObj = Spy()
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("IN")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "BedBathCanada"
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalOrPOError() == true
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for international  user and site TBS_BedBathCanada"(){
		given:
		addressVerificationObj = Spy()
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("IN")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "TBS_BedBathCanada"
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalOrPOError() == true
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for international  user and site TBS_BedBathUS"(){
		given:
		addressVerificationObj = Spy()
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("IN")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "TBS_BedBathUS"
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalOrPOError() == true
		addressVerifyVO.isInternationalError() == true
		addressVerifyVO.getRedirectUrl().equals("/cart/cart.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for internationla user with site id has BuyBuyBaby and country other then US and CA"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("IN")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		orderMock.getSiteId() >> "BuyBuyBaby"
		1*catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>  ["true"]
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 0
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating shipping address for internationla user with site id has TBS_BedBathCanada and country  CA"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("CA")
		shipAddress.setState("Quebec")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "TBS_BedBathCanada"
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>  ["true"]
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_QUEBEC_PP_SHIPPING_ADDRESS, "EN", null) >> "err_quebec_pp_shipping_address"
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for internationla user with site id has BedBathCanada and country  CA"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("CA")
		shipAddress.setState("Quebec")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		addressVerificationObj.getSiteId() >> "BedBathCanada"
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>  ["true"]
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_QUEBEC_PP_SHIPPING_ADDRESS, "EN", null) >> "err_quebec_pp_shipping_address"
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for internationla user with site id has BedBathUS and country  US"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("US")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		1*addressVerificationObj.getSiteId() >> "BedBathUS"
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>  ["true"]
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,["firstName","lastName"])
		list.add(1,[])
		1*shippingHelperMock.checkForRequiredAddressProperties(shipAddress,_) >> list
		1*shippingHelperMock.getAddressPropertyNameMap() >> propertyMap
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 2
		addressVerifyVO.getAddressErrorList().get(0).equalsIgnoreCase("First Name is Missing")
		addressVerifyVO.getAddressErrorList().get(1).equalsIgnoreCase("Last Name is Missing")
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for internationla user with site id has BuyBuyBaby and country  US"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("US")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		1*addressVerificationObj.getSiteId() >> "BuyBuyBaby"
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>  ["true"]
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,["firstName","lastName"])
		1*shippingHelperMock.checkForRequiredAddressProperties(shipAddress,_) >> list
		1*shippingHelperMock.getAddressPropertyNameMap() >> propertyMap
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 2
		addressVerifyVO.getAddressErrorList().get(0).equalsIgnoreCase("First Name is Invalid")
		addressVerifyVO.getAddressErrorList().get(1).equalsIgnoreCase("Last Name is Invalid")
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for paypaluser and shipping address is po box address"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("US")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		1*addressVerificationObj.getSiteId() >> "BuyBuyBaby"
		1*catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>  ["false"]
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PO_BOX_PP_ADDRESS, "EN", null) >> "err_PoBox_pp_address"
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shippingHelperMock.checkForRequiredAddressProperties(shipAddress,_) >> list
		0*shippingHelperMock.getAddressPropertyNameMap() >> propertyMap
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.getAddressErrorList().get(0).equalsIgnoreCase("err_PoBox_pp_address")
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating shipping address for paypaluser and shipping address is non po box address"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("US")
		shipAddress.setAddress1("43206 parsons ave")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		1*addressVerificationObj.getSiteId() >> "BuyBuyBaby"
		1*catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>  ["false"]
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shippingHelperMock.checkForRequiredAddressProperties(shipAddress,_) >> list
		0*shippingHelperMock.getAddressPropertyNameMap() >> propertyMap
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 0
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating shipping address for paypaluser and shipping address is po box address throws system exception"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry(null)
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		1*addressVerificationObj.getSiteId() >> "BuyBuyBaby"
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PO_BOX_PP_ADDRESS, "EN", null) >> "err_PoBox_pp_address"
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shippingHelperMock.checkForRequiredAddressProperties(shipAddress,_) >> list
		0*shippingHelperMock.getAddressPropertyNameMap() >> propertyMap
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBSystemException("")}
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 0
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating shipping address for paypaluser and shipping address is po box address throws Business exception"(){
		given:
		addressVerificationObj = Spy()
		addressVerificationObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		addressVerificationObj.setCatalogTools(catalogToolsMock)
		addressVerificationObj.setAddressVerifyRedirectUrl(addressVerifyRedirectUrl)
		addressVerificationObj.setShippingHelper(shippingHelperMock)
		BBBGetExpressCheckoutDetailsResVO resVo= new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		shipAddress.setCountry("CA")
		shipAddress.setState("columbus")
		resVo.setShippingAddress(shipAddress)
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		1*addressVerificationObj.getSiteId() >> "BedBathCanada"
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PO_BOX_PP_ADDRESS, "EN", null) >> "err_PoBox_pp_address"
		1*shippingHelperMock.checkForRequiredAddressProperties(shipAddress,_) >> null
		0*shippingHelperMock.getAddressPropertyNameMap() >> propertyMap
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBBusinessException("")}
		when:
		boolean valid = addressVerificationObj.validateInternationalAndPOAddress(resVo,addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 0
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating item available in order are eligilble for user selected shipping state can not ship for non POBox"(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setAddress1("43206 parsons ave")
		adr.setCity("Columbus")
		adr.setCountry("US")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		CommerceItemRelationship crItem = Mock()
		BBBCommerceItem cItem = Mock()
		crItem.getCommerceItem() >> cItem
		hrdShip.getCommerceItemRelationships() >> [crItem]
		orderMock.getSiteId() >> "BedBathUS"
		1*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> true 
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_STATE_INCORRECT_PP_ADDRESS, "EN", null) >> "err_state_incorrect_pp_address"
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.getAddressErrorList().get(0).equalsIgnoreCase("err_state_incorrect_pp_address")
		addressVerifyVO.isSuccess() == false
		valid == false
	} 
	def "validating item available in order are eligilble for user selected shipping state can not ship for  POBox"(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setCity("Columbus")
		adr.setCountry("US")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		CommerceItemRelationship crItem = Mock()
		BBBCommerceItem cItem = Mock()
		crItem.getCommerceItem() >> cItem
		hrdShip.getCommerceItemRelationships() >> [crItem]
		orderMock.getSiteId() >> "BedBathUS"
		1*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> true  
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_POBOX_INCORRECT_PP_ADDRESS, "EN", null) >> "err_pobox_incorrect_pp_address"
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.getAddressErrorList().get(0).equalsIgnoreCase("err_pobox_incorrect_pp_address")
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating item available in order are eligilble for user selected shipping zipcode can not ship "(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setAddress1("POB 65502")
		adr.setCity("Columbus")
		adr.setCountry("US")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		CommerceItemRelationship crItem = Mock()
		BBBCommerceItem cItem = Mock()
		crItem.getCommerceItem() >> cItem
		hrdShip.getCommerceItemRelationships() >> [crItem]
		orderMock.getSiteId() >> "BedBathUS"
		1*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> false  
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, "EN", null) >> "err_zip_code_restriction_pp_address"
		catalogToolsMock.isShippingZipCodeRestrictedForSku(_,"BedBathUS", _) >> true
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals("/checkout/shipping/shipping.jsp?shippingRestriction=true")
		addressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
		addressVerifyVO.getAddressErrorList().size() == 1
		addressVerifyVO.getAddressErrorList().get(0).equalsIgnoreCase("err_zip_code_restriction_pp_address")
		addressVerifyVO.isSuccess() == false
		valid == false
	}
	def "validating item available in order are eligilble for user selected shipping  "(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setAddress1("POB 65502")
		adr.setCity("Columbus")
		adr.setCountry("US")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		CommerceItemRelationship crItem = Mock()
		BBBCommerceItem cItem = Mock()
		crItem.getCommerceItem() >> cItem
		hrdShip.getCommerceItemRelationships() >> [crItem]
		orderMock.getSiteId() >> "BedBathUS"
		1*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> false
		1*catalogToolsMock.isShippingZipCodeRestrictedForSku(_,"BedBathUS", _) >> false
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals(null)
		addressVerifyVO.getRedirectState().equals(null)
		addressVerifyVO.getAddressErrorList().size() ==0
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating item available in order are eligilble for user selected shipping zipcode can not ship throws system excpetion "(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setAddress1("POB 65502")
		adr.setCity("Columbus")
		adr.setCountry("US")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		CommerceItemRelationship crItem = Mock()
		BBBCommerceItem cItem = Mock()
		crItem.getCommerceItem() >> cItem
		hrdShip.getCommerceItemRelationships() >> [crItem]
		orderMock.getSiteId() >> "BedBathUS"
		1*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> false
		0*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, "EN", null) >> "err_zip_code_restriction_pp_address"
		catalogToolsMock.isShippingZipCodeRestrictedForSku(_,"BedBathUS", _) >> {throw new BBBSystemException("")}
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals(null)
		addressVerifyVO.getRedirectState().equals(null)
		addressVerifyVO.getAddressErrorList() == null
		addressVerifyVO.isSuccess() == false
		valid == true
	}
	def "validating item available in order are eligilble for user selected shipping zipcode can not ship  throws bussiness exception"(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setAddress1("POB 65502")
		adr.setCity("Columbus")
		adr.setCountry("US")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		CommerceItemRelationship crItem = Mock()
		BBBCommerceItem cItem = Mock()
		crItem.getCommerceItem() >> cItem
		hrdShip.getCommerceItemRelationships() >> [crItem]
		orderMock.getSiteId() >> "BedBathUS"
		1*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> false
		0*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, "EN", null) >> "err_zip_code_restriction_pp_address"
		catalogToolsMock.isShippingZipCodeRestrictedForSku(_,"BedBathUS", _) >> {throw new BBBBusinessException("")}
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals(null)
		addressVerifyVO.getRedirectState().equals(null)
		addressVerifyVO.getAddressErrorList() == null
		addressVerifyVO.isSuccess() == false
		valid == true
	}
	def "validating item available in order are eligilble for user selected shipping not having city "(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setCountry("US")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		orderMock.getSiteId() >> "BedBathUS"
		0*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> false
		0*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, "EN", null) >> "err_zip_code_restriction_pp_address"
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals(null)
		addressVerifyVO.getRedirectState().equals(null)
		addressVerifyVO.getAddressErrorList().isEmpty()
		addressVerifyVO.isSuccess() == true
		valid == true
	}
	def "validating item available in order are eligilble for user selected shipping not having Address1 "(){
		given:
		BBBHardGoodShippingGroup hrdShip = Mock()
		Address adr = new Address()
		hrdShip.getShippingAddress() >> adr
		adr.setFirstName("bbb")
		adr.setLastName("last")
		adr.setCountry("US")
		adr.setAddress1("43206 parsons ave")
		BBBStoreShippingGroup strShip = new BBBStoreShippingGroup()
		PayPalAddressVerifyVO addressVerifyVO =  new PayPalAddressVerifyVO()
		orderMock.getShippingGroups() >> [hrdShip,strShip]
		orderMock.getSiteId() >> "BedBathUS"
		hrdShip.getCommerceItemRelationships() >> []
		0*checkoutManagerMock.checkItemShipToAddress("BedBathUS", _, _) >> false
		0*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_ZIP_CODE_RESTRICTION_PP_ADDRESS, "EN", null) >> "err_zip_code_restriction_pp_address"
		when:
		boolean valid = addressVerificationObj.validateShippingAddressInOrder(orderMock, addressVerifyVO)
		then:
		addressVerifyVO.isInternationalError() == false
		addressVerifyVO.getRedirectUrl().equals(null)
		addressVerifyVO.getRedirectState().equals(null)
		addressVerifyVO.getAddressErrorList().isEmpty()
		addressVerifyVO.isSuccess() == true
		valid == true
	}
}
