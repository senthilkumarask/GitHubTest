package com.bbb.commerce.checkout.droplet

import atg.commerce.order.CommerceItem
import atg.commerce.order.HardgoodShippingGroup
import atg.repository.RepositoryException

import java.util.Map

import com.bbb.commerce.browse.vo.SddZipcodeVO
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.RegionVO
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.catalog.vo.ThresholdVO
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.commerce.common.BBBStoreInventoryContainer
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.commerce.inventory.BBBInventoryManagerImpl
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.profile.session.BBBSessionBean
import spock.lang.specification.BBBExtendedSpec
import com.bbb.utils.BBBConfigRepoUtils;


class GetApplicableShippingMethodsDropletSpecification extends BBBExtendedSpec {
	def  GetApplicableShippingMethodsDroplet asmDropletObject
	def BBBOrder orderMock =  Mock()
	def BBBCommerceItem commerceItemMock = Mock()
	def BBBCommerceItem commerceItemMock1 = Mock()
	
	def BBBShippingGroupManager sgmMock = Mock()
	def BBBHardGoodShippingGroup hgsMock = Mock()
	def BBBSameDayDeliveryManager ssdManagerMock = Mock()
	def BBBCatalogToolsImpl catalogToolsMock = Mock()
	def CommerceItem defCommerceItem = Mock()
	def BBBSameDayDeliveryManager sddManager = Mock()
	def HardgoodShippingGroup coreHDDGroup = Mock()
	def BBBStoreInventoryContainer invContainerMock = Mock()
	def ThresholdVO thresholdVoMock = Mock()
	def BBBInventoryManagerImpl invManagerImplMock = Mock()
	BBBHardGoodShippingGroup hds =  new BBBHardGoodShippingGroup()
	RegionVO regionVO = new RegionVO()
	
	SddZipcodeVO ssdZipcodeVO = new SddZipcodeVO() 
	SddZipcodeVO landingdZipcodeVO = new SddZipcodeVO()
	
	ShipMethodVO sMethodVO = new ShipMethodVO()
	ShipMethodVO sMethodVO1 = new ShipMethodVO()
	
	BBBSessionBean sessionBean = new BBBSessionBean()
	
	
	def setup(){
		asmDropletObject = new GetApplicableShippingMethodsDroplet(shippingGroupManager : sgmMock,sameDayDeliveryManager : ssdManagerMock )
		BBBConfigRepoUtils.setBbbCatalogTools(catalogToolsMock)
		requestMock.resolveName(BBBCoreConstants.BBB_STORE_INVENTORY_CONTAINER) >> invContainerMock
	}

