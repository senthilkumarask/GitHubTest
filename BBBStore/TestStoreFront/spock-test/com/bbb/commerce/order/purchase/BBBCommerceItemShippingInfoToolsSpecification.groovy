package com.bbb.commerce.order.purchase

import atg.commerce.CommerceException
import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemManager
import atg.commerce.order.CommerceItemNotFoundException
import atg.commerce.order.Order
import atg.commerce.order.ShippingGroup
import atg.commerce.order.purchase.CommerceItemShippingInfo
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer
import atg.commerce.order.purchase.PurchaseProcessHelper
import atg.commerce.order.purchase.ShippingGroupContainerService
import atg.commerce.order.purchase.ShippingGroupMapContainer
import atg.repository.RepositoryException

import com.bbb.commerce.order.BBBCommerceItemManager
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.order.bean.BBBCommerceItem
import atg.commerce.order.InvalidParameterException;
import spock.lang.specification.BBBExtendedSpec

class BBBCommerceItemShippingInfoToolsSpecification extends BBBExtendedSpec {

	def BBBCommerceItemShippingInfoTools cisiTools
	def CommerceItemShippingInfoContainer itemShippingContainer = Mock()
	def CommerceItemShippingInfo itemShippingObject = Mock()
	def CommerceItemShippingInfo DeliveryItemInfo = Mock()
	def CommerceItemShippingInfo AssemblyItemInfo = Mock()
	
	
	def PurchaseProcessHelper ppHelperMock = Mock()
	def CommerceItem commItem = Mock()
	def CommerceItem commItem1 = Mock()
	def CommerceItem commItem2 = Mock()
	def CommerceItem commItem3 = Mock()
	def CommerceItem commItem4 = Mock()
	
	def BBBCommerceItem currentItem = Mock()
	def BBBCommerceItem newItem = Mock()
	
	def ShippingGroup sg = Mock()
	def Order order = Mock()
	def AuxiliaryData auxiliaryData = Mock()
	def BBBOrderTools orderToolsMock = Mock()
	
	def BBBCommerceItemManager ciManagerMock = Mock()
	
	def BBBShippingGroupCommerceItemRelationship sgciRelationship = Mock()
	
	CommerceItemShippingInfo itemShippingInfo = new CommerceItemShippingInfo()
	
	def setup(){
		cisiTools = new BBBCommerceItemShippingInfoTools(commerceItemManager : ciManagerMock, orderTools : orderToolsMock,purchaseProcessHelper : ppHelperMock)
	}
	
	def "splitCommerceItemShippingInfoByQuantity. TC when splited quantity is greater then item quantity"(){
		given:
		itemShippingObject.getQuantity() >> 3
		when:
		cisiTools.splitCommerceItemShippingInfoByQuantity(itemShippingContainer, itemShippingObject ,4)
		then:
		CommerceException exception = thrown()
	}
	
	def "splitCommerceItemShippingInfoByQuantity. TC when splited quantity is less then item quantity"(){
		given:
		cisiTools = Spy()
		1*itemShippingObject.getQuantity() >> 4
		1*cisiTools.callSuperSplitCommerceItemShippingInfoByQuantity(itemShippingContainer, itemShippingObject,3) >> itemShippingInfo
		1*itemShippingObject.getShippingGroupName() >> "shipping"
		cisiTools.isLoggingDebug() >> true
		itemShippingObject.getCommerceItem() >> commItem
		commItem.getId() >> "id"
		when:
		CommerceItemShippingInfo value =  cisiTools.splitCommerceItemShippingInfoByQuantity(itemShippingContainer, itemShippingObject ,3)
		then:
		value.getShippingGroupName().equals("shipping")
	}
	
	/***********************************************splitCommerceItemShippingInfoForLTLByQuantity *************************************/
	
