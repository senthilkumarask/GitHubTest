package com.bbb.commerce.checkout.droplet

import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec;

class BBBCheckoutProductDetailDropletSpecification extends BBBExtendedSpec {

	def BBBCheckoutProductDetailDroplet bCPDDropletObject
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBOrder orderMock = Mock()
	SKUDetailVO skuDetailVO = new SKUDetailVO()
	Map<String, Integer> tempAvailabilityMap = new HashMap<String, Integer>()
	def setup(){
		  bCPDDropletObject = new BBBCheckoutProductDetailDroplet(catalogTools:catalogToolsMock)
	}
	
	def "service. TC to check skubelowLine is true when inventory status is available"(){
		given:
		requestMock.getParameter("productId") >> "pro568"
		requestMock.getParameter("skuId") >> "454"
		requestMock.getParameter("commerceItemId") >> "ci23654"
		requestMock.getParameter("siteId") >> "siteId"
		catalogToolsMock.getSKUDetails("siteId", "454", false, true, true) >> skuDetailVO
		requestMock.getObjectParameter("order") >> orderMock
		
		tempAvailabilityMap.put("ci23654", 0)
		orderMock.getAvailabilityMap() >> tempAvailabilityMap
		// inside if (pSKUDetailVO)
		catalogToolsMock.isSKUBelowLine("siteId", "454") >> true
		
		when:
		bCPDDropletObject.service(requestMock, responseMock)
		then:
		skuDetailVO.isSkuBelowLine() == true
		skuDetailVO.isSkuInStock() == true
		1*requestMock.setParameter("pSKUDetailVO", skuDetailVO)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service. TC to check skubelowLine is true when inventory status is limited"(){
		given:
		requestMock.getParameter("productId") >> "pro568"
		requestMock.getParameter("skuId") >> "454"
		requestMock.getParameter("commerceItemId") >> "ci23654"
		requestMock.getParameter("siteId") >> "siteId"
		catalogToolsMock.getSKUDetails("siteId", "454", false, true, true) >> skuDetailVO
		requestMock.getObjectParameter("order") >> orderMock
		//inventory status is 2
		tempAvailabilityMap.put("ci23654", 2)
		orderMock.getAvailabilityMap() >> tempAvailabilityMap
		// inside if (pSKUDetailVO)
		catalogToolsMock.isSKUBelowLine("siteId", "454") >> true
		
		when:
		bCPDDropletObject.service(requestMock, responseMock)
		then:
		skuDetailVO.isSkuBelowLine() == true
		skuDetailVO.isSkuInStock() == true
		1*requestMock.setParameter("pSKUDetailVO", skuDetailVO)
		1*requestMock.serviceParameter("output", requestMock, responseMock)

	}
	
	def "service. TC to check skubelowLine is true when inventory status is not in ( limited or available )"(){
		given:
        setRequestParam()	
		catalogToolsMock.getSKUDetails("siteId", "454", false, true, true) >> skuDetailVO
		requestMock.getObjectParameter("order") >> orderMock
		//inventory status is 4
		tempAvailabilityMap.put("ci23654", 4)
		orderMock.getAvailabilityMap() >> tempAvailabilityMap
		// inside if (pSKUDetailVO)
		catalogToolsMock.isSKUBelowLine("siteId", "454") >> false
		
		when:
		bCPDDropletObject.service(requestMock, responseMock)
		then:
		skuDetailVO.isSkuBelowLine() == false
		skuDetailVO.isSkuInStock() == false
		1*requestMock.setParameter("pSKUDetailVO", skuDetailVO)
		1*requestMock.serviceParameter("output", requestMock, responseMock)

	}
	
	def "service. TC to check sku in stock when tempAvailabilityMap not contain  commerceItemId (pass through request parameter) "(){
		given:
		setRequestParam()
		catalogToolsMock.getSKUDetails("siteId", "454", false, true, true) >> skuDetailVO
		requestMock.getObjectParameter("order") >> orderMock
		tempAvailabilityMap.put("ci23", 0)
		orderMock.getAvailabilityMap() >> tempAvailabilityMap
		// inside if (pSKUDetailVO)
		catalogToolsMock.isSKUBelowLine("siteId", "454") >> false
		
		when:
		bCPDDropletObject.service(requestMock, responseMock)
		then:
		skuDetailVO.isSkuInStock() == true
		1*requestMock.setParameter("pSKUDetailVO", skuDetailVO)
		1*requestMock.serviceParameter("output", requestMock, responseMock)

	}
	
	def "service. TC to check Error Open Parameter add to request when skuDetail VO and availability map is null "(){
		given:
		requestMock.getParameter("productId") >> "pro568"
		requestMock.getParameter("skuId") >> "454"
		requestMock.getParameter("commerceItemId") >> "ci23654"
		requestMock.getParameter("siteId") >> null
		catalogToolsMock.getSKUDetails(null, "454", false, true, true) >> null
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getAvailabilityMap() >> null
		// inside if (pSKUDetailVO)		
		when:
		bCPDDropletObject.service(requestMock, responseMock)
		then:
        //1*bCPDDropletObject.logError("BBBCheckoutProductDetailDroplet : SKUDetail not found from catalog API " + "454")
		1*requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	
	def "service. TC to check Error Open Parameter add to request when BBBBusinessException throws and availability map is null "(){
		given:
		setRequestParam()
		catalogToolsMock.getSKUDetails("siteId", "454", false, true, true) >> skuDetailVO
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getAvailabilityMap() >> null
		// inside if (pSKUDetailVO)
		catalogToolsMock.isSKUBelowLine("siteId", "454") >> {throw new BBBBusinessException("exception") }
		
		when:
		bCPDDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	
	def "service. TC to check Error Open Parameter add to request when BBBSystemException throws while geting skuDetails "(){
		given:
		setRequestParam()
		catalogToolsMock.getSKUDetails("siteId", "454", false, true, true) >> {throw new BBBSystemException("exception") }
	
		when:
		bCPDDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	// sets the common request parameter to request object
	private setRequestParam(){
		
		requestMock.getParameter("productId") >> "pro568"
		requestMock.getParameter("skuId") >> "454"
		requestMock.getParameter("commerceItemId") >> "ci23654"
		requestMock.getParameter("siteId") >> "siteId"

	}
}