	def "service method. Tc to check sku method map whne operation is perSku"  (){
		given:
		asmDropletObject.setSddShipMethodId("method12")
		sMethodVO.setShipMethodId("method12")
		sMethodVO1.setShipMethodId("method12")
		String operation = "perSku"
		String storeId = null
		String skuId = "ksu123"
		String siteId = "USBed"
		
		orderMock.getSiteId() >> siteId
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("operation") >> operation
		//requestMock.getObjectParameter("checkForInventory") >>
		orderMock.getCommerceItems() >> [commerceItemMock,commerceItemMock]
		commerceItemMock.getCatalogRefId() >> skuId
		commerceItemMock.getStoreId() >> storeId
		
		1*sgmMock.getShippingMethodsForSku(skuId, siteId) >> [sMethodVO, sMethodVO1]
		1*sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		
		sgmMock.calculateShippingCost([sMethodVO], orderMock, [hgsMock]) >> {}
		2*ssdManagerMock.getBbbCatalogTools()  >> catalogToolsMock
		catalogToolsMock.getSddShipMethodCharge() >> "300" >> "200"
		
		// for getting sddEligible on 
		1*catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		
		
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		ssdZipcodeVO.setSddEligibility(true)
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO)
		
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter("sddEligiblityStatus", "marketEligible")
		1 * requestMock.setParameter("sddOptionEnabled", false)
		1 * requestMock.setParameter("skuMethodsMap",_)
		1 * requestMock.serviceParameter("output", requestMock, responseMock)
		sMethodVO.getSortShippingCharge() == 300
		sMethodVO1.getSortShippingCharge() == 200
		
	}
	
	def "service method. Tc to check sku method map whne operation is perSku . shipping charg is empty, current zipcode VO is null"  (){
		given:
		asmDropletObject.setSddShipMethodId("method12")
		sMethodVO.setShipMethodId("method12")
		String operation = "perSku"
		String storeId = ""
		String skuId = "ksu123"
		String siteId = "USBed"
		
		orderMock.getSiteId() >> siteId
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("operation") >> operation
		//requestMock.getObjectParameter("checkForInventory") >>
		orderMock.getCommerceItems() >> [commerceItemMock,commerceItemMock1]
		commerceItemMock.getCatalogRefId() >> skuId
		commerceItemMock.getStoreId() >> storeId
		commerceItemMock1.getCatalogRefId() >> "sku2"
		commerceItemMock1.getStoreId() >> storeId

		
		2*sgmMock.getShippingMethodsForSku(_, siteId) >> [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		
		ssdManagerMock.getBbbCatalogTools()  >> catalogToolsMock
		catalogToolsMock.getSddShipMethodCharge() >> ""
		
		// for getting sddEligible on
		1*catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		ssdZipcodeVO.setSddEligibility(true)
		sessionBean.setCurrentZipcodeVO(null)
		
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter("sddEligiblityStatus", "marketIneligible")
		1 * requestMock.setParameter("sddOptionEnabled", false)
		1 * requestMock.setParameter("skuMethodsMap",_)
		1 * requestMock.serviceParameter("output", requestMock, responseMock)
		sMethodVO.getSortShippingCharge() == 0
	}
	
	def "service method. Tc to check sku method map whne operation is perSku . shipping method(gets from ShippingVO and setter method) is different . sddEligibleOn is null"  (){
		given:
		asmDropletObject.setSddShipMethodId("method12")
		sMethodVO.setShipMethodId("method13")
		String operation = "perSku"
		String storeId = ""
		String skuId = "ksu123"
		String siteId = "USBed"
		
		orderMock.getSiteId() >> siteId
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("operation") >> operation
		//requestMock.getObjectParameter("checkForInventory") >>
		orderMock.getCommerceItems() >> [commerceItemMock]
		commerceItemMock.getCatalogRefId() >> skuId
		commerceItemMock.getStoreId() >> storeId
		
		sgmMock.getShippingMethodsForSku(skuId, siteId) >> [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		
		sgmMock.calculateShippingCost([sMethodVO], orderMock, [hgsMock]) >> {}
		ssdManagerMock.getBbbCatalogTools()  >> catalogToolsMock
		catalogToolsMock.getSddShipMethodCharge() >> ""
		
		// for getting sddEligible on
		1*catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> [null]
		
		
		ssdZipcodeVO.setSddEligibility(true)
		sessionBean.setCurrentZipcodeVO(null)
		
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter("sddEligiblityStatus", "marketIneligible")
		1 * requestMock.setParameter("sddOptionEnabled", false)
		1 * requestMock.setParameter("skuMethodsMap",_)
		1 * requestMock.serviceParameter("output", requestMock, responseMock)
		0 * catalogToolsMock.getSddShipMethodCharge()
		0 * requestMock.resolveName("/com/bbb/profile/session/SessionBean")
		sMethodVO.getSortShippingCharge() == 0
	}
	
	def "service method. Tc to check sku method map whne operation is perSku . commerce item type is not BBBCommerceItem"  (){
		given:
		asmDropletObject.setSddShipMethodId("method12")
		sMethodVO.setShipMethodId("method13")
		String operation = "perSku"
		String storeId = ""
		String skuId = "ksu123"
		String siteId = "USBed"
		
		orderMock.getSiteId() >> siteId
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("operation") >> operation
		orderMock.getCommerceItems() >> [defCommerceItem]
		defCommerceItem.getCatalogRefId() >> skuId
		
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.serviceParameter("empty", requestMock, responseMock)

	}
	
	def "service method. Tc to check sku method map whne operation is perSku . commerce item type is  BBBCommerceItem and store id is 123"  (){
		given:
		asmDropletObject.setSddShipMethodId("method12")
		sMethodVO.setShipMethodId("method13")
		String operation = "perSku"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		
		orderMock.getSiteId() >> siteId
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("operation") >> operation
		orderMock.getCommerceItems() >> [commerceItemMock]
		commerceItemMock.getStoreId() >> storeId
		defCommerceItem.getCatalogRefId() >> skuId
		
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.serviceParameter("empty", requestMock, responseMock)

	}
	
	////////////////////////////////// operation is perOrder ////////
	
	def "service method. Tc to check sku method map whne operation is perOrder "  (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3g")
		sMethodVO.setShipMethodId("3g")
		sMethodVO1.setShipMethodId("3g")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = "3g"
		hgsMock.getSddStoreId() >> storeId
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> ""
		requestMock.getObjectParameter("checkForInventory") >> "true"
		
		1*sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO ,sMethodVO1]
		1*sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		
		
		
		1*catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		ssdZipcodeVO.setSddEligibility(true)
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO)
		asmDropletObject.isDisplaySSD(sessionBean, regionVO, true) >> true
		
/*		sessionBean.setLandingZipcodeVO(LandingZipcodeVO)
		LandingZipcodeVO.setSddEligibility(true)
*/		
		1*ssdManagerMock.populateDataInVO(*_) >> regionVO
		1*ssdManagerMock.checkForSDDEligibility(requestMock, orderMock, regionVO, _, zipCode) >> "itemEligible"
		
		////////////////
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock], zipCode)
		ssdManagerMock.getBbbCatalogTools()  >> catalogToolsMock
		catalogToolsMock.getSddShipMethodCharge() >> "300" >> "200"
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.LIMITED_STOCK
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
		
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		1 * requestMock.setParameter("regionVO", regionVO)
		1 * requestMock.setParameter("sddOptionEnabled", true)
	    1 *	requestMock.setParameter("sddEligiblityStatus", "sddEligible")
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3g");
	    1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
	    1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		1 * asmDropletObject.logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
		sMethodVO.getSortShippingCharge() == 300
		sMethodVO1.getSortShippingCharge() == 200
	}
	
	
	def "service method. Tc to check sku method map when operation is perOrder and eligibilityStatus of SBCInventoryForSdd is not 'sddEligible' "  (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3g")
		sMethodVO.setShipMethodId("3g")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = "3g"
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> ""
		requestMock.getObjectParameter("checkForInventory") >> "true"
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock,hgsMock]
		2*hgsMock.getShippingMethod() >>  shippingMethod
		hgsMock.getSddStoreId() >> storeId
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		ssdZipcodeVO.setSddEligibility(true)
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO)
/*		sessionBean.setLandingZipcodeVO(LandingZipcodeVO)
		LandingZipcodeVO.setSddEligibility(true)
	*/	
		asmDropletObject.isDisplaySSD(sessionBean, regionVO, true) >> false
		
		
		ssdManagerMock.populateDataInVO(*_) >> regionVO
		ssdManagerMock.checkForSDDEligibility(requestMock, orderMock, regionVO, _, zipCode) >> "itemEligible"
		
		////////////////
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock],zipCode)
		ssdManagerMock.getBbbCatalogTools()  >> catalogToolsMock
		catalogToolsMock.getSddShipMethodCharge() >> ""
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.NOT_AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		1 * requestMock.setParameter("regionVO", regionVO)
		1 * requestMock.setParameter("sddOptionEnabled", true)
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3g");
	    1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
	    1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		0 * asmDropletObject.logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
		   sMethodVO.getSortShippingCharge() == 0

	}
	
	
	def "service method. Tc to check sku method map when operation is perOrder ,  regionVO is null and shipping group type is not BBBHardGoodShippingGroup" (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3g")
		sMethodVO.setShipMethodId("3gg")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = "3g"
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> ""
		requestMock.getObjectParameter("checkForInventory") >> "true"
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [coreHDDGroup]
		coreHDDGroup.getShippingMethod() >>  shippingMethod
		hgsMock.getSddStoreId() >> storeId
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		ssdZipcodeVO.setSddEligibility(true)
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO)
/*		sessionBean.setLandingZipcodeVO(LandingZipcodeVO)
		LandingZipcodeVO.setSddEligibility(true)
	*/
		asmDropletObject.isDisplaySSD(sessionBean, regionVO, true) >> false
		
		
		ssdManagerMock.populateDataInVO(*_) >> null
		ssdManagerMock.checkForSDDEligibility(requestMock, orderMock, null, _, zipCode) >> "itemEligible"
		
		////////////////
		0*ssdManagerMock.checkSBCInventoryForSdd(_, orderMock) >> "wrong eligability"
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock], zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		0 * requestMock.setParameter("regionVO", regionVO)
		1 * requestMock.setParameter("sddOptionEnabled", true)
		1 *	requestMock.setParameter("sddEligiblityStatus", "itemEligible")
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3gg");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		   sMethodVO.getSortShippingCharge() == 0
	}
	
	def "service method. Tc to check sku method map when operation is perOrder and currentZipCode vo is null" (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3gg")
		sMethodVO.setShipMethodId("3g")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = "3gm"
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> ""
		requestMock.getObjectParameter("checkForInventory") >> "true"
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		hgsMock.getSddStoreId() >> storeId
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		sessionBean.setCurrentZipcodeVO(null)
		asmDropletObject.isDisplaySSD(sessionBean, regionVO, true) >> false
		
		
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock],zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.NOT_AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		0 * requestMock.setParameter("regionVO", regionVO)
		0 * requestMock.setParameter("sddOptionEnabled", true)
		1 *	requestMock.setParameter("sddEligiblityStatus", "")
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3g");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		   sMethodVO.getSortShippingCharge() == 0
	}
	
	/////////////////////////////////// registry Zip ////////////////////////////
	
	def "service method. Tc to check sku method map when operation is perOrder and shippingZip is registryZip" (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3gg")
		sMethodVO.setShipMethodId("3g")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = null
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> "registryZip"
		requestMock.getObjectParameter("checkForInventory") >> "true"
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		
		asmDropletObject.isDisplaySSD(sessionBean, null, false) >> true
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		sessionBean.setCurrentZipcodeVO(null)
		//asmDropletObject.isDisplaySSD(sessionBean, regionVO,true) >> false
		
		
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock], zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		0 * requestMock.setParameter("regionVO", regionVO)
		0 * requestMock.setParameter("sddOptionEnabled", true)
		1 *	requestMock.setParameter("sddEligiblityStatus", "addressIneligible")
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3g");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		1 * asmDropletObject.logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
		   sMethodVO.getSortShippingCharge() == 0
	}
	
	def "service method. Tc to check sku method map when operation is perOrder and shippingZip , registryZip SddEligibility is false  " (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3gg")
		sMethodVO.setShipMethodId("3g")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = null
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> "registryZip"
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		
		asmDropletObject.isDisplaySSD(sessionBean, null , false) >> false
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		sessionBean.setCurrentZipcodeVO(null)
		//asmDropletObject.isDisplaySSD(sessionBean, regionVO, true) >> false
		
		
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock],zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		0 * requestMock.setParameter("regionVO", regionVO)
		0 * requestMock.setParameter("sddOptionEnabled", true)
		1 *	requestMock.setParameter("sddEligiblityStatus", "addressIneligible")
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3g");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		0 * asmDropletObject.logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
		   sMethodVO.getSortShippingCharge() == 0
	}
	
	////////////////////////////////////// not registry Zip ////////////////////////
	
	def "service method. Tc to check sku method map when operation is perOrder and shippingZip is not registryZip" (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3gg")
		sMethodVO.setShipMethodId("3g")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = null
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> "notregistryZip"
		requestMock.getObjectParameter("checkForInventory") >> "false"
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		
		ssdManagerMock.populateDataInVO(*_) >> regionVO
		ssdManagerMock.checkForSDDEligibility(requestMock, orderMock, regionVO, _, "notregistryZip") >> "itemEligible"
	
		//asmDropletObject.checkCondition(sessionBean) >> true
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		sessionBean.setCurrentZipcodeVO(null)
		asmDropletObject.isDisplaySSD(sessionBean, regionVO, true) >> true
		
		
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock],zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		1 * requestMock.setParameter("regionVO", regionVO)
		1 * requestMock.setParameter("sddOptionEnabled", true)
		1 *	requestMock.setParameter("sddEligiblityStatus", "itemEligible")
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3g");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		1 * asmDropletObject.logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
		   sMethodVO.getSortShippingCharge() == 0
	}
	
	def "service method. Tc to check sku method map when operation is perOrder and shippingZip is not registryZip and regionVO is null" (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3gg")
		sMethodVO.setShipMethodId("3g")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = null
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> "notregistryZip"
		requestMock.getObjectParameter("checkForInventory") >> ""
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> ["true"]
		
		ssdManagerMock.populateDataInVO(*_) >> null
		ssdManagerMock.checkForSDDEligibility(requestMock, orderMock, null, _, "notregistryZip") >> "itemEligible"
	
		//asmDropletObject.checkCondition(sessionBean) >> false
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		
		ssdZipcodeVO.setZipcode(zipCode)
		sessionBean.setCurrentZipcodeVO(null)
		asmDropletObject.isDisplaySSD(sessionBean, regionVO, true) >> true
		
		
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock],zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		//1 * requestMock.serviceParameter("empty", requestMock, responseMock)
		0 * requestMock.setParameter("regionVO", regionVO)
		1 * requestMock.setParameter("sddOptionEnabled", true)
		1 *	requestMock.setParameter("sddEligiblityStatus", "itemEligible")
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"3g");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
		0 * asmDropletObject.logDebug("GetApplicableShippingMethodsDroplet : Set DISPLAY_SDD_ALWAYS flag as true" )
		   sMethodVO.getSortShippingCharge() == 0
	}
	
	
	def "service method. Tc to check sku method map when operation is perOrder sddEligibleOn is null" (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3gg")
		sMethodVO.setShipMethodId("ggg")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = "3gg"
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> "notregistryZip"
		requestMock.getObjectParameter("checkForInventory") >> ""
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  [sMethodVO]
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> [null]
			
		
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock],zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		inventoryMap.put(storeId+"|"+skuId,100)
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
		ssdManagerMock.getBbbCatalogTools().getSkuThreshold(_, _) >> thresholdVoMock
		ssdManagerMock.getInventoryManagerImpl() >> invManagerImplMock
		ssdManagerMock.getInventoryManagerImpl().getInventoryStatus(100, 10, thresholdVoMock, storeId) >> BBBInventoryManager.AVAILABLE
		Map<String, Long> skuInventoryMap = new HashMap<>()
		skuInventoryMap.put(skuId,4L)
		ssdManagerMock.getSkuIdQuantityMapFromOrder(orderMock) >> skuInventoryMap
			
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"ggg");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
	}
	
	def "service method. Tc to check sku method map when shipping method VO list is empty" (){
		given:
		asmDropletObject = Spy()
		asmDropletObject.setShippingGroupManager(sgmMock)
		asmDropletObject.setSameDayDeliveryManager(ssdManagerMock)
		
		asmDropletObject.setSddShipMethodId("3gg")
		sMethodVO.setShipMethodId("ggg")
		
		requestMock.getObjectParameter("order") >> orderMock

		
		String operation = "perOrder"
		String storeId = "123"
		String skuId = "ksu123"
		String siteId = "USBed"
		String zipCode = "1254"
		String shippingMethod = "ggg"
		
		requestMock.getObjectParameter("operation") >> operation
		
		requestMock.getParameter("currentZip") >> "notregistryZip"
		
		sgmMock.getShippingMethodsForOrder(orderMock) >>  []
		sgmMock.getHardgoodShippingGroups(orderMock) >>  [hgsMock]
		hgsMock.getShippingMethod() >>  shippingMethod
		
		catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys" , "SameDayDeliveryFlag") >> [null]
			
		
		sgmMock.calculateShippingCost( [sMethodVO], orderMock, [hgsMock],zipCode)
		asmDropletObject.checkSDDInventoryFromSession(requestMock, orderMock, storeId) >> true
		Map<String, Integer> inventoryMap = new HashMap<>()
		invContainerMock.getSddStoreInventoryMap() >> inventoryMap
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock .setParameter(BBBCoreConstants.PRE_SELECTED_SHIP_METHOD,"ggg");
		1 * requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_);
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock,responseMock)
	}
	