	def "splitCommerceItemShippingInfoForLTLByQuantity. TC for split LTL items "(){
		given:
		String siteId = addCommonDetails()
		
		currentItem.getWhiteGloveAssembly() >> "true"
		order.getShippingGroup("sg1") >> sg
		order.getSiteId() >> siteId
		1*ciManagerMock.addLTLAssemblyFeeSku(order, sg, siteId, newItem) >> "newAsscomdId"
		1*ciManagerMock.addLTLDeliveryChargeSku(order, sg, siteId, newItem) >> "newDeliveryId"
		itemShippingObject.getQuantity() >> 10
		
		////createNewInfoForBBBCommerceItem ////
		1*itemShippingObject.getShippingMethod() >> "sdd"
		1*itemShippingObject.getSplitShippingGroupName() >> "ssgg"
		1*itemShippingObject.getShippingGroupName() >> "hard"
		
		////createNewInforForDeliveryItem
		
		//itemShippingContainer.addCommerceItemShippingInfo("deliveryItemId", DeliveryItemInfo) 
		itemShippingContainer.getCommerceItemShippingInfos("deliveryItemId") >> [DeliveryItemInfo]
		2*DeliveryItemInfo.getCommerceItem() >>  commItem2
		DeliveryItemInfo.getShippingGroupName() >> "s1"
		1*DeliveryItemInfo.getShippingMethod() >> "ssde"
		1*order.getCommerceItem(_) >> commItem
		commItem.getId() >> "c1"
		
		// createNewInfoForAssemblyItem
		//itemShippingContainer.addCommerceItemShippingInfo("assemblyItemId", AssemblyItemInfo)
		itemShippingContainer.getCommerceItemShippingInfos("assemblyItemId") >> [AssemblyItemInfo]
		
		
		2*AssemblyItemInfo.getCommerceItem() >>  commItem3
		AssemblyItemInfo.getShippingGroupName() >> "s1"
		1*AssemblyItemInfo.getShippingMethod() >> "ssde"
		1*order.getCommerceItem(_) >> commItem4
		
		commItem4.getId() >> "c2"

		cisiTools.adjustHandlingInstructionsForSplit(_, _, 4) >> {}
		
		//copyCommerceItemProperties
         setItemDetails()		
		when:
		CommerceItemShippingInfo value = cisiTools.splitCommerceItemShippingInfoForLTLByQuantity(itemShippingContainer, itemShippingObject ,4 ,order) 
		then:
			value != null 
			value.getCommerceItem() == newItem
			value.getShippingMethod() == "sdd"
			value.getShippingGroupName() == "hard"
			value.getQuantity() == 4
			1*itemShippingObject.setQuantity(_)
			1*itemShippingObject.setSplitShippingGroupName(null)
			1*itemShippingObject.setSplitQuantity(0L)
			1*DeliveryItemInfo.setQuantity(_);
			1*DeliveryItemInfo.setSplitShippingGroupName(null)
			1*DeliveryItemInfo.setSplitQuantity(0L)
			3*itemShippingContainer.addCommerceItemShippingInfo(_, _)
			1*newItem.setDeliveryItemId(_)
			1*newItem.setAssemblyItemId(_)
	}
	
	def "splitCommerceItemShippingInfoForLTLByQuantity. TC when getWhiteGloveAssembly is false"(){
		given:
		String siteId = addCommonDetails()
		
		currentItem.getWhiteGloveAssembly() >> "false"
		order.getShippingGroup("sg1") >> sg
		order.getSiteId() >> siteId
		//1*ciManagerMock.addLTLAssemblyFeeSku(order, sg, siteId, newItem) >> "newAsscomdId"
		1*ciManagerMock.addLTLDeliveryChargeSku(order, sg, siteId, newItem) >> {throw new RepositoryException("exception")}
		itemShippingObject.getQuantity() >> 10
		
		////createNewInfoForBBBCommerceItem ////
		itemShippingObject.getShippingMethod() >> "sdd"
		itemShippingObject.getSplitShippingGroupName() >> "ssgg"
		itemShippingObject.getShippingGroupName() >> "hard"
		
		//copyCommerceItemProperties
		setItemDetails()
		cisiTools.adjustHandlingInstructionsForSplit(_, _, 4) >> {}
		when:
		CommerceItemShippingInfo value = cisiTools.splitCommerceItemShippingInfoForLTLByQuantity(itemShippingContainer, itemShippingObject ,4 ,order)
		then:
			value != null
			value.getCommerceItem() == newItem
			value.getShippingMethod() == "sdd"
			value.getShippingGroupName() == "hard"
			0*ciManagerMock.addLTLAssemblyFeeSku(order, sg, siteId, newItem)
	}

