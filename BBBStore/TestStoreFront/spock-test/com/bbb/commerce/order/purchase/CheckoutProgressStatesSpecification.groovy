package com.bbb.commerce.order.purchase

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.constants.BBBCoreConstants
import spock.lang.specification.BBBExtendedSpec

class CheckoutProgressStatesSpecification extends BBBExtendedSpec {
	CheckoutProgressStates checkoutStates
	BBBOrderImpl orderMock = Mock()
	BBBCatalogToolsImpl cToolsMock = Mock()
	BBBCheckoutManager cManager =Mock()
	def setup(){
		checkoutStates	=	new CheckoutProgressStates()
		checkoutStates.setCheckoutManager(cManager)
		checkoutStates.setCatalogUtil(cToolsMock)
	}
	def "This is the method to validate single page checkout eligble  "(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> ["true"]
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> ["false"]
		orderMock.getShippingGroupCount() >> 1
		1*cManager.displaySingleShipping(orderMock, false) >> true
		1*cManager.orderContainsLTLItem(orderMock) >> false
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==true
	}
	def "This is the method to validate single page checkout eligble with SinglePageCheckoutOn , SPC_consult_sitespect is null"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> null
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> null
		orderMock.getShippingGroupCount() >> 1
		1*cManager.displaySingleShipping(orderMock, false) >> false
		1*cManager.orderContainsLTLItem(orderMock) >> false
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==false
	}
	def "This is the method to validate single page checkout eligble with empty "(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> []
		orderMock.getShippingGroupCount() >> 2
		1*cManager.displaySingleShipping(orderMock, false) >> false
		1*cManager.orderContainsLTLItem(orderMock) >> false
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==false
	}
	
	def "This is the method to validate multi page checkout eligble"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> ["true"]
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> ["false"]
		orderMock.getShippingGroupCount() >> 2
		1*cManager.displaySingleShipping(orderMock, false) >> false
		1*cManager.orderContainsLTLItem(orderMock) >> false
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==false
	}
	def "This is the method to validate multi page checkout eligble BBBSystemException"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> ["true"]
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> ["false"]
		orderMock.getShippingGroupCount() >> 2
		1*cManager.displaySingleShipping(orderMock, false) >> false
		1*cManager.orderContainsLTLItem(orderMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==false
	}
	def "This is the method to validate multi page checkout eligble BBBBusinessException"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> ["true"]
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> ["false"]
		orderMock.getShippingGroupCount() >> 2
		1*cManager.displaySingleShipping(orderMock, false) >> false
		1*cManager.orderContainsLTLItem(orderMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==false
	}
	def "This is the method to validate multi page checkout eligble with BBBBusinessException"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		orderMock.getShippingGroupCount() >> 2
		1*cManager.displaySingleShipping(orderMock, false) >> false
		1*cManager.orderContainsLTLItem(orderMock) >> true
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==false
	}
	def "This is the method to validate multi page checkout eligble with BBBSystemException"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> {throw new BBBSystemException("Mock for BBBBusinessException")}
		orderMock.getShippingGroupCount() >> 2
		1*cManager.displaySingleShipping(orderMock, false) >> false
		1*cManager.orderContainsLTLItem(orderMock) >> true
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , true)
		then:
		eligble==false
	}
	
	def "This is the method to validate single page checkout eligble SPC_consult_sitespect to true"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> ["true"]
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> ["true"]
		orderMock.getShippingGroupCount() >> 1
		1*cManager.displaySingleShipping(orderMock, false) >> true
		1*cManager.orderContainsLTLItem(orderMock) >> false
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , false)
		then:
		eligble==false
	}
	def "This is the method to validate single page checkout eligble SPC_consult_sitespect to false"(){
		given:
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> ["true"]
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> ["false"]
		orderMock.getShippingGroupCount() >> 1
		1*cManager.displaySingleShipping(orderMock, false) >> true
		1*cManager.orderContainsLTLItem(orderMock) >> false
		when:
		boolean eligble = checkoutStates.spcEligible(orderMock , false)
		then:
		eligble==true
	}
	
}