////////////////////// error scenario /////////////////
	
	def "service. when operatio is perOrder" () {
		given:
		
		requestMock.getObjectParameter("operation") >> "notperOrder"
		
		requestMock.getObjectParameter("order") >> orderMock
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter( "skuMethodsMap",null);
	    1 * requestMock.setParameter("shipMethodVOList",null);
	    1 * requestMock.serviceParameter("error", requestMock,responseMock)
	}	
	
	def "service. when operatio is empty" () {
		given:
		
		requestMock.getObjectParameter("operation") >> ""
		
		requestMock.getObjectParameter("order") >> orderMock
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter( "skuMethodsMap",null);
		1 * requestMock.setParameter("shipMethodVOList",null);
		1 * requestMock.serviceParameter("error", requestMock,responseMock)
	}
	
	def "service. when order is null" () {
		given:
		
		requestMock.getObjectParameter("operation") >> ""
		
		requestMock.getObjectParameter("order") >> null
	
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1 * requestMock.setParameter( "skuMethodsMap",null);
		1 * requestMock.setParameter("shipMethodVOList",null);
		1 * requestMock.serviceParameter("error", requestMock,responseMock)
	}
	
	///////////////////////////// exception scenario //////////////
	def "service. TC for BBBSystemException while getting shipping methods " () {
		given:
		
		requestMock.getObjectParameter("operation") >> "perSku"
		
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getCommerceItems() >> [commerceItemMock]
		sgmMock.getShippingMethodsForSku(*_) >> {throw new BBBSystemException("exception")}
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
        1*requestMock.serviceParameter("error", requestMock,responseMock)	
	}
	
	def "service. TC for BBBBusinessException while shipping methods " () {
		given:
		
		requestMock.getObjectParameter("operation") >> "perSku"
		
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getCommerceItems() >> [commerceItemMock]
		sgmMock.getShippingMethodsForSku(*_) >> {throw new BBBBusinessException("exception")}
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.serviceParameter("error", requestMock,responseMock)
	}
	
	def "service. TC for RepositoryException while shipping methods " () {
		given:
		
		requestMock.getObjectParameter("operation") >> "perSku"
		
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getCommerceItems() >> [commerceItemMock]
		sgmMock.getShippingMethodsForSku(*_) >> {throw new RepositoryException("exception")}
		when:
		asmDropletObject.service(requestMock, responseMock)
		
		then:
		1*requestMock.serviceParameter("error", requestMock,responseMock)
	}
	
	
	
	
	//////////////////////////////////////// isDisplaySSD with region code  ///////////////
	def "checkCondition. when includeRegionVO is true" (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(true)
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, regionVO, true)
		
		then:
		value == true
	}
	
	
	
	def "isDisplaySSD. when includeRegionVO is true and sddEligibility of landingzipcode is false" (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(true)
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO)
		landingdZipcodeVO.setSddEligibility(false)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(sessionBean, regionVO, true)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is true and landingZipCodeVO is null " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO)
		ssdZipcodeVO.setSddEligibility(true)
 
		sessionBean.setLandingZipcodeVO(null) 
		landingdZipcodeVO.setSddEligibility(false)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, regionVO, true)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is true and sddEligibility of currentzip code is false " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO)
		ssdZipcodeVO.setSddEligibility(false)
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(sessionBean, regionVO, true)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is true and currentzip code VO is null " (){
		given:
		sessionBean.setCurrentZipcodeVO(null) 
		ssdZipcodeVO.setSddEligibility(false)
		
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(sessionBean,regionVO, true)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is true and regionVO is null and SddEligibility is flase " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(false)
		
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(false)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(sessionBean,null, true)
		
		then:
		value == false
	}
	
	def "isDisplaySSD. when includeRegionVO is true andregionVO is null  " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(true)
		
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(sessionBean,null, true)
		
		then:
		value == true
	}
	def "isDisplaySSD. when includeRegionVO is true andr SddEligibility of currentZipcode is false " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(false)
		
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(sessionBean,null, true)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when session is null  " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO)
		ssdZipcodeVO.setSddEligibility(false)
		
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO)
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(null,null, true)
		
		then:
		value == false
	}
	
	/////////////////////////////////////////////////////////
	
	def "isDisplaySSD. when includeRegionVO is false" (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(true)
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD(sessionBean, null ,false)
		
		then:
		value == true
	}
	
	
	
	def "isDisplaySSD. when includeRegionVO is false and sddEligibility of landingzipcode is false" (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(true)
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(false)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, null ,false)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is false and landingZipCodeVO is null " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(true)
 
		sessionBean.setLandingZipcodeVO(null) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, null ,false)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is false and landingZipCodeVO is null and  " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(false)
 
		sessionBean.setLandingZipcodeVO(null) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, null ,false)
		
		then:
		value == false
	}
	
	def "isDisplaySSD. when includeRegionVO is false and sddEligibility of currentzip code is false " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(false)
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, null ,false)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is false and currentzip code VO is null " (){
		given:
		sessionBean.setCurrentZipcodeVO(null) 
		ssdZipcodeVO.setSddEligibility(false)
		
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(true)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, null ,false)
		
		then:
		value == true
	}
	
	def "isDisplaySSD. when includeRegionVO is false and SddEligibility is false  " (){
		given:
		sessionBean.setCurrentZipcodeVO(ssdZipcodeVO) 
		ssdZipcodeVO.setSddEligibility(false)
		
 
		sessionBean.setLandingZipcodeVO(landingdZipcodeVO) 
		landingdZipcodeVO.setSddEligibility(false)
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, null ,false )
		
		then:
		value == false
	}
	
	def ". when session is null  " (){
		given:
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( null, null ,false )
		
		then:
		value == false
	}
	
	def "isDisplaySSD. when ssdZipcodeVO and landingdZipcodeVO  is null  " (){
		given:
		sessionBean.setCurrentZipcodeVO(null)
		
 
		sessionBean.setLandingZipcodeVO(null) 
		
		when:
		boolean value = asmDropletObject.isDisplaySSD( sessionBean, null ,false )
		
		then:
		value == false
	}
	
}