	private String addCommonDetails() {
		cisiTools = Spy()
		cisiTools.setCommerceItemManager(ciManagerMock)
		cisiTools.setOrderTools(orderToolsMock)
		cisiTools.setPurchaseProcessHelper(ppHelperMock)

		String skuId = "sku1"
		String siteId = "usBed"
		itemShippingObject.getCommerceItem() >> currentItem
		1*currentItem.getShippingGroupRelationships() >> [sgciRelationship]
		1*sgciRelationship.getShippingGroup() >> sg
		sg.getId() >> "sg1"

		1*currentItem.getAuxiliaryData() >> auxiliaryData
		auxiliaryData.getProductId() >> "p1"
		currentItem.getCatalogRefId() >> skuId

		1*ciManagerMock.createCommerceItem(_,skuId,"p1", 4)  >> newItem

		currentItem.getDeliveryItemId() >> "deliveryItemId"
		currentItem.getAssemblyItemId() >> "assemblyItemId"

		1*ciManagerMock.addAsSeparateItemToOrder(order, newItem)
		newItem.getId() >> "newItem"
		1*ciManagerMock.addItemQuantityToShippingGroup(order,"newItem",  "sg1",4)
		return siteId
	}

	private setItemDetails() {
		1*currentItem.getCommerceItemMoved() >> "mopved"
		1*currentItem.getStoreId() >> "sto1"
		1*currentItem.getRegistryId() >> "r12"
		1*currentItem.isItemMoved()  >> true
		1*currentItem.getRegistryInfo() >> "info"
		1*currentItem.getBts() >>  false
		1*currentItem.isVdcInd() >> true
		1*currentItem.getFreeShippingMethod() >> "10ds"
		1*currentItem.getSkuSurcharge() >> 10
		1*currentItem.getLastModifiedDate() >> new Date()
		1*currentItem.getIsEcoFeeEligible() >> true
		1*currentItem.getLtlShipMethod() >> "ltl"
	}
	
	def "splitCommerceItemShippingInfoForLTLByQuantity. TC when getWhiteGlveAssembly is NULL"(){
		given:
         String siteId = addCommonDetails()
		
		currentItem.getWhiteGloveAssembly() >>null
		order.getShippingGroup("sg1") >> sg
		order.getSiteId() >> siteId
		//1*ciManagerMock.addLTLAssemblyFeeSku(order, sg, siteId, newItem) >> "newAsscomdId"
		1*ciManagerMock.addLTLDeliveryChargeSku(order, sg, siteId, newItem) >> ""
		itemShippingObject.getQuantity() >> 10
		
		////createNewInfoForBBBCommerceItem ////
		itemShippingObject.getShippingMethod() >> "sdd"
		itemShippingObject.getSplitShippingGroupName() >> "ssgg"
		itemShippingObject.getShippingGroupName() >> "hard"
		
		//copyCommerceItemProperties
         setItemDetails()	
		 cisiTools.adjustHandlingInstructionsForSplit(_, _, 4) >> {}
		 when:
		 CommerceItemShippingInfo value = cisiTools.splitCommerceItemShippingInfoForLTLByQuantity(itemShippingContainer, itemShippingObject ,4 ,order)
		 then:
			value != null
			value.getCommerceItem() == newItem
			value.getShippingMethod() == "sdd"
			value.getShippingGroupName() == "hard"
			0*ciManagerMock.addLTLAssemblyFeeSku(order, sg, siteId, newItem)
	}
	
	def "splitCommerceItemShippingInfoForLTLByQuantity. TC when getWhiteGlveAssembly is "(){
		given:
		
		cisiTools = Spy()
		cisiTools.setCommerceItemManager(ciManagerMock)
		cisiTools.setOrderTools(orderToolsMock)
		cisiTools.setPurchaseProcessHelper(ppHelperMock)

		String skuId = "sku1"
		String siteId = "usBed"
		itemShippingObject.getCommerceItem() >> currentItem
		1*currentItem.getShippingGroupRelationships() >> [sgciRelationship]
		1*sgciRelationship.getShippingGroup() >> sg
		sg.getId() >> "sg1"

		1*currentItem.getAuxiliaryData() >> auxiliaryData
		auxiliaryData.getProductId() >> "p1"
		currentItem.getCatalogRefId() >> skuId

		1*ciManagerMock.createCommerceItem(_,skuId,"p1", 4)  >> newItem

		currentItem.getDeliveryItemId() >> ""
		currentItem.getAssemblyItemId() >> "assemblyItemId"

		1*ciManagerMock.addAsSeparateItemToOrder(order, newItem)
		newItem.getId() >> "newItem"
		1*ciManagerMock.addItemQuantityToShippingGroup(order,"newItem",  "sg1",4)

		
		currentItem.getWhiteGloveAssembly() >>null
		order.getShippingGroup("sg1") >> sg
		order.getSiteId() >> siteId
		itemShippingObject.getQuantity() >> 10
		
		////createNewInfoForBBBCommerceItem ////
		itemShippingObject.getShippingMethod() >> "sdd"
		itemShippingObject.getSplitShippingGroupName() >> "ssgg"
		itemShippingObject.getShippingGroupName() >> "hard"
		
		//copyCommerceItemProperties
         setItemDetails()	
		 cisiTools.adjustHandlingInstructionsForSplit(_, _, 4) >> {}
		 when:
		 CommerceItemShippingInfo value = cisiTools.splitCommerceItemShippingInfoForLTLByQuantity(itemShippingContainer, itemShippingObject ,4 ,order)
		 then:
			value != null
			value.getCommerceItem() == newItem
			value.getShippingMethod() == "sdd"
			value.getShippingGroupName() == "hard"
			0*ciManagerMock.addLTLAssemblyFeeSku(order, sg, siteId, newItem)
	}
	
