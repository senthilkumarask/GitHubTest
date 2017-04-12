package com.bbb.commerce.order.purchase

import atg.commerce.CommerceException
import atg.commerce.inventory.InventoryException
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemManager
import atg.commerce.order.CommerceItemNotFoundException
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.CreditCard
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.InvalidParameterException
import atg.commerce.order.OrderManager
import atg.commerce.order.PaymentGroup

import com.bbb.account.vo.order.Address
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO
import com.bbb.commerce.catalog.vo.GiftWrapVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.common.BBBStoreInventoryContainer
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.commerce.order.BBBCreditCard
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.commerce.order.shipping.BBBShippingInfoBean
import com.bbb.common.vo.CommerceItemShipInfoVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBOrderTools
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.validation.BBBValidationRules
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.EcoFeeCommerceItem
import com.bbb.order.bean.GiftWrapCommerceItem
import com.bbb.utils.BBBUtility

import java.util.List;

import javax.transaction.SystemException
import javax.transaction.Transaction
import javax.transaction.TransactionManager

import atg.commerce.order.ShippingGroup
import atg.commerce.order.purchase.AddCommerceItemInfo
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.PricingModelHolder
import atg.commerce.pricing.PricingTools
import atg.core.util.ContactInfo
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryItem
import spock.lang.specification.BBBExtendedSpec

class BBBPurchaseProcessHelperSpecification extends BBBExtendedSpec {
	def BBBPurchaseProcessHelper ppHelper
	def BBBCommerceItem CommerceItme = Mock()
	def BBBCommerceItem CommerceItme1 = Mock()
	def BBBCommerceItem CommerceItme2 = Mock()
	def BBBCommerceItem CommerceItme3 = Mock()
	def BBBCommerceItem CommerceItme4 = Mock()
	def CommerceItem item = Mock()
	
	def CommerceItemManager itemManager = Mock()
	def OrderManager orderManagerMock = Mock()
	def BBBOrderTools orderTools = Mock()
	def EcoFeeCommerceItem efCommerceItem = Mock()
	def BBBStoreShippingGroup sShippingGroup = Mock()
	def BBBOrderImpl order = Mock()
	def ShippingGroup sg = Mock()
	def BBBCatalogTools cTools = Mock()
	def GiftRegistryManager grmMock = Mock()
	def RepositoryItem srItem = Mock()
	def BBBInventoryManager inventoryManagerMock = Mock()
	def BBBShippingGroupManager sgManager = Mock()
	def HardgoodShippingGroup hdsg = Mock()
	def BBBHardGoodShippingGroup bbbHdsg = Mock()
	def BBBStoreShippingGroup storeSG = Mock()
	def PricingTools pricingToolsMock = Mock()
	
	def AddCommerceItemInfo addItemInfo = Mock()
	def TransactionManager tManager = Mock()
	def CommerceItemRelationship cir1 = Mock()
	def CommerceItemRelationship cir2 = Mock()
	def CommerceItemRelationship cir3 = Mock()
	def CommerceItemRelationship cir4 = Mock()
	
	EcoFeeSKUVO cfSkuVo = new EcoFeeSKUVO()
	RegistrySummaryVO rsVO = new RegistrySummaryVO()
	SKUDetailVO sdVO =  new SKUDetailVO(srItem)
	StateVO stateVO = new StateVO()
	StateVO stateVO1 = new StateVO()
	
	def CommerceItemRelationship cir = Mock()
	def GiftWrapCommerceItem gwItem = Mock()
	def CommerceItemShipInfoVO cisiVO = Mock()
	def Transaction tranction = Mock()
	
	def BBBShippingInfoBean sBean = Mock()
    def Map<String, String> SkuAndRefNum = ["18384876":"ref1", "18392518":"ref2", "17823833" : "ref3"]
	
	
	def setup(){
		ppHelper = new BBBPurchaseProcessHelper(commerceItemManager : itemManager , orderManager : orderManagerMock, catalogTools : cTools, giftRegistryManager : grmMock, inventoryManager :inventoryManagerMock, shippingGroupManager :  sgManager, pricingTools : pricingToolsMock, transactionManager : tManager, eximDemo : true , eximDemoSkuAndRefNum:SkuAndRefNum)
		
		
	}
	
	def"addEcoFeeItem.TC to add Eco Fee item into the passed Shipping group "(){
		given: 
		 	def Map<String, String> map = new HashMap<String, String>()
			CommerceItme.getCatalogRefId() >> "sku1"
			cfSkuVo.setEcoFeeSKUId("feId")
			cfSkuVo.setEcoFeeProductId("pId")
			
			orderManagerMock.getOrderTools() >> orderTools
			1*orderTools.getEcoFeeCommerceItemType() >> "echoType"
		
			1*itemManager.createCommerceItem("echoType", "feId", "pId", _) >> efCommerceItem
			sShippingGroup.getStoreId() >> "storeId"
			sShippingGroup.getId() >> "sid"
			
			1*itemManager.addAsSeparateItemToOrder(order, efCommerceItem) >> {}
			1*itemManager.addItemQuantityToShippingGroup(order,_, "sid", _) >> {}
			
			efCommerceItem.getId() >> "efid"
			CommerceItme.getId() >> "cid"
			1*sShippingGroup.getEcoFeeItemMap() >> map
		when:
		
			EcoFeeCommerceItem value = ppHelper.addEcoFeeItem(CommerceItme, order, sShippingGroup, 3, cfSkuVo)
		then:
		
			value != null
			map.get("cid") == "efid"
			1 * efCommerceItem.setStoreId("storeId")
		
	}
	
	def"addEcoFeeItem.TC when EcoFeeProductId "(){
		given:
			CommerceItme.getCatalogRefId() >> "sku1"
			cfSkuVo.setEcoFeeSKUId("feId")
			cfSkuVo.setEcoFeeProductId(null)
			
		when:
		
			EcoFeeCommerceItem value = ppHelper.addEcoFeeItem(CommerceItme, order, sg, 3, cfSkuVo)
		then:
			
			value == null
	
			0 * efCommerceItem.setStoreId("storeId")
			0 * itemManager.createCommerceItem(_, _, _, _)
			0 * sShippingGroup.getEcoFeeItemMap()
			0 * itemManager.addAsSeparateItemToOrder(_, _)
	}
	
	def"addEcoFeeItem.TC when EcoFeeSkuId is null "(){
		given:
			CommerceItme.getCatalogRefId() >> "sku1"
			cfSkuVo.setEcoFeeSKUId(null)
			cfSkuVo.setEcoFeeProductId(null)
			
		when:
		
			EcoFeeCommerceItem value = ppHelper.addEcoFeeItem(CommerceItme, order, sg, 3, cfSkuVo)
		then:
			
			value == null
	
			0 * efCommerceItem.setStoreId("storeId")
			0 * itemManager.createCommerceItem(_, _, _, _)
			0 * sShippingGroup.getEcoFeeItemMap()
			0 * itemManager.addAsSeparateItemToOrder(_, _)
	}
	
	def"addEcoFeeItem.TC when shipping group is not  BBBStoreShippingGroup type "(){
		given:
			CommerceItme.getCatalogRefId() >> "sku1"
			cfSkuVo.setEcoFeeSKUId("feId")
			cfSkuVo.setEcoFeeProductId("pId")
			orderManagerMock.getOrderTools() >> orderTools
			1*orderTools.getEcoFeeCommerceItemType() >> "echoType"
		
			1*itemManager.createCommerceItem("echoType", "feId", "pId", _) >> null

		when:
		
			EcoFeeCommerceItem value = ppHelper.addEcoFeeItem(CommerceItme, order, sg, 3, cfSkuVo)
		then:
		    
			value == null
	
			0 * efCommerceItem.setStoreId("storeId")
			0 * sShippingGroup.getEcoFeeItemMap()
			0 * itemManager.addAsSeparateItemToOrder(_, _)
	}
	
	/**************************************** createCommerceItem ***************************************/
	
	def "createCommerceItem. TC to create commerce item" (){
		given:
		ShipMethodVO smVO = new ShipMethodVO()
		def ArrayList list = [smVO]
		Dictionary<String, String> valueMap = new Hashtable<String, String>()
		valueMap.put("registryId", "valu")
		valueMap.put("referenceNumber", "1")
		valueMap.put("eximErrorExists", "false")
		valueMap.put("prevPrice", "10")
		valueMap.put("shipMethodUnsupported", "true")
		valueMap.put("prevLtlShipMethod", "pltm")
		valueMap.put("whiteGloveAssembly", "white")
		valueMap.put("bts", "true")
		valueMap.put("oos", "false")
		
		//def Dictionary<String, String> valueMap = ["registryId" : "valu", "referenceNumber" : "1", "eximErrorExists" : "false", "prevPrice" : "10", "shipMethodUnsupported" : "true", "prevLtlShipMethod":"pltm", "whiteGloveAssembly":"white", "bts":"true", "oos" :"false"]
		addItemInfo.getSiteId() >> "usBed"
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, "usBed", null) >> CommerceItme
		
		// add BBBProperties 
		addItemInfo.getValue() >> valueMap
		1*order.getRegistryMap() >> ["valu" :rsVO]
		2*cTools.getSKUDetails("usBed", "sku", false, true, true) >> sdVO
		sdVO.setVdcSku(false)
		sdVO.setFreeShipMethods(list)
		sdVO.setShippingSurcharge(10)
		/////getFreeShippingMethodInfo
		smVO.setShipMethodDescription("sameDayDelivery")
		
