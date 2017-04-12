package com.bbb.commerce.order.purchase

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.common.BBBMultishipVO
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import atg.commerce.order.ElectronicShippingGroup
import atg.commerce.order.OrderHolder
import atg.commerce.order.OrderImpl;
import spock.lang.specification.BBBExtendedSpec

class DisplaySingleShippingDropletSpecification extends BBBExtendedSpec {

	def DisplaySingleShippingDroplet dssDroplet
	def BBBCheckoutManager cManager =Mock()
	def BBBCatalogToolsImpl catalogTools = Mock()
	def BBBHardGoodShippingGroup hgsg = Mock()
	def BBBShippingGroupManager sgManager = Mock()
	def ElectronicShippingGroup elsg = Mock()
	def OrderHolder cart = Mock()
	
	def BBBOrderImpl order = Mock()
	ShipMethodVO smVO = new ShipMethodVO()
	
	def setup(){
		dssDroplet  = new DisplaySingleShippingDroplet(manager : cManager)
	}
	
	def "service.TC for single shipping " (){
		given:
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("paypalError") >> "true"
			requestMock.getParameter("isFormException") >> "true"
			requestMock.getParameter("fromMobile") >> "fromMobile"
			order.getCommerceItemCount() >> 2
			cManager.getCatalogTools() >> catalogTools
			1*catalogTools.getAllValuesForKey("FlagDrivenFunctions", "SinglePageCheckoutOn") >> []
			
			order.isPayPalOrder() >> true
			
			1*cManager.orderContainsLTLItem(order) >> false
			2*order.getShippingGroups() >> [hgsg]
			hgsg.getShippingMethod() >> "sdd"
			
			1*cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getShippingMethodForID("sdd") >> smVO
			
		when:
			dssDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isSingle", true)
			1 * requestMock.setParameter("isMultiShipping", false);
			1 * requestMock.setParameter("showSingleShipping", true)
			1 * requestMock.serviceLocalParameter("single", requestMock, responseMock)
			1 * requestMock.setParameter("shippingMethodDetail", smVO)

	} 
	
	def "service.TC for single shipping group type is not HardGoodShippingGroup " (){
		given:
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("paypalError") >> "false"
			requestMock.getParameter("isFormException") >> "false"
			requestMock.getParameter("fromMobile") >> "true"
			order.getCommerceItemCount() >> 2
			cManager.getCatalogTools() >> catalogTools
			1*catalogTools.getAllValuesForKey("FlagDrivenFunctions", "SinglePageCheckoutOn") >> ["true"]
			
			order.isPayPalOrder() >> true
			
			1*cManager.displaySingleShipping(order, true) >> true
			
			1*cManager.orderContainsLTLItem(order) >> false
			order.getShippingGroups() >> [elsg]
			
		when:
			dssDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isSingle", true)
			1 * requestMock.setParameter("isMultiShipping", false);
			1 * requestMock.setParameter("showSingleShipping", true)
			1 * requestMock.serviceLocalParameter("single", requestMock, responseMock)
			0 * requestMock.setParameter("shippingMethodDetail", smVO)

	}
	
	def "service.TC for multi shipping group when request is not comming from mobile " (){
		given:
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("paypalError") >> ""
			requestMock.getParameter("isFormException") >> ""
			requestMock.getParameter("fromMobile") >> "false"
			order.getCommerceItemCount() >> 2
			cManager.getCatalogTools() >> catalogTools
			1*catalogTools.getAllValuesForKey("FlagDrivenFunctions", "SinglePageCheckoutOn") >> ["true"]
			
			order.isPayPalOrder() >> true
			
			cManager.displaySingleShipping(order, false) >> false
			
			cManager.orderContainsLTLItem(order) >> false
			
		when:
			dssDroplet.service(requestMock, responseMock)
		then:
			0 * requestMock.setParameter("isSingle", true)
			1 * requestMock.setParameter("isMultiShipping", true);
			1 * requestMock.setParameter("showSingleShipping", false)
			1 * requestMock.serviceLocalParameter("multi", requestMock, responseMock)

	}
	
