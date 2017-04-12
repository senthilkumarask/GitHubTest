package com.bbb.commerce.order.droplet

import com.bbb.constants.BBBCoreConstants;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem

import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem
import atg.commerce.order.Order
import spock.lang.specification.BBBExtendedSpec;

class LTLCustomItemExclusionDropletSpecification extends BBBExtendedSpec {
   def LTLCustomItemExclusionDroplet lcieDroplet
   def Order order = Mock()
   def CommerceItem commerceItem1 = Mock()
   def CommerceItem commerceItem2 = Mock()
   def AuxiliaryData auxiliaryData1 = Mock()
   def AuxiliaryData auxiliaryData2 = Mock()
   def LTLAssemblyFeeCommerceItem ltlFreeItem = Mock()
   def LTLDeliveryChargeCommerceItem ltldChargeItem = Mock()
   
   
   def setup(){
	  lcieDroplet  = new LTLCustomItemExclusionDroplet()
   }
   
   def"service . TC to check product id list "(){
	   given:
	   requestMock.getObjectParameter("order") >> order
	   1*order.getCommerceItems() >> [commerceItem1, commerceItem2, ltlFreeItem, ltldChargeItem]
	   
	   1*commerceItem1.getAuxiliaryData() >> auxiliaryData1
	   1*commerceItem2.getAuxiliaryData() >> auxiliaryData2
	   
	   auxiliaryData1.getProductId() >> "p1"
	   auxiliaryData2.getProductId() >> "p2"
	   
	   commerceItem1.getCatalogRefId() >> "sku1"
	   commerceItem2.getCatalogRefId() >> "sku2"
	   when:
	   lcieDroplet.service(requestMock, responseMock)
	   then:
	   1*requestMock.setParameter("commerceItemList", ";p1;;;;eVar30=sku1,;p2;;;;eVar30=sku2")
	   1* requestMock.serviceLocalParameter("output", requestMock, responseMock)
   }
   
   def"service . TC when comerce item list is null "(){
	   given:
	   requestMock.getObjectParameter("order") >> order
	   1*order.getCommerceItems() >> null
	   
	   when:
	   lcieDroplet.service(requestMock, responseMock)
	   then:
	   1*requestMock.setParameter("commerceItemList", "")
	   1* requestMock.serviceLocalParameter("output", requestMock, responseMock)
   }
   
   def"service . TC when order is null "(){
	   given:
	   requestMock.getObjectParameter("order") >> null
	   
	   when:
	   lcieDroplet.service(requestMock, responseMock)
	   then:
	   1*requestMock.setParameter("commerceItemList", "")
	   1* requestMock.serviceLocalParameter("output", requestMock, responseMock)
   }
}