		1*itemManager.addItemToOrder(order, _) >> CommerceItme
		order.getCommerceItemCount() >> 1
		CommerceItme.getCatalogRefId() >> "sku1234"
		CommerceItme.getBts() >> false
		order.getCommerceItemsByCatalogRefId("sku1234") >> null
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		item != null
		1 * CommerceItme.setRegistryId("valu")
		1 * CommerceItme.setRegistryInfo(_)
		1 * CommerceItme.setReferenceNumber("1")
		2 * CommerceItme.setEximErrorExists(false)
		1 * CommerceItme.setEximPricingReq(true)
		1 * CommerceItme.setStoreId(_)
		1 * CommerceItme.setPrevPrice(10.0)
		1 * CommerceItme.setShipMethodUnsupported(true)
		1 * CommerceItme.setLtlShipMethod("pltm")
		1 *  CommerceItme.setWhiteGloveAssembly("white")
		2 * CommerceItme.setBts(true)
		1 * CommerceItme.setMsgShownOOS(false)
		1 * CommerceItme.setLastModifiedDate(_)
		1 * CommerceItme.setVdcInd(false)
		1 * CommerceItme.setFreeShippingMethod("sameDayDelivery")
		1 * CommerceItme.setSkuSurcharge(10.0)
		1 * CommerceItme.setIsEcoFeeEligible(_)
	}
	
	def "createCommerceItem. TC to prevPrice, prevLtlShiMethod is blanck" (){
		given:
		def SKUDetailVO sdVO = Mock()
		ShipMethodVO smVO = new ShipMethodVO()
		def ArrayList list = [smVO]
		Dictionary<String, String> valueMap = new Hashtable<String, String>()
		valueMap.put("registryId", "valu")
		valueMap.put("referenceNumber", "1")
		valueMap.put("eximErrorExists", "false")
		valueMap.put("prevPrice", "")
		valueMap.put("shipMethodUnsupported", "true")
		valueMap.put("prevLtlShipMethod", "")
		valueMap.put("whiteGloveAssembly", "white")
		valueMap.put("bts", "true")
		valueMap.put("oos", "")
		valueMap.put("eximPricingReq", "false")
		valueMap.put("ltlShipMethod", "ssssd")
		
		
		//def Dictionary<String, String> valueMap = ["registryId" : "valu", "referenceNumber" : "1", "eximErrorExists" : "false", "prevPrice" : "10", "shipMethodUnsupported" : "true", "prevLtlShipMethod":"pltm", "whiteGloveAssembly":"white", "bts":"true", "oos" :"false"]
		addItemInfo.getSiteId() >> ""
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, _, null) >> CommerceItme
		
		// add BBBProperties
		addItemInfo.getValue() >> valueMap
		1*order.getRegistryMap() >> ["valu" :null]
		1*grmMock.getRegistryInfo("valu", _) >> null
		
		2*cTools.getSKUDetails(null, "sku", false, true, true) >> {throw new BBBSystemException ("exception")} >> sdVO
		
		srItem.getPropertyValue("vdcSkuType") >> "true"
		sdVO.isVdcSku() >> true
		sdVO.getFreeShipMethods() >> null
		/////getFreeShippingMethodInfo
		smVO.setShipMethodDescription("sameDayDelivery")
		
		1*itemManager.addItemToOrder(order, _) >> CommerceItme
		order.getCommerceItemCount() >> 1
		CommerceItme.getCatalogRefId() >> "sku1234"
		CommerceItme.getBts() >> false >> true
		order.getCommerceItemsByCatalogRefId("sku1234") >> [CommerceItme]
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		item != null
		1 * CommerceItme.setRegistryId("valu")
		1 * CommerceItme.setReferenceNumber("1")
		2 * CommerceItme.setEximErrorExists(false)
		1 * CommerceItme.setEximPricingReq(false)
		1 * CommerceItme.setStoreId(_)
		0 * CommerceItme.setPrevPrice(10.0)
		1 * CommerceItme.setShipMethodUnsupported(true)
		1 * CommerceItme.setLtlShipMethod("ssssd")
		1 *  CommerceItme.setWhiteGloveAssembly("white")
		2 * CommerceItme.setBts(true)
		1 * CommerceItme.setMsgShownOOS(true)
		1 * CommerceItme.setLastModifiedDate(_)
		0 * CommerceItme.setVdcInd(false)
		0 * CommerceItme.setFreeShippingMethod("sameDayDelivery")
		1 * CommerceItme.setSkuSurcharge(_)
		1 * CommerceItme.setIsEcoFeeEligible(_)
	}
	
	def "createCommerceItem. TC to registryId is blanck and sku Detail is null" (){
		given:
		def SKUDetailVO sdVO = Mock()
		ShipMethodVO smVO = new ShipMethodVO()
		def ArrayList list = [smVO]
		Dictionary<String, String> valueMap = new Hashtable<String, String>()
		valueMap.put("registryId", "")
		valueMap.put("referenceNumber", "")
		valueMap.put("eximErrorExists", "false")
		valueMap.put("prevPrice", "")
		valueMap.put("shipMethodUnsupported", "true")
		valueMap.put("prevLtlShipMethod", "")
		valueMap.put("whiteGloveAssembly", "white")
		valueMap.put("bts", "true")
		valueMap.put("oos", "")
		valueMap.put("eximPricingReq", "")
		valueMap.put("ltlShipMethod", "ssssd")
		
		
		//def Dictionary<String, String> valueMap = ["registryId" : "valu", "referenceNumber" : "1", "eximErrorExists" : "false", "prevPrice" : "10", "shipMethodUnsupported" : "true", "prevLtlShipMethod":"pltm", "whiteGloveAssembly":"white", "bts":"true", "oos" :"false"]
		addItemInfo.getSiteId() >> ""
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, _, null) >> CommerceItme
		
		// add BBBProperties
		addItemInfo.getValue() >> valueMap
		
		2*cTools.getSKUDetails(null, "sku", false, true, true) >> {throw new BBBBusinessException ("exception")} >> null
		
		srItem.getPropertyValue("vdcSkuType") >> "true"
		sdVO.isVdcSku() >> true
		sdVO.getFreeShipMethods() >> null
		/////getFreeShippingMethodInfo
		smVO.setShipMethodDescription("sameDayDelivery")
		
		itemManager.addItemToOrder(order, _) >> CommerceItme
		order.getCommerceItemCount() >> 1
		CommerceItme.getCatalogRefId() >> "sku1234"
		CommerceItme.getBts() >> false 
		order.getCommerceItemsByCatalogRefId("sku1234") >> [CommerceItme]
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		item != null
		2 * CommerceItme.setEximErrorExists(false)
		1 * CommerceItme.setStoreId(_)
		0 * CommerceItme.setPrevPrice(10.0)
		1 * CommerceItme.setShipMethodUnsupported(true)
		1 * CommerceItme.setLtlShipMethod("ssssd")
		1 *  CommerceItme.setWhiteGloveAssembly("white")
		1 * CommerceItme.setBts(true)
		1 * CommerceItme.setMsgShownOOS(true)
		1 * CommerceItme.setLastModifiedDate(_)
		0 * CommerceItme.setVdcInd(false)
		0 * CommerceItme.setFreeShippingMethod("sameDayDelivery")
	}
	
	def "createCommerceItem. TC to value dictionary is null" (){
		given:
		addItemInfo.getSiteId() >> ""
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, _, null) >> CommerceItme

		addItemInfo.getValue() >> null
		
		1*cTools.getSKUDetails(null, "sku", false, true, true)  >> sdVO
		
		
		itemManager.addItemToOrder(order, _) >> CommerceItme
		order.getCommerceItemCount() >> 1
		CommerceItme.getCatalogRefId() >> "sku1234"
		CommerceItme.getBts() >> false
		order.getCommerceItemsByCatalogRefId("sku1234") >> []
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		item != null
		1 * CommerceItme.setVdcInd(false)
		
	}
	def "createCommerceItem. TC when commerceItemCount in order zero" (){
		given:
		addItemInfo.getSiteId() >> ""
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, _, null) >> CommerceItme

		addItemInfo.getValue() >> null
		
		1*cTools.getSKUDetails(null, "sku", false, true, true)  >> sdVO
		
		
		itemManager.addItemToOrder(order, _) >> CommerceItme
		order.getCommerceItemCount() >> 0
		CommerceItme.getCatalogRefId() >> "sku1234"
		CommerceItme.getBts() >> false
		order.getCommerceItemsByCatalogRefId("sku1234") >> []
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		item != null
		1 * CommerceItme.setVdcInd(false)
		
	}
	
	def "createCommerceItem. TC when product is from college" (){
		given:
		addItemInfo.getSiteId() >> ""
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, _, null) >> CommerceItme

		addItemInfo.getValue() >> null
		
		1*cTools.getSKUDetails(null, "sku", false, true, true)  >> sdVO
		
		
		itemManager.addItemToOrder(order, _) >> CommerceItme
		order.getCommerceItemCount() >> 1
		CommerceItme.getCatalogRefId() >> "sku1234"
		CommerceItme.getBts() >> true
		order.getCommerceItemsByCatalogRefId("sku1234") >> []
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		item != null
		1 * CommerceItme.setVdcInd(false)
		
	}
	/**********************************exception scenario*********************************************/
	
	def "createCommerceItem. TC for BBBBusinessException while getting registry map" (){
		given:
		ShipMethodVO smVO = new ShipMethodVO()
		def ArrayList list = [smVO]
		Dictionary<String, String> valueMap = new Hashtable<String, String>()
		valueMap.put("registryId", "valu")
			
		addItemInfo.getSiteId() >> "usBed"
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, "usBed", null) >> CommerceItme
		
		// add BBBProperties
		addItemInfo.getValue() >> valueMap
		1*order.getRegistryMap() >> ["valu" :null]
		1*grmMock.getRegistryInfo("valu", _) >> {throw new BBBBusinessException("exception")}
		0*itemManager.addItemToOrder(order, _) >> CommerceItme
		
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		0 * CommerceItme.setRegistryInfo(_)
		0 * CommerceItme.setReferenceNumber(_)
		0 * CommerceItme.setEximErrorExists(_)
		0 * CommerceItme.setEximPricingReq(_)
		0 * CommerceItme.setStoreId(_)
		0 * CommerceItme.setPrevPrice(_)
		CommerceException exception = thrown()
	}
	
	def "createCommerceItem. TC for BBBSystemException while getting registry map" (){
		given:
		ShipMethodVO smVO = new ShipMethodVO()
		def ArrayList list = [smVO]
		Dictionary<String, String> valueMap = new Hashtable<String, String>()
		valueMap.put("registryId", "valu")
			
		addItemInfo.getSiteId() >> "usBed"
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, "usBed", null) >> CommerceItme
		0*itemManager.addItemToOrder(order, _) >> CommerceItme
		// add BBBProperties
		addItemInfo.getValue() >> valueMap
		1*order.getRegistryMap() >> ["valu" :null]
		1*grmMock.getRegistryInfo("valu", _) >> {throw new BBBSystemException("exception")}
			
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		0 * CommerceItme.setRegistryInfo(_)
		0 * CommerceItme.setReferenceNumber(_)
		0 * CommerceItme.setEximErrorExists(_)
		0 * CommerceItme.setEximPricingReq(_)
		0 * CommerceItme.setStoreId(_)
		0 * CommerceItme.setPrevPrice(_)
		CommerceException exception = thrown()
		//thrown new CommerceException()
	}
	
	def "createCommerceItem. TC for BBBBusinessException while getting sku details " (){
		given:
		addItemInfo.getSiteId() >> ""
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, _, null) >> CommerceItme

		addItemInfo.getValue() >> null
		
		1*cTools.getSKUDetails(null, "sku", false, true, true)  >> {throw new BBBBusinessException("exception")}
		
		
		itemManager.addItemToOrder(order, _) >> CommerceItme1
		
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		CommerceException exception = thrown()
		0 * CommerceItme.setVdcInd(false)
		
	}
	
	def "createCommerceItem. TC for BBBSystemException while getting sku details " (){
		given:
		addItemInfo.getSiteId() >> ""
		addItemInfo.getCommerceItemType() >> "itemType"
		addItemInfo.getCatalogRefId() >> "sku"
		addItemInfo.getProductId() >> "pid"
		addItemInfo.getQuantity() >> 3
		
		1*itemManager.createCommerceItem("itemType", "sku", null, "pid", null, 3,_, null, _, null) >> CommerceItme

		addItemInfo.getValue() >> null
		
		1*cTools.getSKUDetails(null, "sku", false, true, true)  >> {throw new BBBSystemException("exception")}
		
		
		itemManager.addItemToOrder(order, _) >> CommerceItme1
		
		when:
			CommerceItem item = ppHelper.createCommerceItem(addItemInfo, "skuid" , order)
			
		then:
		CommerceException exception = thrown()
		0 * CommerceItme.setVdcInd(false)
		
	}
	
	/******************************************************* getRegistryInfo *******************************/
	
	def"getRegistryInfo. Tc to get the registry info"(){
		given:
			rsVO.setPrimaryRegistrantFirstName("fName")
			rsVO.setPrimaryRegistrantLastName("lName")
			rsVO.setCoRegistrantFirstName("crFName")
			rsVO.setCoRegistrantLastName("crLName")
			rsVO.setRegistryId("id")
			
		when:
			String value = ppHelper.getRegistryInfo(rsVO)
		then:
		value == "fName lName  :  crFName crLName  :  id"
	}
	
	def"getRegistryInfo. Tc when CoRegistrantLastName is empty"(){
		given:
			rsVO.setPrimaryRegistrantFirstName("fName")
			rsVO.setPrimaryRegistrantLastName("lName")
			rsVO.setCoRegistrantFirstName("crFName")
			rsVO.setCoRegistrantLastName("")
			rsVO.setRegistryId("id")
			
		when:
			String value = ppHelper.getRegistryInfo(rsVO)
		then:
		value == "fName lName  :  id"
	}