	def "service.TC when order is not paypal order" (){
		given:
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("paypalError") >> ""
			requestMock.getParameter("isFormException") >> ""
			requestMock.getParameter("fromMobile") >> ""
			order.getCommerceItemCount() >> 2
			cManager.getCatalogTools() >> catalogTools
			1*catalogTools.getAllValuesForKey("FlagDrivenFunctions", "SinglePageCheckoutOn") >> ["true"]
			
			order.isPayPalOrder() >> false
			
			cManager.displaySingleShipping(order, false) >> false
			
			cManager.orderContainsLTLItem(order) >> true
			
		when:
			dssDroplet.service(requestMock, responseMock)
		then:
			0 * requestMock.setParameter("isSingle", true)
			1 * requestMock.setParameter("isMultiShipping", true);
			1 * requestMock.setParameter("showSingleShipping", false)
			1 * requestMock.serviceLocalParameter("multi", requestMock, responseMock)

	}
	
	/****************************************** exception scenario ********************************/
	
	def "service.TC for BBBSystemException " (){
		given:
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("paypalError") >> "false"
			requestMock.getParameter("isFormException") >> "true"
			requestMock.getParameter("fromMobile") >> "true"
			order.getCommerceItemCount() >> 2
			cManager.getCatalogTools() >> catalogTools
			1*catalogTools.getAllValuesForKey("FlagDrivenFunctions", "SinglePageCheckoutOn") >> ["false"]
			
			order.isPayPalOrder() >> true
			
			cManager.displaySingleShipping(order, true) >> true
			
			cManager.orderContainsLTLItem(order) >> {throw new  BBBSystemException("exception")}
			
		when:
			dssDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isSingle", true)
			0 * requestMock.setParameter("isMultiShipping", _);
			0 * requestMock.setParameter("showSingleShipping", _)
			0 * requestMock.serviceLocalParameter("single", requestMock, responseMock)
			0 * requestMock.setParameter("shippingMethodDetail", smVO)

	}
	
	def "service.TC for BBBBusinessException " (){
		given:
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("paypalError") >> "false"
			requestMock.getParameter("isFormException") >> "true"
			requestMock.getParameter("fromMobile") >> "true"
			order.getCommerceItemCount() >> 2
			cManager.getCatalogTools() >> catalogTools
			1*catalogTools.getAllValuesForKey("FlagDrivenFunctions", "SinglePageCheckoutOn") >> ["false"]
			
			order.isPayPalOrder() >> false
			
			cManager.displaySingleShipping(order, false) >> true
			
			cManager.orderContainsLTLItem(order) >> {throw new  BBBBusinessException("exception")}
			
		when:
			dssDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isSingle", true)
			0 * requestMock.setParameter("isMultiShipping", _);
			0 * requestMock.setParameter("showSingleShipping", _)
			0 * requestMock.serviceLocalParameter("single", requestMock, responseMock)
			0 * requestMock.setParameter("shippingMethodDetail", smVO)

	}
	
	def "service.TC when cart is empty" (){
		given:
			1*requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			requestMock.getObjectParameter("isMultiShipping") >> true
			requestMock.getObjectParameter("showSingleShipping") >> true
		    requestMock.getObjectParameter("order") >> order
			order.getCommerceItemCount() >> 0
			cManager.getCatalogTools() >> catalogTools
			1*catalogTools.getAllValuesForKey("FlagDrivenFunctions", "SinglePageCheckoutOn") >> ["false"]

		when:
			BBBMultishipVO vo = dssDroplet.isMultiShippingOrder()
		then:
			1 * requestMock.setParameter("isMultiShipping", false);
			1 * requestMock.setParameter("showSingleShipping", false)
			1 * requestMock.serviceLocalParameter("cart", requestMock, responseMock)
			1 * requestMock.setParameter("fromMobile", true)
			1 * requestMock.setParameter("order", _)
			vo.isShowSingleShipLink() == true
			vo.isDefaultMultiship() == true

	}
	
}