	def "splitCommerceItemShippingInfoForLTLByQuantity. TC when new commerce item  is null "(){
		given:
		
		cisiTools = Spy()
		cisiTools.setCommerceItemManager(ciManagerMock)
		cisiTools.setOrderTools(orderToolsMock)
		cisiTools.setPurchaseProcessHelper(ppHelperMock)

		String skuId = "sku1"
		String siteId = "usBed"
		itemShippingObject.getCommerceItem() >> currentItem
		1*currentItem.getShippingGroupRelationships() >> [sgciRelationship]
		1*sgciRelationship.getShippingGroup() >> sg
		sg.getId() >> "sg1"

		1*currentItem.getAuxiliaryData() >> auxiliaryData
		auxiliaryData.getProductId() >> "p1"
		currentItem.getCatalogRefId() >> skuId

		1*ciManagerMock.createCommerceItem(_,skuId,"p1", 4)  >> null

		currentItem.getDeliveryItemId() >> ""
		currentItem.getAssemblyItemId() >> "assemblyItemId"


		
		currentItem.getWhiteGloveAssembly() >>null
		order.getShippingGroup("sg1") >> sg
		order.getSiteId() >> siteId
		itemShippingObject.getQuantity() >> 10
		
		////createNewInfoForBBBCommerceItem ////
		itemShippingObject.getShippingMethod() >> "sdd"
		itemShippingObject.getSplitShippingGroupName() >> "ssgg"
		itemShippingObject.getShippingGroupName() >> "hard"
		
		//copyCommerceItemProperties
		 cisiTools.adjustHandlingInstructionsForSplit(_, _, 4) >> {}
		 when:
		 CommerceItemShippingInfo value = cisiTools.splitCommerceItemShippingInfoForLTLByQuantity(itemShippingContainer, itemShippingObject ,4 ,order)
		 then:
			value != null
			value.getCommerceItem() == null
	}
	
	/********************************************************applyCommerceItemShippingInfo****************************************************/
	
	def "applyCommerceItemShippingInfo . TC to apply commerceItemShippingInfo" (){
		given:
		cisiTools = Spy()
		def ShippingGroupMapContainer itemShippingContainer = Mock()
		itemShippingObject.getCommerceItem() >> commItem
		commItem.getId() >> "c1"
		1*order.getCommerceItem("c1") >> commItem1
		expect:
		cisiTools.applyCommerceItemShippingInfo(itemShippingObject, order, itemShippingContainer)
		
		
	}
	
	def "applyCommerceItemShippingInfo . TC for CommerceItemNotFoundException" (){
		given:
		cisiTools = Spy()
		def ShippingGroupMapContainer itemShippingContainer = Mock()
		
		itemShippingObject.getCommerceItem() >> commItem
		commItem.getId() >> "c1"
		1*order.getCommerceItem("c1") >> {throw new CommerceItemNotFoundException("exception")}
		expect:
		cisiTools.applyCommerceItemShippingInfo(itemShippingObject, order, itemShippingContainer)
		
	}
	
	def "applyCommerceItemShippingInfo . TC for InvalidParameterException" (){
		given:
		cisiTools = Spy()
		def ShippingGroupMapContainer itemShippingContainer = Mock()
		
		itemShippingObject.getCommerceItem() >> commItem
		commItem.getId() >> "c1"
		1*order.getCommerceItem("c1") >> {throw new InvalidParameterException("exception")}
		expect:
		cisiTools.applyCommerceItemShippingInfo(itemShippingObject, order, itemShippingContainer)
	}
	
}
