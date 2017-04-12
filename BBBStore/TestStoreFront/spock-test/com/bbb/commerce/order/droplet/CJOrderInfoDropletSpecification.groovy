package com.bbb.commerce.order.droplet

import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem
import atg.commerce.order.OrderImpl;
import atg.commerce.pricing.ItemPriceInfo
import atg.repository.RepositoryItem
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.commerce.pricing.bean.PriceInfoVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.NonMerchandiseCommerceItem
import spock.lang.specification.BBBExtendedSpec

class CJOrderInfoDropletSpecification extends BBBExtendedSpec {
   def CJOrderInfoDroplet cjoInfoDroplet 
   def BBBOrderImpl orderMock = Mock()
   def BBBCommerceItem commerceItemMock1 = Mock()
   def BBBCommerceItem commerceItemMock2 = Mock()
   def AuxiliaryData auxiliaryDataMock = Mock()
   def AuxiliaryData auxiliaryDataMock1 = Mock()
   def NonMerchandiseCommerceItem NMCommerceItem = Mock()
   def RepositoryItem productItem = Mock()
   def RepositoryItem productItem1 = Mock()
   
   def ItemPriceInfo itemPInfoMock = Mock()
   def BBBPricingTools pricingToolsMock = Mock()
   PriceInfoVO priceInfo =  new PriceInfoVO()
   def BBBCatalogTools CatalogToolsMock = Mock()
   
   def setup(){
	  cjoInfoDroplet  = new CJOrderInfoDroplet(pricingTools :pricingToolsMock,catalogTools:CatalogToolsMock )
   }
   
   def "service. TC to add order details in request object" (){
	   given:
	   cjoInfoDroplet = Spy()
	   cjoInfoDroplet.setPricingTools(pricingToolsMock)
	   
	   requestMock.getObjectParameter("order") >> orderMock 
	   String siteId = "USBed"
	   String skuId = "sku1"
	   String skuId2 = "sku2"
	   String productId = "pro123"
	   String prodId2 = "prod2"
	   
	   //requestMock.getParameter("siteId") >> siteId
	   cjoInfoDroplet.extractGetSiteId() >> siteId
	   orderMock.getId() >> "or123"
	   2*orderMock.getCommerceItems() >> [commerceItemMock1,commerceItemMock2]
	   
	   // for commerce Item 1
	   commerceItemMock1.getCatalogRefId() >> skuId
	   commerceItemMock1.getQuantity() >> 2
	   commerceItemMock1.getId() >> "c123"
	   commerceItemMock1.getAuxiliaryData() >> auxiliaryDataMock
	   auxiliaryDataMock.getCatalogRef() >> productItem
	   productItem.getPropertyValue("displayName") >> "pro12"
	  
	   auxiliaryDataMock.getProductId() >> productId
	   commerceItemMock1.getPriceInfo() >> itemPInfoMock
	   
	   // for commerce item 2
	   commerceItemMock2.getCatalogRefId() >> skuId2
	   commerceItemMock2.getQuantity() >> 2
	   commerceItemMock2.getId() >> "c124"
	   2*commerceItemMock2.getAuxiliaryData() >> auxiliaryDataMock1
	   auxiliaryDataMock1.getCatalogRef() >> productItem1
	   productItem1.getPropertyValue("displayName") >> "pro1234"
	  
	   auxiliaryDataMock1.getProductId() >> prodId2
	   commerceItemMock2.getPriceInfo() >> itemPInfoMock

	   4 * itemPInfoMock.getSalePrice() >> 20
	      itemPInfoMock.getAmount() >> 19
	  
	  1* pricingToolsMock.getOrderPriceInfo(_) >> priceInfo
	  
	   when:
	   cjoInfoDroplet.service(requestMock, responseMock)
	   then:
	   1 * requestMock.setParameter("cjItemUrl", ["ITEM1="+skuId+"&AMT1=9.5&QTY1=2","ITEM2="+skuId2+"&AMT2=9.5&QTY2=2"])
	   1 * requestMock.setParameter("cjSkuIds", skuId+"|"+skuId2)
	   1 * requestMock.setParameter("cjSkuPrices", "9.5|9.5")
	   1 * requestMock.setParameter("cjSkuQty", "2|2")
	   1 * requestMock.setParameter("cjBopusOnly", "false")
	   1 * requestMock.serviceParameter("output", requestMock, responseMock);
	   1 * requestMock.setParameter("priceInfoVO",priceInfo);

   }
	
