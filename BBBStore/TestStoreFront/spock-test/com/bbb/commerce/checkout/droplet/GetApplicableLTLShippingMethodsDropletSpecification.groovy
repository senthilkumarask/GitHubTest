package com.bbb.commerce.checkout.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec

class GetApplicableLTLShippingMethodsDropletSpecification extends BBBExtendedSpec {
    def GetApplicableLTLShippingMethodsDroplet aShMethodDropletObject
	def BBBCatalogTools catalogToolsMock = Mock()
    Locale local = Locale.forLanguageTag("en-US");
	ShipMethodVO shippingMethodVO = new ShipMethodVO()
	def setup(){
		aShMethodDropletObject = new GetApplicableLTLShippingMethodsDroplet(catalogTools : catalogToolsMock)
	}
	
	def"service. TC for add  list of shipping method to request param for sku" (){
		given:
		 requestMock.getParameter("skuId") >> "sk123456"
		 requestMock.getParameter("siteId") >> "USBedbath"
		 requestMock.getLocale() >> local
		 shippingMethodVO.setShippingCharge(10)
		 // getting list of shipping methods
		 catalogToolsMock.getLTLEligibleShippingMethods("sk123456", "USBedbath", "en") >> [shippingMethodVO]
		 when:
		 aShMethodDropletObject.service(requestMock, responseMock)
		 then:
		1* requestMock.setParameter("shipMethodVOList", [shippingMethodVO])
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		shippingMethodVO.getShippingCharge() == 10
	}
	
	def"service. TC for add empty list of shipping method to request param when no shippingMethod is available" (){
		given:
		 requestMock.getParameter("skuId") >> "sk123456"
		 requestMock.getParameter("siteId") >> "USBedbath"
		 requestMock.getLocale() >> local
		 // getting list of shipping methods
		 catalogToolsMock.getLTLEligibleShippingMethods("sk123456", "USBedbath", "en") >> []
		 when:
		 aShMethodDropletObject.service(requestMock, responseMock)
		 then:
         1*requestMock.serviceParameter("empty", requestMock, responseMock);	}
	
	
	def"service. TC for error as open parameter when sku and site id is empty " (){
		given:
		 requestMock.getParameter("skuId") >> null
		 requestMock.getParameter("siteId") >> null
		 requestMock.getLocale() >> local
		 // getting list of shipping methods
		 when:
		 aShMethodDropletObject.service(requestMock, responseMock)
		 then:
		 1*requestMock.serviceParameter("error", requestMock, responseMock);
		 1*requestMock.setParameter("shipMethodVOList", null)
		 
		 	}
	def"service. TC for error as open parameter when sku id is empty " (){
		given:
		 requestMock.getParameter("skuId") >> null
		 requestMock.getParameter("siteId") >> "USBedbath"
		 requestMock.getLocale() >> local
		 // getting list of shipping methods
		 when:
		 aShMethodDropletObject.service(requestMock, responseMock)
		 then:
		 1*requestMock.serviceParameter("error", requestMock, responseMock);
		 1*requestMock.setParameter("shipMethodVOList", null)
		 
			 }
	
	def"service. TC for error as open parameter when site id is empty " (){
		given:
		 requestMock.getParameter("skuId") >> "sk123456"
		 requestMock.getParameter("siteId") >> null
		 requestMock.getLocale() >> local
		 // getting list of shipping methods
		 when:
		 aShMethodDropletObject.service(requestMock, responseMock)
		 then:
		 1*requestMock.serviceParameter("error", requestMock, responseMock);
		 1*requestMock.setParameter("shipMethodVOList", null)
		 
			 }
	
	def"service. TC for BBBSystemException while getting eligible the shipping methods" (){
		given:
		 requestMock.getParameter("skuId") >> "sk123456"
		 requestMock.getParameter("siteId") >> "USBedbath"
		 requestMock.getLocale() >> local
		 // getting list of shipping methods
		 catalogToolsMock.getLTLEligibleShippingMethods("sk123456", "USBedbath", "en") >> {throw new BBBSystemException("systemException") }
		 when:
		 aShMethodDropletObject.service(requestMock, responseMock)
		 then:
		 1*requestMock.serviceParameter("error", requestMock, responseMock);
		 
	
}
	def"service. TC for BBBBusinessException while getting eligible the shipping methods" (){
		given:
		 requestMock.getParameter("skuId") >> "sk123456"
		 requestMock.getParameter("siteId") >> "USBedbath"
		 requestMock.getLocale() >> local
		 // getting list of shipping methods
		 catalogToolsMock.getLTLEligibleShippingMethods("sk123456", "USBedbath", "en") >> {throw new BBBBusinessException("systemException") }
		 when:
		 aShMethodDropletObject.service(requestMock, responseMock)
		 then:
		 1*requestMock.serviceParameter("error", requestMock, responseMock);
	
}
	
}
