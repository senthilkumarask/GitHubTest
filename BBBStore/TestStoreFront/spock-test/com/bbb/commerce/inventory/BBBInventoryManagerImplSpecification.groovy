package com.bbb.commerce.inventory

import atg.commerce.inventory.InventoryException
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemNotFoundException
import atg.commerce.order.InvalidParameterException
import atg.commerce.order.OrderHolder

import java.util.List;
import java.util.Map
import java.util.concurrent.ConcurrentHashMap;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.vo.InventoryVO
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.selfservice.common.SelfServiceConstants;

import spock.lang.specification.BBBExtendedSpec

class BBBInventoryManagerImplSpecification extends BBBExtendedSpec {

	BBBInventoryManagerImpl invMngrImpl
	OnlineInventoryManager mOnlineInventoryManager = Mock()
	BBBCatalogTools mCatalogTools = Mock()
	BopusInventoryService mBopusService = Mock()
	
	def setup(){
		invMngrImpl = Spy()
		invMngrImpl.setOnlineInventoryManager(mOnlineInventoryManager)
		invMngrImpl.setCatalogTools(mCatalogTools)
		invMngrImpl.setBopusService(mBopusService)
		invMngrImpl.setLoggingDebug(true)
		invMngrImpl.setLoggingError(true)
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag as true"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "caFlag"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("siteId", "skuId", "operation")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag as true and globalStockLevel set as 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "caFlag"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("siteId", "skuId", "operation")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag as true and  globalStockLevel set as -1"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "caFlag"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> -1L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("siteId", "skuId", "operation")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag as true and  inventoryVO passed as null"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "caFlag"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> null
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("siteId", "skuId", "operation")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is retrieve"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "retrieve")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is addItemFromReg"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "addItemFromReg")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is addItemFromReg and siteId is TBS"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("TBS", "skuId", "addItemFromReg")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is addItemFromReg and siteId is TBS and stock level is 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("TBS", "skuId", "addItemFromReg")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is addItemFromReg and casFlag is null"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "addItemFromReg")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is addItemFromReg and casFlag is null and stockLevel is 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "addItemFromReg")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is addItemFromReg and casFlag is a and stockLevel is 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "addItemFromReg")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation is addItemFromReg and stockLevel is 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "addItemFromReg")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation random"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "random")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation random and stockLevel is 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation random and casFlag is null"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "random")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation random and casFlag is a and stock level is 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("BedBathCanada", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation random and siteId is TBS_BedBathCanada"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("TBS_BedBathCanada", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation random and siteId is random"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("random", "skuId", "random")
		
		then:
			availability == 0
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCSku flag set as false and operation random and siteId is random and stockLevel is 0"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("random", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with vo passed as null"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> null
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("random", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with BBBSystemException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBSystemException("")}
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("random", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with BBBBusinessException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBBusinessException("")}
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("random", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCFlag as true and  BBBBusinessException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBBusinessException("")}
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("random", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getEverLivingProductAvailability() , Happy Path with isVDCFlag as true and   BBBSystemException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getEverLivingSKUDetails(_, _, true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBSystemException("")}
			
		when:
			int availability = invMngrImpl.getEverLivingProductAvailability("random", "skuId", "random")
		
		then:
			availability == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag true"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("siteId", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag true and stockLevel less than 5"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 4L
			vo.getSiteStockLevel() >> 4L
			vo.getGiftRegistryStockLevel() >> 4L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("siteId", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "retrieve", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg and siteId is TBS_BedBathCanada"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("TBS_BedBathCanada", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg and siteId is TBS_BedBathCanada and stockLevel is 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("TBS_BedBathCanada", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg and siteId is TBS_BedBathCanada and casFlag is null"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("TBS_BedBathCanada", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg and siteId is TBS_BedBathCanada and casFlag is a"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("TBS_BedBathCanada", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg and siteId is TBS_BedBathCanada and casFlag is a and stockLevel is 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("TBS_BedBathCanada", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg and siteId is random"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("random", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is addItemFromReg and siteId is random with stockLevel 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("random", "skuId", "addItemFromReg", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is BedBathCanada"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is BedBathCanada with stockLevel 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is BedBathCanada and casFlag is a"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is BedBathCanada with casFlag is a and stockLevel 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is BedBathCanada and casFlag is null"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is BedBathCanada with casFlag is null and stockLevel 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is random"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("random", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability(), Happy path with vdcFlag false and operation is random and siteId is random and stockLevel 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> null
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("random", "skuId", "random", 5L)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability() , Happy Path with  BBBBusinessException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBBusinessException("")}
			
		when:
			int availability = invMngrImpl.getProductAvailability("random", "skuId", "random",5L)
		
		then:
			availability == 1
	}
	
	def "getProductAvailability() , Happy Path with  BBBSystemException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBSystemException("")}
			
		when:
			int availability = invMngrImpl.getProductAvailability("random", "skuId", "random",5L)
		
		then:
			availability == 1
	}
	
	def "getProductAvailability()-2, Happy path with vdcFlag as true"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false,true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("siteId", "skuId", "random", true)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability()-2, Happy path with vdcFlag true and stockLevel as 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false,true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("siteId", "skuId", "random", true)
		
		then:
			availabilityStatus == 1
	}
	
	def "getProductAvailability()-2, Happy path with vdcFlag as false"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false,true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "retrieve", true)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability()-2 , Happy Path with  InventoryException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			1*mCatalogTools.getSKUDetails(_, _, false,true) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBSystemException("")}
			
		when:
			int availability = invMngrImpl.getProductAvailability("random", "skuId", "random",true)
		
		then:
			availability == 1
	}
	
	def "getProductAvailability()-3, Happy path with vdcFlag as true"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("siteId", "skuId", "random",5L, skuDetailVO)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability()-3, Happy path with vdcFlag true and stockLevel as 0"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			vo.getSiteStockLevel() >> 0L
			vo.getGiftRegistryStockLevel() >> 0L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("siteId", "skuId", "random",5L, skuDetailVO)
		
		then:
			
			availabilityStatus == 1
	}
	
	def "getProductAvailability()-3, Happy path with vdcFlag as false"(){
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> false
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 10L
			vo.getSiteStockLevel() >> 10L
			vo.getGiftRegistryStockLevel() >> 10L
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "retrieve",5L, skuDetailVO)
		
		then:
			availabilityStatus == 0
	}
	
	def "getProductAvailability()-3 , Happy Path with  InventoryException() is thrown"(){
		
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			skuDetailVO.getEcomFulfillment() >> "a"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> {throw new BBBSystemException("")}
			
		when:
			int availability = invMngrImpl.getProductAvailability("random", "skuId", "random",5L,skuDetailVO)
		
		then:
			availability == 1
	}
	
	def "getProductAvailability()-3, Happy path with skuDetailVO passed as null"(){
		
		given:
			
		when:
			int availabilityStatus = invMngrImpl.getProductAvailability("BedBathCanada", "skuId", "retrieve",5L, null)
		
		then:
			availabilityStatus == 1
	}
	
	def "getATGInventoryForTBS(), Happy path"(){
		
		when:
			int inventory = invMngrImpl.getATGInventoryForTBS("siteId", "skuId", "operation",5L)
		
		then:
			inventory == 0
	}
	
	def "invalidateItemInventoryCache(), Happy Path"(){
		
		when:
		 	invMngrImpl.invalidateItemInventoryCache("siteId","skuId")
			 
		then:
			BBBPerformanceMonitor.start("BBBInventoryManager invalidateItemInventoryCache")
			1*invMngrImpl.logDebug("invalidateItemInventoryCache() : starts")
			1*invMngrImpl.logDebug("Input Parametrs: skuID - skuId , siteId - siteId", null)
			1*invMngrImpl.logDebug("invalidateItemInventoryCache() : starts")			
			BBBPerformanceMonitor.end("BBBInventoryManager invalidateItemInventoryCache")
	}
	
	def "invalidateItemInventoryCache(), throwing BBBSystemException"(){
		
		given:
			1*mOnlineInventoryManager.invalidateItemInventoryCache(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			 invMngrImpl.invalidateItemInventoryCache("siteId","skuId")
			 
		then:
			BBBPerformanceMonitor.start("BBBInventoryManager invalidateItemInventoryCache")
			1*invMngrImpl.logDebug("invalidateItemInventoryCache() : starts")
			1*invMngrImpl.logDebug("Input Parametrs: skuID - skuId , siteId - siteId", null)
			1*invMngrImpl.logDebug("invalidateItemInventoryCache() : starts")
			BBBPerformanceMonitor.end("BBBInventoryManager invalidateItemInventoryCache")
	}
	
	def "invalidateItemInventoryCache(), throwing BBBBusinessException"(){
		
		given:
			1*mOnlineInventoryManager.invalidateItemInventoryCache(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			
		when:
			 invMngrImpl.invalidateItemInventoryCache("siteId","skuId")
			 
		then:
			BBBPerformanceMonitor.start("BBBInventoryManager invalidateItemInventoryCache")
			1*invMngrImpl.logDebug("invalidateItemInventoryCache() : starts")
			1*invMngrImpl.logDebug("Input Parametrs: skuID - skuId , siteId - siteId", null)
			1*invMngrImpl.logDebug("invalidateItemInventoryCache() : starts")
			BBBPerformanceMonitor.end("BBBInventoryManager invalidateItemInventoryCache")
	}
	
	def "uncachedInventoryCheck(), Happy Path"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def BBBCommerceItem ci = Mock()
		def InventoryVO vo = Mock()
		Map<String, Integer> map = new HashMap<String,Integer>()
		
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
			order.getSiteId() >> "BedBathCanada"
			1*mCatalogTools.getSkuEComFulfillment(_) >> "ecomFullfillment"
			ci.isVdcInd() >> true
			ci.getCatalogRefId() >> "skuId"
			ci.getQuantity() >> 5L
			1*mOnlineInventoryManager.getInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			order.getAvailabilityMap() >> map
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == true
	}
	
	def "uncachedInventoryCheck(), Happy Path with isVdcInd flag false"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def BBBCommerceItem ci = Mock()
		def InventoryVO vo = Mock()
		Map map = new HashMap()
		
		given:
			map.put("id001",20)
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
			order.getSiteId() >> "BedBathCanada"
			order.getAvailabilityMap() >> map
			1*mCatalogTools.getSkuEComFulfillment(_) >> "ecomFullfillment"
			ci.isVdcInd() >> false
			ci.getCatalogRefId() >> "skuId"
			ci.getQuantity() >> 5L
			1*mOnlineInventoryManager.getInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 5L
			vo.getSiteStockLevel() >> 5L
			vo.getGiftRegistryStockLevel() >> 5L
			
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == false
	}
	
	def "uncachedInventoryCheck(), Happy Path with isVdcInd flag false and siteId as TBS_BedBathCanada"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def BBBCommerceItem ci = Mock()
		def InventoryVO vo = Mock()
		Map map = new HashMap()
		
		given:
			map.put("id001",20)
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
			order.getSiteId() >> "TBS_BedBathCanada"
			order.getAvailabilityMap() >> map
			1*mCatalogTools.getSkuEComFulfillment(_) >> "ecomFullfillment"
			ci.isVdcInd() >> false
			ci.getCatalogRefId() >> "skuId"
			ci.getQuantity() >> 5L
			1*mOnlineInventoryManager.getInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 5L
			vo.getSiteStockLevel() >> 5L
			vo.getGiftRegistryStockLevel() >> 5L
			
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == false
	}
	
	def "uncachedInventoryCheck(), Happy Path with random siteId"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def BBBCommerceItem ci = Mock()
		def InventoryVO vo = Mock()
		Map<String, Integer> map = new HashMap<String,Integer>()
		
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
			order.getSiteId() >> "random"
			ci.isVdcInd() >> true
			ci.getCatalogRefId() >> "skuId"
			ci.getQuantity() >> 5L
			1*mOnlineInventoryManager.getInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			order.getAvailabilityMap() >> map
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == true
	}
	
	def "uncachedInventoryCheck(), Happy Path with storeId as not null"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def BBBCommerceItem ci = Mock()
		
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
			ci.getStoreId() >> "storeId"
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == false
	}
	
	def "uncachedInventoryCheck(), Happy Path with commerceItem not instance of BBBCommerceItem"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def CommerceItem ci = Mock()
		
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == false
	}
	
	def "uncachedInventoryCheck(), Happy Path with BBBSystemException thrown"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def BBBCommerceItem ci = Mock()
		
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
			order.getSiteId() >> "BedBathCanada"
			1*mCatalogTools.getSkuEComFulfillment(_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == true
	}
	
	def "uncachedInventoryCheck(), Happy Path with BBBBusinessException thrown"(){
		
		def OrderHolder cart = Mock()
		def BBBOrderImpl order = Mock()
		def BBBCommerceItem ci = Mock()
		
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItems() >> [ci]
			order.getSiteId() >> "BedBathCanada"
			1*mCatalogTools.getSkuEComFulfillment(_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			boolean inventoryOOS =  invMngrImpl.uncachedInventoryCheck()
		
		then:
			inventoryOOS == true
	}
	
	def "getInventorystatusForDummyStore(), Happy path with map as null"(){
		
		Map<String,String> dummyStoreIdsMap = null
		
		given:
			requestMock.getObjectParameter(SelfServiceConstants.DUMMY_STORE_MAP) >> dummyStoreIdsMap
		when:
			int inventoryStatus = invMngrImpl.getInventorystatusForDummyStore("storeId")
		
		then:
			inventoryStatus == 0
	}
	
	def "getInventorystatusForDummyStore(), Happy path"(){
		
		Map<String,String> dummyStoreIdsMap = new HashMap<String,String>()
		
		given:
			dummyStoreIdsMap.put("storeId","store101")
			requestMock.getObjectParameter(SelfServiceConstants.DUMMY_STORE_MAP) >> dummyStoreIdsMap
		when:
			int inventoryStatus = invMngrImpl.getInventorystatusForDummyStore("storeId")
		
		then:
			inventoryStatus == -7
	}
	
	def "getInventorystatusForDummyStore(), Happy path with storeId mismatch"(){
		
		Map<String,String> dummyStoreIdsMap = new HashMap<String,String>()
		
		given:
			dummyStoreIdsMap.put("storeId101","store101")
			requestMock.getObjectParameter(SelfServiceConstants.DUMMY_STORE_MAP) >> dummyStoreIdsMap
		when:
			int inventoryStatus = invMngrImpl.getInventorystatusForDummyStore("storeId")
		
		then:
			inventoryStatus == 0
	}
	
	def "getWebProductInventoryStatus(), Happy path with input passed as empty"(){
		
		List<String> skuIdList = new ArrayList<String>()
		
		given:
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 0
			productinventoryStatus.isEmpty() == true
	}
	
	def "getWebProductInventoryStatus(), Happy path with skuIdList has empty skuId"(){
		
		List<String> skuIdList = new ArrayList<String>()
		
		given:
			skuIdList.add("")
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 0
			productinventoryStatus.isEmpty() == true
	}
	
	def "getWebProductInventoryStatus(), Happy path"(){
		
		List<String> skuIdList = new ArrayList<String>()
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			skuIdList.add("skuId")
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			1*mCatalogTools.getSKUDetails(_, _, false) >> skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			1*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 4L
			vo.getSiteStockLevel() >> 4L
			vo.getGiftRegistryStockLevel() >> 4L
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 1
			productinventoryStatus.isEmpty() == false
			productinventoryStatus.get("skuId") == "true"
	}
	
	def "getWebProductInventoryStatus(), Happy path with stockLevel as 0"(){
		
		List<String> skuIdList = new ArrayList<String>()
		def SKUDetailVO skuDetailVO = Mock()
		def InventoryVO vo = Mock()
		
		given:
			skuIdList.add("skuId")
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			2*mCatalogTools.getSKUDetails(_, _,false)>>skuDetailVO
			skuDetailVO.getEcomFulfillment() >> "e"
			skuDetailVO.isVdcSku() >> true
			2*mOnlineInventoryManager.getCachedInventory(_,_) >> vo
			vo.getGlobalStockLevel() >> 0L
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 1
			productinventoryStatus.isEmpty() == false
			productinventoryStatus.get("skuId") == "false"
	}
	
	def "getWebProductInventoryStatus(), Happy path with skuIdList having skuId"(){
		
		List<String> skuIdList = new ArrayList<String>()
		
		given:
			skuIdList.add("skuId")
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			invMngrImpl.getProductAvailability(_, _,_, 0) >> 2
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 0
			productinventoryStatus.isEmpty() == true
	}
	
	def "getWebProductInventoryStatus(), Happy path with BBBSystemException thrown"(){
		
		List<String> skuIdList = new ArrayList<String>()
		
		given:
			skuIdList.add("skuId")
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			invMngrImpl.getProductAvailability(_, _,_, 0) >> {throw new BBBSystemException("BBBSystemException thrown")}
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 1
			productinventoryStatus.isEmpty() == false
			productinventoryStatus.get("skuId") == "Invalid SKU"
	}
	
	def "getWebProductInventoryStatus(), Happy path with BBBBusinessException thrown and error code as 1000"(){
		
		List<String> skuIdList = new ArrayList<String>()
		BBBBusinessException excep = new BBBBusinessException("")
		
		given:
			skuIdList.add("skuId")
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			excep.setErrorCode("1000")
			invMngrImpl.getProductAvailability(_, _,_, 0) >> {throw excep}
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 1
			productinventoryStatus.isEmpty() == false
			productinventoryStatus.get("skuId") == "Invalid SKU"
	}
	
	def "getWebProductInventoryStatus(), Happy path with BBBBusinessException thrown and error code as 1003"(){
		
		List<String> skuIdList = new ArrayList<String>()
		BBBBusinessException excep = new BBBBusinessException("")
		
		given:
			skuIdList.add("skuId")
			requestMock.getHeader("X-bbb-site-id") >> "siteId"
			excep.setErrorCode("1003")
			invMngrImpl.getProductAvailability(_, _,_, 0) >> {throw excep}
			
		when:
			Map<String,String> productinventoryStatus = invMngrImpl.getWebProductInventoryStatus(skuIdList)
		
		then:
			productinventoryStatus.size() == 1
			productinventoryStatus.isEmpty() == false
			productinventoryStatus.get("skuId") == "false"
	}
	
	def "getBOPUSProductAvailability(), Happy path"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100", 100)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
		
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"storeToStore",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 1
	}
	
	def "getBOPUSProductAvailability(), Happy path with useCache as false"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		Map<String, Integer> inventories = new HashMap<String,Integer>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100", 100)
			inventories.put("store100", 10)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			1*mBopusService.getInventoryForBopusItem(_,_,_) >> inventories
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
		
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"storeToStore",storeInventoryContainer,false,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 1
	}
	
	def "getBOPUSProductAvailability(), Happy path with mStoreIdInventoryMap as null "(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> inventories = new HashMap<String,Integer>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			inventories.put("store100", 10)
			storeInventoryContainer.getStoreIdInventoryMap() >> null
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
		
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",[],10L,"storeToStore",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == true
			getBopusProdAvailability.size() == 0
	}
	
	def "getBOPUSProductAvailability(), Happy path with operation as random"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 100)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 10L
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 0
	}
	
	def "getBOPUSProductAvailability(), Happy path with operation as random and inventoryStatus as dummystock"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 20)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 15L
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			invMngrImpl.getInventorystatusForDummyStore(_) >> -7
			skuThresholdVO.getThresholdLimited() >> 5
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == -7
	}
	
	def "getBOPUSProductAvailability(), Happy path with operation as random and inventoryStatus as limitedStock"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 20)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 15L
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			skuThresholdVO.getThresholdLimited() >> 5
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 2
	}
	
	def "getBOPUSProductAvailability(), Happy path with operation as random and skuThresholdVo is null"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 20)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 15L
			1*mCatalogTools.getSkuThreshold(_,_) >> null
			skuThresholdVO.getThresholdLimited() >> 5
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 0
	}
	
	def "getBOPUSProductAvailability(), Happy path with operation as random and skuThresholdVo is null and requested Qty more than available"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 20)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 15L
			1*mCatalogTools.getSkuThreshold(_,_) >> null
			skuThresholdVO.getThresholdLimited() >> 5
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],30L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 1
	}
	
	def "getBOPUSProductAvailability(), Happy path with operation as random and inventoryStatus as dummystock and threshold available in excess"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 20)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 10L
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			invMngrImpl.getInventorystatusForDummyStore(_) >> -7
			skuThresholdVO.getThresholdLimited() >> 5
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == -7
	}
	
	def "getBOPUSProductAvailability(), Happy path with BBBSystemException is thrown"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 20)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 15L
			1*mCatalogTools.getSkuThreshold(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],30L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			InventoryException excep = thrown()
			excep.getMessage().equals(null)
	}
	
	def "getBOPUSProductAvailability(), Happy path with BBBBusinessException is thrown"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		//List<CommerceItem> list = new ArrayList<CommerceItem>()
		
		def BBBCommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100|skuId", 20)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			skuThresholdVO.getThresholdAvailable() >> 15L
			1*mCatalogTools.getSkuThreshold(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],30L,"random",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			InventoryException excep = thrown()
			excep.getMessage().equals(null)
	}
	
	def "getBOPUSProductAvailability(), Happy path with CommerceItemNotFoundException thrown"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		
		given:
			mStoreIdInventoryMap.put("store100", 100)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> {throw new CommerceItemNotFoundException()}
					
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"storeToStore",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 1
	}
	
	def "getBOPUSProductAvailability(), Happy path with InvalidParameterException thrown"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		
		given:
			mStoreIdInventoryMap.put("store100", 100)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> {throw new InvalidParameterException()}
		
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"storeToStore",storeInventoryContainer,true,"registryId",false,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 1
	}
	
	def "getBOPUSProductAvailability(), commerceItem not instance of BBBCommerceItem"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		Map<String, Integer> inventories = new HashMap<String,Integer>()
		
		def CommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			//ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100", 100)
			inventories.put("store100", 100)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			1*mBopusService.getInventoryForBopusItem(_,_,_) >> inventories
		
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"storeToStore",storeInventoryContainer,true,"registryId",true,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 0
	}
	
	def "getBOPUSProductAvailability(), 1"(){
		
		Map<String,Integer> getBopusProdAvailability = new HashMap<String,Integer>()
		Map<String, Integer> mStoreIdInventoryMap = new ConcurrentHashMap<String, Integer>()
		Map<String, Integer> inventories = new HashMap<String,Integer>()
		
		def CommerceItem ci = Mock()
		def BBBStoreInventoryContainer storeInventoryContainer = Mock()
		def OrderHolder cart = Mock()
		def BBBOrder order = Mock()
		def ThresholdVO skuThresholdVO = Mock()
		
		given:
			//ci.getStoreId() >> "store100"
			ci.getQuantity() >> 10L
			mStoreIdInventoryMap.put("store100", 100)
			inventories.put("store101", 100)
			storeInventoryContainer.getStoreIdInventoryMap() >> mStoreIdInventoryMap
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			order.getCommerceItemsByCatalogRefId("skuId") >> [ci]
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			1*mBopusService.getInventoryForBopusItem(_,_,_) >> inventories
		
		when:
			getBopusProdAvailability = invMngrImpl.getBOPUSProductAvailability("siteId","skuId",["store100"],10L,"random",storeInventoryContainer,true,"registryId",true,true)
			
		then:
			getBopusProdAvailability.isEmpty() == false
			getBopusProdAvailability.size() == 1
			getBopusProdAvailability.get("store100") == 1
	}
}