   def "service. TC to add order details in request object when salePrice of commerce item is 0" (){
	   given:
	   requestMock.getObjectParameter("order") >> orderMock
	   String siteId = "USBed"
	   String skuId = "sku1"
	   String productId = "pro123"
	   
	   requestMock.getParameter("siteId") >> siteId
	   orderMock.getId() >> "or123"
	   2*orderMock.getCommerceItems() >> [commerceItemMock1]
	   // for commerce Item 1
	   commerceItemMock1.getCatalogRefId() >> skuId
	   commerceItemMock1.getQuantity() >> 4
	   commerceItemMock1.getId() >> "c123"
	   commerceItemMock1.getAuxiliaryData() >> auxiliaryDataMock
	   auxiliaryDataMock.getCatalogRef() >> productItem
	   productItem.getPropertyValue("displayName") >> "pro12"
	  
	   auxiliaryDataMock.getProductId() >> productId
	   commerceItemMock1.getPriceInfo() >> itemPInfoMock
	   
	   itemPInfoMock.getSalePrice() >> 0
	   3*itemPInfoMock.getAmount() >> 19
	   1* itemPInfoMock.getListPrice() >> 11
	  
	   1*pricingToolsMock.getOrderPriceInfo(_) >> priceInfo
	  
	   when:
	   cjoInfoDroplet.service(requestMock, responseMock)
	   then:
	   1 * requestMock.setParameter("cjItemUrl", ["ITEM1="+skuId+"&AMT1=4.75&QTY1=4"])
	   1 * requestMock.setParameter("cjSkuIds", skuId)
	   1 * requestMock.setParameter("cjSkuPrices", "4.75")
	   1 * requestMock.setParameter("cjSkuQty", "4")
	   1 * requestMock.setParameter("cjBopusOnly", "false")
	   1 * requestMock.serviceParameter("output", requestMock, responseMock);
	   1 * requestMock.setParameter("priceInfoVO",priceInfo);

   }
   
   def "service. TC to add order details in request object when PriceInfo object of commerce item is null  " (){
	   given:
	   requestMock.getObjectParameter("order") >> orderMock
	   String siteId = "USBed"
	   String skuId = "sku1"
	   String productId = "pro123"
	   
	   requestMock.getParameter("siteId") >> siteId
	   orderMock.getId() >> "or123"
	   2*orderMock.getCommerceItems() >> [commerceItemMock1]
	   // for commerce Item 1
	   commerceItemMock1.getCatalogRefId() >> skuId
	   commerceItemMock1.getQuantity() >> 4
	   commerceItemMock1.getId() >> "c123"
	   commerceItemMock1.getAuxiliaryData() >> auxiliaryDataMock
	   auxiliaryDataMock.getCatalogRef() >> productItem
	   productItem.getPropertyValue("displayName") >> "pro12"
	  
	   auxiliaryDataMock.getProductId() >> productId
	   commerceItemMock1.getPriceInfo() >> null
	   
	  
	  1* pricingToolsMock.getOrderPriceInfo(_) >> priceInfo
	  
	   when:
	   cjoInfoDroplet.service(requestMock, responseMock)
	   then:
	   1 * requestMock.setParameter("cjItemUrl", ["ITEM1="+skuId+"&AMT1=&QTY1=4"])
	   1 * requestMock.setParameter("cjSkuIds", skuId)
	   1 * requestMock.setParameter("cjSkuPrices", "")
	   1 * requestMock.setParameter("cjSkuQty", "4")
	   1 * requestMock.setParameter("cjBopusOnly", "false")
	   1 * requestMock.serviceParameter("output", requestMock, responseMock);
	   1 * requestMock.setParameter("priceInfoVO",priceInfo);
	   0 * itemPInfoMock.getAmount()
   }
   
   def "service. TC for cjBopusOnly when store id of commerce item is not null" (){
	   given:
	   requestMock.getObjectParameter("order") >> orderMock
	   String siteId = "USBed"
	   
	   requestMock.getParameter("siteId") >> siteId
	   orderMock.getId() >> "or123"
	   orderMock.getCommerceItems() >> [commerceItemMock1]
	   commerceItemMock1.getStoreId() >> "storeId"
	  
	  1* pricingToolsMock.getOrderPriceInfo(_) >> priceInfo
	  
	   when:
	   cjoInfoDroplet.service(requestMock, responseMock)
	   then:
       1* requestMock.setParameter("cjBopusOnly", "true") 
	   1 * requestMock.setParameter("priceInfoVO",priceInfo)
	   
   }
   
   def "service. TC for cjBopusOnly when commerce item type is NonMerchandiseCommerceItem" (){
	   given:
	   requestMock.getObjectParameter("order") >> orderMock
	   String siteId = "USBed"
	   
	   requestMock.getParameter("siteId") >> siteId
	   orderMock.getId() >> "or123"
	   orderMock.getCommerceItems() >> [NMCommerceItem]
	  
	   1*pricingToolsMock.getOrderPriceInfo(_) >> priceInfo
	  
	   when:
	   cjoInfoDroplet.service(requestMock, responseMock)
	   then:
	   1* requestMock.setParameter("cjBopusOnly", "true")
	   1 * requestMock.setParameter("priceInfoVO",priceInfo)
	   
   }
   
   def "service. TC when order is null" (){
	   given:
	   requestMock.getObjectParameter("order") >> null
	  
	   when:
	   cjoInfoDroplet.service(requestMock, responseMock)
	   then:
	   0* requestMock.setParameter("cjBopusOnly", "true")
	   0 * requestMock.setParameter("priceInfoVO",priceInfo)
	   0 * requestMock.serviceParameter("output", requestMock, responseMock);
	   
   }
   
}