/*********************************************checkInventory**************************************/

	def"checkInventory. tc for available status of inventory"(){
		given:
		    def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			SKUDetailVO sdVO = Mock() 
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1*sdVO.isVdcSku() >> false
			1*sdVO.getEcomFulfillment() >> "ff"
			
			1 * inventoryManagerMock.getNonVDCProductAvailability("usBed", "skuId", "ff", operation, false, 2) >> 0
		when:
			int value = ppHelper.checkInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 0
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkInventory. tc to check inventory when operation is update cart"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "update"
			SKUDetailVO sdVO = Mock()
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1*sdVO.isVdcSku() >> false
			
		when:
			int value = ppHelper.checkInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 2
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkInventory. tc sku is VDC"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			SKUDetailVO sdVO = Mock()
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1*sdVO.isVdcSku() >> true
			
			1 * inventoryManagerMock.getVDCProductAvailability("usBed", "skuId", 2, false) >> 1
		when:
			int value = ppHelper.checkInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 1
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkInventory. tc to check BOPUS Product Availability "(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "notStoreToOnline"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1 * inventoryManagerMock.getBOPUSProductAvailability("usBed", "skuId", _,2, "onlineToStore", storeInventoryContainer, true, null, false , false) >> ["store" : 2]
			
		when:
			int value = ppHelper.checkInventory("usBed", "skuId", "store", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 2
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
	}
	
	def"checkInventory. tc when operation is StoreToOnline "(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "storeToOnline"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			
		when:
			int value = ppHelper.checkInventory("usBed", "skuId", "store", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 0
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkInventory. tc when sku detail VO is null "(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "storeToOnline"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> null
			
		when:
			int value = ppHelper.checkInventory("usBed", "skuId", "store", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 0
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkInventory. tc for InventoryException"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			SKUDetailVO sdVO = Mock()
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1*sdVO.isVdcSku() >> false
			1*sdVO.getEcomFulfillment() >> "ff"
			
			1 * inventoryManagerMock.getNonVDCProductAvailability("usBed", "skuId", "ff", operation, false, 2) >> {throw new InventoryException("exception")}
		when:
			int value = ppHelper.checkInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 1
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkInventory. tc for Exception while getting sku detail VO"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> {throw new Exception("exception")}
			when:
			int value = ppHelper.checkInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
		    CommerceException exception = thrown()
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
	}
	
	def"checkInventory. tc when sku id is empty"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			when:
			int value = ppHelper.checkInventory("usBed", "", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			CommerceException exception = thrown()
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
			0 * cTools.getSKUDetails (*_)
	}
	
	/***************************************************checkCachedInventory*************************/
	def"checkCachedInventory. tc for available status of inventory"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			SKUDetailVO sdVO = Mock()
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1*sdVO.isVdcSku() >> false
			1*sdVO.getEcomFulfillment() >> "ff"
			
			1 * inventoryManagerMock.getNonVDCProductAvailability("usBed", "skuId", "ff", operation, true, 2) >> 0 
		when:
			int value = ppHelper.checkCachedInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 0
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	
	def"checkCachedInventory. tc sku is VDC"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			SKUDetailVO sdVO = Mock()
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1*sdVO.isVdcSku() >> true
			
			1 * inventoryManagerMock.getVDCProductAvailability("usBed", "skuId", 2, true) >> 1
		when:
			int value = ppHelper.checkCachedInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 1
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkCachedInventory. tc to check BOPUS Product Availability "(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "notStoreToOnline"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1 * inventoryManagerMock.getBOPUSProductAvailability("usBed", "skuId", _,2, "onlineToStore", storeInventoryContainer, true, null, false , false) >> ["store" : 2]
			
		when:
			int value = ppHelper.checkCachedInventory("usBed", "skuId", "store", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 2
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
	}
	
	def"checkCachedInventory. tc when operation is StoreToOnline "(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "storeToOnline"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			
		when:
			int value = ppHelper.checkCachedInventory("usBed", "skuId", "store", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 0
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkCachedInventory. tc when sku detail VO is null "(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "storeToOnline"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> null
			
		when:
			int value = ppHelper.checkCachedInventory("usBed", "skuId", "store", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 0
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkCachedInventory. tc for InventoryException"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			SKUDetailVO sdVO = Mock()
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> sdVO
			1*sdVO.isVdcSku() >> false
			1*sdVO.getEcomFulfillment() >> "ff"
			
			1 * inventoryManagerMock.getNonVDCProductAvailability("usBed", "skuId", "ff", operation, true, 2) >> {throw new InventoryException("exception")}
		when:
			int value = ppHelper.checkCachedInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			value == 1
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
	}
	
	def"checkCachedInventory. tc for Exception while getting sku detail VO"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			1 * cTools.getSKUDetails ("usBed", "skuId", false, true, true) >> {throw new Exception("exception")}
			when:
			int value = ppHelper.checkCachedInventory("usBed", "skuId", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			CommerceException exception = thrown()
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
	}
	
	def"checkCachedInventory. tc when sku id is empty"(){
		given:
			def BBBStoreInventoryContainer storeInventoryContainer = Mock()
			String operation = "noUpdate"
			when:
			int value = ppHelper.checkCachedInventory("usBed", "", "", order, 2, operation, storeInventoryContainer , 2)
		then:
			CommerceException exception = thrown()
			0 * inventoryManagerMock.getBOPUSProductAvailability(*_)
			0 * inventoryManagerMock.getNonVDCProductAvailability(*_)
			0 * cTools.getSKUDetails (*_)
	}
	
	/************************************* getRollupQuantities *************************************** */
	
	def "getRollupQuantities. TC to get the roolup Quantities"(){
		given:
			1*order.getExistsItemsForSameSKU("storeId", "registryId", "skuId") >> [CommerceItme,CommerceItme1]
			CommerceItme.getQuantity() >> 10
			CommerceItme1.getQuantity() >> 10
		
		when:
			long value = ppHelper.getRollupQuantities("storeId", "skuId", order, 2, "registryId")
		then:
		value == 22
	}
	
	def "getRollupQuantities. TC to get the roolup Quantities when ExistsItemsForSameSKU is empty"(){
		given:
			1*order.getExistsItemsForSameSKU("storeId", "registryId", "skuId") >> []
		
		when:
			long value = ppHelper.getRollupQuantities("storeId", "skuId", order, 2, "registryId")
		then:
		value == 2
	}
	
/******************************************** getRollupQtyForUpdate ************************************/	
	def "getRollupQtyForUpdate. TC to to rollup quantity for update" (){
		given:
			1*order.getExistsItemsForSameSKU("storeId", "registryId", "skuId") >> [CommerceItme,CommerceItme1,null]
			CommerceItme.getRegistryId() >> null
			CommerceItme1.getRegistryId() >> "rId"
			CommerceItme.getQuantity() >> 10
			
		when:
			long value = ppHelper.getRollupQtyForUpdate("storeId", "registryId", "skuId", order, 2)
			
		then:
			value == 12
	}
	
	def "getRollupQtyForUpdate. TC when registry id is null" (){
		given:
			1*order.getExistsItemsForSameSKU("storeId",null, "skuId") >> [CommerceItme,CommerceItme1]
			CommerceItme.getRegistryId() >> "rId"
			CommerceItme1.getRegistryId() >> null
			CommerceItme.getQuantity() >> 10
			
		when:
			long value = ppHelper.getRollupQtyForUpdate("storeId", null, "skuId", order, 2)
			
		then:
			value == 12
	}
	
	def "getRollupQtyForUpdate. TC when ExistsItemsForSameSKU list is empty" (){
		given:
			1*order.getExistsItemsForSameSKU("storeId",null, "skuId") >> []
			
		when:
			long value = ppHelper.getRollupQtyForUpdate("storeId", null, "skuId", order, 2)
			
		then:
			value == 2
	}
	
	/**************************************** getShippingGroupForItem *******************************/
	def"getShippingGroupForItem. TC to get the shipping group when store id is not null"(){
		given:
			sgManager
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "stId")
		    addItemInfo.getValue() >> valueMap
			sgManager.getStorePickupShippingGroup("stId", order) >> sg
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
		    value == sg
			
	}
	
	def"getShippingGroupForItem. TC to get the shipping group when store pickup shipping group is  null "(){
		given:
			sgManager
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "stId")
			addItemInfo.getValue() >> valueMap
			sgManager.getStorePickupShippingGroup("stId", order) >> null
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			CommerceException exception = thrown()
			exception.getMessage().equals("Could not create StorePickupShippingGroup")
	}
	
	def"getShippingGroupForItem. TC to get the shipping group when store id is empty "(){
		given:
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "")
			valueMap.put("registryId", "")
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> false
			
			1*sgManager.getFirstNonLTLHardgoodShippingGroup(order) >> null
			
			sgManager.getOrderTools() >> orderTools
			orderTools.getDefaultShippingGroupType() >> "ShippingGroup"
			1*sgManager.createShippingGroup("ShippingGroup") >> sg
			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == sg
			1*sgManager.addShippingGroupToOrder(order, sg)
			
	}
	
	def"getShippingGroupForItem. TC to get the shipping group from FirstNonLTLHardgoodShippingGroup and  store id is null  "(){
		given:
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "")
			valueMap.put("registryId", "")
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> false
			
			1*sgManager.getFirstNonLTLHardgoodShippingGroup(order) >> hdsg
			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == hdsg
			
	}
	
	def"getShippingGroupForItem. TC to get the shipping group when registry id is not empty "(){
		given:
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "" )
			valueMap.put("registryId", "rId")
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> false
			
			1*sgManager.getRegistryShippingGroup("rId", order) >> sg
			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == sg
			
	}
	
	def"getShippingGroupForItem. TC to get LtlShippingGroups "(){
		given:
		    def HardgoodShippingGroup hdsg1 = Mock()
		
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "")
			valueMap.put("registryId", "rId")
			valueMap.put("ltlShipMethod", "ltlMe")
			
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> true
			////getLtlShippingGroups
			1*order.getShippingGroups() >> [hdsg]
			 
			2*hdsg.getShippingMethod() >> "ltlMe"
			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == hdsg
			
	}
	
	def"getShippingGroupForItem. TC to get LtlShippingGroups when shipping method is not ltl "(){
		given:
			def HardgoodShippingGroup hdsg1 = Mock()
		
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "")
			valueMap.put("registryId", "rId")
			valueMap.put("ltlShipMethod", "ltlMe")
			
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> true
			////getLtlShippingGroups
			1*order.getShippingGroups() >> [hdsg, hdsg1,sg,null]
			 
			2*hdsg.getShippingMethod() >> "notltlMe"
			1*hdsg1.getShippingMethod() >> null
			
			sgManager.getOrderTools() >> orderTools
			1*orderTools.getDefaultShippingGroupType() >> "ShippingGroup"

			1*sgManager.createShippingGroup("ShippingGroup") >> sg
			1*sgManager.addShippingGroupToOrder(order, sg)
			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == sg
			1 * sg.setShippingMethod("ltlMe")
			
	}
	
	def"getShippingGroupForItem. TC to get LtlShippingGroups when shipping group list is empty gets from order "(){
		given:
			def HardgoodShippingGroup hdsg1 = Mock()
		
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "")
			valueMap.put("registryId", "rId")
			valueMap.put("ltlShipMethod", "ltlMe")
			
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> true
			////getLtlShippingGroups
			1*order.getShippingGroups() >> []
			
			sgManager.getOrderTools() >> orderTools
			1*orderTools.getDefaultShippingGroupType() >> "ShippingGroup"

			1*sgManager.createShippingGroup("ShippingGroup") >> {throw new CommerceException("exception")}
			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == null
			0 * sg.setShippingMethod("ltlMe")
			0 * sgManager.addShippingGroupToOrder(order, sg)
			
	}
	
	def"getShippingGroupForItem. TC for BBBBusinessException while checking is sku ltl  "(){
		given:
		
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "")
			valueMap.put("registryId", "rId")
			valueMap.put("ltlShipMethod", "ltlMe")
			
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> {throw new BBBBusinessException("exception")}
			////getLtlShippingGroups

			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == null
			0 * sg.setShippingMethod("ltlMe")
			0 * sgManager.addShippingGroupToOrder(order, sg)
			0 * order.getShippingGroups()
	}
	
	def"getShippingGroupForItem. TC for BBBSystemException while checking is sku ltl  "(){
		given:
		
			Dictionary<String, String> valueMap = new Hashtable<String, String>()
			valueMap.put("storeId", "")
			valueMap.put("registryId", "rId")
			valueMap.put("ltlShipMethod", "ltlMe")
			
			addItemInfo.getValue() >> valueMap
			addItemInfo.getCatalogRefId() >> "skuId"
			order.getSiteId() >> "siteId"
			
			1*cTools.isSkuLtl( "siteId", "skuId") >> {throw new BBBSystemException("exception")}
			////getLtlShippingGroups

			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == null
			0 * sg.setShippingMethod("ltlMe")
			0 * sgManager.addShippingGroupToOrder(order, sg)
			0 * order.getShippingGroups()
	}

	def"getShippingGroupForItem. TC when calueMap is empty  "(){
		given:
		
			addItemInfo.getValue() >> null
			
			
		when:
			ShippingGroup value = ppHelper.getShippingGroupForItem(order, addItemInfo, null, "" )
			
		then:
			value == null
			0 * sg.setShippingMethod("ltlMe")
			0 * sgManager.addShippingGroupToOrder(order, sg)
			0 * order.getShippingGroups()
			0 * cTools.isSkuLtl( _, _)
	}
	
	
	/********************************************* deleteItems ****************************************/
	
	def"deleteItems. Tc to remove the selected commerce items"(){
		given:
		
			ppHelper = Spy()
			ppHelper.setTransactionManager(tManager)
			ppHelper.setShippingGroupManager(sgManager)
			ppHelper.setOrderManager(orderManagerMock)
			
			def PricingModelHolder pModle = Mock()
			
			def String [] remItemIds = ["rmid1","rmid2" , "rmid3" ,"rmid4", "rmid5"]
			order.getCommerceItem("rmid1") >> CommerceItme
			order.getCommerceItem("rmid2") >> CommerceItme1
			order.getCommerceItem("rmid3") >> CommerceItme2
			order.getCommerceItem("rmid4") >> CommerceItme3
			order.getCommerceItem("rmid5") >> item
			
			CommerceItme.getStoreId() >> "storeId"
			CommerceItme1.getStoreId() >> null
			CommerceItme2.getStoreId() >> "storeId1"
			CommerceItme3.getStoreId() >> "storeId2"
			
			CommerceItme.getRegistryId() >> "regId"
			CommerceItme1.getRegistryId() >> null
			CommerceItme2.getRegistryId() >> "regId1"
			CommerceItme3.getRegistryId() >> "regId2"
			
			ppHelper.callSuperDeleteItems(*_) >> {}
			
			1*sgManager.getStorePickupShippingGroup("storeId", order) >> storeSG
			1*sgManager.getStorePickupShippingGroup("storeId1", order) >> storeSG
			1*sgManager.getStorePickupShippingGroup("storeId2", order) >> storeSG
			
			3*storeSG.getCommerceItemRelationshipCount() >>> [1,0,1]
			storeSG.getId() >> "storeSG1"
			
			
			1*sgManager.getRegistryPickupShippingGroup("regId", order) >> bbbHdsg
			1*sgManager.getRegistryPickupShippingGroup("regId1", order) >> bbbHdsg
			1*sgManager.getRegistryPickupShippingGroup("regId2", order) >> null
			
			2*bbbHdsg.getCommerceItemRelationshipCount() >> 0 >> 1
			bbbHdsg.getId() >> "hdId1"
			
		when:
			List<String> deletedSku = ppHelper.deleteItems(order, remItemIds, pModle, null,null,null,null)
		then:
			deletedSku.contains("rmid1")
			deletedSku.contains("rmid2")
			deletedSku.contains("rmid3")
			deletedSku.contains("rmid4")
			1*sgManager.removeShippingGroupFromOrder(order, "storeSG1")
			1*sgManager.removeShippingGroupFromOrder(order, "hdId1")
			1*orderManagerMock.updateOrder(order)
	}
	
	def"deleteItems. Tc for Exception"(){
		given:
		
			ppHelper = Spy()
			ppHelper.setTransactionManager(tManager)
			ppHelper.setShippingGroupManager(sgManager)
			ppHelper.setOrderManager(orderManagerMock)
			
			def PricingModelHolder pModle = Mock()
			
			def String [] remItemIds = ["rmid1","rmid2" , "rmid3" ,"rmid4", "rmid5"]
			order.getCommerceItem("rmid1") >> CommerceItme
			
			CommerceItme.getStoreId() >> "storeId"
			
			CommerceItme.getRegistryId() >> "regId"
			
			ppHelper.callSuperDeleteItems(*_) >> {}
			
			1*sgManager.getStorePickupShippingGroup("storeId", order) >> {throw new Exception("Exception")}
			
			
		when:
			List<String> deletedSku = ppHelper.deleteItems(order, remItemIds, pModle, null,null,null,null)
		then:
			CommerceException exception = thrown()
			0 * orderManagerMock.updateOrder(_)
			0* sgManager.getRegistryPickupShippingGroup(_, _)
	}
	
	def"deleteItems. Tc when remItemIds is empty"(){
		given:
		
			ppHelper = Spy()
			ppHelper.setTransactionManager(tManager)
			ppHelper.setShippingGroupManager(sgManager)
			ppHelper.setOrderManager(orderManagerMock)
			
			def PricingModelHolder pModle = Mock()
			
			def String [] remItemIds = []
			order.getCommerceItem("rmid1") >> CommerceItme
			
			
			ppHelper.callSuperDeleteItems(*_) >> {}
			
			//1*sgManager.getStorePickupShippingGroup("storeId", order) >> {throw new Exception("Exception")}
			1*orderManagerMock.updateOrder(_)
			3*tManager.getTransaction() >> tranction >>{throw new SystemException("exception")}
			
		when:
			List<String> deletedSku = ppHelper.deleteItems(order, remItemIds, pModle, null,null,null,null)
		then:
			0* sgManager.getRegistryPickupShippingGroup(_, _)
			0 * order.getCommerceItem(_)
	}
	
	
	/******************************************* manageAddOrRemoveGiftWrapToShippingGroup *************************************************/
	
	def "manageAddOrRemoveGiftWrapToShippingGroup . TC when giftingFlag is true" (){
		given:
			 
		     GiftWrapVO gVO = new GiftWrapVO()
			 //BBBShippingInfoBean sBean = new BBBShippingInfoBean() 
			 cisiVO.getGiftWrap() >> true
			 cisiVO.getGiftMessage() >> "to harry"
			 cisiVO.getGiftingFlag() >> true
			 
			 bbbHdsg.containsGiftWrap() >> false 
			 
			 ////////addGiftWrap
			 1*cTools.getWrapSkuDetails(_) >> gVO
			 1*orderManagerMock.getOrderTools() >> orderTools
			 1*orderTools.getGiftWrapCommerceItemType() >> "giftType"
			 
			 gVO.setWrapSkuId("wpId")
			 gVO.setWrapProductId("wpPid")
			 
			 1*itemManager.createCommerceItem( "giftType", "wpId", "wpPid", 1) >> item
			 item.getId() >> "Id"
			 1*itemManager.addAsSeparateItemToOrder(order, item)
			 1*itemManager.addItemQuantityToShippingGroup(order, "Id", _, 1)
			 /////////////////////////
			 
			 
		when:
			ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,cisiVO )
		then:
		    2* bbbHdsg.setGiftWrapInd(true)
			1* bbbHdsg.setGiftMessage(_)
			1* cisiVO.setGiftWrap(true)
		
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup . TC when containsGiftWrap is true and gift wrap msg is empty" (){
		given:
		 
			 GiftWrapVO gVO = new GiftWrapVO()
			 //BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 cisiVO.getGiftWrap() >> true
			 cisiVO.getGiftMessage() >> ""
			 cisiVO.getGiftingFlag() >> true
			 
			 bbbHdsg.containsGiftWrap() >> true
			 
			 
			 
		when:
			ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,cisiVO )
		then:
			0* bbbHdsg.setGiftWrapInd(true)
			1* bbbHdsg.setGiftMessage("")
			0* cisiVO.setGiftWrap(true)
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup . TC when pGiftWrapFlag is false" (){
		given:
			 
			 //BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 cisiVO.getGiftWrap() >> false
			 cisiVO.getGiftMessage() >> ""
			 cisiVO.getGiftingFlag() >> true
			 
			 gwItem.getId() >> "gwid"
			 bbbHdsg.getCommerceItemRelationships() >> [cir]
			 cir.getCommerceItem() >> gwItem
			 1*itemManager.removeItemFromOrder(order, "gwid") >> {}
			 
			 
		when:
			ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,cisiVO )
		then:
			0* bbbHdsg.setGiftWrapInd(true)
			1* bbbHdsg.setGiftMessage("")
			1* cisiVO.setGiftWrap(false)
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup . TC when giftingFlag is false" (){
		given:
		 
			 //BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 cisiVO.getGiftWrap() >> false
			 cisiVO.getGiftMessage() >> ""
			 cisiVO.getGiftingFlag() >> false
			 1*bbbHdsg.getSpecialInstructions() >> ["giftMessage" : "message"]
			 1*bbbHdsg.containsGiftWrap() >> true
			 
			 // removeGiftWrap
			 gwItem.getId() >> "gwid"
			 1*bbbHdsg.getCommerceItemRelationships() >> [cir]
			 1*cir.getCommerceItem() >> item
			 
			 
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,cisiVO )
		then:
		    value == true
			1* bbbHdsg.setGiftWrapInd(false)
			1* bbbHdsg.setGiftMessage("")
			1* cisiVO.setGiftWrap(false)
			0*itemManager.removeItemFromOrder(_, _)
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup . TC when containsGiftWrap is false" (){
		given:
			 def CommerceItemShipInfoVO cisiVO = Mock()
			 
			 GiftWrapVO gVO = new GiftWrapVO()
			 //BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 cisiVO.getGiftWrap() >> false
			 cisiVO.getGiftMessage() >> ""
			 cisiVO.getGiftingFlag() >> false
			 1*bbbHdsg.getSpecialInstructions() >> ["giftMessage" : "message"]
			 1*bbbHdsg.containsGiftWrap() >> false
			 			 
			 
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,cisiVO )
		then:
			value == false
			1* bbbHdsg.setGiftWrapInd(false)
			1* bbbHdsg.setGiftMessage("")
			0* cisiVO.setGiftWrap(false)
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup . TC when pGiftWrapMessage is null" (){
		given:
			 def CommerceItemShipInfoVO cisiVO = Mock()
			 
			 GiftWrapVO gVO = new GiftWrapVO()
			 //BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 cisiVO.getGiftMessage() >> null
						  
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, sg, "usBed" ,cisiVO )
		then:
			value == false
			0* bbbHdsg.setGiftWrapInd(false)
			0* bbbHdsg.setGiftMessage("")
			0* cisiVO.setGiftWrap(false)
			0*bbbHdsg.getSpecialInstructions()
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup . TC for BBBBusinessException" (){
		given:
			 def CommerceItemShipInfoVO cisiVO = Mock()
			 
			 GiftWrapVO gVO = new GiftWrapVO()
			 //BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 cisiVO.getGiftMessage() >> "<script />"
						  
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, sg, "usBed" ,cisiVO )
		then:
		    BBBBusinessException exception = thrown()
			0* bbbHdsg.setGiftWrapInd(false)
			0* bbbHdsg.setGiftMessage("")
			0* cisiVO.setGiftWrap(false)
			0*bbbHdsg.getSpecialInstructions()
	}
	

	
	/******************************************* manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean *************************************************/
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC when giftingFlag is true" (){
		given:
			 
			 GiftWrapVO gVO = new GiftWrapVO()
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftWrap(true) 
			 sBean.setGiftMessage("message") 
			 sBean.setGiftingFlag(true) 
			 
			 bbbHdsg.containsGiftWrap() >> false
			 
			 ////////addGiftWrap
			 1*cTools.getWrapSkuDetails(_) >> gVO
			 1*orderManagerMock.getOrderTools() >> orderTools
			 1*orderTools.getGiftWrapCommerceItemType() >> "giftType"
			 
			 gVO.setWrapSkuId("wpId")
			 gVO.setWrapProductId("wpPid")
			 
			 1*itemManager.createCommerceItem( "giftType", "wpId", "wpPid", 1) >> item
			 item.getId() >> "Id"
			 1*itemManager.addAsSeparateItemToOrder(order, item)
			 1*itemManager.addItemQuantityToShippingGroup(order, "Id", _, 1)
			 /////////////////////////
			 
			 
		when:
			ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,sBean )
		then:
			2* bbbHdsg.setGiftWrapInd(true)
			1* bbbHdsg.setGiftMessage(_)
		
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC when containsGiftWrap is true and gift wrap msg is null" (){
		given:
		 
			 GiftWrapVO gVO = new GiftWrapVO()
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftWrap(true) 
			 sBean.setGiftMessage(null) 
			 sBean.setGiftingFlag(true) 
			 
			 bbbHdsg.containsGiftWrap() >> true
			 
		when:
			boolean value = ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,sBean )
		then:
			value == false
			0* bbbHdsg.setGiftWrapInd(true)
			1* bbbHdsg.setGiftMessage(null)
			0 * sBean.setGiftWrap(false)
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC when pGiftWrapFlag is false" (){
		given:
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftWrap(false) 
			 sBean.setGiftMessage(null) 
			 sBean.setGiftingFlag(true)  
			 
			 gwItem.getId() >> "gwid"
			1*bbbHdsg.getCommerceItemRelationships() >> [cir]
			 cir.getCommerceItem() >> gwItem
			 1*itemManager.removeItemFromOrder(order, "gwid") >> {}
			 
			 
		when:
			boolean value = ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,sBean )
		then:
		    value == true
			0* bbbHdsg.setGiftWrapInd(true)
			1* bbbHdsg.setGiftMessage(null)
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC when giftingFlag is false" (){
		given:
		 
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftWrap(false)  
			 sBean.setGiftMessage(null) 
			 sBean.setGiftingFlag(false) 
			 1*bbbHdsg.getSpecialInstructions() >> ["giftMessage" : "message"]
			 1*bbbHdsg.containsGiftWrap() >> true
			 
			 // removeGiftWrap
			 gwItem.getId() >> "gwid"
			 1*bbbHdsg.getCommerceItemRelationships() >> [cir]
			 1*cir.getCommerceItem() >> item
			 
			 
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,sBean )
		then:
			value == true
			1* bbbHdsg.setGiftWrapInd(false)
			1* bbbHdsg.setGiftMessage('')
		     sBean.getGiftWrap() == false
			0*itemManager.removeItemFromOrder(_, _)
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC when containsGiftWrap is false" (){
		given:
			 def CommerceItemShipInfoVO cisiVO = Mock()
			 
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftWrap(false) 
			 sBean.setGiftMessage("") 
			 sBean.setGiftingFlag(false) 
			 1*bbbHdsg.getSpecialInstructions() >> ["giftMessage" : "message"]
			 1*bbbHdsg.containsGiftWrap() >> false
						  
			 
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, bbbHdsg, "usBed" ,sBean )
		then:
			value == false
			1* bbbHdsg.setGiftWrapInd(false)
			1* bbbHdsg.setGiftMessage("")
			sBean.getGiftWrap() == false
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC when shipping group type is not HardGoodShipping group" (){
		given:
			 def CommerceItemShipInfoVO cisiVO = Mock()
			 
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftMessage(null) 
						  
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, sg, "usBed" ,sBean )
		then:
			value == false
			0* bbbHdsg.setGiftWrapInd(false)
			0* bbbHdsg.setGiftMessage("")
			sBean.getGiftWrap() == false
			0*bbbHdsg.getSpecialInstructions()
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC for BBBBusinessException" (){
		given:
			 
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftMessage("<script />")  
						  
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, sg, "usBed" ,sBean )
		then:
			BBBBusinessException exception = thrown()
			0* bbbHdsg.setGiftWrapInd(false)
			0* bbbHdsg.setGiftMessage("")
			0*bbbHdsg.getSpecialInstructions()
	}
	
	def "manageAddOrRemoveGiftWrapToShippingGroup for BBBShippingInfoBean . TC for BBBBusinessException when gift message is not valid" (){
		given:
			 
			 BBBShippingInfoBean sBean = new BBBShippingInfoBean()
			 sBean.setGiftMessage("")
			 sBean.setGiftingFlag(true) 
						  
		when:
			boolean value =ppHelper.manageAddOrRemoveGiftWrapToShippingGroup(order, sg, "usBed" ,sBean )
		then:
			BBBBusinessException exception = thrown()
			0* bbbHdsg.setGiftWrapInd(false)
			0* bbbHdsg.setGiftMessage("")
			0*bbbHdsg.getSpecialInstructions()
	}
	
	/******************************** addGiftWrap ********************************/
	
	def "addGiftWrap. TC when created commerce item is null" (){
		given:
			 GiftWrapVO gVO = new GiftWrapVO()
			 1*cTools.getWrapSkuDetails(_) >> gVO
			 1*orderManagerMock.getOrderTools() >> orderTools
			 1*orderTools.getGiftWrapCommerceItemType() >> "giftType"
			 
			 gVO.setWrapSkuId("wpId")
			 gVO.setWrapProductId("wpPid")
			1*itemManager.createCommerceItem( "giftType", "wpId", "wpPid", 1) >> null
		expect:
			ppHelper.addGiftWrap(order, sg, "usBed")
	}
	
	
	def "addGiftWrap. TC when gift wrap vo is null" (){
		given:
			 1*cTools.getWrapSkuDetails(_) >> null
		when:
			ppHelper.addGiftWrap(order, sg, "usBed")
			
		then:	
		    0*itemManager.createCommerceItem( _, _, _, _)
	}
	
	/************************************setRequiredAddressPropertyNames*******************************/
	
	def"setRequiredAddressPropertyNames.TC to add require address property"(){
		given:
		  String [] properties = ["catalogTools"]
		expect:
			ppHelper.setRequiredAddressPropertyNames(properties)
		
	}
	
	def"setRequiredAddressPropertyNames.TC when require address property is null"(){
		given:
		expect:
			ppHelper.setRequiredAddressPropertyNames(null)
	}

