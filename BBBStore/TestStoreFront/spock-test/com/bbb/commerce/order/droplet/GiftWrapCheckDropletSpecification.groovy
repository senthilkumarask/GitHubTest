package com.bbb.commerce.order.droplet

import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.ElectronicShippingGroup
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.Order
import atg.repository.RepositoryItem
import atg.core.util.Address;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.GiftWrapVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.NonMerchandiseCommerceItem

import spock.lang.specification.BBBExtendedSpec

class GiftWrapCheckDropletSpecification extends BBBExtendedSpec {
    def GiftWrapCheckDroplet  gwcDropletObject
	def Order order = Mock()
	def BBBHardGoodShippingGroup hgsgMock = Mock()
	def BBBHardGoodShippingGroup hgsgMock1 = Mock()
	def CommerceItemRelationship itemRelationship = Mock()
	def CommerceItemRelationship itemRelationship1 = Mock()
	def CommerceItemRelationship itemRelationship2 = Mock()
	def CommerceItemRelationship itemRelationship3 = Mock()
	def CommerceItemRelationship itemRelationship4 = Mock()
	
	
	def BBBCatalogTools catalogToolsMock = Mock()
	def CommerceItem commerceItem = Mock()
	def CommerceItem commerceItem1 = Mock()
	def CommerceItem commerceItem2 = Mock()
	def CommerceItem commerceItem3 = Mock()
	def CommerceItem commerceItem4 = Mock()
	NonMerchandiseCommerceItem MerchCommerceItem = Mock()
	
	
	def RepositoryItem shippingMethodItem = Mock()
	def ElectronicShippingGroup elsg = Mock()
	GiftWrapVO giftWrapVO =  new GiftWrapVO()
	SKUDetailVO skuDetail = new SKUDetailVO()
	Address address = new Address()
	
	def setup(){
		gwcDropletObject = new GiftWrapCheckDroplet(BBBCatalogTools :catalogToolsMock )
   }
	
	def"Service. TC  when gift wrape option  milti "(){
		given:
		
		 String siteId = "usBed"
		 String skuId = "sku123"
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 requestMock.getObjectParameter("siteId") >> "usBed"
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock,hgsgMock, elsg]
		 1*catalogToolsMock.getWrapSkuDetails(siteId) >> giftWrapVO
		 2*hgsgMock.getCommerceItemRelationships() >> [itemRelationship, itemRelationship3, itemRelationship1, itemRelationship2]
		 
		 itemRelationship.getCommerceItem() >> commerceItem
		 itemRelationship1.getCommerceItem() >> MerchCommerceItem
		 itemRelationship2.getCommerceItem() >> null
		 itemRelationship3.getCommerceItem() >> commerceItem3
		 
		 
		 commerceItem.getCatalogRefId() >> skuId
		 commerceItem3.getCatalogRefId() >> "sku3"
		 commerceItem.getId() >> "commerce123"
		 commerceItem3.getId() >> "commerce3"
		 
		 itemRelationship.getQuantity() >> 2
		 itemRelationship3.getQuantity() >> 2
		 
		 2*catalogToolsMock.isGiftWrapItem(siteId, skuId) >> false
		 2*catalogToolsMock.isGiftWrapItem(siteId, "sku3") >> false
		 
		 
		 2*catalogToolsMock.getSKUDetails(siteId, skuId , false, true, true) >>  skuDetail
		 2*catalogToolsMock.getSKUDetails(siteId, "sku3" , false, true, true) >>  skuDetail
		 
		 skuDetail.setDisplayName("displayName")
		 hgsgMock.getShippingMethod() >> "shippingMethod"
		 
		 2*catalogToolsMock.getShippingMethod("shippingMethod") >> shippingMethodItem
		 shippingMethodItem.getPropertyValue("shipMethodDescription") >> "same day delivery"
		 
		 hgsgMock.getShippingAddress() >> address
		 
		  hgsgMock.getGiftWrapInd() >> true
		  hgsgMock.containsGiftWrap() >> true
		  hgsgMock.getId() >> "sid"
		  hgsgMock.getGiftWrapMessage() >> "to harry"
		   
		 
		when:
		gwcDropletObject.service(requestMock, responseMock)
		
		then:
		