/***************************************setRequiredLTLAddressPropertyNames******************************/
	
	def"setRequiredLTLAddressPropertyNames.TC to add require address property"(){
		given:
		  String [] properties = ["catalogTools"]
		expect:
			ppHelper.setRequiredLTLAddressPropertyNames(properties)
		
	}
	
	def"setRequiredLTLAddressPropertyNames.TC when require address property is null"(){
		given:
		expect:
			ppHelper.setRequiredLTLAddressPropertyNames(null)
	}
		
/*****************************************addItemToShippingGroup****************************************/
	def"addItemToShippingGroup. tc check ltl item"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			1*ppHelper.callSuperAddItemToShippingGroup(*_) >> {}
			CommerceItme.getCatalogRefId() >> "sku1"
			order.getSiteId() >> "siteId"
			1*cTools.isSkuLtl("siteId", "sku1") >> true
		when:
			ppHelper.addItemToShippingGroup(CommerceItme, addItemInfo, order, hdsg)
		then:
			1*CommerceItme.setLtlItem(true)
		 
	}
	
	def"addItemToShippingGroup. tc when item is not ltl"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			1*ppHelper.callSuperAddItemToShippingGroup(*_) >> {}
			CommerceItme.getCatalogRefId() >> "sku1"
			order.getSiteId() >> "siteId"
			1*cTools.isSkuLtl("siteId", "sku1") >> false
		when:
			ppHelper.addItemToShippingGroup(CommerceItme, addItemInfo, order, hdsg)
		then:
			0*CommerceItme.setLtlItem(true)
		 
	}
	def"addItemToShippingGroup. tc when item type is not BBBCommerceItem"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			1*ppHelper.callSuperAddItemToShippingGroup(*_) >> {}

		when:
			ppHelper.addItemToShippingGroup(item, addItemInfo, order, hdsg)
		then:
			0*CommerceItme.setLtlItem(true)
			0*cTools.isSkuLtl("siteId", "sku1")
		 
	}
	
	def"addItemToShippingGroup. tc for BBBSystemException"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			1*ppHelper.callSuperAddItemToShippingGroup(*_) >> {}
			CommerceItme.getCatalogRefId() >> "sku1"
			order.getSiteId() >> "siteId"
			1*cTools.isSkuLtl("siteId", "sku1") >> {throw new BBBSystemException("exception")}
		when:
			ppHelper.addItemToShippingGroup(CommerceItme, addItemInfo, order, hdsg)
		then:
			0*CommerceItme.setLtlItem(true)
			1*ppHelper.logError("System exception while checking is sku LTL for skuID: "+"sku1", _)
		 
	}
	
	def"addItemToShippingGroup. tc for BBBBusinessException"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			1*ppHelper.callSuperAddItemToShippingGroup(*_) >> {}
			CommerceItme.getCatalogRefId() >> "sku1"
			order.getSiteId() >> "siteId"
			1*cTools.isSkuLtl("siteId", "sku1") >> {throw new BBBBusinessException("exception")}
		when:
			ppHelper.addItemToShippingGroup(CommerceItme, addItemInfo, order, hdsg)
		then:
			0*CommerceItme.setLtlItem(true)
			1*ppHelper.logError("System exception while checking is sku LTL for skuID: "+"sku1", _)
			
		 
	}
	
	/************************************checkForRequiredAddressProperties***********************************/
	def"checkForRequiredAddressProperties. TC to check for required address property when all prperty are valid"(){
		given:
			ppHelper = Spy()
			String [] properties = ["firstName", "lastName", "address1", "city", "state" ,"postalCode" ]
			def ContactInfo cInfo = Mock()
			ppHelper.setRequiredAddressPropertyNames(properties)
			
			cInfo.getFirstName() >> "harry"
			cInfo.getLastName() >> "kumar"
			cInfo.getCompanyName() >> "4556576565"
			cInfo.getAddress1() >> "32Street"
			cInfo.getAddress2() >> "main road"
			cInfo.getAddress3()  >> "near hotel taj"
			cInfo.getCity()  >> "meerut"
			cInfo.getCountry() >> "india"
			cInfo.getState() >> "up"
			cInfo.getPostalCode() >> "A1A 1A1"
			cInfo.getPhoneNumber() >> "1235678901"
			
			1*ppHelper.isValidCountryAndState( "india", "up") >> true
			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredAddressProperties(cInfo, requestMock)
		then:
			errorList.get(1) == []
			errorList.get(0) == []
	}
	
	def"checkForRequiredAddressProperties. TC to check for required address property when fName, lName, address1 etc is not valid "(){
		given:
			ppHelper = Spy()
			String [] properties = ["firstName", "lastName", "address1", "city", "state" ,"postalCode" ]
			def ContactInfo cInfo = Mock()
			ppHelper.setRequiredAddressPropertyNames(properties)
			
			cInfo.getFirstName() >> "***"
			cInfo.getLastName() >> "***"
			cInfo.getCompanyName() >> "undefineddfdfdfdfdfdfdfdfdfd"
			cInfo.getAddress1() >> "**&&&&<><>"
			cInfo.getAddress2() >> "**&&&&<><>"
			cInfo.getAddress3()  >> "**&&&&<><>"
			cInfo.getCity()  >> "1245***"
			cInfo.getCountry() >> ""
			cInfo.getState() >> ""
			cInfo.getPostalCode() >> "A1A 1"
			cInfo.getPhoneNumber() >> "123567"
			
			1*ppHelper.isValidCountryAndState( "", "") >> false
			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredAddressProperties(cInfo, requestMock)
		then:
			errorList.get(0) == ['country', 'state']
			errorList.get(1) == ['firstName', 'lastName', 'company', 'address1', 'address2', 'address3', 'city', 'countryAndState', 'postalCode', 'phoneNumber']
	}
	
	def"checkForRequiredAddressProperties. TC to check for required address property when fName, lName, address1 etc is empty "(){
		given:
			ppHelper = Spy()
			String [] properties = ["firstName", "lastName", "address1", "city", "state1" ,"postalCode" ]
			def ContactInfo cInfo = Mock()
			ppHelper.setRequiredAddressPropertyNames(properties)
			
			cInfo.getFirstName() >> ""
			cInfo.getLastName() >> ""
			cInfo.getCompanyName() >> ""
			cInfo.getAddress1() >> ""
			cInfo.getAddress2() >> ""
			cInfo.getAddress3()  >> ""
			cInfo.getCity()  >> ""
			cInfo.getCountry() >> ""
			cInfo.getState() >> ""
			cInfo.getPostalCode() >> ""
			cInfo.getPhoneNumber() >> ""
			
			1*ppHelper.isValidCountryAndState( "", "") >> false
			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredAddressProperties(cInfo, requestMock)
		then:
			errorList.get(0) == ['firstName', 'lastName', 'address1', 'city', 'country', 'postalCode']
			errorList.get(1) == ['countryAndState']
	}
	
	def"checkForRequiredAddressProperties. TC to check for required address property when value in reqAddrPropNames list is empty "(){
		given:
			ppHelper = Spy()
			String [] properties = ["", "", "", "", "" ,"" ]
			def ContactInfo cInfo = Mock()
			ppHelper.setRequiredAddressPropertyNames(properties)
			
			cInfo.getFirstName() >> ""
			cInfo.getLastName() >> ""
			cInfo.getCompanyName() >> "undefined"
			cInfo.getAddress1() >> ""
			cInfo.getAddress2() >> ""
			cInfo.getAddress3()  >> ""
			cInfo.getCity()  >> ""
			cInfo.getCountry() >> ""
			cInfo.getState() >> ""
			cInfo.getPostalCode() >> ""
			cInfo.getPhoneNumber() >> ""
			
			1*ppHelper.isValidCountryAndState( "", "") >> false
			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredAddressProperties(cInfo, requestMock)
		then:
			errorList.get(0) == ['country']
			errorList.get(1) == ['countryAndState']
	}
	
	/*************************************************checkForRequiredLTLAddressProperties****************************************
	 * 
	 */
	
	def"checkForRequiredLTLAddressProperties. TC to check for required address property when all prperties are valid"(){
		given:
			String [] properties = ["firstName", "lastName", "address1", "city", "state" ,"postalCode", "email" , "phoneNumber"]
			def BBBAddress address = Mock()
			ppHelper.setRequiredLTLAddressPropertyNames(properties)
			
			address.getFirstName() >> "harry"
			address.getLastName() >> "kumar"
			address.getCompanyName() >> "4556576565"
			address.getAddress1() >> "32Street"
			address.getAddress2() >> "main road"
			address.getAddress3()  >> "near hotel taj"
			address.getCity()  >> "meerut"
			address.getCountry() >> "india"
			address.getState() >> "up"
			address.getPostalCode() >> "A1A 1A1"
			address.getPhoneNumber() >> "1235678901"
			address.getEmail() >> "harry@gmail.com"
			address.getAlternatePhoneNumber() >> "1235678901"
			
			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredLTLAddressProperties(address, requestMock)
		then:
			errorList.get(1) == []
			errorList.get(0) == []
	}
	
	def"checkForRequiredLTLAddressProperties. TC to check for required address property when fName, lName, address1 etc is not valid "(){
		given:
			String [] properties = ["firstName", "lastName", "address1", "city", "state" ,"postalCode", "email" , "phoneNumber"]
			def BBBAddress address = Mock()
			ppHelper.setRequiredLTLAddressPropertyNames(properties)
			
			address.getFirstName() >> "***"
			address.getLastName() >> "***"
			address.getCompanyName() >> "undefineddfdfdfdfdfdfdfdfdfd"
			address.getAddress1() >> "**&&&&<><>"
			address.getAddress2() >> "**&&&&<><>"
			address.getAddress3()  >> "**&&&&<><>"
			address.getCity()  >> "1245***"
			address.getCountry() >> ""
			address.getState() >> ""
			address.getPostalCode() >> "A1A 1"
			address.getPhoneNumber() >> "123567"
			address.getEmail() >> "hagmailcom"
			address.getAlternatePhoneNumber() >> "123"
	
			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredLTLAddressProperties(address, requestMock)
		then:
			errorList.get(0) == ['country', 'state']
			errorList.get(1) == ['firstName', 'lastName', 'company', 'address1', 'address2', 'address3', 'city', 'postalCode', 'phoneNumber', 'email', 'alternatePhoneNumber']
	}
	
	def"checkForRequiredLTLAddressProperties. TC to check for required address property when fName, lName, address1 etc is empty "(){
		given:
			String [] properties = ["firstName", "lastName", "address1", "city", "state" ,"postalCode", "email" , "phoneNumber"]
			def BBBAddress address = Mock()
			ppHelper.setRequiredLTLAddressPropertyNames(properties)
			
			address.getFirstName() >> ""
			address.getLastName() >> ""
			address.getCompanyName() >> ""
			address.getAddress1() >> ""
			address.getAddress2() >> ""
			address.getAddress3()  >> ""
			address.getCity()  >> ""
			address.getCountry() >> ""
			address.getState() >> ""
			address.getPostalCode() >> ""
			address.getPhoneNumber() >> ""
			address.getEmail() >> ""
			address.getAlternatePhoneNumber() >> ""

			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredLTLAddressProperties(address, requestMock)
		then:
			errorList.get(0) == ['firstName', 'lastName', 'address1', 'city', 'country', 'state', 'postalCode', 'phoneNumber', 'email']
			errorList.get(1) == []
	}
	
	def"checkForRequiredLTLAddressProperties. TC to check for required address property when value in reqAddrPropNames list is empty "(){
		given:
			String [] properties = ["", "", "", "", "" ,"", "" , ""]
			def BBBAddress address = Mock()
			ppHelper.setRequiredLTLAddressPropertyNames(properties)
			
			address.getCompanyName() >> "undefined"
			
			
		when:
			List<List<String>> errorList = ppHelper.checkForRequiredLTLAddressProperties(address, requestMock)
		then:
			errorList.get(0) == ['country']
			errorList.get(1) == []
	}
	
	
		
	/**************************************** isValidCountryAndState ***********************************************/
	
	def"isValidCountryAndState.TC when country is US"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			
			String state = "new"
			1*ppHelper.getCurrentSiteId() >> ""
			1*cTools.getStates("BedBathUS", true, null) >> [stateVO]
			stateVO.setStateCode("new")
		when:
			boolean value = ppHelper.isValidCountryAndState("us", state)
		then:
		value == true
	}
	
	def"isValidCountryAndState.TC when country is US and current site is not empty"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			
			String state = "new"
			1*ppHelper.getCurrentSiteId() >> "BedBathUS"
			1*cTools.getStates("BedBathUS", true, null) >> [stateVO]
			stateVO.setStateCode("newy")
		when:
			boolean value = ppHelper.isValidCountryAndState("us", state)
		then:
		value == false
	}
	
	def"isValidCountryAndState.TC when country is CA"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			
			String state = "new"
			1*ppHelper.getCurrentSiteId() >> ""
			1*cTools.getStates("BedBathCanada", true, null) >> []
		when:
			boolean value = ppHelper.isValidCountryAndState("ca", state)
		then:
		value == false
	}
	
	def"isValidCountryAndState.TC when country is CA and current site is not empty"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			
			String state = "new"
			1*ppHelper.getCurrentSiteId() >> "BedBathCanada"
			1*cTools.getStates("BedBathCanada", true, null) >> {throw new BBBSystemException("exception")}
		when:
			boolean value = ppHelper.isValidCountryAndState("ca", state)
		then:
		value == false
	}
	
	def"isValidCountryAndState.TC for BBBBusinessException"(){
		given:
			ppHelper = Spy()
			ppHelper.setCatalogTools(cTools)
			
			String state = "new"
			1*ppHelper.getCurrentSiteId() >> "BedBathCanada"
			1*cTools.getStates("BedBathCanada", true, null) >> {throw new BBBBusinessException("exception")}
		when:
			boolean value = ppHelper.isValidCountryAndState("ca", state)
		then:
		value == false
	}
	
	def"isValidCountryAndState.TC when country is empty"(){
		given:
		when:
			boolean value = ppHelper.isValidCountryAndState("", "state")
		then:
		value == false
			0 * ppHelper.getCurrentSiteId()
			0 * cTools.getStates(_, _, _)
	}
	
	def"isValidCountryAndState.TC when state is empty"(){
		given:
		when:
			boolean value = ppHelper.isValidCountryAndState("ca", "")
		then:
		value == false
			0 * ppHelper.getCurrentSiteId()
			0 * cTools.getStates(_, _, _)
	}
	
	def"isValidCountryAndState.TC when country is not in (US or CA)"(){
		given:
		when:
			boolean value = ppHelper.isValidCountryAndState("caaa", "new")
		then:
		value == true
			0 * ppHelper.getCurrentSiteId()
			0 * cTools.getStates(_, _, _)
	}
	
	/*************************************************** getCreditCardFromOrder *******************************************/
	
	def"getCreditCardFromOrder .TC to get he credit card info from order"(){
	    given:
		Calendar calendar = Calendar.getInstance()
		
		def BBBCreditCard cCard = Mock()
		def BBBCreditCard cCard1 = Mock()
		def BBBCreditCard cCard3 = Mock()
		def BBBCreditCard cCard4 = Mock()
		def BBBCreditCard cCard5 = Mock()
		
		def PaymentGroup pg = Mock()
		def BBBAddressImpl address = Mock()
		/////getPaymentGroupOfTypeFromOrder
		1*order.getPaymentGroups() >> [cCard, cCard1, cCard3, pg, cCard4, cCard5]
		1*cCard.getCreditCardType() >> "visa"
		1*cCard1.getCreditCardType() >> ""
		cCard3.getCreditCardType() >> ""
		1*cCard4.getCreditCardType() >> "visa"
		1*cCard5.getCreditCardType() >> "visa"
		
		1*cCard.getCreditCardNumber() >> ""
		1*cCard1.getCreditCardNumber() >> "411111"
		cCard3.getCreditCardNumber() >> ""
		1*cCard4.getCreditCardNumber() >> ""
		1*cCard5.getCreditCardNumber() >> ""
		
		///////////////////////////
		1*cCard.getBillingAddress() >> address
		1*cCard.getCardVerificationNumber() >> "1"
		1*cCard.getId() >> "c1"
		cCard.getCreditCardNumber() >> "4564561"
		cCard.getCreditCardType() >> "visa1"
		1*cCard.getCurrencyCode() >> "01"
		1*cCard.getExpirationDayOfMonth() >> "23"
		1*cCard.getExpirationMonth() >> "12"
		3*cCard.getExpirationYear() >> "2030"
		cCard.getNameOnCard() >> "harry"
		2*cCard.getLastFourDigits() >> "4561"
		1*cCard.getPaymentId() >> "pyId1"
		
		1*cCard1.getBillingAddress() >> address
		1*cCard1.getCardVerificationNumber() >> "2"
		1*cCard1.getId() >> "c2"
		cCard1.getCreditCardNumber() >> "4564561"
		cCard1.getCreditCardType() >> "visa2"
		1*cCard1.getCurrencyCode() >> "01"
		1*cCard1.getExpirationDayOfMonth() >> "23"
		2*cCard1.getExpirationMonth() >> calendar.get(Calendar.MONTH)-1
		3*cCard1.getExpirationYear() >> calendar.get(Calendar.YEAR) 
		cCard1.getNameOnCard() >> "harry"
		1*cCard1.getLastFourDigits() >> ""
		1*cCard1.getPaymentId() >> "pyId2"
		
		1*cCard4.getBillingAddress() >> address
		1*cCard4.getCardVerificationNumber() >> "3"
		1*cCard4.getId() >> "c3"
		cCard4.getCreditCardNumber() >> "4564561"
		cCard4.getCreditCardType() >> "visa3"
		1*cCard4.getCurrencyCode() >> "01"
		1*cCard4.getExpirationDayOfMonth() >> "23"
		2*cCard4.getExpirationMonth() >> calendar.get(Calendar.MONTH)
		3*cCard4.getExpirationYear() >> calendar.get(Calendar.YEAR)
		cCard4.getNameOnCard() >> "harry"
		2*cCard4.getLastFourDigits() >> "45"
		1*cCard4.getPaymentId() >> "pyId3"

		1*cCard5.getBillingAddress() >> address
		1*cCard5.getCardVerificationNumber() >> "4"
		1*cCard5.getId() >> "c4"
		cCard5.getCreditCardNumber() >> "4564561"
		cCard5.getCreditCardType() >> "visa4"
		1*cCard5.getCurrencyCode() >> "01"
		1*cCard5.getExpirationDayOfMonth() >> "23"
		1*cCard5.getExpirationMonth() >> calendar.get(Calendar.MONTH)
		2*cCard5.getExpirationYear() >> calendar.get(Calendar.YEAR)-1
		cCard5.getNameOnCard() >> "harry"
		2*cCard5.getLastFourDigits() >> "45"
		1*cCard5.getPaymentId() >> "pyId4"

		
		when:
		List<BasicBBBCreditCardInfo> value = ppHelper.getCreditCardFromOrder(order)
		then:
		BasicBBBCreditCardInfo info = value.get(0)
		BasicBBBCreditCardInfo info1 = value.get(1)
		BasicBBBCreditCardInfo info2 = value.get(2)
		
		info.getCardVerificationNumber() == "1"
		info.getCreditCardType() == "visa1"
		info.getPaymentId() == "pyId1"
		
		info1.getCardVerificationNumber() == "2"
		info1.getCreditCardType() == "visa2"
		info1.getPaymentId() == "pyId2"

		info2.getCardVerificationNumber() == "3"
		info2.getCreditCardType() == "visa3"
		info2.getPaymentId() == "pyId3"

		
		value.get(1).getCardVerificationNumber() == "2" 
		value.get(2).getCardVerificationNumber() == "3" 
		value.get(3).getCardVerificationNumber() == "4" 
	}
	
	/****************************************************** removeGiftWrapCommerceItemRelationShip *************************************/
	
	def"removeGiftWrapCommerceItemRelationShip. TC to remove GiftWrapCommerceItem relationship"(){
		given:
		    def GiftWrapCommerceItem gwItem = Mock()
			1*order.getShippingGroups() >>  [bbbHdsg, sg]
			1*bbbHdsg.getCommerceItemRelationships() >> [cir1,cir2, cir3, cir4]
			cir1.getCommerceItem() >> gwItem
			cir2.getCommerceItem() >> CommerceItme
			cir3.getCommerceItem() >> null
			cir4.getCommerceItem() >> gwItem
			
			gwItem.getId() >> "gwId"
			2*itemManager.removeAllRelationshipsFromCommerceItem(order, "gwId") >> {}
			2*itemManager.removeItemFromOrder(order,"gwId") >> {}
			//reprice
			1*pricingToolsMock.priceOrderTotal(order, null, null, null, _) >> {}
			1*orderManagerMock.updateOrder(order) >> {}
			3*tManager.getTransaction() >> tranction >>{throw new SystemException("exception")}
			
		when:
			boolean value = ppHelper.removeGiftWrapCommerceItemRelationShip(order, null, null, null)
		then:
			value == true
			2 * bbbHdsg.setGiftMessage("")
	}
	
	def"removeGiftWrapCommerceItemRelationShip. TC for PricingException"(){
		given:
			def GiftWrapCommerceItem gwItem = Mock()
			1*order.getShippingGroups() >>  [bbbHdsg]
			1*bbbHdsg.getCommerceItemRelationships() >> [cir1]
			cir1.getCommerceItem() >> gwItem
			
			gwItem.getId() >> "gwId"
			1*itemManager.removeAllRelationshipsFromCommerceItem(order, "gwId") >> {}
			1*itemManager.removeItemFromOrder(order,"gwId") >> {}
			//reprice
			1*pricingToolsMock.priceOrderTotal(order, null, null, null, _) >> {throw new PricingException("exception")}
			
		when:
			boolean value = ppHelper.removeGiftWrapCommerceItemRelationShip(order, null, null, null)
		then:
			CommerceException exception = thrown()
			1 * bbbHdsg.setGiftMessage("")
			0 * orderManagerMock.updateOrder(order)
	}
	
	def"removeGiftWrapCommerceItemRelationShip. TC when giftWrapItemList"(){
		given:
			1*order.getShippingGroups() >>  [sg]
			
		when:
			boolean value = ppHelper.removeGiftWrapCommerceItemRelationShip(order, null, null, null)
		then:
			0 * bbbHdsg.setGiftMessage("")
			0 * orderManagerMock.updateOrder(order)
			0 * itemManager.removeAllRelationshipsFromCommerceItem(_, _)
	}
	
	/********************************** repriceOrder *********************************/
	
	def"repriceOrder. TC fro Exception"(){
		given:
			1*pricingToolsMock.priceOrderTotal(order, null, null, null, _) >> {throw new Exception("exception")}
		expect:
			ppHelper.repriceOrder(order, null, null, null)
	 
    }
	
	/************************************ isEcoFeeForCurrentSite ***************************/
	
	def"isEcoFeeForCurrentSite. TC to checks if ECOFEE is eligible for current site or not"(){
		given:
			2*order.getSiteId() >> "usBed"
			1*cTools.getContentCatalogConfigration("EcoFeeEligible_usBed") >> ["true"]
		when:
			boolean value = ppHelper.isEcoFeeForCurrentSite(order)
		then:
		  value == true
	 
    }
	
	def"isEcoFeeForCurrentSite. TC to checks if ECOFEE is eligible for current site or not when ContentCatalogConfigration list is having false value"(){
		given:
			2*order.getSiteId() >> "usBed"
			1*cTools.getContentCatalogConfigration("EcoFeeEligible_usBed") >> ["false"]
		when:
			boolean value = ppHelper.isEcoFeeForCurrentSite(order)
		then:
		  value == false
	 
	}
	
	def"isEcoFeeForCurrentSite. TC to checks if ECOFEE is eligible for current site or not when ContentCatalogConfigration is empty "(){
		given:
			2*order.getSiteId() >> "usBed"
			1*cTools.getContentCatalogConfigration("EcoFeeEligible_usBed") >> []
		when:
			boolean value = ppHelper.isEcoFeeForCurrentSite(order)
		then:
		  value == false
	 
	}
	
	def"isEcoFeeForCurrentSite. TC to checks if ECOFEE is eligible for current site or not when ContentCatalogConfigration is null "(){
		given:
			2*order.getSiteId() >> "usBed"
			1*cTools.getContentCatalogConfigration("EcoFeeEligible_usBed") >> null
		when:
			boolean value = ppHelper.isEcoFeeForCurrentSite(order)
		then:
		  value == false
	 
	}
	
	def"isEcoFeeForCurrentSite. TC site id is null "(){
		given:
			2*order.getSiteId() >> null
		when:
			boolean value = ppHelper.isEcoFeeForCurrentSite(order)
		then:
		  value == false
		  0 * cTools.getContentCatalogConfigration(_)
	 
	}
	
	def"isEcoFeeForCurrentSite. TC for BBBSystemException "(){
		given:
			2*order.getSiteId() >> "usBed"
			1*cTools.getContentCatalogConfigration("EcoFeeEligible_usBed") >> {throw new BBBSystemException("exception")}
		when:
			boolean value = ppHelper.isEcoFeeForCurrentSite(order)
		then:
		  value == false
	 
	}
	
	def"isEcoFeeForCurrentSite. TC for BBBBusinessException "(){
		given:
			2*order.getSiteId() >> "usBed"
			1*cTools.getContentCatalogConfigration("EcoFeeEligible_usBed") >> {throw new BBBBusinessException("exception")}
		when:
			boolean value = ppHelper.isEcoFeeForCurrentSite(order)
		then:
		  value == false
	 
	}
	
	/**************************************************** toString *********************************************/
	def "toString. TC for To String" (){
		given:
				
		when:
			String value = ppHelper.toString()
		then:
			value.contains("checkoutManager")
			value.contains("catalogTools")
			value.contains("giftRegistryManager")
			value.contains("claimableManager")
			value.contains("mAddrPropMap")
			value.contains("mReqAddrPropNames")
			value.contains("mStoreOrderTools")
			value.contains("mPricingTools")
			
	}
	
	/*************************************************** eximDemoCode ****************************************/
	
	def "eximDemoCode. when catalog ref id is 18384876 "(){
		given:
			addItemInfo.getCatalogRefId() >> "18384876"
			
		when:
			ppHelper.eximDemoCode(CommerceItme, addItemInfo)
		then:
		1*CommerceItme.setReferenceNumber("ref1")
	}
	
	def "eximDemoCode. when catalog ref id is 18392518" (){
		given:
			addItemInfo.getCatalogRefId() >> "18392518"
			
		when:
			ppHelper.eximDemoCode(CommerceItme, addItemInfo)
		then:
		1*CommerceItme.setReferenceNumber("ref2")
	}
	
	def "eximDemoCode. when catalog ref id is 17823833" (){
		given:
			addItemInfo.getCatalogRefId() >> "17823833"
			
		when:
			ppHelper.eximDemoCode(CommerceItme, addItemInfo)
		then:
		1*CommerceItme.setReferenceNumber("ref3")
	}
	
	def "eximDemoCode. when catalog ref id is not in (17823833,18392518 and 18384876 ) " (){
		given:
			addItemInfo.getCatalogRefId() >> "178"
			
		when:
			ppHelper.eximDemoCode(CommerceItme, addItemInfo)
		then:
		0 * CommerceItme.setReferenceNumber("_")
	}
	
	def "eximDemoCode. when exime demo is false " (){
		given:
		    ppHelper = Spy() 
			
		when:
			ppHelper.eximDemoCode(CommerceItme, addItemInfo)
		then:
		0 * CommerceItme.setReferenceNumber("_")
	}
	
	/******************************************* removeAllGiftWrapFromShippingGroup **************************************************/
	
	def" removeAllGiftWrapFromShippingGroup . when shipping group contain gift wrap"(){
		given:
		  1*bbbHdsg.getSpecialInstructions() >> ["giftMessage" : "message"]
		    bbbHdsg.containsGiftWrap() >> true
		  1*bbbHdsg.getCommerceItemRelationships() >> []
		when:
			ppHelper.removeAllGiftWrapFromShippingGroup(order, bbbHdsg, "us")
		then:
		1* bbbHdsg.setGiftWrapInd(false)
		1* bbbHdsg.setGiftMessage("")
	}

	def" removeAllGiftWrapFromShippingGroup . when shipping group does not contain gift wrap"(){
		given:
		  1*bbbHdsg.getSpecialInstructions() >> ["giftMessage" : "message"]
			bbbHdsg.containsGiftWrap() >> false
		  
		when:
			ppHelper.removeAllGiftWrapFromShippingGroup(order, bbbHdsg, "us")
		then:
		1* bbbHdsg.setGiftWrapInd(false)
		1* bbbHdsg.setGiftMessage("")
		0*bbbHdsg.getCommerceItemRelationships()
	}
	
	/*************************************************** fetchExistingCommerceItem ****************************************/
	
	def"fetchExistingCommerceItem. Tc to check the existing throw  CommerceItemNotFoundException"() {
		
		given :
		CommerceItme.getCatalogRefId() >> "sku1234"
		 order.getCommerceItemsByCatalogRefId("sku1234") >> {throw new CommerceItemNotFoundException()}
		
		when:
		 List<String> existingCIs = ppHelper.fetchExistingCommerceItem(order, CommerceItme)
		then:		 
		 existingCIs == null
	}
	def"fetchExistingCommerceItem. Tc to check the existing throw  InvalidParameterException"() {
		
		given :
		CommerceItme.getCatalogRefId() >> "sku1234"
		 order.getCommerceItemsByCatalogRefId("sku1234") >> {throw new InvalidParameterException()}
		
		when:
		 List<String> existingCIs = ppHelper.fetchExistingCommerceItem(order, CommerceItme)
		then:
		 existingCIs == null
	}
	
	
}