		2*requestMock.setParameter("shippingMethodDescription", "same day delivery")
		2*requestMock.setParameter("shipGroupGiftInd", true )
		2*requestMock.setParameter("giftItemIndicator", true )
		2*requestMock.setParameter("giftWrapFlag" , false)
		2*requestMock.setParameter("shipGroupParam",hgsgMock)
		2*requestMock.setParameter("shipAddress",address)
		2*requestMock.setParameter("shippingMethod","shippingMethod")
		2*requestMock.setParameter("giftWrapMap",["commerce3":'2',"commerce123":'2'])
		2*requestMock.setParameter("commItemList",[commerceItem, commerceItem3])
		2*requestMock.setParameter("shipGroupId",  "sid")
		2*requestMock.setParameter("shipGroupGiftMessage", "to harry")
		2 * requestMock.setParameter('nonGiftWrapSkus', _)
		2*requestMock.serviceParameter("output",requestMock, responseMock);


	}
	
	def"Service. TC when gift wrape option  milti and  shipMethodDescription is empty "(){
		given:
		
		 String siteId = "usBed"
		 String skuId = "sku123"
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 requestMock.getObjectParameter("siteId") >> "usBed"
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock, hgsgMock]
		 1*catalogToolsMock.getWrapSkuDetails(siteId) >> null
		 2*hgsgMock.getCommerceItemRelationships() >> [itemRelationship, itemRelationship]
		 
		 itemRelationship.getCommerceItem() >> commerceItem
		 commerceItem.getCatalogRefId() >> skuId
		 commerceItem.getId() >> "commerce123"
		 itemRelationship.getQuantity() >> 2
		 catalogToolsMock.isGiftWrapItem(siteId, skuId) >> false
		 
		 catalogToolsMock.getSKUDetails(siteId, skuId , false, true, true) >>  null
		 skuDetail.setDisplayName("displayName")
		 hgsgMock.getShippingMethod() >> "shippingMethod"
		 
		 2*catalogToolsMock.getShippingMethod("shippingMethod") >> shippingMethodItem
		 shippingMethodItem.getPropertyValue("shipMethodDescription") >> ""
		 
		 2*hgsgMock.getShippingAddress() >> address
		 
		  hgsgMock.getGiftWrapInd() >> true
		  hgsgMock.containsGiftWrap() >> true
		  hgsgMock.getId() >> "sid"
		  hgsgMock.getGiftWrapMessage() >> "to harry"
		   
		 
		when:
		gwcDropletObject.service(requestMock, responseMock)
		
		then:
		
		0*requestMock.setParameter("shippingMethodDescription", "")
		2*requestMock.setParameter("shipGroupGiftInd", true )
		2*requestMock.setParameter("giftItemIndicator", true )
		2*requestMock.setParameter("giftWrapFlag" , false)
		2*requestMock.setParameter("shipGroupParam",hgsgMock)
		2*requestMock.setParameter("shipAddress",address)
		2*requestMock.setParameter("shippingMethod","shippingMethod")
		2*requestMock.setParameter("giftWrapMap",["commerce123":'2'])
		2*requestMock.setParameter("commItemList",[commerceItem, commerceItem])
		2*requestMock.setParameter("shipGroupId",  "sid")
		2*requestMock.setParameter("shipGroupGiftMessage", "to harry")
		2 * requestMock.setParameter('nonGiftWrapSkus', _)
		2*requestMock.serviceParameter("output",requestMock, responseMock);


	}
	
	def"Service. TC when gift wrape option  milti and  shipping method is empty "(){
		given:
		
		 String siteId = "usBed"
		 String skuId = "sku123"
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 requestMock.getObjectParameter("siteId") >> "usBed"
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock]
		 1*catalogToolsMock.getWrapSkuDetails(siteId) >> giftWrapVO
		 1*hgsgMock.getCommerceItemRelationships() >> [itemRelationship, itemRelationship]
		 
		 itemRelationship.getCommerceItem() >> commerceItem
		 commerceItem.getCatalogRefId() >> skuId
		 commerceItem.getId() >> "commerce123"
		 itemRelationship.getQuantity() >> 2
		 catalogToolsMock.isGiftWrapItem(siteId, skuId) >> true
		 
		 hgsgMock.getShippingMethod() >> ""
		 
		 
		 hgsgMock.getShippingAddress() >> address
		 
		  hgsgMock.getGiftWrapInd() >> true
		  hgsgMock.containsGiftWrap() >> true
		  hgsgMock.getId() >> "sid"
		  hgsgMock.getGiftWrapMessage() >> "to harry"
		  
		  giftWrapVO.setWrapSkuPrice(12) 
		 
		when:
		gwcDropletObject.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("giftWrapFlag" , true)
		1*requestMock.setParameter("giftWrapPrice",12.0)
		1*requestMock.serviceParameter("output",requestMock, responseMock);

	}
	
	def"Service. TC when gift wrape option  milti and  groupRequired is true and gift wrap vo is null "(){
		given:
		
		 String siteId = "usBed"
		 String skuId = "sku123"
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 requestMock.getObjectParameter("siteId") >> "usBed"
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock]
		 1*catalogToolsMock.getWrapSkuDetails(siteId) >> null
		 1*hgsgMock.getCommerceItemRelationships() >> [itemRelationship]
		 
		 itemRelationship.getCommerceItem() >> commerceItem
		 commerceItem.getCatalogRefId() >> skuId
		 commerceItem.getId() >> "commerce123"
		 itemRelationship.getQuantity() >> 2
		 catalogToolsMock.isGiftWrapItem(siteId, skuId) >> true
		 
		 hgsgMock.getShippingMethod() >> ""
		 
		 
		 hgsgMock.getShippingAddress() >> address
		 
		  hgsgMock.getGiftWrapInd() >> true
		  hgsgMock.containsGiftWrap() >> true
		  hgsgMock.getId() >> "sid"
		  hgsgMock.getGiftWrapMessage() >> "to harry"
		  
		  giftWrapVO.getWrapSkuPrice() >> 12
		 
		when:
		gwcDropletObject.service(requestMock, responseMock)
		
		then:
		
		1*requestMock.setParameter("giftWrapFlag" , true)
		0*requestMock.setParameter("giftWrapPrice",12.0)
		1*requestMock.serviceParameter("output",requestMock, responseMock);

	}
		

	
	def"Service. TC when gift wrape option  milti and  shipping group having empty commerce items"(){
		given:
		
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> []
				   
		when:
		gwcDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("empty" , requestMock, responseMock)
		
	}
	
	def"Service. TC when gift wrape option  milti and  shipping group having zero commerce items count"(){
		given:
		
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 order.getCommerceItemCount() >> 0
		 order.getShippingGroups() >> []
				   
		when:
		gwcDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("empty" , requestMock, responseMock)
		
	}
	def"Service. TC when gift wrape option  milti and order is null"(){
		given:
		
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> null
				   
		when:
		gwcDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("empty" , requestMock, responseMock)
		
	}

	def"Service. TC for  BBBBusinessException "(){
		given:
		NonMerchandiseCommerceItem commerceItem = Mock()
		
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 requestMock.getObjectParameter("siteId") >> "usBed"
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock, hgsgMock]
		 hgsgMock.getCommerceItemRelationships() >> [itemRelationship, itemRelationship]
		 
		 itemRelationship.getCommerceItem() >> commerceItem
				  
		 hgsgMock.getShippingMethod() >> "shippingMethod"
		 hgsgMock.getShippingAddress() >> address
		 catalogToolsMock.getShippingMethod("shippingMethod") >> {throw new BBBBusinessException("exception") }
		 
		  hgsgMock.getGiftWrapInd() >> true
		  hgsgMock.containsGiftWrap() >> true
		  hgsgMock.getId() >> "sid"
		  hgsgMock.getGiftWrapMessage() >> "to harry"
				   
		expect:
		gwcDropletObject.service(requestMock, responseMock)
		
	}
	
	def"Service. TC for  BBBSystemException "(){
		given:
		NonMerchandiseCommerceItem commerceItem = Mock()
		
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 requestMock.getObjectParameter("siteId") >> "usBed"
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock, hgsgMock]
		 hgsgMock.getCommerceItemRelationships() >> [itemRelationship, itemRelationship]
		 
		 itemRelationship.getCommerceItem() >> commerceItem
				  
		 hgsgMock.getShippingMethod() >> "shippingMethod"
		 hgsgMock.getShippingAddress() >> address
		 catalogToolsMock.getShippingMethod("shippingMethod") >> {throw new BBBSystemException("exception") }
		 
		  hgsgMock.getGiftWrapInd() >> true
		  hgsgMock.containsGiftWrap() >> true
		  hgsgMock.getId() >> "sid"
		  hgsgMock.getGiftWrapMessage() >> "to harry"
				   
		expect:
		gwcDropletObject.service(requestMock, responseMock)
		
	}
	
	def"Service. TC for  BBBBusinessException while getting skuDetails "(){
		given:
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock, hgsgMock]
		 
         catalogToolsMock.getWrapSkuDetails(_) >> {throw new BBBBusinessException("exception") }				   
		 expect:
		 gwcDropletObject.service(requestMock, responseMock)
		
	}
	
	def"Service. TC for  BBBSystemException while getting skuDetails "(){
		given:
		 requestMock.getObjectParameter("giftWrapOption") >> "multi"
		 requestMock.getObjectParameter("order") >> order
		 order.getCommerceItemCount() >> 2
		 order.getShippingGroups() >> [hgsgMock, hgsgMock]
		 
         catalogToolsMock.getWrapSkuDetails(_) >> {throw new BBBSystemException("exception") }				   
		expect:
		 gwcDropletObject.service(requestMock, responseMock)
		
	}
	
	/************************************************* single shipping group gift wrap **********************************/
	def"service . TC to check  Single shipping group gift wrap"(){
	given:
	    String skuId = "sku123"
	    def NonMerchandiseCommerceItem MCcommerceItem = Mock()
		requestMock.getObjectParameter("giftWrapOption") >> "nonmulti"
		requestMock.getObjectParameter("order") >> order
		requestMock.getObjectParameter("shippingGroup") >> hgsgMock
		requestMock.getObjectParameter("siteId") >> "usBed"
		1*hgsgMock.getCommerceItemRelationships() >> [itemRelationship, itemRelationship1 ,null ,itemRelationship2, itemRelationship3 ]
		itemRelationship1.getCommerceItem() >> MCcommerceItem
		itemRelationship.getCommerceItem() >> commerceItem
		
		itemRelationship2.getCommerceItem() >> commerceItem1
		itemRelationship3.getCommerceItem() >> commerceItem3
		itemRelationship4.getCommerceItem() >> commerceItem4
		
		
		commerceItem.getCatalogRefId() >> skuId
		commerceItem1.getCatalogRefId() >> "sku1"
		commerceItem3.getCatalogRefId() >> "sku3"
		commerceItem4.getCatalogRefId() >> "sku4"
		
		itemRelationship.getQuantity() >> 2
		
		1*catalogToolsMock.isGiftWrapItem("usBed", skuId) >> false
		1*catalogToolsMock.isGiftWrapItem("usBed", "sku1") >> true
		1*catalogToolsMock.isGiftWrapItem("usBed", "sku3") >> true
		
		
		3*catalogToolsMock.getWrapSkuDetails("usBed") >> giftWrapVO >> giftWrapVO >> null 
		giftWrapVO.setWrapSkuPrice(12)
		
		1*catalogToolsMock.getSKUDetails("usBed", skuId , false, true, true) >>  skuDetail
		
		1*catalogToolsMock.getSKUDetails("usBed", "sku3" , false, true, true) >>  skuDetail
		skuDetail.setDisplayName("display")
		
		when:
		gwcDropletObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("giftWrapFlag", true)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		1*requestMock.setParameter("giftWrapPrice", 12.0)
		1*requestMock.setParameter("nonGiftWrapSkus","display,display")
		1*requestMock.serviceParameter("empty", requestMock, responseMock)

	}
	
	def "service. TC for exception scenario" (){
		given:
		requestMock.getObjectParameter("giftWrapOption") >> "nonmulti"
		requestMock.getObjectParameter("order") >> order
		requestMock.getObjectParameter("shippingGroup") >> hgsgMock
		requestMock.getObjectParameter("siteId") >> "usBed"
		hgsgMock.getCommerceItemRelationships() >> [itemRelationship , itemRelationship1 ,  itemRelationship2]
		itemRelationship.getCommerceItem() >> commerceItem
		itemRelationship1.getCommerceItem() >> commerceItem1
		itemRelationship2.getCommerceItem() >> commerceItem2
		
		commerceItem.getCatalogRefId() >> "sku1"
		commerceItem1.getCatalogRefId() >> "sku2"
		commerceItem2.getCatalogRefId() >> "sku3"
		
		
		1*catalogToolsMock.getSKUDetails("usBed", "sku1" , false, true, true) >>  null
		1*catalogToolsMock.getSKUDetails("usBed", "sku2" , false, true, true) >>  {throw new BBBSystemException("exception")}
		1*catalogToolsMock.getSKUDetails("usBed", "sku3" , false, true, true) >>  {throw new BBBBusinessException("exception")}
		
		when:
		gwcDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("giftWrapFlag", false)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		1*requestMock.setParameter("giftWrapPrice", 0.0)
		1*requestMock.setParameter("nonGiftWrapSkus", "")

	}
	
	def "Service . TC when shipping group type is not BBBHardGoodShippingGroup" (){
		given:
		requestMock.getObjectParameter("giftWrapOption") >> "nonmulti"
		requestMock.getObjectParameter("order") >> order
		requestMock.getObjectParameter("shippingGroup") >> elsg
		requestMock.getObjectParameter("siteId") >> null

		when:
		gwcDropletObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.serviceParameter("empty", requestMock, responseMock)
		
	} 
	def "Service . TC when site is null" (){
		given:
		requestMock.getObjectParameter("giftWrapOption") >> "nonmulti"
		requestMock.getObjectParameter("order") >> order
		requestMock.getObjectParameter("shippingGroup") >> hgsgMock
		requestMock.getObjectParameter("siteId") >> null

		when:
		gwcDropletObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.serviceParameter("empty", requestMock, responseMock)
		
	}

}
